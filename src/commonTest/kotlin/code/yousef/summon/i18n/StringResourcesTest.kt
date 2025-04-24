package code.yousef.summon.i18n

import kotlin.test.*

class StringResourcesTest {
    
    @BeforeTest
    fun setup() {
        // Clear any existing translations before each test
        StringResources.clearTranslations()
    }
    
    @Test
    fun testLoadTranslations() {
        // Create a simple JSON string with translations
        val jsonContent = """
        {
            "common": {
                "welcome": "Welcome",
                "goodbye": "Goodbye"
            },
            "errors": {
                "notFound": "Not Found"
            }
        }
        """
        
        // Load translations for English
        StringResources.loadTranslations("en", jsonContent)
        
        // Verify that keys were loaded correctly
        val keys = StringResources.getKeysForLanguage("en")
        assertEquals(3, keys.size)
        assertTrue(keys.contains("common.welcome"))
        assertTrue(keys.contains("common.goodbye"))
        assertTrue(keys.contains("errors.notFound"))
    }
    
    @Test
    fun testGetString() {
        // Load translations for English
        val enJson = """
        {
            "common": {
                "welcome": "Welcome",
                "goodbye": "Goodbye"
            }
        }
        """
        StringResources.loadTranslations("en", enJson)
        
        // Load translations for French
        val frJson = """
        {
            "common": {
                "welcome": "Bienvenue",
                "goodbye": "Au revoir"
            }
        }
        """
        StringResources.loadTranslations("fr", frJson)
        
        // Test getting strings for English
        assertEquals("Welcome", StringResources.getString("common.welcome", "en"))
        assertEquals("Goodbye", StringResources.getString("common.goodbye", "en"))
        
        // Test getting strings for French
        assertEquals("Bienvenue", StringResources.getString("common.welcome", "fr"))
        assertEquals("Au revoir", StringResources.getString("common.goodbye", "fr"))
    }
    
    @Test
    fun testGetStringWithFallback() {
        // Load translations for English
        val enJson = """
        {
            "common": {
                "welcome": "Welcome",
                "goodbye": "Goodbye"
            },
            "errors": {
                "notFound": "Not Found"
            }
        }
        """
        StringResources.loadTranslations("en", enJson)
        
        // Load translations for French (missing some keys)
        val frJson = """
        {
            "common": {
                "welcome": "Bienvenue"
            }
        }
        """
        StringResources.loadTranslations("fr", frJson)
        
        // Test fallback to English for missing French translations
        assertEquals("Bienvenue", StringResources.getString("common.welcome", "fr"))
        assertEquals("Goodbye", StringResources.getString("common.goodbye", "fr", "en"))
        assertEquals("Not Found", StringResources.getString("errors.notFound", "fr", "en"))
        
        // Test key is returned when no translation exists
        assertEquals("unknown.key", StringResources.getString("unknown.key", "fr", "en"))
    }
    
    @Test
    fun testClearTranslations() {
        // Load translations
        val jsonContent = """{"test": "value"}"""
        StringResources.loadTranslations("en", jsonContent)
        
        // Verify translations were loaded
        assertNotEquals(emptySet<String>(), StringResources.getKeysForLanguage("en"))
        
        // Clear translations
        StringResources.clearTranslations()
        
        // Verify translations were cleared
        assertEquals(emptySet<String>(), StringResources.getKeysForLanguage("en"))
    }
    
    @Test
    fun testNestedJsonFlattening() {
        // Create a deeply nested JSON structure
        val jsonContent = """
        {
            "level1": {
                "level2": {
                    "level3": {
                        "deepValue": "Deep Value"
                    }
                },
                "value": "Level 1 Value"
            }
        }
        """
        
        // Load translations
        StringResources.loadTranslations("en", jsonContent)
        
        // Verify flattened keys
        val keys = StringResources.getKeysForLanguage("en")
        assertTrue(keys.contains("level1.level2.level3.deepValue"))
        assertTrue(keys.contains("level1.value"))
        
        // Verify values
        assertEquals("Deep Value", StringResources.getString("level1.level2.level3.deepValue", "en"))
        assertEquals("Level 1 Value", StringResources.getString("level1.value", "en"))
    }
}