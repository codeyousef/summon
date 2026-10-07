package codes.yousef.summon.effects

import kotlinx.serialization.json.Json

/** Represents storage. */
actual class Storage {
    private val inMemoryStorage = mutableMapOf<String, String>()

    /**
     * Sets item.
     *
     * @param key Lookup key.
     * @param value Value to process.
     */
    actual fun setItem(key: String, value: String) {
        inMemoryStorage[key] = value
    }

    /**
     * Returns item.
     *
     * @param key Lookup key.
     * @return The resulting value.
     */
    actual fun getItem(key: String): String? {
        return inMemoryStorage[key]
    }

    /**
     * Removes item.
     *
     * @param key Lookup key.
     */
    actual fun removeItem(key: String) {
        inMemoryStorage.remove(key)
    }

    /** Clears the operation. */
    actual fun clear() {
        inMemoryStorage.clear()
    }

    /**
     * Executes the keys operation.
     *
     * @return The resulting value.
     */
    actual fun keys(): List<String> {
        return inMemoryStorage.keys.toList()
    }

    /**
     * Executes the length operation.
     *
     * @return The resulting value.
     */
    actual fun length(): Int {
        return inMemoryStorage.size
    }

    /**
     * Executes the contains operation.
     *
     * @param key Lookup key.
     * @return The resulting value.
     */
    actual fun contains(key: String): Boolean {
        return inMemoryStorage.containsKey(key)
    }
}

/**
 * Creates local storage.
 *
 * @return The resulting value.
 */
actual fun createLocalStorage(): Storage {
    return Storage()
}

/**
 * Creates session storage.
 *
 * @return The resulting value.
 */
actual fun createSessionStorage(): Storage {
    return Storage()
}

/**
 * Creates memory storage.
 *
 * @return The resulting value.
 */
actual fun createMemoryStorage(): Storage {
    return Storage()
}

/**
 * Executes the serialize to JSON operation.
 *
 * @param value Value to process.
 * @return The resulting value.
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

