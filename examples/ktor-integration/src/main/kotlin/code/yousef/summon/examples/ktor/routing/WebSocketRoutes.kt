package code.yousef.summon.examples.ktor.routing

import code.yousef.summon.examples.ktor.models.UserSession
import code.yousef.summon.examples.ktor.websocket.webSocketManager
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException

fun Route.webSocketRoutes() {
    webSocket("/ws") {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }
        
        // Add connection to WebSocket manager
        webSocketManager.addConnection(this, session)
        
        try {
            // Listen for incoming messages (if needed for client-side actions)
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        // Handle any client messages if needed
                        println("Received message from ${session.username}: ${frame.readText()}")
                    }
                    is Frame.Close -> {
                        println("WebSocket connection closed for ${session.username}")
                        break
                    }
                    else -> {}
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            println("WebSocket closed for ${session.username}")
        } catch (e: Exception) {
            println("WebSocket error for ${session.username}: ${e.message}")
        } finally {
            // Remove connection from WebSocket manager
            webSocketManager.removeConnection(this)
        }
    }
}