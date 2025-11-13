package codes.yousef.summon.integration.quarkus

import jakarta.servlet.http.HttpServlet
import org.jboss.logging.Logger

/**
 * Quarkus Extension for Summon - Provides integration with Quarkus for server-side rendering.
 */
object QuarkusExtension {

    // Constants
    const val FEATURE = "summon"

    /**
     * Get the servlet class for web page rendering.
     * This is needed for Quarkus to detect the servlet.
     */
    fun getServletClass(): Class<out HttpServlet> {
        return SummonServlet::class.java
    }

    /**
     * A simple Summon renderer that can be injected via CDI.
     * This is a temporary implementation until we resolve issues with the complete renderer.
     */
    class SummonRenderer {
        private val logger = Logger.getLogger(SummonRenderer::class.java)

        /**
         * Renders a simple HTML template
         */
        fun renderTemplate(title: String, content: String): String {
            logger.info("Rendering template with title: $title")
            logger.info("Content length: ${content.length} characters")

            val result = """
                <!DOCTYPE html>
                <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <meta name="description" content="A demonstration of Summon with Quarkus">
                        <title>$title</title>
                        <!-- Configure HTMX before loading it -->
                        <script>
                            // Create a global object to configure HTMX
                            window.htmxConfig = {
                                // Enable debug mode to log more information
                                debug: true,
                                // Set a longer timeout for AJAX requests
                                timeout: 10000,
                                // Configure HTMX to process the document on load
                                processOnLoad: true,
                                // Configure HTMX to include additional headers in requests
                                headers: {
                                    'X-Requested-With': 'XMLHttpRequest'
                                },
                                // Log all HTMX events
                                logger: function(elt, event, data) {
                                    if (console) {
                                        console.log("HTMX Event:", event, "Data:", data, "Element:", elt);
                                    }
                                }
                            };
                        </script>
                        <!-- Load HTMX in the head to ensure it's available before components are injected -->
                        <script src="https://unpkg.com/htmx.org@1.9.12"></script>
                        <style>
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

                            /* HTMX indicator styles */
                            .htmx-indicator {
                              opacity: 0;
                              transition: opacity 200ms ease-in;
                            }
                            .htmx-request .htmx-indicator {
                              opacity: 1;
                              display: inline-block !important;
                            }
                            .htmx-request.htmx-indicator {
                              opacity: 1;
                              display: inline-block !important;
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
                            h1 {
                              color: var(--primary-color);
                              margin-bottom: 1rem;
                            }
                            button {
                              background-color: var(--primary-color);
                              color: white;
                              border: none;
                              border-radius: 4px;
                              padding: 8px 16px;
                              cursor: pointer;
                              transition: background-color 0.3s;
                              font-size: 1rem;
                            }
                            button:hover {
                              background-color: #3a85d8;
                            }
                            .nav {
                              background-color: var(--primary-color);
                              color: white;
                              padding: 1rem;
                              margin-bottom: 1rem;
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
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <nav class="nav">
                                <ul class="nav-list">
                                    <li class="nav-item"><a class="nav-link" href="/" hx-get="/" hx-target="body" hx-swap="innerHTML">Home</a></li>
                                    <li class="nav-item"><a class="nav-link" href="/dashboard" hx-get="/dashboard" hx-target="body" hx-swap="innerHTML">Dashboard</a></li>
                                    <li class="nav-item"><a class="nav-link" href="/theme" hx-get="/theme" hx-target="body" hx-swap="innerHTML">Theme</a></li>
                                    <li class="nav-item"><a class="nav-link" href="/chat" hx-get="/chat" hx-target="body" hx-swap="innerHTML">Chat</a></li>
                                </ul>
                            </nav>
                            <div id="app">
                                $content
                            </div>
                        </div>
                        <!-- Add a script to log any HTMX errors -->
                        <script>
                            // Log any HTMX errors
                            document.body.addEventListener('htmx:responseError', function(event) {
                                console.error("HTMX Response Error:", event.detail);
                            });

                            document.body.addEventListener('htmx:sendError', function(event) {
                                console.error("HTMX Send Error:", event.detail);
                            });

                            document.body.addEventListener('htmx:afterRequest', function(event) {
                                console.log("HTMX After Request:", event.detail);
                            });

                            document.body.addEventListener('htmx:beforeRequest', function(event) {
                                console.log("HTMX Before Request:", event.detail);
                            });

                            // Log HTMX initialization status
                            console.log("HTMX Loaded:", typeof htmx !== 'undefined');
                            if (typeof htmx !== 'undefined') {
                                console.log("HTMX Version:", htmx.version);
                                console.log("HTMX Config:", htmx.config);

                                // Manually process the document to ensure HTMX attributes are processed
                                setTimeout(function() {
                                    console.log("Manually processing document with HTMX");
                                    htmx.process(document.body);

                                    // Log all elements with HTMX attributes
                                    const htmxElements = document.querySelectorAll("[hx-get], [hx-post], [hx-put], [hx-delete]");
                                    console.log("Found", htmxElements.length, "elements with HTMX attributes after manual processing");

                                    // Add click event listeners to all buttons with HTMX attributes
                                    document.querySelectorAll("button[hx-get], button[hx-post]").forEach(function(button) {
                                        console.log("Adding debug click listener to button:", button.id || button.textContent);
                                        button.addEventListener('click', function(event) {
                                            console.log("Button clicked:", this.id || this.textContent);
                                            console.log("HTMX attributes:", {
                                                hxGet: this.getAttribute('hx-get'),
                                                hxPost: this.getAttribute('hx-post'),
                                                hxTarget: this.getAttribute('hx-target'),
                                                hxSwap: this.getAttribute('hx-swap'),
                                                hxTrigger: this.getAttribute('hx-trigger')
                                            });
                                        });
                                    });
                                }, 500);
                            }
                        </script>
                    </body>
                </html>
            """.trimIndent()

            logger.info("Finished rendering template, result length: ${result.length} characters")
            return result
        }

        /**
         * Renders a heading element
         */
        fun renderHeading(level: Int, text: String): String {
            return "<h$level>$text</h$level>"
        }

        /**
         * Renders a paragraph element
         */
        fun renderParagraph(text: String): String {
            return "<p>$text</p>"
        }

        /**
         * Renders a button element
         */
        fun renderButton(label: String, onClick: String = ""): String {
            val onClickAttr = if (onClick.isNotEmpty()) " onclick=\"$onClick\"" else ""
            return "<button class=\"btn\"$onClickAttr>$label</button>"
        }
    }
} 
