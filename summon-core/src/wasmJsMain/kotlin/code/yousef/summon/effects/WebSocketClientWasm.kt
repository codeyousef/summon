package code.yousef.summon.effects

import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.runtime.wasmConsoleWarn
import code.yousef.summon.runtime.wasmConsoleError

actual class WebSocketClient {
    private var _state: WebSocketState = WebSocketState.CLOSED
    private var eventHandler: ((WebSocketEvent) -> Unit)? = null

    actual val state: WebSocketState get() = _state
    actual val isConnected: Boolean get() = _state == WebSocketState.OPEN

    actual fun connect(url: String, protocols: List<String>) {
        wasmConsoleLog("WebSocket connecting to: $url with protocols: $protocols - WASM stub")
        _state = WebSocketState.CONNECTING
        // Simulate successful connection
        _state = WebSocketState.OPEN
        eventHandler?.invoke(WebSocketEvent.Connected)
    }

    actual fun send(message: String) {
        wasmConsoleLog("WebSocket sending message: $message - WASM stub")
        if (_state != WebSocketState.OPEN) {
            wasmConsoleWarn("WebSocket is not connected")
        }
    }

    actual fun send(data: ByteArray) {
        wasmConsoleLog("WebSocket sending binary data (${data.size} bytes) - WASM stub")
        if (_state != WebSocketState.OPEN) {
            wasmConsoleWarn("WebSocket is not connected")
        }
    }

    actual fun close(code: Int, reason: String) {
        wasmConsoleLog("WebSocket closing with code: $code, reason: $reason - WASM stub")
        _state = WebSocketState.CLOSING
        _state = WebSocketState.CLOSED
        eventHandler?.invoke(WebSocketEvent.Disconnected)
    }

    actual fun onEvent(handler: (WebSocketEvent) -> Unit) {
        wasmConsoleLog("WebSocket event handler set - WASM stub")
        this.eventHandler = handler
    }
}

actual fun createWebSocket(config: WebSocketConfig): WebSocketClient {
    wasmConsoleLog("Creating WebSocket with config: ${config.url} - WASM stub")
    val client = WebSocketClient()
    client.connect(config.url, config.protocols)
    return client
}

