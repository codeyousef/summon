package codes.yousef.summon.integration.ktor

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.runtime.*
import codes.yousef.summon.test.SlowTest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests to verify that callback IDs match between SSR HTML and hydration data,
 * even in coroutine contexts where threads may switch during rendering.
 * 
 * This test validates the fix for the critical SSR callback hydration issue
 * resolved in version 0.4.8.7.
 */
@SlowTest
class CallbackHydrationTest {

    @Test
    fun `test callback IDs match between HTML and hydration data without coroutine context`() {
        CallbackRegistry.clear()
        
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)
        
        try {
            CallbackRegistry.beginRender()
            
            // Render a button with onClick handler
            @Composable
            fun TestButton() {
                Button(
                    onClick = { println("Button clicked") },
                    label = "Test Button"
                )
            }
            
            val html = renderer.renderComposableRootWithHydration {
                TestButton()
            }
            
            // Extract callback ID from HTML
            val callbackIdPattern = """data-onclick-id="([^"]+)"""".toRegex()
            val htmlCallbackId = callbackIdPattern.find(html)?.groupValues?.get(1)
            
            // Extract callback IDs from hydration data
            val hydrationDataPattern = """"callbacks":\["([^"]+)"\]""".toRegex()
            val hydrationCallbackId = hydrationDataPattern.find(html)?.groupValues?.get(1)
            
            // Verify both were found
            assertTrue(htmlCallbackId != null, "Callback ID should be in HTML")
            assertTrue(hydrationCallbackId != null, "Callback ID should be in hydration data")
            
            // Critical assertion: callback IDs must match
            assertEquals(
                htmlCallbackId, 
                hydrationCallbackId,
                "Callback ID in HTML must match callback ID in hydration data"
            )
        } finally {
            clearPlatformRenderer()
            CallbackRegistry.clear()
        }
    }
    
    @Test
    fun `test callback IDs match with coroutine context switching`() = runBlocking {
        CallbackRegistry.clear()
        
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)
        
        try {
            val callbackContext = CallbackContextElement()
            
            val html = withContext(callbackContext) {
                CallbackRegistry.beginRender()
                
                // Simulate coroutine suspension that might switch threads
                launch {
                    // This could potentially execute on a different thread
                }.join()
                
                // Render a button with onClick handler after potential thread switch
                @Composable
                fun TestButton() {
                    Button(
                        onClick = { println("Button clicked after coroutine") },
                        label = "Test Button"
                    )
                }
                
                renderer.renderComposableRootWithHydration {
                    TestButton()
                }
            }
            
            // Extract callback IDs from HTML and hydration data
            val callbackIdPattern = """data-onclick-id="([^"]+)"""".toRegex()
            val htmlCallbackId = callbackIdPattern.find(html)?.groupValues?.get(1)
            
            val hydrationDataPattern = """"callbacks":\["([^"]+)"\]""".toRegex()
            val hydrationCallbackId = hydrationDataPattern.find(html)?.groupValues?.get(1)
            
            // Verify both were found
            assertTrue(htmlCallbackId != null, "Callback ID should be in HTML")
            assertTrue(hydrationCallbackId != null, "Callback ID should be in hydration data")
            
            // Critical assertion: callback IDs must match even after coroutine suspension
            assertEquals(
                htmlCallbackId, 
                hydrationCallbackId,
                "Callback ID must remain stable across coroutine context switches"
            )
        } finally {
            clearPlatformRenderer()
            CallbackRegistry.clear()
        }
    }
    
    @Test
    fun `test multiple buttons have different but all matching callback IDs`() {
        CallbackRegistry.clear()
        
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)
        
        try {
            @Composable
            fun MultiButtonApp() {
                Button(onClick = { println("Button 1") }, label = "Button 1")
                Button(onClick = { println("Button 2") }, label = "Button 2")
                Button(onClick = { println("Button 3") }, label = "Button 3")
            }
            
            val html = renderer.renderComposableRootWithHydration {
                MultiButtonApp()
            }
            
            // Extract all callback IDs from HTML
            val callbackIdPattern = """data-onclick-id="([^"]+)"""".toRegex()
            val htmlCallbackIds = callbackIdPattern.findAll(html)
                .map { it.groupValues[1] }
                .toList()
            
            // Extract callback IDs from hydration data
            val hydrationDataPattern = """"callbacks":\[([^\]]+)\]""".toRegex()
            val hydrationCallbacksJson = hydrationDataPattern.find(html)?.groupValues?.get(1)
            val hydrationCallbackIds = hydrationCallbacksJson
                ?.replace("\"", "")
                ?.split(",")
                ?.map { it.trim() }
                ?: emptyList()
            
            // Verify we have 3 callbacks
            assertEquals(3, htmlCallbackIds.size, "Should have 3 callback IDs in HTML")
            assertEquals(3, hydrationCallbackIds.size, "Should have 3 callback IDs in hydration data")
            
            // Verify all callback IDs are unique
            assertEquals(htmlCallbackIds.size, htmlCallbackIds.toSet().size, "All HTML callback IDs should be unique")
            
            // Critical assertion: all HTML callback IDs must be in hydration data
            htmlCallbackIds.forEach { htmlId ->
                assertTrue(
                    hydrationCallbackIds.contains(htmlId),
                    "Callback ID $htmlId from HTML must be in hydration data"
                )
            }
        } finally {
            clearPlatformRenderer()
            CallbackRegistry.clear()
        }
    }
    
    @Test
    fun `test callback context element maintains stability`() = runBlocking {
        val context1 = CallbackContextElement()
        val context2 = CallbackContextElement()
        
        // Two different CallbackContextElement instances should have different IDs
        assertFalse(
            context1 == context2,
            "Different CallbackContextElement instances should not be equal"
        )
        
        // But within the same context, the ID should remain stable
        var id1: Long = 0
        var id2: Long = 0
        
        withContext(context1) {
            id1 = codes.yousef.summon.runtime.getStableCallbackContextKey()
            
            // Simulate some work
            launch { }.join()
            
            id2 = codes.yousef.summon.runtime.getStableCallbackContextKey()
        }
        
        assertEquals(id1, id2, "Callback context ID should remain stable within same context")
    }
}
