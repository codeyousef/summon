package code.yousef.summon.i18n

import code.yousef.summon.js.console
import code.yousef.summon.state.mutableStateOf
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
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
            // Make sure the code.yousef.summon.i18n namespace exists
            window.code = window.code || {};
            window.code.yousef = window.code.yousef || {};
            window.code.yousef.summon = window.code.yousef.summon || {};
            window.code.yousef.summon.i18n = window.code.yousef.summon.i18n || {};

            // Expose StringResources
            window.code.yousef.summon.i18n.StringResources = {
                getString: function(key, languageCode, fallbackLanguageCode) {
                    try {
                        console.log("[DEBUG] JS StringResources.getString called with key=" + key + ", languageCode=" + languageCode);
                        return code.yousef.summon.i18n.StringResources.getString_8ym3z0(key, languageCode, fallbackLanguageCode || null);
                    } catch (e) {
                        console.error("[DEBUG] Error in JS StringResources.getString: " + e);
                        return key; // Return the key as fallback
                    }
                },
                getKeysForLanguage: function(languageCode) {
                    try {
                        return code.yousef.summon.i18n.StringResources.getKeysForLanguage_5afk3v(languageCode);
                    } catch (e) {
                        console.error("[DEBUG] Error in JS StringResources.getKeysForLanguage: " + e);
                        return []; // Return empty array as fallback
                    }
                }
            };

            // Expose getCurrentLanguage
            window.code.yousef.summon.i18n.getCurrentLanguage = function() {
                try {
                    return code.yousef.summon.i18n.getCurrentLanguage();
                } catch (e) {
                    console.error("[DEBUG] Error in JS getCurrentLanguage: " + e);
                    return { code: "en", name: "English", direction: { name: "LTR", ordinal: 0 } }; // Return default as fallback
                }
            };

            console.log("[DEBUG] Successfully exposed i18n functions to JavaScript");
        } catch (e) {
            console.error("[DEBUG] Error exposing i18n functions to JavaScript: " + e);
        }
        """
        )
    }

    /**
     * Load language resources from the specified base path
     *
     * @param basePath The base path for language JSON files (e.g., "/i18n/")
     * @param onComplete Callback to be executed when all resources are loaded
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun loadLanguageResources(basePath: String = "/i18n/", onComplete: (() -> Unit)? = null) {
        console.log("[DEBUG] Starting to load language resources from base path: " + basePath)

        // Log the supported languages
        console.log("[DEBUG] Supported languages: " + I18nConfig.supportedLanguages.joinToString(", ") { it.code })

        GlobalScope.launch {
            I18nConfig.supportedLanguages.forEach { language ->
                try {
                    val url = basePath + language.code + ".json"
                    console.log("[DEBUG] Loading language file from URL: " + url)

                    // Try to fetch the resource
                    try {
                        val response = window.fetch(url).await()
                        console.log("[DEBUG] Fetch response for " + language.code + ": status=" + response.status)

                        if (response.ok) {
                            val jsonText = response.text().await()
                            console.log("[DEBUG] Successfully loaded language file for " + language.code + " with length: " + jsonText.length)

                            // Log a sample of the JSON content
                            if (jsonText.length > 100) {
                                console.log("[DEBUG] JSON content sample: " + jsonText.substring(0, 100) + "...")
                            } else {
                                console.log("[DEBUG] JSON content: " + jsonText)
                            }

                            // Load the translations
                            StringResources.loadTranslations(language.code, jsonText)

                            // Log the loaded keys
                            val keys = StringResources.getKeysForLanguage(language.code)
                            console.log("[DEBUG] Loaded translations for " + language.code + ": " + keys.size + " keys")

                            // Log some sample keys and their translations
                            if (keys.isNotEmpty()) {
                                val sampleKeys = keys.take(5)
                                console.log("[DEBUG] Sample translations for " + language.code + ":")
                                sampleKeys.forEach { key ->
                                    val translation = StringResources.getString(key, language.code)
                                    console.log("[DEBUG]   - " + key + ": " + translation)
                                }
                            }
                        } else {
                            console.error("[DEBUG] Failed to load language file for " + language.code + ": status=" + response.status + ", statusText=" + response.statusText)
                        }
                    } catch (e: Exception) {
                        console.error("[DEBUG] Error fetching language file for " + language.code + ": " + e)
                        console.error("[DEBUG] Error stack: " + e.stackTraceToString())
                    }
                } catch (e: Exception) {
                    console.error("[DEBUG] Outer error loading language file for " + language.code + ": " + e)
                    console.error("[DEBUG] Error stack: " + e.stackTraceToString())
                }
            }

            // Log the final state of all loaded translations
            console.log("[DEBUG] Final state of loaded translations:")
            I18nConfig.supportedLanguages.forEach { language ->
                val keys = StringResources.getKeysForLanguage(language.code)
                console.log("[DEBUG] " + language.code + ": " + keys.size + " keys loaded")
            }

            // Call the onComplete callback if provided
            onComplete?.let { callback ->
                console.log("[DEBUG] Calling onComplete callback after loading all language resources")
                callback()
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
 * Extension method to set attributes on HTML elements
 */
fun HTMLElement.setAttribute(name: String, value: String) {
    asDynamic().setAttribute(name, value)
} 
