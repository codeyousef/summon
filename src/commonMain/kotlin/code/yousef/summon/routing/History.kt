package routing

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Navigation history manager that provides access to browser history navigation.
 * This class provides a Kotlin Multiplatform abstraction over browser history.
 */
class History private constructor() {

    /**
     * The current history entries stack
     */
    private val entries = mutableListOf<HistoryEntry>()

    /**
     * Current position in the history stack
     */
    private var currentIndex = -1

    /**
     * Pushes a new entry onto the history stack.
     *
     * @param path The URL path
     * @param state Optional state object to associate with this history entry
     */
    fun push(path: String, state: Map<String, Any> = emptyMap()) {
        // If we're not at the end of the stack, remove all future entries
        if (currentIndex < entries.size - 1) {
            entries.subList(currentIndex + 1, entries.size).clear()
        }

        // Add the new entry
        entries.add(HistoryEntry(path, state))
        currentIndex++

        // Update the browser history (platform-specific)
        updateBrowserHistory(path, state, false)
    }

    /**
     * Replaces the current entry in the history stack.
     *
     * @param path The URL path
     * @param state Optional state object to associate with this history entry
     */
    fun replace(path: String, state: Map<String, Any> = emptyMap()) {
        if (currentIndex >= 0) {
            entries[currentIndex] = HistoryEntry(path, state)
        } else {
            entries.add(HistoryEntry(path, state))
            currentIndex = 0
        }

        // Update the browser history (platform-specific)
        updateBrowserHistory(path, state, true)
    }

    /**
     * Navigates forward in the history stack.
     *
     * @return True if navigation was successful
     */
    fun forward(): Boolean {
        if (currentIndex < entries.size - 1) {
            currentIndex++
            val entry = entries[currentIndex]

            // Perform the navigation (platform-specific)
            navigateInBrowserHistory(1)

            return true
        }
        return false
    }

    /**
     * Navigates backward in the history stack.
     *
     * @return True if navigation was successful
     */
    fun back(): Boolean {
        if (currentIndex > 0) {
            currentIndex--
            val entry = entries[currentIndex]

            // Perform the navigation (platform-specific)
            navigateInBrowserHistory(-1)

            return true
        }
        return false
    }

    /**
     * Goes to a specific position in the history stack.
     *
     * @param delta Number of entries to move (positive for forward, negative for backward)
     * @return True if navigation was successful
     */
    fun go(delta: Int): Boolean {
        val targetIndex = currentIndex + delta

        if (targetIndex >= 0 && targetIndex < entries.size) {
            val steps = targetIndex - currentIndex
            currentIndex = targetIndex

            // Perform the navigation (platform-specific)
            navigateInBrowserHistory(steps)

            return true
        }
        return false
    }

    /**
     * Gets the current entry in the history stack.
     *
     * @return The current history entry or null if history is empty
     */
    fun current(): HistoryEntry? {
        return if (currentIndex >= 0 && currentIndex < entries.size) {
            entries[currentIndex]
        } else {
            null
        }
    }

    /**
     * Gets the length of the history stack.
     *
     * @return The number of entries in the history stack
     */
    fun length(): Int = entries.size

    /**
     * Platform-specific function to update the browser history.
     * This will be implemented differently for JS and JVM.
     */
    private fun updateBrowserHistory(path: String, state: Map<String, Any>, replace: Boolean) {
        // Will be implemented in platform-specific extensions
    }

    /**
     * Platform-specific function to navigate in browser history.
     * This will be implemented differently for JS and JVM.
     */
    private fun navigateInBrowserHistory(steps: Int) {
        // Will be implemented in platform-specific extensions
    }

    /**
     * A single entry in the history stack.
     */
    data class HistoryEntry(
        val path: String,
        val state: Map<String, Any>
    )

    companion object {
        private var instance: History? = null

        /**
         * Gets the singleton instance of History.
         *
         * @return The History singleton instance
         */
        fun getInstance(): History {
            if (instance == null) {
                instance = History()
            }
            return instance!!
        }
    }
} 
