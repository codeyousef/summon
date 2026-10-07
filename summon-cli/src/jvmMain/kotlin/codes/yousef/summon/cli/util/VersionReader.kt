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
     * Reads the VERSION property from packaged resources or the repository root.
     * Missing or blank version metadata is a packaging error; returning a stale
     * fallback would make generated projects depend on the wrong release.
     */
    fun readVersion(): String {
        cachedVersion?.let { return it }

        val resourceVersion = VersionReader::class.java.classLoader
            .getResourceAsStream("version.properties")
            ?.use { stream ->
                Properties().apply { load(stream) }.getProperty("VERSION")
            }
        val fileVersion = if (resourceVersion == null) {
            listOf(
                File("version.properties"),
                File("../version.properties"),
                File("../../version.properties"),
                File(System.getProperty("user.dir"), "version.properties"),
            ).firstOrNull { it.isFile && it.canRead() }
                ?.inputStream()
                ?.use { stream ->
                    Properties().apply { load(stream) }.getProperty("VERSION")
                }
        } else {
            null
        }

        val version = (resourceVersion ?: fileVersion)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: error("VERSION is missing from version.properties")
        cachedVersion = version
        return version
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
