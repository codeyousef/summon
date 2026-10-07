package codes.yousef.summon.components.input

import java.io.RandomAccessFile
import java.nio.file.Files
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FileTransferJvmTest {
    @Test
    fun readsZeroEdgeAndFinalRangesWithOperationAccounting() = runTest {
        val path = Files.createTempFile("summon-range-", ".bin")
        try {
            Files.write(path, ByteArray(10) { it.toByte() })
            val native = path.toFile()
            val file = FileInfo("opaque", native.length(), "application/octet-stream", native)
            val reader = BoundedFileReader(file, FileReadPolicy(maxChunkBytes = 4, maxTotalBytes = 8))

            assertContentEquals(ByteArray(0), reader.read(0, 0))
            assertContentEquals(byteArrayOf(6, 7, 8, 9), reader.read(6, 4))
            assertContentEquals(byteArrayOf(0, 1, 2, 3), reader.read(0, 4))
            assertEquals(8, reader.bytesRead)
            assertFailsWith<FileReadException.LimitExceeded> { reader.read(4, 1) }
        } finally {
            Files.deleteIfExists(path)
        }
    }

    @Test
    fun detectsChangedSourceBeforeResume() = runTest {
        val path = Files.createTempFile("summon-version-", ".bin")
        try {
            Files.write(path, byteArrayOf(1, 2, 3, 4))
            val native = path.toFile()
            val file = FileInfo("opaque", native.length(), "application/octet-stream", native)
            val reader = BoundedFileReader(file, FileReadPolicy(maxChunkBytes = 4, maxTotalBytes = 8))
            assertContentEquals(byteArrayOf(1, 2), reader.read(0, 2))

            native.appendBytes(byteArrayOf(5))
            assertFailsWith<FileReadException.SourceChanged> { reader.read(2, 2) }
        } finally {
            Files.deleteIfExists(path)
        }
    }

    @Test
    fun longOffsetsDoNotNarrowForLargeSparseSources() = runTest {
        val path = Files.createTempFile("summon-large-range-", ".bin")
        try {
            val finalOffset = 3L * 1024 * 1024 * 1024
            RandomAccessFile(path.toFile(), "rw").use { output ->
                output.seek(finalOffset)
                output.write(0x5a)
            }
            val native = path.toFile()
            val file = FileInfo("opaque", native.length(), "application/octet-stream", native)
            val reader = BoundedFileReader(file, FileReadPolicy(maxChunkBytes = 1, maxTotalBytes = 1))
            assertContentEquals(byteArrayOf(0x5a), reader.read(finalOffset, 1))
        } finally {
            Files.deleteIfExists(path)
        }
    }

    @Test
    fun chunkTransferPreservesOffsetsFinalChunkAndStoragePartTarget() = runTest {
        val path = Files.createTempFile("summon-chunks-", ".bin")
        try {
            Files.write(path, ByteArray(10) { it.toByte() })
            val native = path.toFile()
            val file = FileInfo("opaque", native.length(), "application/octet-stream", native)
            val chunks = mutableListOf<Triple<Long, ByteArray, Int>>()
            val progress = mutableListOf<Long>()

            val checkpoint = transferFileInChunks(
                file = file,
                policy = FileReadPolicy(maxChunkBytes = 4, maxTotalBytes = 10),
                plaintextChunkBytes = 4,
                storagePartTargetBytes = 7,
                onProgress = { progress += it.transferredBytes }
            ) { chunk ->
                chunks += Triple(chunk.offset, chunk.bytes.copyOf(), chunk.storagePartTargetBytes)
            }

            assertEquals(listOf(0L, 4L, 8L), chunks.map { it.first })
            assertEquals(listOf(4, 4, 2), chunks.map { it.second.size })
            assertEquals(listOf(7, 7, 7), chunks.map { it.third })
            assertEquals(listOf(0L, 4L, 8L, 10L), progress)
            assertEquals(10, checkpoint.nextOffset)
        } finally {
            Files.deleteIfExists(path)
        }
    }

    @Test
    fun controllerRejectsBeforeReadAndKeepsActionableStatesVisible() {
        val controller = FileUploadController(maxFileBytes = 5, maxOperationBytes = 7)
        controller.select(
            listOf(
                FileInfo("one", 4, "application/octet-stream"),
                FileInfo("oversized", 6, "application/octet-stream"),
                FileInfo("aggregate", 4, "application/octet-stream")
            )
        )

        val entries = controller.entries.value
        assertEquals(FileUploadStatus.Selected, entries[0].status)
        assertEquals(FileUploadStatus.Rejected(FileUploadRejection.FILE_LIMIT), entries[1].status)
        assertEquals(FileUploadStatus.Rejected(FileUploadRejection.OPERATION_LIMIT), entries[2].status)

        controller.updateProgress(entries[0].id, 2)
        assertEquals(FileUploadStatus.Transferring(2, 4), controller.entries.value[0].status)
        controller.requireReselection(entries[0].id)
        assertEquals(FileUploadStatus.ReselectionRequired, controller.entries.value[0].status)
        assertFailsWith<IllegalArgumentException> { controller.retry(entries[0].id) }
        controller.quotaExceeded(entries[0].id)
        assertEquals(FileUploadStatus.QuotaExceeded, controller.entries.value[0].status)
        controller.reportPartialImport(entries[0].id, importedRecords = 3, oversizedRecords = 1)
        assertEquals(FileUploadStatus.PartialImport(3, 1), controller.entries.value[0].status)
        controller.remove(entries[0].id)
        assertEquals(2, controller.entries.value.size)
    }
}
