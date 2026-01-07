package codes.yousef.summon.animation

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.TransitionTimingFunction
import codes.yousef.summon.modifier.transition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AnimationModifiersTest {

    @Test
    fun testBasicAnimate() {
        val modifier = Modifier().animate(
            name = "testAnim",
            duration = 500,
            timingFunction = "ease-in-out",
            delay = 100,
            iterationCount = "2",
            direction = "alternate",
            fillMode = "both"
        )

        val animation = modifier.styles["animation"]
        assertEquals("testAnim 500ms ease-in-out 100ms 2 alternate both", animation)
    }

    @Test
    fun testSpringAnimation() {
        val modifier = Modifier().spring(
            name = "springAnim",
            stiffness = 200f,
            damping = 20f,
            duration = 600,
            iterationCount = "3"
        )

        val animation = modifier.styles["animation"]
        assertEquals(animation?.startsWith("springAnim 600ms"), true)
        assertEquals(animation?.contains("3"), true)
    }

    @Test
    fun testAnimateWithEasing() {
        val modifier = Modifier().animateWithEasing(
            name = "easeAnim",
            easing = Easing.EASE_OUT,
            duration = 400,
            delay = 50,
            iterationCount = "2",
            direction = "reverse",
            fillMode = "forwards"
        )

        val animation = modifier.styles["animation"]
        assertEquals("easeAnim 400ms cubic-bezier(0, 0, 0.2, 1) 50ms 2 reverse forwards", animation)
    }

    @Test
    fun testFadeInAnimation() {
        val modifier = Modifier().fadeIn(
            duration = 300,
            delay = 100,
            easing = Easing.EASE_IN
        )

        val animation = modifier.styles["animation"]
        assertEquals("fade-in 300ms cubic-bezier(0.4, 0, 1, 1) 100ms 1 normal forwards", animation)
    }

    @Test
    fun testFadeOutAnimation() {
        val modifier = Modifier().fadeOut(
            duration = 250,
            delay = 50,
            easing = Easing.EASE_OUT
        )

        val animation = modifier.styles["animation"]
        assertEquals("fade-out 250ms cubic-bezier(0, 0, 0.2, 1) 50ms 1 normal forwards", animation)
    }

    @Test
    fun testSlideInFromTopAnimation() {
        val modifier = Modifier().slideInFromTop(
            duration = 450,
            delay = 75,
            easing = Easing.CUBIC_OUT
        )

        val animation = modifier.styles["animation"]
        assertEquals("slide-in-top 450ms cubic-bezier(0.33, 1, 0.68, 1) 75ms 1 normal forwards", animation)
    }

    @Test
    fun testZoomInAnimation() {
        val modifier = Modifier().zoomIn(
            duration = 350,
            delay = 25,
            easing = Easing.CUBIC_IN
        )

        val animation = modifier.styles["animation"]
        assertEquals("zoom-in 350ms cubic-bezier(0.32, 0, 0.67, 0) 25ms 1 normal forwards", animation)
    }

    @Test
    fun testPulseAnimation() {
        val modifier = Modifier().pulse(
            duration = 1200,
            easing = Easing.SINE_OUT
        )

        val animation = modifier.styles["animation"]
        assertEquals("pulse 1200ms cubic-bezier(0.61, 1, 0.88, 1) 0ms infinite normal forwards", animation)
    }

    @Test
    fun testTransition() {
        val modifier = Modifier().transition(
            property = "opacity",
            duration = 400,
            timingFunction = TransitionTimingFunction.EaseInOut,
            delay = 100
        )

        val transition = modifier.styles["transition"]
        assertNotNull(transition)
        assertTrue(transition.contains("opacity"))
        assertTrue(transition.contains("400ms"))
    }

    @Test
    fun testAnimOpacity() {
        val modifier = Modifier().animOpacity(0.5f)

        val opacity = modifier.styles["opacity"]
        assertEquals("0.5", opacity)
    }

    @Test
    fun testSlideIn() {
        val modifier = Modifier().slideIn(
            direction = SlideDirection.LEFT,
            distance = "50px",
            duration = 300,
            delay = 0
        )

        // The slideIn function calls transition twice, which overwrites the previous value
        // So we should only see the last transition set (for opacity)
        val transition = modifier.styles["transition"]
        assertNotNull(transition, "Transition should be set")
        assertEquals("opacity 300ms ease-out 0ms", transition)

        // Check opacity is set to 1
        assertEquals("1.0", modifier.styles["opacity"])
    }

    @Test
    fun testSlideOut() {
        val modifier = Modifier().slideOut(
            direction = SlideDirection.RIGHT,
            distance = "75%",
            duration = 250,
            delay = 50
        )

        // The slideOut function calls transition twice, which overwrites the previous value
        // So we should only see the last transition set (for opacity)
        val transition = modifier.styles["transition"]
        assertNotNull(transition, "Transition should be set")
        assertEquals("opacity 250ms ease-in 50ms", transition)

        // Check opacity is set to 0
        assertEquals("0.0", modifier.styles["opacity"])

        // Check transform is set correctly
        val transform = modifier.styles["transform"]
        assertNotNull(transform, "Transform should be set")
        assertEquals("translateX(75%)", transform)
    }

    @Test
    fun testScale() {
        val modifier = Modifier().scale(
            scaleX = 1.5f,
            scaleY = 0.8f,
            duration = 350,
            delay = 25
        )

        // Check that transition is set correctly
        val transition = modifier.styles["transition"]
        assertTrue(transition?.contains("transform") == true)

        // Check transform is set correctly
        val transform = modifier.styles["transform"]
        assertEquals("scale(1.5, 0.8)", transform)
    }

    @Test
    fun testAnimStyle() {
        val modifier = Modifier().animStyle("background-color", "red")

        val backgroundColor = modifier.styles["background-color"]
        assertEquals("red", backgroundColor)
    }
}
