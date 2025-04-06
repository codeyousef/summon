package code.yousef.summon.components.input

import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal

/**
 * A composable that represents a semantic form container.
 * It provides the structure (e.g., `<form>` tag) and handles the submit action trigger.
 * Form state (input values) and validation logic should be managed externally (hoisted state).
 *
 * @param onSubmit Lambda invoked when the form submission is triggered (e.g., by a submit button or Enter key).
 *                 The lambda should contain the logic for reading hoisted state, validation, and data submission.
 * @param modifier Modifier applied to the form container.
 * @param content The composable content of the form (e.g., TextFields, Buttons).
 */
@Composable
fun Form(
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    // TODO: Replace PlatformRendererProvider with CompositionLocal access
    val renderer = PlatformRendererProvider.getRenderer()

    // TODO: Renderer signature update required.
    // The renderer needs to create the <form> element and associate the onSubmit lambda
    // with the form's submit event (e.g., onsubmit="...").
    // It likely needs to prevent the default browser form submission.
    renderer.renderForm(
        onSubmit = onSubmit,
        modifier = modifier
    )

    // --- Content Rendering --- 
    // Execute the content lambda to compose the form fields and buttons
    // inside the structure created by the renderer.
    // TODO: Ensure composition context places content correctly within the form.
    content()
}

// The old Form class and its methods (registerField, validate, submit) are removed.
// State management and validation should be handled by the caller using hoisted state. 