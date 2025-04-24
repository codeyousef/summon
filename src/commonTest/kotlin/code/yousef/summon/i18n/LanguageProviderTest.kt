package code.yousef.summon.i18n

import kotlin.test.*

class LanguageProviderTest {

    // Note: Most functions in LanguageProvider.kt are composable functions
    // that depend on CompositionLocal, which requires a composable context.
    // In a real implementation, we would use a testing framework that supports
    // composable testing, such as compose-test-rule.

    // However, we can test some non-composable functions and the basic logic

    @BeforeTest
    fun setup() {
        // Reset the configuration before each test
        I18nConfig.configure {
            language("en", "English")
            language("fr", "French")
            language("ar", "Arabic", LayoutDirection.RTL)
        }

        // Reset to default language
        changeLanguage("en")

        // Clear any existing translations
        StringResources.clearTranslations()
    }

    @Test
    fun testChangeLanguage() {
        // Test changing to an existing language
        val result = changeLanguage("fr")
        assertTrue(result)

        // Verify the current language was updated
        val currentLanguage = getCurrentLanguage()
        assertEquals("fr", currentLanguage.code)
        assertEquals("French", currentLanguage.name)
        assertEquals(LayoutDirection.LTR, currentLanguage.direction)
    }

    @Test
    fun testChangeLanguageToRtl() {
        // Test changing to an RTL language
        val result = changeLanguage("ar")
        assertTrue(result)

        // Verify the current language was updated
        val currentLanguage = getCurrentLanguage()
        assertEquals("ar", currentLanguage.code)
        assertEquals("Arabic", currentLanguage.name)
        assertEquals(LayoutDirection.RTL, currentLanguage.direction)
    }

    @Test
    fun testChangeLanguageToNonExistent() {
        // Test changing to a non-existent language
        val result = changeLanguage("es") // Spanish is not in the list
        assertFalse(result)

        // Verify the current language was not updated (should still be the default)
        val currentLanguage = getCurrentLanguage()
        assertEquals("en", currentLanguage.code) // Default is English
    }

    @Test
    fun testGetCurrentLanguage() {
        // Verify the initial current language is the default
        val currentLanguage = getCurrentLanguage()
        assertEquals("en", currentLanguage.code)
        assertEquals("English", currentLanguage.name)
        assertEquals(LayoutDirection.LTR, currentLanguage.direction)
    }

    // The following test is a placeholder for what would be tested in a real composable context
    @Test
    fun testStringResourceLogic() {
        // This is a simplified test of the logic in the stringResource function
        // In a real test, we would use a composable testing framework

        // Setup translations
        val jsonContent = """
        {
            "common": {
                "welcome": "Welcome",
                "goodbye": "Goodbye"
            }
        }
        """
        StringResources.clearTranslations()
        StringResources.loadTranslations("en", jsonContent)

        // Simulate getting a string resource for the current language
        val key = "common.welcome"
        val languageCode = getCurrentLanguage().code
        val result = StringResources.getString(key, languageCode)

        assertEquals("Welcome", result)
    }
}
