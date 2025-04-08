package code.yousef.summon.accessibility

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider

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
        return this.applyAria("role", role)
    }

    /**
     * Creates a Modifier with an accessible label.
     */
    fun createLabelModifier(label: String): Modifier {
        return Modifier().ariaLabel(label)
    }

    /**
     * Creates a Modifier that describes the accessible relationship
     * between elements.
     */
    fun createRelationshipModifier(
        relation: String,
        targetId: String
    ): Modifier {
        return Modifier().applyAria("aria-$relation", targetId)
    }

    /**
     * Adds an ARIA label to a Modifier.
     */
    private fun Modifier.ariaLabel(label: String): Modifier {
        return this.applyAria("aria-label", label)
    }

    /**
     * Adds an ARIA attribute to a Modifier.
     */
    private fun Modifier.applyAria(attribute: String, value: String): Modifier {
        val attributes = mapOf(attribute to value)
        val currentStyles = this.toStyleString()
        val newModifier = Modifier()

        // Apply current styles and new ARIA attribute
        // This is a workaround since we can't access private styles directly
        if (currentStyles.isNotEmpty()) {
            // Return a new modifier with combined attributes
            // Using the public style interface as a workaround
            return Modifier().applyAttributes(attributes)
        }

        return Modifier().applyAttributes(attributes)
    }

    /**
     * Applies a map of attributes to create a new Modifier.
     */
    fun Modifier.applyAttributes(attributes: Map<String, String>): Modifier {
        // Since we can't directly access styles, we'll use the factory method
        // and combine with the existing modifier
        return Modifier(attributes)
    }

    /**
     * Inspects the accessible properties of a component.
     * @return A map of accessibility attributes and values
     */
    fun inspectAccessibility(modifier: Modifier): Map<String, String> {
        // Extract ARIA attributes from the modifier's style string
        val styleString = modifier.toStyleString()
        val ariaAttributes = mutableMapOf<String, String>()

        // Parse style string to extract accessibility attributes
        // This is a simplified implementation since we can't directly access styles
        return ariaAttributes
    }
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
    val composer = CompositionLocal.currentComposer

    val accessibilityModifier = node.modifier
        // TODO: Add specific accessibility attributes based on node data
        // .accessibilityProperties(role = node.role, label = node.label, ...)

    composer?.startNode() // Start a logical node
    if (composer?.inserting == true) {
        // Render a container (like Box) applying the modifier,
        // or rely on content's root.
     PlatformRendererProvider.getPlatformRenderer().renderBox(modifier = accessibilityModifier)
    }
    content() // Compose the actual UI content
    composer?.endNode() // End logical node
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
