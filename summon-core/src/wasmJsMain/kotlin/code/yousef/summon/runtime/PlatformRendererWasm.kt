package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.*
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.core.FlowContent
import code.yousef.summon.core.FlowContentCompat
import code.yousef.summon.modifier.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

// Since PlatformRenderer has many methods, providing stub implementations for WASM
actual open class PlatformRenderer actual constructor() {
    // Store the composer and root element for recomposition
    private var mainComposer: Composer? = null
    private var mainRootElement: DOMElement? = null
    private var isInitialMount = true

    // DOM reconciliation tracking
    private val currentCompositionElements = mutableSetOf<String>()
    private val previousCompositionElements = mutableSetOf<String>()

    // Element placement tracking to prevent duplicate appendChild calls
    private val placedElements = mutableSetOf<String>()

    actual open fun renderText(text: String, modifier: Modifier) {
        try {
            // Check for hydration markers in modifier
            val summonId = modifier.attributes?.get("data-summon-id")

            // Create or reuse text element
            val existingElement = createOrReuseElement("span", summonId)
            val isNewElement = existingElement == null
            val textElement = existingElement ?: DOMProvider.document.createElement("span")

            textElement.setAttribute("class", "summon-text")
            textElement.setAttribute("data-text", text)

            // Set text content using DOM API
            val elementId = DOMProvider.getNativeElementId(textElement)
            wasmSetElementTextContent(elementId, text)

            // Apply modifier styles and attributes
            applyModifierToElement(textElement, modifier)

            // Only append if it's a NEW element - reused elements are already in the DOM
            if (isNewElement) {
                wasmConsoleLog("Appending NEW Text element $elementId to container")
                appendToCurrentContainer(textElement)
            } else {
                wasmConsoleLog("Skipping append for REUSED Text element $elementId")
            }

        } catch (e: Exception) {
            wasmConsoleError("Failed to render text: $text - ${e.message}")
            // Fallback to console log for now
            wasmConsoleLog("PlatformRenderer renderText: $text - WASM fallback")
        }
    }

    actual open fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
        wasmConsoleLog("PlatformRenderer renderLabel: $text - WASM stub")
    }

    actual open fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        try {
            wasmConsoleLog("renderButton called, onClick = $onClick")
            // Check for hydration markers in modifier
            val summonId = modifier.attributes?.get("data-summon-id")

            // Create or reuse button element
            val existingElement = createOrReuseElement("button", summonId)
            val isNewElement = existingElement == null
            val buttonElement = existingElement ?: DOMProvider.document.createElement("button")

            buttonElement.setAttribute("class", "summon-button")
            buttonElement.setAttribute("type", "button")

            // Set up click event handler with hydration support
            val wrappedOnClick = {
                wasmConsoleLog("Button onClick wrapper called")
                onClick()
                wasmConsoleLog("Button onClick completed")
            }
            attachEventListenerWithHydration(buttonElement, "click", wrappedOnClick)

            // Apply modifier styles and attributes
            applyModifierToElement(buttonElement, modifier)

            // Set up content rendering context
            withContainerContext(buttonElement) {
                val contentScope = createFlowContentCompat()
                content(contentScope)
            }

            // Only append if it's a NEW element - reused elements are already in the DOM
            if (isNewElement) {
                val elementId = DOMProvider.getNativeElementId(buttonElement)
                wasmConsoleLog("Appending NEW Button element $elementId to container")
                appendToCurrentContainer(buttonElement)
            } else {
                val elementId = DOMProvider.getNativeElementId(buttonElement)
                wasmConsoleLog("Skipping append for REUSED Button element $elementId")
            }

        } catch (e: Exception) {
            wasmConsoleError("Failed to render button - ${e.message}")
            wasmConsoleLog("PlatformRenderer renderButton - WASM fallback")
        }
    }

    actual open fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {
        try {
            // Check for hydration markers in modifier
            val summonId = modifier.attributes?.get("data-summon-id")

            // Create or reuse input element
            val existingElement = createOrReuseElement("input", summonId)
            val isNewElement = existingElement == null
            val inputElement = existingElement ?: DOMProvider.document.createElement("input")

            inputElement.setAttribute("class", "summon-textfield")
            inputElement.setAttribute("type", type)

            // Set value only if different from current DOM value to preserve typing
            val elementId = DOMProvider.getNativeElementId(inputElement)
            val currentValue = wasmGetElementValue(elementId) ?: ""
            if (currentValue != value) {
                wasmSetElementValue(elementId, value)
            }

            // Set up value change event handler with hydration support
            attachEventListenerWithHydration(inputElement, "input") {
                try {
                    val newValue = wasmGetElementValue(elementId) ?: ""
                    onValueChange(newValue)
                } catch (e: Exception) {
                    wasmConsoleError("TextField value change handler failed: ${e.message}")
                }
            }

            // Apply modifier styles and attributes
            applyModifierToElement(inputElement, modifier)

            // Only append if it's a NEW element - reused elements are already in the DOM
            if (isNewElement) {
                wasmConsoleLog("Appending NEW TextField element $elementId to container")
                appendToCurrentContainer(inputElement)
            } else {
                wasmConsoleLog("Skipping append for REUSED TextField element $elementId")
            }

        } catch (e: Exception) {
            wasmConsoleError("Failed to render text field: $value - ${e.message}")
            wasmConsoleLog("PlatformRenderer renderTextField: $value - WASM fallback")
        }
    }

    actual open fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        modifier: Modifier
    ) {
        wasmConsoleLog("PlatformRenderer renderSelect - WASM stub")
    }

    actual open fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        min: LocalDate?,
        max: LocalDate?,
        modifier: Modifier
    ) {
        wasmConsoleLog("PlatformRenderer renderDatePicker - WASM stub")
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
        wasmConsoleLog("PlatformRenderer renderTextArea: $value - WASM stub")
    }

    actual open fun addHeadElement(content: String) {
        wasmConsoleLog("PlatformRenderer addHeadElement: $content - WASM stub")
    }

    actual open fun getHeadElements(): List<String> {
        wasmConsoleLog("PlatformRenderer getHeadElements - WASM stub")
        return emptyList()
    }

    actual open fun renderHeadElements(builder: code.yousef.summon.seo.HeadScope.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderHeadElements - WASM stub")
        // For now, just log the head elements that would be rendered
        val headScope = code.yousef.summon.seo.DefaultHeadScope { element ->
            wasmConsoleLog("Head element: $element")
        }
        headScope.builder()
    }

    actual open fun renderComposableRoot(composable: @Composable () -> Unit): String {
        wasmConsoleLog("PlatformRenderer renderComposableRoot - WASM stub")
        return "<div>WASM Stub Content</div>"
    }

    actual open fun renderComposableRootWithHydration(composable: @Composable () -> Unit): String {
        wasmConsoleLog("PlatformRenderer renderComposableRootWithHydration - WASM stub")
        return "<div>WASM Stub Content with Hydration</div>"
    }

    actual open fun hydrateComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
        try {
            wasmConsoleLog("Starting WASM hydration for root element: $rootElementId")

            // Step 1: Read hydration data from server
            val hydrationData = readHydrationData()
            wasmConsoleLog("Hydration data loaded: $hydrationData")

            // Step 2: Find the root element to hydrate
            val rootElement = DOMProvider.document.getElementById(rootElementId)
            if (rootElement == null) {
                wasmConsoleError("Root element not found: $rootElementId")
                return
            }

            // Step 3: Set up hydration context
            setRootContainer(rootElement)
            isHydrating = true
            existingElements.clear()

            // Step 4: Scan existing DOM tree for hydration markers
            scanForHydrationMarkers(rootElement)
            wasmConsoleLog("Found ${existingElements.size} elements with hydration markers")

            // Step 5: Restore component state from server data
            if (hydrationData.isNotEmpty()) {
                restoreServerState(hydrationData)
            }

            // Step 6: Execute composable with hydration mode
            withContainerContext(rootElement) {
                val composer = createComposer()
                try {
                    composer.compose {
                        composable()
                    }
                } finally {
                    isHydrating = false
                }
            }

            // Step 7: Reattach event listeners to existing elements
            reattachEventListeners()

            // Step 8: Mark hydration as complete
            markHydrationComplete(rootElementId)

            wasmConsoleLog("WASM hydration completed successfully")

        } catch (e: Exception) {
            wasmConsoleError("WASM hydration failed: ${e.message}")
            // Fallback: continue with normal rendering
            isHydrating = false
            renderComposableInElement(rootElementId, composable)
        }
    }

    actual open fun renderComposable(composable: @Composable () -> Unit) {
        wasmConsoleLog("PlatformRenderer renderComposable - WASM stub")
    }

    actual open fun renderRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        try {
            // Check for hydration markers in modifier
            val summonId = modifier.attributes?.get("data-summon-id")

            // Create or reuse row element
            val existingElement = createOrReuseElement("div", summonId)
            val isNewElement = existingElement == null
            val rowElement = existingElement ?: DOMProvider.document.createElement("div")

            rowElement.setAttribute("class", "summon-row")

            // Apply CSS flexbox for horizontal layout
            val elementId = DOMProvider.getNativeElementId(rowElement)
            applyFlexboxLayout(elementId, "row")

            // Apply modifier styles and attributes
            applyModifierToElement(rowElement, modifier)

            // Set up content rendering context
            withContainerContext(rowElement) {
                val contentScope = createFlowContentCompat()
                content(contentScope)
            }

            // Only append if it's a NEW element - reused elements are already in the DOM
            if (isNewElement) {
                wasmConsoleLog("Appending NEW Row element $elementId to container")
                appendToCurrentContainer(rowElement)
            } else {
                wasmConsoleLog("Skipping append for REUSED Row element $elementId")
            }

        } catch (e: Exception) {
            wasmConsoleError("Failed to render row - ${e.message}")
            wasmConsoleLog("PlatformRenderer renderRow - WASM fallback")
        }
    }

    actual open fun renderColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        try {
            // Check for hydration markers in modifier
            val summonId = modifier.attributes?.get("data-summon-id")

            // Create or reuse column element
            val existingElement = createOrReuseElement("div", summonId)
            val isNewElement = existingElement == null
            val columnElement = existingElement ?: DOMProvider.document.createElement("div")

            columnElement.setAttribute("class", "summon-column")

            // Apply CSS flexbox for vertical layout
            val elementId = DOMProvider.getNativeElementId(columnElement)
            applyFlexboxLayout(elementId, "column")

            // Apply modifier styles and attributes
            applyModifierToElement(columnElement, modifier)

            // Set up content rendering context
            withContainerContext(columnElement) {
                val contentScope = createFlowContentCompat()
                content(contentScope)
            }

            // Only append if it's a NEW element - reused elements are already in the DOM
            if (isNewElement) {
                wasmConsoleLog("Appending NEW Column element $elementId to container")
                appendToCurrentContainer(columnElement)
            } else {
                wasmConsoleLog("Skipping append for REUSED Column element $elementId")
            }

        } catch (e: Exception) {
            wasmConsoleError("Failed to render column - ${e.message}")
            wasmConsoleLog("PlatformRenderer renderColumn - WASM fallback")
        }
    }

    actual open fun renderBox(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderBox - WASM stub")
    }

    // Additional required methods - stub implementations
    actual open fun renderImage(src: String, alt: String?, modifier: Modifier) {
        wasmConsoleLog("PlatformRenderer renderImage: $src - WASM stub")
    }

    actual open fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    ) {
        wasmConsoleLog("PlatformRenderer renderIcon: $name - WASM stub")
    }

    actual open fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderAlertContainer - WASM stub")
    }

    actual open fun renderBadge(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderBadge - WASM stub")
    }

    actual open fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        wasmConsoleLog("PlatformRenderer renderCheckbox: $checked - WASM stub")
    }

    actual open fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        label: String?,
        modifier: Modifier
    ) {
        wasmConsoleLog("PlatformRenderer renderCheckbox with label: $checked - WASM stub")
    }

    actual open fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
        wasmConsoleLog("PlatformRenderer renderProgress: $value - WASM stub")
    }

    actual open fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        wasmConsoleLog("PlatformRenderer renderFileUpload - WASM stub")
        return { wasmConsoleLog("File upload callback - WASM stub") }
    }

    actual open fun renderForm(
        onSubmit: (() -> Unit)?,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderForm - WASM stub")
    }

    actual open fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderFormField - WASM stub")
    }

    actual open fun renderRadioButton(selected: Boolean, onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {
        wasmConsoleLog("PlatformRenderer renderRadioButton: $selected - WASM stub")
    }

    actual open fun renderRadioButton(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        label: String?,
        enabled: Boolean,
        modifier: Modifier
    ) {
        wasmConsoleLog("PlatformRenderer renderRadioButton with label: $checked - WASM stub")
    }

    actual open fun renderSpacer(modifier: Modifier) {
        wasmConsoleLog("PlatformRenderer renderSpacer - WASM stub")
    }

    actual open fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        wasmConsoleLog("PlatformRenderer renderRangeSlider - WASM stub")
    }

    actual open fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        wasmConsoleLog("PlatformRenderer renderSlider: $value - WASM stub")
    }

    actual open fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        wasmConsoleLog("PlatformRenderer renderSwitch: $checked - WASM stub")
    }

    actual open fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {
        wasmConsoleLog("PlatformRenderer renderTimePicker - WASM stub")
    }

    actual open fun renderAspectRatio(
        ratio: Float,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderAspectRatio: $ratio - WASM stub")
    }

    actual open fun renderCard(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderCard - WASM stub")
    }

    actual open fun renderCard(modifier: Modifier, elevation: Int, content: @Composable () -> Unit) {
        wasmConsoleLog("PlatformRenderer renderCard with elevation: $elevation - WASM stub")
    }

    // Additional missing methods
    actual open fun renderLink(href: String, modifier: Modifier) {
        wasmConsoleLog("PlatformRenderer renderLink - WASM stub")
    }

    actual open fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {
        wasmConsoleLog("PlatformRenderer renderLink - WASM stub")
    }

    actual open fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier
    ) {
        wasmConsoleLog("PlatformRenderer renderEnhancedLink - WASM stub")
    }

    actual open fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    ) {
        wasmConsoleLog("PlatformRenderer renderTabLayout - WASM stub")
    }

    actual open fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit) {
        wasmConsoleLog("PlatformRenderer renderTabLayout - WASM stub")
    }

    actual open fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderTabLayout - WASM stub")
    }

    actual open fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        wasmConsoleLog("PlatformRenderer renderAnimatedVisibility - WASM stub")
    }

    actual open fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit) {
        wasmConsoleLog("PlatformRenderer renderAnimatedVisibility - WASM stub")
    }

    actual open fun renderAnimatedContent(modifier: Modifier) {
        wasmConsoleLog("PlatformRenderer renderAnimatedContent - WASM stub")
    }

    actual open fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit) {
        wasmConsoleLog("PlatformRenderer renderAnimatedContent - WASM stub")
    }

    actual open fun renderBlock(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderBlock - WASM stub")
    }

    actual open fun renderInline(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderInline - WASM stub")
    }

    actual open fun renderDiv(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        try {
            // Check for hydration markers in modifier
            val summonId = modifier.attributes?.get("data-summon-id")

            // Create or reuse div element
            val existingElement = createOrReuseElement("div", summonId)
            val isNewElement = existingElement == null
            val divElement = existingElement ?: DOMProvider.document.createElement("div")

            divElement.setAttribute("class", "summon-div")

            // Apply modifier styles and attributes
            applyModifierToElement(divElement, modifier)

            // Set up content rendering context
            withContainerContext(divElement) {
                val contentScope = createFlowContentCompat()
                content(contentScope)
            }

            // Only append if it's a NEW element - reused elements are already in the DOM
            if (isNewElement) {
                val elementId = DOMProvider.getNativeElementId(divElement)
                wasmConsoleLog("Appending NEW Div element $elementId to container")
                appendToCurrentContainer(divElement)
            } else {
                val elementId = DOMProvider.getNativeElementId(divElement)
                wasmConsoleLog("Skipping append for REUSED Div element $elementId")
            }

        } catch (e: Exception) {
            wasmConsoleError("Failed to render div - ${e.message}")
            wasmConsoleLog("PlatformRenderer renderDiv - WASM fallback")
        }
    }

    actual open fun renderSpan(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderSpan - WASM stub")
    }

    actual open fun renderDivider(modifier: Modifier) {
        wasmConsoleLog("PlatformRenderer renderDivider - WASM stub")
    }

    actual open fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderExpansionPanel - WASM stub")
    }

    actual open fun renderGrid(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderGrid - WASM stub")
    }

    actual open fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderLazyColumn - WASM stub")
    }

    actual open fun renderLazyRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderLazyRow - WASM stub")
    }

    actual open fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderResponsiveLayout - WASM stub")
    }

    actual open fun renderHtmlTag(
        tagName: String,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderHtmlTag - WASM stub")
    }

    actual open fun renderSnackbar(message: String, actionLabel: String?, onAction: (() -> Unit)?) {
        wasmConsoleLog("PlatformRenderer renderSnackbar - WASM stub")
    }

    actual open fun renderDropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderDropdownMenu - WASM stub")
    }

    actual open fun renderTooltip(text: String, modifier: Modifier, content: @Composable () -> Unit) {
        wasmConsoleLog("PlatformRenderer renderTooltip - WASM stub")
    }

    actual open fun renderModal(
        visible: Boolean,
        onDismissRequest: () -> Unit,
        title: String?,
        content: @Composable () -> Unit,
        actions: @Composable (() -> Unit)?
    ) {
        wasmConsoleLog("PlatformRenderer renderModal - WASM stub")
    }

    actual open fun renderModal(
        onDismiss: () -> Unit,
        modifier: Modifier,
        variant: ModalVariant,
        size: ModalSize,
        dismissOnBackdropClick: Boolean,
        showCloseButton: Boolean,
        header: @Composable (() -> Unit)?,
        footer: @Composable (() -> Unit)?,
        content: @Composable () -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderModal complex - WASM stub")
    }

    actual open fun renderScreen(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        wasmConsoleLog("PlatformRenderer renderScreen - WASM stub")
    }

    actual open fun renderHtml(htmlContent: String, modifier: Modifier) {
        wasmConsoleLog("PlatformRenderer renderHtml - WASM stub")
    }

    actual open fun renderHtml(htmlContent: String, modifier: Modifier, sanitize: Boolean) {
        wasmConsoleLog("PlatformRenderer renderHtml - WASM stub")
    }

    actual open fun renderGlobalStyle(css: String) {
        wasmConsoleLog("PlatformRenderer renderGlobalStyle - WASM stub")
    }

    actual open fun renderSurface(modifier: Modifier, elevation: Int, content: @Composable () -> Unit) {
        wasmConsoleLog("PlatformRenderer renderSurface - WASM stub")
    }

    actual open fun renderSwipeToDismiss(
        state: Any,
        background: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderSwipeToDismiss - WASM stub")
    }

    actual open fun renderVerticalPager(
        count: Int,
        state: Any,
        modifier: Modifier,
        content: @Composable (Int) -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderVerticalPager - WASM stub")
    }

    actual open fun renderHorizontalPager(
        count: Int,
        state: Any,
        modifier: Modifier,
        content: @Composable (Int) -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderHorizontalPager - WASM stub")
    }

    actual open fun renderAspectRatioContainer(ratio: Float, modifier: Modifier, content: @Composable () -> Unit) {
        wasmConsoleLog("PlatformRenderer renderAspectRatioContainer - WASM stub")
    }

    actual open fun renderFilePicker(
        onFilesSelected: (List<FileInfo>) -> Unit,
        enabled: Boolean,
        multiple: Boolean,
        accept: String?,
        modifier: Modifier,
        actions: @Composable (() -> Unit)?
    ) {
        wasmConsoleLog("PlatformRenderer renderFilePicker - WASM stub")
    }

    actual open fun renderAlert(
        message: String,
        variant: AlertVariant,
        modifier: Modifier,
        title: String?,
        icon: @Composable (() -> Unit)?,
        actions: @Composable (() -> Unit)?
    ) {
        wasmConsoleLog("PlatformRenderer renderAlert - WASM stub")
    }

    actual open fun renderLinearProgressIndicator(progress: Float?, modifier: Modifier, type: ProgressType) {
        wasmConsoleLog("PlatformRenderer renderLinearProgressIndicator - WASM stub")
    }

    actual open fun renderCircularProgressIndicator(progress: Float?, modifier: Modifier, type: ProgressType) {
        wasmConsoleLog("PlatformRenderer renderCircularProgressIndicator - WASM stub")
    }

    actual open fun renderModalBottomSheet(
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderModalBottomSheet - WASM stub")
    }

    actual open fun renderAlertDialog(
        onDismissRequest: () -> Unit,
        confirmButton: @Composable () -> Unit,
        modifier: Modifier,
        dismissButton: @Composable (() -> Unit)?,
        icon: @Composable (() -> Unit)?,
        title: @Composable (() -> Unit)?,
        text: @Composable (() -> Unit)?
    ) {
        wasmConsoleLog("PlatformRenderer renderAlertDialog - WASM stub")
    }

    actual open fun renderBoxContainer(modifier: Modifier, content: @Composable () -> Unit) {
        wasmConsoleLog("PlatformRenderer renderBoxContainer - WASM stub")
    }

    actual open fun renderLoading(
        modifier: Modifier,
        variant: LoadingVariant,
        size: LoadingSize,
        text: String?,
        textModifier: Modifier
    ) {
        wasmConsoleLog("PlatformRenderer renderLoading - WASM stub")
    }

    actual open fun renderToast(toast: ToastData, onDismiss: () -> Unit, modifier: Modifier) {
        wasmConsoleLog("PlatformRenderer renderToast - WASM stub")
    }

    // WASM DOM Helper Functions
    // These provide the infrastructure needed for actual DOM manipulation

    private val containerStack = mutableListOf<DOMElement>()
    private var rootContainer: DOMElement? = null

    // Hydration-specific state
    private var isHydrating = false
    private val existingElements = mutableMapOf<String, DOMElement>() // data-summon-id -> element
    private val pendingEventListeners = mutableListOf<EventListenerInfo>()

    // Recomposition element cache for preserving DOM elements across recompositions
    private val recompositionElements = mutableMapOf<String, DOMElement>() // data-summon-id -> element
    // Event listener tracking to prevent duplicates during recomposition
    private val attachedEventListeners = mutableSetOf<String>() // "${elementId}-${eventType}"
    private var serverState: Map<String, Any> = emptyMap()

    data class EventListenerInfo(
        val elementId: String,
        val eventType: String,
        val handler: () -> Unit
    )

    /**
     * Apply Modifier styles and attributes to a DOM element.
     */
    private fun applyModifierToElement(element: DOMElement, modifier: Modifier) {
        try {
            val elementId = DOMProvider.getNativeElementId(element)

            // Extract CSS properties from modifier (this would be enhanced with actual modifier parsing)
            // For now, we'll apply basic styling
            modifier.toString().takeIf { it.isNotBlank() }?.let { modifierString ->
                wasmSetElementAttribute(elementId, "data-modifier", modifierString)
            }

            // Apply common Summon styles
            wasmAddClassToElement(elementId, "summon-component")

        } catch (e: Exception) {
            wasmConsoleError("Failed to apply modifier: ${e.message}")
        }
    }

    /**
     * Apply flexbox layout styles to an element.
     */
    private fun applyFlexboxLayout(elementId: String, direction: String) {
        try {
            // Apply flexbox CSS styles using style attribute
            val flexStyles = when (direction) {
                "row" -> "display: flex; flex-direction: row; align-items: center;"
                "column" -> "display: flex; flex-direction: column;"
                else -> "display: flex;"
            }

            val currentStyle = wasmGetElementAttribute(elementId, "style") ?: ""
            val newStyle = "$currentStyle $flexStyles"
            wasmSetElementAttribute(elementId, "style", newStyle)

        } catch (e: Exception) {
            wasmConsoleError("Failed to apply flexbox layout: ${e.message}")
        }
    }

    /**
     * Execute code within a container context for nested rendering.
     */
    private inline fun withContainerContext(container: DOMElement, block: () -> Unit) {
        containerStack.add(container)
        try {
            block()
        } finally {
            if (containerStack.isNotEmpty()) {
                containerStack.removeLastOrNull()
            }
        }
    }

    /**
     * Append an element to the current container context.
     */
    private fun appendToCurrentContainer(element: DOMElement) {
        try {
            val elementId = DOMProvider.getNativeElementId(element)
            val currentContainer = containerStack.lastOrNull() ?: rootContainer

            if (currentContainer != null) {
                val containerId = DOMProvider.getNativeElementId(currentContainer)

                // Check if element is already placed in this recomposition pass
                if (placedElements.contains(elementId)) {
                    wasmConsoleLog("Element $elementId already placed in this recomposition, skipping")
                    return // Skip - already placed
                }

                // Check if element is already correctly positioned
                val parent = wasmGetElementParent(elementId)
                if (parent == containerId) {
                    // Element is already in the right container - don't move it
                    placedElements.add(elementId)
                    wasmConsoleLog("Element $elementId already in correct container $containerId, marking as placed")
                    return
                }

                // Element needs to be appended/moved
                wasmConsoleLog("Appending element $elementId to container $containerId")
                currentContainer.appendChild(element)
                placedElements.add(elementId)
            } else {
                // If no container context, append to document body
                DOMProvider.document.body?.appendChild(element)
                placedElements.add(elementId)
            }
        } catch (e: Exception) {
            wasmConsoleError("Failed to append to container: ${e.message}")
        }
    }

    /**
     * Create a FlowContentCompat scope for content rendering.
     */
    private fun createFlowContentCompat(): FlowContentCompat {
        return object : FlowContentCompat() {
            // Minimal implementation for content rendering context
            // This would be enhanced with full FlowContentCompat implementation
        }
    }

    /**
     * Set the root container for rendering.
     */
    fun setRootContainer(container: DOMElement) {
        rootContainer = container
    }

    /**
     * Initialize the WASM renderer with a root element.
     */
    fun initialize(rootElementId: String = "app") {
        try {
            val rootElement = DOMProvider.document.getElementById(rootElementId)
                ?: DOMProvider.document.createElement("div").also { div ->
                    div.setAttribute("id", rootElementId)
                    DOMProvider.document.body?.appendChild(div)
                }

            setRootContainer(rootElement)
            wasmConsoleLog("WASM PlatformRenderer initialized with root: $rootElementId")

        } catch (e: Exception) {
            wasmConsoleError("Failed to initialize WASM renderer: ${e.message}")
        }
    }

    /**
     * Mount a composable root to a specific DOM element.
     * This is the main entry point for WASM applications.
     */
    fun mountComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
        try {
            wasmConsoleLog("Mounting composable root to element: $rootElementId")

            // Find the root element by ID
            val elementId = wasmGetElementById(rootElementId)
                ?: throw WasmDOMException("Root element not found: $rootElementId")

            // Create a DOM element wrapper
            val rootElement = DOMProvider.createElementFromNative(elementId)

            // Set as root container
            setRootContainer(rootElement)

            // Only clear content on initial mount, not on recomposition
            if (isInitialMount) {
                wasmSetElementInnerHTML(elementId, "")
                isInitialMount = false
            }

            // Ensure we're NOT in hydration mode for fresh rendering
            isHydrating = false
            wasmConsoleLog("Hydration mode set to: $isHydrating")

            // Make sure the platform renderer is set globally
            setPlatformRenderer(this)

            // Provide the LocalPlatformRenderer value for the composition
            LocalPlatformRenderer.provides(this)

            // Get the Recomposer and create a Composer for this composition
            val recomposer = RecomposerHolder.current()
            val composer = recomposer.createComposer()

            // Store for recomposition
            mainComposer = composer
            mainRootElement = rootElement

            // Create a wrapped composable that includes the container context
            val wrappedComposable: @Composable () -> Unit = {
                // Clear placement tracking for new recomposition pass
                placedElements.clear()

                // Swap element sets for proper tracking
                previousCompositionElements.clear()
                previousCompositionElements.addAll(currentCompositionElements)
                currentCompositionElements.clear()

                // Set the root container for recomposition
                setRootContainer(mainRootElement!!)

                // Render within the container context
                withContainerContext(mainRootElement!!) {
                    composable()
                }

                // Remove elements that are no longer in the composition
                val elementsToRemove = previousCompositionElements - currentCompositionElements
                for (elementId in elementsToRemove) {
                    // Remove from cache
                    recompositionElements.remove(elementId)
                    // Remove event listeners
                    val elementNativeId = recompositionElements[elementId]?.let {
                        DOMProvider.getNativeElementId(it)
                    }
                    if (elementNativeId != null) {
                        attachedEventListeners.removeAll { it.startsWith("$elementNativeId-") }
                    }
                    wasmConsoleLog("Removing unused element: $elementId")
                }
            }

            // Store the wrapped composable as the composition root for recomposition
            recomposer.setCompositionRoot(wrappedComposable)

            // Set the active composer so state reads are tracked
            recomposer.setActiveComposer(composer)

            // Render the composable tree with proper composition context
            withContainerContext(rootElement) {
                try {
                    // Start the composition
                    composer.startGroup("root")
                    composable()
                    composer.endGroup()
                } finally {
                    // Clear the active composer after composition
                    recomposer.setActiveComposer(null)
                }
            }

            wasmConsoleLog("Composable root mounted successfully with proper composition context")
        } catch (e: Exception) {
            wasmConsoleError("Failed to mount composable root: ${e.message}")
            throw e
        }
    }

    // ================================================================================================
    // HYDRATION SUPPORT METHODS
    // ================================================================================================

    /**
     * Read hydration data serialized by the server.
     */
    private fun readHydrationData(): String {
        try {
            val scriptElement = wasmQuerySelectorGetId("script#summon-hydration-data")
            return if (scriptElement != null) {
                wasmGetElementTextContent(scriptElement) ?: ""
            } else {
                wasmConsoleWarn("Hydration data script not found")
                ""
            }
        } catch (e: Exception) {
            wasmConsoleError("Failed to read hydration data: ${e.message}")
            return ""
        }
    }

    /**
     * Scan existing DOM tree for elements with hydration markers.
     */
    private fun scanForHydrationMarkers(rootElement: DOMElement) {
        try {
            val rootElementId = DOMProvider.getNativeElementId(rootElement)

            // Find all elements with data-summon-id attributes
            val elementsWithMarkers = wasmQuerySelectorAllGetIds("[data-summon-id]")
            val elementIds = if (elementsWithMarkers.isNotEmpty()) {
                elementsWithMarkers.split(",").filter { it.isNotBlank() }
            } else {
                emptyList()
            }

            for (elementId in elementIds) {
                val element = DOMProvider.document.getElementById(elementId)
                if (element != null) {
                    val summonId = wasmGetElementAttribute(elementId, "data-summon-id")
                    if (summonId != null) {
                        existingElements[summonId] = element
                        wasmConsoleLog("Found existing element: $summonId -> $elementId")
                    }
                }
            }
        } catch (e: Exception) {
            wasmConsoleError("Failed to scan for hydration markers: ${e.message}")
        }
    }

    /**
     * Restore component state from server-provided data.
     */
    private fun restoreServerState(hydrationDataJson: String) {
        try {
            // Parse JSON hydration data (simplified parsing for now)
            // In a full implementation, this would use a proper JSON parser
            wasmConsoleLog("Restoring server state from: $hydrationDataJson")

            // For now, just log that we're ready to restore state
            // TODO: Implement actual JSON parsing and state restoration
            serverState = emptyMap()
        } catch (e: Exception) {
            wasmConsoleError("Failed to restore server state: ${e.message}")
        }
    }

    /**
     * Reattach event listeners to hydrated elements.
     */
    private fun reattachEventListeners() {
        try {
            wasmConsoleLog("Reattaching ${pendingEventListeners.size} event listeners")

            for (listenerInfo in pendingEventListeners) {
                val elementId = wasmGetElementById(listenerInfo.elementId)
                if (elementId != null) {
                    // Create unique handler ID for WASM event system
                    val handlerId = "handler-${elementId}-${listenerInfo.eventType}"

                    // Register the event listener using WASM APIs
                    val success = wasmAddEventListenerById(elementId, listenerInfo.eventType, handlerId)
                    if (success) {
                        wasmConsoleLog("Reattached ${listenerInfo.eventType} listener to $elementId")
                    } else {
                        wasmConsoleWarn("Failed to reattach ${listenerInfo.eventType} listener to $elementId")
                    }
                }
            }

            pendingEventListeners.clear()
        } catch (e: Exception) {
            wasmConsoleError("Failed to reattach event listeners: ${e.message}")
        }
    }

    /**
     * Mark hydration as complete and clean up.
     */
    private fun markHydrationComplete(rootElementId: String) {
        try {
            // Mark the root element as hydrated
            wasmSetElementAttribute(rootElementId, "data-hydration-ready", "true")
            wasmSetElementAttribute(rootElementId, "data-hydrated-by", "wasm")

            // Clear hydration state
            existingElements.clear()
            pendingEventListeners.clear()
            attachedEventListeners.clear()

            wasmConsoleLog("Hydration marked as complete for: $rootElementId")
        } catch (e: Exception) {
            wasmConsoleError("Failed to mark hydration complete: ${e.message}")
        }
    }

    /**
     * Enhanced element creation that checks for existing elements during hydration and recomposition.
     */
    private fun createOrReuseElement(tagName: String, summonId: String? = null): DOMElement? {
        // Track element in current composition
        if (summonId != null) {
            currentCompositionElements.add(summonId)
        }

        // Priority 1: Check recomposition cache if summonId provided
        if (summonId != null && recompositionElements.containsKey(summonId)) {
            val reusedElement = recompositionElements[summonId]
            if (reusedElement != null) {
                wasmConsoleLog("Reusing recomposition element: $summonId")
                return reusedElement
            }
        }

        // Priority 2: Check hydration cache if hydrating
        if (isHydrating && summonId != null && existingElements.containsKey(summonId)) {
            val existingElement = existingElements[summonId]
            if (existingElement != null) {
                wasmConsoleLog("Reusing hydration element: $summonId")
                return existingElement
            }
        }

        // Priority 3: Create new element
        val newElement = DOMProvider.document.createElement(tagName)

        // Cache the new element for future recompositions if summonId provided
        if (summonId != null) {
            recompositionElements[summonId] = newElement
            wasmConsoleLog("Caching new element for recomposition: $summonId")
        }

        return newElement
    }

    /**
     * Enhanced event listener attachment that works with hydration.
     */
    private fun attachEventListenerWithHydration(element: DOMElement, eventType: String, handler: () -> Unit) {
        val elementId = DOMProvider.getNativeElementId(element)
        val listenerKey = "$elementId-$eventType"
        wasmConsoleLog("attachEventListenerWithHydration called: eventType=$eventType, elementId=$elementId, isHydrating=$isHydrating")

        if (isHydrating) {
            // During hydration, queue event listeners for later reattachment
            pendingEventListeners.add(EventListenerInfo(elementId, eventType, handler))
            wasmConsoleLog("Queued event listener for hydration: $eventType on $elementId")
        } else {
            // Check if event listener already exists to prevent duplicates during recomposition
            if (attachedEventListeners.contains(listenerKey)) {
                wasmConsoleLog("Event listener already exists, skipping: $eventType on $elementId")
                return
            }

            // Register callback with the global callback registry and get the ID
            val handlerId = CallbackRegistry.registerCallback {
                try {
                    wasmConsoleLog("=== EXECUTING CALLBACK ===")
                    handler()
                } catch (e: Exception) {
                    wasmConsoleError("Event handler failed: ${e.message}")
                }
            }

            // Register the event handler with the WASM bridge
            val success = wasmAddEventHandler(elementId, eventType, handlerId)
            if (success) {
                wasmConsoleLog("Successfully registered $eventType handler for $elementId with ID: $handlerId")
                // Track that this event listener has been attached
                attachedEventListeners.add(listenerKey)
            } else {
                wasmConsoleError("Failed to register $eventType handler for $elementId")
            }
        }
    }

    /**
     * Fallback method to render composable in an element if hydration fails.
     */
    private fun renderComposableInElement(rootElementId: String, composable: @Composable () -> Unit) {
        try {
            wasmConsoleLog("Fallback rendering for element: $rootElementId")

            val rootElement = DOMProvider.document.getElementById(rootElementId)
            if (rootElement != null) {
                setRootContainer(rootElement)

                withContainerContext(rootElement) {
                    val composer = createComposer()
                    composer.compose {
                        composable()
                    }
                }
            }
        } catch (e: Exception) {
            wasmConsoleError("Fallback rendering failed: ${e.message}")
        }
    }

    /**
     * Create a basic composer for composition.
     */
    private fun createComposer(): BasicComposer {
        return BasicComposer()
    }

    /**
     * Basic composer implementation for WASM.
     */
    private class BasicComposer {
        fun compose(content: @Composable () -> Unit) {
            try {
                // Execute the composable content
                // In a full implementation, this would set up proper composition context
                content()
            } catch (e: Exception) {
                wasmConsoleError("Composition failed: ${e.message}")
            }
        }
    }
}

