package code.yousef.summon.integrations.quarkus

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet

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
        
        /**
         * Renders a simple HTML template
         */
        fun renderTemplate(title: String, content: String): String {
            return """
                <!DOCTYPE html>
                <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <meta name="description" content="A demonstration of Summon with Quarkus">
                        <title>$title</title>
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
                            @media (max-width: 768px) {
                              .row {
                                flex-direction: column;
                              }
                              .col {
                                margin-bottom: 1rem;
                              }
                            }
                        </style>
                    </head>
                    <body>
                        <div id="app">
                            $content
                        </div>
                        <script src="https://unpkg.com/htmx.org@1.9.12"></script>
                    </body>
                </html>
            """.trimIndent()
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