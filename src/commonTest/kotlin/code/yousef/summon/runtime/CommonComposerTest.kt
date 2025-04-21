package code.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import code.yousef.summon.annotation.Composable

class CommonComposerTest {

    @Test
    fun testCommonComposerBasicProperties() {
        val composer = CommonComposer()
        
        // Test inserting property
        assertTrue(composer.inserting, "CommonComposer should have inserting=true")
    }
    
    @Test
    fun testSlotManagement() {
        val composer = CommonComposer()
        
        // Initially, slots should be empty
        assertEquals(null, composer.getSlot(), "Initial slot should be null")
        
        // Set a value in the current slot
        val testValue = "Test Value"
        composer.setSlot(testValue)
        
        // Verify the value was set
        assertEquals(testValue, composer.getSlot(), "Slot should contain the set value")
        
        // Move to the next slot
        composer.nextSlot()
        
        // New slot should be empty
        assertEquals(null, composer.getSlot(), "New slot should be null")
        
        // Set a different value in the new slot
        val testValue2 = "Test Value 2"
        composer.setSlot(testValue2)
        
        // Verify the value was set
        assertEquals(testValue2, composer.getSlot(), "New slot should contain the new value")
        
        // Move back to the first slot (by creating a new composer)
        val newComposer = CommonComposer()
        newComposer.setSlot(testValue)
        assertEquals(testValue, newComposer.getSlot(), "First slot should contain the original value")
    }
    
    @Test
    fun testCompose() {
        val composer = CommonComposer()
        
        // Test compose with a simple composable function
        val result = composer.compose {
            "Composed Value"
        }
        
        assertEquals("Composed Value", result, "compose should return the result of the composable")
    }
    
    @Test
    fun testComposeWithAnnotation() {
        val composer = CommonComposer()
        
        // Define a composable function
        @Composable
        fun testComposable(): String {
            return "Composed with annotation"
        }
        
        // Test compose with the annotated function
        val result = composer.compose {
            testComposable()
        }
        
        assertEquals("Composed with annotation", result, "compose should work with @Composable functions")
    }
    
    @Test
    fun testDispose() {
        val composer = CommonComposer()
        
        // Set some values in slots
        composer.setSlot("Value 1")
        composer.nextSlot()
        composer.setSlot("Value 2")
        
        // Dispose the composer
        composer.dispose()
        
        // Slots should be cleared
        assertEquals(null, composer.getSlot(), "Slots should be cleared after dispose")
    }
    
    @Test
    fun testComposeManagerContext() {
        // Test getting the current composer
        val defaultComposer = ComposeManagerContext.current
        assertNotNull(defaultComposer, "Default composer should not be null")
        
        // Test withComposer
        val customComposer = CommonComposer()
        val result = ComposeManagerContext.withComposer(customComposer) {
            assertEquals(customComposer, ComposeManagerContext.current, "Current composer should be the custom one")
            "Result"
        }
        
        assertEquals("Result", result, "withComposer should return the result of the block")
        
        // After the block, the current composer should be reset
        assertTrue(ComposeManagerContext.current !== customComposer, 
            "Current composer should be reset after withComposer")
    }
    
    @Test
    fun testNestedWithComposer() {
        val outerComposer = CommonComposer()
        val innerComposer = CommonComposer()
        
        ComposeManagerContext.withComposer(outerComposer) {
            assertEquals(outerComposer, ComposeManagerContext.current, "Current composer should be the outer one")
            
            ComposeManagerContext.withComposer(innerComposer) {
                assertEquals(innerComposer, ComposeManagerContext.current, "Current composer should be the inner one")
            }
            
            assertEquals(outerComposer, ComposeManagerContext.current, 
                "Current composer should be restored to the outer one")
        }
    }
}