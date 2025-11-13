package codes.yousef.summon.components.feedback

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.Composer
import codes.yousef.summon.runtime.CompositionLocal
import kotlin.test.*
import kotlin.time.Duration.Companion.milliseconds

/**
 * Tests for the SnackbarHost component and related classes
 */
class SnackbarHostTest {

    // Mock implementation of Composer for testing
    private class MockComposer : Composer {
        override val inserting: Boolean = true

        override fun startNode() {}
        override fun startGroup(key: Any?) {}
        override fun endNode() {}
        override fun endGroup() {}
        override fun changed(value: Any?): Boolean = true
        override fun updateValue(value: Any?) {}
        override fun nextSlot() {}
        override fun getSlot(): Any? = null
        override fun setSlot(value: Any?) {}
        override fun recordRead(state: Any) {}
        override fun recordWrite(state: Any) {}
        override fun reportChanged() {}
        override fun registerDisposable(disposable: () -> Unit) {}
        override fun dispose() {}
        override fun startCompose() {}
        override fun endCompose() {}
        override fun <T> compose(composable: @Composable () -> T): T {
            @Suppress("UNCHECKED_CAST")
            return null as T
        }

        override fun recompose() {
            // Mock implementation
        }

        override fun rememberedValue(key: Any): Any? {
            return null
        }

        override fun updateRememberedValue(key: Any, value: Any?) {
            // Mock implementation
        }
    }

    @Test
    fun testSnackbarDataDefaults() {
        val data = SnackbarData(message = "Test message")

        assertEquals("Test message", data.message)
        assertEquals(SnackbarVariant.DEFAULT, data.variant)
        assertNull(data.action)
        assertNull(data.onAction)
        assertEquals(4000.milliseconds, data.duration)
        assertTrue(data.id.startsWith("snackbar-"), "ID should start with 'snackbar-'")
    }

    @Test
    fun testSnackbarDataCustomValues() {
        val onAction: () -> Unit = {}
        val data = SnackbarData(
            message = "Custom message",
            variant = SnackbarVariant.SUCCESS,
            action = "Undo",
            onAction = onAction,
            duration = 2000.milliseconds,
            id = "custom-id"
        )

        assertEquals("Custom message", data.message)
        assertEquals(SnackbarVariant.SUCCESS, data.variant)
        assertEquals("Undo", data.action)
        assertSame(onAction, data.onAction)
        assertEquals(2000.milliseconds, data.duration)
        assertEquals("custom-id", data.id)
    }

    @Test
    fun testSnackbarHostStateInitialState() {
        val hostState = SnackbarHostState()
        assertTrue(hostState.snackbars.isEmpty())
    }

    @Test
    fun testSnackbarHostStateShowSnackbar() {
        val hostState = SnackbarHostState()

        // Show a snackbar
        val id = hostState.showSnackbar(message = "Test message")

        // Verify snackbar was added
        assertEquals(1, hostState.snackbars.size)
        assertEquals("Test message", hostState.snackbars[0].message)
        assertEquals(id, hostState.snackbars[0].id)
    }

    @Test
    fun testSnackbarHostStateRemoveSnackbar() {
        val hostState = SnackbarHostState()

        // Show two snackbars
        val id1 = hostState.showSnackbar(message = "Message 1")
        val id2 = hostState.showSnackbar(message = "Message 2")

        // Verify both snackbars were added
        assertEquals(2, hostState.snackbars.size)

        // Remove the first snackbar
        hostState.removeSnackbar(id1)

        // Verify only the second snackbar remains
        assertEquals(1, hostState.snackbars.size)
        assertEquals("Message 2", hostState.snackbars[0].message)
        assertEquals(id2, hostState.snackbars[0].id)
    }

    @Test
    fun testSnackbarHostStateClearAll() {
        val hostState = SnackbarHostState()

        // Show multiple snackbars
        hostState.showSnackbar(message = "Message 1")
        hostState.showSnackbar(message = "Message 2")
        hostState.showSnackbar(message = "Message 3")

        // Verify all snackbars were added
        assertEquals(3, hostState.snackbars.size)

        // Clear all snackbars
        hostState.clearAll()

        // Verify all snackbars were removed
        assertTrue(hostState.snackbars.isEmpty())
    }

    @Test
    fun testRememberSnackbarHostState() {
        // This test is limited since we can't fully test remember functionality in a unit test
        // We'll just verify that the function returns a non-null SnackbarHostState

        val mockComposer = MockComposer()

        CompositionLocal.provideComposer(mockComposer) {
            val hostState = rememberSnackbarHostState()
            assertNotNull(hostState)
            // Verify we can use it as a SnackbarHostState
            hostState.showSnackbar("Test message")
            assertTrue(hostState.snackbars.isNotEmpty())
        }
    }

    // Note: We're not testing the actual SnackbarHost composable rendering
    // as it would require complex mocking of the Snackbar composable.
    // Instead, we focus on testing the data classes and state management.
}
