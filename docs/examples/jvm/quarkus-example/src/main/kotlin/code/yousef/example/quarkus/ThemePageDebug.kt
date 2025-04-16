package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * A simplified theme page for debugging the blank page issue
 */
@Composable
fun ThemePageDebug() {
    // Super simple page to debug rendering
    Box(modifier = Modifier().style("class", "container").style("style", "padding: 20px; background-color: #f0f0f0;")) {
        Column {
            // Just a basic text component to verify rendering works
            Text(
                text = "Debug Theme Page",
                modifier = Modifier().style("style", "font-size: 24px; color: red; font-weight: bold;")
            )
            
            // Another simple text component
            Text(
                text = "If you can see this text, basic Summon rendering is working.",
                modifier = Modifier().style("style", "margin-top: 10px;")
            )
        }
    }
} 