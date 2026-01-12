package codes.yousef.summon.integration.quarkus

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.integration.quarkus.QuarkusRenderer.Companion.respondSummonHydrated
import codes.yousef.summon.test.SlowTest
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.*

@SlowTest
class QuarkusIntegrationE2ETest {

    private lateinit var vertx: Vertx
    private val httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    @BeforeTest
    fun setUp() {
        vertx = Vertx.vertx()
    }

    @AfterTest
    fun tearDown() {
        val latch = CountDownLatch(1)
        vertx.close { latch.countDown() }
        latch.await(5, TimeUnit.SECONDS)
    }

    @Test
    fun `respondSummonHydrated returns hydration markup`() {
        withServer({
            get("/hydrated").handler { ctx ->
                ctx.respondSummonHydrated {
                    Text("Hello from Quarkus")
                }
            }
        }) { port ->
            val response = httpGet(port, "/hydrated")
            assertEquals(200, response.statusCode())
            assertTrue(response.body().contains("id=\"summon-hydration-data\""))
        }
    }

    @Test
    fun `router serves hydrated pages`() {
        withServer({
            summonRouter(basePath = "/pages")
        }) { port ->
            val response = httpGet(port, "/pages")
            assertEquals(200, response.statusCode())
            assertTrue(response.body().contains("Welcome to Summon"))
            assertTrue(response.body().contains("id=\"summon-hydration-data\""))
        }
    }

    @Test
    fun `router honours hydration flag`() {
        withServer({
            summonRouter(basePath = "/static", enableHydration = false)
        }) { port ->
            val response = httpGet(port, "/static/about")
            assertEquals(200, response.statusCode())
            assertTrue(
                !response.body().contains("id=\"summon-hydration-data\""),
                "Hydration markers should be omitted when hydration is disabled"
            )
        }
    }

    @Test
    fun `custom not found handler overrides Summon fallback`() {
        withServer({
            summonRouter(basePath = "/static", enableHydration = false) {
                response()
                    .setStatusCode(404)
                    .putHeader("Content-Type", "text/plain; charset=UTF-8")
                    .end("custom-not-found")
            }
        }) { port ->
            val response = httpGet(port, "/static/missing")
            assertEquals(404, response.statusCode())
            assertEquals("custom-not-found", response.body())
        }
    }

    @Test
    fun `Summon not found page is returned when available`() {
        withServer({
            summonRouter(basePath = "/pages")
        }) { port ->
            val response = httpGet(port, "/pages/not-there")
            assertEquals(404, response.statusCode())
            assertTrue(response.body().contains("404 - Page Not Found"))
        }
    }

    private fun withServer(configurer: Router.() -> Unit, test: (Int) -> Unit) {
        val router = Router.router(vertx).apply(configurer)
        val server = vertx.createHttpServer(HttpServerOptions()).requestHandler(router)
        val startLatch = CountDownLatch(1)
        var failure: Throwable? = null
        var activeServer: HttpServer? = null
        server.listen(0) { ar ->
            if (ar.succeeded()) {
                activeServer = ar.result()
            } else {
                failure = ar.cause()
            }
            startLatch.countDown()
        }

        if (!startLatch.await(5, TimeUnit.SECONDS)) {
            server.close()
            throw IllegalStateException("Timed out starting test server")
        }

        failure?.let { throw it }
        val port = activeServer?.actualPort() ?: error("Server did not report listening port")

        try {
            test(port)
        } finally {
            val stopLatch = CountDownLatch(1)
            activeServer.close { stopLatch.countDown() }
            stopLatch.await(5, TimeUnit.SECONDS)
        }
    }

    private fun httpGet(port: Int, path: String): HttpResponse<String> {
        val uri = URI.create("http://localhost:$port$path")
        val request = HttpRequest.newBuilder(uri).GET().build()
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString())
    }
}
