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
import kotlinx.html.*
import kotlinx.html.attributes.AttributeEncoder
import kotlinx.html.attributes.StringAttribute
import kotlinx.html.stream.appendHTML
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

// Interface defined in PlatformRenderer.kt commonMain
// interface FormContent : FlowContent

// JvmPlatformRenderer implementation
class JvmPlatformRenderer : PlatformRenderer {

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

    override fun renderText(text: String, modifier: Modifier) {
        requireBuilder().span {
            applyModifier(modifier)
            +text
        }
    }

    override fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().button {
            applyModifier(modifier)
            attributes["data-onclick-action"] = "true"
            comment(" JS Hook needed for onClick ")
            renderContent(content) // Use helper with receiver
        }
    }

    override fun renderTextField(
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

     override fun <T> renderSelect(
         selectedValue: T?,
         onSelectedChange: (T?) -> Unit,
         options: List<SelectOption<T>>,
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

    override fun renderDatePicker(
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

    override fun addHeadElement(content: String) {
        synchronized(headElements) { headElements.add(content) }
    }

    override fun getHeadElements(): List<String> {
        return headElements.toList()
    }

    /**
     * Renders a composable component in the current context
     * This is a convenience method for rendering a composable without directly accessing FlowContent
     */
    override fun renderComposable(composable: @Composable () -> Unit) {
        requireBuilder() // Ensure context exists before calling content
        renderContent(composable) // Delegate to the internal renderContent helper
    }

    override fun renderComposableRoot(composable: @Composable (() -> Unit)): String {
        if (currentBuilder != null) {
             System.err.println("Warning: JvmPlatformRenderer.renderComposableRoot called while currentBuilder is set.")
         }
        val previousBuilder = currentBuilder
        val sb = StringBuilder()
        currentBuilder = null // Ensure clean context

        try {
            sb.appendHTML(prettyPrint = false).html {
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
        return sb.toString()
    }

    override fun renderRow(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Row styling should be in Modifier ")
            renderContent(content)
        }
    }

    override fun renderColumn(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Column styling should be in Modifier ")
            renderContent(content)
        }
    }

    override fun renderBox(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    override fun renderImage(src: String, alt: String, modifier: Modifier) {
        requireBuilder().img(alt = alt, src = src) {
            applyModifier(modifier)
        }
    }

    override fun renderIcon(
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

    override fun renderAlertContainer(
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

    override fun renderBadge(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().span {
            applyModifier(modifier)
            comment(" Badge styling via Modifier/CSS needed ")
            renderContent(content)
        }
    }

    override fun renderCheckbox(
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

    override fun renderProgress(
        value: Float?,
        type: ProgressType,
        modifier: Modifier
    ) {
        requireBuilder().progress {
            applyModifier(modifier)
            if (value != null) this.value = value.coerceIn(0f, 1f).toString()
            comment(" Progress type '${type.name}' styling needed via Modifier/CSS ")
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
        val inputId = "file-${UUID.randomUUID()}"
        requireBuilder().input(type = InputType.file) {
            applyModifier(modifier)
            if (accept != null) this.accept = accept
            this.multiple = multiple
            this.disabled = !enabled
            if (capture != null) this.attributes["capture"] = capture
            id = inputId
            name = id
            comment(" onFilesSelected handler needed (JS) ")
            attributes["data-onchange-action"] = "true"
        }
        return { System.err.println("Programmatic file upload trigger not available server-side.") }
    }

    override fun renderForm(
        onSubmit: (() -> Unit)?,
        modifier: Modifier,
        content: @Composable (FormContent.() -> Unit)
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

    override fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div { // Wrapper div
            applyModifier(modifier)
            if (isError) {
                attributes["aria-invalid"] = "true"
                if (errorMessageId != null) attributes["aria-describedby"] = errorMessageId
                comment(" Error state styling needed via CSS/Modifier ")
            }
            comment(" Label (for=$labelId) expected separately. isRequired=$isRequired ")
            renderContent(content) // Render input(s)
            if (isError && errorMessageId != null) {
                 div { // Error message container
                     id = errorMessageId
                     attributes["role"] = "alert"
                     comment(" Error message content needed here ")
                 }
             }
        }
    }

    override fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().input(type = InputType.radio) {
            applyModifier(modifier)
            this.checked = selected
            this.disabled = !enabled
            id = "radio-${UUID.randomUUID()}"
            comment(" Radio button 'name' attribute MUST be provided for grouping. onClick handler needed (JS). ")
            attributes["data-onclick-action"] = "true"
        }
    }

    override fun renderSpacer(modifier: Modifier) {
        requireBuilder().div {
            applyModifier(modifier) // Modifier MUST define size via styles
            comment(" Spacer size must be defined by Modifier styles ")
            attributes["aria-hidden"] = "true"
        }
    }

    override fun renderRangeSlider(
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

            label { +"Start: "; input(type = InputType.range) {
                id = "range-start-${UUID.randomUUID()}"
                min = valueRange.start.toString()
                max = valueRange.endInclusive.toString()
                stepValue?.let { this.step = it.toString() }
                this.value = value.start.toString()
                this.disabled = !enabled
                attributes["data-onchange-action"] = "true"
            }}
             br()
             label { +"End: "; input(type = InputType.range) {
                 id = "range-end-${UUID.randomUUID()}"
                 min = valueRange.start.toString()
                 max = valueRange.endInclusive.toString()
                 stepValue?.let { this.step = it.toString() }
                 this.value = value.endInclusive.toString()
                 this.disabled = !enabled
                 attributes["data-onchange-action"] = "true"
             }}
            comment(" onValueChange handler needed (JS) to coordinate sliders ")
        }
    }

    override fun renderSlider(
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

    override fun renderSwitch(
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
                attributes["style"] = "position:absolute; opacity:0; pointer-events:none; width:0; height:0;" // Hide visually
                attributes["data-onchange-action"] = "true"
                comment(" onCheckedChange handler needed (JS) ")
            }
            span { comment(" CSS needed for switch slider appearance ") }
        }
    }

    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().input(type = InputType.time) {
            applyModifier(modifier)
            if (value != null) this.value = value.toString().take(8) // HH:MM:SS
            this.disabled = !enabled
            comment(" 12/24 hour display depends on browser/locale. is24Hour=$is24Hour hint ignored. ")
            id = "time-${UUID.randomUUID()}"
            name = id
            attributes["data-onchange-action"] = "true"
            comment(" onValueChange handler needed (JS) ")
        }
    }

    override fun renderAspectRatio(
        ratio: Float,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Aspect ratio styling should be handled by Modifier/CSS. Applying directly if needed. ")
             val currentStyle = attributes["style"] ?: ""
             // Check the modifier's internal styles instead of the element's current style attribute
             val modifierStyles = modifier.styles // Assuming Modifier has a 'styles' property
             if ("aspect-ratio" !in modifierStyles) { // Check if modifier already sets it
                 attributes["style"] = "$currentStyle;aspect-ratio: $ratio;".trimStart(';')
             }
            renderContent(content)
        }
    }

    override fun renderCard(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Card styling via Modifier/CSS needed ")
            renderContent(content)
        }
    }

    override fun renderLink(href: String, modifier: Modifier) {
        requireBuilder().a(href = href) {
            applyModifier(modifier)
            +href
        }
    }

    override fun renderLink(
        modifier: Modifier,
        href: String,
        content: @Composable () -> Unit
    ) {
        requireBuilder().a(href = href) {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    override fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier
    ) {
        requireBuilder().a(href = href, target = target) {
            applyModifier(modifier)
            if (title != null) this.title = title
            if (ariaLabel != null) attributes["aria-label"] = ariaLabel
            if (ariaDescribedBy != null) attributes["aria-describedby"] = ariaDescribedBy
            comment(" EnhancedLink content missing from signature - using href ")
            +href
        }
    }

    // Fix 3: Assume Tab.content is () -> Unit
    @OptIn(ExperimentalUuidApi::class)
    override fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
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
                            // Handle imported Tab.content as a Composable object
                            tab.content.compose(this)
                        } finally {
                            currentBuilder = savedBuilder
                        }
                        comment(" CSS needed for active state ")
                    }
                }
            }
        }
    }

    override fun renderTabLayout(
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Assuming content lambda defines the full tab structure ")
            renderContent(content)
        }
    }

    override fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Tab layout (String list variant) requires JS ")
            div(classes = "tab-list") {
                 attributes["role"] = "tablist"
                tabs.forEach { tabName ->
                     button(classes = "tab-item ${if (tabName == selectedTab) "selected" else ""}") {
                         attributes["role"] = "tab"
                        attributes["aria-selected"] = (tabName == selectedTab).toString()
                         attributes["data-tab-name"] = tabName
                         attributes["data-onclick-action"] = "true"
                         comment(" onTabSelected handler needed (JS) ")
                         +tabName
                    }
                }
            }
            div(classes = "tab-panels") {
                comment(" Content lambda called unconditionally. Panel logic unclear. ")
                content()
            }
        }
    }

    override fun renderAnimatedVisibility(
        visible: Boolean,
        modifier: Modifier
    ) {
        if (visible) {
            requireBuilder().div {
                applyModifier(modifier)
                attributes["data-animated"] = "true"
                attributes["data-visible"] = "true"
                attributes["data-animation-type"] = "visibility"
                comment(" AnimatedVisibility visible - content missing from signature ")
            }
        } else {
            requireBuilder().div {
                applyModifier(modifier)
                attributes["data-animated"] = "true"
                attributes["data-visible"] = "false"
                attributes["data-animation-type"] = "visibility"
                attributes["style"] = "display: none;" // Hide when not visible
                comment(" AnimatedVisibility hidden - content not rendered ")
            }
        }
    }

    override fun renderAnimatedVisibility(
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            attributes["data-animated"] = "true"
            attributes["data-visible"] = "true"
            attributes["data-animation-type"] = "visibility"
            renderContent(content)
        }
    }

    override fun renderAnimatedContent(modifier: Modifier) {
        requireBuilder().div {
            applyModifier(modifier)
            attributes["data-animated"] = "true"
            attributes["data-animation-type"] = "content"
            comment(" AnimatedContent - Content will be provided separately ")
        }
    }

    override fun renderAnimatedContent(
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            attributes["data-animated"] = "true"
            attributes["data-animation-type"] = "content"
            attributes["data-content-transition"] = "true"
            renderContent(content)
        }
    }

    override fun renderBlock(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div { // Default block element
            applyModifier(modifier)
            renderContent(content)
        }
    }

    override fun renderInline(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().span { // Default inline element
            applyModifier(modifier)
            renderContent(content)
        }
    }

    override fun renderDiv(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    override fun renderSpan(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().span {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    override fun renderDivider(modifier: Modifier) {
        requireBuilder().hr {
            applyModifier(modifier)
        }
    }

    override fun renderExpansionPanel(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().details { // Use native <details>
            applyModifier(modifier)

            // Extract title from modifier if available
            val title = modifier.getAttributeValue("data-expansion-title") ?: "Details"
            val isOpen = modifier.getAttributeValue("data-expansion-open") == "true"

            // Add open attribute if panel should be expanded by default
            if (isOpen) {
                attributes["open"] = "true"
            }

            summary {
                // Use title from modifier or default
                +title
            }

            div(classes = "expansion-content") { // Wrapper for styling
                renderContent(content)
            }
        }
    }

    // Helper extension to get attribute value from modifier
    private fun Modifier.getAttributeValue(key: String): String? {
        // This is a simplified implementation - in a real scenario, you would
        // have a proper way to extract attributes from the Modifier
        return null // Placeholder - implement actual attribute extraction
    }

    override fun renderGrid(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier) // Modifier must apply display:grid styles
            comment(" Grid layout styles needed via Modifier ")
            renderContent(content)
        }
    }

    override fun renderLazyColumn(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div { // Requires JS for true laziness
            applyModifier(modifier) // Modifier must set height/overflow
            comment(" LazyColumn rendered as scrollable div. Needs JS for true laziness. ")
            renderContent(content) // Render all items
        }
    }

    override fun renderLazyRow(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div { // Requires JS for true laziness
            applyModifier(modifier) // Modifier must set width/overflow
            comment(" LazyRow rendered as scrollable div. Needs JS for true laziness. ")
            renderContent(content) // Render all items
        }
    }

    override fun renderResponsiveLayout(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)

            // Add responsive layout attributes
            attributes["class"] = "responsive-layout"
            attributes["data-responsive"] = "true"

            // Add screen size detection script
            script(type = "text/javascript") {
                unsafe {
                    raw("""
                    (function() {
                        // Screen size breakpoints
                        const BREAKPOINTS = {
                            SMALL: 600,   // Mobile phones (< 600px)
                            MEDIUM: 960,  // Tablets (600px - 959px)
                            LARGE: 1280   // Desktop (960px - 1279px)
                            // XLARGE: >= 1280px (Large desktop)
                        };

                        // Function to determine current screen size
                        function determineScreenSize() {
                            const width = window.innerWidth;
                            if (width < BREAKPOINTS.SMALL) {
                                return 'SMALL';
                            } else if (width < BREAKPOINTS.MEDIUM) {
                                return 'MEDIUM';
                            } else if (width < BREAKPOINTS.LARGE) {
                                return 'LARGE';
                            } else {
                                return 'XLARGE';
                            }
                        }

                        // Function to update layout based on screen size
                        function updateLayout() {
                            const layout = document.currentScript.parentElement;
                            const screenSize = determineScreenSize();

                            // Remove all screen size classes
                            layout.classList.remove('small-screen', 'medium-screen', 'large-screen', 'xlarge-screen');

                            // Add current screen size class
                            layout.classList.add(screenSize.toLowerCase() + '-screen');

                            // Set data attribute for current screen size
                            layout.setAttribute('data-screen-size', screenSize);

                            // Dispatch custom event for screen size change
                            const event = new CustomEvent('screenSizeChanged', { 
                                detail: { screenSize: screenSize } 
                            });
                            layout.dispatchEvent(event);
                        }

                        // Initialize layout
                        updateLayout();

                        // Update layout on window resize
                        window.addEventListener('resize', updateLayout);
                    })();
                    """)
                }
            }

            // Render content
            renderContent(content)
        }
    }

    // Render a label element for semantic HTML
    override fun renderLabel(
        text: String,
        modifier: Modifier,
        forElement: String?
    ) {
        requireBuilder().label {
            applyModifier(modifier)
            if (forElement != null) {
                htmlFor = forElement
            }
            +text
        }
    }

    // Fix 4: Fix HTMLTag rendering to avoid visit() issues
    override fun renderHtmlTag(
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
}
