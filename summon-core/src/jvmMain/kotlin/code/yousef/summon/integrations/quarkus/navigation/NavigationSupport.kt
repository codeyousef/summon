package code.yousef.summon.integration.quarkus.navigation

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.layout.Box
import code.yousef.summon.integration.quarkus.htmx.htmx
import code.yousef.summon.integration.quarkus.htmx.htmxGet
import code.yousef.summon.modifier.Modifier

/**
 * Navigation support for Summon with Quarkus.
 * This class provides utilities for handling navigation in Summon applications with Quarkus.
 */
object NavigationSupport {

    /**
     * JavaScript code for handling navigation in Summon applications with Quarkus.
     * This code is injected into the HTML template to ensure components are properly initialized after navigation.
     */
    val navigationScript = """
        <script>
            // Function to process components after navigation
            function processComponents() {
                console.log('Processing components after navigation');
                
                // Find all components that need to be processed
                const components = document.querySelectorAll('[data-summon-component]');
                console.log('Found ' + components.length + ' components to process');
                
                // Process each component
                components.forEach(function(component) {
                    const componentType = component.getAttribute('data-summon-component');
                    const componentId = component.getAttribute('id');
                    console.log('Processing component: ' + componentType + ' (ID: ' + componentId + ')');
                    
                    // Handle different component types
                    switch(componentType) {
                        case 'htmx-container':
                            // Trigger HTMX to load the content
                            if (typeof htmx !== 'undefined') {
                                console.log('Triggering HTMX load for container: ' + componentId);
                                htmx.trigger(component, 'load');
                            } else {
                                console.error('HTMX not loaded, cannot process container: ' + componentId);
                            }
                            break;
                        
                        case 'qute-template':
                            // Qute templates are static, no processing needed
                            console.log('Qute template component, no processing needed: ' + componentId);
                            break;
                            
                        default:
                            console.log('Unknown component type: ' + componentType);
                    }
                });
            }
            
            // Process components after navigation
            document.body.addEventListener('htmx:afterSwap', function(event) {
                console.log('Navigation occurred (htmx:afterSwap), processing components');
                processComponents();
            });
            
            // Handle browser back/forward navigation
            window.addEventListener('popstate', function(event) {
                console.log('Browser navigation occurred (popstate), processing components');
                processComponents();
            });
            
            // Process components on page load
            document.addEventListener('DOMContentLoaded', function() {
                console.log('Page loaded (DOMContentLoaded), processing components');
                processComponents();
            });
            
            // Process components immediately if the document is already loaded
            if (document.readyState === 'complete' || document.readyState === 'interactive') {
                console.log('Document already loaded, processing components immediately');
                setTimeout(processComponents, 0);
            }
            
            // Log HTMX events for debugging
            document.body.addEventListener('htmx:beforeRequest', function(event) {
                console.log('HTMX beforeRequest:', event.detail);
            });
            
            document.body.addEventListener('htmx:afterRequest', function(event) {
                console.log('HTMX afterRequest:', event.detail);
            });
            
            document.body.addEventListener('htmx:responseError', function(event) {
                console.error('HTMX responseError:', event.detail);
            });
            
            document.body.addEventListener('htmx:sendError', function(event) {
                console.error('HTMX sendError:', event.detail);
            });
        </script>
    """.trimIndent()
}

/**
 * A composable function that creates a navigation link using HTMX.
 * This function creates a link that navigates to the specified URL using HTMX.
 *
 * @param href The URL to navigate to
 * @param label The link text
 * @param target The target element to update (default: "body")
 * @param swap The swap method to use (default: "innerHTML")
 * @param pushUrl Whether to update the browser URL (default: true)
 * @param modifier Additional modifiers to apply to the link
 */
@Composable
fun NavigationLink(
    href: String,
    label: String,
    target: String = "body",
    swap: String = "innerHTML",
    pushUrl: Boolean = true,
    modifier: Modifier = Modifier()
) {
    // Create a modifier with the HTMX attributes
    var linkModifier = modifier.htmxGet(
        get = href,
        target = target,
        swap = swap
    )

    // Add push-url attribute if needed
    if (pushUrl) {
        linkModifier = linkModifier.htmx("push-url", "true")
    }

    // Use the standard Link component with the HTMX attributes
    Box(linkModifier.style("href", "#")) {
        code.yousef.summon.components.display.Text(label)
    }
}

/**
 * A composable function that creates a navigation button using HTMX.
 * This function creates a button that navigates to the specified URL using HTMX.
 *
 * @param href The URL to navigate to
 * @param label The button text
 * @param target The target element to update (default: "body")
 * @param swap The swap method to use (default: "innerHTML")
 * @param pushUrl Whether to update the browser URL (default: true)
 * @param modifier Additional modifiers to apply to the button
 */
@Composable
fun NavigationButton(
    href: String,
    label: String,
    target: String = "body",
    swap: String = "innerHTML",
    pushUrl: Boolean = true,
    modifier: Modifier = Modifier()
) {
    // Create a modifier with the HTMX attributes
    var buttonModifier = modifier.htmxGet(
        get = href,
        target = target,
        swap = swap
    )

    // Add push-url attribute if needed
    if (pushUrl) {
        buttonModifier = buttonModifier.htmx("push-url", "true")
    }

    // Use the standard Button component with the HTMX attributes
    code.yousef.summon.components.input.Button(
        label = label,
        onClick = { /* HTMX will handle the click */ },
        modifier = buttonModifier
    )
}