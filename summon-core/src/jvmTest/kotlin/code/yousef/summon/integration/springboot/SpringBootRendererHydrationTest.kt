package codes.yousef.summon.integration.springboot

import codes.yousef.summon.components.display.Text
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpringBootRendererHydrationTest {

    private val renderer = SpringBootRenderer()

    @Test
    fun `renderHydrated produces hydration document`() {
        val response = renderer.renderHydrated(HttpStatus.ACCEPTED) {
            Text("Hello Spring Boot")
        }

        assertEquals(HttpStatus.ACCEPTED, response.statusCode)
        val body = response.body ?: error("Response body expected")
        assertTrue(body.contains("id=\"summon-hydration-data\""))
        assertTrue(body.contains("Hello Spring Boot"))
    }
}

