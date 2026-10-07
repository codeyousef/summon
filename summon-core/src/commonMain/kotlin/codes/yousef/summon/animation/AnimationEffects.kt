package codes.yousef.summon.animation

import codes.yousef.summon.modifier.*

/**
 * Prebuilt animation effects built on top of the core animation modifiers.
 * These helpers keep the frequently-used motion patterns in a dedicated file,
 * reducing the size of `AnimationModifiers.kt` and making each effect easier to locate.
 */

/** Applies the named bounce keyframes. */
fun Modifier.bounce(
    name: String = "bounce",
    duration: Int = 800,
    delay: Int = 0,
    iterationCount: String = "1"
): Modifier =
    animateWithEasing(
        name = name,
        easing = Easing.BOUNCE_OUT,
        duration = duration,
        delay = delay,
        iterationCount = iterationCount
    )

/** Applies the named elastic keyframes. */
fun Modifier.elastic(
    name: String = "elastic",
    duration: Int = 1000,
    delay: Int = 0,
    iterationCount: String = "1"
): Modifier =
    animateWithEasing(
        name = name,
        easing = Easing.ELASTIC_OUT,
        duration = duration,
        delay = delay,
        iterationCount = iterationCount
    )

/** Applies fade-in keyframes. */
fun Modifier.fadeIn(
    duration: Int = 300,
    delay: Int = 0,
    easing: Easing = Easing.EASE_IN_OUT
): Modifier =
    animateWithEasing(
        name = "fade-in",
        easing = easing,
        duration = duration,
        delay = delay
    )

/** Applies fade-out keyframes. */
fun Modifier.fadeOut(
    duration: Int = 300,
    delay: Int = 0,
    easing: Easing = Easing.EASE_IN_OUT
): Modifier =
    animateWithEasing(
        name = "fade-out",
        easing = easing,
        duration = duration,
        delay = delay
    )

/** Applies top-entry slide keyframes. */
fun Modifier.slideInFromTop(
    duration: Int = 500,
    delay: Int = 0,
    easing: Easing = Easing.CUBIC_OUT
): Modifier =
    animateWithEasing(
        name = "slide-in-top",
        easing = easing,
        duration = duration,
        delay = delay
    )

/** Applies bottom-entry slide keyframes. */
fun Modifier.slideInFromBottom(
    duration: Int = 500,
    delay: Int = 0,
    easing: Easing = Easing.CUBIC_OUT
): Modifier =
    animateWithEasing(
        name = "slide-in-bottom",
        easing = easing,
        duration = duration,
        delay = delay
    )

/** Applies zoom-in keyframes. */
fun Modifier.zoomIn(
    duration: Int = 400,
    delay: Int = 0,
    easing: Easing = Easing.CUBIC_OUT
): Modifier =
    animateWithEasing(
        name = "zoom-in",
        easing = easing,
        duration = duration,
        delay = delay
    )

/** Applies zoom-out keyframes. */
fun Modifier.zoomOut(
    duration: Int = 400,
    delay: Int = 0,
    easing: Easing = Easing.CUBIC_IN
): Modifier =
    animateWithEasing(
        name = "zoom-out",
        easing = easing,
        duration = duration,
        delay = delay
    )

/** Applies an infinitely repeating pulse. */
fun Modifier.pulse(
    duration: Int = 1500,
    easing: Easing = Easing.SINE_IN_OUT
): Modifier =
    animateWithEasing(
        name = "pulse",
        easing = easing,
        duration = duration,
        iterationCount = "infinite"
    )

/** Applies horizontal shake keyframes. */
fun Modifier.shake(
    duration: Int = 500,
    iterationCount: String = "1"
): Modifier =
    animate(
        name = "shake",
        duration = duration,
        timingFunction = "ease-in-out",
        iterationCount = iterationCount
    )

/** Applies infinitely alternating floating motion by default. */
fun Modifier.float(
    duration: Int = 3000,
    iterationCount: String = "infinite",
    easing: Easing = Easing.SINE_IN_OUT
): Modifier =
    animateWithEasing(
        name = "float",
        easing = easing,
        duration = duration,
        iterationCount = iterationCount,
        direction = "alternate"
    )

/** Applies an infinitely blinking text-cursor effect. */
fun Modifier.typingCursor(duration: Int = 800): Modifier =
    animate(
        name = "blink",
        duration = duration,
        timingFunction = "steps(1)",
        iterationCount = "infinite"
    )

/** Applies horizontal-axis flip keyframes. */
fun Modifier.flipX(
    duration: Int = 600,
    easing: Easing = Easing.CUBIC_IN_OUT
): Modifier =
    animateWithEasing(
        name = "flip-x",
        easing = easing,
        duration = duration
    )

/** Applies vertical-axis flip keyframes. */
fun Modifier.flipY(
    duration: Int = 600,
    easing: Easing = Easing.CUBIC_IN_OUT
): Modifier =
    animateWithEasing(
        name = "flip-y",
        easing = easing,
        duration = duration
    )
