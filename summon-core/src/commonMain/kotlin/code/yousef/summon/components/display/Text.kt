/**
 * # Display Components Package
 *
 * This package provides components for displaying content and information to users.
 *
 * ## Overview
 *
 * Display components focus on presenting information clearly and accessibly:
 *
 * - **Text**: Full-featured text display with typography and accessibility
 * - **Image**: Image display with loading, fallbacks, and responsive behavior
 * - **Icon**: Icon display with multiple sources and interactive capabilities
 * - **RichText**: Rich text with markup support and advanced formatting
 *
 * These components prioritize accessibility, performance, and cross-platform compatibility.
 *
 * @since 1.0.0
 */
package codes.yousef.summon.components.display

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * A full-featured text display component with comprehensive styling and accessibility support.
 *
 * Text provides advanced text rendering capabilities with extensive typography controls,
 * accessibility features, and semantic markup options. It's designed for displaying
 * content text, messages, and any text that requires rich formatting or accessibility.
 *
 * ## Key Features
 *
 * ### Typography Control
 * - Complete font family, size, weight, and style control
 * - Line height, letter spacing, and word spacing customization
 * - Text decoration and transformation options
 * - Text shadow and advanced visual effects
 *
 * ### Layout and Overflow
 * - Text alignment and white space handling
 * - Word breaking and overflow control
 * - Multi-line truncation with line clamping
 * - Responsive typography support
 *
 * ### Accessibility Features
 * - Full ARIA support with roles, labels, and descriptions
 * - Semantic HTML element selection
 * - Screen reader optimization
 * - Keyboard navigation support
 *
 * ### Cross-Platform Rendering
 * - Consistent behavior across JS and JVM platforms
 * - Platform-specific optimizations
 * - CSS and HTML generation for different targets
 *
 * ## Usage Examples
 *
 * ### Basic Text
 * ```kotlin
 * Text("Hello, World!")
 * ```
 *
 * ### Styled Paragraph
 * ```kotlin
 * Text(
 *     text = "This is a styled paragraph with custom typography.",
 *     modifier = Modifier().padding("16px"),
 *     fontFamily = "Georgia, serif",
 *     lineHeight = "1.6",
 *     textAlign = "justify",
 *     semantic = "paragraph"
 * )
 * ```
 *
 * ### Heading with Accessibility
 * ```kotlin
 * Text(
 *     text = "Section Title",
 *     modifier = Modifier()
 *         .fontSize("24px")
 *         .fontWeight("bold")
 *         .marginBottom("16px"),
 *     role = "heading",
 *     ariaLabel = "Main section title",
 *     semantic = "heading"
 * )
 * ```
 *
 * ### Truncated Text
 * ```kotlin
 * Text(
 *     text = "This is a very long text that will be truncated after three lines...",
 *     maxLines = 3,
 *     overflow = "ellipsis",
 *     modifier = Modifier().width("300px")
 * )
 * ```
 *
 * ### Styled Caption
 * ```kotlin
 * Text(
 *     text = "Photo taken in Paris, 2023",
 *     fontFamily = "Arial, sans-serif",
 *     fontSize = "12px",
 *     textAlign = "center",
 *     textTransform = "uppercase",
 *     letterSpacing = "0.5px",
 *     semantic = "caption",
 *     modifier = Modifier()
 *         .color("#666")
 *         .marginTop("8px")
 * )
 * ```
 *
 * ## Typography Properties
 *
 * | Property | Values | Example |
 * |----------|--------|---------|
 * | `fontFamily` | Font stack | `"Helvetica, Arial, sans-serif"` |
 * | `textAlign` | Alignment | `"left"`, `"center"`, `"right"`, `"justify"` |
 * | `textDecoration` | Decoration | `"underline"`, `"line-through"`, `"none"` |
 * | `textTransform` | Transform | `"uppercase"`, `"lowercase"`, `"capitalize"` |
 * | `lineHeight` | Line height | `"1.5"`, `"24px"`, `"normal"` |
 * | `letterSpacing` | Spacing | `"0.5px"`, `"normal"`, `"0.1em"` |
 * | `whiteSpace` | White space | `"normal"`, `"nowrap"`, `"pre"`, `"pre-line"` |
 * | `wordBreak` | Word breaking | `"normal"`, `"break-all"`, `"keep-all"` |
 *
 * ## Accessibility Guidelines
 *
 * ### ARIA Roles
 * - Use `role="heading"` for headings with appropriate `aria-level`
 * - Use `role="article"` for standalone content
 * - Use `role="note"` for supplementary information
 *
 * ### Labels and Descriptions
 * ```kotlin
 * Text(
 *     text = "Error: Invalid email format",
 *     role = "alert",
 *     ariaLabel = "Form validation error message",
 *     ariaDescribedBy = "email-field-error"
 * )
 * ```
 *
 * ### Semantic HTML
 * The `semantic` parameter controls HTML element selection:
 * - `"paragraph"` → `<p>`
 * - `"heading"` → `<h1>` to `<h6>` (with appropriate styling)
 * - `"caption"` → `<caption>` or `<figcaption>`
 * - `"article"` → `<article>`
 * - `null` → `<span>` (default)
 *
 * ## Performance Considerations
 *
 * - Use [BasicText] for simple text without advanced features
 * - Prefer CSS classes over inline styles for repeated patterns
 * - Consider text length impact on rendering performance
 * - Use `maxLines` to prevent layout issues with dynamic content
 *
 * ## Comparison with BasicText
 *
 * | Feature | Text | BasicText |
 * |---------|------|-----------|
 * | Typography Control | Full | Basic |
 * | Accessibility | Complete | Limited |
 * | Performance | Good | Excellent |
 * | API Complexity | Rich | Simple |
 * | Use Case | Content display | Simple labels |
 *
 * @param text The text content to display
 * @param modifier [Modifier] for layout, styling, and interaction
 * @param overflow Text overflow handling ('ellipsis', 'clip', etc.)
 * @param lineHeight Line height specification (e.g., '1.5', '24px')
 * @param textAlign Text alignment ('left', 'center', 'right', 'justify')
 * @param fontFamily Font family stack for typography
 * @param textDecoration Text decoration style ('underline', 'line-through', 'none')
 * @param textTransform Text transformation ('uppercase', 'lowercase', 'capitalize', 'none')
 * @param letterSpacing Spacing between letters (e.g., '0.5px', 'normal')
 * @param whiteSpace White space handling ('normal', 'nowrap', 'pre', 'pre-line', 'pre-wrap')
 * @param wordBreak Word breaking behavior ('normal', 'break-all', 'keep-all', 'break-word')
 * @param wordSpacing Spacing between words (e.g., '0.5px', 'normal')
 * @param textShadow Text shadow specification (e.g., '1px 1px 2px black')
 * @param maxLines Maximum lines before truncation (enables line clamping)
 * @param role ARIA role for accessibility (e.g., 'heading', 'article', 'note')
 * @param ariaLabel Accessible name for screen readers
 * @param ariaDescribedBy ID of element describing this text for accessibility
 * @param semantic Semantic HTML element type ('paragraph', 'heading', 'caption', etc.)
 * @see code.yousef.summon.components.foundation.BasicText for simple text display
 * @see Label for form-associated text labels
 * @since 1.0.0
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier(),
    overflow: String? = null,
    lineHeight: String? = null,
    textAlign: String? = null,
    fontFamily: String? = null,
    textDecoration: String? = null,
    textTransform: String? = null,
    letterSpacing: String? = null,
    whiteSpace: String? = null,
    wordBreak: String? = null,
    wordSpacing: String? = null,
    textShadow: String? = null,
    maxLines: Int? = null,
    role: String? = null,
    ariaLabel: String? = null,
    ariaDescribedBy: String? = null,
    semantic: String? = null
) {
    // Apply text-specific styles to the modifier
    val additionalStyles = getAdditionalStyles(
        overflow, lineHeight, textAlign, fontFamily, textDecoration,
        textTransform, letterSpacing, whiteSpace, wordBreak, wordSpacing,
        textShadow, maxLines
    )

    // Apply accessibility attributes
    val accessibilityAttrs = getAccessibilityAttributes(role, ariaLabel, ariaDescribedBy)

    // Combine all styles and attributes
    var finalModifier = modifier
    if (additionalStyles.isNotEmpty()) {
        for ((key, value) in additionalStyles) {
            finalModifier = finalModifier.style(key, value)
        }
    }

    // Add accessibility attributes
    for ((key, value) in accessibilityAttrs) {
        finalModifier = finalModifier.attribute(key, value)
    }

    // Add semantic role if provided
    if (semantic != null) {
        finalModifier = finalModifier.attribute("data-semantic", semantic)
    }

    // Call the PlatformRenderer
    val renderer = LocalPlatformRenderer.current
    renderer.renderText(text = text, modifier = finalModifier)
}

