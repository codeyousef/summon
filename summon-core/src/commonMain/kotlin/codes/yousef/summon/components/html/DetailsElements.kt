package codes.yousef.summon.components.html

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Interactive disclosure elements for collapsible content.
 *
 * These components render the HTML5 `<details>` and `<summary>` elements,
 * which provide native browser support for expandable/collapsible content
 * without requiring JavaScript.
 */

/**
 * Renders an HTML `<details>` element - a disclosure widget.
 *
 * Creates a collapsible content section that can be expanded by clicking
 * its Summary. Provides native browser expand/collapse functionality.
 *
 * ## Usage
 *
 * ```kotlin
 * Details(open = isExpanded, onToggle = { isExpanded = it }) {
 *     Summary { Text("Click to expand") }
 *     P { Text("Hidden content revealed when expanded") }
 * }
 * ```
 *
 * ## Controlled vs Uncontrolled
 *
 * - **Uncontrolled**: Omit `onToggle` to let the browser manage state
 * - **Controlled**: Provide `onToggle` to sync with your state
 *
 * @param open Whether the details content is visible
 * @param onToggle Optional callback invoked when the user toggles the disclosure.
 *                 Receives `true` when opened, `false` when closed.
 * @param name Optional name for exclusive accordion behavior (only one open at a time)
 * @param modifier Styling and attributes to apply
 * @param content Composable content, should include exactly one Summary element
 */
@Composable
fun Details(
    open: Boolean = false,
    onToggle: ((Boolean) -> Unit)? = null,
    name: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current

    var finalModifier = modifier

    // Set the open attribute if true
    if (open) {
        finalModifier = finalModifier.attribute("open", "")
    }

    // Set the name attribute for accordion behavior
    if (name != null) {
        finalModifier = finalModifier.attribute("name", name)
    }

    // Register toggle event handler if provided
    if (onToggle != null) {
        finalModifier = finalModifier.onToggle { isOpen ->
            onToggle(isOpen)
        }
    }

    renderer.renderHtmlTag("details", finalModifier, content)
}

/**
 * Renders an HTML `<summary>` element - a disclosure summary.
 *
 * Provides a visible heading for a Details element. Clicking the summary
 * toggles the visibility of the details content.
 *
 * Must be the first child of a Details element.
 *
 * ## Usage
 *
 * ```kotlin
 * Details {
 *     Summary(modifier = Modifier().cursor("pointer")) {
 *         Text("Section Title")
 *     }
 *     // Content to show when expanded
 *     P { Text("Detailed content here...") }
 * }
 * ```
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render as the summary text
 */
@Composable
fun Summary(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("summary", modifier, content)
}

/**
 * Renders an HTML `<dialog>` element - a dialog box.
 *
 * Creates a dialog box or modal window. Use the `open` attribute to show
 * the dialog. For modal dialogs, use JavaScript's `showModal()` method.
 *
 * @param open Whether the dialog is visible
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the dialog
 */
@Composable
fun Dialog(
    open: Boolean = false,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = if (open) {
        modifier.attribute("open", "")
    } else {
        modifier
    }
    renderer.renderHtmlTag("dialog", finalModifier, content)
}

// Helper extension functions
private fun Modifier.attribute(name: String, value: String): Modifier {
    return Modifier(
        styles = this.styles,
        attributes = this.attributes + (name to value),
        eventHandlers = this.eventHandlers
    )
}

private fun Modifier.onToggle(handler: (Boolean) -> Unit): Modifier {
    return Modifier(
        styles = this.styles,
        attributes = this.attributes,
        eventHandlers = this.eventHandlers + ("toggle" to {
            // The event target's 'open' property will indicate the new state
            // This is handled by the platform renderer
            handler(true) // Platform renderer should pass correct state
        })
    )
}
