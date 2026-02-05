package codes.yousef.summon.desktop.storage

import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.StorageEvent
import org.w3c.dom.events.Event

/**
 * JS implementation of SyncedStorage using localStorage and storage events.
 */
actual fun <T> createSyncedStorage(
    key: String,
    defaultValue: T,
    serializer: (T) -> String,
    deserializer: (String) -> T
): SyncedStorage<T> = JsSyncedStorage(key, defaultValue, serializer, deserializer)

/**
 * JavaScript/Browser implementation of SyncedStorage.
 *
 * Uses localStorage for persistence and the 'storage' event for cross-tab synchronization.
 * Note: The storage event only fires in OTHER tabs, not the tab that made the change.
 */
private class JsSyncedStorage<T>(
    private val key: String,
    private val defaultValue: T,
    private val serializer: (T) -> String,
    private val deserializer: (String) -> T
) : SyncedStorage<T> {

    private val listeners = mutableListOf<(T) -> Unit>()
    private var storageListener: ((Event) -> Unit)? = null

    init {
        // Set up storage event listener for cross-tab sync
        storageListener = { event: Event ->
            val storageEvent = event as? StorageEvent
            if (storageEvent?.key == key) {
                val newValue = storageEvent.newValue?.let { deserializer(it) } ?: defaultValue
                notifyListeners(newValue)
            }
        }
        window.addEventListener("storage", storageListener!!)
    }

    override var value: T
        get() {
            val stored = localStorage.getItem(key)
            return if (stored != null) {
                try {
                    deserializer(stored)
                } catch (e: Exception) {
                    console.error("Failed to deserialize stored value for key '$key': ${e.message}")
                    defaultValue
                }
            } else {
                defaultValue
            }
        }
        set(newValue) {
            try {
                val serialized = serializer(newValue)
                localStorage.setItem(key, serialized)
                // Storage event doesn't fire in the same tab, so manually notify
                notifyListeners(newValue)
            } catch (e: Exception) {
                console.error("Failed to serialize value for key '$key': ${e.message}")
            }
        }

    override fun clear() {
        localStorage.removeItem(key)
        notifyListeners(defaultValue)
    }

    override fun exists(): Boolean {
        return localStorage.getItem(key) != null
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
                console.error("Error in SyncedStorage listener: ${e.message}")
            }
        }
    }

    /**
     * Cleanup function to remove the storage event listener.
     * Should be called when the storage is no longer needed.
     */
    fun dispose() {
        storageListener?.let { listener ->
            window.removeEventListener("storage", listener)
        }
        storageListener = null
        listeners.clear()
    }
}
