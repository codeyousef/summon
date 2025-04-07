@file:Suppress("UNCHECKED_CAST") // Keep suppression for now, might be needed elsewhere

package code.yousef.summon

import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.core.LocalDate
import code.yousef.summon.core.LocalTime
import code.yousef.summon.core.style.Color

// Remove old component imports (many are unused now)
// import animation.AnimatedContent
// import animation.AnimatedVisibility
// import components.display.*
// import components.feedback.*
// import components.input.*
// import components.layout.*
// import components.navigation.Link
// import components.navigation.TabLayout
// import core.Composable // Removed interface
// import modifier.Modifier // Replaced with correct import
// import routing.Router // Removed
// import routing.RouterContext // Likely unused now

import kotlin.ranges.ClosedFloatingPointRange
import code.yousef.summon.components.input.TextFieldType
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import components.feedback.AlertVariant

import kotlinx.html.*

// Re-add necessary placeholder type aliases (already defined in PlatformRenderer, but good for clarity here)
typealias AlertType = String
typealias File = Any
typealias LocalDate = Any
typealias LocalTime = Any
typealias TabItem = Any
typealias ClosedFloatingPointRange<T> = ClosedRange<T>

/**
 * JVM implementation of the PlatformRenderer interface.
 * Provides JVM-specific rendering for UI components using kotlinx.html.
 * Updated for annotation-based composition.
 */
class JvmPlatformRenderer : PlatformRenderer {

    // Assume the consumer is set before rendering methods are called
    // This is needed because kotlinx.html functions are extension functions on TagConsumer
    lateinit var consumer: TagConsumer<*>

    // --- Display Components ---

    /**
     * Renders text content within a span.
     */
    override fun renderText(value: String, modifier: Modifier) {
        consumer.span {
            style = modifier.toStyleString() // Apply modifier styles
            // TODO: Apply accessibility attributes from modifier if needed
            +value // Add the text content
        }
    }

    /**
     * Renders an image element.
     */
    override fun renderImage(src: String, alt: String, modifier: Modifier) {
        consumer.img {
            this.src = src
            this.alt = alt
            style = modifier.toStyleString()
            // TODO: Add width/height from modifier if specified
        }
    }

    /**
     * Renders an icon (placeholder, might need specific library integration).
     * Currently renders text, assuming icon name maps to some visual representation (e.g., font icon class).
     */
    override fun renderIcon(name: String, modifier: Modifier) {
        // Placeholder: Render as span with a class, assuming CSS handles the icon display
        // Or potentially use an <i> tag if using FontAwesome etc.
        consumer.span("icon-$name") { // Example class name
            style = modifier.toStyleString()
            // Potentially add ARIA attributes
            attributes["role"] = "img"
            attributes["aria-label"] = name
            // Content could be empty or specific ligature/character depending on icon system
        }
    }

    // --- Input Components ---

    /**
     * Renders a button element.
     */
    override fun renderButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {
        // onClick lambda cannot be directly used in static HTML.
        // Need JS interop or form submission for actions.
        // Add data attribute for potential JS hook.
        consumer.button {
            style = modifier.toStyleString()
            this.disabled = !enabled
            attributes["data-summon-click"] = "true" // JS hook
            // Content added by composable function
        }
    }

     /**
     * Renders a text input field.
     */
    override fun renderTextField(
        value: String, 
        onValueChange: (String) -> Unit, 
        enabled: Boolean,
        readOnly: Boolean,
        type: TextFieldType,
        placeholder: String?,
        modifier: Modifier
    ) {
        // onValueChange cannot be used directly. Needs JS.
        val inputId = "textfield-${value.hashCode()}-${type.name.hashCode()}" // Generate a semi-stable ID
        consumer.input {
            id = inputId
            this.type = when(type) {
                    TextFieldType.Password -> InputType.password
                    TextFieldType.Email -> InputType.email
                    TextFieldType.Number -> InputType.number
                    TextFieldType.Tel -> InputType.tel
                    TextFieldType.Url -> InputType.url
                    TextFieldType.Search -> InputType.search
                    TextFieldType.Date -> InputType.date
                    TextFieldType.Time -> InputType.time
                    else -> InputType.text
                }
            this.value = value
            this.disabled = !enabled
            if (readOnly) {
                attributes["readonly"] = "readonly"
            }
            placeholder?.let { this.placeholder = it }
            style = modifier.toStyleString()
            attributes["data-summon-input"] = "true" // JS hook
        }
    }

