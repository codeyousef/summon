package codes.yousef.summon.components.input

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ariaAttribute
import codes.yousef.summon.modifier.role
import codes.yousef.summon.runtime.key
import codes.yousef.summon.state.State
import codes.yousef.summon.state.mutableStateOf

sealed interface FileUploadStatus {
    data object Selected : FileUploadStatus
    data class Transferring(val transferredBytes: Long, val totalBytes: Long) : FileUploadStatus
    data object Canceled : FileUploadStatus
    data object Complete : FileUploadStatus
    data object ReselectionRequired : FileUploadStatus
    data object QuotaExceeded : FileUploadStatus
    data class PartialImport(val importedRecords: Long, val oversizedRecords: Long) : FileUploadStatus
    data class Rejected(val reason: FileUploadRejection) : FileUploadStatus
    data class Failed(val reason: FileUploadFailure) : FileUploadStatus
}

enum class FileUploadRejection { FILE_LIMIT, OPERATION_LIMIT }
enum class FileUploadFailure { READ, WORKER, INTEGRITY, UNSUPPORTED }

data class FileUploadEntry(
    val id: String,
    val file: FileInfo,
    val status: FileUploadStatus
)

/** Controlled selection and progress model. It never starts a native read itself. */
class FileUploadController(
    val maxFileBytes: Long,
    val maxOperationBytes: Long
) {
    private val mutableEntries = mutableStateOf<List<FileUploadEntry>>(emptyList())
    private var nextId = 0L
    val entries: State<List<FileUploadEntry>> get() = mutableEntries

    init {
        require(maxFileBytes >= 0) { "File limit must be non-negative" }
        require(maxOperationBytes >= 0) { "File operation limit must be non-negative" }
    }

    fun select(files: List<FileInfo>) {
        var acceptedBytes = mutableEntries.value
            .filter { it.status !is FileUploadStatus.Rejected }
            .sumOf { it.file.size }
        val additions = files.map { file ->
            val status = when {
                file.size < 0 || file.size > maxFileBytes ->
                    FileUploadStatus.Rejected(FileUploadRejection.FILE_LIMIT)
                acceptedBytes > maxOperationBytes - file.size ->
                    FileUploadStatus.Rejected(FileUploadRejection.OPERATION_LIMIT)
                else -> {
                    acceptedBytes += file.size
                    FileUploadStatus.Selected
                }
            }
            FileUploadEntry("file-${nextId++}", file, status)
        }
        mutableEntries.value = mutableEntries.value + additions
    }

    fun updateProgress(id: String, transferredBytes: Long) {
        update(id) { entry ->
            require(transferredBytes in 0..entry.file.size) { "Invalid file transfer progress" }
            entry.copy(status = FileUploadStatus.Transferring(transferredBytes, entry.file.size))
        }
    }

    fun complete(id: String) = update(id) { it.copy(status = FileUploadStatus.Complete) }
    fun cancel(id: String) = update(id) { it.copy(status = FileUploadStatus.Canceled) }
    fun requireReselection(id: String) = update(id) { it.copy(status = FileUploadStatus.ReselectionRequired) }
    fun quotaExceeded(id: String) = update(id) { it.copy(status = FileUploadStatus.QuotaExceeded) }
    fun fail(id: String, reason: FileUploadFailure) = update(id) { it.copy(status = FileUploadStatus.Failed(reason)) }
    fun reportPartialImport(id: String, importedRecords: Long, oversizedRecords: Long) = update(id) {
        require(importedRecords >= 0 && oversizedRecords >= 0) { "Import counts must be non-negative" }
        it.copy(status = FileUploadStatus.PartialImport(importedRecords, oversizedRecords))
    }
    fun retry(id: String) = update(id) {
        require(
            it.status == FileUploadStatus.Canceled ||
                it.status == FileUploadStatus.QuotaExceeded ||
                it.status is FileUploadStatus.Failed
        ) { "File upload entry is not retryable" }
        it.copy(status = FileUploadStatus.Selected)
    }
    fun remove(id: String) {
        mutableEntries.value = mutableEntries.value.filterNot { it.id == id }
    }

    private fun update(id: String, transform: (FileUploadEntry) -> FileUploadEntry) {
        var found = false
        mutableEntries.value = mutableEntries.value.map {
            if (it.id == id) {
                found = true
                transform(it)
            } else it
        }
        require(found) { "Unknown file upload entry" }
    }
}

/** File input plus accessible controlled cancel/remove/retry/progress surfaces. */
@Composable
fun ManagedFileUpload(
    controller: FileUploadController,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    multiple: Boolean = false,
    accept: String? = null,
    buttonLabel: String = "Select files",
    label: String = "Files",
    onCancel: (FileUploadEntry) -> Unit = {},
    onRetry: (FileUploadEntry) -> Unit = {}
) {
    Column(modifier) {
        FileUpload(
            onFilesSelected = controller::select,
            enabled = enabled,
            multiple = multiple,
            accept = accept,
            buttonLabel = buttonLabel,
            label = label
        )
        controller.entries.value.forEach { entry ->
            key(entry.id) {
                Row(Modifier().role("group").ariaAttribute("label", entry.file.name)) {
                    Text(entry.file.name)
                    Text(statusText(entry.status), Modifier().role("status").ariaAttribute("live", "polite"))
                    when (entry.status) {
                        is FileUploadStatus.Transferring -> Button(
                            label = "Cancel ${entry.file.name}",
                            onClick = { controller.cancel(entry.id); onCancel(entry) }
                        )
                        FileUploadStatus.Canceled,
                        FileUploadStatus.QuotaExceeded,
                        is FileUploadStatus.Failed -> Button(
                            label = "Retry ${entry.file.name}",
                            onClick = { controller.retry(entry.id); onRetry(entry) }
                        )
                        else -> Unit
                    }
                    Button(label = "Remove ${entry.file.name}", onClick = { controller.remove(entry.id) })
                }
            }
        }
    }
}

private fun statusText(status: FileUploadStatus): String = when (status) {
    FileUploadStatus.Selected -> "Selected"
    is FileUploadStatus.Transferring -> "${status.transferredBytes} of ${status.totalBytes} bytes"
    FileUploadStatus.Canceled -> "Canceled"
    FileUploadStatus.Complete -> "Complete"
    FileUploadStatus.ReselectionRequired -> "Source changed; select the new version"
    FileUploadStatus.QuotaExceeded -> "Storage quota exceeded"
    is FileUploadStatus.PartialImport ->
        "Imported ${status.importedRecords}; oversized ${status.oversizedRecords}"
    is FileUploadStatus.Rejected -> when (status.reason) {
        FileUploadRejection.FILE_LIMIT -> "File exceeds the file limit"
        FileUploadRejection.OPERATION_LIMIT -> "Selection exceeds the operation limit"
    }
    is FileUploadStatus.Failed -> when (status.reason) {
        FileUploadFailure.READ -> "Read failed"
        FileUploadFailure.WORKER -> "Processing worker failed"
        FileUploadFailure.INTEGRITY -> "Integrity check failed"
        FileUploadFailure.UNSUPPORTED -> "File type is unsupported"
    }
}
