package code.yousef.summon.components.input

// Import other types used in PlatformRenderer methods for the mock
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import code.yousef.summon.util.runTestComposable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.*
import code.yousef.summon.runtime.SelectOption as RendererSelectOption

class TextAreaTest {

    @Test
    fun testBasicTextAreaRendering() {
        val renderer = MockPlatformRenderer()
        var textValue = "Initial Text"
        val onValChange: (String) -> Unit = { textValue = it }
        val mod = Modifier()

        runTestComposable(renderer) {
            TextArea(
                value = textValue,
                onValueChange = onValChange,
                modifier = mod,
                enabled = false,
                readOnly = true,
                rows = 5,
                maxLength = 100,
                placeholder = "Enter text..."
            )
        }

        assertTrue(renderer.renderTextAreaCalled)
        assertEquals("Initial Text", renderer.lastTextAreaValueRendered)
        assertSame(onValChange, renderer.lastTextAreaOnValueChangeRendered)
        assertEquals(false, renderer.lastTextAreaEnabledRendered)
        assertEquals(true, renderer.lastTextAreaReadOnlyRendered)
        assertEquals(5, renderer.lastTextAreaRowsRendered)
        assertEquals(100, renderer.lastTextAreaMaxLengthRendered)
        assertEquals("Enter text...", renderer.lastTextAreaPlaceholderRendered)
        assertSame(mod, renderer.lastTextAreaModifierRendered)

        // Simulate value change from renderer
        renderer.lastTextAreaOnValueChangeRendered?.invoke("New Text")
        assertEquals("New Text", textValue)
    }

    @Test
    fun testStatefulTextArea() {
        val renderer = MockPlatformRenderer()
        var externalValue = "Initial"
        val onValChangeExternal: (String) -> Unit = { externalValue = it }

        runTestComposable(renderer) {
            StatefulTextArea(
                initialValue = "Start",
                onValueChange = onValChangeExternal,
                placeholder = "Stateful"
            )
        }

        assertTrue(renderer.renderTextAreaCalled)
        assertEquals("Start", renderer.lastTextAreaValueRendered)
        assertEquals("Stateful", renderer.lastTextAreaPlaceholderRendered)
        assertNotNull(renderer.lastTextAreaOnValueChangeRendered)

        // Simulate internal state update via renderer callback
        renderer.lastTextAreaOnValueChangeRendered?.invoke("Updated")

        // Check external callback was triggered
        assertEquals("Updated", externalValue)

        // Note: We cannot directly check the internal textState.value easily here,
        // but we verified the external callback received the update.
        // A subsequent recomposition would show the renderer receiving "Updated".
    }
}