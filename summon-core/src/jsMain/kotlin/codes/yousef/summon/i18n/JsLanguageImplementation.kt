package codes.yousef.summon.i18n

import kotlinx.browser.document

/**
 * External interface for CustomEvent
 * Note: Not extending Event to avoid WASM compilation issues
 */
external class CustomEvent(type: String, eventInitDict: dynamic = definedExternally) {
    val detail: dynamic
    val type: String
}

/**
 * JavaScript implementation of triggerLanguageChange
 * Updates the document's lang attribute and direction based on the language
 */
actual fun triggerLanguageChange(language: Language) {
    // Set the document's lang attribute
    document.documentElement?.setAttribute("lang", language.code)

    // Set the document's dir attribute based on the language direction
    val direction = when (language.direction) {
        LayoutDirection.RTL -> "rtl"
        LayoutDirection.LTR -> "ltr"
    }
    document.documentElement?.setAttribute("dir", direction)

    // Create event initialization object
    val eventInit = js("({detail: {code: language.code, direction: direction}})")

    // Dispatch a custom event that components can listen for
    val event = CustomEvent("languagechange", eventInit)
    document.dispatchEvent(event.asDynamic())

    // Log the language change
    js("console.log('Language changed to ' + language.name + ' (' + language.code + '), direction: ' + direction)")
}
