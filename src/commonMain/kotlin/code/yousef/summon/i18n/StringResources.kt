package code.yousef.summon.i18n

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

/**
 * Manager for string translation resources
 */
object StringResources {
    private val translations = mutableMapOf<String, Map<String, String>>()
    
    /**
     * Load translations for a specific language from JSON content
     * 
     * @param languageCode ISO language code
     * @param jsonContent JSON content as a string
     */
    fun loadTranslations(languageCode: String, jsonContent: String) {
        val jsonObject = Json.decodeFromString<JsonObject>(jsonContent)
        val stringMap = flattenJsonObject(jsonObject)
        translations[languageCode] = stringMap
    }
    
    /**
     * Get a translated string for the specified key and language
     * 
     * @param key Translation key (e.g., "common.welcome")
     * @param languageCode Current language code
     * @param fallbackLanguageCode Optional fallback language if translation is missing
     * @return The translated string or the key if not found
     */
    fun getString(key: String, languageCode: String, fallbackLanguageCode: String? = null): String {
        return translations[languageCode]?.get(key)
            ?: fallbackLanguageCode?.let { translations[it]?.get(key) }
            ?: key
    }
    
    /**
     * Clear all loaded translations
     */
    fun clearTranslations() {
        translations.clear()
    }
    
    /**
     * Get all keys for a specific language
     * 
     * @param languageCode Language code
     * @return Set of all translation keys for the language
     */
    fun getKeysForLanguage(languageCode: String): Set<String> {
        return translations[languageCode]?.keys ?: emptySet()
    }
    
    /**
     * Flatten a nested JSON object into a map with dot-notation keys
     */
    private fun flattenJsonObject(
        jsonObject: JsonObject, 
        prefix: String = ""
    ): Map<String, String> {
        val result = mutableMapOf<String, String>()
        
        for ((key, element) in jsonObject) {
            val newKey = if (prefix.isEmpty()) key else "$prefix.$key"
            
            when (element) {
                is JsonObject -> {
                    result.putAll(flattenJsonObject(element, newKey))
                }
                is JsonPrimitive -> {
                    if (element.isString) {
                        result[newKey] = element.content
                    }
                }
                else -> {
                    // Ignore arrays and other complex types for now
                }
            }
        }
        
        return result
    }
} 