import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding
import code.yousef.summon.modifier.background
import code.yousef.summon.modifier.borderRadius
import code.yousef.summon.modifier.margin
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.getValue
import code.yousef.summon.state.setValue
import code.yousef.summon.seo.SEO
import code.yousef.summon.seo.OGType
import code.yousef.summon.renderComposableRoot
import code.yousef.summon.runtime.wasmConsoleLog

/**
 * Todo App using pure Summon Framework components.
 * No raw HTML, CSS, or JavaScript - just Summon.
 */
@Composable
fun TodoApp() {
    var todos by remember { mutableStateOf(listOf<String>()) }
    var inputText by remember { mutableStateOf("") }

    // SEO metadata for search engines
    SEO(
        title = "WASM Todo App - Kotlin WebAssembly with Summon",
        description = "A todo list application built with Kotlin, compiled to WebAssembly, using the Summon framework.",
        keywords = listOf("todo", "wasm", "webassembly", "kotlin", "summon"),
        type = OGType.Website
    )

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

        // Input section
        Row(
            modifier = Modifier().padding("0 0 20px 0")
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = "Enter a new todo...",
                modifier = Modifier()
                    .padding("0 10px 0 0")
            )

            Button(
                onClick = {
                    wasmConsoleLog("=== BUTTON CLICKED! ===")
                    wasmConsoleLog("Current inputText: '$inputText'")
                    wasmConsoleLog("Current todos count: ${todos.size}")
                    if (inputText.isNotBlank()) {
                        wasmConsoleLog("Adding todo: '$inputText'")
                        todos = todos + inputText
                        wasmConsoleLog("New todos count: ${todos.size}")
                        wasmConsoleLog("Clearing input text")
                        inputText = ""
                        wasmConsoleLog("Input text cleared, requesting recomposition")
                    } else {
                        wasmConsoleLog("Input text is blank, not adding todo")
                    }
                },
                label = "Add Todo",
                modifier = Modifier()
            )
        }

        // Todo list
        if (todos.isEmpty()) {
            Text(
                text = "No todos yet. Add one above! 👆",
                modifier = Modifier().padding("20px")
            )
        } else {
            Text(
                text = "Your todos (${todos.size}):",
                modifier = Modifier().padding("0 0 15px 0")
            )

            Column {
                todos.forEachIndexed { index, todo ->
                    Row(
                        modifier = Modifier()
                            .padding("8px")
                            .background("#f5f5f5")
                            .borderRadius("8px")
                            .margin("0 0 8px 0")
                    ) {
                        Text(
                            text = "• $todo",
                            modifier = Modifier()
                                .padding("0 10px 0 0")
                        )

                        Button(
                            onClick = {
                                todos = todos.filterIndexed { i, _ -> i != index }
                            },
                            label = "✖",
                            modifier = Modifier()
                        )
                    }
                }
            }

            // Clear all button
            if (todos.isNotEmpty()) {
                Button(
                    onClick = { todos = emptyList() },
                    label = "Clear All",
                    modifier = Modifier()
                        .margin("20px 0 0 0")
                )
            }
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
 * Main entry point for the WASM application.
 * Uses Summon's renderComposableRoot to mount the app.
 */
fun main() {
    // Mount the Todo app to the root element
    renderComposableRoot("root") {
        TodoApp()
    }
}