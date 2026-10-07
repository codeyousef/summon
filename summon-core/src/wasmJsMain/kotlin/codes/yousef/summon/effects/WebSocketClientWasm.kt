package codes.yousef.summon.effects

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@JsFun("(size) => new Uint8Array(size)")
private external fun wasmSocketBytes(size: Int): JsAny

@JsFun("(bytes, index, value) => { bytes[index] = value; }")
private external fun wasmSetSocketByte(bytes: JsAny, index: Int, value: Int)

@JsFun("(handle, message) => handle.sendText(message)")
private external fun wasmSocketSendText(handle: JsAny, message: String): Boolean

@JsFun("(handle, bytes) => handle.sendBytes(bytes)")
private external fun wasmSocketSendBytes(handle: JsAny, bytes: JsAny): Boolean

@JsFun("(handle, url, protocols) => handle.connect(url, protocols)")
private external fun wasmSocketConnect(handle: JsAny, url: String, protocols: String)

@JsFun("(handle) => handle.pause()")
private external fun wasmSocketPause(handle: JsAny)

@JsFun("(handle) => handle.resume()")
private external fun wasmSocketResume(handle: JsAny)

@JsFun("(handle, code, reason) => handle.close(code, reason)")
private external fun wasmSocketClose(handle: JsAny, code: Int, reason: String)

@JsFun("(handle) => handle.dispose()")
private external fun wasmSocketDispose(handle: JsAny)

@JsFun("(handle) => handle.state()")
private external fun wasmSocketState(handle: JsAny): Int

