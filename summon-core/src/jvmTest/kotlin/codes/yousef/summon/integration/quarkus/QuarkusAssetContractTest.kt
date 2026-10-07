package codes.yousef.summon.integration.quarkus

import codes.yousef.summon.integration.quarkus.QuarkusRenderer.Companion.summonCallbackHandler
import codes.yousef.summon.integration.quarkus.QuarkusRenderer.Companion.summonStaticAssets
import codes.yousef.summon.runtime.CallbackRegistry
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuarkusAssetContractTest {
    private lateinit var vertx: Vertx
    private val client = HttpClient.newHttpClient()

    @BeforeTest
    fun setUp() {
        vertx = Vertx.vertx()
        CallbackRegistry.clear()
    }

    @AfterTest
    fun tearDown() {
        CallbackRegistry.clear()
        val latch = CountDownLatch(1)
        vertx.close { latch.countDown() }
        assertTrue(latch.await(5, TimeUnit.SECONDS))
    }

    @Test
    fun staticAssetsServeRawAndCachedGzipWhileWasmRemainsUncompressed() {
        withServer({ summonStaticAssets() }) { port ->
            val raw = get(port, "/summon-hydration.js")
            assertEquals(200, raw.statusCode())
            assertEquals("application/javascript", raw.headers().firstValue("Content-Type").orElse(null))
            assertEquals("public, max-age=31536000, immutable", raw.headers().firstValue("Cache-Control").orElse(null))

            val gzip = get(port, "/summon-assets/summon-hydration.js", "gzip")
            assertEquals(200, gzip.statusCode())
            assertEquals("gzip", gzip.headers().firstValue("Content-Encoding").orElse(null))
            assertContentEquals(gzip.body(), get(port, "/summon-assets/summon-hydration.js", "gzip").body())

            val wasm = get(port, "/summon-hydration.wasm", "gzip")
            assertEquals(200, wasm.statusCode())
            assertFalse(wasm.headers().firstValue("Content-Encoding").isPresent)

            val missing = get(port, "/deadbeef.wasm")
            assertEquals(404, missing.statusCode())
            assertTrue(missing.body().decodeToString().contains("not-found"))
        }
    }

    @Test
    fun callbackRouteRejectsMissingCapabilitiesAndExecutesOwnedCallbacksOnce() {
        var calls = 0
        CallbackRegistry.beginRender()
        val callbackId = CallbackRegistry.registerCallback { calls++ }
        val context = CallbackRegistry.finishRenderAndCollectCallbacks()

        withServer({ summonCallbackHandler() }) { port ->
            assertEquals(400, post(port, "/summon/callback/%20").statusCode())
            assertEquals(404, post(port, "/summon/callback/$callbackId").statusCode())
            assertEquals(200, post(port, "/summon/callback/$callbackId", context.capability).statusCode())
            assertEquals(1, calls)
            assertEquals(404, post(port, "/summon/callback/$callbackId", context.capability).statusCode())
        }
    }

    private fun get(port: Int, path: String, encoding: String? = null): HttpResponse<ByteArray> {
        val builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:$port$path")).GET()
        if (encoding != null) builder.header("Accept-Encoding", encoding)
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray())
    }

    private fun post(port: Int, path: String, capability: String? = null): HttpResponse<ByteArray> {
        val builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:$port$path"))
            .POST(HttpRequest.BodyPublishers.noBody())
        if (capability != null) builder.header("X-Summon-Callback-Context", capability)
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray())
    }

    private fun withServer(configure: Router.() -> Unit, block: (Int) -> Unit) {
        val server = vertx.createHttpServer().requestHandler(Router.router(vertx).apply(configure))
        val started = CountDownLatch(1)
        var active: HttpServer? = null
        var failure: Throwable? = null
        server.listen(0, "127.0.0.1") { result ->
            if (result.succeeded()) active = result.result() else failure = result.cause()
            started.countDown()
        }
        assertTrue(started.await(5, TimeUnit.SECONDS))
        failure?.let { throw it }
        try {
            block(active?.actualPort() ?: error("Server did not start"))
        } finally {
            val stopped = CountDownLatch(1)
            active?.close { stopped.countDown() }
            assertTrue(stopped.await(5, TimeUnit.SECONDS))
        }
    }
}
