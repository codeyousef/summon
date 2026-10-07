package codes.yousef.summon.test

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.modifier.style
import codes.yousef.summon.runtime.CallbackRegistry
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class JvmComponentHarnessTest {
    @Test
    fun unicodeTagsTypedStateAndAmbiguityUseActualSsrSemantics() {
        withComponentHarness(mountJvmComponentHarness {
            Column {
                Text("مرحبا <&> \"Summon\"", Modifier().testTag("arabic").testState("ready", true))
                Text("duplicate")
                Text("duplicate")
            }
        }) { harness ->
            harness.onNodeWithText("مرحبا <&> \"Summon\"")
                .assertExists()
                .assertTextEquals("مرحبا <&> \"Summon\"")
            harness.onNodeWithTag("arabic").assertState("ready", true)
            val ambiguity = assertFailsWith<AssertionError> { harness.onNodeWithText("duplicate") }
            assertTrue(ambiguity.message.orEmpty().contains("found 2"))
            assertTrue(ambiguity.message.orEmpty().contains("Semantic tree"))
            val missing = assertFailsWith<AssertionError> { harness.onNodeWithTag("missing") }
            assertTrue(missing.message.orEmpty().contains("found 0"))
        }
    }

    @Test
    fun clickUsesRenderedCallbackExactlyOnceAndOldHandleBecomesStale() {
        var count = 0
        withComponentHarness(mountJvmComponentHarness {
            Column {
                Text("Count: $count", Modifier().testTag("count"))
                Button(
                    onClick = { count++ },
                    label = "Increment",
                    modifier = Modifier().testTag("increment")
                )
            }
        }) { harness ->
            val snapshot = harness.semanticSnapshot()
            assertTrue(snapshot.contains("\"role\":\"button\""))
            assertTrue(snapshot.contains("\"name\":\"Increment\""))
            val oldCount = harness.onNodeWithTag("count")
            harness.onNodeWithTag("increment").assertEnabled().performClick()
            assertEquals(1, count)
            harness.onNodeWithTag("count").assertTextEquals("Count: 1")
            assertFailsWith<AssertionError> { oldCount.assertExists() }
        }
        assertEquals(0, CallbackRegistry.size())
    }

    @Test
    fun hiddenInertDisabledAndDetachedStatesRemainDistinct() {
        var visible = true
        withComponentHarness(mountJvmComponentHarness {
            Column {
                if (visible) {
                    Column(Modifier().style("display", "none")) {
                        Text("Hidden", Modifier().testTag("hidden"))
                    }
                    Column(Modifier().attribute("inert", "")) {
                        Text("Inert", Modifier().testTag("inert"))
                    }
                }
                Button({}, "Disabled", Modifier().testTag("disabled"), disabled = true)
                Button({ visible = false }, "Remove", Modifier().testTag("remove"))
            }
        }) { harness ->
            val hidden = harness.onNodeWithTag("hidden")
            val inert = harness.onNodeWithTag("inert")
            assertFailsWith<AssertionError> { hidden.assertIsDisplayed() }
            assertFailsWith<AssertionError> { inert.assertIsDisplayed() }
            assertFailsWith<AssertionError> { harness.onNodeWithTag("disabled").assertEnabled() }
            harness.onNodeWithTag("remove").performClick()
            hidden.assertDoesNotExist()
            inert.assertDoesNotExist()
            harness.assertNoNodeWithTag("hidden").assertNoNodeWithText("Hidden")
        }
    }

    @Test
    fun unsupportedJvmBrowserInteractionsAreExplicitAndCleanupSurvivesFailures() {
        val baseline = CallbackRegistry.size()
        repeat(100) { cycle ->
            assertFailsWith<IllegalStateException> {
                withComponentHarness(mountJvmComponentHarness {
                    Button({}, "Cycle $cycle", Modifier().testTag("button"))
                }) { harness ->
                    harness.onNodeWithTag("button").assertExists()
                    error("synthetic assertion failure")
                }
            }
            assertEquals(baseline, CallbackRegistry.size())
        }
        withComponentHarness(mountJvmComponentHarness {
            Text("No browser input", Modifier().testTag("text"))
        }) { harness ->
            assertFailsWith<UnsupportedOperationException> {
                harness.onNodeWithTag("text").performTextInput("value")
            }
            assertFailsWith<UnsupportedOperationException> {
                harness.onNodeWithTag("text").performScrollTo()
            }
        }
    }

    @Test
    fun semanticSnapshotMatchesCommittedUnicodeRtlAndHiddenGoldenRepeatedly() {
        val expected = requireNotNull(javaClass.getResourceAsStream("/goldens/semantic-arabic.snap"))
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
        withComponentHarness(mountJvmComponentHarness {
            Column(Modifier().attribute("dir", "rtl").testTag("fixture")) {
                Text("مرحبا | line 1\nline 2", Modifier().testTag("arabic"))
                Column(Modifier().attribute("hidden", "hidden")) {
                    Text("Hidden text", Modifier().testTag("hidden"))
                }
            }
        }) { harness ->
            harness.assertSemanticSnapshot(expected)
            harness.assertSemanticSnapshot(expected)
            assertTrue(harness.semanticSnapshot().contains("\"text\":\"مرحبا | line 1\\nline 2\""))
            assertTrue(harness.semanticSnapshot().contains("\"visible\":false"))
        }
    }

    @Test
    fun verificationNeverWritesAndReportsSemanticPathForIntentionalChange() {
        val directory = createTempDirectory("summon-semantic-golden").toFile()
        try {
            val golden = File(directory, "fixture.snap")
            withComponentHarness(mountJvmComponentHarness {
                Text("Expected", Modifier().testTag("value"))
            }) { it.updateSemanticGolden(golden) }
            val original = golden.readBytes()

            withComponentHarness(mountJvmComponentHarness {
                Text("Changed", Modifier().testTag("value"))
            }) { harness ->
                val failure = assertFailsWith<AssertionError> { harness.verifySemanticGolden(golden) }
                assertTrue(failure.message.orEmpty().contains("Semantic snapshot differs at path 0"))
                assertTrue(failure.message.orEmpty().contains("- "))
                assertTrue(failure.message.orEmpty().contains("+ "))
            }
            assertTrue(original.contentEquals(golden.readBytes()))
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun missingGoldenFailsAndExplicitUpdateChangesOnlyRequestedFixture() {
        val directory = createTempDirectory("summon-semantic-update").toFile()
        try {
            val requested = File(directory, "requested.snap")
            val neighbor = File(directory, "neighbor.snap").apply { writeText("neighbor", Charsets.UTF_8) }
            withComponentHarness(mountJvmComponentHarness {
                Text("Snapshot", Modifier().testTag("value"))
            }) { harness ->
                assertFailsWith<AssertionError> { harness.verifySemanticGolden(requested) }
                harness.updateSemanticGolden(requested)
                harness.verifySemanticGolden(requested)
            }
            assertEquals("neighbor", neighbor.readText(Charsets.UTF_8))
        } finally {
            directory.deleteRecursively()
        }
    }
}
