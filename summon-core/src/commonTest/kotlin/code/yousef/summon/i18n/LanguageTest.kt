package codes.yousef.summon.i18n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class LanguageTest {
    
    @Test
    fun testLanguageCreation() {
        // Create a language with LTR direction
        val english = Language("en", "English", LayoutDirection.LTR)
        
        // Verify properties
        assertEquals("en", english.code)
        assertEquals("English", english.name)
        assertEquals(LayoutDirection.LTR, english.direction)
        
        // Create a language with RTL direction
        val arabic = Language("ar", "Arabic", LayoutDirection.RTL)
        
        // Verify properties
        assertEquals("ar", arabic.code)
        assertEquals("Arabic", arabic.name)
        assertEquals(LayoutDirection.RTL, arabic.direction)
    }
    
    @Test
    fun testLanguageEquality() {
        // Create two identical languages
        val english1 = Language("en", "English", LayoutDirection.LTR)
        val english2 = Language("en", "English", LayoutDirection.LTR)
        
        // Verify equality
        assertEquals(english1, english2)
        
        // Create a different language
        val french = Language("fr", "French", LayoutDirection.LTR)
        
        // Verify inequality
        assertNotEquals(english1, french)
    }
    
    @Test
    fun testLayoutDirectionValues() {
        // Verify that LayoutDirection has exactly two values
        assertEquals(2, LayoutDirection.values().size)
        
        // Verify the values
        assertTrue(LayoutDirection.values().contains(LayoutDirection.LTR))
        assertTrue(LayoutDirection.values().contains(LayoutDirection.RTL))
    }
}