@JsFun(
    """(url, protocolsText, autoReconnect, reconnectDelay, maxReconnectDelay, maxReconnectAttempts,
        pingInterval, maxMessageBytes, pauseWhenOffline, pauseWhenHidden, callback) => {
        let socket = null, disposed = false, paused = false, generation = 0, attempts = 0;
        let reconnectTimer = null, pingTimer = null;
        let currentUrl = url, currentProtocols = protocolsText;
        const protocols = () => currentProtocols ? currentProtocols.split('\u001f') : [];
        const clearTimers = () => {
            if (reconnectTimer !== null) clearTimeout(reconnectTimer);
            if (pingTimer !== null) clearInterval(pingTimer);
            reconnectTimer = pingTimer = null;
        };
        const schedule = expectedGeneration => {
            if (!autoReconnect || paused || disposed || attempts >= maxReconnectAttempts) return;
            const delay = Math.min(maxReconnectDelay, reconnectDelay * Math.pow(2, Math.min(attempts++, 20)));
            reconnectTimer = setTimeout(() => {
                reconnectTimer = null;
                if (generation === expectedGeneration && !paused && !disposed) open();
            }, delay);
        };
        const open = () => {
            if (disposed || paused || (pauseWhenOffline && !navigator.onLine) ||
                (pauseWhenHidden && document.visibilityState === 'hidden')) return;
            const expectedGeneration = generation;
            try {
                socket = currentProtocols ? new WebSocket(currentUrl, protocols()) : new WebSocket(currentUrl);
                socket.onopen = () => {
                    if (expectedGeneration !== generation || disposed || paused) return;
                    attempts = 0;
                    if (pingInterval > 0) {
                        pingTimer = setInterval(() => {
                            if (expectedGeneration === generation && socket && socket.readyState === WebSocket.OPEN) {
                                socket.send('ping');
                            }
                        }, pingInterval);
                    }
                    callback(0, '');
                };
                socket.onmessage = event => {
                    if (expectedGeneration !== generation || disposed) return;
                    if (typeof event.data !== 'string' || new TextEncoder().encode(event.data).length > maxMessageBytes) {
                        callback(2, 'message-too-large');
                    } else {
                        callback(1, event.data);
                    }
                };
                socket.onerror = () => {
                    if (expectedGeneration === generation && !disposed) callback(2, 'connection');
                };
                socket.onclose = event => {
                    if (expectedGeneration !== generation || disposed) return;
                    if (pingTimer !== null) clearInterval(pingTimer);
                    pingTimer = null;
                    socket = null;
                    callback(3, '');
                    if (!paused && event.code !== 1000) schedule(expectedGeneration);
                };
            } catch (_) {
                callback(2, 'connection');
                schedule(expectedGeneration);
            }
        };
        const pause = () => {
            if (disposed || paused) return;
            paused = true; generation++; clearTimers();
            if (socket) socket.close(1000, '');
            socket = null;
        };
        const resume = () => {
            if (disposed || !paused || (pauseWhenOffline && !navigator.onLine) ||
                (pauseWhenHidden && document.visibilityState === 'hidden')) return;
            paused = false; generation++; open();
        };
        const online = () => { if (pauseWhenOffline) resume(); };
        const offline = () => { if (pauseWhenOffline) pause(); };
        const visibility = () => {
            if (pauseWhenHidden) document.visibilityState === 'hidden' ? pause() : resume();
        };
        addEventListener('online', online);
        addEventListener('offline', offline);
        document.addEventListener('visibilitychange', visibility);
        const handle = {
            connect(nextUrl, nextProtocols) {
                generation++; clearTimers();
                if (socket) socket.close(1000, '');
                socket = null; paused = false; currentUrl = nextUrl; currentProtocols = nextProtocols; open();
            },
            sendText(message) {
                if (!socket || socket.readyState !== WebSocket.OPEN) return false;
                socket.send(message); return true;
            },
            sendBytes(bytes) {
                if (!socket || socket.readyState !== WebSocket.OPEN) return false;
                socket.send(bytes); return true;
            },
            pause, resume,
            close(code, reason) {
                generation++; paused = false; clearTimers();
                if (socket) socket.close(code, reason);
                socket = null;
            },
            dispose() {
                if (disposed) return;
                disposed = true; generation++; clearTimers();
                if (socket) socket.close(1000, '');
                socket = null;
                removeEventListener('online', online);
                removeEventListener('offline', offline);
                document.removeEventListener('visibilitychange', visibility);
            },
            state() {
                if (paused) return 4;
                return socket ? socket.readyState : 3;
            }
        };
        open();
        return handle;
    }"""
)
private external fun wasmCreateSocket(
    url: String,
    protocols: String,
    autoReconnect: Boolean,
    reconnectDelay: Double,
    maxReconnectDelay: Double,
    maxReconnectAttempts: Int,
    pingInterval: Double,
    maxMessageBytes: Int,
    pauseWhenOffline: Boolean,
    pauseWhenHidden: Boolean,
    callback: (Int, String) -> Unit
): JsAny

