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
 * Enum representing different image loading strategies.
 *
 * - `LAZY`: Defer loading until the image is near the viewport (best for below-the-fold images)
 * - `EAGER`: Load immediately (best for above-the-fold LCP images)
 * - `AUTO`: Let the browser decide
 */
enum class ImageLoading(val value: String) {
    LAZY("lazy"),
    EAGER("eager"),
    AUTO("auto")
}

/**
 * Enum representing fetch priority hints for images.
 *
 * Helps the browser prioritize critical images (like LCP candidates) over less important ones.
 *
 * - `HIGH`: Fetch at high priority (use for LCP images, hero images)
 * - `LOW`: Fetch at low priority (use for below-fold images, thumbnails)
 * - `AUTO`: Let the browser decide based on heuristics
 */
enum class FetchPriority(val value: String) {
    HIGH("high"),
    LOW("low"),
    AUTO("auto")
}

/**
 * Enum representing image decoding hints.
 *
 * - `SYNC`: Decode synchronously (may block rendering, use sparingly)
 * - `ASYNC`: Decode asynchronously (non-blocking, best for most images)
 * - `AUTO`: Let the browser decide
 */
enum class ImageDecoding(val value: String) {
    SYNC("sync"),
    ASYNC("async"),
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