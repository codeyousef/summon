package code.yousef.summon.animation

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.display.TextComponent
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LaunchedEffect
import code.yousef.summon.state.mutableStateOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

// Import existing declarations
// PulseEffect comes from CustomAnimations.kt
// EnterTransition, TweenAnimation and Easing

/**
 * Simple data class to represent an offset position with x and y coordinates.
 */
data class Offset(val x: Double, val y: Double)

/**
 * Extension function for Modifier to animate a component into view.
 */
fun Modifier.animateIn(
    initialAlpha: Double,
    targetAlpha: Double,
    initialOffset: Offset,
    targetOffset: Offset,
    duration: Int
): Modifier {
    // A simple implementation that just returns the original modifier with animation attributes
    return this.attribute("style", "animation: fade-in ${duration}ms ease-out;")
        .attribute("class", "animated-element")
}

/**
 * Creates a text component that animates in when first displayed.
 *
 * @param text The text to display
 * @param enterTransition The type of entrance transition to use
 * @param duration The duration of the animation in milliseconds
 * @param modifier The modifier to apply to this component
 * @return A text component that animates in
 */
@Composable
fun animatedText(
    text: String,
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    duration: Int = 500,
    modifier: Modifier = Modifier()
): TextComponent {
    // Create text with animation
    val initialAlpha = 0.0
    val targetAlpha = 1.0

    // Calculate initial and target positions based on entrance type
    val initialOffset = when (enterTransition) {
        EnterTransition.SLIDE_IN -> Offset(0.0, 50.0)
        EnterTransition.FADE_IN -> Offset(0.0, 0.0)
        EnterTransition.EXPAND_IN -> Offset(0.0, 0.0)
        EnterTransition.ZOOM_IN -> Offset(0.0, 0.0)
    }

    // Create the text component with animation
    return TextComponent(
        text = text,
        modifier = modifier.animateIn(
            initialAlpha = initialAlpha,
            targetAlpha = targetAlpha,
            initialOffset = initialOffset,
            targetOffset = Offset(0.0, 0.0),
            duration = duration
        )
    )
}

/**
 * Creates an infinite animation that can be used for repeating effects.
 */
interface InfiniteAnimation {
    fun animateFloat(
        initialValue: Float,
        targetValue: Float,
        animation: TweenAnimation
    ): Float

    fun animateInt(
        initialValue: Int,
        targetValue: Int,
        animation: TweenAnimation
    ): Int
}

/**
 * Creates a pulse animation effect for any component.
 *
 * @param pulseInterval The interval between pulses in milliseconds
 * @param minScale The minimum scale during the pulse
 * @param maxScale The maximum scale during the pulse
 * @param modifier Base modifier to apply
 * @param content The content to animate
 */
