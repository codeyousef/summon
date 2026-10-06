package codes.yousef.summon.components.input

import codes.yousef.summon.modifier.*
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
    fun previewNeverUsesTheTrustedHtmlSink() {
        val renderer = MockPlatformRenderer()

        runComposableTest(renderer) {
            MarkdownEditor(
                value = "# Title <img src=x onerror=alert(1)>",
                onValueChange = {},
                showPreview = true
            )
        }

        assertFalse(renderer.renderHtmlCalled, "Untrusted Markdown preview must not use the HTML sink")
    }
}
