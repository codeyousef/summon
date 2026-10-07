package codes.yousef.summon.components.input

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.CallbackRegistry
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RadioButtonRenderingContractTest {
    @AfterTest
    fun cleanUp() = CallbackRegistry.clear()

    private fun callbackIds(html: String): List<String> =
        Regex("data-onclick-id=\"([^\"]+)\"").findAll(html).map { it.groupValues[1] }.toList()

    @Test
    fun labelsRenderOnEitherSideAndBothEnabledTargetsInvokeTheAction() {
        var calls = 0
        val start = PlatformRenderer().renderComposableRoot {
            RadioButton(true, { calls++ }, label = "Start label", labelPosition = LabelPosition.START)
        }
        assertContains(start, "Start label")
        assertTrue(start.indexOf("Start label") < start.indexOf("type=\"radio\""))
        assertTrue(callbackIds(start).isNotEmpty())
        assertEquals(0, calls)

        val end = PlatformRenderer().renderComposableRoot {
            RadioButton(false, {}, label = "End label", labelPosition = LabelPosition.END)
        }
        assertTrue(end.indexOf("type=\"radio\"") < end.indexOf("End label"))
    }

    @Test
    fun disabledAndAbsentLabelsDoNotInvokeActionsOrRenderLabelSpacing() {
        var calls = 0
        val disabled = PlatformRenderer().renderComposableRoot {
            RadioButton(false, { calls++ }, enabled = false, label = null)
            RadioButtonWithLabel(false, { calls++ }, enabled = false) { Text("Disabled label") }
        }
        assertTrue(callbackIds(disabled).isNotEmpty())
        assertEquals(0, calls)
        assertFalse(disabled.contains("Start label") || disabled.contains("End label"))
    }
}
