package codes.yousef.summon.desktop.storage

import codes.yousef.summon.runtime.wasmConsoleError

/**
 * External declarations for localStorage and storage events in WASM.
 */
@JsName("localStorage")
external object WasmLocalStorage {
    fun getItem(key: String): String?
    fun setItem(key: String, value: String)
    fun removeItem(key: String)
}

@JsName("window")
external object WasmWindow {
    fun addEventListener(type: String, callback: (JsAny) -> Unit)
    fun removeEventListener(type: String, callback: (JsAny) -> Unit)
}

/**
 * External interface for StorageEvent.
 */
external interface WasmStorageEvent : JsAny {
    val key: String?
    val newValue: String?
    val oldValue: String?
}

/**
 * WASM implementation of SyncedStorage using localStorage and storage events.
 */
actual fun <T> createSyncedStorage(
    key: String,
    defaultValue: T,
    serializer: (T) -> String,
    deserializer: (String) -> T
): SyncedStorage<T> = WasmSyncedStorage(key, defaultValue, serializer, deserializer)

/**
 * WebAssembly implementation of SyncedStorage.
 *
 * Uses localStorage for persistence and the 'storage' event for cross-tab synchronization.
 */
private class WasmSyncedStorage<T>(
    private val key: String,
    private val defaultValue: T,
    private val serializer: (T) -> String,
    private val deserializer: (String) -> T
) : SyncedStorage<T> {

    private val listeners = mutableListOf<(T) -> Unit>()
    private var storageCallback: ((JsAny) -> Unit)? = null

    init {
        try {
            // Set up storage event listener for cross-tab sync
            storageCallback = { event: JsAny ->
                try {
                    val storageEvent = event.unsafeCast<WasmStorageEvent>()
                    if (storageEvent.key == key) {
                        val newValue = storageEvent.newValue?.let {
                            try {
                                deserializer(it)
                            } catch (e: Exception) {
                                wasmConsoleError("Failed to deserialize in storage event: ${e.message}")
                                defaultValue
                            }
                        } ?: defaultValue
                        notifyListeners(newValue)
                    }
                } catch (e: Exception) {
                    wasmConsoleError("Error handling storage event: ${e.message}")
                }
            }
            WasmWindow.addEventListener("storage", storageCallback!!)
        } catch (e: Exception) {
            wasmConsoleError("Failed to set up storage listener: ${e.message}")
        }
    }

    override var value: T
        get() {
            return try {
                val stored = WasmLocalStorage.getItem(key)
                if (stored != null) {
                    try {
                        deserializer(stored)
                    } catch (e: Exception) {
                        wasmConsoleError("Failed to deserialize stored value for key '$key': ${e.message}")
                        defaultValue
                    }
                } else {
                    defaultValue
                }
            } catch (e: Exception) {
                wasmConsoleError("Failed to get item from localStorage: ${e.message}")
                defaultValue
            }
        }
        set(newValue) {
            try {
                val serialized = serializer(newValue)
                WasmLocalStorage.setItem(key, serialized)
                // Storage event doesn't fire in the same tab, so manually notify
                notifyListeners(newValue)
            } catch (e: Exception) {
                wasmConsoleError("Failed to set value for key '$key': ${e.message}")
            }
        }

    override fun clear() {
        try {
            WasmLocalStorage.removeItem(key)
            notifyListeners(defaultValue)
        } catch (e: Exception) {
            wasmConsoleError("Failed to remove item from localStorage: ${e.message}")
        }
    }

    override fun exists(): Boolean {
        return try {
            WasmLocalStorage.getItem(key) != null
        } catch (e: Exception) {
            false
        }
    }

    override fun addChangeListener(listener: (T) -> Unit): () -> Unit {
        listeners.add(listener)
        return {
            listeners.remove(listener)
        }
    }

    private fun notifyListeners(newValue: T) {
        listeners.forEach { listener ->
            try {
                listener(newValue)
            } catch (e: Exception) {
                wasmConsoleError("Error in SyncedStorage listener: ${e.message}")
            }
        }
    }

    /**
     * Cleanup function to remove the storage event listener.
     */
    fun dispose() {
        storageCallback?.let { callback ->
            try {
                WasmWindow.removeEventListener("storage", callback)
            } catch (e: Exception) {
                wasmConsoleError("Failed to remove storage listener: ${e.message}")
            }
        }
        storageCallback = null
        listeners.clear()
    }
}
