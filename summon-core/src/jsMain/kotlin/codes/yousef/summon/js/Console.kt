package codes.yousef.summon.js

/**
 * External interface for Console
 */
external interface Console {
    fun log(message: String)
    fun log(message: String, obj: dynamic)
    fun warn(message: String)
    fun warn(message: String, obj: dynamic)
    fun error(message: String)
    fun error(message: String, obj: dynamic)
}

/**
 * External interface for accessing the console
 */
external val console: Console