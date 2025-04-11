package code.yousef.summon.effects

import kotlin.js.Date

/**
 * JavaScript implementation of getCurrentTimeMillis using Date.now()
 */
actual fun getCurrentTimeMillis(): Long = Date.now().toLong() 