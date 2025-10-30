package code.yousef.summon.diagnostics

import code.yousef.summon.components.display.Text
import code.yousef.summon.runtime.CallbackRegistry
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CallbackRegistryLeakTest {

    @AfterEach
    fun tearDown() {
        CallbackRegistry.clear()
        clearPlatformRenderer()
    }

    @Test
    fun `renderComposableRootWithHydration clears callbacks`() {
        repeat(100) { iteration ->
            val renderer = PlatformRenderer()
            setPlatformRenderer(renderer)

            val html = renderer.renderComposableRootWithHydration {
                Text("hello-$iteration")
            }

            assertTrue(html.contains("summon-hydration-data"), "hydration markup should be present")
            assertEquals(0, CallbackRegistry.size(), "Callback registry should be empty after hydration render")
        }
    }
}
