package codes.yousef.summon.validation

import kotlin.test.*

class ValidationResultTest {
    
    @Test
    fun testValidResult() {
        // Create a valid result
        val result = ValidationResult(isValid = true)
        
        // Verify the properties
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }
    
    @Test
    fun testInvalidResult() {
        // Create an invalid result with an error message
        val errorMessage = "This field is required"
        val result = ValidationResult(isValid = false, errorMessage = errorMessage)
        
        // Verify the properties
        assertFalse(result.isValid)
        assertEquals(errorMessage, result.errorMessage)
    }
    
    @Test
    fun testInvalidResultWithNullErrorMessage() {
        // Create an invalid result without an error message
        val result = ValidationResult(isValid = false, errorMessage = null)
        
        // Verify the properties
        assertFalse(result.isValid)
        assertNull(result.errorMessage)
    }
    
    @Test
    fun testEquality() {
        // Create two identical results
        val result1 = ValidationResult(isValid = true)
        val result2 = ValidationResult(isValid = true)
        
        // Verify they are equal
        assertEquals(result1, result2)
        assertEquals(result1.hashCode(), result2.hashCode())
    }
    
    @Test
    fun testInequality() {
        // Create two different results
        val result1 = ValidationResult(isValid = true)
        val result2 = ValidationResult(isValid = false, errorMessage = "Error")
        
        // Verify they are not equal
        assertNotEquals(result1, result2)
        assertNotEquals(result1.hashCode(), result2.hashCode())
    }
    
    @Test
    fun testCopy() {
        // Create a result
        val original = ValidationResult(isValid = false, errorMessage = "Original error")
        
        // Create a copy with a different error message
        val copy = original.copy(errorMessage = "New error")
        
        // Verify the copy has the same isValid but a different error message
        assertEquals(original.isValid, copy.isValid)
        assertNotEquals(original.errorMessage, copy.errorMessage)
        assertEquals("New error", copy.errorMessage)
    }
    
    @Test
    fun testCopyWithDifferentValidity() {
        // Create a result
        val original = ValidationResult(isValid = false, errorMessage = "Error")
        
        // Create a copy with different validity
        val copy = original.copy(isValid = true)
        
        // Verify the copy has different isValid but the same error message
        assertNotEquals(original.isValid, copy.isValid)
        assertTrue(copy.isValid)
        assertEquals(original.errorMessage, copy.errorMessage)
    }
}