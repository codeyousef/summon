package codes.yousef.summon.builder

import codes.yousef.summon.core.registry.JsonBlock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PropertyBridgeContractTest {
    private val leaf = JsonBlock("Text", mapOf("__nodeId" to "leaf", "text" to "old"))
    private val nested = JsonBlock("Column", mapOf("__nodeId" to "nested"), listOf(leaf))
    private val root = JsonBlock("Column", mapOf("__nodeId" to "root", "name" to "Root"), listOf(nested))

    @BeforeTest
    fun setUp() {
        PropertyBridge.clear()
        JsonTreeHistoryManager.clear()
        PropertyBridge.onPropertyChange = null
    }

    @AfterTest
    fun tearDown() {
        PropertyBridge.onPropertyChange = null
        PropertyBridge.clear()
        JsonTreeHistoryManager.clear()
    }

    @Test
    fun boundPropertiesUpdateNotifyAndRemainSeparateFromTreeProperties() {
        val changes = mutableListOf<Triple<String, String, Any?>>()
        PropertyBridge.onPropertyChange = { component, property, value -> changes += Triple(component, property, value) }
        PropertyBridge.bindProperty("bound", "text", "initial")
        assertEquals("initial", PropertyBridge.getProperty("bound", "text"))
        assertTrue(PropertyBridge.updateProperty("bound", "text", "updated"))
        assertTrue(PropertyBridge.updateProperties("bound", mapOf("count" to 2, "enabled" to true)))
        assertEquals(mapOf("text" to "updated", "count" to 2, "enabled" to true), PropertyBridge.getAllProperties("bound"))
        assertEquals(3, changes.size)

        PropertyBridge.removeProperty("bound", "count")
        assertNull(PropertyBridge.getProperty("bound", "count"))
        PropertyBridge.removeAllProperties("bound")
        assertEquals(emptyMap(), PropertyBridge.getAllProperties("bound"))
        assertFalse(PropertyBridge.updateProperty("missing", "text", "value"))
        assertFalse(PropertyBridge.updateProperties("missing", mapOf("text" to "value")))
    }

    @Test
    fun nestedTreeUpdatesAddsMovesRemovesAndSynchronizesHistory() {
        PropertyBridge.currentTree.value = listOf(root)
        JsonTreeHistoryManager.initialize(listOf(root))

        assertEquals("old", PropertyBridge.getProperty("leaf", "text"))
        assertEquals(mapOf("__nodeId" to "root", "name" to "Root"), PropertyBridge.getAllProperties("root"))
        assertTrue(PropertyBridge.updateProperty("leaf", "text", "new"))
        assertEquals("new", PropertyBridge.getProperty("leaf", "text"))
        assertTrue(PropertyBridge.updateProperty("leaf", "text", null))
        assertNull(PropertyBridge.getProperty("leaf", "text"))
        assertFalse(PropertyBridge.updateProperty("unknown", "text", "new"))

        val appended = JsonBlock("Text", mapOf("__nodeId" to "appended"))
        val inserted = JsonBlock("Text", mapOf("__nodeId" to "inserted"))
        assertTrue(PropertyBridge.addChild("nested", appended))
        assertTrue(PropertyBridge.addChild("nested", inserted, 0))
        assertFalse(PropertyBridge.addChild("unknown", JsonBlock("Text")))
        assertEquals("inserted", PropertyBridge.currentTree.value[0].children[0].children[0].props["__nodeId"])

        assertTrue(PropertyBridge.moveComponent("appended", "root", 0))
        assertEquals("appended", PropertyBridge.currentTree.value[0].children[0].props["__nodeId"])
        assertFalse(PropertyBridge.moveComponent("unknown", "root"))
        assertFalse(PropertyBridge.moveComponent("inserted", "unknown"))
        assertFalse(PropertyBridge.moveComponent("root", "root"))
        assertFalse(PropertyBridge.moveComponent("nested", "leaf"))

        assertTrue(PropertyBridge.removeComponent("inserted"))
        assertTrue(PropertyBridge.removeComponent("appended"))
        assertFalse(PropertyBridge.removeComponent("unknown"))

        JsonTreeHistoryManager.initialize(listOf(root.copy(props = root.props + ("name" to "History"))))
        PropertyBridge.syncFromHistory()
        assertEquals("History", PropertyBridge.getProperty("root", "name"))
    }
}
