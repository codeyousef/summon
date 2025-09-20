package code.yousef.summon.core

import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.annotation.Composable

actual class ThreadLocalHolder<T> actual constructor() {
    private var value: T? = null

    actual fun get(): T? {
        wasmConsoleLog("ThreadLocalHolder.get() - WASM stub")
        return value
    }

    actual fun set(value: T?) {
        wasmConsoleLog("ThreadLocalHolder.set($value) - WASM stub")
        this.value = value
    }
}

actual object RenderUtils {
    actual fun renderComposable(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
        wasmConsoleLog("RenderUtils.renderComposable - WASM stub")
        return object : Renderer<Any> {
            override fun render(composable: @Composable () -> Unit): Any {
                wasmConsoleLog("Renderer.render - WASM stub")
                return "wasm-render-result"
            }

            override fun dispose() {
                wasmConsoleLog("Renderer.dispose - WASM stub")
            }
        }
    }

    actual fun hydrate(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
        wasmConsoleLog("RenderUtils.hydrate - WASM stub")
        return renderComposable(container, composable)
    }

    actual fun renderToString(composable: @Composable () -> Unit): String {
        wasmConsoleLog("RenderUtils.renderToString - WASM stub")
        return "<div>WASM Stub Content</div>"
    }

    actual fun renderToFile(composable: @Composable () -> Unit, file: Any) {
        wasmConsoleLog("RenderUtils.renderToFile - WASM stub")
    }
}