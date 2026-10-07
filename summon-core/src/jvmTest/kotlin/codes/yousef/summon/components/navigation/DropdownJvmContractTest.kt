package codes.yousef.summon.components.navigation

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DropdownJvmContractTest {
    @Test
    fun triggerAndAlignmentMatrixUsesDelegatedActionsAndStylesheetHoverWithoutInlineScript() {
        val html = PlatformRenderer().renderComposableRoot {
            Dropdown(
                trigger = { Text("Hover") },
                triggerBehavior = DropdownTrigger.HOVER,
                alignment = DropdownAlignment.LEFT,
                closeOnItemClick = false
            ) { Text("Left item") }
            Dropdown(
                trigger = { Text("Click") },
                triggerBehavior = DropdownTrigger.CLICK,
                alignment = DropdownAlignment.RIGHT,
                closeOnItemClick = true
            ) { Text("Right item") }
            Dropdown(
                trigger = { Text("Both") },
                triggerBehavior = DropdownTrigger.BOTH,
                alignment = DropdownAlignment.CENTER,
                closeOnItemClick = true
            ) { Text("Center item") }
        }

        assertContains(html, "data-dropdown-container=\"true\"")
        assertContains(html, "data-dropdown-trigger=\"true\"")
        assertContains(html, "data-dropdown-menu=\"true\"")
        assertContains(html, "left: 0")
        assertContains(html, "right: 0")
        assertContains(html, "left: 50%")
        assertContains(html, "translateX(-50%)")
        assertContains(html, "[data-dropdown-container=\"true\"]:hover")
        assertTrue(Regex("data-action=\\\"").findAll(html).count() >= 4)
        assertFalse(html.contains("onmouseenter", ignoreCase = true))
        assertFalse(html.contains("onmouseleave", ignoreCase = true))
        assertFalse(html.contains("document.getElementById"))
    }
}
