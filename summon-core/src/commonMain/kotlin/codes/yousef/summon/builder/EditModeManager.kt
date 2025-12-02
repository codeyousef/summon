package codes.yousef.summon.builder

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.state.SummonMutableState
import codes.yousef.summon.state.mutableStateOf

/**
 * Composition local for edit mode state.
 *
 * In edit mode:
 * - Clicks select components instead of triggering navigation
 * - Components display selection borders
 * - Drag-and-drop is enabled
 *
 * In preview mode:
 * - Normal component behavior
 * - Navigation works as expected
 * - No selection UI
 */
val LocalEditMode = CompositionLocal.compositionLocalOf(false)

/**
 * Edit/Preview mode manager for visual builder.
 *
 * Controls whether the application is in edit mode (for building)
 * or preview mode (for testing the built UI).
 *
 * ## Features
 *
 * - **Mode Toggle**: Switch between edit and preview modes
 * - **Reactive State**: Mode changes trigger recomposition
 * - **Component Behavior**: Components check mode to adjust behavior
 *
 * ## Usage
 *
 * ```kotlin
 * // At app root
 * EditModeProvider {
 *     // Your app content
 * }
 *
 * // In components
 * @Composable
 * fun NavigationLink(href: String, text: String) {
 *     val isEditMode = LocalEditMode.current
 *
 *     val onClick = if (isEditMode) {
 *         { SelectionManager.select(nodeId) }
 *     } else {
 *         { Router.navigate(href) }
 *     }
 *
 *     Link(href, onClick = onClick) {
 *         Text(text)
 *     }
 * }
 *
 * // Toggle mode
 * Button(onClick = { EditModeManager.toggleMode() }) {
 *     Text(if (EditModeManager.isEditMode.value) "Preview" else "Edit")
 * }
 * ```
 *
 * @since 1.0.0
 */
object EditModeManager {
    /**
     * Reactive state for edit mode.
     */
    val isEditMode: SummonMutableState<Boolean> = mutableStateOf(false)
    
    /**
     * Callback invoked when mode changes.
     */
    var onModeChange: ((isEditMode: Boolean) -> Unit)? = null
    
    /**
     * Enables edit mode.
     */
    fun enableEditMode() {
        if (!isEditMode.value) {
            isEditMode.value = true
            onModeChange?.invoke(true)
        }
    }
    
    /**
     * Enables preview mode (disables edit mode).
     */
    fun enablePreviewMode() {
        if (isEditMode.value) {
            isEditMode.value = false
            SelectionManager.clearSelection()
            onModeChange?.invoke(false)
        }
    }
    
    /**
     * Toggles between edit and preview modes.
     */
    fun toggleMode() {
        isEditMode.value = !isEditMode.value
        if (!isEditMode.value) {
            SelectionManager.clearSelection()
        }
        onModeChange?.invoke(isEditMode.value)
    }
}

/**
 * Provides edit mode context to child components.
 *
 * This composable updates the LocalEditMode composition local with the current
 * edit mode state before rendering child content.
 *
 * @param initialEditMode Initial mode state (applied once on first call)
 * @param content Child components that can access LocalEditMode.current
 */
@Composable
fun EditModeProvider(
    initialEditMode: Boolean = false,
    content: @Composable () -> Unit
) {
    // Initialize edit mode state if this is the first call
    if (initialEditMode && !EditModeManager.isEditMode.value) {
        EditModeManager.enableEditMode()
    }
    
    // Update the composition local with current state
    LocalEditMode.provides(EditModeManager.isEditMode.value)
    
    // Render content
    content()
}
