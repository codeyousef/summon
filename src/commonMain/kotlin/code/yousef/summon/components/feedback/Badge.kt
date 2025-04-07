package code.yousef.summon.components.feedback

import code.yousef.summon.runtime.PlatformRendererProvider


import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable


/**
 * Badge types for different semantic meanings
 */
enum class BadgeType {
    PRIMARY,    // Primary color badge
    SECONDARY,  // Secondary color badge
    SUCCESS,    // Success indicator
    WARNING,    // Warning indicator
    ERROR,      // Error indicator
    INFO,       // Information indicator
    NEUTRAL     // Neutral badge without specific semantic meaning
}

/**
 * Badge shapes for different visual styles
 */
enum class BadgeShape {
    SQUARE,     // Square badge with minor border radius
    ROUNDED,    // Rounded badge
    PILL,       // Pill-shaped badge (fully rounded)
    DOT         // Dot badge (small circular indicator)
}

/**
 * Badge variants for different styles
 */
enum class BadgeVariant {
    DEFAULT,
    PRIMARY,
    SECONDARY,
    SUCCESS,
    DANGER,
    WARNING,
    INFO,
    LIGHT,
    DARK
}

/**
 * A composable that displays a badge, typically used for status indicators, counters, or labels.
 *
 * @param modifier The modifier to apply to this composable.
 * @param type The semantic type of the badge, influences default styling.
 * @param shape The shape of the badge.
 * @param isOutlined Whether the badge has an outlined style (vs filled).
 * @param size The size preset ("small", "medium", "large"). TODO: Refine sizing.
 * @param onClick Optional click handler for the badge.
 * @param content The composable content displayed inside the badge.
 */
@Composable
fun Badge(
    modifier: Modifier = Modifier(),
    type: BadgeType = BadgeType.PRIMARY,
    shape: BadgeShape = BadgeShape.ROUNDED,
    isOutlined: Boolean = false,
    size: String = "medium",
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    // TODO: Define and apply styles based on type, shape, isOutlined, size
    val badgeStyles = getBadgeStyles(type, shape, isOutlined, size)
    val finalModifier = Modifier(badgeStyles).then(modifier)
        // TODO: Apply clickable modifier if onClick != null

    // TODO: Replace getPlatformRenderer with CompositionLocal access
    val renderer = PlatformRendererProvider.code.yousef.summon.runtime.PlatformRendererProvider.getPlatformRenderer()

    // TODO: Renderer signature update? Pass type/shape/etc.?
    // Assuming renderBadge just needs the final modifier and the content is handled via composition.
    renderer.renderBadge(modifier = finalModifier)

    // Compose the content inside the badge container
    // TODO: Ensure composition context places content correctly.
    content()
}

/**
 * Convenience overload for simple text badges.
 */
@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier(),
    type: BadgeType = BadgeType.PRIMARY,
    shape: BadgeShape = BadgeShape.ROUNDED,
    isOutlined: Boolean = false,
    size: String = "medium",
    onClick: (() -> Unit)? = null
) {
    Badge(
        modifier = modifier,
        type = type,
        shape = shape,
        isOutlined = isOutlined,
        size = size,
        onClick = onClick
    ) {
        Text(text) // Default content is Text
    }
}

// Helper function to calculate badge styles (moved from old class)
// TODO: This logic might be better placed within Modifier extensions or a theme system.
private fun getBadgeStyles(
    type: BadgeType, 
    shape: BadgeShape, 
    isOutlined: Boolean, 
    size: String
): Map<String, String> {
    
    val baseColorStyle = when (type) {
        BadgeType.PRIMARY -> Pair("#2196f3", "#ffffff")
        BadgeType.SECONDARY -> Pair("#9c27b0", "#ffffff")
        BadgeType.SUCCESS -> Pair("#4caf50", "#ffffff")
        BadgeType.WARNING -> Pair("#ff9800", "#ffffff")
        BadgeType.ERROR -> Pair("#f44336", "#ffffff")
        BadgeType.INFO -> Pair("#03a9f4", "#ffffff")
        BadgeType.NEUTRAL -> Pair("#9e9e9e", "#ffffff")
    }
    val (bgColor, textColor) = baseColorStyle
    
    val fillOrOutlineStyle = if (isOutlined) {
        mapOf("background-color" to "transparent", "color" to bgColor, "border" to "1px solid $bgColor")
    } else {
        mapOf("background-color" to bgColor, "color" to textColor)
    }

    val dotSizeValue = when (size) {
        "small" -> "0.5rem"
        "large" -> "1rem"
        else -> "0.75rem" // medium
    }
    val shapeStyle = when (shape) {
        BadgeShape.SQUARE -> mapOf("border-radius" to "2px")
        BadgeShape.ROUNDED -> mapOf("border-radius" to "4px")
        BadgeShape.PILL -> mapOf("border-radius" to "9999px")
        BadgeShape.DOT -> mapOf("border-radius" to "50%", "width" to dotSizeValue, "height" to dotSizeValue, "min-width" to dotSizeValue, "min-height" to dotSizeValue, "padding" to "0")
    }

    val paddingValue = when (size) {
        "small" -> if (shape != BadgeShape.DOT) "0.125rem 0.375rem" else "0"
        "large" -> if (shape != BadgeShape.DOT) "0.375rem 0.75rem" else "0"
        else -> if (shape != BadgeShape.DOT) "0.25rem 0.5rem" else "0"
    }
    val sizeStyle = mapOf(
        "font-size" to when(size) {"small" -> "0.75rem"; "large" -> "1rem"; else -> "0.875rem" },
        "padding" to paddingValue
    )

    // Combine styles (consider precedence)
    return mapOf("display" to "inline-block") + sizeStyle + shapeStyle + fillOrOutlineStyle 
}

