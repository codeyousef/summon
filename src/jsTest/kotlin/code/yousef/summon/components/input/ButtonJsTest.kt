package code.yousef.summon.components.input

// Imports for framework, browser APIs, and html builders
import code.yousef.summon.components.Button // Make sure Button component is imported
import code.yousef.summon.framework.RenderContext
import code.yousef.summon.modifier.Modifier
import kotlinx.browser.document
import kotlinx.html.dom.append
import kotlinx.html.js.div
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import kotlin.test.AfterTest // Import AfterTest
import kotlin.test.BeforeTest // Import BeforeTest if needed
import kotlin.test.Test // Import Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.dom.addClass // For potential variant testing
import kotlinx.dom.hasClass // For potential variant testing
import org.w3c.dom.asList // Helper for NodeList iteration
import kotlin.random.Random // For unique IDs

/**
 * JS tests for the Button component, focusing on rendering and interactions.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING") // If Modifier uses expect/actual
class ButtonJsTest {

    private val testContainers = mutableListOf<HTMLElement>()

    // Helper: Render component to DOM
    private fun renderComponent(content: RenderContext.() -> Unit): HTMLElement {
        val container = document.createElement("div") as HTMLElement
        // Use a more robust unique ID generation if necessary
        container.id = "test-container-btn-${Random.nextInt().toString().replace('-', '_')}" 
        testContainers.add(container)
        document.body?.appendChild(container) ?: error("Document body is not available")

        val context = object : RenderContext {
            override val containerId: String = container.id
            override fun <T : HTMLElement> getElementById(id: String): T? {
                 return document.getElementById(id) as? T
            }
            // Add other RenderContext methods if your Button requires them
        }
        container.append { context.content() }
        return container
    }

    // Helper: Modifier to set ID (Adjust based on your actual Modifier implementation)
    // IMPORTANT: This is a placeholder. You MUST ensure this aligns with your Modifier class.
    // If your Modifier doesn't support `then` or setting attributes this way, adapt the tests.
    private fun Modifier.id(value: String): Modifier {
         return this.then(object : Modifier {
             override val styles: Map<String, String> = emptyMap()
             override val attributes: Map<String, String> = mapOf("id" to value)
             override val eventListeners: Map<String, (dynamic) -> Unit> = emptyMap()
             override val childrenModifiers: List<Modifier> = emptyList()
             override val parentModifier: Modifier? = null
             override fun then(other: Modifier): Modifier = this // Basic implementation for chaining
             override fun toString(): String = "idModifier($value)"
             // Implement other required Modifier abstract members/methods if any exist
         })
     }

    // Helper: Cleanup DOM after tests
    @AfterTest
    fun cleanup() {
        testContainers.forEach { container -> container.parentNode?.removeChild(container) }
        testContainers.clear()
    }

    @Test
    fun testBasicButtonRendering() {
        val buttonLabel = "Click Me Test"
        val buttonId = "basic-button-test"

        renderComponent {
            Button(label = buttonLabel, modifier = Modifier.id(buttonId))
        }

        val buttonElement = document.getElementById(buttonId) as? HTMLButtonElement
        assertNotNull(buttonElement, "Button element with ID '$buttonId' should be found")
        assertEquals("BUTTON", buttonElement.tagName, "Element should be a <button>")
        assertEquals(buttonLabel, buttonElement.textContent?.trim(), "Button label should match")
        assertEquals(false, buttonElement.disabled, "Button should not be disabled by default")
        // Assuming PRIMARY is default and adds a class like 'btn-primary'
        // You might need to use buttonElement.classList.contains("btn-primary")
        // assertTrue(buttonElement.hasClass("btn-primary"), "Should have default primary class")
    }

    @Test
    fun testDisabledButtonRendering() {
        val buttonLabel = "Cannot Click Test"
        val buttonId = "disabled-button-test"

        renderComponent {
            Button(label = buttonLabel, disabled = true, modifier = Modifier.id(buttonId))
        }

        val buttonElement = document.getElementById(buttonId) as? HTMLButtonElement
        assertNotNull(buttonElement, "Disabled button element with ID '$buttonId' should be found")
        assertEquals(true, buttonElement.disabled, "Button's disabled property should be true")
        assertTrue(buttonElement.hasAttribute("disabled"), "Button should have the disabled attribute")
    }

    @Test
    fun testButtonClickHandlerInteraction() {
        val buttonId = "clickable-button-test"
        var clicked = false

        renderComponent {
            Button(label = "Click Handler Test", modifier = Modifier.id(buttonId)) {
                clicked = true
            }
        }

        val buttonElement = document.getElementById(buttonId) as? HTMLButtonElement
        assertNotNull(buttonElement, "Button element needed for click simulation")
        buttonElement.click() // Simulate click
        assertTrue(clicked, "onClick handler should have been executed after button click")
    }

    @Test
    fun testButtonVariantClass() {
        val buttonId = "secondary-button-test"
        // Example: Assuming Button.Variant.SECONDARY should result in CSS class "btn-secondary"
        val expectedVariantClass = "btn-secondary" 

        renderComponent {
             // Adjust Button.Variant.SECONDARY based on your actual enum/class names
             // Ensure your Button component applies the corresponding class based on the variant
            Button(label = "Secondary", variant = Button.Variant.SECONDARY, modifier = Modifier.id(buttonId))
        }

        val buttonElement = document.getElementById(buttonId) as? HTMLButtonElement
        assertNotNull(buttonElement, "Button element with ID '$buttonId' should be found")
        assertTrue(buttonElement.classList.contains(expectedVariantClass), "Button should have class '$expectedVariantClass' for SECONDARY variant")
    }

    // Note: Testing icon rendering requires checking for child elements (e.g., <i> or <span>)
    // within the buttonElement and their classes or content. Add tests for this as needed.
} 