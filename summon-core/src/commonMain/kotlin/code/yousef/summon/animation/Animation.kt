/**
 * # Animation System Package
 *
 * This package provides a comprehensive animation framework for the Summon UI library,
 * enabling smooth, performant animations across all supported platforms (Browser, JVM/Server).
 *
 * ## Overview
 *
 * The animation system in Summon provides:
 *
 * - **Cross-Platform Animations**: Consistent animation behavior across browser and server platforms
 * - **Physics-Based Animations**: Spring animations for natural, organic motion
 * - **Tween Animations**: Traditional keyframe-based animations with easing functions
 * - **CSS Integration**: Seamless integration with CSS animations for optimal performance
 * - **Animation Control**: Fine-grained control over animation lifecycle and state
 * - **Extensive Easing**: 22+ built-in easing functions for diverse animation styles
 *
 * ## Key Components
 *
 * - [Animation] - Base interface for all animation types
 * - [SpringAnimation] - Physics-based spring animations
 * - [TweenAnimation] - Traditional keyframe animations
 * - [AnimationController] - Control animation playback and state
 * - [Easing] - Comprehensive easing function library
 * - [AnimationStatus] - Track animation lifecycle state
 *
 * ## Animation Types
 *
 * ### Spring Animations
 * ```kotlin
 * val springAnim = SpringAnimation(
 *     stiffness = 200f,
 *     damping = 15f,
 *     durationMs = 500
 * )
 * ```
 *
 * ### Tween Animations
 * ```kotlin
 * val tweenAnim = TweenAnimation(
 *     durationMs = 300,
 *     easing = Easing.EASE_IN_OUT
 * )
 * ```
 *
 * ## Platform Integration
 *
 * - **Browser**: Leverages CSS animations and transitions for optimal performance
 * - **Server**: Provides animation calculations for server-side rendering
 * - **Testing**: Deterministic animation behavior for reliable testing
 *
 * @since 1.0.0
 */
package code.yousef.summon.animation

import code.yousef.summon.animation.AnimationController.cancel
import code.yousef.summon.animation.AnimationController.pause
import code.yousef.summon.animation.AnimationController.resume
import code.yousef.summon.animation.AnimationController.stop


/**
 * Represents the current lifecycle status of an animation.
 *
 * Animation status provides a way to track and respond to the current state
 * of running animations. This is particularly useful for:
 * - Coordinating multiple animations
 * - Implementing animation sequences
 * - Providing user feedback during animations
 * - Cleanup and resource management
 *
 * ## Status Transitions
 *
 * Typical animation lifecycle:
 * ```
 * IDLE → RUNNING → STOPPED
 *   ↑      ↕ PAUSED      ↑
 *   └─────────────────────┘
 * ```
 *
 * @see AnimationController for controlling animation status
 * @since 1.0.0
 */
expect enum class AnimationStatus {
    /** Animation has not started, has been reset, or is in its initial state */
    IDLE,

    /** Animation is currently playing and progressing through its timeline */
    RUNNING,

    /** Animation is temporarily paused but can be resumed */
    PAUSED,

    /** Animation has finished playing or has been explicitly stopped */
    STOPPED
}

/**
 * Global animation controller for managing animation playback and state across the application.
 *
 * The AnimationController provides centralized control over animations initiated by the
 * platform renderer. It enables fine-grained control over animation lifecycle, including
 * pausing, resuming, and stopping animations programmatically.
 *
 * ## Use Cases
 *
 * - **Performance Optimization**: Pause animations when the app goes to background
 * - **User Accessibility**: Allow users to disable animations for reduced motion
 * - **Debugging**: Control animation playback during development and testing
 * - **Coordination**: Synchronize multiple animations across the application
 * - **Battery Conservation**: Pause animations to conserve device battery
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Pause all animations when app goes to background
 * fun onAppPaused() {
 *     if (AnimationController.status == AnimationStatus.RUNNING) {
 *         AnimationController.pause()
 *     }
 * }
 *
 * // Resume animations when app becomes active
 * fun onAppResumed() {
 *     if (AnimationController.status == AnimationStatus.PAUSED) {
 *         AnimationController.resume()
 *     }
 * }
 *
 * // Check animation progress for coordination
 * if (AnimationController.progress > 0.5f) {
 *     triggerSecondaryAnimation()
 * }
 * ```
 *
 * ## Platform Behavior
 *
 * - **Browser**: Controls CSS animations and Web Animations API
 * - **Server**: Manages animation calculations for SSR scenarios
 * - **Testing**: Provides synchronous control for predictable test behavior
 *
 * @see AnimationStatus for tracking animation state
 * @see Animation for individual animation instances
 * @since 1.0.0
 */
