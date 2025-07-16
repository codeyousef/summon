package code.yousef.summon.validation

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals

class AllValidatorsTest {
    
    @Test
    fun testRequiredValidator() {
        val validator = RequiredValidator()
        
        // Valid cases
        assertTrue(validator.validate("test").isValid, "Non-empty string should be valid")
        assertTrue(validator.validate("a").isValid, "Single character should be valid")
        assertTrue(validator.validate(" a ").isValid, "String with spaces should be valid if it contains non-space characters")
        
        // Invalid cases
        assertFalse(validator.validate("").isValid, "Empty string should be invalid")
        assertFalse(validator.validate("   ").isValid, "String with only spaces should be invalid")
    }
    
    @Test
    fun testEmailValidator() {
        val validator = EmailValidator()
        
        // Valid cases
        assertTrue(validator.validate("test@example.com").isValid, "Standard email should be valid")
        assertTrue(validator.validate("test.name@example.co.uk").isValid, "Email with subdomain should be valid")
        assertTrue(validator.validate("test+label@example.com").isValid, "Email with + should be valid")
        assertTrue(validator.validate("").isValid, "Empty string should be valid (not required)")
        
        // Invalid cases
        assertFalse(validator.validate("test@").isValid, "Email without domain should be invalid")
        assertFalse(validator.validate("@example.com").isValid, "Email without local part should be invalid")
        assertFalse(validator.validate("test@example").isValid, "Email without TLD should be invalid")
        assertFalse(validator.validate("test.example.com").isValid, "String without @ should be invalid")
    }
    
    @Test
    fun testMinLengthValidator() {
        val validator = MinLengthValidator(5)
        
        // Valid cases
        assertTrue(validator.validate("12345").isValid, "String of exact minimum length should be valid")
        assertTrue(validator.validate("123456").isValid, "String longer than minimum should be valid")
        
        // Invalid cases
        assertFalse(validator.validate("1234").isValid, "String shorter than minimum should be invalid")
        assertFalse(validator.validate("").isValid, "Empty string should be invalid")
    }
    
    @Test
    fun testMaxLengthValidator() {
        val validator = MaxLengthValidator(5)
        
        // Valid cases
        assertTrue(validator.validate("12345").isValid, "String of exact maximum length should be valid")
        assertTrue(validator.validate("1234").isValid, "String shorter than maximum should be valid")
        assertTrue(validator.validate("").isValid, "Empty string should be valid")
        
        // Invalid cases
        assertFalse(validator.validate("123456").isValid, "String longer than maximum should be invalid")
    }
    
    @Test
    fun testPatternValidator() {
        val validator = PatternValidator(Regex("^[0-9]{3}-[0-9]{3}-[0-9]{4}$"))
        
        // Valid cases
        assertTrue(validator.validate("123-456-7890").isValid, "String matching pattern should be valid")
        assertTrue(validator.validate("").isValid, "Empty string should be valid (not required)")
        
        // Invalid cases
        assertFalse(validator.validate("123-456-789").isValid, "String not matching pattern should be invalid")
        assertFalse(validator.validate("abc-def-ghij").isValid, "String with wrong characters should be invalid")
    }
    
    @Test
    fun testCustomValidator() {
        val validator = CustomValidator(
            validateFn = { it.toIntOrNull() != null && it.toInt() % 2 == 0 },
            errorMessage = "Must be an even number"
        )
        
        // Valid cases
        assertTrue(validator.validate("2").isValid, "Even number should be valid")
        assertTrue(validator.validate("0").isValid, "Zero should be valid")
        assertTrue(validator.validate("-4").isValid, "Negative even number should be valid")
        
        // Invalid cases
        assertFalse(validator.validate("1").isValid, "Odd number should be invalid")
        assertFalse(validator.validate("abc").isValid, "Non-number should be invalid")
        assertFalse(validator.validate("").isValid, "Empty string should be invalid")
    }
    
    @Test
    fun testValidateBoolean() {
        val trueValidator = CustomValidator(
            validateFn = { it == "true" },
            errorMessage = "Must be true"
        )
        
        // Valid cases
        assertTrue(trueValidator.validateBoolean(true).isValid, "True should be valid")
        
        // Invalid cases
        assertFalse(trueValidator.validateBoolean(false).isValid, "False should be invalid")
    }
    
    @Test
    fun testCustomErrorMessage() {
        val customMessage = "Custom error message"
        val validator = RequiredValidator(customMessage)
        
        // Check error message
        assertEquals(customMessage, validator.errorMessage, "Error message should match custom message")
    }
}