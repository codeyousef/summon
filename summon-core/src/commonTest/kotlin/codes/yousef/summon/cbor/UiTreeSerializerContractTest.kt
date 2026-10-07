package codes.yousef.summon.cbor

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class UiTreeSerializerContractTest {
    private val child = UiNode(
        id = "child",
        type = "Text",
        props = mapOf("lang" to "日本語"),
        textContent = "Hello 😀",
        eventHandlers = listOf("click")
    )
    private val root = UiNode("root", "Column", children = listOf(child))

    @Test
    fun treeUsesBinaryCborAndRoundTripsDefaultsAndUnicode() {
        val tree = UiTree(root)
        val bytes = UiTreeSerializer.serialize(tree)
        assertFalse(bytes.decodeToString().startsWith("{"), "UI tree payload must be CBOR, not JSON text")
        assertEquals(tree, UiTreeSerializer.deserialize(bytes))
        assertContentEquals(bytes, UiTreeSerializer.serialize(tree))
    }

    @Test
    fun everyPatchVariantRoundTripsInOrder() {
        val patches = listOf(
            UiPatch.Replace("root", child),
            UiPatch.UpdateProps("child", mapOf("role" to "status")),
            UiPatch.AppendChild("root", child),
            UiPatch.RemoveNode("old"),
            UiPatch.UpdateText("child", "updated")
        )
        assertEquals(patches, UiTreeSerializer.deserializePatches(UiTreeSerializer.serializePatches(patches)))
        assertEquals(emptyList(), UiTreeSerializer.deserializePatches(UiTreeSerializer.serializePatches(emptyList())))
    }
}
