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
        return this.copy(attributes = this.attributes + ("role" to role))
    }

    /**
     * Creates a Modifier with an accessible label.
     */
    fun createLabelModifier(label: String): Modifier {
        val modifier = Modifier()
        return modifier.copy(attributes = modifier.attributes + ("aria-label" to label))
    }

    /**
     * Creates a Modifier that describes the accessible relationship
     * between elements.
     */
    fun createRelationshipModifier(
        relation: String,
        targetId: String
    ): Modifier {
        val modifier = Modifier()
        return modifier.copy(attributes = modifier.attributes + ("aria-$relation" to targetId))
    }

    /**
     * Inspects and extracts all accessibility-related attributes from a Modifier.
     *
     * @param modifier The modifier to inspect
     * @return A map of accessibility attribute names to their values
     */
    fun inspectAccessibility(modifier: Modifier): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for (entry in modifier.attributes.entries) {
            val key = entry.key
            if (key.contains("aria-") || key == "role" || key == "tabindex" || key == "disabled") {
                result[entry.key] = entry.value
            }
        }
        return result
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
    CHECKED, DISABLED, EXPANDED, HIDDEN, INVALID, PRESSED, READONLY, REQUIRED, SELECTED
}

/**
 * Composable that creates an accessibility-enhanced container.
 * Uses the provided AccessibilityNode to apply the necessary ARIA attributes and roles.
 */
@Composable
fun AccessibilityContainer(
    node: AccessibilityNode,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
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

/**
 * Builds an AccessibilityNode from a composable function.
 */
fun buildAccessibilityNode(composable: @Composable () -> Unit): AccessibilityNode {
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
    for (child in children) {
        val found = child.findNode(predicate)
        if (found != null) return found
    }
    return null
}

/**
 * Finds all AccessibilityNodes matching a predicate.
 */
fun AccessibilityNode.findAllNodes(predicate: (AccessibilityNode) -> Boolean): List<AccessibilityNode> {
    val result = mutableListOf<AccessibilityNode>()
    if (predicate(this)) result.add(this)
    for (child in children) {
        result.addAll(child.findAllNodes(predicate))
    }
    return result
}

@Composable
fun Semantics(
    modifier: Modifier = Modifier(),
    mergeDescendants: Boolean = false,
    clearAndSetSemantics: Boolean = false,
    content: @Composable () -> Unit
) {
    // Basic semantics container for now
    val renderer = LocalPlatformRenderer.current
    renderer.renderBoxContainer(modifier = modifier, content = content)
}

/**
 * Applies accessibility attributes to a Modifier based on the provided AccessibilityNode.
 *
 * @param node The AccessibilityNode containing accessibility data
 * @return A modifier with accessibility attributes applied
 */
private fun Modifier.applyAccessibilityAttributes(node: AccessibilityNode): Modifier {
    var result = this

    // Apply role attribute
    result = result.copy(attributes = result.attributes + ("role" to node.role.name.lowercase()))

    // Apply label as aria-label if available
    if (node.label != null) {
        result = result.copy(attributes = result.attributes + ("aria-label" to node.label))
    }

    // Apply description as aria-describedby if available
    if (node.description != null) {
        result = result.copy(attributes = result.attributes + ("aria-describedby" to node.description))
    }

    // Apply state attributes
    for (entry in node.state.entries) {
        val stateName = when (entry.key) {
            State.CHECKED -> "aria-checked"
            State.DISABLED -> "aria-disabled"
            State.EXPANDED -> "aria-expanded"
            State.HIDDEN -> "aria-hidden"
            State.INVALID -> "aria-invalid"
            State.PRESSED -> "aria-pressed"
            State.READONLY -> "aria-readonly"
            State.REQUIRED -> "aria-required"
            State.SELECTED -> "aria-selected"
        }
        result = result.copy(attributes = result.attributes + (stateName to entry.value.toString()))
    }

    // Apply other properties
    for (entry in node.properties.entries) {
        result = result.copy(attributes = result.attributes + (entry.key to entry.value))
    }

    return result
}

/**
 * Wrapper class for an accessibility tree structure.
 * Provides a root node that represents the entire tree.
 */
data class AccessibilityTree(
    val rootNode: AccessibilityNode
)