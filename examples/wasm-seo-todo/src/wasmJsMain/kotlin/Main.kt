// import code.yousef.summon.seo.SEO
// import code.yousef.summon.seo.OGType
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.renderComposableRoot
import code.yousef.summon.runtime.DirectDOMRenderer
import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.runtime.wasmExecuteCallback

/**
 * Todo App using pure Summon Framework components.
 * No raw HTML, CSS, or JavaScript - just Summon.
 */
@Composable
fun TodoApp() {
    // Use DirectDOMRenderer instead of state to bypass WASM recomposition issues

    // SEO metadata for search engines - disabled for WASM (SEO is for SSR)
    // SEO(
    //     title = "WASM Todo App - Kotlin WebAssembly with Summon",
    //     description = "A todo list application built with Kotlin, compiled to WebAssembly, using the Summon framework.",
    //     keywords = listOf("todo", "wasm", "webassembly", "kotlin", "summon"),
    //     type = OGType.Website
    // )

    Column(
        modifier = Modifier()
            .padding("40px")
            .background("#ffffff")
            .borderRadius("12px")
    ) {
        // Title
        Text(
            text = "📝 WASM Todo App",
            modifier = Modifier().padding("0 0 10px 0")
        )

        Text(
            text = "Built with Kotlin, WebAssembly & Summon",
            modifier = Modifier().padding("0 0 30px 0")
        )

        // Input section - using DirectDOMRenderer to bypass recomposition
        Row(
            modifier = Modifier().padding("0 0 20px 0")
        ) {
            TextField(
                value = "",
                onValueChange = { value ->
                    wasmConsoleLog("TextField value changed: '$value'")
                    DirectDOMRenderer.updateInputValue(value)
                },
                placeholder = "Enter a new todo...",
                modifier = Modifier()
                    .padding("0 10px 0 0")
            )

            Button(
                onClick = {
                    wasmConsoleLog("=== BUTTON CLICKED! === (DirectDOMRenderer approach)")
                    val currentInput = DirectDOMRenderer.getCurrentInputValue()
                    wasmConsoleLog("Current input from DirectDOMRenderer: '$currentInput'")

                    if (currentInput.isNotBlank()) {
                        wasmConsoleLog("Adding todo via DirectDOMRenderer: '$currentInput'")
                        DirectDOMRenderer.addTodo(currentInput)
                        wasmConsoleLog("Todo added successfully, DOM updated directly")
                    } else {
                        wasmConsoleLog("Input text is blank, not adding todo")
                    }
                },
                label = "Add Todo",
                modifier = Modifier()
            )
        }

        // Todo count - static container that DirectDOMRenderer will update
        Text(
            text = "Loading todos...",
            modifier = Modifier()
                .padding("0 0 15px 0")
        )

        // Todo list container - DirectDOMRenderer will populate this
        Column {
            // DirectDOMRenderer will populate this with todo items
            Text(
                text = "Initializing...",
                modifier = Modifier().padding("20px")
            )
        }

        // Footer
        Text(
            text = "✨ Running in WebAssembly with Summon Framework",
            modifier = Modifier()
                .margin("30px 0 0 0")
                .padding("20px 0 0 0")
        )
    }
}

/**
 * Re-export the wasmExecuteCallback function so it's accessible from JavaScript.
 * This is a bridge function for event handling from JavaScript to WASM.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun executeCallback(callbackId: String): Boolean {
    return wasmExecuteCallback(callbackId)
}

/**
 * Bridge function to remove a todo item by index from JavaScript.
 * This allows the DirectDOMRenderer HTML onclick handlers to work.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun removeTodo(index: Int) {
    wasmConsoleLog("Bridge function removeTodo called with index: $index")
    DirectDOMRenderer.removeTodo(index)
}

/**
 * Bridge function to clear all todos from JavaScript.
 * This allows the DirectDOMRenderer HTML onclick handlers to work.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun clearAllTodos() {
    wasmConsoleLog("Bridge function clearAllTodos called")
    DirectDOMRenderer.clearAllTodos()
}

// Note: executeAnimationFrameCallback is already exported from AnimationFrameCallbackRegistry.kt
// No need to re-export it here to avoid duplicate exports

/**
 * Main entry point for the WASM application.
 * Uses Summon's renderComposableRoot to mount the app.
 */
fun main() {
    try {
        wasmConsoleLog("Main function started")

        // Log that our callback bridge is available
        wasmConsoleLog("Callback bridge function 'executeCallback' is exported")

        // Mount the Todo app to the root element
        renderComposableRoot("root") {
            TodoApp()
        }

        wasmConsoleLog("Main function completed")

        // Initialize DirectDOMRenderer after initial composition
        wasmConsoleLog("Initializing DirectDOMRenderer...")

        // First initialize with placeholder IDs
        DirectDOMRenderer.initialize("placeholder-list", "placeholder-count")

        // Then discover the actual element IDs from the rendered DOM
        wasmConsoleLog("Discovering actual element IDs...")
        val discoverySuccess = DirectDOMRenderer.discoverActualElementIds()

        if (discoverySuccess) {
            wasmConsoleLog("DirectDOMRenderer initialized successfully with discovered element IDs")
        } else {
            wasmConsoleLog("DirectDOMRenderer warning: Could not discover element IDs, using placeholders")
        }
    } catch (e: Exception) {
        wasmConsoleLog("ERROR in main: ${e.message}")
        wasmConsoleLog("Stack trace: ${e.stackTraceToString()}")
    }
}