package code.yousef.summon.accessibility

/**
 * JVM implementation for setting focus.
 * In a server-side HTML rendering context, directly setting focus is generally
 * not possible. This would require client-side JavaScript.
 *
 * @param elementId The ID of the element intended to receive focus.
 * @return `false` as server-side focus setting is not directly supported.
 */
actual fun applyFocusPlatform(elementId: String): Boolean {
    println("[WARN] applyFocusPlatform('$elementId') called on JVM. Server-side focus not supported. Requires client-side JS.")
    return false
} 