package code.yousef.summon.i18n

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.CompositionLocal

/**
 * Context for internationalization support.
 * Provides the current language and translation functions to composables.
 */
data class I18nContext(
    val language: String = "en",
    val translations: Map<String, String> = emptyMap(),
    val translator: (String, String) -> String = { key, lang -> key } // Fallback to key if no translation
) {
    /**
     * Gets a translation for the given key in the current language.
     */
    fun getString(key: String): String {
        return translations[key] ?: translator(key, language)
    }
    
    /**
     * Gets a translation for the given key with simple placeholder replacement.
     * Replaces {0}, {1}, etc. with the provided arguments.
     */
    fun getString(key: String, vararg args: Any): String {
        var template = getString(key)
        args.forEachIndexed { index, arg ->
            template = template.replace("{$index}", arg.toString())
        }
        return template
    }
}

/**
 * CompositionLocal for accessing i18n context.
 */
val LocalI18n = CompositionLocal.compositionLocalOf(I18nContext())

/**
 * Provider component for i18n context.
 */
@Composable
fun I18nProvider(
    language: String,
    translations: Map<String, String> = emptyMap(),
    translator: (String, String) -> String = { key, _ -> key },
    content: @Composable () -> Unit
) {
    val i18nContext = I18nContext(language, translations, translator)
    
    // TODO: This would need proper CompositionLocal provider support in the framework
    // For now, we'll just pass the context through a simple mechanism
    LocalI18n.provides(i18nContext)
    content()
}

/**
 * Hook to access the current i18n context.
 */
@Composable
fun rememberI18n(): I18nContext {
    return LocalI18n.current
}