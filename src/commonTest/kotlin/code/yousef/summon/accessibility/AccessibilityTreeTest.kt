package code.yousef.summon.accessibility

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.LocalPlatformRenderer
import kotlin.test.*

class AccessibilityTreeTest {

    @Test
    fun testCreateRoleModifier() {
        // Test creating a role modifier with enum
        val roleModifier = AccessibilityUtils.createRoleModifier(AccessibilityUtils.NodeRole.BUTTON)
        assertEquals("button", roleModifier.styles["__attr:role"])
        
        // Test creating a role modifier with custom string
        val customRoleModifier = AccessibilityUtils.createRoleModifier("custom-role")
        assertEquals("custom-role", customRoleModifier.styles["__attr:role"])
    }
    
    @Test
    fun testCreateLabelModifier() {
        val labelModifier = AccessibilityUtils.createLabelModifier("Test Label")
        assertEquals("Test Label", labelModifier.styles["__attr:aria-label"])
    }
    
    @Test
    fun testCreateRelationshipModifier() {
        val relationshipModifier = AccessibilityUtils.createRelationshipModifier("describedby", "element-id")
        assertEquals("element-id", relationshipModifier.styles["__attr:aria-describedby"])
    }
    
    @Test
    fun testInspectAccessibility() {
        // Create a modifier with multiple accessibility attributes
        val modifier = Modifier()
            .style("__attr:role", "button")
            .style("__attr:aria-label", "Test Button")
            .style("__attr:aria-describedby", "description-id")
            .style("__attr:tabindex", "0")
            .style("__attr:disabled", "")
            .style("color", "red") // Non-accessibility attribute
        
        // Inspect accessibility attributes
        val accessibilityAttrs = AccessibilityUtils.inspectAccessibility(modifier)
        
        // Verify the results
        assertEquals(5, accessibilityAttrs.size)
        assertEquals("button", accessibilityAttrs["role"])
        assertEquals("Test Button", accessibilityAttrs["aria-label"])
        assertEquals("description-id", accessibilityAttrs["aria-describedby"])
        assertEquals("0", accessibilityAttrs["tabindex"])
        assertEquals("", accessibilityAttrs["disabled"])
        assertNull(accessibilityAttrs["color"]) // Should not include non-accessibility attributes
    }
    
    @Test
    fun testModifierInspectAccessibilityExtension() {
        // Create a modifier with accessibility attributes
        val modifier = Modifier()
            .style("__attr:role", "button")
            .style("__attr:aria-label", "Test Button")
        
        // Use the extension function
        val accessibilityAttrs = modifier.inspectAccessibility()
        
        // Verify the results
        assertEquals(2, accessibilityAttrs.size)
        assertEquals("button", accessibilityAttrs["role"])
        assertEquals("Test Button", accessibilityAttrs["aria-label"])
    }
    
    @Test
    fun testAccessibilityNodeCreation() {
        // Create an AccessibilityNode
        val node = AccessibilityNode(
            role = Role.BUTTON,
            label = "Test Button",
            description = "Button Description",
            state = mapOf(
                State.DISABLED to true,
                State.PRESSED to false
            ),
            properties = mapOf(
                "aria-haspopup" to "true",
                "aria-controls" to "menu-id"
            ),
            modifier = Modifier().style("background-color", "blue")
        )
        
        // Verify the node properties
        assertEquals(Role.BUTTON, node.role)
        assertEquals("Test Button", node.label)
        assertEquals("Button Description", node.description)
        assertEquals(true, node.state[State.DISABLED])
        assertEquals(false, node.state[State.PRESSED])
        assertEquals("true", node.properties["aria-haspopup"])
        assertEquals("menu-id", node.properties["aria-controls"])
        assertEquals("blue", node.modifier.styles["background-color"])
    }
    
    @Test
    fun testAccessibilityTreeCreation() {
        // Create child nodes
        val childNode1 = AccessibilityNode(
            role = Role.CHECKBOX,
            label = "Option 1",
            description = null
        )
        
        val childNode2 = AccessibilityNode(
            role = Role.CHECKBOX,
            label = "Option 2",
            description = null
        )
        
        // Create parent node with children
        val parentNode = AccessibilityNode(
            role = Role.GROUP,
            label = "Options Group",
            description = null,
            children = listOf(childNode1, childNode2)
        )
        
        // Create the accessibility tree
        val tree = AccessibilityTree(parentNode)
        
        // Verify the tree structure
        assertEquals(Role.GROUP, tree.rootNode.role)
        assertEquals("Options Group", tree.rootNode.label)
        assertEquals(2, tree.rootNode.children.size)
        assertEquals(Role.CHECKBOX, tree.rootNode.children[0].role)
        assertEquals("Option 1", tree.rootNode.children[0].label)
        assertEquals(Role.CHECKBOX, tree.rootNode.children[1].role)
        assertEquals("Option 2", tree.rootNode.children[1].label)
    }
    
    @Test
    fun testFindNode() {
        // Create a tree structure
        val childNode1 = AccessibilityNode(
            role = Role.BUTTON,
            label = "Button 1",
            description = null
        )
        
        val childNode2 = AccessibilityNode(
            role = Role.LINK,
            label = "Link 1",
            description = null
        )
        
        val parentNode = AccessibilityNode(
            role = Role.NAVIGATION,
            label = "Navigation",
            description = null,
            children = listOf(childNode1, childNode2)
        )
        
        // Find a node by role
        val foundNode = parentNode.findNode { it.role == Role.LINK }
        
        // Verify the found node
        assertNotNull(foundNode)
        assertEquals(Role.LINK, foundNode.role)
        assertEquals("Link 1", foundNode.label)
        
        // Try to find a non-existent node
        val notFoundNode = parentNode.findNode { it.role == Role.CHECKBOX }
        assertNull(notFoundNode)
    }
    
    @Test
    fun testFindAllNodes() {
        // Create a tree structure with multiple nodes of the same role
        val buttonNode1 = AccessibilityNode(
            role = Role.BUTTON,
            label = "Button 1",
            description = null
        )
        
        val buttonNode2 = AccessibilityNode(
            role = Role.BUTTON,
            label = "Button 2",
            description = null
        )
        
        val linkNode = AccessibilityNode(
            role = Role.LINK,
            label = "Link 1",
            description = null
        )
        
        val parentNode = AccessibilityNode(
            role = Role.NAVIGATION,
            label = "Navigation",
            description = null,
            children = listOf(buttonNode1, buttonNode2, linkNode)
        )
        
        // Find all button nodes
        val buttonNodes = parentNode.findAllNodes { it.role == Role.BUTTON }
        
        // Verify the found nodes
        assertEquals(2, buttonNodes.size)
        assertEquals("Button 1", buttonNodes[0].label)
        assertEquals("Button 2", buttonNodes[1].label)
        
        // Find all nodes with a specific label pattern
        val nodesWithLabel1 = parentNode.findAllNodes { it.label?.contains("1") == true }
        assertEquals(2, nodesWithLabel1.size)
        assertTrue(nodesWithLabel1.any { it.label == "Button 1" })
        assertTrue(nodesWithLabel1.any { it.label == "Link 1" })
    }
    
    // Note: Testing ApplyAccessibilityNode would require mocking the PlatformRenderer,
    // which is beyond the scope of this basic test suite.
}