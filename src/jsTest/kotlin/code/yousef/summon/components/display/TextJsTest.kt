package code.yousef.summon.components.display

import code.yousef.summon.components.Text // Make sure Text component is imported
import code.yousef.summon.framework.RenderContext
import code.yousef.summon.modifier.Modifier
import kotlinx.browser.document
import kotlinx.browser.window // For getComputedStyle
import kotlinx.html.dom.append
import kotlinx.html.js.div
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLParagraphElement // Or HTMLSpanElement, depending on Text impl.
import kotlin.test.*
import org.w3c.dom.asList
import kotlin.random.Random

/**
 * JS tests for the Text component, focusing on rendering and computed styles/classes.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
class TextJsTest {

    private val testContainers = mutableListOf<HTMLElement>()

    // Helper: Render component to DOM and return the *rendered element itself*
    private fun renderComponentAndGetElement(content: RenderContext.() -> Unit): HTMLElement {
        val container = document.createElement("div") as HTMLElement
        container.id = "test-container-txt-${Random.nextInt().toString().replace('-', '_')}"
        testContainers.add(container)
        document.body?.appendChild(container) ?: error("Document body is not available")

        val context = object : RenderContext {
            override val containerId: String = container.id
            override fun <T : HTMLElement> getElementById(id: String): T? {
                 return document.getElementById(id) as? T
            }
        }
        container.append { context.content() }
        // Assume the component renders one top-level element we want to test
        return container.firstElementChild as? HTMLElement 
               ?: error("Rendered component did not produce a testable HTML element")
    }

    // Helper: Modifier to set ID (Adjust based on your actual Modifier implementation)
     private fun Modifier.id(value: String): Modifier {
          // IMPORTANT: Placeholder implementation. Adjust to your Modifier class.
          return this.then(object : Modifier {
              override val styles: Map<String, String> = emptyMap()
              override val attributes: Map<String, String> = mapOf("id" to value)
              override val eventListeners: Map<String, (dynamic) -> Unit> = emptyMap()
              override val childrenModifiers: List<Modifier> = emptyList()
              override val parentModifier: Modifier? = null
              override fun then(other: Modifier): Modifier = this // Simplification
              override fun toString(): String = "idModifier($value)"
          })
      }

    // Helper: Cleanup DOM after tests
    @AfterTest
    fun cleanup() {
        testContainers.forEach { container -> container.parentNode?.removeChild(container) }
        testContainers.clear()
    }

    @Test
    fun testBasicTextRendering() {
        val textContent = "Hello, World!"
        val textId = "basic-text-test"

        // Render and get the actual <p> or <span> element
        val textElement = renderComponentAndGetElement {
            Text(content = textContent, modifier = Modifier.id(textId))
        }

        assertEquals(textId, textElement.id, "Element ID should match")
        assertEquals(textContent, textElement.textContent?.trim(), "Text content should match")
        // Optionally check tag name if consistent (e.g., "P" or "SPAN")
        // assertEquals("P", textElement.tagName)
    }

    @Test
    fun testTextAlignmentStyle() {
        val textContent = "Centered Text"
        val textId = "aligned-text-test"
        val expectedAlign = "center"

        val textElement = renderComponentAndGetElement {
            Text(content = textContent, textAlign = expectedAlign, modifier = Modifier.id(textId))
        }
         
        // Check computed style
        val computedStyle = window.getComputedStyle(textElement)
        assertEquals(expectedAlign, computedStyle.textAlign, "Text should have computed style text-align: $expectedAlign")
    }

     @Test
     fun testTextMaxLinesStyle() {
         val textContent = "Long text that should be clamped by max lines configuration to show ellipsis eventually maybe"
         val textId = "maxlines-text-test"
         val maxLines = 2

         val textElement = renderComponentAndGetElement {
             Text(content = textContent, maxLines = maxLines, modifier = Modifier.id(textId))
         }

         // Check computed styles related to line clamping
         val computedStyle = window.getComputedStyle(textElement)
         // Verify standard CSS properties for line clamping
         assertEquals("-webkit-box", computedStyle.display, "Style 'display' should be '-webkit-box' for maxLines")
         assertEquals(maxLines.toString(), computedStyle.webkitLineClamp, "Style '-webkit-line-clamp' should be '$maxLines'")
         assertEquals("vertical", computedStyle.webkitBoxOrient, "Style '-webkit-box-orient' should be 'vertical'")
         assertEquals("hidden", computedStyle.overflow, "Style 'overflow' should be 'hidden'")
     }

    // TODO: Add tests for other styling properties like fontFamily, textDecoration, textTransform etc.
    // by checking computed styles similar to testTextAlignmentStyle.
} 