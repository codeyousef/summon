package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.input.TextField
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.runtime.SideEffect
import codes.yousef.summon.runtime.key
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.effects.CompositionScope
import codes.yousef.summon.effects.onMount
import codes.yousef.summon.effects.effectWithDepsAndCleanup
import codes.yousef.summon.state.bindMutableStateFlow
import codes.yousef.summon.state.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow

/** Synthetic fixture data only. This is not the application's encryption or login layer. */
class FixtureSession(initialAccount: String = "Synthetic account A") {
    val accountIdentity = initialAccount
    var probeCalculations = 0
    val itemCalculations = mutableMapOf<String, Int>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val source = MutableStateFlow(initialAccount)
    val binding = bindMutableStateFlow(source, scope)
    val count = mutableStateOf(0)
    val loggedIn = mutableStateOf(true)
    val effectEnabled = mutableStateOf(true)
    val effectKey = mutableStateOf(0)
    val activeEffects = mutableStateOf(0)
    val disposedEffects = mutableStateOf(0)
    val items = mutableStateOf(listOf("one", "two", "three"))

    val activeCollectors: Int get() = scope.coroutineContext[Job]!!.children.count { it.isActive }
    var helperMounts = 0
    var helperSetups = 0
    var helperCleanups = 0
    var sideOrderErrors = 0

    fun dispose() {
        binding.dispose()
        scope.cancel()
    }

    fun logout() {
        loggedIn.value = false
        dispose()
    }
}

@Composable
fun FixtureApp(session: FixtureSession) {
    DisposableEffect(session) { { session.dispose() } }
    val effects = object : CompositionScope {
        override fun compose(block: @Composable () -> Unit) = block()
    }
    effects.onMount { session.helperMounts++ }
    effects.effectWithDepsAndCleanup(session.effectKey.value, "stable") {
        session.helperSetups++
        return@effectWithDepsAndCleanup { session.helperCleanups++ }
    }
    val remembered = remember { "remembered after effects" }
    var renderFinished = false
    SideEffect { if (!renderFinished) session.sideOrderErrors++ }
    Column {
        Text("Mounts: ${session.helperMounts}; Setups: ${session.helperSetups}; Cleanups: ${session.helperCleanups}; Side errors: ${session.sideOrderErrors}; $remembered",
            Modifier().attribute("data-testid", "helper-stats"))
        Text("Summon source consumer", Modifier().attribute("data-testid", "fixture-title"))
        Text("Count: ${session.count.value}", Modifier().attribute("data-testid", "counter"))
        Button(onClick = { session.count.value++ }, label = "Increment")
        Text("Active effects: ${session.activeEffects.value}", Modifier().attribute("data-testid", "active-effects"))
        Text("Disposed effects: ${session.disposedEffects.value}", Modifier().attribute("data-testid", "disposed-effects"))
        Button(onClick = { session.effectEnabled.value = !session.effectEnabled.value }, label = "Toggle owned effect")
        Button(onClick = { session.effectKey.value++ }, label = "Change effect key")
        session.items.value.forEach { item ->
            key("item", item) {
                val generation = remember {
                    (session.itemCalculations[item] ?: 0).plus(1).also { session.itemCalculations[item] = it }
                }
                Text("Item $item", Modifier().attribute("key", item).attribute("data-testid", "key-item-$item"))
                Text("$item:$generation", Modifier().attribute("key", "state-$item").attribute("data-testid", "state-item-$item"))
            }
        }
        Button(onClick = { session.items.value = session.items.value.reversed() }, label = "Reverse items")
        Button(onClick = { session.items.value = session.items.value.filter { it != "two" } }, label = "Remove second item")
        Button(onClick = { session.items.value = listOf("two") + session.items.value.filter { it != "two" } }, label = "Insert second item")
        if (session.loggedIn.value) key("account", session.accountIdentity) {
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
        if (session.loggedIn.value && session.effectEnabled.value) key("owned-effect", session.accountIdentity) {
            DisposableEffect(session.effectKey.value) {
                session.activeEffects.value++
                return@DisposableEffect {
                    session.activeEffects.value--
                    session.disposedEffects.value++
                }
            }
        }
        key("trailing-probe", session.accountIdentity) {
            val probe = remember { "generation-${++session.probeCalculations}" }
            Text(probe, Modifier().attribute("data-testid", "group-probe"))
        }
    }
    renderFinished = true
}
