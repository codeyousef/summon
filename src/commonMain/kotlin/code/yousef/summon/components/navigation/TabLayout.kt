package code.yousef.summon.components.navigation

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.core.UIElement

/**
 * Represents a single tab in a TabLayout.
 *
 * @param title The title text to display in the tab.
 * @param icon Optional composable lambda for an icon.
 * @param isClosable Whether this tab can be closed by the user.
 * @param content The composable content lambda for this tab.
 */
data class Tab(
    val title: String,
    val icon: @Composable (() -> Unit)? = null,
    val isClosable: Boolean = false,
    val content: @Composable () -> Unit // Content is a lambda now
)

/**
 * A layout composable that displays a tabbed interface.
 * TabLayout allows organizing content into separate tabs that can be selected by the user.
 *
 * @param tabs The list of tabs to display.
 * @param modifier The modifier to apply to this composable.
 * @param selectedTabIndex The index of the currently selected tab.
 * @param onTabSelected Callback function invoked when a tab is selected.
 */
@Composable
fun TabLayout(
    tabs: List<Tab>,
    modifier: Modifier = Modifier(),
    selectedTabIndex: Int = 0, // State should likely be hoisted
    onTabSelected: (Int) -> Unit = {}
) {
    val tabLayoutData = TabLayoutData(
        tabs = tabs,
        modifier = modifier,
        selectedTabIndex = selectedTabIndex, // Pass current state
        onTabSelected = onTabSelected
    )

    // TODO: Implement rendering of tab headers, selection logic, and display of selected content.
    // Needs state management for selectedTabIndex and composer/renderer integration.
    println("Composable TabLayout function called with ${tabs.size} tabs, selected: $selectedTabIndex")

    // Placeholder: Using UIElement. Structure needs specific renderer handling for tabs/content.
    UIElement(
        factory = { tabLayoutData },
        update = { /* Update logic for selected tab change */ },
        content = {
            // Render tab headers (iterating tabs)
            // Render content of the selected tab (tabs[selectedTabIndex].content())
            if (tabs.isNotEmpty() && selectedTabIndex >= 0 && selectedTabIndex < tabs.size) {
                tabs[selectedTabIndex].content() // Execute content of selected tab
            }
        }
    )
}

/**
 * Internal data class holding parameters for TabLayout.
 */
internal data class TabLayoutData(
    val tabs: List<Tab>,
    val modifier: Modifier,
    val selectedTabIndex: Int,
    val onTabSelected: (Int) -> Unit
)

// Removed duplicate Tab data class definition 