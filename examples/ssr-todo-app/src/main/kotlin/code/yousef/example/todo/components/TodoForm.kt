package code.yousef.example.todo.components

import code.yousef.example.todo.design.*
import code.yousef.example.todo.design.ModifierExtensions.buttonSize
import code.yousef.example.todo.design.ModifierExtensions.typography
import code.yousef.example.todo.models.FormState
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.input.Form
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.ModifierExtras.withAttribute

/**
 * Form component for creating new todos
 */
@Composable
fun CreateTodoForm(
    formState: FormState = FormState.success(),
    modifier: Modifier = Modifier()
) {
    Column(
        modifier = modifier
            .padding(Spacing.MD.value)
            .backgroundColor(SemanticColor.SURFACE.lightValue)
            .borderRadius(BorderRadius.MD.value)
            .style("gap", Spacing.SM.value)
    ) {
        // Show error message if present
        if (formState.hasError) {
            Text(
                text = formState.errorMessage!!,
                modifier = Modifier()
                    .padding(Spacing.SM.value)
                    .backgroundColor(SemanticColor.DANGER.lightValue + "20")
                    .color(SemanticColor.DANGER.lightValue)
                    .borderRadius(BorderRadius.SM.value)
                    .typography(TextSize.SM, FontWeight.MEDIUM)
            )
        }

        // Use Summon Form component with proper attributes for SSR form submission
        Form(
            onSubmit = { /* SSR handled by server routes */ },
            modifier = Modifier()
                .withAttribute("action", "/todos")
                .withAttribute("method", "post")
        ) {
            Row(
                modifier = Modifier()
                    .style("gap", Spacing.SM.value)
                    .withAttribute("style", "width: 100%")
            ) {
                TextField(
                    value = formState.formData["text"] ?: "",
                    onValueChange = { /* SSR - handled by form submission */ },
                    placeholder = "Add new todo...",
                    modifier = Modifier()
                        .withAttribute("name", "text")
                        .withAttribute("style", "flex: 1")
                        .padding(ButtonSize.MEDIUM.padding)
                        .borderRadius(BorderRadius.MD.value)
                        .typography(TextSize.BASE, FontWeight.NORMAL)
                )

                Button(
                    onClick = { /* Form submission handled by server */ },
                    label = "Add Todo",
                    variant = ButtonVariant.PRIMARY,
                    modifier = Modifier()
                        .buttonSize(ButtonSize.MEDIUM)
                        .withAttribute("type", "submit")
                )
            }
        }
    }
}

/**
 * Form component for editing existing todos
 */
@Composable
fun EditTodoForm(
    todoId: Int,
    currentText: String,
    formState: FormState = FormState.success(),
    modifier: Modifier = Modifier()
) {
    Column(
        modifier = modifier
            .style("gap", Spacing.SM.value)
    ) {
        // Show error message if present
        if (formState.hasError) {
            Text(
                text = formState.errorMessage!!,
                modifier = Modifier()
                    .padding(Spacing.SM.value)
                    .backgroundColor(SemanticColor.DANGER.lightValue + "20")
                    .color(SemanticColor.DANGER.lightValue)
                    .borderRadius(BorderRadius.SM.value)
                    .typography(TextSize.SM, FontWeight.MEDIUM)
            )
        }

        // Edit form with proper action for SSR
        Form(
            onSubmit = { /* SSR handled by server routes */ },
            modifier = Modifier()
                .withAttribute("action", "/todos/$todoId/edit")
                .withAttribute("method", "post")
        ) {
            Row(
                modifier = Modifier()
                    .style("gap", Spacing.XS.value)
                    .withAttribute("style", "width: 100%")
            ) {
                TextField(
                    value = formState.formData["text"] ?: currentText,
                    onValueChange = { /* SSR - handled by form submission */ },
                    modifier = Modifier()
                        .withAttribute("name", "text")
                        .withAttribute("style", "flex: 1")
                        .padding(ButtonSize.SMALL.padding)
                        .borderRadius(BorderRadius.MD.value)
                        .typography(TextSize.BASE, FontWeight.NORMAL)
                )

                Button(
                    onClick = { /* Form submission */ },
                    label = "Update",
                    variant = ButtonVariant.PRIMARY,
                    modifier = Modifier()
                        .buttonSize(ButtonSize.SMALL)
                        .withAttribute("type", "submit")
                )

                Button(
                    onClick = { /* JavaScript redirect */ },
                    label = "Cancel",
                    variant = ButtonVariant.SECONDARY,
                    modifier = Modifier()
                        .buttonSize(ButtonSize.SMALL)
                        .withAttribute("type", "button")
                        .withAttribute("onclick", "window.location.href='/'")
                )
            }
        }
    }
}