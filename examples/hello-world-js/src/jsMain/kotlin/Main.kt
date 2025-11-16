import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.renderComposableRoot
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.state.remember

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
            modifier = Modifier()
                .fontSize("24px")
                .padding("0 0 16px 0")
        )

        Button(
            onClick = { setCount(count + 1) },
            label = "Click me!",
            modifier = Modifier()
        )
    }
}

fun main() {
    renderComposableRoot("root") {
        App()
    }
}
