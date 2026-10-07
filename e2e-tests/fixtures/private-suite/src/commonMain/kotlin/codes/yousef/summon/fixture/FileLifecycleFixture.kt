package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.BoundedFileReader
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.input.FileReadException
import codes.yousef.summon.components.input.FileReadPolicy
import codes.yousef.summon.components.input.FileTransferCheckpoint
import codes.yousef.summon.components.input.FileUploadController
import codes.yousef.summon.components.input.ManagedFileUpload
import codes.yousef.summon.components.input.transferFileInChunks
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.media.Video
import codes.yousef.summon.effects.browser.MediaCapabilityState
import codes.yousef.summon.effects.browser.OwnedObjectUrl
import codes.yousef.summon.effects.browser.createVerifiedObjectUrl
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.runtime.LaunchedEffect
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf
import kotlinx.coroutines.delay

private enum class FileFixtureOperation { IDLE, READ_EDGES, TRANSFER, VERIFY_MEDIA, FAIL_INTEGRITY, UNSUPPORTED_MEDIA, RESUME }
private data class FileFixtureCommand(val sequence: Int, val operation: FileFixtureOperation)

class FileLifecycleFixture {
    @Composable
    fun Content() {
        val controller = remember { FileUploadController(maxFileBytes = 20L * 1024 * 1024, maxOperationBytes = 32L * 1024 * 1024) }
        val command = remember { mutableStateOf(FileFixtureCommand(0, FileFixtureOperation.IDLE)) }
        val result = remember { mutableStateOf("Idle") }
        val checkpoint = remember { mutableStateOf<FileTransferCheckpoint?>(null) }
        val objectUrl = remember { mutableStateOf<OwnedObjectUrl?>(null) }

        DisposableEffect(objectUrl.value) {
            val owned = objectUrl.value
            return@DisposableEffect { owned?.close() }
        }

        LaunchedEffect(command.value) {
            val entry = controller.entries.value.lastOrNull()
            when (command.value.operation) {
                FileFixtureOperation.IDLE -> Unit
                FileFixtureOperation.READ_EDGES -> {
                    if (entry == null) return@LaunchedEffect
                    val reader = BoundedFileReader(
                        entry.file,
                        FileReadPolicy(maxChunkBytes = 4, maxTotalBytes = 4)
                    )
                    try {
                        val empty = reader.read(0, 0)
                        val finalLength = minOf(2L, entry.file.size).toInt()
                        val final = reader.read(entry.file.size - finalLength, finalLength)
                        result.value = "Ranges: ${empty.size}:${final.joinToString(",") { (it.toInt() and 0xff).toString() }}"
                    } finally {
                        reader.close()
                    }
                }
                FileFixtureOperation.TRANSFER -> {
                    if (entry == null) return@LaunchedEffect
                    controller.updateProgress(entry.id, 0)
                    var transferCompleted = false
                    try {
                        val completed = transferFileInChunks(
                            file = entry.file,
                            policy = FileReadPolicy(maxChunkBytes = 4 * 1024 * 1024, maxTotalBytes = entry.file.size),
                            plaintextChunkBytes = 4 * 1024 * 1024,
                            onProgress = { controller.updateProgress(entry.id, it.transferredBytes) }
                        ) { delay(1_000) }
                        checkpoint.value = completed
                        transferCompleted = true
                        controller.complete(entry.id)
                        result.value = "Transfer complete: ${completed.nextOffset}"
                    } finally {
                        if (!transferCompleted) {
                            controller.cancel(entry.id)
                            result.value = "Transfer canceled"
                        }
                    }
                }
                FileFixtureOperation.VERIFY_MEDIA -> {
                    if (entry == null) return@LaunchedEffect
                    val length = entry.file.size.toInt()
                    val reader = BoundedFileReader(entry.file, FileReadPolicy(maxChunkBytes = length.coerceAtLeast(1), maxTotalBytes = length.toLong()))
                    val bytes = try { reader.read(0, length) } finally { reader.close() }
                    objectUrl.value?.close()
                    val state = createVerifiedObjectUrl(bytes, entry.file.type.ifBlank { "application/octet-stream" }) { true }
                    objectUrl.value = (state as MediaCapabilityState.Ready).url
                    result.value = "Media ready"
                }
                FileFixtureOperation.FAIL_INTEGRITY -> {
                    objectUrl.value?.close()
                    objectUrl.value = null
                    val state = createVerifiedObjectUrl(byteArrayOf(1), "video/mp4") { false }
                    result.value = if (state == MediaCapabilityState.IntegrityFailed) "Integrity failed" else "Unexpected"
                }
                FileFixtureOperation.UNSUPPORTED_MEDIA -> {
                    objectUrl.value?.close()
                    objectUrl.value = null
                    val state = createVerifiedObjectUrl(
                        bytes = byteArrayOf(1),
                        mimeType = "video/x-private",
                        isMimeTypeSupported = { false }
                    ) { true }
                    result.value = if (state is MediaCapabilityState.Unsupported) {
                        "Unsupported media: ${state.mimeType}"
                    } else {
                        "Unexpected"
                    }
                }
                FileFixtureOperation.RESUME -> {
                    if (entry == null || checkpoint.value == null) return@LaunchedEffect
                    try {
                        transferFileInChunks(
                            file = entry.file,
                            policy = FileReadPolicy(maxChunkBytes = 4, maxTotalBytes = entry.file.size),
                            checkpoint = checkpoint.value!!,
                            plaintextChunkBytes = 4
                        ) { }
                        result.value = "Resume accepted"
                    } catch (_: FileReadException.SourceChanged) {
                        controller.requireReselection(entry.id)
                        result.value = "New version required"
                    }
                }
            }
        }

        Column {
            Text("File lifecycle fixture", Modifier().attribute("data-testid", "file-title"))
            Text(result.value, Modifier().attribute("data-testid", "file-result"))
            ManagedFileUpload(
                controller = controller,
                multiple = true,
                accept = "application/octet-stream,video/mp4",
                label = "Private attachments",
                onCancel = { command.value = FileFixtureCommand(command.value.sequence + 1, FileFixtureOperation.IDLE) },
                onRetry = { }
            )
            Button(onClick = { command.value = command.value.next(FileFixtureOperation.READ_EDGES) }, label = "Read edge ranges")
            Button(onClick = { command.value = command.value.next(FileFixtureOperation.TRANSFER) }, label = "Transfer selected")
            Button(onClick = {
                controller.entries.value.lastOrNull()?.let { controller.cancel(it.id) }
                command.value = command.value.next(FileFixtureOperation.IDLE)
            }, label = "Cancel transfer")
            Button(onClick = { command.value = command.value.next(FileFixtureOperation.VERIFY_MEDIA) }, label = "Create verified media")
            Button(onClick = { command.value = command.value.next(FileFixtureOperation.FAIL_INTEGRITY) }, label = "Fail media integrity")
            Button(onClick = { command.value = command.value.next(FileFixtureOperation.UNSUPPORTED_MEDIA) }, label = "Try unsupported media")
            Button(onClick = { command.value = command.value.next(FileFixtureOperation.RESUME) }, label = "Resume selected")
            Button(onClick = {
                objectUrl.value?.close()
                objectUrl.value = null
                result.value = "Media locked"
            }, label = "Lock media")
            objectUrl.value?.let { url ->
                Video(src = url.value, ariaLabel = "Verified private media", modifier = Modifier().attribute("data-testid", "verified-media"))
            }
        }
    }
}

private fun FileFixtureCommand.next(operation: FileFixtureOperation): FileFixtureCommand =
    FileFixtureCommand(sequence + 1, operation)
