package code.yousef.summon.effects

import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private val timers = ConcurrentHashMap<Int, Timer>()
private val idGenerator = AtomicInteger(0)

/**
 * JVM implementation of getCurrentTimeMillis using System.currentTimeMillis().
 */
actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

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

actual fun clearTimeout(id: Int) {
    timers[id]?.let { timer ->
        timer.cancel()
        timers.remove(id)
    }
} 