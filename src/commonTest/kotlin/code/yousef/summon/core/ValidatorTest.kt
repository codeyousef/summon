package code.yousef.summon.core

import core.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ValidatorTest {
    
    @Test
    fun testRequiredValidator() {
        val validator = RequiredValidator()
        
        // Valid cases
        assertTrue(validator.validate("test"), "Non-empty string should be valid")
        assertTrue(validator.validate("a"), "Single character should be valid")
        assertTrue(validator.validate(" a "), "String with spaces should be valid if it contains non-space characters")
        
        // Invalid cases
        assertFalse(validator.validate(""), "Empty string should be invalid")
        assertFalse(validator.validate("   "), "String with only spaces should be invalid")
    }
    
    @Test
    fun testEmailValidator() {
        val validator = EmailValidator()
        
        // Valid cases
        assertTrue(validator.validate("test@example.com"), "Standard email should be valid")
        assertTrue(validator.validate("test.name@example.co.uk"), "Email with subdomain should be valid")
        assertTrue(validator.validate("test+label@example.com"), "Email with + should be valid")
        assertTrue(validator.validate(""), "Empty string should be valid (not required)")
        
        // Invalid cases
        assertFalse(validator.validate("test@"), "Email without domain should be invalid")
        assertFalse(validator.validate("@example.com"), "Email without local part should be invalid")
        assertFalse(validator.validate("test@example"), "Email without TLD should be invalid")
        assertFalse(validator.validate("test.example.com"), "String without @ should be invalid")
    }
    
    @Test
    fun testMinLengthValidator() {
        val validator = MinLengthValidator(5)
        
        // Valid cases
        assertTrue(validator.validate("12345"), "String of exact minimum length should be valid")
        assertTrue(validator.validate("123456"), "String longer than minimum should be valid")
        
        // Invalid cases
        assertFalse(validator.validate("1234"), "String shorter than minimum should be invalid")
        assertFalse(validator.validate(""), "Empty string should be invalid")
    }
    
    @Test
    fun testMaxLengthValidator() {
        val validator = MaxLengthValidator(5)
        
        // Valid cases
        assertTrue(validator.validate("12345"), "String of exact maximum length should be valid")
        assertTrue(validator.validate("1234"), "String shorter than maximum should be valid")
        assertTrue(validator.validate(""), "Empty string should be valid")
        
        // Invalid cases
        assertFalse(validator.validate("123456"), "String longer than maximum should be invalid")
    }
    
    @Test
    fun testPatternValidator() {
        val validator = PatternValidator(Regex("^[0-9]{3}-[0-9]{3}-[0-9]{4}$"))
        
        // Valid cases
        assertTrue(validator.validate("123-456-7890"), "String matching pattern should be valid")
        assertTrue(validator.validate(""), "Empty string should be valid (not required)")
        
        // Invalid cases
        assertFalse(validator.validate("123-456-789"), "String not matching pattern should be invalid")
        assertFalse(validator.validate("abc-def-ghij"), "String with wrong characters should be invalid")
    }
    
    @Test
    fun testCustomValidator() {
        val validator = CustomValidator(
            validateFn = { it.toIntOrNull() != null && it.toInt() % 2 == 0 },
            errorMessage = "Must be an even number"
        )
        
        // Valid cases
        assertTrue(validator.validate("2"), "Even number should be valid")
        assertTrue(validator.validate("0"), "Zero should be valid")
        assertTrue(validator.validate("-4"), "Negative even number should be valid")
        
        // Invalid cases
        assertFalse(validator.validate("1"), "Odd number should be invalid")
        assertFalse(validator.validate("abc"), "Non-number should be invalid")
        assertFalse(validator.validate(""), "Empty string should be invalid")
    }
    
    @Test
    fun testValidateBoolean() {
        val trueValidator = CustomValidator(
            validateFn = { it == "true" },
            errorMessage = "Must be true"
        )
        
        // Valid cases
        assertTrue(trueValidator.validateBoolean(true), "True should be valid")
        
        // Invalid cases
        assertFalse(trueValidator.validateBoolean(false), "False should be invalid")
    }
    
    @Test
    fun testCustomErrorMessage() {
        val customMessage = "Custom error message"
        val validator = RequiredValidator(customMessage)
        
        // Check error message
        kotlin.test.assertEquals(customMessage, validator.errorMessage, "Error message should match custom message")
    }
}