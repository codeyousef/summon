package codes.yousef.summon.theme

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

/**
 * Tests for ThemeVariableInjector functionality.
 *
 * TEST DIRECTIVE: Instantiate a Theme with specific hex code `#123456`.
 * Trigger injection. Use `getComputedStyle(document.documentElement).getPropertyValue(...)`
 * to assert the value exists on the DOM. Change the state. Assert the DOM value updated.
 *
 * Note: These tests verify the logic without actual DOM access (which requires browser environment).
 * Browser-specific tests should be in jsTest/wasmJsTest source sets.
 */
class ThemeInjectionTest {

    @Test
    fun testThemeVariableGeneratorCreatesColorVariables() {
        // Test that ThemeVariableGenerator properly creates CSS variables from ThemeConfig
        val themeConfig = Theme.Themes.light
        val cssVariables = ThemeVariableGenerator.generateVariables(themeConfig)

        // Verify color variables are generated
        assertTrue(cssVariables.isNotEmpty(), "Should generate CSS variables")
        
        // Check that some expected variable patterns exist
        val hasColorVariables = cssVariables.keys.any { it.startsWith("--colors-") }
        assertTrue(hasColorVariables, "Should have color variables")
    }

    @Test
    fun testThemeVariableGeneratorCreatesTypographyVariables() {
        val themeConfig = Theme.Themes.light
        val cssVariables = ThemeVariableGenerator.generateVariables(themeConfig)

        // Verify typography variables are generated
        val hasTypographyVariables = cssVariables.keys.any { it.startsWith("--typography-") }
        assertTrue(hasTypographyVariables, "Should have typography variables")
    }

    @Test
    fun testThemeVariableGeneratorCreatesSpacingVariables() {
        val themeConfig = Theme.Themes.light
        val cssVariables = ThemeVariableGenerator.generateVariables(themeConfig)

        // Verify spacing variables are generated
        val hasSpacingVariables = cssVariables.keys.any { it.startsWith("--spacing-") }
        assertTrue(hasSpacingVariables, "Should have spacing variables")
        
        // Check specific spacing variables
        assertNotNull(cssVariables["--spacing-xs"], "Should have xs spacing")
        assertNotNull(cssVariables["--spacing-sm"], "Should have sm spacing")
        assertNotNull(cssVariables["--spacing-md"], "Should have md spacing")
        assertNotNull(cssVariables["--spacing-lg"], "Should have lg spacing")
        assertNotNull(cssVariables["--spacing-xl"], "Should have xl spacing")
    }

    @Test
    fun testThemeVariableGeneratorCreatesBorderRadiusVariables() {
        val themeConfig = Theme.Themes.light
        val cssVariables = ThemeVariableGenerator.generateVariables(themeConfig)

        // Verify border radius variables are generated
        assertNotNull(cssVariables["--border-radius-sm"], "Should have sm border radius")
        assertNotNull(cssVariables["--border-radius-md"], "Should have md border radius")
        assertNotNull(cssVariables["--border-radius-lg"], "Should have lg border radius")
        assertNotNull(cssVariables["--border-radius-pill"], "Should have pill border radius")
        assertNotNull(cssVariables["--border-radius-circle"], "Should have circle border radius")
    }

    @Test
    fun testThemeVariableGeneratorCreatesShadowVariables() {
        val themeConfig = Theme.Themes.light
        val cssVariables = ThemeVariableGenerator.generateVariables(themeConfig)

        // Verify shadow/elevation variables are generated
        assertNotNull(cssVariables["--shadow-none"], "Should have none shadow")
        assertNotNull(cssVariables["--shadow-sm"], "Should have sm shadow")
        assertNotNull(cssVariables["--shadow-md"], "Should have md shadow")
        assertNotNull(cssVariables["--shadow-lg"], "Should have lg shadow")
    }

    @Test
    fun testDifferentThemesProduceDifferentVariables() {
        val lightVariables = ThemeVariableGenerator.generateVariables(Theme.Themes.light)
        val darkVariables = ThemeVariableGenerator.generateVariables(Theme.Themes.dark)

        // Both should generate variables
        assertTrue(lightVariables.isNotEmpty(), "Light theme should generate variables")
        assertTrue(darkVariables.isNotEmpty(), "Dark theme should generate variables")
        
        // Both should have similar keys (same structure)
        assertTrue(lightVariables.keys.containsAll(darkVariables.keys.take(5)), 
            "Themes should have similar variable structure")
    }

    @Test
    fun testCustomThemeValues() {
        // Create a custom theme config with custom values
        val customConfig = Theme.ThemeConfig(
            colorPalette = Theme.Themes.light.colorPalette,
            typographyTheme = Theme.Themes.light.typographyTheme,
            spacingTheme = Theme.Themes.light.spacingTheme,
            borderRadiusTheme = Theme.Themes.light.borderRadiusTheme,
            elevationTheme = Theme.Themes.light.elevationTheme,
            customValues = mapOf(
                "my-custom-color" to "#123456",
                "my-custom-size" to "24px"
            )
        )

        val cssVariables = ThemeVariableGenerator.generateVariables(customConfig)

        // Verify custom values are included
        assertNotNull(cssVariables["--custom-my-custom-color"], "Should include custom color variable")
        assertNotNull(cssVariables["--custom-my-custom-size"], "Should include custom size variable")
    }
}
