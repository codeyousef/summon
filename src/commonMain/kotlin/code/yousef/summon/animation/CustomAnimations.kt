package code.yousef.summon.animation

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.attribute
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.runtime.LaunchedEffect
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Types of pulse effects that can be applied to components.
 */
enum class PulseEffect {
    SCALE,  // Pulsing by scaling up and down
    OPACITY, // Pulsing by changing opacity
    COLOR    // Pulsing by changing color
}

/**
 * Creates a button with a pulsing animation effect.
 *
 * @param label Text to display on the button
 * @param onClick Callback to invoke when the button is clicked
 * @param pulseEffect The type of pulse effect to apply
 * @param variant Button style variant
 * @param modifier Base modifier to apply
 */
@Composable
fun PulsatingButton(
    label: String,
    onClick: () -> Unit,
    pulseEffect: PulseEffect = PulseEffect.SCALE,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    modifier: Modifier = Modifier()
) {
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
 * Creates a text component that appears to be typed out character by character.
 *
 * @param text The text to display
 * @param typingSpeed The speed at which characters appear in milliseconds
 * @param modifier The modifier to apply to this component
 */
@Composable
fun TypingText(
    text: String,
    typingSpeed: Duration = 50.milliseconds,
    modifier: Modifier = Modifier()
) {
    // Initial state for how much of the text to show
    val visibleCharacters = mutableStateOf(0)
    
    // Use LaunchedEffect to animate the typing
    LaunchedEffect(text) {
        // Gradually show characters - this is a simplified implementation
        // In a real implementation, we'd use proper timers and delays
        for (i in 1..text.length) {
            visibleCharacters.value = i
        }
    }
    
    Text(
        text = text.take(visibleCharacters.value),
        modifier = modifier.attribute("class", "typing-text")
    )
}

/**
 * Applies a pulse animation to a component.
 *
 * @param duration The duration of one pulse cycle
 * @param content The content to animate
 */
@Composable
fun PulseAnimation(
    duration: Duration = 1000.milliseconds,
    content: @Composable () -> Unit
) {
    // Apply pulse animation using CSS class and attributes
    val pulseModifier = Modifier()
        .attribute("class", "pulse-animation")
        .attribute("style", "animation: pulse ${duration.inWholeMilliseconds}ms infinite ease-in-out; transform-origin: center;")
    
    // Wrap the content with Column to apply modifier
    Column(
        modifier = pulseModifier
    ) {
        content()
    }
}

/**
 * Applies a staggered animation to multiple children.
 *
 * @param staggerDelay Delay between each child's animation
 * @param content The content to animate
 */
@Composable
fun StaggeredAnimation(
    staggerDelay: Duration = 100.milliseconds,
    content: @Composable () -> Unit
) {
    // Use class-based approach for staggered animation
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