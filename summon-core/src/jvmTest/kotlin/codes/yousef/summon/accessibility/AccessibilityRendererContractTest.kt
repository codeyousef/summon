package codes.yousef.summon.accessibility

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class AccessibilityRendererContractTest {
    @Test
    fun containerRendersEveryStateDescriptionAndCustomProperty() {
        val node = AccessibilityNode(
            role = Role.CHECKBOX,
            label = "Terms",
            description = "terms-help",
            state = State.entries.associateWith { it != State.DISABLED },
            properties = mapOf("aria-controls" to "details")
        )
        val html = PlatformRenderer().renderComposableRoot {
            AccessibilityContainer(node) { Text("Accept") }
        }
        assertContains(html, "role=\"checkbox\"")
        assertContains(html, "aria-label=\"Terms\"")
        assertContains(html, "aria-describedby=\"terms-help\"")
        assertContains(html, "aria-checked=\"true\"")
        assertContains(html, "aria-disabled=\"false\"")
        assertContains(html, "aria-expanded=\"true\"")
        assertContains(html, "aria-hidden=\"true\"")
        assertContains(html, "aria-invalid=\"true\"")
        assertContains(html, "aria-pressed=\"true\"")
        assertContains(html, "aria-readonly=\"true\"")
        assertContains(html, "aria-required=\"true\"")
        assertContains(html, "aria-selected=\"true\"")
        assertContains(html, "aria-controls=\"details\"")
    }

    @Test
    fun optionalMetadataIsNotFabricated() {
        val html = PlatformRenderer().renderComposableRoot {
            AccessibilityContainer(AccessibilityNode(role = Role.PRESENTATION, label = null, description = null)) {
                Text("Plain")
            }
        }
        assertFalse(html.contains("aria-label"))
        assertFalse(html.contains("aria-describedby"))
    }
}
