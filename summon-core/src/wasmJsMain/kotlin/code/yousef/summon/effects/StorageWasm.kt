package code.yousef.summon.effects

import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.runtime.wasmConsoleWarn
import code.yousef.summon.runtime.wasmConsoleError

actual class Storage {
    private val inMemoryStorage = mutableMapOf<String, String>()

    actual fun setItem(key: String, value: String) {
        wasmConsoleLog("Storage setItem: $key = $value - WASM stub")
        inMemoryStorage[key] = value
    }

    actual fun getItem(key: String): String? {
        wasmConsoleLog("Storage getItem: $key - WASM stub")
        return inMemoryStorage[key]
    }

    actual fun removeItem(key: String) {
        wasmConsoleLog("Storage removeItem: $key - WASM stub")
        inMemoryStorage.remove(key)
    }

    actual fun clear() {
        wasmConsoleLog("Storage clear - WASM stub")
        inMemoryStorage.clear()
    }

    actual fun keys(): List<String> {
        wasmConsoleLog("Storage keys - WASM stub")
        return inMemoryStorage.keys.toList()
    }

    actual fun length(): Int {
        wasmConsoleLog("Storage length - WASM stub")
        return inMemoryStorage.size
    }

    actual fun contains(key: String): Boolean {
        wasmConsoleLog("Storage contains: $key - WASM stub")
        return inMemoryStorage.containsKey(key)
    }
}

actual fun createLocalStorage(): Storage {
    wasmConsoleLog("Creating local storage - WASM stub")
    return Storage()
}

actual fun createSessionStorage(): Storage {
    wasmConsoleLog("Creating session storage - WASM stub")
    return Storage()
}

actual fun createMemoryStorage(): Storage {
    wasmConsoleLog("Creating memory storage - WASM stub")
    return Storage()
}

actual fun <T> serializeToJson(value: T): String {
    wasmConsoleLog("Serializing to JSON - WASM stub")
    return "{}"
}

actual fun <T> deserializeFromJson(json: String, clazz: Any): T {
    wasmConsoleLog("Deserializing from JSON: $json - WASM stub")
    throw UnsupportedOperationException("JSON deserialization not implemented in WASM")
}

