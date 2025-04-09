package code.yousef.summon

import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.MutableStateImpl
import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

import kotlinx.browser.localStorage
import kotlinx.browser.window

/**
 * JS-specific extension functions for State.
 */

/**
 * Subscribes to state changes and updates UI components when the state changes.
 * @param onValueChanged A function that will be called whenever the state value changes
 */
fun <T> SummonMutableState<T>.subscribeWithRender(onValueChanged: (T) -> Unit) {
    if (this is MutableStateImpl) {
        // Add the listener
        this.addListener { newValue ->
            // Schedule the update on the next animation frame for efficiency
            window.requestAnimationFrame {
                onValueChanged(newValue)
            }
        }
    }
}

/**
 * Persists a state to localStorage, making it survive page refreshes.
 * @param key The key to store the value under in localStorage
 * @param serializer A function to convert the value to a string
 * @param deserializer A function to convert a string back to a value
 */
fun <T> SummonMutableState<T>.persistToLocalStorage(
    key: String,
    serializer: (T) -> String,
    deserializer: (String) -> T
) {
    // Try to load initial value from localStorage
    try {
        val savedValue = localStorage.getItem(key)
        if (savedValue != null) {
            value = deserializer(savedValue)
        }
    } catch (e: Exception) {
        console.error("Failed to load state from localStorage: $e")
    }

    // Save to localStorage when value changes
    if (this is MutableStateImpl) {
        addListener { newValue ->
            try {
                localStorage.setItem(key, serializer(newValue))
            } catch (e: Exception) {
                console.error("Failed to save state to localStorage: $e")
            }
        }
    }
}

/**
 * Persists a state to sessionStorage, making it survive navigation within the same tab.
 * @param key The key to store the value under in sessionStorage
 * @param serializer A function to convert the value to a string
 * @param deserializer A function to convert a string back to a value
 */
fun <T> SummonMutableState<T>.persistToSessionStorage(
    key: String,
    serializer: (T) -> String,
    deserializer: (String) -> T
) {
    // Try to load initial value from sessionStorage
    try {
        val savedValue = window.sessionStorage.getItem(key)
        if (savedValue != null) {
            value = deserializer(savedValue)
        }
    } catch (e: Exception) {
        console.error("Failed to load state from sessionStorage: $e")
    }

    // Save to sessionStorage when value changes
    if (this is MutableStateImpl) {
        addListener { newValue ->
            try {
                window.sessionStorage.setItem(key, serializer(newValue))
            } catch (e: Exception) {
                console.error("Failed to save state to sessionStorage: $e")
            }
        }
    }
} 
