package codes.yousef.summon.test

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.runtime.PlatformRenderer
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PropertyBasedRenderingTest {

    @Test
    fun renderingArbitraryComponentTreesShouldNotCrash() = runTest {
        val renderer = PlatformRenderer()

        checkAll(Generators.componentTree()) { tree ->
            val html = renderer.renderComposableRoot {
                RenderTestComponent(tree)
            }

            // Basic invariants
            assertTrue(html.isNotEmpty(), "Rendered HTML should not be empty")
            // Simple check for tag balance
            assertTrue(html.count { it == '<' } == html.count { it == '>' }, "HTML tags should be balanced")
        }
    }
}

@Composable
fun RenderTestComponent(component: TestComponent) {
    when (component) {
        is TestContainer -> {
            Column {
                component.children.forEach { child ->
                    RenderTestComponent(child)
                }
            }
        }
        is TestText -> {
            Text(text = component.content)
        }
        is TestButton -> {
            Button(label = component.label, onClick = {})
        }
    }
}
