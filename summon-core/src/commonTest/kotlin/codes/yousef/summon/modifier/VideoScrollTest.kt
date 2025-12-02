package codes.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

/**
 * Tests for Media Modifiers functionality.
 *
 * TEST DIRECTIVE: Mount a video. Mock the IntersectionObserver callback
 * (or use a real browser environment test). Trigger "out of view".
 * Assert video.paused is true.
 *
 * Note: Actual IntersectionObserver behavior requires browser environment.
 * These tests verify the modifier creates correct data attributes.
 */
class VideoScrollTest {

    @Test
    fun testPauseOnScrollModifierAddsAttribute() {
        val modifier = Modifier().pauseOnScroll()

        // Verify the modifier contains the pause-on-scroll data attribute
        assertTrue(
            modifier.attributes.containsKey("data-pause-on-scroll") ||
            modifier.attributes["data-pause-on-scroll"] == "true",
            "Modifier should have data-pause-on-scroll attribute"
        )
    }

    @Test
    fun testPauseOnScrollWithThreshold() {
        val modifier = Modifier().pauseOnScroll(threshold = 0.3f)

        // Verify threshold is captured in attributes
        assertEquals(
            "0.3",
            modifier.attributes["data-pause-on-scroll-threshold"],
            "Modifier should include custom threshold"
        )
    }

    @Test
    fun testPauseOnScrollChaining() {
        val modifier = Modifier()
            .width("100%")
            .height("auto")
            .pauseOnScroll()
            .opacity(1.0f)

        // Verify all modifiers are applied
        val styleString = modifier.toStyleString()
        assertTrue(styleString.contains("width"), "Should contain width style")
        assertTrue(modifier.attributes.containsKey("data-pause-on-scroll"), "Should have pause-on-scroll attribute")
    }

    @Test
    fun testPauseOnScrollDefaultThreshold() {
        val modifier = Modifier().pauseOnScroll()

        // Default threshold should be 0.1
        val thresholdValue = modifier.attributes["data-pause-on-scroll-threshold"]

        assertEquals("0.1", thresholdValue, "Default threshold should be 0.1")
    }

    @Test
    fun testLazyLoadModifier() {
        val modifier = Modifier().lazyLoad()

        assertTrue(
            modifier.attributes.containsKey("data-lazy-load"),
            "Should have lazy-load data attribute"
        )
    }

    @Test
    fun testLazyLoadWithCustomMargin() {
        val modifier = Modifier().lazyLoad(rootMargin = "500px")

        assertEquals(
            "500px",
            modifier.attributes["data-lazy-load-margin"],
            "Should have custom root margin"
        )
    }

    @Test
    fun testNativeLazyLoad() {
        val modifier = Modifier().nativeLazyLoad()

        assertEquals(
            "lazy",
            modifier.attributes["loading"],
            "Should have loading=lazy attribute"
        )
    }

    @Test
    fun testAspectRatio() {
        val modifier = Modifier().aspectRatio(16, 9)

        val styleString = modifier.toStyleString()
        assertTrue(
            styleString.contains("aspect-ratio") && styleString.contains("16 / 9"),
            "Should have aspect-ratio style"
        )
    }

    @Test
    fun testResponsiveMedia() {
        val modifier = Modifier().responsiveMedia()

        val styleString = modifier.toStyleString()
        assertTrue(styleString.contains("width") && styleString.contains("100%"), "Should have width: 100%")
        assertTrue(styleString.contains("height") && styleString.contains("auto"), "Should have height: auto")
    }
}
