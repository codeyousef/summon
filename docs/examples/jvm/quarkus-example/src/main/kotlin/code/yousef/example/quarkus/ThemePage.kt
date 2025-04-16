package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField as SummonTextField
import code.yousef.summon.components.input.TextFieldType
import code.yousef.summon.components.input.Select as SummonSelect
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.ModifierExtras.attribute
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember
import kotlinx.html.*

/**
 * Page for customizing the application theme
 */
@Composable
fun ThemePage() {
    Box(modifier = Modifier().style("class", "theme-page")) {
        // Page header
        Box(modifier = Modifier().style("class", "page-header").style("style", "margin-bottom: 2rem;")) {
            Text(
                text = "Theme Customization",
                modifier = Modifier().style("style", "font-size: 1.75rem; font-weight: bold;")
            )
            Text(
                text = "Customize your application's look and feel",
                modifier = Modifier().style("style", "font-size: 1rem; color: #666; margin-top: 0.5rem;")
            )
        }

        // Theme form container
        Box(
            modifier = Modifier().style("class", "theme-form-container").style(
                "style",
                "display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 2rem;"
            )
        ) {
            // Colors section
            ThemeSection(
                title = "Colors",
                description = "Customize the color palette of your application"
            ) {
                ColorPicker("Primary Color", "--primary-color", "#4CAF50")
                ColorPicker("Secondary Color", "--secondary-color", "#2196F3")
                ColorPicker("Accent Color", "--accent-color", "#FF5722")
                ColorPicker("Background Color", "--bg-color", "#FFFFFF")
                ColorPicker("Text Color", "--text-color", "#333333")
                ColorPicker("Error Color", "--error-color", "#F44336")
                ColorPicker("Success Color", "--success-color", "#4CAF50")
                ColorPicker("Info Color", "--info-color", "#2196F3")
                ColorPicker("Warning Color", "--warning-color", "#FFC107")
            }

            // Typography section
            ThemeSection(
                title = "Typography",
                description = "Customize text styles and fonts"
            ) {
                RangePicker("Base Font Size", "--base-font-size", "14", "12", "20", "px")
                RangePicker("Heading Scale", "--heading-scale", "1.2", "1.1", "1.5", "")
                FontPicker("Primary Font", "--primary-font", "Roboto")
                FontPicker("Heading Font", "--heading-font", "Poppins")
                RangePicker("Line Height", "--line-height", "1.5", "1.2", "2", "")
                RangePicker("Letter Spacing", "--letter-spacing", "0", "-0.05", "0.1", "em")
            }

            // Layout section
            ThemeSection(
                title = "Layout",
                description = "Customize spacing and borders"
            ) {
                RangePicker("Border Radius", "--border-radius", "4", "0", "12", "px")
                RangePicker("Base Spacing", "--base-spacing", "16", "8", "24", "px")
                RangePicker("Container Max Width", "--container-max-width", "1200", "900", "1600", "px")
                ColorPicker("Border Color", "--border-color", "#DDDDDD")
                RangePicker("Border Width", "--border-width", "1", "0", "3", "px")
            }

            // Components section
            ThemeSection(
                title = "Components",
                description = "Customize specific component styles"
            ) {
                RangePicker("Button Padding", "--button-padding", "0.5", "0.25", "1", "rem")
                ColorPicker("Button Hover Color", "--button-hover-color", "#3D8B40")
                ColorPicker("Input Border Color", "--input-border-color", "#CCCCCC")
                ColorPicker("Input Focus Color", "--input-focus-color", "#2196F3")
                ColorPicker("Table Header BG", "--table-header-bg", "#F5F5F5")
                ColorPicker("Table Border Color", "--table-border-color", "#E0E0E0")
            }
        }

        // Save theme button
        Box(modifier = Modifier().style("style", "margin-top: 2rem; display: flex; justify-content: flex-end;")) {
            Button(
                label = "Save Theme",
                onClick = {},
                modifier = Modifier()
                    .style("class", "btn btn-primary")
                    .style("style", "background-color: var(--primary-color, #4CAF50); padding: 0.75rem 1.5rem;")
                    .style("hx-post", "/api/theme/save")
                    .style("hx-include", ".theme-form-container")
                    .style("hx-target", "#theme-message")
                    .style("hx-swap", "innerHTML")
            )
        }

        // Theme message container
        Box(modifier = Modifier().style("id", "theme-message").style("style", "margin-top: 1rem;")) {
            // Messages will be inserted here via HTMX
        }
    }
}

/**
 * A section in the theme customization page
 */
