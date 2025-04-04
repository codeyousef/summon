package code.yousef.summon

import kotlinx.browser.window

/**
 * JS-specific extension functions for State.
 */

/**
 * Subscribes to state changes and updates UI components when the state changes.
 * @param onValueChanged A function that will be called whenever the state value changes
 */
fun <T> MutableState<T>.subscribeWithRender(onValueChanged: (T) -> Unit) {
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