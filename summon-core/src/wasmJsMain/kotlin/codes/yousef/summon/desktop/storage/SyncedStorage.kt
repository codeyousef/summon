package codes.yousef.summon.desktop.storage

import codes.yousef.summon.runtime.wasmConsoleError

/**
 * External declarations for localStorage and storage events in WASM.
 */
@JsName("localStorage")
/** Provides wasm local storage operations. */
external object WasmLocalStorage {
    /**
     * Returns item.
     *
     * @param key Lookup key.
     * @return The resulting value.
     */
    fun getItem(key: String): String?
    /**
     * Sets item.
     *
     * @param key Lookup key.
     * @param value Value to process.
     */
    fun setItem(key: String, value: String)
    /**
     * Removes item.
     *
     * @param key Lookup key.
     */
    fun removeItem(key: String)
}

/** Provides WASM window operations. */
@JsName("window")
/** Provides wasm window operations. */
external object WasmWindow {
    /**
     * Adds event listener.
     *
     * @param type The type value.
     * @param callback The callback value.
     */
    fun addEventListener(type: String, callback: (JsAny) -> Unit)
    /**
     * Removes event listener.
     *
     * @param type The type value.
     * @param callback The callback value.
     */
    fun removeEventListener(type: String, callback: (JsAny) -> Unit)
}

/**
 * External interface for StorageEvent.
 */
external interface WasmStorageEvent : JsAny {
    /** The property declaration value. */
    val key: String?
    /** The property declaration value. */
    val newValue: String?
    /** The property declaration value. */
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
    private var storageListenerAttached = false
    private val storageCallback: (JsAny) -> Unit = { event ->
        try {
            val storageEvent = event.unsafeCast<WasmStorageEvent>()
            if (storageEvent.key == key) {
                val newValue = storageEvent.newValue?.let {
                    try {
                        deserializer(it)
                    } catch (_: Exception) {
                        defaultValue
                    }
                } ?: defaultValue
                notifyListeners(newValue)
            }
        } catch (_: Exception) {
            wasmConsoleError("Synced storage event handling failed")
        }
    }

    override var value: T
        get() {
            return try {
                val stored = WasmLocalStorage.getItem(key)
                if (stored != null) {
                    try {
                        deserializer(stored)
                    } catch (_: Exception) {
                        wasmConsoleError("Synced storage deserialization failed")
                        defaultValue
                    }
                } else {
                    defaultValue
                }
            } catch (_: Exception) {
                wasmConsoleError("Synced storage read failed")
                defaultValue
            }
        }
        set(newValue) {
            try {
                val serialized = serializer(newValue)
                WasmLocalStorage.setItem(key, serialized)
                // Storage event doesn't fire in the same tab, so manually notify
                notifyListeners(newValue)
            } catch (_: Exception) {
                wasmConsoleError("Synced storage write failed")
            }
        }

    override fun clear() {
        try {
            WasmLocalStorage.removeItem(key)
            notifyListeners(defaultValue)
        } catch (_: Exception) {
            wasmConsoleError("Synced storage removal failed")
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
        if (!storageListenerAttached) {
            try {
                WasmWindow.addEventListener("storage", storageCallback)
                storageListenerAttached = true
            } catch (_: Exception) {
                wasmConsoleError("Synced storage listener registration failed")
            }
        }
        listeners.add(listener)
        var subscribed = true
        return {
            if (subscribed) {
                subscribed = false
                listeners.remove(listener)
                if (listeners.isEmpty() && storageListenerAttached) {
                    try {
                        WasmWindow.removeEventListener("storage", storageCallback)
                    } catch (_: Exception) {
                        wasmConsoleError("Synced storage listener removal failed")
                    }
                    storageListenerAttached = false
                }
            }
        }
    }

    private fun notifyListeners(newValue: T) {
        listeners.toList().forEach { listener ->
            try {
                listener(newValue)
            } catch (_: Exception) {
                wasmConsoleError("Synced storage observer failed")
            }
        }
    }

}
