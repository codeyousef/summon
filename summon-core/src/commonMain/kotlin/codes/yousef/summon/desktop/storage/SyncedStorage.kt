/**
 * # Synced Storage
 *
 * Cross-tab/cross-window reactive storage system that automatically synchronizes
 * state changes across browser tabs and windows using the localStorage API and
 * storage events.
 *
 * ## Features
 *
 * - **Cross-Tab Sync**: Changes in one tab are automatically reflected in all other tabs
 * - **Reactive State**: Integrates with Summon's state system for automatic recomposition
 * - **Type-Safe**: Generic type parameter with custom serializers/deserializers
 * - **Persistent**: Data survives page reloads and browser restarts
 * - **Composable Integration**: Use `rememberSynced` for seamless integration
 *
 * ## Usage Examples
 *
 * ### Basic Usage
 *
 * ```kotlin
 * // Create a synced storage for a simple string
 * val userPrefs = createSyncedStorage(
 *     key = "user-theme",
 *     defaultValue = "light",
 *     serializer = { it },
 *     deserializer = { it }
 * )
 *
 * // Read value
 * val theme = userPrefs.value
 *
 * // Write value (automatically syncs to other tabs)
 * userPrefs.value = "dark"
 * ```
 *
 * ### Composable Integration
 *
 * ```kotlin
 * @Composable
 * fun ThemeSwitcher() {
 *     val theme = rememberSynced(
 *         key = "user-theme",
 *         defaultValue = "light",
 *         serializer = { it },
 *         deserializer = { it }
 *     )
 *
 *     Button(onClick = {
 *         theme.value = if (theme.value == "light") "dark" else "light"
 *     }) {
 *         Text("Toggle Theme: ${theme.value}")
 *     }
 * }
 * ```
 *
 * ### Complex Objects
 *
 * ```kotlin
 * @Serializable
 * data class UserSettings(
 *     val theme: String,
 *     val language: String,
 *     val notifications: Boolean
 * )
 *
 * val settings = createSyncedStorage(
 *     key = "user-settings",
 *     defaultValue = UserSettings("light", "en", true),
 *     serializer = { Json.encodeToString(it) },
 *     deserializer = { Json.decodeFromString(it) }
 * )
 * ```
 *
 * ## Platform Support
 *
 * | Platform | Implementation |
 * |----------|---------------|
 * | JS       | localStorage + storage event |
 * | WASM     | localStorage + storage event |
 * | JVM      | In-memory only (no cross-process sync) |
 *
 * @since 0.7.0
 */
package codes.yousef.summon.desktop.storage

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf

/**
 * A reactive storage container that synchronizes across browser tabs/windows.
 *
 * Changes to the value are automatically persisted to localStorage and
 * synchronized to all other tabs/windows viewing the same origin.
 *
 * @param T The type of value being stored
 */
interface SyncedStorage<T> {
    /**
     * The current value. Reading returns the latest synced value.
     * Writing updates localStorage and triggers sync to other tabs.
     */
    var value: T

    /**
     * Clears the stored value and resets to the default.
     */
    fun clear()

    /**
     * Checks if a value exists in storage.
     */
    fun exists(): Boolean

    /**
     * Adds a listener that will be called when the value changes
     * (including changes from other tabs).
     *
     * @param listener Callback invoked with the new value
     * @return A function to remove the listener
     */
    fun addChangeListener(listener: (T) -> Unit): () -> Unit
}

/**
 * Creates a synced storage instance for the given key.
 *
 * @param key The localStorage key to use for persistence
 * @param defaultValue The default value if nothing is stored
 * @param serializer Function to convert the value to a string
 * @param deserializer Function to convert a string back to the value
 * @return A new SyncedStorage instance
 */
expect fun <T> createSyncedStorage(
    key: String,
    defaultValue: T,
    serializer: (T) -> String,
    deserializer: (String) -> T
): SyncedStorage<T>

/**
 * Creates and remembers a synced storage instance within a composable.
 *
 * The storage will persist across recompositions and automatically
 * trigger recomposition when the value changes (including from other tabs).
 *
 * @param key The localStorage key to use for persistence
 * @param defaultValue The default value if nothing is stored
 * @param serializer Function to convert the value to a string
 * @param deserializer Function to convert a string back to the value
 * @return A SyncedStorage instance that triggers recomposition on changes
 */
@Composable
fun <T> rememberSynced(
    key: String,
    defaultValue: T,
    serializer: (T) -> String,
    deserializer: (String) -> T
): SyncedStorage<T> {
    val storage = remember { createSyncedStorage(key, defaultValue, serializer, deserializer) }
    val state = remember { mutableStateOf(storage.value) }

    // Set up cross-tab sync
    remember {
        val unsubscribe = storage.addChangeListener { newValue ->
            state.value = newValue
        }
        // Note: In a real implementation, we'd return a cleanup function
        unsubscribe
    }

    // Return a wrapper that keeps the internal state in sync
    return object : SyncedStorage<T> {
        override var value: T
            get() {
                // Reading triggers recomposition through the state
                @Suppress("UNUSED_VARIABLE")
                val unused = state.value
                return storage.value
            }
            set(newValue) {
                storage.value = newValue
                state.value = newValue
            }

        override fun clear() = storage.clear()
        override fun exists() = storage.exists()
        override fun addChangeListener(listener: (T) -> Unit) = storage.addChangeListener(listener)
    }
}

// ============================================
// Convenience Functions for Common Types
// ============================================

/**
 * Creates a synced storage for a String value.
 */
fun createSyncedStringStorage(key: String, defaultValue: String = ""): SyncedStorage<String> =
    createSyncedStorage(key, defaultValue, { it }, { it })

/**
 * Creates a synced storage for an Int value.
 */
fun createSyncedIntStorage(key: String, defaultValue: Int = 0): SyncedStorage<Int> =
    createSyncedStorage(key, defaultValue, { it.toString() }, { it.toInt() })

/**
 * Creates a synced storage for a Boolean value.
 */
fun createSyncedBooleanStorage(key: String, defaultValue: Boolean = false): SyncedStorage<Boolean> =
    createSyncedStorage(key, defaultValue, { it.toString() }, { it.toBoolean() })

/**
 * Creates a synced storage for a Double value.
 */
fun createSyncedDoubleStorage(key: String, defaultValue: Double = 0.0): SyncedStorage<Double> =
    createSyncedStorage(key, defaultValue, { it.toString() }, { it.toDouble() })

/**
 * Creates a synced storage for a Long value.
 */
fun createSyncedLongStorage(key: String, defaultValue: Long = 0L): SyncedStorage<Long> =
    createSyncedStorage(key, defaultValue, { it.toString() }, { it.toLong() })

// ============================================
// Composable Convenience Functions
// ============================================

/**
 * Remembers a synced String value.
 */
@Composable
fun rememberSyncedString(key: String, defaultValue: String = ""): SyncedStorage<String> =
    rememberSynced(key, defaultValue, { it }, { it })

/**
 * Remembers a synced Int value.
 */
@Composable
fun rememberSyncedInt(key: String, defaultValue: Int = 0): SyncedStorage<Int> =
    rememberSynced(key, defaultValue, { it.toString() }, { it.toInt() })

/**
 * Remembers a synced Boolean value.
 */
@Composable
fun rememberSyncedBoolean(key: String, defaultValue: Boolean = false): SyncedStorage<Boolean> =
    rememberSynced(key, defaultValue, { it.toString() }, { it.toBoolean() })
