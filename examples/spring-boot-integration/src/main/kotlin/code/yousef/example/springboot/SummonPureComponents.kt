package code.yousef.example.springboot

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.input.TextArea
import code.yousef.summon.components.layout.*
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.*
import code.yousef.summon.state.MutableState
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.extensions.*

/**
 * Simple chat component without WebSocket (server will handle that)
 */
@Composable
fun SummonChatComponent() {
    val username: MutableState<String> = remember { mutableStateOf("") }
    val currentMessage: MutableState<String> = remember { mutableStateOf("") }
    val messages: MutableState<List<ChatDisplayMessage>> = remember { mutableStateOf(emptyList()) }
    val isConnected: MutableState<Boolean> = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier()
            .maxWidth(800.px)
            .margin("0 auto")
            .padding(20.px)
    ) {
        // Connection section
        if (!isConnected.value) {
            Div(
                modifier = Modifier()
                    .marginBottom(20.px)
                    .padding(20.px)
                    .backgroundColor("white")
                    .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
                    .borderRadius(8.px)
            ) {
                Text(
                    "Join Chat",
                    modifier = Modifier()
                        .fontSize(24.px)
                        .fontWeight(FontWeight.Bold)
                        .marginBottom(16.px)
                )
                
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    placeholder = "Enter your username",
                    modifier = Modifier()
                        .width(300.px)
                        .marginBottom(16.px)
                )
                
                Button(
                    onClick = {
                        if (username.value.trim().isNotEmpty()) {
                            isConnected.value = true
                        }
                    },
                    label = "Connect"
                )
            }
        } else {
            // Chat interface
            Div(
                modifier = Modifier()
                    .marginBottom(20.px)
                    .backgroundColor("white")
                    .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
                    .borderRadius(8.px)
            ) {
                // Header
                Row(
                    modifier = Modifier()
                        .justifyContent("space-between")
                        .alignItems("center")
                        .padding(16.px)
                        .borderBottom("1px", "solid", "#e0e0e0")
                ) {
                    Text(
                        "Chat Room - ${username.value}",
                        modifier = Modifier()
                            .fontSize(18.px)
                            .fontWeight(FontWeight.Bold)
                    )
                    
                    Button(
                        onClick = {
                            isConnected.value = false
                            messages.value = emptyList()
                            username.value = ""
                        },
                        label = "Disconnect"
                    )
                }
                
                // Messages area
                Box(
                    modifier = Modifier()
                        .height(400.px)
                        .overflowY("auto")
                        .padding(16.px)
                        .backgroundColor("#f8f9fa")
                ) {
                    if (messages.value.isEmpty()) {
                        Box(
                            modifier = Modifier()
                                .fillMaxWidth()
                                .height(100.percent)
                                .attribute("style", "display: flex")
                                .alignItems("center")
                                .justifyContent("center")
                        ) {
                            Text(
                                "Welcome to the chat! Messages will appear here.",
                                modifier = Modifier()
                                    .color("#6c757d")
                                    .textAlign("center")
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier().gap(8.px)
                        ) {
                            messages.value.forEach { message ->
                                ChatMessageBubble(message)
                            }
                        }
                    }
                }
                
                // Message input
                Row(
                    modifier = Modifier()
                        .padding(16.px)
                        .gap(12.px)
                        .borderTop("1px", "solid", "#e0e0e0")
                ) {
                    TextField(
                        value = currentMessage.value,
                        onValueChange = { currentMessage.value = it },
                        placeholder = "Type your message...",
                        modifier = Modifier().flex(1)
                    )
                    
                    Button(
                        onClick = {
                            if (currentMessage.value.trim().isNotEmpty()) {
                                // Add message locally for demo
                                messages.value = messages.value + ChatDisplayMessage(
                                    username = username.value,
                                    message = currentMessage.value,
                                    timestamp = 0L,
                                    isOwn = true
                                )
                                currentMessage.value = ""
                            }
                        },
                        label = "Send"
                    )
                }
            }
        }
    }
}

/**
 * Chat message bubble component
 */
@Composable
fun ChatMessageBubble(message: ChatDisplayMessage) {
    Row(
        modifier = Modifier()
            .fillMaxWidth()
            .justifyContent(if (message.isOwn) "flex-end" else "flex-start")
    ) {
        Box(
            modifier = Modifier()
                .maxWidth(70.percent)
                .backgroundColor(if (message.isOwn) "#007bff" else "#ffffff")
                .color(if (message.isOwn) "#ffffff" else "#333333")
                .padding(12.px, 16.px)
                .borderRadius(12.px)
                .boxShadow("0 1px 3px rgba(0,0,0,0.1)")
                .marginBottom(4.px)
        ) {
            Column {
                if (!message.isOwn) {
                    Text(
                        message.username,
                        modifier = Modifier()
                            .fontSize(12.px)
                            .fontWeight(FontWeight.Bold)
                            .color("#666666")
                            .marginBottom(4.px)
                    )
                }
                
                Text(
                    message.message,
                    modifier = Modifier()
                        .fontSize(14.px)
                        .lineHeight(1.4)
                )
            }
        }
    }
}

