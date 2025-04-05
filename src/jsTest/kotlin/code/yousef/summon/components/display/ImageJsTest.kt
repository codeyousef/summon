package code.yousef.summon.components.display

import code.yousef.summon.components.Image // Make sure Image component is imported
import code.yousef.summon.framework.RenderContext
import code.yousef.summon.modifier.Modifier
import kotlinx.browser.document
import kotlinx.browser.window // For getComputedStyle
import kotlinx.html.dom.append
import kotlinx.html.js.div
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement // Correct element type
import kotlin.test.*
import org.w3c.dom.asList
import kotlin.random.Random

/**
 * JS tests for the Image component, focusing on rendering and attributes.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
class ImageJsTest {

    private val testContainers = mutableListOf<HTMLElement>()

    // Helper: Render component to DOM
    private fun renderComponent(content: RenderContext.() -> Unit): HTMLElement {
        val container = document.createElement("div") as HTMLElement
        container.id = "test-container-img-${Random.nextInt().toString().replace('-', '_')}"
        testContainers.add(container)
        document.body?.appendChild(container) ?: error("Document body is not available")

        val context = object : RenderContext {
            override val containerId: String = container.id
            override fun <T : HTMLElement> getElementById(id: String): T? {
                 return document.getElementById(id) as? T
            }
        }
        container.append { context.content() }
        return container
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
    fun testBasicImageRendering() {
        val testSrc = "test-image.png"
        val testAlt = "Basic test image"
        val imgId = "basic-image-test"

        renderComponent {
            Image(src = testSrc, alt = testAlt, modifier = Modifier.id(imgId))
        }

        val imgElement = document.getElementById(imgId) as? HTMLImageElement
        assertNotNull(imgElement, "Image element with ID '$imgId' should be found")
        assertEquals("IMG", imgElement.tagName, "Element should be an <img> tag")
        // Check if src *ends with* the expected value, as browser might resolve full path
        assertTrue(imgElement.src.endsWith(testSrc), "Image src attribute ('${imgElement.src}') should end with '$testSrc'")
        assertEquals(testAlt, imgElement.alt, "Image alt attribute should be '$testAlt'")
    }

    @Test
    fun testImageWithSizingAttributesAndStyle() {
        val testSrc = "sized-image.gif"
        val testAlt = "Sized test image"
        // Test width/height styles if your component sets them via CSS
        val testWidthStyle = "150px"
        val testHeightStyle = "100px"

        val imgId = "sized-image-test"

        renderComponent {
            // Pass CSS units to the component as it likely expects them for styling
            Image(
                src = testSrc,
                alt = testAlt,
                width = testWidthStyle, 
                height = testHeightStyle,
                modifier = Modifier.id(imgId)
            )
        }

        val imgElement = document.getElementById(imgId) as? HTMLImageElement
        assertNotNull(imgElement, "Sized image element with ID '$imgId' should be found")

        // Check computed style (more reliable if sizing is done via CSS)
        val computedStyle = window.getComputedStyle(imgElement)
        assertEquals(testWidthStyle, computedStyle.width, "Image style width should be set to '$testWidthStyle'")
        assertEquals(testHeightStyle, computedStyle.height, "Image style height should be set to '$testHeightStyle'")
        
        // Optionally check attributes IF your component also sets width/height attributes
        // assertEquals("150", imgElement.getAttribute("width"), "Image width attribute") 
        // assertEquals("100", imgElement.getAttribute("height"), "Image height attribute")
    }

    @Test
    fun testImageWithContentDescriptionForAccessibility() {
        // Assuming contentDescription adds an aria-label or similar
        val testSrc = "accessible-image.jpg"
        val testAlt = "Accessible image"
        val description = "A detailed description of the accessible image"
        val imgId = "accessible-image-test"
        // How contentDescription maps to aria attributes might depend on component logic
        val expectedAriaLabel = description 

        renderComponent {
            Image(
                src = testSrc,
                alt = testAlt,
                contentDescription = description,
                modifier = Modifier.id(imgId)
            )
        }

        val imgElement = document.getElementById(imgId) as? HTMLImageElement
        assertNotNull(imgElement, "Accessible image element with ID '$imgId' should be found")
        // Verify the accessibility attribute added by contentDescription
        assertEquals(expectedAriaLabel, imgElement.getAttribute("aria-label"), "Image should have aria-label '$expectedAriaLabel' from contentDescription")
        assertEquals(testAlt, imgElement.alt, "Image alt attribute should still be '$testAlt'")
    }
} 