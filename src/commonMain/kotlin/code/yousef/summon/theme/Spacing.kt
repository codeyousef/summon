package code.yousef.summon.theme

import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.height
import code.yousef.summon.modifier.margin
import code.yousef.summon.modifier.padding
import code.yousef.summon.modifier.width
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer


/**
 * SpacingSystem provides a consistent spacing system for the Summon UI library.
 * It defines standard spacing values that can be used throughout the application
 * to maintain visual consistency.
 */
object Spacing {
    // Base spacing unit (in pixels)
    private const val BASE_UNIT = 4

    /**
     * Extra small spacing (4px)
     */
    const val xs = "${BASE_UNIT}px"

    /**
     * Small spacing (8px)
     */
    const val sm = "${BASE_UNIT * 2}px"

    /**
     * Medium spacing (16px) - Default
     */
    const val md = "${BASE_UNIT * 4}px"

    /**
     * Large spacing (24px)
     */
    const val lg = "${BASE_UNIT * 6}px"

    /**
     * Extra large spacing (32px)
     */
    const val xl = "${BASE_UNIT * 8}px"

    /**
     * Extra-extra large spacing (48px)
     */
    const val xxl = "${BASE_UNIT * 12}px"

    /**
     * Returns spacing in multiples of the base unit
     * @param multiplier Number of base units
     * @return Spacing value as a CSS px string
     */
    fun custom(multiplier: Int): String = "${BASE_UNIT * multiplier}px"

    /**
     * Creates padding values with different spacing on each side.
     *
     * @param top Top padding
     * @param right Right padding
     * @param bottom Bottom padding
     * @param left Left padding
     * @return A padding string formatted for CSS
     */
    fun padding(
        top: String = "0",
        right: String = "0",
        bottom: String = "0",
        left: String = "0"
    ): String {
        return "$top $right $bottom $left"
    }

    /**
     * Creates padding values with vertical and horizontal spacing.
     *
     * @param vertical Padding for top and bottom
     * @param horizontal Padding for left and right
     * @return A padding string formatted for CSS
     */
    fun padding(vertical: String = "0", horizontal: String = "0"): String {
        return "$vertical $horizontal"
    }

    /**
     * Creates margin values with different spacing on each side.
     *
     * @param top Top margin
     * @param right Right margin
     * @param bottom Bottom margin
     * @param left Left margin
     * @return A margin string formatted for CSS
     */
    fun margin(
        top: String = "0",
        right: String = "0",
        bottom: String = "0",
        left: String = "0"
    ): String {
        return "$top $right $bottom $left"
    }

    /**
     * Creates margin values with vertical and horizontal spacing.
     *
     * @param vertical Margin for top and bottom
     * @param horizontal Margin for left and right
     * @return A margin string formatted for CSS
     */
    fun margin(vertical: String = "0", horizontal: String = "0"): String {
        return "$vertical $horizontal"
    }

    /**
     * Applies a vertical space with the specified size.
     *
     * @param size The height of the vertical space
     */
    @code.yousef.summon.runtime.Composable
    fun verticalSpace(size: String = md) {
        Space(size.replace("px", "").toInt(), true)
    }

    /**
     * Applies a horizontal space with the specified size.
     *
     * @param size The width of the horizontal space
     */
    @code.yousef.summon.runtime.Composable
    fun horizontalSpace(size: String = md) {
        Space(size.replace("px", "").toInt(), false)
    }

    /**
     * Creates a spacer with the given size.
     *
     * @param size The size value to be used for the spacer
     * @param vertical Whether the spacer should be vertical (height) or horizontal (width)
     * @return A new Spacer component with the specified size
     */
    @code.yousef.summon.runtime.Composable
    fun Space(size: Int, vertical: Boolean = true): Unit {
        val renderer = LocalPlatformRenderer.current
        
        // Create appropriate modifier based on direction
        val sizeValue = "${size}px"
        val modifier = if (vertical) {
            Modifier().height(sizeValue)
        } else {
            Modifier().width(sizeValue)
        }
        
        // Render the spacer with the modifier
        renderer.renderSpacer(modifier)
    }

