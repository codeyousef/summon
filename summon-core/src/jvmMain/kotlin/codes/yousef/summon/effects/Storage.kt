@file:JvmName("StorageJvm")

package codes.yousef.summon.effects

import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.prefs.Preferences

/**
 * JVM Storage implementation using Java Preferences API and file system

 * @property preferences The preferences value.
 * @property fileStorage The file storage value.
 * @property memoryStorage The memory storage value.
 */
actual class Storage private constructor(
    private val preferences: Preferences?,
    private val fileStorage: FileStorage?,
    private val memoryStorage: MutableMap<String, String>?
) {

    /**
     * Sets item.
     *
     * @param key Lookup key.
     * @param value Value to process.
     */
    actual fun setItem(key: String, value: String) {
        when {
            preferences != null -> {
                try {
                    preferences.put(key, value)
                    preferences.flush()
                } catch (e: Exception) {
                    // Fallback to memory if preferences fail
                    memoryFallback[key] = value
                }
            }

            fileStorage != null -> {
                fileStorage.setItem(key, value)
            }

            memoryStorage != null -> {
                memoryStorage[key] = value
            }
        }
    }

    /**
     * Returns item.
     *
     * @param key Lookup key.
     * @return The resulting value.
     */
    actual fun getItem(key: String): String? {
        return when {
            preferences != null -> {
                try {
                    preferences.get(key, null)
                } catch (e: Exception) {
                    memoryFallback[key]
                }
            }

            fileStorage != null -> {
                fileStorage.getItem(key)
            }

            memoryStorage != null -> {
                memoryStorage[key]
            }

            else -> null
        }
    }

    /**
     * Removes item.
     *
     * @param key Lookup key.
     */
    actual fun removeItem(key: String) {
        when {
            preferences != null -> {
                try {
                    preferences.remove(key)
                    preferences.flush()
                } catch (e: Exception) {
                    memoryFallback.remove(key)
                }
            }

            fileStorage != null -> {
                fileStorage.removeItem(key)
            }

            memoryStorage != null -> {
                memoryStorage.remove(key)
            }
        }
    }

    /** Clears the operation. */
    actual fun clear() {
        when {
            preferences != null -> {
                try {
                    preferences.clear()
                    preferences.flush()
                } catch (e: Exception) {
                    memoryFallback.clear()
                }
            }

            fileStorage != null -> {
                fileStorage.clear()
            }

            memoryStorage != null -> {
                memoryStorage.clear()
            }
        }
    }

    /**
     * Executes the keys operation.
     *
     * @return The resulting value.
     */
    actual fun keys(): List<String> {
        return when {
            preferences != null -> {
                try {
                    preferences.keys().toList()
                } catch (e: Exception) {
                    memoryFallback.keys.toList()
                }
            }

            fileStorage != null -> {
                fileStorage.keys()
            }

            memoryStorage != null -> {
                memoryStorage.keys.toList()
            }

            else -> emptyList()
        }
    }

    /**
     * Executes the length operation.
     *
     * @return The resulting value.
     */
    actual fun length(): Int {
        return keys().size
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

    /** Provides storage factory and constant members. */
    companion object {
        private val memoryFallback = ConcurrentHashMap<String, String>()

        /**
         * Creates preferences storage.
         *
         * @param node The node value.
         * @return The resulting value.
         */
        fun createPreferencesStorage(node: String): Storage {
            return try {
                val prefs = Preferences.userRoot().node(node)
                Storage(prefs, null, null)
            } catch (e: Exception) {
                // Fallback to memory storage
                Storage(null, null, ConcurrentHashMap())
            }
        }

        /**
         * Creates file storage.
         *
         * @param directory The directory value.
         * @return The resulting value.
         */
        fun createFileStorage(directory: String): Storage {
            return try {
                val fileStorage = FileStorage(directory)
                Storage(null, fileStorage, null)
            } catch (e: Exception) {
                // Fallback to memory storage
                Storage(null, null, ConcurrentHashMap())
            }
        }

        /**
         * Creates memory storage.
         *
         * @return The resulting value.
         */
        fun createMemoryStorage(): Storage {
            return Storage(null, null, ConcurrentHashMap())
        }
    }
}

/**
 * File-based storage implementation for JVM
 */
private class FileStorage(private val directory: String) {
    private val storageDir = File(directory).apply {
        if (!exists()) {
            mkdirs()
        }
    }

    fun setItem(key: String, value: String) {
        try {
            val file = File(storageDir, sanitizeFileName(key))
            file.writeText(value)
        } catch (e: Exception) {
            // Handle file write errors
        }
    }

    fun getItem(key: String): String? {
        return try {
            val file = File(storageDir, sanitizeFileName(key))
            if (file.exists()) file.readText() else null
        } catch (e: Exception) {
            null
        }
    }

    fun removeItem(key: String) {
        try {
            val file = File(storageDir, sanitizeFileName(key))
            file.delete()
        } catch (e: Exception) {
            // Handle file delete errors
        }
    }

    fun clear() {
        try {
            storageDir.listFiles()?.forEach { it.delete() }
        } catch (e: Exception) {
            // Handle clear errors
        }
    }

    fun keys(): List<String> {
        return try {
            storageDir.listFiles()?.map { unsanitizeFileName(it.name) } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun sanitizeFileName(name: String): String {
        // Replace invalid file name characters with underscores
        return name.replace(Regex("[<>:\"/\\\\|?*]"), "_")
    }

    private fun unsanitizeFileName(name: String): String {
        // This is a simplification - in a real implementation you'd want
        // a more sophisticated encoding scheme
        return name
    }
}

/**
 * Storage factory functions for JVM
 */
actual fun createLocalStorage(): Storage {
    return Storage.createPreferencesStorage("summon/localStorage")
}

/**
 * Creates session storage.
 *
 * @return The resulting value.
 */
actual fun createSessionStorage(): Storage {
    // For JVM, session storage is memory-based since there's no session concept
    return Storage.createMemoryStorage()
}

/**
 * Creates memory storage.
 *
 * @return The resulting value.
 */
actual fun createMemoryStorage(): Storage {
    return Storage.createMemoryStorage()
}

/**
 * JSON serialization functions for JVM
 */

actual inline fun <reified T> serializeToJson(value: T): String =
    Json.encodeToString(value)

/**
 * Executes the deserialize from JSON operation.
 *
 * @param json The json value.
 * @return The resulting value.
 */
actual inline fun <reified T> deserializeFromJson(json: String): T =
    Json.decodeFromString(json)