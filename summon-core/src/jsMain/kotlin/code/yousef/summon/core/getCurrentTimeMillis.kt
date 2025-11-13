package codes.yousef.summon.core

import kotlin.js.Date

/**
 * JavaScript implementation of getCurrentTimeMillis using the Date API.
 */
actual fun getCurrentTimeMillis(): Long = Date.now().toLong()