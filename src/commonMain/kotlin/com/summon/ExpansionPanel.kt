package com.summon

import kotlinx.html.TagConsumer

/**
 * A layout composable that creates a collapsible panel with a header and expandable content.
 * ExpansionPanel is useful for FAQ sections, settings panels, and other UIs where
 * information needs to be progressively disclosed.
 *
 * @param title The title to display in the header
 * @param content The content to display when expanded
 * @param isExpanded Whether the panel is initially expanded
 * @param onToggle Callback function that is invoked when the expanded state changes
 * @param icon Optional icon to display in the header
 * @param modifier The modifier to apply to this composable
 */
class ExpansionPanel(
    val title: String,
    val content: Composable,
    val isExpanded: Boolean = false,
    val onToggle: (() -> Unit)? = null,
    val icon: Composable? = null,
    val modifier: Modifier = Modifier()
) : Composable, LayoutComponent {
    /**
     * Renders this ExpansionPanel composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderExpansionPanel(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 