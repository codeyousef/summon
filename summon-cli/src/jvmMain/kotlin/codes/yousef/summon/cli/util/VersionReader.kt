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
     * @return The version string, or "0.4.8.8" as fallback
     */
    fun readVersion(): String {
        if (cachedVersion != null) {
            return cachedVersion!!
        }

        return try {
            // Try multiple possible locations for version.properties
            val possiblePaths = listOf(
                File("version.properties"),
                File("../version.properties"),
                File("../../version.properties"),
                // Also check relative to this class's location
                File(System.getProperty("user.dir"), "version.properties")
            )

            val versionFile = possiblePaths.firstOrNull { it.exists() && it.canRead() }

            if (versionFile == null) {
                println("Warning: version.properties not found, using fallback version 0.4.8.8")
                cachedVersion = "0.4.8.8"
                return cachedVersion!!
            }

            val properties = Properties()
            versionFile.inputStream().use { stream ->
                properties.load(stream)
            }

            val version = properties.getProperty("VERSION")
            if (version.isNullOrBlank()) {
                println("Warning: VERSION property not found in version.properties, using fallback")
                cachedVersion = "0.4.8.8"
            } else {
                cachedVersion = version.trim()
            }

            cachedVersion!!
        } catch (e: Exception) {
            println("Error reading version.properties: ${e.message}")
            cachedVersion = "0.4.8.8"
            cachedVersion!!
        }
    }

    /**
     * Validates if a version string follows semantic versioning format.
     * Accepts formats like: 1.0.0, 1.0.0.0, 0.4.8.8
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
