import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.renderComposableRoot
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf

/**
 * Test app to verify that state-based buttons work correctly after the fix.
 * This uses actual Summon state management instead of DirectDOMRenderer workaround.
 */
@Composable
fun TestStateButtonApp() {
    val counter = remember { mutableStateOf(0) }

    Column(modifier = Modifier().padding("40px")) {
        Text(
            text = "State-Based Counter Test",
            modifier = Modifier().padding("0 0 20px 0")
        )

        Text(
            text = "Count: ${counter.value}",
            modifier = Modifier().padding("0 0 20px 0")
        )

        Button(
            onClick = {
                counter.value++
                println("[TestStateButton] Counter incremented to: ${counter.value}")
            },
            label = "Increment Counter"
        )

        Text(
            text = "If this counter updates when you click, the fix is working!",
            modifier = Modifier().padding("20px 0 0 0")
        )
    }
}

/**
 * Alternate main function to test state-based buttons.
 * Uncomment this and comment out the original main() in Main.kt to test.
 */
fun testStateButtonMain() {
    try {
        renderComposableRoot("root") {
            TestStateButtonApp()
        }
        println("[TestStateButton] State-based button app mounted successfully")
    } catch (e: Exception) {
        println("[TestStateButton] Error: ${e.message}")
    }
}
