package codes.yousef.summon.fixture

import codes.yousef.summon.LifecycleAwareComponent
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.lifecycle.LifecycleCoroutineScope
import codes.yousef.summon.lifecycle.LifecycleOwner
import codes.yousef.summon.lifecycle.LifecycleState
import codes.yousef.summon.lifecycle.lifecycleScope
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.state.mutableStateOf
import codes.yousef.summon.whenActive
import kotlinx.coroutines.awaitCancellation

class LifecycleOwnershipFixture {
    private val owner = LifecycleOwner()
    private val scope: LifecycleCoroutineScope = owner.lifecycleScope
    private val starts = mutableStateOf(0)
    private val cleanups = mutableStateOf(0)
    private val cycles = mutableStateOf(0)
    private val component: LifecycleAwareComponent = whenActive(owner, "fixture-owner") {
        starts.value++
        try {
            awaitCancellation()
        } finally {
            cleanups.value++
        }
    }!!

    private fun pause() {
        owner.currentState = LifecycleState.PAUSED
    }

    private fun resume() {
        owner.currentState = LifecycleState.STARTED
        owner.currentState = LifecycleState.RESUMED
    }

    private fun destroy() {
        owner.currentState = LifecycleState.DESTROYED
        component.dispose()
    }

    private fun runCycles() {
        repeat(100) {
            val cycleOwner = LifecycleOwner()
            cycleOwner.lifecycleScope
            cycleOwner.currentState = LifecycleState.DESTROYED
            cycles.value++
        }
    }

    @Composable
    fun Content() {
        Column {
            Text(
                "Starts: ${starts.value}; Cleanups: ${cleanups.value}; Disposed: ${scope.isDisposed}; Cycles: ${cycles.value}",
                Modifier().attribute("data-testid", "lifecycle-ownership-stats")
            )
            Text(
                "Stable scope: ${scope === owner.lifecycleScope}",
                Modifier().attribute("data-testid", "lifecycle-scope-identity")
            )
            Button(onClick = ::pause, label = "Pause owned lifecycle")
            Button(onClick = ::resume, label = "Resume owned lifecycle")
            Button(onClick = ::destroy, label = "Destroy owned lifecycle")
            Button(onClick = ::runCycles, label = "Run 100 lifecycle cycles")
        }
    }
}
