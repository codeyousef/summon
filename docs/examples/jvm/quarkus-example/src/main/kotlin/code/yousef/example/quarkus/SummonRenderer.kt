package code.yousef.example.quarkus

import code.yousef.summon.platform.JvmPlatformRenderer
import code.yousef.summon.runtime.Composable
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.html.*
import kotlinx.html.stream.createHTML

/**
 * A renderer for Summon components integrated with Quarkus.
 * This class handles the transformation of Summon components into HTML.
 */
@ApplicationScoped
class SummonRenderer {
    private val renderer = JvmPlatformRenderer()

    // Store chat JavaScript separately
    private val chatJavaScript = """
        document.addEventListener('DOMContentLoaded', function() {
            let socket;
            let localUsername;
            
            // Join chat button click handler
            document.getElementById('join-chat-btn').addEventListener('click', function() {
                localUsername = document.getElementById('username-input').value.trim();
                
                if (localUsername) {
                    // Connect to WebSocket
                    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
                    socket = new WebSocket(`\${'$'}{wsProtocol}//\${'$'}{window.location.host}/chat/\${'$'}{localUsername}`);
                    
                    // Show chat UI, hide join UI
                    document.getElementById('login-section').style.display = 'none';
                    document.getElementById('chat-container').style.display = 'block';
                    
                    // Store username in a way accessible to the functions below
                    if (typeof window !== 'undefined') {
                       window.chatUsername = localUsername;
                    }

                    // WebSocket event handlers
                    socket.onopen = function(event) {
                        addSystemMessage('Connected to chat server');
                    };
                    
                    socket.onmessage = function(event) {
                        const data = JSON.parse(event.data);
                        
                        if (data.username === 'Server' && data.message.startsWith('Active users:')) {
                            // Handle active users list
                            const usersList = data.message.substring(14).split(', ');
                            updateUsersList(usersList);
                        } else {
                            // Handle regular message
                            addChatMessage(data.username, data.message);
                        }
                    };
                    
                    socket.onclose = function(event) {
                        addSystemMessage('Disconnected from chat server');
                        if (typeof window !== 'undefined') {
                          window.chatUsername = null;
                        }
                    };
                    
                    socket.onerror = function(error) {
                        addSystemMessage('Error: ' + error);
                    };
                } else {
                    alert('Please enter a username');
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
                    socket.send(message);
                    messageInput.value = '';
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
                
                users.forEach(user => {
                    const li = document.createElement('li');
                    li.style = 'padding: 5px 0; border-bottom: 1px solid #eee;';
                    li.innerHTML = '<span style="color: ' + (user === currentUsername ? 'var(--primary-color)' : 'inherit') + 
                                '";">' + user + (user === currentUsername ? ' (You)' : '') + '</span>';
                    usersList.appendChild(li);
                });
            }
            
        });
        """

    /**
     * Renders a Summon component to HTML.
     *
     * @param title The page title
     * @param description Meta description for SEO
     * @param includeChatScript Whether to include the chat-specific JavaScript
     * @param content The composable content to render
     * @return HTML string representation of the component
     */
    fun render(
        title: String = "Summon Quarkus Example",
        description: String = "A demonstration of Summon with Quarkus",
        includeChatScript: Boolean = false, // Add flag to control script inclusion
        content: @Composable () -> Unit
    ): String {
        return createHTML().html {
            renderPage(title, description, includeChatScript, content)
        }
    }

    /**
     * Renders a page with the given title, description, and content.
     */
    private fun HTML.renderPage(
        title: String,
        description: String,
        includeChatScript: Boolean,
        content: @Composable () -> Unit
    ) {
        head {
            meta { charset = "UTF-8" }
            meta {
                attributes["name"] = "viewport"
                attributes["content"] = "width=device-width, initial-scale=1.0"
            }
            // Create a separate description meta tag using its attributes constructor instead of properties
            meta(name = "description", content = description)
            title { +title }
            // Add style directly in the head tag
            style {
                unsafe {
                    +getStyleContent()
                }
            }
        }
        body {
            // Render the Summon composable content using the renderer
            renderer.renderComposable(content, this)
            // Include HTMX
            script { src = "https://unpkg.com/htmx.org@1.9.12" }
            // Conditionally include the chat script
            if (includeChatScript) {
                script {
                    unsafe { +chatJavaScript } // Embed the JS here
                }
            }
            // Include other general scripts if needed
            // script { src = "/script.js" } 
        }
    }

    /**
     * Gets the content for global styles.
     */
    private fun getStyleContent(): String {
        return """
        :root {
          --primary-color: #4695EB;
          --secondary-color: #FF4081;
          --background-color: #FFFFFF;
          --text-color: #333333;
          --success-color: #4CAF50;
          --error-color: #F44336;
          --warning-color: #FF9800;
          --info-color: #2196F3;
        }
        body {
          font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
          line-height: 1.6;
          color: var(--text-color);
          background-color: var(--background-color);
          margin: 0;
          padding: 20px;
        }
        .container {
          max-width: 1200px;
          margin: 0 auto;
          padding: 0 20px;
        }
        .row {
          display: flex;
          flex-wrap: wrap;
          margin: 0 -15px;
        }
        .col {
          flex: 1;
          padding: 0 15px;
        }
        .card {
          background: white;
          border-radius: 8px;
          box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
          padding: 24px;
          margin-bottom: 24px;
          transition: transform 0.3s ease;
        }
        .card:hover {
          transform: translateY(-5px);
        }
        .btn {
          display: inline-block;
          background-color: var(--primary-color);
          color: white;
          border: none;
          border-radius: 4px;
          padding: 8px 16px;
          cursor: pointer;
          transition: background-color 0.3s;
          font-size: 1rem;
        }
        .btn:hover {
          background-color: #3a85d8;
        }
        .btn-secondary {
          background-color: var(--secondary-color);
        }
        .btn-secondary:hover {
          background-color: #e91e63;
        }
        input, select, textarea {
          width: 100%;
          padding: 10px;
          margin: 8px 0;
          border: 1px solid #ddd;
          border-radius: 4px;
          box-sizing: border-box;
        }
        label {
          font-weight: 600;
        }
        .form-group {
          margin-bottom: 16px;
        }
        .error-text {
          color: var(--error-color);
          font-size: 0.9rem;
        }
        .success-text {
          color: var(--success-color);
          font-size: 0.9rem;
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
        .nav-link:hover {
          text-decoration: underline;
        }
        .table {
          width: 100%;
          border-collapse: collapse;
          margin: 1rem 0;
        }
        .table th, .table td {
          border: 1px solid #ddd;
          padding: 12px;
          text-align: left;
        }
        .table th {
          background-color: #f5f5f5;
          font-weight: 600;
        }
        .table tr:nth-child(even) {
          background-color: #f9f9f9;
        }
        @media (max-width: 768px) {
          .row {
            flex-direction: column;
          }
          .col {
            margin-bottom: 1rem;
          }
          .nav-list {
            flex-direction: column;
          }
          .nav-item {
            margin-right: 0;
            margin-bottom: 0.5rem;
          }
        }
        """
    }
} 