@Composable
fun ThemeSection(title: String, description: String, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier().style("class", "theme-section").style(
            "style",
            "background-color: #f8f9fa; border-radius: var(--border-radius, 4px); padding: 1.5rem; box-shadow: 0 2px 4px rgba(0,0,0,0.05);"
        )
    ) {
        Text(
            text = title,
            modifier = Modifier().style("style", "font-size: 1.25rem; font-weight: bold; margin-bottom: 0.5rem;")
        )
        Text(
            text = description,
            modifier = Modifier().style("style", "font-size: 0.875rem; color: #666; margin-bottom: 1.5rem;")
        )
        Box(
            modifier = Modifier().style("class", "theme-options")
                .style("style", "display: flex; flex-direction: column; gap: 1rem;")
        ) {
            content()
        }
    }
}

/**
 * Input component delegating to Summon TextField
 */
@Composable
fun Input(
    type: InputType = InputType.text,
    value: String = "",
    name: String? = null,
    min: String? = null,
    max: String? = null,
    step: String? = null,
    onChange: ((String) -> Unit)? = null
) {
    // Map ThemePage InputType to Summon TextFieldType
    val textFieldType = when (type) {
        InputType.text -> TextFieldType.Text
        InputType.password -> TextFieldType.Password
        InputType.email -> TextFieldType.Email
        InputType.number -> TextFieldType.Number
        InputType.tel -> TextFieldType.Tel
        InputType.url -> TextFieldType.Url
        InputType.search -> TextFieldType.Search
        InputType.date -> TextFieldType.Date
        InputType.time -> TextFieldType.Time
        InputType.color -> TextFieldType.Text // Map color/range to Text
        InputType.range -> TextFieldType.Text
        // Add else branch for other types
        else -> TextFieldType.Text 
    }

    // Build the modifier, adding attributes conditionally
    var modifier = Modifier()
    name?.let { modifier = modifier.attribute("name", it) }
    min?.let { modifier = modifier.attribute("min", it) }
    max?.let { modifier = modifier.attribute("max", it) }
    step?.let { modifier = modifier.attribute("step", it) }
    // Important: Also add the original HTML type attribute for color/range
    if (type == InputType.color || type == InputType.range) {
        modifier = modifier.attribute("type", type.toString())
    }

    SummonTextField(
        value = value,
        onValueChange = { newValue -> onChange?.invoke(newValue) },
        modifier = modifier,
        type = textFieldType,
        // We might need to handle JS onChange differently later
        // placeholder = placeholder // Assuming SummonTextField placeholder is sufficient
    )
}

/**
 * Select component (will delegate to SummonSelect)
 */
@Composable
fun Select(
    value: String = "",
    name: String? = null,
    onChange: ((String) -> Unit)? = null,
    optionsData: List<SelectOption<String>>? = null,
    options: @Composable () -> Unit
) {
    if (optionsData != null) {
        // Remember the selected state based on the initial value, ensuring nullable type
        val selectedState = remember { mutableStateOf<String?>(value) } // Explicit nullable type

        // Build the modifier
        var modifier = Modifier()
        name?.let { modifier = modifier.attribute("name", it) }

        // Call the core SummonSelect component
        SummonSelect(
            selectedValue = selectedState, // Pass the nullable state
            options = optionsData,
            onSelectedChange = { newValue ->
                selectedState.value = newValue // Update local state (already nullable)
                onChange?.invoke(newValue ?: "") // Call original callback
            },
            modifier = modifier,
            // label = null, // Pass label if needed
            // placeholder = null, // Pass placeholder if needed
            // disabled = false // Pass disabled state if needed
        )
    } else {
        // Fallback for the case where options are provided via lambda (if ever needed)
        Text("[TODO: Select name=$name value=$value with lambda options]")
        Box { options() }
    }
}

/**
 * Color picker component for theme customization
 */
@Composable
fun ColorPicker(label: String, cssVar: String, defaultValue: String) {
    Box(
        modifier = Modifier().style("class", "theme-option")
            .style("style", "display: flex; align-items: center;")
    ) {
        Text(
            text = label,
            modifier = Modifier().style("style", "flex: 1;")
        )
        Input(
            type = InputType.color,
            value = defaultValue,
            name = cssVar,
            onChange = { /* Handle onChange event */ }
        )
    }
}

/**
 * Range picker component for theme customization
 */
@Composable
fun RangePicker(
    label: String,
    cssVar: String,
    defaultValue: String,
    min: String,
    max: String,
    unit: String
) {
    Box(
        modifier = Modifier().style("class", "theme-option")
            .style("style", "display: flex; flex-direction: column;")
    ) {
        Box(modifier = Modifier().style("style", "display: flex; align-items: center; margin-bottom: 0.5rem;")) {
            Text(
                text = label,
                modifier = Modifier().style("style", "flex: 1;")
            )
            Text(
                text = "$defaultValue$unit",
                modifier = Modifier().style("id", "${cssVar}-value")
                    .style("style", "font-family: monospace; min-width: 50px; text-align: right;")
            )
        }
        Input(
            type = InputType.range,
            value = defaultValue,
            name = cssVar,
            min = min,
            max = max,
            step = "0.1",
            onChange = { newValue: String ->
                println("[RangePicker onChange] Value changed to: $newValue (JS update commented out)")
            }
        )
    }
}

