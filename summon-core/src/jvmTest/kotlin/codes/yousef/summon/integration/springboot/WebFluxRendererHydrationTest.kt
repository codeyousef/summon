package codes.yousef.summon.integration.springboot

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.test.SlowTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SlowTest
class WebFluxRendererHydrationTest {

    private val renderer = WebFluxRenderer()

    @Test
    fun `renderHydrated emits hydration markup`() {
        val response = renderer.renderHydrated {
            Text("Hello WebFlux")
        }.block()

        assertNotNull(response)
        val html = assertNotNull(response.body)
        assertTrue(html.contains("id=\"summon-hydration-data\""))
        assertTrue(html.contains("Hello WebFlux"))
        assertTrue(response.headers.getFirst("Content-Security-Policy")?.contains("script-src 'self'") == true)
    }
}

