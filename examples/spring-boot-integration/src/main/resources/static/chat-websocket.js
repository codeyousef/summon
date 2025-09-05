/**
 * WebSocket Chat Client
 * Handles real-time chat functionality for the ChatPage component
 */
(function() {
    let ws = null;
    let username = '';
    let isConnected = false;
    
    // Wait for DOM to be ready
    document.addEventListener('DOMContentLoaded', function() {
        initializeChatClient();
    });
    
    // If DOM is already loaded, initialize immediately
    if (document.readyState === 'loading') {
        // DOM is still loading
    } else {
        // DOM is already loaded
        setTimeout(initializeChatClient, 100);
    }
    
    function initializeChatClient() {
        console.log('Initializing WebSocket chat client...');
        
        const usernameInput = document.getElementById('username-input');
        const connectBtn = document.getElementById('connect-btn');
        const connectionStatus = document.getElementById('connection-status');
        const connectionForm = document.getElementById('connection-form');
        const chatInterface = document.getElementById('chat-interface');
        const chatHeaderUsername = document.getElementById('chat-header-username');
        const messagesContainer = document.getElementById('messages-container');
        const messagesArea = document.getElementById('messages-area');
        const messageInput = document.getElementById('message-input');
        const sendBtn = document.getElementById('send-btn');
        const disconnectBtn = document.getElementById('disconnect-btn');
        
        if (!usernameInput) {
            console.warn('Chat elements not found, retrying in 500ms...');
            setTimeout(initializeChatClient, 500);
            return;
        }
        
        // Add placeholder behavior for contenteditable divs
        function addPlaceholderBehavior(element) {
            const placeholder = element.getAttribute('data-placeholder');
            if (!element.textContent.trim()) {
                element.style.color = '#999';
                element.textContent = placeholder;
            }
            
            element.addEventListener('focus', function() {
                if (element.textContent === placeholder) {
                    element.textContent = '';
                    element.style.color = '';
                }
            });
            
            element.addEventListener('blur', function() {
                if (!element.textContent.trim()) {
                    element.style.color = '#999';
                    element.textContent = placeholder;
                }
            });
        }
        
        // Initialize placeholder behavior
        if (usernameInput) addPlaceholderBehavior(usernameInput);
        if (messageInput) addPlaceholderBehavior(messageInput);
        
        // Connect to WebSocket
        function connectToChat() {
            const usernameValue = usernameInput.textContent.trim();
            if (!usernameValue || usernameValue === usernameInput.getAttribute('data-placeholder')) {
                alert('Please enter a username');
                return;
            }
            
            username = usernameValue;
            
            // Show connecting status
            connectionStatus.style.display = 'block';
            connectionStatus.textContent = 'Connecting...';
            connectionStatus.style.color = '#007bff';
            
            // Create WebSocket connection
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const wsUrl = protocol + '//' + window.location.host + '/ws/chat/' + encodeURIComponent(username);
            
            console.log('Connecting to WebSocket:', wsUrl);
            
            ws = new WebSocket(wsUrl);
            
            ws.onopen = function() {
                console.log('WebSocket connected');
                isConnected = true;
                
                // Hide connection form, show chat interface
                connectionForm.style.display = 'none';
                chatInterface.style.display = 'block';
                chatHeaderUsername.textContent = '💬 Chat Room - ' + username;
                
                // Clear messages
                messagesContainer.innerHTML = '';
                addSystemMessage('Connected to chat room!');
            };
            
            ws.onmessage = function(event) {
                console.log('Received message:', event.data);
                try {
                    const message = JSON.parse(event.data);
                    addMessage(message);
                } catch (e) {
                    console.error('Error parsing message:', e);
                    addSystemMessage('Error: ' + event.data);
                }
            };
            
            ws.onclose = function() {
                console.log('WebSocket disconnected');
                isConnected = false;
                addSystemMessage('Disconnected from chat');
            };
            
            ws.onerror = function(error) {
                console.error('WebSocket error:', error);
                connectionStatus.textContent = 'Connection failed. Please try again.';
                connectionStatus.style.color = '#dc3545';
                isConnected = false;
            };
        }
        
        // Disconnect from WebSocket
        function disconnect() {
            if (ws) {
                ws.close();
                ws = null;
            }
            isConnected = false;
            
            // Show connection form, hide chat interface
            connectionForm.style.display = 'block';
            chatInterface.style.display = 'none';
            connectionStatus.style.display = 'none';
            
            // Clear username input
            usernameInput.textContent = usernameInput.getAttribute('data-placeholder');
            usernameInput.style.color = '#999';
        }
        
        // Send message
        function sendMessage() {
            const messageText = messageInput.textContent.trim();
            if (!messageText || messageText === messageInput.getAttribute('data-placeholder')) {
                return;
            }
            
            if (!isConnected || !ws || ws.readyState !== WebSocket.OPEN) {
                addSystemMessage('Not connected to chat. Please reconnect.');
                return;
            }
            
            try {
                ws.send(messageText);
                
                // Clear input
                messageInput.textContent = '';
                messageInput.style.color = '#999';
                messageInput.textContent = messageInput.getAttribute('data-placeholder');
            } catch (e) {
                console.error('Error sending message:', e);
                addSystemMessage('Error sending message');
            }
        }
        
        // Add message to chat
        function addMessage(message) {
            const messageDiv = document.createElement('div');
            const isOwn = message.username === username;
            const isSystem = message.username === 'System';
            
            messageDiv.style.display = 'flex';
            messageDiv.style.width = '100%';
            messageDiv.style.justifyContent = isSystem ? 'center' : (isOwn ? 'flex-end' : 'flex-start');
            
            const bubbleDiv = document.createElement('div');
            bubbleDiv.style.maxWidth = isSystem ? '80%' : '70%';
            bubbleDiv.style.padding = '12px 16px';
            bubbleDiv.style.borderRadius = '12px';
            bubbleDiv.style.marginBottom = '4px';
            
            if (isSystem) {
                bubbleDiv.style.backgroundColor = '#6c757d';
                bubbleDiv.style.color = 'white';
                bubbleDiv.style.textAlign = 'center';
                bubbleDiv.style.fontStyle = 'italic';
            } else if (isOwn) {
                bubbleDiv.style.backgroundColor = '#007bff';
                bubbleDiv.style.color = 'white';
            } else {
                bubbleDiv.style.backgroundColor = '#ffffff';
                bubbleDiv.style.color = '#333333';
                bubbleDiv.style.border = '1px solid #e0e0e0';
            }
            
            bubbleDiv.style.boxShadow = '0 2px 4px rgba(0,0,0,0.1)';
            
            let content = '';
            if (!isOwn && !isSystem) {
                content += '<div style="font-size: 12px; font-weight: bold; color: #666666; margin-bottom: 4px;">' + 
                          escapeHtml(message.username) + '</div>';
            }
            content += '<div style="font-size: 14px; line-height: 1.4;">' + escapeHtml(message.message) + '</div>';
            
            bubbleDiv.innerHTML = content;
            messageDiv.appendChild(bubbleDiv);
            messagesContainer.appendChild(messageDiv);
            
            // Scroll to bottom
            messagesArea.scrollTop = messagesArea.scrollHeight;
        }
        
        // Add system message
        function addSystemMessage(text) {
            addMessage({
                username: 'System',
                message: text,
                timestamp: Date.now()
            });
        }
        
        // Escape HTML
        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }
        
        // Event listeners
        if (connectBtn) {
            connectBtn.addEventListener('click', connectToChat);
        }
        
        if (disconnectBtn) {
            disconnectBtn.addEventListener('click', disconnect);
        }
        
        if (sendBtn) {
            sendBtn.addEventListener('click', sendMessage);
        }
        
        // Enter key to send message
        if (messageInput) {
            messageInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    sendMessage();
                }
            });
        }
        
        // Enter key to connect
        if (usernameInput) {
            usernameInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    connectToChat();
                }
            });
        }
        
        console.log('Chat WebSocket client initialized successfully');
    }
})();
