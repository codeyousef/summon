package codes.yousef.summon.devtools

import kotlin.test.Test

fun inspectorPrivacyAndTimeTravelSample() {
    val nodeId = InspectorNodeId("sample-renderer", 1)
    val source = object : InspectorTreeSource {
        override val rendererId: String = nodeId.rendererId
        override fun snapshot(): List<InspectorNode> =
            listOf(InspectorNode(nodeId, null, "Profile", emptyList(), connected = true))
        override fun observe(listener: InspectorTreeListener): InspectorDisposable = InspectorDisposable {}
        override fun highlight(nodeId: InspectorNodeId?): Boolean = true
        override fun dispose() = Unit
    }
    val session = InspectorSession(source)
    var displayName: String? = "Ada"
    val redacted = session.registerRedactedField(nodeId, "account")
    val public = session.registerPublicField(
        nodeId = nodeId,
        name = "displayName",
        codec = DebugFieldCodecs.nullableString,
        getter = { displayName },
        setter = { displayName = it }
    )
    val timeline = session.createTimeline(capacity = 20)

    try {
        timeline.start()
        check(session.edit(nodeId, "displayName", DebugValue.StringValue("Grace")) == InspectorEditResult.Applied)
        val changedSequence = checkNotNull(timeline.entries.single().sequence)
        check(timeline.restoreTo(null).complete)
        check(displayName == "Ada")
        check(timeline.restoreTo(changedSequence).complete)
        check(displayName == "Grace")
        check(session.fields(nodeId).first { it.name == "account" }.value == null)
    } finally {
        timeline.dispose()
        public.dispose()
        redacted.dispose()
        session.dispose()
    }
}

class DocumentationSamplesTest {
    @Test
    fun inspectorPrivacyAndTimeTravel() {
        inspectorPrivacyAndTimeTravelSample()
    }
}
