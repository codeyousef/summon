package code.yousef.summon.i18n

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * Utility class for RTL layout support
 */
object RtlUtils {

    /**
     * Check if the current layout direction is RTL
     *
     * @return True if the current layout direction is RTL
     */
    @Composable
    fun isRtl(): Boolean {
        val direction = LocalLayoutDirection.current
        val actualDirection = (direction as Function0<LayoutDirection>).invoke()
        return actualDirection == LayoutDirection.RTL
    }

    /**
     * Mirror a set of values based on the current layout direction
     *
     * @param ltrValue The value to use in LTR mode
     * @param rtlValue The value to use in RTL mode
     * @return The appropriate value based on the current direction
     */
    @Composable
    fun <T> directionalValue(ltrValue: T, rtlValue: T): T {
        val direction = LocalLayoutDirection.current
        val actualDirection = (direction as Function0<LayoutDirection>).invoke()
        return if (actualDirection == LayoutDirection.LTR) {
            ltrValue
        } else {
            rtlValue
        }
    }
}

/**
 * Extension function to apply directional padding to all sides
 *
 * @param start Start padding
 * @param top Top padding
 * @param end End padding
 * @param bottom Bottom padding
 * @return Modifier with appropriate directional padding
 */
@Composable
fun Modifier.directionalPadding(
    start: String,
    top: String,
    end: String,
    bottom: String
): Modifier {
    val direction = LocalLayoutDirection.current
    val actualDirection = (direction as Function0<LayoutDirection>).invoke()
    return if (actualDirection == LayoutDirection.LTR) {
        style("padding", "$top $end $bottom $start") // TRBL format
    } else {
        style("padding", "$top $start $bottom $end") // TRBL format with swapped left/right
    }
}

/**
 * Extension function to apply directional margin to all sides
 *
 * @param start Start margin
 * @param top Top margin
 * @param end End margin
 * @param bottom Bottom margin
 * @return Modifier with appropriate directional margin
 */
@Composable
fun Modifier.directionalMargin(
    start: String,
    top: String,
    end: String,
    bottom: String
): Modifier {
    val direction = LocalLayoutDirection.current
    val actualDirection = (direction as Function0<LayoutDirection>).invoke()
    return if (actualDirection == LayoutDirection.LTR) {
        style("margin", "$top $end $bottom $start") // TRBL format
    } else {
        style("margin", "$top $start $bottom $end") // TRBL format with swapped left/right
    }
}

/**
 * Extension function to apply CSS for mirroring an element in RTL mode
 *
 * @return Modifier with appropriate transform for RTL mirroring
 */
@Composable
fun Modifier.mirrorInRtl(): Modifier {
    val direction = LocalLayoutDirection.current
    val actualDirection = (direction as Function0<LayoutDirection>).invoke()
    return if (actualDirection == LayoutDirection.RTL) {
        style("transform", "scaleX(-1)")
    } else {
        this
    }
}

/**
 * Extension function to create a direction-aware flexbox row
 *
 * @return Modifier with appropriate flex direction
 */
@Composable
fun Modifier.directionalRow(): Modifier {
    val direction = LocalLayoutDirection.current
    val actualDirection = (direction as Function0<LayoutDirection>).invoke()
    val value = if (actualDirection == LayoutDirection.LTR) "row" else "row-reverse"
    return style("display", "flex").style("flex-direction", value)
} 