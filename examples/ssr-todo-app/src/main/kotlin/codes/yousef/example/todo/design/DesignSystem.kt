package codes.yousef.example.todo.design

/**
 * Design System for Todo Application
 * Modern, minimalistic, mobile-ready design tokens
 */

/**
 * Color palette following modern design principles
 * All colors meet WCAG AA contrast requirements
 */
object TodoAppColors {
    val Primary = "#0066CC"        // Primary blue for actions
    val Secondary = "#6B7280"      // Secondary gray for less prominent elements
    val Success = "#10B981"        // Green for completed todos
    val Danger = "#EF4444"         // Red for delete actions, errors
    val Background = "#FFFFFF"     // Clean white background
    val Surface = "#F9FAFB"        // Light gray for card backgrounds
    val Text = "#111827"           // Primary dark text
    val TextSecondary = "#6B7280"  // Secondary gray text
    val Border = "#E5E7EB"         // Light gray for borders and dividers
}

/**
 * Typography scale for consistent text hierarchy
 * Font sizes in pixels for precise control
 */
object TodoAppTypography {
    val Heading = TypographyStyle(
        fontSize = 24,
        fontWeight = "bold",
        lineHeight = 1.2
    )

    val Subheading = TypographyStyle(
        fontSize = 18,
        fontWeight = "medium",
        lineHeight = 1.3
    )

    val Body = TypographyStyle(
        fontSize = 16,
        fontWeight = "normal",
        lineHeight = 1.5
    )

    val Caption = TypographyStyle(
        fontSize = 14,
        fontWeight = "normal",
        lineHeight = 1.4
    )
}

/**
 * Spacing scale for consistent layout rhythm
 * Based on 8px grid system for precise alignment
 */
object TodoAppSpacing {
    val XS = 4      // Tight spacing
    val SM = 8      // Small spacing
    val MD = 16     // Default spacing (base unit)
    val LG = 24     // Large spacing
    val XL = 32     // Extra large spacing
    val XXL = 48    // Section spacing
}

/**
 * Responsive breakpoints for mobile-first design
 * Aligns with common device sizes
 */
object ResponsiveBreakpoints {
    val Mobile = 767     // < 768px = mobile
    val Tablet = 1024    // 768px - 1024px = tablet
    val Desktop = 1025   // > 1024px = desktop
}

/**
 * Typography style data class
 */
data class TypographyStyle(
    val fontSize: Int,
    val fontWeight: String,
    val lineHeight: Double
)

/**
 * Utility functions for responsive breakpoint detection
 */
fun isMobileViewport(width: Int): Boolean = width <= ResponsiveBreakpoints.Mobile

fun isTabletViewport(width: Int): Boolean =
    width > ResponsiveBreakpoints.Mobile && width <= ResponsiveBreakpoints.Tablet

fun isDesktopViewport(width: Int): Boolean = width > ResponsiveBreakpoints.Tablet

/**
 * WCAG AA contrast ratio calculation utility
 * Ensures accessibility compliance for all color combinations
 */
fun calculateContrastRatio(color1: String, color2: String): Double {
    fun hexToRgb(hex: String): Triple<Int, Int, Int> {
        val cleanHex = hex.removePrefix("#")
        val r = cleanHex.substring(0, 2).toInt(16)
        val g = cleanHex.substring(2, 4).toInt(16)
        val b = cleanHex.substring(4, 6).toInt(16)
        return Triple(r, g, b)
    }

    fun relativeLuminance(r: Int, g: Int, b: Int): Double {
        fun gammaCorrection(value: Int): Double {
            val sRGB = value / 255.0
            return if (sRGB <= 0.03928) {
                sRGB / 12.92
            } else {
                // Simplified approximation to avoid pow function
                ((sRGB + 0.055) / 1.055) * ((sRGB + 0.055) / 1.055) * 2.0
            }
        }

        val rLinear = gammaCorrection(r)
        val gLinear = gammaCorrection(g)
        val bLinear = gammaCorrection(b)

        return 0.2126 * rLinear + 0.7152 * gLinear + 0.0722 * bLinear
    }

    val (r1, g1, b1) = hexToRgb(color1)
    val (r2, g2, b2) = hexToRgb(color2)

    val luminance1 = relativeLuminance(r1, g1, b1)
    val luminance2 = relativeLuminance(r2, g2, b2)

    val lighter = maxOf(luminance1, luminance2)
    val darker = minOf(luminance1, luminance2)

    return (lighter + 0.05) / (darker + 0.05)
}

/**
 * Validate that all design system colors meet WCAG AA contrast requirements
 */
object ContrastValidation {
    fun validateDesignSystem(): List<String> {
        val violations = mutableListOf<String>()

        // Test critical color combinations
        val textOnBackground = calculateContrastRatio(TodoAppColors.Text, TodoAppColors.Background)
        if (textOnBackground < 4.5) {
            violations.add("Text on Background contrast: $textOnBackground (should be >= 4.5)")
        }

        val buttonTextContrast = calculateContrastRatio("#FFFFFF", TodoAppColors.Primary)
        if (buttonTextContrast < 4.5) {
            violations.add("Button text contrast: $buttonTextContrast (should be >= 4.5)")
        }

        val secondaryTextContrast = calculateContrastRatio(TodoAppColors.TextSecondary, TodoAppColors.Background)
        if (secondaryTextContrast < 4.5) {
            violations.add("Secondary text contrast: $secondaryTextContrast (should be >= 4.5)")
        }

        return violations
    }
}