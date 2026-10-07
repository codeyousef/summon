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
    private val storageListener: (Event) -> Unit = { event ->
        val storageEvent = event as? StorageEvent
        if (storageEvent?.key == key) {
            val newValue = storageEvent.newValue?.let {
                try {
                    deserializer(it)
                } catch (_: Exception) {
                    defaultValue
                }
            } ?: defaultValue
            notifyListeners(newValue)
        }
    }

    override var value: T
        get() {
            val stored = localStorage.getItem(key)
            return if (stored != null) {
                try {
                    deserializer(stored)
                } catch (_: Exception) {
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
                notifyListeners(newValue)
            } catch (_: Exception) {
                // Storage capability and serialization failures leave the prior value unchanged.
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
        if (listeners.isEmpty()) window.addEventListener("storage", storageListener)
        listeners.add(listener)
        var subscribed = true
        return {
            if (subscribed) {
                subscribed = false
                listeners.remove(listener)
                if (listeners.isEmpty()) window.removeEventListener("storage", storageListener)
            }
        }
    }

    private fun notifyListeners(newValue: T) {
        listeners.toList().forEach { listener ->
            try {
                listener(newValue)
            } catch (_: Exception) {
                // A failing observer must not prevent delivery to other owners.
            }
        }
    }

}
