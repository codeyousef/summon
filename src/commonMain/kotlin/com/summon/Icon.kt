package com.summon

import kotlinx.html.TagConsumer

/**
 * Icon type to distinguish between different icon sources
 */
enum class IconType {
    SVG,    // SVG vector icons
    FONT,   // Font icons (e.g., Font Awesome, Material Icons)
    IMAGE   // Image-based icons (fallback option)
}

/**
 * A composable that displays an icon (vector, font, or image-based).
 *
 * @param name The name or identifier of the icon (e.g., "home" for a home icon)
 * @param modifier The modifier to apply to this composable
 * @param type The type of icon (SVG, FONT, IMAGE)
 * @param size The size of the icon (e.g., "24px")
 * @param color The color of the icon (e.g., "#000000")
 * @param fontFamily For font icons, the font family to use (e.g., "FontAwesome")
 * @param svgContent For SVG icons, the raw SVG content
 * @param ariaLabel Accessible label for screen readers
 * @param onClick Optional click handler for the icon
 */
data class Icon(
    val name: String,
    val modifier: Modifier = Modifier(),
    val type: IconType = IconType.FONT,
    val size: String = "24px",
    val color: String = "currentColor",
    val fontFamily: String? = null,
    val svgContent: String? = null,
    val ariaLabel: String? = null,
    val onClick: (() -> Unit)? = null
) : Composable, MediaComponent {
    /**
     * Renders this Icon composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return getPlatformRenderer().renderIcon(this, receiver as TagConsumer<T>)
        }
        return receiver
    }

    /**
     * Gets additional icon-specific styles that should be applied
     * beyond what is in the modifier.
     */
    internal fun getAdditionalStyles(): Map<String, String> {
        val styles = mutableMapOf<String, String>()

        // Add size to styles
        styles["width"] = size
        styles["height"] = size

        // Add color to styles
        styles["color"] = color

        // Add font-specific styles if this is a font icon
        if (type == IconType.FONT) {
            fontFamily?.let { styles["font-family"] = it }
            styles["font-size"] = size
            styles["display"] = "inline-block"
            styles["text-align"] = "center"
            styles["line-height"] = size
        }

        return styles
    }

    /**
     * Gets accessibility attributes for the icon.
     */
    internal fun getAccessibilityAttributes(): Map<String, String> {
        val attributes = mutableMapOf<String, String>()

        // If no aria-label is provided, make the icon presentational
        if (ariaLabel == null) {
            attributes["aria-hidden"] = "true"
            attributes["role"] = "presentation"
        } else {
            attributes["aria-label"] = ariaLabel

            // Make the icon focusable and interactive if it has a click handler
            if (onClick != null) {
                attributes["role"] = "button"
                attributes["tabindex"] = "0"
            }
        }

        return attributes
    }
}

/**
 * Creates a Material Design icon using font.
 * @param name The name of the Material Design icon
 * @param size The size of the icon (e.g., "24px")
 * @param color The color of the icon (e.g., "#000000")
 * @param modifier The modifier to apply to this composable
 */
fun materialIcon(
    name: String,
    size: String = "24px",
    color: String = "currentColor",
    modifier: Modifier = Modifier()
): Icon = Icon(
    name = name,
    modifier = modifier,
    type = IconType.FONT,
    size = size,
    color = color,
    fontFamily = "Material Icons",
    ariaLabel = null
)

/**
 * Creates a Font Awesome icon.
 * @param name The name of the Font Awesome icon (e.g., "fa-home")
 * @param size The size of the icon (e.g., "24px")
 * @param color The color of the icon (e.g., "#000000")
 * @param modifier The modifier to apply to this composable
 */
fun fontAwesomeIcon(
    name: String,
    size: String = "24px",
    color: String = "currentColor",
    modifier: Modifier = Modifier()
): Icon = Icon(
    name = name,
    modifier = modifier,
    type = IconType.FONT,
    size = size,
    color = color,
    fontFamily = "Font Awesome 5 Free",
    ariaLabel = null
)

/**
 * Creates an SVG icon from raw SVG content.
 * @param svgContent The raw SVG content as a string
 * @param size The size of the icon (e.g., "24px")
 * @param color The color to apply to the SVG (will be inserted as a fill attribute)
 * @param ariaLabel Accessible label for screen readers
 * @param modifier The modifier to apply to this composable
 */
fun svgIcon(
    svgContent: String,
    size: String = "24px",
    color: String = "currentColor",
    ariaLabel: String? = null,
    modifier: Modifier = Modifier()
): Icon = Icon(
    name = "svg-icon",
    modifier = modifier,
    type = IconType.SVG,
    size = size,
    color = color,
    svgContent = svgContent,
    ariaLabel = ariaLabel
) 