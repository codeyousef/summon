package codes.yousef.summon.components.input

import java.io.File

/**
 * Actual implementation of FileInfo for the JVM platform.
 */
actual data class FileInfo(
    actual val name: String,
    actual val size: Long,
    actual val type: String,
    val file: File? = null // Platform-specific field to hold the JVM File object
) 