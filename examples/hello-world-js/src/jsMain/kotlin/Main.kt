import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.hydrateComposableRoot
import codes.yousef.summon.state.mutableStateOf
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.runtime.LocalPlatformRenderer

@Composable
fun App() {
    val renderer = LocalPlatformRenderer.current
    val (count, setCount) = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier()
            .padding("40px")
            .background("#f5f5f5")
            .borderRadius("8px")
    ) {
        // Wrap in a form WITHOUT onSubmit
        codes.yousef.summon.components.input.Form(
            modifier = Modifier()
        ) {
            // 1. The HamburgerMenu (which we want to test)
            // We can't easily import it if I broke the build, but let's assume it's there.
            // Actually, let's simulate a "broken" button first to prove the test works.
            
            // A native button with type="submit" (default) should trigger refresh
            renderer.renderNativeButton(
                type = "submit", 
                modifier = Modifier().attribute("id", "submit-btn"),
            ) {
                Text("Submit Button")
            }
            
            // A native button with type="button" should NOT trigger refresh
            renderer.renderNativeButton(
                type = "button", 
                modifier = Modifier().attribute("id", "button-btn"),
            ) {
                Text("Regular Button")
            }
        }
    }
}

fun main() {
    hydrateComposableRoot("root") {
        App()
    }
}