/**
 * Backward compatibility class for existing code that expects a Text class instance
 * rather than using the @Composable function directly.
 *
 * @deprecated Use the @Composable Text function instead for new code
 */
data class TextComponent(
    val text: String,
    val modifier: Modifier = Modifier(),
    val overflow: String? = null,
    val lineHeight: String? = null,
    val textAlign: String? = null,
    val fontFamily: String? = null,
    val textDecoration: String? = null,
    val textTransform: String? = null,
    val letterSpacing: String? = null,
    val whiteSpace: String? = null,
    val wordBreak: String? = null,
    val wordSpacing: String? = null,
    val textShadow: String? = null,
    val maxLines: Int? = null,
    val role: String? = null,
    val ariaLabel: String? = null,
    val ariaDescribedBy: String? = null,
    val semantic: String? = null
) {
    /**
     * Renders this Text using the @Composable function.
     * Used for backward compatibility with existing code.
     */
    @Composable
    fun render() {
        Text(
            text = text,
            modifier = modifier,
            overflow = overflow,
            lineHeight = lineHeight,
            textAlign = textAlign,
            fontFamily = fontFamily,
            textDecoration = textDecoration,
            textTransform = textTransform,
            letterSpacing = letterSpacing,
            whiteSpace = whiteSpace,
            wordBreak = wordBreak,
            wordSpacing = wordSpacing,
            textShadow = textShadow,
            maxLines = maxLines,
            role = role,
            ariaLabel = ariaLabel,
            ariaDescribedBy = ariaDescribedBy,
            semantic = semantic
        )
    }

    /**
     * For compatibility with old TagConsumer-based code
     */
    fun <T> compose(receiver: T): T {
        // No-op for compatibility, actual rendering will happen via render() in a Composable context
        return receiver
    }
}

