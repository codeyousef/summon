package codes.yousef.summon.i18n

import codes.yousef.summon.state.mutableStateOf
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLElement

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

        // Expose i18n functions to JavaScript
        exposeI18nFunctionsToJs()
    }

    /**
     * Exposes i18n functions to JavaScript for direct access
     */
    private fun exposeI18nFunctionsToJs() {
        js(
            """
        try {
            // Make sure the codes.yousef.summon.i18n namespace exists
            window.code = window.code || {};
            window.code.yousef = window.code.yousef || {};
            window.codes.yousef.summon = window.codes.yousef.summon || {};
            window.codes.yousef.summon.i18n = window.codes.yousef.summon.i18n || {};

            // Expose StringResources
            window.codes.yousef.summon.i18n.StringResources = {
                getString: function(key, languageCode, fallbackLanguageCode) {
                    try {
                        return codes.yousef.summon.i18n.StringResources.getString_8ym3z0(key, languageCode, fallbackLanguageCode || null);
                    } catch (e) {
                        return key; // Return the key as fallback
                    }
                },
                getKeysForLanguage: function(languageCode) {
                    try {
                        return codes.yousef.summon.i18n.StringResources.getKeysForLanguage_5afk3v(languageCode);
                    } catch (e) {
                        return []; // Return empty array as fallback
                    }
                }
            };

            // Expose getCurrentLanguage
            window.codes.yousef.summon.i18n.getCurrentLanguage = function() {
                try {
                    return codes.yousef.summon.i18n.getCurrentLanguage();
                } catch (e) {
                    return { code: "en", name: "English", direction: { name: "LTR", ordinal: 0 } }; // Return default as fallback
                }
            };

        } catch (e) {
            // Silently handle errors during i18n setup
        }
        """
        )
    }

    /**
     * Loads language resources in the caller-owned [scope].
     *
     * Canceling the scope cancels pending fetches and suppresses [onComplete]. The returned [Job]
     * can be canceled independently when the owning screen or account is removed.
     */
    fun loadLanguageResources(
        scope: CoroutineScope,
        basePath: String = "/i18n/",
        onComplete: (() -> Unit)? = null
    ): Job = scope.launch {
            I18nConfig.supportedLanguages.forEach { language ->
                try {
                    val url = basePath + language.code + ".json"

                    // Try to fetch the resource
                    try {
                        val response = window.fetch(url).await()

                        if (response.ok) {
                            val jsonText = response.text().await()
                            // Load the translations
                            StringResources.loadTranslations(language.code, jsonText)
                        }
                    } catch (e: Exception) {
                        // Silently handle fetch errors
                    }
                } catch (e: Exception) {
                    // Silently handle outer errors
                }
            }

            // Call the onComplete callback if provided
            onComplete?.invoke()
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
 * Extension method to set attributes on HTML elements
 */
fun HTMLElement.setAttribute(name: String, value: String) {
    asDynamic().setAttribute(name, value)
} 
