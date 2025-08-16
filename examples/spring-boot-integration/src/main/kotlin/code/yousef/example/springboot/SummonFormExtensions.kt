package code.yousef.example.springboot

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.SelectOption
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Div
import code.yousef.summon.state.mutableStateOf

/**
 * Form components that properly render form elements with name attributes for Spring Boot forms.
 */

/**
 * Renders a select element with proper name attribute for form submission.
 */
@Composable
fun FormSelect(
    name: String,
    options: List<Pair<String, String>>,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    
    // Create a custom modifier that includes the name attribute
    val selectModifier = modifier
        .attribute("name", name)
        .attribute("id", name)
    
    // Convert pairs to SelectOption objects for Summon's Select component
    val selectOptions = options.map { (value, text) ->
        SelectOption(
            value = value,
            label = text,
            disabled = false
        )
    }
    
    // Create a dummy state for the selected value (form submission handles the actual value)
    val selectedValue = mutableStateOf<String?>(options.firstOrNull()?.first)
    
    // Use the platform's renderSelect method
    renderer.renderSelect(
        selectedValue = selectedValue.value,
        onSelectedChange = { /* Form submission handles value changes */ },
        options = selectOptions,
        modifier = selectModifier
    )
}

/**
 * Renders an input element with proper name attribute for form submission.
 */
@Composable
fun FormInput(
    name: String,
    type: String = "text",
    value: String = "",
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    
    // Use TextField with a custom modifier that ensures the name attribute is set
    val inputModifier = modifier
        .attribute("name", name)
        .attribute("id", name)
    
    // Use the platform's TextField renderer with a dummy onValueChange
    renderer.renderTextField(
        value = value,
        onValueChange = { /* Form submission handles value changes */ },
        modifier = inputModifier,
        type = type
    )
}

/**
 * Renders a textarea element with proper name attribute for form submission.
 */
@Composable
fun FormTextArea(
    name: String,
    rows: Int = 6,
    value: String = "",
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    
    // Use TextArea with a custom modifier that ensures the name attribute is set
    val textAreaModifier = modifier
        .attribute("name", name)
        .attribute("id", name)
    
    // Use the platform's TextArea renderer
    renderer.renderTextArea(
        value = value,
        onValueChange = { /* Form submission handles value changes */ },
        enabled = true,
        readOnly = false,
        rows = rows,
        maxLength = null,
        placeholder = null,
        modifier = textAreaModifier
    )
}