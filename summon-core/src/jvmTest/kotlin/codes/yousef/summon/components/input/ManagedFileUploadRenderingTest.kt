package codes.yousef.summon.components.input

import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains

class ManagedFileUploadRenderingTest {
    private fun file(name: String, size: Long = 1) = FileInfo(name, size, "application/octet-stream")

    @Test
    fun everyUploadStatusHasAccessibleTextAndApplicableActions() {
        val controller = FileUploadController(maxFileBytes = 2, maxOperationBytes = 11)
        controller.select((0..10).map { file("file-$it") } + file("too-large", 3) + file("over-operation"))
        val ids = controller.entries.value.map { it.id }
        controller.updateProgress(ids[1], 1)
        controller.cancel(ids[2])
        controller.complete(ids[3])
        controller.requireReselection(ids[4])
        controller.quotaExceeded(ids[5])
        controller.reportPartialImport(ids[6], 7, 2)
        FileUploadFailure.entries.forEachIndexed { index, failure -> controller.fail(ids[7 + index], failure) }

        val html = PlatformRenderer().renderComposableRoot {
            ManagedFileUpload(controller, multiple = true, accept = ".bin", label = "Private files")
        }
        listOf(
            "Selected",
            "1 of 1 bytes",
            "Canceled",
            "Complete",
            "Source changed; select the new version",
            "Storage quota exceeded",
            "Imported 7; oversized 2",
            "Read failed",
            "Processing worker failed",
            "Integrity check failed",
            "File type is unsupported",
            "File exceeds the file limit",
            "Selection exceeds the operation limit",
            "Retry file-2",
            "Cancel file-1",
            "Remove file-0",
        ).forEach { assertContains(html, it) }
        assertContains(html, "role=\"status\"")
        assertContains(html, "aria-live=\"polite\"")
    }
}
