package code.yousef.summon.i18n

import code.yousef.summon.runtime.wasmConsoleLog

actual fun triggerLanguageChange(language: Language) {
    wasmConsoleLog("Triggering language change to: ${language.code} - WASM stub")
}