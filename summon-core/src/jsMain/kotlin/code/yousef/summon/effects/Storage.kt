package code.yousef.summon.effects

import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage
import org.w3c.dom.Storage as DomStorage

/**
 * JavaScript Storage implementation wrapping browser storage APIs
 */
actual class Storage(private val domStorage: DomStorage?) {
    
    // In-memory fallback for when DOM storage is not available
    private val memoryFallback = mutableMapOf<String, String>()
    private val useMemoryFallback = domStorage == null
    
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
    
    actual fun contains(key: String): Boolean {
        return getItem(key) != null
    }
}

/**
 * In-memory storage implementation
 */
class MemoryStorage {
    private val memoryMap = mutableMapOf<String, String>()
    
    fun setItem(key: String, value: String) {
        memoryMap[key] = value
    }
    
    fun getItem(key: String): String? {
        return memoryMap[key]
    }
    
    fun removeItem(key: String) {
        memoryMap.remove(key)
    }
    
    fun clear() {
        memoryMap.clear()
    }
    
    fun keys(): List<String> {
        return memoryMap.keys.toList()
    }
    
    fun length(): Int {
        return memoryMap.size
    }
    
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

actual fun createMemoryStorage(): Storage {
    return Storage(null)
}

/**
 * JSON serialization functions for JavaScript
 */
actual fun <T> serializeToJson(value: T): String {
    return JSON.stringify(value)
}

actual fun <T> deserializeFromJson(json: String, clazz: Any): T {
    return JSON.parse<T>(json)
}