package codes.yousef.summon.accessibility

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Utility for checking color contrast according to WCAG 2.1 standards.
 * Helps ensure text is readable for users with visual impairments.
 */
object ContrastChecking {
    /**
     * WCAG conformance levels
     */
    enum class ConformanceLevel {
        /** Minimum level (AA for large text, fails for normal text) */
        A,

        /** Standard level (passes AA) */
        AA,

        /** Enhanced level (passes AAA) */
        AAA
    }

    /**
     * Text size categories according to WCAG
     */
    enum class TextSize {
        /** Text smaller than 18pt (or 14pt bold) */
        NORMAL,

        /** Text 18pt or larger (or 14pt bold or larger) */
        LARGE
    }

    /**
     * Result of a contrast check
     *
     * @property contrastRatio The calculated contrast ratio
     * @property passesA Whether the contrast passes Level A requirements (poor contrast)
     * @property passesAA Whether the contrast passes Level AA requirements (minimum for most text)
     * @property passesAAA Whether the contrast passes Level AAA requirements (enhanced contrast)
     * @property minimumRequiredLevel The minimum required conformance level for the text size
     */
    data class ContrastResult(
        val contrastRatio: Double,
        val passesA: Boolean,
        val passesAA: Boolean,
        val passesAAA: Boolean,
        val minimumRequiredLevel: ConformanceLevel
    )

    /**
     * Calculates the contrast ratio between two colors according to WCAG 2.1.
     *
     * @param foreground The foreground color in hex format (#RRGGBB or #RGB)
     * @param background The background color in hex format (#RRGGBB or #RGB)
     * @param textSize The size of the text being displayed
     * @return The ContrastResult with the contrast ratio and pass/fail status
     */
    fun checkContrast(
        foreground: String,
        background: String,
        textSize: TextSize = TextSize.NORMAL
    ): ContrastResult {
        // Convert hex colors to RGB
        val fgRgb = hexToRgb(foreground)
        val bgRgb = hexToRgb(background)

        // Convert RGB to luminance
        val fgLuminance = rgbToLuminance(fgRgb)
        val bgLuminance = rgbToLuminance(bgRgb)

        // Calculate contrast ratio
        val contrastRatio = calculateContrastRatio(fgLuminance, bgLuminance)

        // Determine minimum required level based on text size
        val minimumRequiredLevel = if (textSize == TextSize.NORMAL) {
            ConformanceLevel.AA
        } else {
            ConformanceLevel.A
        }

        // Check against WCAG thresholds
        val passesA = contrastRatio >= 3.0
        val passesAA = when (textSize) {
            TextSize.NORMAL -> contrastRatio >= 4.5
            TextSize.LARGE -> contrastRatio >= 3.0
        }
        val passesAAA = when (textSize) {
            TextSize.NORMAL -> contrastRatio >= 7.0
            TextSize.LARGE -> contrastRatio >= 4.5
        }

        return ContrastResult(
            contrastRatio = contrastRatio,
            passesA = passesA,
            passesAA = passesAA,
            passesAAA = passesAAA,
            minimumRequiredLevel = minimumRequiredLevel
        )
    }

