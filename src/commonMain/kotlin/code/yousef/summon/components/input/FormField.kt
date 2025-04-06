package code.yousef.summon.components.input

import code.yousef.summon.core.UIElement
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.display.Text

/**
 * A layout composable that wraps an input field (or other content)
 * with a label, helper text, and error message display.
 *
 * @param fieldContent The main content of the form field, typically an input composable like [TextField].
 * @param modifier Optional [Modifier] for the entire FormField layout.
 * @param label Optional composable lambda to display the label above the field content.
 * @param helperText Optional composable lambda for helper text displayed below the field when not in error state.
 * @param errorMessage Optional composable lambda for error message displayed below the field when [isError] is true.
 * @param isError Controls whether the error message or helper text is displayed.
 */
@Composable
fun FormField(
    fieldContent: @Composable () -> Unit,
    modifier: Modifier = Modifier(),
    label: @Composable (() -> Unit)? = null,
    helperText: @Composable (() -> Unit)? = null,
    errorMessage: @Composable (() -> Unit)? = null,
    isError: Boolean = false
) {
    Column(modifier = modifier) {
        label?.invoke()

        fieldContent()

        if (isError && errorMessage != null) {
            errorMessage()
        } else if (!isError && helperText != null) {
            helperText()
        }
    }
} 