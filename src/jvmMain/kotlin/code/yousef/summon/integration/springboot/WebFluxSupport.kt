package code.yousef.summon.integration.springboot

import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.setPlatformRenderer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Support for integrating Summon with Spring WebFlux for reactive rendering.
 * 
 * IMPORTANT: To use these extensions, you must add the following dependencies to your project:
 * - org.springframework.boot:spring-boot-starter-webflux
 * - io.projectreactor.kotlin:reactor-kotlin-extensions
 */
object WebFluxSupport {
    private val renderer = PlatformRenderer()
    
    /**
     * Renders a Summon composable function to a Flux of HTML chunks.
     * This is useful for streaming HTML to clients in a reactive manner.
     *
     * @param title Optional title for the HTML page
     * @param chunkSize Size of HTML chunks to emit (characters)
     * @param content The composable content to render
     * @return Flux of HTML chunks
     */
    fun renderToFlux(
        title: String = "Summon App",
        chunkSize: Int = 1024,
        content: @Composable () -> Unit
    ): Flux<String> {
        // Convert the Flow to a Flux using manual conversion
        val flow = renderToFlow(title, chunkSize, content)
        return Flux.create { emitter ->
            // Use blocking collection for simplicity in this example
            // In a real application, you might want to use a non-blocking approach
            kotlinx.coroutines.runBlocking {
                flow.collect { chunk ->
                    emitter.next(chunk)
                }
                emitter.complete()
            }
        }
    }
    
    /**
     * Renders a Summon composable function to a reactive Publisher of HTML chunks.
     *
     * @param title Optional title for the HTML page
     * @param chunkSize Size of HTML chunks to emit (characters)
     * @param content The composable content to render
     * @return Publisher of HTML chunks
     */
    fun renderToPublisher(
        title: String = "Summon App",
        chunkSize: Int = 1024,
        content: @Composable () -> Unit
    ): Publisher<String> {
        // Use the Flux implementation which implements Publisher
        return renderToFlux(title, chunkSize, content)
    }
    
    /**
     * Renders a Summon composable function to a Flow of HTML chunks.
     *
     * @param title Optional title for the HTML page
     * @param chunkSize Size of HTML chunks to emit (characters)
     * @param content The composable content to render
     * @return Flow of HTML chunks
     */
    fun renderToFlow(
        title: String = "Summon App",
        chunkSize: Int = 1024,
        content: @Composable () -> Unit
    ): Flow<String> = flow {
        // Set up the renderer
        setPlatformRenderer(renderer)
        
        // Start with the HTML header
        val header = buildString {
            append("<!DOCTYPE html>\n<html>\n<head>\n")
            append("  <meta charset=\"UTF-8\">\n")
            append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
            append("  <title>$title</title>\n")
            append("</head>\n<body>\n")
        }
        
        emit(header)
        
        // Create the HTML content
        val htmlContent = buildString {
            appendHTML().div {
                content()
            }
        }
        
        // Split the content into chunks
        val chunks = htmlContent.chunked(chunkSize)
        
        // Emit each chunk
        for (chunk in chunks) {
            emit(chunk)
        }
        
        // End with HTML footer
        emit("\n</body>\n</html>")
    }
} 