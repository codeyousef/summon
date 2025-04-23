package code.yousef.summon.accessibility

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Utilities for accessibility-related tasks.
 */
object AccessibilityUtils {
    /**
     * Basic node types in the accessibility tree
     */
    enum class NodeRole {
        BUTTON,
        CHECKBOX,
        COMBOBOX,
        DIALOG,
        GRID,
        HEADING,
        LINK,
        LISTBOX,
        MENU,
        MENUITEM,
        NAVIGATION,
        PROGRESSBAR,
        RADIOGROUP,
        REGION,
        SEARCH,
        SLIDER,
        SWITCH,
        TAB,
        TABLIST,
        TABPANEL,
        TEXTBOX,
        TOOLTIP
    }

    /**
     * Creates a Modifier with the specified ARIA role.
     */
    fun createRoleModifier(role: NodeRole): Modifier {
        return Modifier().role(role.name.lowercase())
    }

    /**
     * Creates a Modifier with custom ARIA role.
     */
    fun createRoleModifier(customRole: String): Modifier {
        return Modifier().role(customRole)
    }

    /**
     * Adds an ARIA role to a Modifier.
     */
    private fun Modifier.role(role: String): Modifier {
        return this.style("__attr:role", role)
    }

    /**
     * Creates a Modifier with an accessible label.
     */
    fun createLabelModifier(label: String): Modifier {
        return Modifier().style("__attr:aria-label", label)
    }

    /**
     * Creates a Modifier that describes the accessible relationship
     * between elements.
     */
    fun createRelationshipModifier(
        relation: String,
        targetId: String
    ): Modifier {
        return Modifier().style("__attr:aria-$relation", targetId)
    }

    /**
     * Inspects and extracts all accessibility-related attributes from a Modifier.
     *
     * @param modifier The modifier to inspect
     * @return A map of accessibility attribute names to their values
     */
    fun inspectAccessibility(modifier: Modifier): Map<String, String> {
        return modifier.styles.entries
            .filter {
                it.key.startsWith("__attr:") && (
                        it.key.contains("aria-") || it.key.contains("role") ||
                                it.key.contains("tabindex") || it.key.contains("disabled")
                        )
            }
            .associate {
                it.key.removePrefix("__attr:") to it.value
            }
    }
}

// Extension function to make the API more intuitive in Kotlin
fun Modifier.inspectAccessibility(): Map<String, String> {
    return AccessibilityUtils.inspectAccessibility(this)
}

data class AccessibilityNode(
    val role: Role,
    val label: String?, // aria-label
    val description: String?, // aria-describedby (reference to ID)
    val state: Map<State, Boolean> = emptyMap(),
    val properties: Map<String, String> = emptyMap(), // Other aria-* attributes
    val children: List<AccessibilityNode> = emptyList(),
    val modifier: Modifier = Modifier() // Modifier applied to the element itself
)

enum class Role {
    // Document Structure Roles
    ARTICLE, COLUMNHEADER, DEFINITION, DIRECTORY, DOCUMENT, GROUP, HEADING,
    IMG, LIST, LISTITEM, MATH, NOTE, PRESENTATION, REGION, ROW, ROWGROUP,
    ROWHEADER, SEPARATOR, TABLE, TOOLBAR,

    // Widget Roles
    ALERT, ALERTDIALOG, BUTTON, CHECKBOX, DIALOG, GRIDCELL, LINK, LOG, MARQUEE,
    MENUITEM, MENUITEMCHECKBOX, MENUITEMRADIO, OPTION, PROGRESSBAR, RADIO,
    SCROLLBAR, SEARCHBOX, SLIDER, SPINBUTTON, STATUS, SWITCH, TAB, TABPANEL,
    TEXTBOX, TIMER, TOOLTIP, TREEITEM,

    // Landmark Roles
    BANNER, COMPLEMENTARY, CONTENTINFO, FORM, MAIN, NAVIGATION, SEARCH,

    // Live Region Roles
    APPLICATION, LANDMARK
}

enum class State {
    BUSY, CHECKED, DISABLED, EXPANDED, GRABBED, HIDDEN, INVALID, PRESSED,
    SELECTED
}

