package codes.yousef.summon.animation

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class AnimationRenderingContractTest {
    private fun render(block: () -> Unit): String = PlatformRenderer().renderComposableRoot(block)

    @Test
    fun animatedContentMapsEveryTransitionAndDirectionToCss() {
        ContentTransitionType.entries.forEach { transition ->
            ContentDirection.entries.forEach { direction ->
                val html = render {
                    AnimatedContent(
                        targetState = mutableStateOf("value"),
                        transitionType = transition,
                        direction = direction,
                        duration = 125,
                        easing = Easing.LINEAR
                    ) { Text(it) }
                }
                assertContains(html, "125ms linear")
                assertContains(html, "value")
                when (transition) {
                    ContentTransitionType.FADE, ContentTransitionType.CROSSFADE -> assertContains(html, "opacity: 1")
                    ContentTransitionType.SCALE -> assertContains(html, "transform: scale(1)")
                    ContentTransitionType.SLIDE -> assertContains(html, if (direction.name.contains("LEFT") || direction.name.contains("RIGHT")) "translateX(0)" else "translateY(0)")
                }
            }
        }
        assertContains(render { animatedContent(mutableStateOf("simple")) { Text(it) } }, "simple")
        assertContains(render { crossfade(mutableStateOf("cross")) { Text(it) } }, "cross")
    }

    @Test
    fun animationUtilitiesRetainParametersAndRenderEveryItem() {
        val pulse = render { pulseAnimation(750, 0.8f, 1.2f, Modifier().attribute("data-id", "pulse")) { Text("body") } }
        assertContains(pulse, "data-pulse-min-scale=\"0.8\"")
        assertContains(pulse, "data-pulse-max-scale=\"1.2\"")
        assertContains(pulse, "general-pulse 750ms")

        val staggered = render { staggeredAnimation(listOf("a", "b"), 40, EnterTransition.SLIDE_IN) { Text(it) } }
        assertContains(staggered, "data-enter=\"slide_in\"")
        assertContains(staggered, "animation-delay: 0ms")
        assertContains(staggered, "animation-delay: 40ms")

        val entrance = render { animateIn(Modifier().attribute("data-id", "entrance")) { Text("entered") } }
        assertContains(entrance, "summon-fade-in 300ms")
        assertContains(render { pulseAnimation(250.milliseconds) { Text("duration pulse") } }, "general-pulse 250ms")
        assertContains(render { staggeredAnimation(75.milliseconds) { Text("duration stagger") } }, "data-stagger-delay=\"75\"")
    }

    @Test
    fun animatedTextMapsEveryEntryTransition() {
        EnterTransition.entries.forEach { transition ->
            val component = animateInText("text", enterTransition = transition, duration = 640.milliseconds)
            assertEquals("0.0", component.modifier.attributes["data-animation-initial-alpha"])
            assertContains(component.modifier.attributes.getValue("style"), "opacity 640ms ease-out")
            val initialOffset = component.modifier.attributes["data-animation-initial-offset"]
            if (transition == EnterTransition.SLIDE_IN) assertEquals("0.0,50.0", initialOffset)
            else assertEquals("0.0,0.0", initialOffset)
        }
    }

    @Test
    fun animatedVisibilityRendersOnlyVisibleContentAndCarriesTransitionHints() {
        val shown = render {
            AnimatedVisibility(
                visible = true,
                enter = EnterTransition.ZOOM_IN,
                exit = ExitTransition.SHRINK_OUT
            ) { Text("visible") }
            AnimatedVisibility(mutableStateOf(true)) { Text("state-visible") }
        }
        assertContains(shown, "visible")
        assertContains(shown, "state-visible")
        assertContains(shown, "data-enter=\"zoom_in\"")
        assertContains(shown, "data-exit=\"shrink_out\"")
        assertContains(shown, "data-animation-state=\"entering\"")

        val hidden = render { AnimatedVisibility(false) { Text("hidden") } }
        kotlin.test.assertFalse(hidden.contains("hidden"))
    }
}
