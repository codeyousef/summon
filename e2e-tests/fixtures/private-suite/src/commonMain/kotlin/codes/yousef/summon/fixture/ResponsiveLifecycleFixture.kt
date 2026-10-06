package codes.yousef.summon.fixture

import codes.yousef.summon.MountedComposition
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.ResponsiveLayout
import codes.yousef.summon.components.layout.ScreenSize
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.state.mutableStateOf
import kotlinx.coroutines.CancellationException

class ResponsiveSession {
    val revision = mutableStateOf(0)
    val visible = mutableStateOf(true)
}

@Composable
fun ResponsiveContent(session: ResponsiveSession, failure: Throwable?) {
    Column {
        Text("Responsive revision: ${session.revision.value}", Modifier().attribute("data-testid", "responsive-revision"))
        if (session.visible.value) {
            ResponsiveLayout(
                content = mapOf(
                    ScreenSize.SMALL to { Text("Small layout", Modifier().attribute("data-testid", "responsive-content")) },
                    ScreenSize.MEDIUM to { Text("Medium layout", Modifier().attribute("data-testid", "responsive-content")) },
                    ScreenSize.LARGE to { Text("Large layout", Modifier().attribute("data-testid", "responsive-content")) },
                    ScreenSize.XLARGE to { Text("Extra-large layout", Modifier().attribute("data-testid", "responsive-content")) }
                ),
                defaultContent = { Text("Default layout", Modifier().attribute("data-testid", "responsive-content")) },
                modifier = Modifier()
                    .attribute("data-summon-id", "owned-responsive-layout")
                    .attribute("data-testid", "responsive-layout")
            )
        }
        failure?.let { throw it }
    }
}

class ResponsiveLifecycleFixture(
    private val mount: (ResponsiveSession, Throwable?) -> MountedComposition
) {
    private var owner: MountedComposition? = null
    private var current: ResponsiveSession? = null
    private val controlsRevision = mutableStateOf(0)
    private var cycles = 0
    private var failedMounts = 0
    private var cancellations = 0

    fun replace() {
        val next = ResponsiveSession()
        owner = mount(next, null)
        current = next
        controlsRevision.value++
    }

    private fun dispose() {
        owner?.dispose()
        owner?.dispose()
        owner = null
        current = null
        controlsRevision.value++
    }

    private fun failMount() {
        owner = null
        current = null
        val failed = ResponsiveSession()
        try {
            mount(failed, IllegalStateException("Synthetic responsive mount failure"))
        } catch (_: IllegalStateException) {
            failedMounts++
        }
        controlsRevision.value++
    }
    private fun cancelMount() {
        owner = null
        current = null
        val cancelled = ResponsiveSession()
        try {
            mount(cancelled, CancellationException("Synthetic responsive mount cancellation"))
        } catch (_: CancellationException) {
            cancellations++
        }
        controlsRevision.value++
    }



    @Composable
    fun Controls() {
        controlsRevision.value
        Column {
            Text("Cycles: $cycles; Failed mounts: $failedMounts; Cancellations: $cancellations", Modifier().attribute("data-testid", "responsive-stats"))
            Button(onClick = { current?.revision?.value = current?.revision?.value?.plus(1) ?: 0 }, label = "Recompose responsive root")
            Button(onClick = { current?.visible?.value = !(current?.visible?.value ?: false) }, label = "Toggle responsive layout")
            Button(onClick = ::replace, label = "Replace responsive root")
            Button(onClick = ::dispose, label = "Dispose responsive root")
            Button(onClick = ::failMount, label = "Fail responsive mount")
            Button(onClick = ::cancelMount, label = "Cancel responsive mount")
            Button(onClick = {
                dispose()
                repeat(100) {
                    replace()
                    current!!.revision.value++
                    dispose()
                    cycles++
                }
                controlsRevision.value++
            }, label = "Run 100 responsive cycles")
        }
    }
}
