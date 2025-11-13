package codes.yousef.summon.integration.ktor

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.integration.ktor.KtorRenderer.Companion.respondSummonHydrated
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KtorIntegrationE2ETest {

    @Test
    fun hydratedHelperProducesFullDocument() = testApplication {
        application {
            routing {
                get("/hydrated") {
                    call.respondSummonHydrated {
                        Text("Hydrated response!")
                    }
                }
            }
        }

        val response = client.get("/hydrated")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("text/html; charset=UTF-8", response.headers[HttpHeaders.ContentType])

        val body = response.bodyAsText()
        assertTrue(body.startsWith("<!DOCTYPE html>"), "Expected HTML5 doctype in response")
        assertTrue("""id="summon-hydration-data""" in body, "Hydration data script missing")
        assertTrue("Hydrated response!" in body)
    }

    @Test
    fun summonRouterServesFileBasedPagesWithHydration() = testApplication {
        application {
            routing {
                summonRouter(basePath = "/pages")
            }
        }

        val homeResponse = client.get("/pages")
        assertEquals(HttpStatusCode.OK, homeResponse.status)
        val homeBody = homeResponse.bodyAsText()
        assertTrue("Welcome to Summon" in homeBody)
        assertTrue("""id="summon-hydration-data""" in homeBody, "Router should return hydrated HTML by default")

        val aboutResponse = client.get("/pages/about")
        assertEquals(HttpStatusCode.OK, aboutResponse.status)
        val aboutBody = aboutResponse.bodyAsText()
        assertTrue("About Summon" in aboutBody)
        assertTrue("""id="summon-hydration-data""" in aboutBody)
    }

    @Test
    fun summonRouterRendersSummonNotFoundPage() = testApplication {
        application {
            routing {
                summonRouter(basePath = "/pages")
            }
        }

        val missingResponse = client.get("/pages/does-not-exist")
        assertEquals(HttpStatusCode.NotFound, missingResponse.status)
        val body = missingResponse.bodyAsText()
        assertTrue("404 - Page Not Found" in body)
        assertTrue("""id="summon-hydration-data""" in body, "Not found page should still be hydrated by default")
    }

    @Test
    fun summonRouterSupportsStaticRenderingAndCustomNotFoundHandler() = testApplication {
        application {
            routing {
                summonRouter(
                    basePath = "/static",
                    enableHydration = false,
                    notFound = {
                        respondText("custom-not-found", status = HttpStatusCode.NotFound)
                    }
                )
            }
        }

        val staticResponse = client.get("/static/about")
        assertEquals(HttpStatusCode.OK, staticResponse.status)
        val staticBody = staticResponse.bodyAsText()
        assertTrue("About Summon" in staticBody)
        assertFalse("""id="summon-hydration-data""" in staticBody, "Hydration markup should be omitted when disabled")

        val customNotFound = client.get("/static/unknown")
        assertEquals(HttpStatusCode.NotFound, customNotFound.status)
        assertEquals("custom-not-found", customNotFound.bodyAsText())
    }
}
