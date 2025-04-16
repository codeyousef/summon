package code.yousef.example.quarkus.components

import code.yousef.example.quarkus.currentConsumer
import code.yousef.example.quarkus.utils.backgroundColor
import code.yousef.example.quarkus.utils.padding
import code.yousef.summon.components.layout.Box
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.color
import code.yousef.summon.modifier.fontFamily
import code.yousef.summon.modifier.lineHeight
import code.yousef.summon.modifier.margin
import code.yousef.summon.modifier.minHeight
import code.yousef.summon.runtime.Composable
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.HEAD
import kotlinx.html.TagConsumer
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.title
import kotlinx.html.unsafe

/**
 * ThemeWrapper component that provides consistent styling and structure for all pages
 */
@Composable
fun ThemeWrapper(content: @Composable () -> Unit) {
    // Add meta tags and stylesheets to head
    val consumer = currentConsumer()
    
    // Instead of trying to use head{} directly, we'll use the style attribute
    // to inject the CSS and meta tags into the document
    Box(
        modifier = Modifier()
            .style("html", """
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Quarkus + Summon Demo</title>
                    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
                    <script src="https://unpkg.com/htmx.org@1.9.10" defer></script>
                    <link rel="stylesheet" href="/styles.css">
                    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
                </head>
            """)
    ) {}

    // Add global styles
    Box(
        modifier = Modifier()
            .fontFamily("'Inter', sans-serif")
            .lineHeight("1.5")
            .color("#212529")
            .backgroundColor("#f8f9fa")
            .margin("0")
            .padding("0")
            .minHeight("100vh")
    ) {
        // Content of page
        content()
    }

    // Global scripts for theme
    consumer.script {
        unsafe {
            +"""
            document.addEventListener('DOMContentLoaded', function() {
                // Global theme initialization
                console.log('Theme initialized');
                
                // Fade in page content
                const body = document.body;
                body.style.opacity = 0;
                setTimeout(function() {
                    body.style.transition = 'opacity 0.3s ease';
                    body.style.opacity = 1;
                }, 50);
            });
            
            // Handle HTMX transitions
            document.addEventListener('htmx:beforeSwap', function() {
                document.body.style.opacity = 0;
            });
            
            document.addEventListener('htmx:afterSwap', function() {
                setTimeout(function() {
                    document.body.style.opacity = 1;
                }, 50);
                
                // Initialize dropdown toggles
                if (typeof toggleDropdown === 'function') {
                    console.log('Initialized dropdown');
                }
            });
            
            // Global error handler
            document.addEventListener('htmx:responseError', function(event) {
                console.error('HTMX Error:', event.detail.error);
                const errorContainer = document.createElement('div');
                errorContainer.className = 'global-error';
                errorContainer.style.position = 'fixed';
                errorContainer.style.top = '20px';
                errorContainer.style.left = '50%';
                errorContainer.style.transform = 'translateX(-50%)';
                errorContainer.style.backgroundColor = '#f8d7da';
                errorContainer.style.color = '#842029';
                errorContainer.style.padding = '12px 24px';
                errorContainer.style.borderRadius = '4px';
                errorContainer.style.boxShadow = '0 4px 6px rgba(0, 0, 0, 0.1)';
                errorContainer.style.zIndex = '9999';
                
                const status = event.detail.xhr.status;
                errorContainer.textContent = status === 404 
                    ? 'Page not found' 
                    : 'An error occurred. Please try again.';
                
                document.body.appendChild(errorContainer);
                
                setTimeout(function() {
                    errorContainer.style.opacity = '0';
                    errorContainer.style.transition = 'opacity 0.5s ease';
                    setTimeout(function() {
                        errorContainer.remove();
                    }, 500);
                }, 3000);
            });
            """
        }
    }
} 