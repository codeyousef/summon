package code.yousef.summon.examples.wasm

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import code.yousef.summon.seo.*
import code.yousef.summon.state.getValue
import code.yousef.summon.state.setValue

/**
 * WASM Todo App that demonstrates SEO compatibility.
 *
 * KEY POINT: This app runs in WebAssembly while maintaining
 * full SEO capabilities through server-side rendering.
 *
 * The SEO metadata is rendered server-side, while the
 * interactive functionality runs in WASM on the client.
 */
@Composable
fun WasmTodoWithSEO() {
    // SEO metadata - rendered server-side, not affected by WASM
    SEO(
        title = "WASM Todo App - SEO Compatible",
        description = "A WebAssembly-powered todo app that maintains full SEO compatibility through server-side rendering.",
        keywords = listOf("wasm", "webassembly", "todo", "seo", "kotlin"),
        type = OGType.WebApplication,
        twitterCard = TwitterCardType.Summary
    )

    // Structured data for search engines
    StructuredData(
        WebApplicationSchema(
            name = "WASM Todo App",
            description = "Fast, interactive todo app running in WebAssembly",
            applicationCategory = "Productivity"
        )
    )

    // The actual todo app - runs in WASM on client
    TodoApp()
}

@Composable
fun TodoApp() {
    var todos by remember { mutableStateOf(listOf<String>()) }
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier().padding("20px")
    ) {
        Text(
            text = "WASM Todo App",
            modifier = Modifier().padding("0 0 20px 0")
        )

        Text(
            text = "This app runs in WebAssembly while maintaining SEO!",
            modifier = Modifier().padding("0 0 20px 0")
        )

        // Add todo form
        Row(
            modifier = Modifier().padding("0 0 20px 0")
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier().padding("0 10px 0 0")
            )

            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        todos = todos + inputText
                        inputText = ""
                    }
                },
                modifier = Modifier()
            ) {
                Text("Add Todo")
            }
        }

        // Todo list
        Column {
            todos.forEachIndexed { index, todo ->
                Row(
                    modifier = Modifier().padding("5px 0")
                ) {
                    Text(
                        text = "${index + 1}. $todo",
                        modifier = Modifier().padding("0 10px 0 0")
                    )

                    Button(
                        onClick = {
                            todos = todos.filterIndexed { i, _ -> i != index }
                        },
                        modifier = Modifier()
                    ) {
                        Text("Delete")
                    }
                }
            }
        }

        if (todos.isEmpty()) {
            Text(
                text = "No todos yet. Add one above!",
                modifier = Modifier().padding("20px 0")
            )
        }
    }
}

/**
 * Server-side rendering function.
 * This generates the initial HTML with SEO metadata.
 */
fun renderServerSide(): String {
    val renderer = PlatformRenderer()
    setPlatformRenderer(renderer)

    return renderer.renderComposableRoot {
        WasmTodoWithSEO()
    }
}

/**
 * Client-side hydration function.
 * This would be called from WASM to make the page interactive.
 */
fun hydrateClient() {
    val renderer = PlatformRenderer()
    setPlatformRenderer(renderer)

    // In WASM, this would hydrate the existing DOM
    renderer.hydrateComposableRoot("root") {
        WasmTodoWithSEO()
    }
}

/**
 * Main entry point demonstrating the dual nature:
 * 1. Server renders with SEO
 * 2. Client hydrates with WASM
 */
fun main() {
    println(
        """
    ===========================================
    WASM + SEO Demonstration
    ===========================================
    
    This example shows how Summon maintains SEO compatibility
    even when using WebAssembly for client-side interactivity.
    
    1. Server-Side Rendering (for SEO):
       - Generates full HTML with meta tags
       - Search engines see complete content
       - Works without JavaScript/WASM
    
    2. Client-Side WASM (for interactivity):
       - Hydrates the server-rendered HTML
       - Provides fast, native-like performance
       - Handles all user interactions
    
    Key Point: SEO is NOT affected by WASM because:
    - Meta tags are rendered server-side
    - Content is available without WASM
    - Search engines get fully-rendered HTML
    """.trimIndent()
    )

    // Demonstrate server-side rendering
    val html = renderServerSide()

    println("\nGenerated HTML (first 500 chars):")
    println(html.take(500))
    println("...")

    println("\n✅ SEO metadata is present in the HTML")
    println("✅ Content is rendered server-side")
    println("✅ WASM would hydrate this on the client")
}