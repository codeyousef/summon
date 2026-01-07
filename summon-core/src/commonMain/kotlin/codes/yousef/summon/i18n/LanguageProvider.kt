package codes.yousef.summon.i18n

import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.state.mutableStateOf

/**
 * Provides language context to the application
 *
 * @param initialLanguage The initial language to use
 * @param content The content to be provided with language context
 */
@Composable
fun LanguageProvider(
    initialLanguage: Language = I18nConfig.defaultLanguage ?: Language("en", "English", LayoutDirection.LTR),
    content: @Composable () -> Unit
) {
    val language = mutableStateOf(initialLanguage)

    // Store the previous language and direction from CompositionLocal
    val prevLanguage = LocalLanguage.current
    val prevDirection = LocalLayoutDirection.current

    try {
        // Provide the new language and direction via CompositionLocal
        // This makes them accessible to all child composables
        // Wrap in lambdas to match the expected Function0<T> type
        LocalLanguage.provides { language.value }
        LocalLayoutDirection.provides { initialLanguage.direction }

        // Call content with new language values
        content()
    } finally {
        // Restore the previous language and direction when this composable leaves the composition
        // We need to preserve the original function references
        @Suppress("UNCHECKED_CAST")
        LocalLanguage.provides(prevLanguage)
        @Suppress("UNCHECKED_CAST")
        LocalLayoutDirection.provides(prevDirection)
    }
}

/**
 * Utility function to get a localized string
 *
 * @param key The translation key
 * @return The translated string for the current language
 */
@Composable
fun stringResource(key: String): String {
    val languageProvider = LocalLanguage.current

    // Always invoke the function to get the actual value
    val currentLanguage = languageProvider.invoke()

    val defaultLanguage = I18nConfig.defaultLanguage?.code

    return StringResources.getString(key, currentLanguage.code, defaultLanguage)
}

/**
 * Change the application language
 *
 * @param languageCode The language code to switch to
 * @return True if the language was changed, false if not found
 */
fun changeLanguage(languageCode: String): Boolean {
    val newLanguage = I18nConfig.supportedLanguages.find { it.code == languageCode }
    if (newLanguage != null) {
        // Store the new language in a global variable that can be accessed by platform-specific code
        currentLanguage = newLanguage

        // Trigger recomposition - platform-specific implementations will handle this differently
        // For JS, this might involve updating the document's lang attribute and direction
        // For JVM, this might involve updating the application's locale
        triggerLanguageChange(newLanguage)

        return true
    }
    return false
}

// Global variable to store the current language
private var currentLanguage: Language = I18nConfig.defaultLanguage
    ?: Language("en", "English", LayoutDirection.LTR)

/**
 * Get the current language
 *
 * @return The current language
 */
fun getCurrentLanguage(): Language = currentLanguage

/**
 * Platform-specific implementation to trigger language change
 * This will be implemented differently for JS and JVM platforms
 */
expect fun triggerLanguageChange(language: Language)
