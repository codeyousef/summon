package code.yousef.summon.theme

import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.fontFamily
import code.yousef.summon.modifier.letterSpacing
import code.yousef.summon.runtime.Composable

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
    @Composable
    fun H1(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "h1", modifier)
    @Composable
    fun H2(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "h2", modifier)
    @Composable
    fun H3(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "h3", modifier)
    @Composable
    fun H4(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "h4", modifier)
    @Composable
    fun H5(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "h5", modifier)
    @Composable
    fun H6(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "h6", modifier)
    @Composable
    fun Subtitle(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "subtitle", modifier)
    @Composable
    fun Body(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "body", modifier)
    @Composable
    fun BodyLarge(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "bodyLarge", modifier)
    @Composable
    fun BodySmall(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "bodySmall", modifier)
    @Composable
    fun Caption(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "caption", modifier)
    @Composable
    fun Button(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "button", modifier)
    @Composable
    fun Overline(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "overline", modifier)
    @Composable
    fun Link(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "link", modifier)
    @Composable
    fun Code(text: String, modifier: Modifier = Modifier()) = ThemedText(text, "code", modifier)
} 
