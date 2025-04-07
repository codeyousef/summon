package code.yousef.summon.components.display

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.PlatformRendererProvider


/**
 * Enum representing different image loading strategies.
 */
enum class ImageLoading(val value: String) {
    LAZY("lazy"),
    EAGER("eager"),
    AUTO("auto")
}

/**
 * A composable that displays an image.
 *
 * @param src The URL or path of the image source.
 * @param alt Alternative text description for accessibility and SEO.
 * @param modifier Modifier applied to the image element. Use this to control size, shape, borders, etc.
 * @param contentDescription Optional longer description for accessibility (might map to aria-describedby).
 * @param loading Loading strategy ("lazy", "eager", "auto"). TODO: Handle via modifier/attribute.
 */
@Composable
fun Image(
    src: String,
    alt: String,
    modifier: Modifier = Modifier()
) {
    val renderer = PlatformRendererProvider.getPlatformRenderer()
    renderer.renderImage(src, alt, modifier)
} 
