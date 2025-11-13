package codes.yousef.summon.runtime

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
    }

    /**
     * Discover actual element IDs by searching for elements with specific text content
     */
    fun discoverActualElementIds(): Boolean {
        try {
            // Find all span elements and check their text content
            val spanElementIds = wasmQuerySelectorAllGetIds("span")
            if (spanElementIds.isBlank()) {
                return false
            }

            val elementIds = spanElementIds.split(",").map { it.trim() }

            var todoCountFound = false
            var todoListFound = false

            for (elementId in elementIds) {
                val textContent = wasmGetElementTextContent(elementId) ?: continue

                when {
                    textContent.contains("Loading todos...") -> {
                        todoCountElement = elementId
                        todoCountFound = true
                    }

                    textContent.contains("Initializing...") -> {
                        // Find the parent container (Column) that contains this element
                        val parentId = wasmGetElementParentId(elementId)
                        if (parentId != null) {
                            todoListContainer = parentId
                            todoListFound = true
                        }
                    }
                }

                if (todoCountFound && todoListFound) {
                    break
                }
            }

            return todoCountFound && todoListFound

        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Add a todo item directly to the DOM without triggering recomposition
     */
    fun addTodo(todoText: String) {
        if (todoText.isBlank()) return

        currentTodos.add(todoText)
        updateTodoListDOM()
        updateTodoCountDOM()
        clearInputField()
    }

    /**
     * Remove a todo item by index
     */
    fun removeTodo(index: Int) {
        if (index >= 0 && index < currentTodos.size) {
            currentTodos.removeAt(index)
            updateTodoListDOM()
            updateTodoCountDOM()
        }
    }

    /**
     * Clear all todos
     */
    fun clearAllTodos() {
        currentTodos.clear()
        updateTodoListDOM()
        updateTodoCountDOM()
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

