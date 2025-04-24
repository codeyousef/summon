package code.yousef.summon.accessibility

import kotlin.test.*

class ContrastCheckingTest {

    @Test
    fun testCheckContrastWithNormalText() {
        // Test with black text on white background (high contrast)
        val blackOnWhite = ContrastChecking.checkContrast(
            foreground = "#000000",
            background = "#FFFFFF",
            textSize = ContrastChecking.TextSize.NORMAL
        )

        // The contrast ratio should be 21:1
        assertEquals(21.0, blackOnWhite.contrastRatio, 0.1, "Black on white should have 21:1 contrast ratio")
        assertTrue(blackOnWhite.passesA, "Black on white should pass Level A")
        assertTrue(blackOnWhite.passesAA, "Black on white should pass Level AA")
        assertTrue(blackOnWhite.passesAAA, "Black on white should pass Level AAA")
        assertEquals(ContrastChecking.ConformanceLevel.AA, blackOnWhite.minimumRequiredLevel, 
            "Normal text should require AA conformance")

        // Test with low contrast colors
        val lowContrast = ContrastChecking.checkContrast(
            foreground = "#777777",
            background = "#999999",
            textSize = ContrastChecking.TextSize.NORMAL
        )

        // The contrast ratio should be low
        assertTrue(lowContrast.contrastRatio < 3.0, "Similar colors should have low contrast")
        assertFalse(lowContrast.passesA, "Low contrast should fail Level A")
        assertFalse(lowContrast.passesAA, "Low contrast should fail Level AA")
        assertFalse(lowContrast.passesAAA, "Low contrast should fail Level AAA")
    }

    @Test
    fun testCheckContrastWithLargeText() {
        // Test with medium contrast for large text
        val mediumContrast = ContrastChecking.checkContrast(
            foreground = "#555555",
            background = "#EEEEEE",
            textSize = ContrastChecking.TextSize.LARGE
        )

        // Large text has lower requirements
        assertTrue(mediumContrast.contrastRatio >= 3.0, "Medium contrast should be at least 3:1")
        assertTrue(mediumContrast.passesA, "Medium contrast should pass Level A for large text")
        assertTrue(mediumContrast.passesAA, "Medium contrast should pass Level AA for large text")
        // May or may not pass AAA depending on exact contrast

        assertEquals(ContrastChecking.ConformanceLevel.A, mediumContrast.minimumRequiredLevel, 
            "Large text should require A conformance")
    }

    @Test
    fun testHexToRgbConversion() {
        // Test with 6-digit hex
        val blackOnWhite = ContrastChecking.checkContrast("#000000", "#FFFFFF")
        assertEquals(21.0, blackOnWhite.contrastRatio, 0.1, "6-digit hex should be parsed correctly")

        // Test with 3-digit hex
        val shortHex = ContrastChecking.checkContrast("#000", "#FFF")
        assertEquals(21.0, shortHex.contrastRatio, 0.1, "3-digit hex should be parsed correctly")

        // Test with lowercase hex
        val lowercase = ContrastChecking.checkContrast("#ffffff", "#000000")
        assertEquals(21.0, lowercase.contrastRatio, 0.1, "Lowercase hex should be parsed correctly")
    }

    @Test
    fun testSuggestColors() {
        // Test suggesting foreground colors for low contrast
        val suggestions = ContrastChecking.suggestColors(
            foreground = "#777777",
            background = "#999999",
            targetLevel = ContrastChecking.ConformanceLevel.AA,
            adjustForeground = true,
            textSize = ContrastChecking.TextSize.NORMAL
        )

        // Should suggest at least one color
        assertTrue(suggestions.isNotEmpty(), "Should suggest at least one color")

        // Verify that suggested colors have better contrast
        suggestions.forEach { suggestedColor ->
            val newContrast = ContrastChecking.checkContrast(
                foreground = suggestedColor,
                background = "#999999",
                textSize = ContrastChecking.TextSize.NORMAL
            )
            assertTrue(newContrast.passesAA, "Suggested color should pass AA: $suggestedColor")
        }

        // Test suggesting background colors with colors that have more room for adjustment
        val bgSuggestions = ContrastChecking.suggestColors(
            foreground = "#555555",  // Darker foreground
            background = "#AAAAAA",  // Lighter background
            targetLevel = ContrastChecking.ConformanceLevel.AA,
            adjustForeground = false,
            textSize = ContrastChecking.TextSize.NORMAL
        )

        // Should suggest at least one color
        assertTrue(bgSuggestions.isNotEmpty(), "Should suggest at least one background color")

        // Verify that suggested colors have better contrast
        bgSuggestions.forEach { suggestedColor ->
            val newContrast = ContrastChecking.checkContrast(
                foreground = "#555555",
                background = suggestedColor,
                textSize = ContrastChecking.TextSize.NORMAL
            )
            assertTrue(newContrast.passesAA, "Suggested background color should pass AA: $suggestedColor")
        }
    }

    @Test
    fun testAlreadyPassingColors() {
        // Test with colors that already pass the target level
        val suggestions = ContrastChecking.suggestColors(
            foreground = "#000000",
            background = "#FFFFFF",
            targetLevel = ContrastChecking.ConformanceLevel.AAA
        )

        // Should return the original color
        assertEquals(1, suggestions.size, "Should return only the original color")
        assertEquals("#000000", suggestions[0], "Should return the original color")
    }
}
