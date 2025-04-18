package code.yousef.summon.components.navigation

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.getPlatformRenderer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents a single tab in a TabLayout.
 *
 * @param title The title text to display in the tab
 * @param content The content to display when this tab is selected
 * @param icon Optional icon to display alongside the title
 * @param isClosable Whether this tab can be closed by the user
 */
data class Tab @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val title: String,
    val content: @Composable () -> Unit,
    val icon: (@Composable () -> Unit)? = null,
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
@Composable
fun TabLayout(
    tabs: List<Tab>,
    selectedTabIndex: Int = 0,
    onTabSelected: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier()
) {
    getPlatformRenderer().renderTabLayout(
        tabs = tabs,
        selectedTabIndex = selectedTabIndex,
        onTabSelected = onTabSelected ?: {},
        modifier = modifier
    )
}
