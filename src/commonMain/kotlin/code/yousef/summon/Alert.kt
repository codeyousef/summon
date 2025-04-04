package code.yousef.summon

import kotlinx.html.TagConsumer

/**
 * Alert types for different semantic meanings
 */
enum class AlertType {
    INFO,       // Informational messages
    SUCCESS,    // Success messages
    WARNING,    // Warning messages
    ERROR,      // Error messages
    NEUTRAL     // Neutral messages without specific semantic meaning
}

/**
 * A composable that displays alert notifications and messages.
 *
 * @param message The main message text for the alert
 * @param modifier The modifier to apply to this composable
 * @param type The semantic type of the alert
 * @param title Optional title or header for the alert
 * @param isDismissible Whether the alert can be dismissed by the user
 * @param icon Optional icon to display with the alert
 * @param onDismiss Callback to invoke when the alert is dismissed
 * @param actionText Optional text for an action button
 * @param onAction Callback to invoke when the action button is clicked
 */
data class Alert(
    val message: String,
    val modifier: Modifier = Modifier(),
    val type: AlertType = AlertType.INFO,
    val title: String? = null,
    val isDismissible: Boolean = false,
    val icon: Icon? = null,
    val onDismiss: (() -> Unit)? = null,
    val actionText: String? = null,
    val onAction: (() -> Unit)? = null
) : Composable, TextComponent, LayoutComponent {
    /**
     * Renders this Alert composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return getPlatformRenderer().renderAlert(this, receiver as TagConsumer<T>)
        }
        return receiver
    }

    /**
     * Gets type-specific styles for the alert.
     */
    internal fun getTypeStyles(): Map<String, String> {
        return when (type) {
            AlertType.INFO -> mapOf(
                "background-color" to "#e3f2fd",
                "color" to "#0d47a1",
                "border-color" to "#bbdefb"
            )

            AlertType.SUCCESS -> mapOf(
                "background-color" to "#e8f5e9",
                "color" to "#1b5e20",
                "border-color" to "#c8e6c9"
            )

            AlertType.WARNING -> mapOf(
                "background-color" to "#fff3e0",
                "color" to "#e65100",
                "border-color" to "#ffe0b2"
            )

            AlertType.ERROR -> mapOf(
                "background-color" to "#ffebee",
                "color" to "#b71c1c",
                "border-color" to "#ffcdd2"
            )

            AlertType.NEUTRAL -> mapOf(
                "background-color" to "#f5f5f5",
                "color" to "#212121",
                "border-color" to "#e0e0e0"
            )
        }
    }

    /**
     * Gets accessibility attributes for the alert.
     */
    internal fun getAccessibilityAttributes(): Map<String, String> {
        val attributes = mutableMapOf<String, String>()

        // Set appropriate role
        attributes["role"] = "alert"

        // Set aria-live for screen readers
        attributes["aria-live"] = if (type == AlertType.ERROR) "assertive" else "polite"

        // If dismissible, add appropriate controls
        if (isDismissible) {
            attributes["aria-atomic"] = "true"
        }

        return attributes
    }
}

/**
 * Creates an info alert for informational messages.
 * @param message The main message text for the alert
 * @param title Optional title or header for the alert
 * @param isDismissible Whether the alert can be dismissed by the user
 * @param onDismiss Callback to invoke when the alert is dismissed
 * @param modifier The modifier to apply to this composable
 */
fun infoAlert(
    message: String,
    title: String? = null,
    isDismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier()
): Alert = Alert(
    message = message,
    modifier = modifier,
    type = AlertType.INFO,
    title = title,
    isDismissible = isDismissible,
    onDismiss = onDismiss
)

/**
 * Creates a success alert for success messages.
 * @param message The main message text for the alert
 * @param title Optional title or header for the alert
 * @param isDismissible Whether the alert can be dismissed by the user
 * @param onDismiss Callback to invoke when the alert is dismissed
 * @param modifier The modifier to apply to this composable
 */
fun successAlert(
    message: String,
    title: String? = null,
    isDismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier()
): Alert = Alert(
    message = message,
    modifier = modifier,
    type = AlertType.SUCCESS,
    title = title,
    isDismissible = isDismissible,
    onDismiss = onDismiss
)

/**
 * Creates a warning alert for warning messages.
 * @param message The main message text for the alert
 * @param title Optional title or header for the alert
 * @param isDismissible Whether the alert can be dismissed by the user
 * @param onDismiss Callback to invoke when the alert is dismissed
 * @param modifier The modifier to apply to this composable
 */
fun warningAlert(
    message: String,
    title: String? = null,
    isDismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier()
): Alert = Alert(
    message = message,
    modifier = modifier,
    type = AlertType.WARNING,
    title = title,
    isDismissible = isDismissible,
    onDismiss = onDismiss
)

/**
 * Creates an error alert for error messages.
 * @param message The main message text for the alert
 * @param title Optional title or header for the alert
 * @param isDismissible Whether the alert can be dismissed by the user
 * @param onDismiss Callback to invoke when the alert is dismissed
 * @param modifier The modifier to apply to this composable
 */
fun errorAlert(
    message: String,
    title: String? = null,
    isDismissible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier()
): Alert = Alert(
    message = message,
    modifier = modifier,
    type = AlertType.ERROR,
    title = title,
    isDismissible = isDismissible,
    onDismiss = onDismiss
) 