package codes.yousef.summon.components.feedback

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class BadgeRenderingContractTest {
    @Test
    fun badgeContentOptionsAndEveryVisualVariantRenderThroughTheContainer() {
        val html = PlatformRenderer().renderComposableRoot {
            BadgeType.entries.forEach { type ->
                Badge(type.name, type = type, shape = BadgeShape.SQUARE, isOutlined = true)
            }
            Badge("Large", shape = BadgeShape.ROUNDED, size = "large", onClick = {})
            Badge(
                "Decorated",
                shape = BadgeShape.PILL,
                displayStart = { Text("Start") },
                displayEnd = { Text("End") },
                iconEnd = { error("displayEnd must take precedence") }
            )
            Badge("Legacy", iconEnd = { Text("Legacy end") })
            DotBadge(BadgeType.SUCCESS)
            CounterBadge(3)
            StatusBadge("Ready", BadgeType.INFO)
        }

        BadgeType.entries.forEach { assertContains(html, it.name) }
        assertContains(html, "Start")
        assertContains(html, "End")
        assertContains(html, "Legacy end")
        assertContains(html, "role=\"button\"")
        assertContains(html, "role=\"status\"")
        assertFalse(html.contains("displayEnd must take precedence"))
    }
}
