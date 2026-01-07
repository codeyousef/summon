package codes.yousef.summon.i18n

import kotlin.test.*

class I18nConfigTest {

    @BeforeTest
    fun setup() {
        // Reset the configuration before each test
        I18nConfig.configure {
            // Empty configuration to reset
        }
    }

    @Test
    fun testConfigureLanguages() {
        // Configure with multiple languages
        I18nConfig.configure {
            language("en", "English")
            language("fr", "French")
            language("ar", "Arabic", LayoutDirection.RTL)
        }

        // Verify languages were added
        val languages = I18nConfig.supportedLanguages
        assertEquals(3, languages.size)

        // Verify English
        val english = languages.find { it.code == "en" }
        assertNotNull(english)
        assertEquals("English", english.name)
        assertEquals(LayoutDirection.LTR, english.direction)

        // Verify French
        val french = languages.find { it.code == "fr" }
        assertNotNull(french)
        assertEquals("French", french.name)
        assertEquals(LayoutDirection.LTR, french.direction)

        // Verify Arabic
        val arabic = languages.find { it.code == "ar" }
        assertNotNull(arabic)
        assertEquals("Arabic", arabic.name)
        assertEquals(LayoutDirection.RTL, arabic.direction)
    }

    @Test
    fun testDefaultLanguage() {
        // Configure with multiple languages
        I18nConfig.configure {
            language("en", "English")
            language("fr", "French")
            language("de", "German")
        }

        // Verify the first language is the default
        val defaultLanguage = I18nConfig.defaultLanguage
        assertNotNull(defaultLanguage)
        assertEquals("en", defaultLanguage.code)
    }

    @Test
    fun testSetDefaultLanguage() {
        // Configure with multiple languages and set a default
        I18nConfig.configure {
            language("en", "English")
            language("fr", "French")
            language("de", "German")
            setDefault("fr")
        }

        // Verify the specified language is the default
        val defaultLanguage = I18nConfig.defaultLanguage
        assertNotNull(defaultLanguage)
        assertEquals("fr", defaultLanguage.code)
    }

    @Test
    fun testSetNonExistentDefaultLanguage() {
        // Configure with multiple languages and try to set a non-existent default
        I18nConfig.configure {
            language("en", "English")
            language("fr", "French")
            setDefault("es") // Spanish is not in the list
        }

        // Verify the first language is still the default
        val defaultLanguage = I18nConfig.defaultLanguage
        assertNotNull(defaultLanguage)
        assertEquals("en", defaultLanguage.code)
    }

    @Test
    fun testReconfiguration() {
        // Configure initially
        I18nConfig.configure {
            language("en", "English")
            language("fr", "French")
        }

        // Verify initial configuration
        assertEquals(2, I18nConfig.supportedLanguages.size)

        // Reconfigure with different languages
        I18nConfig.configure {
            language("es", "Spanish")
            language("it", "Italian")
            language("de", "German")
        }

        // Verify new configuration replaced the old one
        val languages = I18nConfig.supportedLanguages
        assertEquals(3, languages.size)
        assertTrue(languages.any { it.code == "es" })
        assertTrue(languages.any { it.code == "it" })
        assertTrue(languages.any { it.code == "de" })
        assertFalse(languages.any { it.code == "en" })
        assertFalse(languages.any { it.code == "fr" })
    }

    @Test
    fun testEmptyConfiguration() {
        // Configure with no languages
        I18nConfig.configure {
            // Empty configuration
        }

        // Verify no languages and null default
        assertTrue(I18nConfig.supportedLanguages.isEmpty())
        assertNull(I18nConfig.defaultLanguage)
    }
}