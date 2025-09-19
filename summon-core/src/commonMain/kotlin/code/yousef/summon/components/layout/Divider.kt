package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * # Divider
 *
 * A visual separator component that creates horizontal or vertical lines to separate
 * content sections and improve layout organization and readability.
 *
 * ## Overview
 *
 * Divider provides visual separation with:
 * - **Orientation control** - Horizontal or vertical divider lines
 * - **Customizable appearance** - Color, thickness, and length control
 * - **Responsive design** - Adaptive styling based on context
 * - **Semantic separation** - Improves content organization and accessibility
 *
 * ## Basic Usage
 *
 * ### Horizontal Divider
 * ```kotlin
 * @Composable
 * fun ContentSections() {
 *     Column {
 *         Text("Section 1 content...")
 *         Divider(
 *             modifier = Modifier()
 *                 .width(Width.FULL)
 *                 .margin(vertical = Spacing.MD)
 *         )
 *         Text("Section 2 content...")
 *     }
 * }
 * ```
 *
 * ### Vertical Divider
 * ```kotlin
 * @Composable
 * fun SideBySideSections() {
 *     Row(modifier = Modifier().height("200px")) {
 *         Text("Left content")
 *         Divider(
 *             isVertical = true,
 *             modifier = Modifier()
 *                 .height(Height.FULL)
 *                 .margin(horizontal = Spacing.MD)
 *         )
 *         Text("Right content")
 *     }
 * }
 * ```
 *
 * ### Styled Divider
 * ```kotlin
 * @Composable
 * fun StyledDivider() {
 *     Divider(
 *         thickness = "2px",
 *         color = "#e5e7eb",
 *         modifier = Modifier()
 *             .width("80%")
 *             .margin(Margin.AUTO)
 *             .borderRadius("1px")
 *     )
 * }
 * ```
 *
 * @param isVertical Whether the divider should be vertical (true) or horizontal (false)
 * @param thickness The thickness of the divider in pixels or other CSS units
 * @param color The color of the divider
 * @param length The length of the divider (width for horizontal, height for vertical)
 * @param modifier The modifier to apply to this composable
 *
 * @see Spacer for invisible spacing
 * @see Column for vertical layouts
 * @see Row for horizontal layouts
 *
 * @sample DividerSamples.horizontalDivider
 * @sample DividerSamples.verticalDivider
 * @sample DividerSamples.styledDivider
 *
 * @since 1.0.0
 */
@Composable
fun Divider(
    isVertical: Boolean = false,
    thickness: String = "1px",
    color: String = "#CCCCCC",
    length: String = "100%",
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderDivider(modifier)
}
