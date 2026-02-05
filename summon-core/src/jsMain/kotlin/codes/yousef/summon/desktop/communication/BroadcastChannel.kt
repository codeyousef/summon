package codes.yousef.summon.desktop.communication

import org.w3c.dom.MessageEvent

/**
 * External declaration for the BroadcastChannel Web API.
 */
external class BroadcastChannel(name: String) {
    val name: String
    fun postMessage(message: dynamic)
    fun close()
    var onmessage: ((MessageEvent) -> Unit)?
    var onmessageerror: ((MessageEvent) -> Unit)?
}

/**
 * JS implementation of SummonBroadcastChannel using the BroadcastChannel API.
 */
actual fun createBroadcastChannel(name: String): SummonBroadcastChannel<String> =
    JsBroadcastChannel(name)

/**
 * JavaScript/Browser implementation using the native BroadcastChannel API.
 */
private class JsBroadcastChannel(
    override val name: String
) : SummonBroadcastChannel<String> {

    private val channel = BroadcastChannel(name)
    private val handlers = mutableListOf<(String) -> Unit>()
    private var open = true

    init {
        channel.onmessage = { event: MessageEvent ->
            val data = event.data
            if (data != null) {
                val message = data.toString()
                handlers.forEach { handler ->
                    try {
                        handler(message)
                    } catch (e: Exception) {
                        console.error("Error in BroadcastChannel handler: ${e.message}")
                    }
                }
            }
        }

        channel.onmessageerror = { event: MessageEvent ->
            console.error("BroadcastChannel message error on channel '$name'")
        }
    }

    override fun postMessage(message: String) {
        if (!open) {
            console.warn("Cannot post message to closed BroadcastChannel '$name'")
            return
        }
        channel.postMessage(message)
    }

    override fun onMessage(handler: (String) -> Unit): () -> Unit {
        handlers.add(handler)
        return {
            handlers.remove(handler)
        }
    }

    override fun close() {
        if (open) {
            open = false
            channel.close()
            handlers.clear()
        }
    }

    override fun isOpen(): Boolean = open
}
