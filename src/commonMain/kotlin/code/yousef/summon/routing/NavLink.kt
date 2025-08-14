package code.yousef.summon.routing

import code.yousef.summon.components.navigation.Link
import code.yousef.summon.modifier.EventModifiers.onClick
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal


// Removed placeholder Router object

/**
 * A special type of Link that interacts with the Router to navigate
 * within a single-page application without a full page reload.
 *
 * It determines if the link is currently active based on the Router's state.
 *
 * @param to The target path for navigation (e.g., "/about", "/users/123").
 * @param modifier Modifier applied to the underlying Link component.
 * @param activeModifier Additional modifier applied only when this link is active.
 * @param exact If true, the link is only active if the current path exactly matches `to`.
 *              If false, it's active if the current path starts with `to`.
 * @param content The content to display within the link (e.g., Text).
 */
@Composable
fun NavLink(
    to: String,
    modifier: Modifier = Modifier(),
    activeModifier: Modifier = Modifier(),
    exact: Boolean = false,
    content: @Composable () -> Unit
) {
    val composer = CompositionLocal.currentComposer
    // Access router via CompositionLocal
    val router = LocalRouter

    // Determine if this link is active
    val currentPath = router?.currentPath ?: "" // Get the current path from the router
    val isActive = if (router != null) {
        if (exact) {
            currentPath == to
        } else {
            currentPath.startsWith(to)
        }
    } else {
        false
    }

    // Combine base modifier with active modifier if necessary
    val finalModifier = if (isActive) modifier.then(activeModifier) else modifier

    Link(
        href = to,
        modifier = finalModifier
            .onClick {
                // Implement client-side navigation
                if (router != null) {
                    // Prevent default browser navigation handled by platform
                    // event.preventDefault() removed
                    // Navigate using the router
                    router.navigate(to)
                }
                // If router is null, let the default link behavior handle it
            }
    ) {
        content()
    }
}

// Removed old NavLink class that implemented Composable 
