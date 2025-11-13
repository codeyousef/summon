/**
 * # Animation Extensions
 *
 * Cross-platform animation extensions for the Summon framework's animation system.
 * These extensions provide a unified API for animation operations across JVM and JavaScript platforms.
 *
 * ## Key Features
 *
 * - **Cross-Platform Support**: Works seamlessly on both JVM and JavaScript runtimes
 * - **Animation Controllers**: Platform-specific animation lifecycle management
 * - **Timing Utilities**: Consistent delay and timing operations across platforms
 * - **Performance Optimized**: Leverages platform-specific optimizations
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Start an animation with custom duration
 * val controller = createAnimationController()
 * controller.startAnimation(durationMs = 500)
 *
 * // Use delay in animation sequences
 * LaunchedEffect(key1 = animationTrigger) {
 *     delay(100) // Wait before starting animation
 *     startAnimation()
 * }
 * ```
 *
 * ## Platform Implementations
 *
 * - **JVM**: Uses Java's animation timing and threading capabilities
 * - **JavaScript**: Leverages browser's `requestAnimationFrame` and `setTimeout`
 *
 * @see AnimationController for animation lifecycle management
 * @see Animation for core animation primitives
 * @since 1.0.0
 */
package codes.yousef.summon.animation

/**
 * Starts an animation with the specified duration on an [AnimationController].
 *
 * This extension function provides a cross-platform interface for beginning
 * animation playback. The implementation varies by platform to leverage
 * platform-specific animation capabilities.
 *
 * ## Platform Behavior
 *
 * - **JVM**: Uses Java's animation framework with precise timing
 * - **JavaScript**: Integrates with browser's animation APIs for optimal performance
 *
 * ## Usage
 *
 * ```kotlin
 * val controller = AnimationController()
 *
 * // Start a 300ms animation
 * controller.startAnimation(durationMs = 300)
 *
 * // Start with custom timing
 * controller.startAnimation(durationMs = 1000)
 * ```
 *
 * @param durationMs The duration of the animation in milliseconds. Must be positive.
 * @throws IllegalArgumentException if [durationMs] is negative
 * @see AnimationController for controller lifecycle
 * @see stopAnimation for stopping animations
 * @since 1.0.0
 */
expect fun AnimationController.startAnimation(durationMs: Int)

/**
 * Suspends the current coroutine for the specified amount of time.
 *
 * This function provides consistent delay behavior across platforms,
 * making it ideal for animation sequencing, timing control, and
 * creating animation chains.
 *
 * ## Platform Implementation
 *
 * - **JVM**: Uses `kotlinx.coroutines.delay` with thread-based timing
 * - **JavaScript**: Uses browser's `setTimeout` wrapped in coroutine context
 *
 * ## Usage in Animations
 *
 * ```kotlin
 * @Composable
 * fun SequentialAnimation() {
 *     var stage by remember { mutableStateOf(0) }
 *
 *     LaunchedEffect(Unit) {
 *         delay(500)  // Initial delay
 *         stage = 1   // First animation stage
 *
 *         delay(300)  // Timing between animations
 *         stage = 2   // Second animation stage
 *
 *         delay(200)
 *         stage = 3   // Final stage
 *     }
 * }
 *
 * // Staggered animation timing
 * LaunchedEffect(items) {
 *     items.forEachIndexed { index, item ->
 *         delay(index * 100L) // Stagger by 100ms
 *         animateItem(item)
 *     }
 * }
 * ```
 *
 * ## Performance Notes
 *
 * - Minimal overhead on both platforms
 * - Does not block the main thread
 * - Can be cancelled like any coroutine
 *
 * @param timeMillis The time to delay in milliseconds. Must be non-negative.
 * @throws IllegalArgumentException if [timeMillis] is negative
 * @see kotlinx.coroutines.delay for coroutine delay semantics
 * @see LaunchedEffect for composable-scoped delays
 * @since 1.0.0
 */
expect suspend fun delay(timeMillis: Long)
