import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.navigation.HamburgerMenu
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.hydrateComposableRoot
import codes.yousef.summon.state.mutableStateOf
import codes.yousef.summon.runtime.remember

/**
 * Test app for verifying client-side hydration functionality.
 * This app tests:
 * - HamburgerMenu toggle functionality
 * - Basic button click with state updates
 * - Various interactive components
 */
@Composable
fun App() {
    val (count, setCount) = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier()
            .padding("20px")
            .fillMaxWidth()
    ) {
        // Title
        Text(
            text = "Hydration Test App",
            modifier = Modifier()
                .style("font-size", "24px")
                .style("font-weight", "bold")
                .style("margin-bottom", "20px")
        )

        // Hamburger Menu - shows on mobile (viewport width < 768px typically)
        Box(
            modifier = Modifier()
                .fillMaxWidth()
                .background("#f0f0f0")
                .borderRadius("8px")
                .padding("10px")
                .style("margin-bottom", "20px")
        ) {
            HamburgerMenu(
                modifier = Modifier()
                    .fillMaxWidth(),
                iconColor = "#333"
            ) {
                // Menu content
                Column(
                    modifier = Modifier()
                        .padding("10px")
                        .background("#fff")
                        .borderRadius("4px")
                        .style("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                ) {
                    Text(
                        text = "Menu Item 1",
                        modifier = Modifier()
                            .padding("10px")
                            .style("cursor", "pointer")
                    )
                    Text(
                        text = "Menu Item 2",
                        modifier = Modifier()
                            .padding("10px")
                            .style("cursor", "pointer")
                    )
                    Text(
                        text = "Menu Item 3",
                        modifier = Modifier()
                            .padding("10px")
                            .style("cursor", "pointer")
                    )
                }
            }
        }

        // Counter section to test basic reactivity
        Box(
            modifier = Modifier()
                .fillMaxWidth()
                .background("#e8f4e8")
                .borderRadius("8px")
                .padding("20px")
        ) {
            Column {
                Text(
                    text = "Counter Test",
                    modifier = Modifier()
                        .style("font-size", "18px")
                        .style("font-weight", "bold")
                        .style("margin-bottom", "10px")
                )
                Text(
                    text = "Count: $count",
                    modifier = Modifier()
                        .style("font-size", "16px")
                        .style("margin-bottom", "10px")
                )
                Button(
                    label = "Click me",
                    onClick = { setCount(count + 1) },
                    modifier = Modifier()
                        .padding("10px 20px")
                        .background("#4CAF50")
                        .style("color", "white")
                        .borderRadius("4px")
                        .style("border", "none")
                        .style("cursor", "pointer")
                )
            }
        }
    }
}

fun main() {
    println("Hydration test app starting...")
    hydrateComposableRoot("root") {
        App()
    }
    println("Hydration test app initialized")
}
