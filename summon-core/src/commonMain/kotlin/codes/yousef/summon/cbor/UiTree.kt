package codes.yousef.summon.cbor

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Represents a platform-agnostic UI tree node.
 * This structure is designed to be serialized (e.g., via CBOR) and sent to a host environment
 * or a different thread/process for rendering.
 */
@Serializable
data class UiTree(
    val root: UiNode
)

/**
 * Represents a single node in the UI tree.
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
    @Serializable
    data class Replace(val nodeId: String, val newNode: UiNode) : UiPatch()

    @Serializable
    data class UpdateProps(val nodeId: String, val props: Map<String, String>) : UiPatch()

    @Serializable
    data class AppendChild(val parentId: String, val child: UiNode) : UiPatch()

    @Serializable
    data class RemoveNode(val nodeId: String) : UiPatch()
    
    @Serializable
    data class UpdateText(val nodeId: String, val text: String) : UiPatch()
}
