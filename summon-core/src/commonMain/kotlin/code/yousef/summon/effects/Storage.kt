package code.yousef.summon.effects

/**
 * Storage types available
 */
enum class StorageType {
    /** Local storage - persists across browser sessions */
    LOCAL,
    /** Session storage - persists only for the current session */
    SESSION,
    /** Memory storage - persists only while the application is running */
    MEMORY
}

/**
 * Cross-platform storage interface for persisting key-value data
 */
expect class Storage {
    /**
     * Store a value with the given key
     * @param key The storage key
     * @param value The value to store
     */
    fun setItem(key: String, value: String)
    
    /**
     * Retrieve a value by key
     * @param key The storage key
     * @return The stored value or null if not found
     */
    fun getItem(key: String): String?
    
    /**
     * Remove an item by key
     * @param key The storage key
     */
    fun removeItem(key: String)
    
    /**
     * Clear all stored data
     */
    fun clear()
    
    /**
     * Get all stored keys
     * @return List of all keys in storage
     */
    fun keys(): List<String>
    
    /**
     * Get the number of items in storage
     * @return The count of stored items
     */
    fun length(): Int
    
    /**
     * Check if a key exists in storage
     * @param key The storage key
     * @return true if the key exists
     */
    fun contains(key: String): Boolean
}

/**
 * Storage factory functions
 */
expect fun createLocalStorage(): Storage
expect fun createSessionStorage(): Storage
expect fun createMemoryStorage(): Storage

/**
 * Get storage by type
 */
fun getStorage(type: StorageType): Storage = when (type) {
    StorageType.LOCAL -> createLocalStorage()
    StorageType.SESSION -> createSessionStorage()
    StorageType.MEMORY -> createMemoryStorage()
}

/**
 * Typed storage wrapper for working with structured data
 */
class TypedStorage(private val storage: Storage, private val keyPrefix: String = "") {
    
    /**
     * Store a typed value (uses JSON serialization)
     */
    fun <T> setTypedItem(key: String, value: T) {
        val json = serializeToJson(value)
        storage.setItem(prefixedKey(key), json)
    }
    
    /**
     * Retrieve a typed value (uses JSON deserialization)
     */
    fun <T> getTypedItem(key: String, clazz: Any): T? {
        val json = storage.getItem(prefixedKey(key)) ?: return null
        return try {
            deserializeFromJson<T>(json, clazz)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Store a string value
     */
    fun setString(key: String, value: String) {
        storage.setItem(prefixedKey(key), value)
    }
    
    /**
     * Retrieve a string value
     */
    fun getString(key: String): String? {
        return storage.getItem(prefixedKey(key))
    }
    
    /**
     * Store an integer value
     */
    fun setInt(key: String, value: Int) {
        storage.setItem(prefixedKey(key), value.toString())
    }
    
    /**
     * Retrieve an integer value
     */
    fun getInt(key: String): Int? {
        return storage.getItem(prefixedKey(key))?.toIntOrNull()
    }
    
    /**
     * Store a long value
     */
    fun setLong(key: String, value: Long) {
        storage.setItem(prefixedKey(key), value.toString())
    }
    
    /**
     * Retrieve a long value
     */
    fun getLong(key: String): Long? {
        return storage.getItem(prefixedKey(key))?.toLongOrNull()
    }
    
    /**
     * Store a boolean value
     */
    fun setBoolean(key: String, value: Boolean) {
        storage.setItem(prefixedKey(key), value.toString())
    }
    
    /**
     * Retrieve a boolean value
     */
    fun getBoolean(key: String): Boolean? {
        return storage.getItem(prefixedKey(key))?.toBooleanStrictOrNull()
    }
    
    /**
     * Store a float value
     */
    fun setFloat(key: String, value: Float) {
        storage.setItem(prefixedKey(key), value.toString())
    }
    
    /**
     * Retrieve a float value
     */
    fun getFloat(key: String): Float? {
        return storage.getItem(prefixedKey(key))?.toFloatOrNull()
    }
    
    /**
     * Store a double value
     */
    fun setDouble(key: String, value: Double) {
        storage.setItem(prefixedKey(key), value.toString())
    }
    
    /**
     * Retrieve a double value
     */
    fun getDouble(key: String): Double? {
        return storage.getItem(prefixedKey(key))?.toDoubleOrNull()
    }
    
    /**
     * Remove an item
     */
    fun removeItem(key: String) {
        storage.removeItem(prefixedKey(key))
    }
    
    /**
     * Check if a key exists
     */
    fun contains(key: String): Boolean {
        return storage.contains(prefixedKey(key))
    }
    
    /**
     * Get all keys with the current prefix
     */
    fun keys(): List<String> {
        val prefix = prefixedKey("")
        return storage.keys()
            .filter { it.startsWith(prefix) }
            .map { it.removePrefix(prefix) }
    }
    
    /**
     * Clear all items with the current prefix
     */
    fun clear() {
        val keysToRemove = keys()
        keysToRemove.forEach { removeItem(it) }
    }
    
    private fun prefixedKey(key: String): String {
        return if (keyPrefix.isNotEmpty()) "$keyPrefix.$key" else key
    }
}

/**
 * Platform-specific JSON serialization functions
 */
expect fun <T> serializeToJson(value: T): String
expect fun <T> deserializeFromJson(json: String, clazz: Any): T

/**
 * Storage utility functions
 */
object StorageUtils {
    
    /**
     * Create a typed storage with a specific prefix
     */
    fun createTypedStorage(type: StorageType, prefix: String = ""): TypedStorage {
        return TypedStorage(getStorage(type), prefix)
    }
    
    /**
     * Create a scoped storage for a specific feature/module
     */
    fun createScopedStorage(type: StorageType, scope: String): TypedStorage {
        return TypedStorage(getStorage(type), "summon.$scope")
    }
    
    /**
     * Migrate data from one storage type to another
     */
    fun migrateStorage(fromType: StorageType, toType: StorageType, keyPrefix: String = "") {
        val fromStorage = getStorage(fromType)
        val toStorage = getStorage(toType)
        
        fromStorage.keys()
            .filter { keyPrefix.isEmpty() || it.startsWith(keyPrefix) }
            .forEach { key ->
                val value = fromStorage.getItem(key)
                if (value != null) {
                    toStorage.setItem(key, value)
                }
            }
    }
    
    /**
     * Get storage usage information (where supported)
     */
    fun getStorageInfo(type: StorageType): StorageInfo {
        val storage = getStorage(type)
        val keys = storage.keys()
        val totalItems = keys.size
        
        // Calculate approximate size
        var totalSize = 0L
        keys.forEach { key ->
            val value = storage.getItem(key)
            if (value != null) {
                totalSize += key.length + value.length
            }
        }
        
        return StorageInfo(
            type = type,
            itemCount = totalItems,
            approximateSize = totalSize,
            keys = keys
        )
    }
}

/**
 * Storage information data class
 */
data class StorageInfo(
    val type: StorageType,
    val itemCount: Int,
    val approximateSize: Long,
    val keys: List<String>
)