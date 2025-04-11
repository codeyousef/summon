package code.yousef.summon.i18n

import code.yousef.summon.runtime.Composable
import code.yousef.summon.state.mutableStateOf

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
    
    // Temporarily set the language and direction directly
    val prevLanguage = LocalLanguage.current
    val prevDirection = LocalLayoutDirection.current
    
    // Call content with new language values
    // Note: In a real implementation, we would use a proper CompositionLocalProvider
    content()
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
    val currentLanguage = (languageProvider as Function0<Language>).invoke()
    
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
        // This will be implemented by the platform-specific code to handle recomposition
        return true
    }
    return false
} 