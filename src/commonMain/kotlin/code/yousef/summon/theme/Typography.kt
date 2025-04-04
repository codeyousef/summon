package code.yousef.summon.theme

import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier

/**
 * Typography provides consistent text styling presets across the application.
 * It defines standard font styles for different text elements like headings,
 * body text, captions, etc.
 */
object Typography {
    /**
     * Represents a text style with all the typography properties
     */
    data class TextStyle(
        val fontFamily: String,
        val fontSize: String,
        val fontWeight: String,
        val lineHeight: String,
        val letterSpacing: String = "normal",
        val textTransform: String? = null
    ) {
        /**
         * Convert the TextStyle to a map of CSS property-value pairs
         * @return A map of CSS properties and values
         */
        fun toStyleMap(): Map<String, String> {
            val styles = mutableMapOf(
                "font-family" to fontFamily,
                "font-size" to fontSize,
                "font-weight" to fontWeight,
                "line-height" to lineHeight,
                "letter-spacing" to letterSpacing
            )

            textTransform?.let { styles["text-transform"] = it }

            return styles
        }

        /**
         * Apply this text style to a modifier
         * @param modifier The modifier to apply the style to
         * @return A new modifier with the style applied
         */
        fun applyTo(modifier: Modifier): Modifier {
            return Modifier(modifier.styles + toStyleMap())
        }
    }

    /**
     * Font families
     */
    object FontFamilies {
        /**
         * Default sans-serif system font stack
         */
        const val system =
            "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif"

        /**
         * Monospace system font stack
         */
        const val monospace = "SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace"

        /**
         * Serif system font stack
         */
        const val serif = "Georgia, Cambria, 'Times New Roman', Times, serif"
    }

    /**
     * Font weights
     */
    object FontWeights {
        const val thin = "100"
        const val extraLight = "200"
        const val light = "300"
        const val regular = "400"
        const val medium = "500"
        const val semiBold = "600"
        const val bold = "700"
        const val extraBold = "800"
        const val black = "900"
    }

    /**
     * Heading level 1 style (h1)
     */
    val h1 = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "2.5rem",
        fontWeight = FontWeights.bold,
        lineHeight = "1.2",
        letterSpacing = "-0.02em"
    )

    /**
     * Heading level 2 style (h2)
     */
    val h2 = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "2rem",
        fontWeight = FontWeights.bold,
        lineHeight = "1.3",
        letterSpacing = "-0.01em"
    )

    /**
     * Heading level 3 style (h3)
     */
    val h3 = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "1.75rem",
        fontWeight = FontWeights.semiBold,
        lineHeight = "1.4",
        letterSpacing = "-0.01em"
    )

    /**
     * Heading level 4 style (h4)
     */
    val h4 = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "1.5rem",
        fontWeight = FontWeights.semiBold,
        lineHeight = "1.4"
    )

    /**
     * Heading level 5 style (h5)
     */
    val h5 = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "1.25rem",
        fontWeight = FontWeights.semiBold,
        lineHeight = "1.4"
    )

    /**
     * Heading level 6 style (h6)
     */
    val h6 = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "1rem",
        fontWeight = FontWeights.semiBold,
        lineHeight = "1.4"
    )

    /**
     * Subtitle style, used for subtitles and headings with less emphasis
     */
    val subtitle = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "1.15rem",
        fontWeight = FontWeights.medium,
        lineHeight = "1.5",
        letterSpacing = "0.01em"
    )

    /**
     * Body text style, used for most content
     */
    val body = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "1rem",
        fontWeight = FontWeights.regular,
        lineHeight = "1.5"
    )

    /**
     * Body large style, used for emphasized body text
     */
    val bodyLarge = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "1.125rem",
        fontWeight = FontWeights.regular,
        lineHeight = "1.5"
    )

    /**
     * Body small style, used for secondary information
     */
    val bodySmall = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "0.875rem",
        fontWeight = FontWeights.regular,
        lineHeight = "1.5"
    )

    /**
     * Caption style, used for captions and labels
     */
    val caption = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "0.75rem",
        fontWeight = FontWeights.regular,
        lineHeight = "1.5"
    )

    /**
     * Overline style, used for small labels above other content
     */
    val overline = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "0.75rem",
        fontWeight = FontWeights.medium,
        lineHeight = "1.5",
        letterSpacing = "0.05em",
        textTransform = "uppercase"
    )

    /**
     * Button text style, used for button labels
     */
    val button = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "0.875rem",
        fontWeight = FontWeights.medium,
        lineHeight = "1.75",
        letterSpacing = "0.05em",
        textTransform = "uppercase"
    )

    /**
     * Link text style, used for hyperlinks
     */
    val link = TextStyle(
        fontFamily = FontFamilies.system,
        fontSize = "1rem",
        fontWeight = FontWeights.regular,
        lineHeight = "1.5"
    )

    /**
     * Code text style, used for code snippets
     */
    val code = TextStyle(
        fontFamily = FontFamilies.monospace,
        fontSize = "0.9rem",
        fontWeight = FontWeights.regular,
        lineHeight = "1.5"
    )

    /**
     * Custom preset for article headings
     */
    val articleHeading = TextStyle(
        fontFamily = FontFamilies.serif,
        fontSize = "2.25rem",
        fontWeight = FontWeights.bold,
        lineHeight = "1.3",
        letterSpacing = "-0.01em"
    )

    /**
     * Custom preset for article body
     */
    val articleBody = TextStyle(
        fontFamily = FontFamilies.serif,
        fontSize = "1.125rem",
        fontWeight = FontWeights.regular,
        lineHeight = "1.7"
    )
}

