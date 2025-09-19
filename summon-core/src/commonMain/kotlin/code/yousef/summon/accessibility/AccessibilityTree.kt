/**
 * # Accessibility Tree Package
 *
 * This package provides comprehensive accessibility support for the Summon UI framework,
 * including ARIA attributes, accessibility tree management, and assistive technology integration.
 *
 * ## Overview
 *
 * The accessibility package enables developers to create inclusive applications that work
 * seamlessly with screen readers, keyboard navigation, and other assistive technologies.
 * It provides:
 *
 * - **Accessibility Tree**: Hierarchical representation of UI elements for assistive technology
 * - **ARIA Support**: Comprehensive ARIA roles, properties, and states
 * - **Semantic Roles**: Predefined roles for common UI patterns
 * - **Accessibility Node Management**: Building and querying accessibility information
 * - **Modifier Integration**: Seamless integration with Summon's modifier system
 *
 * ## Key Components
 *
 * - [AccessibilityTree] - Root container for accessibility structure
 * - [AccessibilityNode] - Individual elements in the accessibility tree
 * - [Role] - ARIA roles for semantic meaning
 * - [State] - ARIA states for dynamic properties
 * - [AccessibilityUtils] - Utilities for working with accessibility
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Create accessible button
 * Button(
 *     onClick = { /* action */ },
 *     modifier = Modifier()
 *         .accessibilityRole(Role.BUTTON)
 *         .accessibilityLabel("Save document")
 *         .accessibilityState(State.DISABLED, false)
 * ) {
 *     Text("Save")
 * }
 *
 * // Build accessibility tree
 * val node = AccessibilityNode(
 *     role = Role.BUTTON,
 *     label = "Submit form",
 *     state = mapOf(State.DISABLED to false),
 *     properties = mapOf("aria-describedby" to "help-text")
 * )
 * ```
 *
 * @since 1.0.0
 */
package code.yousef.summon.accessibility

import code.yousef.summon.accessibility.AccessibilityUtils.createRelationshipModifier
import code.yousef.summon.accessibility.AccessibilityUtils.createRoleModifier
import code.yousef.summon.accessibility.Role.*
import code.yousef.summon.accessibility.State.*
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Comprehensive utilities for accessibility-related tasks in the Summon framework.
 *
 * AccessibilityUtils provides a centralized API for working with accessibility features,
 * including ARIA attribute management, accessibility tree inspection, and modifier creation
 * for assistive technology support.
 *
 * ## Key Features
 *
 * - **Role Management**: Apply semantic ARIA roles to UI elements
 * - **Label Creation**: Add accessible labels and descriptions
 * - **Relationship Mapping**: Define relationships between UI elements
 * - **Accessibility Inspection**: Query and validate accessibility properties
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Create accessible form field
 * val fieldModifier = AccessibilityUtils.createRoleModifier(NodeRole.TEXTBOX)
 *     .plus(AccessibilityUtils.createLabelModifier("Email address"))
 *     .plus(AccessibilityUtils.createRelationshipModifier("describedby", "email-help"))
 *
 * TextField(
 *     value = email,
 *     onValueChange = { email = it },
 *     modifier = fieldModifier
 * )
 * ```
 *
 * @see AccessibilityNode
 * @see Role
 * @see State
 * @since 1.0.0
 */
