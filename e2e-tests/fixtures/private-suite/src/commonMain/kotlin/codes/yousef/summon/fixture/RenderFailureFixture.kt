package codes.yousef.summon.fixture

import codes.yousef.summon.MountedComposition
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.input.TextField
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.state.mutableStateOf
import kotlinx.coroutines.CancellationException

/** All payloads are synthetic; caught failures are counted, never logged or displayed. */
class RenderFailureFixture(private val mount: (@Composable () -> Unit) -> MountedComposition) {
    private val sessions = mutableListOf<FixtureSession>()
    private var owner: MountedComposition? = null
    private val revision = mutableStateOf(0)
    private var failures = 0
    private var cancellations = 0
    private var active = 0
    private var cleanups = 0
    private var kind = "Column"

    private fun render(layout: String, fail: Boolean, cancel: Boolean = false) {
        kind = layout
        val session = FixtureSession("PRIVATE_RENDER_VALUE_SENTINEL")
        sessions.add(session)
        owner?.dispose()
        owner = null
        try {
            owner = mount {
                DisposableEffect(session) { active++; { active--; cleanups++; session.dispose() } }
                val content: @Composable () -> Unit = {
                    DisposableEffect("nested") { active++; { active--; cleanups++ } }
                    Text("PRIVATE_RENDER_TEXT_SENTINEL", Modifier().attribute("key", "PRIVATE_RENDER_KEY_SENTINEL"))
                    TextField(session.binding.state.value, { session.binding.state.value = it },
                        Modifier().attribute("data-testid", "failure-input"))
                    if (cancel) throw CancellationException("PRIVATE_RENDER_CANCEL_SENTINEL")
                    if (fail) error("PRIVATE_RENDER_ERROR_SENTINEL")
                    Text("Healthy $layout", Modifier().attribute("data-testid", "healthy-layout"))
                }
                when (layout) {
                    "Column" -> Column { content() }
                    "Row" -> Row { content() }
                    "Box" -> Box { content() }
                    "Div" -> LocalPlatformRenderer.current.renderDiv(Modifier()) { content() }
                    else -> error("Unknown synthetic layout")
                }
            }
        } catch (_: CancellationException) {
            cancellations++
        } catch (_: Throwable) {
            failures++
        }
        revision.value++
    }

    @Composable fun Controls() {
        revision.value
        Column {
            Text("Failures: $failures; Cancellations: $cancellations; Active: $active; Collectors: ${sessions.sumOf { it.activeCollectors }}; Cleanups: $cleanups",
                Modifier().attribute("data-testid", "failure-stats"))
            listOf("Column", "Row", "Box", "Div").forEach { layout ->
                Button(onClick = { render(layout, true) }, label = "Fail $layout")
            }
            Button(onClick = { render("Column", false, true) }, label = "Cancel Column")
            Button(onClick = { render(kind, false) }, label = "Mount healthy layout")
            Button(onClick = { owner?.dispose(); owner?.dispose(); revision.value++ }, label = "Dispose healthy layout")
        }
    }
}
