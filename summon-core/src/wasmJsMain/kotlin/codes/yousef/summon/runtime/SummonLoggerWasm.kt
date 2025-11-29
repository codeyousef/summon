package codes.yousef.summon.runtime

/**
 * Check if running in production environment (non-localhost).
 */
@JsFun("() => { try { const h = window.location.hostname; return h !== 'localhost' && h !== '127.0.0.1'; } catch(e) { return false; } }")
private external fun wasmIsProductionEnvironment(): Boolean

/**
 * WebAssembly implementation of SummonLogger.
 * In production (non-localhost), only errors are logged to reduce main-thread blocking.
 */
actual object SummonLogger {
    private val isProduction: Boolean = wasmIsProductionEnvironment()

    actual fun log(message: String) {
        if (!isProduction) {
            wasmConsoleLog("[SUMMON] $message")
        }
    }

    actual fun warn(message: String) {
        if (!isProduction) {
            wasmConsoleWarn("[SUMMON] $message")
        }
    }

    actual fun error(message: String) {
        // Always log errors, even in production
        wasmConsoleError("[SUMMON] $message")
    }
}

