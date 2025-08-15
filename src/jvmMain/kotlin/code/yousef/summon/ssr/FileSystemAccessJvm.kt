package code.yousef.summon.ssr

import java.io.File

/**
 * JVM implementation of platform-specific file system access
 */
actual object FileSystemAccess {
    /**
     * Creates a directory if it doesn't exist
     */
    actual fun createDirectory(path: String) {
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    /**
     * Writes text content to a file
     */
    actual fun writeTextFile(path: String, content: String) {
        // Create parent directories if they don't exist
        val file = File(path)
        file.parentFile?.mkdirs()

        // Write the content to the file
        file.writeText(content)
    }

    /**
     * Reads text content from a file
     */
    actual fun readTextFile(path: String): String {
        return File(path).readText()
    }
} 