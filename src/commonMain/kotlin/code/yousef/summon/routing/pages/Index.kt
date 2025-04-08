package code.yousef.summon.routing.pages


import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal

import code.yousef.summon.routing.NavLink

/**
 * A simple composable function representing the Index (Home) page.
 */
@Composable
fun IndexPage() {
    val composer = CompositionLocal.currentComposer

    Column(
        modifier = Modifier().padding("20px")
    ) {
        Text("Welcome to Summon!")
        Text("This is the index page.")
        
        // Example Navigation
        NavLink(to = "/about") {
            Text("About Page")
        }
        // Add more content/links
    }
} 
