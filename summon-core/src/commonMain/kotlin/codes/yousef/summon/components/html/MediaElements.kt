package codes.yousef.summon.components.html

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Media-related HTML5 elements for figures, captions, and embedded content.
 *
 * These components render actual HTML media elements (`<figure>`, `<figcaption>`, etc.)
 * providing proper semantics and accessibility for media content.
 */

// ============================================
// Figure Elements
// ============================================

/**
 * Renders an HTML `<figure>` element - self-contained content.
 *
 * Use for illustrations, diagrams, photos, code listings, etc. that are
 * referenced from the main content but could be moved without affecting the flow.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content (typically media and optional Figcaption)
 */
@Composable
fun Figure(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("figure", modifier, content)
}

/**
 * Renders an HTML `<figcaption>` element - a figure caption.
 *
 * Provides a caption or legend for a Figure element.
 * Should be the first or last child of a Figure.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render as the caption
 */
@Composable
fun Figcaption(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("figcaption", modifier, content)
}

// ============================================
// Embedded Content Elements
// ============================================

/**
 * Renders an HTML `<iframe>` element - an inline frame.
 *
 * Embeds another HTML page within the current page.
 *
 * @param src URL of the page to embed
 * @param title Accessible title for the iframe
 * @param width Width of the iframe
 * @param height Height of the iframe
 * @param sandbox Optional space-separated sandbox restrictions
 * @param allow Optional feature policy
 * @param loading Optional lazy loading: "lazy" or "eager"
 * @param referrerpolicy Optional referrer policy
 * @param modifier Styling and attributes to apply
 */
@Composable
fun Iframe(
    src: String,
    title: String,
    width: String? = null,
    height: String? = null,
    sandbox: String? = null,
    allow: String? = null,
    loading: String? = null,
    referrerpolicy: String? = null,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier
        .attribute("src", src)
        .attribute("title", title)
    if (width != null) finalModifier = finalModifier.attribute("width", width)
    if (height != null) finalModifier = finalModifier.attribute("height", height)
    if (sandbox != null) finalModifier = finalModifier.attribute("sandbox", sandbox)
    if (allow != null) finalModifier = finalModifier.attribute("allow", allow)
    if (loading != null) finalModifier = finalModifier.attribute("loading", loading)
    if (referrerpolicy != null) finalModifier = finalModifier.attribute("referrerpolicy", referrerpolicy)
    renderer.renderHtmlTag("iframe", finalModifier) {}
}

/**
 * Renders an HTML `<embed>` element - external content.
 *
 * Embeds external content using a plugin or external application.
 *
 * @param src URL of the resource to embed
 * @param type MIME type of the embedded content
 * @param width Width of the embedded content
 * @param height Height of the embedded content
 * @param modifier Styling and attributes to apply
 */
@Composable
fun Embed(
    src: String,
    type: String,
    width: String? = null,
    height: String? = null,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier
        .attribute("src", src)
        .attribute("type", type)
    if (width != null) finalModifier = finalModifier.attribute("width", width)
    if (height != null) finalModifier = finalModifier.attribute("height", height)
    renderer.renderHtmlTag("embed", finalModifier) {}
}

/**
 * Renders an HTML `<object>` element - external resource.
 *
 * Represents an external resource that can be treated as an image,
 * nested browsing context, or resource to be handled by a plugin.
 *
 * @param data URL of the resource
 * @param type MIME type of the resource
 * @param width Width of the object
 * @param height Height of the object
 * @param name Name for the embedded browsing context
 * @param form ID of a form element to associate with
 * @param modifier Styling and attributes to apply
 * @param content Fallback content to display if the object cannot be rendered
 */
@Composable
fun ObjectTag(
    data: String? = null,
    type: String? = null,
    width: String? = null,
    height: String? = null,
    name: String? = null,
    form: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit = {}
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier
    if (data != null) finalModifier = finalModifier.attribute("data", data)
    if (type != null) finalModifier = finalModifier.attribute("type", type)
    if (width != null) finalModifier = finalModifier.attribute("width", width)
    if (height != null) finalModifier = finalModifier.attribute("height", height)
    if (name != null) finalModifier = finalModifier.attribute("name", name)
    if (form != null) finalModifier = finalModifier.attribute("form", form)
    renderer.renderHtmlTag("object", finalModifier, content)
}

/**
 * Renders an HTML `<param>` element - object parameter.
 *
 * Defines parameters for an Object element.
 *
 * @param name Name of the parameter
 * @param value Value of the parameter
 * @param modifier Styling and attributes to apply
 */
@Composable
fun Param(
    name: String,
    value: String,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = modifier
        .attribute("name", name)
        .attribute("value", value)
    renderer.renderHtmlTag("param", finalModifier) {}
}

