package codes.yousef.summon.components.display

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * A composable that displays a picture element with multiple image sources for modern format support.
 *
 * The Picture element allows you to provide multiple image formats (WebP, AVIF, etc.) and
 * let the browser choose the best supported format, improving performance on modern browsers
 * while maintaining compatibility with older ones.
 *
 * Example usage:
 * ```kotlin
 * Picture(
 *     sources = listOf(
 *         ImageSource(srcset = "image.avif", type = "image/avif"),
 *         ImageSource(srcset = "image.webp", type = "image/webp"),
 *     ),
 *     fallbackSrc = "image.jpg",
 *     alt = "A beautiful landscape",
 *     width = "800",
 *     height = "600"
 * )
 * ```
 *
 * @param sources List of image sources in order of preference (browser picks first supported)
 * @param fallbackSrc The fallback image URL for browsers that don't support any source
 * @param alt Alternative text for accessibility
 * @param modifier The modifier to be applied to the picture container
 * @param width Optional width of the image (important for CLS prevention)
 * @param height Optional height of the image (important for CLS prevention)
 * @param loading Specifies how the browser should load the image
 * @param fetchPriority Hints the browser about the priority for fetching this image
 * @param decoding Hints the browser about how to decode the image
 */
@Composable
fun Picture(
    sources: List<ImageSource>,
    fallbackSrc: String,
    alt: String,
    modifier: Modifier = Modifier(),
    width: String? = null,
    height: String? = null,
    loading: ImageLoading = ImageLoading.LAZY,
    fetchPriority: FetchPriority? = null,
    decoding: ImageDecoding? = null
) {
    val renderer = LocalPlatformRenderer.current

    // Build modifier for picture container
    var pictureModifier = modifier

    // Render the picture element with sources
    renderer.renderHtmlTag("picture", pictureModifier) {
        // Render each source element
        sources.forEach { source ->
            var sourceModifier = Modifier()
                .attribute("srcset", source.srcset)

            source.type?.let { sourceModifier = sourceModifier.attribute("type", it) }
            source.media?.let { sourceModifier = sourceModifier.attribute("media", it) }
            source.sizes?.let { sourceModifier = sourceModifier.attribute("sizes", it) }

            renderer.renderHtmlTag("source", sourceModifier) {}
        }

        // Render the fallback img element
        var imgModifier = Modifier()
            .attribute("src", fallbackSrc)
            .attribute("alt", alt)
            .attribute("loading", loading.value)

        width?.let { imgModifier = imgModifier.attribute("width", it) }
        height?.let { imgModifier = imgModifier.attribute("height", it) }
        fetchPriority?.let { imgModifier = imgModifier.attribute("fetchpriority", it.value) }
        decoding?.let { imgModifier = imgModifier.attribute("decoding", it.value) }

        renderer.renderHtmlTag("img", imgModifier) {}
    }
}

/**
 * A convenience composable for responsive images with modern format support.
 *
 * This is a simplified version of Picture that automatically creates sources
 * for WebP and AVIF formats based on the original image URL.
 *
 * Example:
 * ```kotlin
 * ResponsiveImage(
 *     baseSrc = "/images/hero",
 *     extension = "jpg",
 *     alt = "Hero image",
 *     widths = listOf(320, 640, 1280),
 *     sizes = "(max-width: 640px) 100vw, 50vw"
 * )
 * ```
 *
 * @param baseSrc Base path without extension (e.g., "/images/hero")
 * @param extension Original file extension (e.g., "jpg", "png")
 * @param alt Alternative text for accessibility
 * @param widths List of image widths for srcset generation
 * @param sizes The sizes attribute for responsive loading
 * @param modifier The modifier to be applied to the picture container
 * @param width Display width (CSS units or pixels)
 * @param height Display height (CSS units or pixels)
 * @param loading Specifies how the browser should load the image
 * @param fetchPriority Hints the browser about the priority for fetching this image
 * @param includeAvif Whether to include AVIF source (requires AVIF images on server)
 * @param includeWebp Whether to include WebP source (requires WebP images on server)
 */
@Composable
fun ResponsiveImage(
    baseSrc: String,
    extension: String,
    alt: String,
    widths: List<Int> = listOf(320, 640, 1024, 1920),
    sizes: String = "100vw",
    modifier: Modifier = Modifier(),
    width: String? = null,
    height: String? = null,
    loading: ImageLoading = ImageLoading.LAZY,
    fetchPriority: FetchPriority? = null,
    includeAvif: Boolean = true,
    includeWebp: Boolean = true
) {
    // Generate srcset strings for each format
    fun generateSrcset(ext: String): String =
        widths.joinToString(", ") { w -> "$baseSrc-$w.$ext ${w}w" }

    val sources = buildList {
        if (includeAvif) {
            add(ImageSource(
                srcset = generateSrcset("avif"),
                type = "image/avif",
                sizes = sizes
            ))
        }
        if (includeWebp) {
            add(ImageSource(
                srcset = generateSrcset("webp"),
                type = "image/webp",
                sizes = sizes
            ))
        }
        // Original format as last source
        add(ImageSource(
            srcset = generateSrcset(extension),
            type = when (extension.lowercase()) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                "gif" -> "image/gif"
                else -> null
            },
            sizes = sizes
        ))
    }

    // Fallback to the largest size in original format
    val fallbackSrc = "$baseSrc-${widths.maxOrNull() ?: 1024}.$extension"

    Picture(
        sources = sources,
        fallbackSrc = fallbackSrc,
        alt = alt,
        modifier = modifier,
        width = width,
        height = height,
        loading = loading,
        fetchPriority = fetchPriority
    )
}
