package code.yousef.summon.effects

/**
 * JVM implementation of getCurrentTimeMillis using System.currentTimeMillis().
 */
actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis() 