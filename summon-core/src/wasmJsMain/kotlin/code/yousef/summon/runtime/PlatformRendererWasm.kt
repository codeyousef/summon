package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.*
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.core.FlowContentCompat
import code.yousef.summon.core.createWasmFlowContentCompat
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
    private val containerChildrenStack = mutableListOf<MutableList<String>>()

    // Event handler tracking
    private val eventHandlerIds = mutableMapOf<String, String>() // "${elementId}-${eventType}" -> handlerId
    private var eventHandlerCounter = 0

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
        if (isStringRenderMode) {
            // HTML string building mode for SSR
            val modifierAttrs = buildModifierAttributes(modifier)
            val element = HtmlElement(
                tagName = "span",
                attributes = mutableMapOf(
                    "class" to "summon-text",
                    "data-text" to text
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
                // Check for hydration markers in modifier
                val summonId = modifier.attributes?.get("data-summon-id") ?: "text-${text.hashCode()}"

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
    }

    actual open fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
        wasmConsoleLog("PlatformRenderer renderLabel: $text - WASM stub")
    }

    actual open fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        if (isStringRenderMode) {
            // HTML string building mode for SSR
            val summonId = modifier.attributes?.get("data-summon-id") ?: "button-${onClick.hashCode()}"
            val modifierAttrs = buildModifierAttributes(modifier)
            val element = HtmlElement(
                tagName = "button",
                attributes = mutableMapOf(
                    "class" to "summon-button",
                    "type" to "button",
                    "data-summon-id" to summonId
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
            // DOM rendering mode for client
            try {
                wasmConsoleLog("renderButton called, onClick = $onClick")
                // Check for hydration markers in modifier
                val summonId = modifier.attributes?.get("data-summon-id") ?: "button-${onClick.hashCode()}"

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
    }

    actual open fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {
        if (isStringRenderMode) {
            // HTML string building mode for SSR
            val summonId = modifier.attributes?.get("data-summon-id") ?: "textfield-${onValueChange.hashCode()}"
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
                val summonId = modifier.attributes?.get("data-summon-id") ?: "textfield-${onValueChange.hashCode()}"

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
                    safeWasmConsoleLog("Added head element: $content")
                }
            } catch (e: Throwable) {
                // Ignore errors - expected in test environment
                safeWasmConsoleLog("Could not add head element in DOM mode: ${e.message}")
            }
        }
    }

    actual open fun getHeadElements(): List<String> {
        return headElements.toList()
    }

    actual open fun renderHeadElements(builder: code.yousef.summon.seo.HeadScope.() -> Unit) {
        val headScope = code.yousef.summon.seo.DefaultHeadScope { element ->
            addHeadElement(element)
        }
        headScope.builder()
    }

    actual open fun renderComposableRoot(composable: @Composable () -> Unit): String {
        try {
            safeWasmConsoleLog("PlatformRenderer renderComposableRoot - WASM implementation")

            // Switch to string rendering mode
            isStringRenderMode = true
            htmlBuilder.clear()
            htmlStack.clear()

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
                safeWasmConsoleError("Error executing composable: ${e.message}")
                return "<div class=\"summon-error\">Composition error: ${escapeHtml(e.message ?: "Unknown")}</div>"
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
            safeWasmConsoleError("renderComposableRoot failed: ${e.message}")
            return "<div class=\"summon-error\">Render error: ${escapeHtml(e.message ?: "Unknown")}</div>"
        } finally {
            isStringRenderMode = false
        }
    }

    actual open fun renderComposableRootWithHydration(composable: @Composable () -> Unit): String {
        try {
            safeWasmConsoleLog("PlatformRenderer renderComposableRootWithHydration - WASM implementation")

            // Switch to string rendering mode with hydration markers
            isStringRenderMode = true
            htmlBuilder.clear()
            htmlStack.clear()

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
                safeWasmConsoleError("Error executing composable with hydration: ${e.message}")
                return "<div class=\"summon-error\" data-summon-hydration=\"error\">Composition error: ${escapeHtml(e.message ?: "Unknown")}</div>"
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
            safeWasmConsoleError("renderComposableRootWithHydration failed: ${e.message}")
            return "<div class=\"summon-error\" data-summon-hydration=\"error\">Render error: ${escapeHtml(e.message ?: "Unknown")}</div>"
        } finally {
            isStringRenderMode = false
        }
    }

    actual open fun hydrateComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
        try {
            safeWasmConsoleLog("Starting WASM hydration for root element: $rootElementId")

            // Switch to DOM mode and enable hydration
            isStringRenderMode = false
            isHydrating = true

            // Try to get the root element - handle gracefully if not found
            val rootElementNativeId = try {
                val id = wasmGetElementById(rootElementId)
                if (id != null) {
                    safeWasmConsoleLog("Found root element for hydration: $rootElementId")
                    id
                } else {
                    safeWasmConsoleWarn("Root element $rootElementId not found, creating fallback")
                    // Create a fallback element
                    try {
                        val fallback = DOMProvider.document.createElement("div")
                        fallback.setAttribute("id", rootElementId)
                        DOMProvider.getNativeElementId(fallback)
                    } catch (createError: Throwable) {
                        // Can't create fallback, use dummy ID
                        "test-notfound-$rootElementId"
                    }
                }
            } catch (e: Throwable) {
                safeWasmConsoleWarn("Could not get root element: ${e.message}, using fallback")
                // Create a fallback element in test environment
                try {
                    val fallback = DOMProvider.document.createElement("div")
                    fallback.setAttribute("id", rootElementId)
                    try {
                        DOMProvider.getNativeElementId(fallback)
                    } catch (idError: Throwable) {
                        // Can't get native ID, use fallback
                        "test-fallback-$rootElementId"
                    }
                } catch (fallbackError: Throwable) {
                    // Even fallback failed (test environment), use dummy ID
                    "test-root-$rootElementId"
                }
            }

            // Create DOM element wrapper
            val rootElement = try {
                DOMProvider.createElementFromNative(rootElementNativeId)
            } catch (e: Exception) {
                safeWasmConsoleWarn("Could not wrap root element: ${e.message}")
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
                    composable()
                }

                // Reattach event listeners after hydration
                reattachEventListeners()

                // Mark hydration as complete
                markHydrationComplete(rootElementNativeId)

                safeWasmConsoleLog("Hydration completed successfully for $rootElementId")
            } catch (e: Exception) {
                safeWasmConsoleError("Error during hydration: ${e.message}")
                // Don't throw - hydration should be graceful
                // Fall back to client-side rendering
                safeWasmConsoleLog("Falling back to client-side rendering")
                renderComposableInElement(rootElementId, composable)
            }
        } catch (e: Exception) {
            safeWasmConsoleError("hydrateComposableRoot failed gracefully: ${e.message}")
            // Don't throw - hydration failures should not break the app
            // Try to render fresh content as fallback
            try {
                safeWasmConsoleLog("Attempting fallback client-side render")
                renderComposableInElement(rootElementId, composable)
            } catch (fallbackError: Exception) {
                safeWasmConsoleError("Fallback rendering also failed: ${fallbackError.message}")
            }
        } finally {
            isHydrating = false
        }
    }

    actual open fun renderComposable(composable: @Composable () -> Unit) {
        wasmConsoleLog("PlatformRenderer renderComposable - WASM stub")
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
        try {
            // Check for hydration markers in modifier or use stable counter
            val summonId = modifier.attributes?.get("data-summon-id") ?: "row-${++rowCounter}"

            // Create or reuse row element (returns new element or null if reused)
            val newElement = createOrReuseElement("div", summonId)
            val isNewElement = newElement != null
            val rowElement = if (newElement != null) {
                newElement
            } else {
                // Element is being reused - get it from the recomposition cache
                recompositionElements[summonId]
                    ?: throw WasmDOMException("Failed to retrieve reused element: $summonId")
            }

            // Step 1: Set class attribute
            try {
                wasmConsoleLog("Row Step 1: Setting class attribute for $summonId")
                rowElement.setAttribute("class", "summon-row")
                wasmConsoleLog("Row Step 1: SUCCESS - Class attribute set")
            } catch (e: Exception) {
                wasmConsoleError("Row Step 1: FAILED - setAttribute: ${e.message}")
                throw e
            }

            // Step 2: Get native element ID
            val elementId = try {
                wasmConsoleLog("Row Step 2: Getting native element ID for $summonId")
                val id = DOMProvider.getNativeElementId(rowElement)
                wasmConsoleLog("Row Step 2: SUCCESS - Got element ID: $id")
                id
            } catch (e: Exception) {
                wasmConsoleError("Row Step 2: FAILED - getNativeElementId: ${e.message}")
                throw e
            }

            // Step 3: Apply flexbox layout
            try {
                wasmConsoleLog("Row Step 3: Applying flexbox layout for $elementId")
                applyFlexboxLayout(elementId, "row")
                wasmConsoleLog("Row Step 3: SUCCESS - Flexbox layout applied")
            } catch (e: Exception) {
                wasmConsoleError("Row Step 3: FAILED - applyFlexboxLayout: ${e.message}")
                throw e
            }

            // Step 4: Apply modifier styles
            try {
                wasmConsoleLog("Row Step 4: Applying modifier to element $elementId")
                applyModifierToElement(rowElement, modifier)
                wasmConsoleLog("Row Step 4: SUCCESS - Modifier applied")
            } catch (e: Exception) {
                wasmConsoleError("Row Step 4: FAILED - applyModifierToElement: ${e.message}")
                throw e
            }

            // Step 5: Set up content rendering context
            try {
                wasmConsoleLog("Row Step 5: Setting up container context for $elementId")
                withContainerContext(rowElement) {
                    wasmConsoleLog("Row Step 5a: Inside container context")
                    wasmConsoleLog("Row Step 5b: Executing content block directly (no extension receiver)")
                    // Direct invocation without extension receiver to avoid WASM cast issues
                    content()
                    wasmConsoleLog("Row Step 5c: Content block completed successfully")
                }
                wasmConsoleLog("Row Step 5: SUCCESS - Container context completed")
            } catch (e: Exception) {
                wasmConsoleError("Row Step 5: FAILED - withContainerContext: ${e.message}")
                throw e
            }

            // Step 6: Append to container if new element
            if (isNewElement) {
                try {
                    wasmConsoleLog("Row Step 6: Appending NEW Row element $elementId to container")
                    appendToCurrentContainer(rowElement)
                    wasmConsoleLog("Row Step 6: SUCCESS - Element appended to container")
                } catch (e: Exception) {
                    wasmConsoleError("Row Step 6: FAILED - appendToCurrentContainer: ${e.message}")
                    throw e
                }
            } else {
                wasmConsoleLog("Row Step 6: Skipping append for REUSED Row element $elementId")
            }

        } catch (e: Exception) {
            wasmConsoleError("Failed to render row - ${e.message}")
            wasmConsoleLog("PlatformRenderer renderRow - WASM fallback")
        }
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
        try {
            // Check for hydration markers in modifier or use stable counter
            val summonId = modifier.attributes?.get("data-summon-id") ?: "column-${++columnCounter}"

            // Create or reuse column element (returns new element or null if reused)
            val newElement = createOrReuseElement("div", summonId)
            val isNewElement = newElement != null
            val columnElement = if (newElement != null) {
                newElement
            } else {
                // Element is being reused - get it from the recomposition cache
                recompositionElements[summonId]
                    ?: throw WasmDOMException("Failed to retrieve reused element: $summonId")
            }

            // Step 1: Set class attribute
            try {
                wasmConsoleLog("Column Step 1: Setting class attribute for $summonId")
                columnElement.setAttribute("class", "summon-column")
                wasmConsoleLog("Column Step 1: SUCCESS - Class attribute set")
            } catch (e: Exception) {
                wasmConsoleError("Column Step 1: FAILED - setAttribute: ${e.message}")
                throw e
            }

            // Step 2: Get native element ID
            val elementId = try {
                wasmConsoleLog("Column Step 2: Getting native element ID for $summonId")
                val id = DOMProvider.getNativeElementId(columnElement)
                wasmConsoleLog("Column Step 2: SUCCESS - Got element ID: $id")
                id
            } catch (e: Exception) {
                wasmConsoleError("Column Step 2: FAILED - getNativeElementId: ${e.message}")
                throw e
            }

            // Step 3: Apply flexbox layout
            try {
                wasmConsoleLog("Column Step 3: Applying flexbox layout for $elementId")
                applyFlexboxLayout(elementId, "column")
                wasmConsoleLog("Column Step 3: SUCCESS - Flexbox layout applied")
            } catch (e: Exception) {
                wasmConsoleError("Column Step 3: FAILED - applyFlexboxLayout: ${e.message}")
                throw e
            }

            // Step 4: Apply modifier styles
            try {
                wasmConsoleLog("Column Step 4: Applying modifier to element $elementId")
                applyModifierToElement(columnElement, modifier)
                wasmConsoleLog("Column Step 4: SUCCESS - Modifier applied")
            } catch (e: Exception) {
                wasmConsoleError("Column Step 4: FAILED - applyModifierToElement: ${e.message}")
                throw e
            }

            // Step 5: Set up content rendering context
            try {
                wasmConsoleLog("Column Step 5: Setting up container context for $elementId")
                withContainerContext(columnElement) {
                    wasmConsoleLog("Column Step 5a: Inside container context")
                    wasmConsoleLog("Column Step 5b: Executing content block directly (no extension receiver)")
                    // Direct invocation without extension receiver to avoid WASM cast issues
                    content()
                    wasmConsoleLog("Column Step 5c: Content block completed successfully")
                }
                wasmConsoleLog("Column Step 5: SUCCESS - Container context completed")
            } catch (e: Exception) {
                wasmConsoleError("Column Step 5: FAILED - withContainerContext: ${e.message}")
                throw e
            }

            // Step 6: Append to container if new element
            if (isNewElement) {
                try {
                    wasmConsoleLog("Column Step 6: Appending NEW Column element $elementId to container")
                    appendToCurrentContainer(columnElement)
                    wasmConsoleLog("Column Step 6: SUCCESS - Element appended to container")
                } catch (e: Exception) {
                    wasmConsoleError("Column Step 6: FAILED - appendToCurrentContainer: ${e.message}")
                    throw e
                }
            } else {
                wasmConsoleLog("Column Step 6: Skipping append for REUSED Column element $elementId")
            }

        } catch (e: Exception) {
            wasmConsoleError("Failed to render column - ${e.message}")
            wasmConsoleLog("PlatformRenderer renderColumn - WASM fallback")
        }
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
            // DOM mode - stub for now
            wasmConsoleLog("PlatformRenderer renderBox - WASM DOM mode stub")
            // Could implement similar to renderRow/Column if needed
        }
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

    actual open fun renderNativeInput(
        type: String,
        modifier: Modifier,
        value: String?,
        isChecked: Boolean?
    ) {
        wasmConsoleLog("PlatformRenderer renderNativeInput($type) - WASM stub")
    }

    actual open fun renderNativeTextarea(
        modifier: Modifier,
        value: String?
    ) {
        wasmConsoleLog("PlatformRenderer renderNativeTextarea - WASM stub")
    }

    actual open fun renderNativeSelect(
        modifier: Modifier,
        options: List<NativeSelectOption>
    ) {
        wasmConsoleLog("PlatformRenderer renderNativeSelect(${options.size}) - WASM stub")
    }

    actual open fun renderNativeButton(
        type: String,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderNativeButton($type) - WASM stub")
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
        modifier: Modifier,
        fallbackText: String?
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
            val divElement = if (existingElement != null) {
                existingElement
            } else {
                // Element is being reused - get it from the recomposition cache
                recompositionElements[summonId]
                    ?: throw WasmDOMException("Failed to retrieve reused element: $summonId")
            }

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

    actual open fun renderCanvas(
        modifier: Modifier,
        width: Int?,
        height: Int?,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        wasmConsoleLog("PlatformRenderer renderCanvas - WASM stub")
    }

    actual open fun renderScriptTag(
        src: String?,
        async: Boolean,
        defer: Boolean,
        type: String?,
        modifier: Modifier,
        inlineContent: String?
    ) {
        wasmConsoleLog("PlatformRenderer renderScriptTag - WASM stub")
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
        wasmConsoleLog("Reset element counters for recomposition")
    }

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
            val styleText = modifier.toStyleStringKebabCase()
            if (styleText.isNotEmpty()) {
                wasmSetElementStyle(elementId, styleText)
            }

            modifier.attributes.forEach { (name, value) ->
                when (name.lowercase()) {
                    "disabled" -> {
                        wasmSetElementDisabled(elementId, true)
                        wasmSetElementAttribute(elementId, name, value)
                    }

                    else -> wasmSetElementAttribute(elementId, name, value)
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
        val containerId = DOMProvider.getNativeElementId(container)
        containerStack.add(containerId)
        containerChildrenStack.add(mutableListOf())
        try {
            block()
        } finally {
            val expectedChildren = containerChildrenStack.removeLastOrNull() ?: mutableListOf()
            reconcileContainerChildren(containerId, expectedChildren)
            containerStack.removeLastOrNull()
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
                    wasmConsoleLog("Element $elementId already placed in this recomposition, skipping")
                    return // Skip - already placed
                }

                // Check if element is already correctly positioned
                val parent = wasmGetElementParent(elementId)
                if (parent == containerId) {
                    // Element is already in the right container - don't move it
                    recordElementPlacement(elementId)
                    wasmConsoleLog("Element $elementId already in correct container $containerId, marking as placed")
                    return
                }

                // Element needs to be appended/moved
                wasmConsoleLog("Appending element $elementId to container $containerId")
                // Use WASM external function directly to avoid type casting issues
                wasmAppendChildById(containerId, elementId)
                recordElementPlacement(elementId)
            } else {
                // If no container context, append to document body
                val bodyId = wasmGetElementById("body") ?: "body"
                wasmAppendChildById(bodyId, elementId)
                recordElementPlacement(elementId)
            }
        } catch (e: Exception) {
            wasmConsoleError("Failed to append to container: ${e.message}")
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
    } catch (_: Throwable) {
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

        expectedChildren.forEach { childId ->
            if (childId.isNotBlank()) {
                wasmAppendChildById(id, childId)
            }
        }
    }

    private fun cleanupEventHandlersForElement(elementId: String) {
        val keys = eventHandlerIds.keys.filter { it.startsWith("$elementId-") }
        for (key in keys) {
            val handlerId = eventHandlerIds.remove(key) ?: continue
            val eventType = key.substringAfterLast('-')
            try {
                wasmRemoveEventHandler(elementId, eventType, handlerId)
            } catch (_: Throwable) {
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
                // Reset ID counters for stable ID generation across recompositions
                // This ensures elements rendered in the same order get the same IDs
                rowCounter = 0
                columnCounter = 0

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
                    wasmConsoleLog("Removing unused element: $elementId")
                    removeElementFromDom(elementId)
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
                safeWasmConsoleWarn("Hydration data script not found")
                ""
            }
        } catch (e: Throwable) {
            // Expected in test environment where external functions don't exist
            safeWasmConsoleWarn("Could not read hydration data in test environment")
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
                // In test environment, DOMProvider might not work
                safeWasmConsoleWarn("Could not get native element ID in test environment")
                return
            }

            // Find all elements with data-summon-id attributes
            val elementsWithMarkers = try {
                wasmQuerySelectorAllGetIds("[data-summon-id]")
            } catch (e: Throwable) {
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
                            safeWasmConsoleLog("Found existing element: $summonId -> $elementId")
                        }
                    }
                } catch (e: Throwable) {
                    // Ignore individual element errors
                }
            }
        } catch (e: Throwable) {
            safeWasmConsoleError("Failed to scan for hydration markers: ${e.message}")
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
            safeWasmConsoleLog("Reattaching ${pendingEventListeners.size} event listeners")

            for (listenerInfo in pendingEventListeners) {
                runCatching {
                    attachEventListenerInternal(listenerInfo.elementId, listenerInfo.eventType, listenerInfo.handler)
                }.onSuccess {
                    safeWasmConsoleLog("Reattached ${listenerInfo.eventType} listener to ${listenerInfo.elementId}")
                }.onFailure { error ->
                    safeWasmConsoleWarn("Failed to reattach ${listenerInfo.eventType} listener to ${listenerInfo.elementId}: ${error.message}")
                }
            }

            pendingEventListeners.clear()
        } catch (e: Throwable) {
            safeWasmConsoleError("Failed to reattach event listeners: ${e.message}")
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

            safeWasmConsoleLog("Hydration marked as complete for: $rootElementId")
        } catch (e: Throwable) {
            safeWasmConsoleError("Failed to mark hydration complete: ${e.message}")
        }
    }

    /**
     * Enhanced element creation that checks for existing elements during hydration and recomposition.
     * Returns null if element is reused (already in DOM), or the new element if created
     */
    private fun createOrReuseElement(tagName: String, summonId: String? = null): DOMElement? {
        // Generate a summonId if not provided
        val effectiveSummonId = summonId ?: "$tagName-${wasmPerformanceNow().toLong()}"
        wasmConsoleLog("createOrReuseElement: Starting for $tagName with ID $effectiveSummonId")

        // Track element in current composition
        currentCompositionElements.add(effectiveSummonId)

        // Priority 1: Check recomposition cache
        if (recompositionElements.containsKey(effectiveSummonId)) {
            val reusedElement = recompositionElements[effectiveSummonId]
            if (reusedElement != null) {
                try {
                    // Validate the reused element type
                    val elementType = reusedElement::class.simpleName ?: "Unknown"
                    wasmConsoleLog("createOrReuseElement: Found cached element type: $elementType")

                    // Validate the element by trying to get its native ID
                    DOMProvider.getNativeElementId(reusedElement)
                    wasmConsoleLog("createOrReuseElement: Reusing valid recomposition element: $effectiveSummonId")
                    return null // Return null to indicate element is reused and already in DOM
                } catch (e: Exception) {
                    // CRITICAL FIX: When validation fails, immediately create new element
                    wasmConsoleError("createOrReuseElement: Validation failed for cached element: ${e.message}")
                    // Remove the invalid element from cache
                    recompositionElements.remove(effectiveSummonId)
                    wasmConsoleLog("createOrReuseElement: Removed invalid element from cache, creating fresh element now")
                    // IMMEDIATE FIX: Don't check other caches, create fresh element right away
                    // This prevents the "bad cast" error by ensuring we always have a valid element
                    return createFreshElement(tagName, effectiveSummonId)
                }
            }
        } else {
            wasmConsoleLog("createOrReuseElement: No element found in recomposition cache for: $effectiveSummonId")
        }

        // Priority 2: Check hydration cache if hydrating
        if (isHydrating && existingElements.containsKey(effectiveSummonId)) {
            val existingElement = existingElements[effectiveSummonId]
            if (existingElement != null) {
                try {
                    val elementType = existingElement::class.simpleName ?: "Unknown"
                    wasmConsoleLog("createOrReuseElement: Found hydration element type: $elementType")

                    // Validate the element by trying to get its native ID
                    DOMProvider.getNativeElementId(existingElement)
                    wasmConsoleLog("createOrReuseElement: Reusing valid hydration element: $effectiveSummonId")
                    // Move to recomposition cache for future use
                    recompositionElements[effectiveSummonId] = existingElement
                    return null // Return null to indicate element is reused and already in DOM
                } catch (e: Exception) {
                    // CRITICAL FIX: When validation fails, immediately create new element
                    wasmConsoleError("createOrReuseElement: Validation failed for hydration element: ${e.message}")
                    existingElements.remove(effectiveSummonId)
                    wasmConsoleLog("createOrReuseElement: Removed invalid hydration element, creating fresh element now")
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
            wasmConsoleLog("createFreshElement: Creating new $tagName element with ID $effectiveSummonId")
            val newElement = DOMProvider.document.createElement(tagName)

            // Validate the newly created element
            val newElementType = newElement::class.simpleName ?: "Unknown"
            wasmConsoleLog("createFreshElement: Created element type: $newElementType")

            // Validate the newly created element by trying to get its native ID
            try {
                DOMProvider.getNativeElementId(newElement)
                wasmConsoleLog("createFreshElement: New element validation successful")
            } catch (typeError: IllegalArgumentException) {
                wasmConsoleError("createFreshElement: CRITICAL ERROR - DOMProvider.document.createElement returned wrong type: $newElementType")
                wasmConsoleError("createFreshElement: Type validation failed: $typeError")
                throw WasmDOMException("Element creation failed - wrong type returned: $newElementType")
            }

            // Cache the new element for future recompositions
            recompositionElements[effectiveSummonId] = newElement
            wasmConsoleLog("createFreshElement: Created and cached new element: $effectiveSummonId")

            return newElement // Return the new element to be appended to DOM
        } catch (e: Exception) {
            wasmConsoleError("createFreshElement: FAILED to create element: ${e.message}")
            throw e
        }
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
            attachEventListenerInternal(elementId, eventType, handler, listenerKey)
        }
    }

    private fun attachEventListenerInternal(
        elementId: String,
        eventType: String,
        handler: () -> Unit,
        listenerKey: String = "$elementId-$eventType"
    ) {
        eventHandlerIds[listenerKey]?.let { existingId ->
            try {
                wasmRemoveEventHandler(elementId, eventType, existingId)
            } catch (_: Throwable) {
                // ignore errors removing stale handlers
            }
            eventHandlerIds.remove(listenerKey)
            attachedEventListeners.remove(listenerKey)
        }

        val handlerId = generateEventHandlerId(listenerKey)

        registerWasmEventCallback(handlerId) {
            try {
                wasmConsoleLog("Executing registered callback $handlerId for $listenerKey")
                handler()
            } catch (e: Exception) {
                wasmConsoleError("Event handler failed: ${e.message}")
            }
        }

        val success = wasmAddEventHandler(elementId, eventType, handlerId)
        if (success) {
            wasmConsoleLog("Successfully registered $eventType handler for $elementId with ID: $handlerId")
            eventHandlerIds[listenerKey] = handlerId
            attachedEventListeners.add(listenerKey)
            runCatching {
                wasmSetElementAttribute(elementId, "data-summon-handler-$eventType", handlerId)
            }
        } else {
            wasmConsoleError("Failed to register $eventType handler for $elementId")
            eventHandlerIds.remove(listenerKey)
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

    // ================================================================================================
    // HTML STRING BUILDING HELPERS
    // ================================================================================================

    /**
     * Build HTML attributes map from Modifier.
     */
    private fun buildModifierAttributes(modifier: Modifier): Map<String, String> {
        val attrs = mutableMapOf<String, String>()

        // Extract attributes from modifier if available
        modifier.attributes?.forEach { (key, value) ->
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
            sb.append(" $key=\"$value\"")
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
     * Safe console logging methods that handle exceptions and missing functions.
     * These are truly safe - they won't throw even if the external function doesn't exist.
     */
    private fun safeWasmConsoleLog(message: String) {
        try {
            // Try to call the external function
            wasmConsoleLog(message)
        } catch (e: Throwable) {
            // Ignore all errors including missing function errors
            // This is expected in test environments
        }
    }

    private fun safeWasmConsoleError(message: String) {
        try {
            // Try to call the external function
            wasmConsoleError(message)
        } catch (e: Throwable) {
            // Ignore all errors including missing function errors
            // This is expected in test environments
        }
    }

    private fun safeWasmConsoleWarn(message: String) {
        try {
            // Try to call the external function
            wasmConsoleWarn(message)
        } catch (e: Throwable) {
            // Ignore all errors including missing function errors
            // This is expected in test environments
        }
    }
}
