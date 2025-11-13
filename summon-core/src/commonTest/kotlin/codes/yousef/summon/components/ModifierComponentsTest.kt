package codes.yousef.summon.components

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests for the marker interfaces in ModifierComponents.kt
 */
class ModifierComponentsTest {

    /**
     * Test class that implements all marker interfaces to verify they can be implemented
     */
    private class TestComponent :
        ScrollableComponent,
        InputComponent,
        TextComponent,
        LayoutComponent,
        ClickableComponent,
        FocusableComponent,
        MediaComponent

    /**
     * Test that verifies all marker interfaces can be implemented
     */
    @Test
    fun testMarkerInterfacesCanBeImplemented() {
        val component = TestComponent()

        // Verify component can be used where these interfaces are required
        val scrollable: ScrollableComponent = component
        val input: InputComponent = component
        val text: TextComponent = component
        val layout: LayoutComponent = component
        val clickable: ClickableComponent = component
        val focusable: FocusableComponent = component
        val media: MediaComponent = component

        // Just assert true to show the test passed
        assertTrue(true, "Component successfully implements all marker interfaces")
    }

    /**
     * Test that verifies type checking works for marker interfaces
     */
    @Test
    fun testTypeChecking() {
        val component = TestComponent()

        // Function that accepts only ScrollableComponent
        fun acceptScrollable(scrollable: ScrollableComponent) {
            // Just verify we can call the function with this parameter
            assertTrue(true, "Successfully passed ScrollableComponent")
        }

        // Function that accepts only InputComponent
        fun acceptInput(input: InputComponent) {
            // Just verify we can call the function with this parameter
            assertTrue(true, "Successfully passed InputComponent")
        }

        // Verify component can be passed to functions requiring specific interfaces
        acceptScrollable(component)
        acceptInput(component)
    }
}
