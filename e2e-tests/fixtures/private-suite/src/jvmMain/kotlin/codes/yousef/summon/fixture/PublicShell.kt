package codes.yousef.summon.fixture
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.clearPlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer
fun main() {
    val renderer = PlatformRenderer()
    setPlatformRenderer(renderer)
    try {
        println(renderer.renderComposableRoot { Text("Public shell: sign in to unlock") })
    } finally {
        clearPlatformRenderer()
    }
}
