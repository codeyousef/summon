package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.integrations.quarkus.QuarkusExtension
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.JvmPlatformRenderer
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.platform.renderToString
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.jboss.logging.Logger

/**
 * A renderer for HTML components integrated with Quarkus.
 * This class handles the transformation of components into HTML.
 * It uses the SummonRenderer from the Summon library's Quarkus integration.
 */
@ApplicationScoped
class SummonRenderer {
    private val logger = Logger.getLogger(SummonRenderer::class.java)

    @Inject
    private lateinit var summonRenderer: QuarkusExtension.SummonRenderer

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
     * Helper method to provide the LocalPlatformRenderer CompositionLocal and render the content.
     * This method renders just the composable content without generating a complete HTML document.
     * 
     * @param content The composable content lambda
     * @return The rendered HTML as a string
     */
    private fun renderWithPlatformRenderer(content: @Composable () -> Unit): String {
        // Create a JvmPlatformRenderer instance
        val renderer = JvmPlatformRenderer()

        // Provide the renderer to the LocalPlatformRenderer CompositionLocal
        val provided = LocalPlatformRenderer.provides(renderer)

        // Use renderComposableRoot to generate the complete HTML document
        val fullHtml = renderer.renderComposableRoot { 
            provided.run {
                content()
            }
        }

        // Extract just the content from the body tag using a regular expression
        val bodyContentRegex = Regex("<body>(.*?)</body>", RegexOption.DOT_MATCHES_ALL)
        val bodyContentMatch = bodyContentRegex.find(fullHtml)

        // Return just the content, or the full HTML if the regex doesn't match
        return bodyContentMatch?.groupValues?.get(1)?.trim() ?: fullHtml
    }

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
            // Use the Summon library's QuarkusExtension.SummonRenderer to render the content
            logger.info("SummonRenderer.render() - Rendering composable content using Summon's QuarkusExtension...")

            // Render the composable content to a string
            val renderedComposableHtml = try {
                renderWithPlatformRenderer(content)
            } catch (e: Exception) {
                logger.error("ERROR rendering composable content: ${e.message}", e)
                // Provide error details within the page structure
                renderWithPlatformRenderer { ErrorComponent("Composable Rendering Error", e) }
            }

            // Use the Summon library's QuarkusExtension.SummonRenderer to render the template
            val result = summonRenderer.renderTemplate(title, renderedComposableHtml)

            logger.info("SummonRenderer.render() completed successfully, result length: ${result.length}")
            return result
        } catch (e: Exception) {
            // Catch errors during overall page structure rendering
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
            // Render the composable directly using Summon's SSR function with platform renderer
            val result = renderWithPlatformRenderer(content)

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

    // The renderPageStructure and getGlobalStyles methods have been removed
    // as we're now using the QuarkusExtension.SummonRenderer from the Summon library

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
        // The LocalPlatformRenderer CompositionLocal is now provided by the renderWithPlatformRenderer helper method
        Box(modifier = Modifier().style("color", "red").padding("1rem")) {
            Text("Error: $title")
            Text("Message: ${e.message ?: "No details"}")
        }
    }
} 
