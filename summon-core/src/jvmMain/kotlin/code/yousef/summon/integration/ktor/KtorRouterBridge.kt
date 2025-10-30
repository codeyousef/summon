package code.yousef.summon.integration.ktor

import code.yousef.summon.runtime.CallbackRegistry
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import code.yousef.summon.routing.DefaultPageRegistry
import code.yousef.summon.routing.PageLoader
import code.yousef.summon.routing.PageRegistry
import code.yousef.summon.routing.RouterComponent
import code.yousef.summon.routing.createFileBasedServerRouter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlin.LazyThreadSafetyMode
import kotlin.text.Charsets

private val defaultNotFoundHandler: suspend ApplicationCall.() -> Unit = {
    respond(HttpStatusCode.NotFound)
}

/**
 * Mount the Summon server router at a base path in Ktor.
 *
 * @param basePath e.g. "/" or "/app"
 * @param enableHydration when true, render with hydration support
 * @param notFound handler for unmatched routes (defaults to 404)
 */
fun Routing.summonRouter(
    basePath: String = "/",
    enableHydration: Boolean = true,
    notFound: suspend ApplicationCall.() -> Unit = defaultNotFoundHandler
) {
    val normalizedBasePath = basePath.normalizeBasePath()
    val registry: PageRegistry by lazy(LazyThreadSafetyMode.PUBLICATION) {
        DefaultPageRegistry().apply {
            PageLoader.registerPages(this)
        }
    }

    val handler: suspend ApplicationCall.() -> Unit = handler@{
        val requestPath = resolveRouterPath(normalizedBasePath)
        val hasRoute = registry.hasRouteFor(requestPath)
        val hasSummonNotFound = registry.getNotFoundPage() != null

        if (!hasRoute && (!hasSummonNotFound || notFound !== defaultNotFoundHandler)) {
            notFound(this)
            return@handler
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

        val status = if (hasRoute) HttpStatusCode.OK else HttpStatusCode.NotFound
        respondText(html, ContentType.Text.Html.withCharset(Charsets.UTF_8), status)
    }

    val installRoutes: Route.() -> Unit = {
        get {
            call.handler()
        }
        get("{...}") {
            call.handler()
        }
    }

    if (normalizedBasePath == "/") {
        this.installRoutes()
    } else {
        route(normalizedBasePath) {
            installRoutes()
        }
    }
}

private fun String.normalizeBasePath(): String {
    if (isBlank() || this == "/") {
        return "/"
    }

    val withLeadingSlash = if (startsWith('/')) this else "/$this"
    return withLeadingSlash.trimEnd('/').ifBlank { "/" }
}

private fun ApplicationCall.resolveRouterPath(basePath: String): String {
    val fullPath = request.path()
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
