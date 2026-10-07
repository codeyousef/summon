package codes.yousef.summon.effects

import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage
import org.w3c.dom.Storage as DomStorage

/**
 * JavaScript Storage implementation wrapping browser storage APIs

 * @property domStorage The dom storage value.
 */
actual class Storage(private val domStorage: DomStorage?) {

    // In-memory fallback for when DOM storage is not available
    private val memoryFallback = mutableMapOf<String, String>()
    private val useMemoryFallback = domStorage == null

    /**
     * Sets item.
     *
     * @param key Lookup key.
     * @param value Value to process.
     */
    actual fun setItem(key: String, value: String) {
        try {
            if (useMemoryFallback) {
                memoryFallback[key] = value
            } else {
                domStorage?.setItem(key, value)
            }
        } catch (e: Exception) {
            // Storage quota exceeded or other error, fallback to memory
            memoryFallback[key] = value
        }
    }

    /**
     * Returns item.
     *
     * @param key Lookup key.
     * @return The resulting value.
     */
    actual fun getItem(key: String): String? {
        return try {
            if (useMemoryFallback) {
                memoryFallback[key]
            } else {
                domStorage?.getItem(key)
            }
        } catch (e: Exception) {
            memoryFallback[key]
        }
    }

    /**
     * Removes item.
     *
     * @param key Lookup key.
     */
    actual fun removeItem(key: String) {
        try {
            if (useMemoryFallback) {
                memoryFallback.remove(key)
            } else {
                domStorage?.removeItem(key)
            }
        } catch (e: Exception) {
            memoryFallback.remove(key)
        }
    }

    /** Clears the operation. */
    actual fun clear() {
        try {
            if (useMemoryFallback) {
                memoryFallback.clear()
            } else {
                domStorage?.clear()
            }
        } catch (e: Exception) {
            memoryFallback.clear()
        }
    }

    /**
     * Executes the keys operation.
     *
     * @return The resulting value.
     */
    actual fun keys(): List<String> {
        return try {
            if (useMemoryFallback) {
                memoryFallback.keys.toList()
            } else {
                domStorage?.let { storage ->
                    (0 until storage.length).mapNotNull { index ->
                        storage.key(index)
                    }
                } ?: emptyList()
            }
        } catch (e: Exception) {
            memoryFallback.keys.toList()
        }
    }

    /**
     * Executes the length operation.
     *
     * @return The resulting value.
     */
    actual fun length(): Int {
        return try {
            if (useMemoryFallback) {
                memoryFallback.size
            } else {
                domStorage?.length ?: 0
            }
        } catch (e: Exception) {
            memoryFallback.size
        }
    }

    /**
     * Executes the contains operation.
     *
     * @param key Lookup key.
     * @return The resulting value.
     */
    actual fun contains(key: String): Boolean {
        return getItem(key) != null
    }
}

/**
 * In-memory storage implementation
 */
class MemoryStorage {
    private val memoryMap = mutableMapOf<String, String>()

    /**
     * Sets item.
     *
     * @param key Lookup key.
     * @param value Value to process.
     */
    fun setItem(key: String, value: String) {
        memoryMap[key] = value
    }

    /**
     * Returns item.
     *
     * @param key Lookup key.
     * @return The resulting value.
     */
    fun getItem(key: String): String? {
        return memoryMap[key]
    }

    /**
     * Removes item.
     *
     * @param key Lookup key.
     */
    fun removeItem(key: String) {
        memoryMap.remove(key)
    }

    /** Clears the operation. */
    fun clear() {
        memoryMap.clear()
    }

    /**
     * Executes the keys operation.
     *
     * @return The resulting value.
     */
    fun keys(): List<String> {
        return memoryMap.keys.toList()
    }

    /**
     * Executes the length operation.
     *
     * @return The resulting value.
     */
    fun length(): Int {
        return memoryMap.size
    }

    /**
     * Executes the contains operation.
     *
     * @param key Lookup key.
     * @return The resulting value.
     */
    fun contains(key: String): Boolean {
        return memoryMap.containsKey(key)
    }
}

/**
 * Storage factory functions for JavaScript
 */
actual fun createLocalStorage(): Storage {
    return try {
        // Test if localStorage is available and working
        localStorage.setItem("__summon_test__", "test")
        localStorage.removeItem("__summon_test__")
        Storage(localStorage)
    } catch (e: Exception) {
        // localStorage not available, use memory storage
        Storage(null)
    }
}

/**
 * Creates session storage.
 *
 * @return The resulting value.
 */
actual fun createSessionStorage(): Storage {
    return try {
        // Test if sessionStorage is available and working
        sessionStorage.setItem("__summon_test__", "test")
        sessionStorage.removeItem("__summon_test__")
        Storage(sessionStorage)
    } catch (e: Exception) {
        // sessionStorage not available, use memory storage
        Storage(null)
    }
}

/**
 * Creates memory storage.
 *
 * @return The resulting value.
 */
actual fun createMemoryStorage(): Storage {
    return Storage(null)
}

/**
 * JSON serialization functions for JavaScript
 */
actual inline fun <reified T> serializeToJson(value: T): String = JSON.stringify(value)

/**
 * Executes the deserialize from JSON operation.
 *
 * @param json The json value.
 * @return The resulting value.
 */
actual inline fun <reified T> deserializeFromJson(json: String): T = JSON.parse(json)