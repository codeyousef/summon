package codes.yousef.example.todo.components

import codes.yousef.example.todo.models.FormState
import codes.yousef.example.todo.models.Todo
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier

/**
 * Component for displaying a list of todos
 */
@Composable
fun TodoList(
    todos: List<Todo>,
    editingTodoId: Int? = null,
    editFormState: FormState = FormState.success(),
    modifier: Modifier = Modifier()
) {
    Column(modifier = modifier) {
        if (todos.isEmpty()) {
            Text(
                text = "No todos yet. Add one above! ✨",
                modifier = Modifier()
            )
        } else {
            Text(
                text = "Your Todos",
                modifier = Modifier()
            )

            todos.forEach { todo ->
                if (editingTodoId == todo.id) {
                    // Show editable version
                    EditableTodoItem(
                        todo = todo,
                        formState = editFormState,
                        modifier = Modifier()
                    )
                } else {
                    // Show regular version
                    TodoItem(
                        todo = todo,
                        modifier = Modifier()
                    )
                }
            }
        }
    }
}

/**
 * Component for displaying todos grouped by completion status
 */
@Composable
fun GroupedTodoList(
    todos: List<Todo>,
    editingTodoId: Int? = null,
    editFormState: FormState = FormState.success(),
    modifier: Modifier = Modifier()
) {
    val (completedTodos, activeTodos) = todos.partition { it.completed }

    Column(modifier = modifier) {
        // Active todos first
        if (activeTodos.isNotEmpty()) {
            Text(
                text = "Active Todos (${activeTodos.size}):",
                modifier = Modifier()
            )

            activeTodos.forEach { todo ->
                if (editingTodoId == todo.id) {
                    EditableTodoItem(
                        todo = todo,
                        formState = editFormState,
                        modifier = Modifier()
                    )
                } else {
                    TodoItem(
                        todo = todo,
                        modifier = Modifier()
                    )
                }
            }
        }

        // Completed todos second
        if (completedTodos.isNotEmpty()) {
            Text(
                text = "Completed Todos (${completedTodos.size}):",
                modifier = Modifier()
            )

            completedTodos.forEach { todo ->
                if (editingTodoId == todo.id) {
                    EditableTodoItem(
                        todo = todo,
                        formState = editFormState,
                        modifier = Modifier()
                    )
                } else {
                    TodoItem(
                        todo = todo,
                        modifier = Modifier()
                    )
                }
            }
        }

        // Empty state
        if (todos.isEmpty()) {
            Text(
                text = "No todos yet. Add one above!",
                modifier = Modifier()
            )
        }
    }
}