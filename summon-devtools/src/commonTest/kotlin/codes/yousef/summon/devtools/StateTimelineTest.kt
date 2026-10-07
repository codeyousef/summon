package codes.yousef.summon.devtools

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class StateTimelineTest {
    @Test
    fun defaultCapacityRetainsExactlyNewestTwoHundredChanges() {
        val fixture = Fixture()
        val timeline = fixture.session.createTimeline()
        timeline.start()

        repeat(300) {
            fixture.value++
            timeline.sample()
        }

        assertEquals(200, timeline.entries.size)
        assertEquals(101L, timeline.entries.first().sequence)
        assertEquals(300L, timeline.entries.last().sequence)
        assertEquals(DebugValue.LongValue(100), timeline.entries.first().before)
        assertEquals(DebugValue.LongValue(300), timeline.entries.last().after)
    }

    @Test
    fun scrubDoesNotRecordAndNextMutationBranchesDeterministically() {
        val fixture = Fixture()
        val timeline = fixture.session.createTimeline()
        timeline.start()
        fixture.value = 1
        timeline.sample()
        fixture.value = 2
        timeline.sample()

        val restored = timeline.restoreTo(1)
        assertTrue(restored.complete)
        assertEquals(1, fixture.value)
        assertEquals(2, timeline.entries.size)
        timeline.pause()
        timeline.start()

        fixture.value = 9
        timeline.sample()
        assertEquals(listOf(1L, 3L), timeline.entries.map { it.sequence })
        assertEquals(DebugValue.LongValue(1), timeline.entries.last().before)
        assertEquals(DebugValue.LongValue(9), timeline.entries.last().after)
    }

    @Test
    fun readOnlyFieldsRemainVisiblyUnrestorable() {
        val fixture = Fixture(writable = false)
        val timeline = fixture.session.createTimeline()
        timeline.start()
        fixture.value = 4
        timeline.sample()

        val result = timeline.restoreTo(null)

        assertFalse(result.complete)
        assertEquals(0, result.appliedFields)
        assertEquals(listOf(fixture.fieldId), result.unrestorableFields)
        assertEquals(4, fixture.value)
        assertEquals(1, timeline.entries.size)
    }

    @Test
    fun recordingLifecycleEqualWritesAndInvalidCapacitiesAreEnforced() {
        val fixture = Fixture()
        assertFailsWith<IllegalArgumentException> { fixture.session.createTimeline(0) }
        assertFailsWith<IllegalArgumentException> {
            fixture.session.createTimeline(StateTimeline.MAX_CAPACITY + 1)
        }
        val timeline = fixture.session.createTimeline(3)
        timeline.start()
        timeline.sample()
        assertTrue(timeline.entries.isEmpty())
        fixture.value = 1
        timeline.pause()
        timeline.sample()
        assertTrue(timeline.entries.isEmpty())
        timeline.start()
        fixture.value = 2
        timeline.sample()
        timeline.stop()
        fixture.value = 3
        timeline.sample()
        assertEquals(1, timeline.entries.size)
        timeline.clear()
        assertTrue(timeline.entries.isEmpty())
    }

    @Test
    fun importsValidateCompletelyBeforeAnySetterOrActionAndRoundTripStructurally() {
        var setterCalls = 0
        var actionCalls = 0
        val fixture = Fixture(onSet = { setterCalls++ })
        fixture.session.registerDebugAction(
            fixture.node,
            "increment",
            DebugActionEffect.PURE_UI
        ) {
            actionCalls++
            fixture.value++
        }
        val timeline = fixture.session.createTimeline()
        timeline.start()
        fixture.session.edit(fixture.node, "count", DebugValue.LongValue(7))
        val exported = timeline.exportSession()
        val imported = timeline.importSession(exported)
        assertEquals(DebugSessionPlan(timeline.entries), imported)
        assertEquals(1, setterCalls)
        assertEquals(0, actionCalls)

        val unknownAction = exported.replace("\"after\"", "\"actionId\":\"missing\",\"after\"")
        assertFailsWith<IllegalArgumentException> { timeline.importSession(unknownAction) }
        assertEquals(1, setterCalls)
        assertEquals(0, actionCalls)
        assertFailsWith<IllegalArgumentException> {
            timeline.importSession(exported.replace("\"formatVersion\":1", "\"formatVersion\":2"))
        }
        assertFailsWith<IllegalArgumentException> {
            timeline.importSession("""{"formatVersion":1,"entries":[{"sequence":"1"}]}""")
        }
        val oversizedValue = "x".repeat(DebugFieldCodecs.MAX_STRING_UTF8_BYTES + 1)
        assertFailsWith<IllegalArgumentException> {
            timeline.importSession(
                """{"formatVersion":1,"entries":[{"sequence":"1","field":{"nodeId":"0","name":"count","codec":"long?"},"before":{"kind":"string","value":"$oversizedValue"},"after":{"kind":"long","value":"1"}}]}"""
            )
        }
        assertFailsWith<IllegalArgumentException> {
            timeline.importSession(
                """{"formatVersion":1,"entries":[{"sequence":"1","field":{"nodeId":"0","name":"count","codec":"long?"},"before":{"kind":"double","value":1e999},"after":{"kind":"long","value":"1"}}]}"""
            )
        }
        assertEquals(1, setterCalls)
        assertEquals(0, actionCalls)


        val duplicateSequence = exported.replace(
            "\"entries\":[",
            "\"entries\":[${exported.substringAfter("\"entries\":[").substringBeforeLast("]")},"
        )
        assertFailsWith<IllegalArgumentException> { timeline.importSession(duplicateSequence) }
        assertEquals(1, setterCalls)
        assertEquals(0, actionCalls)

        val oversized = " ".repeat(DebugSessionCodec.MAX_JSON_UTF8_BYTES + 1)
        assertFailsWith<IllegalArgumentException> { timeline.importSession(oversized) }
        assertEquals(1, setterCalls)
        assertEquals(0, actionCalls)
    }

    @Test
    fun onlyPureUiActionsCanBeRetainedAndReplayStopsAtFailure() {
        val fixture = Fixture()
        DebugActionEffect.entries.filter { it != DebugActionEffect.PURE_UI }.forEach { effect ->
            assertFailsWith<IllegalArgumentException> {
                fixture.session.registerDebugAction(fixture.node, "blocked-${effect.name}", effect) {}
            }
        }
        val registration = fixture.session.registerDebugAction(
            fixture.node,
            "increment",
            DebugActionEffect.PURE_UI
        ) {
            fixture.value++
        }
        val timeline = fixture.session.createTimeline()
        timeline.start()
        assertIs<DebugActionResult.Applied>(timeline.performAction("increment"))
        registration.dispose()

        val result = timeline.replay()

        assertIs<TimelineReplayResult.Failed>(result)
        assertEquals(1L, result.sequence)
        assertEquals(0, fixture.value)
    }

    @Test
    fun sensitivePublicNamesCredentialUrlsAndDisposedReferencesAreRejectedOrCleared() {
        val fixture = Fixture()
        assertFailsWith<IllegalArgumentException> {
            fixture.session.registerPublicField(
                fixture.node,
                "accessToken",
                DebugFieldCodecs.nullableString,
                { "secret" }
            )
        }
        val timeline = fixture.session.createTimeline()
        timeline.start()
        fixture.value = 1
        timeline.sample()
        assertEquals(1, timeline.entries.size)
        timeline.dispose()
        assertTrue(timeline.entries.isEmpty())
        assertFailsWith<IllegalStateException> { timeline.start() }

        val unsafe = DebugValue.StringValue("https://user:password@example.test/path")
        assertFailsWith<IllegalArgumentException> { validateDebugValue(unsafe) }
    }

    @Test
    fun replayApplyAndForwardRestoreCompleteForWritableFields() {
        val fixture = Fixture()
        val timeline = fixture.session.createTimeline(4)
        timeline.start()
        fixture.value = 1
        timeline.sample()
        fixture.value = 2
        timeline.sample()
        assertEquals(2L, timeline.appliedSequence)

        assertEquals(2, timeline.restoreTo(null).appliedFields)
        assertEquals(0, fixture.value)
        assertEquals(2, timeline.restoreTo(2).appliedFields)
        assertEquals(2, fixture.value)
        val replayed = assertIs<TimelineReplayResult.Completed>(timeline.replay())
        assertEquals(2, replayed.appliedEntries)
        assertEquals(2, fixture.value)

        val exported = timeline.exportSession()
        val plan = timeline.importSession(exported)
        timeline.clear()
        assertTrue(timeline.entries.isEmpty())
        assertEquals(2, timeline.apply(plan).appliedFields)
        assertEquals(plan.entries, timeline.entries)
        assertFailsWith<IllegalArgumentException> {
            fixture.session.createTimeline(1).apply(plan)
        }
        assertEquals(0, timeline.apply(DebugSessionPlan(emptyList())).appliedFields)
        assertEquals(null, timeline.appliedSequence)
    }

    @Test
    fun successfulActionsReplayOnceAndRejectedActionsAreReported() {
        val fixture = Fixture()
        fixture.session.registerDebugAction(fixture.node, "increment", DebugActionEffect.PURE_UI) {
            fixture.value++
        }
        val timeline = fixture.session.createTimeline()
        timeline.start()
        assertIs<DebugActionResult.Applied>(timeline.performAction("increment"))
        assertEquals(1, fixture.value)
        val replayed = assertIs<TimelineReplayResult.Completed>(timeline.replay())
        assertEquals(1, replayed.appliedEntries)
        assertEquals(1, fixture.value)
        assertIs<DebugActionResult.Rejected>(timeline.performAction("missing"))
        timeline.start()
        timeline.start()
        timeline.pause()
        timeline.pause()
        timeline.stop()
    }

    @Test
    fun mutationModelsRejectInvalidIdentifiersSequencesAndValues() {
        assertFailsWith<IllegalArgumentException> { DebugFieldId(-1, "count", "long") }
        assertFailsWith<IllegalArgumentException> { DebugFieldId(0, "", "long") }
        assertFailsWith<IllegalArgumentException> { DebugFieldId(0, "count", "") }
        val id = DebugFieldId(0, "count", "long")
        assertFailsWith<IllegalArgumentException> {
            StateMutation(0, id, DebugValue.LongValue(0), DebugValue.LongValue(1))
        }
        assertFailsWith<IllegalArgumentException> {
            StateMutation(1, id, DebugValue.LongValue(0), DebugValue.LongValue(0))
        }
        assertFailsWith<IllegalArgumentException> {
            StateMutation(1, id, DebugValue.LongValue(0), DebugValue.LongValue(1), "bad action!")
        }
        assertFailsWith<IllegalArgumentException> { validateDebugValue(DebugValue.DoubleValue(Double.NaN)) }
        assertFailsWith<IllegalArgumentException> { requireDebugIdentifier("", "Field") }
        assertEquals("DebugSessionPlan(formatVersion=1, entries=[])", DebugSessionPlan(emptyList()).toString())
    }

    @Test
    fun codecEncodesEveryDebugValueAndRejectsMalformedDocumentShapes() {
        val id = DebugFieldId(0, "field", "test")
        val encoded = DebugSessionCodec.encode(
            DebugSessionPlan(
                listOf(
                    StateMutation(1, id, DebugValue.Null, DebugValue.StringValue("value")),
                    StateMutation(2, id, DebugValue.BooleanValue(false), DebugValue.BooleanValue(true)),
                    StateMutation(3, id, DebugValue.LongValue(1), DebugValue.LongValue(2)),
                    StateMutation(4, id, DebugValue.DoubleValue(1.5), DebugValue.DoubleValue(2.5), "action"),
                )
            )
        )
        listOf("\"kind\":\"null\"", "\"kind\":\"string\"", "\"kind\":\"boolean\"", "\"kind\":\"long\"", "\"kind\":\"double\"").forEach {
            assertTrue(encoded.contains(it))
        }
        assertTrue(encoded.contains("\"actionId\":\"action\""))

        val fixture = Fixture()
        val timeline = fixture.session.createTimeline()
        val field = """"field":{"nodeId":"0","name":"count","codec":"long?"}"""
        val long0 = """{"kind":"long","value":"0"}"""
        val long1 = """{"kind":"long","value":"1"}"""
        val explicitNullAction = """{"formatVersion":1,"entries":[{"sequence":"1",$field,"before":$long0,"after":$long1,"actionId":null}]}"""
        assertEquals(1, timeline.importSession(explicitNullAction).entries.size)

        listOf(
            "[]",
            "{}",
            """{"formatVersion":1,"entries":[],"unknown":true}""",
            """{"formatVersion":"1","entries":[]}""",
            """{"formatVersion":1.5,"entries":[]}""",
            """{"formatVersion":2147483648,"entries":[]}""",
            """{"formatVersion":1}""",
            """{"formatVersion":1,"entries":{}}""",
            """{"formatVersion":1,"entries":[1]}""",
            """{"formatVersion":1,"entries":[{"sequence":"1",$field,"before":$long0,"after":$long1,"unknown":true}]}""",
            """{"formatVersion":1,"entries":[{"sequence":1,$field,"before":$long0,"after":$long1}]}""",
            """{"formatVersion":1,"entries":[{"sequence":"x",$field,"before":$long0,"after":$long1}]}""",
            """{"formatVersion":1,"entries":[{"sequence":"${Long.MAX_VALUE}",$field,"before":$long0,"after":$long1}]}""",
            """{"formatVersion":1,"entries":[{"sequence":"1","before":$long0,"after":$long1}]}""",
            """{"formatVersion":1,"entries":[{"sequence":"1","field":1,"before":$long0,"after":$long1}]}""",
            """{"formatVersion":1,"entries":[{"sequence":"1","field":{"nodeId":"x","name":"count","codec":"long?"},"before":$long0,"after":$long1}]}""",
            """{"formatVersion":1,"entries":[{"sequence":"1",$field,"before":[],"after":$long1}]}""",
            """{"formatVersion":1,"entries":[{"sequence":"1",$field,"before":{"kind":"null","value":"bad"},"after":$long1}]}""",
            """{"formatVersion":1,"entries":[{"sequence":"1",$field,"before":{"kind":"boolean","value":"bad"},"after":$long1}]}""",
            """{"formatVersion":1,"entries":[{"sequence":"1",$field,"before":{"kind":"long","value":"bad"},"after":$long1}]}""",
            """{"formatVersion":1,"entries":[{"sequence":"1",$field,"before":{"kind":"unknown"},"after":$long1}]}""",
            """{"formatVersion":1,"entries":[{"sequence":"1",$field,"before":$long0,"after":$long1,"actionId":{}}]}""",
        ).forEach { malformed ->
            assertFailsWith<IllegalArgumentException>(malformed) { timeline.importSession(malformed) }
        }
    }

    @Test
    fun restoreReportsRetiredFieldsAndNewWritesReplaceAbandonedHistory() {
        val retired = Fixture()
        val retiredTimeline = retired.session.createTimeline()
        retiredTimeline.start()
        retired.value = 1
        retiredTimeline.sample()
        assertFailsWith<IllegalArgumentException> { retiredTimeline.restoreTo(99) }
        retired.registration.dispose()
        val restore = retiredTimeline.restoreTo(null)
        assertFalse(restore.complete)
        assertEquals(listOf(retired.fieldId), restore.unrestorableFields)
        val replay = assertIs<TimelineReplayResult.Failed>(retiredTimeline.replay())
        assertEquals(1L, replay.sequence)
        retiredTimeline.dispose()
        retiredTimeline.dispose()

        val fixture = Fixture()
        val timeline = fixture.session.createTimeline()
        timeline.start()
        fixture.value = 1
        timeline.sample()
        fixture.value = 2
        timeline.sample()
        timeline.restoreTo(1)
        fixture.value = 3
        timeline.sample()
        assertEquals(listOf(1L, 3L), timeline.entries.map { it.sequence })
        assertEquals(3, fixture.value)
    }

    @Test
    fun identifierAndCredentialBoundariesFailClosed() {
        assertFailsWith<IllegalArgumentException> { DebugFieldId(0, "x".repeat(129), "long") }
        assertFailsWith<IllegalArgumentException> { DebugFieldId(0, "count", "x".repeat(129)) }
        assertFailsWith<IllegalArgumentException> { requireDebugIdentifier("bad value", "Field") }
        assertFailsWith<IllegalArgumentException> { requireDebugIdentifier("x".repeat(129), "Field") }
        requireDebugIdentifier("safe._-:09", "Field")
        validateDebugValue(DebugValue.StringValue("https://example.test"))
        validateDebugValue(DebugValue.StringValue("https://example.test/path"))
        assertFailsWith<IllegalArgumentException> {
            validateDebugValue(DebugValue.StringValue("https://user@example.test"))
        }
        assertFalse(TimelineRestoreResult(0, listOf(DebugFieldId(0, "count", "long"))).complete)
        assertFalse(DebugSessionPlan(emptyList()).equals("not a plan"))
        assertFalse(
            DebugSessionPlan(emptyList()) == DebugSessionPlan(
                listOf(
                    StateMutation(
                        1,
                        DebugFieldId(0, "count", "long"),
                        DebugValue.LongValue(0),
                        DebugValue.LongValue(1),
                    )
                )
            )
        )
    }

    private class Fixture(
        writable: Boolean = true,
        onSet: () -> Unit = {}
    ) {
        private val source = FakeTreeSource()
        val session = InspectorSession(source)
        val node = source.node
        var value = 0
        val fieldId = DebugFieldId(node.localId, "count", "long?")
        val registration: InspectorDisposable

        init {
            registration = session.registerPublicField(
                node,
                "count",
                object : DebugFieldCodec<Int> {
                    override val typeName: String = "long?"
                    override fun encode(value: Int): DebugValue = DebugValue.LongValue(value.toLong())
                    override fun decode(value: DebugValue): Int =
                        (value as? DebugValue.LongValue)?.value?.toInt()
                            ?: throw IllegalArgumentException("Expected integral value")
                },
                { value },
                if (writable) {
                    { next ->
                        onSet()
                        value = next
                    }
                } else {
                    null
                }
            )
            session.registerRedactedField(node, "credential")
        }
    }

    private class FakeTreeSource : InspectorTreeSource {
        override val rendererId: String = "timeline-test"
        val node = InspectorNodeId(rendererId, 0)
        private val nodes = listOf(InspectorNode(node, null, "root", emptyList(), true))

        override fun snapshot(): List<InspectorNode> = nodes
        override fun observe(listener: InspectorTreeListener): InspectorDisposable = InspectorDisposable {}
        override fun highlight(nodeId: InspectorNodeId?): Boolean = true
        override fun dispose() = Unit
    }
}
