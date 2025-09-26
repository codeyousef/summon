package code.yousef.summon.i18n

import code.yousef.summon.core.mapOfCompat

/**
 * Interface for providing translations.
 */
interface TranslationProvider {
    /**
     * Gets a translation for the given key and language.
     */
    fun getTranslation(key: String, language: String): String?
    
    /**
     * Gets all available languages.
     */
    fun getAvailableLanguages(): List<String>
}

/**
 * Map-based translation provider.
 */
class MapTranslationProvider(
    private val translations: Map<String, Map<String, String>>
) : TranslationProvider {
    
    override fun getTranslation(key: String, language: String): String? {
        return translations[language]?.get(key)
    }
    
    override fun getAvailableLanguages(): List<String> {
        return translations.keys.toList()
    }
}

/**
 * Utility to create a translation provider from nested maps.
 */
fun translationProvider(
    vararg languageMaps: Pair<String, Map<String, String>>
): TranslationProvider {
    return MapTranslationProvider(mapOfCompat(*languageMaps))
}