object AccessibilityUtils {
    /**
     * Fundamental node types in the accessibility tree for common UI elements.
     *
     * This enum provides a simplified, type-safe interface for the most commonly used
     * ARIA roles in modern web applications. Each role communicates semantic meaning
     * to assistive technologies like screen readers.
     *
     * ## Usage Categories
     *
     * - **Interactive Elements**: BUTTON, CHECKBOX, SLIDER, SWITCH
     * - **Form Controls**: TEXTBOX, COMBOBOX, RADIOGROUP
     * - **Navigation**: LINK, MENU, MENUITEM, TAB, TABLIST
     * - **Information**: HEADING, TOOLTIP, PROGRESSBAR
     * - **Containers**: DIALOG, GRID, LISTBOX, NAVIGATION, REGION
     *
     * ## Example
     *
     * ```kotlin
     * val buttonModifier = AccessibilityUtils.createRoleModifier(NodeRole.BUTTON)
     * val headingModifier = AccessibilityUtils.createRoleModifier(NodeRole.HEADING)
     * ```
     *
     * @see Role for the complete ARIA specification-compliant enum
     * @since 1.0.0
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
     * Creates a modifier with the specified ARIA role for semantic meaning.
     *
     * ARIA roles communicate the purpose and behavior of UI elements to assistive
     * technologies. This function creates a modifier that applies the appropriate
     * role attribute to the rendered element.
     *
     * ## Role Application
     *
     * The role is automatically converted to lowercase and applied as a `role`
     * attribute in the HTML output. For example:
     * - `NodeRole.BUTTON` becomes `role="button"`
     * - `NodeRole.TEXTBOX` becomes `role="textbox"`
     *
     * ## Example
     *
     * ```kotlin
     * // Create a custom button with proper semantics
     * Box(
     *     modifier = AccessibilityUtils.createRoleModifier(NodeRole.BUTTON)
     *         .onClick { /* handle click */ }
     * ) {
     *     Text("Custom Button")
     * }
     * ```
     *
     * @param role The semantic role for this UI element
     * @return A modifier with the ARIA role applied
     * @see NodeRole for available role types
     * @since 1.0.0
     */
    fun createRoleModifier(role: NodeRole): Modifier {
        return Modifier().role(role.name.lowercase())
    }

    /**
     * Creates a modifier with a custom ARIA role for specialized use cases.
     *
     * While [createRoleModifier(NodeRole)] covers common roles, this function allows
     * you to specify any valid ARIA role, including experimental or specialized roles
     * not covered by the [NodeRole] enum.
     *
     * ## Custom Role Usage
     *
     * Use this function when you need:
     * - Experimental ARIA roles not yet in the standard
     * - Application-specific semantic roles
     * - Roles from newer ARIA specifications
     *
     * ## Example
     *
     * ```kotlin
     * // Use a specialized role for a data visualization
     * val chartModifier = AccessibilityUtils.createRoleModifier("treegrid")
     *
     * // Use an experimental role
     * val customModifier = AccessibilityUtils.createRoleModifier("application")
     * ```
     *
     * @param customRole A valid ARIA role string
     * @return A modifier with the custom ARIA role applied
     * @see createRoleModifier for common predefined roles
     * @since 1.0.0
     */
    fun createRoleModifier(customRole: String): Modifier {
        return Modifier().role(customRole)
    }

    /**
     * Adds an ARIA role to a Modifier.
     */
    private fun Modifier.role(role: String): Modifier {
        return this.copy(attributes = this.attributes + mapOf("role" to role))
    }

    /**
     * Creates a modifier with an accessible label for screen reader users.
     *
     * Accessible labels provide textual descriptions of UI elements that might not
     * have visible text or need additional context for assistive technology users.
     * The label is applied as an `aria-label` attribute.
     *
     * ## When to Use Labels
     *
     * - Icons or image buttons without visible text
     * - Form inputs where the visual label is insufficient
     * - Complex widgets that need additional context
     * - Elements where visible text doesn't fully describe the function
     *
     * ## Example
     *
     * ```kotlin
     * // Icon button with accessible label
     * Button(
     *     onClick = { saveDocument() },
     *     modifier = AccessibilityUtils.createLabelModifier("Save document")
     * ) {
     *     Icon("save")
     * }
     *
     * // Search input with descriptive label
     * TextField(
     *     value = query,
     *     onValueChange = { query = it },
     *     modifier = AccessibilityUtils.createLabelModifier("Search products by name or category")
     * )
     * ```
     *
     * @param label The accessible label text for screen readers
     * @return A modifier with the aria-label attribute applied
     * @see createRelationshipModifier for referencing separate label elements
     * @since 1.0.0
     */
    fun createLabelModifier(label: String): Modifier {
        val modifier = Modifier()
        return modifier.copy(attributes = modifier.attributes + mapOf("aria-label" to label))
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
        return modifier.copy(attributes = modifier.attributes + mapOf("aria-$relation" to targetId))
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

/**
 * Represents a node in the accessibility tree with comprehensive ARIA support.
 *
 * An AccessibilityNode encapsulates all accessibility-related information for a UI element,
 * including its semantic role, accessible labels, dynamic states, and relationships with
 * other elements. This structure mirrors the accessibility tree that assistive technologies
 * use to understand application structure.
 *
 * ## Node Hierarchy
 *
 * AccessibilityNodes can contain child nodes, forming a tree structure that represents
 * the logical hierarchy of UI elements. This tree may differ from the visual layout,
 * focusing instead on logical relationships for screen reader navigation.
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Create a form field with label and help text
 * val emailField = AccessibilityNode(
 *     role = Role.TEXTBOX,
 *     label = "Email address",
 *     description = "email-help-text",
 *     state = mapOf(
 *         State.REQUIRED to true,
 *         State.INVALID to false
 *     ),
 *     properties = mapOf(
 *         "aria-describedby" to "email-help",
 *         "aria-autocomplete" to "email"
 *     )
 * )
 *
 * // Create a complex widget with children
 * val tabPanel = AccessibilityNode(
 *     role = Role.TABPANEL,
 *     label = "Settings",
 *     children = listOf(
 *         AccessibilityNode(role = Role.HEADING, label = "General Settings"),
 *         AccessibilityNode(role = Role.BUTTON, label = "Save Changes")
 *     )
 * )
 * ```
 *
 * @property role The semantic ARIA role that defines the element's purpose
 * @property label The accessible label (aria-label) for screen readers
 * @property description Reference to element that describes this node (aria-describedby)
 * @property state Map of ARIA states with their boolean values (e.g., checked, expanded)
 * @property properties Additional ARIA attributes and their string values
 * @property children List of child nodes in the accessibility hierarchy
 * @property modifier Summon modifier applied to the visual element
 * @see Role for available semantic roles
 * @see State for available accessibility states
 * @since 1.0.0
 */
