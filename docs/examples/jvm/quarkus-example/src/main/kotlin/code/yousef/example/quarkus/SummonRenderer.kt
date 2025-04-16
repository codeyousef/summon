package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.integrations.quarkus.QuarkusExtension.SummonRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.platform.renderToString
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.jboss.logging.Logger

/**
 * A renderer for HTML components integrated with Quarkus.
 * This class handles the transformation of components into HTML.
 */
@ApplicationScoped
class SummonRenderer {
    private val logger = Logger.getLogger(SummonRenderer::class.java)

    // Store chat JavaScript separately
    private val chatJavaScript = """
        document.addEventListener('DOMContentLoaded', function() {
            let socket;
            let localUsername;
            
            // Join chat button click handler
            document.getElementById('join-chat-btn')?.addEventListener('click', function() {
                localUsername = document.getElementById('username-input').value.trim();
                
                if (localUsername) {
                    // Connect to WebSocket
                    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
                    socket = new WebSocket(`\${'$'}{wsProtocol}//\${'$'}{window.location.host}/chat/\${'$'}{localUsername}`);
                    console.log('WebSocket connecting to:', `\${'$'}{wsProtocol}//\${'$'}{window.location.host}/chat/\${'$'}{localUsername}`);
                    
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
                        console.log('WebSocket connection opened');
                    };
                    
                    socket.onmessage = function(event) {
                        console.log('WebSocket message received:', event.data);
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
                        console.log('WebSocket connection closed:', event.code, event.reason);
                        if (typeof window !== 'undefined') {
                          window.chatUsername = null;
                        }
                    };
                    
                    socket.onerror = function(error) {
                        addSystemMessage('Error: Connection failed');
                        console.error('WebSocket error:', error);
                    };
                } else {
                    alert('Please enter a username');
                }
            });
            
            // Send message button click handler
            document.getElementById('send-btn')?.addEventListener('click', sendMessage);
            
            // Enter key to send message
            document.getElementById('message-input')?.addEventListener('keypress', function(event) {
                if (event.key === 'Enter') {
                    sendMessage();
                }
            });
            
            function sendMessage() {
                const messageInput = document.getElementById('message-input');
                const message = messageInput?.value.trim();
                
                if (message && socket && socket.readyState === WebSocket.OPEN) {
                    socket.send(message);
                    if (messageInput) messageInput.value = '';
                }
            }
            
            function addChatMessage(username, message) {
                const chatMessages = document.getElementById('chat-messages');
                if (!chatMessages) return;
                
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
                if (!usersList) return;
                
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
     * Render HTML content with a full page structure using Summon composables.
     *
     * @param title The page title
     * @param description Meta description for the page
     * @param includeChatScript Whether to include chat-specific JavaScript
     * @param content The composable content lambda
     * @return The rendered HTML as a string
     */
    fun render(
        title: String = "Application with Quarkus",
        description: String = "A web application running on Quarkus",
        includeChatScript: Boolean = false,
        content: @Composable () -> Unit
    ): String {
        logger.info("SummonRenderer.render() called with title: $title")

        try {
            // 1. Render the composable part using Summon's SSR function
            logger.info("SummonRenderer.render() - Rendering composable content...")
            val renderedComposableHtml = try {
                renderToString { content() }
            } catch (e: Exception) {
                logger.error("ERROR rendering composable content: ${e.message}", e)
                // Provide error details within the page structure
                renderToString { ErrorComponent("Composable Rendering Error", e) }
            }
            logger.info("SummonRenderer.render() - Composable content rendered, length: ${renderedComposableHtml.length}")

            // 2. Build the full HTML page structure using kotlinx.html
            val result = createHTML().html {
                // Use the renamed helper function
                renderPageStructure(title, description, includeChatScript) {
                    // 3. Insert the pre-rendered composable HTML
                    unsafe { raw(renderedComposableHtml) }
                }
            }
            logger.info("SummonRenderer.render() completed successfully, result length: ${result.length}")
            return result
        } catch (e: Exception) {
            // Catch errors during overall page structure rendering (less likely now)
            logger.error("ERROR in SummonRenderer.render() structure generation: ${e.message}", e)
            return createErrorHtml("Page Structure Rendering Error", e)
        }
    }

    /**
     * Render an HTML fragment using Summon composables.
     * This is useful for HTMX partial updates.
     *
     * @param content The composable content lambda
     * @return The rendered HTML fragment as a string
     */
    fun render(content: @Composable () -> Unit): String {
        logger.info("SummonRenderer.render() called without title (component-only)")

        try {
            // Render the composable directly using Summon's SSR function
            val result = renderToString { content() }

            logger.info("SummonRenderer.render() completed successfully for component, result length: ${result.length}")
            return result
        } catch (e: Exception) {
            logger.error("ERROR in SummonRenderer.render() (component-only): ${e.message}", e)
            // Return an error message formatted as HTML
            return "<div style='color: red; padding: 1rem; border: 1px solid red;'>Rendering Error: ${e.message}<br><pre>${
                e.stackTraceToString().take(500)
            }...</pre></div>"
        }
    }

    /**
     * Internal method to render the static HTML page structure (head, body shell).
     */
    private fun HTML.renderPageStructure(
        title: String,
        description: String,
        includeChatScript: Boolean,
        bodyContentInserter: BODY.() -> Unit
    ) {
        logger.info("SummonRenderer.renderPageStructure() - Rendering static HTML shell")

        head {
            meta { charset = "UTF-8" }
            meta {
                name = "viewport"
                this.attributes["content"] = "width=device-width, initial-scale=1.0"
            }
            meta {
                name = "description"
                this.attributes["content"] = description
            }
            title(title)

            style {
                unsafe {
                    raw(getGlobalStyles())
                }
            }

            script {
                unsafe {
                    raw(
                        """
                        document.addEventListener('DOMContentLoaded', function() {
                            console.log("SummonRenderer: DOM content loaded");
                            
                            // Apply theme from localStorage if available
                            const storedTheme = localStorage.getItem('theme');
                            if (storedTheme) {
                                document.documentElement.setAttribute('data-theme', storedTheme);
                                const themeSelect = document.getElementById('theme-select');
                                if (themeSelect) themeSelect.value = storedTheme;
                            }
                            
                            // Add event listener for theme selector
                            const themeSelect = document.getElementById('theme-select');
                            if (themeSelect) {
                                themeSelect.addEventListener('change', function(e) {
                                    const theme = e.target.value;
                                    document.documentElement.setAttribute('data-theme', theme);
                                    localStorage.setItem('theme', theme);
                                });
                            }
                            
                            // Custom theme controls
                            const customControls = document.querySelectorAll('.custom-theme-control');
                            customControls.forEach(control => {
                                control.addEventListener('input', function(e) {
                                    const prop = e.target.getAttribute('data-property');
                                    const value = e.target.value;
                                    
                                    document.documentElement.style.setProperty(prop, value);
                                    localStorage.setItem(prop, value);
                                    
                                    // Update the color display
                                    const displayEl = document.querySelector(`[data-display="${'$'}prop"]`);
                                    if (displayEl) displayEl.textContent = value;
                                });
                                
                                // Load saved values from localStorage
                                const prop = control.getAttribute('data-property');
                                const savedValue = localStorage.getItem(prop);
                                if (savedValue) {
                                    control.value = savedValue;
                                    document.documentElement.style.setProperty(prop, savedValue);
                                    
                                    // Update the color display
                                    const displayEl = document.querySelector(`[data-display="${'$'}prop"]`);
                                    if (displayEl) displayEl.textContent = savedValue;
                                }
                            });
                        });
                        
