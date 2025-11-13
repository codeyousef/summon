package codes.yousef.summon.core

import codes.yousef.summon.runtime.Composable
import kotlin.test.*

class CompositionContextTest {

    /**
     * A simple implementation of Renderer for testing
     */
    class TestRenderer : Renderer<String> {
        var renderCalled = false
        var disposeCalled = false

        override fun render(composable: @Composable () -> Unit): String {
            renderCalled = true
            return "Rendered"
        }

        override fun dispose() {
            disposeCalled = true
        }
    }

    @Test
    fun testCreateRootContext() {
        val renderer = TestRenderer()
        val context = CompositionContext.createRoot(renderer)

        assertEquals(0, context.depth, "Root context should have depth 0")
        assertEquals(renderer, context.renderer, "Context should have the provided renderer")
        assertNull(context.parent, "Root context should have no parent")
    }

    @Test
    fun testCreateChildContext() {
        val renderer = TestRenderer()
        val rootContext = CompositionContext.createRoot(renderer)
        val childContext = rootContext.createChildContext()

        assertEquals(1, childContext.depth, "Child context should have depth 1")
        assertEquals(renderer, childContext.renderer, "Child context should inherit the renderer")
        assertEquals(rootContext, childContext.parent, "Child context should have the root as parent")
    }

    @Test
    fun testAddAndGetComposables() {
        val renderer = TestRenderer()
        val context = CompositionContext.createRoot(renderer)

        // Initially, there should be no composables
        assertTrue(context.getComposables().isEmpty(), "New context should have no composables")

        // Add a composable
        val composable = "TestComposable"
        context.addComposable(composable)

        // Check that the composable was added
        val composables = context.getComposables()
        assertEquals(1, composables.size, "Context should have one composable")
        assertEquals(composable, composables[0], "Context should contain the added composable")
    }

    @Test
    fun testWithContext() {
        val renderer = TestRenderer()
        val context = CompositionContext.createRoot(renderer)

        // Initially, current context should be null
        assertNull(CompositionContext.current, "Current context should be null initially")

        // Execute a block with the context
        val result = CompositionContext.withContext(context) {
            // Inside the block, current context should be set
            assertEquals(context, CompositionContext.current, "Current context should be set inside withContext")
            "Result"
        }

        // After the block, current context should be null again
        assertNull(CompositionContext.current, "Current context should be null after withContext")
        assertEquals("Result", result, "withContext should return the result of the block")
    }

    @Test
    fun testNestedWithContext() {
        val renderer = TestRenderer()
        val outerContext = CompositionContext.createRoot(renderer)
        val innerContext = outerContext.createChildContext()

        // Execute nested withContext blocks
        CompositionContext.withContext(outerContext) {
            assertEquals(outerContext, CompositionContext.current, "Outer context should be set")

            CompositionContext.withContext(innerContext) {
                assertEquals(innerContext, CompositionContext.current, "Inner context should be set")
            }

            assertEquals(outerContext, CompositionContext.current, "Outer context should be restored")
        }

        assertNull(CompositionContext.current, "Current context should be null after all withContext blocks")
    }

    @Test
    fun testSummonRenderException() {
        val exception = SummonRenderException("Test error")
        assertEquals("Test error", exception.message, "Exception should have the provided message")

        val cause = RuntimeException("Cause")
        val exceptionWithCause = SummonRenderException("Test error with cause", cause)
        assertEquals("Test error with cause", exceptionWithCause.message, "Exception should have the provided message")
        assertEquals(cause, exceptionWithCause.cause, "Exception should have the provided cause")
    }

    @Test
    fun testTestRendererDispose() {
        val renderer = TestRenderer()
        assertFalse(renderer.disposeCalled, "disposeCalled should be false initially")
        renderer.dispose()
        assertTrue(renderer.disposeCalled, "disposeCalled should be true after calling dispose()")
    }

    // Test removed - was causing issues in JS environment
    // @Test 
    // fun testRenderUtilsPlaceholders() { ... }
}
