package code.yousef.summon.i18n

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * Demonstration component showing i18n capabilities - simplified to avoid compilation errors
 * NOTE: This is a placeholder implementation that would need to be integrated with actual UI components
 */
@Composable
fun I18nDemo() {
    // This is a simplified implementation to avoid compilation errors
    // In a real app, this would use the Summon component system
    
    // Get current language and layout direction
    val languageProvider = LocalLanguage.current
    val layoutDirection = LocalLayoutDirection.current
    
    // Always invoke the function to get the actual value
    val currentLanguage = (languageProvider as Function0<Language>).invoke()
    
    // Always invoke the function to get the actual value
    val actualDirection = (layoutDirection as Function0<LayoutDirection>).invoke()
    
    val isRtl = actualDirection == LayoutDirection.RTL
    
    // Demonstrate language and direction information
    val directionText = if (isRtl) "RTL" else "LTR"
    val welcomeText = StringResources.getString("common.welcome", currentLanguage.code)
    
    // Output would show welcome message in the current language
    // and would adapt layout based on the text direction
}

/**
 * Simple placeholder for the language selector - not a real implementation
 */
@Composable
private fun LanguageSelector() {
    // This would show language buttons for each supported language
    // When clicked, it would call changeLanguage()
    
    // Example: Display language options in a list
    for (language in I18nConfig.supportedLanguages) {
        // In the real implementation, this would be buttons for each language
    }
} 