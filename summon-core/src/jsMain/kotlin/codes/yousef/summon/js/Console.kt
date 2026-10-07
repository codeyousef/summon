package codes.yousef.summon.js

/**
 * External interface for Console
 */
external interface Console {
    /**
     * Executes the log operation.
     *
     * @param message Message content.
     */
    fun log(message: String)
    /**
     * Executes the log operation.
     *
     * @param message Message content.
     * @param obj The obj value.
     */
    fun log(message: String, obj: dynamic)
    /**
     * Executes the warn operation.
     *
     * @param message Message content.
     */
    fun warn(message: String)
    /**
     * Executes the warn operation.
     *
     * @param message Message content.
     * @param obj The obj value.
     */
    fun warn(message: String, obj: dynamic)
    /**
     * Executes the error operation.
     *
     * @param message Message content.
     */
    fun error(message: String)
    /**
     * Executes the error operation.
     *
     * @param message Message content.
     * @param obj The obj value.
     */
    fun error(message: String, obj: dynamic)
}

/**
 * External interface for accessing the console
 */
external val console: Console