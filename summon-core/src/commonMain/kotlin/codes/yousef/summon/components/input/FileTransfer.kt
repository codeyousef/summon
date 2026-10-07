package codes.yousef.summon.components.input

/** Default plaintext chunk size used by suite cryptography adapters. */
const val DEFAULT_PLAINTEXT_CHUNK_BYTES: Int = 4 * 1024 * 1024

/** Default storage-part target. This is deliberately distinct from the plaintext chunk size. */
const val DEFAULT_STORAGE_PART_BYTES: Int = 16 * 1024 * 1024

/**
 * Stable metadata used to reject a changed source during resume.
 *
 * @property size source size in bytes
 * @property lastModifiedMillis source modification timestamp
 */
data class FileSourceVersion(
    val size: Long,
    val lastModifiedMillis: Long
)

/**
 * Caller-owned bounds for native file reads.
 *
 * @property maxChunkBytes maximum bytes per native range read
 * @property maxTotalBytes maximum bytes consumed by one reader
 */
data class FileReadPolicy(
    val maxChunkBytes: Int = DEFAULT_PLAINTEXT_CHUNK_BYTES,
    val maxTotalBytes: Long
) {
    init {
        require(maxChunkBytes > 0) { "File chunk limit must be positive" }
        require(maxTotalBytes >= 0) { "File operation limit must be non-negative" }
    }
}

/** Bounded native file-read failure. */
sealed class FileReadException(message: String) : Exception(message) {
    /** Requested range is negative, overflowed, or outside the source. */
    class InvalidRange : FileReadException("Invalid file range")
    /** Chunk or operation byte limit was exceeded. */
    class LimitExceeded : FileReadException("File read limit exceeded")
    /** Source metadata changed after selection. */
    class SourceChanged : FileReadException("File source changed; reselection is required")
    /** Platform cannot provide native range reads. */
    class Unavailable : FileReadException("Native file reads are unavailable on this platform")
    /** Native range read failed or returned the wrong byte count. */
    class Failed : FileReadException("Native file read failed")
}

internal fun validateFileRange(
    fileSize: Long,
    offset: Long,
    length: Int,
    policy: FileReadPolicy,
    consumedBytes: Long = 0
) {
    if (fileSize < 0 || offset < 0 || length < 0 || consumedBytes < 0) throw FileReadException.InvalidRange()
    if (length > policy.maxChunkBytes) throw FileReadException.LimitExceeded()
    val end = offset + length.toLong()
    if (end < offset || end > fileSize) throw FileReadException.InvalidRange()
    val operationEnd = consumedBytes + length.toLong()
    if (operationEnd < consumedBytes || operationEnd > policy.maxTotalBytes) throw FileReadException.LimitExceeded()
}

/**
 * Sequentially accounts native range reads for one operation.
 *
 * The source capability remains owned by [FileInfo]. Closing this reader releases only the
 * operation's accounting state; callers decide when the selected source is removed.

 * @property file The file value.
 * @property policy The policy value.
 */
class BoundedFileReader(
    private val file: FileInfo,
    private val policy: FileReadPolicy,
    expectedVersion: FileSourceVersion = file.sourceVersion
) : AutoCloseable {
    private val expectedVersion = expectedVersion
    private var consumedBytes = 0L
    private var closed = false

    /** Bytes consumed by this reader. */
    val bytesRead: Long get() = consumedBytes
    /** Source version this reader requires. */
    val sourceVersion: FileSourceVersion get() = expectedVersion

    /** Reads exactly [length] bytes at [offset], enforcing source stability and policy bounds. */
    suspend fun read(offset: Long, length: Int): ByteArray {
        if (closed) throw IllegalStateException("File reader is closed")
        if (file.sourceVersion != expectedVersion) throw FileReadException.SourceChanged()
        validateFileRange(file.size, offset, length, policy, consumedBytes)
        val bytes = file.readRange(offset, length)
        if (bytes.size != length) throw FileReadException.Failed()
        if (file.sourceVersion != expectedVersion) throw FileReadException.SourceChanged()
        consumedBytes += bytes.size
        return bytes
    }

    /** Permanently closes this reader. */
    override fun close() {
        closed = true
    }
}

/**
 * Resumable transfer position.
 *
 * @property sourceVersion source identity required for resume
 * @property nextOffset next unread byte offset
 */
data class FileTransferCheckpoint(
    val sourceVersion: FileSourceVersion,
    val nextOffset: Long
) {
    init {
        require(nextOffset >= 0) { "File transfer offset must be non-negative" }
    }
}

/**
 * One plaintext chunk.
 *
 * @property offset source byte offset
 * @property bytes caller-consumed plaintext bytes
 * @property storagePartTargetBytes scheduling metadata, not this buffer's actual size
 */
class FileTransferChunk internal constructor(
    val offset: Long,
    val bytes: ByteArray,
    val storagePartTargetBytes: Int
)

/**
 * File-transfer progress.
 *
 * @property transferredBytes acknowledged source bytes
 * @property totalBytes source size
 */
data class FileTransferProgress(
    val transferredBytes: Long,
    val totalBytes: Long
)

/**
 * Streams ordered native ranges to caller-owned encryption/upload work.
 *
 * [consume] must return only after it has copied or acknowledged the supplied bytes. Summon drops
 * its reference before reading the next chunk. Cancellation propagates and aborts an active
 * browser range read.
 */
suspend fun transferFileInChunks(
    file: FileInfo,
    policy: FileReadPolicy,
    checkpoint: FileTransferCheckpoint = FileTransferCheckpoint(file.sourceVersion, 0),
    plaintextChunkBytes: Int = DEFAULT_PLAINTEXT_CHUNK_BYTES,
    storagePartTargetBytes: Int = DEFAULT_STORAGE_PART_BYTES,
    onProgress: (FileTransferProgress) -> Unit = {},
    consume: suspend (FileTransferChunk) -> Unit
): FileTransferCheckpoint {
    require(plaintextChunkBytes > 0 && plaintextChunkBytes <= policy.maxChunkBytes) {
        "Plaintext chunk size exceeds the file read policy"
    }
    require(storagePartTargetBytes > 0) { "Storage part target must be positive" }
    if (checkpoint.sourceVersion != file.sourceVersion) throw FileReadException.SourceChanged()
    if (checkpoint.nextOffset > file.size) throw FileReadException.InvalidRange()
    if (file.size > policy.maxTotalBytes) throw FileReadException.LimitExceeded()

    val reader = BoundedFileReader(file, policy, checkpoint.sourceVersion)
    var offset = checkpoint.nextOffset
    try {
        onProgress(FileTransferProgress(offset, file.size))
        while (offset < file.size) {
            val length = minOf(plaintextChunkBytes.toLong(), file.size - offset).toInt()
            val bytes = reader.read(offset, length)
            consume(FileTransferChunk(offset, bytes, storagePartTargetBytes))
            offset += length
            onProgress(FileTransferProgress(offset, file.size))
        }
        return FileTransferCheckpoint(checkpoint.sourceVersion, offset)
    } finally {
        reader.close()
    }
}
