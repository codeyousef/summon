package code.yousef.summon.integration.springboot

import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse
import kotlin.test.Test
import kotlin.test.assertTrue

class SpringWebFluxRouterTest {

    @Test
    fun `summonRouter serves hydrated pages`() {
        val client = client(WebFluxSupport.summonRouter(basePath = "/pages"))

        client.get().uri("/pages").exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
            .expectBody(String::class.java)
            .value {
                assertTrue(it.contains("Welcome to Summon"))
                assertTrue(it.contains("id=\"summon-hydration-data\""))
            }
    }

    @Test
    fun `summonRouter can disable hydration`() {
        val client = client(WebFluxSupport.summonRouter(basePath = "/static", enableHydration = false))

        client.get().uri("/static/about").exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .value { body ->
                assertTrue(
                    !body.contains("id=\"summon-hydration-data\""),
                    "Hydration markers should be omitted when disabled"
                )
            }
    }

    @Test
    fun `custom not found handler overrides Summon fallback`() {
        val client = client(
            WebFluxSupport.summonRouter(
                basePath = "/app",
                enableHydration = false,
                notFound = {
                    ServerResponse.status(org.springframework.http.HttpStatus.NOT_FOUND).bodyValue("custom-not-found")
                }
            )
        )

        client.get().uri("/app/missing").exchange()
            .expectStatus().isNotFound
            .expectBody(String::class.java)
            .isEqualTo("custom-not-found")
    }

    private fun client(routerFunction: org.springframework.web.reactive.function.server.RouterFunction<org.springframework.web.reactive.function.server.ServerResponse>): WebTestClient =
        WebTestClient.bindToRouterFunction(routerFunction).build()
}
