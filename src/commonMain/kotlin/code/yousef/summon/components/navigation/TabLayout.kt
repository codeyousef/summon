package code.yousef.summon.components.navigation

import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal

/**
 * Represents a single tab in a TabLayout.
 *
 * @param title The title text to display in the tab.
 * @param icon Optional composable lambda for an icon displayed alongside the title.
 * @param isClosable Whether this tab can be closed by the user (TODO: Needs renderer/state support).
 * @param content The composable lambda defining the content to display when this tab is selected.
 */
data class Tab(
    val title: String,
    val icon: (@Composable () -> Unit)? = null,
    val isClosable: Boolean = false, // TODO: Handle closable tabs
    val content: @Composable () -> Unit
)

/**
 * A layout composable that displays a tabbed interface.
 * Organizes content into selectable tabs.
 *
 * @param tabs The list of `Tab` data objects defining the tabs.
 * @param selectedTabIndex The index of the currently selected tab.
 * @param onTabSelected Callback invoked when a tab is selected by the user.
 * @param modifier Modifier applied to the TabLayout container.
 */
@Composable
fun TabLayout(
    tabs: List<Tab>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier()
) {
    // TODO: Validate selectedTabIndex?
    if (tabs.isEmpty()) return // Don't render if no tabs
    val validSelectedIndex = selectedTabIndex.coerceIn(tabs.indices)

    // TODO: Replace PlatformRendererProvider with CompositionLocal access
    val renderer = PlatformRendererProvider.getRenderer()

    // TODO: Update renderer signature or adapt data passed.
    // The renderer likely needs info from `tabs` (titles, icons?) to build the tab bar.
    // It should handle clicks on tabs and call `onTabSelected`.
    // We pass the current index and the callback.
    renderer.renderTabLayout(
        tabs = tabs, // Assuming renderer can handle List<Tab> or needs adaptation
        selectedTabIndex = validSelectedIndex,
        onTabSelected = onTabSelected,
        modifier = modifier
    )

    // --- Content Composition --- 
    // Compose the content of the *selected* tab. 
    // This happens within the context potentially set up by the renderer.
    // TODO: Ensure composition context places this content correctly.
    if (validSelectedIndex in tabs.indices) {
        tabs[validSelectedIndex].content()
    }
    // --- End Content Composition ---
} 