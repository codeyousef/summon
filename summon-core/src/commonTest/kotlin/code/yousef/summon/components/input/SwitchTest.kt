package code.yousef.summon.components.input

// Import other types used in PlatformRenderer methods for the mock
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.util.runComposableTest
import kotlin.test.*

class SwitchTest {

    @Test
    fun testSwitchInteractionAndRecomposition() {
        val mockRenderer = MockPlatformRenderer()
        var checkedState = false
        val testModifier = Modifier()

        runComposableTest(mockRenderer) {
            Switch(
                checked = checkedState,
                onCheckedChange = { checkedState = it },
                modifier = testModifier,
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderSwitchCalled, "renderSwitch should be called")
        assertEquals(false, mockRenderer.lastSwitchCheckedRendered, "Initial checked state mismatch")
        assertEquals(true, mockRenderer.lastSwitchEnabledRendered, "Enabled state mismatch")
        assertSame(testModifier, mockRenderer.lastSwitchModifierRendered, "Modifier mismatch")

        // Simulate state change
        assertNotNull(mockRenderer.lastSwitchOnCheckedChangeRendered, "onCheckedChange callback should not be null")
        mockRenderer.lastSwitchOnCheckedChangeRendered?.invoke(true)
        assertEquals(true, checkedState, "Checked state did not update after callback")

        // Recompose with new state (simulate by running composable again)
        runComposableTest(mockRenderer) {
            Switch(
                checked = checkedState, // Now true
                onCheckedChange = { checkedState = it },
                modifier = testModifier,
                enabled = false // Change enabled state for verification
            )
        }
        assertEquals(true, mockRenderer.lastSwitchCheckedRendered, "Updated checked state mismatch")
        assertEquals(false, mockRenderer.lastSwitchEnabledRendered, "Updated enabled state mismatch")
    }

    @Test
    fun testStatefulSwitch() {
        val mockRenderer = MockPlatformRenderer()
        var lastCallbackValue: Boolean? = null

        runComposableTest(mockRenderer) {
            StatefulSwitch(
                initialChecked = true,
                onCheckedChange = { lastCallbackValue = it },
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderSwitchCalled, "renderSwitch should be called for StatefulSwitch")
        assertEquals(true, mockRenderer.lastSwitchCheckedRendered, "Initial checked state for StatefulSwitch mismatch")
        assertEquals(true, mockRenderer.lastSwitchEnabledRendered, "Enabled state for StatefulSwitch mismatch")
        assertNotNull(
            mockRenderer.lastSwitchOnCheckedChangeRendered,
            "onCheckedChange callback should not be null for StatefulSwitch"
        )

        // Simulate user toggling the switch via the renderer's callback
        mockRenderer.lastSwitchOnCheckedChangeRendered?.invoke(false)

        // Verify the external callback was triggered
        assertEquals(false, lastCallbackValue, "onCheckedChange callback mismatch for StatefulSwitch")
    }

    @Test
    fun testStatefulSwitchDefaultInitialState() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            StatefulSwitch() // Uses default initialChecked = false
        }
        assertTrue(mockRenderer.renderSwitchCalled)
        assertFalse(mockRenderer.lastSwitchCheckedRendered ?: true, "Default initial checked state should be false")
    }

    @Test
    fun testDisabledSwitchDoesNotCallCallback() {
        val mockRenderer = MockPlatformRenderer()
        var callbackCalled = false
        val onValChange: (Boolean) -> Unit = { callbackCalled = true }

        runComposableTest(mockRenderer) {
            Switch(
                checked = false,
                onCheckedChange = onValChange,
                enabled = false, // Explicitly disable the switch
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderSwitchCalled, "renderSwitch should be called")
        assertEquals(false, mockRenderer.lastSwitchEnabledRendered, "Switch should be rendered as disabled")
        assertNotNull(mockRenderer.lastSwitchOnCheckedChangeRendered, "onCheckedChange callback should be captured")

        // Attempt to invoke the callback (simulating user interaction with a disabled switch)
        mockRenderer.lastSwitchOnCheckedChangeRendered?.invoke(true)

        // Assert that the external callback variable was NOT changed
        assertFalse(callbackCalled, "onCheckedChange callback should not be invoked when the switch is disabled")
    }
}