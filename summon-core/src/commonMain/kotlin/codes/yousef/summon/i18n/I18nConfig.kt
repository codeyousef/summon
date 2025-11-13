package codes.yousef.summon.i18n

import codes.yousef.summon.runtime.CompositionLocal

/**
 * Configuration object for internationalization settings
 */
object I18nConfig {
    private val _supportedLanguages = mutableListOf<Language>()
    val supportedLanguages: List<Language> get() = _supportedLanguages.toList()

    var defaultLanguage: Language? = null
        private set

    /**
     * Configure internationalization settings using the DSL builder
     */
    fun configure(block: I18nConfigBuilder.() -> Unit) {
        val builder = I18nConfigBuilder()
        builder.block()
        _supportedLanguages.clear()
        _supportedLanguages.addAll(builder.languages)
        defaultLanguage = builder.defaultLanguage ?: supportedLanguages.firstOrNull()
    }

    /**
     * Change the application language
     *
     * @param languageCode The language code to switch to
     * @return True if the language was changed, false if not found
     */
    fun changeLanguage(languageCode: String): Boolean {
        return codes.yousef.summon.i18n.changeLanguage(languageCode)
    }
}

/**
 * Builder class for i18n configuration DSL
 */
class I18nConfigBuilder {
    val languages = mutableListOf<Language>()
    var defaultLanguage: Language? = null

    /**
     * Add a supported language to the configuration
     *
     * @param code ISO language code (e.g., "en", "fr", "ar")
     * @param name Display name of the language
     * @param direction Text direction (defaults to LTR)
     */
    fun language(code: String, name: String, direction: LayoutDirection = LayoutDirection.LTR) {
        val language = Language(code, name, direction)
        languages.add(language)

        // If this is the first language or explicitly set as default
        if (defaultLanguage == null) {
            defaultLanguage = language
        }
    }

    /**
     * Set the default language by language code
     *
     * @param code Language code of the default language
     */
    fun setDefault(code: String) {
        defaultLanguage = languages.find { it.code == code }
    }
}

/**
 * CompositionLocal to provide the current language throughout the app
 */
val LocalLanguage = CompositionLocal.compositionLocalOf {
    Language("en", "English", LayoutDirection.LTR)
}

/**
 * CompositionLocal to provide the current layout direction
 */
val LocalLayoutDirection = CompositionLocal.compositionLocalOf { LayoutDirection.LTR } 
