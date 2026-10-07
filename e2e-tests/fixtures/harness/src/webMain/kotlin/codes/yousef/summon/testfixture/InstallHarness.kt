package codes.yousef.summon.testfixture

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.test.mountBrowserComponentHarness
import codes.yousef.summon.test.testTag
import codes.yousef.summon.test.withComponentHarness
import codes.yousef.summon.modifier.Modifier
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

fun installHarnessFixture() {
    val fixture = HarnessFixture()
    val harness = mountBrowserComponentHarness("root") { fixture.Content() }
    val output = document.createElement("output") as HTMLElement
    output.setAttribute("data-testid", "harness-result")
    document.body?.appendChild(output)
    var stage = "semantic lookup"

    val result = runCatching {
        harness.onNodeWithText("مرحبا <&> \"Summon\"").assertIsDisplayed()
        harness.onNodeWithTag("arabic").assertTextEquals("مرحبا <&> \"Summon\"")
        stage = "ambiguity"
        check(runCatching { harness.onNodeWithText("duplicate") }.exceptionOrNull() != null)
        check(runCatching { harness.onNodeWithTag("missing") }.exceptionOrNull() != null)
        stage = "visibility"
        check(runCatching { harness.onNodeWithTag("hidden").assertIsDisplayed() }.exceptionOrNull() != null)
        stage = "disabled"
        check(runCatching { harness.onNodeWithTag("disabled").assertEnabled() }.exceptionOrNull() != null)
        stage = "click"

        harness.onNodeWithTag("increment").performClick()
        harness.onNodeWithTag("count").assertTextEquals("Count: 1").assertState("value", 1L)
        check(fixture.clickCallbacks == 1)
        stage = "text input"

        harness.onNodeWithTag("input").performTextInput("سلام")
        harness.onNodeWithTag("mirror").assertTextEquals("سلام")
        check(fixture.inputCallbacks == 1)
        stage = "scroll and detach"

        val target = harness.onNodeWithTag("target")
        target.performScrollTo()
        harness.onNodeWithTag("remove").performClick()
        target.assertDoesNotExist()
        harness.assertNoNodeWithTag("target")
        stage = "lifecycle"

        repeat(100) { cycle ->
            val root = document.createElement("div") as HTMLElement
            root.id = "harness-cycle-$cycle"
            root.setAttribute("data-harness-cycle-root", "")
            document.body?.appendChild(root)
            check(runCatching {
                withComponentHarness(mountBrowserComponentHarness(root.id) {
                    Text("Cycle $cycle", Modifier().testTag("cycle"))
                }) { cycleHarness ->
                    cycleHarness.onNodeWithTag("cycle").assertExists()
                    error("synthetic cycle failure")
                }
            }.isFailure)
            root.remove()
        }
        check(document.querySelectorAll("[data-harness-cycle-root]").length == 0)
        "PASS clicks=${fixture.clickCallbacks} inputs=${fixture.inputCallbacks} cycles=100"
    }
    output.textContent = result.getOrElse { error ->
        "FAIL stage=$stage ${error::class.simpleName}: ${error.message}"
    }
}
