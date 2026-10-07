package codes.yousef.summon.integration

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.integration.ktor.KtorRenderer
import codes.yousef.summon.integration.quarkus.QuarkusRenderer
import io.vertx.core.Future
import io.vertx.core.MultiMap
import io.vertx.core.http.HttpServerResponse
import java.lang.reflect.Proxy
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class FrameworkRendererContractTest {
    @Test
    fun ktorStringRenderingUsesARealCompositionAndPropagatesFailures() {
        val renderer = KtorRenderer()
        val html = renderer.renderToString { Text("Ktor body") }
        assertContains(html, "Ktor body")
        assertContains(html, "viewport")
        assertFailsWith<IllegalStateException> {
            renderer.renderToString { error("synthetic render failure") }
        }
    }

    @Test
    fun quarkusStaticAndHydratedRenderingApplyStatusHeadersAndBody() {
        val calls = mutableListOf<Pair<String, List<Any?>>>()
        lateinit var response: HttpServerResponse
        response = Proxy.newProxyInstance(
            HttpServerResponse::class.java.classLoader,
            arrayOf(HttpServerResponse::class.java)
        ) { _, method, arguments ->
            val args = arguments?.toList().orEmpty()
            calls += method.name to args
            when {
                method.returnType == HttpServerResponse::class.java -> response
                method.returnType == Future::class.java -> Future.succeededFuture<Void>()
                method.returnType == MultiMap::class.java -> MultiMap.caseInsensitiveMultiMap()
                method.returnType == Boolean::class.javaPrimitiveType -> false
                method.returnType == Int::class.javaPrimitiveType -> 0
                method.returnType == Long::class.javaPrimitiveType -> 0L
                else -> null
            }
        } as HttpServerResponse

        val renderer = QuarkusRenderer(response)
        renderer.render(title = "Static", statusCode = 201) { Text("Static body") }
        renderer.renderHydrated(statusCode = 202) { Text("Hydrated body") }

        assertTrue(calls.any { (name, args) -> name == "setStatusCode" && args == listOf(201) })
        assertTrue(calls.any { (name, args) -> name == "setStatusCode" && args == listOf(202) })
        assertTrue(calls.any { (name, args) -> name == "putHeader" && args.firstOrNull() == "Content-Type" })
        assertTrue(calls.any { (name, args) -> name == "putHeader" && args.firstOrNull() == "Content-Security-Policy" })
        val bodies = calls.filter { it.first == "end" }.flatMap { it.second }.joinToString("\n")
        assertContains(bodies, "Static body")
        assertContains(bodies, "Hydrated body")
        assertEquals(2, calls.count { it.first == "end" })
    }
}
