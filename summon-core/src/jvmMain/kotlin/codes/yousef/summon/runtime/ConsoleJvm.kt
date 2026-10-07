package codes.yousef.summon.runtime

/**
 * JVM implementation of SummonLogger for logging.
 */
actual object SummonLogger {
    /**
     * Executes the log operation.
     *
     * @param message Message content.
     */
    actual fun log(message: String) {
        println("[SUMMON-LOG] $message")
    }

    /**
     * Executes the warn operation.
     *
     * @param message Message content.
     */
    actual fun warn(message: String) {
        System.err.println("[SUMMON-WARN] $message")
    }

    /**
     * Executes the error operation.
     *
     * @param message Message content.
     */
    actual fun error(message: String) {
        System.err.println("[SUMMON-ERROR] $message")
    }
}