package code.yousef.summon.components.display

import code.yousef.summon.MediaComponent
import code.yousef.summon.core.Composable
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
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
 * Icon component for displaying vector icons or icon fonts.
 *
 * @param name The name or identifier of the icon
 * @param size The size of the icon in pixels
 * @param modifier The modifier to apply to this composable
 * @param color Optional color override for the icon
 * @param type The type of icon (SVG, FONT, or IMAGE)
 * @param fontFamily The font family for font icons
 * @param ariaLabel Accessible label for screen readers
 * @param svgContent The raw SVG content for SVG icons
 * @param onClick Optional callback to be invoked when the icon is clicked
 */
class Icon(
    val name: String,
    val size: String = "24px",
    val modifier: Modifier = Modifier(),
    val color: String? = null,
    val type: IconType = IconType.SVG,
    val fontFamily: String? = null,
    val ariaLabel: String? = null,
    val svgContent: String? = null,
    val onClick: (() -> Unit)? = null
) : Composable, MediaComponent {

    /**
     * Renders this Icon composable using the platform-specific renderer.
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderIcon(this, receiver as TagConsumer<T>)
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
        color?.let { styles["color"] = it }

        return styles
    }

    /**
     * Gets accessibility attributes for the icon.
     */
    internal fun getAccessibilityAttributes(): Map<String, String> {
        val attributes = mutableMapOf<String, String>()

        // If no aria-label is provided, make the icon presentational
        if (name.endsWith("-icon", true)) {
            attributes["aria-hidden"] = "true"
            attributes["role"] = "presentation"
        } else {
            attributes["aria-label"] = name
        }

        return attributes
    }

    companion object {
        // Size presets
        object Size {
            const val SMALL = "16px"
            const val MEDIUM = "24px"
            const val LARGE = "32px"
            const val CUSTOM_24 = "24px"
        }

        // Common icons
        val Add = Icon("add")
        val Delete = Icon("delete")
        val Edit = Icon("edit")
        val Download = Icon("download")
        val Upload = Icon("upload")
    }
}

/**
 * Creates a Material Design icon using font.
 * @param name The name of the Material Design icon
 * @param size The size of the icon (e.g., "24px")
 * @param color The color of the icon (e.g., "#000000")
 * @param modifier The modifier to apply to this composable
 * @param onClick Optional callback to be invoked when the icon is clicked
 */
fun materialIcon(
    name: String,
    size: String = "24px",
    color: String = "currentColor",
    modifier: Modifier = Modifier(),
    onClick: (() -> Unit)? = null
): Icon = Icon(
    name = name,
    modifier = modifier,
    type = IconType.FONT,
    size = size,
    color = color,
    fontFamily = "Material Icons",
    ariaLabel = null,
    onClick = onClick
)

/**
 * Creates a Font Awesome icon.
 * @param name The name of the Font Awesome icon (e.g., "fa-home")
 * @param size The size of the icon (e.g., "24px")
 * @param color The color of the icon (e.g., "#000000")
 * @param modifier The modifier to apply to this composable
 * @param onClick Optional callback to be invoked when the icon is clicked
 */
fun fontAwesomeIcon(
    name: String,
    size: String = "24px",
    color: String = "currentColor",
    modifier: Modifier = Modifier(),
    onClick: (() -> Unit)? = null
): Icon = Icon(
    name = name,
    modifier = modifier,
    type = IconType.FONT,
    size = size,
    color = color,
    fontFamily = "Font Awesome 5 Free",
    ariaLabel = null,
    onClick = onClick
)

/**
 * Creates an SVG icon from raw SVG content.
 * @param svgContent The raw SVG content as a string
 * @param size The size of the icon (e.g., "24px")
 * @param color The color to apply to the SVG (will be inserted as a fill attribute)
 * @param ariaLabel Accessible label for screen readers
 * @param modifier The modifier to apply to this composable
 * @param onClick Optional callback to be invoked when the icon is clicked
 */
fun svgIcon(
    svgContent: String,
    size: String = "24px",
    color: String = "currentColor",
    ariaLabel: String? = null,
    modifier: Modifier = Modifier(),
    onClick: (() -> Unit)? = null
): Icon = Icon(
    name = "svg-icon",
    modifier = modifier,
    type = IconType.SVG,
    size = size,
    color = color,
    svgContent = svgContent,
    ariaLabel = ariaLabel,
    onClick = onClick
) 