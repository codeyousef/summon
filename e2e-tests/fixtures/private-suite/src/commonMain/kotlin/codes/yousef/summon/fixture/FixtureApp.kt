package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.input.TextField
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.state.bindMutableStateFlow
import codes.yousef.summon.state.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow

/** Synthetic fixture data only. This is not the application's encryption or login layer. */
class FixtureSession {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val source = MutableStateFlow("Synthetic account A")
    val binding = bindMutableStateFlow(source, scope)
    val count = mutableStateOf(0)
    val loggedIn = mutableStateOf(true)
    val effectEnabled = mutableStateOf(true)
    val effectKey = mutableStateOf(0)
    val activeEffects = mutableStateOf(0)
    val disposedEffects = mutableStateOf(0)
    val items = mutableStateOf(listOf("one", "two", "three"))

    fun logout() {
        loggedIn.value = false
        binding.dispose()
        scope.cancel()
    }
}

@Composable
fun FixtureApp(session: FixtureSession) {
    Column {
        Text("Summon source consumer", Modifier().attribute("data-testid", "fixture-title"))
        Text("Count: ${session.count.value}", Modifier().attribute("data-testid", "counter"))
        Button(onClick = { session.count.value++ }, label = "Increment")
        Text("Active effects: ${session.activeEffects.value}", Modifier().attribute("data-testid", "active-effects"))
        Text("Disposed effects: ${session.disposedEffects.value}", Modifier().attribute("data-testid", "disposed-effects"))
        Button(onClick = { session.effectEnabled.value = !session.effectEnabled.value }, label = "Toggle owned effect")
        Button(onClick = { session.effectKey.value++ }, label = "Change effect key")
        session.items.value.forEach { item ->
            Text("Item $item", Modifier().attribute("key", item).attribute("data-testid", "key-item-$item"))
        }
        Button(onClick = { session.items.value = session.items.value.reversed() }, label = "Reverse items")
        if (session.loggedIn.value) {
            Text(session.binding.state.value, Modifier().attribute("data-testid", "account-value"))
            TextField(
                value = session.binding.state.value,
                onValueChange = { session.binding.state.value = it },
                label = "Synthetic account value",
                modifier = Modifier().attribute("data-testid", "controlled-input")
            )
            Button(onClick = session::logout, label = "Logout")
        } else {
            Text("Locked", Modifier().attribute("data-testid", "locked"))
            Button(onClick = { session.source.value = "Late account A result" }, label = "Emit late result")
        }
        if (session.loggedIn.value && session.effectEnabled.value) {
            DisposableEffect(session.effectKey.value) {
                session.activeEffects.value++
                return@DisposableEffect {
                    session.activeEffects.value--
                    session.disposedEffects.value++
                }
            }
        }
    }
}
