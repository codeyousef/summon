package code.yousef.summon

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.CopyOnWriteArrayList
import java.util.prefs.Preferences

/**
 * JVM implementation for State management.
 */

/**
 * Provides JVM-specific implementation for state observation.
 * This implementation uses thread-safe collections for observers.
 */
class StateObserver<T>(private val state: MutableState<T>) {
    private val observers = CopyOnWriteArrayList<(T) -> Unit>()

    init {
        if (state is MutableStateImpl) {
            state.addListener { value ->
                notifyObservers(value)
            }
        }
    }

    /**
     * Adds an observer that will be notified when the state changes.
     */
    fun addObserver(observer: (T) -> Unit) {
        observers.add(observer)
        // Immediately notify with current value
        observer(state.value)
    }

    /**
     * Removes an observer.
     */
    fun removeObserver(observer: (T) -> Unit) {
        observers.remove(observer)
    }

    private fun notifyObservers(value: T) {
        observers.forEach { it(value) }
    }
}

/**
 * Creates a state observer for this state.
 */
fun <T> MutableState<T>.createObserver(): StateObserver<T> = StateObserver(this)

/**
 * Formats this state value as a string for debugging.
 */
fun <T> State<T>.toJvmString(): String = "State(value=${value})"

/**
 * Persists a state to Java Preferences, making it survive application restarts.
 * Note: This only works for primitive types and strings.
 *
 * @param key The key to store the value under in Preferences
 * @param nodeName The name of the preferences node to use
 */
fun <T> MutableState<T>.persistToPreferences(
    key: String,
    nodeName: String = "code.yousef.summon"
) {
    val prefs = Preferences.userRoot().node(nodeName)

    // Load initial value if exists
    @Suppress("UNCHECKED_CAST")
    when (value) {
        is String -> {
            val savedValue = prefs.get(key, value as String)
            this.value = savedValue as T
        }

        is Int -> {
            val savedValue = prefs.getInt(key, value as Int)
            this.value = savedValue as T
        }

        is Long -> {
            val savedValue = prefs.getLong(key, value as Long)
            this.value = savedValue as T
        }

        is Boolean -> {
            val savedValue = prefs.getBoolean(key, value as Boolean)
            this.value = savedValue as T
        }

        is Float -> {
            val savedValue = prefs.getFloat(key, value as Float)
            this.value = savedValue as T
        }

        is Double -> {
            val savedValue = prefs.getDouble(key, value as Double)
            this.value = savedValue as T
        }

        else -> throw IllegalArgumentException("Unsupported type for preferences: ${value?.let { it::class.java.name }}")
    }

    // Save to preferences when value changes
    if (this is MutableStateImpl) {
        addListener { newValue ->
            when (newValue) {
                is String -> prefs.put(key, newValue)
                is Int -> prefs.putInt(key, newValue)
                is Long -> prefs.putLong(key, newValue)
                is Boolean -> prefs.putBoolean(key, newValue)
                is Float -> prefs.putFloat(key, newValue)
                is Double -> prefs.putDouble(key, newValue)
                else -> throw IllegalArgumentException("Unsupported type for preferences: ${newValue?.let { it::class.java.name }}")
            }
            prefs.flush()
        }
    }
}

/**
 * Persists a state to a file, making it survive application restarts.
 *
 * @param filePath The path to the file where the state should be saved
 * @param serializer A function to convert the value to a string
 * @param deserializer A function to convert a string back to a value
 */
fun <T> MutableState<T>.persistToFile(
    filePath: String,
    serializer: (T) -> String,
    deserializer: (String) -> T
) {
    val file = File(filePath)

    // Load initial value if file exists
    if (file.exists()) {
        try {
            val content = Files.readString(Paths.get(filePath))
            value = deserializer(content)
        } catch (e: Exception) {
            System.err.println("Failed to load state from file: $e")
        }
    }

    // Save to file when value changes
    if (this is MutableStateImpl) {
        addListener { newValue ->
            try {
                file.parentFile?.mkdirs() // Create parent directories if they don't exist
                Files.writeString(Paths.get(filePath), serializer(newValue))
            } catch (e: Exception) {
                System.err.println("Failed to save state to file: $e")
            }
        }
    }
} 