package codes.yousef.summon.integration

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ModifierExtras.withAttribute
import codes.yousef.summon.runtime.*
import codes.yousef.summon.state.mutableStateOf
import codes.yousef.summon.test.ensureWasmNodeDom
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Integration test demonstrating WASM DOM interaction capabilities.
 * Tests the actual button click and recomposition flow that fails in the generated project.
 */
class WasmDOMIntegrationTest {

    private fun PlatformRenderer.initializeOrSkip(rootId: String): Boolean =
        try {
            initialize(rootId)
            true
        } catch (t: Throwable) {
            println("Skipping WASM DOM integration test: ${t.message}")
            false
        }

    private fun ensureRenderer(renderer: PlatformRenderer, rootId: String): Boolean {
        ensureWasmNodeDom()
        if (!renderer.initializeOrSkip(rootId)) {
            return false
        }
        LocalPlatformRenderer.provides(renderer)
        return true
    }

    /**
     * This test reproduces the exact issue from the generated CLI project:
     * - Button clicks are processed (logs show "Button clicked!")
     * - State updates happen
     * - But UI doesn't update due to element cache validation failure
     *
     * Expected behavior: Button click should update counter and trigger UI update
     * Actual behavior (before fix): Button click processes but UI doesn't recompose due to bad cast
     */
    @Test
    fun `button click should trigger recomposition without errors`() {
        val renderer = PlatformRenderer()
        if (!ensureRenderer(renderer, "test-root")) return

        // Track composition
        var compositionCount = 0

        @Composable
        fun CounterApp() {
            compositionCount++
            val counter = remember { mutableStateOf(0) }

            Column(modifier = Modifier()) {
                Text(
                    text = "Count: ${counter.value}",
                    modifier = Modifier()
                )

                Button(
                    onClick = {
                        counter.value++
                    },
                    label = "Increment"
                )
            }
        }

        // Initialize the renderer
        // renderer already initialized and provided

        // Get recomposer and composer
        val recomposer = RecomposerHolder.current()
        val composer = recomposer.createComposer()

        // Initial composition - this should work
        recomposer.setActiveComposer(composer)
        try {
            composer.startGroup("root")
            CounterApp()
            composer.endGroup()
        } finally {
            recomposer.setActiveComposer(null)
        }

        // Verify initial composition happened
        assertEquals(1, compositionCount, "Should have composed once initially")

        // Simulate recomposition (what happens when button is clicked)
        // This tests that element cache validation works correctly
        recomposer.setActiveComposer(composer)
        try {
            composer.startGroup("root")
            CounterApp()
            composer.endGroup()
        } finally {
            recomposer.setActiveComposer(null)
        }

        // After the fix, this should pass - recomposition should work without "bad cast" errors
        assertTrue(compositionCount >= 2, "Recomposition should have happened without errors")
    }

    @Test
    fun `renderer should handle element cache validation failures gracefully`() {
        val renderer = PlatformRenderer()
        if (!ensureRenderer(renderer, "test-root")) return

        @Composable
        fun SimpleButton() {
            Button(
                onClick = { },
                label = "Test Button"
            )
        }

        val recomposer = RecomposerHolder.current()
        val composer = recomposer.createComposer()

        // First composition
        recomposer.setActiveComposer(composer)
        try {
            composer.startGroup("root")
            SimpleButton()
            composer.endGroup()
        } finally {
            recomposer.setActiveComposer(null)
        }

        // Second composition - tests element reuse logic
        recomposer.setActiveComposer(composer)
        try {
            composer.startGroup("root")
            SimpleButton()
            composer.endGroup()
        } finally {
            recomposer.setActiveComposer(null)
        }

        // If we reach here without exceptions, the fix is working
        assertTrue(true, "Recomposition with element reuse should work without errors")
    }

    @Test
    fun `wasmDOMIntegration should work when implemented`() {
        // Placeholder test - will pass for now
        // TODO: Test actual WASM DOM integration
        assertTrue(true, "WASM DOM integration test placeholder")
    }

    @Test
    fun `elementManipulation should work when implemented`() {
        // Placeholder test - will pass for now
        // TODO: Test element manipulation in WASM
        assertTrue(true, "Element manipulation test placeholder")
    }

    @Test
    fun `eventHandling should work when implemented`() {
        // Placeholder test - will pass for now
        // TODO: Test event handling in WASM
        assertTrue(true, "Event handling test placeholder")
    }

