package code.yousef.summon.modifier

import code.yousef.summon.extensions.percent
import code.yousef.summon.extensions.px

/**
 * Extension functions for Layout Modifiers
 * These are implemented to match the test expectations in LayoutModifierTest
 */

/**
 * Sets the minimum width of the element.
 */
fun Modifier.minWidth(value: String): Modifier =
    style("min-width", value)

/**
 * Sets the minimum height of the element.
 */
fun Modifier.minHeight(value: String): Modifier =
    style("min-height", value)

/**
 * Sets the maximum height of the element.
 */
fun Modifier.maxHeight(value: String): Modifier =
    style("max-height", value)

// fillMaxWidth() removed - exists as member function in Modifier class

// padding(String) removed - exists as member function in Modifier class

/**
 * Sets different padding for horizontal and vertical.
 */
fun Modifier.padding(vertical: String, horizontal: String): Modifier =
    style("padding", "$vertical $horizontal")

/**
 * Sets padding for the top side of the element.
 */
fun Modifier.paddingTop(value: String): Modifier =
    style("padding-top", value)

/**
 * Sets padding for the right side of the element.
 */
fun Modifier.paddingRight(value: String): Modifier =
    style("padding-right", value)

/**
 * Sets padding for the bottom side of the element.
 */
fun Modifier.paddingBottom(value: String): Modifier =
    style("padding-bottom", value)

/**
 * Sets padding for the left side of the element.
 */
fun Modifier.paddingLeft(value: String): Modifier =
    style("padding-left", value)

// margin(String) removed - exists as member function in Modifier class

/**
 * Sets margin for horizontal and vertical dimensions.
 */
fun Modifier.margin(vertical: String, horizontal: String): Modifier =
    style("margin", "$vertical $horizontal")

/**
 * Sets margin for all four sides individually.
 */
fun Modifier.margin(top: String, right: String, bottom: String, left: String): Modifier =
    style("margin", "$top $right $bottom $left")

/**
 * Sets margin for specific sides using named parameters.
 * This allows for more readable code when setting margin on only specific sides.
 */
