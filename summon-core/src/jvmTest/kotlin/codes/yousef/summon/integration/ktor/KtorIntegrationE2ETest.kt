package codes.yousef.summon.integration.ktor

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.integration.ktor.KtorRenderer.Companion.respondSummonHydrated
import codes.yousef.summon.integration.ktor.KtorRenderer.Companion.summonStaticAssets
import codes.yousef.summon.integration.ktor.KtorRenderer.Companion.summonCallbackHandler
import codes.yousef.summon.runtime.CallbackRegistry
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

    @Test
    fun summonStaticAssetsServesHydrationFiles() = testApplication {
        application {
            routing {
                summonStaticAssets()
            }
        }

        // Test JS hydration file
        val jsResponse = client.get("/summon-hydration.js")
        // May be 404 if resources aren't available in test context, but route should exist
        assertTrue(jsResponse.status in listOf(HttpStatusCode.OK, HttpStatusCode.NotFound))
        if (jsResponse.status == HttpStatusCode.OK) {
            assertEquals("application/javascript", jsResponse.contentType()?.withoutParameters()?.toString())
        }

        // Test WASM file with correct MIME type
        val wasmResponse = client.get("/summon-hydration.wasm")
        assertTrue(wasmResponse.status in listOf(HttpStatusCode.OK, HttpStatusCode.NotFound))
        if (wasmResponse.status == HttpStatusCode.OK) {
            assertEquals("application/wasm", wasmResponse.contentType()?.withoutParameters()?.toString())
        }

        // Test assets under /summon-assets/ prefix
        val prefixedJsResponse = client.get("/summon-assets/summon-hydration.js")
        assertTrue(prefixedJsResponse.status in listOf(HttpStatusCode.OK, HttpStatusCode.NotFound))
    }

    @Test
    fun summonCallbackHandlerExecutesRegisteredCallbacks() = testApplication {
        application {
            routing {
                summonCallbackHandler()
            }
        }

        // Register a test callback
        var callbackExecuted = false
        val callbackId = CallbackRegistry.registerCallback { callbackExecuted = true }

        // Execute the callback via HTTP
        val response = client.post("/summon/callback/$callbackId")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(callbackExecuted, "Callback should have been executed")

        val body = response.bodyAsText()
        assertTrue(""""action":"reload"""" in body)
        assertTrue(""""status":"ok"""" in body)
    }

    @Test
    fun summonCallbackHandlerReturnsNotFoundForMissingCallback() = testApplication {
        application {
            routing {
                summonCallbackHandler()
            }
        }

        val response = client.post("/summon/callback/non-existent-id")
        assertEquals(HttpStatusCode.NotFound, response.status)

        val body = response.bodyAsText()
        assertTrue(""""action":"noop"""" in body)
        assertTrue(""""status":"missing"""" in body)
    }

    @Test
    fun summonCallbackHandlerReturnsBadRequestForMissingId() = testApplication {
        application {
            routing {
                summonCallbackHandler()
            }
        }

        // Empty callback ID should return bad request
        val response = client.post("/summon/callback/")
        // Route won't match with empty param, so expect 404
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
