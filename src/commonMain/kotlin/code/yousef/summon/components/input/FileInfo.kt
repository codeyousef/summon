package code.yousef.summon.components.input

/**
 * Data class representing a file selected for upload.
 */
data class FileInfo(
    val name: String,
    val size: Long,
    val type: String,
    val lastModified: Long
) 