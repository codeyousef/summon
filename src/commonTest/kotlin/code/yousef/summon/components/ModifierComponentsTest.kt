package code.yousef.summon.components

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
        
        // Verify component implements each interface
        assertTrue(component is ScrollableComponent, "Component should implement ScrollableComponent")
        assertTrue(component is InputComponent, "Component should implement InputComponent")
        assertTrue(component is TextComponent, "Component should implement TextComponent")
        assertTrue(component is LayoutComponent, "Component should implement LayoutComponent")
        assertTrue(component is ClickableComponent, "Component should implement ClickableComponent")
        assertTrue(component is FocusableComponent, "Component should implement FocusableComponent")
        assertTrue(component is MediaComponent, "Component should implement MediaComponent")
    }

    /**
     * Test that verifies type checking works for marker interfaces
     */
    @Test
    fun testTypeChecking() {
        val component = TestComponent()
        
        // Function that accepts only ScrollableComponent
        fun acceptScrollable(scrollable: ScrollableComponent) {
            assertTrue(scrollable is ScrollableComponent)
        }
        
        // Function that accepts only InputComponent
        fun acceptInput(input: InputComponent) {
            assertTrue(input is InputComponent)
        }
        
        // Verify component can be passed to functions requiring specific interfaces
        acceptScrollable(component)
        acceptInput(component)
    }
}