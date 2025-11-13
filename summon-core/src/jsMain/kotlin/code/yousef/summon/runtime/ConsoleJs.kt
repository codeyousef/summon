package codes.yousef.summon.runtime

/**
 * JavaScript implementation of SummonLogger for logging.
 */
actual object SummonLogger {
    actual fun log(message: String) {
        js("console.log('[SUMMON] ' + message)")
    }
    
    actual fun warn(message: String) {
        js("console.warn('[SUMMON] ' + message)")
    }
    
    actual fun error(message: String) {
        js("console.error('[SUMMON] ' + message)")
    }
}