/**
 * # Custom Animations
 *
 * Specialized animation components and effects for the Summon framework.
 * This module provides pre-built animated components with sophisticated
 * visual effects, perfect for creating engaging user interfaces.
 *
 * ## Featured Components
 *
 * ### Interactive Elements
 * - `PulsatingButton` - Button with configurable pulsing effects
 * - `TypingText` - Character-by-character text reveal animation
 * - `PulseAnimation` - Generic pulse effect wrapper component
 * - `StaggeredAnimation` - Sequential animation for multiple children
 *
 * ### Animation Effects
 * - **Scale Pulse**: Rhythmic size changes for attention-grabbing elements
 * - **Opacity Pulse**: Gentle fade in/out breathing effects
 * - **Color Pulse**: Dynamic color transitions for visual emphasis
 * - **Typing Effect**: Realistic text typing simulation with timing control
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Pulsating call-to-action button
 * PulsatingButton(
 *     label = "Get Started",
 *     onClick = { startOnboarding() },
 *     pulseEffect = PulseEffect.SCALE,
 *     variant = ButtonVariant.PRIMARY
 * )
 *
 * // Typing text effect for dynamic content
 * TypingText(
 *     text = "Welcome to the future of UI development!",
 *     typingSpeed = 80.milliseconds
 * )
 *
 * // Pulse wrapper for any content
 * PulseAnimation(duration = 1500.milliseconds) {
 *     Icon(icon = "notification", size = 24)
 * }
 *
 * // Staggered list entrance
 * StaggeredAnimation(staggerDelay = 100.milliseconds) {
 *     items.forEach { item ->
 *         ListItem(item = item)
 *     }
 * }
 * ```
 *
 * ## Performance Features
 *
 * - **Hardware Acceleration**: Uses transform and opacity for optimal performance
 * - **Lifecycle Awareness**: Animations pause/resume with component lifecycle
 * - **Memory Efficient**: Minimal overhead with automatic cleanup
 * - **Cancellation Support**: Respects component unmounting and state changes
 *
 * ## Customization Options
 *
 * All components support extensive customization:
 *
 * - **Timing Control**: Adjustable durations, delays, and speeds
 * - **Visual Effects**: Multiple pulse types and transition styles
 * - **Styling Integration**: Full modifier support for styling and layout
 * - **State Integration**: Reactive to state changes and user interactions
 *
 * @see AnimationModifiers for modifier-based animations
 * @see AnimationUtils for animation helper functions
 * @see LaunchedEffect for custom animation orchestration
 * @since 1.0.0
 */
package codes.yousef.summon.animation

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.input.ButtonVariant
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LaunchedEffect
import codes.yousef.summon.state.mutableStateOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Defines the types of pulse effects that can be applied to components.
 *
 * Each effect creates a different visual rhythm and serves different UI purposes:
 *
 * - **SCALE**: Most noticeable, perfect for call-to-action elements
 * - **OPACITY**: Subtle and elegant, ideal for ambient notifications
 * - **COLOR**: Dynamic and engaging, great for status indicators
 *
 * ## Usage Context
 *
 * ```kotlin
 * // For urgent actions
 * PulseEffect.SCALE -> Bold, attention-grabbing
 *
 * // For ambient notifications
 * PulseEffect.OPACITY -> Gentle, non-intrusive
 *
 * // For status changes
 * PulseEffect.COLOR -> Informative, dynamic
 * ```
 *
 * @see PulsatingButton for usage with interactive elements
 * @see PulseAnimation for generic pulse applications
 * @since 1.0.0
 */
enum class PulseEffect {
    /** Pulsing by scaling up and down - most visually prominent. */
    SCALE,

    /** Pulsing by changing opacity - subtle and elegant. */
    OPACITY,

    /** Pulsing by changing color - informative and dynamic. */
    COLOR
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
        // Reset to 0 characters when text changes
        visibleCharacters.value = 0

        // Gradually show characters with proper delays
        val delayMs = typingSpeed.inWholeMilliseconds

        // Reveal one character at a time with the specified delay
        for (i in 1..text.length) {
            // Update the number of visible characters
            visibleCharacters.value = i

            // Wait for the typing speed duration before showing the next character
            delay(delayMs)
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
        .attribute(
            "style",
            "animation: pulse ${duration.inWholeMilliseconds}ms infinite ease-in-out; transform-origin: center;"
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
