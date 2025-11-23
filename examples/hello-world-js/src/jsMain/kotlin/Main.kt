import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.navigation.HamburgerMenu
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ModifierExtras.withAttribute
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
        // Add Hamburger Menu for E2E testing
        HamburgerMenu(
            modifier = Modifier().withAttribute("data-test-id", "hamburger-menu"),
            menuContent = {
                Column(modifier = Modifier().withAttribute("data-test-id", "menu-content")) {
                    Text("Menu Item 1", Modifier().padding("8px"))
                    Text("Menu Item 2", Modifier().padding("8px"))
                }
            }
        )

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
    hydrateComposableRoot("root") {
        App()
    }
}