expect object AnimationController {
    /**
     * Pauses all currently running animations.
     *
     * When paused, animations retain their current progress and can be resumed
     * from the same position. This is useful for performance optimization and
     * accessibility features.
     *
     * @see resume
     * @see AnimationStatus.PAUSED
     * @since 1.0.0
     */
    fun pause()

    /**
     * Resumes all paused animations from their current progress.
     *
     * Animations continue from where they were paused, maintaining smooth
     * visual continuity. Has no effect if animations are not currently paused.
     *
     * @see pause
     * @see AnimationStatus.RUNNING
     * @since 1.0.0
     */
    fun resume()

    /**
     * Cancels all animations and resets them to their initial state.
     *
     * This immediately stops all animations and returns animated properties
     * to their starting values. Use this for immediate animation termination
     * with visual reset.
     *
     * @see stop
     * @see AnimationStatus.IDLE
     * @since 1.0.0
     */
    fun cancel()

    /**
     * Stops all animations at their current progress without resetting.
     *
     * Unlike [cancel], this preserves the current animated values, allowing
     * animations to stop naturally at their current position.
     *
     * @see cancel
     * @see AnimationStatus.STOPPED
     * @since 1.0.0
     */
    fun stop()

    /**
     * The current status of the global animation system.
     *
     * Reflects the overall state of animations in the application:
     * - [AnimationStatus.IDLE] - No animations are running
     * - [AnimationStatus.RUNNING] - Animations are actively playing
     * - [AnimationStatus.PAUSED] - Animations are temporarily paused
     * - [AnimationStatus.STOPPED] - Animations have been stopped
     *
     * @see AnimationStatus
     * @since 1.0.0
     */
    val status: AnimationStatus

    /**
     * The current progress of the primary animation (typically 0.0 to 1.0).
     *
     * When multiple animations are running, this represents the progress of
     * the most recently started or primary animation. The value ranges from:
     * - 0.0: Animation start
     * - 1.0: Animation completion
     * - Values may exceed 1.0 for repeating animations
     *
     * ## Usage
     *
     * ```kotlin
     * // Coordinate secondary animations based on progress
     * when {
     *     AnimationController.progress >= 0.5f -> showSecondaryUI()
     *     AnimationController.progress >= 0.8f -> prepareNextScreen()
     * }
     * ```
     *
     * @since 1.0.0
     */
    val progress: Float
}

