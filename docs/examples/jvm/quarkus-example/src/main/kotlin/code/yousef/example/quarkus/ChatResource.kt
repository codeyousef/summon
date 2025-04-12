package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.Composable
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.websocket.*
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
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

    @OnOpen
    fun onOpen(session: Session, @PathParam("username") username: String) {
        logger.info("Chat session opened for: $username")
        
        sessions[username] = session
        users.add(username)
        
        // Broadcast user joined message
        broadcast("Server", "$username joined the chat")
        
        // Send active users list
        session.asyncRemote.sendText(formatMessage("Server", "Active users: ${users.joinToString(", ")}"))
    }

    @OnClose
    fun onClose(session: Session, @PathParam("username") username: String) {
        logger.info("Chat session closed for: $username")
        
        sessions.remove(username)
        users.remove(username)
        
        // Broadcast user left message
        broadcast("Server", "$username left the chat")
    }

    @OnError
    fun onError(session: Session, @PathParam("username") username: String, throwable: Throwable) {
        logger.error("Chat error for user $username", throwable)
        
        sessions.remove(username)
        users.remove(username)
        
        // Broadcast error message
        broadcast("Server", "$username left the chat due to an error")
    }

    @OnMessage
    fun onMessage(message: String, @PathParam("username") username: String) {
        logger.info("Received message from $username: $message")
        
        // Broadcast the message to all users
        broadcast(username, message)
    }

    private fun broadcast(username: String, message: String) {
        val formattedMessage = formatMessage(username, message)
        sessions.values.forEach { session ->
            session.asyncRemote.sendText(formattedMessage)
        }
    }

    private fun formatMessage(username: String, message: String): String {
        return """
            {
                "username": "$username",
                "message": "$message",
                "timestamp": "${System.currentTimeMillis()}"
            }
        """.trimIndent()
    }
}

/**
 * Chat page resource.
 */
@Path("/chat")
class ChatResource {
    
    @Inject
    lateinit var summonRenderer: SummonRenderer
    
    /**
     * Render the chat page.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun chat(): String {
        // Pass includeChatScript = true to the renderer
        return summonRenderer.render(title = "Real-time Chat", includeChatScript = true) {
            AppRoot {
                ChatMainView()
            }
        }
    }
}

/**
 * Main chat view component
 */
@Composable
fun ChatMainView() {
    Column(
        modifier = Modifier()
            .padding("1rem")
            .width("100%")
    ) {
        Text(
            text = "Real-time Chat",
            modifier = Modifier()
                .fontSize("1.5rem")
                .fontWeight("bold")
                .margin("0 0 1rem 0")
        )
        
        // Login card
        Card(
            modifier = Modifier()
                .margin("0 0 1rem 0")
                .attribute("id", "login-section")
        ) {
            Column(
                modifier = Modifier()
                    .padding("1rem")
                    .attribute("style", "gap: 1rem;")
            ) {
                Box(
                    modifier = Modifier()
                        .attribute("class", "form-group")
                ) {
                    Text(
                        text = "Username",
                        modifier = Modifier()
                            .attribute("style", "display: block;")
                            .margin("0 0 0.5rem 0")
                    )
                    
                    TextField(
                        value = "",
                        onValueChange = {},
                        placeholder = "Enter your username",
                        modifier = Modifier()
                            .attribute("id", "username-input")
                            .width("100%")
                            .attribute("required", "true")
                    )
                }
                
                Button(
                    label = "Join Chat",
                    onClick = {},
                    modifier = Modifier()
                        .attribute("id", "join-chat-btn")
                        .attribute("class", "btn")
                )
            }
        }
        
        // Chat container (initially hidden)
        Box(
            modifier = Modifier()
                .attribute("id", "chat-container")
                .attribute("style", "display: none;")
                .width("100%")
        ) {
            Column(
                modifier = Modifier()
                    .width("100%")
                    .attribute("style", "gap: 1rem;")
            ) {
                // Messages Container
                Card(
                    modifier = Modifier()
                        .height("400px")
                        .margin("0 0 1rem 0")
                        .width("100%")
                ) {
                    Column(
                        modifier = Modifier()
                            .width("100%")
                            .height("100%")
                    ) {
                        // Messages Area
                        Box(
                            modifier = Modifier()
                                .attribute("id", "chat-messages")
                                .height("350px")
                                .attribute("style", "overflow: auto;")
                                .padding("1rem")
                                .attribute("style", "border-bottom: 1px solid #eee;")
                        ) {
                            // Messages will appear here
                        }
                        
                        // Message Input Area
                        Row(
                            modifier = Modifier()
                                .padding("10px")
                                .attribute("style", "display: flex; gap: 10px;")
                                .width("100%")
                        ) {
                            TextField(
                                value = "",
                                onValueChange = {},
                                placeholder = "Type your message...",
                                modifier = Modifier()
                                    .attribute("id", "message-input")
                                    .flex("1")
                            )
                            
                            Button(
                                label = "Send",
                                onClick = {},
                                modifier = Modifier()
                                    .attribute("id", "send-btn")
                                    .attribute("class", "btn")
                            )
                        }
                    }
                }
                
                // Active Users Card
                Card(
                    modifier = Modifier()
                        .width("100%")
                ) {
                    Column(
                        modifier = Modifier()
                            .padding("1rem")
                    ) {
                        Text(
                            text = "Active Users",
                            modifier = Modifier()
                                .fontSize("1.2rem")
                                .fontWeight("bold")
                                .margin("0 0 0.5rem 0")
                        )
                        
                        Box(
                            modifier = Modifier()
                                .attribute("id", "users-list")
                                .attribute("style", "list-style-type: none;")
                                .padding("0")
                        ) {
                            // Active users will appear here
                        }
                    }
                }
            }
        }
    }
} 