package codes.yousef.summon.effects

import codes.yousef.summon.modifier.Modifier
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import kotlin.test.*

class ElementRefTest {

    @Test
    fun testElementRefCreation() {
        val ref = ElementRef()
        assertNull(ref.getElement())
        assertNotNull(ref.getId())
        assertTrue(ref.getId().startsWith("element-"))
    }

    @Test
    fun testSetElement() {
        val ref = ElementRef()
        val div = document.createElement("div") as HTMLDivElement

        ref.setElement(div)

        assertEquals(div, ref.getElement())
        // Element should get an ID if it doesn't have one
        assertEquals(ref.getId(), div.id)
    }

    @Test
    fun testSetElementWithExistingId() {
        val ref = ElementRef()
        val div = document.createElement("div") as HTMLDivElement
        div.id = "custom-id"

        ref.setElement(div)

        assertEquals(div, ref.getElement())
        // Element should keep its existing ID
        assertEquals("custom-id", div.id)
        assertEquals("custom-id", ref.getId())
    }

    @Test
    fun testClearElement() {
        val ref = ElementRef()
        val div = document.createElement("div") as HTMLDivElement

        ref.setElement(div)
        assertNotNull(ref.getElement())

        ref.clear()
        assertNull(ref.getElement())
    }

    @Test
    fun testIsAttached() {
        val ref = ElementRef()
        val div = document.createElement("div") as HTMLDivElement

        // Not attached when no element
        assertFalse(ref.isAttached())

        // Not attached when element is not in DOM
        ref.setElement(div)
        assertFalse(ref.isAttached())

        // Attached when element is in DOM
        document.body?.appendChild(div)
        assertTrue(ref.isAttached())

        // Clean up
        document.body?.removeChild(div)
        assertFalse(ref.isAttached())
    }

    @Test
    fun testElementRefWithModifier() {
        // Test the modifier extension
        val ref = ElementRef()
        val modifier = Modifier().ref(ref)

        // Check that the modifier has the ref ID as an attribute
        val attributes = modifier.attributes
        assertNotNull(attributes)
        assertEquals(ref.getId(), attributes["data-element-ref"])
    }

    @Test
    fun testOnElementAttachedModifier() {
        // Test the onElementAttached modifier extension
        val modifier = Modifier().onElementAttached { element ->
            // This callback would be called when element is attached
        }

        // Check that the modifier has the attribute
        val attributes = modifier.attributes
        assertNotNull(attributes)
        assertEquals("true", attributes["data-on-attached"])
    }
}