/**
 * Create a Text component with a Typography preset style
 * @param text The text content
 * @param style The Typography preset to use
 * @param modifier Optional additional modifiers to apply
 * @return A Text component with the style applied
 */
fun typographyText(text: String, style: Typography.TextStyle, modifier: Modifier = Modifier()): Text {
    val fontFamily = style.fontFamily
    val lineHeight = style.lineHeight
    val styledModifier = style.applyTo(modifier)

    return Text(
        text = text,
        modifier = styledModifier,
        fontFamily = fontFamily,
        lineHeight = lineHeight
    )
}

/**
 * Extension functions for easy creation of styled text components
 */

/**
 * Create a heading level 1 text
 * @param text The text content
 * @param modifier Optional additional modifiers
 * @return A Text component with h1 styling
 */
fun h1Text(text: String, modifier: Modifier = Modifier()): Text =
    typographyText(text, Typography.h1, modifier)

/**
 * Create a heading level 2 text
 * @param text The text content
 * @param modifier Optional additional modifiers
 * @return A Text component with h2 styling
 */
fun h2Text(text: String, modifier: Modifier = Modifier()): Text =
    typographyText(text, Typography.h2, modifier)

/**
 * Create a heading level 3 text
 * @param text The text content
 * @param modifier Optional additional modifiers
 * @return A Text component with h3 styling
 */
fun h3Text(text: String, modifier: Modifier = Modifier()): Text =
    typographyText(text, Typography.h3, modifier)

/**
 * Create a heading level 4 text
 * @param text The text content
 * @param modifier Optional additional modifiers
 * @return A Text component with h4 styling
 */
fun h4Text(text: String, modifier: Modifier = Modifier()): Text =
    typographyText(text, Typography.h4, modifier)

/**
 * Create a heading level 5 text
 * @param text The text content
 * @param modifier Optional additional modifiers
 * @return A Text component with h5 styling
 */
fun h5Text(text: String, modifier: Modifier = Modifier()): Text =
    typographyText(text, Typography.h5, modifier)

/**
 * Create a heading level 6 text
 * @param text The text content
 * @param modifier Optional additional modifiers
 * @return A Text component with h6 styling
 */
fun h6Text(text: String, modifier: Modifier = Modifier()): Text =
    typographyText(text, Typography.h6, modifier)

/**
 * Create a body text
 * @param text The text content
 * @param modifier Optional additional modifiers
 * @return A Text component with body text styling
 */
fun bodyText(text: String, modifier: Modifier = Modifier()): Text =
    typographyText(text, Typography.body, modifier)

/**
 * Create a caption text
 * @param text The text content
 * @param modifier Optional additional modifiers
 * @return A Text component with caption styling
 */
fun captionText(text: String, modifier: Modifier = Modifier()): Text =
    typographyText(text, Typography.caption, modifier)

/**
 * Create a button text
 * @param text The text content
 * @param modifier Optional additional modifiers
 * @return A Text component with button text styling
 */
fun buttonText(text: String, modifier: Modifier = Modifier()): Text =
    typographyText(text, Typography.button, modifier)

/**
 * Create a code snippet text
 * @param text The text content
 * @param modifier Optional additional modifiers
 * @return A Text component with code styling
 */
fun codeText(text: String, modifier: Modifier = Modifier()): Text =
    typographyText(text, Typography.code, modifier) 