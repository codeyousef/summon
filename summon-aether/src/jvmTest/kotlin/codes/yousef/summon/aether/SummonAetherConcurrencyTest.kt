package codes.yousef.summon.aether

import codes.yousef.aether.core.Attributes
import codes.yousef.aether.core.Cookie
import codes.yousef.aether.core.Cookies
import codes.yousef.aether.core.Exchange
import codes.yousef.aether.core.Headers
import codes.yousef.aether.core.HttpMethod
import codes.yousef.aether.core.Request
import codes.yousef.aether.core.Response
import codes.yousef.summon.core.error.ComponentNotFoundException
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.CallbackRegistry
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.getPlatformRenderer
import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class SummonAetherConcurrencyTest {
    @Test
    fun rendererAndCallbacksRemainRequestOwnedAcrossDispatcherHops() = runBlocking {
        CallbackRegistry.clear()
        Executors.newFixedThreadPool(2).asCoroutineDispatcher().use { dispatcher ->
            val renderers = List(2) { PlatformRenderer() }
            val contexts = coroutineScope {
                renderers.mapIndexed { index, renderer ->
                    async {
                        withRenderingContext(renderer) {
                            CallbackRegistry.beginRender()
                            val callbackId = CallbackRegistry.registerCallback { }
                            repeat(20) {
                                withContext(dispatcher) {
                                    yield()
                                    assertSame(renderer, getPlatformRenderer())
                                }
                            }
                            index to (callbackId to CallbackRegistry.finishRenderAndCollectCallbacks())
                        }
                    }
                }.awaitAll()
            }.sortedBy { it.first }

            val (callbackA, contextA) = contexts[0].second
            val (callbackB, contextB) = contexts[1].second
            assertNotEquals(callbackA, callbackB)
            assertTrue(callbackA in contextA.callbackIds)
            assertTrue(callbackB in contextB.callbackIds)
            assertFalse(CallbackRegistry.executeRemoteCallback(callbackA, contextB.capability))
            assertFalse(CallbackRegistry.executeRemoteCallback(callbackB, contextA.capability))
            assertTrue(CallbackRegistry.executeRemoteCallback(callbackA, contextA.capability))
            assertTrue(CallbackRegistry.executeRemoteCallback(callbackB, contextB.capability))
        }
        assertNoAmbientRenderer()
    }

    @Test
    fun failedRenderRestoresRendererAndRevokesItsCallbacks() = runBlocking {
        CallbackRegistry.clear()
        var callbackId = ""
        var capability = ""
        try {
            withRenderingContext(PlatformRenderer()) {
                CallbackRegistry.beginRender()
                callbackId = CallbackRegistry.registerCallback { }
                capability = CallbackRegistry.finishRenderAndCollectCallbacks().capability
                CallbackRegistry.beginRender()
                CallbackRegistry.registerCallback { }
                error("render failed")
            }
            fail("Expected render failure")
        } catch (expected: IllegalStateException) {
            assertEquals("render failed", expected.message)
        }

        assertNoAmbientRenderer()
        assertTrue(CallbackRegistry.executeRemoteCallback(callbackId, capability))
        assertEquals(0, CallbackRegistry.size())
    }

    @Test
    fun cancellationRestoresRendererAndRevokesPendingCallbacks() = runBlocking {
        CallbackRegistry.clear()
        var callbackId = ""
        val cancellation = kotlinx.coroutines.CancellationException("cancel request")

        try {
            withRenderingContext(PlatformRenderer()) {
                CallbackRegistry.beginRender()
                callbackId = CallbackRegistry.registerCallback { }
                yield()
                throw cancellation
            }
            fail("Expected cancellation")
        } catch (actual: kotlinx.coroutines.CancellationException) {
            assertEquals(cancellation.message, actual.message)
        }

        assertFalse(CallbackRegistry.hasCallback(callbackId))
        assertNoAmbientRenderer()
    }

    @Test
    fun abortingOneRequestDoesNotAffectConcurrentResponse() = runBlocking {
        CallbackRegistry.clear()
        val callbackFromA = CompletableDeferred<String>()
        val requestA = launch {
            withRenderingContext(PlatformRenderer()) {
                CallbackRegistry.beginRender()
                callbackFromA.complete(CallbackRegistry.registerCallback { })
                awaitCancellation()
            }
        }

        val abandonedCallback = callbackFromA.await()
        val requestB = CapturingExchange("/mail")
        requestB.respondSummon { Text("public-b-clean") }
        requestA.cancelAndJoin()

        assertFalse(CallbackRegistry.hasCallback(abandonedCallback))
        assertTrue(requestB.response.body.contains("public-b-clean"))
        assertNoAmbientRenderer()
    }

    @Test
    fun concurrentResponsesDoNotLeakPrivateMarkers() = runBlocking {
        val responses = coroutineScope {
            listOf("account-a", "account-b").map { marker ->
                async {
                    val exchange = CapturingExchange("/mail/thread/$marker")
                    exchange.response.setHeader("Cache-Control", "no-store")
                    exchange.response.setHeader("Content-Security-Policy", "default-src 'self'")
                    exchange.respondSummon { Text("public-$marker") }
                    marker to exchange.response
                }
            }.awaitAll()
        }

        val responseA = responses.single { it.first == "account-a" }.second
        val responseB = responses.single { it.first == "account-b" }.second
        assertEquals(200, responseA.statusCode)
        assertEquals(200, responseB.statusCode)
        assertTrue(responseA.body.contains("public-account-a"))
        assertFalse(responseA.body.contains("account-b"))
        assertTrue(responseB.body.contains("public-account-b"))
        assertFalse(responseB.body.contains("account-a"))
        responses.forEach { (_, response) ->
            assertEquals("no-store", response.recordedHeaders["Cache-Control"])
            assertEquals("default-src 'self'", response.recordedHeaders["Content-Security-Policy"])
        }
        assertNoAmbientRenderer()
    }

    private fun assertNoAmbientRenderer() {
        assertFailsWith<ComponentNotFoundException> {
            getPlatformRenderer()
        }
    }
}

private class CapturingExchange(path: String) : Exchange {
    override val request: Request = object : Request {
        override val method = HttpMethod.GET
        override val uri = path
        override val path = path
        override val query = ""
        override val headers = Headers(emptyMap())
        override val cookies = Cookies(emptyMap())
        override suspend fun bodyBytes(): ByteArray = byteArrayOf()
        override suspend fun bodyText(): String = ""
        override fun queryParameters(): Map<String, List<String>> = emptyMap()
        override fun queryParameter(name: String): String? = null
    }
    override val response = CapturingResponse()
    override val attributes = Attributes()
}

private class CapturingResponse : Response {
    override var statusCode: Int = 0
    override var statusMessage: String? = null
    override val headers = Headers.HeadersBuilder()
    override val cookies = mutableListOf<Cookie>()
    val body = StringBuilder()
    val recordedHeaders = mutableMapOf<String, String>()

    override suspend fun write(data: ByteArray) {
        yield()
        body.append(data.decodeToString())
    }

    override suspend fun write(text: String) {
        yield()
        body.append(text)
    }

    override suspend fun end() = Unit

    override fun setHeader(name: String, value: String) {
        recordedHeaders[name] = value
        headers.set(name, value)
    }

    override fun addHeader(name: String, value: String) {
        recordedHeaders[name] = listOfNotNull(recordedHeaders[name], value).joinToString(",")
        headers.add(name, value)
    }

    override fun setCookie(cookie: Cookie) {
        cookies.add(cookie)
    }
}
