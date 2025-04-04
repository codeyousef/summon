package code.yousef.summon

import kotlinx.html.TagConsumer

/**
 * A composable that wraps form controls with common functionality like labels and validation messages.
 * @param label Optional label to display for the form field
 * @param fieldContent The form control to wrap
 * @param helper Optional helper text to display below the field
 * @param error Optional error message to display
 * @param required Whether the field is required
 * @param modifier The modifier to apply to this composable
 */
class FormField(
    val label: String? = null,
    val fieldContent: Composable,
    val helper: String? = null,
    val error: String? = null,
    val required: Boolean = false,
    val modifier: Modifier = Modifier()
) : Composable {
    /**
     * Renders this FormField composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderFormField(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 