package codes.yousef.summon.integration.ktor

import codes.yousef.summon.routing.*
import codes.yousef.summon.integration.ensureRouterLeadingSlash
import codes.yousef.summon.integration.hasRouterRouteFor
import codes.yousef.summon.integration.normalizeRouterBasePath
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.RenderingContextElement
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Route
import kotlinx.coroutines.withContext

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
    val normalizedBasePath = basePath.normalizeRouterBasePath()
    val registry: PageRegistry by lazy(LazyThreadSafetyMode.PUBLICATION) {
        DefaultPageRegistry().apply {
            PageLoader.registerPages(this)
        }
    }

    val handler: suspend ApplicationCall.() -> Unit = handler@{
        val requestPath = resolveRouterPath(normalizedBasePath)
        val hasRoute = registry.hasRouterRouteFor(requestPath)
        val hasSummonNotFound = registry.getNotFoundPage() != null

        if (!hasRoute && (!hasSummonNotFound || notFound !== defaultNotFoundHandler)) {
            notFound(this)
            return@handler
        }

        val renderer = PlatformRenderer()
        val router = createFileBasedServerRouter(requestPath)

        val html = withContext(RenderingContextElement(renderer)) {
            if (enableHydration) {
                renderer.renderComposableRootWithHydration {
                    RouterComponent(router, requestPath)
                }
            } else {
                renderer.renderComposableRoot {
                    RouterComponent(router, requestPath)
                }
            }
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


private fun ApplicationCall.resolveRouterPath(basePath: String): String {
    val fullPath = request.path()
    if (basePath == "/") {
        return fullPath.ensureRouterLeadingSlash().ifBlank { "/" }
    }

    val relative = fullPath.removePrefix(basePath).ifBlank { "/" }
    return relative.ensureRouterLeadingSlash()
}
