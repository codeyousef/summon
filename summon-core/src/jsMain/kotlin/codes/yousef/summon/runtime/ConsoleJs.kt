package codes.yousef.summon.runtime

/**
 * JavaScript implementation of SummonLogger for logging.
 * In production (non-localhost), only errors are logged to reduce main-thread blocking.
 */
actual object SummonLogger {
    private val isProduction: Boolean = js("window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1'") as Boolean

    actual fun log(message: String) {
        if (!isProduction) {
            js("console.log('[SUMMON] ' + message)")
        }
    }

    actual fun warn(message: String) {
        if (!isProduction) {
            js("console.warn('[SUMMON] ' + message)")
        }
    }

    actual fun error(message: String) {
        // Always log errors, even in production
        js("console.error('[SUMMON] ' + message)")
    }
}