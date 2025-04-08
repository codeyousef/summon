package code.yousef.summon.modifier

/**
 * Extension functions for Styling Modifiers
 * These are implemented to match the test expectations in StylingModifierTest
 */

/**
 * Sets the background image.
 */
fun Modifier.backgroundImage(value: String): Modifier =
    style("background-image", value)

/**
 * Sets the background size.
 */
fun Modifier.backgroundSize(value: String): Modifier =
    style("background-size", value)

/**
 * Sets the background position.
 */
fun Modifier.backgroundPosition(value: String): Modifier =
    style("background-position", value)

/**
 * Sets the background repeat behavior.
 */
fun Modifier.backgroundRepeat(value: String): Modifier =
    style("background-repeat", value)

/**
 * Combined background settings.
 */
fun Modifier.background(color: String, image: String, position: String, size: String, repeat: String): Modifier =
    this.background(color)
        .backgroundImage(image)
        .backgroundPosition(position)
        .backgroundSize(size)
        .backgroundRepeat(repeat)

/**
 * Sets the font family.
 */
fun Modifier.fontFamily(value: String): Modifier =
    style("font-family", value)

/**
 * Sets the font style (normal, italic, etc).
 */
fun Modifier.fontStyle(value: String): Modifier =
    style("font-style", value)

/**
 * Sets the text alignment.
 */
fun Modifier.textAlign(value: String): Modifier =
    style("text-align", value)

/**
 * Sets the text decoration (underline, line-through, etc).
 */
fun Modifier.textDecoration(value: String): Modifier =
    style("text-decoration", value)

/**
 * Sets the line height.
 */
fun Modifier.lineHeight(value: String): Modifier =
    style("line-height", value)

/**
 * Sets the letter spacing.
 */
fun Modifier.letterSpacing(value: String): Modifier =
    style("letter-spacing", value)

/**
 * Sets the text transformation (uppercase, lowercase, etc).
 */
fun Modifier.textTransform(value: String): Modifier =
    style("text-transform", value)

/**
 * Sets the border width.
 */
fun Modifier.borderWidth(value: String): Modifier =
    style("border-width", value)

/**
 * Sets the border style (solid, dashed, etc).
 */
fun Modifier.borderStyle(value: String): Modifier =
    style("border-style", value)

/**
 * Sets the border color.
 */
fun Modifier.borderColor(value: String): Modifier =
    style("border-color", value)

/**
 * Sets the top border.
 */
fun Modifier.borderTop(value: String): Modifier =
    style("border-top", value)

/**
 * Sets the right border.
 */
fun Modifier.borderRight(value: String): Modifier =
    style("border-right", value)

/**
 * Sets the bottom border.
 */
fun Modifier.borderBottom(value: String): Modifier =
    style("border-bottom", value)

/**
 * Sets the left border.
 */
fun Modifier.borderLeft(value: String): Modifier =
    style("border-left", value)

/**
 * Sets the box shadow.
 */
fun Modifier.boxShadow(value: String): Modifier =
    style("box-shadow", value)

/**
 * Sets the transition property.
 */
fun Modifier.transition(value: String): Modifier =
    style("transition", value)

/**
 * Sets which properties should transition.
 */
fun Modifier.transitionProperty(value: String): Modifier =
    style("transition-property", value)

/**
 * Sets the transition duration.
 */
fun Modifier.transitionDuration(value: String): Modifier =
    style("transition-duration", value)

/**
 * Sets the transition timing function.
 */
fun Modifier.transitionTimingFunction(value: String): Modifier =
    style("transition-timing-function", value)

/**
 * Sets the transition delay.
 */
fun Modifier.transitionDelay(value: String): Modifier =
    style("transition-delay", value)

/**
 * Sets the transform property.
 */
fun Modifier.transform(value: String): Modifier =
    style("transform", value)

/**
 * Translates the element along the X axis.
 */
fun Modifier.translateX(value: String): Modifier =
    style("transform", "translateX(${value})")

/**
 * Translates the element along the Y axis.
 */
fun Modifier.translateY(value: String): Modifier =
    style("transform", "translateY(${value})")

/**
 * Scales the element.
 */
fun Modifier.scale(value: Double): Modifier =
    style("transform", "scale(${value})")

/**
 * Rotates the element.
 */
fun Modifier.rotate(value: String): Modifier =
    style("transform", "rotate(${value})")

/**
 * Applies a blur filter.
 */
fun Modifier.blur(value: String): Modifier =
    style("filter", "blur(${value})")

/**
 * Applies a brightness filter.
 */
fun Modifier.brightness(value: Double): Modifier =
    style("filter", "brightness(${value})")

/**
 * Applies a contrast filter.
 */
fun Modifier.contrast(value: Double): Modifier =
    style("filter", "contrast(${value})")

/**
 * Applies a grayscale filter.
 */
fun Modifier.grayscale(value: Double): Modifier =
    style("filter", "grayscale(${value})")

/**
 * Applies a custom filter.
 */
fun Modifier.filter(value: String): Modifier =
    style("filter", value)

/**
 * Sets the animation.
 */
fun Modifier.animation(value: String): Modifier =
    style("animation", value)

/**
 * Sets the animation name.
 */
fun Modifier.animationName(value: String): Modifier =
    style("animation-name", value)

/**
 * Sets the animation duration.
 */
fun Modifier.animationDuration(value: String): Modifier =
    style("animation-duration", value)

/**
 * Sets the animation timing function.
 */
fun Modifier.animationTimingFunction(value: String): Modifier =
    style("animation-timing-function", value)

/**
 * Sets the animation delay.
 */
fun Modifier.animationDelay(value: String): Modifier =
    style("animation-delay", value)

/**
 * Sets the animation iteration count.
 */
fun Modifier.animationIterationCount(value: String): Modifier =
    style("animation-iteration-count", value)

/**
 * Sets the scroll behavior.
 */
fun Modifier.scrollBehavior(value: String): Modifier =
    style("scroll-behavior", value)

/**
 * Sets the scrollbar width.
 */
fun Modifier.scrollbarWidth(value: String): Modifier =
    style("scrollbar-width", value)

/**
 * Adds hover effect using another Modifier.
 */
fun Modifier.hover(hoverModifier: Modifier): Modifier {
    return hover(hoverModifier.styles)
}

/**
 * Sets the pointer-events CSS property.
 * Common values: "auto", "none".
 */
fun Modifier.pointerEvents(value: String): Modifier =
    style("pointer-events", value) 
