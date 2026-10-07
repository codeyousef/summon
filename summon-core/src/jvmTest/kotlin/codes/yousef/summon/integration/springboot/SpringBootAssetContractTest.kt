package codes.yousef.summon.integration.springboot

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.WriteListener
import jakarta.servlet.http.HttpServletResponse
import codes.yousef.summon.runtime.CallbackRegistry
import org.springframework.http.HttpHeaders
import java.lang.reflect.Proxy
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SpringBootAssetContractTest {
    @AfterTest
    fun clearCallbacks() {
        CallbackRegistry.clear()
    }

    private fun request(acceptEncoding: String): HttpServletRequest = Proxy.newProxyInstance(
        HttpServletRequest::class.java.classLoader,
        arrayOf(HttpServletRequest::class.java)
    ) { _, method, args ->
        when {
            method.name == "getHeader" && args?.singleOrNull() == "Accept-Encoding" -> acceptEncoding
            method.returnType == Boolean::class.javaPrimitiveType -> false
            method.returnType == Int::class.javaPrimitiveType -> 0
            method.returnType == Long::class.javaPrimitiveType -> 0L
            else -> null
        }
    } as HttpServletRequest

    @Test
    fun libraryAssetsExposeTypesCachingAndOptionalGzip() {
        val plain = SpringBootRenderer.getSummonAsset("summon-hydration.js")
        assertTrue(plain.statusCode.is2xxSuccessful)
        assertEquals("application/javascript", assertNotNull(plain.headers.contentType).toString())
        assertEquals("public, max-age=31536000, immutable", plain.headers.cacheControl)
        assertEquals("Accept-Encoding", plain.headers.getFirst(HttpHeaders.VARY))
        val plainBody = assertNotNull(plain.body)

        val gzip = SpringBootRenderer.getSummonAsset("summon-hydration.js", request("br, gzip"))
        assertEquals("gzip", gzip.headers.getFirst(HttpHeaders.CONTENT_ENCODING))
        val gzipBody = assertNotNull(gzip.body)
        assertTrue(gzipBody.size < plainBody.size)
        assertEquals(0x1f.toByte(), gzipBody[0])
        assertEquals(0x8b.toByte(), gzipBody[1])
        assertContentEquals(gzipBody, SpringBootRenderer.getSummonAsset("summon-hydration.js", request("gzip")).body)

        val wasm = SpringBootRenderer.getSummonAsset("summon-hydration.wasm", request("gzip"))
        assertEquals("application/wasm", assertNotNull(wasm.headers.contentType).toString())
        assertNull(wasm.headers.getFirst(HttpHeaders.CONTENT_ENCODING))
        val wrapper = SpringBootRenderer.getSummonAsset("summon-hydration.wasm.js")
        assertEquals("application/javascript", assertNotNull(wrapper.headers.contentType).toString())
        assertTrue(SpringBootRenderer.getSummonAsset("missing.asset").statusCode.is4xxClientError)
    }

    @Test
    fun servletAssetHandlerNormalizesPathsTypesAndMissingResponses() {
        val script = ResponseCapture()
        SpringBootRenderer.handleSummonAsset(requestForPath("/static/summon-hydration.js"), script.response)
        assertEquals(200, script.status)
        assertEquals("application/javascript", script.contentType)
        assertTrue(script.bytes.size() > 0)

        val wasm = ResponseCapture()
        SpringBootRenderer.handleSummonAsset(requestForPath("/summon-hydration.wasm"), wasm.response)
        assertEquals(200, wasm.status)
        assertEquals("application/wasm", wasm.contentType)

        val missingHash = ResponseCapture()
        SpringBootRenderer.handleSummonAsset(requestForPath("/deadbeef.wasm"), missingHash.response)
        assertEquals(404, missingHash.status)
        assertTrue(missingHash.text.toString().contains("\"asset\":\"deadbeef.wasm\""))

        val missingBinary = ResponseCapture()
        SpringBootRenderer.handleSummonAsset(requestForPath("/missing.bin"), missingBinary.response)
        assertEquals(404, missingBinary.status)
        assertEquals("application/json", missingBinary.contentType)
    }

    @Test
    fun callbacksRequireCapabilitiesExecuteOnceAndReportMissingEntries() {
        assertTrue(SpringBootRenderer.handleCallback(null, null).statusCode.is4xxClientError)
        assertTrue(SpringBootRenderer.handleCallback("id", "").statusCode.is4xxClientError)

        var calls = 0
        CallbackRegistry.beginRender()
        val callbackId = CallbackRegistry.registerCallback { calls++ }
        val context = CallbackRegistry.finishRenderAndCollectCallbacks()
        assertTrue(SpringBootRenderer.handleCallback(callbackId, "wrong").statusCode.is4xxClientError)
        assertTrue(SpringBootRenderer.handleCallback(callbackId, context.capability).statusCode.is2xxSuccessful)
        assertEquals(1, calls)
        assertTrue(SpringBootRenderer.handleCallback(callbackId, context.capability).statusCode.is4xxClientError)
    }

    private fun requestForPath(path: String): HttpServletRequest = Proxy.newProxyInstance(
        HttpServletRequest::class.java.classLoader,
        arrayOf(HttpServletRequest::class.java)
    ) { _, method, _ ->
        when {
            method.name == "getServletPath" -> path
            method.returnType == Boolean::class.javaPrimitiveType -> false
            method.returnType == Int::class.javaPrimitiveType -> 0
            method.returnType == Long::class.javaPrimitiveType -> 0L
            else -> null
        }
    } as HttpServletRequest

    private class ResponseCapture {
        var status = 0
        var contentType: String? = null
        val bytes = ByteArrayOutputStream()
        val text = StringWriter()
        private val output = object : ServletOutputStream() {
            override fun isReady(): Boolean = true
            override fun setWriteListener(listener: WriteListener?) = Unit
            override fun write(value: Int) = bytes.write(value)
        }
        val response: HttpServletResponse = Proxy.newProxyInstance(
            HttpServletResponse::class.java.classLoader,
            arrayOf(HttpServletResponse::class.java)
        ) { _, method, args ->
            when (method.name) {
                "setStatus" -> status = args!![0] as Int
                "setContentType" -> contentType = args!![0] as String
                "setHeader" -> Unit
                "getOutputStream" -> output
                "getWriter" -> PrintWriter(text, true)
            }
            when {
                method.name == "getOutputStream" -> output
                method.name == "getWriter" -> PrintWriter(text, true)
                method.returnType == Boolean::class.javaPrimitiveType -> false
                method.returnType == Int::class.javaPrimitiveType -> 0
                method.returnType == Long::class.javaPrimitiveType -> 0L
                else -> null
            }
        } as HttpServletResponse
    }

    @Test
    fun rendererFactoryReturnsIndependentInstances() {
        val first = SpringBootRenderer.getCurrentRenderer()
        val second = SpringBootRenderer.getCurrentRenderer()
        assertTrue(first !== second)
    }
}
