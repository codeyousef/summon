package codes.yousef.summon.test

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.modifier.style
import codes.yousef.summon.runtime.CallbackRegistry
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
}
