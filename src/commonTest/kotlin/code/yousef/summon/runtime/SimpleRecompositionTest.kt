package code.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimpleRecompositionTest {

    @Test
    fun testMutableStateListOf() {
        val list = mutableStateListOf(1, 2, 3)

        assertEquals(3, list.size)
        assertEquals(listOf(1, 2, 3), list.toList())

        list.add(4)
        assertEquals(4, list.size)
        assertEquals(4, list[3])

        list.removeAt(0)
        assertEquals(3, list.size)
        assertEquals(listOf(2, 3, 4), list.toList())

        list[0] = 10
        assertEquals(10, list[0])

        list.clear()
        assertEquals(0, list.size)
        assertTrue(list.isEmpty())
    }

    @Test
    fun testComposerStartRestartableGroup() {
        val renderer = MockPlatformRenderer()
        val composer = CommonComposer()

        CompositionLocal.provideComposer(composer) {
            LocalPlatformRenderer.provides(renderer)

            composer.startRestartableGroup("test-group")
            // Group content
            composer.endRestartableGroup()
        }

        // Test passes if no exceptions thrown
        assertTrue(true)
    }

    @Test
    fun testComposerKey() {
        val renderer = MockPlatformRenderer()
        val composer = CommonComposer()

        var keyExecuted = false

        CompositionLocal.provideComposer(composer) {
            LocalPlatformRenderer.provides(renderer)

            composer.key("test-key") {
                keyExecuted = true
            }
        }

        assertTrue(keyExecuted)
    }
}