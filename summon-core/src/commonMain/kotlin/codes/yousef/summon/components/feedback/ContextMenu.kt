package codes.yousef.summon.components.feedback

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.*
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Portal
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.mutableStateOf
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.getValue
import codes.yousef.summon.state.setValue
import codes.yousef.summon.events.PointerEvent
import codes.yousef.summon.modifier.Position

/**
 * Represents an item in a context menu.
 *
 * @property label The text to display.
 * @property icon Optional icon name.
 * @property onClick Action to perform when clicked.
 */
data class ContextMenuItem(
    val label: String,
    val icon: String? = null,
    val onClick: () -> Unit
)

/**
 * A wrapper that displays a context menu on right-click (or long-press).
 *
 * @param items The list of items to display in the menu.
 * @param modifier Modifier to apply to the wrapper.
 * @param content The content that triggers the context menu.
 */
@Composable
fun ContextMenuArea(
    items: () -> List<ContextMenuItem>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isOpen by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf(Pair(0.0, 0.0)) }

    Box(
        modifier = modifier.onContextMenu { event: PointerEvent ->
            position = Pair(event.clientX, event.clientY)
            isOpen = true
        }
    ) {
        content()
        
        if (isOpen) {
            Portal(target = "body") {
                // Overlay to close on click outside
                Box(
                    modifier = Modifier
                        .position(Position.Fixed)
                        .top("0").left("0").right("0").bottom("0")
                        .onClick { isOpen = false }
                        .zIndex(9998)
                ) {
                    // Empty content for overlay
                }
                
                // Menu
                Box(
                    modifier = Modifier
                        .position(Position.Fixed)
                        .left("${position.first}px")
                        .top("${position.second}px")
                        .backgroundColor("white")
                        .boxShadow("0 2px 10px rgba(0,0,0,0.2)")
                        .borderRadius("4px")
                        .padding("4px 0")
                        .zIndex(9999)
                ) {
                    Column {
                        items().forEach { item ->
                            Box(
                                modifier = Modifier
                                    .padding("8px 16px")
                                    .cursor("pointer")
                                    .hover(Modifier.backgroundColor("#f0f0f0"))
                                    .onClick {
                                        item.onClick()
                                        isOpen = false
                                    }
                            ) {
                                Text(item.label)
                            }
                        }
                    }
                }
            }
        }
    }
}
