package codes.yousef.summon.components.navigation

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

/**
 * A responsive hamburger menu component that toggles visibility of content.
 *
 * This component uses a native button element to ensure accessibility and proper behavior.
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
    val renderer = LocalPlatformRenderer.current

    Column(modifier = modifier) {
        // Hamburger Button
        // We use renderButton to ensure cross-platform compatibility (Wasm/JS) and proper event handling.
        renderer.renderButton(
            onClick = { isOpen.value = !isOpen.value },
            modifier = Modifier()
                .cursor(Cursor.Pointer)
                .padding("8px")
                .display(Display.Flex)
                .alignItems(AlignItems.Center)
                .justifyContent(JustifyContent.Center)
                // Reset button styles
                .style("background", "transparent")
                .style("border", "none")
                .style("outline", "none")
                .style("min-width", "40px")
                .style("min-height", "40px")
                // Accessibility
                .attribute("type", "button")
                .attribute("aria-label", if (isOpen.value) "Close menu" else "Open menu")
                .attribute("aria-expanded", isOpen.value.toString())
                .withAttribute("data-test-id", "hamburger-button")
        ) {
            MaterialIcon(
                name = if (isOpen.value) "close" else "menu",
                modifier = Modifier()
                    .fontSize("24px")
                    .style("user-select", "none"), // Prevent text selection
                color = iconColor
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
