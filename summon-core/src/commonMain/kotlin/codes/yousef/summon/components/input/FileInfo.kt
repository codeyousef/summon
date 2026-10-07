package codes.yousef.summon.components.input

/**
 * Represents information about a file selected by the user.
 * Common properties are defined here, platform implementations may add specific details.
 */
expect class FileInfo {
    val name: String
    val size: Long
    val type: String
    val lastModifiedMillis: Long
    val sourceVersion: FileSourceVersion

    /**
     * Reads a fresh native byte range. Callers must apply operation accounting through
     * [BoundedFileReader].
     */
    internal suspend fun readRange(offset: Long, length: Int): ByteArray

    operator fun component1(): String
    operator fun component2(): Long
    operator fun component3(): String
}