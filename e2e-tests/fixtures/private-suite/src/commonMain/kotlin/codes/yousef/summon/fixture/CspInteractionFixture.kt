package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.feedback.Modal
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.LazyColumn
import codes.yousef.summon.components.layout.LazyListState
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.modifier.style
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf

class CspInteractionFixture {
    @Composable
    fun Content() {
        val modalOpen = remember { mutableStateOf(false) }
        val lazyState = remember { LazyListState() }
        Column {
            Text("Strict CSP interactions", Modifier().attribute("data-testid", "csp-title"))
            Button(onClick = { modalOpen.value = true }, label = "Open CSP dialog")
            Modal(
                isOpen = modalOpen.value,
                onDismiss = { modalOpen.value = false },
                showCloseButton = false,
                footer = {
                    Button(onClick = { modalOpen.value = false }, label = "Close CSP dialog")
                }
            ) {
                Text("CSP dialog content", Modifier().attribute("data-testid", "csp-dialog-content"))
            }
            Text(
                "Scroll: ${lazyState.scrollPosition.toInt()}",
                Modifier().attribute("data-testid", "csp-scroll-position")
            )
            LazyColumn(
                state = lazyState,
                modifier = Modifier()
                    .attribute("data-testid", "csp-lazy-list")
                    .style("height", "140px")
                    .style("width", "240px")
            ) {
                items((0 until 100).toList(), key = { it }) { index ->
                    Text(
                        "Virtual row $index",
                        Modifier()
                            .attribute("data-testid", "csp-row-$index")
                            .style("display", "block")
                            .style("min-height", "50px")
                    )
                }
            }
        }
    }
}