data class AccessibilityNode(
    val role: Role,
    val label: String?, // aria-label
    val description: String?, // aria-describedby (reference to ID)
    val state: Map<State, Boolean> = emptyMap(),
    val properties: Map<String, String> = emptyMap(), // Other aria-* attributes
    val children: List<AccessibilityNode> = emptyList(),
    val modifier: Modifier = Modifier() // Modifier applied to the element itself
)

/**
 * Comprehensive ARIA roles based on the W3C ARIA specification.
 *
 * ARIA roles define the semantic meaning and behavior of UI elements for assistive
 * technologies. This enum provides type-safe access to all standard ARIA roles,
 * organized by category for easier discovery and usage.
 *
 * ## Role Categories
 *
 * ### Document Structure Roles
 * Define the structure and organization of content:
 * - [ARTICLE] - Self-contained content that could be distributed independently
 * - [HEADING] - Section heading content
 * - [LIST] / [LISTITEM] - List containers and items
 * - [TABLE] / [ROW] / [COLUMNHEADER] - Table structures
 *
 * ### Widget Roles
 * Interactive elements that users can operate:
 * - [BUTTON] - Clickable button element
 * - [CHECKBOX] - Two-state checkbox input
 * - [TEXTBOX] - Text input field
 * - [SLIDER] - Range input control
 *
 * ### Landmark Roles
 * Navigation and page structure landmarks:
 * - [MAIN] - Primary content area
 * - [NAVIGATION] - Navigation links
 * - [BANNER] - Site header content
 * - [CONTENTINFO] - Site footer content
 *
 * ### Live Region Roles
 * Dynamic content that updates automatically:
 * - [ALERT] - Important messages requiring immediate attention
 * - [STATUS] - Advisory information about system state
 * - [LOG] - Live scrolling content
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Create a form with proper landmark structure
 * AccessibilityNode(
 *     role = Role.FORM,
 *     label = "User Registration",
 *     children = listOf(
 *         AccessibilityNode(role = Role.TEXTBOX, label = "Email"),
 *         AccessibilityNode(role = Role.TEXTBOX, label = "Password"),
 *         AccessibilityNode(role = Role.BUTTON, label = "Submit")
 *     )
 * )
 * ```
 *
 * @see [W3C ARIA Roles](https://www.w3.org/TR/wai-aria-1.2/#role_definitions)
 * @since 1.0.0
 */
enum class Role {
    // Document Structure Roles
    /** Self-contained composition that could be distributed independently */
    ARTICLE,

    /** Column header in a table or grid */
    COLUMNHEADER,

    /** Definition of a term or concept */
    DEFINITION,

    /** List of references to group members */
    DIRECTORY,

    /** Entire document content */
    DOCUMENT,

    /** Set of UI objects that are not intended to be included in a page summary */
    GROUP,

    /** Heading for a section of the page */
    HEADING,

    /** Image content */
    IMG,

    /** Group of non-interactive list items */
    LIST,

    /** Single item in a list */
    LISTITEM,

    /** Mathematical expression */
    MATH,

    /** Section of content that is explanatory */
    NOTE,

    /** Element whose implicit semantics will not be published */
    PRESENTATION,

    /** Perceivable section containing content relevant to a specific purpose */
    REGION,

    /** Row of cells in a tabular container */
    ROW,

    /** Group containing one or more row elements */
    ROWGROUP,

    /** Cell containing header information for a row */
    ROWHEADER,

    /** Divider that separates sections of content */
    SEPARATOR,

    /** Section containing data arranged in rows and columns */
    TABLE,

    /** Collection of commonly used function buttons */
    TOOLBAR,

    // Widget Roles
    /** Message with important, usually time-sensitive, information */
    ALERT,

    /** Alert dialog that contains an alert message */
    ALERTDIALOG,

    /** Input that allows for user-triggered actions */
    BUTTON,

    /** Checkable input with three possible values: true, false, or mixed */
    CHECKBOX,

    /** Dialog window or modal overlay */
    DIALOG,

    /** Cell in a grid or treegrid */
    GRIDCELL,

    /** Interactive reference to an internal or external resource */
    LINK,

    /** Live region where new information is added in meaningful order */
    LOG,

    /** Live region with scrolling text */
    MARQUEE,

    /** Option in a set of choices contained by a menu or menubar */
    MENUITEM,

