import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.renderComposableRoot
import code.yousef.summon.runtime.DirectDOMRenderer

@Composable
fun App() {
    Column(
        modifier = Modifier()
            .padding("40px")
            .background("#f5f5f5")
            .borderRadius("8px")
    ) {
        Text(
            text = "Hello, WASM! 🚀",
            modifier = Modifier()
                .fontSize("32px")
                .fontWeight("bold")
                .padding("0 0 16px 0")
        )

        Text(
            text = "Running WebAssembly with Summon",
            modifier = Modifier()
                .fontSize("18px")
                .padding("0 0 24px 0")
                .color("#666")
        )

        Button(
            onClick = {
                println("Button clicked from WASM!")
            },
            label = "Click me!",
            modifier = Modifier()
        )
    }
}

fun main() {
    try {
        renderComposableRoot("root") {
            App()
        }
    } catch (e: Exception) {
        println("Error initializing app: ${e.message}")
    }
}
