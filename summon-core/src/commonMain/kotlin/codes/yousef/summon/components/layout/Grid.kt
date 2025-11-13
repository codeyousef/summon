package codes.yousef.summon.components.layout

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.getPlatformRenderer

/**
 * # Grid
 *
 * A powerful two-dimensional layout component using CSS Grid that provides precise control
 * over rows, columns, and item placement. Grid is ideal for complex layouts that require
 * both horizontal and vertical alignment control.
 *
 * ## Overview
 *
 * Grid creates sophisticated layouts with:
 * - **Two-dimensional control** - Manage both rows and columns simultaneously
 * - **Flexible sizing** - Use fr units, fixed sizes, auto, and minmax() functions
 * - **Grid areas** - Name regions for semantic item placement
 * - **Responsive design** - Adapt layouts based on screen size and container queries
 * - **Gap control** - Consistent spacing between grid items
 *
 * ## Key Features
 *
 * ### Layout Capabilities
 * - **Explicit grids** - Define specific row and column tracks
 * - **Implicit grids** - Auto-generate tracks for overflow content
 * - **Grid areas** - Named regions for semantic layout
 * - **Item placement** - Precise control over child positioning
 * - **Alignment** - Grid-level and item-level alignment options
 *
 * ## Basic Usage
 *
 * ### Simple Grid
 * ```kotlin
 * @Composable
 * fun SimpleGrid() {
 *     Grid(
 *         columns = "repeat(3, 1fr)",
 *         gap = "1rem",
 *         modifier = Modifier().padding(Spacing.MD)
 *     ) {
 *         repeat(6) { index ->
 *             Card(
 *                 modifier = Modifier()
 *                     .backgroundColor(Color.BLUE_100)
 *                     .padding(Spacing.MD)
 *                     .borderRadius("8px")
 *             ) {
 *                 Text("Item $index")
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ### Responsive Grid
 * ```kotlin
 * @Composable
 * fun ResponsiveGrid() {
 *     Grid(
 *         columns = "repeat(auto-fit, minmax(250px, 1fr))",
 *         gap = "1.5rem",
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .padding(Spacing.LG)
 *     ) {
 *         gridItems.forEach { item ->
 *             GridItem(item)
 *         }
 *     }
 * }
 * ```
 *
 * ### Named Grid Areas
 * ```kotlin
 * @Composable
 * fun LayoutGrid() {
 *     Grid(
 *         columns = "1fr 3fr 1fr",
 *         rows = "auto 1fr auto",
 *         areas = """
 *             "header header header"
 *             "sidebar main aside"
 *             "footer footer footer"
 *         """.trimIndent(),
 *         gap = "1rem",
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .height("100vh")
 *     ) {
 *         Box(modifier = Modifier().gridArea("header")) { Header() }
 *         Box(modifier = Modifier().gridArea("sidebar")) { Sidebar() }
 *         Box(modifier = Modifier().gridArea("main")) { MainContent() }
 *         Box(modifier = Modifier().gridArea("aside")) { AsideContent() }
 *         Box(modifier = Modifier().gridArea("footer")) { Footer() }
 *     }
 * }
 * ```
 *
 * ## Advanced Layouts
 *
 * ### Dashboard Grid
 * ```kotlin
 * @Composable
 * fun DashboardGrid() {
 *     Grid(
 *         columns = "repeat(12, 1fr)",
 *         rows = "repeat(4, minmax(200px, auto))",
 *         gap = "1rem",
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .padding(Spacing.LG)
 *     ) {
 *         // Large chart - spans 8 columns, 2 rows
 *         Card(
 *             modifier = Modifier()
 *                 .gridColumn("1 / 9")
 *                 .gridRow("1 / 3")
 *                 .padding(Spacing.MD)
 *         ) {
 *             ChartComponent()
 *         }
 *
 *         // Stats cards - each spans 2 columns
 *         repeat(3) { index ->
 *             Card(
 *                 modifier = Modifier()
 *                     .gridColumn("${9 + (index * 2)} / ${11 + (index * 2)}")
 *                     .gridRow("1 / 2")
 *                     .padding(Spacing.SM)
 *             ) {
 *                 StatCard(stats[index])
 *             }
 *         }
 *
 *         // Data table - spans full width
 *         Card(
 *             modifier = Modifier()
 *                 .gridColumn("1 / -1")
 *                 .gridRow("3 / 5")
 *                 .padding(Spacing.MD)
 *         ) {
 *             DataTable()
 *         }
 *     }
 * }
 * ```
 *
 * ### Photo Gallery
 * ```kotlin
 * @Composable
 * fun PhotoGallery(photos: List<Photo>) {
 *     Grid(
 *         columns = "repeat(auto-fill, minmax(200px, 1fr))",
 *         gap = "0.5rem",
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .padding(Spacing.MD)
 *             .attribute("data-masonry", "true")
 *     ) {
 *         photos.forEach { photo ->
 *             Card(
 *                 modifier = Modifier()
 *                     .borderRadius("8px")
 *                     .overflow(Overflow.HIDDEN)
 *                     .aspectRatio(photo.aspectRatio)
 *                     .hover {
 *                         transform("scale(1.05)")
 *                         transition("transform 0.3s ease")
 *                     }
 *             ) {
 *                 Image(
 *                     src = photo.url,
 *                     alt = photo.description,
 *                     modifier = Modifier()
 *                         .width(Width.FULL)
 *                         .height(Height.FULL)
 *                         .objectFit(ObjectFit.COVER)
 *                 )
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @param columns The number of columns or a template string (e.g. "1fr 2fr 1fr" or "repeat(3, 1fr)")
 * @param rows The number of rows or a template string (e.g. "auto 1fr auto" or "repeat(2, minmax(100px, auto))")
 * @param gap The gap between grid items (e.g. "10px" or "10px 20px" for row/column gaps)
 * @param areas Optional grid template areas definition
 * @param modifier The modifier to apply to this composable
 * @param content The composable content to display inside the grid
 *
 * @see Column for single-column layouts
 * @see Row for single-row layouts
 * @see LazyColumn for virtualized vertical lists
 * @see LazyRow for virtualized horizontal lists
 *
 * @sample GridSamples.simpleGrid
 * @sample GridSamples.responsiveGrid
 * @sample GridSamples.namedAreas
 * @sample GridSamples.dashboardLayout
 *
 * @since 1.0.0
 */
@Composable
fun Grid(
    columns: String,
    rows: String = "auto",
    gap: String = "0",
    areas: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    // Prepare the modifier with grid styles
    val gridStyles = mutableMapOf(
        "display" to "grid",
        "grid-template-columns" to columns,
        "grid-template-rows" to rows,
        "gap" to gap
    )
    areas?.let { gridStyles["grid-template-areas"] = it }

    // Create a new modifier that preserves all existing styles and attributes
    val finalModifier = Modifier(modifier.styles + gridStyles, modifier.attributes)

    getPlatformRenderer().renderGrid(
        modifier = finalModifier,
        content = content
    )
}
