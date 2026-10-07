package codes.yousef.summon.effects.browser

import codes.yousef.summon.effects.ClipboardAPI
import codes.yousef.summon.effects.CompositionScope
import codes.yousef.summon.effects.onMountWithCleanup
import codes.yousef.summon.runtime.Composable
import kotlin.js.Promise

/**
 * External declaration for the browser's clipboard API
 */
external interface ClipboardNavigator {
    /** The property declaration value. */
    val clipboard: ClipboardAPI?
}

/** Contract for clipboard API. */
external interface ClipboardAPI {
    /**
     * Executes the read text operation.
     *
     * @return The resulting value.
     */
    fun readText(): Promise<String>
    /**
     * Executes the write text operation.
     *
     * @param text The text value.
     * @return The resulting value.
     */
    fun writeText(text: String): Promise<dynamic>
}

/** The property declaration value. */
@JsName("navigator")
/** The property declaration value. */
external val clipboardNavigator: ClipboardNavigator

/**
 * JavaScript implementation of the ClipboardAPI interface
 */
class JsClipboardAPI : ClipboardAPI {

    private var lastClipboardContent: String? = null

    /**
     * Executes the read text operation.
     *
     * @return The resulting value.
     */
    override fun readText(): String {
        // Clipboard API is async, but our interface is sync
        // For now, return cached value or empty string
        // In a real app, you'd want to use a suspend function
        return lastClipboardContent ?: ""
    }

    /**
     * Executes the write text operation.
     *
     * @param text The text value.
     */
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

    /**
     * Returns whether this value has text.
     *
     * @return The resulting value.
     */
    override fun hasText(): Boolean {
        return lastClipboardContent?.isNotEmpty() == true
    }

    /** Clears the operation. */
    override fun clear() {
        writeText("")
        lastClipboardContent = null
    }

    // JS-specific implementation could include additional methods
    /**
     * Executes the write HTML operation.
     *
     * @param html The html value.
     */
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
/** Provides console operations. */
external object console {
    /**
     * Executes the log operation.
     *
     * @param message Message content.
     */
    fun log(message: String)
    /**
     * Executes the error operation.
     *
     * @param message Message content.
     */
    fun error(message: String)
}
