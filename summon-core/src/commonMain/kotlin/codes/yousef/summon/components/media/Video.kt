package codes.yousef.summon.components.media

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Video source definition for multiple format support.
 *
 * @property src URL to the video file
 * @property type MIME type (e.g., "video/mp4", "video/webm")
 */
data class VideoSource(
    val src: String,
    val type: String
)

/**
 * # Video Component
 *
 * A type-safe video element component that maps Kotlin booleans to proper HTML5
 * video attributes and enforces browser policies (e.g., autoplay requires muted).
 *
 * ## Features
 *
 * - **Type-Safe Attributes**: Boolean properties for autoplay, muted, loop, controls
 * - **Browser Policy Enforcement**: Automatically sets muted when autoplay is requested
 * - **Multiple Sources**: Supports fallback video sources for format compatibility
 * - **Poster Support**: Optional poster image for initial display
 * - **Accessibility**: Built-in support for captions and descriptions
 *
 * ## Browser Autoplay Policy
 *
 * Modern browsers require videos to be muted for autoplay to work. If you set
 * `autoplay = true` with `muted = false`, the component will automatically
 * set `muted = true` to comply with browser policies.
 *
 * ## Usage
 *
 * ```kotlin
 * // Basic usage
 * Video(
 *     src = "https://example.com/video.mp4",
 *     modifier = Modifier().width("100%").maxWidth("800px")
 * )
 *
 * // Autoplay with loop (muted is auto-enforced)
 * Video(
 *     src = "https://example.com/hero.mp4",
 *     autoplay = true,
 *     loop = true,
 *     controls = false,
 *     poster = "https://example.com/poster.jpg"
 * )
 *
 * // Multiple sources for format fallback
 * Video(
 *     sources = listOf(
 *         VideoSource("video.webm", "video/webm"),
 *         VideoSource("video.mp4", "video/mp4")
 *     ),
 *     controls = true
 * )
 * ```
 *
 * @param src Primary video source URL (ignored if sources is provided)
 * @param sources List of video sources for format fallback
 * @param poster URL to poster image displayed before playback
 * @param autoplay Whether to start playing automatically (requires muted)
 * @param muted Whether the video is muted
 * @param loop Whether to loop the video
 * @param controls Whether to show playback controls
 * @param playsInline Whether to play inline on mobile (not fullscreen)
 * @param preload Preload strategy: "none", "metadata", or "auto"
 * @param crossorigin CORS setting: "anonymous" or "use-credentials"
 * @param width Video width (can also use modifier)
 * @param height Video height (can also use modifier)
 * @param ariaLabel Accessibility label for the video
 * @param modifier Styling modifier for the video element
 *
 * @see VideoSource for defining multiple sources
 * @since 1.0.0
 */
@Composable
fun Video(
    src: String? = null,
    sources: List<VideoSource> = emptyList(),
    poster: String? = null,
    autoplay: Boolean = false,
    muted: Boolean = false,
    loop: Boolean = false,
    controls: Boolean = true,
    playsInline: Boolean = true,
    preload: String = "metadata",
    crossorigin: String? = null,
    width: String? = null,
    height: String? = null,
    ariaLabel: String? = null,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    
    // Enforce browser autoplay policy: autoplay requires muted
    val effectiveMuted = if (autoplay && !muted) {
        println("[Video] Warning: autoplay requires muted. Setting muted=true.")
        true
    } else {
        muted
    }
    
    // Build attributes
    var finalModifier = modifier
    
    // Add dimensions
    width?.let { finalModifier = finalModifier.style("width", it) }
    height?.let { finalModifier = finalModifier.style("height", it) }
    
    // Add video-specific attributes
    if (autoplay) finalModifier = finalModifier.attribute("autoplay", "")
    if (effectiveMuted) finalModifier = finalModifier.attribute("muted", "")
    if (loop) finalModifier = finalModifier.attribute("loop", "")
    if (controls) finalModifier = finalModifier.attribute("controls", "")
    if (playsInline) finalModifier = finalModifier.attribute("playsinline", "")
    
    finalModifier = finalModifier.attribute("preload", preload)
    
    poster?.let { finalModifier = finalModifier.attribute("poster", it) }
    crossorigin?.let { finalModifier = finalModifier.attribute("crossorigin", it) }
    ariaLabel?.let { finalModifier = finalModifier.ariaAttribute("label", it) }
    
    // Add data attributes for JavaScript interaction
    finalModifier = finalModifier
        .dataAttribute("video-autoplay", autoplay.toString())
        .dataAttribute("video-muted", effectiveMuted.toString())
    
    // Determine video sources
    val videoSources = if (sources.isNotEmpty()) {
        sources
    } else if (src != null) {
        // Infer type from URL extension
        val type = when {
            src.endsWith(".mp4", ignoreCase = true) -> "video/mp4"
            src.endsWith(".webm", ignoreCase = true) -> "video/webm"
            src.endsWith(".ogg", ignoreCase = true) -> "video/ogg"
            src.endsWith(".mov", ignoreCase = true) -> "video/quicktime"
            else -> "video/mp4"
        }
        listOf(VideoSource(src, type))
    } else {
        emptyList()
    }
    
    // Render video element with sources
    renderer.renderHtmlTag("video", finalModifier) {
        videoSources.forEach { source ->
            val sourceModifier = Modifier()
                .attribute("src", source.src)
                .attribute("type", source.type)
            renderer.renderHtmlTag("source", sourceModifier) {}
        }
        
        // Fallback text
        renderer.renderText(
            text = "Your browser does not support the video element.",
            modifier = Modifier()
        )
    }
}

/**
 * # Audio Component
 *
 * A type-safe audio element component.
 *
 * @param src Primary audio source URL
 * @param autoplay Whether to start playing automatically
 * @param muted Whether the audio is muted
 * @param loop Whether to loop the audio
 * @param controls Whether to show playback controls
 * @param preload Preload strategy
 * @param modifier Styling modifier
 *
 * @since 1.0.0
 */
@Composable
fun Audio(
    src: String,
    autoplay: Boolean = false,
    muted: Boolean = false,
    loop: Boolean = false,
    controls: Boolean = true,
    preload: String = "metadata",
    ariaLabel: String? = null,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    
    var finalModifier = modifier
        .attribute("src", src)
        .attribute("preload", preload)
    
    if (autoplay) finalModifier = finalModifier.attribute("autoplay", "")
    if (muted) finalModifier = finalModifier.attribute("muted", "")
    if (loop) finalModifier = finalModifier.attribute("loop", "")
    if (controls) finalModifier = finalModifier.attribute("controls", "")
    ariaLabel?.let { finalModifier = finalModifier.ariaAttribute("label", it) }
    
    renderer.renderHtmlTag("audio", finalModifier) {
        renderer.renderText(
            text = "Your browser does not support the audio element.",
            modifier = Modifier()
        )
    }
}