/**
 * Font picker component for theme customization
 */
@Composable
fun FontPicker(label: String, cssVar: String, defaultValue: String) {
    val fonts = listOf(
        "Arial",
        "Helvetica",
        "Roboto",
        "Open Sans",
        "Lato",
        "Montserrat",
        "Poppins",
        "Nunito",
        "Source Sans Pro",
        "PT Sans"
    )

    // Create the list of SelectOption required by SummonSelect
    val selectOptions = fonts.map { font ->
        SelectOption(
            value = font,
            label = font,
            selected = font == defaultValue // selected flag might not be used by SummonSelect directly
        )
    }

    Box(modifier = Modifier().style("class", "theme-option").style("style", "display: flex; align-items: center;")) {
        Text(
            text = label,
            modifier = Modifier().style("style", "flex: 1;")
        )
        Box(modifier = Modifier().style("style", "width: 140px;")) {
            // Call the Select placeholder, passing the generated options list
            // We'll implement the actual SummonSelect call in the Select function next
            Select(
                value = defaultValue,
                name = cssVar,
                onChange = {},
                // Pass the list to our Select placeholder (which needs modification)
                // For now, the lambda is ignored by placeholder, but structure is ready
                optionsData = selectOptions 
            ) { 
                // This lambda block becomes unused when using optionsData
            }
        }
    }
}

/**
 * A row with a color picker and color value display
 */
@Composable
fun ColorPickerRow(
    label: String,
    initialColor: String,
    onColorChange: (String) -> Unit
) {
    val colorValue = remember { mutableStateOf(initialColor) }

    Row(modifier = Modifier().style("style", "margin-bottom: 1rem; align-items: center;")) {
        Column(modifier = Modifier().style("style", "flex: 1;")) {
            Text(
                text = label,
                modifier = Modifier().style("style", "margin-bottom: 0.5rem;")
            )

            Row(modifier = Modifier().style("style", "align-items: center;")) {
                // Color picker
                ColorPicker(
                    label = label,
                    cssVar = label.replace(" ", "-").toLowerCase(),
                    defaultValue = colorValue.value
                )

                // Color hex value
                Text(
                    text = colorValue.value,
                    modifier = Modifier().style(
                        "style",
                        "font-family: monospace; background-color: #f5f5f5; " +
                                "padding: 4px 8px; border-radius: 4px; margin-left: 10px;"
                    )
                )
            }
        }
    }
}

/**
 * Theme preview component
 */
@Composable
fun ThemePreview(
    primaryColor: String,
    textColor: String,
    backgroundColor: String,
    fontSize: Int
) {
    Box(
        modifier = Modifier().style("id", "theme-preview")
            .style(
                "style",
                "margin-top: 2rem; padding: 1.5rem; border: 1px solid #ddd; " +
                        "border-radius: 8px; background-color: $backgroundColor; " +
                        "color: $textColor; font-size: ${fontSize}px;"
            )
    ) {
        Column {
            Text(
                text = "Theme Preview",
                modifier = Modifier().style(
                    "style",
                    "color: $primaryColor; font-size: 1.25rem; font-weight: bold; margin-bottom: 1rem;"
                )
            )

            Text(
                text = "This is a preview of how your theme will look with the selected colors and font size.",
                modifier = Modifier().style("style", "margin-bottom: 1rem;")
            )

            Row {
                Button(
                    label = "Primary Button",
                    onClick = {},
                    modifier = Modifier().style(
                        "style",
                        "background-color: $primaryColor; color: white; border: none; " +
                                "padding: 8px 16px; border-radius: 4px; cursor: pointer;"
                    )
                )

                Button(
                    label = "Secondary Button",
                    onClick = {},
                    modifier = Modifier().style(
                        "style",
                        "background-color: #FF4081; color: white; border: none; " +
                                "padding: 8px 16px; border-radius: 4px; cursor: pointer; margin-left: 1rem;"
                    )
                )
            }
        }
    }
}

/**
 * Apply a predefined theme
 */
private fun applyTheme(theme: String) {
    // This function would be implemented via JS Bridge in a full implementation
    // JavaScript code that would be executed: 
    // document.documentElement.setAttribute('data-theme', theme);
}

/**
 * Apply a custom theme
 */
private fun applyCustomTheme(
    primaryColor: String,
    textColor: String,
    backgroundColor: String,
    fontSize: Int
) {
    // This function would be implemented via JS Bridge in a full implementation
    // JavaScript code that would be executed:
    // document.documentElement.style.setProperty('--primary-color', primaryColor);
    // document.documentElement.style.setProperty('--text-color', textColor);
    // document.documentElement.style.setProperty('--bg-color', backgroundColor);
    // document.documentElement.style.setProperty('--font-size', fontSize + 'px');
}

/** Extension function to capitalize the first letter of a string */
private fun String.capitalize(): String {
    return this.replaceFirstChar { it.uppercase() }
} 