/**
 * Simple server time component
 */
@Composable
fun SummonServerTimeComponent() {
    val currentTime: MutableState<String> = remember { mutableStateOf("Loading...") }

    Div(
        modifier = Modifier()
            .textAlign("center")
            .margin(20.px, 0.px)
            .padding(20.px)
            .backgroundColor("white")
            .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
            .borderRadius(8.px)
    ) {
        Text(
            "🕒 Current Server Time",
            modifier = Modifier()
                .fontSize(20.px)
                .fontWeight(FontWeight.Bold)
                .marginBottom(12.px)
        )
        
        Text(
            currentTime.value,
            modifier = Modifier()
                .fontSize(18.px)
                .color("#007bff")
        )
    }
}

/**
 * User management component
 */
@Composable
fun SummonUserManagementComponent(initialUsers: List<User>) {
    val users: MutableState<List<User>> = remember { mutableStateOf(initialUsers) }

    Column {
        // Header
        Row(
            modifier = Modifier()
                .justifyContent("space-between")
                .alignItems("center")
                .marginBottom(20.px)
        ) {
            Text(
                "👥 User Management",
                modifier = Modifier()
                    .fontSize(24.px)
                    .fontWeight(FontWeight.Bold)
            )
        }

        // Users table
        Div(
            modifier = Modifier()
                .backgroundColor("white")
                .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
                .borderRadius(8.px)
        ) {
            if (users.value.isEmpty()) {
                Box(
                    modifier = Modifier()
                        .padding(40.px)
                        .textAlign("center")
                ) {
                    Text(
                        "No users found",
                        modifier = Modifier().color("#6c757d")
                    )
                }
            } else {
                Column {
                    // Table header
                    Row(
                        modifier = Modifier()
                            .backgroundColor("#f8f9fa")
                            .padding(12.px, 16.px)
                            .fontWeight(FontWeight.Bold)
                            .borderBottom("1px", "solid", "#dee2e6")
                    ) {
                        Text("ID", modifier = Modifier().flex(1))
                        Text("Name", modifier = Modifier().flex(2))
                        Text("Email", modifier = Modifier().flex(2))
                        Text("Role", modifier = Modifier().flex(1))
                        Text("Status", modifier = Modifier().flex(1))
                        Text("Actions", modifier = Modifier().flex(2))
                    }
                    
                    // Table rows
                    users.value.forEach { user ->
                        Row(
                            modifier = Modifier()
                                .padding(12.px, 16.px)
                                .borderBottom("1px", "solid", "#f0f0f0")
                                .alignItems("center")
                        ) {
                            Text(user.id.toString(), modifier = Modifier().flex(1))
                            Text(user.name, modifier = Modifier().flex(2))
                            Text(user.email, modifier = Modifier().flex(2))
                            Text(user.role, modifier = Modifier().flex(1))
                            Text(
                                if (user.active) "Active" else "Inactive",
                                modifier = Modifier()
                                    .flex(1)
                                    .color(if (user.active) "#28a745" else "#dc3545")
                            )
                            
                            // Actions
                            Row(
                                modifier = Modifier()
                                    .flex(2)
                                    .gap(8.px)
                            ) {
                                Button(
                                    onClick = {
                                        // Navigate to edit page
                                        // Using window.location for navigation since we're in a browser context
                                    },
                                    label = "Edit",
                                    modifier = Modifier()
                                        .padding("0.5rem 1rem")
                                        .backgroundColor("#007bff")
                                        .color("white")
                                        .borderRadius(4.px)
                                        .cursor("pointer")
                                        .attribute("onclick", "window.location.href='/users/${user.id}/edit'")
                                        .hover { backgroundColor("#0056b3") }
                                )
                                
                                Div(
                                    modifier = Modifier()
                                        .padding("0.5rem 1rem")
                                        .backgroundColor("#dc3545")
                                        .color("white")
                                        .borderRadius(4.px)
                                        .cursor("pointer")
                                        .textAlign("center")
                                        .attribute("onclick", "if(confirm('Are you sure you want to delete user ${user.name}?')) { var form = document.createElement('form'); form.method='POST'; form.action='/users/${user.id}/delete'; document.body.appendChild(form); form.submit(); }")
                                        .hover { backgroundColor("#c82333") }
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Data class for chat display messages
 */
data class ChatDisplayMessage(
    val username: String,
    val message: String,
    val timestamp: Long,
    val isOwn: Boolean
)