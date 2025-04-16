package code.yousef.summon.i18n

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLScriptElement
import org.w3c.dom.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.await
import org.w3c.fetch.RequestInit
import code.yousef.summon.state.mutableStateOf

/**
 * Global state for the current language in JS platform
 */
private val currentLanguageState = mutableStateOf(
    I18nConfig.defaultLanguage ?: Language("en", "English", LayoutDirection.LTR)
)

/**
 * JavaScript-specific implementation for loading i18n translation files
 */
object JsI18nImplementation {

    /**
     * Initialize i18n for the JS platform
     */
    fun init() {
        // Set the document direction initially
        updateDocumentDirection()

        // Override changeLanguage function for JS platform
        setupLanguageChanging()
    }

    /**
     * Load language resources from the specified base path
     * 
     * @param basePath The base path for language JSON files (e.g., "/i18n/")
     */
    fun loadLanguageResources(basePath: String = "/i18n/") {
        GlobalScope.launch {
            I18nConfig.supportedLanguages.forEach { language ->
                try {
                    val response = window.fetch("$basePath${language.code}.json").await()
                    if (response.ok) {
                        val jsonText = response.text().await()
                        StringResources.loadTranslations(language.code, jsonText)
                    } else {
                        console.error("Failed to load language file for ${language.code}")
                    }
                } catch (e: Exception) {
                    console.error("Error loading language file for ${language.code}: $e")
                }
            }
        }
    }

    /**
     * Sets up language changing functionality for JS platform
     */
    private fun setupLanguageChanging() {
        val originalChangeLanguage = ::changeLanguage

        // Replace the common changeLanguage function with JS-specific implementation
        @Suppress("UNUSED_VARIABLE")
        val newChangeLanguage = { languageCode: String ->
            val language = I18nConfig.supportedLanguages.find { it.code == languageCode }
            if (language != null) {
                currentLanguageState.value = language
                updateDocumentDirection()
                originalChangeLanguage(languageCode)
                true
            } else {
                false
            }
        }
    }

    /**
     * Updates the document direction based on the current language
     */
    private fun updateDocumentDirection() {
        val direction = currentLanguageState.value.direction
        val dirValue = if (direction == LayoutDirection.RTL) "rtl" else "ltr"
        document.documentElement?.setAttribute("dir", dirValue)
        document.body?.setAttribute("dir", dirValue)
    }
}

/**
 * Get the current language for JS platform
 */
fun JsI18nImplementation.getCurrentLanguageJs(): Language {
    return currentLanguageState.value
}

/**
 * Extension property to access the console
 */
external val console: Console

/**
 * Console interface for JS
 */
external interface Console {
    fun log(message: String)
    fun error(message: String)
}

/**
 * Extension method to set attributes on HTML elements
 */
fun HTMLElement.setAttribute(name: String, value: String) {
    asDynamic().setAttribute(name, value)
} 
