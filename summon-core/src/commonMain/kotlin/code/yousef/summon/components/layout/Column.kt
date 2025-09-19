package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Display
import code.yousef.summon.modifier.FlexDirection
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * # Column
 *
 * A layout component that arranges its children in a vertical sequence using CSS Flexbox.
 * Column provides powerful vertical layout capabilities with intuitive alignment and spacing controls.
 *
 * ## Overview
 *
 * Column is one of the fundamental layout components in Summon, providing vertical arrangement
 * of child components. It's built on CSS Flexbox and offers comprehensive control over:
 *
 * - **Vertical arrangement** - Distribute children along the vertical axis
 * - **Horizontal alignment** - Align children within the container width
 * - **Spacing management** - Control gaps between children
 * - **Responsive behavior** - Adapt to different screen sizes and constraints
 *
 * ## Key Features
 *
 * ### Arrangement Options
 * - **Top aligned** - Children start from the top (default)
 * - **Bottom aligned** - Children align to the bottom
 * - **Center aligned** - Children center vertically
 * - **Space between** - Equal space between children
 * - **Space around** - Equal space around each child
 * - **Space evenly** - Equal space between and around children
 *
 * ### Alignment Controls
 * - **Start aligned** - Children align to the start edge (default)
 * - **End aligned** - Children align to the end edge
 * - **Center aligned** - Children center horizontally
 * - **Stretch** - Children stretch to fill available width
 * - **Baseline** - Children align to text baseline
 *
 * ## Basic Usage
 *
 * ### Simple Vertical Stack
 * ```kotlin
 * @Composable
 * fun SimpleStack() {
 *     Column(
 *         modifier = Modifier()
 *             .padding(Spacing.MD)
 *             .width(Width.FULL)
 *     ) {
 *         Text("First item")
 *         Text("Second item")
 *         Text("Third item")
 *     }
 * }
 * ```
 *
 * ### Centered Content
 * ```kotlin
 * @Composable
 * fun CenteredColumn() {
 *     Column(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .height("400px")
 *             .alignItems(AlignItems.CENTER)
 *             .justifyContent(JustifyContent.CENTER)
 *             .padding(Spacing.LG)
 *     ) {
 *         Text("Centered Title", style = TextStyle.HEADING_2)
 *         Text("Centered subtitle", style = TextStyle.BODY_LARGE)
 *         Button(onClick = { }) {
 *             Text("Action Button")
 *         }
 *     }
 * }
 * ```
 *
 * ## Arrangement and Alignment
 *
 * ### Vertical Arrangement (Main Axis)
 * ```kotlin
 * @Composable
 * fun ArrangementExamples() {
 *     // Space between children
 *     Column(
 *         modifier = Modifier()
 *             .height("300px")
 *             .justifyContent(JustifyContent.SPACE_BETWEEN)
 *     ) {
 *         Text("Top")
 *         Text("Middle")
 *         Text("Bottom")
 *     }
 *
 *     // Space around children
 *     Column(
 *         modifier = Modifier()
 *             .height("300px")
 *             .justifyContent(JustifyContent.SPACE_AROUND)
 *     ) {
 *         Text("Item 1")
 *         Text("Item 2")
 *         Text("Item 3")
 *     }
 *
 *     // Center all children
 *     Column(
 *         modifier = Modifier()
 *             .height("300px")
 *             .justifyContent(JustifyContent.CENTER)
 *     ) {
 *         Text("Centered Item 1")
 *         Text("Centered Item 2")
 *     }
 * }
 * ```
 *
 * ### Horizontal Alignment (Cross Axis)
 * ```kotlin
 * @Composable
 * fun AlignmentExamples() {
 *     // Start alignment (default)
 *     Column(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .alignItems(AlignItems.FLEX_START)
 *     ) {
 *         Text("Left aligned")
 *         Button(onClick = { }) { Text("Button") }
 *     }
 *
 *     // Center alignment
 *     Column(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .alignItems(AlignItems.CENTER)
 *     ) {
 *         Text("Center aligned")
 *         Button(onClick = { }) { Text("Button") }
 *     }
 *
 *     // End alignment
 *     Column(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .alignItems(AlignItems.FLEX_END)
 *     ) {
 *         Text("Right aligned")
 *         Button(onClick = { }) { Text("Button") }
 *     }
 *
 *     // Stretch alignment
 *     Column(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .alignItems(AlignItems.STRETCH)
 *     ) {
 *         Text("Stretched text")
 *         Button(onClick = { }) { Text("Stretched button") }
 *     }
 * }
 * ```
 *
 * ## Spacing and Gaps
 *
 * ### Gap Control
 * ```kotlin
 * @Composable
 * fun SpacingExamples() {
 *     // Uniform gap between all children
 *     Column(
 *         modifier = Modifier()
 *             .gap(Spacing.MD)
 *             .padding(Spacing.LG)
 *     ) {
 *         repeat(5) { index ->
 *             Card(
 *                 modifier = Modifier()
 *                     .width(Width.FULL)
 *                     .padding(Spacing.SM)
 *             ) {
 *                 Text("Card $index")
 *             }
 *         }
 *     }
 *
 *     // Custom spacing with spacers
 *     Column {
 *         Text("Title")
 *         Spacer(modifier = Modifier().height(Spacing.LG))
 *
 *         Text("Subtitle")
 *         Spacer(modifier = Modifier().height(Spacing.MD))
 *
 *         Text("Content")
 *         Spacer(modifier = Modifier().height(Spacing.XL))
 *
 *         Button(onClick = { }) { Text("Action") }
 *     }
 * }
 * ```
 *
 * ### Dynamic Spacing
 * ```kotlin
 * @Composable
 * fun DynamicSpacing(isCompact: Boolean) {
 *     Column(
 *         modifier = Modifier()
 *             .gap(if (isCompact) Spacing.SM else Spacing.LG)
 *             .padding(if (isCompact) Spacing.MD else Spacing.XL)
 *     ) {
 *         Text("Dynamic layout")
 *         Text("Spacing adapts to context")
 *         Button(onClick = { }) { Text("Action") }
 *     }
 * }
 * ```
 *
 * ## Responsive Design
 *
 * ### Breakpoint-Based Layouts
 * ```kotlin
 * @Composable
 * fun ResponsiveColumn() {
 *     Column(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .padding(Spacing.SM)
 *             .gap(Spacing.SM)
 *             .mediaQuery(MediaQuery.MD) {
 *                 padding(Spacing.LG)
 *                 gap(Spacing.MD)
 *                 alignItems(AlignItems.CENTER)
 *             }
 *             .mediaQuery(MediaQuery.LG) {
 *                 padding(Spacing.XL)
 *                 gap(Spacing.LG)
 *                 maxWidth(MaxWidth.LG)
 *                 margin(Margin.AUTO)
 *             }
 *     ) {
 *         Text("Responsive Column")
 *         Text("Adapts to screen size")
 *         Row(
 *             modifier = Modifier()
 *                 .gap(Spacing.SM)
 *                 .mediaQuery(MediaQuery.MD) {
 *                     gap(Spacing.MD)
 *                 }
 *         ) {
 *             Button(onClick = { }) { Text("Button 1") }
 *             Button(onClick = { }) { Text("Button 2") }
 *         }
 *     }
 * }
 * ```
 *
 * ### Mobile-First Design
 * ```kotlin
 * @Composable
 * fun MobileFirstColumn() {
 *     Column(
 *         modifier = Modifier()
 *             // Mobile styles (default)
 *             .width(Width.FULL)
 *             .padding(Spacing.MD)
 *             .gap(Spacing.SM)
 *             .alignItems(AlignItems.STRETCH)
 *             // Tablet styles
 *             .mediaQuery(MediaQuery.MD) {
 *                 padding(Spacing.LG)
 *                 gap(Spacing.MD)
 *                 alignItems(AlignItems.CENTER)
 *                 maxWidth("600px")
 *                 margin(Margin.AUTO)
 *             }
 *             // Desktop styles
 *             .mediaQuery(MediaQuery.LG) {
 *                 padding(Spacing.XL)
 *                 gap(Spacing.LG)
 *                 maxWidth("800px")
 *             }
 *     ) {
 *         content()
 *     }
 * }
 * ```
 *
 * ## Advanced Layouts
 *
 * ### Card Grid with Columns
 * ```kotlin
 * @Composable
 * fun CardGrid(items: List<CardData>) {
 *     Column(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .gap(Spacing.LG)
 *             .padding(Spacing.LG)
 *     ) {
 *         // Header
 *         Row(
 *             modifier = Modifier()
 *                 .width(Width.FULL)
 *                 .justifyContent(JustifyContent.SPACE_BETWEEN)
 *                 .alignItems(AlignItems.CENTER)
 *         ) {
 *             Text("Card Grid", style = TextStyle.HEADING_2)
 *             Button(onClick = { }) { Text("View All") }
 *         }
 *
 *         // Cards in responsive grid
 *         Column(
 *             modifier = Modifier()
 *                 .gap(Spacing.MD)
 *                 .mediaQuery(MediaQuery.MD) {
 *                     displayGrid()
 *                     gridTemplateColumns("repeat(2, 1fr)")
 *                 }
 *                 .mediaQuery(MediaQuery.LG) {
 *                     gridTemplateColumns("repeat(3, 1fr)")
 *                 }
 *         ) {
 *             items.forEach { item ->
 *                 Card(
 *                     modifier = Modifier()
 *                         .width(Width.FULL)
 *                         .borderRadius("12px")
 *                         .shadow("0 4px 6px rgba(0,0,0,0.1)")
 *                 ) {
 *                     CardContent(item)
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ### Form Layout
 * ```kotlin
 * @Composable
 * fun FormLayout() {
 *     Column(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .maxWidth("500px")
 *             .margin(Margin.AUTO)
 *             .padding(Spacing.LG)
 *             .gap(Spacing.LG)
 *     ) {
 *         Text("Contact Form", style = TextStyle.HEADING_2)
 *
 *         Column(modifier = Modifier().gap(Spacing.MD)) {
 *             TextField(
 *                 value = "",
 *                 onValueChange = { },
 *                 label = "Full Name",
 *                 modifier = Modifier().width(Width.FULL)
 *             )
 *
 *             TextField(
 *                 value = "",
 *                 onValueChange = { },
 *                 label = "Email",
 *                 type = InputType.EMAIL,
 *                 modifier = Modifier().width(Width.FULL)
 *             )
 *
 *             TextArea(
 *                 value = "",
 *                 onValueChange = { },
 *                 label = "Message",
 *                 rows = 4,
 *                 modifier = Modifier().width(Width.FULL)
 *             )
 *         }
 *
 *         Row(
 *             modifier = Modifier()
 *                 .width(Width.FULL)
 *                 .justifyContent(JustifyContent.SPACE_BETWEEN)
 *                 .gap(Spacing.MD)
 *         ) {
 *             Button(
 *                 onClick = { },
 *                 variant = ButtonVariant.OUTLINE,
 *                 modifier = Modifier().flexGrow(1)
 *             ) {
 *                 Text("Cancel")
 *             }
 *             Button(
 *                 onClick = { },
 *                 variant = ButtonVariant.PRIMARY,
 *                 modifier = Modifier().flexGrow(1)
 *             ) {
 *                 Text("Submit")
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ### Navigation Layout
 * ```kotlin
 * @Composable
 * fun NavigationLayout() {
 *     Column(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .height(Height.FULL)
 *     ) {
 *         // Header
 *         Box(
 *             modifier = Modifier()
 *                 .width(Width.FULL)
 *                 .height("60px")
 *                 .backgroundColor(Color.BLUE_600)
 *                 .color(Color.WHITE)
 *                 .displayFlex()
 *                 .alignItems(AlignItems.CENTER)
 *                 .padding(horizontal = Spacing.LG)
 *         ) {
 *             Text("App Header", style = TextStyle.HEADING_3)
 *         }
 *
 *         // Main content area (grows to fill space)
 *         Column(
 *             modifier = Modifier()
 *                 .width(Width.FULL)
 *                 .flexGrow(1)
 *                 .overflow(Overflow.AUTO)
 *                 .padding(Spacing.LG)
 *                 .gap(Spacing.LG)
 *         ) {
 *             Text("Main Content", style = TextStyle.HEADING_2)
 *             repeat(10) { index ->
 *                 Card(modifier = Modifier().width(Width.FULL)) {
 *                     Text("Content item $index")
 *                 }
 *             }
 *         }
 *
 *         // Footer
 *         Box(
 *             modifier = Modifier()
 *                 .width(Width.FULL)
 *                 .height("50px")
 *                 .backgroundColor(Color.GRAY_100)
 *                 .displayFlex()
 *                 .alignItems(AlignItems.CENTER)
 *                 .justifyContent(JustifyContent.CENTER)
 *         ) {
 *             Text("Footer", style = TextStyle.CAPTION)
 *         }
 *     }
 * }
 * ```
 *
 * ## Accessibility
 *
 * ### Semantic Structure
 * ```kotlin
 * @Composable
 * fun AccessibleColumn() {
 *     Column(
 *         modifier = Modifier()
 *             .role("main")
 *             .ariaLabel("Article content")
 *             .padding(Spacing.LG)
 *             .gap(Spacing.MD)
 *     ) {
 *         Text(
 *             text = "Article Title",
 *             style = TextStyle.HEADING_1,
 *             modifier = Modifier().role("heading").ariaLevel(1)
 *         )
 *
 *         Column(
 *             modifier = Modifier()
 *                 .role("region")
 *                 .ariaLabel("Article metadata")
 *                 .gap(Spacing.SM)
 *         ) {
 *             Text("Published: January 1, 2024")
 *             Text("Author: John Doe")
 *         }
 *
 *         Column(
 *             modifier = Modifier()
 *                 .role("region")
 *                 .ariaLabel("Article content")
 *                 .gap(Spacing.MD)
 *         ) {
 *             Text("Article paragraph 1...")
 *             Text("Article paragraph 2...")
 *         }
 *     }
 * }
 * ```
 *
 * ### Focus Management
 * ```kotlin
 * @Composable
 * fun FocusableColumn() {
 *     Column(
 *         modifier = Modifier()
 *             .tabIndex(0)
 *             .focusable()
 *             .keyboardNavigation(KeyboardNavigation.VERTICAL)
 *             .gap(Spacing.SM)
 *             .focus {
 *                 outline("2px solid #4285f4")
 *                 outlineOffset("2px")
 *             }
 *     ) {
 *         repeat(5) { index ->
 *             Button(
 *                 onClick = { },
 *                 modifier = Modifier()
 *                     .width(Width.FULL)
 *                     .tabIndex(0)
 *             ) {
 *                 Text("Focusable Item $index")
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ## Performance Optimization
 *
 * ### Lazy Loading Integration
 * ```kotlin
 * @Composable
 * fun OptimizedColumn(items: List<ItemData>) {
 *     Column(
 *         modifier = Modifier()
 *             .height("400px")
 *             .overflow(Overflow.AUTO)
 *     ) {
 *         // Use LazyColumn for large lists
 *         if (items.size > 50) {
 *             LazyColumn {
 *                 items(items) { item ->
 *                     ItemComponent(item)
 *                 }
 *             }
 *         } else {
 *             // Regular Column for small lists
 *             Column(modifier = Modifier().gap(Spacing.SM)) {
 *                 items.forEach { item ->
 *                     ItemComponent(item)
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ### Memory Efficient Rendering
 * ```kotlin
 * @Composable
 * fun EfficientColumn() {
 *     Column(
 *         modifier = Modifier()
 *             .contentVisibility(ContentVisibility.AUTO)
 *             .containIntrinsicSize(ContainIntrinsicSize.LAYOUT)
 *             .gap(Spacing.MD)
 *     ) {
 *         // Only render visible content
 *         content()
 *     }
 * }
 * ```
 *
 * ## CSS Integration
 *
 * ### Custom CSS Classes
 * ```kotlin
 * @Composable
 * fun StyledColumn() {
 *     Column(
 *         modifier = Modifier()
 *             .className("custom-column")
 *             .className("responsive-stack")
 *             .style("--column-gap", "1rem")
 *             .style("--column-padding", "2rem")
 *     ) {
 *         content()
 *     }
 * }
 *
 * // Companion CSS
 * @Composable
 * fun ColumnStyles() {
 *     GlobalStyle("""
 *         .custom-column {
 *             background: linear-gradient(to bottom, #f8fafc, #e2e8f0);
 *             border-radius: 0.75rem;
 *             box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1);
 *         }
 *
 *         .responsive-stack {
 *             gap: var(--column-gap, 1rem);
 *             padding: var(--column-padding, 1rem);
 *         }
 *
 *         @media (max-width: 768px) {
 *             .responsive-stack {
 *                 --column-gap: 0.5rem;
 *                 --column-padding: 1rem;
 *             }
 *         }
 *
 *         @media (min-width: 1024px) {
 *             .responsive-stack {
 *                 --column-gap: 1.5rem;
 *                 --column-padding: 3rem;
 *             }
 *         }
 *     """)
 * }
 * ```
 *
 * ## Platform Behavior
 *
 * ### Browser Implementation
 * - Renders as HTML `<div>` with `display: flex` and `flex-direction: column`
 * - Supports all CSS Flexbox properties for alignment and distribution
 * - Hardware acceleration for scroll and animations
 * - Container queries for responsive behavior
 * - Intersection Observer for performance optimization
 *
 * ### JVM Implementation
 * - Server-side layout calculations for SSR
 * - Responsive breakpoint evaluation during render
 * - CSS generation with computed styles
 * - Layout measurement for hydration coordination
 *
 * ## Testing Strategies
 *
 * ### Layout Testing
 * ```kotlin
 * @Test
 * fun testColumnLayout() = runComposingTest {
 *     setContent {
 *         Column(
 *             modifier = Modifier()
 *                 .testTag("column")
 *                 .gap(Spacing.MD)
 *                 .alignItems(AlignItems.CENTER)
 *         ) {
 *             Text("Item 1", modifier = Modifier().testTag("item1"))
 *             Text("Item 2", modifier = Modifier().testTag("item2"))
 *             Text("Item 3", modifier = Modifier().testTag("item3"))
 *         }
 *     }
 *
 *     onNodeWithTag("column")
 *         .assertExists()
 *         .assertStyleEquals("display", "flex")
 *         .assertStyleEquals("flex-direction", "column")
 *         .assertStyleEquals("align-items", "center")
 *         .assertStyleEquals("gap", "1rem")
 *
 *     onNodeWithTag("item1").assertExists()
 *     onNodeWithTag("item2").assertExists()
 *     onNodeWithTag("item3").assertExists()
 * }
 * ```
 *
 * ### Responsive Testing
 * ```kotlin
 * @Test
 * fun testResponsiveColumn() = runComposingTest {
 *     setContent {
 *         Column(
 *             modifier = Modifier()
 *                 .testTag("responsive")
 *                 .gap(Spacing.SM)
 *                 .mediaQuery(MediaQuery.MD) {
 *                     gap(Spacing.LG)
 *                     alignItems(AlignItems.CENTER)
 *                 }
 *         ) {
 *             Text("Content")
 *         }
 *     }
 *
 *     // Test mobile layout
 *     setViewportSize(400, 800)
 *     onNodeWithTag("responsive")
 *         .assertStyleEquals("gap", "0.5rem")
 *
 *     // Test tablet layout
 *     setViewportSize(768, 1024)
 *     onNodeWithTag("responsive")
 *         .assertStyleEquals("gap", "2rem")
 *         .assertStyleEquals("align-items", "center")
 * }
 * ```
 *
 * ## Migration Guide
 *
 * ### From CSS Flexbox
 * ```css
 * /* CSS */
 * .column {
 *   display: flex;
 *   flex-direction: column;
 *   gap: 1rem;
 *   align-items: center;
 *   justify-content: space-between;
 * }
 * ```
 *
 * ```kotlin
 * // Summon equivalent
 * @Composable
 * fun MigratedColumn() {
 *     Column(
 *         modifier = Modifier()
 *             .gap(Spacing.MD)
 *             .alignItems(AlignItems.CENTER)
 *             .justifyContent(JustifyContent.SPACE_BETWEEN)
 *     ) {
 *         content()
 *     }
 * }
 * ```
 *
 * ### From React/Flutter
 * ```typescript
 * // React
 * <div style={{
 *   display: 'flex',
 *   flexDirection: 'column',
 *   gap: '16px',
 *   alignItems: 'center'
 * }}>
 *   <span>Item 1</span>
 *   <span>Item 2</span>
 * </div>
 * ```
 *
 * ```kotlin
 * // Summon equivalent
 * @Composable
 * fun MigratedFromReact() {
 *     Column(
 *         modifier = Modifier()
 *             .gap(Spacing.MD)
 *             .alignItems(AlignItems.CENTER)
 *     ) {
 *         Text("Item 1")
 *         Text("Item 2")
 *     }
 * }
 * ```
 *
 * @param modifier The modifier to be applied to this layout. Controls layout behavior, styling, and responsive properties.
 * @param content The content to be displayed inside the column. Children are arranged vertically in the order they appear.
 *
 * @see Row for horizontal layouts
 * @see Box for free-form positioning
 * @see LazyColumn for virtualized vertical lists
 * @see Grid for two-dimensional layouts
 * @see Spacer for adding spacing between elements
 *
 * @sample ColumnSamples.basicStack
 * @sample ColumnSamples.centeredContent
 * @sample ColumnSamples.spaceBetween
 * @sample ColumnSamples.responsiveLayout
 *
 * @since 1.0.0
 */
@Composable
fun Column(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    // Use platform renderer directly
    val renderer = LocalPlatformRenderer.current

    // Apply default flex styles for Column using type-safe enums
    val columnModifier = Modifier()
        .style("display", Display.Flex.value)
        .style("flex-direction", FlexDirection.Column.value)
        .then(modifier)

    // Call renderColumn and pass the content lambda
    renderer.renderColumn(
        modifier = columnModifier,
        content = { // Wrap the original content lambda
            content()
        }
    )
}
