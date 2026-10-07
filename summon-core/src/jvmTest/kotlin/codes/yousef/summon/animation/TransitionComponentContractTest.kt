package codes.yousef.summon.animation

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.core.style.Color
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class TransitionComponentContractTest {
    @Test
    fun convertersAndTransitionStatePreserveDomainValues() {
        assertEquals(1.25f, FloatConverter.convertToVector(1.25f))
        assertEquals(2.5f, FloatConverter.convertFromVector(2.5f))
        assertEquals(7f, IntConverter.convertToVector(7))
        assertEquals(7, IntConverter.convertFromVector(7.9f))
        val color = Color(0x11223344u)
        assertEquals(color.alphaFloat, ColorConverter.convertToVector(color))
        assertEquals(0.5f, ColorConverter.convertFromVector(0.5f).alphaFloat, 0.01f)

        val transition = Transition("first", TransitionSpec())
        assertEquals("first", transition.state.value)
        transition.updateState("second")
        assertEquals("second", transition.state.value)
    }

    @Test
    fun transitionWrappersRenderAndObserveMutableState() {
        val state = mutableStateOf("initial")
        lateinit var captured: Transition<String>
        val html = PlatformRenderer().renderComposableRoot {
            TransitionComponent(state) {
                captured = it
                Text(it.state.value)
            }
            transition(state) { Text("alias-${it.state.value}") }
            InfiniteTransitionEffect(running = false) { Text("paused") }
            InfiniteTransitionEffect(running = true) { Text("running") }
            infiniteTransition(running = false) { Text("alias-paused") }
        }
        assertContains(html, "initial")
        assertContains(html, "alias-initial")
        assertContains(html, "paused")
        assertContains(html, "running")
        state.value = "updated"
        assertEquals("updated", captured.state.value)
    }
}
