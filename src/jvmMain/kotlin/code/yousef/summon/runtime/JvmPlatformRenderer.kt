package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

// Interface defined in PlatformRenderer.kt commonMain
// interface FormContent : FlowContent

// PlatformRenderer implementation
@OptIn(ExperimentalUuidApi::class)
actual open class PlatformRenderer {

    private val headElements = mutableListOf<String>()

    // Fix 1: Remove ThreadLocal, use instance variable.
    private var currentBuilder: FlowContent? = null

    // Apply Modifier based *only* on Modifier.toStyleString()
    // This extension function applies to any FlowOrMetaDataContent
    private fun FlowOrMetaDataContent.applyModifier(modifier: Modifier) {
        val styleString = modifier.toStyleString()
        if (styleString.isNotBlank()) {
            (this as? CommonAttributeGroupFacade)?.style = styleString
        }
    }

    // Helper to render composable content with FlowContent receiver
    private fun <T : FlowContent> T.renderContent(content: @Composable FlowContent.() -> Unit) {
        this.content() // Invoke as extension function
    }

    // Helper to render composable content without FlowContent receiver
    private fun renderContent(content: @Composable () -> Unit) {
        requireBuilder() // Ensure context exists before calling content
        content() // Execute lambda, assumes it uses implicit kotlinx.html context
    }

    // Get the current builder context
    private fun requireBuilder(): FlowContent {
        return currentBuilder ?: error("Rendering function called outside of renderComposableRoot scope")
    }

    actual open fun renderText(text: String, modifier: Modifier) {
        requireBuilder().span {
            applyModifier(modifier)
            +text
        }
    }

    actual open fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
        requireBuilder().label {
            applyModifier(modifier)
            if (forElement != null) {
                htmlFor = forElement // Changed from htmlFor
            }
        }
        +text
    }

    actual open fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    ) {
        requireBuilder().button {
            applyModifier(modifier)
            attributes["data-onclick-action"] = "true"
            comment(" JS Hook needed for onClick ")
            renderContent(content) // Use helper with receiver
        }
    }

    actual open fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier,
        type: String
    ) {
        requireBuilder().input(type = InputType.text) {
            applyModifier(modifier)
            this.value = value
            id = "input-${UUID.randomUUID()}"
            name = id // Use generated ID as name too
            attributes["data-onchange-action"] = "true"
            comment(" onValueChange handler needed (JS) ")
        }
    }

    actual open fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<code.yousef.summon.runtime.SelectOption<T>>,
        modifier: Modifier
    ) {
        requireBuilder().select {
            applyModifier(modifier)
            id = "select-${UUID.randomUUID()}"
            name = id
            attributes["data-onchange-action"] = "true"
            comment(" onSelectedChange handler needed (JS) ")

            options.forEach { optionData ->
                option {
                    this.value = optionData.value.toString()
                    if (optionData.value == selectedValue) this.selected = true
                    this.disabled = optionData.disabled
                    +optionData.label
                }
            }
        }
    }

    actual open fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        min: LocalDate?,
        max: LocalDate?,
        modifier: Modifier
    ) {
        requireBuilder().input(type = InputType.date) {
            applyModifier(modifier)
            if (value != null) this.value = value.toString()
            if (min != null) this.min = min.toString()
            if (max != null) this.max = max.toString()
            this.disabled = !enabled
            id = "date-${UUID.randomUUID()}"
            name = id
            attributes["data-onchange-action"] = "true"
            comment(" onValueChange handler needed (JS) ")
        }
    }

    actual open fun renderTextArea(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        rows: Int?,
        maxLength: Int?,
        placeholder: String?,
        modifier: Modifier
    ) {
        requireBuilder().textArea {
            applyModifier(modifier)
            if (rows != null) this.rows = rows.toString()
            if (maxLength != null) this.maxLength = maxLength.toString()
            if (placeholder != null) this.placeholder = placeholder
            this.disabled = !enabled
            this.readonly = readOnly
            id = "textarea-${UUID.randomUUID()}"
            name = id
            attributes["data-onchange-action"] = "true"
            comment(" onValueChange handler needed (JS) ")
            +value
        }
    }

    actual open fun addHeadElement(content: String) {
        headElements.add(content)
    }

    actual open fun getHeadElements(): List<String> {
        return headElements.toList()
    }

    // Renders a composable component in the current context
