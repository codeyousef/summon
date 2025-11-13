package codes.yousef.summon.ssr

import kotlinx.browser.window

/**
 * JS implementation of platform-specific file system access.
 * Note: This is mostly a stub implementation since browser JS cannot access the file system directly.
 * In a real application, this would call server endpoints or use browser's File System Access API for PWAs.
 */
actual object FileSystemAccess {
    /**
     * Creates a directory if it doesn't exist.
     * In browser JS, this is a no-op.
     */
    actual fun createDirectory(path: String) {
        js("console.log('FileSystemAccess.createDirectory called with path: ' + path)")
        // Browser JS cannot create directories in the file system directly
        // In a real app, this would call a server endpoint
    }

    /**
     * Writes text content to a file.
     * In browser JS, this simulates writing by saving to localStorage or triggering a download.
     */
    actual fun writeTextFile(path: String, content: String) {
        js("console.log('FileSystemAccess.writeTextFile called with path: ' + path)")

        // For demonstration, store in localStorage
        try {
            window.localStorage.setItem("summon-file-" + path, content)
            js("console.log('Content stored in localStorage with key: summon-file-' + path)")
        } catch (e: Exception) {
            js("console.error('Error storing content in localStorage: ' + e.message)")
        }

        // In a real app, this might:
        // 1. Call a server endpoint to save the file
        // 2. Use the File System Access API for PWAs
        // 3. Trigger a download for the user
    }

    /**
     * Reads text content from a file.
     * In browser JS, this simulates reading from localStorage.
     */
    actual fun readTextFile(path: String): String {
        js("console.log('FileSystemAccess.readTextFile called with path: ' + path)")

        // For demonstration, try to retrieve from localStorage
        return try {
            val content = window.localStorage.getItem("summon-file-" + path)
            if (content != null) {
                js("console.log('Content retrieved from localStorage')")
                content
            } else {
                js("console.warn('No content found for path: ' + path)")
                "// No content available for " + path
            }
        } catch (e: Exception) {
            js("console.error('Error reading content from localStorage: ' + e.message)")
            "// Error reading file: " + e.message
        }

        // In a real app, this might call a server endpoint to fetch the file content
    }

    /**
     * Helper function to log to the browser console
     */
    private object console {
        fun log(message: String) {
            js("console.log(message)")
        }

        fun error(message: String) {
            js("console.error(message)")
        }

        fun warn(message: String) {
            js("console.warn(message)")
        }
    }
} 
