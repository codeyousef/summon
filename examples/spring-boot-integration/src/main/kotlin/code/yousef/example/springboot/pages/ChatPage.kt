package code.yousef.example.springboot.pages

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.*
import code.yousef.summon.modifier.*
import code.yousef.summon.modifier.FontWeight
import code.yousef.summon.modifier.AlignItems
import code.yousef.summon.modifier.JustifyContent
import code.yousef.summon.runtime.*
import code.yousef.summon.state.MutableState
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.extensions.*

/**
 * Chat page data classes
 */
data class ChatMessage(
    val id: Long = 0,
    val username: String,
    val message: String,
    val timestamp: Long,
    val isOwn: Boolean
)

/**
 * Themed Chat page component using proper Summon theming
 */
@Composable
fun ChatPage() {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    // Use ThemeProvider to provide theme context
    code.yousef.example.springboot.theme.AppThemeProvider(isDarkMode = theme.isDarkMode == true) {
        Column(
            modifier = Modifier()
                .maxWidth(1200.px)
                .margin("0 auto")
                .padding(2.rem)
                .style("min-height", "calc(100vh - 200px)")
                .style("background-color", theme.backgroundColor)
        ) {
            
            // Page Header
            ChatHeader()
            
            // Chat interface with WebSocket functionality
            ChatInterfaceWithWebSocket()
            
            // Include WebSocket JavaScript
            WebSocketScript()
        }
    }
}

