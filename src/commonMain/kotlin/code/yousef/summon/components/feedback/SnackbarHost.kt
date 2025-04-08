package code.yousef.summon.components.feedback

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Data class representing a Snackbar message to be displayed
 */
data class SnackbarData(
    val message: String,
    val variant: SnackbarVariant = SnackbarVariant.DEFAULT,
    val action: String? = null,
    val onAction: (() -> Unit)? = null,
    val duration: Duration = 4000.milliseconds,
    val id: String = "snackbar-${Random.nextInt(100000)}"
)

/**
 * State holder for managing snackbars in a SnackbarHost
 */
class SnackbarHostState {
    private val _snackbars = mutableStateOf<List<SnackbarData>>(emptyList())
    val snackbars: List<SnackbarData>
        get() = _snackbars.value

    /**
     * Show a snackbar with the given data
     *
     * @param message The message to display
     * @param variant The style variant of the snackbar
     * @param action Optional action text
     * @param onAction Optional callback for when the action is clicked
     * @param duration How long to display the snackbar before automatically dismissing
     * @return The ID of the shown snackbar
     */
    fun showSnackbar(
        message: String,
        variant: SnackbarVariant = SnackbarVariant.DEFAULT,
        action: String? = null,
        onAction: (() -> Unit)? = null,
        duration: Duration = 4000.milliseconds
    ): String {
        val snackbar = SnackbarData(
            message = message,
            variant = variant,
            action = action,
            onAction = onAction,
            duration = duration
        )
        _snackbars.value = _snackbars.value + snackbar

        return snackbar.id
    }

    /**
     * Remove a specific snackbar by ID
     *
     * @param id The ID of the snackbar to remove
     */
    fun removeSnackbar(id: String) {
        _snackbars.value = _snackbars.value.filter { it.id != id }
    }

    /**
     * Remove all snackbars
     */
    fun clearAll() {
        _snackbars.value = emptyList()
    }
}

/**
 * Remember a SnackbarHostState
 *
 * @return A remembered SnackbarHostState instance
 */
@Composable
fun rememberSnackbarHostState(): SnackbarHostState {
    return remember { SnackbarHostState() }
}

/**
 * A composable that hosts and displays multiple snackbars
 *
 * @param hostState The state object that controls snackbar visibility
 * @param modifier Modifier to be applied to the snackbar host
 * @param horizontalPosition Horizontal alignment of snackbars
 * @param verticalPosition Vertical alignment of snackbars
 */
@Composable
fun SnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier(),
    horizontalPosition: SnackbarHorizontalPosition = SnackbarHorizontalPosition.CENTER,
    verticalPosition: SnackbarVerticalPosition = SnackbarVerticalPosition.BOTTOM
) {
    val snackbars = hostState.snackbars

    // Display each snackbar in the state
    for (snackbar in snackbars) {
        Snackbar(
            message = snackbar.message,
            variant = snackbar.variant,
            action = snackbar.action,
            onAction = {
                snackbar.onAction?.invoke()
                hostState.removeSnackbar(snackbar.id)
            },
            onDismiss = {
                hostState.removeSnackbar(snackbar.id)
            },
            duration = snackbar.duration,
            horizontalPosition = horizontalPosition,
            verticalPosition = verticalPosition,
            modifier = modifier
        )
    }
}

/**
 * Example usage of SnackbarHost:
 *
 * @Composable
 * fun MyScreen() {
 *     val snackbarHostState = rememberSnackbarHostState()
 *
 *     Button(onClick = {
 *         snackbarHostState.showSnackbar(
 *             message = "Operation completed successfully",
 *             variant = SnackbarVariant.SUCCESS,
 *             action = "Undo",
 *             onAction = { /* Undo logic */ }
 *         )
 *     }) {
 *         Text("Show Snackbar")
 *     }
 *
 *     // Add SnackbarHost at the root of your composable hierarchy
 *     SnackbarHost(hostState = snackbarHostState)
 * }
 */ 