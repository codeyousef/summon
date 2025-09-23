package code.yousef.summon.runtime

/**
 * DirectDOMRenderer bypasses the normal WASM recomposition system to avoid
 * "RuntimeError: bad cast" issues during recomposition. Instead of using the
 * complex composition system, it directly manipulates DOM elements when state changes.
 */
object DirectDOMRenderer {
    private var todoListContainer: String? = null
    private var todoCountElement: String? = null
    private var currentTodos = mutableListOf<String>()
    private var currentInputValue = ""

    /**
     * Initialize the direct DOM renderer with element IDs
     */
    fun initialize(todoListContainerId: String, todoCountElementId: String) {
        todoListContainer = todoListContainerId
        todoCountElement = todoCountElementId
        wasmConsoleLog("DirectDOMRenderer initialized with container: $todoListContainerId")
    }

    /**
     * Discover actual element IDs by searching for elements with specific text content
     */
    fun discoverActualElementIds(): Boolean {
        wasmConsoleLog("DirectDOMRenderer: Starting element discovery...")

        try {
            // Find all span elements and check their text content
            val spanElementIds = wasmQuerySelectorAllGetIds("span")
            if (spanElementIds.isBlank()) {
                wasmConsoleLog("DirectDOMRenderer: No span elements found")
                return false
            }

            val elementIds = spanElementIds.split(",").map { it.trim() }
            wasmConsoleLog("DirectDOMRenderer: Found ${elementIds.size} span elements to check")

            var todoCountFound = false
            var todoListFound = false

            for (elementId in elementIds) {
                val textContent = wasmGetElementTextContent(elementId) ?: continue
                wasmConsoleLog("DirectDOMRenderer: Checking element $elementId with text: '$textContent'")

                when {
                    textContent.contains("Loading todos...") -> {
                        wasmConsoleLog("DirectDOMRenderer: Found todo count element: $elementId")
                        todoCountElement = elementId
                        todoCountFound = true
                    }

                    textContent.contains("Initializing...") -> {
                        wasmConsoleLog("DirectDOMRenderer: Found todo list placeholder element: $elementId")
                        // Find the parent container (Column) that contains this element
                        val parentId = wasmGetElementParentId(elementId)
                        if (parentId != null) {
                            wasmConsoleLog("DirectDOMRenderer: Found todo list container: $parentId")
                            todoListContainer = parentId
                            todoListFound = true
                        }
                    }
                }

                if (todoCountFound && todoListFound) {
                    break
                }
            }

            if (todoCountFound && todoListFound) {
                wasmConsoleLog("DirectDOMRenderer: Successfully discovered elements - count: $todoCountElement, list: $todoListContainer")
                return true
            } else {
                wasmConsoleLog("DirectDOMRenderer: Failed to discover elements - count found: $todoCountFound, list found: $todoListFound")
                return false
            }

        } catch (e: Exception) {
            wasmConsoleLog("DirectDOMRenderer: Error during discovery: ${e.message}")
            return false
        }
    }

    /**
     * Add a todo item directly to the DOM without triggering recomposition
     */
    fun addTodo(todoText: String) {
        if (todoText.isBlank()) return

        wasmConsoleLog("DirectDOMRenderer: Adding todo '$todoText'")
        currentTodos.add(todoText)
        updateTodoListDOM()
        updateTodoCountDOM()
        clearInputField()
        wasmConsoleLog("DirectDOMRenderer: Todo added successfully, DOM updated")
    }

    /**
     * Remove a todo item by index
     */
    fun removeTodo(index: Int) {
        if (index >= 0 && index < currentTodos.size) {
            wasmConsoleLog("DirectDOMRenderer: Removing todo at index $index")
            currentTodos.removeAt(index)
            updateTodoListDOM()
            updateTodoCountDOM()
            wasmConsoleLog("DirectDOMRenderer: Todo removed successfully, DOM updated")
        }
    }

    /**
     * Clear all todos
     */
    fun clearAllTodos() {
        wasmConsoleLog("DirectDOMRenderer: Clearing all todos")
        currentTodos.clear()
        updateTodoListDOM()
        updateTodoCountDOM()
        wasmConsoleLog("DirectDOMRenderer: All todos cleared, DOM updated")
    }

    /**
     * Update input field value tracking
     */
    fun updateInputValue(value: String) {
        currentInputValue = value
    }

    /**
     * Get current input value
     */
    fun getCurrentInputValue(): String = currentInputValue

    /**
     * Update the todo list DOM elements directly
     */
    private fun updateTodoListDOM() {
        val containerId = todoListContainer ?: return

        // Clear existing content
        wasmSetElementInnerHTML(containerId, "")

        if (currentTodos.isEmpty()) {
            // Show empty message
            val emptyMessageHTML = """
                <div style="padding: 20px;">
                    No todos yet. Add one above! 👆
                </div>
            """.trimIndent()
            wasmSetElementInnerHTML(containerId, emptyMessageHTML)
        } else {
            // Create todo items HTML
            val todoItemsHTML = StringBuilder()
            currentTodos.forEachIndexed { index, todo ->
                todoItemsHTML.append(
                    """
                    <div style="padding: 8px; background: #f5f5f5; border-radius: 8px; margin: 0 0 8px 0; display: flex; justify-content: space-between; align-items: center;">
                        <span style="padding: 0 10px 0 0;">• $todo</span>
                        <button onclick="window.removeTodo($index)" style="background: #ff4444; color: white; border: none; border-radius: 4px; padding: 4px 8px; cursor: pointer;">✖</button>
                    </div>
                """.trimIndent()
                )
            }

            // Add clear all button if there are todos
            if (currentTodos.isNotEmpty()) {
                todoItemsHTML.append(
                    """
                    <button onclick="window.clearAllTodos()" style="margin: 20px 0 0 0; padding: 8px 16px; background: #666; color: white; border: none; border-radius: 4px; cursor: pointer;">
                        Clear All
                    </button>
                """.trimIndent()
                )
            }

            wasmSetElementInnerHTML(containerId, todoItemsHTML.toString())
        }
    }

    /**
     * Update the todo count display
     */
    private fun updateTodoCountDOM() {
        val countElementId = todoCountElement ?: return
        val countText = if (currentTodos.isEmpty()) {
            "No todos yet. Add one above! 👆"
        } else {
            "Your todos (${currentTodos.size}):"
        }
        wasmSetElementTextContent(countElementId, countText)
    }

    /**
     * Clear the input field directly
     */
    private fun clearInputField() {
        // Find the input element and clear it
        // We'll need to identify the input element ID from the logs
        val inputElementId = "wasm-elem-5" // From console logs, this is the TextField element
        wasmSetElementValue(inputElementId, "")
        currentInputValue = ""
    }
}

