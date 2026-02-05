@file:JvmName("BroadcastChannelJvm")

package codes.yousef.summon.desktop.communication

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * JVM implementation of SummonBroadcastChannel.
 *
 * On JVM (server-side), there's no native BroadcastChannel API.
 * This implementation provides an in-process event bus that allows
 * communication between different parts of the same JVM application.
 *
 * Note: This does NOT provide cross-process communication.
 * For true cross-process messaging on JVM, consider using:
 * - Redis Pub/Sub
 * - Message queues (RabbitMQ, Kafka)
 * - WebSockets with a message broker
 */
actual fun createBroadcastChannel(name: String): SummonBroadcastChannel<String> =
    JvmBroadcastChannel(name)

/**
 * JVM/Server-side implementation using an in-process event bus.
 */
private class JvmBroadcastChannel(
    override val name: String
) : SummonBroadcastChannel<String> {

    companion object {
        // Global channel registry for in-process communication
        private val channels = ConcurrentHashMap<String, CopyOnWriteArrayList<(String) -> Unit>>()
    }

    private val localHandlers = CopyOnWriteArrayList<(String) -> Unit>()

    @Volatile
    private var open = true

    init {
        // Register in the global channel list
        channels.getOrPut(name) { CopyOnWriteArrayList() }
    }

    override fun postMessage(message: String) {
        if (!open) {
            println("Warning: Cannot post message to closed BroadcastChannel '$name'")
            return
        }

        // Get all handlers for this channel name
        val channelHandlers = channels[name] ?: return

        // Send to all handlers EXCEPT our own local handlers (mimics browser behavior)
        channelHandlers.forEach { handler ->
            if (handler !in localHandlers) {
                try {
                    handler(message)
                } catch (e: Exception) {
                    println("Error in BroadcastChannel handler: ${e.message}")
                }
            }
        }
    }

    override fun onMessage(handler: (String) -> Unit): () -> Unit {
        localHandlers.add(handler)

        // Also add to global channel
        channels[name]?.add(handler)

        return {
            localHandlers.remove(handler)
            channels[name]?.remove(handler)
        }
    }

    override fun close() {
        if (open) {
            open = false

            // Remove all local handlers from global channel
            val channelHandlers = channels[name]
            localHandlers.forEach { handler ->
                channelHandlers?.remove(handler)
            }
            localHandlers.clear()

            // Clean up empty channels
            if (channelHandlers?.isEmpty() == true) {
                channels.remove(name)
            }
        }
    }

    override fun isOpen(): Boolean = open
}
