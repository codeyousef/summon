package codes.yousef.summon.runtime

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlinx.coroutines.CancellationException

class CallbackRegistryTest {

    @Test fun cancellationRetainsItsIdentityAndDetachesTheOneShotCallback() {
        val cancellation = CancellationException("Synthetic private callback failure")
        val id = CallbackRegistry.registerCallback { throw cancellation }
        assertSame(cancellation, assertFailsWith<CancellationException> { CallbackRegistry.executeCallback(id) })
        assertFalse(CallbackRegistry.hasCallback(id))
    }

    @Test fun ordinaryFailedCallbackIsConsumedAndCannotExecuteTwice() {
        var calls = 0
        val id = CallbackRegistry.registerCallback { calls++; error("Synthetic private callback failure") }
        assertFalse(CallbackRegistry.executeCallback(id))
        assertFalse(CallbackRegistry.executeCallback(id))
        assertEquals(1, calls)
        assertFalse(CallbackRegistry.hasCallback(id))
    }

    @BeforeTest
    fun resetRegistry() {
        CallbackRegistry.clear()
    }

    @Test
    fun registerCallbacksWithinRenderCycle() {
        var executed = false
        CallbackRegistry.beginRender()
        val callbackId = CallbackRegistry.registerCallback { executed = true }
        val context = CallbackRegistry.finishRenderAndCollectCallbacks()

        assertTrue(callbackId in context.callbackIds)
        assertFalse(CallbackRegistry.executeRemoteCallback(callbackId, "wrong-context"))
        assertTrue(CallbackRegistry.executeRemoteCallback(callbackId, context.capability))
        assertTrue(executed)
        assertFalse(CallbackRegistry.executeRemoteCallback(callbackId, context.capability))
    }

    @Test
    fun callbacksSurviveBetweenRenderAndExecution() {
        var count = 0
        CallbackRegistry.beginRender()
        val callbackId = CallbackRegistry.registerCallback { count++ }
        val context = CallbackRegistry.finishRenderAndCollectCallbacks()
        CallbackRegistry.abandonRenderContext()

        assertTrue(CallbackRegistry.executeRemoteCallback(callbackId, context.capability))
        assertEquals(1, count)
    }
}
