package code.yousef.summon.effects

import code.yousef.summon.runtime.Composable

/**
 * Simplified dummy scope for testing.
 * This class is used by multiple test files to avoid duplication.
 */
class TestCompositionScope : CompositionScope {
    var composeCallCount = 0
    var lastComposedBlock: (@Composable () -> Unit)? = null
    
    override fun compose(block: @Composable () -> Unit) {
        composeCallCount++
        lastComposedBlock = block
        // Don't actually execute the block in tests
    }
}