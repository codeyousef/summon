package code.yousef.summon.components.navigation

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider


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
    val composer = CompositionLocal.currentComposer
    if (tabs.isEmpty()) return
    val validSelectedIndex = selectedTabIndex.coerceIn(tabs.indices)

    composer?.startNode() // Start TabLayout node
    if (composer?.inserting == true) {
        val renderer = PlatformRendererProvider.getPlatformRenderer()
        // Renderer handles rendering the tab bar UI and potentially the content container
        renderer.renderTabLayout(
            tabs = tabs, 
            selectedTabIndex = validSelectedIndex,
            onTabSelected = onTabSelected,
            modifier = modifier
        )
    }

    // --- Content Composition --- 
    // Compose the content of the selected tab within the TabLayout node
    if (validSelectedIndex in tabs.indices) {
        tabs[validSelectedIndex].content()
    }
    // --- End Content Composition ---
    
    composer?.endNode() // End TabLayout node
} 
