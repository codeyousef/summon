package codes.yousef.summon.validation

import kotlin.test.*

class RequiredValidatorTest {

    private lateinit var validator: RequiredValidator
    private val errorMessage = "This field is required"

    @BeforeTest
    fun setup() {
        validator = RequiredValidator(errorMessage)
    }

    @Test
    fun testValidateWithNonEmptyValue() {
        // Test with a non-empty value
        val result = validator.validate("Test value")

        // Verify the result
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun testValidateWithEmptyValue() {
        // Test with an empty value
        val result = validator.validate("")

        // Verify the result
        assertFalse(result.isValid)
        assertEquals(errorMessage, result.errorMessage)
    }

    @Test
    fun testValidateWithWhitespaceOnly() {
        // Test with a value that contains only whitespace
        val result = validator.validate("   ")

        // Verify the result
        assertFalse(result.isValid)
        assertEquals(errorMessage, result.errorMessage)
    }

    @Test
    fun testValidateWithSingleCharacter() {
        // Test with a single character
        val result = validator.validate("a")

        // Verify the result
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun testValidateWithSpecialCharacters() {
        // Test with special characters
        val result = validator.validate("!@#$%^&*()")

        // Verify the result
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun testValidateWithNumbers() {
        // Test with numbers
        val result = validator.validate("12345")

        // Verify the result
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun testValidateWithMixedContent() {
        // Test with a mix of characters, numbers, and special characters
        val result = validator.validate("Test123!@#")

        // Verify the result
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun testDifferentErrorMessages() {
        // Create validators with different error messages
        val validator1 = RequiredValidator("Field cannot be empty")
        val validator2 = RequiredValidator("Please fill in this field")

        // Test with an empty value
        val result1 = validator1.validate("")
        val result2 = validator2.validate("")

        // Verify the results have different error messages
        assertFalse(result1.isValid)
        assertFalse(result2.isValid)
        assertEquals("Field cannot be empty", result1.errorMessage)
        assertEquals("Please fill in this field", result2.errorMessage)
    }
}