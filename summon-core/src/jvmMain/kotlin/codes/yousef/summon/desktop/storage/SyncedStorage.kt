@file:JvmName("SyncedStorageJvm")

package codes.yousef.summon.desktop.storage

/**
 * JVM implementation of SyncedStorage.
 *
 * On JVM (server-side), there's no localStorage or cross-tab sync capability.
 * This implementation uses an in-memory store, which is useful for:
 * - Testing
 * - SSR initial state
 * - Server-side applications with single-instance state
 *
 * Note: Changes are NOT persisted across JVM restarts and there's no
 * cross-process synchronization.
 */
actual fun <T> createSyncedStorage(
    key: String,
    defaultValue: T,
    serializer: (T) -> String,
    deserializer: (String) -> T
): SyncedStorage<T> = JvmSyncedStorage(key, defaultValue, serializer, deserializer)

/**
 * JVM/Server-side implementation of SyncedStorage.
 *
 * Uses an in-memory store with thread-safe access.
 */
private class JvmSyncedStorage<T>(
    private val key: String,
    private val defaultValue: T,
    private val serializer: (T) -> String,
    private val deserializer: (String) -> T
) : SyncedStorage<T> {

    companion object {
        // Global in-memory store shared across all JvmSyncedStorage instances
        private val store = mutableMapOf<String, String>()
        private val listeners = mutableMapOf<String, MutableList<(String?) -> Unit>>()
        private val lock = Any()
    }

    private val localListeners = mutableListOf<(T) -> Unit>()

    init {
        // Register for global notifications
        synchronized(lock) {
            listeners.getOrPut(key) { mutableListOf() }.add { serializedValue ->
                val newValue = if (serializedValue != null) {
                    try {
                        deserializer(serializedValue)
                    } catch (e: Exception) {
                        defaultValue
                    }
                } else {
                    defaultValue
                }
                notifyLocalListeners(newValue)
            }
        }
    }

    override var value: T
        get() {
            synchronized(lock) {
                val stored = store[key]
                return if (stored != null) {
                    try {
                        deserializer(stored)
                    } catch (e: Exception) {
                        defaultValue
                    }
                } else {
                    defaultValue
                }
            }
        }
        set(newValue) {
            synchronized(lock) {
                try {
                    val serialized = serializer(newValue)
                    store[key] = serialized
                    // Notify all listeners (including other instances with the same key)
                    listeners[key]?.forEach { listener ->
                        listener(serialized)
                    }
                } catch (e: Exception) {
                    // Log error in production
                }
            }
        }

    override fun clear() {
        synchronized(lock) {
            store.remove(key)
            listeners[key]?.forEach { listener ->
                listener(null)
            }
        }
    }

    override fun exists(): Boolean {
        synchronized(lock) {
            return store.containsKey(key)
        }
    }

    override fun addChangeListener(listener: (T) -> Unit): () -> Unit {
        localListeners.add(listener)
        return {
            localListeners.remove(listener)
        }
    }

    private fun notifyLocalListeners(newValue: T) {
        localListeners.forEach { listener ->
            try {
                listener(newValue)
            } catch (e: Exception) {
                // Swallow exceptions in listeners
            }
        }
    }
}
