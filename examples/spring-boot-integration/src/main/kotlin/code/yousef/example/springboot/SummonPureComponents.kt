package code.yousef.example.springboot

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.feedback.*
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.input.TextArea
import code.yousef.summon.components.layout.*
import code.yousef.summon.effects.*
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.*
import code.yousef.summon.state.*
import code.yousef.summon.core.style.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pure Summon chat component replacing raw JavaScript WebSocket handling
 */
@Composable
fun SummonChatComponent() {
    val username = remember { mutableStateOf("") }
    val currentMessage = remember { mutableStateOf("") }
    val messages = remember { mutableStateOf<List<ChatDisplayMessage>>(emptyList()) }
    val connectionStatus = remember { mutableStateOf("Disconnected") }
    val isConnected = remember { mutableStateOf(false) }
    val webSocketClient = remember { mutableStateOf<WebSocketClient?>(null) }
    val toastManager = rememberToastManager()

    LaunchedEffect(Unit) {
        // Setup WebSocket configuration
        val wsProtocol = if (js("window.location.protocol") == "https:") "wss:" else "ws:"
        val wsUrl = "$wsProtocol//${js("window.location.host")}/chat"
        
        val config = WebSocketConfig(
            url = wsUrl,
            autoReconnect = true,
            reconnectDelay = 3000,
            maxReconnectAttempts = 5
        )
        
        val client = createWebSocket(config)
        webSocketClient.value = client
        
        client.onEvent { event ->
            when (event) {
                is WebSocketEvent.Connected -> {
                    connectionStatus.value = "Connected"
                    isConnected.value = true
                    toastManager.showSuccess("Connected to chat server")
                }
                is WebSocketEvent.Disconnected -> {
                    connectionStatus.value = "Disconnected"
                    isConnected.value = false
                    toastManager.showWarning("Disconnected from chat server")
                }
                is WebSocketEvent.Error -> {
                    connectionStatus.value = "Error"
                    isConnected.value = false
                    toastManager.showError("Connection error: ${event.error}")
                }
                is WebSocketEvent.Message -> {
                    try {
                        val chatMessage = JSON.parse<ChatMessage>(event.data)
                        val displayMessage = ChatDisplayMessage(
                            username = chatMessage.username,
                            message = chatMessage.message,
                            timestamp = chatMessage.timestamp,
                            isOwn = chatMessage.username == username.value
                        )
                        messages.value = messages.value + displayMessage
                    } catch (e: Exception) {
                        toastManager.showError("Failed to parse message")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier()
            .maxWidth("800px")
            .margin("0 auto")
            .padding("20px")
    ) {
        // Connection section
        if (!isConnected.value) {
            Card(
                modifier = Modifier()
                    .marginBottom("20px")
                    .padding("20px")
            ) {
                Text(
                    text = "Join Chat",
                    modifier = Modifier()
                        .fontSize("24px")
                        .fontWeight("600")
                        .marginBottom("16px")
                )
                
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    placeholder = "Enter your username",
                    modifier = Modifier()
                        .width("300px")
                        .marginBottom("16px")
                )
                
                Button(
                    onClick = {
                        if (username.value.trim().isNotEmpty()) {
                            webSocketClient.value?.connect(webSocketClient.value?.config?.url ?: "")
                        } else {
                            toastManager.showWarning("Please enter a username")
                        }
                    },
                    label = "Connect",
                    variant = ButtonVariant.PRIMARY,
                    enabled = username.value.trim().isNotEmpty()
                )
            }
        } else {
            // Chat interface
            Card(
                modifier = Modifier()
                    .marginBottom("20px")
            ) {
                // Header
                Row(
                    modifier = Modifier()
                        .justifyContent("space-between")
                        .alignItems("center")
                        .padding("16px")
                        .borderBottom("1px solid #e0e0e0")
                ) {
                    Text(
                        text = "Chat Room - ${username.value}",
                        modifier = Modifier().fontSize("18px").fontWeight("600")
                    )
                    
                    Row(
                        modifier = Modifier()
                            .alignItems("center")
                            .gap("12px")
                    ) {
                        Text(
                            text = "Status: $connectionStatus",
                            modifier = Modifier()
                                .fontSize("14px")
                                .color(if (isConnected.value) "#28a745" else "#dc3545")
                        )
                        
                        Button(
                            onClick = {
                                webSocketClient.value?.close()
                                isConnected.value = false
                                messages.value = emptyList()
                                username.value = ""
                            },
                            label = "Disconnect",
                            variant = ButtonVariant.SECONDARY
                        )
                    }
                }
                
                // Messages area
                Box(
                    modifier = Modifier()
                        .height("400px")
                        .overflowY("auto")
                        .padding("16px")
                        .backgroundColor("#f8f9fa")
                ) {
                    if (messages.value.isEmpty()) {
                        Box(
                            modifier = Modifier()
                                .width("100%")
                                .height("100%")
                                .displayFlex()
                                .alignItems("center")
                                .justifyContent("center")
                        ) {
                            Text(
                                text = "Welcome to the chat! Messages will appear here.",
                                modifier = Modifier()
                                    .color("#6c757d")
                                    .textAlign("center")
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier().gap("8px")
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
                        .padding("16px")
                        .gap("12px")
                        .borderTop("1px solid #e0e0e0")
                ) {
                    TextField(
                        value = currentMessage.value,
                        onValueChange = { currentMessage.value = it },
                        placeholder = "Type your message...",
                        modifier = Modifier().flex("1"),
                        onKeyPress = { key ->
                            if (key == "Enter" && currentMessage.value.trim().isNotEmpty()) {
                                webSocketClient.value?.send(currentMessage.value)
                                currentMessage.value = ""
                            }
                        }
                    )
                    
                    Button(
                        onClick = {
                            if (currentMessage.value.trim().isNotEmpty()) {
                                webSocketClient.value?.send(currentMessage.value)
                                currentMessage.value = ""
                            }
                        },
                        label = "Send",
                        variant = ButtonVariant.PRIMARY,
                        enabled = currentMessage.value.trim().isNotEmpty()
                    )
                }
            }
        }
        
        // Toast container
        ToastProvider(position = ToastPosition.TOP_RIGHT) { manager ->
            // Toast manager is provided by context
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
            .width("100%")
            .justifyContent(if (message.isOwn) "flex-end" else "flex-start")
    ) {
        Box(
            modifier = Modifier()
                .maxWidth("70%")
                .backgroundColor(if (message.isOwn) "#007bff" else "#ffffff")
                .color(if (message.isOwn) "#ffffff" else "#333333")
                .padding("12px 16px")
                .borderRadius("12px")
                .boxShadow("0 1px 3px rgba(0,0,0,0.1)")
                .marginBottom("4px")
        ) {
            Column {
                if (!message.isOwn) {
                    Text(
                        text = message.username,
                        modifier = Modifier()
                            .fontSize("12px")
                            .fontWeight("600")
                            .color("#666666")
                            .marginBottom("4px")
                    )
                }
                
                Text(
                    text = message.message,
                    modifier = Modifier().fontSize("14px").lineHeight("1.4")
                )
            }
        }
    }
}

/**
 * Real-time server time component using Summon HTTP client
 */
@Composable
fun SummonServerTimeComponent() {
    val currentTime = remember { mutableStateOf("Loading...") }
    val httpClient = remember { createHttpClient() }
    val isUpdating = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                isUpdating.value = true
                val response = httpClient.get("/api/time")
                currentTime.value = response.body
                isUpdating.value = false
                delay(1000) // Update every second
            } catch (e: Exception) {
                currentTime.value = "Error loading time"
                isUpdating.value = false
                delay(5000) // Retry after 5 seconds on error
            }
        }
    }

    Card(
        modifier = Modifier()
            .textAlign("center")
            .margin("20px 0")
    ) {
        Text(
            text = "🕒 Current Server Time",
            modifier = Modifier()
                .fontSize("20px")
                .fontWeight("600")
                .marginBottom("12px")
        )
        
        Row(
            modifier = Modifier()
                .justifyContent("center")
                .alignItems("center")
                .gap("8px")
        ) {
            Text(
                text = currentTime.value,
                modifier = Modifier()
                    .fontSize("18px")
                    .color("#007bff")
                    .transition("opacity 0.3s")
                    .opacity(if (isUpdating.value) "0.7" else "1.0")
            )
            
            if (isUpdating.value) {
                InlineLoading(size = LoadingSize.SMALL)
            }
        }
    }
}

/**
 * User management component with Summon HTTP client and modals
 */
@Composable
fun SummonUserManagementComponent(initialUsers: List<User>) {
    val users = remember { mutableStateOf(initialUsers) }
    val isLoading = remember { mutableStateOf(false) }
    val showEditModal = remember { mutableStateOf(false) }
    val editingUser = remember { mutableStateOf<User?>(null) }
    val showDeleteModal = remember { mutableStateOf(false) }
    val deletingUser = remember { mutableStateOf<User?>(null) }
    val httpClient = remember { createHttpClient() }
    val toastManager = rememberToastManager()

    suspend fun refreshUsers() {
        try {
            isLoading.value = true
            val response = httpClient.get("/api/users")
            val userList = JSON.parse<Array<User>>(response.body).toList()
            users.value = userList
            toastManager.showSuccess("Users refreshed")
        } catch (e: Exception) {
            toastManager.showError("Failed to refresh users: ${e.message}")
        } finally {
            isLoading.value = false
        }
    }

    suspend fun deleteUser(user: User) {
        try {
            isLoading.value = true
            httpClient.delete("/api/users/${user.id}")
            refreshUsers()
            toastManager.showSuccess("User ${user.name} deleted successfully")
        } catch (e: Exception) {
            toastManager.showError("Failed to delete user: ${e.message}")
            isLoading.value = false
        }
    }

    suspend fun toggleUserStatus(user: User) {
        try {
            isLoading.value = true
            val action = if (user.active) "deactivate" else "activate"
            httpClient.post("/api/users/${user.id}/$action", "")
            refreshUsers()
            toastManager.showSuccess("User ${user.name} ${action}d successfully")
        } catch (e: Exception) {
            toastManager.showError("Failed to ${if (user.active) "deactivate" else "activate"} user: ${e.message}")
            isLoading.value = false
        }
    }

    Column {
        // Header with refresh button
        Row(
            modifier = Modifier()
                .justifyContent("space-between")
                .alignItems("center")
                .marginBottom("20px")
        ) {
            Text(
                text = "👥 User Management",
                modifier = Modifier()
                    .fontSize("24px")
                    .fontWeight("600")
            )
            
            Button(
                onClick = { 
                    CoroutineScope(Dispatchers.Main).launch {
                        refreshUsers()
                    }
                },
                label = "Refresh",
                variant = ButtonVariant.SECONDARY,
                enabled = !isLoading.value
            )
        }

        // Loading overlay
        if (isLoading.value) {
            LoadingOverlay(
                isVisible = true,
                text = "Loading users...",
                variant = LoadingVariant.SPINNER,
                size = LoadingSize.LARGE
            )
        }

        // Users table
        Card {
            if (users.value.isEmpty()) {
                Box(
                    modifier = Modifier()
                        .padding("40px")
                        .textAlign("center")
                ) {
                    Text(
                        text = "No users found",
                        modifier = Modifier().color("#6c757d")
                    )
                }
            } else {
                Column {
                    // Table header
                    Row(
                        modifier = Modifier()
                            .backgroundColor("#f8f9fa")
                            .padding("12px 16px")
                            .fontWeight("600")
                            .borderBottom("1px solid #dee2e6")
                    ) {
                        Text("ID", modifier = Modifier().flex("1"))
                        Text("Name", modifier = Modifier().flex("2"))
                        Text("Email", modifier = Modifier().flex("2"))
                        Text("Role", modifier = Modifier().flex("1"))
                        Text("Status", modifier = Modifier().flex("1"))
                        Text("Actions", modifier = Modifier().flex("2"))
                    }
                    
                    // Table rows
                    users.value.forEach { user ->
                        Row(
                            modifier = Modifier()
                                .padding("12px 16px")
                                .borderBottom("1px solid #f0f0f0")
                                .alignItems("center")
                        ) {
                            Text(user.id.toString(), modifier = Modifier().flex("1"))
                            Text(user.name, modifier = Modifier().flex("2"))
                            Text(user.email, modifier = Modifier().flex("2"))
                            Text(user.role, modifier = Modifier().flex("1"))
                            Text(
                                text = if (user.active) "Active" else "Inactive",
                                modifier = Modifier()
                                    .flex("1")
                                    .color(if (user.active) "#28a745" else "#dc3545")
                            )
                            
                            // Actions
                            Row(
                                modifier = Modifier()
                                    .flex("2")
                                    .gap("8px")
                            ) {
                                Button(
                                    onClick = {
                                        editingUser.value = user
                                        showEditModal.value = true
                                    },
                                    label = "Edit",
                                    variant = ButtonVariant.SECONDARY
                                )
                                
                                Button(
                                    onClick = {
                                        deletingUser.value = user
                                        showDeleteModal.value = true
                                    },
                                    label = "Delete",
                                    variant = ButtonVariant.PRIMARY,
                                    modifier = Modifier().backgroundColor("#dc3545")
                                )
                                
                                Button(
                                    onClick = {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            toggleUserStatus(user)
                                        }
                                    },
                                    label = if (user.active) "Deactivate" else "Activate",
                                    variant = ButtonVariant.SECONDARY,
                                    enabled = !isLoading.value
                                )
                            }
                        }
                    }
                }
            }
        }

        // Edit user modal
        if (showEditModal.value && editingUser.value != null) {
            val user = editingUser.value!!
            val editName = remember { mutableStateOf(user.name) }
            val editEmail = remember { mutableStateOf(user.email) }
            val editRole = remember { mutableStateOf(user.role) }

            Modal(
                isOpen = showEditModal.value,
                onDismiss = { showEditModal.value = false },
                variant = ModalVariant.DEFAULT,
                size = ModalSize.MEDIUM,
                header = {
                    Text(
                        text = "Edit User",
                        modifier = Modifier()
                            .fontSize("20px")
                            .fontWeight("600")
                    )
                },
                footer = {
                    Row(
                        modifier = Modifier()
                            .justifyContent("flex-end")
                            .gap("12px")
                    ) {
                        Button(
                            onClick = { showEditModal.value = false },
                            label = "Cancel",
                            variant = ButtonVariant.SECONDARY
                        )
                        
                        Button(
                            onClick = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        val updatedUser = user.copy(
                                            name = editName.value,
                                            email = editEmail.value,
                                            role = editRole.value
                                        )
                                        
                                        httpClient.put(
                                            "/api/users/${user.id}",
                                            JSON.stringify(updatedUser)
                                        )
                                        
                                        refreshUsers()
                                        showEditModal.value = false
                                        toastManager.showSuccess("User updated successfully")
                                    } catch (e: Exception) {
                                        toastManager.showError("Failed to update user: ${e.message}")
                                    }
                                }
                            },
                            label = "Save",
                            variant = ButtonVariant.PRIMARY
                        )
                    }
                }
            ) {
                Column(
                    modifier = Modifier().gap("16px")
                ) {
                    TextField(
                        value = editName.value,
                        onValueChange = { editName.value = it },
                        label = "Name",
                        modifier = Modifier().width("100%")
                    )
                    
                    TextField(
                        value = editEmail.value,
                        onValueChange = { editEmail.value = it },
                        label = "Email",
                        type = "email",
                        modifier = Modifier().width("100%")
                    )
                    
                    TextField(
                        value = editRole.value,
                        onValueChange = { editRole.value = it },
                        label = "Role",
                        modifier = Modifier().width("100%")
                    )
                }
            }
        }

        // Delete confirmation modal
        if (showDeleteModal.value && deletingUser.value != null) {
            val user = deletingUser.value!!
            
            ConfirmationModal(
                isOpen = showDeleteModal.value,
                title = "Delete User",
                message = "Are you sure you want to delete user \"${user.name}\"? This action cannot be undone.",
                confirmText = "Delete",
                cancelText = "Cancel",
                onConfirm = {
                    CoroutineScope(Dispatchers.Main).launch {
                        deleteUser(user)
                        showDeleteModal.value = false
                    }
                },
                onCancel = { showDeleteModal.value = false }
            )
        }

        // Toast container
        ToastProvider(position = ToastPosition.TOP_RIGHT) { manager ->
            // Manager provided by context
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