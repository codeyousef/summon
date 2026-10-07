package codes.yousef.summon.devtoolsfixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.key
import codes.yousef.summon.state.mutableStateOf

class InspectorFixture(private val name: String) {
    val count = mutableStateOf(0)
    private val reversed = mutableStateOf(false)

    @Composable
    fun Content() {
        Column(modifier = Modifier().attribute("data-testid", "$name-app")) {
            Text("$name count: ${count.value}", Modifier().attribute("data-testid", "$name-counter"))
            Button(onClick = { count.value++ }, label = "Increment $name counter")
            Button(onClick = { reversed.value = !reversed.value }, label = "Reorder $name children")
            val ids = if (reversed.value) listOf("second", "first") else listOf("first", "second")
            ids.forEach { id ->
                key(id) {
                    Column(modifier = Modifier().attribute("data-testid", "$name-item-$id")) {
                        Text("$name item $id")
                    }
                }
            }
        }
    }
}
