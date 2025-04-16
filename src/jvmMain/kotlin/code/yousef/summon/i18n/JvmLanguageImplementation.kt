package code.yousef.summon.i18n

import java.util.Locale

/**
 * JVM implementation of triggerLanguageChange
 * Updates the default locale based on the language
 */
actual fun triggerLanguageChange(language: Language) {
    // Set the default locale based on the language code
    val locale = when (language.code) {
        "en" -> Locale.ENGLISH
        "fr" -> Locale.FRENCH
        "de" -> Locale.GERMAN
        "it" -> Locale.ITALIAN
        "ja" -> Locale.JAPANESE
        "ko" -> Locale.KOREAN
        "zh" -> Locale.CHINESE
        "es" -> Locale("es")
        "ar" -> Locale("ar")
        "ru" -> Locale("ru")
        else -> Locale(language.code)
    }
    
    // Set the default locale
    Locale.setDefault(locale)
    
    // Log the language change
    println("Language changed to ${language.name} (${language.code}), locale: $locale")
    
    // Note: In a real application, we would also need to trigger recomposition
    // of any components that depend on the language. This would typically be
    // done through a state management system or event bus.
}