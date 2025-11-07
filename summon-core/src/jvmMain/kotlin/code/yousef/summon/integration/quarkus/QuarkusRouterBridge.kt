package code.yousef.summon.integration.quarkus

import code.yousef.summon.routing.*
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

private val defaultNotFoundHandler: RoutingContext.() -> Unit = {
    response()
        .setStatusCode(404)
        .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
        .end()
}

/**
 * Mount the Summon server router at a base path in Vert.x/Quarkus.
 *
 * @param basePath e.g. "/" or "/app"
 * @param enableHydration when true, render with hydration support
 * @param notFound handler for unmatched routes (defaults to 404)
 */
fun Router.summonRouter(
    basePath: String = "/",
    enableHydration: Boolean = true,
    notFound: RoutingContext.() -> Unit = defaultNotFoundHandler
) {
    val normalizedBasePath = basePath.normalizeBasePath()
    val registry: PageRegistry by lazy(LazyThreadSafetyMode.PUBLICATION) {
        DefaultPageRegistry().apply {
            PageLoader.registerPages(this)
        }
    }

    val handler: RoutingContext.() -> Unit = handler@{
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
            clearPlatformRenderer()
        }

        val status = if (hasRoute) 200 else 404
        response()
            .setStatusCode(status)
            .putHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
            .end(html)
    }

    if (normalizedBasePath == "/") {
        this.get("/").handler(handler)
        this.get("/*").handler(handler)
    } else {
        this.get(normalizedBasePath).handler(handler)
        this.get("$normalizedBasePath/").handler(handler)
        this.get("$normalizedBasePath/*").handler(handler)
    }
}

private fun String.normalizeBasePath(): String {
    if (isBlank() || this == "/") {
        return "/"
    }

    val withLeadingSlash = if (startsWith('/')) this else "/$this"
    return withLeadingSlash.trimEnd('/').ifBlank { "/" }
}

private fun RoutingContext.resolveRouterPath(basePath: String): String {
    val fullPath = this.normalisedPath() ?: request().path()
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
