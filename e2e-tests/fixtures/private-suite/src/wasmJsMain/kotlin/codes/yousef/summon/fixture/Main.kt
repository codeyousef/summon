package codes.yousef.summon.fixture
import codes.yousef.summon.renderComposableRoot
import codes.yousef.summon.mountComposableRoot
import codes.yousef.summon.runtime.wasmGetLocationSearch
fun main() {
    if ((wasmGetLocationSearch() ?: "").contains("lifecycle=true")) {
        val fixture = RootLifecycleFixture { session, fail ->
            mountComposableRoot("root") {
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
    val session = FixtureSession()
    renderComposableRoot("root") { FixtureApp(session) }
    if (wasmGetLocationSearch()?.contains("roots=two") == true) {
        val second = FixtureSession("Synthetic account B")
        renderComposableRoot("second-root") { FixtureApp(second) }
    }
}