/**
 * Base interface for all animation types in the Summon framework.
 *
 * The Animation interface provides a unified contract for all animation implementations,
 * enabling consistent behavior across different animation types (spring, tween, etc.)
 * and platforms (browser, server). All animations can be controlled, queried, and
 * integrated with the broader Summon ecosystem.
 *
 * ## Core Concepts
 *
 * ### Animation Progress
 * All animations operate on a normalized progress scale from 0.0 to 1.0:
 * - 0.0 represents the animation start state
 * - 1.0 represents the animation end state
 * - Intermediate values represent proportional progress
 *
 * ### Cross-Platform Compatibility
 * Animations provide both:
 * - **Programmatic Access**: [getValue] for real-time calculations
 * - **CSS Integration**: [toCssAnimationString] and [toCssKeyframes] for browser optimization
 *
 * ### Animation Types
 * - **Physics-Based**: [SpringAnimation] for natural, organic motion
 * - **Keyframe-Based**: [TweenAnimation] for precise, controlled animations
 * - **Custom**: Implement this interface for specialized animation behavior
 *
 * ## Implementation Guidelines
 *
 * When implementing custom animations:
 * 1. Ensure [getValue] returns values in the expected range for your use case
 * 2. Provide meaningful CSS output for browser compatibility
 * 3. Consider performance implications of complex calculations
 * 4. Handle edge cases (negative fractions, values > 1.0)
 * 5. Make animations deterministic for testing
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Create a spring animation
 * val springAnim = SpringAnimation(
 *     stiffness = 200f,
 *     damping = 15f,
 *     durationMs = 400
 * )
 *
 * // Use programmatically
 * val progress = 0.5f
 * val animatedValue = springAnim.getValue(progress)
 *
 * // Generate CSS for browser
 * val cssAnimation = springAnim.toCssAnimationString()
 * val cssKeyframes = springAnim.toCssKeyframes("myAnimation")
 * ```
 *
 * ## Performance Considerations
 *
 * - **Browser**: CSS animations are hardware-accelerated when possible
 * - **Server**: Calculations are performed for SSR scenarios
 * - **Memory**: Animation instances should be lightweight and reusable
 * - **CPU**: Complex easing functions may impact performance on lower-end devices
 *
 * @see SpringAnimation for physics-based animations
 * @see TweenAnimation for traditional keyframe animations
 * @see Easing for available easing functions
 * @see AnimationController for global animation control
 * @since 1.0.0
 */
interface Animation {
    /**
     * Get the current animation value.
     *
     * @param fraction The animation progress between 0.0 and 1.0
     * @return The animated value at the given progress
     */
    fun getValue(fraction: Float): Float

    /**
     * The total duration of the animation in milliseconds.
     */
    val durationMs: Int

    /**
     * Whether the animation should repeat.
     */
    val repeating: Boolean

    /**
     * Get the CSS animation string representation
     */
    fun toCssAnimationString(): String

    /**
     * Get the CSS keyframes definition
     */
    fun toCssKeyframes(keyframesName: String): String
}

/**
 * A physics-based spring animation.
 *
 * @param stiffness The stiffness of the spring (higher = more rigid)
 * @param damping The amount of damping (higher = less oscillation)
 * @param durationMs The total animation duration in milliseconds
 * @param repeating Whether the animation should repeat
 */
class SpringAnimation(
    val stiffness: Float = 150f,
    val damping: Float = 12f,
    override val durationMs: Int = 300,
    override val repeating: Boolean = false
) : Animation {
    override fun getValue(fraction: Float): Float {
        // Simple approximation of a spring animation
        return 1f - (1f - fraction) * kotlin.math.exp(-damping * fraction)
    }

    override fun toCssAnimationString(): String {
        val timingFunction = "cubic-bezier(0.5, 0.0, 0.2, 1.0)" // Spring-like curve
        return "${durationMs}ms $timingFunction"
    }

    override fun toCssKeyframes(keyframesName: String): String {
        val steps = 20
        val keyframes = StringBuilder("@keyframes $keyframesName {\n")

        for (i in 0..steps) {
            val progress = i.toFloat() / steps
            val value = getValue(progress)
            keyframes.append("  ${(progress * 100).toInt()}% { transform: scale($value); }\n")
        }

        keyframes.append("}")
        return keyframes.toString()
    }
}

/**
 * Animation that changes at a constant rate, with optional easing.
 *
 * @param durationMs The animation duration in milliseconds
 * @param easing The easing function to apply
 * @param repeating Whether the animation should repeat
 */
