package codes.yousef.summon.components.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FileUploadControllerContractTest {
    private fun file(name: String, size: Long) = FileInfo(name, size, "application/octet-stream")

    @Test
    fun selectionEnforcesPerFileAndCumulativeLimitsWithoutCountingRejectedFiles() {
        assertFailsWith<IllegalArgumentException> { FileUploadController(-1, 1) }
        assertFailsWith<IllegalArgumentException> { FileUploadController(1, -1) }
        val controller = FileUploadController(maxFileBytes = 5, maxOperationBytes = 7)
        controller.select(listOf(file("negative", -1), file("large", 6), file("first", 4), file("second", 4)))
        val entries = controller.entries.value
        assertEquals(listOf("file-0", "file-1", "file-2", "file-3"), entries.map { it.id })
        assertEquals(FileUploadRejection.FILE_LIMIT, assertIs<FileUploadStatus.Rejected>(entries[0].status).reason)
        assertEquals(FileUploadRejection.FILE_LIMIT, assertIs<FileUploadStatus.Rejected>(entries[1].status).reason)
        assertEquals(FileUploadStatus.Selected, entries[2].status)
        assertEquals(FileUploadRejection.OPERATION_LIMIT, assertIs<FileUploadStatus.Rejected>(entries[3].status).reason)
    }

    @Test
    fun lifecycleTransitionsValidateProgressRetryImportCountsAndUnknownIds() {
        val controller = FileUploadController(10, 100)
        controller.select(listOf(file("a", 10), file("b", 8), file("c", 6)))
        val (a, b, c) = controller.entries.value.map { it.id }

        controller.updateProgress(a, 5)
        assertEquals(FileUploadStatus.Transferring(5, 10), controller.entries.value[0].status)
        assertFailsWith<IllegalArgumentException> { controller.updateProgress(a, 11) }
        assertFailsWith<IllegalArgumentException> { controller.updateProgress("missing", 0) }
        assertFailsWith<IllegalArgumentException> { controller.retry(a) }

        controller.cancel(a)
        controller.retry(a)
        controller.quotaExceeded(b)
        controller.retry(b)
        controller.fail(c, FileUploadFailure.INTEGRITY)
        controller.retry(c)
        assertTrue(controller.entries.value.all { it.status == FileUploadStatus.Selected })

        controller.complete(a)
        controller.requireReselection(b)
        controller.reportPartialImport(c, 3, 2)
        assertFailsWith<IllegalArgumentException> { controller.reportPartialImport(c, -1, 0) }
        controller.remove(b)
        assertEquals(listOf(a, c), controller.entries.value.map { it.id })
    }
}