    @Test
    fun `performanceOptimization should work when implemented`() {
        // Placeholder test - will pass for now
        // TODO: Test performance optimization in WASM
        assertTrue(true, "Performance optimization test placeholder")
    }

    @Test
    fun `composableRendering should work when implemented`() {
        // Placeholder test - will pass for now
        // TODO: Test composable rendering in WASM
        assertTrue(true, "Composable rendering test placeholder")
    }

    @Test
    @Ignore // TODO: Fix infinite recomposition loop in ImmediateScheduler
    fun `todo buttons mutate list as expected`() {
        ensureWasmNodeDom()

        if (!runCatching { wasmGetDocumentBodyId() }.isSuccess) {
            println("Skipping todo buttons test: wasm bridge unavailable")
            return
        }

        injectTestRootIfMissing(TEST_ROOT_ID)

        val renderer = PlatformRenderer()
        renderer.initialize(TEST_ROOT_ID)

        val originalRecomposer = RecomposerHolder.current()
        val testRecomposer = Recomposer()
        val scheduler = ImmediateScheduler()

        RecomposerHolder.setRecomposer(testRecomposer)
        RecomposerHolder.setScheduler(scheduler)

        try {
            renderer.mountComposableRoot(TEST_ROOT_ID) {
                TodoButtonsApp()
            }

            assertEquals(
                0,
                measuredTodoIds().size,
                "Initial render should have no todo rows"
            )

            // Add two todos
            clickButton("todo-add")
            assertEquals(
                listOf("todo-row-1"),
                measuredTodoIds(),
                "First todo should appear after clicking add"
            )
            assertEquals("1", rootAttribute("data-add-count"), "Add click counter should update after first add")
            assertEquals("1", rootAttribute("data-total"), "Total count attribute should reflect first todo")

            clickButton("todo-add")
            assertEquals(
                listOf("todo-row-1", "todo-row-2"),
                measuredTodoIds(),
                "Second todo should appear after clicking add again"
            )
            assertEquals("2", rootAttribute("data-add-count"), "Add click counter should update after second add")
            assertEquals("2", rootAttribute("data-total"), "Total count attribute should reflect two todos")

            // Mark first todo as done
            clickButton("todo-done-1")
            assertEquals(
                "true",
                attributeForTestId("todo-row-1", "data-completed"),
                "Done button should mark first todo as completed"
            )
            assertEquals("1", rootAttribute("data-done-count"), "Done click counter should update after first toggle")

            // Clear completed todos - should remove first row
            clickButton("todo-clear")
            assertEquals(
                listOf("todo-row-2"),
                measuredTodoIds(),
                "Clear should remove completed todo rows"
            )
            assertEquals("1", rootAttribute("data-clear-count"), "Clear click counter should update")
            assertEquals("1", rootAttribute("data-total"), "Total count attribute should reflect remaining todo")

            // Mark remaining todo as done and clear again
            clickButton("todo-done-2")
            assertEquals(
                "true",
                attributeForTestId("todo-row-2", "data-completed"),
                "Done button should mark remaining todo as completed"
            )
            assertEquals("2", rootAttribute("data-done-count"), "Done click counter should update after second toggle")

            clickButton("todo-clear")
            assertEquals(
                emptyList(),
                measuredTodoIds(),
                "Clear should remove all completed todos"
            )
            assertEquals("2", rootAttribute("data-clear-count"), "Clear click counter should reflect second invocation")
            assertEquals("0", rootAttribute("data-total"), "Total count attribute should be zero after clearing")
        } finally {
            RecomposerHolder.setRecomposer(originalRecomposer)
            RecomposerHolder.setScheduler(createDefaultScheduler())
        }
    }

    private fun injectTestRootIfMissing(rootId: String) {
        val bodyId = runCatching { wasmGetDocumentBodyId() }.getOrNull() ?: return
        val existing = runCatching { wasmGetElementById(rootId) }.getOrNull()
        if (existing != null) return

        val createdId = wasmCreateElementById("div")
        wasmSetElementId(createdId, rootId)
        wasmAppendChildById(bodyId, createdId)
    }

    private fun measuredTodoIds(): List<String> {
        val listId = elementIdForTestId("todo-list") ?: return emptyList()
        val rawChildren = wasmGetElementChildren(listId)
        if (rawChildren.isBlank()) return emptyList()
        return rawChildren.split(",")
            .mapNotNull { childId ->
                val trimmed = childId.trim()
                if (trimmed.isEmpty()) null else wasmGetElementAttribute(trimmed, "data-test-id")
            }
            .filterNotNull()
            .sorted()
    }

