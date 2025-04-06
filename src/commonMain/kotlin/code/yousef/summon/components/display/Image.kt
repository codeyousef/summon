package code.yousef.summon.components.display

import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable

/**
 * Enum representing different image loading strategies, corresponding to the HTML `loading` attribute.
 */
enum class ImageLoading(val value: String) {
    LAZY("lazy"),
    EAGER("eager"),
    AUTO("auto")
}

/**
 * A composable that displays an image from a given source URL.
 *
 * @param src The URL source of the image.
 * @param alt Alternative text for the image, crucial for accessibility.
 * @param modifier Optional [Modifier] for styling and layout.
 * @param contentDescription Optional longer description (can be used for accessibility, e.g., aria-describedby).
 * @param loading Specifies the browser loading strategy using [ImageLoading].
 * @param width Optional explicit width for the image (e.g., "100px").
 * @param height Optional explicit height for the image (e.g., "100px").
 */
@Composable
fun Image(
    src: String,
    alt: String,
    modifier: Modifier = Modifier(),
    contentDescription: String? = null,
    loading: ImageLoading = ImageLoading.LAZY,
    width: String? = null,
    height: String? = null
) {
    // 1. Create data holder
    val imageData = ImageData(
        src = src,
        alt = alt,
        modifier = modifier,
        contentDescription = contentDescription,
        loading = loading,
        width = width,
        height = height
    )

    // 2. Delegate rendering
    // Placeholder logic - needs composer/renderer integration.
    // The renderer (adapt renderImage) needs to:
    // - Create the <img> element.
    // - Set src, alt, loading, width, height attributes.
    // - Apply modifier styles.
    // - Handle contentDescription for accessibility (e.g., aria-describedby if needed).

    println("Composable Image function called for src: $src") // Placeholder

    // Example conceptual rendering call:
    // PlatformRendererProvider.getRenderer().renderImage(imageData)
}

/**
 * Internal data class holding parameters for the Image renderer.
 */
data class ImageData(
    val src: String,
    val alt: String,
    val modifier: Modifier,
    val contentDescription: String?,
    val loading: ImageLoading,
    val width: String?,
    val height: String?
) /* : MediaComponent */ // Removed inheritance

// Removed the old Image data class implementation 