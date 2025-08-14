package code.yousef.summon.components.feedback

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * Builder class for creating Alert components with a fluent API.
 * This reduces the complexity of calling code when there are many parameters.
 */
class AlertBuilder {
    var message: String = ""
    var title: String? = null
    var variant: AlertVariant = AlertVariant.INFO
    var onDismiss: (() -> Unit)? = null
    var icon: (@Composable () -> Unit)? = null
    var actions: (@Composable () -> Unit)? = null
    var modifier: Modifier = Modifier()

    /**
     * Builds the Alert component with the configured parameters.
     *
     * @return An Alert component through the Alert function
     */
    fun build() {
        Alert(
            message = message,
            modifier = modifier,
            variant = variant,
            onDismiss = onDismiss,
            title = title,
            icon = icon,
            actions = actions
        )
    }
}

/**
 * DSL-style function for creating alerts with a builder pattern.
 *
 * @param block Lambda with receiver to configure the AlertBuilder
 */
fun alert(block: AlertBuilder.() -> Unit) {
    val builder = AlertBuilder()
    builder.block()
    builder.build()
}

/**
 * Example usage:
 *
 * alert {
 *     message = "Operation completed successfully!"
 *     variant = AlertVariant.SUCCESS
 *     onDismiss = { /* handle dismiss */ }
 *     actions = { Button(onClick = { /* handle action */ }, label = "View Details") }
 * }
 */ 