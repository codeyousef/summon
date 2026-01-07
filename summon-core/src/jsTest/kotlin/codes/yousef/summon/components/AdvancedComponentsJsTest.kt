package codes.yousef.summon.components

import codes.yousef.summon.components.display.Chart
import codes.yousef.summon.components.display.RichMarkdown
import codes.yousef.summon.components.input.CodeEditor
import codes.yousef.summon.components.layout.SplitPane
import codes.yousef.summon.runtime.PlatformRenderer
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AdvancedComponentsJsTest {

    @Test
    fun testRichMarkdownJS() {
        val renderer = PlatformRenderer()
        val markdown = "# Hello JS"
        
        val html = renderer.renderComposableRoot {
            RichMarkdown(markdown)
        }
        
        val div = document.createElement("div")
        div.innerHTML = html
        val renderedDiv = div.firstElementChild as? HTMLElement
        
        assertNotNull(renderedDiv, "Should render a div")
    }

    @Test
    fun testCodeEditorJS() {
        val renderer = PlatformRenderer()
        val code = "val x = 1"
        
        val html = renderer.renderComposableRoot {
            CodeEditor(code, {}, "kotlin")
        }
        
        val div = document.createElement("div")
        div.innerHTML = html
        val textarea = div.querySelector("textarea") as? HTMLTextAreaElement
        
        assertNotNull(textarea, "Should render a textarea")
        assertEquals(code, textarea.value, "Textarea value should match code")
    }

    @Test
    fun testChartJS() {
        val renderer = PlatformRenderer()
        val type = "line"
        val data = "{}"
        
        val html = renderer.renderComposableRoot {
            Chart(type, data)
        }
        
        val div = document.createElement("div")
        div.innerHTML = html
        val canvas = div.querySelector("canvas") as? HTMLCanvasElement
        
        assertNotNull(canvas, "Should render a canvas")
    }

    @Test
    fun testSplitPaneJS() {
        val renderer = PlatformRenderer()
        
        val html = renderer.renderComposableRoot {
            SplitPane(
                orientation = "horizontal",
                first = {},
                second = {}
            )
        }
        
        val div = document.createElement("div")
        div.innerHTML = html
        // The first child is the wrapper div from renderComposableRoot
        val wrapper = div.firstElementChild as? HTMLElement
        // The split pane container is the first child of the wrapper
        val container = wrapper?.firstElementChild as? HTMLElement
        
        assertNotNull(container, "Should render a container")
        
        // Check styles (note: style property names in JS are camelCase)
        assertEquals("flex", container.style.display, "Display should be flex")
        assertEquals("row", container.style.flexDirection, "Flex direction should be row")

        assertEquals(3, container.children.length, "Should have 3 children")
    }
}
