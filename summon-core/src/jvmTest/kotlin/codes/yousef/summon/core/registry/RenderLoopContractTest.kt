package codes.yousef.summon.core.registry

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.padding
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RenderLoopContractTest {
    @AfterTest
    fun clearRegistry() = ComponentRegistry.clear()

    @Test
    fun nestedRegisteredComponentsReceiveNodeIdsPropsAndChildren() {
        val seen = mutableListOf<Map<String, Any>>()
        ComponentRegistry.register("column") { props ->
            seen += props
            { Column(props.getModifier("modifier")) { props.renderChildren() } }
        }
        ComponentRegistry.register("text") { props ->
            seen += props
            { Text(props.getString("text", "fallback")) }
        }
        val tree = listOf(
            JsonBlock(
                "column",
                mapOf("modifier" to Modifier().padding("4px")),
                listOf(JsonBlock("text", mapOf("text" to "nested")))
            ),
            JsonBlock("text", emptyMap())
        )
        val html = PlatformRenderer().renderComposableRoot { RenderLoop.render(tree, "parent") }
        assertContains(html, "nested")
        assertContains(html, "fallback")
        assertContains(html, "padding: 4px")
        assertEquals("parent-0", seen[0]["__nodeId"])
        assertEquals("parent-0-0", seen[1]["__nodeId"])
        assertEquals("parent-1", seen[2]["__nodeId"])
        assertTrue(seen[0].containsKey("__children"))
        assertFalse(seen[2].containsKey("__children"))
    }

    @Test
    fun componentFailuresRenderBoundedEscapedErrorDetails() {
        ComponentRegistry.register("broken") { error("factory <failed>") }
        val html = PlatformRenderer().renderComposableRoot {
            RenderLoop.render(listOf(JsonBlock("broken")))
        }
        assertContains(html, "data-error-boundary=\"true\"")
        assertContains(html, "data-node-id=\"root-0\"")
        assertContains(html, "data-component-type=\"broken\"")
        assertContains(html, "factory &lt;failed&gt;")
        assertFalse(html.contains("factory <failed>"))
    }

    @Test
    fun propertyHelpersReturnTypedValuesAndDefaults() {
        val modifier = Modifier().padding("1px")
        val props: Map<String, Any> = mapOf(
            "modifier" to modifier,
            "string" to "value",
            "int" to 2.9,
            "boolean" to true,
            "__renderChildren" to ({})
        )
        assertEquals(modifier, props.getModifier("modifier"))
        assertEquals(Modifier(), emptyMap<String, Any>().getModifier("modifier"))
        assertEquals("value", props.getString("string"))
        assertEquals("default", props.getString("missing", "default"))
        assertEquals(2, props.getInt("int"))
        assertEquals(3, props.getInt("missing", 3))
        assertEquals(true, props.getBoolean("boolean"))
        assertEquals(false, props.getBoolean("missing"))
    }
}
