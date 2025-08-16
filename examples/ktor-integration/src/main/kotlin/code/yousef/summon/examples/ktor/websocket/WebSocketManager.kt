package code.yousef.summon.examples.ktor.websocket

import code.yousef.summon.examples.ktor.models.UserSession
import code.yousef.summon.examples.ktor.models.WebSocketMessage
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class WebSocketManager {
    private val connections = ConcurrentHashMap<String, MutableSet<DefaultWebSocketSession>>()
    private val userSessions = ConcurrentHashMap<DefaultWebSocketSession, UserSession>()
    
    suspend fun addConnection(session: DefaultWebSocketSession, userSession: UserSession) {
        userSessions[session] = userSession
        connections.computeIfAbsent(userSession.username) { mutableSetOf() }.add(session)
        
        // Notify others that user connected
        broadcastToOthers(userSession.username, WebSocketMessage.UserConnected(userSession.username))
        
        println("User ${userSession.username} connected. Total connections: ${getTotalConnections()}")
    }
    
    suspend fun removeConnection(session: DefaultWebSocketSession) {
        val userSession = userSessions.remove(session)
        userSession?.let { user ->
            connections[user.username]?.remove(session)
            if (connections[user.username]?.isEmpty() == true) {
                connections.remove(user.username)
                // Notify others that user disconnected
                broadcastToOthers(user.username, WebSocketMessage.UserDisconnected(user.username))
            }
            println("User ${user.username} disconnected. Total connections: ${getTotalConnections()}")
        }
    }
    
    suspend fun broadcastToUser(username: String, message: WebSocketMessage) {
        connections[username]?.forEach { session ->
            try {
                session.send(Frame.Text(Json.encodeToString(message)))
            } catch (e: Exception) {
                println("Error sending message to $username: ${e.message}")
                // Remove broken connection
                removeConnection(session)
            }
        }
    }
    
    suspend fun broadcastToAll(message: WebSocketMessage) {
        connections.values.flatten().forEach { session ->
            try {
                session.send(Frame.Text(Json.encodeToString(message)))
            } catch (e: Exception) {
                println("Error broadcasting message: ${e.message}")
                removeConnection(session)
            }
        }
    }
    
    suspend fun broadcastToOthers(excludeUsername: String, message: WebSocketMessage) {
        connections.entries
            .filter { it.key != excludeUsername }
            .forEach { (_, sessions) ->
                sessions.forEach { session ->
                    try {
                        session.send(Frame.Text(Json.encodeToString(message)))
                    } catch (e: Exception) {
                        println("Error broadcasting message: ${e.message}")
                        removeConnection(session)
                    }
                }
            }
    }
    
    fun getUserSession(session: DefaultWebSocketSession): UserSession? {
        return userSessions[session]
    }
    
    fun getConnectedUsers(): List<String> {
        return connections.keys.toList()
    }
    
    fun getTotalConnections(): Int {
        return connections.values.sumOf { it.size }
    }
    
    fun getUserConnectionCount(username: String): Int {
        return connections[username]?.size ?: 0
    }
}

// Global WebSocket manager instance
val webSocketManager = WebSocketManager()