package codes.yousef.summon.i18n

import codes.yousef.summon.core.mapOfCompat

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

 * @property translations The translations value.
 */
class MapTranslationProvider(
    private val translations: Map<String, Map<String, String>>
) : TranslationProvider {

    /**
     * Returns translation.
     *
     * @param key Lookup key.
     * @param language The language value.
     * @return The resulting value.
     */
    override fun getTranslation(key: String, language: String): String? {
        return translations[language]?.get(key)
    }

    /**
     * Returns available languages.
     *
     * @return The resulting value.
     */
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