package codes.yousef.summon.components.input

/**
 * User-selected native file capability.
 *
 * Metadata is safe to retain; native bytes are read only through bounded internal range reads.
 *
 * @property name source file name
 * @property size selected source size in bytes
 * @property type reported media type
 * @property lastModifiedMillis source modification timestamp
 * @property sourceVersion stable metadata used to reject changed sources
 */
expect class FileInfo {
    /** The property declaration value. */
    val name: String
    /** The property declaration value. */
    val size: Long
    /** The property declaration value. */
    val type: String
    /** The property declaration value. */
    val lastModifiedMillis: Long
    /** The property declaration value. */
    val sourceVersion: FileSourceVersion

    /**
     * Reads a fresh native byte range. Callers must apply operation accounting through
     * [BoundedFileReader].
     */
    internal suspend fun readRange(offset: Long, length: Int): ByteArray

    /** Returns [name] for destructuring compatibility. */
    operator fun component1(): String
    /** Returns [size] for destructuring compatibility. */
    operator fun component2(): Long
    /** Returns [type] for destructuring compatibility. */
    operator fun component3(): String
}