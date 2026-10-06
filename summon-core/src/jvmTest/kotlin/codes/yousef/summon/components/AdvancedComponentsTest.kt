package codes.yousef.summon.components

import codes.yousef.summon.components.display.Chart
import codes.yousef.summon.components.display.RichMarkdown
import codes.yousef.summon.components.input.CodeEditor
import codes.yousef.summon.components.layout.SplitPane
import codes.yousef.summon.runtime.PlatformRenderer
import org.jsoup.Jsoup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdvancedComponentsTest {

    @Test
    fun richMarkdownSsrTreatsSourceAsPlaintext() {
        val renderer = PlatformRenderer()
        val markdown = "# Hello\n<script>globalThis.xss=1</script>\n**World**"

        val html = renderer.renderComposableRoot {
            RichMarkdown(markdown)
        }

        assertTrue(html.contains("# Hello"))
        assertTrue(html.contains("&lt;script&gt;globalThis.xss=1&lt;/script&gt;"))
        assertTrue(html.contains("**World**"))
        assertFalse(html.contains("<h1>"))
        assertFalse(html.contains("<script>"))
    }

    @Test
    fun testCodeEditorSSR() {
        val renderer = PlatformRenderer()
        val code = "fun main() {}"
        
        val html = renderer.renderComposableRoot {
            CodeEditor(
                value = code,
                onValueChange = {},
                language = "kotlin"
            )
        }
        
        val doc = Jsoup.parse(html)
        val pre = doc.select("pre").first()
        val codeElement = pre?.select("code")?.first()
        
        assertTrue(pre != null, "Pre tag should exist")
        assertTrue(codeElement != null, "Code tag should exist")
        assertEquals(codeElement.className().contains("language-kotlin"), true, "Should have language class")
        assertEquals(code, codeElement.text(), "Code content should match")
        
        val container = doc.select("div[data-summon-component='code-editor']").first()
        assertTrue(container != null, "Container with data attribute should exist")
    }

    @Test
    fun testChartSSR() {
        val renderer = PlatformRenderer()
        val type = "bar"
        val data = """{"labels":["A","B"],"datasets":[{"data":[1,2]}]}"""
        
        val html = renderer.renderComposableRoot {
            Chart(type, data)
        }
        
        val doc = Jsoup.parse(html)
        val canvas = doc.select("canvas").first()
        
        assertTrue(canvas != null, "Canvas should exist")
        assertEquals("chart", canvas.attr("data-summon-component"))
        assertEquals(type, canvas.attr("data-chart-type"))
        assertEquals(data, canvas.attr("data-chart-data"))
    }

    @Test
    fun testSplitPaneSSR() {
        val renderer = PlatformRenderer()
        
        val html = renderer.renderComposableRoot {
            SplitPane(
                orientation = "vertical",
                first = { RichMarkdown("Pane 1") },
                second = { RichMarkdown("Pane 2") }
            )
        }
        
        val doc = Jsoup.parse(html)
        val container = doc.select("div[data-summon-component='split-pane']").first()
        
        assertTrue(container != null, "Split pane container should exist")
        assertEquals("vertical", container.attr("data-orientation"))

        val firstPane = container.select("div[data-pane='first']").first()
        val divider = container.select("div[data-pane='divider']").first()
        val secondPane = container.select("div[data-pane='second']").first()

        assertTrue(firstPane != null, "First pane should exist")
        assertTrue(divider != null, "Divider should exist")
        assertTrue(secondPane != null, "Second pane should exist")
        
        assertTrue(html.contains("Pane 1"), "Should contain content of first pane")
        assertTrue(html.contains("Pane 2"), "Should contain content of second pane")
    }
}
