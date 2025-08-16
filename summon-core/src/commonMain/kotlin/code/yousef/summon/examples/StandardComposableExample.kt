package code.yousef.summon.examples

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Div
import code.yousef.summon.extensions.px
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.annotation.StandardComposable as Composable

/**
 * This example demonstrates using the StandardComposable annotation
 * for new composable functions.
 *
 * Following the consolidation outlined in docs/migration/annotation-consolidation.md,
 * new code should use the StandardComposable type alias imported as Composable.
 */
@Composable
fun StandardComposableExample(text: String) {
    Div(
        modifier = Modifier().padding(16.px)
    ) {
        Text("Using StandardComposable: $text")
    }
}

/**
 * A more complex example showing nested components using StandardComposable
 */
@Composable
fun NestedStandardComposableExample() {
    Div {
        StandardComposableExample("Hello World")
        StandardComposableExample("This is a demonstration")
        StandardComposableExample("Of using StandardComposable consistently")
    }
} 