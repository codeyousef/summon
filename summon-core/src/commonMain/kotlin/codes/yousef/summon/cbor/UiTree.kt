package codes.yousef.summon.cbor

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Platform-agnostic serializable UI tree.
 *
 * @property root root node sent to the host renderer
 */
@Serializable
data class UiTree(
    val root: UiNode
)

/**
 * One serializable node in a UI tree.
 *
 * @property id stable node identifier
 * @property type renderer component type
 * @property props serialized properties
 * @property children ordered child nodes
 * @property textContent optional text payload
 * @property eventHandlers registered event names
 */
@Serializable
data class UiNode(
    val id: String,
    val type: String,
    val props: Map<String, String> = emptyMap(),
    val children: List<UiNode> = emptyList(),
    val textContent: String? = null,
    // We use a simplified representation for events and other complex data
    val eventHandlers: List<String> = emptyList()
)

/**
 * Represents a diff/patch operation for the UI tree.
 * This allows for efficient updates instead of sending the full tree every time.
 */
@Serializable
sealed class UiPatch {
/**
 * Replaces a node.
 *
 * @property nodeId node being replaced
 * @property newNode replacement subtree
 */
    @Serializable
    data class Replace(val nodeId: String, val newNode: UiNode) : UiPatch()

/**
 * Replaces a node's properties.
 *
 * @property nodeId target node
 * @property props complete replacement properties
 */
    @Serializable
    data class UpdateProps(val nodeId: String, val props: Map<String, String>) : UiPatch()

/**
 * Appends a child.
 *
 * @property parentId destination parent
 * @property child appended subtree
 */
    @Serializable
    data class AppendChild(val parentId: String, val child: UiNode) : UiPatch()

/**
 * Removes a node.
 *
 * @property nodeId removed subtree root
 */
    @Serializable
    data class RemoveNode(val nodeId: String) : UiPatch()

/**
 * Replaces a node's text.
 *
 * @property nodeId target node
 * @property text replacement text
 */
    @Serializable
    data class UpdateText(val nodeId: String, val text: String) : UiPatch()
}
