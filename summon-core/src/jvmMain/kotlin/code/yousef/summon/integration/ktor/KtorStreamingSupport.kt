package code.yousef.summon.integration.ktor

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.CallbackRegistry
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.html.div
import kotlinx.html.stream.createHTML

/**
 * Support for streaming Summon component rendering in Ktor applications.
 * This is useful for large pages where you want to start sending HTML to the client
 * before the entire page is rendered.
 *
 * IMPORTANT: To use this class, you must add the following dependencies to your project:
 * - io.ktor:ktor-server-core
 * - io.ktor:ktor-server-html-builder
 */
object KtorStreamingSupport {

    /**
     * Creates a Flow of HTML chunks for a Summon component.
     *
     * @param title The HTML page title
     * @param chunkSize Size of HTML chunks to emit (characters)
     * @param content The composable content to render
     * @return Flow of HTML chunks
     */
    fun createHtmlFlow(
        title: String = "Summon App",
        chunkSize: Int = 1024,
        content: @Composable () -> Unit
    ): Flow<String> = flow {
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)

        try {
            val header = buildString {
                append("<!DOCTYPE html>\n<html>\n<head>\n")
                append("  <meta charset=\"UTF-8\">\n")
                append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
                append("  <title>$title</title>\n")
                append("</head>\n<body>\n")
            }

            emit(header)

            val htmlContent = createHTML().div {
                content()
            }

            val chunks = htmlContent.chunked(chunkSize)
            for (chunk in chunks) {
                emit(chunk)
            }

            emit("\n</body>\n</html>")
        } finally {
            CallbackRegistry.clear()
            clearPlatformRenderer()
        }
    }

    /**
     * Extension function to respond with a streamed Summon component.
     * This function integrates with Ktor's ApplicationCall.
     *
     * @param title The HTML page title
     * @param chunkSize Size of HTML chunks to emit (characters)
     * @param content The composable content to render
     */
    suspend fun ApplicationCall.respondStreamingSummon(
        title: String = "Summon App",
        chunkSize: Int = 1024,
        content: @Composable () -> Unit
    ) {
        respondBytesWriter(ContentType.Text.Html.withCharset(Charsets.UTF_8)) {
            val flow = createHtmlFlow(title, chunkSize, content)
            flow.collect { chunk ->
                writeStringUtf8(chunk)
                flush()
            }
        }
    }
}
