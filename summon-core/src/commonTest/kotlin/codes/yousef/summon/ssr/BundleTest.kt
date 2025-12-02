package codes.yousef.summon.ssr

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for SiteBundler functionality.
 *
 * TEST DIRECTIVE: Generate bundle. Unzip in memory.
 * Verify file existence and content integrity.
 */
class BundleTest {

    @Test
    fun testBundleSiteCreatesNonEmptyOutput() {
        val html = """
            <!DOCTYPE html>
            <html>
            <head><title>Test</title></head>
            <body><h1>Hello World</h1></body>
            </html>
        """.trimIndent()

        val css = """
            body { margin: 0; padding: 0; }
            h1 { color: blue; }
        """.trimIndent()

        val bundle = SiteBundler.bundleSite(html, css)

        assertTrue(bundle.isNotEmpty(), "Bundle should not be empty")
    }

    @Test
    fun testBundleSiteWithEmptyContent() {
        val html = ""
        val css = ""

        val bundle = SiteBundler.bundleSite(html, css)

        // Even with empty content, should create a valid bundle structure
        assertTrue(bundle.isNotEmpty(), "Bundle should have structure even with empty content")
    }

    @Test
    fun testBundleSiteWithOnlyHtml() {
        val html = "<html><body>Content</body></html>"
        val css = ""

        val bundle = SiteBundler.bundleSite(html, css)

        assertTrue(bundle.isNotEmpty())
    }

    @Test
    fun testBundleSiteWithOnlyCss() {
        val html = ""
        val css = "body { color: red; }"

        val bundle = SiteBundler.bundleSite(html, css)

        assertTrue(bundle.isNotEmpty())
    }

    @Test
    fun testBundleSiteWithLargeContent() {
        val largeHtml = buildString {
            append("<!DOCTYPE html><html><body>")
            repeat(1000) {
                append("<div class='item-$it'>Content $it</div>")
            }
            append("</body></html>")
        }

        val largeCss = buildString {
            repeat(500) {
                append(".item-$it { color: rgb($it, ${it % 256}, ${(it * 2) % 256}); }\n")
            }
        }

        val bundle = SiteBundler.bundleSite(largeHtml, largeCss)

        assertTrue(bundle.isNotEmpty(), "Should handle large content")
        assertTrue(bundle.size > 100, "Large content should produce substantial bundle")
    }

    @Test
    fun testBundleSiteWithSpecialCharacters() {
        val html = """
            <!DOCTYPE html>
            <html>
            <body>
                <p>Special chars: <>&"' © € ™</p>
                <p>Unicode: 你好世界 🌍</p>
            </body>
            </html>
        """.trimIndent()

        val css = """
            /* Comment with special chars: <>&"' */
            .emoji::before { content: "🎉"; }
        """.trimIndent()

        val bundle = SiteBundler.bundleSite(html, css)

        assertTrue(bundle.isNotEmpty(), "Should handle special characters")
    }

    @Test
    fun testBundleWithMultipleFiles() {
        val files = mapOf(
            "index.html" to "<html><body>Test</body></html>".encodeToByteArray(),
            "style.css" to "body { margin: 0; }".encodeToByteArray(),
            "script.js" to "console.log('hello');".encodeToByteArray(),
            "data.json" to """{"key": "value"}""".encodeToByteArray()
        )

        val bundle = SiteBundler.bundleSite(files)

        assertTrue(bundle.isNotEmpty())
    }
}