    /**
     * Creates a vertical spacer with a standard height.
     */
    @code.yousef.summon.runtime.Composable
    fun VerticalSpace() {
        Space(16, true) // Use 16px as the default size
    }
}

/**
 * Extension functions to easily apply spacing to components using the Spacing system
 */

/**
 * Apply padding using the Spacing system values
 */
fun Modifier.spacingPadding(value: String): Modifier = this.padding(value)

/**
 * Apply different padding to each side using the Spacing system values
 */
fun Modifier.spacingPadding(
    top: String = Spacing.xs,
    right: String = Spacing.xs,
    bottom: String = Spacing.xs,
    left: String = Spacing.xs
): Modifier = this.padding(top, right, bottom, left)

/**
 * Apply margin using the Spacing system values
 */
fun Modifier.spacingMargin(value: String): Modifier = this.margin(value)

/**
 * Apply different margin to each side using the Spacing system values
 */
fun Modifier.spacingMargin(
    top: String = Spacing.xs,
    right: String = Spacing.xs,
    bottom: String = Spacing.xs,
    left: String = Spacing.xs
): Modifier = this.margin(top, right, bottom, left)

/**
 * Apply horizontal padding (left and right) using the Spacing system values
 */
fun Modifier.spacingPaddingHorizontal(value: String): Modifier =
    this.padding(top = "0", right = value, bottom = "0", left = value)

/**
 * Apply vertical padding (top and bottom) using the Spacing system values
 */
fun Modifier.spacingPaddingVertical(value: String): Modifier =
    this.padding(top = value, right = "0", bottom = value, left = "0")

/**
 * Apply horizontal margin (left and right) using the Spacing system values
 */
fun Modifier.spacingMarginHorizontal(value: String): Modifier =
    this.margin(top = "0", right = value, bottom = "0", left = value)

/**
 * Apply vertical margin (top and bottom) using the Spacing system values
 */
fun Modifier.spacingMarginVertical(value: String): Modifier =
    this.margin(top = value, right = "0", bottom = value, left = "0")

/**
 * Create a Spacer with consistent spacing using the Spacing system
 */
fun createSpacer(size: String, isVertical: Boolean = true): Modifier {
    return if (isVertical) {
        Modifier().height(size)
    } else {
        Modifier().width(size)
    }
}

/**
 * A composable that renders an empty, sized element, typically used for spacing.
 *
 * @param modifier Modifier to control the size (width/height) of the spacer.
 */
@Composable
fun Spacer(modifier: Modifier) {
    // Use platform renderer directly
    val renderer = LocalPlatformRenderer.current
    renderer.renderSpacer(modifier)
}

// --- Convenience Spacers (Example - Needs Theme Integration) ---
// These should ideally pull values from a Theme object.

/** Adds horizontal space based on theme defaults (Extra Small). */
@Composable
fun HorizontalSpacerXS() {
    Spacer(Modifier().width("4px")) // Placeholder value
}

/** Adds horizontal space based on theme defaults (Small). */
@Composable
fun HorizontalSpacerS() {
    Spacer(Modifier().width("8px")) // Placeholder value
}

/** Adds horizontal space based on theme defaults (Medium). */
@Composable
fun HorizontalSpacerM() {
    Spacer(Modifier().width("16px")) // Placeholder value
}

/** Adds vertical space based on theme defaults (Extra Small). */
@Composable
fun VerticalSpacerXS() {
    Spacer(Modifier().height("4px")) // Placeholder value
}

/** Adds vertical space based on theme defaults (Small). */
@Composable
fun VerticalSpacerS() {
    Spacer(Modifier().height("8px")) // Placeholder value
}

/** Adds vertical space based on theme defaults (Medium). */
@Composable
fun VerticalSpacerM() {
    Spacer(Modifier().height("16px")) // Placeholder value
}
