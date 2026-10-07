package codes.yousef.summon.integration.quarkus

import codes.yousef.summon.routing.*
import codes.yousef.summon.integration.ensureRouterLeadingSlash
import codes.yousef.summon.integration.hasRouterRouteFor
import codes.yousef.summon.integration.normalizeRouterBasePath
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.clearPlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer
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
    val normalizedBasePath = basePath.normalizeRouterBasePath()
    val registry: PageRegistry by lazy(LazyThreadSafetyMode.PUBLICATION) {
        DefaultPageRegistry().apply {
            PageLoader.registerPages(this)
        }
    }

    val handler: RoutingContext.() -> Unit = handler@{
        val requestPath = resolveRouterPath(normalizedBasePath)
        val hasRoute = registry.hasRouterRouteFor(requestPath)
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


private fun RoutingContext.resolveRouterPath(basePath: String): String {
    val fullPath = this.normalizedPath() ?: request().path()
    if (basePath == "/") {
        return fullPath.ensureRouterLeadingSlash().ifBlank { "/" }
    }

    val relative = fullPath.removePrefix(basePath).ifBlank { "/" }
    return relative.ensureRouterLeadingSlash()
}
