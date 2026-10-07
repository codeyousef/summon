package codes.yousef.summon.testfixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.input.TextField
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.style
import codes.yousef.summon.state.mutableStateOf
import codes.yousef.summon.test.testState
import codes.yousef.summon.test.testTag

class HarnessFixture {
    private val count = mutableStateOf(0)
    private val text = mutableStateOf("")
    private val targetVisible = mutableStateOf(true)
    var clickCallbacks = 0
    var inputCallbacks = 0

    @Composable
    fun Content() {
        Column(Modifier().testTag("app")) {
            Text("مرحبا <&> \"Summon\"", Modifier().testTag("arabic"))
            Text("duplicate")
            Text("duplicate")
            Text(
                "Count: ${count.value}",
                Modifier().testTag("count").testState("value", count.value.toLong())
            )
            Button(
                onClick = {
                    clickCallbacks++
                    count.value++
                },
                label = "Increment",
                modifier = Modifier().testTag("increment")
            )
            TextField(
                value = text.value,
                onValueChange = {
                    inputCallbacks++
                    text.value = it
                },
                label = "Arabic input",
                modifier = Modifier().testTag("input")
            )
            Text(text.value, Modifier().testTag("mirror"))
            Box(Modifier().style("display", "none")) {
                Text("Hidden text", Modifier().testTag("hidden"))
            }
            Button({}, "Disabled", Modifier().testTag("disabled"), disabled = true)
            Button(
                onClick = { targetVisible.value = false },
                label = "Remove target",
                modifier = Modifier().testTag("remove")
            )
            if (targetVisible.value) {
                Text("Scroll target", Modifier().testTag("target").style("margin-top", "1800px"))
            }
        }
    }
}
