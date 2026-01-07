package codes.yousef.summon.components.foundation

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.PointerEvents
import codes.yousef.summon.modifier.pointerEvents
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HtmlPrimitivesTest {

    @Test
    fun canvasEmitsAttributesAndDimensions() {
        val renderer = MockPlatformRenderer()

        runComposableTest(renderer) {
            Canvas(
                id = "gl",
                width = 1024,
                height = 512,
                modifier = Modifier()
                    .pointerEvents(PointerEvents.None),
                dataAttributes = mapOf("scope" to "hero")
            )
        }

        assertTrue(renderer.renderCanvasCalled, "Canvas should delegate to renderCanvas")
        assertEquals(1024, renderer.lastCanvasWidth)
        assertEquals(512, renderer.lastCanvasHeight)
        val modifier = renderer.lastCanvasModifier
        assertNotNull(modifier, "Canvas modifier should be forwarded")
        assertEquals("gl", modifier.attributes["id"])
        assertEquals("hero", modifier.attributes["data-scope"])
        assertEquals("none", modifier.styles["pointer-events"])
    }

    @Test
    fun scriptTagSupportsExternalSources() {
        val renderer = MockPlatformRenderer()

        runComposableTest(renderer) {
            ScriptTag(
                src = "/assets/app.js",
                async = true,
                defer = true,
                id = "app-script",
                dataAttributes = mapOf("scope" to "hero")
            )
        }

        assertTrue(renderer.renderScriptTagCalled, "ScriptTag should delegate to renderer")
        assertEquals("/assets/app.js", renderer.lastScriptSrcRendered)
        assertEquals(true, renderer.lastScriptAsyncRendered)
        assertEquals(true, renderer.lastScriptDeferRendered)
        val modifier = renderer.lastScriptModifierRendered
        assertNotNull(modifier, "Script modifier should be captured")
        assertEquals("app-script", modifier.attributes["id"])
        assertEquals("hero", modifier.attributes["data-scope"])
    }

    @Test
    fun scriptTagSupportsInlineContent() {
        val renderer = MockPlatformRenderer()

        runComposableTest(renderer) {
            ScriptTag(
                type = "module",
                dataAttributes = mapOf("analytics" to "hero"),
                inlineContent = "console.log('inline')"
            )
        }

        assertTrue(renderer.renderScriptTagCalled, "Inline scripts should render")
        assertEquals("module", renderer.lastScriptTypeRendered)
        assertEquals("console.log('inline')", renderer.lastScriptInlineContentRendered)
        assertEquals("hero", renderer.lastScriptModifierRendered?.attributes?.get("data-analytics"))
    }
}