class TweenAnimation(
    override val durationMs: Int = 300,
    val easing: Easing = Easing.LINEAR,
    override val repeating: Boolean = false
) : Animation {
    override fun getValue(fraction: Float): Float {
        // Apply the easing function
        return easing.getValue(fraction)
    }

    override fun toCssAnimationString(): String {
        return "${durationMs}ms ${easing.toCssString()}"
    }

    override fun toCssKeyframes(keyframesName: String): String {
        return """
            @keyframes $keyframesName {
              0% { transform: scale(0); opacity: 0; }
              100% { transform: scale(1); opacity: 1; }
            }
        """.trimIndent()
    }
}

/**
 * Enhanced easing functions for animations.
 * This enum provides the standard easing functions as well as integration
 * with the more comprehensive EasingFunctions object.
 */
enum class Easing {
    LINEAR,      // Constant rate of change
    EASE_IN,     // Start slow, end fast
    EASE_OUT,    // Start fast, end slow
    EASE_IN_OUT, // Start slow, middle fast, end slow

    // Advanced Easing Types
    SINE_IN,
    SINE_OUT,
    SINE_IN_OUT,
    QUAD_IN,
    QUAD_OUT,
    QUAD_IN_OUT,
    CUBIC_IN,
    CUBIC_OUT,
    CUBIC_IN_OUT,
    QUART_IN,
    QUART_OUT,
    QUART_IN_OUT,
    QUINT_IN,
    QUINT_OUT,
    QUINT_IN_OUT,
    EXPO_IN,
    EXPO_OUT,
    EXPO_IN_OUT,
    CIRC_IN,
    CIRC_OUT,
    CIRC_IN_OUT,
    BACK_IN,
    BACK_OUT,
    BACK_IN_OUT,
    ELASTIC_IN,
    ELASTIC_OUT,
    ELASTIC_IN_OUT,
    BOUNCE_IN,
    BOUNCE_OUT,
    BOUNCE_IN_OUT;

    /**
     * Get the easing function value at the specified fraction.
     */
    fun getValue(fraction: Float): Float {
        return when (this) {
            LINEAR -> EasingFunctions.linear(fraction)
            EASE_IN -> EasingFunctions.easeInQuad(fraction)
            EASE_OUT -> EasingFunctions.easeOutQuad(fraction)
            EASE_IN_OUT -> EasingFunctions.easeInOutQuad(fraction)
            SINE_IN -> EasingFunctions.easeInSine(fraction)
            SINE_OUT -> EasingFunctions.easeOutSine(fraction)
            SINE_IN_OUT -> EasingFunctions.easeInOutSine(fraction)
            QUAD_IN -> EasingFunctions.easeInQuad(fraction)
            QUAD_OUT -> EasingFunctions.easeOutQuad(fraction)
            QUAD_IN_OUT -> EasingFunctions.easeInOutQuad(fraction)
            CUBIC_IN -> EasingFunctions.easeInCubic(fraction)
            CUBIC_OUT -> EasingFunctions.easeOutCubic(fraction)
            CUBIC_IN_OUT -> EasingFunctions.easeInOutCubic(fraction)
            QUART_IN -> EasingFunctions.easeInQuart(fraction)
            QUART_OUT -> EasingFunctions.easeOutQuart(fraction)
            QUART_IN_OUT -> EasingFunctions.easeInOutQuart(fraction)
            QUINT_IN -> EasingFunctions.easeInQuint(fraction)
            QUINT_OUT -> EasingFunctions.easeOutQuint(fraction)
            QUINT_IN_OUT -> EasingFunctions.easeInOutQuint(fraction)
            EXPO_IN -> EasingFunctions.easeInExpo(fraction)
            EXPO_OUT -> EasingFunctions.easeOutExpo(fraction)
            EXPO_IN_OUT -> EasingFunctions.easeInOutExpo(fraction)
            CIRC_IN -> EasingFunctions.easeInCirc(fraction)
            CIRC_OUT -> EasingFunctions.easeOutCirc(fraction)
            CIRC_IN_OUT -> EasingFunctions.easeInOutCirc(fraction)
            BACK_IN -> EasingFunctions.easeInBack(fraction)
            BACK_OUT -> EasingFunctions.easeOutBack(fraction)
            BACK_IN_OUT -> EasingFunctions.easeInOutBack(fraction)
            ELASTIC_IN -> EasingFunctions.easeInElastic(fraction)
            ELASTIC_OUT -> EasingFunctions.easeOutElastic(fraction)
            ELASTIC_IN_OUT -> EasingFunctions.easeInOutElastic(fraction)
            BOUNCE_IN -> EasingFunctions.easeInBounce(fraction)
            BOUNCE_OUT -> EasingFunctions.easeOutBounce(fraction)
            BOUNCE_IN_OUT -> EasingFunctions.easeInOutBounce(fraction)
        }
    }

