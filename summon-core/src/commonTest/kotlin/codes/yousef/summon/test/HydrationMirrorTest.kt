package codes.yousef.summon.test

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertTrue

class HydrationMirrorTest {

    @Test
    fun testDeterministicRendering() {
        val renderer = PlatformRenderer()
        
        val html = renderer.renderComposableRoot {
            Column {
                Text("Mirror Test")
            }
        }
        
        // Basic verification that rendering produces output on both platforms
        assertTrue(html.isNotEmpty(), "Rendered HTML should not be empty")
        assertTrue(html.contains("Mirror Test"), "Rendered HTML should contain the text content")
    }
}
