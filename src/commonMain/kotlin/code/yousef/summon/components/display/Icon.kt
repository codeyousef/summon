package code.yousef.summon.components.display

import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable

/**
 * Icon type to distinguish between different icon sources
 */
enum class IconType {
    SVG,    // SVG vector icons
    FONT,   // Font icons (e.g., Font Awesome, Material Icons)
    IMAGE   // Image-based icons (fallback option)
}

/**
 * Displays a vector icon (SVG, Font) or a small image-based icon.
 *
 * @param name The name or identifier of the icon (used for font icons, or as part of accessibility).
 * @param modifier Optional [Modifier] for styling and layout.
 * @param size The desired size of the icon (e.g., "24px").
 * @param color Optional color override for the icon (primarily for SVG and font icons).
 * @param type The type of icon source using [IconType].
 * @param fontFamily The font family if using [IconType.FONT].
 * @param ariaLabel Optional accessible label for screen readers. If null, attempts reasonable defaults.
 * @param svgContent The raw SVG content if using [IconType.SVG].
 * @param onClick Optional lambda executed when the icon is clicked.
 */
@Composable
fun Icon(
    name: String,
    modifier: Modifier = Modifier(),
    size: String = Size.MEDIUM,
    color: String? = null,
    type: IconType = IconType.SVG,
    fontFamily: String? = null,
    ariaLabel: String? = null,
    svgContent: String? = null,
    onClick: (() -> Unit)? = null
) {
    // 1. Create data holder
    val iconData = IconData(
        name = name,
        modifier = modifier,
        size = size,
        color = color,
        type = type,
        fontFamily = fontFamily,
        ariaLabel = ariaLabel,
        svgContent = svgContent,
        onClick = onClick
    )

    // 2. Delegate rendering
    // Placeholder logic - needs composer/renderer integration.
    // The renderer (adapt renderIcon) needs to:
    // - Create the appropriate HTML element (<i>, <span> for font, <img> for image, inline <svg> or <img> for SVG).
    // - Apply styles (size, color) from data and modifier.
    // - Set content (icon name/ligature for font, src for image, innerHTML for SVG).
    // - Apply accessibility attributes.
    // - Attach click listener if onClick is provided.

    println("Composable Icon function called for: $name") // Placeholder

    // Example conceptual rendering call:
    // PlatformRendererProvider.getRenderer().renderIcon(iconData)
}

/**
 * Internal data class holding parameters for the Icon renderer.
 */
data class IconData(
    val name: String,
    val modifier: Modifier,
    val size: String,
    val color: String?,
    val type: IconType,
    val fontFamily: String?,
    val ariaLabel: String?,
    val svgContent: String?,
    val onClick: (() -> Unit)?
) {

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
        color?.let { styles["color"] = it }

        // For font icons, ensure font-family is applied if specified
        if (type == IconType.FONT && fontFamily != null) {
            styles["font-family"] = fontFamily
        }

        return styles
    }

    /**
     * Gets accessibility attributes for the icon.
     */
    internal fun getAccessibilityAttributes(): Map<String, String> {
        val attributes = mutableMapOf<String, String>()

        // Use provided ariaLabel if available, otherwise make presentational
        if (ariaLabel != null) {
            attributes["aria-label"] = ariaLabel
            attributes["role"] = "img" // Role image is good practice for icons with labels
        } else {
            attributes["aria-hidden"] = "true"
            // Role presentation might be redundant if aria-hidden is true
            attributes["role"] = "presentation"
        }

        return attributes
    }
}

/**
 * Companion object for presets, factory functions remain.
 */
object Size {
    const val SMALL = "16px"
    const val MEDIUM = "24px"
    const val LARGE = "32px"
    const val CUSTOM_24 = "24px"
}

/**
 * Creates a Material Design icon using font.
 * @param name The name of the Material Design icon
 * @param modifier The modifier to apply to this composable
 * @param size The size of the icon (e.g., "24px")
 * @param color The color of the icon (e.g., "#000000")
 * @param onClick Optional callback to be invoked when the icon is clicked
 */
@Composable
fun MaterialIcon(
    name: String,
    modifier: Modifier = Modifier(),
    size: String = Size.MEDIUM,
    color: String? = null,
    onClick: (() -> Unit)? = null
) {
    Icon(
        name = name,
        modifier = modifier,
        type = IconType.FONT,
        size = size,
        color = color,
        fontFamily = "Material Icons",
        ariaLabel = null,
        onClick = onClick
    )
}

/**
 * Creates a Font Awesome icon.
 * @param name The name of the Font Awesome icon (e.g., "fa-home")
 * @param modifier The modifier to apply to this composable
 * @param size The size of the icon (e.g., "24px")
 * @param color The color of the icon (e.g., "#000000")
 * @param onClick Optional callback to be invoked when the icon is clicked
 */
@Composable
fun FontAwesomeIcon(
    name: String,
    modifier: Modifier = Modifier(),
    size: String = Size.MEDIUM,
    color: String? = null,
    onClick: (() -> Unit)? = null
) {
    // Note: FontAwesome often uses CSS classes (e.g., "fas fa-home").
    // This implementation assumes direct font ligature/name rendering.
    // May need adjustment depending on how FontAwesome is integrated.
    Icon(
        name = name,
        modifier = modifier,
        type = IconType.FONT,
        size = size,
        color = color,
        fontFamily = "Font Awesome 5 Free",
        ariaLabel = null,
        onClick = onClick
    )
}

/**
 * Creates an SVG icon from raw SVG content.
 * @param svgContent The raw SVG content as a string
 * @param modifier The modifier to apply to this composable
 * @param size The size of the icon (e.g., "24px")
 * @param color The color to apply to the SVG (will be inserted as a fill attribute, or use CSS currentColor)
 * @param ariaLabel Accessible label for screen readers
 * @param onClick Optional callback to be invoked when the icon is clicked
 */
@Composable
fun SvgIcon(
    svgContent: String,
    modifier: Modifier = Modifier(),
    size: String = Size.MEDIUM,
    color: String? = null,
    ariaLabel: String? = null,
    onClick: (() -> Unit)? = null
) {
    Icon(
        name = "svg-icon",
        modifier = modifier,
        type = IconType.SVG,
        size = size,
        color = color,
        svgContent = svgContent,
        ariaLabel = ariaLabel,
        onClick = onClick
    )
} 