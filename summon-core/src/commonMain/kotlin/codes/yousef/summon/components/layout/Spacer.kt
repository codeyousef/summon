package codes.yousef.summon.components.layout

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * # Spacer
 *
 * A layout component that creates empty space with specified dimensions, providing precise
 * control over spacing between elements in layouts.
 *
 * ## Overview
 *
 * Spacer creates flexible or fixed spacing by:
 * - **Fixed spacing** - Exact pixel or unit-based spacing
 * - **Flexible spacing** - Growing/shrinking space that adapts to available room
 * - **Responsive spacing** - Different spacing values at different breakpoints
 * - **Layout integration** - Works seamlessly with Flexbox and Grid layouts
 *
 * ## Basic Usage
 *
 * ### Fixed Spacing
 * ```kotlin
 * @Composable
 * fun VerticalLayout() {
 *     Column {
 *         Text("First item")
 *         Spacer(modifier = Modifier().height("20px"))
 *         Text("Second item")
 *         Spacer(modifier = Modifier().height(Spacing.LG))
 *         Text("Third item")
 *     }
 * }
 * ```
 *
 * ### Flexible Spacing
 * ```kotlin
 * @Composable
 * fun FlexibleLayout() {
 *     Row(modifier = Modifier().width(Width.FULL)) {
 *         Text("Left")
 *         Spacer(modifier = Modifier().flexGrow(1)) // Grows to fill space
 *         Text("Right")
 *     }
 * }
 * ```
 *
 * ### Responsive Spacing
 * ```kotlin
 * @Composable
 * fun ResponsiveSpacing() {
 *     Column {
 *         Text("Title")
 *         Spacer(
 *             modifier = Modifier()
 *                 .height(Spacing.SM)
 *                 .mediaQuery(MediaQuery.MD) {
 *                     height(Spacing.LG)
 *                 }
 *                 .mediaQuery(MediaQuery.LG) {
 *                     height(Spacing.XL)
 *                 }
 *         )
 *         Text("Content")
 *     }
 * }
 * ```
 *
 * @param modifier The modifier to be applied to this component. Use width/height modifiers to control spacing dimensions.
 *
 * @see Column for vertical layouts
 * @see Row for horizontal layouts
 * @see Box for positioned layouts
 *
 * @sample SpacerSamples.fixedSpacing
 * @sample SpacerSamples.flexibleSpacing
 * @sample SpacerSamples.responsiveSpacing
 *
 * @since 1.0.0
 */
@Composable
fun Spacer(
    modifier: Modifier = Modifier()
) {
    // Use platform renderer directly
    val renderer = LocalPlatformRenderer.current
    renderer.renderSpacer(modifier)
} 