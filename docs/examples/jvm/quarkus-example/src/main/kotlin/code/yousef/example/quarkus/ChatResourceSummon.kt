package code.yousef.example.quarkus

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.Logger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

// Message data class
data class ChatMessage(
    val username: String,
    val content: String,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
)

// In-memory storage for the chat app
@ApplicationScoped
class ChatService {
    val messages = CopyOnWriteArrayList<ChatMessage>()
    val activeUsers = ConcurrentHashMap<String, Boolean>()

    fun addUser(username: String) {
        if (!activeUsers.containsKey(username)) {
            activeUsers[username] = true
            addSystemMessage("$username joined the chat")
        }
    }

    fun removeUser(username: String) {
        if (activeUsers.containsKey(username)) {
            activeUsers.remove(username)
            addSystemMessage("$username left the chat")
        }
    }

    fun addMessage(username: String, content: String) {
        messages.add(ChatMessage(username, content))
        // Limit to last 50 messages to avoid memory issues
        if (messages.size > 50) {
            messages.removeAt(0)
        }
    }

    private fun addSystemMessage(content: String) {
        addMessage("System", content)
    }

    fun getActiveUsers(): List<String> {
        return activeUsers.keys.toList()
    }
}

/**
 * Chat page resource using HTML generation.
 */
@Path("/chat-summon")
class ChatResourceSummon {
    private val logger = Logger.getLogger(ChatResourceSummon::class.java)

    @Inject
    lateinit var summonRenderer: SummonRenderer

    @Inject
    lateinit var chatService: ChatService

    /**
     * Render the chat page using direct HTML.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun chat(): String {
        logger.info("============= ChatResourceSummon.chat() START - rendering chat-summon page =============")

        try {
            // Direct HTML implementation
            return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Real-time Chat</title>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        :root {
                            --primary-color: #4695EB;
                            --secondary-color: #FF4081;
                            --text-color: #333333;
                            --bg-color: #FFFFFF;
                            --card-bg-color: #FFFFFF;
                            --border-color: #DDDDDD;
                        }
                        body { 
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            line-height: 1.6;
                            padding: 2rem;
                            max-width: 1200px;
                            margin: 0 auto;
                            color: var(--text-color);
                            background-color: var(--bg-color);
                        }
                        h1, h2, h3 { color: var(--primary-color); }
                        .card {
                            border: 1px solid var(--border-color);
                            border-radius: 8px;
                            padding: 1.5rem;
                            margin-bottom: 1.5rem;
                            background-color: var(--card-bg-color);
                            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                        }
                        .nav {
                            background-color: var(--primary-color);
                            color: white;
                            padding: 1rem;
                            margin-bottom: 2rem;
                            border-radius: 8px;
                        }
                        .nav-list {
                            display: flex;
                            list-style-type: none;
                            margin: 0;
                            padding: 0;
                        }
                        .nav-item {
                            margin-right: 1.5rem;
                        }
                        .nav-link {
                            color: white;
                            text-decoration: none;
                            font-weight: 500;
                        }
                        .form-group {
                            margin-bottom: 1rem;
                        }
                        input, textarea {
                            width: 100%;
                            padding: 0.75rem;
                            border: 1px solid #ddd;
                            border-radius: 4px;
                            box-sizing: border-box;
                            font-family: inherit;
                            font-size: 1rem;
                        }
                        .btn {
                            display: inline-block;
                            background-color: var(--primary-color);
                            color: white;
                            border: none;
                            border-radius: 4px;
                            padding: 10px 16px;
                            cursor: pointer;
                            font-size: 1rem;
                        }
                        #chat-messages {
                            height: 350px;
                            overflow-y: auto;
                            padding: 1rem;
                            border: 1px solid #ddd;
                            border-radius: 4px;
                            background-color: #f9f9f9;
                            margin-bottom: 1rem;
                        }
                        .message {
                            padding: 8px 12px;
                            margin: 5px 0;
                            border-radius: 5px;
                        }
                        .incoming {
                            background-color: #f1f1f1;
                            align-self: flex-start;
                        }
                        .outgoing {
                            background-color: #e3f2fd;
                            align-self: flex-end;
                            text-align: right;
                        }
                        #chat-container {
                            display: none;
                        }
                        #users-list {
                            list-style-type: none;
                            padding: 0;
                        }
                        #users-list li {
                            padding: 5px 0;
                            border-bottom: 1px solid #eee;
                        }
                        .chat-layout {
                            display: grid;
                            grid-template-columns: 1fr 300px;
                            gap: 1rem;
                        }
                        @media (max-width: 768px) {
                            .chat-layout {
                                grid-template-columns: 1fr;
                            }
                            .nav-list {
                                flex-direction: column;
                            }
                            .nav-item {
                                margin-right: 0;
                                margin-bottom: 0.5rem;
                            }
                        }
                    </style>
                    <script src="https://unpkg.com/htmx.org@1.9.12"></script>
                </head>
                <body>
                    <!-- Navigation -->
                    <nav class="nav">
                        <ul class="nav-list">
                            <li class="nav-item"><a href="/" class="nav-link">Home</a></li>
                            <li class="nav-item"><a href="/users" class="nav-link">Users</a></li>
                            <li class="nav-item"><a href="/dashboard" class="nav-link">Dashboard</a></li>
                            <li class="nav-item"><a href="/theme" class="nav-link">Theme</a></li>
                            <li class="nav-item"><a href="/chat" class="nav-link">Chat</a></li>
                        </ul>
                    </nav>
                    
                    <h1>Real-time Chat</h1>
                    
                    <!-- Login Form -->
                    <div id="login-form-container">
                        <div class="card">
                            <form id="login-form" hx-post="/chat-summon/join" hx-target="#login-form-container" hx-swap="outerHTML">
                                <div class="form-group">
                                    <input type="text" id="username-input" name="username" placeholder="Enter your username" required>
                                </div>
                                <button type="submit" class="btn">Join Chat</button>
                            </form>
                        </div>
                    </div>
                    
                    <script>
                        document.addEventListener('DOMContentLoaded', function() {
                            // Any client-side JS initialization here
                            console.log("Chat page loaded");
                        });
                    </script>
                </body>
                </html>
            """.trimIndent()
            
        } catch (e: Exception) {
            logger.error("CRITICAL ERROR in ChatResourceSummon.chat(): ${e.message}", e)
            
            // Return a very simple error page
            return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Chat - Error Page</title>
                    <meta charset="UTF-8">
                </head>
                <body>
                    <h1 style="color: red;">Critical Error Rendering Chat Page</h1>
                    <p>There was a serious error rendering the chat page:</p>
                    <pre>${e.message}</pre>
                    <pre>${e.stackTraceToString()}</pre>
                    <p><a href="/">Return to Home</a></p>
                </body>
                </html>
            """.trimIndent()
        }
    }

    /**
     * API endpoint to get all messages
     */
    @GET
    @Path("/messages")
    @Produces(MediaType.TEXT_HTML)
    fun getMessages(): String {
        val messagesHtml = StringBuilder()
        
        if (chatService.messages.isEmpty()) {
            messagesHtml.append("""
                <div style="font-style: italic; color: #888; text-align: center; padding: 1rem;">
                    No messages yet. Start the conversation!
                </div>
            """.trimIndent())
        } else {
            for (message in chatService.messages) {
                val isSystem = message.username == "System"
                val messageStyle = if (isSystem) {
                    "padding: 5px 10px; margin: 5px 0; font-style: italic; color: #888; text-align: center;"
                } else {
                    "padding: 8px 12px; margin: 5px 0; border-radius: 5px; background-color: #f1f1f1;"
                }
                
                messagesHtml.append("""
                    <div style="${messageStyle}">
                """.trimIndent())
                
                if (isSystem) {
                    messagesHtml.append(message.content)
                } else {
                    messagesHtml.append("""
                        <div>
                            <strong style="font-weight: bold; font-size: 0.9rem; color: #555;">${message.username}</strong>
                            <div>${message.content}</div>
                            <div style="font-size: 0.7rem; color: #888; text-align: right;">${message.timestamp}</div>
                        </div>
                    """.trimIndent())
                }
                
                messagesHtml.append("</div>")
            }
        }
        
        return messagesHtml.toString()
    }

