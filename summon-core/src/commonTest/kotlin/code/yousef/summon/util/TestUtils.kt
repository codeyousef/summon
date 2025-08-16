package code.yousef.summon.util

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.Composer
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.PlatformRenderer

// Basic Mock Composer (moved from component tests)
internal class TestComposer : Composer {
    private val slots = mutableMapOf<Int, Any?>()
    private var currentSlot = 0
    override val inserting: Boolean get() = true
    private var nodeDepth = 0
    override fun startNode() {
        nodeDepth++
    }

    override fun endNode() {
        nodeDepth--
    }

    override fun startGroup(key: Any?) {}
    override fun endGroup() {}
    override fun changed(value: Any?): Boolean = true
    override fun updateValue(value: Any?) {
        slots[currentSlot] = value
    }

    override fun nextSlot() {
        currentSlot++
    }

    override fun getSlot(): Any? = slots.getOrPut(currentSlot) { null }
    override fun setSlot(value: Any?) {
        slots[currentSlot] = value
    }

    override fun recordRead(state: Any) {}
    override fun recordWrite(state: Any) {}
    override fun reportChanged() {}
    override fun registerDisposable(disposable: () -> Unit) {}
    override fun dispose() {}
    override fun startCompose() {
        startNode()
    }

    override fun endCompose() {
        endNode()
    }

    override fun <T> compose(composable: @Composable () -> T): T {
        startCompose()
        val result = composable()
        endCompose()
        return result
    }
    
    override fun recompose() {
        // Test implementation
    }
    
    override fun rememberedValue(key: Any): Any? {
        return slots[key.hashCode()]
    }
    
    override fun updateRememberedValue(key: Any, value: Any?) {
        slots[key.hashCode()] = value
    }
}

// Helper to run composable in test environment (moved from component tests)
// Note: This is kept for backward compatibility. New tests should use TestSetupUtils.
@Deprecated(
    "Use runBasicComposableTest from TestSetupUtils instead",
    ReplaceWith("runBasicComposableTest(renderer, block = block)", "code.yousef.summon.util.runBasicComposableTest")
)
internal fun runTestComposable(renderer: PlatformRenderer, block: @Composable () -> Unit) {
    CompositionLocal.provideComposer(TestComposer()) {
        val provider = LocalPlatformRenderer.provides(renderer)
        provider.current // Access current
        block()
    }
} 