package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.IconType
import codes.yousef.summon.components.foundation.TrustedCss
import codes.yousef.summon.components.foundation.TrustedHtml
import codes.yousef.summon.components.foundation.TrustedSvg
import codes.yousef.summon.components.feedback.*
import codes.yousef.summon.components.input.FileInfo
import codes.yousef.summon.components.navigation.Tab
import codes.yousef.summon.core.FlowContentCompat
import codes.yousef.summon.core.createWasmFlowContentCompat
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.toStyleStringKebabCase
import kotlinx.browser.window
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.w3c.dom.events.Event

// Since PlatformRenderer has many methods, providing stub implementations for WASM
actual open class PlatformRenderer actual constructor() {
    private val diagnostics = RendererDiagnostics { wasmConsoleError(it) }
    // Store the composer and root element for recomposition
    private var mainRootElement: DOMElement? = null
    private var isInitialMount = true
    private var focusedElementBeforeRecomposition: String? = null
    private var selectionStartBeforeRecomposition: Int? = null
    private var selectionEndBeforeRecomposition: Int? = null

    // DOM reconciliation tracking
    private val currentCompositionElements = mutableSetOf<String>()
    private val previousCompositionElements = mutableSetOf<String>()

    // Element placement tracking to prevent duplicate appendChild calls
    private val placedElements = mutableSetOf<String>()
    private val containerChildrenStack = mutableListOf<MutableList<String>>()

    // Event handler tracking
    private val eventHandlerIds = mutableMapOf<String, String>() // "${elementId}-${eventType}" -> handlerId
    private var eventHandlerCounter = 0
    private class ResponsiveSubscription(
        val listener: (Event) -> Unit,
        var active: Boolean = true
    )

    private val responsiveSubscriptions = mutableMapOf<String, ResponsiveSubscription>()

    // HTML building for server-side rendering
    private val htmlBuilder = StringBuilder()
    private val htmlStack = mutableListOf<HtmlElement>()

    // Track if we're in HTML string building mode vs DOM mode
    private var isStringRenderMode = false

    // Head elements collection
    private val headElements = mutableListOf<String>()

    private data class HtmlElement(
        val tagName: String,
        val attributes: MutableMap<String, String> = mutableMapOf(),
        val content: StringBuilder = StringBuilder()
    )

    actual open fun renderText(text: String, modifier: Modifier) {
        // Generate deterministic ID
        val key = modifier.attributes["key"]
        val sid = generateNextId("span", key)

        if (isStringRenderMode) {
            // HTML string building mode for SSR
            val modifierAttrs = buildModifierAttributes(modifier)
            val element = HtmlElement(
                tagName = "span",
                attributes = mutableMapOf(
                    "class" to "summon-text",
                    "data-text" to text,
                    "data-sid" to sid
                ).apply {
                    if (modifierAttrs.isNotEmpty()) {
                        putAll(modifierAttrs)
                    }
                }
            )
            element.content.append(escapeHtml(text))

            if (htmlStack.isNotEmpty()) {
                htmlStack.last().content.append(renderHtmlElement(element))
            } else {
                htmlBuilder.append(renderHtmlElement(element))
            }
        } else {
            // DOM rendering mode for client
            try {
                // Use generated SID
                val summonId = sid

                // Create or reuse text element (returns new element or null if reused)
                val newElement = createOrReuseElement("span", summonId)
                val isNewElement = newElement != null
                val textElement = if (newElement != null) {
                    newElement
                } else {
                    // Element is being reused - get it from the recomposition cache
                    recompositionElements[summonId]
                        ?: throw WasmDOMException("Failed to retrieve reused element: $summonId")
                }

                textElement.setAttribute("class", "summon-text")
                textElement.setAttribute("data-text", text)
                textElement.setAttribute("data-sid", sid)

                // Set text content using DOM API
                val elementId = DOMProvider.getNativeElementId(textElement)
                check(wasmSetElementTextContent(elementId, text)) { "Cannot update rendered text" }

                // Apply modifier styles and attributes
                applyModifierToElement(textElement, modifier)

                // Always append to container to ensure it's registered in the current composition
                // appendToCurrentContainer handles reused elements efficiently (skips if already in correct parent)
                appendToCurrentContainer(textElement)

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                diagnostics.failure()
                throw e
                // Fallback to console log for now
            }
        }
    }

    actual open fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
        diagnostics.unsupported()
    }

    actual open fun renderRawHtml(html: TrustedHtml) {
        if (isStringRenderMode) {
            val element = HtmlElement(
                tagName = "div",
                attributes = mutableMapOf("data-raw-html" to "true")
            )
            element.content.append(html.value)
            htmlStack.add(element)
        } else {
            try {
                val summonId = "raw-html-${html.value.hashCode()}"
                val newElement = createOrReuseElement("div", summonId)
                val div = newElement ?: recompositionElements[summonId]
                    ?: throw WasmDOMException("Failed to retrieve reused element")
                @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
                setInnerHTML(div as JsAny, html.value)
                appendToCurrentContainer(div)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                diagnostics.failure()
                throw e
            }
        }
    }

    actual open fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        // Generate deterministic ID
        val key = modifier.attributes["key"]
        val sid = generateNextId("button", key)

        if (isStringRenderMode) {
            // HTML string building mode for SSR
            val modifierAttrs = buildModifierAttributes(modifier)
            val element = HtmlElement(
                tagName = "button",
                attributes = mutableMapOf(
                    "class" to "summon-button",
                    "type" to "button",
                    "data-sid" to sid
                ).apply {
                    if (modifierAttrs.isNotEmpty()) {
                        putAll(modifierAttrs)
                    }
                }
            )

            // Push element onto stack for nested content
            htmlStack.add(element)
            // Push ID for children
            pushId(sid)
            try {
                // Render nested content
                val contentScope = createFlowContentCompat()
                content(contentScope)
            } finally {
                // Pop ID
                popId()
                // Pop element and add to parent or root
                htmlStack.removeLastOrNull()
                if (htmlStack.isNotEmpty()) {
                    htmlStack.last().content.append(renderHtmlElement(element))
                } else {
                    htmlBuilder.append(renderHtmlElement(element))
                }
            }
        } else {
            // DOM rendering mode for client
            try {
                // Use generated SID
                val summonId = sid

                // Create or reuse button element (returns new element or null if reused)
                val newElement = createOrReuseElement("button", summonId)
                val isNewElement = newElement != null
                val buttonElement = if (newElement != null) {
                    newElement
                } else {
                    // Element is being reused - get it from the recomposition cache
                    recompositionElements[summonId]
                        ?: throw WasmDOMException("Failed to retrieve reused element: $summonId")
                }

                buttonElement.setAttribute("class", "summon-button")
                buttonElement.setAttribute("type", "button")
                buttonElement.setAttribute("data-sid", sid)

                // Set up click event handler with hydration support
                val wrappedOnClick = {
                    onClick()
                }
                attachEventListenerWithHydration(buttonElement, "click", wrappedOnClick)

                // Apply modifier styles and attributes
                applyModifierToElement(buttonElement, modifier)

                // Push ID for children
                pushId(sid)

                // Set up content rendering context
                try {
                    withContainerContext(buttonElement) {
                        val contentScope = createFlowContentCompat()
                        content(contentScope)
                    }
                } finally { popId() }

                // Always append to container to ensure it's registered in the current composition
                val elementId = DOMProvider.getNativeElementId(buttonElement)
                appendToCurrentContainer(buttonElement)

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                diagnostics.failure()
                throw e
            }
        }
    }

    actual open fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {
        if (isStringRenderMode) {
            // HTML string building mode for SSR
            val summonId = modifier.attributes["data-summon-id"] ?: "textfield-${onValueChange.hashCode()}"
            val modifierAttrs = buildModifierAttributes(modifier)
            val element = HtmlElement(
                tagName = "input",
                attributes = mutableMapOf(
                    "class" to "summon-textfield",
                    "type" to type,
                    "value" to escapeHtmlAttribute(value),
                    "data-summon-id" to summonId
                ).apply {
                    if (modifierAttrs.isNotEmpty()) {
                        putAll(modifierAttrs)
                    }
                }
            )
            // Input is self-closing, no content needed

            if (htmlStack.isNotEmpty()) {
                htmlStack.last().content.append(renderHtmlElement(element))
            } else {
                htmlBuilder.append(renderHtmlElement(element))
            }
        } else {
            // DOM rendering mode for client
            try {
                // Check for hydration markers in modifier
                val summonId = modifier.attributes["data-summon-id"] ?: "textfield-${onValueChange.hashCode()}"

                // Create or reuse input element (returns new element or null if reused)
                val newElement = createOrReuseElement("input", summonId)
                val isNewElement = newElement != null
                val inputElement = if (newElement != null) {
                    newElement
                } else {
                    // Element is being reused - get it from the recomposition cache
                    recompositionElements[summonId]
                        ?: throw WasmDOMException("Failed to retrieve reused element: $summonId")
                }

                inputElement.setAttribute("class", "summon-textfield")
                inputElement.setAttribute("type", type)

                // Set value only if different from current DOM value to preserve typing
                val elementId = DOMProvider.getNativeElementId(inputElement)
                val currentValue = wasmGetElementValue(elementId) ?: ""
                if (currentValue != value) {
                    check(wasmSetElementValue(elementId, value)) { "Cannot update rendered input" }
                }

                // Set up value change event handler with hydration support
                attachEventListenerWithHydration(inputElement, "input") {
                    try {
                        val newValue = wasmGetElementValue(elementId) ?: ""
                        onValueChange(newValue)
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        diagnostics.failure()
                    }
                }

                // Apply modifier styles and attributes
                applyModifierToElement(inputElement, modifier)

                // Always append to container to ensure it's registered in the current composition
                appendToCurrentContainer(inputElement)

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                diagnostics.failure()
                throw e
            }
        }
    }

    actual open fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        modifier: Modifier
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        min: LocalDate?,
        max: LocalDate?,
        modifier: Modifier
    ) {
        diagnostics.unsupported()
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
        diagnostics.unsupported()
    }

    actual open fun addHeadElement(content: String) {
        headElements.add(content)
        if (!isStringRenderMode) {
            // If in DOM mode, try to add to the actual document head
            try {
                val headId = wasmQuerySelectorGetId("head")
                if (headId != null) {
                    val tempDiv = DOMProvider.document.createElement("div")
                    val tempId = DOMProvider.getNativeElementId(tempDiv)
                    wasmSetElementInnerHTML(tempId, content)
                    // Note: This is simplified - in production you'd parse and append properly
                }
            } catch (e: Throwable) {
                if (e is CancellationException) throw e
                // Ignore errors - expected in test environment
            }
        }
    }

    actual open fun getHeadElements(): List<String> {
        return headElements.toList()
    }

    actual open fun renderHeadElements(builder: codes.yousef.summon.seo.HeadScope.() -> Unit) {
        val headScope = codes.yousef.summon.seo.DefaultHeadScope { element ->
            addHeadElement(element)
        }
        headScope.builder()
    }

    actual open fun renderComposableRoot(composable: @Composable () -> Unit): String {
        try {

            // Switch to string rendering mode
            isStringRenderMode = true
            htmlBuilder.clear()
            htmlStack.clear()

            // Provide the renderer so composables can access it
            LocalPlatformRenderer.provides(this)

            // Create root element in HTML stack
            val rootElement = HtmlElement(
                tagName = "div",
                attributes = mutableMapOf("class" to "summon-root")
            )
            htmlStack.add(rootElement)

            try {
                // Execute the composable content in string mode
                composable()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                diagnostics.failure()
                return "<div class=\"summon-error\">Composition error</div>"
            } finally {
                // Pop root element and build final HTML
                htmlStack.clear()
                isStringRenderMode = false
            }

            // Build the final HTML string
            val finalHtml = renderHtmlElement(rootElement)

            return if (finalHtml.isNotEmpty()) {
                finalHtml
            } else {
                "<div class=\"summon-root\"><!-- Empty composition --></div>"
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            diagnostics.failure()
            return "<div class=\"summon-error\">Render error</div>"
        } finally {
            isStringRenderMode = false
        }
    }

    actual open fun renderComposableRootWithHydration(composable: @Composable () -> Unit): String {
        return renderComposableRootWithHydration(null, composable)
    }

    actual open fun renderComposableRootWithHydration(state: Any?, composable: @Composable () -> Unit): String {
        try {

            // Switch to string rendering mode with hydration markers
            isStringRenderMode = true
            htmlBuilder.clear()
            htmlStack.clear()

            // Provide the renderer so composables can access it
            LocalPlatformRenderer.provides(this)

            // Create root element with hydration markers
            val rootElement = HtmlElement(
                tagName = "div",
                attributes = mutableMapOf(
                    "class" to "summon-root",
                    "data-summon-hydration" to "enabled",
                    "data-summon-version" to "1.0"
                )
            )
            htmlStack.add(rootElement)

            // Add hydration script data
            val hydrationData = HtmlElement(
                tagName = "script",
                attributes = mutableMapOf(
                    "id" to "summon-hydration-data",
                    "type" to "application/json"
                )
            )
            val timestamp = try {
                wasmPerformanceNow().toLong()
            } catch (e: Throwable) {
                if (e is CancellationException) throw e
                // Use fallback timestamp if wasmPerformanceNow is not available (test environment)
                // Just use a static value for tests
                1234567890L
            }
            hydrationData.content.append("{\"hydratable\":true,\"timestamp\":$timestamp}")
            rootElement.content.append(renderHtmlElement(hydrationData))

            try {
                // Execute the composable content in string mode with hydration
                composable()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                diagnostics.failure()
                return "<div class=\"summon-error\" data-summon-hydration=\"error\">Composition error</div>"
            } finally {
                // Pop root element and build final HTML
                htmlStack.clear()
                isStringRenderMode = false
            }

            // Build the final HTML string with hydration markers
            val finalHtml = renderHtmlElement(rootElement)

            return if (finalHtml.isNotEmpty()) {
                finalHtml
            } else {
                "<div class=\"summon-root\" data-summon-hydration=\"enabled\"><!-- Empty composition --></div>"
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            diagnostics.failure()
            return "<div class=\"summon-error\" data-summon-hydration=\"error\">Render error</div>"
        } finally {
            isStringRenderMode = false
        }
    }

    actual open fun hydrateComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
        try {

            // Switch to DOM mode and enable hydration
            isStringRenderMode = false
            isHydrating = true

            // Make sure the platform renderer is set globally so composables use THIS instance
            setPlatformRenderer(this)
            LocalPlatformRenderer.provides(this)

            // Try to get the root element - handle gracefully if not found
            val rootElementNativeId = try {
                val id = wasmGetElementById(rootElementId)
                if (id != null) {
                    id
                } else {
                    // Create a fallback element
                    try {
                        val fallback = DOMProvider.document.createElement("div")
                        fallback.setAttribute("id", rootElementId)
                        DOMProvider.getNativeElementId(fallback)
                    } catch (createError: Throwable) {
                        if (createError is CancellationException) throw createError
                        // Can't create fallback, use dummy ID
                        "test-notfound-$rootElementId"
                    }
                }
            } catch (e: Throwable) {
                if (e is CancellationException) throw e
                // Create a fallback element in test environment
                try {
                    val fallback = DOMProvider.document.createElement("div")
                    fallback.setAttribute("id", rootElementId)
                    try {
                        DOMProvider.getNativeElementId(fallback)
                    } catch (idError: Throwable) {
                        if (idError is CancellationException) throw idError
                        // Can't get native ID, use fallback
                        "test-fallback-$rootElementId"
                    }
                } catch (fallbackError: Throwable) {
                    if (fallbackError is CancellationException) throw fallbackError
                    // Even fallback failed (test environment), use dummy ID
                    "test-root-$rootElementId"
                }
            }

            // Create DOM element wrapper
            val rootElement = try {
                DOMProvider.createElementFromNative(rootElementNativeId)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                DOMProvider.document.createElement("div")
            }

            // Store as main root for composition
            mainRootElement = rootElement

            // Scan for existing elements with hydration markers
            scanForHydrationMarkers(rootElement)

            // Read hydration data if available
            val hydrationDataJson = readHydrationData()
            if (hydrationDataJson.isNotEmpty()) {
                restoreServerState(hydrationDataJson)
            }

            // Execute the composable content for hydration
            try {
                // Set up container context for hydration
                withContainerContext(rootElement) {
                    // Provide this renderer to the composition
                    LocalPlatformRenderer.provides(this)
                    composable()
                }

                // Reattach event listeners after hydration
                reattachEventListeners()

                // Mark hydration as complete
                markHydrationComplete(rootElementNativeId)

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                diagnostics.failure()
                // Don't throw - hydration should be graceful
                // Fall back to client-side rendering
                renderComposableInElement(rootElementId, composable)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            diagnostics.failure()
            // Don't throw - hydration failures should not break the app
            // Try to render fresh content as fallback
            try {
                renderComposableInElement(rootElementId, composable)
            } catch (fallbackError: Exception) {
                if (fallbackError is CancellationException) throw fallbackError
                diagnostics.failure()
            }
        } finally {
            isHydrating = false
        }
    }

    /**
     * Helper to render composable in a specific element (client-side render fallback)
     */
    // Removed duplicate renderComposableInElement definition


    actual open fun renderComposable(composable: @Composable () -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        if (isStringRenderMode) {
            // HTML string building mode
            val modifierAttrs = buildModifierAttributes(modifier)
            val element = HtmlElement(
                tagName = "div",
                attributes = mutableMapOf(
                    "class" to "summon-row",
                    "style" to "display: flex; flex-direction: row; align-items: center;"
                ).apply {
                    if (modifierAttrs.isNotEmpty()) {
                        putAll(modifierAttrs)
                    }
                }
            )

            // Push element onto stack for nested content
            htmlStack.add(element)
            try {
                // Render nested content
                val contentScope = createFlowContentCompat()
                content(contentScope)
            } finally {
                // Pop element and add to parent or root
                htmlStack.removeLastOrNull()
                if (htmlStack.isNotEmpty()) {
                    htmlStack.last().content.append(renderHtmlElement(element))
                } else {
                    htmlBuilder.append(renderHtmlElement(element))
                }
            }
        } else {
            // DOM mode - use existing implementation
            renderRowWasmSafe(modifier) {
                // Convert extension receiver lambda to regular lambda
                val flowContent = createWasmFlowContentCompat()
                flowContent.content()
            }
        }
    }

    private fun renderRowWasmSafe(modifier: Modifier, content: @Composable () -> Unit) {
        renderContainerDom("row", "summon-row", modifier, "row", content = content)
    }

    actual open fun renderColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        if (isStringRenderMode) {
            // HTML string building mode
            val modifierAttrs = buildModifierAttributes(modifier)
            val element = HtmlElement(
                tagName = "div",
                attributes = mutableMapOf(
                    "class" to "summon-column",
                    "style" to "display: flex; flex-direction: column;"
                ).apply {
                    if (modifierAttrs.isNotEmpty()) {
                        putAll(modifierAttrs)
                    }
                }
            )

            // Push element onto stack for nested content
            htmlStack.add(element)
            try {
                // Render nested content
                val contentScope = createFlowContentCompat()
                content(contentScope)
            } finally {
                // Pop element and add to parent or root
                htmlStack.removeLastOrNull()
                if (htmlStack.isNotEmpty()) {
                    htmlStack.last().content.append(renderHtmlElement(element))
                } else {
                    htmlBuilder.append(renderHtmlElement(element))
                }
            }
        } else {
            // DOM mode - use existing implementation
            renderColumnWasmSafe(modifier) {
                // Convert extension receiver lambda to regular lambda
                val flowContent = createWasmFlowContentCompat()
                flowContent.content()
            }
        }
    }

    private fun renderColumnWasmSafe(modifier: Modifier, content: @Composable () -> Unit) {
        renderContainerDom("column", "summon-column", modifier, "column", content = content)
    }

    actual open fun renderBox(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        if (isStringRenderMode) {
            // HTML string building mode
            val modifierAttrs = buildModifierAttributes(modifier)
            val element = HtmlElement(
                tagName = "div",
                attributes = mutableMapOf(
                    "class" to "summon-box"
                ).apply {
                    if (modifierAttrs.isNotEmpty()) {
                        putAll(modifierAttrs)
                    }
                }
            )

            // Push element onto stack for nested content
            htmlStack.add(element)
            try {
                // Render nested content
                val contentScope = createFlowContentCompat()
                content(contentScope)
            } finally {
                // Pop element and add to parent or root
                htmlStack.removeLastOrNull()
                if (htmlStack.isNotEmpty()) {
                    htmlStack.last().content.append(renderHtmlElement(element))
                } else {
                    htmlBuilder.append(renderHtmlElement(element))
                }
            }
        } else {
            renderContainerDom("box", "summon-box", modifier) {
                createWasmFlowContentCompat().content()
            }
        }
    }

    // Additional required methods - stub implementations
    actual open fun renderImage(src: String, alt: String?, modifier: Modifier) {
        val imageModifier = modifier.attribute("src", src).attribute("alt", alt ?: "")
        renderContainerDom("image", "summon-image", imageModifier, elementTag = "img") {}
    }

    actual open fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: TrustedSvg?,
        type: IconType
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderBadge(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        label: String?,
        modifier: Modifier
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
        diagnostics.unsupported()
    }

    actual open fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        diagnostics.unsupported()
        return { diagnostics.unsupported() }
    }

    actual open fun renderForm(
        onSubmit: (() -> Unit)?,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderNativeInput(
        type: String,
        modifier: Modifier,
        value: String?,
        isChecked: Boolean?
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderNativeTextarea(
        modifier: Modifier,
        value: String?
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderNativeSelect(
        modifier: Modifier,
        options: List<NativeSelectOption>
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderNativeButton(
        type: String,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderRadioButton(selected: Boolean, onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {
        diagnostics.unsupported()
    }

    actual open fun renderRadioButton(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        label: String?,
        enabled: Boolean,
        modifier: Modifier
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderSpacer(modifier: Modifier) {
        diagnostics.unsupported()
    }

    actual open fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderAspectRatio(
        ratio: Float,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderCard(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderCard(modifier: Modifier, elevation: Int, content: @Composable () -> Unit) {
        diagnostics.unsupported()
    }

    // Additional missing methods
    actual open fun renderLink(href: String, modifier: Modifier) {
        renderContainerDom("link", "summon-link", modifier.attribute("href", href), elementTag = "a") {}
    }

    actual open fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {
        renderContainerDom("link", "summon-link", modifier.attribute("href", href), elementTag = "a", content = content)
    }

    actual open fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier,
        fallbackText: String?
    ) {
        val linkModifier = linkModifier(modifier, href, target, title, ariaLabel, ariaDescribedBy)
        renderContainerDom("link", "summon-link", linkModifier, elementTag = "a") {
            if (fallbackText != null) renderText(fallbackText, Modifier())
        }
    }

    actual open fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        val linkModifier = linkModifier(modifier, href, target, title, ariaLabel, ariaDescribedBy)
        renderContainerDom("link", "summon-link", linkModifier, elementTag = "a", content = content)
    }

    private fun linkModifier(
        modifier: Modifier,
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?
    ): Modifier {
        var result = modifier.attribute("href", href)
        if (target != null) result = result.attribute("target", target)
        if (title != null) result = result.attribute("title", title)
        if (ariaLabel != null) result = result.attribute("aria-label", ariaLabel)
        if (ariaDescribedBy != null) result = result.attribute("aria-describedby", ariaDescribedBy)
        return result
    }

    actual open fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        diagnostics.unsupported()
    }

    actual open fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderAnimatedContent(modifier: Modifier) {
        diagnostics.unsupported()
    }

    actual open fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderBlock(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderInline(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderContainerDom("inline", "summon-inline", modifier, elementTag = "span") {
            createWasmFlowContentCompat().content()
        }
    }

    actual open fun renderDiv(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderContainerDom("div", "summon-div", modifier) { createWasmFlowContentCompat().content() }
    }

    private fun renderContainerDom(
        identityTag: String,
        className: String,
        modifier: Modifier,
        direction: String? = null,
        elementTag: String = "div",
        content: @Composable () -> Unit
    ) {
        val sid = modifier.attributes["data-summon-id"] ?: generateNextId(identityTag, modifier.attributes["key"])
        try {
            val element = createOrReuseElement(elementTag, sid) ?: recompositionElements[sid]
                ?: throw WasmDOMException("Cannot retrieve a composition element")
            element.setAttribute("class", className)
            element.setAttribute("data-sid", sid)
            if (direction != null) applyFlexboxLayout(DOMProvider.getNativeElementId(element), direction)
            applyModifierToElement(element, modifier)
            pushId(sid)
            try { withContainerContext(element, content) } finally { popId() }
            appendToCurrentContainer(element)
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            diagnostics.failure()
            throw error
        }
    }

    actual open fun renderSpan(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderContainerDom("span", "summon-span", modifier, elementTag = "span") {
            createWasmFlowContentCompat().content()
        }
    }

    actual open fun renderDivider(modifier: Modifier) {
        diagnostics.unsupported()
    }

    actual open fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderGrid(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderLazyRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        if (isStringRenderMode) {
            // HTML string building mode
            val modifierAttrs = buildModifierAttributes(modifier)
            val element = HtmlElement(
                tagName = "div",
                attributes = mutableMapOf(
                    "class" to "summon-responsive-layout",
                    "style" to "width: 100%;"
                ).apply {
                    if (modifierAttrs.isNotEmpty()) {
                        putAll(modifierAttrs)
                    }
                }
            )

            // Push element onto stack for nested content
            htmlStack.add(element)
            try {
                // Render nested content
                val contentScope = createFlowContentCompat()
                content(contentScope)
            } finally {
                // Pop element and add to parent or root
                htmlStack.removeLastOrNull()
                if (htmlStack.isNotEmpty()) {
                    htmlStack.last().content.append(renderHtmlElement(element))
                } else {
                    htmlBuilder.append(renderHtmlElement(element))
                }
            }
        } else {
            // DOM mode
            renderResponsiveLayoutWasmSafe(modifier) {
                val flowContent = createWasmFlowContentCompat()
                flowContent.content()
            }
        }
    }

    private fun renderResponsiveLayoutWasmSafe(modifier: Modifier, content: @Composable () -> Unit) {
        try {
            // Check for hydration markers in modifier or use stable counter
            val summonId = modifier.attributes["data-summon-id"] ?: "responsive-${++columnCounter}"

            // Create or reuse element
            val newElement = createOrReuseElement("div", summonId)
            val element = newElement
                ?: (recompositionElements[summonId]
                    ?: throw WasmDOMException("Failed to retrieve reused element: $summonId"))

            // Inject styles if not present
            val styleId = "summon-responsive-styles"
            val existingStyle = DOMProvider.document.getElementById(styleId)
            if (existingStyle == null) {
                val style = DOMProvider.document.createElement("style")
                style.setAttribute("id", styleId)

                val css = """
                    [data-screen-size="SMALL"] .small-content { display: block !important; }
                    [data-screen-size="MEDIUM"] .medium-content { display: block !important; }
                    [data-screen-size="LARGE"] .large-content { display: block !important; }
                    [data-screen-size="XLARGE"] .xlarge-content { display: block !important; }
                """.trimIndent()

                wasmSetElementTextContent(DOMProvider.getNativeElementId(style), css)
                DOMProvider.document.head?.appendChild(style)
            }

            fun updateLayout() {
                val size = when {
                    window.innerWidth < 600 -> "SMALL"
                    window.innerWidth < 960 -> "MEDIUM"
                    window.innerWidth < 1280 -> "LARGE"
                    else -> "XLARGE"
                }
                element.setAttribute("data-screen-size", size)

                val elementId = DOMProvider.getNativeElementId(element)
                wasmRemoveClassFromElement(elementId, "small-screen medium-screen large-screen xlarge-screen")
                wasmAddClassToElement(elementId, "${size.lowercase()}-screen")
            }

            val elementId = DOMProvider.getNativeElementId(element)
            if (responsiveSubscriptions[elementId] == null) {
                lateinit var subscription: ResponsiveSubscription
                val listener: (Event) -> Unit = {
                    if (subscription.active) updateLayout()
                }
                subscription = ResponsiveSubscription(listener)
                window.addEventListener("resize", listener)
                responsiveSubscriptions[elementId] = subscription
            }
            updateLayout()

            // Apply modifier
            applyModifierToElement(element, modifier)

            // Content
            withContainerContext(element) {
                content()
            }

            // Append
            appendToCurrentContainer(element)

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            diagnostics.failure()
            throw e
        }
    }

    actual open fun renderHtmlTag(
        tagName: String,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        // Generate deterministic ID
        val key = modifier.attributes["key"]
        val sid = generateNextId(tagName, key)

        if (isStringRenderMode) {
            // HTML string building mode for SSR
            val modifierAttrs = buildModifierAttributes(modifier)
            val element = HtmlElement(
                tagName = tagName,
                attributes = mutableMapOf(
                    "class" to "summon-$tagName",
                    "data-sid" to sid
                ).apply {
                    if (modifierAttrs.isNotEmpty()) {
                        putAll(modifierAttrs)
                    }
                }
            )

            // Push element onto stack for nested content
            htmlStack.add(element)
            // Push ID for children
            pushId(sid)
            try {
                // Render nested content
                val contentScope = createFlowContentCompat()
                content(contentScope)
            } finally {
                // Pop ID
                popId()
                // Pop element and add to parent or root
                htmlStack.removeLastOrNull()
                if (htmlStack.isNotEmpty()) {
                    htmlStack.last().content.append(renderHtmlElement(element))
                } else {
                    htmlBuilder.append(renderHtmlElement(element))
                }
            }
        } else {
            // DOM rendering mode
            try {
                // Use generated SID
                val summonId = sid

                // Create or reuse element
                val newElement = createOrReuseElement(tagName, summonId)
                val isNewElement = newElement != null
                val htmlElement = if (newElement != null) {
                    newElement
                } else {
                    // Element is being reused - get it from the recomposition cache
                    recompositionElements[summonId]
                        ?: throw WasmDOMException("Failed to retrieve reused element: $summonId")
                }

                htmlElement.setAttribute("class", "summon-$tagName")
                htmlElement.setAttribute("data-sid", sid)

                // Apply modifier styles and attributes
                applyModifierToElement(htmlElement, modifier)

                // Push ID for children
                pushId(sid)

                // Set up content rendering context
                try {
                    withContainerContext(htmlElement) {
                        val contentScope = createFlowContentCompat()
                        content(contentScope)
                    }
                } finally { popId() }

                // Append to container
                val elementId = DOMProvider.getNativeElementId(htmlElement)
                if (isNewElement) {
                } else {
                }
                appendToCurrentContainer(htmlElement)

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                diagnostics.failure()
                throw e
            }
        }
    }

    actual open fun renderCanvas(
        modifier: Modifier,
        width: Int?,
        height: Int?,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderScriptTag(
        src: String?,
        async: Boolean,
        defer: Boolean,
        type: String?,
        modifier: Modifier,
        inlineContent: String?
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderSnackbar(message: String, actionLabel: String?, onAction: (() -> Unit)?) {
        diagnostics.unsupported()
    }

    actual open fun renderDropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderTooltip(text: String, modifier: Modifier, content: @Composable () -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderModal(
        visible: Boolean,
        onDismissRequest: () -> Unit,
        title: String?,
        content: @Composable () -> Unit,
        actions: @Composable (() -> Unit)?
    ) {
        diagnostics.unsupported()
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
        diagnostics.unsupported()
    }

    actual open fun renderScreen(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderHtml(htmlContent: TrustedHtml, modifier: Modifier) {
        diagnostics.unsupported()
    }

    actual open fun renderGlobalStyle(css: TrustedCss) {
        diagnostics.unsupported()
    }

    actual open fun renderSurface(modifier: Modifier, elevation: Int, content: @Composable () -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderSwipeToDismiss(
        state: Any,
        background: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderVerticalPager(
        count: Int,
        state: Any,
        modifier: Modifier,
        content: @Composable (Int) -> Unit
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderHorizontalPager(
        count: Int,
        state: Any,
        modifier: Modifier,
        content: @Composable (Int) -> Unit
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderAspectRatioContainer(ratio: Float, modifier: Modifier, content: @Composable () -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderFilePicker(
        onFilesSelected: (List<FileInfo>) -> Unit,
        enabled: Boolean,
        multiple: Boolean,
        accept: String?,
        modifier: Modifier,
        actions: @Composable (() -> Unit)?
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderAlert(
        message: String,
        variant: AlertVariant,
        modifier: Modifier,
        title: String?,
        icon: @Composable (() -> Unit)?,
        actions: @Composable (() -> Unit)?
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderLinearProgressIndicator(progress: Float?, modifier: Modifier, type: ProgressType) {
        diagnostics.unsupported()
    }

    actual open fun renderCircularProgressIndicator(progress: Float?, modifier: Modifier, type: ProgressType) {
        diagnostics.unsupported()
    }

    actual open fun renderModalBottomSheet(
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        diagnostics.unsupported()
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
        diagnostics.unsupported()
    }

    actual open fun renderBoxContainer(modifier: Modifier, content: @Composable () -> Unit) {
        diagnostics.unsupported()
    }

    actual open fun renderLoading(
        modifier: Modifier,
        variant: LoadingVariant,
        size: LoadingSize,
        text: String?,
        textModifier: Modifier
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderToast(toast: ToastData, onDismiss: () -> Unit, modifier: Modifier) {
        diagnostics.unsupported()
    }

    actual open fun renderRichMarkdown(markdown: String, modifier: Modifier) {
        renderText(markdown, modifier)
    }

    actual open fun renderCodeEditor(
        value: String,
        onValueChange: (String) -> Unit,
        language: String,
        readOnly: Boolean,
        modifier: Modifier
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderChart(
        type: String,
        dataJson: String,
        optionsJson: String?,
        modifier: Modifier
    ) {
        diagnostics.unsupported()
    }

    actual open fun renderSplitPane(
        orientation: String,
        modifier: Modifier,
        first: @Composable () -> Unit,
        second: @Composable () -> Unit
    ) {
        diagnostics.unsupported()
    }

    // Deterministic ID generation
    private val idStack = mutableListOf("root")
    private val childCounters = mutableListOf(mutableMapOf<String, Int>())

    private fun generateNextId(tagName: String, key: String? = null): String {
        val parentId = idStack.last()
        val counters = childCounters.last()

        val count = counters.getOrPut(tagName) { 0 } + 1
        counters[tagName] = count

        return if (key != null) {
            "$parentId/$tagName[$key]"
        } else {
            "$parentId/$tagName-$count"
        }
    }

    private fun pushId(id: String) {
        idStack.add(id)
        childCounters.add(mutableMapOf())
    }

    private fun popId() {
        if (idStack.size > 1) {
            idStack.removeAt(idStack.lastIndex)
            childCounters.removeAt(childCounters.lastIndex)
        }
    }


    private val containerStack = mutableListOf<String?>()
    private var rootContainer: String? = null

    // Stable ID counters for Row and Column elements to ensure reuse during recomposition
    private var rowCounter = 0
    private var columnCounter = 0

    /**
     * Resets element counters to ensure stable IDs across recompositions.
     * This should be called at the start of each recomposition to ensure
     * elements get the same IDs and can be reused from cache.
     */
    fun resetElementCounters() {
        rowCounter = 0
        columnCounter = 0
        idStack.clear()
        idStack.add("root")
        childCounters.clear()
        childCounters.add(mutableMapOf())
        placedElements.clear()
    }

    actual open fun startRecomposition() {
        focusedElementBeforeRecomposition = wasmGetActiveElementId()
        focusedElementBeforeRecomposition?.let { focused ->
            selectionStartBeforeRecomposition = wasmGetInputSelectionStart(focused)
            selectionEndBeforeRecomposition = wasmGetInputSelectionEnd(focused)
        }
        resetElementCounters()
    }

    actual open fun endRecomposition() {
        focusedElementBeforeRecomposition?.let { focused ->
            wasmRestoreElementFocus(
                elementId = focused,
                selectionStart = selectionStartBeforeRecomposition,
                selectionEnd = selectionEndBeforeRecomposition
            )
        }
        focusedElementBeforeRecomposition = null
        selectionStartBeforeRecomposition = null
        selectionEndBeforeRecomposition = null
    }

    // Hydration-specific state
    private var isHydrating = false

    /**
     * Manually set hydration mode.
     * Used by HydrationManager to coordinate hydration of individual components.
     */
    fun setHydrationMode(enabled: Boolean) {
        isHydrating = enabled
    }

    /**
     * Manually prepare for hydration by scanning markers and enabling hydration mode.
     * Used by HydrationManager or tests.
     */
    fun prepareForHydration(rootElementId: String) {
        try {
            val elementId = wasmGetElementById(rootElementId)
            if (elementId != null) {
                val rootElement = DOMProvider.createElementFromNative(elementId)
                scanForHydrationMarkers(rootElement)
                isHydrating = true

                // Load server state
                val stateJson = wasmGetSummonState()
                if (stateJson != null) {
                    // In a full implementation, we would parse this JSON into serverState
                    // serverState = Json.decodeFromString(stateJson)
                }

            } else {
            }
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            diagnostics.failure()
        }
    }

    /**
     * Finalize hydration by reattaching event listeners and disabling hydration mode.
     */
    fun finalizeHydration() {
        reattachEventListeners()
        isHydrating = false
    }

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
            val styleText = modifier.toStyleStringKebabCase()
            if (styleText.isNotEmpty()) {
                check(wasmSetElementStyle(elementId, styleText)) { "Cannot apply rendered style" }
            }

            modifier.attributes.forEach { (name, value) ->
                when (name.lowercase()) {
                    "disabled" -> {
                        check(wasmSetElementDisabled(elementId, true)) { "Cannot update input availability" }
                        check(wasmSetElementAttribute(elementId, name, value)) { "Cannot apply rendered attribute" }
                    }

                    else -> check(wasmSetElementAttribute(elementId, name, value)) { "Cannot apply rendered attribute" }
                }
            }

            if (modifier.eventHandlers.isNotEmpty()) {
                val disabled = modifier.attributes.containsKey("disabled")
                modifier.eventHandlers.forEach { (eventName, handler) ->
                    if (!(disabled && eventName.equals("click", ignoreCase = true))) {
                        attachEventListenerWithHydration(element, eventName.lowercase(), handler)
                    }
                }
            }

            wasmAddClassToElement(elementId, "summon-component")

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            diagnostics.failure()
            throw e
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
            check(wasmSetElementAttribute(elementId, "style", newStyle)) { "Cannot apply layout style" }

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            diagnostics.failure()
            throw e
        }
    }

    /**
     * Execute code within a container context for nested rendering.
     */
    private inline fun withContainerContext(container: DOMElement, block: () -> Unit) {
        val containerId = DOMProvider.getNativeElementId(container)
        containerStack.add(containerId)
        containerChildrenStack.add(mutableListOf())
        try {
            block()
        } finally {
            val expectedChildren = containerChildrenStack.removeLastOrNull() ?: mutableListOf()
            try { reconcileContainerChildren(containerId, expectedChildren) }
            finally { containerStack.removeLastOrNull() }
        }
    }

    /**
     * Append an element to the current container context.
     */
    private fun appendToCurrentContainer(element: DOMElement) {
        try {
            val elementId = DOMProvider.getNativeElementId(element)
            val containerId = containerStack.lastOrNull() ?: rootContainer

            if (containerId != null) {

                // Check if element is already placed in this recomposition pass
                if (placedElements.contains(elementId)) {
                    return // Skip - already placed
                }

                // Check if element is already correctly positioned
                val parent = wasmGetElementParent(elementId)
                if (parent == containerId) {
                    // Element is already in the right container - don't move it
                    recordElementPlacement(elementId)
                    return
                }

                // Element needs to be appended/moved
                // Use WASM external function directly to avoid type casting issues
                check(wasmAppendChildById(containerId, elementId)) { "Cannot append rendered element" }
                recordElementPlacement(elementId)
            } else {
                // If no container context, append to document body
                val bodyId = wasmGetElementById("body") ?: "body"
                check(wasmAppendChildById(bodyId, elementId)) { "Cannot append rendered element" }
                recordElementPlacement(elementId)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            diagnostics.failure()
            throw e
        }
    }

    private fun recordElementPlacement(elementId: String) {
        placedElements.add(elementId)
        containerChildrenStack.lastOrNull()?.let { children ->
            if (!children.contains(elementId)) {
                children.add(elementId)
            }
        }
    }

    private fun safeGetElementChildren(containerId: String): List<String> = try {
        wasmGetElementChildren(containerId)
            .split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    } catch (ignoredError: Throwable) {
        if (ignoredError is CancellationException) throw ignoredError
        emptyList()
    }

    private fun reconcileContainerChildren(containerId: String?, expectedChildren: List<String>) {
        val id = containerId ?: return
        val expectedSet = expectedChildren.toSet()
        val currentChildren = safeGetElementChildren(id)

        currentChildren.filter { it !in expectedSet }.forEach { childId ->
            cleanupEventHandlersForElement(childId)
            wasmRemoveElementById(childId)
        }

        // Moving a focused input, even within the same parent, can drop focus and
        // selection. Leave already ordered children connected and move only nodes
        // whose actual position differs from the required composition order.
        val orderedChildren = safeGetElementChildren(id).toMutableList()
        expectedChildren.forEachIndexed { index, childId ->
            if (orderedChildren.getOrNull(index) != childId) {
                val before = orderedChildren.getOrNull(index)
                val moved = if (before != null) {
                    wasmInsertBeforeById(id, childId, before)
                } else {
                    wasmAppendChildById(id, childId)
                }
                check(moved) { "Unable to reconcile rendered child" }
                orderedChildren.remove(childId)
                orderedChildren.add(index, childId)
            }
        }
    }

    private fun cleanupResponsiveSubscription(elementId: String) {
        responsiveSubscriptions.remove(elementId)?.let { subscription ->
            subscription.active = false
            window.removeEventListener("resize", subscription.listener)
        }
    }

    private fun cleanupEventHandlersForElement(elementId: String) {
        cleanupResponsiveSubscription(elementId)
        val keys = eventHandlerIds.keys.filter { it.startsWith("$elementId-") }
        for (key in keys) {
            val handlerId = eventHandlerIds.remove(key) ?: continue
            val eventType = key.substringAfterLast('-')
            try {
                wasmRemoveEventHandler(elementId, eventType, handlerId)
            } catch (ignoredError: Throwable) {
                if (ignoredError is CancellationException) throw ignoredError
                // ignore removal errors
            }
            attachedEventListeners.remove(key)
        }
    }

    private fun removeElementFromDom(summonId: String) {
        val domElement = recompositionElements[summonId] ?: existingElements[summonId]
        val nativeId = domElement?.let {
            runCatching { DOMProvider.getNativeElementId(it) }.getOrNull()
        }

        if (nativeId != null) {
            cleanupEventHandlersForElement(nativeId)
            wasmRemoveElementById(nativeId)
            placedElements.remove(nativeId)
        }

        recompositionElements.remove(summonId)
        existingElements.remove(summonId)
        currentCompositionElements.remove(summonId)
    }

    private fun generateEventHandlerId(listenerKey: String): String {
        eventHandlerCounter = (eventHandlerCounter + 1) and Int.MAX_VALUE
        return buildString {
            append("evh-")
            append(eventHandlerCounter)
            append('-')
            append(listenerKey.hashCode().toUInt().toString(16))
        }
    }

    /**
     * Create a FlowContentCompat scope for content rendering.
     */
    private fun createFlowContentCompat(): FlowContentCompat {
        return createWasmFlowContentCompat()
    }

    /**
     * Set the root container for rendering.
     */
    fun setRootContainer(container: DOMElement) {
        rootContainer = DOMProvider.getNativeElementId(container)
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

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            diagnostics.failure()
        }
    }

    /**
     * Mount a composable root to a specific DOM element.
     * This is the main entry point for WASM applications.
     */
    fun mountComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
        codes.yousef.summon.mountWasmRoot(this, rootElementId, null, composable)
    }

    internal fun mountedRoot(rootElementId: String, composable: @Composable () -> Unit): @Composable () -> Unit {
        val elementId = wasmGetElementById(rootElementId)
            ?: throw WasmDOMException("Root element not found: $rootElementId")
        val rootElement = DOMProvider.createElementFromNative(elementId)
        setRootContainer(rootElement)
        wasmSetElementInnerHTML(elementId, "")
        isInitialMount = false
        isHydrating = false
        mainRootElement = rootElement
        return {
            previousCompositionElements.clear()
            previousCompositionElements.addAll(currentCompositionElements)
            currentCompositionElements.clear()
            setRootContainer(rootElement)
            withContainerContext(rootElement) { composable() }
            (previousCompositionElements - currentCompositionElements).forEach { removeElementFromDom(it) }
        }
    }

    internal fun releaseMountedElements() {
        responsiveSubscriptions.keys.toList().forEach(::cleanupResponsiveSubscription)
        (recompositionElements.keys + existingElements.keys).toList().forEach { removeElementFromDom(it) }
        recompositionElements.clear()
        existingElements.clear()
        currentCompositionElements.clear()
        previousCompositionElements.clear()
        eventHandlerIds.clear()
        attachedEventListeners.clear()
        placedElements.clear()
        containerStack.clear()
        rootContainer = null
        mainRootElement = null
        resetElementCounters()
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
                ""
            }
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            // Expected in test environment where external functions don't exist
            return ""
        }
    }

    /**
     * Scan existing DOM tree for elements with hydration markers.
     */
    private fun scanForHydrationMarkers(rootElement: DOMElement) {
        try {
            val rootElementId = try {
                DOMProvider.getNativeElementId(rootElement)
            } catch (e: Throwable) {
                if (e is CancellationException) throw e
                // In test environment, DOMProvider might not work
                return
            }

            // Find all elements with data-summon-id attributes
            val elementsWithMarkers = try {
                wasmQuerySelectorAllGetIds("[data-summon-id]")
            } catch (e: Throwable) {
                if (e is CancellationException) throw e
                // External function not available in test
                ""
            }

            val elementIds = if (elementsWithMarkers.isNotEmpty()) {
                elementsWithMarkers.split(",").filter { it.isNotBlank() }
            } else {
                emptyList()
            }

            for (elementId in elementIds) {
                try {
                    val element = DOMProvider.document.getElementById(elementId)
                    if (element != null) {
                        val summonId = wasmGetElementAttribute(elementId, "data-summon-id")
                        if (summonId != null) {
                            existingElements[summonId] = element
                        }
                    }
                } catch (e: Throwable) {
                    if (e is CancellationException) throw e
                    // Ignore individual element errors
                }
            }
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            diagnostics.failure()
        }
    }

    /**
     * Restore component state from server-provided data.
     */
    private fun restoreServerState(hydrationDataJson: String) {
        try {
            // Parse JSON hydration data (simplified parsing for now)
            // In a full implementation, this would use a proper JSON parser

            // For now, just log that we're ready to restore state
            // TODO: Implement actual JSON parsing and state restoration
            serverState = emptyMap()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            diagnostics.failure()
        }
    }

    /**
     * Reattach event listeners to hydrated elements.
     */
    private fun reattachEventListeners() {
        try {

            for (listenerInfo in pendingEventListeners) {
                runCatching {
                    attachEventListenerInternal(listenerInfo.elementId, listenerInfo.eventType, listenerInfo.handler)
                }.onSuccess {
                }.onFailure { error ->
                }
            }

            pendingEventListeners.clear()
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            diagnostics.failure()
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

        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            diagnostics.failure()
        }
    }

    /**
     * Enhanced element creation that checks for existing elements during hydration and recomposition.
     * Returns null if element is reused (already in DOM), or the new element if created
     */
    private fun createOrReuseElement(tagName: String, summonId: String? = null): DOMElement? {
        // Generate a summonId if not provided
        val effectiveSummonId = summonId ?: "$tagName-${wasmPerformanceNow().toLong()}"

        // Track element in current composition
        currentCompositionElements.add(effectiveSummonId)

        // Priority 1: Check recomposition cache
        if (recompositionElements.containsKey(effectiveSummonId)) {
            val reusedElement = recompositionElements[effectiveSummonId]
            if (reusedElement != null) {
                try {
                    // Validate the reused element type
                    val elementType = reusedElement::class.simpleName ?: "Unknown"

                    // Validate the element by trying to get its native ID
                    DOMProvider.getNativeElementId(reusedElement)
                    return null // Return null to indicate element is reused and already in DOM
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    // CRITICAL FIX: When validation fails, immediately create new element
                    diagnostics.failure()
                    // Remove the invalid element from cache
                    recompositionElements.remove(effectiveSummonId)
                    // IMMEDIATE FIX: Don't check other caches, create fresh element right away
                    // This prevents the "bad cast" error by ensuring we always have a valid element
                    return createFreshElement(tagName, effectiveSummonId)
                }
            }
        } else {
        }

        // Priority 2: Check hydration cache if hydrating
        if (isHydrating && existingElements.containsKey(effectiveSummonId)) {
            val existingElement = existingElements[effectiveSummonId]
            if (existingElement != null) {
                try {
                    val elementType = existingElement::class.simpleName ?: "Unknown"

                    // Validate the element by trying to get its native ID
                    DOMProvider.getNativeElementId(existingElement)
                    // Move to recomposition cache for future use
                    recompositionElements[effectiveSummonId] = existingElement
                    return null // Return null to indicate element is reused and already in DOM
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    // CRITICAL FIX: When validation fails, immediately create new element
                    diagnostics.failure()
                    existingElements.remove(effectiveSummonId)
                    // IMMEDIATE FIX: Create fresh element right away
                    return createFreshElement(tagName, effectiveSummonId)
                }
            }
        }

        // Priority 3: Create new element (no cached element found)
        return createFreshElement(tagName, effectiveSummonId)
    }

    /**
     * Helper method to create a fresh DOM element and cache it.
     * Extracted to avoid code duplication and ensure consistent element creation.
     */
    private fun createFreshElement(tagName: String, effectiveSummonId: String): DOMElement {
        try {
            val newElement = DOMProvider.document.createElement(tagName)

            // Validate the newly created element
            val newElementType = newElement::class.simpleName ?: "Unknown"

            // Validate the newly created element by trying to get its native ID
            try {
                DOMProvider.getNativeElementId(newElement)
            } catch (typeError: IllegalArgumentException) {
                diagnostics.failure()
                diagnostics.failure()
                throw WasmDOMException("Element creation failed - wrong type returned: $newElementType")
            }

            // Cache the new element for future recompositions
            recompositionElements[effectiveSummonId] = newElement

            return newElement // Return the new element to be appended to DOM
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            diagnostics.failure()
            throw e
        }
    }

    /**
     * Enhanced event listener attachment that works with hydration.
     */
    private fun attachEventListenerWithHydration(element: DOMElement, eventType: String, handler: () -> Unit) {
        val elementId = DOMProvider.getNativeElementId(element)
        val listenerKey = "$elementId-$eventType"

        if (isHydrating) {
            // During hydration, queue event listeners for later reattachment
            pendingEventListeners.add(EventListenerInfo(elementId, eventType, handler))
        } else {
            attachEventListenerInternal(elementId, eventType, handler, listenerKey)
        }
    }

    private fun attachEventListenerInternal(
        elementId: String,
        eventType: String,
        handler: () -> Unit,
        listenerKey: String = "$elementId-$eventType"
    ) {
        val latestHandler = {
            try {
                handler()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                diagnostics.failure()
            }
        }
        eventHandlerIds[listenerKey]?.let { existingId ->
            registerWasmEventCallback(existingId, latestHandler)
            return
        }

        val handlerId = generateEventHandlerId(listenerKey)
        registerWasmEventCallback(handlerId, latestHandler)

        val success = wasmAddEventHandler(elementId, eventType, handlerId)
        if (success) {
            eventHandlerIds[listenerKey] = handlerId
            attachedEventListeners.add(listenerKey)
            runCatching {
                wasmSetElementAttribute(elementId, "data-summon-handler-$eventType", handlerId)
            }
        } else {
            diagnostics.failure()
            eventHandlerIds.remove(listenerKey)
        }
    }

    /**
     * Fallback method to render composable in an element if hydration fails.
     */
    private fun renderComposableInElement(rootElementId: String, composable: @Composable () -> Unit) {
        try {

            val rootElement = DOMProvider.document.getElementById(rootElementId)
            if (rootElement != null) {
                setRootContainer(rootElement)

                withContainerContext(rootElement) {
                    val composer = createComposer()
                    composer.compose {
                        // Provide this renderer to the composition
                        LocalPlatformRenderer.provides(this@PlatformRenderer)
                        composable()
                    }
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            diagnostics.failure()
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
        fun compose(content: @Composable () -> Unit) = content()
    }

    // ================================================================================================
    // HTML STRING BUILDING HELPERS
    // ================================================================================================

    /**
     * Build HTML attributes map from Modifier.
     */
    private fun buildModifierAttributes(modifier: Modifier): Map<String, String> {
        val attrs = mutableMapOf<String, String>()

        // Extract attributes from modifier if available
        modifier.attributes.forEach { (key, value) ->
            attrs[key] = value
        }

        // Convert modifier styles to style attribute if needed
        // This is simplified - in production you'd parse the modifier properly
        val modifierString = modifier.toString()
        if (modifierString.isNotEmpty() && modifierString != "Modifier") {
            attrs["data-modifier"] = modifierString
        }

        return attrs
    }

    /**
     * Render an HtmlElement to HTML string.
     */
    private fun renderHtmlElement(element: HtmlElement): String {
        val sb = StringBuilder()

        // Opening tag
        sb.append("<${element.tagName}")

        // Attributes
        element.attributes.forEach { (key, value) ->
            sb.append(" $key=\"${escapeHtmlAttribute(value)}\"")
        }

        // Self-closing tags
        if (element.tagName in listOf("input", "br", "hr", "img", "meta", "link")) {
            sb.append(" />")
        } else {
            sb.append(">")

            // Content
            sb.append(element.content.toString())

            // Closing tag
            sb.append("</${element.tagName}>")
        }

        return sb.toString()
    }

    /**
     * Escape HTML content to prevent XSS.
     */
    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    /**
     * Escape HTML attribute values.
     */
    private fun escapeHtmlAttribute(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
    }

    /**
     * Renders a menu bar as a horizontal navigation component.
     * In WASM, this generates HTML for SSR or uses innerHTML for client-side rendering.
     */
    actual open fun renderMenuBar(
        menus: List<codes.yousef.summon.desktop.menu.Menu>,
        modifier: Modifier
    ) {
        val menuBarStyles = buildString {
            append("display: flex; ")
            append("gap: 0; ")
            append("background-color: #f8f9fa; ")
            append("padding: 0 8px; ")
            append("border-bottom: 1px solid #dee2e6;")
        }

        val modifierStyles = modifier.toStyleStringKebabCase()
        val combinedStyles = if (modifierStyles.isNotEmpty()) "$modifierStyles; $menuBarStyles" else menuBarStyles

        // Build HTML string for the menu bar
        val htmlContent = buildString {
            append("<nav data-summon-component=\"menu-bar\" role=\"menubar\" style=\"$combinedStyles\">")
            menus.forEach { menu ->
                append(renderMenuToHtml(menu))
            }
            append("</nav>")
        }

        if (isStringRenderMode) {
            // String rendering mode for SSR
            if (htmlStack.isNotEmpty()) {
                htmlStack.last().content.append(htmlContent)
            } else {
                htmlBuilder.append(htmlContent)
            }
        } else {
            // Client-side: use innerHTML approach via wrapper div
            // Generate a unique ID for the wrapper
            val wrapperSid = generateNextId("menu-bar-wrapper", null)

            // Create a wrapper element and set innerHTML
            val newElement = createOrReuseElement("div", wrapperSid)
            if (newElement != null) {
                val elementId = DOMProvider.getNativeElementId(newElement)
                wasmSetElementInnerHTML(elementId, htmlContent)

                // Track for proper DOM placement
                currentCompositionElements.add(wrapperSid)
                recompositionElements[wrapperSid] = newElement

                // Placement handled by standard composition flow
            }
        }
    }

    /**
     * Renders a menu to HTML string for SSR.
     */
    private fun renderMenuToHtml(menu: codes.yousef.summon.desktop.menu.Menu): String {
        val sb = StringBuilder()
        sb.append("<div class=\"summon-menu\" style=\"position: relative;\" role=\"none\">")

        // Menu button
        val disabled = if (menu.disabled) " disabled" else ""
        sb.append("<button type=\"button\" role=\"menuitem\" aria-haspopup=\"true\" aria-expanded=\"false\"$disabled ")
        sb.append("style=\"padding: 8px 12px; background: none; border: none; cursor: pointer; font-size: 14px;\">")
        sb.append(escapeHtml(menu.label))
        sb.append("</button>")

        // Dropdown menu
        sb.append("<ul class=\"summon-menu-dropdown\" role=\"menu\" style=\"display: none; position: absolute; top: 100%; left: 0; min-width: 160px; background: white; border: 1px solid #dee2e6; border-radius: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.15); padding: 4px 0; margin: 0; list-style: none; z-index: 1000;\">")
        menu.items.forEach { item ->
            sb.append(renderMenuItemToHtml(item, 0))
        }
        sb.append("</ul>")
        sb.append("</div>")
        return sb.toString()
    }

    /**
     * Renders a menu item to HTML string.
     */
    private fun renderMenuItemToHtml(item: codes.yousef.summon.desktop.menu.MenuItem, depth: Int): String {
        val sb = StringBuilder()
        sb.append("<li role=\"none\" style=\"list-style: none;\">")

        if (item.isSeparator) {
            sb.append("<hr style=\"margin: 4px 0; border: none; border-top: 1px solid #dee2e6;\">")
        } else if (item.submenu != null) {
            sb.append("<div style=\"position: relative;\">")
            val disabled = if (item.disabled) " disabled" else ""
            sb.append("<button type=\"button\" role=\"menuitem\" aria-haspopup=\"true\" aria-expanded=\"false\"$disabled ")
            sb.append("style=\"display: flex; justify-content: space-between; width: 100%; padding: 8px 12px; background: none; border: none; cursor: pointer; text-align: left; font-size: 14px;\">")
            sb.append("<span>${escapeHtml(item.label)}</span>")
            sb.append("<span>▶</span>")
            sb.append("</button>")

            sb.append("<ul class=\"summon-submenu\" role=\"menu\" style=\"display: none; position: absolute; left: 100%; top: 0; min-width: 160px; background: white; border: 1px solid #dee2e6; border-radius: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.15); padding: 4px 0; margin: 0; list-style: none; z-index: ${1001 + depth};\">")
            item.submenu.forEach { subItem ->
                sb.append(renderMenuItemToHtml(subItem, depth + 1))
            }
            sb.append("</ul>")
            sb.append("</div>")
        } else {
            val disabled = if (item.disabled) " disabled" else ""
            val ariaChecked = item.checked?.let { " aria-checked=\"$it\"" } ?: ""
            sb.append("<button type=\"button\" role=\"menuitem\"$disabled$ariaChecked ")
            sb.append("style=\"display: flex; justify-content: space-between; width: 100%; padding: 8px 12px; background: none; border: none; cursor: pointer; text-align: left; font-size: 14px;\">")

            sb.append("<span>")
            if (item.checked == true) sb.append("✓ ")
            item.icon?.let { sb.append("$it ") }
            sb.append(escapeHtml(item.label))
            sb.append("</span>")

            item.shortcut?.let { shortcut ->
                sb.append("<span style=\"margin-left: 24px; color: #6c757d; font-size: 12px;\">")
                sb.append(escapeHtml(shortcut.toDisplayString()))
                sb.append("</span>")
            }
            sb.append("</button>")
        }

        sb.append("</li>")
        return sb.toString()
    }
}

private fun setInnerHTML(element: JsAny, html: String) {
    js("element.innerHTML = html")
}