    /**
     * API endpoint to get active users
     */
    @GET
    @Path("/users")
    @Produces(MediaType.TEXT_HTML)
    fun getUsers(): String {
        val usersHtml = StringBuilder()
        
        if (chatService.activeUsers.isEmpty()) {
            usersHtml.append("""
                <div style="font-style: italic; color: #888;">No active users</div>
            """.trimIndent())
        } else {
            for (username in chatService.getActiveUsers()) {
                usersHtml.append("""
                    <div style="padding: 5px 0; border-bottom: 1px solid #eee;">
                        <span style="color: var(--primary-color);">${username}</span>
                    </div>
                """.trimIndent())
            }
        }
        
        return usersHtml.toString()
    }

    /**
     * API endpoint to add a message
     */
    @POST
    @Path("/send")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun sendMessage(
        @FormParam("username") username: String,
        @FormParam("message") message: String
    ): String {
        if (username.isNotBlank() && message.isNotBlank()) {
            chatService.addUser(username)
            chatService.addMessage(username, message)
        }
        return getMessages()
    }

    /**
     * API endpoint to join chat
     */
    @POST
    @Path("/join")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun joinChat(@FormParam("username") username: String): String {
        if (username.isNotBlank()) {
            chatService.addUser(username)
        }
        
        return """
            <div id="chat-container" style="width: 100%;">
                <div class="chat-layout">
                    <!-- Messages Container -->
                    <div class="card">
                        <div style="width: 100%; height: 100%;">
                            <!-- Messages Area -->
                            <div id="chat-messages" 
                                 style="height: 350px; overflow: auto; border-bottom: 1px solid #eee; padding: 1rem;"
                                 hx-get="/chat-summon/messages"
                                 hx-trigger="load, every 2s">
                                <!-- Initial empty state - messages will load via HTMX -->
                            </div>

                            <!-- Message Input Area -->
                            <form style="display: flex; padding: 10px; gap: 10px;"
                                  hx-post="/chat-summon/send"
                                  hx-target="#chat-messages"
                                  hx-swap="innerHTML">
                                <!-- Hidden username field -->
                                <input type="hidden" name="username" value="${username}">
                                
                                <input type="text" name="message" placeholder="Type your message..." 
                                       style="flex: 1;" required>
                                       
                                <button type="submit" class="btn">Send</button>
                            </form>
                        </div>
                    </div>
                    
                    <!-- Active Users Card -->
                    <div class="card">
                        <div style="padding: 1rem;">
                            <h3 style="font-size: 1.2rem; font-weight: bold; margin: 0 0 0.5rem 0;">Active Users</h3>
                            
                            <div id="users-list" 
                                 style="list-style-type: none; padding: 0;"
                                 hx-get="/chat-summon/users"
                                 hx-trigger="load, every 3s">
                                <!-- Initial empty state - users will load via HTMX -->
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <script>
                // Show the chat container
                document.getElementById('chat-container').style.display = 'block';
                
                // Store username for reference
                if (typeof window !== 'undefined') {
                    window.chatUsername = "${username}";
                }
            </script>
        """.trimIndent()
    }
} 