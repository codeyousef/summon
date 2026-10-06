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
        val registeredIds = CallbackRegistry.finishRenderAndCollectCallbackIds()

        assertTrue(callbackId in registeredIds, "Newly registered callback IDs should be collected")
        assertTrue(CallbackRegistry.executeCallback(callbackId), "Callback should execute successfully")
        assertTrue(executed, "Callback block should update state when invoked")
    }

    @Test
    fun callbacksSurviveBetweenRenderAndExecution() {
        var count = 0
        CallbackRegistry.beginRender()
        val callbackId = CallbackRegistry.registerCallback { count++ }
        CallbackRegistry.finishRenderAndCollectCallbackIds()
        CallbackRegistry.abandonRenderContext()

        assertTrue(CallbackRegistry.executeCallback(callbackId), "Callback should still be available after render")
        assertEquals(1, count, "Callback handler should run exactly once")
    }
}
