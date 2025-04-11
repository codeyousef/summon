package code.yousef.summon.effects

/**
 * Interface for clipboard operations that can be implemented by platform-specific code
 */
interface ClipboardAPI {
    /**
     * Read text from the clipboard
     * @return The text content from clipboard, or empty string if not available
     */
    fun readText(): String
    
    /**
     * Write text to the clipboard
     * @param text The text content to write to clipboard
     */
    fun writeText(text: String)
    
    /**
     * Check if the clipboard contains text
     * @return True if the clipboard contains text, false otherwise
     */
    fun hasText(): Boolean
    
    /**
     * Clear the clipboard contents
     */
    fun clear()
} 