package codes.yousef.summon.components.layout

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * # Box
 *
 * A fundamental layout container that provides flexible positioning and arrangement for its children.
 * Box serves as the primary building block for creating custom layouts with absolute, relative,
 * or default positioning strategies.
 *
 * ## Overview
 *
 * Box is the most basic and versatile layout component in Summon, equivalent to a `<div>` element
 * in HTML. It provides a foundation for creating complex layouts by offering:
 *
 * - **Flexible positioning** - Stack children with absolute/relative positioning
 * - **Content alignment** - Center or align children within the container
 * - **Z-index layering** - Control stacking order of overlapping elements
 * - **Responsive containers** - Adaptable sizing based on content or constraints
 *
 * ## Key Features
 *
 * ### Positioning Strategies
 * - **Default flow** - Children follow normal document flow
 * - **Absolute positioning** - Precise pixel-level placement
 * - **Relative positioning** - Offset from normal position
 * - **Fixed positioning** - Viewport-relative positioning
 *
 * ### Layout Capabilities
 * - **Overflow control** - Handle content that exceeds container bounds
 * - **Clipping** - Clip child content to container boundaries
 * - **Scrolling** - Enable scrollable areas for overflowing content
 * - **Aspect ratio** - Maintain proportional dimensions
 *
 * ## Basic Usage
 *
 * ### Simple Container
 * ```kotlin
 * @Composable
 * fun SimpleCard() {
 *     Box(
 *         modifier = Modifier()
 *             .padding(Spacing.MD)
 *             .backgroundColor(Color.WHITE)
 *             .borderRadius("8px")
 *             .shadow("0 2px 4px rgba(0,0,0,0.1)")
 *     ) {
 *         Text("Card content")
 *     }
 * }
 * ```
 *
 * ### Centered Content
 * ```kotlin
 * @Composable
 * fun CenteredBox() {
 *     Box(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .height("400px")
 *             .displayFlex()
 *             .alignItems(AlignItems.CENTER)
 *             .justifyContent(JustifyContent.CENTER)
 *             .backgroundColor(Color.GRAY_100)
 *     ) {
 *         Text("Centered content")
 *     }
 * }
 * ```
 *
 * ## Advanced Positioning
 *
 * ### Absolute Positioning
 * ```kotlin
 * @Composable
 * fun OverlayContainer() {
 *     Box(
 *         modifier = Modifier()
 *             .position(Position.RELATIVE)
 *             .width("300px")
 *             .height("200px")
 *             .backgroundColor(Color.BLUE_500)
 *     ) {
 *         // Background content
 *         Text("Background")
 *
 *         // Overlay badge
 *         Box(
 *             modifier = Modifier()
 *                 .position(Position.ABSOLUTE)
 *                 .top("10px")
 *                 .right("10px")
 *                 .backgroundColor(Color.RED_500)
 *                 .color(Color.WHITE)
 *                 .padding("4px 8px")
 *                 .borderRadius("12px")
 *                 .zIndex(10)
 *         ) {
 *             Text("NEW")
 *         }
 *     }
 * }
 * ```
 *
 * ### Layered Layout
 * ```kotlin
 * @Composable
 * fun LayeredInterface() {
 *     Box(modifier = Modifier().position(Position.RELATIVE)) {
 *         // Base layer
 *         Image(
 *             src = "background.jpg",
 *             modifier = Modifier()
 *                 .width(Width.FULL)
 *                 .height("300px")
 *                 .objectFit(ObjectFit.COVER)
 *         )
 *
 *         // Overlay gradient
 *         Box(
 *             modifier = Modifier()
 *                 .position(Position.ABSOLUTE)
 *                 .top("0")
 *                 .left("0")
 *                 .width(Width.FULL)
 *                 .height(Height.FULL)
 *                 .background("linear-gradient(to bottom, transparent 0%, rgba(0,0,0,0.8) 100%)")
 *                 .zIndex(1)
 *         )
 *
 *         // Content layer
 *         Box(
 *             modifier = Modifier()
 *                 .position(Position.ABSOLUTE)
 *                 .bottom("20px")
 *                 .left("20px")
 *                 .right("20px")
 *                 .zIndex(2)
 *         ) {
 *             Column {
 *                 Text(
 *                     text = "Overlay Title",
 *                     style = TextStyle.HEADING_2,
 *                     color = Color.WHITE
 *                 )
 *                 Text(
 *                     text = "Description text over image",
 *                     color = Color.GRAY_300
 *                 )
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ## Responsive Design
 *
 * ### Breakpoint-Based Layouts
 * ```kotlin
 * @Composable
 * fun ResponsiveBox() {
 *     Box(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .padding(Spacing.SM)
 *             .mediaQuery(MediaQuery.MD) {
 *                 padding(Spacing.LG)
 *                 maxWidth(MaxWidth.LG)
 *                 margin(Margin.AUTO)
 *             }
 *             .mediaQuery(MediaQuery.LG) {
 *                 padding(Spacing.XL)
 *                 displayGrid()
 *                 gridTemplateColumns("1fr 300px")
 *                 gap(Spacing.LG)
 *             }
 *     ) {
 *         content()
 *     }
 * }
 * ```
 *
 * ### Container Queries
 * ```kotlin
 * @Composable
 * fun AdaptiveContainer() {
 *     Box(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .maxWidth("800px")
 *             .containerType(ContainerType.INLINE_SIZE)
 *             .containerQuery("(max-width: 600px)") {
 *                 displayFlex()
 *                 flexDirection(FlexDirection.COLUMN)
 *             }
 *             .containerQuery("(min-width: 601px)") {
 *                 displayGrid()
 *                 gridTemplateColumns("2fr 1fr")
 *             }
 *     ) {
 *         content()
 *     }
 * }
 * ```
 *
 * ## Scroll and Overflow
 *
 * ### Scrollable Areas
 * ```kotlin
 * @Composable
 * fun ScrollableContent() {
 *     Box(
 *         modifier = Modifier()
 *             .width("400px")
 *             .height("300px")
 *             .overflow(Overflow.AUTO)
 *             .border("1px solid #e5e7eb")
 *             .borderRadius("8px")
 *             .backgroundColor(Color.WHITE)
 *     ) {
 *         Column(modifier = Modifier().padding(Spacing.MD)) {
 *             repeat(20) { index ->
 *                 Text(
 *                     text = "Scrollable item $index",
 *                     modifier = Modifier().padding(Spacing.SM)
 *                 )
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ### Clipped Content
 * ```kotlin
 * @Composable
 * fun ClippedContainer() {
 *     Box(
 *         modifier = Modifier()
 *             .width("200px")
 *             .height("100px")
 *             .overflow(Overflow.HIDDEN)
 *             .borderRadius("12px")
 *             .position(Position.RELATIVE)
 *     ) {
 *         Image(
 *             src = "large-image.jpg",
 *             modifier = Modifier()
 *                 .width("300px")
 *                 .height("200px")
 *                 .objectFit(ObjectFit.COVER)
 *         )
 *     }
 * }
 * ```
 *
 * ## Accessibility
 *
 * ### Semantic Structure
 * ```kotlin
 * @Composable
 * fun AccessibleCard() {
 *     Box(
 *         modifier = Modifier()
 *             .role("article")
 *             .ariaLabel("Product information card")
 *             .tabIndex(0)
 *             .focusable()
 *             .padding(Spacing.MD)
 *             .border("2px solid transparent")
 *             .borderRadius("8px")
 *             .focus {
 *                 borderColor(Color.BLUE_500)
 *                 outline("none")
 *             }
 *     ) {
 *         Column {
 *             Text("Product Title", style = TextStyle.HEADING_3)
 *             Text("Product description...")
 *         }
 *     }
 * }
 * ```
 *
 * ### Screen Reader Support
 * ```kotlin
 * @Composable
 * fun VisuallyHiddenContainer() {
 *     Box(
 *         modifier = Modifier()
 *             .position(Position.ABSOLUTE)
 *             .left("-10000px")
 *             .top("auto")
 *             .width("1px")
 *             .height("1px")
 *             .overflow(Overflow.HIDDEN)
 *             .ariaLive("polite")
 *     ) {
 *         Text("Screen reader announcement")
 *     }
 * }
 * ```
 *
 * ## Performance Optimization
 *
 * ### Virtual Scrolling Support
 * ```kotlin
 * @Composable
 * fun VirtualScrollContainer() {
 *     Box(
 *         modifier = Modifier()
 *             .height("400px")
 *             .overflow(Overflow.AUTO)
 *             .attribute("data-virtual-scroll", "true")
 *             .attribute("data-item-height", "50")
 *             .attribute("data-buffer-size", "5")
 *     ) {
 *         // Virtual scroll implementation handles rendering
 *         LazyColumn {
 *             items(largeDataSet) { item ->
 *                 ItemComponent(item)
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ### GPU Acceleration
 * ```kotlin
 * @Composable
 * fun AcceleratedBox() {
 *     Box(
 *         modifier = Modifier()
 *             .transform("translateZ(0)") // Force GPU layer
 *             .willChange("transform")
 *             .backfaceVisibility("hidden")
 *             .animation("slideIn 0.3s ease-out")
 *     ) {
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
 * fun StyledBox() {
 *     Box(
 *         modifier = Modifier()
 *             .className("custom-container")
 *             .className("responsive-box")
 *             .style("--custom-property", "value")
 *             .style("container-type", "inline-size")
 *     ) {
 *         content()
 *     }
 * }
 *
 * // Companion CSS
 * @Composable
 * fun BoxStyles() {
 *     GlobalStyle("""
 *         .custom-container {
 *             background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
 *             backdrop-filter: blur(10px);
 *             border: 1px solid rgba(255, 255, 255, 0.1);
 *         }
 *
 *         .responsive-box {
 *             container-type: inline-size;
 *         }
 *
 *         @container (max-width: 400px) {
 *             .responsive-box {
 *                 padding: 1rem;
 *                 font-size: 0.875rem;
 *             }
 *         }
 *
 *         @container (min-width: 401px) {
 *             .responsive-box {
 *                 padding: 2rem;
 *                 font-size: 1rem;
 *             }
 *         }
 *     """)
 * }
 * ```
 *
 * ## Platform Behavior
 *
 * ### Browser Implementation
 * - Renders as HTML `<div>` element
 * - Supports all CSS positioning properties
 * - Hardware acceleration available via CSS transforms
 * - Container queries supported in modern browsers
 * - Intersection Observer API for viewport tracking
 *
 * ### JVM Implementation
 * - Server-side rendering with computed styles
 * - Layout calculations for SSR hydration
 * - Responsive breakpoint evaluation
 * - CSS extraction for client delivery
 *
 * ## Testing Strategies
 *
 * ### Layout Testing
 * ```kotlin
 * @Test
 * fun testBoxPositioning() = runComposingTest {
 *     setContent {
 *         Box(
 *             modifier = Modifier()
 *                 .testTag("container")
 *                 .width("300px")
 *                 .height("200px")
 *                 .position(Position.RELATIVE)
 *         ) {
 *             Box(
 *                 modifier = Modifier()
 *                     .testTag("child")
 *                     .position(Position.ABSOLUTE)
 *                     .top("10px")
 *                     .left("20px")
 *                     .width("50px")
 *                     .height("30px")
 *             )
 *         }
 *     }
 *
 *     onNodeWithTag("container")
 *         .assertExists()
 *         .assertStyleEquals("position", "relative")
 *         .assertStyleEquals("width", "300px")
 *
 *     onNodeWithTag("child")
 *         .assertExists()
 *         .assertStyleEquals("position", "absolute")
 *         .assertStyleEquals("top", "10px")
 *         .assertStyleEquals("left", "20px")
 * }
 * ```
 *
 * ### Responsive Testing
 * ```kotlin
 * @Test
 * fun testResponsiveBox() = runComposingTest {
 *     setContent {
 *         Box(
 *             modifier = Modifier()
 *                 .testTag("responsive")
 *                 .width(Width.FULL)
 *                 .mediaQuery(MediaQuery.MD) {
 *                     maxWidth(MaxWidth.LG)
 *                     margin(Margin.AUTO)
 *                 }
 *         ) {
 *             content()
 *         }
 *     }
 *
 *     // Test mobile layout
 *     setViewportSize(400, 800)
 *     onNodeWithTag("responsive")
 *         .assertStyleEquals("width", "100%")
 *         .assertStyleDoesNotContain("max-width")
 *
 *     // Test tablet layout
 *     setViewportSize(768, 1024)
 *     onNodeWithTag("responsive")
 *         .assertStyleEquals("max-width", "1024px")
 *         .assertStyleEquals("margin", "0 auto")
 * }
 * ```
 *
 * ## Migration Guide
 *
 * ### From CSS/HTML
 * ```html
 * <!-- HTML/CSS -->
 * <div class="container">
 *   <div class="overlay">Content</div>
 * </div>
 *
 * <style>
 * .container {
 *   position: relative;
 *   width: 300px;
 *   height: 200px;
 * }
 * .overlay {
 *   position: absolute;
 *   top: 10px;
 *   right: 10px;
 * }
 * </style>
 * ```
 *
 * ```kotlin
 * // Summon equivalent
 * @Composable
 * fun MigratedLayout() {
 *     Box(
 *         modifier = Modifier()
 *             .position(Position.RELATIVE)
 *             .width("300px")
 *             .height("200px")
 *     ) {
 *         Box(
 *             modifier = Modifier()
 *                 .position(Position.ABSOLUTE)
 *                 .top("10px")
 *                 .right("10px")
 *         ) {
 *             Text("Content")
 *         }
 *     }
 * }
 * ```
 *
 * ### From React/CSS-in-JS
 * ```typescript
 * // React with styled-components
 * const Container = styled.div`
 *   position: relative;
 *   width: 100%;
 *   max-width: 1200px;
 *   margin: 0 auto;
 *   padding: 1rem;
 *
 *   @media (min-width: 768px) {
 *     padding: 2rem;
 *   }
 * `;
 * ```
 *
 * ```kotlin
 * // Summon equivalent
 * @Composable
 * fun ResponsiveContainer() {
 *     Box(
 *         modifier = Modifier()
 *             .position(Position.RELATIVE)
 *             .width(Width.FULL)
 *             .maxWidth("1200px")
 *             .margin(Margin.AUTO)
 *             .padding(Spacing.MD)
 *             .mediaQuery(MediaQuery.MD) {
 *                 padding(Spacing.LG)
 *             }
 *     ) {
 *         content()
 *     }
 * }
 * ```
 *
 * @param modifier The modifier to apply to this layout. Controls positioning, sizing, styling, and behavior.
 * @param content The children composables to place inside the box. Receives FlowContent scope for HTML-compatible nesting.
 *
 * @see Column for vertical layouts
 * @see Row for horizontal layouts
 * @see Grid for two-dimensional layouts
 * @see AspectRatio for proportional containers
 * @see Card for elevated containers
 *
 * @sample BoxSamples.basicContainer
 * @sample BoxSamples.centeredContent
 * @sample BoxSamples.absolutePositioning
 * @sample BoxSamples.scrollableArea
 *
 * @since 1.0.0
 */
@Composable
fun Box(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit) {
    val renderer = LocalPlatformRenderer.current
    // Call the renderBox method
    renderer.renderBox(modifier, content)
}