/** Represents web socket client. */
actual class WebSocketClient {
    private lateinit var config: WebSocketConfig
    private lateinit var scope: CoroutineScope
    private lateinit var events: Channel<WebSocketEvent>
    private lateinit var handle: JsAny
    private var eventHandler: ((WebSocketEvent) -> Unit)? = null
    private var disposed = false

    internal fun initialize(config: WebSocketConfig, scope: CoroutineScope) {
        this.config = config
        this.scope = scope
        events = Channel(config.maxQueuedEvents)
        scope.launch {
            while (isActive) {
                val event = events.receive()
                while (isActive && eventHandler == null) delay(1)
                eventHandler?.invoke(event)
            }
        }
        handle = wasmCreateSocket(
            config.url,
            config.protocols.joinToString("\u001f"),
            config.autoReconnect,
            config.reconnectDelay.toDouble(),
            config.maxReconnectDelay.toDouble(),
            config.maxReconnectAttempts,
            config.pingInterval.toDouble(),
            config.maxMessageBytes,
            config.pauseWhenOffline,
            config.pauseWhenHidden,
            ::receive
        )
        scope.coroutineContext[kotlinx.coroutines.Job]?.invokeOnCompletion { dispose() }
    }

    private fun receive(kind: Int, data: String) {
        if (disposed) return
        val event = when (kind) {
            0 -> WebSocketEvent.Connected
            1 -> if (config.payloadPolicy == WebSocketPayloadPolicy.OPAQUE_HINT) {
                if (data.length <= 256 && data.all { it.isLetterOrDigit() || it in "._:-" }) {
                    WebSocketEvent.Hint(data)
                } else {
                    WebSocketEvent.Error(WebSocketErrorCode.INVALID_HINT)
                }
            } else {
                WebSocketEvent.Message(data)
            }
            2 -> WebSocketEvent.Error(
                if (data == "message-too-large") WebSocketErrorCode.MESSAGE_TOO_LARGE
                else WebSocketErrorCode.CONNECTION
            )
            else -> WebSocketEvent.Disconnected
        }
        if (!events.trySend(event).isSuccess) {
            eventHandler?.invoke(WebSocketEvent.Error(WebSocketErrorCode.QUEUE_OVERFLOW))
        }
    }

    /**
     * Executes the connect operation.
     *
     * @param url Target URL.
     * @param protocols The protocols value.
     */
    actual fun connect(url: String, protocols: List<String>) {
        config = config.copy(url = url, protocols = protocols)
        wasmSocketConnect(handle, url, protocols.joinToString("\u001f"))
    }

    /**
     * Sends the supplied value.
     *
     * @param message Message content.
     */
    actual fun send(message: String) {
        if (message.encodeToByteArray().size > config.maxMessageBytes) {
            receive(2, "message-too-large")
        } else if (!wasmSocketSendText(handle, message)) {
            eventHandler?.invoke(WebSocketEvent.Error(WebSocketErrorCode.NOT_CONNECTED))
        }
    }

    /**
     * Sends the supplied value.
     *
     * @param data The data value.
     */
    actual fun send(data: ByteArray) {
        if (data.size > config.maxMessageBytes) {
            receive(2, "message-too-large")
            return
        }
        val bytes = wasmSocketBytes(data.size)
        data.forEachIndexed { index, byte -> wasmSetSocketByte(bytes, index, byte.toInt() and 0xff) }
        if (!wasmSocketSendBytes(handle, bytes)) {
            eventHandler?.invoke(WebSocketEvent.Error(WebSocketErrorCode.NOT_CONNECTED))
        }
    }

    /** Pauses the operation. */
    actual fun pause() = wasmSocketPause(handle)

    /** Resumes the operation. */
    actual fun resume() = wasmSocketResume(handle)

    /**
     * Closes the operation.
     *
     * @param code The code value.
     * @param reason The reason value.
     */
    actual fun close(code: Int, reason: String) = wasmSocketClose(handle, code, reason)

    /** Disposes the operation. */
    actual fun dispose() {
        if (disposed) return
        disposed = true
        wasmSocketDispose(handle)
        events.close()
        eventHandler = null
    }

    /** The property declaration value. */
    actual val state: WebSocketState
        get() = when (wasmSocketState(handle)) {
            0 -> WebSocketState.CONNECTING
            1 -> WebSocketState.OPEN
            2 -> WebSocketState.CLOSING
            4 -> WebSocketState.PAUSED
            else -> WebSocketState.CLOSED
        }

    /** The property declaration value. */
    actual val isConnected: Boolean
        get() = !disposed && wasmSocketState(handle) == 1

    /**
     * Handles event.
     *
     * @param handler The handler value.
     */
    actual fun onEvent(handler: (WebSocketEvent) -> Unit) {
        eventHandler = handler
    }
}

/**
 * Creates web socket.
 *
 * @param config The config value.
 * @param scope Coroutine or composition scope.
 * @return The resulting value.
 */
actual fun createWebSocket(config: WebSocketConfig, scope: CoroutineScope): WebSocketClient =
    WebSocketClient().also { it.initialize(config, scope) }

