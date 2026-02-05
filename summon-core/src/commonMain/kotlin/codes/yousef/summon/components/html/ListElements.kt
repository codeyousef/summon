package codes.yousef.summon.components.html

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * List-related HTML5 elements for creating ordered, unordered, and description lists.
 *
 * These components render actual HTML list elements (`<ul>`, `<ol>`, `<li>`, `<dl>`, etc.)
 * providing proper document structure and accessibility.
 */

// ============================================
// Unordered Lists
// ============================================

/**
 * Renders an HTML `<ul>` element - an unordered (bulleted) list.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content containing Li elements
 */
@Composable
fun Ul(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("ul", modifier, content)
}

// ============================================
// Ordered Lists
// ============================================

/**
 * Renders an HTML `<ol>` element - an ordered (numbered) list.
 *
 * @param start Optional starting number for the list
 * @param reversed Whether the list should be displayed in reverse order
 * @param type Optional list marker type: "1" (numbers), "a" (lowercase letters),
 *             "A" (uppercase letters), "i" (lowercase roman), "I" (uppercase roman)
 * @param modifier Styling and attributes to apply
 * @param content Composable content containing Li elements
 */
@Composable
fun Ol(
    start: Int? = null,
    reversed: Boolean = false,
    type: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier
    if (start != null) finalModifier = finalModifier.attribute("start", start.toString())
    if (reversed) finalModifier = finalModifier.attribute("reversed", "")
    if (type != null) finalModifier = finalModifier.attribute("type", type)
    renderer.renderHtmlTag("ol", finalModifier, content)
}

// ============================================
// List Items
// ============================================

/**
 * Renders an HTML `<li>` element - a list item.
 *
 * Must be used inside Ul, Ol, or Menu elements.
 *
 * @param value Optional value attribute for ordered lists (overrides the list's numbering)
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the list item
 */
@Composable
fun Li(
    value: Int? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = if (value != null) {
        modifier.attribute("value", value.toString())
    } else {
        modifier
    }
    renderer.renderHtmlTag("li", finalModifier, content)
}

// ============================================
// Description Lists
// ============================================

/**
 * Renders an HTML `<dl>` element - a description list.
 *
 * Contains groups of term-description pairs (Dt and Dd elements).
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content containing Dt and Dd elements
 */
@Composable
fun Dl(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("dl", modifier, content)
}

/**
 * Renders an HTML `<dt>` element - a description term.
 *
 * Must be used inside a Dl element, followed by one or more Dd elements.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the term
 */
@Composable
fun Dt(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("dt", modifier, content)
}

/**
 * Renders an HTML `<dd>` element - a description details.
 *
 * Must be used inside a Dl element, following one or more Dt elements.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the description
 */
@Composable
fun Dd(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("dd", modifier, content)
}

// ============================================
// Menu (context menu list)
// ============================================

/**
 * Renders an HTML `<menu>` element - a list of commands.
 *
 * Semantically equivalent to ul but intended for interactive lists of commands.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content containing Li elements
 */
@Composable
fun Menu(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("menu", modifier, content)
}

// Helper extension function
private fun Modifier.attribute(name: String, value: String): Modifier {
    return Modifier(
        styles = this.styles,
        attributes = this.attributes + (name to value),
        eventHandlers = this.eventHandlers
    )
}
