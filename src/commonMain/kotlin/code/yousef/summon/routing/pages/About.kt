package code.yousef.summon.routing.pages


import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal


/**
 * A simple composable function representing the About page.
 */
@Composable
fun AboutPage() {
    val composer = CompositionLocal.currentComposer

    Column(
        modifier = Modifier().padding("20px")
    ) {
        Text("About Summon")
        Text("Summon is a Kotlin Multiplatform UI framework...")
    }
}
