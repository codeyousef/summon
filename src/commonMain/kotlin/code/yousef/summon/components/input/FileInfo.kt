package code.yousef.summon.components.input

/**
 * Represents information about a file selected by the user.
 * Common properties are defined here, platform implementations may add specific details.
 */
expect class FileInfo {
    val name: String // The name of the file.
    val size: Long   // The size of the file in bytes.
    val type: String // The MIME type of the file.

    // Add component functions explicitly if needed for destructuring, mirroring data class behavior
    operator fun component1(): String
    operator fun component2(): Long
    operator fun component3(): String
}