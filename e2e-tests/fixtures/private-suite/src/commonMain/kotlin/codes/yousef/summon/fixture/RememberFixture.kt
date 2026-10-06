package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf

/** Real UI with a stable prefix, isolating cache lifetime from pending call-site grouping. */
class RememberFixture {
    private data class CacheKey(val name: String) { override fun hashCode() = 1 }
    private val firstKey = CacheKey("first")
    private val secondKey = CacheKey("second")
    private val count = mutableStateOf(0)
    private val key = mutableStateOf(0)
    private val visible = mutableStateOf(true)
    private var nullCalculations = 0
    private var keyedNullCalculations = 0
    private var probeCalculations = 0

    @Composable fun Content() {
        DisposableEffect(Unit) { {} }
        val nullValue = remember<String?> { nullCalculations++; null }
        val keyedNullValue = remember<String?>(key.value) { keyedNullCalculations++; null }
        check(nullValue == null && keyedNullValue == null)
        val composer = CompositionLocal.currentComposer!!
        composer.updateRememberedValue(firstKey, "first")
        composer.updateRememberedValue(secondKey, "second")
        composer.updateRememberedValue(1, "numeric")
        val named = "${composer.rememberedValue(firstKey)}/${composer.rememberedValue(secondKey)}/${composer.rememberedValue(1)}"
        Column {
            Text("Summon source consumer", Modifier().attribute("data-testid", "fixture-title"))
            Text("Count: ${count.value}", Modifier().attribute("data-testid", "counter"))
            Button(onClick = { count.value++ }, label = "Increment")
            Text("Null: $nullCalculations; Keyed null: $keyedNullCalculations; Named: $named",
                Modifier().attribute("data-testid", "remember-stats"))
            Button(onClick = { key.value++ }, label = "Change effect key")
            Button(onClick = { visible.value = !visible.value }, label = "Toggle remembered probe")
            if (visible.value) {
                val value = remember { "generation-${++probeCalculations}" }
                Text(value, Modifier().attribute("data-testid", "remembered-probe"))
            }
        }
    }
}
