package codes.yousef.summon.theme

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.fontFamily
import codes.yousef.summon.modifier.letterSpacing
import codes.yousef.summon.runtime.Composable

/**
 * A collection of composable functions for themed text styles.
 */
object TextComponents {
    /**
     * Generic text style applier
     */
    @Composable
    private fun ThemedText(
        text: String,
        styleName: String,
        modifier: Modifier = Modifier()
    ) {
        // For backward compatibility, still use getTextStyle
        val style = Theme.getTextStyle(styleName)
        val styledModifier = modifier.let { mod ->
            var result = mod
            if (style.fontFamily != null) result = result.fontFamily(style.fontFamily, null)
            if (style.fontSize != null) result = result.fontSize(style.fontSize)
            if (style.fontWeight != null) result = result.fontWeight(style.fontWeight)
            if (style.letterSpacing != null) result = result.letterSpacing(style.letterSpacing, null)
            if (style.color != null) result = result.color(style.color)
            if (style.textDecoration != null) result = result.style("text-decoration", style.textDecoration)
            if (style.lineHeight != null) result = result.style("line-height", style.lineHeight)
            if (style.fontStyle != null) result = result.style("font-style", style.fontStyle)
            result
        }

        Text(text = text, modifier = styledModifier)
    }

    // Specific Themed Text Composables
    /**
     * Renders h1.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun H1(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "h1", modifier)

    /**
     * Renders h2.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun H2(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "h2", modifier)

    /**
     * Renders h3.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun H3(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "h3", modifier)

    /**
     * Renders h4.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun H4(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "h4", modifier)

    /**
     * Renders h5.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun H5(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "h5", modifier)

    /**
     * Renders h6.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun H6(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "h6", modifier)

    /**
     * Renders subtitle.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun Subtitle(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "subtitle", modifier)

    /**
     * Renders body.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun Body(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "body", modifier)

    /**
     * Renders body large.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun BodyLarge(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "bodyLarge", modifier)

    /**
     * Renders body small.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun BodySmall(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "bodySmall", modifier)

    /**
     * Renders caption.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun Caption(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "caption", modifier)

    /**
     * Renders button.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun Button(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "button", modifier)

    /**
     * Renders overline.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun Overline(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "overline", modifier)

    /**
     * Renders link.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun Link(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "link", modifier)

    /**
     * Renders code.
     *
     * @param text The text value.
     * @param modifier Styles and attributes applied to the rendered element.
     */
    @Composable
    fun Code(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "code", modifier)
}
