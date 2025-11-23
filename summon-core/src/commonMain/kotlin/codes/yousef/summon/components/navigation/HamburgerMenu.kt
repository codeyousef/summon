package codes.yousef.summon.components.navigation

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.MaterialIcon
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.ModifierExtras.onClick
import codes.yousef.summon.modifier.ModifierExtras.withAttribute
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf

/**
 * A responsive hamburger menu component that toggles visibility of content.
 *
 * This component uses a div-based button (Box) to ensure no form submission or page refresh occurs.
 * It includes proper accessibility attributes to function as a button for screen readers.
 *
 * @param modifier Modifier to apply to the container
 * @param iconColor Color of the hamburger icon
 * @param menuContent The content to display when the menu is open
 */
@Composable
fun HamburgerMenu(
    modifier: Modifier = Modifier(),
    iconColor: String = "#333333",
    menuContent: @Composable () -> Unit
) {
    val isOpen = remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Hamburger Button
        // We use a Box (div) instead of a button element to guarantee no default form submission behavior
        // causing page refreshes. We add accessibility roles to make it behave like a button for AT.
        Box(
            modifier = Modifier()
                .cursor(Cursor.Pointer)
                .padding("8px")
                .display(Display.Flex)
                .alignItems(AlignItems.Center)
                .justifyContent(JustifyContent.Center)
                // Accessibility
                .role("button")
                .withAttribute("tabindex", "0")
                .withAttribute("data-test-id", "hamburger-button")
                .ariaLabel(if (isOpen.value) "Close menu" else "Open menu")
                .withAttribute("aria-expanded", isOpen.value.toString())
                // Event handling
                .onClick { 
                    isOpen.value = !isOpen.value 
                }
                // Add key handler for keyboard accessibility (Enter/Space) if needed, 
                // though onClick often handles this in some frameworks, explicit handling is safer for divs
                .withAttribute("onkeydown", "if(event.key==='Enter'||event.key===' '){event.preventDefault();this.click();}")
        ) {
            MaterialIcon(
                name = if (isOpen.value) "close" else "menu",
                modifier = Modifier()
                    .fontSize("24px")
                    .color(iconColor)
                    .style("user-select", "none") // Prevent text selection
            )
        }

        // Menu Content
        if (isOpen.value) {
            Box(
                modifier = Modifier()
                    .fillMaxWidth()
                    .style("z-index", "1000")
            ) {
                menuContent()
            }
        }
    }
}
