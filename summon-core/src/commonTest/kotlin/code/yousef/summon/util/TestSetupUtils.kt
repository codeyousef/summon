package codes.yousef.summon.util

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.Composer
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.MockPlatformRenderer

/**
 * Comprehensive test setup utilities for Summon framework tests.
 * These utilities provide common test patterns and reduce boilerplate.
 */

/**
 * Runs a composable test with a basic mock setup.
 * This is the most common test pattern for simple component tests.
 * 
 * @param renderer Optional custom renderer. Defaults to MockPlatformRenderer.
 * @param composer Optional custom composer. Defaults to MockComposer.
 * @param block The composable code to test.
 * @return The renderer used, for further assertions.
 */
fun runComposableTest(
    renderer: MockPlatformRenderer = MockPlatformRenderer(),
    composer: Composer = MockComposer(),
    block: @Composable () -> Unit
): MockPlatformRenderer {
    CompositionLocal.provideComposer(composer) {
        val provider = LocalPlatformRenderer.provides(renderer)
        provider.current // Access current to ensure it's set
        block()
    }
    return renderer
}

/**
 * Runs a composable test with detailed tracking capabilities.
 * Useful for tests that need to track reads, writes, and disposables.
 * 
 * @param renderer Optional custom renderer. Defaults to MockPlatformRenderer.
 * @param block The composable code to test.
 * @return A pair of the renderer and detailed composer for assertions.
 */
fun runDetailedComposableTest(
    renderer: MockPlatformRenderer = MockPlatformRenderer(),
    block: @Composable () -> Unit
): Pair<MockPlatformRenderer, DetailedMockComposer> {
    val composer = DetailedMockComposer()
    CompositionLocal.provideComposer(composer) {
        val provider = LocalPlatformRenderer.provides(renderer)
        provider.current // Access current to ensure it's set
        block()
    }
    return renderer to composer
}

/**
 * Runs a composable test and automatically disposes resources.
 * Useful for testing lifecycle and cleanup behavior.
 * 
 * @param renderer Optional custom renderer. Defaults to MockPlatformRenderer.
 * @param block The composable code to test.
 * @return The renderer used, for further assertions.
 */
fun runComposableTestWithCleanup(
    renderer: MockPlatformRenderer = MockPlatformRenderer(),
    block: @Composable () -> Unit
): MockPlatformRenderer {
    val composer = DetailedMockComposer()
    try {
        CompositionLocal.provideComposer(composer) {
            val provider = LocalPlatformRenderer.provides(renderer)
            provider.current // Access current to ensure it's set
            block()
        }
    } finally {
        composer.dispose()
    }
    return renderer
}

/**
 * Sets up a test environment and runs multiple test scenarios.
 * Useful for parameterized tests or testing multiple states.
 * 
 * @param scenarios List of test scenarios to run.
 */
fun runMultipleTestScenarios(
    vararg scenarios: TestScenario
) {
    scenarios.forEach { scenario ->
        val renderer = MockPlatformRenderer()
        runComposableTest(renderer) {
            scenario.test()
        }
        scenario.verify(renderer)
    }
}

/**
 * Represents a test scenario with a test action and verification.
 */
data class TestScenario(
    val name: String,
    val test: @Composable () -> Unit,
    val verify: (MockPlatformRenderer) -> Unit
)

/**
 * Extension functions for common Modifier assertions in tests.
 */

/**
 * Checks if a modifier has a specific style property with the given value.
 */
fun Modifier.hasStyle(property: String, value: String): Boolean =
    styles[property] == value

/**
 * Checks if a modifier has a background style with the given color.
 * Handles various background property formats.
 */
fun Modifier.hasBackground(color: String): Boolean =
    hasStyle("background", color) || 
    hasStyle("background-color", color) ||
    hasStyle("background", "$color !important") || 
    hasStyle("background-color", "$color !important")

/**
 * Checks if a modifier has a color style with the given value.
 * Handles important declarations.
 */
fun Modifier.hasColor(color: String): Boolean =
    hasStyle("color", color) || hasStyle("color", "$color !important")

/**
 * Checks if a modifier has a specific attribute.
 */
fun Modifier.hasAttribute(name: String, value: String): Boolean =
    attributes[name] == value

/**
 * Checks if a modifier has any of the given classes.
 */
fun Modifier.hasClass(className: String): Boolean =
    attributes["class"]?.split(" ")?.contains(className) == true

/**
 * Common test data providers
 */
object TestData {
    val sampleTexts = listOf(
        "Hello World",
        "Test Text",
        "Lorem ipsum dolor sit amet",
        "",
        "   ",
        "Special chars: @#$%"
    )
    
    val sampleModifiers = listOf(
        Modifier(),
        Modifier().background("red").color("white"),
        Modifier().padding("10px").margin("5px"),
        Modifier().width("100%").height("200px")
    )
    
    val sampleClickHandlers = listOf<() -> Unit>(
        { },
        { println("Clicked!") },
        { throw RuntimeException("Test exception") }
    )
}

/**
 * Test environment setup helpers
 */
object TestEnvironmentSetup {
    /**
     * Resets all test mocks to their initial state.
     */
    fun resetMocks(vararg mocks: Any) {
        mocks.forEach { mock ->
            when (mock) {
                is MockPlatformRenderer -> {
                    // Reset all tracking properties
                    mock.renderTextCalled = false
                    mock.renderButtonCalled = false
                    mock.renderTextFieldCalled = false
                    mock.renderImageCalled = false
                    mock.renderBoxCalled = false
                    mock.renderRowCalled = false
                    mock.renderColumnCalled = false
                    mock.renderLinkCalled = false
                    mock.renderSelectCalled = false
                    mock.renderCheckboxCalled = false
                    // Add more resets as needed
                }
                is MockComposer -> {
                    mock.nodeStartedCount = 0
                    mock.nodeEndedCount = 0
                    mock.groupStartedCount = 0
                    mock.groupEndedCount = 0
                }
                is DetailedMockComposer -> {
                    mock.trackedReads.clear()
                    mock.trackedWrites.clear()
                    mock.disposables.clear()
                    mock.disposeCalled = false
                }
            }
        }
    }
    
    /**
     * Creates a test environment with pre-configured mocks.
     */
    fun createEnvironment(
        withDetailedComposer: Boolean = false,
        withRouter: Boolean = false
    ): TestEnvironmentContext {
        return TestEnvironmentContext(
            renderer = MockPlatformRenderer(),
            composer = if (withDetailedComposer) DetailedMockComposer() else MockComposer(),
            router = if (withRouter) MockRouter() else null
        )
    }
}

/**
 * Container for test environment components
 */
data class TestEnvironmentContext(
    val renderer: MockPlatformRenderer,
    val composer: Composer,
    val router: MockRouter? = null
) {
    /**
     * Runs a test within this environment context.
     */
    fun runTest(block: @Composable () -> Unit) {
        CompositionLocal.provideComposer(composer) {
            val provider = LocalPlatformRenderer.provides(renderer)
            provider.current // Access current to ensure it's set
            // If router is needed, it would be provided here too
            block()
        }
    }
    
    /**
     * Cleans up the test environment.
     */
    fun cleanup() {
        if (composer is DetailedMockComposer) {
            composer.dispose()
        }
    }
}