package code.yousef.example.todo.components

import code.yousef.example.todo.design.*
import code.yousef.example.todo.design.ModifierExtensions.buttonSize
import code.yousef.example.todo.design.ModifierExtensions.typography
import code.yousef.example.todo.models.FormState
import code.yousef.example.todo.models.Todo
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.input.Form
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.AlignItems
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.ModifierExtras.withAttribute
import code.yousef.summon.modifier.alignItems
import code.yousef.summon.modifier.textDecoration

/**
 * Component for displaying a single todo item with actions
 */
@Composable
fun TodoItem(
    todo: Todo,
    isEditing: Boolean = false,
    modifier: Modifier = Modifier()
) {
    Row(
        modifier = modifier
            .padding(Spacing.MD.value)
            .backgroundColor(SemanticColor.SURFACE.lightValue)
            .borderRadius(BorderRadius.MD.value)
            .alignItems(AlignItems.Center)
            .style("gap", Spacing.SM.value)
    ) {
        // Clickable completion checkbox as individual form
        Form(
            onSubmit = { /* SSR handled by server routes */ },
            modifier = Modifier()
                .withAttribute("action", "/todos/${todo.id}/toggle")
                .withAttribute("method", "post")
        ) {
            Button(
                onClick = { /* Form submission */ },
                label = if (todo.completed) "✅" else "⬜",
                variant = ButtonVariant.LINK,
                modifier = Modifier()
                    .withAttribute("type", "submit")
                    .buttonSize(ButtonSize.SMALL)
            )
        }

        // Todo text
        Text(
            text = todo.text,
            modifier = Modifier()
                .style("flex", "1")
                .typography(TextSize.BASE, FontWeight.NORMAL)
                .then(
                    if (todo.completed) {
                        Modifier()
                            .textDecoration("line-through", null)
                            .opacity(0.6f)
                    } else {
                        Modifier()
                    }
                )
        )

        // Action buttons
        Row(
            modifier = Modifier()
                .style("gap", Spacing.XS.value)
                .alignItems(AlignItems.Center)
        ) {
            // Toggle completion button as individual form
            Form(
                onSubmit = { /* SSR handled by server routes */ },
                modifier = Modifier()
                    .withAttribute("action", "/todos/${todo.id}/toggle")
                    .withAttribute("method", "post")
            ) {
                Button(
                    onClick = { /* Form submission */ },
                    label = if (todo.completed) "Undo" else "Complete",
                    variant = ButtonVariant.SECONDARY,
                    modifier = Modifier()
                        .buttonSize(ButtonSize.SMALL)
                        .withAttribute("type", "submit")
                )
            }

            // Edit button - redirect to edit mode
            Button(
                onClick = { /* JavaScript redirect */ },
                label = "Edit",
                variant = ButtonVariant.SECONDARY,
                modifier = Modifier()
                    .buttonSize(ButtonSize.SMALL)
                    .withAttribute("onclick", "window.location.href='/?edit=${todo.id}'")
                    .withAttribute("type", "button")
            )

            // Delete button as individual form
            Form(
                onSubmit = { /* SSR handled by server routes */ },
                modifier = Modifier()
                    .withAttribute("action", "/todos/${todo.id}/delete")
                    .withAttribute("method", "post")
            ) {
                Button(
                    onClick = { /* Form submission */ },
                    label = "Delete",
                    variant = ButtonVariant.DANGER,
                    modifier = Modifier()
                        .buttonSize(ButtonSize.SMALL)
                        .withAttribute("type", "submit")
                        .withAttribute("onclick", "return confirm('Delete this todo?')")
                )
            }
        }
    }
}

/**
 * Component for displaying a todo item in edit mode
 */
@Composable
fun EditableTodoItem(
    todo: Todo,
    formState: FormState = FormState.success(),
    modifier: Modifier = Modifier()
) {
    Row(
        modifier = modifier
            .padding(Spacing.MD.value)
            .backgroundColor(SemanticColor.SURFACE.lightValue)
            .borderRadius(BorderRadius.MD.value)
            .alignItems(AlignItems.Center)
            .style("gap", Spacing.SM.value)
    ) {
        // Completion status (not editable during text edit)
        Text(
            text = if (todo.completed) "✅" else "⬜",
            modifier = Modifier()
        )

        // Edit form for the todo text
        EditTodoForm(
            todoId = todo.id,
            currentText = todo.text,
            formState = formState,
            modifier = Modifier()
        )
    }
}