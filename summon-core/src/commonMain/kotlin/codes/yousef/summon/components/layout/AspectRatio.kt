package codes.yousef.summon.components.layout

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer


/**
 * # AspectRatio
 *
 * A layout component that maintains a specific width-to-height ratio for its content,
 * ensuring consistent proportions across different screen sizes and containers.
 *
 * ## Overview
 *
 * AspectRatio maintains proportional sizing by:
 * - **Ratio preservation** - Maintains exact width/height relationships
 * - **Responsive scaling** - Adapts size while preserving proportions
 * - **Content fitting** - Ensures content fits within the constrained area
 * - **Overflow control** - Handles content that exceeds aspect ratio bounds
 *
 * ## Basic Usage
 *
 * ### Image Aspect Ratio
 * ```kotlin
 * @Composable
 * fun ImageCard() {
 *     AspectRatio(
 *         ratio = 16f / 9f, // 16:9 aspect ratio
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .borderRadius("8px")
 *             .overflow(Overflow.HIDDEN)
 *     ) {
 *         Image(
 *             src = "image.jpg",
 *             alt = "Featured image",
 *             modifier = Modifier()
 *                 .width(Width.FULL)
 *                 .height(Height.FULL)
 *                 .objectFit(ObjectFit.COVER)
 *         )
 *     }
 * }
 * ```
 *
 * ### Video Container
 * ```kotlin
 * @Composable
 * fun VideoPlayer() {
 *     AspectRatio(
 *         ratio = 16f / 9f,
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .maxWidth("800px")
 *             .backgroundColor(Color.BLACK)
 *             .borderRadius("12px")
 *             .overflow(Overflow.HIDDEN)
 *     ) {
 *         VideoComponent()
 *     }
 * }
 * ```
 *
 * @param ratio The desired aspect ratio (width / height).
 * @param modifier Modifier to apply to the aspect ratio container.
 * @param content The composable content to display within the aspect ratio container.
 *
 * @see Box for flexible containers
 * @see Image for image display
 * @see Card for content containers
 *
 * @sample AspectRatioSamples.imageContainer
 * @sample AspectRatioSamples.videoPlayer
 * @sample AspectRatioSamples.squareContainer
 *
 * @since 1.0.0
 */
@Composable
fun AspectRatio(
    ratio: Float,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    val platformRenderer = LocalPlatformRenderer.current

    // Use the platform renderer to create the aspect ratio container
    platformRenderer.renderAspectRatio(
        ratio = ratio,
        modifier = modifier,
        content = {
            content()
        }
    )
} 
