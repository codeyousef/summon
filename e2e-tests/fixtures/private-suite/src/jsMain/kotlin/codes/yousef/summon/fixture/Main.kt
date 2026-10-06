package codes.yousef.summon.fixture
import codes.yousef.summon.renderComposableRoot
import codes.yousef.summon.runtime.MicrotaskScheduler
import codes.yousef.summon.mountComposableRoot
import kotlinx.browser.window
fun main() {
    if (window.location.search.contains("remember=true")) {
        val fixture = RememberFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if (window.location.search.contains("lifecycle=true")) {
        val fixture = RootLifecycleFixture { session, fail ->
            mountComposableRoot("root", if (window.location.search.contains("scheduler=microtask")) MicrotaskScheduler() else null) {
                FixtureApp(session)
                if (fail) error("Synthetic failed mount")
            }
        }
        fixture.replace()
        val second = FixtureSession("Synthetic account B")
        renderComposableRoot("second-root") { FixtureApp(second) }
        mountComposableRoot("controls") { fixture.Controls() }
        return
    }
    if (window.location.search.contains("scheduler=microtask")) {
        val session = FixtureSession()
        mountComposableRoot("root", MicrotaskScheduler()) { FixtureApp(session) }
        return
    }
    val session = FixtureSession()
    renderComposableRoot("root") { FixtureApp(session) }
    if (window.location.search.contains("roots=two")) {
        val second = FixtureSession("Synthetic account B")
        renderComposableRoot("second-root") { FixtureApp(second) }
    }
}
