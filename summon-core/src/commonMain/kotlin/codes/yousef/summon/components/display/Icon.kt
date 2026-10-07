package codes.yousef.summon.components.display

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.foundation.TrustedSvg
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.ariaLabel
import codes.yousef.summon.modifier.fontFamily
import codes.yousef.summon.runtime.LocalPlatformRenderer


/**
 * Icon type to distinguish between different icon sources
 */
enum class IconType {
    /** The svg icon type option. */
    SVG,
    /** The font icon type option. */
    FONT,
    /** The image icon type option. */
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
 * @param svgContent Inline SVG explicitly trusted as application author code.
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
    svgContent: TrustedSvg? = null,
    // --- Accessibility & Interaction ---
    ariaLabel: String? = null, // Prefer setting via modifier.accessibility
    onClick: (() -> Unit)? = null // JS only for now
) {
    var finalModifier = modifier
    size?.let { finalModifier = finalModifier.size(it) }
    color?.let { finalModifier = finalModifier.color(it) }
    fontFamily?.let { finalModifier = finalModifier.fontFamily(it, null) }

    // Apply accessibility attributes if provided
    if (ariaLabel != null) {
        finalModifier = finalModifier.ariaLabel(ariaLabel)
        // Also set the role for better accessibility
        finalModifier = finalModifier.role("img")
    }

    // For clickable icons, set appropriate cursor and role
    if (onClick != null) {
        finalModifier = finalModifier.cursor("pointer")
        // If not already set, add a button role for interactive icons
        if (ariaLabel == null) {
            finalModifier = finalModifier.role("button")
        }
    }

    val renderer = LocalPlatformRenderer.current

    // Render the icon - platform renderer will handle the different types
    renderer.renderIcon(name, finalModifier, onClick, svgContent, type)
}


// --- Predefined Icons and Helper Functions (Adapted to call the Composable) ---

/** Common icon components and size values. */
object IconDefaults {
    // Size presets
    /** Standard icon CSS sizes. */
    object Size {
        /** Compact icon size. */
        const val SMALL = "16px"
        /** Default icon size. */
        const val MEDIUM = "24px"
        /** Prominent icon size. */
        const val LARGE = "32px"
    }

    // Common icons - Now call the @Composable function
    /** Renders the standard add icon. */
    @Composable
    fun Add(modifier: Modifier = Modifier()) = Icon("add", modifier)

    /** Renders the standard delete icon. */
    @Composable
    fun Delete(modifier: Modifier = Modifier()) = Icon("delete", modifier)

    /** Renders the standard edit icon. */
    @Composable
    fun Edit(modifier: Modifier = Modifier()) = Icon("edit", modifier)

    /** Renders the standard download icon. */
    @Composable
    fun Download(modifier: Modifier = Modifier()) = Icon("download", modifier)

    /** Renders the standard upload icon. */
    @Composable
    fun Upload(modifier: Modifier = Modifier()) = Icon("upload", modifier)

    // Add common status icons (using Material Icon names as examples)
    /** Renders an informational status icon. */
    @Composable
    fun Info(modifier: Modifier = Modifier()) = MaterialIcon("info", modifier)

    /** Renders a success status icon. */
    @Composable
    fun CheckCircle(modifier: Modifier = Modifier()) = MaterialIcon("check_circle", modifier)

    /** Renders a warning status icon. */
    @Composable
    fun Warning(modifier: Modifier = Modifier()) = MaterialIcon("warning", modifier)

    /** Renders an error status icon. */
    @Composable
    fun Error(modifier: Modifier = Modifier()) = MaterialIcon("error", modifier)

    /** Renders a close or dismiss icon. */
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
        modifier = modifier.addClass("material-icons"),
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

/** Creates an SVG icon from markup explicitly trusted as application author code. */
@Composable
fun SvgIcon(
    svgContent: TrustedSvg,
    modifier: Modifier = Modifier(),
    size: String = IconDefaults.Size.MEDIUM,
    color: String? = null,
    ariaLabel: String? = null,
    onClick: (() -> Unit)? = null
) {
    Icon(
        name = ariaLabel ?: "svg-icon",
        modifier = modifier,
        size = size,
        color = color,
        type = IconType.SVG,
        svgContent = svgContent,
        ariaLabel = ariaLabel,
        onClick = onClick
    )
}
