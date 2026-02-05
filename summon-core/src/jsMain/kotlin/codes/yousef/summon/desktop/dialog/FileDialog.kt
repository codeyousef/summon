package codes.yousef.summon.desktop.dialog

import codes.yousef.summon.components.input.FileInfo
import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.get
import kotlin.coroutines.resume

/**
 * Check if showOpenFilePicker is available.
 */
private fun hasFileSystemAccess(): Boolean {
    return js("typeof window.showOpenFilePicker === 'function'") as Boolean
}

actual fun isFileSystemAccessSupported(): Boolean = hasFileSystemAccess()

actual suspend fun showOpenFileDialog(options: FileDialogOptions): List<FileInfo>? {
    return if (hasFileSystemAccess()) {
        showOpenFileDialogModern(options)
    } else {
        showOpenFileDialogFallback(options)
    }
}

private suspend fun showOpenFileDialogModern(options: FileDialogOptions): List<FileInfo>? {
    return suspendCancellableCoroutine { cont ->
        try {
            val jsOptions = js("{}")
            jsOptions["multiple"] = options.multiple

            if (options.types.isNotEmpty()) {
                val jsTypes = js("[]")
                options.types.forEachIndexed { index, type ->
                    val jsType = js("{}")
                    jsType["description"] = type.description
                    val jsAccept = js("{}")
                    type.accept.forEach { (mime, exts) ->
                        jsAccept[mime] = exts.toTypedArray()
                    }
                    jsType["accept"] = jsAccept
                    jsTypes[index] = jsType
                }
                jsOptions["types"] = jsTypes
            }

            val promise = js("window.showOpenFilePicker(jsOptions)")
            promise.then { handles: dynamic ->
                val files = mutableListOf<FileInfo>()
                val handleArray = handles as Array<dynamic>

                var processed = 0
                if (handleArray.isEmpty()) {
                    cont.resume(files)
                    return@then
                }

                handleArray.forEach { handle ->
                    handle.getFile().then { file: File ->
                        files.add(
                            FileInfo(
                                name = file.name,
                                size = file.size.toLong(),
                                type = file.type,
                                jsFile = file
                            )
                        )
                        processed++
                        if (processed == handleArray.size) {
                            cont.resume(files)
                        }
                    }
                }
            }.catch {
                // User cancelled or error
                cont.resume(null)
            }
        } catch (e: Exception) {
            cont.resume(null)
        }
    }
}

private suspend fun showOpenFileDialogFallback(options: FileDialogOptions): List<FileInfo>? {
    return suspendCancellableCoroutine { cont ->
        val input = document.createElement("input") as HTMLInputElement
        input.type = "file"
        input.multiple = options.multiple

        if (options.types.isNotEmpty()) {
            val accept = options.types.flatMap { type ->
                type.accept.flatMap { (_, exts) -> exts }
            }.joinToString(",")
            input.accept = accept
        }

        input.onchange = {
            val files = input.files
            if (files != null && files.length > 0) {
                val result = mutableListOf<FileInfo>()
                for (i in 0 until files.length) {
                    val file = files[i]!!
                    result.add(
                        FileInfo(
                            name = file.name,
                            size = file.size.toLong(),
                            type = file.type,
                            jsFile = file
                        )
                    )
                }
                cont.resume(result)
            } else {
                cont.resume(null)
            }
            input.remove()
        }

        input.click()
    }
}

actual suspend fun showDirectoryPicker(title: String?, startIn: String?): DirectoryHandle? {
    if (!hasFileSystemAccess()) return null

    return suspendCancellableCoroutine { cont ->
        try {
            val promise = js("window.showDirectoryPicker()")
            promise.then { handle: dynamic ->
                cont.resume(JsDirectoryHandle(handle))
            }.catch {
                cont.resume(null)
            }
        } catch (e: Exception) {
            cont.resume(null)
        }
    }
}

actual suspend fun showSaveFileDialog(options: SaveDialogOptions): SaveDialogResult? {
    if (!hasFileSystemAccess()) return null

    return suspendCancellableCoroutine { cont ->
        try {
            val jsOptions = js("{}")
            options.suggestedName?.let { jsOptions["suggestedName"] = it }

            val promise = js("window.showSaveFilePicker(jsOptions)")
            promise.then { handle: dynamic ->
                cont.resume(
                    SaveDialogResult(
                    name = handle.name as String,
                    write = { content ->
                        try {
                            val writable = handle.createWritable()
                            writable.write(content)
                            writable.close()
                            true
                        } catch (e: Exception) {
                            false
                        }
                    }
                ))
            }.catch {
                cont.resume(null)
            }
        } catch (e: Exception) {
            cont.resume(null)
        }
    }
}

private class JsDirectoryHandle(private val handle: dynamic) : DirectoryHandle {
    override val name: String get() = handle.name as String

    override suspend fun getFile(name: String): FileInfo? {
        return suspendCancellableCoroutine { cont ->
            try {
                handle.getFileHandle(name).then { fileHandle: dynamic ->
                    fileHandle.getFile().then { file: File ->
                        cont.resume(
                            FileInfo(
                                name = file.name,
                                size = file.size.toLong(),
                                type = file.type,
                                jsFile = file
                            )
                        )
                    }
                }.catch {
                    cont.resume(null)
                }
            } catch (e: Exception) {
                cont.resume(null)
            }
        }
    }

    override suspend fun listFiles(): List<String> {
        // Directory listing requires async iteration which is complex in JS interop
        // Return empty list as a stub - full implementation would need external JS helper
        return emptyList()
    }
}
