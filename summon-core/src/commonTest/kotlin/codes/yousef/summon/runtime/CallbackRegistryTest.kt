package codes.yousef.summon.runtime

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CallbackRegistryTest {

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
