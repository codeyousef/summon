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
import code.yousef.summon.runtime.LocalPlatformRenderer
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
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
 * Chat page resource using Summon components.
 */
@Path("/chat-summon")
class ChatResourceSummon {

    @Inject
    lateinit var summonRenderer: SummonRenderer

    @Inject
    lateinit var chatService: ChatService

    /**
     * Render the chat page using Summon components.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun chat(): String {
        return summonRenderer.render(title = "Real-time Chat - Summon") {
            AppRoot {
                ChatInterface()
            }
        }
    }

    /**
     * API endpoint to get all messages
     */
    @GET
    @Path("/messages")
    @Produces(MediaType.TEXT_HTML)
    fun getMessages(): String {
        return summonRenderer.render {
            MessagesComponent(chatService.messages)
        }
    }

    /**
     * API endpoint to get active users
     */
    @GET
    @Path("/users")
    @Produces(MediaType.TEXT_HTML)
    fun getUsers(): String {
        return summonRenderer.render {
            UsersListComponent(chatService.getActiveUsers())
        }
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
        return summonRenderer.render {
            MessagesComponent(chatService.messages)
        }
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
        return summonRenderer.render {
            ChatContainer(username)
        }
    }
}

/**
 * Main chat interface component
 */
@Composable
fun ChatInterface() {
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

        // Login Form
        Box(
            modifier = Modifier()
                .attribute("id", "login-form-container")
        ) {
            LoginForm()
        }
    }
}

/**
 * Login form component
 */
@Composable
fun LoginForm() {
    Card(
        modifier = Modifier()
            .margin("0 0 1rem 0")
    ) {
        Column(
            modifier = Modifier()
                .padding("1rem")
                .attribute("style", "gap: 1rem;")
        ) {
            form(
                modifier = Modifier()
                    .attribute("id", "login-form")
                    .attribute("hx-post", "/chat-summon/join")
                    .attribute("hx-target", "#login-form-container")
                    .attribute("hx-swap", "outerHTML")
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = "Enter your username",
                    modifier = Modifier()
                        .attribute("id", "username-input")
                        .attribute("name", "username")
                        .width("100%")
                        .attribute("required", "true")
                )

                Button(
                    label = "Join Chat",
                    onClick = {},
                    modifier = Modifier()
                        .attribute("type", "submit")
                        .margin("0.5rem 0 0 0")
                )
            }
        }
    }
}

/**
 * Chat container component
 */
@Composable
fun ChatContainer(username: String) {
    Box(
        modifier = Modifier()
            .attribute("id", "chat-container")
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
                            .attribute("style", "overflow: auto; border-bottom: 1px solid #eee;")
                            .padding("1rem")
                            .attribute("hx-get", "/chat-summon/messages")
                            .attribute("hx-trigger", "load, every 2s")
                    ) {
                        // Initial empty state - messages will load via HTMX
                    }

                    // Message Input Area
                    form(
                        modifier = Modifier()
                            .attribute("style", "display: flex; padding: 10px; gap: 10px;")
                            .attribute("hx-post", "/chat-summon/send")
                            .attribute("hx-target", "#chat-messages")
                            .attribute("hx-swap", "innerHTML")
                    ) {
                        // Hidden username field
                        TextField(
                            value = username,
                            onValueChange = {},
                            modifier = Modifier()
                                .attribute("name", "username")
                                .attribute("type", "hidden")
                        )

                        TextField(
                            value = "",
                            onValueChange = {},
                            placeholder = "Type your message...",
                            modifier = Modifier()
                                .attribute("name", "message")
                                .flex("1")
                                .attribute("required", "true")
                        )

                        Button(
                            label = "Send",
                            onClick = {},
                            modifier = Modifier()
                                .attribute("type", "submit")
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
                            .attribute("style", "list-style-type: none; padding: 0;")
                            .attribute("hx-get", "/chat-summon/users")
                            .attribute("hx-trigger", "load, every 3s")
                    ) {
                        // Initial empty state - users will load via HTMX
                    }
                }
            }
        }
    }
}

/**
 * Messages component
 */
@Composable
fun MessagesComponent(messages: List<ChatMessage>) {
    Column(
        modifier = Modifier()
            .width("100%")
    ) {
        if (messages.isEmpty()) {
            Text(
                text = "No messages yet. Start the conversation!",
                modifier = Modifier()
                    .attribute("style", "font-style: italic;")
                    .color("#888")
                    .textAlign("center")
                    .padding("1rem")
            )
        } else {
            messages.forEach { message ->
                MessageBubble(message)
            }
        }
    }
}

/**
 * Single message bubble
 */
@Composable
fun MessageBubble(message: ChatMessage) {
    val isSystem = message.username == "System"

    Box(
        modifier = if (isSystem) {
            Modifier()
                .padding("5px 10px")
                .margin("5px 0")
                .attribute("style", "font-style: italic;")
                .color("#888")
                .textAlign("center")
        } else {
            Modifier()
                .padding("8px 12px")
                .margin("5px 0")
                .borderRadius("5px")
                .backgroundColor("#f1f1f1")
        }
    ) {
        if (isSystem) {
            Text(text = message.content)
        } else {
            Column {
                Text(
                    text = message.username,
                    modifier = Modifier()
                        .fontWeight("bold")
                        .fontSize("0.9rem")
                        .color("#555")
                )
                Text(text = message.content)
                Text(
                    text = message.timestamp,
                    modifier = Modifier()
                        .fontSize("0.7rem")
                        .color("#888")
                        .textAlign("right")
                )
            }
        }
    }
}

/**
 * Users list component
 */
@Composable
fun UsersListComponent(users: List<String>) {
    Column {
        if (users.isEmpty()) {
            Text(
                text = "No active users",
                modifier = Modifier()
                    .attribute("style", "font-style: italic;")
                    .color("#888")
            )
        } else {
            users.forEach { username ->
                Box(
                    modifier = Modifier()
                        .padding("5px 0")
                        .attribute("style", "border-bottom: 1px solid #eee;")
                ) {
                    Text(
                        text = username,
                        modifier = Modifier()
                            .color("var(--primary-color)")
                    )
                }
            }
        }
    }
}

/**
 * Form component (using Box with a 'form' tag modifier)
 */
@Composable
fun form(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier,
        content = content
    )
} 