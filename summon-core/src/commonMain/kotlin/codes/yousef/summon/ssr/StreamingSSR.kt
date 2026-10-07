package codes.yousef.summon.ssr

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.getPlatformRenderer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.stream.appendHTML

/**
 * Implementation of streaming server-side rendering for Summon components
 * This allows rendering large pages as a stream of HTML chunks
 */
class StreamingRenderer(
    private val hydrationSupport: HydrationSupport = StandardHydrationSupport(),
    private val platformRenderer: PlatformRenderer = getPlatformRenderer(),
    private val chunkSize: Int = 4096
) : StreamingServerSideRenderer {
    /**
     * Render a composable to an HTML stream
     *
     * @param composable The composable to render
     * @param context Optional rendering context with additional metadata
     * @return Flow of HTML chunks
     */
    override fun renderStream(composable: @Composable () -> Unit, context: RenderContext): Flow<String> = flow {
        // First, emit the HTML header
        emit(generateHtmlHeader(context))

        // Then, emit the body opening tag and root div with streaming attributes
        emit(
            """<body>
               |<div id="root" data-summon-hydration="root" data-summon-streaming="true">
               |""".trimMargin()
        )

        // Render the component in chunks
        val fullHtml = renderToString(composable)

        // Use intelligent chunking for better HTML structure preservation
        val chunks = intelligentChunking(fullHtml, chunkSize)

        chunks.forEachIndexed { index, chunk ->
            emit("""<div data-summon-chunk="${index + 1}">$chunk</div>""")
            emit("""<template data-summon-chunk-loaded="${index + 1}"></template>""")
        }

        // Emit the closing div tag
        emit("</div>")

        context.publicState?.let {
            emit(generateInitialStateScript(context))
        }

        // Add hydration script if needed
        if (context.enableHydration) {
            // Generate hydration data
            val hydrationData = hydrationSupport.generateHydrationData(composable, HydrationStrategy.PROGRESSIVE)

            val safeHydrationData = scriptSafeJson(hydrationData)
            emit("""<script type="application/json" id="summon-hydration-data">$safeHydrationData</script>""")
            emit("""<template data-summon-stream-complete="true" data-chunks="${chunks.size}"></template>""")
            emit("<script src=\"/summon-hydration.js\" defer></script>")
        }

        // Close the body and html tags
        emit("\n</body>\n</html>")
    }

    /**
     * Intelligently chunk HTML content at logical boundaries
     *
     * @param html The HTML content to chunk
     * @param targetChunkSize The target size for each chunk
     * @return A list of HTML chunks
     */
    private fun intelligentChunking(html: String, targetChunkSize: Int): List<String> {
        if (html.length <= targetChunkSize) {
            return listOf(html)
        }

        val chunks = mutableListOf<String>()
        var currentPos = 0

        while (currentPos < html.length) {
            // Calculate the end position for this chunk
            var endPos = minOf(currentPos + targetChunkSize, html.length)

            // If we're not at the end of the string, try to find a good breaking point
            if (endPos < html.length) {
                // Look for closing tags as good breaking points
                val closingTags = listOf(
                    "</div>", "</p>", "</section>", "</article>",
                    "</li>", "</ul>", "</ol>", "</table>",
                    "</tr>", "</td>", "</h1>", "</h2>", "</h3>"
                )

                // Find the last occurrence of any closing tag within our range
                var bestBreakPoint = -1
                for (tag in closingTags) {
                    val tagPos = html.lastIndexOf(tag, endPos)
                    if (tagPos > currentPos && tagPos + tag.length <= endPos && tagPos > bestBreakPoint) {
                        bestBreakPoint = tagPos + tag.length
                    }
                }

                // If we found a good breaking point, use it
                if (bestBreakPoint > 0) {
                    endPos = bestBreakPoint
                } else {
                    // Otherwise, look for the end of a tag or a space
                    val tagEnd = html.indexOf('>', endPos)
                    if (tagEnd > 0 && tagEnd - endPos < 100) { // Don't look too far ahead
                        endPos = tagEnd + 1
                    } else {
                        // Last resort: break at a space
                        val spacePos = html.lastIndexOf(' ', endPos)
                        if (spacePos > currentPos && spacePos - currentPos >= targetChunkSize / 2) {
                            endPos = spacePos + 1
                        }
                    }
                }
            }

            // Extract the chunk and add it to our list
            chunks.add(html.substring(currentPos, endPos))
            currentPos = endPos
        }

        return chunks
    }

    /**
     * Generates the HTML header section
     */
    private fun generateHtmlHeader(context: RenderContext): String {
        val seo = context.seoMetadata

        val metaTags = buildMetaTags(seo, context.metadata)
        val openGraphTags = buildOpenGraphTags(seo.openGraph)
        val twitterCardTags = buildTwitterCardTags(seo.twitterCard)
        val structuredDataScript = if (seo.structuredData.isNotEmpty()) {
            """<script type="application/ld+json">${scriptSafeJson(seo.structuredData)}</script>"""
        } else ""

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                $metaTags
                $openGraphTags
                $twitterCardTags
                $structuredDataScript
                ${generateCanonicalLink(seo.canonical)}
                <title>${seo.title}</title>
                <link rel="stylesheet" href="/summon.css">
            </head>
        """.trimIndent()
    }

    /**
     * Renders a composable to a string
     */
    internal fun renderToString(composable: @Composable () -> Unit): String {
        return platformRenderer.renderComposableRoot {
            composable()
        }
    }

    /**
     * Builds meta tags from SEO metadata
     */
    private fun buildMetaTags(seo: SeoMetadata, additionalMetadata: Map<String, String>): String {
        val sb = StringBuilder()
        sb.appendHTML(prettyPrint = false).apply {
            if (seo.description.isNotEmpty()) {
                meta(name = "description", content = seo.description)
            }
            if (seo.keywords.isNotEmpty()) {
                meta(name = "keywords", content = seo.keywords.joinToString(", "))
            }
            meta(name = "robots", content = seo.robots)
            seo.customMetaTags.forEach { (name, content) ->
                meta(name = name, content = content)
            }
            additionalMetadata.forEach { (name, content) ->
                meta(name = name, content = content)
            }
        }
        return sb.toString()
    }

    /**
     * Builds OpenGraph tags from OpenGraph metadata
     */
    private fun buildOpenGraphTags(og: OpenGraphMetadata): String {
        val sb = StringBuilder()
        sb.appendHTML(prettyPrint = false).apply {
            if (og.title.isNotEmpty()) meta(name = "og:title", content = og.title)
            if (og.description.isNotEmpty()) meta(name = "og:description", content = og.description)
            if (og.type.isNotEmpty()) meta(name = "og:type", content = og.type)
            if (og.url.isNotEmpty()) meta(name = "og:url", content = og.url)
            if (og.image.isNotEmpty()) meta(name = "og:image", content = og.image)
            if (og.siteName.isNotEmpty()) meta(name = "og:site_name", content = og.siteName)
        }
        return sb.toString()
    }

    /**
     * Builds Twitter Card tags from Twitter Card metadata
     */
    private fun buildTwitterCardTags(twitter: TwitterCardMetadata): String {
        val sb = StringBuilder()
        sb.appendHTML(prettyPrint = false).apply {
            if (twitter.card.isNotEmpty()) meta(name = "twitter:card", content = twitter.card)
            if (twitter.site.isNotEmpty()) meta(name = "twitter:site", content = twitter.site)
            if (twitter.creator.isNotEmpty()) meta(name = "twitter:creator", content = twitter.creator)
            if (twitter.title.isNotEmpty()) meta(name = "twitter:title", content = twitter.title)
            if (twitter.description.isNotEmpty()) meta(name = "twitter:description", content = twitter.description)
            if (twitter.image.isNotEmpty()) meta(name = "twitter:image", content = twitter.image)
        }
        return sb.toString()
    }

    /**
     * Generates a canonical link if a canonical URL is provided
     */
    private fun generateCanonicalLink(canonical: String): String {
        return if (canonical.isNotEmpty()) {
            val sb = StringBuilder()
            sb.appendHTML(prettyPrint = false).link(rel = "canonical", href = canonical)
            sb.toString()
        } else ""
    }

    /** Emits explicitly public state as inert bounded JSON. */
    private fun generateInitialStateScript(context: RenderContext): String {
        val publicState = context.publicState ?: return ""
        val serializedState = scriptSafeJson(publicState.json)
        return """<script id="summon-public-state" type="application/json">$serializedState</script>"""
    }
}

/**
 * Utility object for streaming server-side rendering
 */
object StreamingSSR {
    private val renderer = StreamingRenderer()

    /**
     * Intelligently chunk HTML content at logical boundaries
     *
     * @param html The HTML content to chunk
     * @param targetChunkSize The target size for each chunk
     * @return A list of HTML chunks
     */
    private fun intelligentChunking(html: String, targetChunkSize: Int): List<String> {
        if (html.length <= targetChunkSize) {
            return listOf(html)
        }

        val chunks = mutableListOf<String>()
        var currentPos = 0

        while (currentPos < html.length) {
            // Calculate the end position for this chunk
            var endPos = minOf(currentPos + targetChunkSize, html.length)

            // If we're not at the end of the string, try to find a good breaking point
            if (endPos < html.length) {
                // Look for closing tags as good breaking points
                val closingTags = listOf(
                    "</div>",
                    "</p>",
                    "</section>",
                    "</article>",
                    "</li>",
                    "</ul>",
                    "</ol>",
                    "</table>",
                    "</tr>",
                    "</td>"
                )

                // Find the last occurrence of any closing tag within our range
                var bestBreakPoint = -1
                for (tag in closingTags) {
                    val tagPos = html.lastIndexOf(tag, endPos)
                    if (tagPos > currentPos && tagPos + tag.length <= endPos && tagPos > bestBreakPoint) {
                        bestBreakPoint = tagPos + tag.length
                    }
                }

                // If we found a good breaking point, use it
                if (bestBreakPoint > 0) {
                    endPos = bestBreakPoint
                } else {
                    // Otherwise, look for the end of a tag or a space
                    val tagEnd = html.indexOf('>', endPos)
                    if (tagEnd > 0 && tagEnd - endPos < 100) { // Don't look too far ahead
                        endPos = tagEnd + 1
                    } else {
                        // Last resort: break at a space
                        val spacePos = html.lastIndexOf(' ', endPos)
                        if (spacePos > currentPos && spacePos - currentPos >= targetChunkSize / 2) {
                            endPos = spacePos + 1
                        }
                    }
                }
            }

            // Extract the chunk and add it to our list
            chunks.add(html.substring(currentPos, endPos))
            currentPos = endPos
        }

        return chunks
    }

    /**
     * Render a composable to an HTML stream
     *
     * @param composable The composable to render
     * @param context Optional rendering context with additional metadata
     * @return Flow of HTML chunks
     */
    fun renderStream(composable: @Composable () -> Unit, context: RenderContext = RenderContext()): Flow<String> {
        return renderer.renderStream(composable, context)
    }

    /**
     * Create a custom streaming renderer with specific configuration
     *
     * @param hydrationSupport The hydration support implementation to use
     * @param chunkSize The size of HTML chunks to emit
     * @return A configured StreamingRenderer
     */
    fun createRenderer(
        hydrationSupport: HydrationSupport = StandardHydrationSupport(),
        platformRenderer: PlatformRenderer = getPlatformRenderer(),
        chunkSize: Int = 4096
    ): StreamingRenderer {
        return StreamingRenderer(hydrationSupport, platformRenderer, chunkSize)
    }

    /**
     * Renders a composable function to a Flow of String chunks.
     *
     * @param content The root composable function to render.
     * @return A Flow emitting HTML chunks as strings.
     */
    fun renderToFlow(content: @Composable () -> Unit): Flow<String> = flow {

        // 2. Set up document structure
        // Emit header part first for faster first contentful paint
        emit("<!DOCTYPE html>\n<html>\n<head>")
        emit("<meta charset=\"UTF-8\">\n")
        emit("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
        emit("<title>Summon Streaming SSR</title>\n")

        emit("</head>\n<body>")

        // 3. Begin content container with streaming attributes
        emit("<div id=\"summon-root\" data-summon-streaming=\"true\">\n")

        // 4. Execute content within composition context to generate HTML in chunks
        val chunks = mutableListOf<String>()

        // Render the full HTML
        val fullHtml = renderer.renderToString(content)

        // Use intelligent chunking to split at logical boundaries
        val chunkSize = 4096
        chunks.addAll(intelligentChunking(fullHtml, chunkSize))

        chunks.forEachIndexed { index, chunk ->
            emit("<div data-summon-chunk=\"$index\">$chunk</div>\n")
            emit("<template data-summon-chunk-loaded=\"$index\"></template>\n")
        }

        // 6. Close the content container
        emit("</div>\n")

        val hydrationData = scriptSafeJson(StreamingHydrationSupport.generateHydrationData())
        emit(
            """
            <script id="summon-hydration-data" type="application/json">$hydrationData</script>
            <template data-summon-stream-complete="true" data-chunks="${chunks.size}"></template>
            <script src="/summon-hydration.js" defer></script>
        """.trimIndent()
        )

        // 8. Close document
        emit("</body>\n</html>")
    }
}

/** Minimal streaming hydration metadata; component effects stay client-owned. */
private object StreamingHydrationSupport {
    fun generateHydrationData(): String =
        """{"version":1,"strategy":"PROGRESSIVE","components":[]}"""
}
