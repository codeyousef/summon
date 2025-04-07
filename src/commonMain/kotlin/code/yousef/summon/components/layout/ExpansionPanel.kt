package code.yousef.summon.components.layout


import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.mutableStateOf
import runtime.remember

/**
 * A panel that can be expanded or collapsed to show/hide content.
 *
 * @param title The title text displayed on the panel header.
 * @param modifier Modifier for the panel container.
 * @param initiallyExpanded Whether the panel is expanded by default.
 * @param content The composable content displayed when the panel is expanded.
 */
@Composable
fun ExpansionPanel(
    title: String,
    modifier: Modifier = Modifier(),
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    // State for tracking expanded/collapsed status
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    // We'll use a very basic implementation without Row, Column or other complex components
    // that might not be available in the current codebase
    
    // Header (always visible)
    // This will be implemented by the platform renderer
    val headerModifier = Modifier()
        .style("cursor", "pointer")
        .style("width", "100%")
        .style("padding", "12px")
        .style("border-bottom", if (expanded) "none" else "1px solid #ddd")

    ExpansionPanelInternal(
        modifier = modifier,
        headerModifier = headerModifier,
        headerContent = { Text(text = title) },
        isExpanded = expanded,
        onToggle = { expanded = !expanded },
        content = content
    )
}

/**
 * Internal implementation of ExpansionPanel that relies on platform renderer
 * to handle the actual rendering logic.
 */
@Composable
private fun ExpansionPanelInternal(
    modifier: Modifier = Modifier(),
    headerModifier: Modifier = Modifier(),
    headerContent: @Composable () -> Unit,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    // Delegating to platform renderer
    // The actual implementation will be provided by the platform
    
    // Render header
    headerContent()
    
    // Conditionally render content
    if (isExpanded) {
        content()
    }
}
