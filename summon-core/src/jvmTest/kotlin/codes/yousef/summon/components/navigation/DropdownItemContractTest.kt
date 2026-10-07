package codes.yousef.summon.components.navigation

import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class DropdownItemContractTest {
    @Test
    fun itemsUseNativeLinksCallbacksAndDisabledSemantics() {
        val html = PlatformRenderer().renderComposableRoot {
            DropdownItem("Link", href = "/safe")
            DropdownItem("Action", onClick = {})
            DropdownItem("Disabled link", href = "/blocked", enabled = false)
            DropdownItem("Plain")
            DropdownDivider()
        }
        assertContains(html, "href=\"/safe\"")
        assertContains(html, "title=\"Link\"")
        assertContains(html, "data-onclick-id")
        assertContains(html, "aria-disabled=\"true\"")
        assertContains(html, "Disabled link")
        assertFalse(html.contains("href=\"/blocked\""))
        assertFalse(html.contains("handleDropdownItemClick"))
        assertContains(html, "height: 1px")
    }
}
