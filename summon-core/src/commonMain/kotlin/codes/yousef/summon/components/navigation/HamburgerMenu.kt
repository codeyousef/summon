package codes.yousef.summon.components.navigation

import codes.yousef.summon.action.UiAction
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.MaterialIcon
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.ModifierExtras.onClick
import codes.yousef.summon.modifier.ModifierExtras.withAttribute
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * A responsive hamburger menu component that toggles visibility of content.
 *
 * This component uses client-side toggle via data-action to avoid server round-trips.
 * The menu content is always rendered but hidden initially, allowing pure client-side toggling.
 *
 * @param modifier Modifier to apply to the container
 * @param iconColor Color of the hamburger icon
 * @param menuContent The content to display when the menu is open
 */
@Composable
fun HamburgerMenu(
    modifier: Modifier = Modifier(),
    iconColor: String? = null,
    menuContent: @Composable () -> Unit
) {
    val isOpen = remember { mutableStateOf(false) }
    HamburgerMenu(
        modifier = modifier,
        isOpen = isOpen.value,
        onToggle = { isOpen.value = !isOpen.value },
        iconColor = iconColor,
        menuContent = menuContent
    )
}

// Counter for generating unique menu IDs
private var menuIdCounter = 0

/**
 * A responsive hamburger menu component that toggles visibility of content.
 * Controlled version where state is managed by the caller.
 *
 * This component uses client-side toggle via data-action to avoid server round-trips
 * that would cause page refreshes. The menu content is always rendered but hidden
 * when closed, allowing the GlobalEventListener to toggle visibility purely client-side.
 *
 * @param modifier Modifier to apply to the container
 * @param isOpen Whether the menu is currently open
 * @param onToggle Callback when the menu toggle button is clicked (used for client-side state)
 * @param iconColor Color of the hamburger icon
 * @param menuContent The content to display when the menu is open
 */
@Composable
fun HamburgerMenu(
    modifier: Modifier = Modifier(),
    isOpen: Boolean,
    onToggle: () -> Unit,
    iconColor: String? = null,
    menuContent: @Composable () -> Unit
) {
    val renderer = LocalPlatformRenderer.current

    // Generate unique ID for this menu's content
    val menuContentId = remember { "hamburger-menu-${++menuIdCounter}" }

    // Serialize the toggle action for client-side handling
    val toggleAction = UiAction.ToggleVisibility(menuContentId)
    val actionJson = Json.encodeToString(toggleAction)

    Column(modifier = modifier) {
        // Hamburger Button
        // We use a div (Box) with role="button" instead of a native button element.
        // This is to absolutely guarantee that no browser default behavior (like form submission or page refresh)
        // can be triggered, which was reported as an issue even with type="button".
        //
        // The data-action attribute contains a serialized UiAction.ToggleVisibility which is
        // handled purely client-side by GlobalEventListener -> ClientDispatcher without any
        // server round-trip.
        renderer.renderBox(
            modifier = Modifier()
                .cursor(Cursor.Pointer)
                .padding("8px")
                .display(Display.Flex)
                .alignItems(AlignItems.Center)
                .justifyContent(JustifyContent.Center)
                .style("min-width", "40px")
                .style("min-height", "40px")
                // Accessibility
                .attribute("role", "button")
                .attribute("tabindex", "0")
                .attribute("aria-label", if (isOpen) "Close menu" else "Open menu")
                .attribute("aria-expanded", isOpen.toString())
                .attribute("aria-controls", menuContentId)
                .withAttribute("data-test-id", "hamburger-button")
                // Client-side toggle action - no server round-trip needed
                .attribute("data-action", actionJson)
                .attribute("data-hamburger-toggle", "true")
        ) {
            MaterialIcon(
                name = if (isOpen) "close" else "menu",
                modifier = Modifier()
                    .fontSize("24px")
                    .style("user-select", "none"), // Prevent text selection
                color = iconColor
            )
        }

        // Menu Content - ALWAYS rendered but hidden when closed
        // This allows client-side toggling via GlobalEventListener without server round-trips.
        // The display style is toggled by ClientDispatcher.dispatch(UiAction.ToggleVisibility).
        Box(
            modifier = Modifier()
                .fillMaxWidth()
                .style("z-index", "1000")
                .attribute("id", menuContentId)
                // Initially hidden if not open; ClientDispatcher will toggle this
                .style("display", if (isOpen) "block" else "none")
        ) {
            menuContent()
        }
    }
}
