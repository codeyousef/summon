package code.yousef.example.quarkus

import jakarta.enterprise.context.ApplicationScoped
import jakarta.websocket.*
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import org.jboss.logging.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Real-time chat implementation using WebSockets.
 */
@ServerEndpoint("/chat/{username}")
@ApplicationScoped
class ChatSocket {
    private val logger = Logger.getLogger(ChatSocket::class.java)
    private val sessions = ConcurrentHashMap<String, Session>()
    private val users = CopyOnWriteArrayList<String>()

    init {
        logger.info("=================== ChatSocket initialized ===================")
        println("=================== ChatSocket initialized ===================")
    }

    @OnOpen
    fun onOpen(session: Session, @PathParam("username") username: String) {
        logger.info("Chat session opened for: $username, session ID: ${session.id}")
        println("WEBSOCKET OPEN: Chat session opened for: $username, session ID: ${session.id}")

        // Store session and username
        sessions[username] = session
        users.add(username)

        // Print active sessions for debugging
        logger.info("WEBSOCKET ACTIVE SESSIONS: ${sessions.size} - Users: ${users.joinToString()}")
        println("WEBSOCKET ACTIVE SESSIONS: ${sessions.size} - Users: ${users.joinToString()}")

        // Broadcast user joined message
        try {
            broadcast("Server", "$username joined the chat")
        } catch (e: Exception) {
            logger.error("Error broadcasting join message for $username", e)
            println("WEBSOCKET ERROR broadcasting join: ${e.message}")
            e.printStackTrace()
        }

        // Send active users list
        try {
            val message = formatMessage("Server", "Active users: ${users.joinToString(", ")}")
            logger.info("Sending active users message to $username: $message")
            session.asyncRemote.sendText(message)
            println("WEBSOCKET: Active users message sent to $username")
        } catch (e: Exception) {
            logger.error("Error sending initial message to $username", e)
            println("WEBSOCKET ERROR sending message: ${e.message}")
            e.printStackTrace()
        }
    }

    @OnClose
    fun onClose(session: Session, @PathParam("username") username: String) {
        logger.info("Chat session closed for: $username, session ID: ${session.id}")
        println("WEBSOCKET CLOSE: Chat session closed for: $username, session ID: ${session.id}")

        sessions.remove(username)
        users.remove(username)

        // Broadcast user left message
        broadcast("Server", "$username left the chat")
    }

    @OnError
    fun onError(session: Session, @PathParam("username") username: String, throwable: Throwable) {
        logger.error("Chat error for user $username", throwable)
        println("WEBSOCKET ERROR for user $username: ${throwable.message}")
        println("WEBSOCKET ERROR stack trace: ${throwable.stackTraceToString()}")

        sessions.remove(username)
        users.remove(username)

        // Broadcast error message
        broadcast("Server", "$username left the chat due to an error")
    }

    @OnMessage
    fun onMessage(message: String, @PathParam("username") username: String) {
        logger.info("Received message from $username: $message")
        println("WEBSOCKET MESSAGE: Received from $username: $message")

        // Broadcast the message to all users
        broadcast(username, message)
    }

    private fun broadcast(username: String, message: String) {
        val formattedMessage = formatMessage(username, message)
        logger.info("WEBSOCKET BROADCAST: From $username to ${sessions.size} users, message: $message")
        println("WEBSOCKET BROADCAST: From $username to ${sessions.size} users, message: $message")

        sessions.values.forEach { session ->
            try {
                session.asyncRemote.sendText(formattedMessage)
            } catch (e: Exception) {
                logger.error("Error sending message to session", e)
                println("WEBSOCKET ERROR broadcasting: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun formatMessage(username: String, message: String): String {
        val timestamp = System.currentTimeMillis()
        logger.debug("WEBSOCKET FORMAT: Formatting message from $username at $timestamp")

        return """
            {
                "username": "$username",
                "message": "$message",
                "timestamp": "$timestamp"
            }
        """.trimIndent()
    }
} 