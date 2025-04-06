package code.yousef.summon.components.feedback

import code.yousef.summon.modifier.Modifier


/**
 * Builder class for creating Alert components with a fluent API.
 * This reduces the complexity of calling code when there are many parameters.
 */
class AlertBuilder {
    var message: String = ""
    var title: String? = null
    var type: AlertType = AlertType.INFO
    var isDismissible: Boolean = false
    var iconName: String? = null
    var onDismiss: (() -> Unit)? = null
    var actionText: String? = null
    var onAction: (() -> Unit)? = null
    var modifier: Modifier = Modifier()

    /**
     * Builds the Alert component with the configured parameters.
     *
     * @return An Alert instance
     */
    fun build(): Alert = Alert(
        message = message,
        title = title,
        type = type,
        isDismissible = isDismissible,
        iconName = iconName,
        onDismiss = onDismiss,
        actionText = actionText,
        onAction = onAction,
        modifier = modifier
    )
}

/**
 * DSL-style function for creating alerts with a builder pattern.
 *
 * @param block Lambda with receiver to configure the AlertBuilder
 * @return The configured Alert component
 */
fun alert(block: AlertBuilder.() -> Unit): Alert {
    val builder = AlertBuilder()
    builder.block()
    return builder.build()
}

/**
 * Example usage:
 *
 * val myAlert = alert {
 *     message = "Operation completed successfully!"
 *     type = AlertType.SUCCESS
 *     isDismissible = true
 *     actionText = "View Details"
 *     onAction = { /* open details screen */ }
 * }
 */ 