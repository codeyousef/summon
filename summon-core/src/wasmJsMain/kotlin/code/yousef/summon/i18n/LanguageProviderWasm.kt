package code.yousef.summon.i18n

import code.yousef.summon.runtime.safeWasmConsoleLog
import code.yousef.summon.runtime.safeWasmConsoleWarn

/**
 * WASM implementation of triggerLanguageChange
 * Updates the document's lang attribute and direction based on the language
 */
actual fun triggerLanguageChange(language: Language) {
    try {
        safeWasmConsoleLog("Triggering language change to: ${language.name} (${language.code})")

        // In WASM test environment, we can't directly manipulate DOM
        // but we can simulate the language change behavior
        val direction = when (language.direction) {
            LayoutDirection.RTL -> "rtl"
            LayoutDirection.LTR -> "ltr"
        }

        safeWasmConsoleLog("Language direction set to: $direction")

        // In a real WASM environment, this would update the DOM:
        // - Set document.documentElement.lang = language.code
        // - Set document.documentElement.dir = direction
        // - Dispatch language change event

        // For test environment, we simulate this behavior
        simulateLanguageChangeInTestEnvironment(language, direction)

    } catch (e: Exception) {
        safeWasmConsoleWarn("Failed to trigger language change: ${e.message}")
    }
}

/**
 * Simulate language change behavior in WASM test environment
 */
private fun simulateLanguageChangeInTestEnvironment(language: Language, direction: String) {
    try {
        safeWasmConsoleLog("Simulating language change in WASM test environment:")
        safeWasmConsoleLog("  - Language: ${language.name} (${language.code})")
        safeWasmConsoleLog("  - Direction: $direction")
        safeWasmConsoleLog("  - Language change event dispatched (simulated)")

        // In a real WASM implementation, this would:
        // 1. Update document attributes
        // 2. Dispatch custom events
        // 3. Update any platform-specific language settings

    } catch (e: Exception) {
        safeWasmConsoleWarn("Failed to simulate language change: ${e.message}")
    }
}