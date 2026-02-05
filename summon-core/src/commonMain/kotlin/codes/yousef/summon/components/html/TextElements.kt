package codes.yousef.summon.components.html

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Text-related HTML5 elements for semantic content markup.
 *
 * These components render actual HTML text elements (`<h1>`, `<p>`, `<blockquote>`, etc.)
 * providing proper document structure and accessibility.
 */

// ============================================
// Heading Elements
// ============================================

/**
 * Renders an HTML `<h1>` element - the highest level heading.
 *
 * There should typically be only one h1 per page, representing the main title.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the heading
 */
@Composable
fun H1(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("h1", modifier, content)
}

/**
 * Renders an HTML `<h2>` element - a second level heading.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the heading
 */
@Composable
fun H2(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("h2", modifier, content)
}

/**
 * Renders an HTML `<h3>` element - a third level heading.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the heading
 */
@Composable
fun H3(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("h3", modifier, content)
}

/**
 * Renders an HTML `<h4>` element - a fourth level heading.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the heading
 */
@Composable
fun H4(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("h4", modifier, content)
}

/**
 * Renders an HTML `<h5>` element - a fifth level heading.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the heading
 */
@Composable
fun H5(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("h5", modifier, content)
}

/**
 * Renders an HTML `<h6>` element - the lowest level heading.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the heading
 */
@Composable
fun H6(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("h6", modifier, content)
}

// ============================================
// Paragraph and Block Text Elements
// ============================================

/**
 * Renders an HTML `<p>` element - a paragraph of text.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the paragraph
 */
@Composable
fun P(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("p", modifier, content)
}

/**
 * Renders an HTML `<blockquote>` element - an extended quotation.
 *
 * @param cite Optional URL to the source of the quotation
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the blockquote
 */
@Composable
fun Blockquote(
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
    renderer.renderHtmlTag("blockquote", finalModifier, content)
}

/**
 * Renders an HTML `<pre>` element - preformatted text.
 *
 * Whitespace and line breaks are preserved in pre elements.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the pre
 */
@Composable
fun Pre(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("pre", modifier, content)
}

/**
 * Renders an HTML `<code>` element - inline computer code.
 *
 * For code blocks, wrap Code inside Pre.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the code element
 */
@Composable
fun Code(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("code", modifier, content)
}

// ============================================
// Text Formatting Elements
// ============================================

/**
 * Renders an HTML `<strong>` element - text with strong importance.
 *
 * Typically displayed in bold. Use for important content, not just visual styling.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the strong element
 */
@Composable
fun Strong(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("strong", modifier, content)
}

/**
 * Renders an HTML `<em>` element - text with emphatic stress.
 *
 * Typically displayed in italics. Use for content that should be stressed when read aloud.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the em element
 */
@Composable
fun Em(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("em", modifier, content)
}

/**
 * Renders an HTML `<small>` element - side comments or small print.
 *
 * Use for fine print, caveats, legal restrictions, or copyright notices.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the small element
 */
@Composable
fun Small(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("small", modifier, content)
}

/**
 * Renders an HTML `<mark>` element - highlighted/marked text.
 *
 * Use to highlight text for reference or notation purposes, such as search results.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the mark element
 */
@Composable
fun Mark(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("mark", modifier, content)
}

/**
 * Renders an HTML `<del>` element - deleted text.
 *
 * Represents text that has been deleted from the document.
 *
 * @param cite Optional URL to a document explaining the change
 * @param datetime Optional date/time of the change
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the del element
 */
@Composable
fun Del(
    cite: String? = null,
    datetime: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier
    if (cite != null) finalModifier = finalModifier.attribute("cite", cite)
    if (datetime != null) finalModifier = finalModifier.attribute("datetime", datetime)
    renderer.renderHtmlTag("del", finalModifier, content)
}

/**
 * Renders an HTML `<ins>` element - inserted text.
 *
 * Represents text that has been inserted into the document.
 *
 * @param cite Optional URL to a document explaining the change
 * @param datetime Optional date/time of the change
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the ins element
 */
@Composable
fun Ins(
    cite: String? = null,
    datetime: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier
    if (cite != null) finalModifier = finalModifier.attribute("cite", cite)
    if (datetime != null) finalModifier = finalModifier.attribute("datetime", datetime)
    renderer.renderHtmlTag("ins", finalModifier, content)
}

/**
 * Renders an HTML `<sub>` element - subscript text.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the sub element
 */
@Composable
fun Sub(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("sub", modifier, content)
}

/**
 * Renders an HTML `<sup>` element - superscript text.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the sup element
 */
@Composable
fun Sup(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("sup", modifier, content)
}

/**
 * Renders an HTML `<s>` element - strikethrough text.
 *
 * Use for content that is no longer accurate or relevant but should not be removed.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the s element
 */
@Composable
fun S(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("s", modifier, content)
}

/**
 * Renders an HTML `<u>` element - unarticulated annotation.
 *
 * Use for text that has some non-textual annotation (e.g., misspelled words in Chinese).
 * Avoid using just for styling purposes.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the u element
 */
@Composable
fun U(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("u", modifier, content)
}

/**
 * Renders an HTML `<b>` element - bring attention to text.
 *
 * Use to draw attention to text without conveying extra importance.
 * For important text, use Strong instead.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the b element
 */
@Composable
fun B(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("b", modifier, content)
}

/**
 * Renders an HTML `<i>` element - idiomatic text.
 *
 * Use for technical terms, foreign phrases, thoughts, or ship names.
 * For emphasized text, use Em instead.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the i element
 */
@Composable
fun I(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("i", modifier, content)
}

// Helper extension function
private fun Modifier.attribute(name: String, value: String): Modifier {
    return Modifier(
        styles = this.styles,
        attributes = this.attributes + (name to value),
        eventHandlers = this.eventHandlers
    )
}
