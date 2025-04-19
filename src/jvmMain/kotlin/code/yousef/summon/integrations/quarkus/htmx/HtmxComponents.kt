package code.yousef.summon.integrations.quarkus.htmx

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier

/**
 * Composable components for HTMX integration.
 * These components use the HtmxAttributeHandler to properly handle HTMX attributes.
 */

/**
 * A button that triggers an HTMX request.
 * 
 * @param label The button label
 * @param endpoint The endpoint to request
 * @param method The HTTP method to use (get, post, put, delete)
 * @param target The target element to update (optional)
 * @param swap The swap method to use (optional)
 * @param trigger The trigger event (default: "click")
 * @param indicator The ID of an element to use as an indicator (optional)
 * @param confirm A confirmation message to show before sending the request (optional)
 * @param modifier Additional modifiers to apply
 */
@Composable
fun HtmxButton(
    label: String,
    endpoint: String,
    method: String = "get",
    target: String? = null,
    swap: String? = null,
    trigger: String? = "click",
    indicator: String? = null,
    confirm: String? = null,
    modifier: Modifier = Modifier()
) {
    // Apply HTMX attributes based on the method
    val htmxModifier = when (method.lowercase()) {
        "post" -> modifier.htmxPost(endpoint, target, swap, trigger)
        "put" -> modifier.htmx("put", endpoint)
        "delete" -> modifier.htmx("delete", endpoint)
        else -> modifier.htmxGet(endpoint, target, swap, trigger)
    }
    
    // Apply additional HTMX attributes if provided
    val finalModifier = htmxModifier.apply {
        if (indicator != null) htmx("indicator", indicator)
        if (confirm != null) htmx("confirm", confirm)
    }
    
    // Render the button with HTMX attributes
    Button(
        label = label,
        onClick = { /* HTMX will handle this */ },
        modifier = finalModifier
    )
}

/**
 * A container that loads content from an HTMX endpoint.
 * 
 * @param id The ID of the container element
 * @param endpoint The endpoint to load content from
 * @param trigger The HTMX trigger event (default: "load")
 * @param target The HTMX target element (optional)
 * @param swap The HTMX swap method (optional)
 * @param minHeight The minimum height of the container (default: "200px")
 * @param loadingText The text to display while loading (default: "Loading...")
 * @param modifier Additional modifiers to apply
 */
@Composable
fun HtmxContainer(
    id: String,
    endpoint: String,
    trigger: String = "load",
    target: String? = null,
    swap: String? = null,
    minHeight: String = "200px",
    loadingText: String = "Loading...",
    modifier: Modifier = Modifier()
) {
    // Create a modifier with HTMX attributes
    val htmxModifier = modifier
        .htmlAttribute("id", id)
        .htmxGet(endpoint, target, swap, trigger)
        .style("min-height", minHeight)
    
    // Render a Box with the HTMX attributes
    Box(htmxModifier) {
        Text(loadingText)
    }
}

/**
 * A form that submits via HTMX.
 * 
 * @param action The form action (endpoint)
 * @param method The HTTP method to use (default: "post")
 * @param target The target element to update (optional)
 * @param swap The swap method to use (optional)
 * @param trigger The trigger event (default: "submit")
 * @param modifier Additional modifiers to apply
 * @param content The form content
 */
@Composable
fun HtmxForm(
    action: String,
    method: String = "post",
    target: String? = null,
    swap: String? = null,
    trigger: String? = "submit",
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    // Apply HTMX attributes based on the method
    val htmxModifier = when (method.lowercase()) {
        "get" -> modifier.htmxGet(action, target, swap, trigger)
        "put" -> modifier.htmx("put", action)
        "delete" -> modifier.htmx("delete", action)
        else -> modifier.htmxPost(action, target, swap, trigger)
    }
    
    // Render a Box with the HTMX attributes
    Box(htmxModifier) {
        content()
    }
}

/**
 * A component that shows a loading indicator during HTMX requests.
 * 
 * @param id The ID of the indicator element
 * @param text The text to display (default: "Loading...")
 * @param modifier Additional modifiers to apply
 */
@Composable
fun HtmxIndicator(
    id: String,
    text: String = "Loading...",
    modifier: Modifier = Modifier()
) {
    Box(
        modifier = modifier
            .htmlAttribute("id", id)
            .htmlAttribute("class", "htmx-indicator")
            .style("display", "none")
    ) {
        Text(text)
    }
}

/**
 * A component that triggers an HTMX request on a specific event.
 * 
 * @param event The event to trigger on (e.g., "click", "load", "revealed")
 * @param endpoint The endpoint to request
 * @param method The HTTP method to use (get, post, put, delete)
 * @param target The target element to update (optional)
 * @param swap The swap method to use (optional)
 * @param modifier Additional modifiers to apply
 * @param content The content of the component
 */
@Composable
fun HtmxTrigger(
    event: String,
    endpoint: String,
    method: String = "get",
    target: String? = null,
    swap: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    // Apply HTMX attributes based on the method
    val htmxModifier = when (method.lowercase()) {
        "post" -> modifier.htmxPost(endpoint, target, swap, event)
        "put" -> modifier.htmx("put", endpoint).htmx("trigger", event)
        "delete" -> modifier.htmx("delete", endpoint).htmx("trigger", event)
        else -> modifier.htmxGet(endpoint, target, swap, event)
    }
    
    // Render a Box with the HTMX attributes
    Box(htmxModifier) {
        content()
    }
}

/**
 * A component that updates its content from an HTMX endpoint at regular intervals.
 * 
 * @param endpoint The endpoint to request
 * @param interval The interval in milliseconds (default: 2000)
 * @param target The target element to update (optional)
 * @param swap The swap method to use (optional)
 * @param modifier Additional modifiers to apply
 * @param content The initial content of the component
 */
@Composable
fun HtmxPolling(
    endpoint: String,
    interval: Int = 2000,
    target: String? = null,
    swap: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    // Create a trigger string for polling
    val trigger = "every ${interval}ms"
    
    // Apply HTMX attributes for polling
    val htmxModifier = modifier.htmxGet(endpoint, target, swap, trigger)
    
    // Render a Box with the HTMX attributes
    Box(htmxModifier) {
        content()
    }
}