package codes.yousef.summon.effects

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private val timers = ConcurrentHashMap<Int, Timer>()
private val idGenerator = AtomicInteger(0)

/**
 * JVM implementation of currentTimeMillis using System.currentTimeMillis().
 */
actual fun currentTimeMillis(): Long = System.currentTimeMillis()

/**
 * Sets timeout.
 *
 * @param delayMs The delay ms value.
 * @param callback The callback value.
 * @return The resulting value.
 */
actual fun setTimeout(delayMs: Int, callback: () -> Unit): Int {
    val id = idGenerator.incrementAndGet()
    val timer = Timer(true) // Daemon timer
    timer.schedule(object : TimerTask() {
        override fun run() {
            callback()
            timers.remove(id)
        }
    }, delayMs.toLong())

    timers[id] = timer
    return id
}

/**
 * Clears timeout.
 *
 * @param id Stable identifier.
 */
actual fun clearTimeout(id: Int) {
    timers[id]?.let { timer ->
        timer.cancel()
        timers.remove(id)
    }
} 