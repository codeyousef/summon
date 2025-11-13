package codes.yousef.summon.modifier

import codes.yousef.summon.components.MediaComponent
import codes.yousef.summon.components.ScrollableComponent
import codes.yousef.summon.components.TextComponent

/**
 * This file contains type-safe modifier extensions that can only be applied to specific component types.
 * These extensions restrict certain modifiers to components that implement the appropriate marker interfaces.
 */

// Text-specific modifiers
/**
 * Sets the font family.
 * Only applicable to text components.
 */
fun Modifier.fontFamily(value: String, component: TextComponent? = null): Modifier =
    style("font-family", value)

/**
 * Sets the font style (normal, italic, etc).
 * Only applicable to text components.
 */
fun Modifier.fontStyle(value: String, component: TextComponent? = null): Modifier =
    style("font-style", value)

/**
 * Sets the font style using the FontStyle enum.
 * Only applicable to text components.
 */
fun Modifier.fontStyle(value: FontStyle, component: TextComponent? = null): Modifier =
    style("font-style", value.toString())

/**
 * Sets the font weight (normal, bold, etc).
 * Only applicable to text components.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
fun Modifier.fontWeight(value: String, component: TextComponent? = null): Modifier =
    style("font-weight", value)

/**
 * Sets the text alignment.
 * Only applicable to text components.
 */
fun Modifier.textAlign(value: String, component: TextComponent? = null): Modifier =
    style("text-align", value)

/**
 * Sets the text alignment using the TextAlign enum.
 * Only applicable to text components.
 */
fun Modifier.textAlign(value: TextAlign, component: TextComponent? = null): Modifier =
    style("text-align", value.toString())

/**
 * Sets the text decoration (underline, line-through, etc).
 * Only applicable to text components.
 */
fun Modifier.textDecoration(value: String, component: TextComponent? = null): Modifier =
    style("text-decoration", value)

/**
 * Sets the text decoration using the TextDecoration enum.
 * Only applicable to text components.
 */
fun Modifier.textDecoration(value: TextDecoration, component: TextComponent? = null): Modifier =
    textDecoration(value.toString(), component)

/**
 * Sets multiple text decorations (e.g., underline + line-through).
 * Only applicable to text components.
 */
fun Modifier.textDecoration(vararg values: TextDecoration, component: TextComponent? = null): Modifier {
    require(values.isNotEmpty()) { "textDecoration requires at least one value" }
    return textDecoration(values.joinToString(" ") { it.toString() }, component)
}

/**
 * Sets the line height with a string value.
 * Only applicable to text components.
 */
fun Modifier.lineHeight(value: String, component: TextComponent? = null): Modifier =
    style("line-height", value)

/**
 * Sets the line height with a numeric value (e.g., 1.5).
 * Only applicable to text components.
 */
fun Modifier.lineHeight(value: Number, component: TextComponent? = null): Modifier =
    style("line-height", value.toString())

/**
 * Sets the letter spacing.
 * Only applicable to text components.
 */
fun Modifier.letterSpacing(value: String, component: TextComponent? = null): Modifier =
    style("letter-spacing", value)

/**
 * Sets the letter spacing with a numeric value in pixels.
 * Only applicable to text components.
 */
fun Modifier.letterSpacing(value: Number, component: TextComponent? = null): Modifier =
    style("letter-spacing", "${value}px")

/**
 * Controls white-space handling.
 * Only applicable to text components.
 */
fun Modifier.whiteSpace(value: String, component: TextComponent? = null): Modifier =
    style("white-space", value)

/**
 * Controls white-space handling using the WhiteSpace enum.
 * Only applicable to text components.
 */
fun Modifier.whiteSpace(value: WhiteSpace, component: TextComponent? = null): Modifier =
    whiteSpace(value.toString(), component)

/**
 * Sets the text transformation (uppercase, lowercase, etc).
 * Only applicable to text components.
 */
fun Modifier.textTransform(value: String, component: TextComponent? = null): Modifier =
    style("text-transform", value)

/**
 * Sets the text transformation using the TextTransform enum.
 * Only applicable to text components.
 */
fun Modifier.textTransform(value: TextTransform, component: TextComponent? = null): Modifier =
    style("text-transform", value.toString())

// Media-specific modifiers
/**
 * Sets the object-fit property for images and videos.
 * Only applicable to media components.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
fun Modifier.objectFit(value: String, component: MediaComponent? = null): Modifier =
    style("object-fit", value)

// Scrollable-specific modifiers
/**
 * Sets the scroll behavior.
 * Only applicable to scrollable components.
 */
fun Modifier.scrollBehavior(value: String, component: ScrollableComponent? = null): Modifier =
    style("scroll-behavior", value)

/**
 * Sets the scrollbar width.
 * Only applicable to scrollable components.
 */
fun Modifier.scrollbarWidth(value: String, component: ScrollableComponent? = null): Modifier =
    style("scrollbar-width", value)
