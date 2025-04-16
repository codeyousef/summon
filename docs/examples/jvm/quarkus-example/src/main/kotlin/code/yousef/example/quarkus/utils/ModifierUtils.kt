package code.yousef.example.quarkus.utils

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.style

/**
 * Utility extension functions for Modifiers to avoid name conflicts
 * with the Summon library. These functions provide consistent
 * implementation across the application.
 */

/**
 * Sets margin for vertical and horizontal sides.
 * This avoids conflicts with other margin implementations.
 */
fun Modifier.marginVH(vertical: String, horizontal: String): Modifier =
    style("margin", "$vertical $horizontal")

/**
 * Sets padding for vertical and horizontal sides.
 * This avoids conflicts with other padding implementations.
 */
fun Modifier.paddingVH(vertical: String, horizontal: String): Modifier =
    style("padding", "$vertical $horizontal")

/**
 * Sets padding for all sides with a single value.
 * Provides additional padding function for common use.
 */
fun Modifier.padding(all: String): Modifier =
    style("padding", all)

/**
 * Sets background color for an element.
 */
fun Modifier.backgroundColor(value: String): Modifier =
    style("background-color", value)

/**
 * Creates a border-bottom with custom width, style, and color.
 */
fun Modifier.borderBottomCustom(width: String, style: String, color: String): Modifier =
    style("border-bottom", "$width $style $color")

/**
 * Additional utility functions for specific styling needs.
 * These should be used instead of duplicating implementations in component files.
 */

/**
 * Adds box shadow with a single parameter for the complete shadow value.
 */
fun Modifier.boxShadow(value: String): Modifier = 
    style("box-shadow", value)

/**
 * Sets transform with a single parameter for the transform value.
 */
fun Modifier.transform(value: String): Modifier = 
    style("transform", value)

/**
 * Sets hover effects with optional parameters.
 */
fun Modifier.hover(
    boxShadow: String? = null,
    transform: String? = null,
    backgroundColor: String? = null,
    color: String? = null
): Modifier {
    val hoverStyles = mutableListOf<Pair<String, String>>()
    if (boxShadow != null) hoverStyles.add("box-shadow" to boxShadow)
    if (transform != null) hoverStyles.add("transform" to transform)
    if (backgroundColor != null) hoverStyles.add("background-color" to backgroundColor)
    if (color != null) hoverStyles.add("color" to color)

    val stylesString = hoverStyles.joinToString(";") { (prop, value) -> "$prop:$value" }
    return style("__hover", stylesString)
}

/**
 * Directional margin utility functions - only include those not already in the library
 */
fun Modifier.marginRight(value: String): Modifier = style("margin-right", value)
fun Modifier.marginLeft(value: String): Modifier = style("margin-left", value)

/**
 * Sets the flex property for flexible layouts.
 */
fun Modifier.flex(value: String): Modifier = style("flex", value)

/**
 * Sets the font size of text elements.
 */
fun Modifier.fontSize(value: String): Modifier = style("font-size", value)

/**
 * Sets the font weight of text elements.
 */
fun Modifier.fontWeight(value: String): Modifier = style("font-weight", value) 