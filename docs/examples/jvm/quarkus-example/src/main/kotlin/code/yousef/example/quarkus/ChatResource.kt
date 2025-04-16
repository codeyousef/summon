package code.yousef.example.quarkus

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.Logger

/**
 * Chat page resource.
 */
@Path("/chat")
class ChatResource {
    private val logger = Logger.getLogger(ChatResource::class.java)

    @Inject
    lateinit var summonRenderer: SummonRenderer

    init {
        logger.info("=================== ChatResource initialized ===================")
        println("=================== ChatResource initialized ===================")
    }

    /**
     * Render the chat page.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun chat(): String {
        logger.info("================ ChatResource.chat() CALLED ================")
        println("================ ChatResource.chat() CALLED ================")

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Real-time Chat</title>
                <style>
                    body { 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        padding: 2rem;
                        max-width: 1200px;
                        margin: 0 auto;
                        color: #333;
                    }
                    h1, h2, h3 { color: #4695EB; }
                    .card {
                        border: 1px solid #ddd;
                        border-radius: 8px;
                        padding: 1.5rem;
                        margin-bottom: 1.5rem;
                        background-color: #fff;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }
                    .nav {
                        background-color: #4695EB;
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
                        background-color: #4695EB;
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
                    }
                    #debug-log {
                        height: 200px;
                        overflow-y: auto;
                        padding: 10px;
                        font-family: monospace;
                        font-size: 12px;
                        background-color: #f5f5f5;
                        border: 1px solid #ddd;
                        border-radius: 4px;
                        color: #333;
                    }
                    .debug-entry {
                        margin-bottom: 5px;
                        border-bottom: 1px solid #eee;
                        padding-bottom: 5px;
                    }
                    .timestamp {
                        color: #666;
                        font-size: 11px;
                    }
                    .debug-error {
                        color: red;
                    }
                    .debug-info {
                        color: blue;
                    }
                    .connection-info {
                        font-size: 0.9rem;
                        margin-bottom: 1rem;
                        padding: 8px;
                        border-radius: 4px;
                        text-align: center;
                    }
                    .connected {
                        background-color: #e8f5e9;
                        color: #2e7d32;
                    }
                    .disconnected {
                        background-color: #ffebee;
                        color: #c62828;
                    }
                    .connecting {
                        background-color: #fff8e1;
                        color: #ff8f00;
                    }
                </style>
            </head>
            <body>
                <!-- Navigation -->
                <nav class="nav">
                    <ul class="nav-list">
                        <li class="nav-item"><a href="/" class="nav-link">Home</a></li>
                        <li class="nav-item"><a href="/users" class="nav-link">Users</a></li>
                        <li class="nav-item"><a href="/dashboard" class="nav-link">Dashboard</a></li>
                        <li class="nav-item"><a href="/contact" class="nav-link">Contact</a></li>
                        <li class="nav-item"><a href="/theme" class="nav-link">Theme</a></li>
                        <li class="nav-item"><a href="/chat" class="nav-link">Chat</a></li>
                    </ul>
                </nav>
                
                <h1>Real-time Chat</h1>
                
                <!-- Connection info and debugging -->
                <div class="card">
                    <h2>Connection Info</h2>
                    <p>Server Info: Port 8081, Host: ${System.getProperty("quarkus.http.host") ?: "0.0.0.0"}</p>
                    <p>WebSocket Endpoint: /chat/{username}</p>
                    <p>Current URL: <span id="current-url">Loading...</span></p>
                </div>
                
                <!-- Login Section -->
                <div class="card" id="login-section">
                    <div class="form-group">
                        <label for="username-input">Username</label>
                        <input type="text" id="username-input" placeholder="Enter your username" required>
                    </div>
                    <button id="join-chat-btn" class="btn">Join Chat</button>
                </div>
                
                <!-- Connection status -->
                <div id="connection-status" class="connection-info disconnected">
                    Not connected
                </div>
                
                <!-- Chat Container (initially hidden) -->
                <div id="chat-container">
                    <div class="chat-layout">
                        <div class="card">
                            <div id="chat-messages"></div>
                            <div style="display: flex; gap: 0.5rem;">
                                <input type="text" id="message-input" placeholder="Type a message...">
                                <button id="send-btn" class="btn">Send</button>
                            </div>
                        </div>
                        
                        <div class="card">
                            <h3>Active Users</h3>
                            <ul id="users-list"></ul>
                        </div>
                    </div>
                </div>
                
                <!-- Debug Log -->
                <div class="card">
                    <h3>Debug Log</h3>
                    <div id="debug-log"></div>
                </div>
                
                <script>
                    document.addEventListener('DOMContentLoaded', function() {
                        // Display current URL for debugging
                        document.getElementById('current-url').textContent = window.location.href;
                        
                        let socket;
                        let localUsername;
                        let reconnectAttempts = 0;
                        let reconnectTimer = null;
                        const MAX_RECONNECT_ATTEMPTS = 5;
                        const RECONNECT_DELAY = 3000; // 3 seconds
                        
                        const connectionStatus = document.getElementById('connection-status');
                        const debugLog = document.getElementById('debug-log');
                        
                        // Debug logger function
                        function log(message, type = 'info') {
                            const now = new Date();
                            const timestamp = now.toLocaleTimeString() + '.' + now.getMilliseconds();
                            
                            const entry = document.createElement('div');
                            entry.className = 'debug-entry';
                            
                            if (type === 'error') {
                                entry.classList.add('debug-error');
                            } else if (type === 'info') {
                                entry.classList.add('debug-info');
                            }
                            
                            entry.innerHTML = `<span class="timestamp">[\${'$'}{timestamp}]</span> \${'$'}{message}`;
                            debugLog.appendChild(entry);
                            
                            // Auto-scroll to the bottom
                            debugLog.scrollTop = debugLog.scrollHeight;
                            
                            // Also log to console
                            console.log(`[\${'$'}{timestamp}] \${'$'}{message}`);
                        }
                        
                        // Update connection status display
                        function updateConnectionStatus(status) {
                            connectionStatus.className = 'connection-info ' + status;
                            
                            switch(status) {
                                case 'connected':
                                    connectionStatus.textContent = 'Connected to chat server';
                                    break;
                                case 'connecting':
                                    connectionStatus.textContent = 'Connecting to chat server...';
                                    break;
                                case 'disconnected':
                                    connectionStatus.textContent = 'Disconnected from chat server';
                                    break;
                            }
                        }
                        
                        // Attempt to connect WebSocket
                        function connectWebSocket(username) {
                            // Update status to connecting
                            updateConnectionStatus('connecting');
                            
                            try {
                                // Get WebSocket URL
                                const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
const wsUrl = wsProtocol + '//' + window.location.host + '/chat/' + username;
socket = new WebSocket(wsUrl);

                                
                                // WebSocket event handlers
                                socket.onopen = function(event) {
                                    log('WebSocket connection opened', 'info');
                                    addSystemMessage('Connected to chat server');
                                    reconnectAttempts = 0;
                                    updateConnectionStatus('connected');
                                    
                                    if (reconnectTimer) {
                                        clearTimeout(reconnectTimer);
                                        reconnectTimer = null;
                                    }
                                };
                                
                                socket.onmessage = function(event) {
                                    log(`WebSocket message received: \${'$'}{event.data}`, 'info');
                                    
                                    try {
                                        const data = JSON.parse(event.data);
                                        
                                        if (data.username === 'Server' && data.message.startsWith('Active users:')) {
                                            // Handle active users list
                                            const usersList = data.message.substring(14).split(', ');
                                            updateUsersList(usersList);
                                        } else {
                                            // Handle regular message
                                            addChatMessage(data.username, data.message);
                                        }
                                    } catch (e) {
                                        log(`Error parsing message: \${'$'}{e.message}`, 'error');
                                    }
                                };
                                
                                socket.onclose = function(event) {
                                    log(`WebSocket connection closed: code=\${'$'}{event.code}, reason=\${'$'}{event.reason || 'No reason provided'}`, 'error');
                                    addSystemMessage('Disconnected from chat server');
                                    updateConnectionStatus('disconnected');
                                    
                                    // Attempt to reconnect if not closing intentionally
                                    if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                                        reconnectAttempts++;
                                        log(`Attempting to reconnect (\${'$'}{reconnectAttempts}/\${'$'}{MAX_RECONNECT_ATTEMPTS}) in \${'$'}{RECONNECT_DELAY/1000} seconds...`, 'info');
                                        
                                        reconnectTimer = setTimeout(function() {
                                            connectWebSocket(username);
                                        }, RECONNECT_DELAY);
                                    } else {
                                        log('Maximum reconnection attempts reached. Please refresh the page to try again.', 'error');
                                    }
                                };
                                
                                socket.onerror = function(error) {
                                    log(`WebSocket error: \${'$'}{error.message || 'Unknown error'}`, 'error');
                                    addSystemMessage('Error: Connection failed');
                                };
                                
                                return true;
                            } catch (e) {
                                log(`Error creating WebSocket: \${'$'}{e.message}`, 'error');
                                updateConnectionStatus('disconnected');
                                return false;
                            }
                        }
                        
                        // Join chat button click handler
                        document.getElementById('join-chat-btn').addEventListener('click', function() {
                            localUsername = document.getElementById('username-input').value.trim();
                            
                            if (localUsername) {
                                log(`User "\${'$'}{localUsername}" attempting to join chat`, 'info');
                                
                                // Connect to WebSocket
                                if (connectWebSocket(localUsername)) {
                                    // Show chat UI, hide join UI
                                    document.getElementById('login-section').style.display = 'none';
                                    document.getElementById('chat-container').style.display = 'block';
                                    
                                    // Store username in a way accessible to the functions below
                                    if (typeof window !== 'undefined') {
                                       window.chatUsername = localUsername;
                                    }
                                }
                            } else {
                                alert('Please enter a username');
                                log('Join attempt failed: No username provided', 'error');
                            }
                        });
                        
                        // Send message button click handler
                        document.getElementById('send-btn').addEventListener('click', sendMessage);
                        
                        // Enter key to send message
                        document.getElementById('message-input').addEventListener('keypress', function(event) {
                            if (event.key === 'Enter') {
                                sendMessage();
                            }
                        });
                        
                        function sendMessage() {
                            const messageInput = document.getElementById('message-input');
                            const message = messageInput.value.trim();
                            
                            if (message && socket && socket.readyState === WebSocket.OPEN) {
                                log(`Sending message: "\${'$'}{message}"`, 'info');
                                socket.send(message);
                                messageInput.value = '';
                            } else if (!socket || socket.readyState !== WebSocket.OPEN) {
                                log('Cannot send message: WebSocket is not connected', 'error');
                                addSystemMessage('Cannot send message: Not connected to server');
                            }
                        }
                        
                        function addChatMessage(username, message) {
                            const chatMessages = document.getElementById('chat-messages');
                            const messageElement = document.createElement('div');
                            
                            const currentUsername = (typeof window !== 'undefined' && window.chatUsername) ? window.chatUsername : null;
                            messageElement.className = username === currentUsername ? 'message outgoing' : 'message incoming';
                            
                            // Style based on message type
                            if (username === 'Server') {
                                messageElement.style = 'padding: 5px 10px; margin: 5px 0; font-style: italic; color: #888; text-align: center;';
                            } else {
                                messageElement.style = 'padding: 8px 12px; margin: 5px 0; border-radius: 5px; background-color: ' + 
                                                    (username === currentUsername ? '#e3f2fd' : '#f1f1f1') + ';';
                            }
                            
                            messageElement.innerHTML = username === 'Server' ? message : '<strong>' + username + ':</strong> ' + message;
                            chatMessages.appendChild(messageElement);
                            
                            // Scroll to bottom
                            chatMessages.scrollTop = chatMessages.scrollHeight;
                        }
                        
                        function addSystemMessage(message) {
                            addChatMessage('Server', message);
                        }
                        
                        function updateUsersList(users) {
                            const usersList = document.getElementById('users-list');
                            usersList.innerHTML = '';
                            
                            const currentUsername = (typeof window !== 'undefined' && window.chatUsername) ? window.chatUsername : null;
                            
                            log(`Updating users list: \${'$'}{users.join(', ')}`, 'info');
                            
                            users.forEach(user => {
                                const li = document.createElement('li');
                                li.style = 'padding: 5px 0; border-bottom: 1px solid #eee;';
                                li.innerHTML = '<span style="color: ' + (user === currentUsername ? '#4695EB' : 'inherit') + 
                                            '";">' + user + (user === currentUsername ? ' (You)' : '') + '</span>';
                                usersList.appendChild(li);
                            });
                        }
                        
                        // Log initial page load
                        log('Chat page loaded', 'info');
                        updateConnectionStatus('disconnected');
                    });
                </script>
            </body>
            </html>
        """.trimIndent()
    }

    /**
     * Diagnostic endpoint for WebSocket testing.
     */
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_HTML)
    fun testWebSocketConnection(): String {
        logger.info("ChatResource.testWebSocketConnection() called - Running diagnostics")
        println("ChatResource.testWebSocketConnection() called - Running diagnostics")

        return """
    <!DOCTYPE html>
    <html>
    <!-- other HTML remains the same -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Display current URL for debugging
            document.getElementById('current-url').textContent = window.location.href;
            
            let socket;
            let localUsername;
            // other JavaScript variables remain the same
            
            // Attempt to connect WebSocket
            function connectWebSocket(username) {
                // Update status to connecting
                updateConnectionStatus('connecting');
                
                try {
                    // Directly construct the WebSocket URL without template literals
                    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
                    const wsUrl = wsProtocol + '//' + window.location.host + '/chat/' + username;
                    
                    log('Attempting to connect to WebSocket at: ' + wsUrl);
                    
                    // Create WebSocket connection
                    socket = new WebSocket(wsUrl);
                    
                    // WebSocket event handlers remain the same
                }
                catch (e) {
                    // Error handling remains the same
                }
            }
            
            // remaining JavaScript functions remain the same
        });
    </script>
    </html>
""".trimIndent()
    }
} 