// This is a convenience method for rendering a composable without directly accessing FlowContent
    actual open fun renderComposable(composable: @Composable () -> Unit) {
        currentBuilder?.let { builder ->
            // If there's a current builder, execute content within its context
            // This assumes composable() will use the implicit receiver (builder)
            // Or, if composable() itself manages FlowContent, it might work differently.
            // For kotlinx.html, direct execution like this should be fine if composable uses html {} blocks.
            composable()
        }
            ?: error("renderComposable called without an active FlowContent builder. Ensure it's within renderComposableRoot or a parent Composable.")
    }

    actual open fun renderComposableRoot(composable: @Composable (() -> Unit)): String {
        val currentContext = currentBuilder
        val result = StringBuilder()
        try {
            result.appendHTML(prettyPrint = false).html {
                head {
                    meta(charset = "UTF-8")
                    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                    title("Summon App")
                    synchronized(headElements) { headElements.forEach { unsafe { raw(it) } } }
                }
                body {
                    currentBuilder = this // Set context
                    try {
                        renderContent(composable)
                    } finally {
                        currentBuilder = null // Ensure context is cleared
                    }
                }
            }
        } finally {
            if (currentBuilder != null) { // Double-check clearance on exception
                System.err.println("Warning: currentBuilder not cleared properly in renderComposableRoot.")
                currentBuilder = null
            }
            // Restore previous context if needed (should be null)
            // currentBuilder = previousBuilder
        }
        return result.toString()
    }

    actual open fun renderRow(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Row styling should be in Modifier ")
            renderContent(content)
        }
    }

    actual open fun renderColumn(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Column styling should be in Modifier ")
            renderContent(content)
        }
    }

    actual open fun renderBox(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    actual open fun renderBoxContainer(modifier: Modifier, content: @Composable () -> Unit) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier.display("block"))) // Typically a div
            renderContent(content)
        }
    }

    actual open fun renderImage(src: String, alt: String?, modifier: Modifier) {
        requireBuilder().img {
            this.src = src
            this.alt = alt ?: ""
            applyModifier(modifier)
        }
    }

    actual open fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    ) {
        requireBuilder().span {
            applyModifier(modifier)
            if (onClick != null) {
                attributes["data-onclick-action"] = "true"
                comment(" JS Hook needed for onClick ")
                val currentStyle = attributes["style"] ?: ""
                if ("cursor" !in currentStyle) attributes["style"] = "$currentStyle;cursor:pointer;".trimStart(';')
            }
            if (svgContent != null) {
                unsafe { raw(svgContent) }
            } else {
                i { comment(" Icon font class for '$name' needed via CSS/Modifier ") }
            }
        }
    }

    actual open fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            attributes["role"] = "alert"
            applyModifier(modifier)
            comment(" Alert styling for variant '${variant?.name}' needed via CSS/Modifier ")
            renderContent(content)
        }
    }

    actual open fun renderBadge(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().span {
            applyModifier(modifier)
            comment(" Badge styling via Modifier/CSS needed ")
            renderContent(content)
        }
    }

    actual open fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().input(type = InputType.checkBox) {
            applyModifier(modifier)
            this.checked = checked
            this.disabled = !enabled
            id = "checkbox-${UUID.randomUUID()}"
            name = id
            attributes["data-onchange-action"] = "true"
            comment(" onCheckedChange handler needed (JS) ")
        }
    }

    actual open fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().input(type = InputType.range) {
            applyModifier(modifier)
            min = valueRange.start.toString()
            max = valueRange.endInclusive.toString()
            if (steps > 0) step = ((valueRange.endInclusive - valueRange.start) / steps).toString()
            this.value = value.toString()
            this.disabled = !enabled
            id = "slider-${UUID.randomUUID()}"
            name = id
            comment(" onValueChange handler needed (JS) ")
            attributes["data-onchange-action"] = "true"
        }
    }

    actual open fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().div { // Requires JS library
            applyModifier(modifier)
            comment(" Range Slider requires JS library. Rendering basic inputs fallback. ")
            val stepValue = if (steps > 0) ((valueRange.endInclusive - valueRange.start) / steps) else null

            label {
                +"Start: "; input(type = InputType.range) {
                id = "range-start-${UUID.randomUUID()}"
                min = valueRange.start.toString()
                max = valueRange.endInclusive.toString()
                stepValue?.let { this.step = it.toString() }
                this.value = value.start.toString()
                this.disabled = !enabled
                attributes["data-onchange-action"] = "true"
            }
            }
            br()
            label {
                +"End: "; input(type = InputType.range) {
                id = "range-end-${UUID.randomUUID()}"
                min = valueRange.start.toString()
                max = valueRange.endInclusive.toString()
                stepValue?.let { this.step = it.toString() }
                this.value = value.endInclusive.toString()
                this.disabled = !enabled
                attributes["data-onchange-action"] = "true"
            }
            }
            comment(" onValueChange handler needed (JS) to coordinate sliders ")
        }
    }

    actual open fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().input(type = InputType.time) {
            applyModifier(modifier)
            if (value != null) this.value = value.toString().take(8) // HH:MM:SS
            this.disabled = !enabled
            comment(" 12/24 hour display depends on browser/locale. ")
            id = "time-${UUID.randomUUID()}"
            name = id
            attributes["data-onchange-action"] = "true"
            comment(" onValueChange handler needed (JS) ")
        }
    }

    actual open fun renderLink(href: String, modifier: Modifier) {
        requireBuilder().a(href = href) {
            applyModifier(modifier)
            +href
        }
    }

    actual open fun renderLink(
        href: String,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    ) {
        requireBuilder().a(href = href) {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    actual open fun renderFileUpload(
        onFileSelected: (FileInfo?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        val inputId = "file-${UUID.randomUUID()}"
        requireBuilder().input(type = InputType.file) {
            applyModifier(modifier)
            this.disabled = !enabled
            id = inputId
            name = id
            comment(" onFileSelected handler needed (JS) ")
            attributes["data-onchange-action"] = "true"
        }
        return { System.err.println("Programmatic file upload trigger not available server-side.") }
    }

    actual open fun renderForm(
        onSubmit: () -> Unit,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().form {
            applyModifier(modifier)
            if (onSubmit != null) {
                attributes["data-onsubmit-action"] = "true"
                comment(" onSubmit handler needed (JS). Form action/method? ")
            }
            // Fix duplicate supertype: FormContent extends FlowContent
            val formContentImpl = object : FormContent by this {}
            content(formContentImpl)
        }
    }

    actual open fun renderTabs(
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier,
        tabs: List<Tab>
    ) {
        val builder = requireBuilder() // Get the builder context once
        builder.div { // Outer container
            this.applyModifier(modifier) // Apply modifier to the outer div
            comment(" Tab layout requires JS for interaction ")

            // Tab Headers
            div(classes = "tab-list") {
                attributes["role"] = "tablist"
                tabs.forEachIndexed { index, tab ->
                    val tabId = "tab-${tab.id}"
                    val panelId = "panel-${tab.id}"
                    // Fixed: Use standard kotlinx.html button()
                    button(classes = "tab-item ${if (index == selectedTabIndex) "selected" else ""}") {
                        attributes["role"] = "tab"
                        attributes["aria-controls"] = panelId
                        attributes["aria-selected"] = (index == selectedTabIndex).toString()
                        attributes["data-tab-index"] = index.toString()
                        attributes["data-onclick-action"] = "true"
                        attributes["id"] = tabId
                        comment(" onTabSelected handler needed (JS) ")
                        comment(" CSS needed for selected state ")
                        +(tab.title ?: "Tab ${index + 1}")
                    }
                }
            }

            // Tab Panels
            div(classes = "tab-panels") {
                tabs.forEachIndexed { index, tab ->
                    val panelId = "panel-${tab.id}"
                    val tabId = "tab-${tab.id}" // Match ID generation
                    // Fixed: Use standard kotlinx.html div()
                    div(classes = "tab-panel ${if (index == selectedTabIndex) "active" else ""}") {
                        attributes["role"] = "tabpanel"
                        attributes["aria-labelledby"] = tabId
                        attributes["id"] = panelId
                        if (index != selectedTabIndex) {
                            comment(" Inactive panel should be hidden via CSS/JS ")
                        }
                        // Fixed: Handle composable content
                        val savedBuilder = currentBuilder
                        currentBuilder = this
                        try {
                            // Handle imported Tab.content as a @Composable function
                            tab.content()
                        } finally {
                            currentBuilder = savedBuilder
                        }
                        comment(" CSS needed for active state ")
                    }
                }
            }
        }
    }

    actual open fun renderTab(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Tab content should be wrapped in a tab panel ")
            renderContent(content)
        }
    }

    actual open fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().label { // Requires specific CSS
            applyModifier(modifier) // Style label, CSS targets input/span
            comment(" Switch requires CSS styling ")
            input(type = InputType.checkBox) {
                this.checked = checked
                this.disabled = !enabled
                attributes["style"] =
                    "position:absolute; opacity:0; pointer-events:none; width:0; height:0;" // Hide visually
                attributes["data-onchange-action"] = "true"
                comment(" onCheckedChange handler needed (JS) ")
            }
            span { comment(" CSS needed for switch slider appearance ") }
        }
    }

    actual open fun renderHtmlTag(
        tagName: String,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        val builder = requireBuilder()

        // Create a div container to hold our custom tag content
        builder.div {
            // Apply the modifier to the div for now
            applyModifier(modifier)

            // Add a comment indicating this is a custom tag wrapper
            comment(" Custom tag '$tagName' wrapped in div. JS needed to transform DOM. ")

            // Add data attribute to help JS transform this element
            attributes["data-custom-tag"] = tagName

            // Render the content in the div
            renderContent(content)
        }

        // Add a comment about the limitation
        builder.comment(" Note: Direct custom tag rendering requires JS DOM manipulation ")
    }

    actual open fun renderSpan(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        requireBuilder().span {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    actual open fun renderDivider(modifier: Modifier) {
        requireBuilder().hr {
            applyModifier(modifier)
        }
    }

    actual open fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        // Basic div implementation for JVM, actual expansion behavior might need JS
        requireBuilder().div {
            applyModifier(modifier.then(Modifier.border(1, "solid", "grey")))
            comment(" ExpansionPanel: JS might be needed for interactive expand/collapse ")
            renderContent(content)
        }
    }

    actual open fun renderGrid(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier.display("grid")))
            renderContent(content)
        }
    }

    actual open fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        // JVM equivalent: scrollable div
        requireBuilder().div {
            applyModifier(modifier.then(Modifier.overflowY("auto")))
            renderContent(content)
        }
    }

    actual open fun renderLazyRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        // JVM equivalent: scrollable div
        requireBuilder().div {
            applyModifier(modifier.then(Modifier.overflowX("auto")))
            renderContent(content)
        }
    }

    actual open fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        // Basic div, actual responsiveness will depend on CSS within modifier and content
        requireBuilder().div {
            applyModifier(modifier)
            comment(" ResponsiveLayout: Ensure CSS handles different screen sizes ")
            // Add screen size detection script (example from previous JvmPlatformRenderer)
            script(type = "text/javascript") {
                unsafe {
                    raw(
                        """
                    (function() {
                        const BREAKPOINTS = { SMALL: 600, MEDIUM: 960, LARGE: 1280 };
                        function determineScreenSize() {
                            const width = window.innerWidth;
                            if (width < BREAKPOINTS.SMALL) return 'SMALL';
                            else if (width < BREAKPOINTS.MEDIUM) return 'MEDIUM';
                            else if (width < BREAKPOINTS.LARGE) return 'LARGE';
                            else return 'XLARGE';
                        }
                        function updateLayout() {
                            const layout = document.currentScript ? document.currentScript.parentElement : document.body;
                            const screenSize = determineScreenSize();
                            layout.classList.remove('small-screen', 'medium-screen', 'large-screen', 'xlarge-screen');
                            layout.classList.add(screenSize.toLowerCase() + '-screen');
                            layout.setAttribute('data-screen-size', screenSize);
                            const event = new CustomEvent('screenSizeChanged', { detail: { screenSize: screenSize } });
                            layout.dispatchEvent(event);
                        }
                        if (document.readyState === 'loading') { document.addEventListener('DOMContentLoaded', updateLayout); }
                        else { updateLayout(); }
                        window.addEventListener('resize', updateLayout);
                    })();
                    """
                    )
                }
            }
            renderContent(content)
        }
    }

    actual open fun renderSnackbar(message: String, actionLabel: String?, onAction: (() -> Unit)?) {
        // JVM: Simple text representation, or a div styled to look like a snackbar.
        // Actual snackbar behavior (timing, dismissal) is typically JS-driven.
        requireBuilder().div {
            style =
                "position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%); background-color: #333; color: white; padding: 10px 20px; border-radius: 4px; z-index: 1000;"
            +message
            if (actionLabel != null) {
                button {
                    style = "margin-left: 15px; background: none; border: none; color: #80deea; cursor: pointer;"
                    +actionLabel
                    if (onAction != null) {
                        comment(" JS hook for onAction on snackbar button needed ")
                        // attributes["data-onclick-action"] = createJsAction(onAction) // Placeholder for JS hookup
                    }
                }
            }
            comment(" Snackbar: JS needed for timeout and dismissal ")
        }
    }

    actual open fun renderDropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable (() -> Unit)
    ) {
        if (expanded) {
            requireBuilder().div {
                applyModifier(
                    modifier.then(
                        Modifier.position("absolute").border(1, "solid", "#ccc").backgroundColor("white").zIndex(100)
                    )
                )
                comment(" DropdownMenu: JS for positioning and dismissal (onDismissRequest) needed ")
                attributes["data-onclick-dismiss"] = "true" // Example for JS hook
                renderContent(content)
            }
        }
    }

    actual open fun renderTooltip(text: String, modifier: Modifier, content: @Composable (() -> Unit)) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier.position("relative")))
            attributes["title"] = text // Basic browser tooltip
            comment(" Tooltip: For custom styling, JS/CSS solution is better ")
            renderContent(content)
        }
    }

    actual open fun renderModal(
        visible: Boolean,
        onDismissRequest: () -> Unit,
        title: String?,
        content: @Composable (() -> Unit),
        actions: @Composable (() -> Unit)?
    ) {
        if (visible) {
            requireBuilder().div { // Modal overlay
                style =
                    "position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 2000;"
                attributes["data-onclick-dismiss-modal"] = "true" // Hook for closing by clicking overlay

                div { // Modal content
                    style =
                        "background-color: white; padding: 20px; border-radius: 5px; min-width: 300px; max-width: 80%;"
                    applyModifier(modifier) // Apply user modifier to content box
                    if (title != null) {
                        h3 { +title }
                    }
                    renderContent(content)
                    if (actions != null) {
                        div { style = "margin-top: 10px;"; renderContent(actions) }
                    }
                }
                comment(" Modal: JS for onDismissRequest and complex interactions needed ")
            }
        }
    }

    actual open fun renderScreen(modifier: Modifier, content: @Composable (FlowContent.() -> Unit)) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier.width("100%").height("100vh")))
            renderContent(content)
        }
    }

    actual open fun renderHtml(htmlContent: String, modifier: Modifier) {
        requireBuilder().div { // Wrapper div to apply modifier
            applyModifier(modifier)
            unsafe { raw(htmlContent) }
        }
    }

    actual open fun renderSurface(modifier: Modifier, elevation: Int, content: @Composable (() -> Unit)) {
        // Elevation can be simulated with box-shadow
        val elevationStyle = when (elevation) {
            1 -> "box-shadow: 0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24);"
            2 -> "box-shadow: 0 3px 6px rgba(0,0,0,0.16), 0 3px 6px rgba(0,0,0,0.23);"
            // Add more levels as needed
            else -> ""
        }
        requireBuilder().div {
            applyModifier(modifier)
            if (elevationStyle.isNotBlank()) {
                attributes["style"] = (attributes["style"] ?: "") + elevationStyle
            }
            renderContent(content)
        }
    }

    actual open fun renderSwipeToDismiss(
        state: Any, // State object, likely for JS interop
        background: @Composable (() -> Unit),
        modifier: Modifier,
        content: @Composable (() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" SwipeToDismiss: Full functionality requires JS. This is a static representation. ")
            div { // Background placeholder
                style = "position: absolute; top: 0; left: 0; right: 0; bottom: 0; z-index: 0;"
                renderContent(background)
            }
            div { // Content placeholder
                style = "position: relative; z-index: 1; background-color: white;" // Ensure content is above background
                renderContent(content)
            }
        }
    }

    actual open fun renderVerticalPager(count: Int, state: Any, modifier: Modifier, content: @Composable ((Int) -> Unit)) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" VerticalPager: JS for pagination logic needed. Displaying first page. ")
            if (count > 0) {
                renderContent { content(0) } // Render first page as placeholder
            }
        }
    }

    actual open fun renderHorizontalPager(count: Int, state: Any, modifier: Modifier, content: @Composable ((Int) -> Unit)) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" HorizontalPager: JS for pagination logic needed. Displaying first page. ")
            if (count > 0) {
                renderContent { content(0) } // Render first page as placeholder
            }
        }
    }

    actual open fun renderAspectRatioContainer(ratio: Float, modifier: Modifier, content: @Composable (() -> Unit)) {
        requireBuilder().div {
            // CSS trick for aspect ratio box
            val paddingBottom = "${(1f / ratio) * 100}%"
            val outerStyle = "position: relative; width: 100%; height: 0; padding-bottom: $paddingBottom;"
            val innerStyle = "position: absolute; top: 0; left: 0; width: 100%; height: 100%;"

            attributes["style"] = (attributes["style"] ?: "") + outerStyle
            applyModifier(modifier) // Apply user modifier to outer div

            div { // Inner div for content
                attributes["style"] = innerStyle
                renderContent(content)
            }
        }
    }

    actual open fun renderFilePicker(
        onFilesSelected: (List<FileInfo>) -> Unit,
        enabled: Boolean,
        multiple: Boolean,
        accept: String?,
        modifier: Modifier,
        actions: @Composable (() -> Unit)? // Content for the button itself
    ) {
        requireBuilder().input(type = InputType.file) {
            applyModifier(modifier)
            this.multiple = multiple
            if (accept != null) this.accept = accept
            this.disabled = !enabled
            // JS needed to handle onFilesSelected and map to FileInfo
            comment(" FilePicker: JS needed for onFilesSelected callback and FileInfo creation ")
            attributes["data-onfilesselected-action"] = "true"
            // 'actions' would typically be the content of a button that triggers this input if it's hidden
            // For a direct input, 'actions' might not be directly applicable this way.
            // If 'actions' represents the button text/content for a styled picker:
            if (actions != null) {
                comment(" FilePicker 'actions' provided, but direct input shown. Consider custom styling with a label/button. ")
                // This actual implementation shows a raw file input. To use `actions` as the clickable element,
                // this input would need to be hidden and triggered by a button rendered via `actions`.
            }
        }
    }

    actual open fun renderAlert(
        message: String,
        variant: AlertVariant,
        modifier: Modifier,
        title: String?,
        icon: @Composable (() -> Unit)?,
        actions: @Composable (() -> Unit)?
    ) {
        val alertColor = when (variant) {
            AlertVariant.INFO -> "#e0f7fa"
            AlertVariant.SUCCESS -> "#e8f5e9"
            AlertVariant.WARNING -> "#fff3e0"
            AlertVariant.ERROR -> "#ffebee"
        }
        requireBuilder().div {
            style =
                "padding: 15px; margin-bottom: 20px; border: 1px solid transparent; border-radius: 4px; background-color: $alertColor;"
            applyModifier(modifier)
            if (icon != null) {
                span { style = "margin-right: 10px;"; renderContent(icon) }
            }
            if (title != null) {
                strong { +title; style = "display: block; margin-bottom: 5px;" }
            }
            +message
            if (actions != null) {
                div { style = "margin-top: 10px;"; renderContent(actions) }
            }
        }
    }

    actual open fun renderCard(modifier: Modifier, elevation: Int, content: @Composable (() -> Unit)) {
        // Re-use renderSurface for card appearance
        renderSurface(modifier.then(Modifier.border(1, "solid", "#ddd")), elevation, content)
    }

    actual open fun renderLinearProgressIndicator(progress: Float?, modifier: Modifier, type: ProgressType) {
        requireBuilder().progress {
            applyModifier(modifier)
            if (type == ProgressType.DETERMINATE && progress != null) {
                this.value = progress.toString()
                max = "1"
            } else {
                comment(" Indeterminate LinearProgressIndicator ")
                // No value attribute for indeterminate HTML progress, styling might be needed
            }
        }
    }

    actual open fun renderCircularProgressIndicator(progress: Float?, modifier: Modifier, type: ProgressType) {
        // HTML doesn't have a native circular progress. Simulate with text or requires SVG/JS.
        requireBuilder().div {
            applyModifier(modifier)
            if (type == ProgressType.DETERMINATE && progress != null) {
                +"Progress: ${(progress * 100).toInt()}% (Circular - requires SVG/JS for visual)"
            } else {
                +"Loading... (Circular - requires SVG/JS for visual)"
            }
            comment(" CircularProgressIndicator: Visual representation requires SVG/CSS or JS library. ")
        }
    }

    actual open fun renderModalBottomSheet(
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable (() -> Unit)
    ) {
        // Simplified modal for JVM, true bottom sheet behavior is JS/CSS driven.
        requireBuilder().div { // Overlay
            style =
                "position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.3); display: flex; align-items: flex-end; justify-content: center; z-index: 1500;"
            attributes["data-onclick-dismiss-modal-sheet"] = "true"

            div { // Sheet content
                style =
                    "background-color: white; padding: 20px; border-top-left-radius: 8px; border-top-right-radius: 8px; width: 100%; max-width: 600px; box-shadow: 0 -2px 10px rgba(0,0,0,0.1);"
                applyModifier(modifier)
                renderContent(content)
            }
            comment(" ModalBottomSheet: JS for onDismissRequest and animations needed ")
        }
    }

    actual open fun renderAlertDialog(
        onDismissRequest: () -> Unit,
        confirmButton: @Composable (() -> Unit),
        modifier: Modifier,
        dismissButton: @Composable (() -> Unit)?,
        icon: @Composable (() -> Unit)?,
        title: @Composable (() -> Unit)?,
        text: @Composable (() -> Unit)?
    ) {
        requireBuilder().div { // Overlay
            style =
                "position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 2500;"

            div { // Dialog box
                style =
                    "background-color: white; padding: 25px; border-radius: 8px; min-width: 280px; max-width: 560px; box-shadow: 0 4px 20px rgba(0,0,0,0.2);"
                applyModifier(modifier)
                if (icon != null) {
                    div { style = "text-align: center; margin-bottom: 15px;"; renderContent(icon) }
                }
                if (title != null) {
                    div { style = "font-size: 1.25em; font-weight: bold; margin-bottom: 10px;"; renderContent(title) }
                }
                if (text != null) {
                    div { style = "margin-bottom: 20px;"; renderContent(text) }
                }
                div { // Buttons
                    style = "display: flex; justify-content: flex-end; gap: 10px;"
                    if (dismissButton != null) {
                        renderContent(dismissButton)
                    }
                    renderContent(confirmButton)
                }
                comment(" AlertDialog: JS for onDismissRequest and button actions needed ")
            }
        }
    }

    actual open fun renderRadioButton(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        label: String?,
        enabled: Boolean,
        modifier: Modifier
    ) {
        val radioId = "radio-${UUID.randomUUID()}"
        requireBuilder().span { // Wrapper for radio and label
            applyModifier(modifier)
            input(
                type = InputType.radio,
                name = "radio-group-${hashCode()}"
            ) { // Ensure unique name for groups if needed
                id = radioId
                this.checked = checked
                this.disabled = !enabled
                attributes["data-onchange-action"] = "true"
                comment(" onCheckedChange JS hook needed ")
            }
            if (label != null) {
                label(htmlFor = radioId) { +label; style = "margin-left: 8px;" }
            }
        }
    }

    actual open fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        label: String?,
        modifier: Modifier
    ) {
        val checkboxId = "checkbox-${UUID.randomUUID()}"
        requireBuilder().span { // Wrapper for checkbox and label
            applyModifier(modifier)
            input(type = InputType.checkBox) {
                id = checkboxId
                this.checked = checked
                this.disabled = !enabled
                attributes["data-onchange-action"] = "true"
                comment(" onCheckedChange JS hook needed ")
            }
            if (label != null) {
                label(htmlFor = checkboxId) { +label; style = "margin-left: 8px;" }
            }
        }
    }
}
