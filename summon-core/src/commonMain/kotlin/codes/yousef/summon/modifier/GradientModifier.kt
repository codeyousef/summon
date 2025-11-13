/**
 * # Gradient Modifiers
 *
 * Advanced gradient styling modifiers for the Summon framework.
 * This module provides comprehensive gradient capabilities including
 * linear, radial, and conic gradients, plus modern visual effects.
 *
 * ## Core Features
 *
 * - **Linear Gradients**: Directional color transitions
 * - **Radial Gradients**: Circular and elliptical gradients
 * - **Conic Gradients**: Angular color transitions
 * - **Glass Morphism**: Modern frosted glass effects
 * - **Preset Collection**: Ready-to-use gradient themes
 * - **Cross-Browser Support**: Compatible gradients across browsers
 *
 * ## Gradient Types
 *
 * ### Linear Gradients
 * - Directional color transitions
 * - Customizable angles and directions
 * - Multiple color stops support
 *
 * ### Radial Gradients
 * - Circular and elliptical patterns
 * - Configurable shapes and sizes
 * - Center positioning control
 *
 * ### Conic Gradients
 * - Angular color transitions
 * - Customizable starting angles
 * - Perfect for circular progress indicators
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Linear gradient with custom direction
 * Box(
 *     modifier = Modifier()
 *         .linearGradient(
 *             direction = "45deg",
 *             "#ff6b6b", "#4ecdc4", "#45b7d1"
 *         )
 * )
 *
 * // Radial gradient for spotlight effects
 * Card(
 *     modifier = Modifier()
 *         .radialGradient(
 *             shape = "ellipse",
 *             "#ffffff", "#f0f0f0", "#e0e0e0"
 *         )
 * )
 *
 * // Conic gradient for loading spinners
 * Box(
 *     modifier = Modifier()
 *         .conicGradient(
 *             from = "0deg",
 *             "#ff0000", "#ffff00", "#00ff00", "#00ffff", "#0000ff", "#ff00ff", "#ff0000"
 *         )
 * )
 *
 * // Glass morphism effect for modern UI
 * Modal(
 *     modifier = Modifier()
 *         .glassMorphism(
 *             backgroundColor = "rgba(255, 255, 255, 0.1)",
 *             backdropFilter = "blur(20px)",
 *             border = "1px solid rgba(255, 255, 255, 0.2)"
 *         )
 * )
 * ```
 *
 * ## Preset Gradients
 *
 * ```kotlin
 * // Use preset gradients for consistent branding
 * with(GradientPresets) {
 *     // Primary brand gradient
 *     Button(modifier = Modifier().modernPrimary())
 *
 *     // Ocean-themed gradient
 *     Header(modifier = Modifier().oceanBlue())
 *
 *     // Dark mode compatible
 *     Sidebar(modifier = Modifier().darkModeGradient())
 * }
 * ```
 *
 * ## Advanced Patterns
 *
 * ### Multi-Stop Linear Gradients
 * ```kotlin
 * Box(
 *     modifier = Modifier()
 *         .gradientBackground(
 *             "linear-gradient(90deg, " +
 *             "#ff0000 0%, " +
 *             "#ffff00 25%, " +
 *             "#00ff00 50%, " +
 *             "#00ffff 75%, " +
 *             "#0000ff 100%)"
 *         )
 * )
 * ```
 *
 * ### Animated Gradients
 * ```kotlin
 * Box(
 *     modifier = Modifier()
 *         .linearGradient("45deg", "#ff6b6b", "#4ecdc4")
 *         .animTransition("background-position", 3000, "ease-in-out")
 * )
 * ```
 *
 * ### Layered Effects
 * ```kotlin
 * Card(
 *     modifier = Modifier()
 *         .glassMorphism()
 *         .linearGradient(
 *             direction = "135deg",
 *             "rgba(255, 255, 255, 0.1)",
 *             "rgba(255, 255, 255, 0.05)"
 *         )
 * )
 * ```
 *
 * ## Performance Considerations
 *
 * - **Hardware Acceleration**: Gradients leverage GPU rendering
 * - **Memory Efficiency**: CSS gradients use minimal memory
 * - **Render Optimization**: Smooth gradients without image assets
 * - **Browser Compatibility**: Fallback support for older browsers
 *
 * ## Design Guidelines
 *
 * ### Color Theory
 * - Use complementary colors for vibrant effects
 * - Consider accessibility and contrast ratios
 * - Test gradients in both light and dark themes
 *
 * ### Visual Hierarchy
 * - Subtle gradients for backgrounds
 * - Bold gradients for accent elements
 * - Consistent gradient direction across components
 *
 * ### Performance
 * - Limit gradient complexity for better performance
 * - Use CSS gradients instead of image gradients
 * - Test on various devices and screen sizes
 *
 * @see StylingModifiers for basic styling
 * @see AnimationModifiers for animated gradients
 * @see ColorSystem for color management
 * @since 1.0.0
 */
package codes.yousef.summon.modifier

/**
 * Applies a linear gradient background.
 */
fun Modifier.gradientBackground(gradient: String): Modifier =
    style("background", gradient)

/**
 * Applies a linear gradient background with specified direction.
 */
fun Modifier.linearGradient(
    direction: String = "to right",
    vararg colors: String
): Modifier {
    val colorString = colors.joinToString(", ")
    return style("background", "linear-gradient($direction, $colorString)")
}

/**
 * Applies a radial gradient background.
 */
fun Modifier.radialGradient(
    shape: String = "circle",
    vararg colors: String
): Modifier {
    val colorString = colors.joinToString(", ")
    return style("background", "radial-gradient($shape, $colorString)")
}

/**
 * Applies a conic gradient background.
 */
fun Modifier.conicGradient(
    from: String = "0deg",
    vararg colors: String
): Modifier {
    val colorString = colors.joinToString(", ")
    return style("background", "conic-gradient(from $from, $colorString)")
}

/**
 * Applies glass morphism effect with backdrop filter.
 */
fun Modifier.glassMorphism(
    backgroundColor: String = "rgba(255, 255, 255, 0.25)",
    backdropFilter: String = "blur(10px)",
    border: String = "1px solid rgba(255, 255, 255, 0.2)"
): Modifier =
    style("background", backgroundColor)
        .style("backdrop-filter", backdropFilter)
        .style("-webkit-backdrop-filter", backdropFilter) // Safari support
        .style("border", border)

/**
 * Common gradient presets for quick use.
 */
object GradientPresets {
    fun Modifier.modernPrimary(): Modifier =
        gradientBackground("linear-gradient(135deg, #667eea 0%, #764ba2 100%)")

    fun Modifier.modernSecondary(): Modifier =
        gradientBackground("linear-gradient(135deg, #f093fb 0%, #f5576c 100%)")

    fun Modifier.oceanBlue(): Modifier =
        gradientBackground("linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)")

    fun Modifier.warmSunset(): Modifier =
        gradientBackground("linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)")

    fun Modifier.softPink(): Modifier =
        gradientBackground("linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)")

    fun Modifier.darkModeGradient(): Modifier =
        gradientBackground("linear-gradient(135deg, #4a5568 0%, #2d3748 100%)")
}