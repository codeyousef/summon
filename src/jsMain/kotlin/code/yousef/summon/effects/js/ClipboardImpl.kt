package code.yousef.summon.effects.js

import code.yousef.summon.effects.ClipboardAPI
import code.yousef.summon.effects.CompositionScope
import code.yousef.summon.effects.onMountWithCleanup
import code.yousef.summon.runtime.Composable

/**
 * JavaScript implementation of the ClipboardAPI interface
 */
class JsClipboardAPI : ClipboardAPI {
    override fun readText(): String {
        // In a real implementation, this would use navigator.clipboard.readText()
        // For now, just return an empty string
        return ""
    }
    
    override fun writeText(text: String) {
        // In a real implementation, this would use navigator.clipboard.writeText(text)
        console.log("Writing to clipboard: $text")
    }
    
    override fun hasText(): Boolean {
        // In a real implementation, this would check the clipboard contents
        return false
    }
    
    override fun clear() {
        // In a real implementation, this would clear the clipboard
        writeText("")
    }
    
    // JS-specific implementation could include additional methods
    fun writeHtml(html: String) {
        // Implementation for writing HTML to clipboard
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
} 