package code.yousef.summon.components.layout

import code.yousef.summon.core.FlowContent
import code.yousef.summon.modifier.Display
import code.yousef.summon.modifier.FlexDirection
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * # Row
 *
 * A layout component that arranges its children in a horizontal sequence using CSS Flexbox.
 * Row provides powerful horizontal layout capabilities with intuitive alignment and spacing controls.
 *
 * ## Overview
 *
 * Row is a fundamental layout component in Summon that arranges child components horizontally.
 * Built on CSS Flexbox, it offers comprehensive control over:
 *
 * - **Horizontal arrangement** - Distribute children along the horizontal axis
 * - **Vertical alignment** - Align children within the container height
 * - **Spacing management** - Control gaps between children
 * - **Responsive behavior** - Adapt to different screen sizes and constraints
 *
 * ## Key Features
 *
 * ### Arrangement Options
 * - **Start aligned** - Children start from the left (default)
 * - **End aligned** - Children align to the right
 * - **Center aligned** - Children center horizontally
 * - **Space between** - Equal space between children
 * - **Space around** - Equal space around each child
 * - **Space evenly** - Equal space between and around children
 *
 * ### Alignment Controls
 * - **Top aligned** - Children align to the top edge
 * - **Bottom aligned** - Children align to the bottom edge
 * - **Center aligned** - Children center vertically (default)
 * - **Stretch** - Children stretch to fill available height
 * - **Baseline** - Children align to text baseline
 *
 * ## Basic Usage
 *
 * ### Simple Horizontal Stack
 * ```kotlin
 * @Composable
 * fun SimpleRow() {
 *     Row(
 *         modifier = Modifier()
 *             .padding(Spacing.MD)
 *             .gap(Spacing.SM)
 *     ) {
 *         Text("First")
 *         Text("Second")
 *         Text("Third")
 *     }
 * }
 * ```
 *
 * ### Button Group
 * ```kotlin
 * @Composable
 * fun ButtonGroup() {
 *     Row(
 *         modifier = Modifier()
 *             .gap(Spacing.SM)
 *             .alignItems(AlignItems.CENTER)
 *     ) {
 *         Button(onClick = { }, variant = ButtonVariant.PRIMARY) {
 *             Text("Save")
 *         }
 *         Button(onClick = { }, variant = ButtonVariant.OUTLINE) {
 *             Text("Cancel")
 *         }
 *         Button(onClick = { }, variant = ButtonVariant.GHOST) {
 *             Text("Help")
 *         }
 *     }
 * }
 * ```
 *
 * ## Arrangement and Alignment
 *
 * ### Horizontal Arrangement (Main Axis)
 * ```kotlin
 * @Composable
 * fun ArrangementExamples() {
 *     // Space between items
 *     Row(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .justifyContent(JustifyContent.SPACE_BETWEEN)
 *     ) {
 *         Text("Left")
 *         Text("Center")
 *         Text("Right")
 *     }
 *
 *     // Center all items
 *     Row(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .justifyContent(JustifyContent.CENTER)
 *             .gap(Spacing.MD)
 *     ) {
 *         Button(onClick = { }) { Text("Action 1") }
 *         Button(onClick = { }) { Text("Action 2") }
 *     }
 * }
 * ```
 *
 * ### Vertical Alignment (Cross Axis)
 * ```kotlin
 * @Composable
 * fun AlignmentExamples() {
 *     Row(
 *         modifier = Modifier()
 *             .height("100px")
 *             .alignItems(AlignItems.CENTER)
 *             .gap(Spacing.MD)
 *     ) {
 *         Icon(Icons.STAR)
 *         Text("Centered content")
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
 * fun ResponsiveRow() {
 *     Row(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .gap(Spacing.SM)
 *             .mediaQuery(MediaQuery.MD) {
 *                 gap(Spacing.MD)
 *                 justifyContent(JustifyContent.SPACE_BETWEEN)
 *             }
 *             .mediaQuery(MediaQuery.LG) {
 *                 gap(Spacing.LG)
 *                 padding(Spacing.LG)
 *             }
 *     ) {
 *         Text("Responsive Row")
 *         Button(onClick = { }) { Text("Action") }
 *     }
 * }
 * ```
 *
 * ### Mobile Stacking
 * ```kotlin
 * @Composable
 * fun StackableRow() {
 *     Row(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .gap(Spacing.SM)
 *             .mediaQuery(MediaQuery.SM_DOWN) {
 *                 flexDirection(FlexDirection.COLUMN)
 *                 alignItems(AlignItems.STRETCH)
 *             }
 *     ) {
 *         Button(
 *             onClick = { },
 *             modifier = Modifier().flexGrow(1)
 *         ) {
 *             Text("Primary Action")
 *         }
 *         Button(
 *             onClick = { },
 *             variant = ButtonVariant.OUTLINE,
 *             modifier = Modifier().flexGrow(1)
 *         ) {
 *             Text("Secondary")
 *         }
 *     }
 * }
 * ```
 *
 * ## Advanced Layouts
 *
 * ### Navigation Bar
 * ```kotlin
 * @Composable
 * fun NavigationBar() {
 *     Row(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .height("60px")
 *             .backgroundColor(Color.WHITE)
 *             .borderBottom("1px solid #e5e7eb")
 *             .padding(horizontal = Spacing.LG)
 *             .justifyContent(JustifyContent.SPACE_BETWEEN)
 *             .alignItems(AlignItems.CENTER)
 *     ) {
 *         // Left side - Logo
 *         Row(
 *             modifier = Modifier()
 *                 .gap(Spacing.MD)
 *                 .alignItems(AlignItems.CENTER)
 *         ) {
 *             Image(
 *                 src = "logo.svg",
 *                 alt = "Company Logo",
 *                 modifier = Modifier().width("32px").height("32px")
 *             )
 *             Text("App Name", style = TextStyle.HEADING_3)
 *         }
 *
 *         // Center - Navigation links
 *         Row(
 *             modifier = Modifier()
 *                 .gap(Spacing.LG)
 *                 .mediaQuery(MediaQuery.MD_DOWN) {
 *                     display(Display.NONE)
 *                 }
 *         ) {
 *             Link(href = "/home") { Text("Home") }
 *             Link(href = "/products") { Text("Products") }
 *             Link(href = "/about") { Text("About") }
 *         }
 *
 *         // Right side - User actions
 *         Row(modifier = Modifier().gap(Spacing.SM)) {
 *             Button(
 *                 onClick = { },
 *                 variant = ButtonVariant.GHOST
 *             ) {
 *                 Text("Sign In")
 *             }
 *             Button(
 *                 onClick = { },
 *                 variant = ButtonVariant.PRIMARY
 *             ) {
 *                 Text("Sign Up")
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ### Card Layout
 * ```kotlin
 * @Composable
 * fun ProductCard(product: Product) {
 *     Card(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .padding(Spacing.MD)
 *             .borderRadius("12px")
 *             .shadow("0 4px 6px rgba(0,0,0,0.1)")
 *     ) {
 *         Row(
 *             modifier = Modifier()
 *                 .padding(Spacing.MD)
 *                 .gap(Spacing.MD)
 *                 .alignItems(AlignItems.CENTER)
 *         ) {
 *             // Product image
 *             Image(
 *                 src = product.imageUrl,
 *                 alt = product.name,
 *                 modifier = Modifier()
 *                     .width("80px")
 *                     .height("80px")
 *                     .borderRadius("8px")
 *                     .objectFit(ObjectFit.COVER)
 *             )
 *
 *             // Product details (grow to fill space)
 *             Column(
 *                 modifier = Modifier()
 *                     .flexGrow(1)
 *                     .gap(Spacing.SM)
 *             ) {
 *                 Text(product.name, style = TextStyle.HEADING_4)
 *                 Text(product.description, style = TextStyle.BODY_SMALL)
 *                 Text(
 *                     text = "$${product.price}",
 *                     style = TextStyle.HEADING_4,
 *                     color = Color.GREEN_600
 *                 )
 *             }
 *
 *             // Action button
 *             Button(
 *                 onClick = { },
 *                 variant = ButtonVariant.PRIMARY
 *             ) {
 *                 Text("Add to Cart")
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ### Form Field
 * ```kotlin
 * @Composable
 * fun FormFieldRow(
 *     label: String,
 *     value: String,
 *     onValueChange: (String) -> Unit,
 *     isRequired: Boolean = false,
 *     error: String? = null
 * ) {
 *     Column(modifier = Modifier().gap(Spacing.SM)) {
 *         Row(
 *             modifier = Modifier()
 *                 .alignItems(AlignItems.CENTER)
 *                 .gap(Spacing.XS)
 *         ) {
 *             Text(label, style = TextStyle.LABEL)
 *             if (isRequired) {
 *                 Text("*", color = Color.RED_500)
 *             }
 *         }
 *
 *         Row(
 *             modifier = Modifier()
 *                 .width(Width.FULL)
 *                 .alignItems(AlignItems.CENTER)
 *                 .gap(Spacing.SM)
 *         ) {
 *             TextField(
 *                 value = value,
 *                 onValueChange = onValueChange,
 *                 modifier = Modifier()
 *                     .flexGrow(1)
 *                     .borderColor(if (error != null) Color.RED_500 else Color.GRAY_300)
 *             )
 *
 *             if (error != null) {
 *                 Icon(
 *                     icon = Icons.ERROR,
 *                     color = Color.RED_500,
 *                     modifier = Modifier().size("20px")
 *                 )
 *             }
 *         }
 *
 *         if (error != null) {
 *             Text(
 *                 text = error,
 *                 style = TextStyle.CAPTION,
 *                 color = Color.RED_500
 *             )
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
 * fun AccessibleRow() {
 *     Row(
 *         modifier = Modifier()
 *             .role("toolbar")
 *             .ariaLabel("Action buttons")
 *             .gap(Spacing.SM)
 *             .padding(Spacing.MD)
 *     ) {
 *         Button(
 *             onClick = { },
 *             modifier = Modifier()
 *                 .ariaLabel("Save document")
 *                 .keyboardShortcut("Ctrl+S")
 *         ) {
 *             Text("Save")
 *         }
 *         Button(
 *             onClick = { },
 *             modifier = Modifier()
 *                 .ariaLabel("Copy content")
 *                 .keyboardShortcut("Ctrl+C")
 *         ) {
 *             Text("Copy")
 *         }
 *     }
 * }
 * ```
 *
 * ## CSS Integration
 *
 * ### Custom CSS Classes
 * ```kotlin
 * @Composable
 * fun StyledRow() {
 *     Row(
 *         modifier = Modifier()
 *             .className("custom-row")
 *             .className("responsive-flex")
 *             .style("--row-gap", "1rem")
 *             .style("--row-padding", "1.5rem")
 *     ) {
 *         content()
 *     }
 * }
 *
 * // Companion CSS
 * @Composable
 * fun RowStyles() {
 *     GlobalStyle("""
 *         .custom-row {
 *             background: linear-gradient(to right, #667eea, #764ba2);
 *             border-radius: 0.5rem;
 *             box-shadow: 0 4px 6px rgba(0,0,0,0.1);
 *         }
 *
 *         .responsive-flex {
 *             gap: var(--row-gap, 1rem);
 *             padding: var(--row-padding, 1rem);
 *         }
 *
 *         @media (max-width: 768px) {
 *             .responsive-flex {
 *                 flex-direction: column;
 *                 --row-gap: 0.5rem;
 *             }
 *         }
 *     """)
 * }
 * ```
 *
 * ## Platform Behavior
 *
 * ### Browser Implementation
 * - Renders as HTML `<div>` with `display: flex` and `flex-direction: row`
 * - Supports all CSS Flexbox properties for alignment and distribution
 * - Hardware acceleration for animations and transforms
 * - Container queries for responsive behavior
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
 * fun testRowLayout() = runComposingTest {
 *     setContent {
 *         Row(
 *             modifier = Modifier()
 *                 .testTag("row")
 *                 .gap(Spacing.MD)
 *                 .alignItems(AlignItems.CENTER)
 *         ) {
 *             Text("Item 1", modifier = Modifier().testTag("item1"))
 *             Text("Item 2", modifier = Modifier().testTag("item2"))
 *         }
 *     }
 *
 *     onNodeWithTag("row")
 *         .assertExists()
 *         .assertStyleEquals("display", "flex")
 *         .assertStyleEquals("flex-direction", "row")
 *         .assertStyleEquals("align-items", "center")
 *         .assertStyleEquals("gap", "1rem")
 * }
 * ```
 *
 * ## Migration Guide
 *
 * ### From CSS Flexbox
 * ```css
 * /* CSS */
 * .row {
 *   display: flex;
 *   flex-direction: row;
 *   gap: 1rem;
 *   align-items: center;
 *   justify-content: space-between;
 * }
 * ```
 *
 * ```kotlin
 * // Summon equivalent
 * @Composable
 * fun MigratedRow() {
 *     Row(
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
 * @param modifier The modifier to be applied to this layout. Controls layout behavior, styling, and responsive properties.
 * @param content The content to be displayed inside the row. Children are arranged horizontally in the order they appear.
 *
 * @see Column for vertical layouts
 * @see Box for free-form positioning
 * @see LazyRow for virtualized horizontal lists
 * @see Grid for two-dimensional layouts
 * @see Spacer for adding spacing between elements
 *
 * @sample RowSamples.basicStack
 * @sample RowSamples.buttonGroup
 * @sample RowSamples.navigationBar
 * @sample RowSamples.responsiveLayout
 *
 * @since 1.0.0
 */
@Composable
fun Row(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    // Use platform renderer directly
    val renderer = LocalPlatformRenderer.current

    // Apply default flex styles for Row using type-safe enums
    val rowModifier = Modifier()
        .style("display", Display.Flex.value)
        .style("flex-direction", FlexDirection.Row.value)
        .then(modifier)

    renderer.renderRow(rowModifier, content)
}

object Alignment {
    enum class Vertical { Top, CenterVertically, Bottom }
    enum class Horizontal { Start, CenterHorizontally, End }
}

object Arrangement {
    enum class Horizontal { Start, End, Center, SpaceBetween, SpaceAround, SpaceEvenly }
    enum class Vertical { Top, Bottom, Center, SpaceBetween, SpaceAround, SpaceEvenly }
} 
