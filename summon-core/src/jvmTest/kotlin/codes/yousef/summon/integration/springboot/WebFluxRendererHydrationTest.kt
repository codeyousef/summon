package codes.yousef.summon.integration.springboot

import codes.yousef.summon.components.display.Text
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WebFluxRendererHydrationTest {

    private val renderer = WebFluxRenderer()

    @Test
    fun `renderHydrated emits hydration markup`() {
        val html = renderer.renderHydrated {
            Text("Hello WebFlux")
        }.block()

        assertNotNull(html)
        assertTrue(html.contains("id=\"summon-hydration-data\""))
        assertTrue(html.contains("Hello WebFlux"))
    }
}