    /**
     * Renders a text area input field.
     */
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
        val areaId = "textarea-${value.hashCode()}"
        consumer.textArea {
            id = areaId
            this.disabled = !enabled
            if (readOnly) {
                attributes["readonly"] = "readonly"
            }
            rows?.let { this.rows = it.toString() }
            maxLength?.let { this.maxLength = it.toString() }
            placeholder?.let { this.placeholder = it }
            style = modifier.toStyleString()
            attributes["data-summon-textarea"] = "true" // JS hook
            +value
        }
    }

    /**
     * Renders a checkbox input.
     */
    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        val checkId = "checkbox-${checked.hashCode()}"
        consumer.input {
            id = checkId
                type = InputType.checkBox
            this.checked = checked
            this.disabled = !enabled
            style = modifier.toStyleString()
            attributes["data-summon-checkbox"] = "true" // JS hook
        }
    }

    /**
     * Renders a radio button input.
     */
    override fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        val radioId = "radio-${selected.hashCode()}"
        consumer.input {
                id = radioId
                type = InputType.radio
            this.checked = selected
            this.disabled = !enabled
            style = modifier.toStyleString()
            attributes["data-summon-radio"] = "true" // JS hook
        }
    }

    /**
     * Renders a select dropdown.
     */
    override fun <T> renderSelect(
        value: T?,
        onValueChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        enabled: Boolean,
        modifier: Modifier
    ) {
        val selectId = "select-${options.hashCode()}"
        consumer.select {
                id = selectId
            this.disabled = !enabled
            style = modifier.toStyleString()
            attributes["data-summon-select"] = "true" // JS hook
            
            options.forEach { option ->
                    option {
                    this.value = option.value?.toString() ?: ""
                    this.disabled = option.disabled
                    this.selected = (option.value == value)
                        +option.label
                    }
                }
        }
    }

    /**
     * Renders a switch toggle (visual representation might need CSS/JS).
     * Renders as a checkbox for basic functionality.
     */
    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Render as a styled checkbox for now
        val switchId = "switch-${checked.hashCode()}"
        consumer.label("switch-label") { // Add class for easier styling
            style = modifier.toStyleString() // Apply modifier to the label/container
            input(InputType.checkBox, classes = "switch-input") {
                id = switchId
                this.checked = checked
                this.disabled = !enabled
                attributes["data-summon-switch"] = "true" // JS hook
                attributes["role"] = "switch"
                attributes["aria-checked"] = checked.toString()
            }
            span("switch-slider") { /* For CSS styling */ }
        }
    }

    /**
     * Renders a file upload input.
     */
    override fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        val fileId = "file-${modifier.hashCode()}"
        consumer.input { 
            id = fileId
            type = InputType.file
            this.multiple = multiple
            this.disabled = !enabled
            accept?.let { this.accept = it }
            capture?.let { attributes["capture"] = it }
            style = modifier.toStyleString()
            attributes["data-summon-file"] = "true" // JS hook
        }
        
        // Return a function that can be used to trigger a click on the file input
        return { /* In JVM environment, this is a no-op */ }
    }

    /**
     * Renders a range slider input.
     */
    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // HTML range input typically supports a single value, not a range directly.
        // Rendering a single value slider for now. Range might need custom elements or two inputs.
        val sliderId = "slider-${value.hashCode()}"
        consumer.input { 
            id = sliderId
            type = InputType.range
            // Assuming we use the start of the range for the single value
            this.value = value.start.toString()
            this.min = valueRange.start.toString()
            this.max = valueRange.endInclusive.toString()
            if (steps > 0) {
                val stepValue = (valueRange.endInclusive - valueRange.start) / steps
                this.step = stepValue.toString()
            }
            this.disabled = !enabled
            style = modifier.toStyleString()
            attributes["data-summon-slider"] = "true" // JS hook
        }
    }

    /**
     * Renders a form container
     */
    override fun renderForm(onSubmit: () -> Unit, modifier: Modifier) {
        consumer.form {
            action = "javascript:void(0);" // Prevent default submission
            attributes["data-summon-form"] = "true" // JS hook for onSubmit
            style = modifier.toStyleString()
        }
    }

    /**
     * Renders a date picker input.
     */
    override fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        val dateId = "date-${modifier.hashCode()}"
        consumer.input { 
            id = dateId
            type = InputType.date
            // Assuming LocalDate can be formatted to YYYY-MM-DD string
            this.value = value?.toString() ?: "" // Format appropriately
            this.disabled = !enabled
            style = modifier.toStyleString()
            attributes["data-summon-date"] = "true" // JS hook
        }
    }

    /**
     * Renders a time picker input.
     */
    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        val timeId = "time-${modifier.hashCode()}"
        consumer.input { 
            id = timeId
            type = InputType.time
            // Assuming LocalTime can be formatted to HH:MM string
            this.value = value?.toString() ?: "" // Format appropriately
            this.disabled = !enabled
            style = modifier.toStyleString()
            attributes["data-summon-time"] = "true" // JS hook
        }
    }

    // --- Layout Components ---
    // These now just render the opening tag structure. Content is added by composable functions.

    /**
     * Renders the start of a Row layout (div with flex-direction: row).
     */
    override fun renderRow(modifier: Modifier) {
        consumer.div {
            // Combine base styles with modifier styles
            val baseStyles = mapOf("display" to "flex", "flex-direction" to "row")
            style = (baseStyles + modifier.styles).entries.joinToString(";") { (k, v) -> "$k:$v" }
            // Children are rendered by the @Composable function calling this
        }
    }

    /**
     * Renders the start of a Column layout (div with flex-direction: column).
     */
    override fun renderColumn(modifier: Modifier) {
        consumer.div {
            val baseStyles = mapOf("display" to "flex", "flex-direction" to "column")
            style = (baseStyles + modifier.styles).entries.joinToString(";") { (k, v) -> "$k:$v" }
        }
    }

    /**
     * Renders a Spacer element (div with height or width).
     */
    override fun renderSpacer(modifier: Modifier) {
        consumer.div {
            style = modifier.toStyleString()
        }
    }

    /**
     * Renders the start of a Box layout (basic div).
     */
    override fun renderBox(modifier: Modifier) {
        consumer.div {
            style = modifier.toStyleString()
        }
    }

    /**
     * Renders the start of a Card layout (div with card-like styling).
     */
    override fun renderCard(modifier: Modifier) {
        consumer.div("card") { // Add a class for easier styling
            // Combine base card styles (example) with modifier styles
            val baseStyles = mapOf(
                "border" to "1px solid #ccc",
                "border-radius" to "8px",
                "padding" to "16px",
                "box-shadow" to "0 2px 4px rgba(0,0,0,0.1)"
            )
            style = (baseStyles + modifier.styles).entries.joinToString(";") { (k, v) -> "$k:$v" }
        }
    }

    /**
     * Renders a Divider element (hr).
     */
    override fun renderDivider(modifier: Modifier) {
        consumer.hr {
            style = modifier.toStyleString()
        }
    }

    /**
     * Renders the start of a Grid layout.
     */
    override fun renderGrid(modifier: Modifier) {
        consumer.div {
            val baseStyles = mapOf(
                "display" to "grid",
                "gap" to "16px" // Example gap
            )
            style = (baseStyles + modifier.styles).entries.joinToString(";") { (k, v) -> "$k:$v" }
        }
    }

    /**
     * Renders the start of an AspectRatio container.
     */
    override fun renderAspectRatio(modifier: Modifier) {
        consumer.div {
            style = modifier.toStyleString()
        }
    }

    /**
     * Renders the start of a ResponsiveLayout container (basic div).
     */
    override fun renderResponsiveLayout(modifier: Modifier) {
        consumer.div {
            style = modifier.toStyleString()
        }
    }

    /**
     * Renders the start of a LazyColumn container (basic div).
     */
    override fun renderLazyColumn(modifier: Modifier) {
        consumer.div("lazy-column") { // Add class for potential JS/CSS
            style = modifier.toStyleString()
        }
    }

    /**
     * Renders the start of a LazyRow container (basic div).
     */
    override fun renderLazyRow(modifier: Modifier) {
        consumer.div("lazy-row") {
            style = modifier.toStyleString()
        }
    }

    /**
     * Renders the start of an ExpansionPanel container.
     */
    override fun renderExpansionPanel(modifier: Modifier) {
        consumer.div("expansion-panel") {
            style = modifier.toStyleString()
        }
    }

    // --- Navigation Components ---

    /**
     * Renders the start of a Link (anchor tag).
     */
    override fun renderLink(href: String, modifier: Modifier) {
        consumer.a { 
            this.href = href
            style = modifier.toStyleString()
            // Content (e.g., text) is rendered by the @Composable function
        }
    }

    /**
     * Renders a TabLayout structure.
     */
    override fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    ) {
        consumer.div("tab-layout") {
            style = modifier.toStyleString()
            // Render Tab Headers (e.g., as buttons or links)
            div("tab-headers") {
                tabs.forEachIndexed { index, tab ->
                    button(classes = if (index == selectedTabIndex) "tab active" else "tab") {
                        attributes["data-tab-index"] = index.toString() // For JS hook
                        attributes["role"] = "tab"
                        attributes["aria-selected"] = (index == selectedTabIndex).toString()
                        +tab.title
                    }
                }
            }
            // Tab Content Area (content filled by selected tab's composable)
            div("tab-content") {
                // The actual content for the selected tab is rendered elsewhere,
                // triggered by the composition based on selectedTabIndex state.
            }
        }
    }

    // --- Feedback Components ---

    /**
     * Renders an Alert container.
     */
    override fun renderAlertContainer(variant: AlertVariant?, modifier: Modifier) {
        val variantClass = variant?.let { "alert-${it.name.lowercase()}" } ?: "alert-info"
        consumer.div("alert $variantClass") { 
            style = modifier.toStyleString()
            attributes["role"] = "alert"
        }
    }

    /**
     * Renders a badge container.
     */
    override fun renderBadge(modifier: Modifier) {
        consumer.span("badge") {
            style = modifier.toStyleString()
        }
    }

    /**
     * Renders a tooltip container.
     */
    override fun renderTooltipContainer(modifier: Modifier) {
        consumer.div("tooltip-container") {
            style = modifier.toStyleString()
        }
    }

    /**
     * Renders a progress indicator.
     */
    override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
        if (type == ProgressType.CIRCULAR) {
            // Circular progress not well-supported in HTML, use a container with text for now
            consumer.div("progress-circular") {
                style = modifier.toStyleString()
                +(value?.let { "${(it * 100).toInt()}%" } ?: "Loading...")
            }
            return
        }
        
        // Linear progress
        consumer.progress {
            value?.let { this.value = it.toString() }
            this.max = "1.0"
            style = modifier.toStyleString()
        }
    }

    /**
     * Renders an animated visibility container.
     */
    override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        if (visible) {
        consumer.div {
                style = modifier.toStyleString()
            }
        }
        // If not visible, render nothing
    }

    /**
     * Renders an animated content container.
     */
    override fun renderAnimatedContent(modifier: Modifier) {
        consumer.div {
            style = modifier.toStyleString()
        }
    }

    // --- Helper Methods ---

    /**
     * Converts a Modifier to a CSS style string.
     */
    private fun Modifier.toStyleString(): String {
        return this.styles.entries.joinToString(";") { (k, v) -> "$k:$v" }
    }
}

// Helper function to get styles from Modifier (moved inside or keep standalone?)
// fun Modifier.toStyleString(): String {
//    return this.styles.entries.joinToString(";") { (key, value) -> "$key:$value" }
// }