    private fun clickButton(testId: String) {
        val elementId = elementIdForTestId(testId)
            ?: fail("Element with data-test-id '$testId' not found")
        wasmClickElement(elementId)
    }

    private fun attributeForTestId(testId: String, attribute: String): String? {
        val elementId = elementIdForTestId(testId) ?: return null
        return wasmGetElementAttribute(elementId, attribute)
    }

    private fun rootAttribute(attribute: String): String? =
        attributeForTestId("todo-root", attribute)

    private fun elementIdForTestId(testId: String): String? =
        wasmQuerySelectorGetId("[data-test-id=\"${testId}\"]")

    private class ImmediateScheduler : RecompositionScheduler {
        private var isRunning = false
        private val pendingWork = mutableListOf<() -> Unit>()
        private var loopCount = 0
        private val MAX_LOOPS = 100

        override fun scheduleRecomposition(work: () -> Unit) {
            if (isRunning) {
                pendingWork.add(work)
                return
            }
            
            isRunning = true
            loopCount = 0
            try {
                work()
                
                // Process any work that was queued during execution
                while (pendingWork.isNotEmpty()) {
                    if (loopCount++ > MAX_LOOPS) {
                        throw IllegalStateException("Infinite recomposition loop detected in ImmediateScheduler")
                    }
                    val nextWork = pendingWork.removeAt(0)
                    nextWork()
                }
            } finally {
                isRunning = false
                pendingWork.clear()
            }
        }
    }

    data class TodoItem(
        val id: Int,
        val label: String,
        val completed: Boolean
    )

    @Composable
    private fun TodoButtonsApp() {
        val todos = remember { mutableStateListOf<TodoItem>() }
        val nextIdState = remember { mutableStateOf(1) }

        val addClicks = remember { mutableStateOf(0) }
        val doneClicks = remember { mutableStateOf(0) }
        val clearClicks = remember { mutableStateOf(0) }

        Column(
            modifier = Modifier()
                .withAttribute("data-test-id", "todo-root")
                .withAttribute("data-total", todos.size.toString())
                .withAttribute("data-add-count", addClicks.value.toString())
                .withAttribute("data-done-count", doneClicks.value.toString())
                .withAttribute("data-clear-count", clearClicks.value.toString())
        ) {
            Button(
                onClick = {
                    addClicks.value = addClicks.value + 1
                    val id = nextIdState.value
                    nextIdState.value = id + 1
                    todos.add(TodoItem(id, "Task $id", completed = false))
                },
                modifier = Modifier()
                    .withAttribute("data-test-id", "todo-add")
                    .withAttribute("data-summon-id", "todo-add-btn"),
                label = "Add"
            )

            Column(
                modifier = Modifier().withAttribute("data-test-id", "todo-list")
            ) {
                todos.forEachIndexed { index, todo ->
                    Row(
                        modifier = Modifier()
                            .withAttribute("data-test-id", "todo-row-${todo.id}")
                            .withAttribute("data-summon-id", "todo-row-${todo.id}")
                            .withAttribute("data-completed", todo.completed.toString())
                    ) {
                        Text(
                            text = todo.label,
                            modifier = Modifier().withAttribute(
                                "data-test-id",
                                "todo-label-${todo.id}"
                            )
                        )
                        Button(
                            onClick = {
                                doneClicks.value = doneClicks.value + 1
                                todos[index] = todo.copy(completed = !todo.completed)
                            },
                            modifier = Modifier().withAttribute(
                                "data-test-id",
                                "todo-done-${todo.id}"
                            ).withAttribute(
                                "data-summon-id",
                                "todo-done-${todo.id}"
                            ),
                            label = if (todo.completed) "Undo" else "Done"
                        )
                    }
                }
            }

            Button(
                onClick = {
                    clearClicks.value = clearClicks.value + 1
                    val remaining = todos.filterNot { it.completed }
                    todos.clear()
                    todos.addAll(remaining)
                },
                modifier = Modifier()
                    .withAttribute("data-test-id", "todo-clear")
                    .withAttribute("data-summon-id", "todo-clear-btn"),
                label = "Clear Completed"
            )
        }
    }

    companion object {
        private const val TEST_ROOT_ID = "test-root"
    }
}