    /** Menuitem with a checkable state */
    MENUITEMCHECKBOX,

    /** Checkable menuitem in a set of elements with the same role */
    MENUITEMRADIO,

    /** Selectable item in a select list */
    OPTION,

    /** Element that displays the progress status for tasks */
    PROGRESSBAR,

    /** Checkable input in a group of radio roles */
    RADIO,

    /** Scrollbar control */
    SCROLLBAR,

    /** Textbox intended for specifying search criteria */
    SEARCHBOX,

    /** Input where the user selects a value from within a given range */
    SLIDER,

    /** Form of range that expects the user to select from among discrete choices */
    SPINBUTTON,

    /** Live region whose content is advisory information */
    STATUS,

    /** Checkable input that communicates if a value is on or off */
    SWITCH,

    /** Grouping label providing a title for its related tabpanel */
    TAB,

    /** Container for the resources associated with a tab */
    TABPANEL,

    /** Input that allows free-form text as its value */
    TEXTBOX,

    /** Live region containing time sensitive information */
    TIMER,

    /** Contextual popup that displays a description for an element */
    TOOLTIP,

    /** Option item of a tree */
    TREEITEM,

    // Landmark Roles
    /** Header content of a page */
    BANNER,

    /** Supporting section that relates to the main content */
    COMPLEMENTARY,

    /** Information about the parent document */
    CONTENTINFO,

    /** Landmark region that contains a collection of items for user input */
    FORM,

    /** Main content of the document */
    MAIN,

    /** Collection of navigational elements */
    NAVIGATION,

    /** Landmark region that contains a collection of items for searching */
    SEARCH,

    // Live Region Roles
    /** Container whose elements are assistive technology applications */
    APPLICATION,

    /** Abstract superclass for landmark roles */
    LANDMARK
}

/**
 * ARIA states that represent dynamic properties of UI elements.
 *
 * ARIA states describe the current condition of UI elements and can change
 * frequently during user interaction. Unlike properties, states are typically
 * toggled between true/false values based on user actions or application logic.
 *
 * ## State Categories
 *
 * ### Selection States
 * - [CHECKED] - Whether a checkbox or radio button is selected
 * - [SELECTED] - Whether an option or item is currently selected
 * - [PRESSED] - Whether a toggle button is in pressed state
 *
 * ### Visibility States
 * - [HIDDEN] - Whether the element is hidden from assistive technology
 * - [EXPANDED] - Whether a collapsible element is expanded
 *
 * ### Interaction States
 * - [DISABLED] - Whether the element can be interacted with
 * - [READONLY] - Whether the element's value can be modified
 * - [REQUIRED] - Whether input is required for form submission
 *
 * ### Validation States
 * - [INVALID] - Whether the element's value fails validation
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Create a checkbox with proper states
 * val checkboxNode = AccessibilityNode(
 *     role = Role.CHECKBOX,
 *     label = "Accept terms and conditions",
 *     state = mapOf(
 *         State.CHECKED to isAccepted,
 *         State.REQUIRED to true,
 *         State.DISABLED to !canAccept
 *     )
 * )
 *
 * // Create an expandable section
 * val accordionNode = AccessibilityNode(
 *     role = Role.BUTTON,
 *     label = "Show advanced options",
 *     state = mapOf(
 *         State.EXPANDED to isExpanded
 *     )
 * )
 * ```
 *
 * @see [W3C ARIA States](https://www.w3.org/TR/wai-aria-1.2/#state_prop_def)
 * @since 1.0.0
 */
enum class State {
    /** Whether a checkable element is checked */
    CHECKED,

    /** Whether the element is disabled and cannot be interacted with */
    DISABLED,

    /** Whether a collapsible element is expanded */
    EXPANDED,

    /** Whether the element is hidden from assistive technology */
    HIDDEN,

    /** Whether the element's value is invalid according to validation rules */
    INVALID,

    /** Whether a toggle button is in pressed state */
    PRESSED,

    /** Whether the element's value cannot be modified by the user */
    READONLY,

    /** Whether the element must have a value for form submission */
    REQUIRED,

    /** Whether an option or item is currently selected */
    SELECTED
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
    result = result.copy(attributes = result.attributes + mapOf("role" to node.role.name.lowercase()))

    // Apply label as aria-label if available
    if (node.label != null) {
        result = result.copy(attributes = result.attributes + mapOf("aria-label" to node.label))
    }

    // Apply description as aria-describedby if available
    if (node.description != null) {
        result = result.copy(attributes = result.attributes + mapOf("aria-describedby" to node.description))
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
        result = result.copy(attributes = result.attributes + mapOf(stateName to entry.value.toString()))
    }

    // Apply other properties
    for (entry in node.properties.entries) {
        result = result.copy(attributes = result.attributes + mapOf(entry.key to entry.value))
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