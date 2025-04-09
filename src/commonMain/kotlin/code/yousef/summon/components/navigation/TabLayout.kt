package code.yousef.summon.components.navigation

import code.yousef.summon.components.LayoutComponent
import code.yousef.summon.core.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.PlatformRendererProviderLegacy.getRenderer
import kotlinx.html.TagConsumer

/**
 * Represents a single tab in a TabLayout.
 *
 * @param title The title text to display in the tab
 * @param content The content to display when this tab is selected
 * @param icon Optional icon to display alongside the title
 * @param isClosable Whether this tab can be closed by the user
 */
data class Tab(
    val title: String,
    val content: Composable,
    val icon: Composable? = null,
    val isClosable: Boolean = false
)

/**
 * A layout composable that displays a tabbed interface.
 * TabLayout allows organizing content into separate tabs that can be selected by the user.
 *
 * @param tabs The list of tabs to display
 * @param selectedTabIndex The index of the currently selected tab
 * @param onTabSelected Callback function that is invoked when a tab is selected
 * @param modifier The modifier to apply to this composable
 */
class TabLayout(
    val tabs: List<Tab>,
    val selectedTabIndex: Int = 0,
    val onTabSelected: ((Int) -> Unit)? = null,
    val modifier: Modifier = Modifier()
) : Composable, LayoutComponent {
    /**
     * Renders this TabLayout composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            getRenderer().renderTabLayout(
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = onTabSelected ?: {},
                modifier = modifier
            )
        }
        return receiver
    }
} 