package com.summon

import kotlinx.html.TagConsumer

/**
 * A composable that displays an image with support for accessibility attributes.
 *
 * @param src The URL of the image.
 * @param alt Alternative text for the image for accessibility and SEO purposes.
 * @param modifier The modifier to be applied to the image.
 * @param contentDescription Optional detailed description of the image content.
 * @param loading Specifies how the browser should load the image ("lazy", "eager", or "auto").
 * @param width Optional width of the image.
 * @param height Optional height of the image.
 */
data class Image(
    val src: String,
    val alt: String,
    val modifier: Modifier = Modifier(),
    val contentDescription: String? = null,
    val loading: ImageLoading = ImageLoading.LAZY,
    val width: String? = null,
    val height: String? = null
) : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderImage(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
}

/**
 * Enum representing different image loading strategies.
 */
enum class ImageLoading(val value: String) {
    LAZY("lazy"),
    EAGER("eager"),
    AUTO("auto")
} 