                        console.log("SummonRenderer: Page is loading");
                    """
                    )
                }
            }

            if (includeChatScript) {
                script {
                    unsafe {
                        raw(chatJavaScript)
                    }
                }
            }
        }

        body {
            // Navigation bar
            nav {
                attributes["class"] = "nav"
                ul {
                    attributes["class"] = "nav-list"
                    li {
                        attributes["class"] = "nav-item"
                        a("/") {
                            attributes["class"] = "nav-link"
                            +"Home"
                        }
                    }
                    li {
                        attributes["class"] = "nav-item"
                        a("/users") {
                            attributes["class"] = "nav-link"
                            +"Users"
                        }
                    }
                    li {
                        attributes["class"] = "nav-item"
                        a("/dashboard") {
                            attributes["class"] = "nav-link"
                            +"Dashboard"
                        }
                    }
                    li {
                        attributes["class"] = "nav-item"
                        a("/theme") {
                            attributes["class"] = "nav-link"
                            +"Theme"
                        }
                    }
                    li {
                        attributes["class"] = "nav-item"
                        a("/chat") {
                            attributes["class"] = "nav-link"
                            +"Chat"
                        }
                    }
                }
            }

            // Add this message as a direct HTML message (for debugging)
            p { +"This message is added directly to the HTML output in SummonRenderer.renderPage()" }

            // Content container
            div {
                attributes["class"] = "content-container"

                try {
                    logger.info("SummonRenderer.renderPageStructure() - About to render content")

                    // Call the provided lambda using the correct outer receiver (@body)
                    this@body.bodyContentInserter()

                    logger.info("SummonRenderer.renderPageStructure() - Content rendered successfully")
                } catch (e: Exception) {
                    logger.error("ERROR in SummonRenderer.renderPageStructure() - content rendering: ${e.message}", e)

                    // Add an error message directly to the HTML
                    div {
                        attributes["style"] = "color: red; padding: 1rem; border: 1px solid red; margin: 1rem 0;"
                        h2 { +"Error Rendering Content" }
                        p { +"There was an error rendering the page content:" }
                        pre { +e.message.toString() }
                        pre { +e.stackTraceToString() }
                    }
                }
            }

            // Footer
            footer {
                attributes["class"] = "footer"
                p { +"© ${java.time.Year.now().value} Quarkus Example App" }
            }
        }

        logger.info("SummonRenderer.renderPageStructure() - HTML structure completed")
    }

    /**
     * Get the global CSS styles.
     */
    fun getGlobalStyles(): String {
        return """
            :root {
                /* Default Theme (Light) */
                --primary-color: #4695EB;
                --secondary-color: #FF4081;
                --background-color: #FFFFFF;
                --text-color: #333333;
                --card-bg-color: #FFFFFF;
                --panel-color: #FAFAFA;
                --border-color: #DDDDDD;
                --success-color: #4CAF50;
                --warning-color: #FF9800;
                --error-color: #F44336;
                --info-color: #2196F3;
            }
            
