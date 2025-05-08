// RenderUtilsExtensions.kt
package code.yousef.summon.core

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.Renderer

/**
 * This is a direct implementation of the renderComposable method that's called by the JavaScript code.
 * It's annotated with @JsName to ensure it's correctly exposed to JavaScript with the exact name
 * that the Kotlin compiler generates.
 */
@JsName("renderComposable_udbimr_k")
fun RenderUtils.actualRenderComposable(composable: @Composable () -> Unit): Renderer<Any> {
    // This is just a placeholder - the real implementation is in the JS patch
    js("console.log('Kotlin implementation called, but JS patch should have taken over');")

    return object : Renderer<Any> {
        override fun render(composable: @Composable () -> Unit): Any {
            js("if (typeof composable === 'function') composable();")
            return js("({})")
        }

        override fun dispose() {
            // No-op
        }
    }
}
