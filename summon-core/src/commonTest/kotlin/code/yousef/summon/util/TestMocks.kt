package code.yousef.summon.util

import code.yousef.summon.runtime.Composer
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.routing.Router
import code.yousef.summon.annotation.Composable

/**
 * Shared test mock implementations to reduce code duplication across test files.
 */

/**
 * A basic mock implementation of Composer for testing.
 * This provides the minimal implementation required by the Composer interface.
 */
open class MockComposer : Composer {
    override var inserting: Boolean = false
    
    // Track method calls for verification
    var nodeStartedCount = 0
    var nodeEndedCount = 0
    var groupStartedCount = 0
    var groupEndedCount = 0
    
    override fun startNode() {
        nodeStartedCount++
    }
    
    override fun startGroup(key: Any?) {
        groupStartedCount++
    }
    
    override fun endNode() {
        nodeEndedCount++
    }
    
    override fun endGroup() {
        groupEndedCount++
    }
    
    override fun changed(value: Any?): Boolean = true
    
    override fun updateValue(value: Any?) {}
    
    override fun nextSlot() {}
    
    override fun getSlot(): Any? = null
    
    override fun setSlot(value: Any?) {}
    
    override fun recordRead(state: Any) {}
    
    override fun recordWrite(state: Any) {}
    
    override fun reportChanged() {}
    
    override fun registerDisposable(disposable: () -> Unit) {}
    
    override fun recompose() {}
    
    override fun rememberedValue(key: Any): Any? = null
    
    override fun updateRememberedValue(key: Any, value: Any?) {}
    
    override fun dispose() {}
    
    override fun startCompose() {}
    
    override fun endCompose() {}
    
    override fun <T> compose(composable: @Composable () -> T): T {
        @Suppress("UNCHECKED_CAST")
        return null as T
    }
}

/**
 * An extended mock Composer that tracks more detailed information for testing.
 */
class DetailedMockComposer : MockComposer() {
    private val slots = mutableListOf<Any?>()
    private var currentSlotIndex = 0
    
    val trackedReads = mutableSetOf<Any>()
    val trackedWrites = mutableSetOf<Any>()
    val disposables = mutableListOf<() -> Unit>()
    
    var disposeCalled = false
    
    override fun nextSlot() {
        currentSlotIndex++
    }
    
    override fun getSlot(): Any? {
        return if (currentSlotIndex < slots.size) {
            slots[currentSlotIndex]
        } else {
            null
        }
    }
    
    override fun updateValue(value: Any?) {
        if (currentSlotIndex < slots.size) {
            slots[currentSlotIndex] = value
        } else {
            slots.add(value)
        }
    }
    
    override fun setSlot(value: Any?) {
        if (currentSlotIndex < slots.size) {
            slots[currentSlotIndex] = value
        } else {
            slots.add(value)
        }
    }
    
    override fun recordRead(state: Any) {
        trackedReads.add(state)
    }
    
    override fun recordWrite(state: Any) {
        trackedWrites.add(state)
    }
    
    override fun reportChanged() {}
    
    override fun registerDisposable(disposable: () -> Unit) {
        disposables.add(disposable)
    }
    
    override fun recompose() {}
    
    override fun rememberedValue(key: Any): Any? = null
    
    override fun updateRememberedValue(key: Any, value: Any?) {}
    
    override fun dispose() {
        disposeCalled = true
        disposables.forEach { it() }
        disposables.clear()
    }
    
    override fun startCompose() {}
    
    override fun endCompose() {}
    
    override fun <T> compose(composable: @Composable () -> T): T {
        @Suppress("UNCHECKED_CAST")
        return null as T
    }
    
    fun disposeAll() {
        dispose()
    }
}

/**
 * A mock implementation of Router for testing navigation.
 */
open class MockRouter(
    override val currentPath: String = "/test"
) : Router {
    var lastNavigatedPath: String? = null
    var lastPushState: Boolean = true
    var createCalled = false
    
    override fun navigate(path: String, pushState: Boolean) {
        lastNavigatedPath = path
        lastPushState = pushState
    }
    
    @Composable
    override fun create(initialPath: String) {
        createCalled = true
    }
}

/**
 * Common validation error messages used across tests.
 * These now delegate to the centralized ValidationMessages.
 */
object TestValidationMessages {
    const val REQUIRED_FIELD = code.yousef.summon.validation.ValidationMessages.REQUIRED_FIELD
    const val INVALID_EMAIL = code.yousef.summon.validation.ValidationMessages.INVALID_EMAIL
    const val MIN_LENGTH = code.yousef.summon.validation.ValidationMessages.MIN_LENGTH_TEMPLATE
    const val MAX_LENGTH = code.yousef.summon.validation.ValidationMessages.MAX_LENGTH_TEMPLATE
    const val INVALID_FORMAT = code.yousef.summon.validation.ValidationMessages.INVALID_FORMAT
    const val MUST_BE_NUMBER = code.yousef.summon.validation.ValidationMessages.MUST_BE_NUMBER
    const val MUST_BE_POSITIVE = code.yousef.summon.validation.ValidationMessages.MUST_BE_POSITIVE
}

/**
 * Common test setup utilities
 */
object TestSetup {
    /**
     * Creates a basic test environment with mock composer and renderer
     */
    fun createBasicTestEnvironment(): TestEnvironment {
        return TestEnvironment(
            composer = MockComposer(),
            renderer = code.yousef.summon.runtime.MockPlatformRenderer()
        )
    }
    
    /**
     * Creates a detailed test environment for more complex testing scenarios
     */
    fun createDetailedTestEnvironment(): DetailedTestEnvironment {
        return DetailedTestEnvironment(
            composer = DetailedMockComposer(),
            renderer = code.yousef.summon.runtime.MockPlatformRenderer()
        )
    }
}

/**
 * Container for basic test environment components
 */
data class TestEnvironment(
    val composer: MockComposer,
    val renderer: code.yousef.summon.runtime.MockPlatformRenderer
)

/**
 * Container for detailed test environment components
 */
data class DetailedTestEnvironment(
    val composer: DetailedMockComposer,
    val renderer: code.yousef.summon.runtime.MockPlatformRenderer
)