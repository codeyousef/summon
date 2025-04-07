package code.yousef.summon.theme

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider


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
fun createSpacer(size: String, isVertical: Boolean = true): Spacer =
    Spacer(size = size, isVertical = isVertical)

/**
 * A composable that renders an empty, sized element, typically used for spacing.
 *
 * @param modifier Modifier to control the size (width/height) of the spacer.
 */
@Composable
fun Spacer(modifier: Modifier) {
    val composer = CompositionLocal.currentComposer
    composer?.startNode() // Start Spacer node
    if (composer?.inserting == true) {
        PlatformRendererProvider.getPlatformRenderer().renderBox(modifier = modifier) // Use renderBox or a specific renderSpacer
    }
    // No content lambda for Spacer
    composer?.endNode() // End Spacer node
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
