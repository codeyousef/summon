package codes.yousef.summon.runtime

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class JvmComposerContractTest {
    @Test
    fun slotsGroupsNodesRememberedValuesAndDisposalHaveDefinedLifecycle() {
        val composer = JvmComposer.create()
        composer.endNode()
        composer.endGroup()
        assertTrue(composer.changed("first"))
        assertFalse(composer.changed("first"))
        composer.nextSlot()
        assertNull(composer.getSlot())
        composer.updateValue(42)
        assertEquals(42, composer.getSlot())

        composer.startGroup("group")
        composer.setSlot("group-value")
        composer.endGroup()
        composer.startNode()
        composer.endNode()

        val key = Any()
        composer.updateRememberedValue(key, "remembered")
        assertEquals("remembered", composer.rememberedValue(key))
        var disposed = false
        composer.registerDisposable { disposed = true }
        composer.dispose()
        assertTrue(disposed)
        assertNull(composer.rememberedValue(key))
    }

    @Test
    fun composeAlwaysClosesAndStateWritesNotifyRegisteredOwners() {
        val composer = JvmComposer()
        val state = Any()
        val notification = CountDownLatch(1)
        composer.registerStateListener(state) { notification.countDown() }
        composer.recordWrite(state)
        assertTrue(notification.await(5, TimeUnit.SECONDS))

        val marker = Any()
        assertSame(marker, composer.compose { marker })
        assertFailsWith<IllegalStateException> {
            composer.compose { error("synthetic composition failure") }
        }
        composer.recompose()
        composer.dispose()
    }
}