/**
 * Creates a status badge with appropriate styling for status indicators.
 * @param status The status text to display
 * @param type The type of status
 * @param modifier The modifier to apply to this composable
 */
@Composable
fun statusBadge(
    status: String,
    type: BadgeType,
    modifier: Modifier = Modifier()
) {
    Badge(
        text = status,
        modifier = modifier,
        type = type,
        shape = BadgeShape.PILL
    )
}

/**
 * Creates a counter badge, typically used for notifications or counts.
 * @param count The count to display
 * @param modifier The modifier to apply to this composable
 */
@Composable
fun counterBadge(
    count: Int,
    modifier: Modifier = Modifier()
) {
    Badge(
        text = count.toString(),
        modifier = modifier,
        type = BadgeType.PRIMARY,
        shape = BadgeShape.PILL,
        size = "small"
    )
}

/**
 * Creates a simple dot badge, typically used to indicate status without text.
 * @param type The type of status
 * @param modifier The modifier to apply to this composable
 */
@Composable
fun dotBadge(
    type: BadgeType,
    modifier: Modifier = Modifier()
) {
    Badge(
        modifier = modifier,
        type = type,
        shape = BadgeShape.DOT,
        size = "small"
    ) {
        // Empty content for a dot
    }
}

// Helper to get variant-specific styles
private fun getBadgeVariantModifier(variant: BadgeVariant): Modifier {
    return when (variant) {
        BadgeVariant.DEFAULT -> Modifier().background("#e0e0e0").color("#333333")
        BadgeVariant.PRIMARY -> Modifier().background("#0d6efd").color("#ffffff")
        BadgeVariant.SECONDARY -> Modifier().background("#6c757d").color("#ffffff")
        BadgeVariant.SUCCESS -> Modifier().background("#198754").color("#ffffff")
        BadgeVariant.DANGER -> Modifier().background("#dc3545").color("#ffffff")
        BadgeVariant.WARNING -> Modifier().background("#ffc107").color("#000000")
        BadgeVariant.INFO -> Modifier().background("#0dcaf0").color("#000000")
        BadgeVariant.LIGHT -> Modifier().background("#f8f9fa").color("#000000")
        BadgeVariant.DARK -> Modifier().background("#212529").color("#ffffff")
    }
}

// Helper function to create a badge with simple text content
@Composable
fun Badge(text: String, modifier: Modifier = Modifier(), variant: BadgeVariant = BadgeVariant.DEFAULT) {
    Badge(modifier = modifier, variant = variant) {
        Text(text)
    }
}

// Helper function to create a status badge (e.g., for online/offline status)
@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier()) {
    val variant = when (status.lowercase()) {
        "online", "active", "success" -> BadgeVariant.SUCCESS
        "offline", "inactive", "error" -> BadgeVariant.DANGER
        "busy", "warning" -> BadgeVariant.WARNING
        else -> BadgeVariant.SECONDARY // Default
    }
    Badge(modifier = modifier, variant = variant) {
        Text(status)
    }
}

@Composable
fun Badge(
    modifier: Modifier = Modifier(),
    variant: BadgeVariant = BadgeVariant.DEFAULT,
    content: @Composable () -> Unit
) {
    // Base styles + Variant styles + User modifier
    val baseModifier = Modifier()
        .display("inline-block") 
        .padding("0.35em 0.65em") 
        .fontSize(".75em") 
        .fontWeight("bold") 
        .borderRadius("0.375rem") 
        .textAlign("center")
        .apply { 
        }

    val variantModifier = getBadgeVariantModifier(variant)

    val finalModifier = baseModifier
        .then(variantModifier) 
        .then(modifier) 

    // TODO: Replace getPlatformRenderer with CompositionLocal access
    val renderer = PlatformRendererProvider.getPlatformRenderer()

    renderer.renderBadge(modifier = finalModifier)

    content()
} 