// ============================================
// Audio/Video Source Elements
// ============================================

/**
 * Renders an HTML `<source>` element - media source.
 *
 * Specifies media resources for Picture, Audio, or Video elements.
 *
 * @param src URL of the media resource
 * @param type MIME type of the resource
 * @param srcset For Picture: comma-separated list of image sources
 * @param sizes For Picture: comma-separated list of source sizes
 * @param media For Picture: media query for when to use this source
 * @param modifier Styling and attributes to apply
 */
@Composable
fun Source(
    src: String? = null,
    type: String? = null,
    srcset: String? = null,
    sizes: String? = null,
    media: String? = null,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier
    if (src != null) finalModifier = finalModifier.attribute("src", src)
    if (type != null) finalModifier = finalModifier.attribute("type", type)
    if (srcset != null) finalModifier = finalModifier.attribute("srcset", srcset)
    if (sizes != null) finalModifier = finalModifier.attribute("sizes", sizes)
    if (media != null) finalModifier = finalModifier.attribute("media", media)
    renderer.renderHtmlTag("source", finalModifier) {}
}

/**
 * Renders an HTML `<track>` element - text track.
 *
 * Specifies timed text tracks for Video or Audio elements (subtitles, captions).
 *
 * @param src URL of the track file
 * @param kind Kind of text track: "subtitles", "captions", "descriptions", "chapters", "metadata"
 * @param srclang Language of the track text data
 * @param label User-readable title of the track
 * @param default Whether this track should be enabled by default
 * @param modifier Styling and attributes to apply
 */
@Composable
fun Track(
    src: String,
    kind: String = "subtitles",
    srclang: String? = null,
    label: String? = null,
    default: Boolean = false,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier
        .attribute("src", src)
        .attribute("kind", kind)
    if (srclang != null) finalModifier = finalModifier.attribute("srclang", srclang)
    if (label != null) finalModifier = finalModifier.attribute("label", label)
    if (default) finalModifier = finalModifier.attribute("default", "")
    renderer.renderHtmlTag("track", finalModifier) {}
}

/**
 * Renders an HTML `<audio>` element - audio content.
 *
 * Embeds audio content with optional controls.
 *
 * @param src URL of the audio file (or use Source elements in content)
 * @param controls Whether to show audio controls
 * @param autoplay Whether to start playing automatically
 * @param loop Whether to loop the audio
 * @param muted Whether the audio is muted initially
 * @param preload Preload hint: "none", "metadata", "auto"
 * @param modifier Styling and attributes to apply
 * @param content Composable content (Source elements and fallback content)
 */
@Composable
fun Audio(
    src: String? = null,
    controls: Boolean = true,
    autoplay: Boolean = false,
    loop: Boolean = false,
    muted: Boolean = false,
    preload: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit = {}
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier
    if (src != null) finalModifier = finalModifier.attribute("src", src)
    if (controls) finalModifier = finalModifier.attribute("controls", "")
    if (autoplay) finalModifier = finalModifier.attribute("autoplay", "")
    if (loop) finalModifier = finalModifier.attribute("loop", "")
    if (muted) finalModifier = finalModifier.attribute("muted", "")
    if (preload != null) finalModifier = finalModifier.attribute("preload", preload)
    renderer.renderHtmlTag("audio", finalModifier, content)
}

// ============================================
// Progress and Meter Elements
// ============================================

/**
 * Renders an HTML `<meter>` element - a scalar measurement.
 *
 * Represents a scalar value within a known range, such as disk usage or voting results.
 *
 * @param value Current value (must be between min and max)
 * @param min Minimum value (default 0)
 * @param max Maximum value (default 1)
 * @param low Values below this are considered low
 * @param high Values above this are considered high
 * @param optimum The optimal value
 * @param modifier Styling and attributes to apply
 * @param content Fallback content to display
 */
@Composable
fun Meter(
    value: Double,
    min: Double = 0.0,
    max: Double = 1.0,
    low: Double? = null,
    high: Double? = null,
    optimum: Double? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit = {}
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier
        .attribute("value", value.toString())
        .attribute("min", min.toString())
        .attribute("max", max.toString())
    if (low != null) finalModifier = finalModifier.attribute("low", low.toString())
    if (high != null) finalModifier = finalModifier.attribute("high", high.toString())
    if (optimum != null) finalModifier = finalModifier.attribute("optimum", optimum.toString())
    renderer.renderHtmlTag("meter", finalModifier, content)
}

// Helper extension function
private fun Modifier.attribute(name: String, value: String): Modifier {
    return Modifier(
        styles = this.styles,
        attributes = this.attributes + (name to value),
        eventHandlers = this.eventHandlers
    )
}
