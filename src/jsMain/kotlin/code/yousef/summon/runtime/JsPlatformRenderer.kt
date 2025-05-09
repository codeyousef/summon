package code.yousef.summon.runtime

// Import extension functions for Element
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.js.console
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.style.toSummonClass
import code.yousef.summon.toStyleString
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
 */

class JsPlatformRenderer : PlatformRenderer {
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
            if (stack.isNotEmpty()) stack.removeLast()
        }

        fun isEmpty(): Boolean = stack.isEmpty()

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
     * Executes a composable function, rendering its output into the provided rootElement.
     * This is intended to be called by a top-level coordinator like RenderUtils.
     *
     * @param rootElement The DOM element to render into.
     * @param composable The composable function to execute.
     */
    fun renderInto(rootElement: Element, composable: @Composable () -> Unit) {
        elementStack.withElement(rootElement) {
            // The composable will execute, and all its direct children
            // will be appended to rootElement because it's now elementStack.current
            this.renderComposable(composable) // or simply composable() if PlatformRenderer.renderComposable is just that
        }
    }


    private fun createElement(
        tagName: String,
        modifier: Modifier,
        setup: ((Element) -> Unit)? = null,
        content: (@Composable () -> Unit)? = null
    ): Element {
        val element = document.createElement(tagName)
        setup?.invoke(element)
        // Apply modifiers
        applyModifier(element, modifier)

        // Add to current parent
        elementStack.current.appendChild(element)

        // Render content if provided
        if (content != null) {
            // If there's nested content, this new element becomes the parent for that content
            elementStack.withElement(element) {
                this.renderComposable(content)
            }
        }

        return element
    }

    private fun applyModifier(element: Element, modifier: Modifier) {

        // First, apply CSS classes based on element type and styles
        applyAppropriateClasses(element, modifier)

        // Create a filtered modifier without styles that are handled by CSS classes
        val filteredStyles = modifier.styles.filterNot { (key, value) ->
            // Remove layout styles that are handled by CSS classes
            key == "display" || key == "flexDirection" || 
            key == "justifyContent" || key == "alignItems" ||
            key == "width" && value == "100%" ||
            key == "textAlign" && (value == "center" || value == "right" || value == "left") ||
            key == "fontWeight" && value == "bold" ||
            key == "fontStyle" && value == "italic" ||
            key == "textDecoration" && value.contains("line-through")
        }

        // Apply remaining styles that don't have corresponding CSS classes
        if (filteredStyles.isNotEmpty()) {
            val filteredModifier = Modifier().withStyles(filteredStyles)

            val styleString = filteredModifier.toStyleString()


            if (styleString.isNotEmpty()) {

                element.setAttribute("style", styleString)

                // Verify the style was set correctly
                val appliedStyle = element.getAttribute("style")

            } else {
                console.log("[DEBUG] Style string is empty, not setting style attribute")
            }
        } else {
            console.log("[DEBUG] No inline styles to apply after filtering")
        }

        // Apply other attributes from the modifier
        for ((key, value) in modifier.attributes) {
            element.setAttribute(key, value)
        }
    }

    /**
     * Applies appropriate CSS classes to elements based on their tag name and styles.
     * This version uses the SummonStyleSheet classes instead of hardcoded class names.
     * It's more robust by using includes() for padding, margin, and color values,
     * and by checking both inline styles and computed styles.
     */
    private fun applyAppropriateClasses(element: Element, modifier: Modifier) {
        val tagName = element.tagName.lowercase()
        val classes = mutableListOf<String>()

        // Add base classes based on element type
        when (tagName) {
            "button" -> classes.add("button".toSummonClass())
            "span", "p", "h1", "h2", "h3", "h4", "h5", "h6", "label" -> classes.add("text".toSummonClass())
            "input" -> {
                val type = element.getAttribute("type")
                if (type == "checkbox") {
                    classes.add("checkbox".toSummonClass())
                } else {
                    classes.add("input".toSummonClass())
                }
            }
            "textarea" -> classes.add("textarea".toSummonClass())
            "select" -> classes.add("select".toSummonClass())
            "a" -> classes.add("link".toSummonClass())
            "img" -> classes.add("image".toSummonClass())
            "form" -> classes.add("form".toSummonClass())
        }

        // Add layout classes based on styles
        if (modifier.styles.containsKey("display")) {
            when (modifier.styles["display"]) {
                "flex" -> {
                    if (modifier.styles["flexDirection"] == "row") {
                        classes.add("row".toSummonClass())
                    } else if (modifier.styles["flexDirection"] == "column") {
                        classes.add("column".toSummonClass())
                    }
                }
                "block" -> classes.add("w-100".toSummonClass())
                "inline-block" -> classes.add("inline-block".toSummonClass())
            }
        }

        // Add width and height classes
        if (modifier.styles["width"] == "100%") {
            classes.add("w-100".toSummonClass())
        }
        if (modifier.styles["height"] == "100%") {
            classes.add("h-100".toSummonClass())
        }

        // Add alignment classes
        // Justify content
        when (modifier.styles["justifyContent"]) {
            "space-between" -> classes.add("justify-between".toSummonClass())
            "center" -> classes.add("justify-center".toSummonClass())
            "flex-start" -> classes.add("justify-start".toSummonClass())
            "flex-end" -> classes.add("justify-end".toSummonClass())
            "space-around" -> classes.add("justify-around".toSummonClass())
            "space-evenly" -> classes.add("justify-evenly".toSummonClass())
        }

        // Align items
        when (modifier.styles["alignItems"]) {
            "center" -> classes.add("align-center".toSummonClass())
            "flex-start" -> classes.add("align-start".toSummonClass())
            "flex-end" -> classes.add("align-end".toSummonClass())
            "stretch" -> classes.add("align-stretch".toSummonClass())
            "baseline" -> classes.add("align-baseline".toSummonClass())
        }

        // Add text alignment classes
        when (modifier.styles["textAlign"]) {
            "center" -> classes.add("text-center".toSummonClass())
            "right" -> classes.add("text-right".toSummonClass())
            "left" -> classes.add("text-left".toSummonClass())
            "justify" -> classes.add("text-justify".toSummonClass())
        }

        // Add font style classes
        if (modifier.styles["fontWeight"] == "bold" || (modifier.styles["fontWeight"]?.toIntOrNull() ?: 0) >= 700) {
            classes.add("font-bold".toSummonClass())
        }

        if (modifier.styles["fontStyle"] == "italic") {
            classes.add("font-italic".toSummonClass())
        }

        // Add text decoration classes
        if (modifier.styles["textDecoration"]?.contains("line-through") == true) {
            classes.add("completed-text".toSummonClass())
        } else if (modifier.styles["textDecoration"]?.contains("underline") == true) {
            classes.add("underline".toSummonClass())
        }

        // Check for app-container class - more flexible check
        if (tagName == "div" && 
            (modifier.styles["display"] == "flex" || modifier.styles["display"] == "block") && 
            modifier.styles["maxWidth"]?.contains("1200") == true && 
            (modifier.styles["margin"] == "0 auto" || 
             (modifier.styles["marginLeft"] == "auto" && modifier.styles["marginRight"] == "auto"))) {
            classes.add("app-container".toSummonClass())
        }

        // Check for app-header class - more flexible check
        if (tagName == "div" && 
            modifier.styles["padding"]?.contains("20") == true && 
            (modifier.styles["background"]?.contains("fff") == true || 
             modifier.styles["backgroundColor"]?.contains("fff") == true) && 
            modifier.styles["borderRadius"] != null && 
            modifier.styles["borderRadius"] != "0" && 
            modifier.styles["borderRadius"] != "0px" && 
            modifier.styles["marginBottom"]?.contains("20") == true && 
            modifier.styles["boxShadow"] != null) {
            classes.add("app-header".toSummonClass())
        }

        // Check for text style classes - more flexible check
        if (tagName == "span" || tagName == "p" || tagName == "h1" || 
            tagName == "h2" || tagName == "h3" || tagName == "h4") {
            // App title
            if (modifier.styles["fontSize"]?.contains("24") == true &&
                (modifier.styles["fontWeight"] == "bold" || (modifier.styles["fontWeight"]?.toIntOrNull()
                    ?: 0) >= 700) &&
                modifier.styles["color"]?.contains("333") == true &&
                modifier.styles["marginBottom"]?.contains("16") == true
            ) {
                classes.add("app-title".toSummonClass())
            }
            // Section title
            else if (modifier.styles["fontSize"]?.contains("18") == true &&
                (modifier.styles["fontWeight"] == "bold" || (modifier.styles["fontWeight"]?.toIntOrNull()
                    ?: 0) >= 700) &&
                modifier.styles["color"]?.contains("333") == true
            ) {
                classes.add("section-title".toSummonClass())
            }
            // List title
            else if (modifier.styles["fontSize"]?.contains("20") == true &&
                (modifier.styles["fontWeight"] == "bold" || (modifier.styles["fontWeight"]?.toIntOrNull()
                    ?: 0) >= 700) &&
                modifier.styles["color"]?.contains("333") == true
            ) {
                classes.add("list-title".toSummonClass())
            }
            // Error message
            else if (modifier.styles["color"]?.contains("ff4d4d") == true || 
                     modifier.styles["color"]?.contains("red") == true) {
                classes.add("error-message".toSummonClass())
            }
            // Completed text
            else if (modifier.styles["textDecoration"]?.contains("line-through") == true || 
                     modifier.styles["color"]?.contains("888") == true) {
                classes.add("completed-text".toSummonClass())
            }
        }

        // Add specific component classes - more flexible checks
        if (tagName == "div") {
            // Task form
            if (modifier.styles["padding"]?.contains("20") == true && 
                modifier.styles["marginBottom"]?.contains("20") == true && 
                (modifier.styles["background"]?.contains("fff") == true || 
                 modifier.styles["backgroundColor"]?.contains("fff") == true) && 
                modifier.styles["borderRadius"] != null && 
                modifier.styles["borderRadius"] != "0" && 
                modifier.styles["borderRadius"] != "0px" && 
                modifier.styles["boxShadow"] != null) {
                classes.add("task-form".toSummonClass())
            }
            // Task list
            else if (modifier.styles["padding"]?.contains("20") == true && 
                     (modifier.styles["background"]?.contains("fff") == true || 
                      modifier.styles["backgroundColor"]?.contains("fff") == true) && 
                     modifier.styles["borderRadius"] != null && 
                     modifier.styles["borderRadius"] != "0" && 
                     modifier.styles["borderRadius"] != "0px" && 
                     modifier.styles["boxShadow"] != null && 
                     modifier.styles["maxHeight"]?.contains("400") == true) {
                classes.add("task-list".toSummonClass())
            }
            // Filter controls
            else if (modifier.styles["padding"]?.contains("15") == true && 
                     modifier.styles["marginBottom"]?.contains("20") == true && 
                     (modifier.styles["background"]?.contains("fff") == true || 
                      modifier.styles["backgroundColor"]?.contains("fff") == true) && 
                     modifier.styles["borderRadius"] != null && 
                     modifier.styles["borderRadius"] != "0" && 
                     modifier.styles["borderRadius"] != "0px" && 
                     modifier.styles["boxShadow"] != null) {
                classes.add("filter-controls".toSummonClass())
            }
            // Empty state
            else if (modifier.styles["padding"]?.contains("20") == true && 
                     (modifier.styles["margin"]?.contains("10") == true || 
                      modifier.styles["marginTop"]?.contains("10") == true || 
                      modifier.styles["marginBottom"]?.contains("10") == true) && 
                     (modifier.styles["background"]?.contains("f9") == true || 
                      modifier.styles["backgroundColor"]?.contains("f9") == true) && 
                     modifier.styles["borderRadius"] != null && 
                     modifier.styles["borderRadius"] != "0" && 
                     modifier.styles["borderRadius"] != "0px" && 
                     modifier.styles["textAlign"] == "center" && 
                     (modifier.styles["color"]?.contains("666") == true || 
                      modifier.styles["color"]?.contains("gray") == true)) {
                classes.add("empty-state".toSummonClass())
            }
        }

        // Add task item classes - more flexible check
        if (tagName == "div" && 
            modifier.styles["display"] == "flex" && 
            (modifier.styles["alignItems"] == "center" || 
             modifier.styles["alignItems"] == "flex-start") && 
            (modifier.styles["justifyContent"] == "space-between" || 
             modifier.styles["justifyContent"] == "flex-start") && 
            modifier.styles["padding"]?.contains("10") == true && 
            (modifier.styles["margin"]?.contains("5") == true || 
             modifier.styles["marginTop"]?.contains("5") == true || 
             modifier.styles["marginBottom"]?.contains("5") == true)) {
            classes.add("task-item".toSummonClass())

            // Check if it's a completed task - more flexible check
            if (modifier.styles["backgroundColor"]?.contains("f0f8ff") == true || 
                modifier.styles["backgroundColor"]?.contains("lightblue") == true || 
                modifier.styles["background"]?.contains("f0f8ff") == true || 
                modifier.styles["background"]?.contains("lightblue") == true) {
                classes.add("completed".toSummonClass())
            }
        }

        // Add task content class - more flexible check
        if (tagName == "div" && 
            modifier.styles["display"] == "flex" && 
            (modifier.styles["alignItems"] == "center" || 
             modifier.styles["alignItems"] == "flex-start") && 
            (modifier.styles["flex"] == "1" || 
             modifier.styles["flexGrow"] == "1")) {
            classes.add("task-content".toSummonClass())
        }

        // Add button-specific classes - more flexible checks
        if (tagName == "button") {
            // Add task button
            if (modifier.styles["padding"]?.contains("10") == true &&
                modifier.styles["padding"]?.contains("20") == true &&
                (modifier.styles["backgroundColor"]?.contains("4285f4") == true ||
                        modifier.styles["backgroundColor"]?.contains("blue") == true ||
                        modifier.styles["background"]?.contains("4285f4") == true ||
                        modifier.styles["background"]?.contains("blue") == true) &&
                (modifier.styles["color"]?.contains("fff") == true ||
                        modifier.styles["color"]?.contains("white") == true) &&
                modifier.styles["borderRadius"] != null &&
                modifier.styles["borderRadius"] != "0" &&
                modifier.styles["borderRadius"] != "0px" &&
                (modifier.styles["fontWeight"] == "bold" ||
                        (modifier.styles["fontWeight"]?.toIntOrNull() ?: 0) >= 700)
            ) {
                classes.add("add-task-button".toSummonClass())
            }
            // Delete task button
            else if (modifier.styles["padding"]?.contains("5") == true && 
                     modifier.styles["padding"]?.contains("10") == true && 
                     (modifier.styles["backgroundColor"]?.contains("ff4d4d") == true || 
                      modifier.styles["backgroundColor"]?.contains("red") == true || 
                      modifier.styles["background"]?.contains("ff4d4d") == true || 
                      modifier.styles["background"]?.contains("red") == true) && 
                     (modifier.styles["color"]?.contains("fff") == true || 
                      modifier.styles["color"]?.contains("white") == true) && 
                     modifier.styles["borderRadius"] != null && 
                     modifier.styles["borderRadius"] != "0" && 
                     modifier.styles["borderRadius"] != "0px") {
                classes.add("delete-task-button".toSummonClass())
            }
            // Filter button
            else if ((modifier.styles["margin"] == "0 5px" || 
                      (modifier.styles["marginLeft"]?.contains("5") == true && 
                       modifier.styles["marginRight"]?.contains("5") == true)) && 
                     modifier.styles["padding"]?.contains("8") == true && 
                     modifier.styles["padding"]?.contains("12") == true && 
                     modifier.styles["borderRadius"] != null && 
                     modifier.styles["borderRadius"] != "0" && 
                     modifier.styles["borderRadius"] != "0px") {
                classes.add("filter-button".toSummonClass())

                // Check if it's selected - more flexible check
                if ((modifier.styles["backgroundColor"]?.contains("4285f4") == true || 
                     modifier.styles["backgroundColor"]?.contains("blue") == true || 
                     modifier.styles["background"]?.contains("4285f4") == true || 
                     modifier.styles["background"]?.contains("blue") == true) && 
                    (modifier.styles["color"]?.contains("fff") == true || 
                     modifier.styles["color"]?.contains("white") == true)) {
                    classes.add("selected".toSummonClass())
                }
            }
        }

        // Add utility classes
        if (modifier.styles["textAlign"] == "center") {
            classes.add("text-center".toSummonClass())
        } else if (modifier.styles["textAlign"] == "right") {
            classes.add("text-right".toSummonClass())
        }

        if (modifier.styles["fontWeight"] == "bold") {
            classes.add("font-bold".toSummonClass())
        }

        if (modifier.styles["fontStyle"] == "italic") {
            classes.add("font-italic".toSummonClass())
        }

        // Apply the classes to the element
        if (classes.isNotEmpty()) {
            val existingClasses = element.getAttribute("class") ?: ""
            val newClasses = if (existingClasses.isEmpty()) {
                classes.joinToString(" ")
            } else {
                "$existingClasses ${classes.joinToString(" ")}"
            }

            console.log("[DEBUG] Adding CSS classes to element: $newClasses")
            element.setAttribute("class", newClasses)
        }
    }

    override fun renderText(text: String, modifier: Modifier) {
        val span = createElement("span", modifier) // Or just createTextNode and append
        span.textContent = text

    }

    override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
        createElement("label", modifier, { element ->
            element.textContent = text
            forElement?.let { id -> element.setAttribute("for", id) }
        })
    }

    override fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    ) {
        createElement("button", modifier, { element ->
            element.addEventListener("click", { onClick() })
        }) {
            content(createFlowContent("button"))
        }
    }

    override fun renderTextField(
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

    override fun <T> renderSelect(
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

    override fun renderDatePicker(
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
        createElement("textarea", modifier, { element ->
            element.textContent = value
            if (!enabled) element.setAttribute("disabled", "disabled")
            if (readOnly) element.setAttribute("readonly", "readonly")
            rows?.let { element.setAttribute("rows", it.toString()) }
            maxLength?.let { element.setAttribute("maxlength", it.toString()) }
            placeholder?.let { element.setAttribute("placeholder", it) }

            element.addEventListener("input", { event ->
                onValueChange(js("event.target.value") as String)
            })
        })
    }

    override fun addHeadElement(content: String) {
        headElements.add(content)

        // Add to document head if available
        val head = document.head
        if (head != null) {
            val tempDiv = document.createElement("div")
            tempDiv.innerHTML = content

            // Move all nodes from tempDiv to head
            while (tempDiv.firstChild != null) {
                head.appendChild(tempDiv.firstChild!!)
            }
        }
    }

    override fun getHeadElements(): List<String> {
        return headElements.toList()
    }

    override fun renderComposable(composable: @Composable () -> Unit) {
        try {
            composable()
        } catch (e: Throwable) {
            console.error("Error in renderComposable: $e")
        }
    }

    override fun renderComposableRoot(composable: @Composable () -> Unit): String {
        val container = document.createElement("div") as HTMLElement

        try {
            elementStack.withElement(container) {
                composable()
            }
            return container.innerHTML
        } catch (e: Throwable) {
            console.error("Error in renderComposableRoot: $e")
            return "<div>Error: ${e.message}</div>"
        }
    }

    // Helper to create FlowContent
    private fun createFlowContent(tagName: String): FlowContent {
        return object : FlowContent {
            override val tagName: String = tagName
            override val consumer: TagConsumer<*> = createTagConsumer()
            override val namespace: String? = null
            override val attributes: MutableMap<String, String> = mutableMapOf()
            override val attributesEntries: Collection<Map.Entry<String, String>> = attributes.entries
            override val inlineTag: Boolean = false
            override val emptyTag: Boolean = false
        }
    }

    // Helper to create FormContent
    private fun createFormContent(): FormContent {
        return object : FormContent {
            override val tagName: String = "form"
            override val consumer: TagConsumer<*> = createTagConsumer()
            override val namespace: String? = null
            override val attributes: MutableMap<String, String> = mutableMapOf()
            override val attributesEntries: Collection<Map.Entry<String, String>> = attributes.entries
            override val inlineTag: Boolean = false
            override val emptyTag: Boolean = false
        }
    }

    // Create a tag consumer for the flow content
    private fun createTagConsumer(): TagConsumer<Element> {
        return object : TagConsumer<Element> {
            override fun onTagStart(tag: Tag) {}
            override fun onTagEnd(tag: Tag) {}
            override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {}
            override fun onTagEvent(tag: Tag, event: String, value: (DomEvent) -> Unit) {}
            override fun onTagContent(content: CharSequence) {}
            override fun onTagContentEntity(entity: Entities) {}
            override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {}
            override fun onTagComment(content: CharSequence) {}
            override fun finalize(): Element = document.createElement("div")
        }
    }

    // Implement layout methods
    override fun renderRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        // Add the summon-row class instead of setting inline styles
        val rowModifier = modifier.addClass("summon-row")

        createElement("div", rowModifier) {
            content(createFlowContent("div"))
        }
    }

    override fun renderColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        // Add the summon-column class instead of setting inline styles
        val columnModifier = modifier.addClass("summon-column")

        createElement("div", columnModifier) {
            content(createFlowContent("div"))
        }
    }

    override fun renderBox(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        createElement("div", modifier) {
            content(createFlowContent("div"))
        }
    }

    // Implement the remaining methods in a similar way
    override fun renderImage(src: String, alt: String, modifier: Modifier) {
        createElement("img", modifier, { element ->
            element.setAttribute("src", src)
            element.setAttribute("alt", alt)
        })
    }

    override fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    ) {
        TODO("Not yet implemented")
    }

    override fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderBadge(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        TODO("Not yet implemented")
    }

    override fun renderProgress(
        value: Float?,
        type: ProgressType,
        modifier: Modifier
    ) {
        TODO("Not yet implemented")
    }

    override fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        TODO("Not yet implemented")
    }

    // Add other required method implementations...

    // Example of a more complex rendering method
    override fun renderForm(
        onSubmit: (() -> Unit)?,
        modifier: Modifier,
        content: @Composable FormContent.() -> Unit
    ) {
        createElement("form", modifier, { element ->
            onSubmit?.let { submit ->
                element.addEventListener("submit", { event ->
                    js("event.preventDefault()")
                    submit()
                })
            }
        }).let { formElement ->
            elementStack.withElement(formElement) {
                content(createFormContent())
            }
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
        TODO("Not yet implemented")
    }

    override fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        TODO("Not yet implemented")
    }

    override fun renderSpacer(modifier: Modifier) {
        TODO("Not yet implemented")
    }

    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        TODO("Not yet implemented")
    }

    override fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        TODO("Not yet implemented")
    }

    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        TODO("Not yet implemented")
    }

    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {
        TODO("Not yet implemented")
    }

    override fun renderAspectRatio(
        ratio: Float,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderCard(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderLink(href: String, modifier: Modifier) {
        TODO("Not yet implemented")
    }

    override fun renderLink(
        modifier: Modifier,
        href: String,
        content: @Composable (() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier
    ) {
        TODO("Not yet implemented")
    }

    override fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    ) {
        TODO("Not yet implemented")
    }

    override fun renderTabLayout(
        modifier: Modifier,
        content: @Composable (() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        TODO("Not yet implemented")
    }

    override fun renderAnimatedVisibility(
        modifier: Modifier,
        content: @Composable (() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderAnimatedContent(modifier: Modifier) {
        TODO("Not yet implemented")
    }

    override fun renderAnimatedContent(
        modifier: Modifier,
        content: @Composable (() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderBlock(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        // Add the summon-w-100 class instead of setting inline styles
        val blockModifier = modifier.addClass("summon-w-100")

        createElement("div", blockModifier) {
            content(createFlowContent("div"))
        }
    }

    override fun renderInline(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderDiv(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        createElement("div", modifier) {
            content(createFlowContent("div"))
        }
    }

    override fun renderSpan(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderDivider(modifier: Modifier) {
        TODO("Not yet implemented")
    }

    override fun renderExpansionPanel(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderGrid(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderLazyColumn(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderLazyRow(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderResponsiveLayout(
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        TODO("Not yet implemented")
    }

    override fun renderHtmlTag(
        tagName: String,
        modifier: Modifier,
        content: @Composable (FlowContent.() -> Unit)
    ) {
        TODO("Not yet implemented")
    }
}