@Composable
fun pulseAnimation(
    pulseInterval: Int = 1000,
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    // Simplified implementation that just renders the content without animation
    // Until we implement the proper animation system
    Column(
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Creates a staggered animation where each child appears with a delay.
 *
 * @param items List of items to display with staggered animation
 * @param staggerDelay Delay between each item's animation in milliseconds
 * @param enterTransition The animation to use for each item
 * @param modifier Base modifier to apply
 * @param itemContent Function to create content for each item
 */
@Composable
fun <T> staggeredAnimation(
    items: List<T>,
    staggerDelay: Int = 100,
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    modifier: Modifier = Modifier(),
    itemContent: @Composable (T) -> Unit
) {
    // Simplified implementation that just renders the items without animation
    Column(
        modifier = modifier
    ) {
        items.forEach { item ->
            itemContent(item)
        }
    }
}

/**
 * A utility function that wraps a component with an entry animation.
 * Currently a placeholder that returns the original modifier without animation.
 */
@Composable
fun animateIn(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    // Placeholder: currently no animation applied
    content()
}

/**
 * Creates a button with a pulsing animation effect.
 *
 * @param label Text to display on the button
 * @param onClick Callback to invoke when the button is clicked
 * @param pulseEffect The type of pulse effect to apply
 * @param variant Button style variant
 * @param modifier Base modifier to apply
 * @return A pulsating Button component
 */
@Composable
fun pulsatingButton(
    label: String,
    onClick: () -> Unit,
    pulseEffect: PulseEffect = PulseEffect.SCALE,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    modifier: Modifier = Modifier()
): Unit {
    // Apply different pulse effects based on the selected type
    val animationName = when (pulseEffect) {
        PulseEffect.SCALE -> "pulse-scale"
        PulseEffect.OPACITY -> "pulse-opacity"
        PulseEffect.COLOR -> "pulse-color"
    }

    val animatedModifier = modifier
        .attribute("style", "animation: $animationName 1.5s infinite ease-in-out; transform-origin: center;")
        .attribute("class", "$animationName-animation")

    Button(
        label = label,
        onClick = onClick,
        variant = variant,
        modifier = animatedModifier
    )
}

/**
 * Animate the entrance of a text component with various transition effects.
 */
fun animateInText(
    text: String,
    modifier: Modifier = Modifier(),
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    duration: Duration = 500.milliseconds
): TextComponent {
    // Create text with animation
    val initialAlpha = 0.0
    val targetAlpha = 1.0

    // Calculate initial and target positions based on entrance type
    val initialOffset = when (enterTransition) {
        EnterTransition.SLIDE_IN -> Offset(0.0, 50.0)
        EnterTransition.FADE_IN -> Offset(0.0, 0.0)
        EnterTransition.EXPAND_IN -> Offset(0.0, 0.0)
        EnterTransition.ZOOM_IN -> Offset(0.0, 0.0)
    }

    // Create the text component with animation
    return TextComponent(
        text = text,
        modifier = modifier.animateIn(
            initialAlpha = initialAlpha,
            targetAlpha = targetAlpha,
            initialOffset = initialOffset,
            targetOffset = Offset(0.0, 0.0),
            duration = duration.inWholeMilliseconds.toInt()
        )
    )
}

/**
 * Creates a text component that appears to be typed out character by character.
 *
 * @param text The text to display
 * @param typingSpeed The speed at which characters appear in milliseconds
 * @param modifier The modifier to apply to this component
 * @return A text component with a typing animation
 */
@Composable
fun typingText(
    text: String,
    typingSpeed: Duration = 50.milliseconds,
    modifier: Modifier = Modifier()
) {
    // Initial state for how much of the text to show
    val visibleCharacters = mutableStateOf(0)

    // Use LaunchedEffect to animate the typing
    LaunchedEffect(text) {
        // Gradually show characters
        for (i in 1..text.length) {
            visibleCharacters.value = i
            // Note: We can't use delay here as it's not available in a direct way
            // This is a simplified implementation
        }
    }

    Text(
        text = text.take(visibleCharacters.value),
        modifier = modifier.attribute("class", "typing-text")
    )
}

/**
 * Applies a pulse animation to a component.
 */
@Composable
fun pulseAnimation(
    duration: Duration = 1000.milliseconds,
    content: @Composable () -> Unit
) {
    // Apply pulse animation using CSS class and attributes
    val pulseModifier = Modifier()
        .attribute("class", "general-pulse-animation")
        .attribute(
            "style",
            "animation: general-pulse ${duration.inWholeMilliseconds}ms infinite ease-in-out; transform-origin: center;"
        )

    // Wrap the content with Column to apply modifier
    Column(
        modifier = pulseModifier
    ) {
        content()
    }
}

/**
 * Applies a staggered animation to multiple children.
 */
@Composable
fun staggeredAnimation(
    staggerDelay: Duration = 100.milliseconds,
    content: @Composable () -> Unit
) {
    // Use simple class-based approach for staggered animation
    val staggerModifier = Modifier()
        .attribute("class", "staggered-container")
        .attribute("data-stagger-delay", staggerDelay.inWholeMilliseconds.toString())

    // Wrap the content in a Column with our stagger modifier
    Column(
        modifier = staggerModifier
    ) {
        content()
    }
}
