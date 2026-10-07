package codes.yousef.summon.components.feedback

import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class ProgressRenderingContractTest {
    @Test
    fun progressInvocationRendersDeterminateAndIndeterminateAccessibility() {
        Progress(value = 1)()
        val html = PlatformRenderer().renderComposableRoot {
            Progress(value = 25, maxValue = 50, type = ProgressType.LINEAR, label = "Upload")()
            Progress(value = 150, type = ProgressType.CIRCULAR, size = "small")()
            Progress(value = null, type = ProgressType.INDETERMINATE, size = "large")()
        }

        assertContains(html, "aria-valuenow=\"25\"")
        assertContains(html, "aria-valuetext=\"50%\"")
        assertContains(html, "aria-label=\"Upload\"")
        assertContains(html, "aria-valuetext=\"Loading\"")
        assertContains(html, "width: 24px")
        assertFalse(html.contains("aria-valuenow=\"null\""))
    }
}
