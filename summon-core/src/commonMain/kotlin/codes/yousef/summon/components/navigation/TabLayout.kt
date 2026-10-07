package codes.yousef.summon.components.navigation

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.getPlatformRenderer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * One tab and its lazily rendered content.
 *
 * @property id stable tab identity
 * @property title visible tab label
 * @property content content rendered while selected
 * @property icon optional label icon
 * @property isClosable whether the UI may expose a close action
 */
data class Tab @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val title: String,
    val content: @Composable () -> Unit,
    val icon: (@Composable () -> Unit)? = null,
    val isClosable: Boolean = false
) {
    /** Convenience factories. */
    companion object {
        /**
         * Creates a Tab with generated ID for testing purposes
         */
        @OptIn(ExperimentalUuidApi::class)
        operator fun invoke(
            label: String,
            value: String
        ): Tab = Tab(
            id = Uuid.random(),
            title = label,
            content = { }
        )
    }
}

/**
 * # TabLayout
 *
 * A navigation component that organizes content into selectable tabs, providing
 * intuitive content switching and space-efficient information presentation.
 *
 * ## Overview
 *
 * TabLayout creates tabbed interfaces with:
 * - **Content organization** - Group related content into logical sections
 * - **Space efficiency** - Show multiple content areas in limited space
 * - **Navigation patterns** - Familiar tab-based navigation interface
 * - **Accessibility** - Full keyboard navigation and screen reader support
 * - **Customization** - Flexible styling and layout options
 *
 * ## Key Features
 *
 * ### Tab Management
 * - **Dynamic tabs** - Add, remove, and reorder tabs programmatically
 * - **Tab states** - Active, inactive, disabled, and loading states
 * - **Closable tabs** - Optional close buttons for user-closable tabs
 * - **Tab icons** - Support for icons alongside tab titles
 *
 * ### Navigation & UX
 * - **Smooth transitions** - Animated content switching
 * - **Keyboard navigation** - Arrow key navigation between tabs
 * - **Touch gestures** - Swipe navigation on mobile devices
 * - **Persistence** - Remember selected tab across sessions
 *
 * ## Basic Usage
 *
 * ### Simple Tabs
 * ```kotlin
 * @Composable
 * fun ContentTabs() {
 *     val tabs = listOf(
 *         Tab(Uuid.random(), "Overview") { OverviewContent() },
 *         Tab(Uuid.random(), "Details") { DetailsContent() },
 *         Tab(Uuid.random(), "Reviews") { ReviewsContent() }
 *     )
 *
 *     var selectedTab by remember { mutableStateOf(0) }
 *
 *     TabLayout(
 *         tabs = tabs,
 *         selectedTabIndex = selectedTab,
 *         onTabSelected = { selectedTab = it },
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .height("400px")
 *     )
 * }
 * ```
 *
 * ### Tabs with Icons
 * ```kotlin
 * @Composable
 * fun IconTabs() {
 *     val tabs = listOf(
 *         Tab(
 *             ID = Uuid.random(),
 *             title = "Dashboard",
 *             icon = { Icon(Icons.DASHBOARD) },
 *             content = { DashboardContent() }
 *         ),
 *         Tab(
 *             ID = Uuid.random(),
 *             title = "Analytics",
 *             icon = { Icon(Icons.CHART) },
 *             content = { AnalyticsContent() }
 *         ),
 *         Tab(
 *             ID = Uuid.random(),
 *             title = "Settings",
 *             icon = { Icon(Icons.SETTINGS) },
 *             content = { SettingsContent() }
 *         )
 *     )
 *
 *     TabLayout(
 *         tabs = tabs,
 *         selectedTabIndex = 0,
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .borderBottom("1px solid #e5e7eb")
 *     )
 * }
 * ```
 *
 * ### Closable Tabs
 * ```kotlin
 * @Composable
 * fun EditableTabs() {
 *     var tabs by remember {
 *         mutableStateOf(
 *             listOf(
 *                 Tab(
 *                     ID = Uuid.random(),
 *                     title = "Document 1",
 *                     isClosable = true,
 *                     content = { DocumentEditor("doc1") }
 *                 ),
 *                 Tab(
 *                     ID = Uuid.random(),
 *                     title = "Document 2",
 *                     isClosable = true,
 *                     content = { DocumentEditor("doc2") }
 *                 )
 *             )
 *         )
 *     }
 *
 *     var selectedTab by remember { mutableStateOf(0) }
 *
 *     TabLayout(
 *         tabs = tabs,
 *         selectedTabIndex = selectedTab,
 *         onTabSelected = { selectedTab = it },
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .height("500px")
 *     )
 * }
 * ```
 *
 * @param tabs The list of tabs to display
 * @param selectedTabIndex The index of the currently selected tab
 * @param onTabSelected Callback function that is invoked when a tab is selected
 * @param modifier The modifier to apply to this composable
 *
 * @see Tab for tab data structure
 * @see Link for navigation links
 * @see codes.yousef.summon.components.input.Button for tab-like buttons
 * @see codes.yousef.summon.components.layout.Column for vertical tab layouts
 *
 *
 * @since 1.0.0
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
