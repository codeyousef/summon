package codes.yousef.summon.desktop.communication

import codes.yousef.summon.runtime.wasmConsoleError
import codes.yousef.summon.runtime.wasmConsoleWarn

/**
 * External declarations for BroadcastChannel API in WASM.
 */
@JsName("BroadcastChannel")
external class WasmBroadcastChannel(name: String) : JsAny {
    val name: String
    fun postMessage(message: JsAny?)
    fun close()
}

/**
 * External interface for MessageEvent in WASM.
 */
external interface WasmMessageEvent : JsAny {
    val data: JsAny?
}

/**
 * JavaScript helper function to set onmessage handler.
 */
@JsFun("(channel, handler) => { channel.onmessage = (e) => handler(e); }")
external fun setOnMessage(channel: JsAny, handler: (WasmMessageEvent) -> Unit)

/**
 * JavaScript helper function to convert string to JsAny.
 */
@JsFun("(str) => str")
external fun stringToJsAny(str: String): JsAny

/**
 * JavaScript helper function to convert JsAny to string.
 */
@JsFun("(val) => String(val)")
external fun jsAnyToString(value: JsAny?): String

/**
 * WASM implementation of SummonBroadcastChannel using the BroadcastChannel API.
 */
actual fun createBroadcastChannel(name: String): SummonBroadcastChannel<String> =
    WasmBroadcastChannelImpl(name)

/**
 * WebAssembly implementation using the native BroadcastChannel API.
 */
private class WasmBroadcastChannelImpl(
    override val name: String
) : SummonBroadcastChannel<String> {

    private val channel: WasmBroadcastChannel
    private val handlers = mutableListOf<(String) -> Unit>()
    private var open = true

    init {
        channel = try {
            WasmBroadcastChannel(name)
        } catch (e: Exception) {
            wasmConsoleError("Failed to create BroadcastChannel: ${e.message}")
            throw e
        }

        try {
            setOnMessage(channel) { event: WasmMessageEvent ->
                val data = event.data
                if (data != null) {
                    val message = jsAnyToString(data)
                    handlers.forEach { handler ->
                        try {
                            handler(message)
                        } catch (e: Exception) {
                            wasmConsoleError("Error in BroadcastChannel handler: ${e.message}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            wasmConsoleError("Failed to set up message handler: ${e.message}")
        }
    }

    override fun postMessage(message: String) {
        if (!open) {
            wasmConsoleWarn("Cannot post message to closed BroadcastChannel '$name'")
            return
        }
        try {
            channel.postMessage(stringToJsAny(message))
        } catch (e: Exception) {
            wasmConsoleError("Failed to post message: ${e.message}")
        }
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
            try {
                channel.close()
            } catch (e: Exception) {
                wasmConsoleError("Failed to close channel: ${e.message}")
            }
            handlers.clear()
        }
    }

    override fun isOpen(): Boolean = open
}
