package codes.yousef.summon.i18n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class I18nUtilsTest {
    
    @Test
    fun testFormatMessage() {
        // Test basic placeholder replacement
        val message = "Hello, {name}!"
        val params = mapOf("name" to "World")
        val result = I18nUtils.formatMessage(message, params)
        assertEquals("Hello, World!", result)
        
        // Test multiple placeholders
        val message2 = "Hello, {firstName} {lastName}!"
        val params2 = mapOf("firstName" to "John", "lastName" to "Doe")
        val result2 = I18nUtils.formatMessage(message2, params2)
        assertEquals("Hello, John Doe!", result2)
        
        // Test with non-string values
        val message3 = "You have {count} items in your cart."
        val params3 = mapOf("count" to 5)
        val result3 = I18nUtils.formatMessage(message3, params3)
        assertEquals("You have 5 items in your cart.", result3)
        
        // Test with missing placeholders (should leave them unchanged)
        val message4 = "Hello, {name}! Your score is {score}."
        val params4 = mapOf("name" to "Player")
        val result4 = I18nUtils.formatMessage(message4, params4)
        assertEquals("Hello, Player! Your score is {score}.", result4)
    }
    
    @Test
    fun testPluralString() {
        // Test with count = 0 and zero form provided
        val result1 = I18nUtils.pluralString(
            count = 0,
            zero = "No items",
            one = "One item",
            other = "{count} items"
        )
        assertEquals("No items", result1)
        
        // Test with count = 0 and no zero form
        val result2 = I18nUtils.pluralString(
            count = 0,
            one = "One item",
            other = "{count} items"
        )
        assertEquals("0 items", result2)
        
        // Test with count = 1
        val result3 = I18nUtils.pluralString(
            count = 1,
            zero = "No items",
            one = "One item",
            other = "{count} items"
        )
        assertEquals("One item", result3)
        
        // Test with count = 2 and few form provided
        val result4 = I18nUtils.pluralString(
            count = 2,
            zero = "No items",
            one = "One item",
            few = "A few items ({count})",
            other = "{count} items"
        )
        assertEquals("A few items (2)", result4)
        
        // Test with count = 10 and many form provided
        val result5 = I18nUtils.pluralString(
            count = 10,
            zero = "No items",
            one = "One item",
            few = "A few items ({count})",
            many = "Many items ({count})",
            other = "{count} items"
        )
        assertEquals("Many items (10)", result5)
        
        // Test with count = 100 (other form)
        val result6 = I18nUtils.pluralString(
            count = 100,
            zero = "No items",
            one = "One item",
            few = "A few items ({count})",
            many = "Many items ({count})",
            other = "{count} items"
        )
        assertEquals("100 items", result6)
    }
    
    @Test
    fun testGenderString() {
        // Test male gender
        val result1 = I18nUtils.genderString(
            gender = "male",
            male = "He likes this",
            female = "She likes this",
            other = "They like this"
        )
        assertEquals("He likes this", result1)
        
        // Test female gender
        val result2 = I18nUtils.genderString(
            gender = "female",
            male = "He likes this",
            female = "She likes this",
            other = "They like this"
        )
        assertEquals("She likes this", result2)
        
        // Test other gender
        val result3 = I18nUtils.genderString(
            gender = "non-binary",
            male = "He likes this",
            female = "She likes this",
            other = "They like this"
        )
        assertEquals("They like this", result3)
        
        // Test case insensitivity
        val result4 = I18nUtils.genderString(
            gender = "MALE",
            male = "He likes this",
            female = "She likes this",
            other = "They like this"
        )
        assertEquals("He likes this", result4)
    }
    
    // Note: The following tests for formatNumber and formatDate are limited
    // because they depend on CompositionLocal which requires a composable context.
    // In a real implementation, we would use a testing framework that supports
    // composable testing, such as compose-test-rule.
    
    @Test
    fun testDateFormat() {
        // Test that all DateFormat values exist
        val formats = DateFormat.values()
        assertEquals(4, formats.size)
        assertTrue(formats.contains(DateFormat.SHORT))
        assertTrue(formats.contains(DateFormat.MEDIUM))
        assertTrue(formats.contains(DateFormat.LONG))
        assertTrue(formats.contains(DateFormat.FULL))
    }
}