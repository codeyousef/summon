package code.yousef.summon.components.display

import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal

/**
 * Icon type to distinguish between different icon sources
 */
enum class IconType {
    SVG,    // SVG vector icons
    FONT,   // Font icons (e.g., Font Awesome, Material Icons)
    IMAGE   // Image-based icons (fallback option)
}

/**
 * Displays an Icon.
 * Delegates rendering to the platform-specific renderer.
 *
 * @param name The name or identifier of the icon (used for font icons or accessibility).
 * @param modifier Modifier for applying styling and layout properties.
 * @param size The size of the icon (e.g., "24px"). Should ideally be controlled via modifier.
 * @param color Optional color override for the icon. Should ideally be controlled via modifier.
 * @param type The type of icon (SVG, FONT, or IMAGE). Determines rendering strategy.
 * @param fontFamily The font family for font icons. Should ideally be controlled via modifier.
 * @param ariaLabel Accessible label for screen readers. Should ideally be controlled via modifier.
 * @param svgContent The raw SVG content for SVG icons.
 * @param onClick Optional callback to be invoked when the icon is clicked (Requires JS handling).
 */
@Composable
fun Icon(
    name: String, // Used for font-icon class/ligature or aria-label
    modifier: Modifier = Modifier(),
    size: String? = null, // Prefer setting size via modifier.width/height
    color: String? = null, // Prefer setting color via modifier.color
    // --- Parameters influencing rendering strategy ---
    type: IconType = IconType.SVG, // Might not be needed if rendering is unified
    fontFamily: String? = null, // Prefer setting via modifier.fontFamily
    svgContent: String? = null, // For inline SVG rendering
    // --- Accessibility & Interaction ---
    ariaLabel: String? = null, // Prefer setting via modifier.accessibility
    onClick: (() -> Unit)? = null // JS only for now
) {
    // Combine explicit parameters into the modifier if provided
    // This provides backward compatibility but encourages using Modifier directly
    var finalModifier = modifier
    size?.let { finalModifier = finalModifier.size(it) }
    color?.let { finalModifier = finalModifier.color(it) }
    fontFamily?.let { finalModifier = finalModifier.fontFamily(it) }
    // TODO: Handle accessibility attributes via Modifier extension
    // ariaLabel?.let { finalModifier = finalModifier.accessibilityLabel(it) }
    
    // TODO: Handle onClick via Modifier extension (e.g., modifier.clickable { onClick?.invoke() })
    
    // TODO: The renderer needs to handle different icon types (SVG, Font) based on parameters/modifier
    // For now, just pass the name and modifier.
    // svgContent might need to be handled differently (e.g., specific renderer method or passed via modifier attribute)
    
    val renderer = PlatformRendererProvider.getRenderer()
    
    // TODO: Adapt renderer call based on type/svgContent if necessary
    renderer.renderIcon(
        name = name, // Pass name for font icons or ARIA label fallback
        modifier = finalModifier 
        // Other parameters like svgContent might be needed here or handled internally by renderer based on modifier
    )
}


// --- Predefined Icons and Helper Functions (Adapted to call the Composable) ---

object IconDefaults {
    // Size presets
    object Size {
        const val SMALL = "16px"
        const val MEDIUM = "24px"
        const val LARGE = "32px"
    }

    // Common icons - Now call the @Composable function
    @Composable
    fun Add(modifier: Modifier = Modifier()) = Icon("add", modifier)
    @Composable
    fun Delete(modifier: Modifier = Modifier()) = Icon("delete", modifier)
    @Composable
    fun Edit(modifier: Modifier = Modifier()) = Icon("edit", modifier)
    @Composable
    fun Download(modifier: Modifier = Modifier()) = Icon("download", modifier)
    @Composable
    fun Upload(modifier: Modifier = Modifier()) = Icon("upload", modifier)

    // Add common status icons (using Material Icon names as examples)
    @Composable
    fun Info(modifier: Modifier = Modifier()) = MaterialIcon("info", modifier)
    @Composable
    fun CheckCircle(modifier: Modifier = Modifier()) = MaterialIcon("check_circle", modifier)
    @Composable
    fun Warning(modifier: Modifier = Modifier()) = MaterialIcon("warning", modifier)
    @Composable
    fun Error(modifier: Modifier = Modifier()) = MaterialIcon("error", modifier)
    @Composable
    fun Close(modifier: Modifier = Modifier()) = MaterialIcon("close", modifier) // Useful for dismiss
}

/**
 * Creates a Material Design icon using font.
 * Note: Requires the Material Icons font family to be loaded.
 */
@Composable
fun MaterialIcon(
    name: String,
    modifier: Modifier = Modifier(),
    size: String = IconDefaults.Size.MEDIUM,
    color: String? = null, // Default color is usually inherited
    onClick: (() -> Unit)? = null
) {
    Icon(
        name = name, // Material Icons use ligatures
        modifier = modifier,
        size = size,
        color = color,
        type = IconType.FONT,
        fontFamily = "Material Icons", // Specify font family
        onClick = onClick
    )
}

/**
 * Creates a Font Awesome icon.
 * Note: Requires the Font Awesome font family to be loaded.
 */
@Composable
fun FontAwesomeIcon(
    name: String, // e.g., "fas fa-home" or just "home" depending on setup
    modifier: Modifier = Modifier(),
    size: String = IconDefaults.Size.MEDIUM,
    color: String? = null,
    fontFamily: String = "Font Awesome 5 Free", // Adjust as needed
    onClick: (() -> Unit)? = null
) {
    Icon(
        name = name, // Used for CSS class typically
        modifier = modifier,
        size = size,
        color = color,
        type = IconType.FONT,
        fontFamily = fontFamily,
        onClick = onClick
    )
}

/**
 * Creates an SVG icon from raw SVG content.
 * Note: `renderIcon` needs to support handling raw SVG content.
 */
@Composable
fun SvgIcon(
    svgContent: String,
    modifier: Modifier = Modifier(),
    size: String = IconDefaults.Size.MEDIUM,
    color: String? = null, // Renderer needs to apply this to the SVG
    ariaLabel: String? = null,
    onClick: (() -> Unit)? = null
) {
    Icon(
        name = ariaLabel ?: "svg-icon", // Use label for name fallback
        modifier = modifier,
        size = size,
        color = color,
        type = IconType.SVG,
        svgContent = svgContent, // Pass SVG content
        ariaLabel = ariaLabel,
        onClick = onClick
    )
} 