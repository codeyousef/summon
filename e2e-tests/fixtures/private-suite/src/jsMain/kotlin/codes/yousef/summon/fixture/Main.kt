package codes.yousef.summon.fixture
import codes.yousef.summon.renderComposableRoot
import codes.yousef.summon.runtime.MicrotaskScheduler
import codes.yousef.summon.runtime.RecomposerHolder
import kotlinx.browser.window
fun main() {
    if (window.location.search.contains("scheduler=microtask")) {
        RecomposerHolder.setScheduler(MicrotaskScheduler())
    }
    val session = FixtureSession()
    renderComposableRoot("root") { FixtureApp(session) }
}
