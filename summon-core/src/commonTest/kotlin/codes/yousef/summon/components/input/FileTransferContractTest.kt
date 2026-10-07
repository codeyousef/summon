package codes.yousef.summon.components.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FileTransferContractTest {
    private val policy = FileReadPolicy(maxChunkBytes = 4, maxTotalBytes = 6)

    @Test
    fun validatesRangeOverflowChunkAndOperationBoundsBeforeReading() {
        validateFileRange(fileSize = 10, offset = 0, length = 0, policy = policy)
        validateFileRange(fileSize = 10, offset = 6, length = 4, policy = policy)

        assertFailsWith<FileReadException.InvalidRange> {
            validateFileRange(fileSize = 10, offset = -1, length = 1, policy = policy)
        }
        assertFailsWith<FileReadException.InvalidRange> {
            validateFileRange(fileSize = 10, offset = Long.MAX_VALUE, length = 1, policy = policy)
        }
        assertFailsWith<FileReadException.InvalidRange> {
            validateFileRange(fileSize = 10, offset = 9, length = 2, policy = policy)
        }
        assertFailsWith<FileReadException.LimitExceeded> {
            validateFileRange(fileSize = 10, offset = 0, length = 5, policy = policy)
        }
        assertFailsWith<FileReadException.LimitExceeded> {
            validateFileRange(fileSize = 10, offset = 4, length = 3, policy = policy, consumedBytes = 4)
        }
        listOf(
            { validateFileRange(fileSize = -1, offset = 0, length = 0, policy = policy) },
            { validateFileRange(fileSize = 10, offset = 0, length = -1, policy = policy) },
            { validateFileRange(fileSize = 10, offset = 0, length = 1, policy = policy, consumedBytes = -1) }
        ).forEach { invalid ->
            assertFailsWith<FileReadException.InvalidRange> { invalid() }
        }
        assertFailsWith<FileReadException.LimitExceeded> {
            validateFileRange(
                fileSize = 10,
                offset = 0,
                length = 1,
                policy = FileReadPolicy(maxChunkBytes = 1, maxTotalBytes = Long.MAX_VALUE),
                consumedBytes = Long.MAX_VALUE
            )
        }
    }

    @Test
    fun cryptoChunksAndStoragePartsRemainDistinctDefaults() {
        assertEquals(4 * 1024 * 1024, DEFAULT_PLAINTEXT_CHUNK_BYTES)
        assertEquals(16 * 1024 * 1024, DEFAULT_STORAGE_PART_BYTES)
    }

    @Test
    fun rejectsInvalidPolicies() {
        assertFailsWith<IllegalArgumentException> { FileReadPolicy(maxChunkBytes = 0, maxTotalBytes = 1) }
        assertFailsWith<IllegalArgumentException> { FileReadPolicy(maxTotalBytes = -1) }
    }
}
