package codes.yousef.summon.aether

import codes.yousef.aether.core.Exchange
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.clearPlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer
import codes.yousef.summon.seo.HeadScope

/**
 * Responds with a server-side rendered Summon page.
 *
 * @param status The HTTP status code (default: 200).
 * @param head A lambda to configure the head elements (title, meta tags, etc.).
 * @param content The composable content to render.
 */
suspend fun Exchange.respondSummon(
    status: Int = 200,
    head: (HeadScope) -> Unit = {},
    content: @Composable () -> Unit
) {
    val renderer = PlatformRenderer()
    setPlatformRenderer(renderer)
    
    try {
        val html = withRenderingContext {
             renderer.renderComposableRoot {
                renderer.renderHeadElements(head)
                content()
            }
        }
        respondHtml(status, html)
    } finally {
        clearPlatformRenderer()
    }
}

internal expect suspend fun <T> withRenderingContext(block: suspend () -> T): T
