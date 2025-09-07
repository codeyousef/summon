package code.yousef.summon.modifier

/**
 * Extension functions for applying gradient backgrounds to modifiers.
 */

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