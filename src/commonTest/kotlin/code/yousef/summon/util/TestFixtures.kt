package code.yousef.summon.util

/**
 * Simple data class for testing FileUpload, avoiding expect/actual issues.
 */
data class TestFileInfo(
    val name: String,
    val size: Long,
    val type: String
) 