@Composable
fun ChatInterfaceWithWebSocket() {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    // HTML structure that will be manipulated by JavaScript
    Div(modifier = Modifier().attribute("id", "chat-container")) {
        // Connection form
        Div(
            modifier = Modifier()
                .attribute("id", "connection-form")
                .maxWidth(400.px)
                .margin("0 auto")
                .padding(2.rem)
                .style("background-color", if (theme.isDarkMode == true) "rgba(30, 41, 59, 0.9)" else "rgba(255, 255, 255, 0.9)")
                .style("box-shadow", if (theme.isDarkMode == true) "0 4px 6px rgba(0, 0, 0, 0.3)" else "0 4px 6px rgba(0, 0, 0, 0.1)")
                .borderRadius(12.px)
        ) {
            Text(
                "Join Chat Room",
                modifier = Modifier()
                    .fontSize(1.5.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .style("color", theme.textColor)
                    .marginBottom(1.5.rem)
                    .textAlign("center")
                    .style("display", "block")
            )
            
            // Username input
            Div(
                modifier = Modifier()
                    .attribute("style", "margin-bottom: 1rem;")
            ) {
                Div(
                    modifier = Modifier()
                        .fillMaxWidth()
                        .padding(0.75.rem)
                        .fontSize(1.rem)
                        .borderRadius(8.px)
                        .style("border", "2px solid ${theme.borderColor}")
                        .style("background-color", theme.backgroundColor)
                        .style("color", theme.textColor)
                        .attribute("contenteditable", "true")
                        .attribute("id", "username-input")
                        .attribute("data-placeholder", "Enter your username")
                        .attribute("style", "min-height: 1.2em; outline: none;")
                ) {}
            }
            
            // Connect button
            Button(
                onClick = {},
                label = "Connect to Chat",
                modifier = Modifier()
                    .attribute("id", "connect-btn")
                    .padding("0.75rem 2rem")
                    .style("background-color", theme.primaryColor)
                    .color("white")
                    .borderRadius(8.px)
                    .cursor("pointer")
                    .fontSize(1.rem)
                    .fontWeight(FontWeight.SemiBold.value)
                    .style("display", "block")
                    .style("margin", "0 auto")
                    .style("width", "100%")
            )
            
            // Connection status
            Div(
                modifier = Modifier()
                    .attribute("id", "connection-status")
                    .marginTop(1.rem)
                    .textAlign("center")
                    .style("display", "none")
            ) {}
        }
        
        // Chat interface (initially hidden)
        Div(
            modifier = Modifier()
                .attribute("id", "chat-interface")
                .style("display", "none")
                .fillMaxWidth()
                .style("background-color", if (theme.isDarkMode == true) "rgba(30, 41, 59, 0.9)" else "rgba(255, 255, 255, 0.9)")
                .style("box-shadow", if (theme.isDarkMode == true) "0 4px 6px rgba(0, 0, 0, 0.3)" else "0 4px 6px rgba(0, 0, 0, 0.1)")
                .borderRadius(12.px)
                .style("overflow", "hidden")
        ) {
            // Chat header
            Div(
                modifier = Modifier()
                    .style("display", "flex")
                    .style("justify-content", "space-between")
                    .style("align-items", "center")
                    .padding(1.rem)
                    .style("border-bottom", "1px solid ${theme.borderColor}")
                    .style("background-color", if (theme.isDarkMode == true) "rgba(51, 65, 85, 0.5)" else "rgba(248, 250, 252, 0.8)")
            ) {
                Text(
                    "💬 Chat Room",
                    modifier = Modifier()
                        .attribute("id", "chat-header-username")
                        .fontSize(1.25.rem)
                        .fontWeight(FontWeight.Bold.value)
                        .style("color", theme.textColor)
                )
                
                Button(
                    onClick = {},
                    label = "Disconnect",
                    modifier = Modifier()
                        .attribute("id", "disconnect-btn")
                        .padding("0.5rem 1rem")
                        .backgroundColor("#dc3545")
                        .color("white")
                        .borderRadius(6.px)
                        .cursor("pointer")
                )
            }
            
            // Messages area
            Div(
                modifier = Modifier()
                    .attribute("id", "messages-area")
                    .height(400.px)
                    .overflowY("auto")
                    .padding(1.rem)
                    .style("background-color", if (theme.isDarkMode == true) "rgba(15, 23, 42, 0.5)" else "rgba(248, 250, 252, 0.5)")
            ) {
                Div(
                    modifier = Modifier()
                        .attribute("id", "messages-container")
                        .style("display", "flex")
                        .style("flex-direction", "column")
                        .style("gap", "12px")
                ) {
                    // Messages will be added here by JavaScript
                }
            }
            
            // Message input area
            Div(
                modifier = Modifier()
                    .style("display", "flex")
                    .padding(1.rem)
                    .style("gap", "12px")
                    .style("border-top", "1px solid ${theme.borderColor}")
                    .style("align-items", "center")
            ) {
                Div(
                    modifier = Modifier()
                        .style("flex", "1")
                        .padding(0.75.rem)
                        .fontSize(1.rem)
                        .borderRadius(8.px)
                        .style("border", "2px solid ${theme.borderColor}")
                        .style("background-color", theme.backgroundColor)
                        .style("color", theme.textColor)
                        .attribute("contenteditable", "true")
                        .attribute("id", "message-input")
                        .attribute("data-placeholder", "Type your message...")
                        .attribute("style", "min-height: 1.2em; outline: none;")
                ) {}
                
                Button(
                    onClick = {},
                    label = "Send",
                    modifier = Modifier()
                        .attribute("id", "send-btn")
                        .padding("0.75rem 1.5rem")
                        .style("background-color", theme.primaryColor)
                        .color("white")
                        .borderRadius(8.px)
                        .cursor("pointer")
                        .fontSize(1.rem)
                        .fontWeight(FontWeight.SemiBold.value)
                )
            }
        }
    }
}

@Composable 
fun WebSocketScript() {
    // JavaScript for WebSocket functionality - will be injected via script tag
    Div(
        modifier = Modifier()
            .attribute("id", "websocket-script-container")
            .attribute("style", "display: none;")
    ) {
        // Script content will be added via the HTML template
    }
}
fun ChatHeader() {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Div(
        modifier = Modifier()
            .marginBottom(2.rem)
    ) {
        Text(
            "💬 Chat Room",
            modifier = Modifier()
                .fontSize(2.rem)
                .fontWeight(FontWeight.Bold.value)
                .style("color", theme.textColor)
                .marginBottom(0.5.rem)
        )
        Text(
            "Connect and start chatting with other users in real-time",
            modifier = Modifier()
                .fontSize(1.rem)
                .style("color", theme.secondaryColor)
        )
    }
}

@Composable
fun ConnectionCard(
    username: MutableState<String>,
    onConnect: () -> Unit
) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Div(
        modifier = Modifier()
            .maxWidth(400.px)
            .margin("0 auto")
            .padding(2.rem)
            .style("background-color", if (theme.isDarkMode == true) "rgba(30, 41, 59, 0.9)" else "rgba(255, 255, 255, 0.9)")
            .style("box-shadow", if (theme.isDarkMode == true) "0 4px 6px rgba(0, 0, 0, 0.3)" else "0 4px 6px rgba(0, 0, 0, 0.1)")
            .borderRadius(12.px)
    ) {
        Column(
            modifier = Modifier()
                .alignItems(AlignItems.Center)
        ) {
            Text(
                "Join Chat Room",
                modifier = Modifier()
                    .fontSize(1.5.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .style("color", theme.textColor)
                    .marginBottom(1.5.rem)
            )
            
            TextField(
                value = username.value,
                onValueChange = { username.value = it },
                placeholder = "Enter your username",
                modifier = Modifier()
                    .fillMaxWidth()
                    .padding(0.75.rem)
                    .fontSize(1.rem)
                    .borderRadius(8.px)
                    .style("border", "2px solid ${theme.borderColor}")
                    .style("background-color", theme.backgroundColor)
                    .style("color", theme.textColor)
                    .marginBottom(1.rem)
            )
            
            Button(
                onClick = onConnect,
                label = "Connect to Chat",
                modifier = Modifier()
                    .padding("0.75rem 2rem")
                    .style("background-color", theme.primaryColor)
                    .color("white")
                    .borderRadius(8.px)
                    .cursor("pointer")
                    .fontSize(1.rem)
                    .fontWeight(FontWeight.SemiBold.value)
                    .hover(Modifier()
                        .style("background-color", if (theme.isDarkMode == true) "#3b82f6" else "#1565c0"))
            )
        }
    }
}

@Composable
fun ChatInterface(
    username: String,
    messages: List<ChatMessage>,
    currentMessage: MutableState<String>,
    onSendMessage: () -> Unit,
    onDisconnect: () -> Unit
) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Div(
        modifier = Modifier()
            .fillMaxWidth()
            .style("background-color", if (theme.isDarkMode == true) "rgba(30, 41, 59, 0.9)" else "rgba(255, 255, 255, 0.9)")
            .style("box-shadow", if (theme.isDarkMode == true) "0 4px 6px rgba(0, 0, 0, 0.3)" else "0 4px 6px rgba(0, 0, 0, 0.1)")
            .borderRadius(12.px)
            .style("overflow", "hidden")
    ) {
        Column {
            // Chat header
            Row(
                modifier = Modifier()
                    .justifyContent(JustifyContent.SpaceBetween)
                    .alignItems(AlignItems.Center)
                    .padding(1.rem)
                    .style("border-bottom", "1px solid ${theme.borderColor}")
                    .style("background-color", if (theme.isDarkMode == true) "rgba(51, 65, 85, 0.5)" else "rgba(248, 250, 252, 0.8)")
            ) {
                Text(
                    "💬 Chat Room - $username",
                    modifier = Modifier()
                        .fontSize(1.25.rem)
                        .fontWeight(FontWeight.Bold.value)
                        .style("color", theme.textColor)
                )
                
                Button(
                    onClick = onDisconnect,
                    label = "Disconnect",
                    modifier = Modifier()
                        .padding("0.5rem 1rem")
                        .backgroundColor("#dc3545")
                        .color("white")
                        .borderRadius(6.px)
                        .cursor("pointer")
                        .hover(Modifier().backgroundColor("#c82333"))
                )
            }
            
            // Messages area
            MessagesArea(messages = messages)
            
            // Message input
            MessageInput(
                currentMessage = currentMessage,
                onSendMessage = onSendMessage
            )
        }
    }
}

