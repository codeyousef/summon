package codes.yousef.summon.modifier

/**
 * Media-related modifier extensions for controlling video/audio behavior.
 *
 * ## Features
 *
 * - **pauseOnScroll**: Pauses media when element scrolls out of view
 * - **lazyLoad**: Loads media only when near viewport
 *
 * @since 1.0.0
 */

/**
 * Marks an element for pause-on-scroll behavior.
 *
 * When the element containing a `<video>` or `<audio>` child scrolls out of
 * the viewport, the media will be paused. When it scrolls back into view,
 * playback will resume if it was playing before.
 *
 * This modifier works by adding a data attribute that JavaScript observes
 * using IntersectionObserver.
 *
 * ## Usage
 *
 * ```kotlin
 * Box(
 *     modifier = Modifier().pauseOnScroll()
 * ) {
 *     Video(
 *         src = "hero-video.mp4",
 *         autoplay = true,
 *         muted = true,
 *         loop = true
 *     )
 * }
 * ```
 *
 * ## JavaScript Integration
 *
 * The framework automatically initializes IntersectionObserver for elements
 * with `data-pause-on-scroll="true"`. Custom implementation can be added via:
 *
 * ```javascript
 * document.querySelectorAll('[data-pause-on-scroll="true"]').forEach(el => {
 *     const observer = new IntersectionObserver(entries => {
 *         entries.forEach(entry => {
 *             const video = el.querySelector('video');
 *             if (video) {
 *                 if (entry.isIntersecting) {
 *                     if (video.dataset.wasPlaying === 'true') {
 *                         video.play();
 *                     }
 *                 } else {
 *                     video.dataset.wasPlaying = !video.paused;
 *                     video.pause();
 *                 }
 *             }
 *         });
 *     });
 *     observer.observe(el);
 * });
 * ```
 *
 * @param threshold Visibility threshold (0.0-1.0) at which to trigger (default 0.1)
 * @return Modifier with pause-on-scroll data attribute
 */
fun Modifier.pauseOnScroll(threshold: Float = 0.1f): Modifier {
    return this
        .dataAttribute("pause-on-scroll", "true")
        .dataAttribute("pause-on-scroll-threshold", threshold.toString())
}

/**
 * Marks an element for lazy loading when near the viewport.
 *
 * Elements with this modifier will defer loading until they are
 * within a certain distance of the viewport.
 *
 * @param rootMargin Distance from viewport to trigger loading (e.g., "200px")
 * @return Modifier with lazy-load data attribute
 */
fun Modifier.lazyLoad(rootMargin: String = "200px"): Modifier {
    return this
        .dataAttribute("lazy-load", "true")
        .dataAttribute("lazy-load-margin", rootMargin)
}

/**
 * Enables native lazy loading for images and iframes.
 *
 * Uses the browser's native `loading="lazy"` attribute.
 *
 * @return Modifier with loading attribute set to lazy
 */
fun Modifier.nativeLazyLoad(): Modifier {
    return this.attribute("loading", "lazy")
}

/**
 * Sets the aspect ratio for responsive media containers.
 *
 * Uses CSS aspect-ratio property for modern browsers.
 *
 * @param width Aspect width (e.g., 16)
 * @param height Aspect height (e.g., 9)
 * @return Modifier with aspect-ratio style
 */
fun Modifier.aspectRatio(width: Int, height: Int): Modifier {
    return this.style("aspect-ratio", "$width / $height")
}

/**
 * Makes media responsive while maintaining aspect ratio.
 *
 * Commonly used for videos to make them fill their container width
 * while maintaining the correct height ratio.
 *
 * @return Modifier with responsive styles
 */
fun Modifier.responsiveMedia(): Modifier {
    return this
        .style("width", "100%")
        .style("height", "auto")
        .style("max-width", "100%")
}
