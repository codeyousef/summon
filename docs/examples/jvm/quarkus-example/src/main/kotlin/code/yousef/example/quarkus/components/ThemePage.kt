package code.yousef.example.quarkus.components

import code.yousef.example.quarkus.AppRoot
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.navigation.Link
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * Theme customization page component.
 */
@Composable
fun ThemePage() {
    // Main app content
    AppRoot {
        Column(Modifier().width("100%")) {
            // Page title
            Text("Theme Customization", Modifier().style("style", "font-size: 2.5rem; font-weight: bold; margin-bottom: 1rem;"))
            
            Text("Choose a theme that suits your preference. The theme will be applied to the entire application.")
            
            // Current theme indicator
            Row(Modifier().style("style", "margin: 1rem 0;")) {
                Text("Current Theme: ", Modifier().style("style", "font-weight: bold;"))
                Text("Light", Modifier().style("id", "current-theme-name"))
            }
            
            // Predefined themes
            Card(Modifier().style("style", "margin-bottom: 2rem;")) {
                Text("Predefined Themes", Modifier().style("style", "font-size: 1.5rem; font-weight: bold; margin-bottom: 1rem;"))
                
                // Theme options grid
                Box(Modifier()
                    .style("style", "display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;")
                    .style("class", "theme-options")
                ) {
                    // Light theme
                    ThemeOption("light", "Light", "Clean and bright theme with blue accents", isActive = true)
                    
                    // Dark theme
                    ThemeOption("dark", "Dark", "Modern dark theme with purple accents")
                    
                    // Forest theme
                    ThemeOption("forest", "Forest", "Calming green theme inspired by nature")
                    
                    // Ocean theme
                    ThemeOption("ocean", "Ocean", "Cool blue theme inspired by the sea")
                }
            }
            
            // Custom theme
            Card(Modifier().style("style", "margin-bottom: 2rem;")) {
                Text("Custom Theme", Modifier().style("style", "font-size: 1.5rem; font-weight: bold; margin-bottom: 1rem;"))
                
                Text("Create your own custom theme by adjusting the colors and font size below.")
                
                // Color pickers
                Column(Modifier().style("style", "margin: 1.5rem 0;")) {
                    // Primary color
                    ColorPickerRow("Primary Color", "primary-color", "#4695EB")
                    
                    // Text color
                    ColorPickerRow("Text Color", "text-color", "#333333")
                    
                    // Background color
                    ColorPickerRow("Background Color", "bg-color", "#FFFFFF")
                }
                
                // Font size slider
                Column(Modifier().style("style", "margin-bottom: 1.5rem;")) {
                    Row {
                        Text("Font Size: ")
                        Text("16", Modifier().style("id", "font-size-value"))
                        Text("px")
                    }
                    
                    // Custom HTML for slider with event handler
                    HtmlSlider(
                        id = "font-size-slider",
                        min = 12,
                        max = 24,
                        value = 16,
                        onInput = "document.getElementById('font-size-value').textContent = this.value;"
                    )
                }
                
                // Apply button
                Button(
                    label = "Apply Custom Theme",
                    onClick = { /* Empty function, we'll use onclick attribute instead */ },
                    modifier = Modifier()
                        .style("id", "apply-custom-theme")
                        .style("onclick", "applyCustomTheme(" +
                            "document.getElementById('primary-color').value, " +
                            "document.getElementById('text-color').value, " +
                            "document.getElementById('bg-color').value, " +
                            "document.getElementById('font-size-slider').value);")
                )
                
                // Preview section
                Box(
                    Modifier()
                        .style("style", "margin-top: 2rem; border: 1px solid var(--border-color); padding: 1rem; border-radius: 8px;")
                ) {
                    Text("Theme Preview", Modifier().style("style", "font-size: 1.5rem; font-weight: bold; margin-bottom: 0.5rem;"))
                    
                    Text("This section shows a preview of how your custom theme will look.")
                    
                    // Sample UI elements for preview
                    Row(Modifier().style("style", "margin: 1rem 0;")) {
                        Button(
                            label = "Primary Button",
                            onClick = { /* Empty function */ },
                            modifier = Modifier().style("style", "margin-right: 10px;")
                        )
                        
                        Button(
                            label = "Secondary Button",
                            onClick = { /* Empty function */ },
                            modifier = Modifier().style("style", "background-color: var(--secondary-color); color: white;")
                        )
                    }
                    
                    Row(Modifier().style("style", "margin-top: 10px;")) {
                        Box(Modifier().style("href", "#").style("style", "display: inline;")) {
                            Text("Sample Link")
                        }
                        Text(" - This is a sample text to show what the text will look like.")
                    }
                }
            }
            
            // Add JavaScript for theme switching
            HtmlRawScript("""
                // Function to switch themes
                function switchTheme(theme) {
                    // Remove all theme classes
                    document.body.classList.remove('theme-light', 'theme-dark', 'theme-forest', 'theme-ocean');
                    
                    // Add the selected theme class
                    document.body.classList.add('theme-' + theme);
                    
                    // Update the current theme name
                    document.getElementById('current-theme-name').textContent = theme.charAt(0).toUpperCase() + theme.slice(1);
                    
                    // Remove active class from all theme options
                    document.querySelectorAll('.theme-option').forEach(option => {
                        option.classList.remove('active');
                    });
                    
                    // Add active class to the selected theme
                    document.querySelector('[data-theme="' + theme + '"]').classList.add('active');
                    
                    // Make an AJAX call to update the server-side theme preference
                    fetch('/api/theme/' + theme, { method: 'POST' })
                        .then(response => response.text())
                        .then(data => {
                            console.log('Theme updated on server:', data);
                        })
                        .catch(error => {
                            console.error('Error updating theme:', error);
                        });
                }
                
                // Function to apply custom theme
                function applyCustomTheme(primaryColor, textColor, bgColor, fontSize) {
                    // Apply custom CSS variables
                    document.documentElement.style.setProperty('--primary-color', primaryColor);
                    document.documentElement.style.setProperty('--text-color', textColor);
                    document.documentElement.style.setProperty('--bg-color', bgColor);
                    document.documentElement.style.setProperty('--font-size', fontSize + 'px');
                    
                    // Update the current theme name
                    document.getElementById('current-theme-name').textContent = 'Custom';
                    
                    // Remove active class from all theme options
                    document.querySelectorAll('.theme-option').forEach(option => {
                        option.classList.remove('active');
                    });
                    
                    // Make an AJAX call to update the server-side theme preference
                    const formData = new FormData();
                    formData.append('primaryColor', primaryColor);
                    formData.append('textColor', textColor);
                    formData.append('backgroundColor', bgColor);
                    formData.append('fontSize', fontSize);
                    
                    fetch('/api/theme/custom', {
                        method: 'POST',
                        body: formData
                    })
                        .then(response => response.text())
                        .then(data => {
                            console.log('Custom theme applied:', data);
                        })
                        .catch(error => {
                            console.error('Error applying custom theme:', error);
                        });
                }
                
                // Add click handlers to theme options
                document.addEventListener('DOMContentLoaded', function() {
                    document.querySelectorAll('.theme-option').forEach(option => {
                        option.addEventListener('click', function() {
                            const theme = this.getAttribute('data-theme');
                            switchTheme(theme);
                        });
                    });
                });
            """)
        }
    }
}

