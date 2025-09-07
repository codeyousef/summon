package code.yousef.summon.components.input

import org.w3c.files.File

/**
 * Actual implementation of FileInfo for the JS platform.
 */
actual data class FileInfo(
    actual val name: String,
    actual val size: Long,
    actual val type: String,
    val jsFile: File // Platform-specific field to hold the JS File object
)