/**
 * Gets a map of all additional style properties that should be applied
 * beyond what is in the modifier.
 */
private fun getAdditionalStyles(
    overflow: String?,
    lineHeight: String?,
    textAlign: String?,
    fontFamily: String?,
    textDecoration: String?,
    textTransform: String?,
    letterSpacing: String?,
    whiteSpace: String?,
    wordBreak: String?,
    wordSpacing: String?,
    textShadow: String?,
    maxLines: Int?
): Map<String, String> {
    val styles = mutableMapOf<String, String>()

    overflow?.let { styles["overflow"] = it }
    overflow?.let { styles["text-overflow"] = it }
    lineHeight?.let { styles["line-height"] = it }
    textAlign?.let { styles["text-align"] = it }
    fontFamily?.let { styles["font-family"] = it }
    textDecoration?.let { styles["text-decoration"] = it }
    textTransform?.let { styles["text-transform"] = it }
    letterSpacing?.let { styles["letter-spacing"] = it }
    whiteSpace?.let { styles["white-space"] = it }
    wordBreak?.let { styles["word-break"] = it }
    wordSpacing?.let { styles["word-spacing"] = it }
    textShadow?.let { styles["text-shadow"] = it }

    // Handle max lines with -webkit-line-clamp for truncating
    if (maxLines != null) {
        styles["display"] = "-webkit-box"
        styles["-webkit-line-clamp"] = maxLines.toString()
        styles["-webkit-box-orient"] = "vertical"
        styles["overflow"] = "hidden"
    }

    return styles
}

/**
 * Gets a map of all accessibility attributes that should be applied.
 */
private fun getAccessibilityAttributes(
    role: String?,
    ariaLabel: String?,
    ariaDescribedBy: String?
): Map<String, String> {
    val attributes = mutableMapOf<String, String>()

    role?.let { attributes["role"] = it }
    ariaLabel?.let { attributes["aria-label"] = it }
    ariaDescribedBy?.let { attributes["aria-describedby"] = it }

    return attributes
}

/**
 * A composable that displays text specifically designed for form labels.
 * This component renders as a semantic HTML label element and should be used
 * when the text is labeling a form control.
 *
 * For general text display that is not associated with form controls,
 * use the [Text] component instead.
 *
 * @param text The text string to display.
 * @param modifier The modifier to apply to this composable.
 * @param forElement Optional ID of the form element this label is associated with.
 *                   This creates a programmatic association between the label and the form control.
 */
@Composable
fun Label(text: String, modifier: Modifier = Modifier(), forElement: String? = null) {
    // Get the platform renderer
    val renderer = LocalPlatformRenderer.current

    // Use the semantic renderLabel method
    renderer.renderLabel(text = text, modifier = modifier, forElement = forElement)
}
