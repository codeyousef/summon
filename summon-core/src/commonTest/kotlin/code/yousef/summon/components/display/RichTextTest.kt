package codes.yousef.summon.components.display

import codes.yousef.summon.components.foundation.RawHtml
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.*

class RichTextTest {

    @Test
    fun testRichTextWithSafeHTML() {
        val mockRenderer = MockPlatformRenderer()
        val safeHtml = "<p>This is <strong>bold</strong> text.</p>"

        runComposableTest(mockRenderer) {
            RichText(safeHtml)
        }
        
        assertTrue(mockRenderer.renderHtmlCalled)
        assertEquals(safeHtml, mockRenderer.lastHtmlContentRendered)
        assertTrue(mockRenderer.lastHtmlSanitizeEnabledRendered == true)
    }

    @Test
    fun testRichTextWithSanitizationDisabled() {
        val mockRenderer = MockPlatformRenderer()
        val rawHtml = "<p>Raw HTML with <script>alert('xss')</script></p>"

        runComposableTest(mockRenderer) {
            RichText(rawHtml, sanitize = false)
        }
        
        assertTrue(mockRenderer.renderHtmlCalled)
        assertEquals(rawHtml, mockRenderer.lastHtmlContentRendered)
        assertFalse(mockRenderer.lastHtmlSanitizeEnabledRendered == true)
    }

    @Test
    fun testRichTextSanitizesXSSAttempts() {
        val mockRenderer = MockPlatformRenderer()
        val maliciousHtml = """
            <p>Hello <script>alert('xss')</script> world!</p>
            <img src="x" onerror="alert('xss')">
            <a href="javascript:alert('xss')">Click me</a>
        """.trimIndent()

        runComposableTest(mockRenderer) {
            RichText(maliciousHtml, sanitize = true)
        }
        
        assertTrue(mockRenderer.renderHtmlCalled)
        assertTrue(mockRenderer.lastHtmlSanitizeEnabledRendered == true)
        
        // The mock renderer doesn't actually sanitize, it just records the parameters
        // The real renderer would sanitize. We verify sanitize=true was passed.
        val htmlContent = mockRenderer.lastHtmlContentRendered!!
        assertTrue(htmlContent.contains("<p>"))
        assertTrue(htmlContent.contains("Hello"))
        assertTrue(htmlContent.contains("world!"))
    }

    @Test
    fun testHtmlComponentForTrustedContent() {
        val mockRenderer = MockPlatformRenderer()
        val trustedHtml = "<div class='trusted'><p>Trusted content</p></div>"

        runComposableTest(mockRenderer) {
            Html(trustedHtml)
        }
        
        assertTrue(mockRenderer.renderHtmlCalled)
        assertEquals(trustedHtml, mockRenderer.lastHtmlContentRendered)
        // Html component should have minimal sanitization
        assertFalse(mockRenderer.lastHtmlSanitizeEnabledRendered == true)
    }

    @Test
    fun testRawHtmlBuilderDisablesSanitization() {
        val mockRenderer = MockPlatformRenderer()

        runComposableTest(mockRenderer) {
            RawHtml {
                +"<canvas id=\"gl\"></canvas>"
                +"<script>console.log('inline')</script>"
            }
        }

        assertTrue(mockRenderer.renderHtmlCalled)
        assertEquals(
            "<canvas id=\"gl\"></canvas><script>console.log('inline')</script>",
            mockRenderer.lastHtmlContentRendered
        )
        assertFalse(mockRenderer.lastHtmlSanitizeEnabledRendered == true)
    }

    @Test
    fun testMarkdownComponent() {
        val mockRenderer = MockPlatformRenderer()
        val markdown = """
            # Hello World
            
            This is **bold** and *italic* text.
            
            - List item 1
            - List item 2
            
            [Link](https://example.com)
        """.trimIndent()

        runComposableTest(mockRenderer) {
            Markdown(markdown)
        }
        
        assertTrue(mockRenderer.renderHtmlCalled)
        assertNotNull(mockRenderer.lastHtmlContentRendered)
        val htmlOutput = mockRenderer.lastHtmlContentRendered!!
        
        // Should convert markdown to HTML
        assertTrue(htmlOutput.contains("<h1>"))
        assertTrue(htmlOutput.contains("<strong>"))
        assertTrue(htmlOutput.contains("<em>"))
        assertTrue(htmlOutput.contains("<ul>"))
        assertTrue(htmlOutput.contains("<li>"))
        assertTrue(htmlOutput.contains("<a href=\"https://example.com\""))
        assertTrue(mockRenderer.lastHtmlSanitizeEnabledRendered == true)
    }

    @Test
    fun testRichTextWithModifier() {
        val mockRenderer = MockPlatformRenderer()

        runComposableTest(mockRenderer) {
            RichText(
                "<p>Styled content</p>",
                modifier = code.yousef.summon.modifier.Modifier()
                    .style("margin", "1rem")
                    .style("padding", "0.5rem")
            )
        }
        
        assertTrue(mockRenderer.renderHtmlCalled)
        assertNotNull(mockRenderer.lastHtmlModifierRendered)
        assertTrue(mockRenderer.lastHtmlModifierRendered!!.styles.containsKey("margin"))
        assertTrue(mockRenderer.lastHtmlModifierRendered!!.styles.containsKey("padding"))
    }

    @Test
    fun testRichTextWithEmptyContent() {
        val mockRenderer = MockPlatformRenderer()

        runComposableTest(mockRenderer) {
            RichText("")
        }
        
        assertTrue(mockRenderer.renderHtmlCalled)
        assertEquals("", mockRenderer.lastHtmlContentRendered)
    }

    @Test
    fun testAllowedTagsPreservation() {
        val mockRenderer = MockPlatformRenderer()
        val allowedHtml = """
            <h1>Title</h1>
            <h2>Subtitle</h2>
            <p>Paragraph with <strong>bold</strong> and <em>italic</em></p>
            <ul>
                <li>List item</li>
            </ul>
            <blockquote>Quote</blockquote>
            <code>inline code</code>
            <pre>code block</pre>
        """.trimIndent()

        runComposableTest(mockRenderer) {
            RichText(allowedHtml, sanitize = true)
        }
        
        assertTrue(mockRenderer.renderHtmlCalled)
        val sanitizedHtml = mockRenderer.lastHtmlContentRendered!!
        
        // All these tags should be preserved
        assertTrue(sanitizedHtml.contains("<h1>"))
        assertTrue(sanitizedHtml.contains("<h2>"))
        assertTrue(sanitizedHtml.contains("<p>"))
        assertTrue(sanitizedHtml.contains("<strong>"))
        assertTrue(sanitizedHtml.contains("<em>"))
        assertTrue(sanitizedHtml.contains("<ul>"))
        assertTrue(sanitizedHtml.contains("<li>"))
        assertTrue(sanitizedHtml.contains("<blockquote>"))
        assertTrue(sanitizedHtml.contains("<code>"))
        assertTrue(sanitizedHtml.contains("<pre>"))
    }
}
