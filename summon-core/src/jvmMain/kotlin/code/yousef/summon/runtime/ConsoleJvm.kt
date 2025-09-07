package code.yousef.summon.runtime

/**
 * JVM implementation of SummonLogger for logging.
 */
actual object SummonLogger {
    actual fun log(message: String) {
        println("[SUMMON-LOG] $message")
    }
    
    actual fun warn(message: String) {
        System.err.println("[SUMMON-WARN] $message")
    }
    
    actual fun error(message: String) {
        System.err.println("[SUMMON-ERROR] $message")
    }
}