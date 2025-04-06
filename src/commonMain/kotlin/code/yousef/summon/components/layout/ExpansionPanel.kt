package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.core.UIElement

/**
 * A layout composable that creates a collapsible panel with a header and expandable content.
 * ExpansionPanel is useful for FAQ sections, settings panels, and other UIs where
 * information needs to be progressively disclosed.
 *
 * @param title The title to display in the header.
 * @param modifier The modifier to apply to this composable.
 * @param isExpanded Whether the panel is initially expanded.
 * @param onToggle Callback function invoked when the expanded state changes.
 * @param icon Optional composable lambda for an icon in the header.
 * @param content The composable content lambda to display when expanded.
 */
@Composable
fun ExpansionPanel(
    title: String,
    modifier: Modifier = Modifier(),
    isExpanded: Boolean = false,
    onToggle: (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val expansionPanelData = ExpansionPanelData(
        title = title,
        modifier = modifier,
        isExpanded = isExpanded,
        onToggle = onToggle,
        icon = icon,
        // Note: content lambda is passed separately to UIElement/composer
    )

    // TODO: Implement rendering logic for header, toggle, icon, and conditional content display.
    println("Composable ExpansionPanel function called with title: $title, expanded: $isExpanded")

    // Placeholder: Using UIElement. Structure might need specific renderer handling.
    UIElement(
        factory = { expansionPanelData },
        update = { /* Update logic */ },
        content = {
            // Render header (title, icon, toggle button)
            // Conditionally render content based on isExpanded state
            if (isExpanded) {
                content() // Execute the content lambda if expanded
            }
        }
    )
}

/**
 * Internal data class holding non-content parameters for ExpansionPanel.
 */
internal data class ExpansionPanelData(
    val title: String,
    val modifier: Modifier,
    val isExpanded: Boolean,
    val onToggle: (() -> Unit)?,
    val icon: @Composable (() -> Unit)?
    // content lambda is handled by the composition process, not stored here directly
) 