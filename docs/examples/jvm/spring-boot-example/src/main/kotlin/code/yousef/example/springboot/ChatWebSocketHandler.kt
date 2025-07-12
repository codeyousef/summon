package code.yousef.example.springboot

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import java.util.concurrent.ConcurrentHashMap

/**
 * WebSocket configuration for chat functionality.
 */
@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(ChatWebSocketHandler(), "/chat")
            .setAllowedOrigins("*") // In production, specify actual origins
    }
}

/**
 * WebSocket handler for real-time chat functionality.
 * Manages chat sessions and broadcasts messages to connected users.
 */
@Component
class ChatWebSocketHandler : WebSocketHandler {
    
    private val logger = LoggerFactory.getLogger(ChatWebSocketHandler::class.java)
    private val objectMapper = ObjectMapper()
    
    // Store active sessions with usernames
    private val sessions = ConcurrentHashMap<String, WebSocketSession>()
    private val usernames = ConcurrentHashMap<String, String>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("WebSocket connection established: ${session.id}")
        
        // Extract username from URI path if provided
        val uri = session.uri
        val username = extractUsernameFromUri(uri.toString()) ?: "User${sessions.size + 1}"
        
        sessions[session.id] = session
        usernames[session.id] = username
        
        // Send welcome message
        sendMessageToSession(session, ChatMessage(
            username = "System",
            message = "Welcome to the chat, $username!",
            timestamp = System.currentTimeMillis()
        ))
        
        // Broadcast user joined
        broadcastMessage(ChatMessage(
            username = "System",
            message = "$username joined the chat",
            timestamp = System.currentTimeMillis()
        ), excludeSessionId = session.id)
        
        // Send active users list
        sendActiveUsersList()
        
        logger.info("User $username connected. Total users: ${sessions.size}")
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        try {
            val username = usernames[session.id] ?: "Unknown"
            val messageText = message.payload.toString()
            
            logger.info("Received message from $username: $messageText")
            
            // Handle special commands
            when {
                messageText.startsWith("/users") -> {
                    sendActiveUsersList(session)
                    return
                }
                messageText.startsWith("/help") -> {
                    sendHelpMessage(session)
                    return
                }
            }
            
            // Broadcast regular message
            val chatMessage = ChatMessage(
                username = username,
                message = messageText,
                timestamp = System.currentTimeMillis()
            )
            
            broadcastMessage(chatMessage)
            
        } catch (e: Exception) {
            logger.error("Error handling message", e)
            sendErrorMessage(session, "Error processing your message")
        }
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.error("WebSocket transport error for session ${session.id}", exception)
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        val username = usernames.remove(session.id) ?: "Unknown"
        sessions.remove(session.id)
        
        logger.info("WebSocket connection closed for user $username: ${closeStatus.reason}")
        
        // Broadcast user left
        if (sessions.isNotEmpty()) {
            broadcastMessage(ChatMessage(
                username = "System",
                message = "$username left the chat",
                timestamp = System.currentTimeMillis()
            ))
            
            // Update active users list
            sendActiveUsersList()
        }
        
        logger.info("User $username disconnected. Total users: ${sessions.size}")
    }

    override fun supportsPartialMessages(): Boolean = false

    /**
     * Broadcast a message to all connected sessions.
     */
    private fun broadcastMessage(message: ChatMessage, excludeSessionId: String? = null) {
        val messageJson = objectMapper.writeValueAsString(message)
        
        sessions.values.forEach { session ->
            if (session.id != excludeSessionId && session.isOpen) {
                try {
                    session.sendMessage(TextMessage(messageJson))
                } catch (e: Exception) {
                    logger.error("Error sending message to session ${session.id}", e)
                    // Remove broken session
                    sessions.remove(session.id)
                    usernames.remove(session.id)
                }
            }
        }
    }
    
    /**
     * Send a message to a specific session.
     */
    private fun sendMessageToSession(session: WebSocketSession, message: ChatMessage) {
        try {
            if (session.isOpen) {
                val messageJson = objectMapper.writeValueAsString(message)
                session.sendMessage(TextMessage(messageJson))
            }
        } catch (e: Exception) {
            logger.error("Error sending message to session ${session.id}", e)
        }
    }
    
    /**
     * Send the active users list to all sessions or a specific session.
     */
    private fun sendActiveUsersList(targetSession: WebSocketSession? = null) {
        val activeUsers = usernames.values.toList()
        val message = ChatMessage(
            username = "System",
            message = "Active users: ${activeUsers.joinToString(", ")}",
            timestamp = System.currentTimeMillis()
        )
        
        if (targetSession != null) {
            sendMessageToSession(targetSession, message)
        } else {
            broadcastMessage(message)
        }
    }
    
    /**
     * Send help message to a session.
     */
    private fun sendHelpMessage(session: WebSocketSession) {
        val helpText = """
            Available commands:
            /users - Show active users
            /help - Show this help message
            
            Just type your message to chat with everyone!
        """.trimIndent()
        
        sendMessageToSession(session, ChatMessage(
            username = "System",
            message = helpText,
            timestamp = System.currentTimeMillis()
        ))
    }
    
    /**
     * Send error message to a session.
     */
    private fun sendErrorMessage(session: WebSocketSession, error: String) {
        sendMessageToSession(session, ChatMessage(
            username = "System",
            message = "Error: $error",
            timestamp = System.currentTimeMillis()
        ))
    }
    
    /**
     * Extract username from WebSocket URI.
     */
    private fun extractUsernameFromUri(uri: String): String? {
        // URI format: /chat/{username}
        val parts = uri.split("/")
        return if (parts.size >= 3) {
            parts.last().takeIf { it.isNotBlank() }
        } else null
    }
}

/**
 * Data class representing a chat message.
 */
data class ChatMessage(
    val username: String,
    val message: String,
    val timestamp: Long
)