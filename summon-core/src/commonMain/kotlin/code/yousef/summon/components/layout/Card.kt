package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.EventModifiers.onClick
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * # Card
 *
 * A Material Design-inspired container component that groups related content with elevation,
 * rounded corners, and visual hierarchy. Card provides a structured foundation for displaying
 * information in a visually appealing and organized manner.
 *
 * ## Overview
 *
 * Card is a versatile layout component that creates visually distinct content areas with:
 * - **Material elevation** - Shadow effects for visual hierarchy
 * - **Rounded corners** - Customizable border radius
 * - **Interactive states** - Support for hover, focus, and click interactions
 * - **Content organization** - Structured container for related information
 *
 * ## Key Features
 *
 * ### Visual Design
 * - **Elevation system** - Multiple shadow levels for depth perception
 * - **Border radius** - Customizable corner rounding
 * - **Interactive feedback** - Hover and focus state animations
 * - **Responsive sizing** - Adapts to content and container constraints
 *
 * ### Content Organization
 * - **Header/Body/Footer** - Logical content sections
 * - **Media integration** - Images, videos, and rich content
 * - **Action areas** - Buttons and interactive elements
 * - **Nested layouts** - Compose with other layout components
 *
 * ## Basic Usage
 *
 * ### Simple Card
 * ```kotlin
 * @Composable
 * fun SimpleCard() {
 *     Card(
 *         modifier = Modifier()
 *             .width("300px")
 *             .padding(Spacing.MD)
 *     ) {
 *         Column(modifier = Modifier().padding(Spacing.MD)) {
 *             Text("Card Title", style = TextStyle.HEADING_3)
 *             Text("Card content goes here...")
 *         }
 *     }
 * }
 * ```
 *
 * ### Elevated Card
 * ```kotlin
 * @Composable
 * fun ElevatedCard() {
 *     Card(
 *         elevation = "8px",
 *         borderRadius = "12px",
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .maxWidth("400px")
 *             .backgroundColor(Color.WHITE)
 *     ) {
 *         content()
 *     }
 * }
 * ```
 *
 * ### Interactive Card
 * ```kotlin
 * @Composable
 * fun InteractiveCard(onClick: () -> Unit) {
 *     Card(
 *         onClick = onClick,
 *         modifier = Modifier()
 *             .cursor("pointer")
 *             .transition("all 0.2s ease-in-out")
 *             .hover {
 *                 transform("translateY(-4px)")
 *                 shadow("0 8px 25px rgba(0,0,0,0.15)")
 *             }
 *     ) {
 *         content()
 *     }
 * }
 * ```
 *
 * ## Advanced Layouts
 *
 * ### Product Card
 * ```kotlin
 * @Composable
 * fun ProductCard(product: Product) {
 *     Card(
 *         modifier = Modifier()
 *             .width("280px")
 *             .borderRadius("16px")
 *             .overflow(Overflow.HIDDEN)
 *             .backgroundColor(Color.WHITE)
 *             .shadow("0 4px 6px rgba(0,0,0,0.1)")
 *     ) {
 *         Column {
 *             // Product image
 *             Image(
 *                 src = product.imageUrl,
 *                 alt = product.name,
 *                 modifier = Modifier()
 *                     .width(Width.FULL)
 *                     .height("200px")
 *                     .objectFit(ObjectFit.COVER)
 *             )
 *
 *             // Product details
 *             Column(
 *                 modifier = Modifier()
 *                     .padding(Spacing.MD)
 *                     .gap(Spacing.SM)
 *             ) {
 *                 Text(product.name, style = TextStyle.HEADING_4)
 *                 Text(
 *                     text = product.description,
 *                     style = TextStyle.BODY_SMALL,
 *                     color = Color.GRAY_600
 *                 )
 *
 *                 Row(
 *                     modifier = Modifier()
 *                         .width(Width.FULL)
 *                         .justifyContent(JustifyContent.SPACE_BETWEEN)
 *                         .alignItems(AlignItems.CENTER)
 *                 ) {
 *                     Text(
 *                         text = "$${product.price}",
 *                         style = TextStyle.HEADING_4,
 *                         color = Color.GREEN_600
 *                     )
 *                     Button(
 *                         onClick = { },
 *                         size = ButtonSize.SMALL
 *                     ) {
 *                         Text("Add to Cart")
 *                     }
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ### Profile Card
 * ```kotlin
 * @Composable
 * fun ProfileCard(user: User) {
 *     Card(
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .maxWidth("350px")
 *             .borderRadius("20px")
 *             .backgroundColor(Color.WHITE)
 *             .shadow("0 10px 25px rgba(0,0,0,0.1)")
 *     ) {
 *         Column(
 *             modifier = Modifier()
 *                 .padding(Spacing.LG)
 *                 .alignItems(AlignItems.CENTER)
 *                 .gap(Spacing.MD)
 *         ) {
 *             // Avatar
 *             Box(
 *                 modifier = Modifier()
 *                     .width("80px")
 *                     .height("80px")
 *                     .borderRadius("50%")
 *                     .overflow(Overflow.HIDDEN)
 *                     .border("3px solid #e5e7eb")
 *             ) {
 *                 Image(
 *                     src = user.avatarUrl,
 *                     alt = "${user.name} avatar",
 *                     modifier = Modifier()
 *                         .width(Width.FULL)
 *                         .height(Height.FULL)
 *                         .objectFit(ObjectFit.COVER)
 *                 )
 *             }
 *
 *             // User info
 *             Column(
 *                 modifier = Modifier()
 *                     .alignItems(AlignItems.CENTER)
 *                     .gap(Spacing.XS)
 *             ) {
 *                 Text(user.name, style = TextStyle.HEADING_3)
 *                 Text(user.title, style = TextStyle.BODY_LARGE, color = Color.GRAY_600)
 *                 Text(user.email, style = TextStyle.BODY_SMALL, color = Color.GRAY_500)
 *             }
 *
 *             // Actions
 *             Row(modifier = Modifier().gap(Spacing.SM)) {
 *                 Button(
 *                     onClick = { },
 *                     variant = ButtonVariant.PRIMARY,
 *                     modifier = Modifier().flexGrow(1)
 *                 ) {
 *                     Text("Connect")
 *                 }
 *                 Button(
 *                     onClick = { },
 *                     variant = ButtonVariant.OUTLINE,
 *                     modifier = Modifier().flexGrow(1)
 *                 ) {
 *                     Text("Message")
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ## Responsive Design
 *
 * ### Adaptive Card Grid
 * ```kotlin
 * @Composable
 * fun CardGrid(items: List<CardData>) {
 *     Column(
 *         modifier = Modifier()
 *             .gap(Spacing.LG)
 *             .mediaQuery(MediaQuery.MD) {
 *                 displayGrid()
 *                 gridTemplateColumns("repeat(2, 1fr)")
 *             }
 *             .mediaQuery(MediaQuery.LG) {
 *                 gridTemplateColumns("repeat(3, 1fr)")
 *             }
 *     ) {
 *         items.forEach { item ->
 *             Card(
 *                 modifier = Modifier()
 *                     .width(Width.FULL)
 *                     .borderRadius("12px")
 *                     .backgroundColor(Color.WHITE)
 *                     .shadow("0 2px 4px rgba(0,0,0,0.1)")
 *                     .hover {
 *                         transform("translateY(-2px)")
 *                         shadow("0 4px 8px rgba(0,0,0,0.15)")
 *                     }
 *             ) {
 *                 CardContent(item)
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @param modifier Modifier for applying styling and layout properties to the card
 * @param elevation Optional elevation for shadow effect (CSS box-shadow)
 * @param borderRadius Optional border radius for rounded corners
 * @param onClick Optional click handler for interactive cards (Not directly handled by Card itself, apply via modifier)
 * @param content Composable content to be contained within the card
 *
 * @see Box for basic containers
 * @see Column for vertical card layouts
 * @see Row for horizontal card arrangements
 * @see Button for interactive card actions
 *
 * @sample CardSamples.simpleCard
 * @sample CardSamples.productCard
 * @sample CardSamples.interactiveCard
 * @sample CardSamples.elevatedCard
 *
 * @since 1.0.0
 */
@Composable
fun Card(
    modifier: Modifier = Modifier(), // Use Modifier() constructor
    elevation: String = "2px", // Consider using type-safe units or number types
    borderRadius: String = "4px",
    onClick: (() -> Unit)? = null, // onClick needs to be applied via modifier
    content: @Composable () -> Unit
) {
    // Apply styling properties to the modifier
    var finalModifier = modifier
        .shadow("0", elevation, "8px", "rgba(0,0,0,0.1)") // Assuming shadow exists with this signature
        .borderRadius(borderRadius)

    // Apply onClick via modifier if provided
    onClick?.let { finalModifier = finalModifier.onClick(it) }

    // Get the correct PlatformRenderer
    val renderer = LocalPlatformRenderer.current // No cast needed

    // Call renderCard, wrapping content lambda
    renderer.renderCard(
        modifier = finalModifier,
        content = { // Wrap content lambda
            content()
        }
    )
} 