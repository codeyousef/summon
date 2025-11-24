package codes.yousef.summon.cli.util

import java.io.File
import java.util.*

/**
 * Utility to read version from version.properties file.
 * Implements caching to avoid repeated file reads.
 */
object VersionReader {
    private var cachedVersion: String? = null

    /**
     * Reads the VERSION property from version.properties in the repository root.
     * Returns cached version if already loaded.
     *
     * @return The version string, or "0.5.1.2" as fallback
     */
    fun readVersion(): String {
        if (cachedVersion != null) {
            return cachedVersion!!
        }

        return try {
            // 1. Try to load from classpath (for packaged JAR)
            val resourceStream = VersionReader::class.java.classLoader.getResourceAsStream("version.properties")
            if (resourceStream != null) {
                val properties = Properties()
                resourceStream.use { properties.load(it) }
                val version = properties.getProperty("VERSION")
                if (!version.isNullOrBlank()) {
                    cachedVersion = version.trim()
                    return cachedVersion!!
                }
            }

            // 2. Fallback to file system (for development/tests)
            val possiblePaths = listOf(
                File("version.properties"),
                File("../version.properties"),
                File("../../version.properties"),
                // Also check relative to this class's location
                File(System.getProperty("user.dir"), "version.properties")
            )

            val versionFile = possiblePaths.firstOrNull { it.exists() && it.canRead() }

            if (versionFile == null) {
                println("Warning: version.properties not found, using fallback version 0.5.1.2")
                cachedVersion = "0.5.1.2"
                return cachedVersion!!
            }

            val properties = Properties()
            versionFile.inputStream().use { stream ->
                properties.load(stream)
            }

            val version = properties.getProperty("VERSION")
            if (version.isNullOrBlank()) {
                println("Warning: VERSION property not found in version.properties, using fallback")
                cachedVersion = "0.5.1.2"
            } else {
                cachedVersion = version.trim()
            }

            cachedVersion!!
        } catch (e: Exception) {
                println("Error reading version.properties: ${e.message}")
                cachedVersion = "0.5.1.2"
                cachedVersion!!
            }
    }

    /**
     * Validates if a version string follows semantic versioning format.
     * Accepts formats like: 1.0.0, 1.0.0.0, 0.5.0.2
     */
    fun isValidSemver(version: String): Boolean {
        val semverRegex = Regex("""^\d+\.\d+\.\d+(\.\d+)?(-[a-zA-Z0-9.-]+)?$""")
        return semverRegex.matches(version)
    }

    /**
     * Clears the cached version (useful for testing).
     */
    internal fun clearCache() {
        cachedVersion = null
    }
}
