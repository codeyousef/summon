package code.yousef.summon.integration.springboot

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.CallbackRegistry
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import code.yousef.summon.routing.DefaultPageRegistry
import code.yousef.summon.routing.PageLoader
import code.yousef.summon.routing.PageRegistry
import code.yousef.summon.routing.RouterComponent
import code.yousef.summon.routing.createFileBasedServerRouter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.html.div
import kotlinx.html.stream.appendHTML
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import kotlin.LazyThreadSafetyMode

/**
 * Support for integrating Summon with Spring WebFlux for reactive rendering.
 *
 * IMPORTANT: To use these extensions, you must add the following dependencies to your project:
 * - org.springframework.boot:spring-boot-starter-webflux
 * - io.projectreactor.kotlin:reactor-kotlin-extensions
 */
object WebFluxSupport {
    private val defaultNotFoundHandler: (ServerRequest) -> Mono<ServerResponse> = {
        ServerResponse.status(HttpStatus.NOT_FOUND).build()
    }

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
        return renderToFlow(title, chunkSize, content).asFlux()
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
        // Set up a renderer scoped to this collection
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

            val htmlContent = buildString {
                appendHTML().div {
                    content()
                }
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
     * Mounts the Summon server router as a WebFlux {@link RouterFunction}.
     */
    fun summonRouter(
        basePath: String = "/",
        enableHydration: Boolean = true,
        notFound: (ServerRequest) -> Mono<ServerResponse> = defaultNotFoundHandler
    ): RouterFunction<ServerResponse> {
        val normalizedBasePath = basePath.normalizeBasePath()
        val registry: PageRegistry by lazy(LazyThreadSafetyMode.PUBLICATION) {
            DefaultPageRegistry().apply {
                PageLoader.registerPages(this)
            }
        }

        val handler = HandlerFunction<ServerResponse> { request ->
            handleRouterRequest(request, normalizedBasePath, enableHydration, registry, notFound)
        }

        val exactPath = if (normalizedBasePath == "/") "/" else normalizedBasePath
        val wildcardPath = if (normalizedBasePath == "/") "/{*path}" else "$normalizedBasePath/{*path}"

        return RouterFunctions.route(RequestPredicates.GET(exactPath), handler)
            .andRoute(RequestPredicates.GET(wildcardPath), handler)
    }

    private fun handleRouterRequest(
        request: ServerRequest,
        basePath: String,
        enableHydration: Boolean,
        registry: PageRegistry,
        notFound: (ServerRequest) -> Mono<ServerResponse>
    ): Mono<ServerResponse> {
        val requestPath = request.resolveRouterPath(basePath)
        val hasRoute = registry.hasRouteFor(requestPath)
        val hasSummonNotFound = registry.getNotFoundPage() != null

        if (!hasRoute && (!hasSummonNotFound || notFound !== defaultNotFoundHandler)) {
            return notFound(request)
        }

        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)
        val router = createFileBasedServerRouter(requestPath)

        val html = try {
            if (enableHydration) {
                renderer.renderComposableRootWithHydration {
                    RouterComponent(router, requestPath)
                }
            } else {
                renderer.renderComposableRoot {
                    RouterComponent(router, requestPath)
                }
            }
        } finally {
            CallbackRegistry.clear()
            clearPlatformRenderer()
        }

        val status = if (hasRoute) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ServerResponse.status(status)
            .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
            .bodyValue(html)
    }
}

private fun String.normalizeBasePath(): String {
    if (isBlank() || this == "/") {
        return "/"
    }

    val withLeadingSlash = if (startsWith('/')) this else "/$this"
    return withLeadingSlash.trimEnd('/').ifBlank { "/" }
}

private fun ServerRequest.resolveRouterPath(basePath: String): String {
    val fullPath = this.path()
    if (basePath == "/") {
        return fullPath.ensureLeadingSlash().ifBlank { "/" }
    }

    val relative = fullPath.removePrefix(basePath).ifBlank { "/" }
    return relative.ensureLeadingSlash()
}

private fun String.ensureLeadingSlash(): String = when {
    isBlank() -> "/"
    startsWith('/') -> this
    else -> "/$this"
}

private fun PageRegistry.hasRouteFor(path: String): Boolean {
    val normalizedPath = path.ensureLeadingSlash()
    val routes = getPages()
    if (routes.isEmpty()) return false

    return routes.keys.any { pattern -> patternMatches(pattern, normalizedPath) }
}

private fun patternMatches(pattern: String, path: String): Boolean {
    val normalizedPattern = pattern.ensureLeadingSlash()
    if (normalizedPattern == path) return true

    val patternSegments = normalizedPattern.trim('/').takeIf { it.isNotEmpty() }?.split('/') ?: emptyList()
    val pathSegments = path.trim('/').takeIf { it.isNotEmpty() }?.split('/') ?: emptyList()

    if (patternSegments.isEmpty()) {
        return pathSegments.isEmpty()
    }

    val catchAll = patternSegments.lastOrNull() == "*"
    if (!catchAll && patternSegments.size != pathSegments.size) {
        return false
    }

    if (catchAll && pathSegments.size < patternSegments.size - 1) {
        return false
    }

    patternSegments.forEachIndexed { index, segment ->
        if (segment == "*") {
            return true
        }
        val candidate = pathSegments.getOrNull(index) ?: return false
        if (!segment.startsWith(":")) {
            if (segment != candidate) {
                return false
            }
        }
    }

    return !catchAll && patternSegments.size == pathSegments.size || catchAll
}
