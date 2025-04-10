package code.yousef.summon.components.display

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * A composable that displays an image with support for accessibility attributes.
 *
 * @param src The URL of the image.
 * @param alt Alternative text for the image for accessibility and SEO purposes.
 * @param modifier The modifier to be applied to the image.
 * @param contentDescription Optional detailed description of the image content.
 * @param loading Specifies how the browser should load the image ("lazy", "eager", or "auto").
 * @param width Optional width of the image in pixels or CSS units.
 * @param height Optional height of the image in pixels or CSS units.
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
    onLoad: (() -> Unit)? = null,
    onError: (() -> Unit)? = null
) {
    // Get the platform renderer
    val renderer = LocalPlatformRenderer.current
    
    // Create additional attributes for the image
    val attributes = mutableMapOf<String, String>()
    
    // Add loading attribute if specified
    attributes["loading"] = loading.value
    
    // Add width and height if specified
    width?.let { attributes["width"] = it }
    height?.let { attributes["height"] = it }
    
    // Add aria-label for accessibility if contentDescription is provided
    contentDescription?.let { attributes["aria-label"] = it }
    
    // Render the image
    renderer.renderImage(src, alt, modifier)
    
    // Note: The actual implementation would need to handle onLoad and onError callbacks
    // when the platform renderer supports them
}

/**
 * Enum representing different image loading strategies.
 */
enum class ImageLoading(val value: String) {
    LAZY("lazy"),
    EAGER("eager"),
    AUTO("auto")
} 