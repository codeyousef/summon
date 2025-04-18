package code.yousef.summon.ssr

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.CommonComposer
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.getPlatformRenderer
import kotlinx.coroutines.delay
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
        emit("""<body>
               |<div id="root" data-summon-hydration="root" data-summon-streaming="true">
               |<script>
               |    // Initialize streaming state
               |    window.__SUMMON_STREAMING = {
               |        chunks: 0,
               |        complete: false,
               |        startTime: Date.now()
               |    };
               |    // Function to mark chunk as loaded
               |    window.__SUMMON_CHUNK_LOADED = function(index) {
               |        window.__SUMMON_STREAMING.chunks++;
               |        document.dispatchEvent(new CustomEvent('summon:chunk-loaded', { 
               |            detail: { index: index, total: window.__SUMMON_STREAMING.chunks }
               |        }));
               |    };
               |</script>
               |""".trimMargin())

        // Render the component in chunks
        val fullHtml = renderToString(composable)

        // Use intelligent chunking for better HTML structure preservation
        val chunks = intelligentChunking(fullHtml, chunkSize)

        // Emit each chunk with progress tracking
        chunks.forEachIndexed { index, chunk ->
            // Wrap chunk in a container with data attribute for tracking
            emit("""<div data-summon-chunk="${index + 1}">$chunk</div>""")

            // Add a script to mark this chunk as loaded
            emit("""<script>window.__SUMMON_CHUNK_LOADED(${index + 1});</script>""")

            // Add a small delay between chunks to simulate network conditions
            // and allow the browser to process each chunk
            delay(10) // Small delay for demonstration purposes
        }

        // Emit the closing div tag
        emit("</div>")

        // Add initial state script if needed
        if (context.initialState.isNotEmpty()) {
            emit(generateInitialStateScript(context))
        }

        // Add hydration script if needed
        if (context.enableHydration) {
            // Generate hydration data
            val hydrationData = hydrationSupport.generateHydrationData(composable, HydrationStrategy.PROGRESSIVE)

            // Add the hydration data script
            emit("""
                |<script type="application/json" id="summon-hydration-data" style="display:none">
                |    $hydrationData
                |</script>
                |""".trimMargin())

            // Add the streaming completion script
            emit("""
                |<script>
                |    // Mark streaming as complete
                |    window.__SUMMON_STREAMING.complete = true;
                |    window.__SUMMON_STREAMING.totalChunks = ${chunks.size};
                |    window.__SUMMON_STREAMING.endTime = Date.now();
                |    window.__SUMMON_STREAMING.duration = window.__SUMMON_STREAMING.endTime - window.__SUMMON_STREAMING.startTime;
                |    
                |    // Dispatch completion event
                |    document.dispatchEvent(new CustomEvent('summon:streaming-complete', {
                |        detail: { 
                |            chunks: ${chunks.size},
                |            duration: window.__SUMMON_STREAMING.duration
                |        }
                |    }));
                |    
                |    console.log('Summon streaming complete:', window.__SUMMON_STREAMING);
                |</script>
                |""".trimMargin())

            // Add the hydration script
            emit("<script src=\"/summon-hydration.js\"></script>")
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
                val closingTags = listOf("</div>", "</p>", "</section>", "</article>", 
                                         "</li>", "</ul>", "</ol>", "</table>", 
                                         "</tr>", "</td>", "</h1>", "</h2>", "</h3>")

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
            """<script type="application/ld+json">${seo.structuredData}</script>"""
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
                <script>
                    // Script for progressive rendering support
                    document.addEventListener('DOMContentLoaded', () => {
                        console.log('Summon Streaming SSR completed');
                    });
                </script>
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

    /**
     * Generates a script tag with initial state for hydration
     */
    private fun generateInitialStateScript(context: RenderContext): String {
        if (context.initialState.isEmpty()) {
            return """<script>window.__SUMMON_INITIAL_STATE__ = {};</script>"""
        }

        // Serialize the initial state to JSON
        val serializedState = serializeInitialState(context.initialState)

        // Create a script tag with the serialized state and event dispatch
        return """
            <script>
                window.__SUMMON_INITIAL_STATE__ = $serializedState;
                document.dispatchEvent(new CustomEvent('summon:state-loaded'));
                console.log('Summon initial state loaded');
            </script>
        """.trimIndent()
    }

    /**
     * Serialize the initial state to JSON
     */
    private fun serializeInitialState(state: Map<String, Any?>): String {
        if (state.isEmpty()) return "{}"

        return buildString {
            append("{")
            state.entries.forEachIndexed { index, (key, value) ->
                if (index > 0) append(",")
                append("\n    \"$key\": ")
                append(serializeValue(value))
            }
            append("\n}")
        }
    }

    /**
     * Serialize a value to JSON
     */
    private fun serializeValue(value: Any?): String {
        return when (value) {
            null -> "null"
            is String -> "\"${escapeJsonString(value)}\""
            is Number, is Boolean -> value.toString()
            is Map<*, *> -> {
                val map = value.entries.associate { 
                    (it.key as? String ?: it.key.toString()) to it.value 
                }
                buildString {
                    append("{")
                    map.entries.forEachIndexed { index, (key, mapValue) ->
                        if (index > 0) append(", ")
                        append("\"$key\": ")
                        append(serializeValue(mapValue))
                    }
                    append("}")
                }
            }
            is List<*> -> {
                buildString {
                    append("[")
                    value.forEachIndexed { index, item ->
                        if (index > 0) append(", ")
                        append(serializeValue(item))
                    }
                    append("]")
                }
            }
            is Array<*> -> serializeValue(value.toList())
            else -> "\"${escapeJsonString(value.toString())}\""
        }
    }

    /**
     * Escape special characters in JSON strings
     */
    private fun escapeJsonString(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\b", "\\b")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
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
                val closingTags = listOf("</div>", "</p>", "</section>", "</article>", "</li>", "</ul>", "</ol>", "</table>", "</tr>", "</td>")

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
        println("StreamingSSR.renderToFlow called.")

        // 1. Create a streaming renderer context
        val context = RenderContext(enableHydration = true)
        val composer = CommonComposer() // Use CommonComposer instead of JvmComposer

        // 2. Set up document structure
        // Emit header part first for faster first contentful paint
        emit("<!DOCTYPE html>\n<html>\n<head>")
        emit("<meta charset=\"UTF-8\">\n")
        emit("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
        emit("<title>Summon Streaming SSR</title>\n")

        // Emit streaming-specific script that helps with progressive rendering
        emit("""
            <script>
                // Progressive rendering support
                window.__SUMMON_CHUNKS_LOADED = 0;
                window.__SUMMON_STREAMING = true;
                window.__summonChunkLoaded = function() {
                    window.__SUMMON_CHUNKS_LOADED++;
                    document.dispatchEvent(new CustomEvent('summon:chunk-loaded'));
                };
            </script>
        """.trimIndent())
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

        // 5. Emit each chunk with progress markers for client-side processing
        chunks.forEachIndexed { index, chunk ->
            // Wrap each chunk with a progress marker for client-side tracking
            emit("<div data-summon-chunk=\"$index\">$chunk</div>\n")
            emit("<script>window.__summonChunkLoaded();</script>\n")

            // Add a small delay between chunks to simulate network streaming
            // In a real production environment, this would be controlled by network backpressure
            kotlinx.coroutines.delay(10)
        }

        // 6. Close the content container
        emit("</div>\n")

        // 7. Add hydration script for client-side reactivation
        val hydrationData = StreamingHydrationSupport.generateHydrationData(content)
        emit("""
            <script id="summon-hydration-data" type="application/json">
                $hydrationData
            </script>
            <script>
                // Signal that streaming is complete
                window.__SUMMON_STREAMING_COMPLETE = true;
                document.dispatchEvent(new CustomEvent('summon:streaming-complete', {
                    detail: { chunkCount: ${chunks.size} }
                }));
            </script>
            <script src="/summon-hydration.js"></script>
        """.trimIndent())

        // 8. Close document
        emit("</body>\n</html>")
    }
}

/**
 * A simple interface for hydration support in streaming SSR
 */
private object StreamingHydrationSupport {
    /**
     * Generates hydration data for client-side reactivation
     */
    fun generateHydrationData(content: @Composable () -> Unit): String {
        // Create a hydration context to track components
        val context = HydrationContext()

        // Track the composition
        context.trackComposition { content() }

        // Generate hydration data with progressive strategy
        return context.generateHydrationData(HydrationStrategy.PROGRESSIVE)
    }

    /**
     * Generate a unique ID for a component
     */
    private fun generateComponentId(prefix: String): String {
        return "$prefix-${kotlin.random.Random.nextInt(100000, 999999)}"
    }
}
