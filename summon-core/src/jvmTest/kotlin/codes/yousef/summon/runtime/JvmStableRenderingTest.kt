package codes.yousef.summon.runtime

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.TextField
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import org.jsoup.Jsoup
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmStableRenderingTest {
    @Test
    fun controlledPrivateViewRendersOnceAndDeterministically() {
        fun render() = PlatformRenderer().renderComposableRoot {
            Column {
                Text("Unavailable", Modifier().attribute("data-testid", "request-result"))
                TextField(
                    value = "Synthetic account",
                    onValueChange = {},
                    label = "Account",
                    modifier = Modifier().attribute("data-testid", "controlled-input")
                )
                listOf("one", "two", "three").forEach { item ->
                    key("identity-item", item) {
                        Text(item, Modifier().attribute("data-testid", "identity-$item"))
                    }
                }
            }
        }

        listOf(render(), render()).map(Jsoup::parse).forEach { document ->
            assertEquals(1, document.select("[data-testid=request-result]").size)
            assertEquals("Unavailable", document.selectFirst("[data-testid=request-result]")?.text())
            assertEquals(1, document.select("[data-testid=controlled-input]").size)
            assertEquals("Synthetic account", document.selectFirst("[data-testid=controlled-input]")?.attr("value"))
            assertEquals(
                listOf("one", "two", "three"),
                document.select("[data-testid^=identity-]").map { it.text() }
            )
        }
    }
}