    /**
     * Convert to CSS timing function string.
     * For advanced easing functions, we convert them to approximate cubic-bezier values.
     */
    fun toCssString(): String {
        return when (this) {
            LINEAR -> "linear"
            EASE_IN -> "cubic-bezier(0.4, 0, 1, 1)"
            EASE_OUT -> "cubic-bezier(0, 0, 0.2, 1)"
            EASE_IN_OUT -> "cubic-bezier(0.4, 0, 0.2, 1)"
            SINE_IN -> "cubic-bezier(0.12, 0, 0.39, 0)"
            SINE_OUT -> "cubic-bezier(0.61, 1, 0.88, 1)"
            SINE_IN_OUT -> "cubic-bezier(0.37, 0, 0.63, 1)"
            QUAD_IN -> "cubic-bezier(0.11, 0, 0.5, 0)"
            QUAD_OUT -> "cubic-bezier(0.5, 1, 0.89, 1)"
            QUAD_IN_OUT -> "cubic-bezier(0.45, 0, 0.55, 1)"
            CUBIC_IN -> "cubic-bezier(0.32, 0, 0.67, 0)"
            CUBIC_OUT -> "cubic-bezier(0.33, 1, 0.68, 1)"
            CUBIC_IN_OUT -> "cubic-bezier(0.65, 0, 0.35, 1)"
            QUART_IN -> "cubic-bezier(0.5, 0, 0.75, 0)"
            QUART_OUT -> "cubic-bezier(0.25, 1, 0.5, 1)"
            QUART_IN_OUT -> "cubic-bezier(0.76, 0, 0.24, 1)"
            QUINT_IN -> "cubic-bezier(0.64, 0, 0.78, 0)"
            QUINT_OUT -> "cubic-bezier(0.22, 1, 0.36, 1)"
            QUINT_IN_OUT -> "cubic-bezier(0.83, 0, 0.17, 1)"
            EXPO_IN -> "cubic-bezier(0.7, 0, 0.84, 0)"
            EXPO_OUT -> "cubic-bezier(0.16, 1, 0.3, 1)"
            EXPO_IN_OUT -> "cubic-bezier(0.87, 0, 0.13, 1)"
            CIRC_IN -> "cubic-bezier(0.55, 0, 1, 0.45)"
            CIRC_OUT -> "cubic-bezier(0, 0.55, 0.45, 1)"
            CIRC_IN_OUT -> "cubic-bezier(0.85, 0, 0.15, 1)"
            BACK_IN -> "cubic-bezier(0.36, 0, 0.66, -0.56)"
            BACK_OUT -> "cubic-bezier(0.34, 1.56, 0.64, 1)"
            BACK_IN_OUT -> "cubic-bezier(0.68, -0.6, 0.32, 1.6)"
            ELASTIC_IN -> "cubic-bezier(0.7, 0, 0.3, 1)"
            ELASTIC_OUT -> "cubic-bezier(0.3, 1, 0.7, 1)"
            ELASTIC_IN_OUT -> "cubic-bezier(0.9, 0.1, 0.1, 0.9)"
            BOUNCE_IN -> "cubic-bezier(0.5, 0, 1, 1)"
            BOUNCE_OUT -> "cubic-bezier(0, 0, 0.5, 1)"
            BOUNCE_IN_OUT -> "cubic-bezier(0.5, 0, 0.5, 1)"
        }
    }
}
