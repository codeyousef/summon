package codes.yousef.summon.animation

import codes.yousef.summon.modifier.*

/**
 * Prebuilt animation effects built on top of the core animation modifiers.
 * These helpers keep the frequently-used motion patterns in a dedicated file,
 * reducing the size of `AnimationModifiers.kt` and making each effect easier to locate.
 */

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

fun Modifier.typingCursor(duration: Int = 800): Modifier =
    animate(
        name = "blink",
        duration = duration,
        timingFunction = "steps(1)",
        iterationCount = "infinite"
    )

fun Modifier.flipX(
    duration: Int = 600,
    easing: Easing = Easing.CUBIC_IN_OUT
): Modifier =
    animateWithEasing(
        name = "flip-x",
        easing = easing,
        duration = duration
    )

fun Modifier.flipY(
    duration: Int = 600,
    easing: Easing = Easing.CUBIC_IN_OUT
): Modifier =
    animateWithEasing(
        name = "flip-y",
        easing = easing,
        duration = duration
    )
