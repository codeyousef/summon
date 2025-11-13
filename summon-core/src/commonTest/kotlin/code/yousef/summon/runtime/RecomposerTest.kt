package codes.yousef.summon.runtime

import kotlin.test.*

/**
 * Tests for the Recomposer class
 * 
 * Note: This test focuses on the parts of Recomposer that don't rely on expect functions.
 * The platform-specific functionality is tested in platform-specific tests.
 */
class RecomposerTest {

    @Test
    fun testCreateComposer() {
        // Create a recomposer
        val recomposer = Recomposer()

        // Create a composer
        val composer = recomposer.createComposer()

        // Verify that the composer is not null
        assertNotNull(composer, "Composer should not be null")

        // Verify that the composer is a RecomposerBackedComposer
        assertTrue(Recomposer.isComposerImpl(composer), "Composer should be a RecomposerBackedComposer")
    }

    @Test
    fun testComposerBasicOperations() {
        // Create a recomposer
        val recomposer = Recomposer()

        // Create a composer
        val composer = recomposer.createComposer()

        // Test basic composer operations

        // Test slot operations
        // The first call to changed() will always return true because the slot is initially null
        assertTrue(composer.changed("initial"), "Initial value should trigger change")
        // The second call with the same value should return false
        assertFalse(composer.changed("initial"), "Same value should not trigger change")
        // A different value should trigger a change
        assertTrue(composer.changed("new value"), "New value should trigger change")
        assertEquals("new value", composer.getSlot(), "Slot should contain the new value")

        composer.updateValue("updated value")
        assertEquals("updated value", composer.getSlot(), "Slot should be updated")

        composer.nextSlot()
        composer.setSlot("next slot value")
        assertEquals("next slot value", composer.getSlot(), "Next slot should contain the value")

        // Test node and group operations
        composer.startNode()
        composer.startGroup("group key")
        composer.endGroup()
        composer.endNode()
    }

    @Test
    fun testComposition() {
        // Create a recomposer
        val recomposer = Recomposer()

        // Create a composer
        val composer = recomposer.createComposer()

        // Test composition
        var composableCalled = false
        composer.compose {
            composableCalled = true
        }
        assertTrue(composableCalled, "Composable should be called during composition")
    }

    @Test
    fun testDisposal() {
        // Create a recomposer
        val recomposer = Recomposer()

        // Create a composer
        val composer = recomposer.createComposer()

        // Test disposal
        var disposableCalled = false
        composer.registerDisposable {
            disposableCalled = true
        }
        composer.dispose()
        assertTrue(disposableCalled, "Disposable should be called during disposal")
    }

    @Test
    fun testGetPendingRecompositions() {
        // Create a recomposer
        val recomposer = Recomposer()

        // Get the pending recompositions set
        val pendingRecompositions = recomposer.getPendingRecompositions()

        // Verify that the set is not null
        assertNotNull(pendingRecompositions, "Pending recompositions set should not be null")

        // Verify that the set is initially empty
        assertTrue(pendingRecompositions.isEmpty(), "Pending recompositions set should be initially empty")
    }

    // Note: We can't test internal methods like setActiveComposer and recordRead
    // directly in Kotlin Multiplatform without reflection, which isn't well-supported
    // across all platforms. These methods are indirectly tested through the public API.
}
