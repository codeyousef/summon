package codes.yousef.summon.fixture
import codes.yousef.summon.renderComposableRoot
fun main() {
    val session = FixtureSession()
    renderComposableRoot("root") { FixtureApp(session) }
}
