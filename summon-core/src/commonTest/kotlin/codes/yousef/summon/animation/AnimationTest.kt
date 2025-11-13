package codes.yousef.summon.animation

import kotlin.math.abs
import kotlin.test.*

class AnimationTest {

    private val delta = 0.001f // Tolerance for float comparisons

    // --- SpringAnimation Tests ---

    @Test
    fun testSpringAnimationDefaults() {
        val anim = SpringAnimation()
        assertEquals(300, anim.durationMs)
        assertFalse(anim.repeating)
        assertEquals(150f, anim.stiffness, delta)
        assertEquals(12f, anim.damping, delta)
    }

    @Test
    fun testSpringAnimationCustomValues() {
        val anim = SpringAnimation(stiffness = 100f, damping = 10f, durationMs = 500, repeating = true)
        assertEquals(500, anim.durationMs)
        assertTrue(anim.repeating)
        assertEquals(100f, anim.stiffness, delta)
        assertEquals(10f, anim.damping, delta)
    }

    @Test
    fun testSpringAnimationGetValue() {
        val anim = SpringAnimation()
        assertEquals(0f, anim.getValue(0.0f), delta) // Start value
        assertTrue(anim.getValue(0.5f) > 0f && anim.getValue(0.5f) < 1f) // Intermediate value
        assertEquals(1f, anim.getValue(1.0f), delta) // End value (approximation might not be exact 1)
    }

    @Test
    fun testSpringAnimationToCssAnimationString() {
        val anim = SpringAnimation(durationMs = 450)
        assertEquals("450ms cubic-bezier(0.5, 0.0, 0.2, 1.0)", anim.toCssAnimationString())
    }

    @Test
    fun testSpringAnimationToCssKeyframes() {
        val anim = SpringAnimation()
        val keyframesName = "testSpring"
        val css = anim.toCssKeyframes(keyframesName)

        val lines = css.lines()

        assertEquals(0.0f, anim.getValue(0.0f), delta, "getValue(0.0f) should be 0.0f")

        assertTrue(
            lines.firstOrNull()?.startsWith("@keyframes $keyframesName {") ?: false,
            "CSS should start with @keyframes declaration"
        )

        // Isolate the 0% line check - allow scale(0) or scale(0.0)
        val line0Percent = lines.find { it.trim().startsWith("0%") }
        assertNotNull(line0Percent, "Could not find the 0% keyframe line.")
        assertTrue(
            line0Percent.contains("scale(0)") || line0Percent.contains("scale(0.0)"),
            "The 0% keyframe line ('$line0Percent') must contain 'scale(0)' or 'scale(0.0)'"
        )

        // Check 100% line - allow scale(1) or scale(1.0)
        assertTrue(
            lines.any {
                val trimmed = it.trim()
                trimmed.startsWith("100%") && (trimmed.contains("scale(1)") || trimmed.contains("scale(1.0)"))
            },
            "CSS should contain 100% keyframe with scale(1) or scale(1.0)"
        )

        // Check for an intermediate step to confirm generation logic
        assertTrue(
            lines.any { it.trim().startsWith("50%") },
            "CSS should contain intermediate keyframes (e.g., 50%)"
        )

        assertTrue(
            lines.lastOrNull() == "}",
            "CSS should end with closing brace"
        )
    }

    // --- TweenAnimation Tests ---

    @Test
    fun testTweenAnimationDefaults() {
        val anim = TweenAnimation()
        assertEquals(300, anim.durationMs)
        assertFalse(anim.repeating)
        assertEquals(Easing.LINEAR, anim.easing)
    }

    @Test
    fun testTweenAnimationCustomValues() {
        val anim = TweenAnimation(durationMs = 600, easing = Easing.EASE_IN_OUT, repeating = true)
        assertEquals(600, anim.durationMs)
        assertTrue(anim.repeating)
        assertEquals(Easing.EASE_IN_OUT, anim.easing)
    }

    @Test
    fun testTweenAnimationGetValue() {
        val linearAnim = TweenAnimation(easing = Easing.LINEAR)
        assertEquals(0.0f, linearAnim.getValue(0.0f), delta)
        assertEquals(0.5f, linearAnim.getValue(0.5f), delta)
        assertEquals(1.0f, linearAnim.getValue(1.0f), delta)

        val easeInAnim = TweenAnimation(easing = Easing.EASE_IN)
        assertEquals(0.0f, easeInAnim.getValue(0.0f), delta)
        assertTrue(easeInAnim.getValue(0.5f) < 0.5f) // Starts slow
        assertEquals(1.0f, easeInAnim.getValue(1.0f), delta)

        val easeOutAnim = TweenAnimation(easing = Easing.EASE_OUT)
        assertEquals(0.0f, easeOutAnim.getValue(0.0f), delta)
        assertTrue(easeOutAnim.getValue(0.5f) > 0.5f) // Ends slow (fast start)
        assertEquals(1.0f, easeOutAnim.getValue(1.0f), delta)
    }

    @Test
    fun testTweenAnimationToCssAnimationString() {
        val linearAnim = TweenAnimation(durationMs = 250, easing = Easing.LINEAR)
        assertEquals("250ms linear", linearAnim.toCssAnimationString())

        val easeInOutAnim = TweenAnimation(durationMs = 500, easing = Easing.EASE_IN_OUT)
        assertEquals("500ms cubic-bezier(0.4, 0, 0.2, 1)", easeInOutAnim.toCssAnimationString())
    }

    @Test
    fun testTweenAnimationToCssKeyframes() {
        val anim = TweenAnimation()
        val keyframesName = "testTween"
        val expectedCss = """
            @keyframes $keyframesName {
              0% { transform: scale(0); opacity: 0; }
              100% { transform: scale(1); opacity: 1; }
            }
        """.trimIndent()
        assertEquals(expectedCss, anim.toCssKeyframes(keyframesName))
    }

    // --- Easing Enum Tests ---

    @Test
    fun testEasingGetValueBoundaries() {
        Easing.entries.forEach { easing ->
            assertEquals(0.0f, easing.getValue(0.0f), delta, "Easing.${easing.name} failed at 0.0")
            // Allow small tolerance around 1.0 due to float precision in complex functions
            assertTrue(
                abs(1.0f - easing.getValue(1.0f)) < delta,
                "Easing.${easing.name} failed at 1.0 (value: ${easing.getValue(1.0f)})"
            )
        }
    }

    @Test
    fun testEasingToCssString() {
        assertEquals("linear", Easing.LINEAR.toCssString())
        assertEquals("cubic-bezier(0.4, 0, 1, 1)", Easing.EASE_IN.toCssString())
        assertEquals("cubic-bezier(0, 0, 0.2, 1)", Easing.EASE_OUT.toCssString())
        assertEquals("cubic-bezier(0.4, 0, 0.2, 1)", Easing.EASE_IN_OUT.toCssString())
        assertEquals("cubic-bezier(0.12, 0, 0.39, 0)", Easing.SINE_IN.toCssString())
        // Add more checks if specific bezier curves are critical
    }
} 