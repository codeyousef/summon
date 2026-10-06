package codes.yousef.summon.fixture

import codes.yousef.aether.core.Attributes
import codes.yousef.aether.core.Cookie
import codes.yousef.aether.core.Cookies
import codes.yousef.aether.core.Exchange
import codes.yousef.aether.core.Headers
import codes.yousef.aether.core.HttpMethod
import codes.yousef.aether.core.Request
import codes.yousef.aether.core.Response
import codes.yousef.summon.aether.respondSummon
import codes.yousef.summon.components.display.Text
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val deepLinks = listOf(
        "/mail",
        "/mail/thread/opaque-1",
        "/mail/compose",
        "/calendar",
        "/calendar/event/event-1",
        "/aliases",
        "/aliases/alias-1",
        "/security",
        "/security/devices",
        "/security/recovery"
    )
    var renderedShell = ""
    for (path in deepLinks) {
        val exchange = CapturingExchange(path)
        exchange.respondSummon { Text("Public shell: sign in to unlock") }
        check(exchange.response.statusCode == 200)
        check(exchange.response.body.contains("Public shell: sign in to unlock"))
        check(!exchange.response.body.contains("opaque-1"))
        renderedShell = exchange.response.body.toString()
    }
    println(renderedShell)
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

    override suspend fun write(data: ByteArray) {
        body.append(data.decodeToString())
    }

    override suspend fun write(text: String) {
        body.append(text)
    }

    override suspend fun end() = Unit

    override fun setHeader(name: String, value: String) {
        headers.set(name, value)
    }

    override fun addHeader(name: String, value: String) {
        headers.add(name, value)
    }

    override fun setCookie(cookie: Cookie) {
        cookies.add(cookie)
    }
}