/**
 * Represents the root of an accessibility tree for a composable subtree.
 * Holds the structure, not directly renderable UI.
 */
class AccessibilityTree(val rootNode: AccessibilityNode) {
    // Logic to traverse or query the tree can go here.
}

/**
 * A composable function intended to *apply* accessibility properties from an
 * AccessibilityNode to its child content during composition.
 */
@Composable
fun ApplyAccessibilityNode(
    node: AccessibilityNode,
    content: @Composable () -> Unit
) {
    // Get current platform renderer
    val renderer = LocalPlatformRenderer.current

    // Apply accessibility attributes to the modifier
    val accessibilityModifier = node.modifier
        .applyAccessibilityAttributes(node)

    // Use the platform renderer to create a container with the accessibility attributes
    // and render the content inside it
    renderer.renderBox(
        modifier = accessibilityModifier,
        content = { content() }
    )
}

// --- Helper Functions --- (Might need review/update based on usage)

/**
 * Builds an AccessibilityNode from a composable function.
 * WARNING: This approach is likely flawed for the new composition system.
 */
fun buildAccessibilityNode(composable: @Composable () -> Unit): AccessibilityNode {
    println("Warning: buildAccessibilityNode is likely incorrect in the new composition system.")
    return AccessibilityNode(
        role = Role.PRESENTATION, // Default/unknown
        label = null,
        description = null
    )
}

/**
 * Finds the first AccessibilityNode matching a predicate.
 */
fun AccessibilityNode.findNode(predicate: (AccessibilityNode) -> Boolean): AccessibilityNode? {
    if (predicate(this)) return this
    return children.firstNotNullOfOrNull { it.findNode(predicate) }
}

/**
 * Finds all AccessibilityNodes matching a predicate.
 */
fun AccessibilityNode.findAllNodes(predicate: (AccessibilityNode) -> Boolean): List<AccessibilityNode> {
    val result = mutableListOf<AccessibilityNode>()
    if (predicate(this)) result.add(this)
    children.forEach { result.addAll(it.findAllNodes(predicate)) }
    return result
}

@Composable
fun Semantics(
    modifier: Modifier = Modifier(),
    mergeDescendants: Boolean = false,
    clearAndSetSemantics: Boolean = false,
    contentDescription: String? = null,
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    // ... existing code ...
}

/**
 * Extension function for Modifier to apply accessibility attributes from an AccessibilityNode.
 *
 * @param node The AccessibilityNode containing accessibility data
 * @return A modifier with accessibility attributes applied
 */
private fun Modifier.applyAccessibilityAttributes(node: AccessibilityNode): Modifier {
    var result = this

    // Apply role attribute
    result = result.style("__attr:role", node.role.name.lowercase())

    // Apply label as aria-label if available
    node.label?.let {
        result = result.style("__attr:aria-label", it)
    }

    // Apply description as aria-describedby if available
    node.description?.let {
        result = result.style("__attr:aria-describedby", it)
    }

    // Apply state attributes
    for ((state, isActive) in node.state) {
        when (state) {
            State.BUSY -> if (isActive) result = result.style("__attr:aria-busy", "true")
            State.CHECKED -> if (isActive) result = result.style("__attr:aria-checked", "true")
            State.DISABLED -> if (isActive) {
                result = result.style("__attr:aria-disabled", "true")
                result = result.style("__attr:disabled", "")
            }

            State.EXPANDED -> if (isActive) result = result.style("__attr:aria-expanded", "true")
            State.GRABBED -> if (isActive) result = result.style("__attr:aria-grabbed", "true")
            State.HIDDEN -> if (isActive) result = result.style("__attr:aria-hidden", "true")
            State.INVALID -> if (isActive) result = result.style("__attr:aria-invalid", "true")
            State.PRESSED -> if (isActive) result = result.style("__attr:aria-pressed", "true")
            State.SELECTED -> if (isActive) result = result.style("__attr:aria-selected", "true")
        }
    }

    // Apply all custom properties
    for ((name, value) in node.properties) {
        result = result.style("__attr:$name", value)
    }

    return result
}
