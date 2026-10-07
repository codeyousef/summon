package codes.yousef.summon.devtools

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class InspectorSessionTest {
    @Test
    fun redactedAndReadOnlyFieldsCannotWriteOrExposeValues() {
        val source = FakeTreeSource("renderer-a")
        val node = source.nodes.single().id
        var publicValue: String? = "visible"
        val session = InspectorSession(source)
        session.registerField(node, "credential")
        session.registerPublicField(node, "status", DebugFieldCodecs.nullableString, { publicValue })

        val fields = session.fields(node)
        assertEquals(null, fields.single { it.name == "credential" }.value)
        assertEquals(DebugValue.StringValue("visible"), fields.single { it.name == "status" }.value)
        assertIs<InspectorEditResult.Rejected>(
            session.edit(node, "credential", DebugValue.StringValue("replacement"))
        )
        assertIs<InspectorEditResult.Rejected>(
            session.edit(node, "status", DebugValue.StringValue("replacement"))
        )
        assertEquals("visible", publicValue)
    }

    @Test
    fun typedMutableEditUsesDispatcherAndRejectsWrongTypeWithoutMutation() {
        val source = FakeTreeSource("renderer-a")
        val node = source.nodes.single().id
        var dispatches = 0
        var count: Long? = 2
        val session = InspectorSession(source) { action ->
            dispatches++
            action()
        }
        session.registerPublicField(node, "count", DebugFieldCodecs.nullableLong, { count }) { count = it }

        assertIs<InspectorEditResult.Rejected>(
            session.edit(node, "count", DebugValue.StringValue("3"))
        )
        assertEquals(0, dispatches)
        assertEquals(2L, count)
        assertEquals(InspectorEditResult.Applied, session.edit(node, "count", DebugValue.LongValue(3)))
        assertEquals(1, dispatches)
        assertEquals(3L, count)
    }

    @Test
    fun rootsCannotRegisterOrHighlightEachOthersNodes() {
        val first = FakeTreeSource("first")
        val second = FakeTreeSource("second")
        val firstSession = InspectorSession(first)
        val secondNode = second.nodes.single().id

        assertFailsWith<IllegalArgumentException> {
            firstSession.registerRedactedField(secondNode, "private")
        }
        assertFalse(firstSession.highlight(secondNode))
    }

    @Test
    fun treeChangesRemoveFieldsAndDisposalReleasesEveryReference() {
        val source = FakeTreeSource("renderer-a")
        val node = source.nodes.single().id
        val session = InspectorSession(source)
        session.registerPublicField(node, "status", DebugFieldCodecs.nullableBoolean, { true })
        var observed = 0
        session.observeTree { observed++ }

        source.update(emptyList())
        assertEquals(2, observed)
        assertFailsWith<IllegalArgumentException> { session.fields(node) }

        session.dispose()
        session.dispose()
        assertTrue(source.disposed)
        assertEquals(null, source.highlighted)
        assertFailsWith<IllegalStateException> { session.tree() }
    }

    @Test
    fun stringsAreBoundByUtf8BytesAndNonfiniteNumbersAreRejected() {
        val tooLarge = "ع".repeat(2_049)
        assertFailsWith<IllegalArgumentException> {
            DebugFieldCodecs.nullableString.encode(tooLarge)
        }
        assertFailsWith<IllegalArgumentException> { DebugValue.DoubleValue(Double.NaN) }
    }

    @Test
    fun builtInCodecsRoundTripNullAndTypedValuesAndRejectMismatches() {
        assertEquals(DebugValue.Null, DebugFieldCodecs.nullableBoolean.encode(null))
        assertEquals(DebugValue.BooleanValue(true), DebugFieldCodecs.nullableBoolean.encode(true))
        assertEquals(true, DebugFieldCodecs.nullableBoolean.decode(DebugValue.BooleanValue(true)))
        assertEquals(null, DebugFieldCodecs.nullableBoolean.decode(DebugValue.Null))
        assertFailsWith<IllegalArgumentException> {
            DebugFieldCodecs.nullableBoolean.decode(DebugValue.LongValue(1))
        }

        assertEquals(DebugValue.StringValue("ok"), DebugFieldCodecs.nullableString.encode("ok"))
        assertEquals("ok", DebugFieldCodecs.nullableString.decode(DebugValue.StringValue("ok")))
        assertEquals(null, DebugFieldCodecs.nullableString.decode(DebugValue.Null))
        assertFailsWith<IllegalArgumentException> {
            DebugFieldCodecs.nullableString.decode(DebugValue.BooleanValue(false))
        }

        assertEquals(DebugValue.LongValue(2), DebugFieldCodecs.nullableLong.encode(2))
        assertEquals(2, DebugFieldCodecs.nullableLong.decode(DebugValue.LongValue(2)))
        assertEquals(null, DebugFieldCodecs.nullableLong.decode(DebugValue.Null))
        assertFailsWith<IllegalArgumentException> {
            DebugFieldCodecs.nullableLong.decode(DebugValue.DoubleValue(2.0))
        }

        assertEquals(DebugValue.DoubleValue(2.5), DebugFieldCodecs.nullableDouble.encode(2.5))
        assertEquals(2.5, DebugFieldCodecs.nullableDouble.decode(DebugValue.DoubleValue(2.5)))
        assertEquals(null, DebugFieldCodecs.nullableDouble.decode(DebugValue.Null))
        assertFailsWith<IllegalArgumentException> {
            DebugFieldCodecs.nullableDouble.decode(DebugValue.StringValue("2.5"))
        }
        assertFailsWith<IllegalArgumentException> { InspectorNodeId("", 0) }
        assertFailsWith<IllegalArgumentException> { InspectorNodeId("renderer", -1) }
    }

    private class FakeTreeSource(override val rendererId: String) : InspectorTreeSource {
        var nodes: List<InspectorNode> = listOf(
            InspectorNode(
                InspectorNodeId(rendererId, 0),
                parentId = null,
                label = "root",
                children = emptyList(),
                connected = true
            )
        )
        private val listeners = linkedSetOf<InspectorTreeListener>()
        var highlighted: InspectorNodeId? = null
        var disposed = false

        override fun snapshot(): List<InspectorNode> = nodes

        override fun observe(listener: InspectorTreeListener): InspectorDisposable {
            listeners += listener
            return InspectorDisposable { listeners -= listener }
        }

        override fun highlight(nodeId: InspectorNodeId?): Boolean {
            highlighted = nodeId
            return nodeId == null || nodes.any { it.id == nodeId && it.connected }
        }

        override fun dispose() {
            disposed = true
            listeners.clear()
            nodes = emptyList()
        }

        fun update(value: List<InspectorNode>) {
            nodes = value
            listeners.toList().forEach { it.onTreeChanged(value) }
        }
    }
}
