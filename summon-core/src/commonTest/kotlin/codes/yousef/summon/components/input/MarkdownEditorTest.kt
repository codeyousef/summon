package codes.yousef.summon.components.input

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MarkdownEditorTest {

    @Test
    fun rendersTextAreaWithProvidedValue() {
        val renderer = MockPlatformRenderer()

        runComposableTest(renderer) {
            MarkdownEditor(
                value = "Hello **world**",
                onValueChange = {},
                modifier = Modifier(),
                placeholder = "Start typing..."
            )
        }

        assertTrue(renderer.renderTextAreaCalled, "TextArea should be rendered")
        assertEquals("Hello **world**", renderer.lastTextAreaValueRendered, "TextArea should receive initial value")
    }

    @Test
    fun rendersPreviewWhenEnabled() {
        val renderer = MockPlatformRenderer()

        runComposableTest(renderer) {
            MarkdownEditor(
                value = "# Title",
                onValueChange = {},
                showPreview = true
            )
        }

        assertTrue(renderer.renderHtmlCalled, "Preview should render HTML when enabled")
    }

    @Test
    fun skipsPreviewWhenDisabled() {
        val renderer = MockPlatformRenderer()

        runComposableTest(renderer) {
            MarkdownEditor(
                value = "# Title",
                onValueChange = {},
                showPreview = false
            )
        }

        assertFalse(renderer.renderHtmlCalled, "Preview should not render when disabled")
    }
}
