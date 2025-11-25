package codes.yousef.summon.runtime

// Import extension functions for Element
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.IconType
import codes.yousef.summon.components.feedback.AlertVariant
import codes.yousef.summon.components.feedback.ProgressType
import codes.yousef.summon.components.input.FileInfo
import codes.yousef.summon.components.navigation.Tab
import codes.yousef.summon.core.FlowContentCompat
import codes.yousef.summon.core.asFlowContentCompat
import codes.yousef.summon.js.console
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ModifierExtras.withAttribute
import codes.yousef.summon.runtime.LocalPlatformRenderer
import kotlinx.browser.document
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.*
import org.w3c.dom.*
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.Event as DomEvent

/**
 * JavaScript platform implementation of PlatformRenderer.
 * This implementation follows Kobweb's approach using direct DOM manipulation.
 * This class is not exported to JavaScript and contains all the non-exportable types.
 */
actual open class PlatformRenderer {
    // Element stack for managing parent-child relationships
    private val elementStack = ElementStack()

    // For head elements management
    private val headElements = mutableListOf<String>()

    // Element caching for smart recomposition
    private val elementCache = mutableMapOf<String, Element>()
    private val usedElements = mutableSetOf<String>()
    private var elementCounter = 0
    private var isRecomposing = false
    private var currentParentKey = "root"
    private val parentKeyStack = mutableListOf<String>()
    private val parentChildrenMap = mutableMapOf<Element, MutableList<String>>()
    private val keyToParentMap = mutableMapOf<String, Element>()
    private val listenerRegistry = mutableMapOf<Element, MutableMap<String, EventListener>>()
    private var handlerCounter = 0

    private class ElementStack {
        private val stack = mutableListOf<Element>()

        val current: Element get() = stack.lastOrNull() ?: document.body!!

        fun push(element: Element) {
            stack.add(element)
        }

        fun pop() {
            if (stack.size > 0) stack.removeAt(stack.lastIndex)
        }

        fun withElement(element: Element, block: () -> Unit) {
            push(element)
            try {
                block()
            } finally {
                pop()
            }
        }
    }

    /**
     * Starts a recomposition cycle. Call this before recomposing to track which elements are used.
     */
    actual open fun startRecomposition() {
        isRecomposing = true
        usedElements.clear()
        elementCounter = 0  // Reset counter to ensure consistent keys across recompositions
        currentParentKey = "root"  // Reset parent key
        parentKeyStack.clear()
        // Don't clear parentChildrenMap or keyToParentMap - we need them to track existing elements
    }

    /**
     * Ends a recomposition cycle. Removes elements that weren't used in this composition.
     */
    actual open fun endRecomposition() {
        if (isRecomposing) {
            // Remove unused elements from their parents and cache
            val unusedKeys = elementCache.keys - usedElements
            unusedKeys.forEach { key ->
                val element = elementCache[key]
                if (element != null) {
                    element.parentNode?.removeChild(element)
                    elementCache.remove(key)
                }
            }
            isRecomposing = false
        }
    }

    /**
     * Generates a stable key for an element based on its type and position
     */
    private fun generateElementKey(tagName: String): String {
        val key = "$currentParentKey:$tagName:${elementCounter++}"
        return key
    }

    /**
     * Renders content into a specific container element.
     * This sets up the element stack to use the container as the root.
     */
    fun renderInto(container: HTMLElement, content: @Composable () -> Unit) {
        elementStack.push(container)
        parentKeyStack.add(currentParentKey)
        currentParentKey = "container"

        // Track this parent's children for proper cleanup
        if (isRecomposing && !parentChildrenMap.containsKey(container)) {
            parentChildrenMap[container] = mutableListOf()
        }

        try {
            content()

            // After rendering, clean up any children that weren't part of this composition
            if (isRecomposing) {
                cleanupUnusedChildren(container)
            }
        } finally {
            elementStack.pop()
            currentParentKey = parentKeyStack.removeAt(parentKeyStack.lastIndex)
        }
    }

    /**
     * Removes children that weren't used in the current composition
     */
    private fun cleanupUnusedChildren(parent: Element) {
        val childKeys = parentChildrenMap[parent] ?: return
        val unusedChildKeys = childKeys.filter { !usedElements.contains(it) }

        unusedChildKeys.forEach { key ->
            val element = elementCache[key]
            if (element != null && element.parentNode == parent) {
                clearEventListeners(element)
                parent.removeChild(element)
                elementCache.remove(key)
                keyToParentMap.remove(key)
            }
        }

        // Update the children list to only include used elements
        parentChildrenMap[parent] = childKeys.filter { usedElements.contains(it) }.toMutableList()
    }

    private fun createElement(
        tagName: String,
        modifier: Modifier,
        setup: ((Element) -> Unit)? = null,
        content: (@Composable () -> Unit)? = null
    ): Element {
        // Generate a key for this element
        val elementKey = generateElementKey(tagName)

        val parent = elementStack.current

        // Try to reuse existing element if we're recomposing
        val element = if (isRecomposing && elementCache.containsKey(elementKey)) {
            val cached = elementCache[elementKey]!!

            // Check if element needs to be moved to a different parent
            val oldParent = keyToParentMap[elementKey]
            if (oldParent != parent) {
                // Element is being moved to a new parent
                if (cached.parentNode != null) {
                    cached.parentNode?.removeChild(cached)
                }
                parent.appendChild(cached)
                keyToParentMap[elementKey] = parent
            } else if (cached.parentNode != parent) {
                // Element should be in this parent but isn't (shouldn't happen but handle it)
                parent.appendChild(cached)
            }

            // Preserve focus state for input elements
            val wasFocused = cached == document.activeElement
            val selectionStart = if (cached is HTMLInputElement) cached.selectionStart else null
            val selectionEnd = if (cached is HTMLInputElement) cached.selectionEnd else null

            // Apply new modifiers (this will update styles and attributes)
            applyModifier(cached, modifier)

            // Restore focus and selection if this element had focus
            if (wasFocused) {
                (cached as? HTMLElement)?.focus()
                if (cached is HTMLInputElement && selectionStart != null && selectionEnd != null) {
                    cached.setSelectionRange(selectionStart, selectionEnd)
                }
            }

            cached
        } else {
            // Create new element
            val newElement = document.createElement(tagName)

            // Apply modifiers
            applyModifier(newElement, modifier)

            // Add to cache and track parent relationship
            elementCache[elementKey] = newElement
            keyToParentMap[elementKey] = parent

            // Add to current parent
            parent.appendChild(newElement)

            newElement
        }

        // Track this element as a child of its parent
        if (isRecomposing) {
            val parentChildren = parentChildrenMap.getOrPut(parent) { mutableListOf() }
            if (!parentChildren.contains(elementKey)) {
                parentChildren.add(elementKey)
            }
        }

        // Mark element as used in this composition
        usedElements.add(elementKey)

        // Apply custom setup
        setup?.invoke(element)

        // Render content if provided
        if (content != null) {
            elementStack.push(element)
            parentKeyStack.add(currentParentKey)
            currentParentKey = elementKey
            try {
                content()
            } finally {
                elementStack.pop()
                currentParentKey = parentKeyStack.removeAt(parentKeyStack.lastIndex)
            }
        }

        // Handle portal teleportation if specified
        val portalTarget = modifier.attributes["data-portal-target"]
        if (portalTarget != null) {
            PortalManager.portal(element, portalTarget)
        }

        return element
    }

    private fun generateHandlerId(eventType: String): String {
        val id = handlerCounter++
        return "js-$eventType-$id"
    }

    private fun registerEventListener(
        element: Element,
        eventType: String,
        handler: (DomEvent) -> Unit
    ) {
        val listeners = listenerRegistry.getOrPut(element) { mutableMapOf() }
        listeners[eventType]?.let { existing ->
            element.removeEventListener(eventType, existing)
        }
        val listener = EventListener { event -> handler(event) }
        element.addEventListener(eventType, listener)
        listeners[eventType] = listener
        element.setAttribute("data-summon-handler-$eventType", generateHandlerId(eventType))
    }

    private fun clearEventListeners(element: Element) {
        val listeners = listenerRegistry.remove(element) ?: return
        listeners.forEach { (eventType, listener) ->
            element.removeEventListener(eventType, listener)
            element.removeAttribute("data-summon-handler-$eventType")
        }
        // Also clean up any injected styles for this element
        StyleInjector.cleanupElementStyles(element)
        // And unportal if it's in a portal
        if (PortalManager.isPortaled(element)) {
            PortalManager.unportal(element)
        }
    }

    private fun cssPropertyName(key: String): String =
        if (key.contains('-')) {
            key
        } else {
            key.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
        }

    private fun ensureSummonId(element: Element): String {
        val existing = element.getAttribute("data-summon-id")
        if (existing != null && existing.isNotBlank()) {
            return existing
        }
        val generated = "summon-js-" + (js("Math.random().toString(36).substring(2, 10)") as String)
        element.setAttribute("data-summon-id", generated)
        return generated
    }

    private fun parseStyleString(styleString: String): Map<String, String> {
        return styleString.split(";")
            .filter { it.isNotBlank() }
            .mapNotNull { rule ->
                val parts = rule.split(":", limit = 2)
                if (parts.size == 2) {
                    parts[0].trim() to parts[1].trim()
                } else null
            }
            .toMap()
    }

    private fun applyModifier(element: Element, modifier: Modifier) {
        // Apply styles using the toStyleString method which converts camelCase properties to kebab-case
        val styleString = modifier.toStyleString()
        if (js("styleString.length > 0") as Boolean) {
            element.setAttribute("style", styleString)
        }

        // Apply other attributes from the modifier
        for ((key, value) in modifier.attributes) {
            when {
                key == "data-hover-styles" -> {
                    val styles = parseStyleString(value)
                    StyleInjector.injectPseudoSelectorStyles(element, ":hover", styles)
                }

                key == "data-focus-styles" -> {
                    val styles = parseStyleString(value)
                    StyleInjector.injectPseudoSelectorStyles(element, ":focus", styles)
                }

                key == "data-active-styles" -> {
                    val styles = parseStyleString(value)
                    StyleInjector.injectPseudoSelectorStyles(element, ":active", styles)
                }

                key == "data-focus-within-styles" -> {
                    val styles = parseStyleString(value)
                    StyleInjector.injectPseudoSelectorStyles(element, ":focus-within", styles)
                }

                key == "data-first-child-styles" -> {
                    val styles = parseStyleString(value)
                    StyleInjector.injectPseudoSelectorStyles(element, ":first-child", styles)
                }

                key == "data-last-child-styles" -> {
                    val styles = parseStyleString(value)
                    StyleInjector.injectPseudoSelectorStyles(element, ":last-child", styles)
                }

                key == "data-nth-child-styles" -> {
                    val parts = value.split("|||")
                    if (parts.size == 2) {
                        val selector = parts[0]
                        val styles = parseStyleString(parts[1])
                        StyleInjector.injectPseudoSelectorStyles(element, ":nth-child($selector)", styles)
                    }
                }

                key == "data-only-child-styles" -> {
                    val styles = parseStyleString(value)
                    StyleInjector.injectPseudoSelectorStyles(element, ":only-child", styles)
                }

                key == "data-visited-styles" -> {
                    val styles = parseStyleString(value)
                    StyleInjector.injectPseudoSelectorStyles(element, ":visited", styles)
                }

                key == "data-disabled-styles" -> {
                    val styles = parseStyleString(value)
                    StyleInjector.injectPseudoSelectorStyles(element, ":disabled", styles)
                }

                key == "data-checked-styles" -> {
                    val styles = parseStyleString(value)
                    StyleInjector.injectPseudoSelectorStyles(element, ":checked", styles)
                }

                key == "data-media-queries" -> {
                    // Format: "query1{styles1}|query2{styles2}|..."
                    value.split("|").forEach { queryBlock ->
                        val queryMatch = Regex("""^([^{]+)\{([^}]+)\}$""").find(queryBlock)
                        if (queryMatch != null) {
                            val (mediaQuery, stylesString) = queryMatch.destructured
                            val styles = parseStyleString(stylesString)
                            StyleInjector.injectMediaQueryStyles(element, mediaQuery, styles)
                        }
                    }
                }

                else -> {
                    element.setAttribute(key, value)
                }
            }
        }

        if (modifier.eventHandlers.isNotEmpty()) {
            val isDisabled = modifier.attributes.containsKey("disabled")
            modifier.eventHandlers.forEach { (eventName, handler) ->
                val lowerEvent = eventName.lowercase()
                if (isDisabled && lowerEvent == "click") {
                    return@forEach
                }
                registerEventListener(element, lowerEvent) { event ->
                    if (lowerEvent == "click") {
                        event.preventDefault()
                        event.stopPropagation()
                    }
                    handler()
                }
            }
        }

        if (modifier.pseudoElements.isNotEmpty()) {
            val hostId = ensureSummonId(element)
            modifier.pseudoElements.forEach { pseudo ->
                val cssBody = pseudo.styles.entries.joinToString("; ") {
                    "${cssPropertyName(it.key)}: ${it.value};"
                }
                val styleElement = document.createElement("style")
                styleElement.textContent = """
                    [data-summon-id="$hostId"]${pseudo.element.selector} {
                        content: ${pseudo.content};
                        $cssBody
                    }
                """.trimIndent()
                document.head?.appendChild(styleElement)
            }
        }
    }

    actual open fun renderText(text: String, modifier: Modifier) {
        createElement("span", modifier, { element ->
            element.textContent = text
        })
    }

    actual open fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
        createElement("label", modifier, { element ->
            element.textContent = text
            forElement?.let { id -> element.setAttribute("for", id) }
        })
    }

    actual open fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        createElement("button", modifier, { element ->
            val hasType = modifier.attributes.containsKey("type")
            if (!hasType) {
                element.setAttribute("type", "button")
            }

            val isDisabled = modifier.attributes.containsKey("disabled")
            if (!isDisabled) {
                registerEventListener(element, "click") { event ->
                    event.preventDefault()
                    event.stopPropagation()
                    onClick()
                }
            }
        }) {
            // Call the content directly without FlowContent
            // The content lambda will call Text(label) which will render into the button
            content(createFlowContent("button"))
        }
    }

    actual open fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier,
        type: String
    ) {
        createElement("input", modifier, { element ->
            element.setAttribute("type", type)

            // Only update value if it's different from current value
            // This prevents cursor jumping and focus issues
            val inputElement = element as? HTMLInputElement
            if (inputElement != null && inputElement.value != value) {
                // Save cursor position before updating value
                val selectionStart = inputElement.selectionStart
                val selectionEnd = inputElement.selectionEnd

                inputElement.value = value

                // Restore cursor position if element has focus
                if (element == document.activeElement && selectionStart != null && selectionEnd != null) {
                    inputElement.setSelectionRange(selectionStart, selectionEnd)
                }
            } else if (inputElement == null) {
                element.setAttribute("value", value)
            }

            registerEventListener(element, "input") { event ->
                val target = event.target.asDynamic()
                onValueChange(target.value as String)
            }
        })
    }

    actual open fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        modifier: Modifier
    ) {
        createElement("select", modifier, { element ->
            // Clear existing options before adding new ones
            // This prevents duplication when the element is reused
            while (element.firstChild != null) {
                element.removeChild(element.firstChild!!)
            }

            // Create option elements
            options.forEachIndexed { index, option ->
                val optionElement = document.createElement("option")
                optionElement.textContent = option.label
                optionElement.asDynamic().value = index.toString()
                optionElement.asDynamic().disabled = option.disabled

                // Set selected state
                if (option.value == selectedValue) {
                    optionElement.asDynamic().selected = true
                }

                element.appendChild(optionElement)
            }

            registerEventListener(element, "change") { event ->
                console.log("Select changed!")
                val selectElement = event.target.asDynamic()
                val selectedIndex = selectElement.selectedIndex as Int
                console.log("Selected index: $selectedIndex")
                if (selectedIndex >= 0 && selectedIndex < options.size) {
                    onSelectedChange(options[selectedIndex].value)
                } else {
                    onSelectedChange(null)
                }
            }
        })
    }

    actual open fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        min: LocalDate?,
        max: LocalDate?,
        modifier: Modifier
    ) {
        createElement("input", modifier, { element ->
            element.setAttribute("type", "date")
            value?.let { element.setAttribute("value", it.toString()) }
            min?.let { element.setAttribute("min", it.toString()) }
            max?.let { element.setAttribute("max", it.toString()) }
            if (!enabled) element.setAttribute("disabled", "disabled")

            registerEventListener(element, "change") { event ->
                val dateValue = event.target.asDynamic().value as? String
                if (dateValue != null && dateValue.isNotEmpty()) {
                    try {
                        val parts = dateValue.split('-')
                        val year = parts[0].toInt()
                        val month = parts[1].toInt()
                        val day = parts[2].toInt()
                        val date = LocalDate(year, month, day)
                        onValueChange(date)
                    } catch (e: Exception) {
                        console.error("Error parsing date: $e")
                        onValueChange(null)
                    }
                } else {
                    onValueChange(null)
                }
            }
        })
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
        createElement("textarea", modifier, { element ->
            element.textContent = value
            if (!enabled) element.setAttribute("disabled", "disabled")
            if (readOnly) element.setAttribute("readonly", "readonly")
            rows?.let { element.setAttribute("rows", it.toString()) }
            maxLength?.let { element.setAttribute("maxlength", it.toString()) }
            placeholder?.let { element.setAttribute("placeholder", it) }

            registerEventListener(element, "input") { event ->
                val textareaElement = event.target.asDynamic()
                onValueChange(textareaElement.value as String)
            }
        })
    }

    actual open fun addHeadElement(content: String) {
        // Add the raw string content for a head element (e.g., a <link> or <style> tag)
        val trimmedContent = content.trim()
        if (trimmedContent.startsWith("<") && trimmedContent.endsWith(">")) {
            headElements.add(trimmedContent)
        }
    }

    actual open fun getHeadElements(): List<String> = headElements.toList()

    actual open fun renderHeadElements(builder: codes.yousef.summon.seo.HeadScope.() -> Unit) {
        val headScope = codes.yousef.summon.seo.DefaultHeadScope { element ->
            // Add the raw HTML element to head elements collection
            // In browser environment, we'd typically parse and add to DOM
            addHeadElement(element)
        }
        headScope.builder()
    }

    actual open fun renderComposable(composable: @Composable () -> Unit) {
        // This function usually relies on an existing parent from the elementStack
        // It's primarily for rendering nested composables within an existing structure
        composable()
    }

    actual open fun renderComposableRoot(composable: @Composable () -> Unit): String {
        // Create a detached root element for rendering if not using a specific container
        val rootElement = document.createElement("div")
        
        // Provide this renderer to the composition local so child composables can access it
        LocalPlatformRenderer.provides(this)
        
        // Temporarily set this as the current element for rendering
        elementStack.withElement(rootElement) {
            composable()
        }
        return rootElement.outerHTML
    }

    actual open fun renderComposableRootWithHydration(composable: @Composable () -> Unit): String {
        return renderComposableRootWithHydration(null, composable)
    }

    actual open fun renderComposableRootWithHydration(state: Any?, composable: @Composable () -> Unit): String {
        // Produce hydration-ready markup by adding Summon-specific markers and payload container
        val rootElement = document.createElement("div")
        rootElement.setAttribute("data-summon-hydration", "root")
        rootElement.setAttribute("data-summon-renderer", "js")
        rootElement.setAttribute("data-summon-version", js("globalThis.SUMMON_VERSION") ?: "0.5.2.4")

        // Provide this renderer to the composition local so child composables can access it
        LocalPlatformRenderer.provides(this)

        elementStack.withElement(rootElement) {
            composable()
        }

        // Attach an empty hydration payload placeholder to align with server render contract
        val payloadElement = document.createElement("script")
        payloadElement.setAttribute("type", "application/json")
        payloadElement.setAttribute("data-summon-hydration", "payload")
        payloadElement.textContent =
            js("JSON.stringify({ callbacks: [], renderer: 'js', timestamp: Date.now() })") as String
        rootElement.appendChild(payloadElement)

        return rootElement.outerHTML
    }

    actual open fun hydrateComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
        val rootElement = document.getElementById(rootElementId)
        if (rootElement != null) {
            // Clear existing content and re-render with client-side interactivity
            elementStack.withElement(rootElement) {
                // Clear the element first
                rootElement.innerHTML = ""

                // Set up recomposer for this root
                val recomposer = RecomposerHolder.current()
                recomposer.setCompositionRoot(composable)

                // Provide this renderer to the composition local so child composables can access it
                LocalPlatformRenderer.provides(this)

                // Render the composable
                recomposer.composeInitial(composable)
            }
        } else {
            console.error("Could not find element with ID: $rootElementId for hydration")
        }
    }

    // Helper to create FlowContentCompat
    private fun createFlowContent(tagName: String): FlowContentCompat {
        val consumer = createTagConsumer()
        val attrs = mutableMapOf<String, String>()

        val flowContent = object : FlowContent {
            override val tagName: String = tagName
            override val consumer: TagConsumer<*> = consumer
            override val namespace: String? = null
            override val attributes: MutableMap<String, String> = attrs
            override val attributesEntries: Collection<Map.Entry<String, String>>
                get() = attributes.entries
            override val inlineTag: Boolean = tagName in setOf(
                "span",
                "a",
                "b",
                "i",
                "em",
                "strong",
                "code",
                "cite",
                "q",
                "small",
                "mark",
                "del",
                "ins",
                "sub",
                "sup"
            )
            override val emptyTag: Boolean = tagName in setOf(
                "br",
                "hr",
                "img",
                "input",
                "meta",
                "area",
                "base",
                "col",
                "embed",
                "link",
                "param",
                "source",
                "track",
                "wbr"
            )
        }

        return flowContent.asFlowContentCompat()
    }

    // Helper to create FormContent
    private fun createFormContent(): FormContent {
        // Since FormContent is a typealias for FlowContent, we can just use createFlowContent
        return createFlowContent("form")
    }

    // Create a tag consumer for the flow content
    private fun createTagConsumer(): TagConsumer<Element> = object : TagConsumer<Element> {
        override fun onTagStart(tag: Tag) {
            val newElement = document.createElement(tag.tagName)
            tag.attributes.forEach { (key, value) ->
                newElement.setAttribute(key, value)
            }
            elementStack.current.appendChild(newElement)
            elementStack.push(newElement)
        }

        override fun onTagEnd(tag: Tag) {
            elementStack.pop()
        }

        override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
            if (value == null) {
                elementStack.current.removeAttribute(attribute)
            } else {
                elementStack.current.setAttribute(attribute, value)
            }
        }

        override fun onTagEvent(tag: Tag, event: String, value: (DomEvent) -> Unit) {
            elementStack.current.addEventListener(event, value)
        }

        override fun onTagContent(content: CharSequence) {
            elementStack.current.appendChild(document.createTextNode(content.toString()))
        }

        override fun onTagContentEntity(entity: Entities) {
            // Entities are not directly supported in DOM manipulation this way.
            // This would require parsing the entity or using innerHTML, which we are trying to avoid for direct composable rendering.
            // For now, we'll append as text, but this might not render correctly for all entities.
            elementStack.current.appendChild(document.createTextNode(entity.text))
        }

        override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {
            // Similar to onTagContentEntity, direct unsafe HTML is tricky.
            // The most straightforward way in DOM is innerHTML, but that has security implications and bypasses some of the controlled rendering.
            // We'll create a temporary element, set its innerHTML, and then append its children.
            // This is a common workaround but should be used judiciously.
            val tempDiv = document.createElement("div")
            val unsafe = object : Unsafe {
                override operator fun String.unaryPlus() {
                    tempDiv.innerHTML += this
                }
            }
            unsafe.block()
            while (tempDiv.firstChild != null) {
                elementStack.current.appendChild(tempDiv.firstChild!!)
            }
        }

        override fun finalize(): Element {
            // This finalize is more for builder patterns where a single root is constructed and returned.
            // In our direct manipulation model, elements are added to the stack's current parent directly.
            // Returning the current element on the stack, or perhaps the initial root, might make sense
            // depending on how the consumer of this TagConsumer is structured.
            // For FlowContent used within renderButton etc., this might not be directly used to return a value.
            return elementStack.current
        }

        override fun onTagComment(content: CharSequence) { // Renamed from onComment and added override
            elementStack.current.appendChild(document.createComment(content.toString()))
        }
    }

    // Extended PlatformRenderer methods with default implementations or TODOs

    actual open fun renderColumn(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        val columnModifier = modifier
            .style("display", "flex")
            .style("flexDirection", "column")

        createElement("div", columnModifier) {
            content(createFlowContent("div"))
        }
    }

    actual open fun renderRow(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        val rowModifier = Modifier(
            modifier.styles + mapOf(
                "display" to "flex",
                "flexDirection" to "row"
            )
        )
        createElement("div", rowModifier) {
            content(createFlowContent("div"))
        }
    }

    actual open fun renderBoxContainer(modifier: Modifier, content: @Composable () -> Unit) {
        createElement("div", modifier) {
            content()
        }
    }

    actual open fun renderBox(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        createElement("div", modifier) {
            content(createFlowContent("div"))
        }
    }

    actual open fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    ) {
        createElement("i", modifier, setup = { element ->
            when (type) {
                IconType.FONT -> {
                    element.textContent = name
                }
                IconType.SVG -> {
                    svgContent?.let { element.innerHTML = it }
                }
                else -> {}
            }

            if (onClick != null) {
                registerEventListener(element, "click") { event ->
                    event.preventDefault()
                    event.stopPropagation()
                    onClick()
                }
            }
        })
    }

    actual open fun renderImage(
        src: String,
        alt: String?,
        modifier: Modifier
    ) {
        createElement("img", modifier, { element ->
            element.setAttribute("src", src)
            alt?.let { element.setAttribute("alt", it) }
        })
    }

    actual open fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        label: String?,
        modifier: Modifier
    ) {
        console.log("Rendering checkbox: checked=$checked, enabled=$enabled, label=$label")

        // Ensure checkbox is visible with explicit styles
        val checkboxModifier = modifier
            .style("width", "18px")
            .style("height", "18px")
            .style("cursor", if (enabled) "pointer" else "not-allowed")
            .style("margin-right", "8px")

        createElement("input", checkboxModifier, { element ->
            element.setAttribute("type", "checkbox")
            element.asDynamic().checked = checked
            if (!enabled) element.setAttribute("disabled", "disabled")
            registerEventListener(element, "change") { event ->
                console.log("Checkbox changed!")
                val inputElement = event.target.asDynamic()
                onCheckedChange(inputElement.checked as Boolean)
            }
        })
        label?.let { renderText(it, Modifier()) }
    }

    actual open fun renderRadioButton(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        label: String?,
        enabled: Boolean,
        modifier: Modifier
    ) {
        createElement("input", modifier, { element ->
            element.setAttribute("type", "radio")
            element.asDynamic().checked = checked
            if (!enabled) element.setAttribute("disabled", "disabled")
            registerEventListener(element, "change") { event ->
                val radioElement = event.target.asDynamic()
                onCheckedChange(radioElement.checked as Boolean)
            }
        })
        label?.let { renderText(it, Modifier()) }
    }

    actual open fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Switches are custom components, often CSS-based or using a div and checkbox
        // This is a placeholder for a more complex component
        renderCheckbox(checked, onCheckedChange, enabled, null, modifier.attribute("role", "switch"))
    }

    actual open fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        createElement("input", modifier, { element ->
            element.setAttribute("type", "range")
            element.setAttribute("value", value.toString())
            element.setAttribute("min", valueRange.start.toString())
            element.setAttribute("max", valueRange.endInclusive.toString())
            if (steps > 0) {
                val stepValue = (valueRange.endInclusive - valueRange.start) / (steps + 1)
                element.setAttribute("step", stepValue.toString())
            }
            if (!enabled) element.setAttribute("disabled", "disabled")

            registerEventListener(element, "input") { event ->
                onValueChange((event.target.asDynamic().value as String).toFloat())
            }
        })
    }

    actual open fun renderDropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit // ColumnScope.() -> Unit for specific layout
    ) {
        // Dropdown menus are complex. This is a very basic representation.
        if (expanded) {
            createElement("div", modifier) { // This div would be styled as a dropdown
                content()
            }
            // Might need a global click listener to handle onDismissRequest
        }
    }

    actual open fun renderAlertDialog(
        onDismissRequest: () -> Unit,
        confirmButton: @Composable () -> Unit,
        modifier: Modifier,
        dismissButton: (@Composable () -> Unit)?,
        icon: (@Composable () -> Unit)?,
        title: (@Composable () -> Unit)?,
        text: (@Composable () -> Unit)?
    ) {
        // Dialogs typically overlay content and require specific styling and structure
        // This is a placeholder structure
        createElement(
            "div",
            modifier.withAttribute("role", "alertdialog")
        ) { // Base alert div
            // Apply styles based on variant (success, info, warning, error)
            icon?.invoke()
            title?.invoke()
            text?.invoke()
            confirmButton()
            dismissButton?.invoke()
            // A button or mechanism to call onDismissRequest would be part of this structure
        }
    }

    actual open fun renderModalBottomSheet(
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit // ColumnScope.() -> Unit usually
    ) {
        // Similar to AlertDialog, requires specific structure and styling
        createElement("div", modifier) { // Styled as a bottom sheet
            content()
            // Mechanism for onDismissRequest
        }
    }

    actual open fun renderCircularProgressIndicator(
        progress: Float?,
        modifier: Modifier,
        type: ProgressType
    ) {
        // Placeholder - could be an SVG or a styled div
        createElement("div", modifier, setup = { element -> // Explicitly named 'setup'
            element.setAttribute("role", "progressbar")
            progress?.let { element.setAttribute("aria-valuenow", it.toString()) }
            // Apply styles for circular progress
        })
    }

    actual open fun renderLinearProgressIndicator(
        progress: Float?,
        modifier: Modifier,
        type: ProgressType
    ) {
        createElement("progress", modifier, setup = { element -> // Explicitly named 'setup'
            progress?.let { element.setAttribute("value", it.toString()) }
            // Linear progress elements might need max attribute if progress is not 0-1
        })
    }

    actual open fun renderTooltip(
        text: String,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        // Tooltips often wrap content and show text on hover/focus
        // Basic: wrap content in a div with a title attribute
        createElement("div", modifier.withAttribute("title", text)) {
            content()
        }
    }

    actual open fun renderCard(
        modifier: Modifier,
        elevation: Int,
        content: @Composable () -> Unit
    ) {
        // Cards are styled containers
        createElement("div", modifier) { // Apply card-specific styles, possibly using elevation
            content()
        }
    }

    actual open fun renderCard(modifier: Modifier, content: @Composable (FlowContentCompat.() -> Unit)) {
        // Cards are styled containers with default card styling
        val cardModifier = modifier
            .style("border", "1px solid #e0e0e0")
            .style("borderRadius", "8px")
            .style("boxShadow", "0 2px 4px rgba(0,0,0,0.1)")
            .style("backgroundColor", "white")
            .style("padding", "16px")
            .style("margin", "8px 0")

        createElement("div", cardModifier) {
            content(createFlowContent("div"))
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
        createElement(
            "div",
            modifier.withAttribute("role", if (variant == AlertVariant.ERROR) "alert" else "status")
        ) { // Base alert div
            // Apply styles based on variant (success, info, warning, error)
            icon?.invoke()
            title?.let { renderText(it, Modifier()) } // Needs specific styling for title
            renderText(message, Modifier()) // Needs specific styling for message
            actions?.invoke()
        }
    }

    actual open fun renderFilePicker(
        onFilesSelected: (List<FileInfo>) -> Unit,
        enabled: Boolean,
        multiple: Boolean,
        accept: String?,
        modifier: Modifier,
        actions: @Composable (() -> Unit)?
    ) {
        createElement("input", modifier, { element ->
            element.setAttribute("type", "file")
            if (multiple) element.setAttribute("multiple", "")
            accept?.let { element.setAttribute("accept", it) }
            if (!enabled) element.setAttribute("disabled", "disabled")

            registerEventListener(element, "change") { event ->
                val files = event.target.asDynamic().files
                val fileList = mutableListOf<FileInfo>()
                val length = files.length as Int
                for (i in 0 until length) {
                    val file = files[i]
                    fileList.add(
                        FileInfo(
                            name = js("file.name") as String,
                            size = js("file.size") as Long,
                            type = js("file.type") as String,
                            jsFile = file
                        )
                    )
                }
                onFilesSelected(fileList)
            }
        })
    }

    actual open fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {
        createElement("input", modifier, { element ->
            element.setAttribute("type", "time")
            value?.let {
                element.setAttribute(
                    "value",
                    it.toString().substringBeforeLast('.')
                )
            } // Format as HH:mm or HH:mm:ss
            if (!enabled) element.setAttribute("disabled", "disabled")

            registerEventListener(element, "change") { event ->
                val timeValue = event.target.asDynamic().value as? String
                if (timeValue != null && timeValue.isNotEmpty()) {
                    try {
                        val parts = timeValue.split(':')
                        val hour = parts[0].toInt()
                        val minute = parts[1].toInt()
                        val second = if (parts.size > 2) parts[2].toInt() else 0
                        onValueChange(LocalTime(hour, minute, second))
                    } catch (e: Exception) {
                        console.error("Error parsing time: $e")
                        onValueChange(null)
                    }
                } else {
                    onValueChange(null)
                }
            }
        })
    }


    actual open fun renderHorizontalPager(
        count: Int,
        state: Any, // PagerState equivalent
        modifier: Modifier,
        content: @Composable (page: Int) -> Unit
    ) {
        // Simple horizontal pager implementation
        createElement("div", modifier) {
            for (i in 0 until count) {
                createElement("div", Modifier()) {
                    content(i)
                }
            }
        }
    }

    actual open fun renderVerticalPager(
        count: Int,
        state: Any, // PagerState equivalent
        modifier: Modifier,
        content: @Composable (page: Int) -> Unit
    ) {
        // Simple vertical pager implementation
        createElement("div", modifier) {
            for (i in 0 until count) {
                createElement("div", Modifier()) {
                    content(i)
                }
            }
        }
    }

    actual open fun renderSwipeToDismiss(
        state: Any, // DismissState equivalent
        background: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        // Simple swipe to dismiss implementation with layered background and content
        createElement("div", modifier) {
            // Background layer (revealed on swipe)
            createElement("div", Modifier()) {
                background()
            }
            // Content layer
            createElement("div", Modifier()) {
                content()
            }
        }
    }

    actual open fun renderSurface(
        modifier: Modifier,
        elevation: Int,
        content: @Composable (() -> Unit)
    ) {
        createElement("div", modifier) { // Apply surface styles, elevation
            content()
        }
    }

    actual open fun renderHtml(htmlContent: String, modifier: Modifier) {
        createElement("div", modifier, setup = { element ->
            (element as? HTMLElement)?.innerHTML = htmlContent
        })
    }

    actual open fun renderScreen(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        // Screen is typically a full-height container
        val screenModifier = modifier
            .style("minHeight", "100vh")
            .style("width", "100%")

        createElement("div", screenModifier) {
            content(createFlowContent("div"))
        }
    }

    actual open fun renderLink(href: String, modifier: Modifier) {
        createElement("a", modifier, { element ->
            element.setAttribute("href", href)
        })
    }

    actual open fun renderLink(
        modifier: Modifier,
        href: String,
        content: @Composable (() -> Unit)
    ) {
        createElement("a", modifier, { element ->
            element.setAttribute("href", href)
        }) {
            content()
        }
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
        createElement("a", modifier, { element ->
            element.setAttribute("href", href)
            target?.let { element.setAttribute("target", it) }
            title?.let { element.setAttribute("title", it) }
            ariaLabel?.let { element.setAttribute("aria-label", it) }
            ariaDescribedBy?.let { element.setAttribute("aria-describedby", it) }
        })
    }

    actual open fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    ) {
        createElement("div", modifier.attribute("role", "tablist")) {
            tabs.forEachIndexed { index, tab ->
                createElement(
                    "button",
                    Modifier()
                        .attribute("role", "tab")
                        .attribute("aria-selected", (index == selectedTabIndex).toString())
                        .attribute("tabindex", if (index == selectedTabIndex) "0" else "-1")
                        .className(if (index == selectedTabIndex) "tab-active" else "tab"),
                    { element ->
                        registerEventListener(element, "click") { event ->
                            event.preventDefault()
                            onTabSelected(index)
                        }
                    }
                ) {
                    renderText(tab.title, Modifier())
                }
            }
        }
    }

    actual open fun renderTabLayout(
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        createElement("div", modifier.attribute("role", "tablist")) {
            content()
        }
    }

    actual open fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    ) {
        createElement("div", modifier.attribute("role", "tablist")) {
            tabs.forEach { tab ->
                createElement(
                    "button",
                    Modifier()
                        .attribute("role", "tab")
                        .attribute("aria-selected", (tab == selectedTab).toString())
                        .attribute("tabindex", if (tab == selectedTab) "0" else "-1")
                        .className(if (tab == selectedTab) "tab-active" else "tab"),
                    { element ->
                        registerEventListener(element, "click") { event ->
                            event.preventDefault()
                            onTabSelected(tab)
                        }
                    }
                ) {
                    renderText(tab, Modifier())
                }
            }
        }
        // Render content area
        content()
    }

    actual open fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        createElement("div", modifier)
    }

    actual open fun renderAnimatedVisibility(
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        // Simple animated visibility with fade-in transition
        createElement("div", modifier) {
            content()
        }
    }

    actual open fun renderAnimatedContent(modifier: Modifier) {
        // Simple animated content container with transition classes
        createElement("div", modifier)
    }

    actual open fun renderAnimatedContent(
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        // Animated content with transition support
        val animatedModifier = modifier
            .className("animated-content")
            .style("transition", "all 0.3s ease")

        createElement("div", animatedModifier) {
            content()
        }
    }

    actual open fun renderBlock(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        // Create a block element (div with display: block)
        val blockModifier = Modifier(
            modifier.styles + mapOf(
                "display" to "block"
            )
        )

        createElement("div", blockModifier) {
            content(createFlowContent("div"))
        }
    }

    actual open fun renderInline(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        // Create an inline element (span with display: inline)
        val inlineModifier = Modifier(
            modifier.styles + mapOf(
                "display" to "inline"
            )
        )

        createElement("span", inlineModifier) {
            content(createFlowContent("span"))
        }
    }

    actual open fun renderDiv(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        createElement("div", modifier) {
            content(createFlowContent("div"))
        }
    }

    actual open fun renderSpan(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        createElement("span", modifier) {
            content(createFlowContent("span"))
        }
    }

    actual open fun renderDivider(modifier: Modifier) {
        createElement("hr", modifier)
    }

    actual open fun renderExpansionPanel(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        // Expansion panels typically have a header and expandable content
        // Using details/summary HTML elements for native expand/collapse behavior
        createElement("details", modifier) {
            content(createFlowContent("details"))
        }
    }

    actual open fun renderGrid(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        // Grid layout using CSS Grid
        val gridModifier = modifier
            .style("display", "grid")

        createElement("div", gridModifier) {
            content(createFlowContent("div"))
        }
    }

    actual open fun renderLazyColumn(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        // Lazy column is a scrollable vertical list
        val lazyColumnModifier = modifier
            .style("display", "flex")
            .style("flexDirection", "column")
            .style("overflowY", "auto")

        createElement("div", lazyColumnModifier) {
            content(createFlowContent("div"))
        }
    }

    actual open fun renderLazyRow(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        // Lazy row is a scrollable horizontal list
        val lazyRowModifier = modifier
            .style("display", "flex")
            .style("flexDirection", "row")
            .style("overflowX", "auto")
            .style("whiteSpace", "nowrap")

        createElement("div", lazyRowModifier) {
            content(createFlowContent("div"))
        }
    }

    actual open fun renderResponsiveLayout(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        createElement("div", modifier, setup = { element ->
            // Inject styles if not present
            if (document.getElementById("summon-responsive-styles") == null) {
                val style = document.createElement("style")
                style.id = "summon-responsive-styles"
                style.textContent = """
                    [data-screen-size="SMALL"] .small-content { display: block !important; }
                    [data-screen-size="MEDIUM"] .medium-content { display: block !important; }
                    [data-screen-size="LARGE"] .large-content { display: block !important; }
                    [data-screen-size="XLARGE"] .xlarge-content { display: block !important; }
                """.trimIndent()
                document.head?.appendChild(style)
            }

            // Add logic to detect screen size and update attributes
            fun updateLayout() {
                val width = kotlinx.browser.window.innerWidth
                val size = when {
                    width < 600 -> "SMALL"
                    width < 960 -> "MEDIUM"
                    width < 1280 -> "LARGE"
                    else -> "XLARGE"
                }
                element.setAttribute("data-screen-size", size)
                
                // Update classes for styling hooks
                element.classList.remove("small-screen", "medium-screen", "large-screen", "xlarge-screen")
                element.classList.add("${size.lowercase()}-screen")
            }
            
            kotlinx.browser.window.addEventListener("resize", { updateLayout() })
            // Initial update
            updateLayout()
        }) {
            content(createFlowContent("div"))
        }
    }

    actual open fun renderHtmlTag(
        tagName: String,
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        createElement(tagName, modifier) {
            content(createFlowContent(tagName))
        }
    }

    actual open fun renderCanvas(
        modifier: Modifier,
        width: Int?,
        height: Int?,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        createElement("canvas", modifier, { element ->
            val canvas = element as? HTMLCanvasElement
            width?.let { canvas?.width = it }
            height?.let { canvas?.height = it }
        }) {
            content(createFlowContent("canvas"))
        }
    }

    actual open fun renderScriptTag(
        src: String?,
        async: Boolean,
        defer: Boolean,
        type: String?,
        modifier: Modifier,
        inlineContent: String?
    ) {
        createElement("script", modifier, { element ->
            val script = element as? HTMLScriptElement
            type?.let { script?.type = it }
            if (src != null) {
                script?.src = src
            }
            script?.async = async
            script?.defer = defer
            if (!inlineContent.isNullOrEmpty()) {
                script?.textContent = inlineContent
            }
        })
    }

    actual open fun renderModal(
        visible: Boolean,
        onDismissRequest: () -> Unit,
        title: String?,
        content: @Composable () -> Unit,
        actions: @Composable (() -> Unit)?
    ) {
        if (visible) {
            // Modal backdrop
            createElement(
                "div", Modifier()
                    .className("modal-backdrop")
                    .style("position", "fixed")
                    .style("top", "0")
                    .style("left", "0")
                    .style("width", "100%")
                    .style("height", "100%")
                    .style("backgroundColor", "rgba(0, 0, 0, 0.5)")
                    .style("display", "flex")
                    .style("alignItems", "center")
                    .style("justifyContent", "center")
                    .style("zIndex", "1000"),
                { element ->
                    registerEventListener(element, "click") { event ->
                        if (event.target == element) {
                            onDismissRequest()
                        }
                    }
                }
            ) {
                // Modal content
                createElement(
                    "div", Modifier()
                        .className("modal-content")
                        .style("backgroundColor", "white")
                        .style("borderRadius", "8px")
                        .style("padding", "24px")
                        .style("maxWidth", "500px")
                        .style("width", "90%"),
                    { element ->
                        // Stop propagation to prevent backdrop click
                        registerEventListener(element, "click") { event ->
                            event.stopPropagation()
                        }
                    }
                ) {
                    // Title
                    title?.let {
                        renderText(
                            it, Modifier()
                                .style("fontSize", "20px")
                                .style("fontWeight", "bold")
                                .style("marginBottom", "16px")
                        )
                    }

                    // Content
                    content()

                    // Actions
                    actions?.let {
                        createElement(
                            "div", Modifier()
                                .style("marginTop", "24px")
                                .style("display", "flex")
                                .style("justifyContent", "flex-end")
                                .style("gap", "8px")
                        ) {
                            it()
                        }
                    }
                }
            }
        }
    }

    actual open fun renderSnackbar(
        message: String,
        actionLabel: String?,
        onAction: (() -> Unit)?
    ) {
        // Snackbar is typically shown at the bottom of the screen
        // Creating a div positioned at the bottom
        createElement(
            "div", Modifier()
                .className("snackbar")
                .style("position", "fixed")
                .style("bottom", "20px")
                .style("left", "50%")
                .style("transform", "translateX(-50%)")
                .style("backgroundColor", "#323232")
                .style("color", "white")
                .style("padding", "16px")
                .style("borderRadius", "4px")
                .style("display", "flex")
                .style("alignItems", "center")
                .style("gap", "16px")
                .style("zIndex", "1000")
        ) {
            // Message text
            renderText(message, Modifier())

            // Action button if provided
            if (actionLabel != null && onAction != null) {
                createElement(
                    "button", Modifier()
                        .style("background", "none")
                        .style("border", "none")
                        .style("color", "#4CAF50")
                        .style("cursor", "pointer")
                        .style("textTransform", "uppercase")
                        .style("fontWeight", "bold")
                        .style("padding", "8px"),
                    { element ->
                        registerEventListener(element, "click") { event ->
                            event.preventDefault()
                            onAction()
                        }
                    }
                ) {
                    renderText(actionLabel, Modifier())
                }
            }
        }
    }

    actual open fun renderSpacer(modifier: Modifier) {
        // Spacer is just an empty div with specified dimensions
        createElement("div", modifier)
    }

    actual open fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
    }

    actual open fun renderBadge(modifier: Modifier, content: @Composable (FlowContentCompat.() -> Unit)) {
    }

    actual open fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        renderCheckbox(checked, onCheckedChange, enabled, null, modifier)
    }

    actual open fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
    }

    actual open fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        var inputElement: Element? = null

        inputElement = createElement("input", modifier, { element ->
            element.setAttribute("type", "file")
            if (multiple) element.setAttribute("multiple", "")
            accept?.let { element.setAttribute("accept", it) }
            capture?.let { element.setAttribute("capture", it) }
            if (!enabled) element.setAttribute("disabled", "disabled")

            registerEventListener(element, "change") { event ->
                val files = event.target.asDynamic().files
                val fileList = mutableListOf<FileInfo>()
                val length = files.length as Int
                for (i in 0 until length) {
                    val file = files[i]
                    fileList.add(
                        FileInfo(
                            name = js("file.name") as String,
                            size = js("file.size") as Long,
                            type = js("file.type") as String,
                            jsFile = file
                        )
                    )
                }
                onFilesSelected(fileList)
            }
        })

        // Return a trigger function that programmatically clicks the input
        return {
            inputElement?.asDynamic()?.click()
        }
    }

    actual open fun renderForm(
        onSubmit: (() -> Unit)?,
        modifier: Modifier,
        content: @Composable (FormContent.() -> Unit)
    ) {
        createElement("form", modifier, { element ->
            if (onSubmit != null) {
                registerEventListener(element, "submit") { event ->
                    event.preventDefault()
                    onSubmit()
                }
            }
        }) {
            content(createFlowContent("form"))
        }
    }

    actual open fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
    }

    actual open fun renderNativeInput(
        type: String,
        modifier: Modifier,
        value: String?,
        isChecked: Boolean?
    ) {
        createElement("input", modifier, { element ->
            element.setAttribute("type", type)
            val inputElement = element as? HTMLInputElement
            if (value != null && inputElement != null && inputElement.value != value) {
                inputElement.value = value
                inputElement.defaultValue = value
            }
            if (isChecked != null && inputElement != null) {
                inputElement.checked = isChecked
            }
        })
    }

    actual open fun renderNativeTextarea(
        modifier: Modifier,
        value: String?
    ) {
        createElement("textarea", modifier, { element ->
            val textarea = element as? HTMLTextAreaElement
            val resolvedValue = value ?: ""
            if (textarea != null && textarea.value != resolvedValue) {
                textarea.value = resolvedValue
                textarea.defaultValue = resolvedValue
            }
            if (element.textContent != resolvedValue) {
                element.textContent = resolvedValue
            }
        })
    }

    actual open fun renderNativeSelect(
        modifier: Modifier,
        options: List<NativeSelectOption>
    ) {
        createElement("select", modifier, { element ->
            val selectElement = element as? HTMLSelectElement ?: return@createElement

            while (selectElement.firstChild != null) {
                selectElement.removeChild(selectElement.firstChild!!)
            }

            options.forEach { optionConfig ->
                val optionElement = (element.ownerDocument ?: document).createElement("option") as HTMLOptionElement
                optionElement.value = optionConfig.value
                optionElement.text = optionConfig.label
                optionElement.selected = optionConfig.isSelected
                optionElement.disabled = optionConfig.isDisabled || optionConfig.isPlaceholder
                optionElement.hidden = optionConfig.isPlaceholder
                selectElement.appendChild(optionElement)
            }
        })
    }

    actual open fun renderNativeButton(
        type: String,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        createElement("button", modifier, { element ->
            element.setAttribute("type", type)
        }) {
            content(createFlowContent("button"))
        }
    }

    actual open fun renderRadioButton(selected: Boolean, onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {
        renderRadioButton(selected, { onClick() }, null, enabled, modifier)
    }

    actual open fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Range sliders require custom implementation with two inputs
        // For now, create a wrapper div with two range inputs
        createElement("div", modifier.className("range-slider")) {
            // Start value slider
            createElement("input", Modifier().attribute("type", "range"), { element ->
                element.setAttribute("min", valueRange.start.toString())
                element.setAttribute("max", valueRange.endInclusive.toString())
                element.setAttribute("value", value.start.toString())
                if (steps > 0) {
                    val stepValue = (valueRange.endInclusive - valueRange.start) / (steps + 1)
                    element.setAttribute("step", stepValue.toString())
                }
                if (!enabled) element.setAttribute("disabled", "disabled")

                registerEventListener(element, "input") { event ->
                    val newStart = (event.target.asDynamic().value as String).toFloat()
                    val newEnd = maxOf(newStart, value.endInclusive)
                    onValueChange(newStart..newEnd)
                }
            })

            // End value slider
            createElement("input", Modifier().attribute("type", "range"), { element ->
                element.setAttribute("min", valueRange.start.toString())
                element.setAttribute("max", valueRange.endInclusive.toString())
                element.setAttribute("value", value.endInclusive.toString())
                if (steps > 0) {
                    val stepValue = (valueRange.endInclusive - valueRange.start) / (steps + 1)
                    element.setAttribute("step", stepValue.toString())
                }
                if (!enabled) element.setAttribute("disabled", "disabled")

                registerEventListener(element, "input") { event ->
                    val newEnd = (event.target.asDynamic().value as String).toFloat()
                    val newStart = minOf(value.start, newEnd)
                    onValueChange(newStart..newEnd)
                }
            })
        }
    }

    actual open fun renderAspectRatio(
        ratio: Float,
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        // Use padding-bottom trick to maintain aspect ratio
        val aspectRatioModifier = modifier
            .style("position", "relative")
            .style("paddingBottom", "${(1f / ratio) * 100}%")

        createElement("div", aspectRatioModifier) {
            createElement(
                "div", Modifier()
                    .style("position", "absolute")
                    .style("top", "0")
                    .style("left", "0")
                    .style("width", "100%")
                    .style("height", "100%")
            ) {
                content(createFlowContent("div"))
            }
        }
    }

    actual open fun renderAspectRatioContainer(ratio: Float, modifier: Modifier, content: @Composable () -> Unit) {
        val aspectRatioModifier = modifier.style("padding-bottom", "${(1f / ratio) * 100}%")
            .style("position", "relative")
        createElement("div", aspectRatioModifier) {
            createElement(
                "div",
                Modifier().style("position", "absolute").style("top", "0").style("left", "0").style("width", "100%")
                    .style("height", "100%")
            ) {
                content()
            }
        }
    }

    actual open fun renderHtml(htmlContent: String, modifier: Modifier, sanitize: Boolean) {
        val safeContent = if (sanitize) sanitizeHtml(htmlContent) else htmlContent
        val element = createElement("div", modifier)
        element.innerHTML = safeContent
    }

    actual open fun renderGlobalStyle(css: String) {
        val head = kotlinx.browser.document.head
        val style = kotlinx.browser.document.createElement("style")
        style.textContent = css
        head?.appendChild(style)
    }

    private fun sanitizeHtml(htmlContent: String): String {
        // Basic HTML sanitization for client-side
        var sanitized = htmlContent

        // Remove script tags
        sanitized = sanitized.replace(Regex("<script[^>]*>.*?</script>", RegexOption.IGNORE_CASE), "")

        // Remove dangerous event handlers
        sanitized = sanitized.replace(Regex("\\s(on\\w+)=[\"'][^\"']*[\"']", RegexOption.IGNORE_CASE), "")

        // Remove javascript: URLs
        sanitized = sanitized.replace(Regex("\\shref=[\"']javascript:[^\"']*[\"']", RegexOption.IGNORE_CASE), "")

        return sanitized
    }

    actual open fun renderModal(
        onDismiss: () -> Unit,
        modifier: Modifier,
        variant: codes.yousef.summon.components.feedback.ModalVariant,
        size: codes.yousef.summon.components.feedback.ModalSize,
        dismissOnBackdropClick: Boolean,
        showCloseButton: Boolean,
        header: (@Composable () -> Unit)?,
        footer: (@Composable () -> Unit)?,
        content: @Composable () -> Unit
    ) {
        // Create modal overlay with styles
        val overlayModifier = modifier
            .style("position", "fixed")
            .style("top", "0")
            .style("left", "0")
            .style("width", "100%")
            .style("height", "100%")
            .style("background-color", "rgba(0, 0, 0, 0.5)")
            .style("z-index", "1000")
            .style("display", "flex")
            .style("align-items", "center")
            .style("justify-content", "center")

        // Create modal dialog
        val modalSize = when (size) {
            codes.yousef.summon.components.feedback.ModalSize.SMALL -> "400px"
            codes.yousef.summon.components.feedback.ModalSize.MEDIUM -> "600px"
            codes.yousef.summon.components.feedback.ModalSize.LARGE -> "800px"
            codes.yousef.summon.components.feedback.ModalSize.EXTRA_LARGE -> "1000px"
        }

        val modalModifier = Modifier()
            .style("background-color", "#ffffff")
            .style("border-radius", "8px")
            .style("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.3)")
            .style("max-height", "90vh")
            .style("overflow", "auto")
            .style("max-width", modalSize)
            .let { baseModifier ->
                when (variant) {
                    codes.yousef.summon.components.feedback.ModalVariant.ALERT -> {
                        baseModifier.style("border", "2px solid #ff6b6b")
                    }

                    codes.yousef.summon.components.feedback.ModalVariant.CONFIRMATION -> {
                        baseModifier.style("border", "2px solid #4ecdc4")
                    }

                    codes.yousef.summon.components.feedback.ModalVariant.FULLSCREEN -> {
                        baseModifier
                            .style("width", "100vw")
                            .style("height", "100vh")
                            .style("border-radius", "0")
                    }

                    else -> baseModifier
                }
            }

        createElement("div", overlayModifier, { overlayElement ->
            // Handle backdrop click
            if (dismissOnBackdropClick) {
                overlayElement.addEventListener("click", { event ->
                    // Only dismiss if clicking the overlay itself, not the modal content
                    if (event.target == overlayElement) {
                        onDismiss()
                    }
                })
            }

            // Handle ESC key
            val escKeyHandler = { event: dynamic ->
                if (event.keyCode == 27) { // ESC key
                    onDismiss()
                }
            }
            js("document.addEventListener('keydown', escKeyHandler)")
        }) {
            createElement("div", modalModifier, { modalElement ->
                // Prevent event bubbling
                modalElement.addEventListener("click", { event ->
                    event.stopPropagation()
                })
            }) {
                // Modal header
                header?.let { headerContent ->
                    val headerModifier = Modifier()
                        .style("border-bottom", "1px solid #e0e0e0")
                        .style("display", "flex")
                        .style("justify-content", "space-between")
                        .style("align-items", "center")

                    createElement("div", headerModifier) {
                        headerContent()

                        // Close button
                        if (showCloseButton) {
                            val closeButtonModifier = Modifier()
                                .style("background-color", "transparent")
                                .style("border", "none")
                                .style("font-size", "24px")
                                .style("cursor", "pointer")
                                .style("padding", "8px")

                            createElement("button", closeButtonModifier, { closeButton ->
                                closeButton.textContent = "×"
                                closeButton.addEventListener("click", { onDismiss() })
                            })
                        }
                    }
                }

                // Modal content
                createElement("div", Modifier()) {
                    content()
                }

                // Modal footer
                footer?.let { footerContent ->
                    val footerModifier = Modifier()
                        .style("border-top", "1px solid #e0e0e0")

                    createElement("div", footerModifier) {
                        footerContent()
                    }
                }
            }
        }
    }

    actual open fun renderLoading(
        modifier: Modifier,
        variant: codes.yousef.summon.components.feedback.LoadingVariant,
        size: codes.yousef.summon.components.feedback.LoadingSize,
        text: String?,
        textModifier: Modifier
    ) {
        val sizeValue = when (size) {
            codes.yousef.summon.components.feedback.LoadingSize.SMALL -> 16
            codes.yousef.summon.components.feedback.LoadingSize.MEDIUM -> 24
            codes.yousef.summon.components.feedback.LoadingSize.LARGE -> 32
            codes.yousef.summon.components.feedback.LoadingSize.EXTRA_LARGE -> 48
        }

        val containerModifier = modifier
            .style("display", "flex")
            .style("flex-direction", "column")
            .style("align-items", "center")
            .style("gap", "8px")

        createElement("div", containerModifier, { container ->

            when (variant) {
                codes.yousef.summon.components.feedback.LoadingVariant.SPINNER -> {
                    val spinnerModifier = Modifier()
                        .style("width", "${sizeValue}px")
                        .style("height", "${sizeValue}px")
                        .style("border", "2px solid #f3f3f3")
                        .style("border-top", "2px solid #3498db")
                        .style("border-radius", "50%")
                        .style("animation", "summon-spin 2s linear infinite")

                    elementStack.withElement(container) {
                        createElement("div", spinnerModifier)
                    }
                }

                codes.yousef.summon.components.feedback.LoadingVariant.DOTS -> {
                    val dotsContainerModifier = Modifier()
                        .style("display", "flex")
                        .style("align-items", "center")
                        .style("gap", "4px")

                    elementStack.withElement(container) {
                        createElement("div", dotsContainerModifier) {
                            repeat(3) { index ->
                                val dotModifier = Modifier()
                                    .style("width", "${sizeValue / 3}px")
                                    .style("height", "${sizeValue / 3}px")
                                    .style("background-color", "#3498db")
                                    .style("border-radius", "50%")
                                    .style(
                                        "animation",
                                        "summon-dot-pulse 1.4s ease-in-out ${index * 0.16}s infinite both"
                                    )

                                createElement("div", dotModifier)
                            }
                        }
                    }
                }

                codes.yousef.summon.components.feedback.LoadingVariant.LINEAR -> {
                    val linearContainerModifier = Modifier()
                        .style("width", "${sizeValue}px")
                        .style("height", "4px")
                        .style("background-color", "#f3f3f3")
                        .style("border-radius", "2px")
                        .style("overflow", "hidden")
                        .style("position", "relative")

                    elementStack.withElement(container) {
                        createElement("div", linearContainerModifier) {
                            val progressBarModifier = Modifier()
                                .style("position", "absolute")
                                .style("top", "0")
                                .style("left", "-100%")
                                .style("width", "100%")
                                .style("height", "100%")
                                .style("background-color", "#3498db")
                                .style("animation", "summon-linear-progress 2s linear infinite")

                            createElement("div", progressBarModifier)
                        }
                    }
                }

                codes.yousef.summon.components.feedback.LoadingVariant.CIRCULAR -> {
                    val circularContainerModifier = Modifier()
                        .style("width", "${sizeValue}px")
                        .style("height", "${sizeValue}px")
                        .style("position", "relative")

                    elementStack.withElement(container) {
                        createElement("div", circularContainerModifier) {
                            val backgroundCircleModifier = Modifier()
                                .style("position", "absolute")
                                .style("width", "100%")
                                .style("height", "100%")
                                .style("border", "2px solid #f3f3f3")
                                .style("border-radius", "50%")

                            createElement("div", backgroundCircleModifier)

                            val progressCircleModifier = Modifier()
                                .style("position", "absolute")
                                .style("width", "100%")
                                .style("height", "100%")
                                .style("border", "2px solid transparent")
                                .style("border-top", "2px solid #3498db")
                                .style("border-radius", "50%")
                                .style("animation", "summon-circular-progress 1.5s linear infinite")

                            createElement("div", progressCircleModifier)
                        }
                    }
                }
            }

            text?.let {
                val textElementModifier = textModifier
                    .style("font-size", "14px")
                    .style("color", "#666")
                    .style("text-align", "center")

                elementStack.withElement(container) {
                    createElement("div", textElementModifier, { element ->
                        element.textContent = it
                    })
                }
            }
        }) {
            // Content is handled above
        }

        // Ensure CSS animations are added to the document
        ensureLoadingAnimations()
    }

    private fun ensureLoadingAnimations() {
        if (document.getElementById("summon-loading-animations") != null) return

        val style = document.createElement("style") as HTMLStyleElement
        style.id = "summon-loading-animations"
        style.textContent = """
            @keyframes summon-spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
            
            @keyframes summon-dot-pulse {
                0%, 80%, 100% {
                    transform: scale(0);
                    opacity: 0.5;
                }
                40% {
                    transform: scale(1);
                    opacity: 1;
                }
            }
            
            @keyframes summon-linear-progress {
                0% { left: -100%; }
                100% { left: 100%; }
            }
            
            @keyframes summon-circular-progress {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        """.trimIndent()

        document.head!!.appendChild(style)
    }

    actual open fun renderToast(
        toast: codes.yousef.summon.components.feedback.ToastData,
        onDismiss: () -> Unit,
        modifier: Modifier
    ) {
        val (bgColor, borderColor, textColor) = when (toast.variant) {
            codes.yousef.summon.components.feedback.ToastVariant.INFO -> Triple("#e3f2fd", "#2196f3", "#1976d2")
            codes.yousef.summon.components.feedback.ToastVariant.SUCCESS -> Triple("#e8f5e8", "#4caf50", "#388e3c")
            codes.yousef.summon.components.feedback.ToastVariant.WARNING -> Triple("#fff3e0", "#ff9800", "#f57c00")
            codes.yousef.summon.components.feedback.ToastVariant.ERROR -> Triple("#ffebee", "#f44336", "#d32f2f")
        }

        val toastModifier = modifier
            .style("background-color", bgColor)
            .style("border", "1px solid $borderColor")
            .style("border-radius", "6px")
            .style("padding", "12px 16px")
            .style("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
            .style("display", "flex")
            .style("align-items", "center")
            .style("justify-content", "space-between")
            .style("gap", "12px")
            .style("min-width", "300px")
            .style("max-width", "400px")
            .style("animation", "summon-toast-slide-in 0.3s ease-out")

        createElement("div", toastModifier, { container ->

            // Toast content
            val contentModifier = Modifier()
                .style("flex", "1")
                .style("color", textColor)
                .style("font-size", "14px")
                .style("line-height", "1.4")

            elementStack.withElement(container) {
                createElement("div", contentModifier, { element ->
                    element.textContent = toast.message
                })

                // Action button and dismiss button container
                val actionsContainerModifier = Modifier()
                    .style("display", "flex")
                    .style("align-items", "center")
                    .style("gap", "8px")

                createElement("div", actionsContainerModifier) {
                    // Action button if provided
                    toast.action?.let { action ->
                        val actionButtonModifier = Modifier()
                            .style("background", "transparent")
                            .style("border", "1px solid $borderColor")
                            .style("color", textColor)
                            .style("padding", "4px 8px")
                            .style("border-radius", "4px")
                            .style("font-size", "12px")
                            .style("cursor", "pointer")
                            .style("transition", "background-color 0.2s")

                        createElement("button", actionButtonModifier, { element ->
                            element.textContent = action.label
                            registerEventListener(element, "click") { event ->
                                event.preventDefault()
                                action.onClick()
                            }
                        })
                    }

                    // Dismiss button if dismissible
                    if (toast.dismissible) {
                        val dismissButtonModifier = Modifier()
                            .style("background", "transparent")
                            .style("border", "none")
                            .style("color", textColor)
                            .style("font-size", "16px")
                            .style("cursor", "pointer")
                            .style("padding", "0")
                            .style("width", "20px")
                            .style("height", "20px")
                            .style("display", "flex")
                            .style("align-items", "center")
                            .style("justify-content", "center")
                            .style("opacity", "0.7")
                            .style("transition", "opacity 0.2s")

                        createElement("button", dismissButtonModifier, { element ->
                            element.textContent = "×"
                            registerEventListener(element, "click") { event ->
                                event.preventDefault()
                                onDismiss()
                            }
                        })
                    }
                }
            }
        }) {
            // Content is handled above
        }

        // Ensure toast animations are added to the document
        ensureToastAnimations()
    }

    private fun ensureToastAnimations() {
        if (document.getElementById("summon-toast-animations") != null) return

        val style = document.createElement("style") as HTMLStyleElement
        style.id = "summon-toast-animations"
        style.textContent = """
            @keyframes summon-toast-slide-in {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            
            @keyframes summon-toast-slide-out {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(100%);
                    opacity: 0;
                }
            }
        """.trimIndent()

        document.head!!.appendChild(style)
    }
}
