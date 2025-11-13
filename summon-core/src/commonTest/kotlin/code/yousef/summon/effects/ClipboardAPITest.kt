package codes.yousef.summon.effects

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Mock implementation of ClipboardAPI for testing
private class MockClipboardAPI : ClipboardAPI {
    private var clipboardContent: String = ""
    
    override fun readText(): String {
        return clipboardContent
    }
    
    override fun writeText(text: String) {
        clipboardContent = text
    }
    
    override fun hasText(): Boolean {
        return clipboardContent.isNotEmpty()
    }
    
    override fun clear() {
        clipboardContent = ""
    }
}

class ClipboardAPITest {
    
    @Test
    fun testWriteAndReadText() {
        val clipboard = MockClipboardAPI()
        val testText = "Hello, clipboard!"
        
        // Write text to clipboard
        clipboard.writeText(testText)
        
        // Read text from clipboard
        val readText = clipboard.readText()
        
        // Verify text was written and read correctly
        assertEquals(testText, readText, "Text read from clipboard should match text written to clipboard")
    }
    
    @Test
    fun testHasText() {
        val clipboard = MockClipboardAPI()
        
        // Initially, clipboard should be empty
        assertFalse(clipboard.hasText(), "Clipboard should initially be empty")
        
        // Write text to clipboard
        clipboard.writeText("Some text")
        
        // Now clipboard should have text
        assertTrue(clipboard.hasText(), "Clipboard should have text after writing")
    }
    
    @Test
    fun testClear() {
        val clipboard = MockClipboardAPI()
        
        // Write text to clipboard
        clipboard.writeText("Some text")
        
        // Verify clipboard has text
        assertTrue(clipboard.hasText(), "Clipboard should have text after writing")
        
        // Clear clipboard
        clipboard.clear()
        
        // Verify clipboard is empty
        assertFalse(clipboard.hasText(), "Clipboard should be empty after clearing")
        assertEquals("", clipboard.readText(), "Clipboard content should be empty string after clearing")
    }
    
    @Test
    fun testOverwriteText() {
        val clipboard = MockClipboardAPI()
        
        // Write initial text to clipboard
        clipboard.writeText("Initial text")
        
        // Verify initial text
        assertEquals("Initial text", clipboard.readText(), "Clipboard should contain initial text")
        
        // Overwrite with new text
        clipboard.writeText("New text")
        
        // Verify new text
        assertEquals("New text", clipboard.readText(), "Clipboard should contain new text after overwriting")
    }
    
    @Test
    fun testEmptyText() {
        val clipboard = MockClipboardAPI()
        
        // Write empty text to clipboard
        clipboard.writeText("")
        
        // Verify clipboard is considered empty
        assertFalse(clipboard.hasText(), "Clipboard should be considered empty with empty string")
        assertEquals("", clipboard.readText(), "Clipboard should return empty string")
    }
}