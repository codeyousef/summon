package code.yousef.summon.runtime

// Import extension functions for Element
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.withAttribute
import code.yousef.summon.modifier.withAttributes
import kotlinx.browser.document
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.*
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
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

    private fun createElement(
        tagName: String,
        modifier: Modifier,
        setup: ((Element) -> Unit)? = null,
        content: (@Composable () -> Unit)? = null
    ): Element {
        val element = document.createElement(tagName)

        // Apply modifiers
        applyModifier(element, modifier)

        // Apply custom setup
        setup?.invoke(element)

        // Add to current parent
        elementStack.current.appendChild(element)

        // Render content if provided
        if (content != null) {
            elementStack.push(element)
            try {
                content()
            } finally {
                elementStack.pop()
            }
        }

        return element
    }

    private fun applyModifier(element: Element, modifier: Modifier) {
        // Apply styles using the toStyleString method which converts camelCase properties to kebab-case
        val styleString = modifier.toStyleString()
        if (js("styleString.length > 0") as Boolean) {
            element.setAttribute("style", styleString)
        }

        // Apply other attributes from the modifier
        for ((key, value) in modifier.attributes) {
            if (key == "data-hover-styles") {
                // Create a unique class name for this element's hover styles
                val uniqueClassName = "hover-${js("Math.random().toString(36).substring(2, 10)") as String}"

                // Add the class to the element
                val currentClasses = element.getAttribute("class") ?: ""
                element.setAttribute("class", "$currentClasses $uniqueClassName")

                // Create a style element for the hover styles
                val styleElement = document.createElement("style")
                val hoverStyles = value.split(";").filter { it.isNotEmpty() }
                    .joinToString("") { "  ${it.trim()};\n" }

                styleElement.textContent = """
                    .$uniqueClassName:hover {
                    $hoverStyles
                    }
                """.trimIndent()

                // Add the style element to the document head
                document.head?.appendChild(styleElement)
            } else {
                element.setAttribute(key, value)
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
        content: @Composable FlowContent.() -> Unit
    ) {
        // Create a style element for button variants if it doesn't exist yet
        if (document.getElementById("summon-button-styles") == null) {
            val styleElement = document.createElement("style")
            styleElement.id = "summon-button-styles"
            styleElement.textContent = """
                /* Primary Button - Purple background with white text and rounded corners */
                button.summon-btn-primary {
                    background-color: #6200ee !important;
                    color: #ffffff !important;
                    border: none !important;
                    padding: 12px 24px !important;
                    border-radius: 30px !important;
                    font-weight: 700 !important;
                    text-transform: uppercase !important;
                    letter-spacing: 1px !important;
                    transition: all 0.3s ease !important;
                    box-shadow: 0 4px 8px rgba(98, 0, 238, 0.3) !important;
                }

                button.summon-btn-primary:hover {
                    background-color: #7c4dff !important;
                    transform: translateY(-3px) !important;
                    box-shadow: 0 6px 12px rgba(98, 0, 238, 0.4) !important;
                }

                /* Secondary Button - White background with gray text and border */
                button.summon-btn-secondary {
                    background-color: #ffffff !important;
                    color: #6c757d !important;
                    border: 2px solid #6c757d !important;
                    padding: 10px 16px !important;
                    border-radius: 6px !important;
                    font-weight: 600 !important;
                    text-transform: none !important;
                    transition: all 0.3s ease !important;
                    box-shadow: none !important;
                }

                button.summon-btn-secondary:hover {
                    background-color: #f8f9fa !important;
                    color: #5c636a !important;
                    border-color: #5c636a !important;
                    transform: translateY(-2px) !important;
                    box-shadow: 0 2px 4px rgba(108, 117, 125, 0.2) !important;
                }
            """.trimIndent()
            document.head?.appendChild(styleElement)
        }

        // Extract the variant from the modifier
        val variant = modifier.attributes["data-variant"]

        // Create a new modifier without certain style properties that would conflict with our CSS classes
        val filteredStyles = modifier.styles.filterKeys { key -> 
            when (variant) {
                "primary", "secondary" -> !listOf(
                    // Colors and borders
                    "background-color", "color", "border", "border-color", 
                    "border-width", "border-style", "border-radius",
                    // Typography
                    "font-weight", "text-transform", "letter-spacing", "font-size",
                    // Spacing
                    "padding", "padding-top", "padding-right", "padding-bottom", "padding-left",
                    "margin", "margin-top", "margin-right", "margin-bottom", "margin-left",
                    // Effects
                    "box-shadow", "transform", "transition", "box-shadow",
                    // Hover effects will be handled by CSS
                    "hover"
                ).contains(key)
                else -> true
            }
        }

        // Also filter out hover styles attribute for primary and secondary buttons
        val filteredAttributes = if (variant == "primary" || variant == "secondary") {
            modifier.attributes.filterKeys { key -> key != "data-hover-styles" }
        } else {
            modifier.attributes
        }

        val filteredModifier = Modifier(
            styles = filteredStyles,
            attributes = filteredAttributes
        )

        createElement("button", filteredModifier, { element ->
            element.addEventListener("click", { onClick() })

            // Add variant-specific class based on data-variant attribute
            when (variant) {
                "primary" -> {
                    val currentClasses = element.getAttribute("class") ?: ""
                    element.setAttribute("class", "$currentClasses summon-btn-primary")
                }
                "secondary" -> {
                    val currentClasses = element.getAttribute("class") ?: ""
                    element.setAttribute("class", "$currentClasses summon-btn-secondary")
                }
            }
        }) {
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
            element.setAttribute("value", value)
            element.addEventListener("input", { event ->
                val inputValue = js("event.target.value")
                onValueChange(inputValue as String)
            })
        })
    }

    actual open fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        modifier: Modifier
    ) {
        createElement("select", modifier, { element ->
            // Create option elements
            options.forEachIndexed { index, option ->
                val optionElement = document.createElement("option")
                optionElement.textContent = option.label
                optionElement.value = index.toString()
                optionElement.disabled = option.disabled

                // Set selected state
                if (option.value == selectedValue) {
                    optionElement.selected = true
                }

                element.appendChild(optionElement)
            }

            // Add change event listener
            element.addEventListener("change", { event ->
                val selectElement = event.target as HTMLElement
                val selectedIndex = js("selectElement.selectedIndex")
                if (selectedIndex >= 0 && selectedIndex < options.size) {
                    onSelectedChange(options[selectedIndex as Int].value)
                } else {
                    onSelectedChange(null)
                }
            })
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

            element.addEventListener("change", { event ->
                val dateValue = js("event.target.value") as String?
                if (dateValue != null && js("dateValue.length > 0") as Boolean) {
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
            })
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

            element.addEventListener("input", { event ->
                val textareaValue = js("event.target.value")
                onValueChange(textareaValue as String)
            })
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

    actual open fun renderComposable(composable: @Composable () -> Unit) {
        // This function usually relies on an existing parent from the elementStack
        // It's primarily for rendering nested composables within an existing structure
        composable()
    }

    actual open fun renderComposableRoot(composable: @Composable () -> Unit): String {
        // Create a detached root element for rendering if not using a specific container
        val rootElement = document.createElement("div")
        // Temporarily set this as the current element for rendering
        elementStack.withElement(rootElement) {
            composable()
        }
        return rootElement.outerHTML
    }

    // Helper to create FlowContent
    private fun createFlowContent(tagName: String): FlowContent {
        val consumer = createTagConsumer()
        val attrs = mutableMapOf<String, String>()

        return object : FlowContent {
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
        content: @Composable (FlowContent.() -> Unit)
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
        content: @Composable (FlowContent.() -> Unit)
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

    actual open fun renderBox(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
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
        // Assuming IconType has a method to get its SVG path or similar representation
        // For simplicity, let's assume it's a class name for a font icon or an SVG string
        // This needs a more concrete implementation based on how IconType is defined
        createElement("i", modifier, setup = { element -> // Explicitly named 'setup'
            // If IconType provides a class name:
            // element.className = icon.getClassName()
            // If IconType provides SVG content:
            // element.innerHTML = icon.getSvgContent()
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
        // Checkboxes are often complex with labels. Simplistic version:
        createElement("input", modifier, { element ->
            element.setAttribute("type", "checkbox")
            element.asDynamic().checked = checked
            if (!enabled) element.setAttribute("disabled", "disabled")
            element.addEventListener("change", { event ->
                onCheckedChange(js("event.target.checked") as Boolean)
            })
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
            element.addEventListener("change", { event ->
                onCheckedChange(js("event.target.checked") as Boolean)
            })
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

            element.addEventListener("input", { event ->
                onValueChange((js("event.target.value") as String).toFloat())
            })
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

    actual open fun renderCard(modifier: Modifier, content: @Composable (FlowContent.() -> Unit)) {
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

            element.addEventListener("change", { event ->
                val files = js("event.target.files")
                val fileList = mutableListOf<FileInfo>()
                val length = js("files.length") as Int
                for (i in 0 until length) {
                    val file = js("files[i]")
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
            })
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

            element.addEventListener("change", { event ->
                val timeValue = js("event.target.value") as String?
                if (timeValue != null && js("timeValue.length > 0") as Boolean) {
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
            })
        })
    }


    actual open fun renderHorizontalPager(
        count: Int,
        state: Any, // PagerState equivalent
        modifier: Modifier,
        content: @Composable (page: Int) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    actual open fun renderVerticalPager(
        count: Int,
        state: Any, // PagerState equivalent
        modifier: Modifier,
        content: @Composable (page: Int) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    actual open fun renderSwipeToDismiss(
        state: Any, // DismissState equivalent
        background: @Composable () -> Unit,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        TODO("Not yet implemented")
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
        content: @Composable (FlowContent.() -> Unit)
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
        modifier: Modifier
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
                        element.addEventListener("click", {
                            onTabSelected(index)
                        })
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
                        element.addEventListener("click", {
                            onTabSelected(tab)
                        })
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
        TODO("Not yet implemented")
    }

    actual open fun renderAnimatedVisibility(
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        TODO("Not yet implemented")
    }

    actual open fun renderAnimatedContent(modifier: Modifier) {
        // Simple animated content container with transition classes
        createElement("div", modifier.className("animated-content"))
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
        content: @Composable (FlowContent.() -> Unit)
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
        content: @Composable (FlowContent.() -> Unit)
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
        content: @Composable (FlowContent.() -> Unit)
    ) {
        createElement("div", modifier) {
            content(createFlowContent("div"))
        }
    }

    actual open fun renderSpan(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
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
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Expansion panels typically have a header and expandable content
        // Using details/summary HTML elements for native expand/collapse behavior
        createElement("details", modifier) {
            content(createFlowContent("details"))
        }
    }

    actual open fun renderGrid(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
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
        content: @Composable (FlowContent.() -> Unit)
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
        content: @Composable (FlowContent.() -> Unit)
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
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Responsive layout container with flexbox for adaptability
        val responsiveModifier = modifier
            .style("display", "flex")
            .style("flexWrap", "wrap")
            .style("width", "100%")

        createElement("div", responsiveModifier) {
            content(createFlowContent("div"))
        }
    }

    actual open fun renderHtmlTag(
        tagName: String,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        createElement(tagName, modifier) {
            content(createFlowContent(tagName))
        }
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
                    element.addEventListener("click", { event ->
                        // Only dismiss if clicking backdrop, not modal content
                        if (event.target == element) {
                            onDismissRequest()
                        }
                    })
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
                        element.addEventListener("click", { event ->
                            event.stopPropagation()
                        })
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
                        element.addEventListener("click", { onAction() })
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
        content: @Composable (FlowContent.() -> Unit)
    ) {
    }

    actual open fun renderBadge(modifier: Modifier, content: @Composable (FlowContent.() -> Unit)) {
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

            element.addEventListener("change", { event ->
                val files = js("event.target.files")
                val fileList = mutableListOf<FileInfo>()
                val length = js("files.length") as Int
                for (i in 0 until length) {
                    val file = js("files[i]")
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
            })
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
                element.addEventListener("submit", { event ->
                    event.preventDefault() // Prevent default form submission
                    onSubmit()
                })
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
        content: @Composable (FlowContent.() -> Unit)
    ) {
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

                element.addEventListener("input", { event ->
                    val newStart = (js("event.target.value") as String).toFloat()
                    val newEnd = maxOf(newStart, value.endInclusive)
                    onValueChange(newStart..newEnd)
                })
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

                element.addEventListener("input", { event ->
                    val newEnd = (js("event.target.value") as String).toFloat()
                    val newStart = minOf(value.start, newEnd)
                    onValueChange(newStart..newEnd)
                })
            })
        }
    }

    actual open fun renderAspectRatio(ratio: Float, modifier: Modifier, content: @Composable (FlowContent.() -> Unit)) {
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
}
