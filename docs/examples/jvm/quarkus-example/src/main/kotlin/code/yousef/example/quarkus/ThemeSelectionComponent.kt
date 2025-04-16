package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * A component that allows selecting different themes for the application.
 */
@Composable
fun ThemeSelectionComponent() {
    Box(modifier = Modifier().style("class", "theme-container").style("style", "max-width: 800px; margin: 0 auto;")) {
        Column {
            Box(
                modifier = Modifier().style(
                    "style",
                    "font-size: 1.5rem; font-weight: bold;"
                )
            ) { Text("Theme Selection") }
            Box(
                modifier = Modifier().style(
                    "style",
                    "margin-bottom: 1rem;"
                )
            ) { Text("Choose a theme that suits your preference. The theme will be applied to the entire application.") }

            Box(
                modifier = Modifier().style("class", "current-theme")
                    .style("style", "margin-bottom: 1rem; font-weight: bold;")
            ) {
                Text("Current Theme: ")
                Box(
                    modifier = Modifier().style("id", "current-theme-name").style("style", "display: inline;")
                ) { Text("Light") }
            }

            Box(
                modifier = Modifier().style("class", "theme-options").style(
                    "style",
                    "display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;"
                )
            ) {
                ThemeOption(
                    name = "Light",
                    description = "Clean and bright theme with blue accents",
                    themeClass = "light"
                )

                ThemeOption(
                    name = "Dark",
                    description = "Dark mode with purple highlights",
                    themeClass = "dark"
                )

                ThemeOption(
                    name = "Forest",
                    description = "Nature-inspired with green accents",
                    themeClass = "forest"
                )

                ThemeOption(
                    name = "Ocean",
                    description = "Calm blue theme inspired by the ocean",
                    themeClass = "ocean"
                )
            }
        }
    }
}

/**
 * A single theme option that can be selected.
 */
@Composable
fun ThemeOption(name: String, description: String, themeClass: String) {
    Box(
        modifier = Modifier()
            .style("data-theme", themeClass)
            .style("class", "theme-option card")
            .style("style", "cursor: pointer; transition: transform 0.2s;")
            .style("hx-post", "/api/theme/$themeClass")
            .style("hx-swap", "none")
    ) {
        Box(modifier = Modifier().style("style", "font-size: 1.25rem; font-weight: bold;")) { Text(name) }
        Box(modifier = Modifier().style("style", "margin: 0.5rem 0;")) { Text(description) }
        Button(
            label = "Select",
            onClick = {},
            modifier = Modifier()
                .style("class", "btn")
                .style("hx-post", "/api/theme/$themeClass")
                .style("hx-target", "#theme-response")
                .style("hx-swap", "innerHTML")
        )
    }
} 