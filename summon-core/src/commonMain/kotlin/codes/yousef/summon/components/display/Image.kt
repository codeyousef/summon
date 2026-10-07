package codes.yousef.summon.components.display

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * A composable that displays an image with support for accessibility and performance attributes.
 *
 * @param src The URL of the image.
 * @param alt Alternative text for the image for accessibility and SEO purposes.
 * @param modifier The modifier to be applied to the image.
 * @param contentDescription Optional detailed description of the image content.
 * @param loading Specifies how the browser should load the image ("lazy", "eager", or "auto").
 * @param width Optional width of the image in pixels or CSS units. **Important for CLS prevention.**
 * @param height Optional height of the image in pixels or CSS units. **Important for CLS prevention.**
 * @param srcset Responsive image sources for different viewport sizes (e.g., "image-320.jpg 320w, image-640.jpg 640w").
 * @param sizes Media conditions describing image display size (e.g., "(max-width: 600px) 100vw, 50vw").
 * @param fetchPriority Hints the browser about the priority for fetching this image.
 * @param decoding Hints the browser about how to decode the image.
 * @param onLoad Optional callback that is invoked when the image is loaded.
 * @param onError Optional callback that is invoked when the image fails to load.
 */
@Composable
fun Image(
    src: String,
    alt: String,
    modifier: Modifier = Modifier(),
    contentDescription: String? = null,
    loading: ImageLoading = ImageLoading.LAZY,
    width: String? = null,
    height: String? = null,
    srcset: String? = null,
    sizes: String? = null,
    fetchPriority: FetchPriority? = null,
    decoding: ImageDecoding? = null,
    onLoad: (() -> Unit)? = null,
    onError: (() -> Unit)? = null
) {
    // Get the platform renderer
    val renderer = LocalPlatformRenderer.current

    // Build modifier with all image attributes
    var imageModifier = modifier

    // Add loading attribute
    imageModifier = imageModifier.attribute("loading", loading.value)

    // Add width and height if specified (important for CLS prevention)
    width?.let { imageModifier = imageModifier.attribute("width", it) }
    height?.let { imageModifier = imageModifier.attribute("height", it) }

    // Add aria-label for accessibility if contentDescription is provided
    contentDescription?.let { imageModifier = imageModifier.attribute("aria-label", it) }

    // Add responsive image attributes (srcset and sizes)
    srcset?.let { imageModifier = imageModifier.attribute("srcset", it) }
    sizes?.let { imageModifier = imageModifier.attribute("sizes", it) }

    // Add performance hints
    fetchPriority?.let { imageModifier = imageModifier.attribute("fetchpriority", it.value) }
    decoding?.let { imageModifier = imageModifier.attribute("decoding", it.value) }

    // Render the image
    renderer.renderImage(src, alt, imageModifier)

    // Note: The actual implementation would need to handle onLoad and onError callbacks
    // when the platform renderer supports them
}

/**
 * Browser image loading strategy.
 *
 * @property value HTML `loading` attribute value
 */
enum class ImageLoading(val value: String) {
    /** The lazy image loading option. */
    LAZY("lazy"),
    /** The eager image loading option. */
    EAGER("eager"),
    /** The auto image loading option. */
    AUTO("auto")
}

/**
 * Browser fetch-priority hint.
 *
 * @property value HTML `fetchpriority` attribute value
 */
enum class FetchPriority(val value: String) {
    /** The high fetch priority option. */
    HIGH("high"),
    /** The low fetch priority option. */
    LOW("low"),
    /** The auto fetch priority option. */
    AUTO("auto")
}

/**
 * Browser image-decoding hint.
 *
 * @property value HTML `decoding` attribute value
 */
enum class ImageDecoding(val value: String) {
    /** The sync image decoding option. */
    SYNC("sync"),
    /** The async image decoding option. */
    ASYNC("async"),
    /** The auto image decoding option. */
    AUTO("auto")
}

/**
 * Data class representing a source for the Picture element.
 *
 * @param srcset The srcset attribute for this source
 * @param type The MIME type (e.g., "image/webp", "image/avif")
 * @param media Optional media query for responsive sources
 * @param sizes Optional sizes attribute
 */
data class ImageSource(
    val srcset: String,
    val type: String? = null,
    val media: String? = null,
    val sizes: String? = null
) 