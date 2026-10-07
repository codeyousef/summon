package codes.yousef.summon.components.feedback

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class LoadingRendererContractTest {
    @Test
    fun everyVariantAndSizeRendersItsDistinctIndicatorAndOptionalText() {
        val combinations = LoadingVariant.entries.zip(LoadingSize.entries)
        combinations.forEach { (variant, size) ->
            val html = PlatformRenderer().renderComposableRoot {
                Loading(
                    variant = variant,
                    size = size,
                    text = "Loading $variant",
                    textModifier = Modifier().attribute("data-loading-text", variant.name),
                )
            }
            assertContains(html, "Loading $variant")
            assertContains(html, "data-loading-text=\"${variant.name}\"")
            assertContains(html, "summon-${variant.name.lowercase()}")
        }
    }

    @Test
    fun hiddenAndTextlessIndicatorsOmitTheirOptionalContent() {
        val hidden = PlatformRenderer().renderComposableRoot { Loading(isVisible = false, text = "hidden") }
        assertFalse(hidden.contains("hidden"))

        val visible = PlatformRenderer().renderComposableRoot {
            Loading(variant = LoadingVariant.SPINNER, size = LoadingSize.SMALL)
        }
        assertContains(visible, "summon-spinner")
        assertFalse(visible.contains("data-loading-text"))
    }
}
