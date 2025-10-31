package code.yousef.summon.integration.quarkus.htmx

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.layout.Box
import code.yousef.summon.integration.quarkus.qute.QuteTemplateRenderer
import code.yousef.summon.modifier.Modifier
import io.quarkus.qute.Template

/**
 * Composable components that integrate HTMX with Qute templates.
 * These components combine the HTMX attribute handling with Qute template rendering.
 */

/**
 * A composable function that renders a Qute template with HTMX attributes.
 * This function is useful for creating components that update via HTMX.
 *
 * @param template The Qute template to render
 * @param data Map of data to pass to the template
 * @param hxGet The HTMX GET endpoint (optional)
 * @param hxPost The HTMX POST endpoint (optional)
 * @param hxTrigger The HTMX trigger event (optional)
 * @param hxTarget The HTMX target element (optional)
 * @param hxSwap The HTMX swap method (optional)
 * @param modifier Additional modifiers to apply to the container
 */
@Composable
fun HtmxQuteTemplate(
    template: Template,
    data: Map<String, Any>,
    hxGet: String? = null,
    hxPost: String? = null,
    hxTrigger: String? = null,
    hxTarget: String? = null,
    hxSwap: String? = null,
    modifier: Modifier = Modifier()
) {
    // Render the template
    val html = QuteTemplateRenderer.renderTemplate(template, data)

    // Apply HTMX attributes
    var htmxModifier = modifier
    if (hxGet != null) htmxModifier = htmxModifier.htmlAttribute("hx-get", hxGet)
    if (hxPost != null) htmxModifier = htmxModifier.htmlAttribute("hx-post", hxPost)
    if (hxTrigger != null) htmxModifier = htmxModifier.htmlAttribute("hx-trigger", hxTrigger)
    if (hxTarget != null) htmxModifier = htmxModifier.htmlAttribute("hx-target", hxTarget)
    if (hxSwap != null) htmxModifier = htmxModifier.htmlAttribute("hx-swap", hxSwap)

    // Use the htmlAttribute extension function to add the raw HTML content
    Box(htmxModifier.htmlAttribute("__raw_html", html)) {
        // Empty content as the HTML is provided via the __raw_html attribute
    }
}

/**
 * A composable function that renders a Qute template with HTMX attributes.
 * This overload allows for a more concise syntax with varargs.
 *
 * @param template The Qute template to render
 * @param hxGet The HTMX GET endpoint (optional)
 * @param hxPost The HTMX POST endpoint (optional)
 * @param hxTrigger The HTMX trigger event (optional)
 * @param hxTarget The HTMX target element (optional)
 * @param hxSwap The HTMX swap method (optional)
 * @param modifier Additional modifiers to apply to the container
 * @param pairs Pairs of data to pass to the template
 */
@Composable
fun HtmxQuteTemplate(
    template: Template,
    hxGet: String? = null,
    hxPost: String? = null,
    hxTrigger: String? = null,
    hxTarget: String? = null,
    hxSwap: String? = null,
    modifier: Modifier = Modifier(),
    vararg pairs: Pair<String, Any>
) {
    HtmxQuteTemplate(
        template = template,
        data = pairs.toMap(),
        hxGet = hxGet,
        hxPost = hxPost,
        hxTrigger = hxTrigger,
        hxTarget = hxTarget,
        hxSwap = hxSwap,
        modifier = modifier
    )
}

/**
 * A composable function that renders a Qute template that loads content from an HTMX endpoint.
 *
 * @param id The ID of the container element
 * @param template The Qute template to render
 * @param data Map of data to pass to the template
 * @param endpoint The endpoint to load content from
 * @param trigger The HTMX trigger event (default: "load")
 * @param target The HTMX target element (optional)
 * @param swap The HTMX swap method (optional)
 * @param modifier Additional modifiers to apply to the container
 */
@Composable
fun HtmxQuteContainer(
    id: String,
    template: Template,
    data: Map<String, Any>,
    endpoint: String,
    trigger: String = "load",
    target: String? = null,
    swap: String? = null,
    modifier: Modifier = Modifier()
) {
    // Render the template
    val html = QuteTemplateRenderer.renderTemplate(template, data)

    // Create a modifier with HTMX attributes
    val htmxModifier = modifier
        .htmlAttribute("id", id)
        .htmxGet(endpoint, target, swap, trigger)

    // Use the htmlAttribute extension function to add the raw HTML content
    Box(htmxModifier.htmlAttribute("__raw_html", html)) {
        // Empty content as the HTML is provided via the __raw_html attribute
    }
}

/**
 * A composable function that renders a Qute template that loads content from an HTMX endpoint.
 * This overload allows for a more concise syntax with varargs.
 *
 * @param id The ID of the container element
 * @param template The Qute template to render
 * @param endpoint The endpoint to load content from
 * @param trigger The HTMX trigger event (default: "load")
 * @param target The HTMX target element (optional)
 * @param swap The HTMX swap method (optional)
 * @param modifier Additional modifiers to apply to the container
 * @param pairs Pairs of data to pass to the template
 */
@Composable
fun HtmxQuteContainer(
    id: String,
    template: Template,
    endpoint: String,
    trigger: String = "load",
    target: String? = null,
    swap: String? = null,
    modifier: Modifier = Modifier(),
    vararg pairs: Pair<String, Any>
) {
    HtmxQuteContainer(
        id = id,
        template = template,
        data = pairs.toMap(),
        endpoint = endpoint,
        trigger = trigger,
        target = target,
        swap = swap,
        modifier = modifier
    )
}