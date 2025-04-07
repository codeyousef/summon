package code.yousef.summon.modifier

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Auto margin modifiers for centering elements.
 * These modifiers provide convenient ways to center elements using auto margins.
 */

/**
 * Sets the margin for vertical and horizontal sides with the horizontal margins set to auto.
 * Useful for horizontal centering of elements with a fixed width.
 * Can be called without parameters for simple horizontal centering.
 * 
 * @param vertical The vertical margin value (top and bottom)
 */
fun Modifier.marginHorizontalAuto(vertical: String = "0px"): Modifier =
    style("margin", "$vertical auto")

/**
 * Sets horizontal margins to auto with zero vertical margins.
 * This is a convenience function to avoid parameter ambiguity issues.
 * Useful for simple horizontal centering of elements with a fixed width.
 */
fun Modifier.marginHorizontalAutoZero(): Modifier =
    style("margin", "0px auto")

/**
 * Sets the margin for vertical and horizontal sides with the vertical margins set to auto.
 * Useful for vertical centering of elements with a fixed height in certain containers.
 * 
 * @param horizontal The horizontal margin value (left and right)
 */
fun Modifier.marginVerticalAuto(horizontal: String = "0px"): Modifier =
    style("margin", "auto $horizontal")

/**
 * Sets vertical margins to auto with zero horizontal margins.
 * This is a convenience function to avoid parameter ambiguity issues.
 * Useful for simple vertical centering of elements with a fixed height.
 */
fun Modifier.marginVerticalAutoZero(): Modifier =
    style("margin", "auto 0px")

/**
 * Sets all margins to auto.
 * Useful for centering elements both horizontally and vertically in certain contexts.
 */
fun Modifier.marginAuto(): Modifier = style("margin", "auto")

/**
 * Sets the margin for vertical and horizontal sides with the horizontal margins set to auto.
 * Useful for horizontal centering of elements with a fixed width.
 * Can be called without parameters for simple horizontal centering.
 * 
 * @param vertical The vertical margin in pixels (top and bottom)
 */
fun Modifier.marginHorizontalAuto(vertical: Number = 0): Modifier =
    style("margin", "${vertical}px auto")

/**
 * Sets the margin for vertical and horizontal sides with the vertical margins set to auto.
 * Useful for vertical centering of elements with a fixed height in certain containers.
 * 
 * @param horizontal The horizontal margin in pixels
 */
fun Modifier.marginVerticalAuto(horizontal: Number = 0): Modifier =
    style("margin", "auto ${horizontal}px") 
