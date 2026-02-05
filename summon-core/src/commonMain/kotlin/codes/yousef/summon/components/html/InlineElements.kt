package codes.yousef.summon.components.html

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Inline HTML5 elements for semantic text markup.
 *
 * These components render actual HTML inline elements (`<a>`, `<span>`, `<time>`, etc.)
 * providing proper semantics and accessibility.
 */

// ============================================
// Links
// ============================================

/**
 * Renders an HTML `<a>` element - a hyperlink.
 *
 * @param href The URL the link points to
 * @param target Optional target attribute: "_blank", "_self", "_parent", "_top"
 * @param rel Optional relationship between current and linked document
 * @param download Optional filename to download the resource as
 * @param hreflang Optional language of the linked resource
 * @param type Optional MIME type of the linked resource
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the link
 */
@Composable
fun A(
    href: String,
    target: String? = null,
    rel: String? = null,
    download: String? = null,
    hreflang: String? = null,
    type: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier.attribute("href", href)
    if (target != null) finalModifier = finalModifier.attribute("target", target)
    if (rel != null) finalModifier = finalModifier.attribute("rel", rel)
    if (download != null) finalModifier = finalModifier.attribute("download", download)
    if (hreflang != null) finalModifier = finalModifier.attribute("hreflang", hreflang)
    if (type != null) finalModifier = finalModifier.attribute("type", type)
    renderer.renderHtmlTag("a", finalModifier, content)
}

// ============================================
// Generic Inline Container
// ============================================

/**
 * Renders an HTML `<span>` element - a generic inline container.
 *
 * Use when no other semantic element is appropriate.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the span
 */
@Composable
fun Span(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("span", modifier, content)
}

// ============================================
// Semantic Inline Elements
// ============================================

/**
 * Renders an HTML `<time>` element - a date/time value.
 *
 * @param datetime Optional machine-readable date/time (ISO 8601 format)
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render (human-readable date/time)
 */
@Composable
fun Time(
    datetime: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = if (datetime != null) {
        modifier.attribute("datetime", datetime)
    } else {
        modifier
    }
    renderer.renderHtmlTag("time", finalModifier, content)
}

/**
 * Renders an HTML `<abbr>` element - an abbreviation or acronym.
 *
 * @param title The full expansion of the abbreviation
 * @param modifier Styling and attributes to apply
 * @param content Composable content (the abbreviation itself)
 */
@Composable
fun Abbr(
    title: String,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = modifier.attribute("title", title)
    renderer.renderHtmlTag("abbr", finalModifier, content)
}

/**
 * Renders an HTML `<cite>` element - a citation or reference.
 *
 * Use for titles of creative works (books, songs, films, etc.).
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the cite
 */
@Composable
fun Cite(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("cite", modifier, content)
}

/**
 * Renders an HTML `<q>` element - a short inline quotation.
 *
 * Browsers typically add quotation marks around the content.
 *
 * @param cite Optional URL to the source of the quotation
 * @param modifier Styling and attributes to apply
 * @param content Composable content (the quotation text)
 */
@Composable
fun Q(
    cite: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = if (cite != null) {
        modifier.attribute("cite", cite)
    } else {
        modifier
    }
    renderer.renderHtmlTag("q", finalModifier, content)
}

/**
 * Renders an HTML `<kbd>` element - keyboard input.
 *
 * Represents user input from a keyboard, voice, or other input device.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the kbd
 */
@Composable
fun Kbd(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("kbd", modifier, content)
}

/**
 * Renders an HTML `<samp>` element - sample output.
 *
 * Represents sample or quoted output from a computer program.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the samp
 */
@Composable
fun Samp(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("samp", modifier, content)
}

/**
 * Renders an HTML `<var>` element - a variable.
 *
 * Represents a variable in a mathematical expression or programming context.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the var
 */
@Composable
fun Var(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("var", modifier, content)
}

/**
 * Renders an HTML `<dfn>` element - a definition term.
 *
 * Indicates the term being defined in a definition or explanation.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the dfn
 */
@Composable
fun Dfn(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("dfn", modifier, content)
}

/**
 * Renders an HTML `<data>` element - machine-readable equivalent.
 *
 * Links content with a machine-readable translation.
 *
 * @param value The machine-readable value
 * @param modifier Styling and attributes to apply
 * @param content Composable content (human-readable representation)
 */
@Composable
fun Data(
    value: String,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = modifier.attribute("value", value)
    renderer.renderHtmlTag("data", finalModifier, content)
}

/**
 * Renders an HTML `<bdi>` element - bidirectional isolation.
 *
 * Isolates a span of text that might be formatted in a different direction.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the bdi
 */
@Composable
fun Bdi(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("bdi", modifier, content)
}

/**
 * Renders an HTML `<bdo>` element - bidirectional override.
 *
 * Overrides the current text direction.
 *
 * @param dir The text direction: "ltr" (left-to-right) or "rtl" (right-to-left)
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the bdo
 */
@Composable
fun Bdo(
    dir: String,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = modifier.attribute("dir", dir)
    renderer.renderHtmlTag("bdo", finalModifier, content)
}

/**
 * Renders an HTML `<ruby>` element - a ruby annotation container.
 *
 * Used for showing pronunciation of East Asian characters.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content containing text and Rt/Rp elements
 */
@Composable
fun Ruby(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("ruby", modifier, content)
}

/**
 * Renders an HTML `<rt>` element - ruby annotation text.
 *
 * Provides pronunciation of characters in a Ruby element.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content (the pronunciation guide)
 */
@Composable
fun Rt(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("rt", modifier, content)
}

/**
 * Renders an HTML `<rp>` element - ruby fallback parentheses.
 *
 * Provides fallback parentheses for browsers that don't support ruby annotations.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content (typically "(" or ")")
 */
@Composable
fun Rp(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("rp", modifier, content)
}

/**
 * Renders an HTML `<wbr>` element - a word break opportunity.
 *
 * Suggests where a line break may occur.
 *
 * @param modifier Styling and attributes to apply
 */
@Composable
fun Wbr(
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("wbr", modifier) {}
}

/**
 * Renders an HTML `<br>` element - a line break.
 *
 * @param modifier Styling and attributes to apply
 */
@Composable
fun Br(
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("br", modifier) {}
}

// Helper extension function
private fun Modifier.attribute(name: String, value: String): Modifier {
    return Modifier(
        styles = this.styles,
        attributes = this.attributes + (name to value),
        eventHandlers = this.eventHandlers
    )
}