            /* Dark Theme */
            [data-theme="dark"] {
                --primary-color: #90CAF9;
                --secondary-color: #FF80AB;
                --background-color: #121212;
                --text-color: #E0E0E0;
                --card-bg-color: #1E1E1E;
                --panel-color: #262626;
                --border-color: #333333;
                --success-color: #81C784;
                --warning-color: #FFB74D;
                --error-color: #E57373;
                --info-color: #64B5F6;
            }
            
            /* Forest Theme */
            [data-theme="forest"] {
                --primary-color: #2E7D32;
                --secondary-color: #8D6E63;
                --background-color: #E8F5E9;
                --text-color: #33691E;
                --card-bg-color: #F1F8E9;
                --panel-color: #DCEDC8;
                --border-color: #AED581;
                --success-color: #689F38;
                --warning-color: #FF8F00;
                --error-color: #D32F2F;
                --info-color: #0288D1;
            }
            
            /* Ocean Theme */
            [data-theme="ocean"] {
                --primary-color: #0277BD;
                --secondary-color: #00BCD4;
                --background-color: #E1F5FE;
                --text-color: #01579B;
                --card-bg-color: #E1F5FE;
                --panel-color: #B3E5FC;
                --border-color: #81D4FA;
                --success-color: #009688;
                --warning-color: #F57F17;
                --error-color: #C2185B;
                --info-color: #0288D1;
            }
            
