package codes.yousef.summon.theme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ThemeManagerTest {

    @Test
    fun testGetTypography() {
        // Test that we can get the current typography
        val typography = ThemeManager.getTypography()
        
        // Verify it's not null
        assertNotNull(typography)
    }
    
    @Test
    fun testSetAndGetTypography() {
        // Save original typography to restore later
        val originalTypography = ThemeManager.getTypography()
        
        try {
            // Create a custom typography
            val customTypography = Typography()
            
            // Set the typography
            ThemeManager.setTypography(customTypography)
            
            // Verify it was set correctly
            val retrievedTypography = ThemeManager.getTypography()
            assertEquals(customTypography, retrievedTypography)
        } finally {
            // Restore original typography
            ThemeManager.setTypography(originalTypography)
        }
    }
    
    @Test
    fun testDefaultThemeConfiguration() {
        // Test the default theme configuration
        val defaultConfig = DefaultThemeConfiguration()
        
        // Verify it has a typography
        assertNotNull(defaultConfig.typography)
    }
}