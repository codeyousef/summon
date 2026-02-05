package codes.yousef.summon.desktop.pip

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer
import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * JS implementation of PipWindow using Document Picture-in-Picture API.
 */
private class JsPipWindow(
    private val pipWindow: dynamic
) : PipWindow {
    override val width: Int
        get() = try {
            (pipWindow.innerWidth as? Number)?.toInt() ?: 300
        } catch (e: Exception) {
            300
        }

    override val height: Int
        get() = try {
            (pipWindow.innerHeight as? Number)?.toInt() ?: 200
        } catch (e: Exception) {
            200
        }

    override val isOpen: Boolean
        get() = try {
            !pipWindow.closed
        } catch (e: Exception) {
            false
        }

    override fun close() {
        try {
            pipWindow.close()
        } catch (e: Exception) {
            // Ignore errors when closing
        }
    }

    override fun focus() {
        try {
            pipWindow.focus()
        } catch (e: Exception) {
            // Ignore errors when focusing
        }
    }

    fun getDocument(): dynamic = pipWindow.document
}

/**
 * Checks if Document Picture-in-Picture API is available.
 */
private fun hasDocumentPipApi(): Boolean {
    return try {
        js("'documentPictureInPicture' in window") as Boolean
    } catch (e: Exception) {
        false
    }
}

actual fun isPictureInPictureSupported(): Boolean = hasDocumentPipApi()

actual suspend fun requestPictureInPicture(
    options: PipOptions
): PipResult {
    if (!hasDocumentPipApi()) {
        return PipResult.NotSupported
    }

    return suspendCancellableCoroutine { cont ->
        try {
            val jsOptions = js("{}")
            jsOptions["width"] = options.width
            jsOptions["height"] = options.height

            val promise = js("window.documentPictureInPicture.requestWindow(jsOptions)")
            promise.then { pipWindow: dynamic ->
                // Copy stylesheets if requested
                if (options.copyStyleSheets) {
                    copyStyleSheets(pipWindow.document)
                }

                cont.resume(PipResult.Success(JsPipWindow(pipWindow)))
            }.catch { error: dynamic ->
                val errorMessage = error?.message?.toString() ?: "Unknown error"
                if (errorMessage.contains("denied") || errorMessage.contains("permission")) {
                    cont.resume(PipResult.UserDenied)
                } else {
                    cont.resume(PipResult.Error(errorMessage))
                }
            }
        } catch (e: Exception) {
            cont.resume(PipResult.Error(e.message ?: "Failed to request PiP"))
        }
    }
}

/**
 * Copies stylesheets from the main document to the PiP document.
 */
private fun copyStyleSheets(pipDocument: dynamic) {
    try {
        // Copy linked stylesheets
        val links = document.querySelectorAll("link[rel='stylesheet']")
        for (i in 0 until links.length) {
            val link = links.item(i)
            val clone = pipDocument.createElement("link")
            clone.rel = "stylesheet"
            clone.href = link.asDynamic().href
            pipDocument.head.appendChild(clone)
        }

        // Copy style elements
        val styles = document.querySelectorAll("style")
        for (i in 0 until styles.length) {
            val style = styles.item(i)
            val clone = pipDocument.createElement("style")
            clone.textContent = style?.textContent
            pipDocument.head.appendChild(clone)
        }
    } catch (e: Exception) {
        // Best effort - ignore stylesheet copy errors
    }
}

@Composable
actual fun PictureInPictureContent(
    window: PipWindow,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val renderer = LocalPlatformRenderer.current

    // Get the PiP document body
    val pipWindow = window as? JsPipWindow ?: return
    val pipDocument = pipWindow.getDocument()

    if (pipDocument == null || !window.isOpen) {
        return
    }

    // Render content into the PiP window's body
    // This is a simplified implementation - full support would need
    // a separate composition root in the PiP window
    try {
        val body = pipDocument.body
        if (body != null) {
            // For now, render a simple container
            // Full composable support would require creating a new PlatformRenderer
            // targeting the PiP window's document
            val container = pipDocument.createElement("div")
            container.id = "summon-pip-content"
            body.appendChild(container)

            // Note: Full composable rendering into PiP would require:
            // 1. Creating a new PlatformRenderer for the PiP document
            // 2. Running composition in that context
            // This is a placeholder for the basic structure
        }
    } catch (e: Exception) {
        // Handle render errors
    }
}
