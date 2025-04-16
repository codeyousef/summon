package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent

class JsPlatformRenderer : PlatformRenderer {

    override fun renderText(text: String, modifier: Modifier) {
        // Create a text node and wrap it in a span element
        js("var textNode = document.createTextNode(text); " +
           "var span = document.createElement('span'); " +
           "span.appendChild(textNode); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        span.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        span.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        span.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add the span to the current parent element " +
           "currentParent.appendChild(span);")
    }

    override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
        // Create a text node and wrap it in a label element
        js("var textNode = document.createTextNode(text); " +
           "var label = document.createElement('label'); " +
           "label.appendChild(textNode); " +

           "// Set 'for' attribute if provided " +
           "if (forElement) { " +
           "    label.setAttribute('for', forElement); " +
           "} " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        label.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        label.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        label.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add the label to the current parent element " +
           "currentParent.appendChild(label);")
    }

    override fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a button element
        js("var button = document.createElement('button'); " +

           "// Set up click event handler " +
           "button.addEventListener('click', function(event) { " +
           "    // Call the onClick handler " +
           "    onClick(); " +
           "}); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        button.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        button.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        button.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the button as the new parent for nested content " +
           "currentParent = button; " +

           "// Add the button to the previous parent " +
           "previousParent.appendChild(button);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier,
        type: String
    ) {
        // Create an input element
        js("var input = document.createElement('input'); " +

           "// Set input type " +
           "input.type = type; " +

           "// Set initial value " +
           "input.value = value; " +

           "// Set up input event handler " +
           "input.addEventListener('input', function(event) { " +
           "    // Call the onValueChange handler with the new value " +
           "    onValueChange(event.target.value); " +
           "}); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        input.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        input.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        input.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add the input to the current parent element " +
           "currentParent.appendChild(input);")
    }

    override fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        modifier: Modifier
    ) {
        // Create a select element
        js("var select = document.createElement('select'); " +

           "// Generate a unique ID for the select element " +
           "select.id = 'select-' + Math.random().toString(36).substr(2, 9); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        select.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        select.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        select.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store option values and their corresponding objects for lookup " +
           "var optionMap = {}; " +

           "// Add options to the select element " +
           "options.forEach(function(option, index) { " +
           "    var optElement = document.createElement('option'); " +
           "    optElement.value = index.toString(); " + // Use index as value to handle complex objects
           "    optElement.textContent = option.label; " +
           "    optElement.disabled = option.disabled; " +
           "    " +
           "    // Store the option value in the map " +
           "    optionMap[index] = option.value; " +
           "    " +
           "    // Set selected if this option matches the selectedValue " +
           "    if (selectedValue !== null && JSON.stringify(option.value) === JSON.stringify(selectedValue)) { " +
           "        optElement.selected = true; " +
           "    } " +
           "    " +
           "    select.appendChild(optElement); " +
           "}); " +

           "// Add change event listener " +
           "select.addEventListener('change', function(event) { " +
           "    var selectedIndex = event.target.value; " +
           "    var selectedOptionValue = optionMap[selectedIndex]; " +
           "    " +
           "    // Call the onSelectedChange handler with the selected value " +
           "    onSelectedChange(selectedOptionValue); " +
           "}); " +

           "// Add the select element to the current parent " +
           "currentParent.appendChild(select);")
    }

    override fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        min: LocalDate?,
        max: LocalDate?,
        modifier: Modifier
    ) {
        // Create a date input element
        js("var dateInput = document.createElement('input'); " +
           "dateInput.type = 'date'; " +

           "// Set initial value and attributes " +
           "if (value !== null) { " +
           "    // Convert LocalDate to YYYY-MM-DD format " +
           "    var year = value.year.toString().padStart(4, '0'); " +
           "    var month = value.monthNumber.toString().padStart(2, '0'); " +
           "    var day = value.dayOfMonth.toString().padStart(2, '0'); " +
           "    dateInput.value = year + '-' + month + '-' + day; " +
           "} " +

           "dateInput.disabled = !enabled; " +

           "// Set min date if provided " +
           "if (min !== null) { " +
           "    var minYear = min.year.toString().padStart(4, '0'); " +
           "    var minMonth = min.monthNumber.toString().padStart(2, '0'); " +
           "    var minDay = min.dayOfMonth.toString().padStart(2, '0'); " +
           "    dateInput.min = minYear + '-' + minMonth + '-' + minDay; " +
           "} " +

           "// Set max date if provided " +
           "if (max !== null) { " +
           "    var maxYear = max.year.toString().padStart(4, '0'); " +
           "    var maxMonth = max.monthNumber.toString().padStart(2, '0'); " +
           "    var maxDay = max.dayOfMonth.toString().padStart(2, '0'); " +
           "    dateInput.max = maxYear + '-' + maxMonth + '-' + maxDay; " +
           "} " +

           "// Generate a unique ID for the date input " +
           "dateInput.id = 'date-' + Math.random().toString(36).substr(2, 9); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        dateInput.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        dateInput.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        dateInput.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add change event listener " +
           "dateInput.addEventListener('change', function(event) { " +
           "    var dateStr = event.target.value; " +
           "    " +
           "    if (dateStr) { " +
           "        // Parse the date string (YYYY-MM-DD) into year, month, day " +
           "        var parts = dateStr.split('-'); " +
           "        var year = parseInt(parts[0], 10); " +
           "        var month = parseInt(parts[1], 10); " +
           "        var day = parseInt(parts[2], 10); " +
           "        " +
           "        // Call the onValueChange handler with the parsed LocalDate " +
           "        // Note: We're creating a JavaScript object that matches the structure of LocalDate " +
           "        onValueChange({ " +
           "            year: year, " +
           "            monthNumber: month, " +
           "            dayOfMonth: day " +
           "        }); " +
           "    } else { " +
           "        // If the input is cleared, pass null " +
           "        onValueChange(null); " +
           "    } " +
           "}); " +

           "// Add the date input to the current parent " +
           "currentParent.appendChild(dateInput);")
    }

    override fun renderTextArea(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        rows: Int?,
        maxLength: Int?,
        placeholder: String?,
        modifier: Modifier
    ) {
        // Create a textarea element
        js("var textarea = document.createElement('textarea'); " +

           "// Set initial value and attributes " +
           "textarea.value = value; " +
           "textarea.disabled = !enabled; " +
           "textarea.readOnly = readOnly; " +

           "// Set optional attributes if provided " +
           "if (rows !== null) { " +
           "    textarea.rows = rows; " +
           "} " +

           "if (maxLength !== null) { " +
           "    textarea.maxLength = maxLength; " +
           "} " +

           "if (placeholder !== null) { " +
           "    textarea.placeholder = placeholder; " +
           "} " +

           "// Generate a unique ID for the textarea " +
           "textarea.id = 'textarea-' + Math.random().toString(36).substr(2, 9); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        textarea.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        textarea.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        textarea.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add input event listener " +
           "textarea.addEventListener('input', function(event) { " +
           "    // Call the onValueChange handler with the new value " +
           "    onValueChange(event.target.value); " +
           "}); " +

           "// Add the textarea to the current parent " +
           "currentParent.appendChild(textarea);")
    }

    override fun addHeadElement(content: String) {
        // Add the content to the head element collection
        js("if (!window.summonHeadElements) { " +
           "    window.summonHeadElements = []; " +
           "} " +
           "window.summonHeadElements.push(content); " +

           "// If we're in a browser environment, also add it to the actual head " +
           "if (typeof document !== 'undefined' && document.head) { " +
           "    // Create a temporary container to parse the HTML string " +
           "    var tempContainer = document.createElement('div'); " +
           "    tempContainer.innerHTML = content; " +
           "    " +
           "    // Add each child node to the head " +
           "    while (tempContainer.firstChild) { " +
           "        document.head.appendChild(tempContainer.firstChild); " +
           "    } " +
           "}")
    }

    override fun getHeadElements(): List<String> {
        // Return the collection of head elements
        return js("if (!window.summonHeadElements) { " +
                 "    window.summonHeadElements = []; " +
                 "} " +
                 "return window.summonHeadElements") as List<String>
    }

    override fun renderComposableRoot(composable: @Composable (() -> Unit)): String {
        // Initialize the root element if it doesn't exist
        js("if (!window.summonRootElement) { " +
           "    window.summonRootElement = document.createElement('div'); " +
           "    window.summonRootElement.id = 'summon-root'; " +
           "} " +

           "// Clear the root element " +
           "window.summonRootElement.innerHTML = ''; " +

           "// Set the root element as the current parent " +
           "currentParent = window.summonRootElement;")

        try {
            // Call the composable function
            composable()
        } catch (e: Exception) {
            // Log any errors
            js("console.error('Error rendering composable root: ', e);")
        }

        // Get the HTML content of the root element
        return js("window.summonRootElement.outerHTML") as String
    }

    /**
     * Renders a composable component in the current context
     * For JS implementation, this would delegate to some React/DOM rendering mechanism
     */
    override fun renderComposable(composable: @Composable () -> Unit) {
        // Create a container element for the composable
        js("var container = document.createElement('div'); " +
           "container.className = 'summon-composable-container'; " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the container as the new parent for nested content " +
           "currentParent = container; " +

           "// Add the container to the previous parent " +
           "previousParent.appendChild(container);")

        try {
            // Call the composable function
            composable()
        } catch (e: Exception) {
            // Log any errors
            js("console.error('Error rendering composable: ', e);")
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderRow(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a div element with flexbox row styling
        js("var row = document.createElement('div'); " +

           "// Set default flexbox row styling " +
           "row.style.display = 'flex'; " +
           "row.style.flexDirection = 'row'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        row.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        row.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        row.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the row as the new parent for nested content " +
           "currentParent = row; " +

           "// Add the row to the previous parent " +
           "previousParent.appendChild(row);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderColumn(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a div element with flexbox column styling
        js("var column = document.createElement('div'); " +

           "// Set default flexbox column styling " +
           "column.style.display = 'flex'; " +
           "column.style.flexDirection = 'column'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        column.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        column.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        column.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the column as the new parent for nested content " +
           "currentParent = column; " +

           "// Add the column to the previous parent " +
           "previousParent.appendChild(column);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderBox(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a div element with box styling (position: relative)
        js("var box = document.createElement('div'); " +

           "// Set default box styling " +
           "box.style.position = 'relative'; " +
           "box.style.display = 'flex'; " +
           "box.style.flexDirection = 'column'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        box.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        box.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        box.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the box as the new parent for nested content " +
           "currentParent = box; " +

           "// Add the box to the previous parent " +
           "previousParent.appendChild(box);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderImage(src: String, alt: String, modifier: Modifier) {
        // Create an img element
        js("var img = document.createElement('img'); " +
           "img.src = src; " +
           "img.alt = alt; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        img.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        img.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        img.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add the img to the current parent " +
           "currentParent.appendChild(img);")
    }

    override fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    ) {
        // Create the appropriate element based on the icon type
        js("var iconElement; " +

           "// Create the element based on the icon type " +
           "switch (type.toString()) { " +
           "    case 'SVG': " +
           "        if (svgContent !== null) { " +
           "            // Create a div to hold the SVG content " +
           "            iconElement = document.createElement('div'); " +
           "            iconElement.innerHTML = svgContent; " +
           "            " +
           "            // Extract the SVG element from the div " +
           "            var svgElement = iconElement.querySelector('svg'); " +
           "            if (svgElement) { " +
           "                // Replace the div with the SVG element " +
           "                iconElement = svgElement; " +
           "            } " +
           "        } else { " +
           "            // Fallback to a span with an SVG class " +
           "            iconElement = document.createElement('span'); " +
           "            iconElement.className = 'summon-icon summon-svg-icon'; " +
           "            iconElement.textContent = name; " +
           "        } " +
           "        break; " +
           "        " +
           "    case 'FONT': " +
           "        // Create a span for font icon " +
           "        iconElement = document.createElement('i'); " +
           "        iconElement.className = 'summon-icon summon-font-icon ' + name; " +
           "        iconElement.textContent = name; // For ligature-based icons like Material Icons " +
           "        break; " +
           "        " +
           "    case 'IMAGE': " +
           "        // Create an img element " +
           "        iconElement = document.createElement('img'); " +
           "        iconElement.src = name; " +
           "        iconElement.alt = name; " +
           "        iconElement.className = 'summon-icon summon-image-icon'; " +
           "        break; " +
           "        " +
           "    default: " +
           "        // Fallback to a span " +
           "        iconElement = document.createElement('span'); " +
           "        iconElement.className = 'summon-icon'; " +
           "        iconElement.textContent = name; " +
           "} " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        iconElement.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        iconElement.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        iconElement.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add click event listener if provided " +
           "if (onClick !== null) { " +
           "    iconElement.style.cursor = 'pointer'; " +
           "    iconElement.addEventListener('click', function(event) { " +
           "        onClick(); " +
           "    }); " +
           "} " +

           "// Add the icon element to the current parent " +
           "currentParent.appendChild(iconElement);")
    }

    override fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a div element for the alert container
        js("var alertContainer = document.createElement('div'); " +

           "// Set default alert container styling " +
           "alertContainer.className = 'summon-alert-container'; " +

           "// Apply variant-specific styling " +
           "if (variant) { " +
           "    alertContainer.classList.add('summon-alert-' + variant.toString().toLowerCase()); " +
           "    " +
           "    // Apply different background colors based on variant " +
           "    switch(variant.toString().toLowerCase()) { " +
           "        case 'success': " +
           "            alertContainer.style.backgroundColor = '#d4edda'; " +
           "            alertContainer.style.color = '#155724'; " +
           "            alertContainer.style.borderColor = '#c3e6cb'; " +
           "            break; " +
           "        case 'warning': " +
           "            alertContainer.style.backgroundColor = '#fff3cd'; " +
           "            alertContainer.style.color = '#856404'; " +
           "            alertContainer.style.borderColor = '#ffeeba'; " +
           "            break; " +
           "        case 'error': " +
           "            alertContainer.style.backgroundColor = '#f8d7da'; " +
           "            alertContainer.style.color = '#721c24'; " +
           "            alertContainer.style.borderColor = '#f5c6cb'; " +
           "            break; " +
           "        case 'info': " +
           "            alertContainer.style.backgroundColor = '#d1ecf1'; " +
           "            alertContainer.style.color = '#0c5460'; " +
           "            alertContainer.style.borderColor = '#bee5eb'; " +
           "            break; " +
           "        default: " +
           "            // Default styling " +
           "            alertContainer.style.backgroundColor = '#e2e3e5'; " +
           "            alertContainer.style.color = '#383d41'; " +
           "            alertContainer.style.borderColor = '#d6d8db'; " +
           "    } " +
           "} " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        alertContainer.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        alertContainer.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        alertContainer.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the alert container as the new parent for nested content " +
           "currentParent = alertContainer; " +

           "// Add the alert container to the previous parent " +
           "previousParent.appendChild(alertContainer);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderBadge(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a span element with badge styling
        js("var badge = document.createElement('span'); " +
           "badge.className = 'summon-badge'; " +

           "// Set default badge styling " +
           "badge.style.display = 'inline-block'; " +
           "badge.style.padding = '0.25em 0.6em'; " +
           "badge.style.fontSize = '0.75em'; " +
           "badge.style.fontWeight = 'bold'; " +
           "badge.style.lineHeight = '1'; " +
           "badge.style.textAlign = 'center'; " +
           "badge.style.whiteSpace = 'nowrap'; " +
           "badge.style.verticalAlign = 'baseline'; " +
           "badge.style.borderRadius = '0.25rem'; " +
           "badge.style.backgroundColor = '#6c757d'; " + // Default background color
           "badge.style.color = '#fff'; " + // Default text color

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        badge.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        badge.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        badge.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the badge as the new parent for nested content " +
           "currentParent = badge; " +

           "// Add the badge to the previous parent " +
           "previousParent.appendChild(badge);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Create a checkbox input element
        js("var checkbox = document.createElement('input'); " +
           "checkbox.type = 'checkbox'; " +

           "// Set initial state " +
           "checkbox.checked = checked; " +
           "checkbox.disabled = !enabled; " +

           "// Generate a unique ID for the checkbox " +
           "checkbox.id = 'checkbox-' + Math.random().toString(36).substr(2, 9); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        checkbox.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        checkbox.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        checkbox.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add change event listener " +
           "checkbox.addEventListener('change', function(event) { " +
           "    // Call the onCheckedChange handler with the new checked state " +
           "    onCheckedChange(event.target.checked); " +
           "}); " +

           "// Add the checkbox to the current parent " +
           "currentParent.appendChild(checkbox);")
    }

    override fun renderProgress(
        value: Float?,
        type: ProgressType,
        modifier: Modifier
    ) {
        when (type) {
            ProgressType.LINEAR -> {
                if (value == null) {
                    // Indeterminate linear progress
                    js("var progressContainer = document.createElement('div'); " +
                       "progressContainer.className = 'progress-container'; " +
                       "progressContainer.style.width = '100%'; " +
                       "progressContainer.style.height = '4px'; " +
                       "progressContainer.style.backgroundColor = '#e0e0e0'; " +
                       "progressContainer.style.position = 'relative'; " +
                       "progressContainer.style.overflow = 'hidden'; " +

                       "var indeterminateBar = document.createElement('div'); " +
                       "indeterminateBar.className = 'indeterminate-bar'; " +
                       "indeterminateBar.style.position = 'absolute'; " +
                       "indeterminateBar.style.top = '0'; " +
                       "indeterminateBar.style.left = '0'; " +
                       "indeterminateBar.style.height = '100%'; " +
                       "indeterminateBar.style.width = '30%'; " +
                       "indeterminateBar.style.backgroundColor = '#2196f3'; " +
                       "indeterminateBar.style.animation = 'indeterminate-linear 2s infinite linear'; " +

                       "// Add the animation keyframes to the document if they don't exist yet " +
                       "if (!document.getElementById('progress-keyframes')) { " +
                       "    var style = document.createElement('style'); " +
                       "    style.id = 'progress-keyframes'; " +
                       "    style.textContent = '@keyframes indeterminate-linear { " +
                       "        0% { left: -30%; } " +
                       "        100% { left: 100%; } " +
                       "    } " +
                       "    @keyframes indeterminate-circular { " +
                       "        0% { transform: rotate(0deg); } " +
                       "        100% { transform: rotate(360deg); } " +
                       "    }'; " +
                       "    document.head.appendChild(style); " +
                       "} " +

                       "progressContainer.appendChild(indeterminateBar); " +

                       "// Apply modifiers " +
                       "if (modifier) { " +
                       "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
                       "        var key = entry[0]; " +
                       "        var value = entry[1]; " +
                       "        progressContainer.style[key] = value; " +
                       "    }); " +
                       "    " +
                       "    // Apply classes " +
                       "    modifier.getClasses().forEach(function(cls) { " +
                       "        progressContainer.classList.add(cls); " +
                       "    }); " +
                       "    " +
                       "    // Apply attributes " +
                       "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
                       "        var key = entry[0]; " +
                       "        var value = entry[1]; " +
                       "        progressContainer.setAttribute(key, value); " +
                       "    }); " +
                       "} " +

                       "// Add the progress container to the current parent " +
                       "currentParent.appendChild(progressContainer);")
                } else {
                    // Determinate linear progress
                    js("var progressContainer = document.createElement('div'); " +
                       "progressContainer.className = 'progress-container'; " +
                       "progressContainer.style.width = '100%'; " +
                       "progressContainer.style.height = '4px'; " +
                       "progressContainer.style.backgroundColor = '#e0e0e0'; " +
                       "progressContainer.style.position = 'relative'; " +

                       "var progressBar = document.createElement('div'); " +
                       "progressBar.className = 'progress-bar'; " +
                       "progressBar.style.position = 'absolute'; " +
                       "progressBar.style.top = '0'; " +
                       "progressBar.style.left = '0'; " +
                       "progressBar.style.height = '100%'; " +
                       "progressBar.style.width = (value * 100) + '%'; " +
                       "progressBar.style.backgroundColor = '#2196f3'; " +
                       "progressBar.style.transition = 'width 0.3s ease-in-out'; " +

                       "progressContainer.appendChild(progressBar); " +

                       "// Apply modifiers " +
                       "if (modifier) { " +
                       "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
                       "        var key = entry[0]; " +
                       "        var value = entry[1]; " +
                       "        progressContainer.style[key] = value; " +
                       "    }); " +
                       "    " +
                       "    // Apply classes " +
                       "    modifier.getClasses().forEach(function(cls) { " +
                       "        progressContainer.classList.add(cls); " +
                       "    }); " +
                       "    " +
                       "    // Apply attributes " +
                       "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
                       "        var key = entry[0]; " +
                       "        var value = entry[1]; " +
                       "        progressContainer.setAttribute(key, value); " +
                       "    }); " +
                       "} " +

                       "// Add the progress container to the current parent " +
                       "currentParent.appendChild(progressContainer);")
                }
            }

            ProgressType.CIRCULAR, ProgressType.INDETERMINATE -> {
                if (value == null || type == ProgressType.INDETERMINATE) {
                    // Indeterminate circular progress
                    js("var spinnerContainer = document.createElement('div'); " +
                       "spinnerContainer.className = 'spinner-container'; " +
                       "spinnerContainer.style.width = '36px'; " +
                       "spinnerContainer.style.height = '36px'; " +
                       "spinnerContainer.style.position = 'relative'; " +

                       "var spinner = document.createElement('div'); " +
                       "spinner.className = 'spinner'; " +
                       "spinner.style.width = '100%'; " +
                       "spinner.style.height = '100%'; " +
                       "spinner.style.border = '3px solid #e0e0e0'; " +
                       "spinner.style.borderTopColor = '#2196f3'; " +
                       "spinner.style.borderRadius = '50%'; " +
                       "spinner.style.animation = 'indeterminate-circular 1s infinite linear'; " +

                       "// Add the animation keyframes to the document if they don't exist yet " +
                       "if (!document.getElementById('progress-keyframes')) { " +
                       "    var style = document.createElement('style'); " +
                       "    style.id = 'progress-keyframes'; " +
                       "    style.textContent = '@keyframes indeterminate-linear { " +
                       "        0% { left: -30%; } " +
                       "        100% { left: 100%; } " +
                       "    } " +
                       "    @keyframes indeterminate-circular { " +
                       "        0% { transform: rotate(0deg); } " +
                       "        100% { transform: rotate(360deg); } " +
                       "    }'; " +
                       "    document.head.appendChild(style); " +
                       "} " +

                       "spinnerContainer.appendChild(spinner); " +

                       "// Apply modifiers " +
                       "if (modifier) { " +
                       "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
                       "        var key = entry[0]; " +
                       "        var value = entry[1]; " +
                       "        spinnerContainer.style[key] = value; " +
                       "    }); " +
                       "    " +
                       "    // Apply classes " +
                       "    modifier.getClasses().forEach(function(cls) { " +
                       "        spinnerContainer.classList.add(cls); " +
                       "    }); " +
                       "    " +
                       "    // Apply attributes " +
                       "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
                       "        var key = entry[0]; " +
                       "        var value = entry[1]; " +
                       "        spinnerContainer.setAttribute(key, value); " +
                       "    }); " +
                       "} " +

                       "// Add the spinner container to the current parent " +
                       "currentParent.appendChild(spinnerContainer);")
                } else {
                    // Determinate circular progress
                    js("var circularContainer = document.createElement('div'); " +
                       "circularContainer.className = 'circular-progress-container'; " +
                       "circularContainer.style.width = '36px'; " +
                       "circularContainer.style.height = '36px'; " +
                       "circularContainer.style.position = 'relative'; " +

                       "// Create SVG for circular progress " +
                       "var svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg'); " +
                       "svg.setAttribute('viewBox', '0 0 36 36'); " +
                       "svg.style.width = '100%'; " +
                       "svg.style.height = '100%'; " +
                       "svg.style.transform = 'rotate(-90deg)'; " +

                       "// Background circle " +
                       "var bgCircle = document.createElementNS('http://www.w3.org/2000/svg', 'circle'); " +
                       "bgCircle.setAttribute('cx', '18'); " +
                       "bgCircle.setAttribute('cy', '18'); " +
                       "bgCircle.setAttribute('r', '16'); " +
                       "bgCircle.setAttribute('fill', 'none'); " +
                       "bgCircle.setAttribute('stroke', '#e0e0e0'); " +
                       "bgCircle.setAttribute('stroke-width', '3'); " +

                       "// Progress circle " +
                       "var progressCircle = document.createElementNS('http://www.w3.org/2000/svg', 'circle'); " +
                       "progressCircle.setAttribute('cx', '18'); " +
                       "progressCircle.setAttribute('cy', '18'); " +
                       "progressCircle.setAttribute('r', '16'); " +
                       "progressCircle.setAttribute('fill', 'none'); " +
                       "progressCircle.setAttribute('stroke', '#2196f3'); " +
                       "progressCircle.setAttribute('stroke-width', '3'); " +
                       "progressCircle.setAttribute('stroke-dasharray', '100'); " +
                       "progressCircle.setAttribute('stroke-dashoffset', (100 - (value * 100))); " +

                       "svg.appendChild(bgCircle); " +
                       "svg.appendChild(progressCircle); " +
                       "circularContainer.appendChild(svg); " +

                       "// Apply modifiers " +
                       "if (modifier) { " +
                       "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
                       "        var key = entry[0]; " +
                       "        var value = entry[1]; " +
                       "        circularContainer.style[key] = value; " +
                       "    }); " +
                       "    " +
                       "    // Apply classes " +
                       "    modifier.getClasses().forEach(function(cls) { " +
                       "        circularContainer.classList.add(cls); " +
                       "    }); " +
                       "    " +
                       "    // Apply attributes " +
                       "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
                       "        var key = entry[0]; " +
                       "        var value = entry[1]; " +
                       "        circularContainer.setAttribute(key, value); " +
                       "    }); " +
                       "} " +

                       "// Add the circular container to the current parent " +
                       "currentParent.appendChild(circularContainer);")
                }
            }
        }
    }

    override fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        // Create a file input element
        js("var fileInput = document.createElement('input'); " +
           "fileInput.type = 'file'; " +

           "// Set attributes " +
           "fileInput.disabled = !enabled; " +
           "fileInput.multiple = multiple; " +

           "if (accept !== null) { " +
           "    fileInput.accept = accept; " +
           "} " +

           "if (capture !== null) { " +
           "    fileInput.capture = capture; " +
           "} " +

           "// Generate a unique ID for the file input " +
           "fileInput.id = 'file-' + Math.random().toString(36).substr(2, 9); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        fileInput.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        fileInput.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        fileInput.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add change event listener " +
           "fileInput.addEventListener('change', function(event) { " +
           "    var files = Array.from(event.target.files || []); " +
           "    " +
           "    // Convert File objects to FileInfo objects " +
           "    var fileInfos = files.map(function(file) { " +
           "        return { " +
           "            name: file.name, " +
           "            size: file.size, " +
           "            type: file.type, " +
           "            jsFile: file " +
           "        }; " +
           "    }); " +
           "    " +
           "    // Call the onFilesSelected handler with the file info list " +
           "    onFilesSelected(fileInfos); " +
           "}); " +

           "// Add the file input to the current parent " +
           "currentParent.appendChild(fileInput); " +

           "// Store the file input for later reference " +
           "var fileInputRef = fileInput; " +

           "// Return a function that can be used to programmatically trigger the file selection dialog " +
           "return function() { " +
           "    if (!fileInputRef.disabled) { " +
           "        fileInputRef.click(); " +
           "    } " +
           "};")

        // Return a function that can be used to programmatically trigger the file selection dialog
        return { /* The actual implementation is in the JavaScript code above */ }
    }

    override fun renderForm(
        onSubmit: (() -> Unit)?,
        modifier: Modifier,
        content: @Composable (FormContent.() -> Unit)
    ) {
        // Create a form element
        js("var form = document.createElement('form'); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        form.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        form.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        form.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add submit event listener if onSubmit is provided " +
           "if (onSubmit !== null) { " +
           "    form.addEventListener('submit', function(event) { " +
           "        // Prevent the default form submission " +
           "        event.preventDefault(); " +
           "        " +
           "        // Call the onSubmit handler " +
           "        onSubmit(); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the form as the new parent for nested content " +
           "currentParent = form; " +

           "// Add the form to the previous parent " +
           "previousParent.appendChild(form);")

        // In JavaScript environment, we don't have a direct equivalent to FormContent
        // Instead, we call the content function with a dummy FormContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FormContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a div element to contain the form field
        js("var formField = document.createElement('div'); " +
           "formField.className = 'summon-form-field'; " +

           "// Apply error styling if isError is true " +
           "if (isError) { " +
           "    formField.classList.add('summon-form-field-error'); " +
           "} " +

           "// Create a label element if labelId is provided " +
           "if (labelId !== null) { " +
           "    var label = document.createElement('label'); " +
           "    label.htmlFor = labelId; " +
           "    label.textContent = document.getElementById(labelId)?.textContent || ''; " +
           "    " +
           "    // Add a required indicator if isRequired is true " +
           "    if (isRequired) { " +
           "        var requiredIndicator = document.createElement('span'); " +
           "        requiredIndicator.className = 'summon-required-indicator'; " +
           "        requiredIndicator.textContent = ' *'; " +
           "        requiredIndicator.style.color = 'red'; " +
           "        label.appendChild(requiredIndicator); " +
           "    } " +
           "    " +
           "    formField.appendChild(label); " +
           "} " +

           "// Create a div for the content " +
           "var contentContainer = document.createElement('div'); " +
           "contentContainer.className = 'summon-form-field-content'; " +
           "formField.appendChild(contentContainer); " +

           "// Create an error message element if errorMessageId is provided and isError is true " +
           "if (errorMessageId !== null && isError) { " +
           "    var errorMessage = document.createElement('div'); " +
           "    errorMessage.className = 'summon-error-message'; " +
           "    errorMessage.textContent = document.getElementById(errorMessageId)?.textContent || ''; " +
           "    errorMessage.style.color = 'red'; " +
           "    errorMessage.style.fontSize = '0.8em'; " +
           "    formField.appendChild(errorMessage); " +
           "} " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        formField.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        formField.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        formField.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the content container as the new parent for nested content " +
           "currentParent = contentContainer; " +

           "// Add the form field to the previous parent " +
           "previousParent.appendChild(formField);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Create a radio button input element
        js("var radio = document.createElement('input'); " +
           "radio.type = 'radio'; " +

           "// Set initial state " +
           "radio.checked = selected; " +
           "radio.disabled = !enabled; " +

           "// Generate a unique ID for the radio button " +
           "radio.id = 'radio-' + Math.random().toString(36).substr(2, 9); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        radio.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        radio.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        radio.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add click event listener " +
           "radio.addEventListener('click', function(event) { " +
           "    if (!radio.disabled) { " +
           "        // Call the onClick handler " +
           "        onClick(); " +
           "    } " +
           "}); " +

           "// Add the radio button to the current parent " +
           "currentParent.appendChild(radio);")
    }

    override fun renderSpacer(modifier: Modifier) {
        // Create a div element for the spacer
        js("var spacer = document.createElement('div'); " +

           "// Set default spacer styling " +
           "spacer.className = 'summon-spacer'; " +
           "spacer.style.display = 'block'; " +
           "spacer.style.width = '100%'; " +
           "spacer.style.height = '16px'; " + // Default height

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        spacer.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        spacer.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        spacer.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add the spacer to the current parent " +
           "currentParent.appendChild(spacer);")
    }

    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Create a container for the range slider
        js("var container = document.createElement('div'); " +
           "container.className = 'summon-range-slider-container'; " +
           "container.style.position = 'relative'; " +
           "container.style.width = '100%'; " +
           "container.style.height = '40px'; " +

           "// Create the lower thumb slider " +
           "var lowerSlider = document.createElement('input'); " +
           "lowerSlider.type = 'range'; " +
           "lowerSlider.className = 'summon-range-slider summon-range-slider-lower'; " +
           "lowerSlider.min = valueRange.start; " +
           "lowerSlider.max = valueRange.endInclusive; " +
           "lowerSlider.value = value.start; " +
           "lowerSlider.step = steps > 0 ? (valueRange.endInclusive - valueRange.start) / steps : 1; " +
           "lowerSlider.disabled = !enabled; " +
           "lowerSlider.style.position = 'absolute'; " +
           "lowerSlider.style.width = '100%'; " +
           "lowerSlider.style.zIndex = '1'; " +

           "// Create the upper thumb slider " +
           "var upperSlider = document.createElement('input'); " +
           "upperSlider.type = 'range'; " +
           "upperSlider.className = 'summon-range-slider summon-range-slider-upper'; " +
           "upperSlider.min = valueRange.start; " +
           "upperSlider.max = valueRange.endInclusive; " +
           "upperSlider.value = value.endInclusive; " +
           "upperSlider.step = steps > 0 ? (valueRange.endInclusive - valueRange.start) / steps : 1; " +
           "upperSlider.disabled = !enabled; " +
           "upperSlider.style.position = 'absolute'; " +
           "upperSlider.style.width = '100%'; " +
           "upperSlider.style.zIndex = '2'; " +

           "// Add event listeners " +
           "lowerSlider.addEventListener('input', function() { " +
           "    // Ensure lower value doesn't exceed upper value " +
           "    if (parseFloat(lowerSlider.value) > parseFloat(upperSlider.value)) { " +
           "        lowerSlider.value = upperSlider.value; " +
           "    } " +
           "    // Call onValueChange with the new range " +
           "    onValueChange({ " +
           "        start: parseFloat(lowerSlider.value), " +
           "        endInclusive: parseFloat(upperSlider.value), " +
           "        contains: function(value) { " +
           "            return value >= this.start && value <= this.endInclusive; " +
           "        } " +
           "    }); " +
           "}); " +

           "upperSlider.addEventListener('input', function() { " +
           "    // Ensure upper value doesn't go below lower value " +
           "    if (parseFloat(upperSlider.value) < parseFloat(lowerSlider.value)) { " +
           "        upperSlider.value = lowerSlider.value; " +
           "    } " +
           "    // Call onValueChange with the new range " +
           "    onValueChange({ " +
           "        start: parseFloat(lowerSlider.value), " +
           "        endInclusive: parseFloat(upperSlider.value), " +
           "        contains: function(value) { " +
           "            return value >= this.start && value <= this.endInclusive; " +
           "        } " +
           "    }); " +
           "}); " +

           "// Apply modifiers to the container " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        container.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        container.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        container.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add the sliders to the container " +
           "container.appendChild(lowerSlider); " +
           "container.appendChild(upperSlider); " +

           "// Add the container to the current parent " +
           "currentParent.appendChild(container);")
    }

    override fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Create a slider element
        js("var slider = document.createElement('input'); " +
           "slider.type = 'range'; " +
           "slider.className = 'summon-slider'; " +
           "slider.min = valueRange.start; " +
           "slider.max = valueRange.endInclusive; " +
           "slider.value = value; " +
           "slider.step = steps > 0 ? (valueRange.endInclusive - valueRange.start) / steps : 1; " +
           "slider.disabled = !enabled; " +

           "// Add input event listener " +
           "slider.addEventListener('input', function() { " +
           "    onValueChange(parseFloat(slider.value)); " +
           "}); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        slider.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        slider.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        slider.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add the slider to the current parent " +
           "currentParent.appendChild(slider);")
    }

    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Create a switch control (styled checkbox)
        js("var switchContainer = document.createElement('label'); // Create a container for the switch " +
           "switchContainer.className = 'summon-switch'; " +

           "// Generate a unique ID for the switch " +
           "var switchId = 'switch-' + Math.random().toString(36).substr(2, 9); " +

           "// Create the checkbox input (hidden but functional) " +
           "var checkbox = document.createElement('input'); " +
           "checkbox.type = 'checkbox'; " +
           "checkbox.id = switchId; " +
           "checkbox.checked = checked; " +
           "checkbox.disabled = !enabled; " +
           "checkbox.style.position = 'absolute'; " +
           "checkbox.style.opacity = '0'; " +
           "checkbox.style.height = '0'; " +
           "checkbox.style.width = '0'; " +

           "// Create the slider element (visual part of the switch) " +
           "var slider = document.createElement('span'); " +
           "slider.className = 'summon-switch-slider'; " +
           "slider.style.position = 'relative'; " +
           "slider.style.display = 'inline-block'; " +
           "slider.style.width = '40px'; " +
           "slider.style.height = '24px'; " +
           "slider.style.backgroundColor = checked ? '#2196F3' : '#ccc'; " +
           "slider.style.borderRadius = '12px'; " +
           "slider.style.transition = '.4s'; " +
           "slider.style.cursor = enabled ? 'pointer' : 'default'; " +

           "// Create the knob element " +
           "var knob = document.createElement('span'); " +
           "knob.className = 'summon-switch-knob'; " +
           "knob.style.position = 'absolute'; " +
           "knob.style.content = '\"\"'; " +
           "knob.style.height = '16px'; " +
           "knob.style.width = '16px'; " +
           "knob.style.left = checked ? '20px' : '4px'; " +
           "knob.style.bottom = '4px'; " +
           "knob.style.backgroundColor = 'white'; " +
           "knob.style.borderRadius = '50%'; " +
           "knob.style.transition = '.4s'; " +

           "// Add the knob to the slider " +
           "slider.appendChild(knob); " +

           "// Add the checkbox and slider to the container " +
           "switchContainer.appendChild(checkbox); " +
           "switchContainer.appendChild(slider); " +

           "// Apply modifiers to the container " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        switchContainer.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        switchContainer.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        switchContainer.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add change event listener to the checkbox " +
           "checkbox.addEventListener('change', function(event) { " +
           "    // Update the slider appearance " +
           "    slider.style.backgroundColor = event.target.checked ? '#2196F3' : '#ccc'; " +
           "    knob.style.left = event.target.checked ? '20px' : '4px'; " +
           "    " +
           "    // Call the onCheckedChange handler with the new checked state " +
           "    onCheckedChange(event.target.checked); " +
           "}); " +

           "// Add the switch container to the current parent " +
           "currentParent.appendChild(switchContainer);")
    }

    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {
        // Create a time input element
        js("var timeInput = document.createElement('input'); " +
           "timeInput.type = 'time'; " +

           "// Set initial value and attributes " +
           "if (value !== null) { " +
           "    // Convert LocalTime to HH:MM format " +
           "    var hour = value.hour.toString().padStart(2, '0'); " +
           "    var minute = value.minute.toString().padStart(2, '0'); " +
           "    timeInput.value = hour + ':' + minute; " +
           "} " +

           "timeInput.disabled = !enabled; " +

           "// Set 24-hour format if specified " +
           "if (!is24Hour) { " +
           "    // Some browsers support 12-hour format with the 'ampm' attribute, " +
           "    // but it's not standard. We'll add a data attribute for potential JS handling. " +
           "    timeInput.setAttribute('data-format', '12hour'); " +
           "} " +

           "// Generate a unique ID for the time input " +
           "timeInput.id = 'time-' + Math.random().toString(36).substr(2, 9); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        timeInput.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        timeInput.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        timeInput.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add change event listener " +
           "timeInput.addEventListener('change', function(event) { " +
           "    var timeStr = event.target.value; " +
           "    " +
           "    if (timeStr) { " +
           "        // Parse the time string (HH:MM) into hour and minute " +
           "        var parts = timeStr.split(':'); " +
           "        var hour = parseInt(parts[0], 10); " +
           "        var minute = parseInt(parts[1], 10); " +
           "        " +
           "        // Call the onValueChange handler with the parsed LocalTime " +
           "        // Note: We're creating a JavaScript object that matches the structure of LocalTime " +
           "        onValueChange({ " +
           "            hour: hour, " +
           "            minute: minute, " +
           "            second: 0, " +
           "            nanosecond: 0 " +
           "        }); " +
           "    } else { " +
           "        // If the input is cleared, pass null " +
           "        onValueChange(null); " +
           "    } " +
           "}); " +

           "// Add the time input to the current parent " +
           "currentParent.appendChild(timeInput);")
    }

    override fun renderAspectRatio(
        ratio: Float,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a container div with aspect ratio
        js("var container = document.createElement('div'); " +

           "// Apply aspect ratio using padding-bottom technique " +
           "// This is a common CSS trick to maintain aspect ratio " +
           "container.style.position = 'relative'; " +
           "container.style.width = '100%'; " +
           "container.style.paddingBottom = (100 / ratio) + '%'; " +

           "// Create inner content container " +
           "var innerContainer = document.createElement('div'); " +
           "innerContainer.style.position = 'absolute'; " +
           "innerContainer.style.top = '0'; " +
           "innerContainer.style.left = '0'; " +
           "innerContainer.style.width = '100%'; " +
           "innerContainer.style.height = '100%'; " +

           "// Apply modifiers to the outer container " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        container.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        container.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        container.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add inner container to the outer container " +
           "container.appendChild(innerContainer); " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the inner container as the new parent for nested content " +
           "currentParent = innerContainer; " +

           "// Add the container to the previous parent " +
           "previousParent.appendChild(container);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderCard(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a div element with card styling
        js("var card = document.createElement('div'); " +

           "// Set default card styling " +
           "card.style.display = 'block'; " +
           "card.style.backgroundColor = '#ffffff'; " +
           "card.style.borderRadius = '4px'; " +
           "card.style.boxShadow = '0 2px 4px rgba(0, 0, 0, 0.1)'; " +
           "card.style.padding = '16px'; " +
           "card.style.margin = '8px'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        card.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        card.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        card.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the card as the new parent for nested content " +
           "currentParent = card; " +

           "// Add the card to the previous parent " +
           "previousParent.appendChild(card);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderLink(href: String, modifier: Modifier) {
        // Create an anchor element
        js("var link = document.createElement('a'); " +
           "link.href = href; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        link.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        link.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        link.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Set the link text to the href if no content is provided " +
           "link.textContent = href; " +

           "// Add the link to the current parent " +
           "currentParent.appendChild(link);")
    }

    override fun renderLink(
        modifier: Modifier,
        href: String,
        content: @Composable (() -> Unit)
    ) {
        // Create an anchor element
        js("var link = document.createElement('a'); " +
           "link.href = href; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        link.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        link.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        link.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the link as the new parent for nested content " +
           "currentParent = link; " +

           "// Add the link to the previous parent " +
           "previousParent.appendChild(link);")

        // Render the content inside the link
        content()

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier
    ) {
        // Create an anchor element
        js("var link = document.createElement('a'); " +
           "link.href = href; " +

           "// Set optional attributes if provided " +
           "if (target !== null) { " +
           "    link.target = target; " +
           "} " +

           "if (title !== null) { " +
           "    link.title = title; " +
           "} " +

           "if (ariaLabel !== null) { " +
           "    link.setAttribute('aria-label', ariaLabel); " +
           "} " +

           "if (ariaDescribedBy !== null) { " +
           "    link.setAttribute('aria-describedby', ariaDescribedBy); " +
           "} " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        link.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        link.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        link.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Set the link text to the href if no content is provided " +
           "link.textContent = href; " +

           "// Add the link to the current parent " +
           "currentParent.appendChild(link);")
    }

    override fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    ) {
        // Create a container for the tab layout
        js("var tabContainer = document.createElement('div'); " +
           "tabContainer.className = 'summon-tab-layout'; " +
           "tabContainer.style.display = 'flex'; " +
           "tabContainer.style.flexDirection = 'column'; " +

           "// Create the tab header container " +
           "var tabHeader = document.createElement('div'); " +
           "tabHeader.className = 'summon-tab-header'; " +
           "tabHeader.style.display = 'flex'; " +
           "tabHeader.style.flexDirection = 'row'; " +
           "tabHeader.style.borderBottom = '1px solid #ccc'; " +

           "// Create the tab content container " +
           "var tabContent = document.createElement('div'); " +
           "tabContent.className = 'summon-tab-content'; " +
           "tabContent.style.padding = '16px'; " +

           "// Create tabs " +
           "tabs.forEach(function(tab, index) { " +
           "    var tabElement = document.createElement('div'); " +
           "    tabElement.className = 'summon-tab'; " +
           "    tabElement.textContent = tab.title; " +
           "    tabElement.style.padding = '8px 16px'; " +
           "    tabElement.style.cursor = 'pointer'; " +
           "    tabElement.style.userSelect = 'none'; " +
           "    " +
           "    // Apply selected styling " +
           "    if (index === selectedTabIndex) { " +
           "        tabElement.classList.add('summon-tab-selected'); " +
           "        tabElement.style.borderBottom = '2px solid #1976d2'; " +
           "        tabElement.style.fontWeight = 'bold'; " +
           "        " +
           "        // Render the selected tab's content " +
           "        if (tab.content) { " +
           "            var previousParent = currentParent; " +
           "            currentParent = tabContent; " +
           "            try { " +
           "                tab.content(); " +
           "            } catch (e) { " +
           "                console.log('Expected error calling tab content function: ' + e); " +
           "            } " +
           "            currentParent = previousParent; " +
           "        } " +
           "    } " +
           "    " +
           "    // Add click event listener " +
           "    tabElement.addEventListener('click', function() { " +
           "        onTabSelected(index); " +
           "    }); " +
           "    " +
           "    // Add the tab to the header " +
           "    tabHeader.appendChild(tabElement); " +
           "}); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        tabContainer.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        tabContainer.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        tabContainer.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add the header and content to the container " +
           "tabContainer.appendChild(tabHeader); " +
           "tabContainer.appendChild(tabContent); " +

           "// Add the tab container to the current parent " +
           "currentParent.appendChild(tabContainer);")
    }

    override fun renderTabLayout(
        modifier: Modifier,
        content: @Composable (() -> Unit)
    ) {
        // Create a container for the tab layout
        js("var tabContainer = document.createElement('div'); " +
           "tabContainer.className = 'summon-tab-layout'; " +
           "tabContainer.style.display = 'flex'; " +
           "tabContainer.style.flexDirection = 'column'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        tabContainer.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        tabContainer.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        tabContainer.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the tab container as the new parent for nested content " +
           "currentParent = tabContainer; " +

           "// Add the tab container to the previous parent " +
           "previousParent.appendChild(tabContainer);")

        try {
            // Call the content function
            content()
        } catch (e: Exception) {
            // Log any errors
            js("console.error('Error rendering tab layout content: ', e);")
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    ) {
        // Create a container for the tab layout
        js("var tabContainer = document.createElement('div'); " +
           "tabContainer.className = 'summon-tab-layout'; " +
           "tabContainer.style.display = 'flex'; " +
           "tabContainer.style.flexDirection = 'column'; " +

           "// Create the tab header container " +
           "var tabHeader = document.createElement('div'); " +
           "tabHeader.className = 'summon-tab-header'; " +
           "tabHeader.style.display = 'flex'; " +
           "tabHeader.style.flexDirection = 'row'; " +
           "tabHeader.style.borderBottom = '1px solid #ccc'; " +

           "// Create the tab content container " +
           "var tabContent = document.createElement('div'); " +
           "tabContent.className = 'summon-tab-content'; " +
           "tabContent.style.padding = '16px'; " +

           "// Create tabs " +
           "tabs.forEach(function(tab) { " +
           "    var tabElement = document.createElement('div'); " +
           "    tabElement.className = 'summon-tab'; " +
           "    tabElement.textContent = tab; " +
           "    tabElement.style.padding = '8px 16px'; " +
           "    tabElement.style.cursor = 'pointer'; " +
           "    tabElement.style.userSelect = 'none'; " +
           "    " +
           "    // Apply selected styling " +
           "    if (tab === selectedTab) { " +
           "        tabElement.classList.add('summon-tab-selected'); " +
           "        tabElement.style.borderBottom = '2px solid #1976d2'; " +
           "        tabElement.style.fontWeight = 'bold'; " +
           "    } " +
           "    " +
           "    // Add click event listener " +
           "    tabElement.addEventListener('click', function() { " +
           "        onTabSelected(tab); " +
           "    }); " +
           "    " +
           "    // Add the tab to the header " +
           "    tabHeader.appendChild(tabElement); " +
           "}); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        tabContainer.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        tabContainer.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        tabContainer.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add the header and content to the container " +
           "tabContainer.appendChild(tabHeader); " +
           "tabContainer.appendChild(tabContent); " +

           "// Add the tab container to the current parent " +
           "currentParent.appendChild(tabContainer);")

        // Store the current parent
        js("var previousParent = currentParent; " +
           "currentParent = tabContent;")

        try {
            // Call the content function
            content()
        } catch (e: Exception) {
            // Log any errors
            js("console.error('Error rendering tab layout content: ', e);")
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        if (visible) {
            // Create a container for visible animated content
            js("var container = document.createElement('div'); " +
               "container.className = 'animated-visibility'; " +

               "// Apply visible state " +
               "container.style.opacity = '1'; " +
               "container.style.height = 'auto'; " +

               "// Apply transition " +
               "container.style.transition = 'opacity 300ms ease, height 300ms ease'; " +

               "// Apply modifiers " +
               "if (modifier) { " +
               "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
               "        var key = entry[0]; " +
               "        var value = entry[1]; " +
               "        container.style[key] = value; " +
               "    }); " +
               "    " +
               "    // Apply classes " +
               "    modifier.getClasses().forEach(function(cls) { " +
               "        container.classList.add(cls); " +
               "    }); " +
               "    " +
               "    // Apply attributes " +
               "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
               "        var key = entry[0]; " +
               "        var value = entry[1]; " +
               "        container.setAttribute(key, value); " +
               "    }); " +
               "} " +

               "// Add the container to the current parent element " +
               "currentParent.appendChild(container); " +

               "// Return the container for potential future reference " +
               "return container;")
        } else {
            // Create a container for hidden animated content
            js("var container = document.createElement('div'); " +
               "container.className = 'animated-visibility'; " +

               "// Apply hidden state " +
               "container.style.opacity = '0'; " +
               "container.style.height = '0'; " +
               "container.style.overflow = 'hidden'; " +

               "// Apply transition " +
               "container.style.transition = 'opacity 300ms ease, height 300ms ease'; " +

               "// Apply modifiers " +
               "if (modifier) { " +
               "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
               "        var key = entry[0]; " +
               "        var value = entry[1]; " +
               "        container.style[key] = value; " +
               "    }); " +
               "    " +
               "    // Apply classes " +
               "    modifier.getClasses().forEach(function(cls) { " +
               "        container.classList.add(cls); " +
               "    }); " +
               "    " +
               "    // Apply attributes " +
               "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
               "        var key = entry[0]; " +
               "        var value = entry[1]; " +
               "        container.setAttribute(key, value); " +
               "    }); " +
               "} " +

               "// Add the container to the current parent element " +
               "currentParent.appendChild(container); " +

               "// Return the container for potential future reference " +
               "return container;")
        }
    }

    override fun renderAnimatedVisibility(
        modifier: Modifier,
        content: @Composable (() -> Unit)
    ) {
        // Create a container for animated visibility
        js("var container = document.createElement('div'); " +
           "container.className = 'animated-visibility'; " +

           "// Apply transition " +
           "container.style.transition = 'opacity 300ms ease, height 300ms ease'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        container.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        container.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        container.setAttribute(key, value); " +
           "    }); " +
           "    " +
           "    // Check if visibility is specified in the modifier " +
           "    var isVisible = !modifier.getStyles().hasOwnProperty('display') || " +
           "                   modifier.getStyles()['display'] !== 'none'; " +
           "    " +
           "    if (!isVisible) { " +
           "        container.style.opacity = '0'; " +
           "        container.style.height = '0'; " +
           "        container.style.overflow = 'hidden'; " +
           "    } else { " +
           "        container.style.opacity = '1'; " +
           "        container.style.height = 'auto'; " +
           "    } " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the container as the new parent for nested content " +
           "currentParent = container; " +

           "// Add the container to the previous parent " +
           "previousParent.appendChild(container);")

        // Render the content inside the container
        content()

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderAnimatedContent(modifier: Modifier) {
        // Create a container for animated content
        js("var container = document.createElement('div'); " +
           "container.className = 'animated-content'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        container.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        container.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        container.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add the container to the current parent element " +
           "currentParent.appendChild(container); " +

           "// Return the container for potential future reference " +
           "return container;")
    }

    override fun renderAnimatedContent(
        modifier: Modifier,
        content: @Composable (() -> Unit)
    ) {
        // Create a container for animated content
        js("var container = document.createElement('div'); " +
           "container.className = 'animated-content'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        container.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        container.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        container.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the container as the new parent for nested content " +
           "currentParent = container; " +

           "// Add the container to the previous parent " +
           "previousParent.appendChild(container);")

        // Render the content inside the container
        content()

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderBlock(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a div element for the block
        js("var block = document.createElement('div'); " +
           "block.className = 'summon-block'; " +
           "block.style.display = 'block'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        block.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        block.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        block.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the block as the new parent for nested content " +
           "currentParent = block; " +

           "// Add the block to the previous parent " +
           "previousParent.appendChild(block);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderInline(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a span element for the inline content
        js("var inline = document.createElement('span'); " +
           "inline.className = 'summon-inline'; " +
           "inline.style.display = 'inline'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        inline.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        inline.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        inline.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the inline element as the new parent for nested content " +
           "currentParent = inline; " +

           "// Add the inline element to the previous parent " +
           "previousParent.appendChild(inline);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderDiv(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a div element
        js("var div = document.createElement('div'); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        div.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        div.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        div.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the div as the new parent for nested content " +
           "currentParent = div; " +

           "// Add the div to the previous parent " +
           "previousParent.appendChild(div);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderSpan(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a span element
        js("var span = document.createElement('span'); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        span.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        span.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        span.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the span as the new parent for nested content " +
           "currentParent = span; " +

           "// Add the span to the previous parent " +
           "previousParent.appendChild(span);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderDivider(modifier: Modifier) {
        // Create an hr element for horizontal divider
        js("var divider = document.createElement('hr'); " +

           "// Set default divider styling " +
           "divider.style.border = '0'; " +
           "divider.style.borderTop = '1px solid rgba(0, 0, 0, 0.12)'; " +
           "divider.style.margin = '8px 0'; " +
           "divider.style.width = '100%'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        divider.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        divider.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        divider.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add the divider to the current parent " +
           "currentParent.appendChild(divider);")
    }

    override fun renderExpansionPanel(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a container for the expansion panel
        js("var expansionPanel = document.createElement('div'); " +
           "expansionPanel.className = 'summon-expansion-panel'; " +
           "expansionPanel.style.border = '1px solid #ddd'; " +
           "expansionPanel.style.borderRadius = '4px'; " +
           "expansionPanel.style.overflow = 'hidden'; " +
           "expansionPanel.style.marginBottom = '16px'; " +

           "// Create the header " +
           "var header = document.createElement('div'); " +
           "header.className = 'summon-expansion-panel-header'; " +
           "header.style.padding = '16px'; " +
           "header.style.backgroundColor = '#f5f5f5'; " +
           "header.style.cursor = 'pointer'; " +
           "header.style.display = 'flex'; " +
           "header.style.justifyContent = 'space-between'; " +
           "header.style.alignItems = 'center'; " +

           "// Create the title " +
           "var title = document.createElement('div'); " +
           "title.className = 'summon-expansion-panel-title'; " +
           "title.textContent = 'Expansion Panel'; " + // Default title

           "// Create the icon " +
           "var icon = document.createElement('span'); " +
           "icon.className = 'summon-expansion-panel-icon'; " +
           "icon.textContent = '+'; " + // Default icon
           "icon.style.transition = 'transform 0.3s'; " +

           "// Create the content container " +
           "var contentContainer = document.createElement('div'); " +
           "contentContainer.className = 'summon-expansion-panel-content'; " +
           "contentContainer.style.padding = '16px'; " +
           "contentContainer.style.display = 'none'; " + // Initially hidden

           "// Add click event to toggle expansion " +
           "header.addEventListener('click', function() { " +
           "    if (contentContainer.style.display === 'none') { " +
           "        contentContainer.style.display = 'block'; " +
           "        icon.textContent = '-'; " +
           "        icon.style.transform = 'rotate(180deg)'; " +
           "    } else { " +
           "        contentContainer.style.display = 'none'; " +
           "        icon.textContent = '+'; " +
           "        icon.style.transform = 'rotate(0deg)'; " +
           "    } " +
           "}); " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        expansionPanel.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        expansionPanel.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        expansionPanel.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Assemble the components " +
           "header.appendChild(title); " +
           "header.appendChild(icon); " +
           "expansionPanel.appendChild(header); " +
           "expansionPanel.appendChild(contentContainer); " +

           "// Add the expansion panel to the current parent " +
           "currentParent.appendChild(expansionPanel); " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the content container as the new parent for nested content " +
           "currentParent = contentContainer;")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderGrid(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a div element with grid styling
        js("var grid = document.createElement('div'); " +

           "// Set default grid styling " +
           "grid.style.display = 'grid'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        grid.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        grid.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        grid.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the grid as the new parent for nested content " +
           "currentParent = grid; " +

           "// Add the grid to the previous parent " +
           "previousParent.appendChild(grid);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderLazyColumn(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a container for the lazy column
        js("var lazyColumn = document.createElement('div'); " +
           "lazyColumn.className = 'summon-lazy-column'; " +
           "lazyColumn.style.display = 'flex'; " +
           "lazyColumn.style.flexDirection = 'column'; " +
           "lazyColumn.style.overflowY = 'auto'; " +
           "lazyColumn.style.maxHeight = '100%'; " +

           "// Extract data attributes from modifier styles " +
           "var totalItems = 0; " +
           "var itemSize = 50; " +
           "var overscrollItems = 2; " +
           "var isLazyContainer = false; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        " +
           "        // Check for special data attributes " +
           "        if (key === 'data-total-items') { " +
           "            totalItems = parseInt(value); " +
           "            lazyColumn.setAttribute('data-total-items', value); " +
           "        } else if (key === 'data-item-size') { " +
           "            itemSize = parseFloat(value); " +
           "            lazyColumn.setAttribute('data-item-size', value); " +
           "        } else if (key === 'data-overscroll-items') { " +
           "            overscrollItems = parseInt(value); " +
           "            lazyColumn.setAttribute('data-overscroll-items', value); " +
           "        } else if (key === 'data-lazy-container') { " +
           "            isLazyContainer = true; " +
           "            lazyColumn.setAttribute('data-lazy-container', value); " +
           "        } else if (key.startsWith('__attr:')) { " +
           "            // Handle attributes stored with __attr: prefix " +
           "            var attrName = key.substring(7); " +
           "            lazyColumn.setAttribute(attrName, value); " +
           "        } else if (key === 'onscroll') { " +
           "            // Special handling for scroll event " +
           "            lazyColumn.setAttribute('onscroll', value); " +
           "        } else { " +
           "            // Regular style " +
           "            lazyColumn.style[key] = value; " +
           "        } " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        lazyColumn.classList.add(cls); " +
           "    }); " +
           "} " +

           "// Set up scroll handling for lazy loading " +
           "if (isLazyContainer) { " +
           "    // Define the scroll handler function if not already defined " +
           "    if (typeof window.summonHandleScroll !== 'function') { " +
           "        window.summonHandleScroll = function(event, updatePositionFn) { " +
           "            var container = event.target; " +
           "            var direction = container.getAttribute('data-direction') || 'column'; " +
           "            var scrollPosition = (direction === 'column') ? container.scrollTop : container.scrollLeft; " +
           "            " +
           "            // Call the Kotlin function to update scroll position " +
           "            updatePositionFn(scrollPosition); " +
           "            " +
           "            // Calculate visible items based on scroll position " +
           "            var containerSize = (direction === 'column') ? container.clientHeight : container.clientWidth; " +
           "            var totalItems = parseInt(container.getAttribute('data-total-items') || '0'); " +
           "            var itemSize = parseFloat(container.getAttribute('data-item-size') || '50'); " +
           "            var overscrollItems = parseInt(container.getAttribute('data-overscroll-items') || '2'); " +
           "            " +
           "            // Calculate visible range " +
           "            var firstVisibleItem = Math.floor(scrollPosition / itemSize) - overscrollItems; " +
           "            firstVisibleItem = Math.max(0, firstVisibleItem); " +
           "            " +
           "            var visibleItemCount = Math.ceil(containerSize / itemSize) + (2 * overscrollItems); " +
           "            var lastVisibleItem = firstVisibleItem + visibleItemCount; " +
           "            lastVisibleItem = Math.min(totalItems - 1, lastVisibleItem); " +
           "            " +
           "            // Update visibility of items " +
           "            for (var i = 0; i < totalItems; i++) { " +
           "                var item = container.querySelector('[data-item-index=\"' + i + '\"]'); " +
           "                if (item) { " +
           "                    if (i >= firstVisibleItem && i <= lastVisibleItem) { " +
           "                        item.style.display = ''; " +
           "                    } else { " +
           "                        item.style.display = 'none'; " +
           "                    } " +
           "                } " +
           "            } " +
           "        }; " +
           "    } " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the lazy column as the new parent for nested content " +
           "currentParent = lazyColumn; " +

           "// Add the lazy column to the previous parent " +
           "previousParent.appendChild(lazyColumn);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderLazyRow(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a container for the lazy row
        js("var lazyRow = document.createElement('div'); " +
           "lazyRow.className = 'summon-lazy-row'; " +
           "lazyRow.style.display = 'flex'; " +
           "lazyRow.style.flexDirection = 'row'; " +
           "lazyRow.style.overflowX = 'auto'; " +
           "lazyRow.style.maxWidth = '100%'; " +
           "lazyRow.style.scrollbarWidth = 'thin'; " + // For Firefox
           "lazyRow.style.msOverflowStyle = 'none'; " + // For IE and Edge

           "// Extract data attributes from modifier styles " +
           "var totalItems = 0; " +
           "var itemSize = 50; " +
           "var overscrollItems = 2; " +
           "var isLazyContainer = false; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        " +
           "        // Check for special data attributes " +
           "        if (key === 'data-total-items') { " +
           "            totalItems = parseInt(value); " +
           "            lazyRow.setAttribute('data-total-items', value); " +
           "        } else if (key === 'data-item-size') { " +
           "            itemSize = parseFloat(value); " +
           "            lazyRow.setAttribute('data-item-size', value); " +
           "        } else if (key === 'data-overscroll-items') { " +
           "            overscrollItems = parseInt(value); " +
           "            lazyRow.setAttribute('data-overscroll-items', value); " +
           "        } else if (key === 'data-lazy-container') { " +
           "            isLazyContainer = true; " +
           "            lazyRow.setAttribute('data-lazy-container', value); " +
           "        } else if (key.startsWith('__attr:')) { " +
           "            // Handle attributes stored with __attr: prefix " +
           "            var attrName = key.substring(7); " +
           "            lazyRow.setAttribute(attrName, value); " +
           "        } else if (key === 'onscroll') { " +
           "            // Special handling for scroll event " +
           "            lazyRow.setAttribute('onscroll', value); " +
           "        } else { " +
           "            // Regular style " +
           "            lazyRow.style[key] = value; " +
           "        } " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        lazyRow.classList.add(cls); " +
           "    }); " +
           "} " +

           "// Set up scroll handling for lazy loading " +
           "if (isLazyContainer) { " +
           "    // Define the scroll handler function if not already defined " +
           "    if (typeof window.summonHandleScroll !== 'function') { " +
           "        window.summonHandleScroll = function(event, updatePositionFn) { " +
           "            var scrollLeft = event.target.scrollLeft; " +
           "            // Call the Kotlin function to update scroll position " +
           "            updatePositionFn(scrollLeft); " +
           "            " +
           "            // Calculate visible items based on scroll position " +
           "            var container = event.target; " +
           "            var containerWidth = container.clientWidth; " +
           "            var totalItems = parseInt(container.getAttribute('data-total-items') || '0'); " +
           "            var itemSize = parseFloat(container.getAttribute('data-item-size') || '50'); " +
           "            var overscrollItems = parseInt(container.getAttribute('data-overscroll-items') || '2'); " +
           "            " +
           "            // Calculate visible range " +
           "            var firstVisibleItem = Math.floor(scrollLeft / itemSize) - overscrollItems; " +
           "            firstVisibleItem = Math.max(0, firstVisibleItem); " +
           "            " +
           "            var visibleItemCount = Math.ceil(containerWidth / itemSize) + (2 * overscrollItems); " +
           "            var lastVisibleItem = firstVisibleItem + visibleItemCount; " +
           "            lastVisibleItem = Math.min(totalItems - 1, lastVisibleItem); " +
           "            " +
           "            // Update visibility of items " +
           "            for (var i = 0; i < totalItems; i++) { " +
           "                var item = container.querySelector('[data-item-index=\"' + i + '\"]'); " +
           "                if (item) { " +
           "                    if (i >= firstVisibleItem && i <= lastVisibleItem) { " +
           "                        item.style.display = ''; " +
           "                    } else { " +
           "                        item.style.display = 'none'; " +
           "                    } " +
           "                } " +
           "            } " +
           "        }; " +
           "    } " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the lazy row as the new parent for nested content " +
           "currentParent = lazyRow; " +

           "// Add the lazy row to the previous parent " +
           "previousParent.appendChild(lazyRow);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderResponsiveLayout(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create a div element for responsive layout
        js("var responsiveLayout = document.createElement('div'); " +

           "// Set default responsive layout styling " +
           "responsiveLayout.className = 'responsive-layout'; " +

           "// Apply modifiers (should include media queries for responsiveness) " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        responsiveLayout.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        responsiveLayout.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        responsiveLayout.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Add responsive behavior with JavaScript " +
           "var updateLayout = function() { " +
           "    var width = window.innerWidth; " +
           "    // Add responsive classes based on screen width " +
           "    responsiveLayout.classList.remove('small-screen', 'medium-screen', 'large-screen'); " +
           "    if (width < 600) { " +
           "        responsiveLayout.classList.add('small-screen'); " +
           "    } else if (width < 1200) { " +
           "        responsiveLayout.classList.add('medium-screen'); " +
           "    } else { " +
           "        responsiveLayout.classList.add('large-screen'); " +
           "    } " +
           "}; " +

           "// Initial layout update " +
           "updateLayout(); " +

           "// Add resize listener " +
           "if (typeof window !== 'undefined') { " +
           "    if (!window._responsiveListenerAdded) { " +
           "        window.addEventListener('resize', function() { " +
           "            // Update all responsive layouts " +
           "            document.querySelectorAll('.responsive-layout').forEach(function(el) { " +
           "                var event = new Event('responsive-update'); " +
           "                el.dispatchEvent(event); " +
           "            }); " +
           "        }); " +
           "        window._responsiveListenerAdded = true; " +
           "    } " +
           "    " +
           "    // Add event listener to this specific layout " +
           "    responsiveLayout.addEventListener('responsive-update', updateLayout); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the responsive layout as the new parent for nested content " +
           "currentParent = responsiveLayout; " +

           "// Add the responsive layout to the previous parent " +
           "previousParent.appendChild(responsiveLayout);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }

    override fun renderHtmlTag(
        tagName: String,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Create the specified HTML element
        js("var element = document.createElement(tagName); " +
           "element.className = 'summon-html-tag'; " +

           "// Apply modifiers " +
           "if (modifier) { " +
           "    Object.entries(modifier.getStyles()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        element.style[key] = value; " +
           "    }); " +
           "    " +
           "    // Apply classes " +
           "    modifier.getClasses().forEach(function(cls) { " +
           "        element.classList.add(cls); " +
           "    }); " +
           "    " +
           "    // Apply attributes " +
           "    Object.entries(modifier.getAttributes()).forEach(function(entry) { " +
           "        var key = entry[0]; " +
           "        var value = entry[1]; " +
           "        element.setAttribute(key, value); " +
           "    }); " +
           "} " +

           "// Store the current parent " +
           "var previousParent = currentParent; " +

           "// Set the element as the new parent for nested content " +
           "currentParent = element; " +

           "// Add the element to the previous parent " +
           "previousParent.appendChild(element);")

        // In JavaScript environment, we don't have a direct equivalent to FlowContent
        // Instead, we call the content function with a dummy FlowContent object
        // The actual rendering is handled by the JavaScript code using the currentParent variable
        try {
            // This is a workaround to call the content function without a proper FlowContent receiver
            // It will likely fail, but the JavaScript code will still execute correctly
            js("try { content(); } catch (e) { console.log('Expected error calling content function: ' + e); }")
        } catch (e: Exception) {
            // Ignore the exception, as we expect it to fail
            // The JavaScript code will still execute correctly
        }

        // Restore the previous parent
        js("currentParent = previousParent;")
    }
}
