import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.hydrateComposableRoot
import codes.yousef.summon.state.mutableStateOf
import codes.yousef.summon.runtime.remember

@Composable
fun App() {
    val (count, setCount) = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier()
            .padding("40px")
            .background("#f5f5f5")
            .borderRadius("8px")
    ) {
        Text(
            text = "Hello, Summon! 👋",
            modifier = Modifier()
                .fontSize("32px")
                .fontWeight("bold")
                .padding("0 0 16px 0")
        )

        Text(
            text = "Count: $count",
            modifier = Modifier().padding("0 0 16px 0")
        )

        Button(
            onClick = { setCount(count + 1) },
            modifier = Modifier()
                .background("#007bff")
                .color("white")
                .padding("10px 20px")
                .borderRadius("4px")
        ) {
            Text("Click me")
        }
    }
}

fun main() {
    hydrateComposableRoot("root") {
        App()
    }
}
