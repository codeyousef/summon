package codes.yousef.summon.components.layout

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.getPlatformRenderer

/**
 * A layout composable that creates a collapsible panel with a header and expandable content.
 * ExpansionPanel is useful for FAQ sections, settings panels, and other UIs where
 * information needs to be progressively disclosed.
 *
 * @param title The title to display in the header
 * @param isExpanded Whether the panel is initially expanded
 * @param onToggle Callback function that is invoked when the expanded state changes
 * @param icon Optional icon to display in the header
 * @param modifier The modifier to apply to this composable
 * @param content The content to display when expanded
 */
@Composable
fun ExpansionPanel(
    title: String,
    isExpanded: Boolean = false,
    onToggle: (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    getPlatformRenderer().renderExpansionPanel(
        modifier = modifier,
        content = content
    )
}
