package codes.yousef.summon.test

import codes.yousef.summon.components.display.Image
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ComponentHarnessContractTest {
    @Test
    fun semanticRolesCoverNativeAndExplicitMappings() {
        assertEquals("custom", semanticRole("div", "custom", null))
        assertEquals("generic", semanticRole("div", "", null))
        assertEquals("button", semanticRole("button", null, null))
        assertEquals("link", semanticRole("a", null, null))
        assertEquals("img", semanticRole("img", null, null))
        assertEquals("textbox", semanticRole("textarea", null, null))
        assertEquals("button", semanticRole("input", null, "submit"))
        assertEquals("button", semanticRole("input", null, "button"))
        assertEquals("button", semanticRole("input", null, "reset"))
        assertEquals("checkbox", semanticRole("input", null, "checkbox"))
        assertEquals("radio", semanticRole("input", null, "radio"))
        assertEquals("slider", semanticRole("input", null, "range"))
        assertEquals("textbox", semanticRole("input", null, "email"))
        assertEquals("textbox", semanticRole("input", null, null))
        assertEquals("combobox", semanticRole("select", null, null))
        assertEquals("list", semanticRole("ol", null, null))
        assertEquals("list", semanticRole("ul", null, null))
        assertEquals("generic", semanticRole("UNKNOWN", null, null))
        assertEquals("listitem", semanticRole("li", null, null))
    }

    @Test
    fun testMetadataRejectsAmbiguousOrNonFiniteValues() {
        assertFailsWith<IllegalArgumentException> { Modifier().testTag("") }
        assertFailsWith<IllegalArgumentException> { Modifier().testTag("x".repeat(257)) }
        assertFailsWith<IllegalArgumentException> { Modifier().testState("1bad", true) }
        assertFailsWith<IllegalArgumentException> { Modifier().testState("valid", listOf(1)) }
        assertFailsWith<IllegalArgumentException> { Modifier().testState("valid", Double.NaN) }
        assertFailsWith<IllegalArgumentException> { Modifier().testState("valid", Float.POSITIVE_INFINITY) }
        val modifier = Modifier().testState("string", "value").testState("long", 2L)
            .testState("int", 3).testState("double", 4.5).testState("float", 6.5f)
        assertEquals("value", modifier.attributes["${TEST_STATE_PREFIX}string"])
        assertEquals("6.5", modifier.attributes["${TEST_STATE_PREFIX}float"])
    }

    @Test
    fun schedulerDrainsCancelsAndRejectsWorkAfterClose() {
        val scheduler = HarnessScheduler()
        val values = mutableListOf<Int>()
        scheduler.scheduleRecomposition { values += 1 }
        scheduler.scheduleRecomposition { values += 2 }
        assertEquals(2, scheduler.pendingWorkCount)
        scheduler.drain(1_000)
        assertEquals(listOf(1, 2), values)
        scheduler.scheduleRecomposition { values += 3 }
        scheduler.cancelPendingRecomposition()
        assertEquals(0, scheduler.pendingWorkCount)
        assertFailsWith<IllegalArgumentException> { scheduler.drain(0) }
        scheduler.close()
        scheduler.close()
        assertFailsWith<IllegalStateException> { scheduler.scheduleRecomposition {} }
    }

    @Test
    fun schedulerBoundsRunawayWorkAndHarnessClosesAfterAdapterFailure() {
        val scheduler = HarnessScheduler()
        lateinit var repeat: () -> Unit
        repeat = { scheduler.scheduleRecomposition(repeat) }
        scheduler.scheduleRecomposition(repeat)
        assertFailsWith<AssertionError> { scheduler.drain(60_000) }

        val failingAdapter = object : SemanticHarnessAdapter {
            override val scheduler = HarnessScheduler()
            override fun snapshot(): List<SemanticSnapshot> = emptyList()
            override fun click(identity: Long) = Unit
            override fun textInput(identity: Long, value: String) = Unit
            override fun scrollTo(identity: Long) = Unit
            override fun dispose() = error("synthetic disposal failure")
        }
        val harness = ComponentHarness(failingAdapter)
        assertFailsWith<IllegalStateException> { harness.dispose() }
        assertFailsWith<IllegalStateException> { failingAdapter.scheduler.scheduleRecomposition {} }
    }

    @Test
    fun matchersAssertionsAndJsonEscapingExposeActionableFailures() {
        withComponentHarness(mountJvmComponentHarness {
            Column {
                Text("prefix exact suffix\n\t\b\u0001", Modifier().testTag("text")
                    .testState("string", "quoted\"\\")
                    .testState("boolean", true)
                    .testState("long", 2L)
                    .testState("double", 3.5))
                Image("/image.png", "Description", Modifier().testTag("image"))
                Button({}, "Enabled", Modifier().testTag("button"))
            }
        }) { harness ->
            harness.onNodeWithText("exact", substring = true).assertExists()
            val text = harness.onNodeWithTag("text")
                .assertIsDisplayed().assertState("string", "quoted\"\\")
                .assertState("boolean", true).assertState("long", 2L).assertState("double", 3.5)
            assertFailsWith<AssertionError> { text.assertTextEquals("wrong") }
            assertFailsWith<AssertionError> { text.assertState("missing", "value") }
            assertFailsWith<AssertionError> { text.assertState("long", 3L) }
            assertFailsWith<IllegalArgumentException> { text.assertState("double", Double.NaN) }
            assertFailsWith<IllegalArgumentException> { harness.onNodeWithText("") }
            assertFailsWith<IllegalArgumentException> { harness.onNodeWithTag("") }
            assertFailsWith<AssertionError> { harness.assertNoNodeWithText("prefix", substring = true) }
            assertFailsWith<AssertionError> { harness.assertNoNodeWithTag("image") }
            val snapshot = harness.semanticSnapshot()
            assertTrue(snapshot.contains("\\n\\t\\b\\u0001"))
            assertTrue(snapshot.contains("quoted\\\"\\\\"))
            harness.awaitIdle()
            harness.dispose()
            harness.dispose()
            assertFailsWith<IllegalStateException> { harness.semanticSnapshot() }
        }
    }

    @Test
    fun semanticFailuresDetachAndActionsCoverLiveTreeBoundaries() {
        val nodes = mutableListOf(
            snapshot(1, "visible", displayed = true),
            snapshot(2, "hidden", displayed = false),
            snapshot(3, "inert", inert = true),
            snapshot(4, "detached", connected = false),
            snapshot(5, "disabled", enabled = false),
            snapshot(6, "duplicate", displayed = true),
            snapshot(7, "duplicate", displayed = true),
        )
        val adapter = RecordingAdapter(nodes)
        val harness = ComponentHarness(adapter)
        try {
            harness.onNodeWithTag("visible").assertIsDisplayed().assertEnabled()
                .performClick().performTextInput("value").performScrollTo()
            assertEquals(listOf("click:1", "input:1:value", "scroll:1"), adapter.actions)
            assertFailsWith<AssertionError> { harness.onNodeWithTag("hidden").assertIsDisplayed() }
            assertFailsWith<AssertionError> { harness.onNodeWithTag("inert").assertIsDisplayed() }
            assertFailsWith<AssertionError> { harness.onNodeWithTag("detached").assertIsDisplayed() }
            assertFailsWith<AssertionError> { harness.onNodeWithTag("disabled").assertEnabled() }
            assertFailsWith<AssertionError> { harness.onNodeWithTag("inert").assertEnabled() }
            assertFailsWith<AssertionError> { harness.onNodeWithTag("missing") }
            assertFailsWith<AssertionError> { harness.onNodeWithText("duplicate") }
            harness.assertNoNodeWithText("absent").assertNoNodeWithTag("absent")

            val removable = harness.onNodeWithTag("visible")
            assertFailsWith<AssertionError> { removable.assertDoesNotExist() }
            nodes.removeAll { it.identity == 1L }
            removable.assertDoesNotExist()
            assertFailsWith<AssertionError> { removable.assertExists() }

            val actual = harness.semanticSnapshot()
            assertFailsWith<AssertionError> { harness.assertSemanticSnapshot(actual.replace("hidden", "changed")) }
        } finally {
            harness.dispose()
        }
        assertTrue(adapter.disposed)
    }

    @Test
    fun snapshotsBoundOutputAndHeaderOnlyDiffsRemainActionable() {
        val oversized = mutableListOf(snapshot(1, "x".repeat(ComponentHarness.MAX_SEMANTIC_SNAPSHOT_CHARS)))
        val bounded = ComponentHarness(RecordingAdapter(oversized))
        try {
            assertFailsWith<AssertionError> { bounded.semanticSnapshot() }
        } finally {
            bounded.dispose()
        }

        val harness = ComponentHarness(RecordingAdapter(mutableListOf(snapshot(1, "node"))))
        try {
            val actual = harness.semanticSnapshot()
            val failure = assertFailsWith<AssertionError> {
                harness.assertSemanticSnapshot(actual.replace("summon-semantic-snapshot:v1", "wrong-header"))
            }
            assertTrue(failure.message.orEmpty().contains("path <header>"))
        } finally {
            harness.dispose()
        }
    }

    private fun snapshot(
        identity: Long,
        tag: String,
        connected: Boolean = true,
        displayed: Boolean = true,
        inert: Boolean = false,
        enabled: Boolean = true,
    ) = SemanticSnapshot(
        identity = identity,
        parentIdentity = null,
        elementName = "div",
        text = tag,
        tag = tag,
        connected = connected,
        displayed = displayed,
        role = "generic",
        name = tag,
        inert = inert,
        enabled = enabled,
        states = emptyMap()
    )

    private class RecordingAdapter(private val nodes: MutableList<SemanticSnapshot>) : SemanticHarnessAdapter {
        override val scheduler = HarnessScheduler()
        val actions = mutableListOf<String>()
        var disposed = false
        override fun snapshot(): List<SemanticSnapshot> = nodes.toList()
        override fun click(identity: Long) { actions += "click:$identity" }
        override fun textInput(identity: Long, value: String) { actions += "input:$identity:$value" }
        override fun scrollTo(identity: Long) { actions += "scroll:$identity" }
        override fun dispose() { disposed = true }
    }
}