            body {
                margin: 0;
                padding: 0;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: var(--background-color);
                color: var(--text-color);
                line-height: 1.6;
            }
            
            .content-container {
                max-width: 1200px;
                margin: 0 auto;
                padding: 1rem;
            }
            
            .nav {
                background-color: var(--primary-color);
                color: white;
                padding: 1rem;
            }
            
            .nav-list {
                display: flex;
                list-style: none;
                margin: 0;
                padding: 0;
            }
            
            .nav-item {
                margin-right: 1.5rem;
            }
            
            .nav-link {
                color: white;
                text-decoration: none;
                font-weight: 600;
            }
            
            .nav-link:hover {
                text-decoration: underline;
            }
            
            h1, h2, h3, h4, h5, h6 {
                color: var(--primary-color);
            }
            
            .card {
                background-color: var(--card-bg-color);
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                padding: 1.5rem;
                margin-bottom: 1.5rem;
            }
            
            button, .btn {
                background-color: var(--primary-color);
                color: white;
                border: none;
                border-radius: 4px;
                padding: 0.5rem 1rem;
                cursor: pointer;
                font-size: 1rem;
            }
            
            button:hover, .btn:hover {
                opacity: 0.9;
            }
            
            input, textarea, select {
                width: 100%;
                padding: 0.75rem;
                border: 1px solid var(--border-color);
                border-radius: 4px;
                margin-bottom: 1rem;
                background-color: var(--background-color);
                color: var(--text-color);
            }
            
            table {
                width: 100%;
                border-collapse: collapse;
                margin: 1rem 0;
            }
            
            th, td {
                padding: 0.75rem;
                text-align: left;
                border-bottom: 1px solid var(--border-color);
            }
            
            th {
                background-color: var(--panel-color);
            }
            
            .footer {
                margin-top: 2rem;
                padding: 1rem;
                background-color: var(--panel-color);
                text-align: center;
                border-top: 1px solid var(--border-color);
            }
            
            /* Chat styles */
            #chat-container {
                display: none;
            }
            
            #chat-messages {
                height: 300px;
                overflow-y: auto;
                padding: 1rem;
                background-color: var(--card-bg-color);
                border: 1px solid var(--border-color);
                border-radius: 4px;
                margin-bottom: 1rem;
            }
            
            .message {
                padding: 8px 12px;
                margin: 5px 0;
                border-radius: 5px;
            }
            
            .incoming {
                background-color: var(--panel-color);
                align-self: flex-start;
            }
            
            .outgoing {
                background-color: var(--primary-color);
                color: white;
                align-self: flex-end;
                text-align: right;
            }
            
            #users-list {
                list-style-type: none;
                padding: 0;
            }
            
            /* Theme customization */
            .theme-options {
                display: flex;
                flex-wrap: wrap;
                gap: 1rem;
                margin: 1rem 0;
            }
            
            .custom-theme-section {
                margin: 2rem 0;
                padding: 1rem;
                background-color: var(--panel-color);
                border-radius: 8px;
            }
            
            .color-display {
                display: inline-block;
                min-width: 100px;
                font-family: monospace;
            }
            
            /* Responsive adjustments */
            @media (max-width: 768px) {
                .nav-list {
                    flex-direction: column;
                }
                
                .nav-item {
                    margin-right: 0;
                    margin-bottom: 0.5rem;
                }
                
                .theme-options {
                    flex-direction: column;
                }
            }
        """.trimIndent()
    }

    private fun createErrorHtml(errorTitle: String, e: Exception): String {
        return createHTML().html {
            head {
                title("Rendering Error")
            }
            body {
                h1 { +"Rendering Error" }
                p { +"There was an error rendering the page:" }
                pre { +e.message!! }
                pre { +e.stackTraceToString() }
                p { a("/") { +"Return to Home" } }
            }
        }
    }

    @Composable
    private fun ErrorComponent(title: String, e: Exception) {
        Box(modifier = Modifier().style("color", "red").padding("1rem")) {
            Text("Error: $title")
            Text("Message: ${e.message ?: "No details"}")
        }
    }
} 