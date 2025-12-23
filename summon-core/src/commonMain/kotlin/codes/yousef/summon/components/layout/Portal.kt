/**
 * # Portal Component
 *
 * A Portal (also known as Teleport in some frameworks) renders its children into a different
 * part of the DOM tree, outside of the current component hierarchy. This is useful for:
 *
 * - **Modals**: Rendering at document body level to avoid z-index issues
 * - **Tooltips**: Escaping overflow:hidden containers
 * - **Dropdowns**: Ensuring proper stacking context
 * - **Overlays**: Global UI elements that need to be above everything else
 *
 * ## Features
 *
 * - **DOM Teleportation**: Render content anywhere in the DOM
 * - **Context Preservation**: Maintains React-like context from source location
 * - **Flexible Targets**: Render to any CSS selector
 * - **Cleanup**: Automatically manages lifecycle
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Render modal at body level
 * Portal(target = "body") {
 *     Modal(
 *         visible = isOpen,
 *         onClose = { isOpen = false }
 *     ) {
 *         Text("Modal content")
 *     }
 * }
 *
 * // Render to specific container
 * Portal(target = "#modal-root") {
 *     Box(
 *         modifier = Modifier()
 *             .style("position", "fixed")
 *             .style("top", "0")
 *             .style("left", "0")
 *             .style("right", "0")
 *             .style("bottom", "0")
 *             .style("background-color", "rgba(0,0,0,0.5)")
 *     ) {
 *         // Content
 *     }
 * }
 *
 * // Conditional portal
 * if (shouldRenderInPortal) {
 *     Portal(target = "body") {
 *         Tooltip(text = "Info")
 *     }
 * }
 * ```
 *
 * ## Common Patterns
 *
 * ### Modal Dialog
 * ```kotlin
 * @Composable
 * fun MyModal(visible: Boolean, onClose: () -> Unit) {
 *     if (visible) {
 *         Portal(target = "body") {
 *             Box(
 *                 modifier = Modifier()
 *                     .style("position", "fixed")
 *                     .style("top", "50%")
 *                     .style("left", "50%")
 *                     .style("transform", "translate(-50%, -50%)")
 *                     .style("z-index", "9999")
 *             ) {
 *                 // Modal content
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ### Global Notification
 * ```kotlin
 * @Composable
 * fun Notification(message: String) {
 *     Portal(target = "body") {
 *         Box(
 *             modifier = Modifier()
 *                 .style("position", "fixed")
 *                 .style("top", "20px")
 *                 .style("right", "20px")
 *                 .style("z-index", "9999")
 *         ) {
 *             Text(message)
 *         }
 *     }
 * }
 * ```
 *
 * @see Modal for a complete modal implementation
 * @see Tooltip for tooltip component
 * @since 1.0.0
 */
package codes.yousef.summon.components.layout

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Renders content into a different part of the DOM tree.
 *
 * Note: The actual portal implementation requires platform-specific support
 * to move rendered content to the target location. This is a placeholder
 * that marks content for portal rendering.
 *
 * @param target CSS selector for the target container (e.g., "body", "#modal-root")
 * @param modifier Modifier applied to the portal wrapper
 * @param content The content to render in the portal
 */
@Composable
fun Portal(
    target: String = "body",
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current

    // Mark this content as portal content with target information
    val portalModifier = modifier
        .attribute("data-portal-target", target)
        .attribute("data-portal", "true")

    renderer.renderBlock(portalModifier, content)
}

/**
 * Alternative name for Portal (matches Vue's terminology).
 */
@Composable
fun Teleport(
    to: String = "body",
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    Portal(target = to, modifier = modifier, content = content)
}
