package code.yousef.summon.effects.browser

import code.yousef.summon.effects.ClipboardAPI
import code.yousef.summon.effects.CompositionScope
import code.yousef.summon.effects.onMountWithCleanup
import code.yousef.summon.runtime.Composable
import kotlin.js.Promise

/**
 * External declaration for the browser's clipboard API
 */
external interface ClipboardNavigator {
    val clipboard: ClipboardAPI?
}

external interface ClipboardAPI {
    fun readText(): Promise<String>
    fun writeText(text: String): Promise<dynamic>
}

@JsName("navigator")
external val clipboardNavigator: ClipboardNavigator

/**
 * JavaScript implementation of the ClipboardAPI interface
 */
class JsClipboardAPI : ClipboardAPI {

    private var lastClipboardContent: String? = null

    override fun readText(): String {
        // Clipboard API is async, but our interface is sync
        // For now, return cached value or empty string
        // In a real app, you'd want to use a suspend function
        return lastClipboardContent ?: ""
    }

    override fun writeText(text: String) {
        // Check if clipboard API is available
        if (isClipboardAvailable()) {
            try {
                js("navigator.clipboard.writeText(text).then(function() { console.log('Successfully wrote to clipboard: ' + text); }, function(error) { console.error('Failed to write to clipboard: ' + error); })")
                lastClipboardContent = text
            } catch (e: Throwable) {
                console.error("Failed to write to clipboard: ${e.message}")
                fallbackCopyToClipboard(text)
            }
        } else {
            // Fallback for older browsers or insecure contexts
            fallbackCopyToClipboard(text)
        }
    }

    override fun hasText(): Boolean {
        return lastClipboardContent?.isNotEmpty() == true
    }

    override fun clear() {
        writeText("")
        lastClipboardContent = null
    }

    // JS-specific implementation could include additional methods
    fun writeHtml(html: String) {
        // Implementation for writing HTML to clipboard
        // This would require using ClipboardItem API
    }

    /**
     * Check if the clipboard API is available
     */
    private fun isClipboardAvailable(): Boolean {
        return js("typeof navigator !== 'undefined' && navigator.clipboard !== undefined") as Boolean
    }

    /**
     * Fallback method for copying text when clipboard API is not available
     */
    private fun fallbackCopyToClipboard(text: String) {
        // Create a temporary textarea element
        val textArea = js("document.createElement('textarea')")
        textArea.value = text
        textArea.style.position = "fixed"
        textArea.style.left = "-999999px"
        textArea.style.top = "-999999px"

        js("document.body.appendChild(textArea)")
        textArea.focus()
        textArea.select()

        try {
            val successful = js("document.execCommand('copy')") as Boolean
            if (successful) {
                lastClipboardContent = text
                console.log("Fallback copy successful")
            } else {
                console.error("Fallback copy failed")
            }
        } catch (e: Throwable) {
            console.error("Fallback copy error: ${e.message}")
        } finally {
            js("document.body.removeChild(textArea)")
        }
    }
}

/**
 * Effect for clipboard API (JS implementation)
 *
 * @return ClipboardAPI object for reading/writing to the clipboard
 */
@Composable
fun CompositionScope.useClipboard(): ClipboardAPI {
    val clipboard = JsClipboardAPI()

    onMountWithCleanup {
        // Set up any necessary event listeners or resources

        // Return cleanup function
        {
            // Clean up any resources
        }
    }

    return clipboard
}

// JS Console logging utility
external object console {
    fun log(message: String)
    fun error(message: String)
} 