    /**
     * Suggests alternative colors with better contrast if the current contrast is insufficient.
     *
     * @param foreground The foreground color in hex format
     * @param background The background color in hex format
     * @param targetLevel The desired conformance level
     * @param adjustForeground Whether to adjust the foreground color (true) or background color (false)
     * @param textSize The size of the text
     * @return A list of suggested colors that meet the target contrast level
     */
    fun suggestColors(
        foreground: String,
        background: String,
        targetLevel: ConformanceLevel = ConformanceLevel.AA,
        adjustForeground: Boolean = true,
        textSize: TextSize = TextSize.NORMAL
    ): List<String> {
        // Check current contrast
        val currentContrast = checkContrast(foreground, background, textSize)

        // If already passing the target level, return the original color
        if ((targetLevel == ConformanceLevel.A && currentContrast.passesA) ||
            (targetLevel == ConformanceLevel.AA && currentContrast.passesAA) ||
            (targetLevel == ConformanceLevel.AAA && currentContrast.passesAAA)
        ) {
            return listOf(if (adjustForeground) foreground else background)
        }

        // Determine target contrast ratio
        val targetRatio = when (targetLevel) {
            ConformanceLevel.A -> 3.0
            ConformanceLevel.AA -> if (textSize == TextSize.NORMAL) 4.5 else 3.0
            ConformanceLevel.AAA -> if (textSize == TextSize.NORMAL) 7.0 else 4.5
        }

        // Generate alternative colors
        val suggestions = mutableListOf<String>()

        // Simple implementation: adjust brightness to meet contrast requirements
        if (adjustForeground) {
            val bgLuminance = rgbToLuminance(hexToRgb(background))

            // Try darker version
            val darkerColor = adjustBrightness(foreground, -0.3)
            if (calculateContrastRatio(rgbToLuminance(hexToRgb(darkerColor)), bgLuminance) >= targetRatio) {
                suggestions.add(darkerColor)
            }

            // Try lighter version
            val lighterColor = adjustBrightness(foreground, 0.3)
            if (calculateContrastRatio(rgbToLuminance(hexToRgb(lighterColor)), bgLuminance) >= targetRatio) {
                suggestions.add(lighterColor)
            }
        } else {
            val fgLuminance = rgbToLuminance(hexToRgb(foreground))

            // Try darker version
            val darkerColor = adjustBrightness(background, -0.3)
            if (calculateContrastRatio(fgLuminance, rgbToLuminance(hexToRgb(darkerColor))) >= targetRatio) {
                suggestions.add(darkerColor)
            }

            // Try lighter version
            val lighterColor = adjustBrightness(background, 0.3)
            if (calculateContrastRatio(fgLuminance, rgbToLuminance(hexToRgb(lighterColor))) >= targetRatio) {
                suggestions.add(lighterColor)
            }
        }

        return suggestions
    }

    /**
     * Converts a hex color string to RGB triple.
     */
    private fun hexToRgb(hex: String): Triple<Int, Int, Int> {
        val cleanHex = hex.replace("#", "")

        val r: Int
        val g: Int
        val b: Int

        if (cleanHex.length == 3) {
            // Handle shorthand hex format
            r = (cleanHex[0].toString() + cleanHex[0].toString()).toInt(16)
            g = (cleanHex[1].toString() + cleanHex[1].toString()).toInt(16)
            b = (cleanHex[2].toString() + cleanHex[2].toString()).toInt(16)
        } else {
            // Standard hex format
            r = cleanHex.substring(0, 2).toInt(16)
            g = cleanHex.substring(2, 4).toInt(16)
            b = cleanHex.substring(4, 6).toInt(16)
        }

        return Triple(r, g, b)
    }

    /**
     * Converts an RGB triple to a relative luminance value according to WCAG.
     */
    private fun rgbToLuminance(rgb: Triple<Int, Int, Int>): Double {
        val (r, g, b) = rgb

        // Convert RGB to sRGB
        val rsRGB = r / 255.0
        val gsRGB = g / 255.0
        val bsRGB = b / 255.0

        // Convert sRGB to luminance components
        val rLum = if (rsRGB <= 0.03928) rsRGB / 12.92 else ((rsRGB + 0.055) / 1.055).pow(2.4)
        val gLum = if (gsRGB <= 0.03928) gsRGB / 12.92 else ((gsRGB + 0.055) / 1.055).pow(2.4)
        val bLum = if (bsRGB <= 0.03928) bsRGB / 12.92 else ((bsRGB + 0.055) / 1.055).pow(2.4)

        // Calculate luminance using WCAG formula
        return 0.2126 * rLum + 0.7152 * gLum + 0.0722 * bLum
    }

    /**
     * Calculates the contrast ratio between two luminance values according to WCAG.
     */
    private fun calculateContrastRatio(luminance1: Double, luminance2: Double): Double {
        val lighter = max(luminance1, luminance2)
        val darker = min(luminance1, luminance2)

        return (lighter + 0.05) / (darker + 0.05)
    }

    /**
     * Adjusts the brightness of a color by a given amount (-1.0 to 1.0).
     */
    private fun adjustBrightness(hexColor: String, adjustment: Double): String {
        val rgb = hexToRgb(hexColor)
        val (r, g, b) = rgb

        // Adjust brightness
        val adjustedR = (r + (adjustment * 255)).toInt().coerceIn(0, 255)
        val adjustedG = (g + (adjustment * 255)).toInt().coerceIn(0, 255)
        val adjustedB = (b + (adjustment * 255)).toInt().coerceIn(0, 255)

        // Convert back to hex
        return "#${adjustedR.toString(16).padStart(2, '0')}${
            adjustedG.toString(16).padStart(2, '0')
        }${adjustedB.toString(16).padStart(2, '0')}"
    }
} 