@Composable
fun MessagesArea(messages: List<ChatMessage>) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Div(
        modifier = Modifier()
            .height(400.px)
            .overflowY("auto")
            .padding(1.rem)
            .style("background-color", if (theme.isDarkMode == true) "rgba(15, 23, 42, 0.5)" else "rgba(248, 250, 252, 0.5)")
    ) {
        if (messages.isEmpty()) {
            Div(
                modifier = Modifier()
                    .fillMaxWidth()
                    .height(100.percent)
                    .style("display", "flex")
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            ) {
                Text(
                    "Welcome to the chat! Start a conversation by typing a message below.",
                    modifier = Modifier()
                        .style("color", theme.secondaryColor)
                        .textAlign("center")
                        .fontSize(1.1.rem)
                )
            }
        } else {
            Column(
                modifier = Modifier().style("gap", "12px")
            ) {
                messages.forEach { message ->
                    ChatMessageBubble(message)
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    currentMessage: MutableState<String>,
    onSendMessage: () -> Unit
) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Row(
        modifier = Modifier()
            .padding(1.rem)
            .style("gap", "12px")
            .style("border-top", "1px solid ${theme.borderColor}")
            .alignItems(AlignItems.Center)
    ) {
        TextField(
            value = currentMessage.value,
            onValueChange = { currentMessage.value = it },
            placeholder = "Type your message...",
            modifier = Modifier()
                .style("flex", "1")
                .padding(0.75.rem)
                .fontSize(1.rem)
                .borderRadius(8.px)
                .style("border", "2px solid ${theme.borderColor}")
                .style("background-color", theme.backgroundColor)
                .style("color", theme.textColor)
        )
        
        Button(
            onClick = onSendMessage,
            label = "Send",
            modifier = Modifier()
                .padding("0.75rem 1.5rem")
                .style("background-color", theme.primaryColor)
                .color("white")
                .borderRadius(8.px)
                .cursor("pointer")
                .fontSize(1.rem)
                .fontWeight(FontWeight.SemiBold.value)
                .hover(Modifier()
                    .style("background-color", if (theme.isDarkMode == true) "#3b82f6" else "#1565c0"))
        )
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Row(
        modifier = Modifier()
            .fillMaxWidth()
            .justifyContent(if (message.isOwn) JustifyContent.FlexEnd else JustifyContent.FlexStart)
    ) {
        Div(
            modifier = Modifier()
                .maxWidth(70.percent)
                .style("background-color", if (message.isOwn) theme.primaryColor else if (theme.isDarkMode == true) "rgba(51, 65, 85, 0.8)" else "#ffffff")
                .style("color", if (message.isOwn) "white" else theme.textColor)
                .padding(12.px, 16.px)
                .borderRadius(12.px)
                .style("box-shadow", if (theme.isDarkMode == true) "0 2px 4px rgba(0,0,0,0.3)" else "0 2px 4px rgba(0,0,0,0.1)")
                .marginBottom(4.px)
                .style("border", if (!message.isOwn && theme.isDarkMode == false) "1px solid ${theme.borderColor}" else "none")
        ) {
            Column {
                if (!message.isOwn) {
                    Text(
                        message.username,
                        modifier = Modifier()
                            .fontSize(12.px)
                            .fontWeight(FontWeight.Bold.value)
                            .style("color", theme.secondaryColor)
                            .marginBottom(4.px)
                    )
                }
                
                Text(
                    message.message,
                    modifier = Modifier()
                        .fontSize(14.px)
                        .style("line-height", "1.4")
                )
            }
        }
    }
}
