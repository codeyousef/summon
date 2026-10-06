package codes.yousef.summon.fixture

import codes.yousef.summon.MountedComposition
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.state.mutableStateOf

class RootLifecycleFixture(private val mount: (FixtureSession, Boolean) -> MountedComposition) {
    private var owner: MountedComposition? = null
    private val retired = mutableListOf<FixtureSession>()
    private var current: FixtureSession? = null
    private val revision = mutableStateOf(0)
    private var cycles = 0
    private var failedMounts = 0

    fun replace() {
        val previous = current
        val next = FixtureSession()
        owner = mount(next, false)
        if (previous != null) retired.add(previous)
        current = next
        revision.value++
    }

    private fun dispose() {
        owner?.dispose()
        owner?.dispose()
        current?.let { retired.add(it) }
        current = null
        owner = null
        revision.value++
    }

    private fun failMount() {
        current?.let { retired.add(it) }
        current = null
        owner = null
        val failed = FixtureSession()
        retired.add(failed)
        try { mount(failed, true) } catch (_: IllegalStateException) { failedMounts++ }
        revision.value++
    }

    @Composable fun Controls() {
        revision.value
        Column {
            Text("Cycles: $cycles; Effects: ${retired.sumOf { it.activeEffects.value }}; Collectors: ${retired.sumOf { it.activeCollectors }}; Last count: ${retired.lastOrNull()?.count?.value ?: 0}",
                Modifier().attribute("data-testid", "root-stats"))
            Text("Failed mounts: $failedMounts", Modifier().attribute("data-testid", "failed-mounts"))
            Button(onClick = ::failMount, label = "Fail first mount")
            Button(onClick = ::replace, label = "Replace first root")
            Button(onClick = ::dispose, label = "Dispose first root")
            Button(onClick = {
                current?.count?.value = 10
                dispose()
            }, label = "Queue update and dispose")
            Button(onClick = {
                dispose()
                repeat(100) {
                    replace()
                    current!!.count.value++
                    dispose()
                    cycles++
                }
                revision.value++
            }, label = "Run 100 mount cycles")
            Button(onClick = {
                retired.forEach { it.source.value = "Late retired result" }
                revision.value++
            }, label = "Emit retired results")
            Button(onClick = { revision.value++ }, label = "Refresh lifecycle stats")
        }
    }
}