fun Modifier.marginOf(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier {
    // If only one parameter is provided, set that specific margin direction
    if (top != null && right == null && bottom == null && left == null) {
        return style("margin-top", top)
    }
    if (right != null && top == null && bottom == null && left == null) {
        return style("margin-right", right)
    }
    if (bottom != null && top == null && right == null && left == null) {
        return style("margin-bottom", bottom)
    }
    if (left != null && top == null && right == null && bottom == null) {
        return style("margin-left", left)
    }

    // If multiple parameters are provided, construct the margin string
    val marginParts = arrayOf(
        top ?: "0",
        right ?: "0",
        bottom ?: "0",
        left ?: "0"
    )
    return style("margin", marginParts.joinToString(" "))
}

/**
 * Sets padding for a specific side using a named parameter.
 * This allows for more readable code when setting padding on only one side.
 */
fun Modifier.paddingOf(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier {
    // If only one parameter is provided, set that specific padding direction
    if (top != null && right == null && bottom == null && left == null) {
        return style("padding-top", top)
    }
    if (right != null && top == null && bottom == null && left == null) {
        return style("padding-right", right)
    }
    if (bottom != null && top == null && right == null && left == null) {
        return style("padding-bottom", bottom)
    }
    if (left != null && top == null && right == null && bottom == null) {
        return style("padding-left", left)
    }

    // If multiple parameters are provided, construct the padding string
    val paddingParts = arrayOf(
        top ?: "0",
        right ?: "0",
        bottom ?: "0",
        left ?: "0"
    )
    return style("padding", paddingParts.joinToString(" "))
}

// marginTop(String) removed - exists as member function in Modifier class

// marginRight(String) removed - exists as member function in Modifier class

// marginBottom(String) removed - exists as member function in Modifier class

// marginLeft(String) removed - exists as member function in Modifier class

/**
 * Sets the flex wrap property.
 */
fun Modifier.flexWrap(value: String): Modifier =
    style("flex-wrap", value)

/**
 * Sets the flex wrap property using the FlexWrap enum.
 */
fun Modifier.flexWrap(value: FlexWrap): Modifier =
    style("flex-wrap", value.toString())

/**
 * Sets the flex grow property.
 */
fun Modifier.flexGrow(value: Int): Modifier =
    style("flex-grow", value.toString())

/**
 * Sets the flex shrink property.
 */
fun Modifier.flexShrink(value: Int): Modifier =
    style("flex-shrink", value.toString())

/**
 * Sets the flex basis property.
 */
fun Modifier.flexBasis(value: String): Modifier =
    style("flex-basis", value)

/**
 * Sets the align-self property.
 */
fun Modifier.alignSelf(value: String): Modifier =
    style("align-self", value)

/**
 * Sets the align-content property.
 */
fun Modifier.alignContent(value: String): Modifier =
    style("align-content", value)

/**
 * Sets the justify-items property.
 */
fun Modifier.justifyItems(value: String): Modifier =
    style("justify-items", value)

/**
 * Sets the justify-self property.
 */
fun Modifier.justifySelf(value: String): Modifier =
    style("justify-self", value)

/**
 * Sets the grid-gap property.
 */
fun Modifier.gridGap(value: String): Modifier =
    style("grid-gap", value)

/**
 * Sets the grid-area property.
 */
fun Modifier.gridArea(value: String): Modifier =
    style("grid-area", value)

/**
 * Sets the grid-column property.
 */
fun Modifier.gridColumn(value: String): Modifier =
    style("grid-column", value)

/**
 * Sets the grid-row property.
 */
fun Modifier.gridRow(value: String): Modifier =
    style("grid-row", value)

/**
 * Sets the z-index property as String (member function takes Int).
 */
fun Modifier.zIndex(value: String): Modifier =
    style("z-index", value)

/**
 * Sets the overflow-x property.
 */
fun Modifier.overflowX(value: String): Modifier =
    style("overflow-x", value)

/**
 * Sets the overflow-y property.
 */
fun Modifier.overflowY(value: String): Modifier =
    style("overflow-y", value)

/**
 * Sets the visibility property.
 */
fun Modifier.visibility(value: String): Modifier =
    style("visibility", value)

// fontSize(String) removed - exists as member function in Modifier class

// cursor(String) removed - exists as member function in Modifier class

/**
 * Sets the cursor property using the Cursor enum.
 */
fun Modifier.cursor(value: Cursor): Modifier =
    style("cursor", value.toString())

// ========================
// Advanced Layout Positioning (Type-Safe)
// ========================

/**
 * Positions an element at a specific angle and radius from the center using radial coordinates.
 * This is useful for orbital layouts and circular positioning.
 *
 * @param angle The angle position using RadialAngle enum
 * @param radius The distance from center using RadialRadius enum
 * @return A new Modifier with radial positioning applied
 */
fun Modifier.radialPosition(angle: RadialAngle, radius: RadialRadius): Modifier {
    val radians = angle.degrees.toDouble() * kotlin.math.PI / 180.0
    val radiusValue = radius.value.toDouble()
    val x = radiusValue * kotlin.math.cos(radians)
    val y = radiusValue * kotlin.math.sin(radians)

    return position(Position.Absolute)
        .style("left", "calc(50% + ${x.px})")
        .style("top", "calc(50% + ${y.px})")
        .transform(TransformFunction.Translate to "-50% -50%")
}

/**
 * Positions an element in a circular layout arrangement.
 * Automatically calculates the angle based on the item's position in the circle.
 *
 * @param radius The circle radius using RadialRadius enum
 * @param totalItems Total number of items in the circle
 * @param currentIndex Zero-based index of this item (0 to totalItems-1)
 * @return A new Modifier with circular layout positioning applied
 */
fun Modifier.circularLayout(radius: RadialRadius, totalItems: Int, currentIndex: Int): Modifier {
    val angleStep = 360.0 / totalItems
    val angleDegrees = (angleStep * currentIndex).toInt()
    val angle = RadialAngle.fromDegrees(angleDegrees) ?: RadialAngle.Deg0
    return radialPosition(angle, radius)
}

/**
 * Centers an element absolutely within its container.
 *
 * @return A new Modifier with absolute center positioning
 */
fun Modifier.centerAbsolute(): Modifier =
    position(Position.Absolute)
        .style("left", 50.percent)
        .style("top", 50.percent)
        .transform(TransformFunction.Translate to "-50% -50%")

/**
 * Centers an element with fixed positioning (relative to viewport).
 *
 * @return A new Modifier with fixed center positioning
 */
fun Modifier.fixedCenter(): Modifier =
    position(Position.Fixed)
        .style("left", 50.percent)
        .style("top", 50.percent)
        .transform(TransformFunction.Translate to "-50% -50%")

/**
 * Applies a relative offset from the element's normal position.
 *
 * @param x Horizontal offset with unit extension (e.g., 10.px)
 * @param y Vertical offset with unit extension (e.g., 20.px)
 * @return A new Modifier with relative offset applied
 */
fun Modifier.relativeOffset(x: Number, y: Number): Modifier =
    position(Position.Relative)
        .style("left", x.px)
        .style("top", y.px)

/**
 * Applies a floating animation effect with type-safe parameters.
 *
 * @param duration Animation duration using AnimationDuration enum
 * @param intensity Float distance using FloatIntensity enum
 * @return A new Modifier with floating animation applied
 */
fun Modifier.floatingAnimation(
    duration: AnimationDuration = AnimationDuration.Slow,
    intensity: FloatIntensity = FloatIntensity.Gentle
): Modifier =
    animation(
        name = "float-${intensity.value}",
        duration = duration,
        easing = "ease-in-out",
        iterationCount = "infinite",
        direction = AnimationDirection.Alternate
    )

/**
 * Applies a continuous rotation animation with type-safe parameters.
 *
 * @param speed Rotation speed using RotationSpeed enum
 * @param clockwise Direction of rotation (true = clockwise, false = counter-clockwise)
 * @return A new Modifier with rotation animation applied
 */
fun Modifier.rotatingAnimation(
    speed: RotationSpeed = RotationSpeed.Slow,
    clockwise: Boolean = true
): Modifier {
    val direction = if (clockwise) AnimationDirection.Normal else AnimationDirection.Reverse
    return animation(
        name = "rotate-360",
        duration = fromSpeed(speed),
        easing = "linear",
        iterationCount = "infinite",
        direction = direction
    )
}

/**
 * Extension to convert RotationSpeed to AnimationDuration.
 * Helper function for rotation animations.
 */
private fun fromSpeed(speed: RotationSpeed): AnimationDuration =
    when (speed.value.toInt()) {
        30 -> AnimationDuration.VerySlow
        20 -> AnimationDuration.VerySlow
        10 -> AnimationDuration.Slow
        5 -> AnimationDuration.Medium
        2 -> AnimationDuration.Fast
        else -> AnimationDuration.Medium
    }