/**
 * Theme option card component.
 */
@Composable
fun ThemeOption(theme: String, name: String, description: String, isActive: Boolean = false) {
    Box(
        Modifier()
            .style("class", "theme-option${if (isActive) " active" else ""}")
            .style("data-theme", theme)
            .style("style", "padding: 1rem; border-radius: 8px; cursor: pointer;")
    ) {
        Text(name, Modifier().style("style", "font-size: 1.5rem; font-weight: bold; margin-bottom: 0.5rem;"))
        Text(description)
    }
}

/**
 * Color picker row component.
 */
@Composable
fun ColorPickerRow(label: String, id: String, defaultValue: String) {
    Row(
        Modifier()
            .style("style", "display: flex; align-items: center; margin-bottom: 1rem;")
    ) {
        Box(Modifier().style("style", "width: 150px;")) {
            Text(label, Modifier().style("for", id))
        }
        
        // We need to use HTML directly for color input
        HtmlColorInput(id, defaultValue)
        
        // Text input to show the color value
        TextField(
            value = defaultValue,
            onValueChange = { /* Empty function */ },
            modifier = Modifier()
                .style("size", "7")
                .style("style", "text-align: center;")
                .style("oninput", "document.getElementById('$id').value = this.value;")
        )
    }
}

/**
 * HTML color input component (not available in Summon directly).
 */
@Composable
fun HtmlColorInput(id: String, value: String) {
    Box {
        Text("") // Empty content to satisfy the content parameter
        Box(Modifier().style("html", """<input type="color" id="$id" value="$value" style="margin-right: 10px;">""")) {
            Text("") // Empty content to satisfy the content parameter
        }
    }
}

/**
 * HTML range slider component (not available in Summon directly).
 */
@Composable
fun HtmlSlider(id: String, min: Int, max: Int, value: Int, onInput: String) {
    Box {
        Text("") // Empty content to satisfy the content parameter
        Box(Modifier().style("html", """<input type="range" id="$id" min="$min" max="$max" value="$value" oninput="$onInput">""")) {
            Text("") // Empty content to satisfy the content parameter
        }
    }
}

/**
 * HTML script component to add JavaScript inline.
 */
@Composable
fun HtmlRawScript(script: String) {
    Box {
        Text("") // Empty content to satisfy the content parameter
        Box(Modifier().style("html", """<script>${script}</script>""")) {
            Text("") // Empty content to satisfy the content parameter
        }
    }
} 