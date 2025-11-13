package codes.yousef.summon.components.feedback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for the AlertVariant enum
 */
class AlertVariantTest {

    @Test
    fun testEnumValues() {
        // Verify that all expected enum values exist
        val values = AlertVariant.values()
        
        // Check that SUCCESS exists
        assertTrue(values.any { it == AlertVariant.SUCCESS }, "SUCCESS variant should exist")
        
        // Check that WARNING exists
        assertTrue(values.any { it == AlertVariant.WARNING }, "WARNING variant should exist")
        
        // Check that ERROR exists
        assertTrue(values.any { it == AlertVariant.ERROR }, "ERROR variant should exist")
        
        // Check that INFO exists
        assertTrue(values.any { it == AlertVariant.INFO }, "INFO variant should exist")
        
        // Check that NEUTRAL exists
        assertTrue(values.any { it == AlertVariant.NEUTRAL }, "NEUTRAL variant should exist")
    }
    
    @Test
    fun testEnumCount() {
        // Verify that there are exactly 5 enum values
        assertEquals(5, AlertVariant.values().size, "There should be exactly 5 alert variants")
    }
    
    @Test
    fun testEnumNames() {
        // Verify that each enum value has the expected name
        assertEquals("SUCCESS", AlertVariant.SUCCESS.name)
        assertEquals("WARNING", AlertVariant.WARNING.name)
        assertEquals("ERROR", AlertVariant.ERROR.name)
        assertEquals("INFO", AlertVariant.INFO.name)
        assertEquals("NEUTRAL", AlertVariant.NEUTRAL.name)
    }
    
    @Test
    fun testEnumOrdinals() {
        // Verify that each enum value has the expected ordinal
        assertEquals(0, AlertVariant.SUCCESS.ordinal)
        assertEquals(1, AlertVariant.WARNING.ordinal)
        assertEquals(2, AlertVariant.ERROR.ordinal)
        assertEquals(3, AlertVariant.INFO.ordinal)
        assertEquals(4, AlertVariant.NEUTRAL.ordinal)
    }
}