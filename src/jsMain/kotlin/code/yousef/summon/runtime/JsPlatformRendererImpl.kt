package code.yousef.summon.runtime

// Import extension functions for Element
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.toStyleString
import code.yousef.summon.toStyleStringCamelCase
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
class JsPlatformRendererImpl : PlatformRenderer {
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
            element.setAttribute(key, value)
        }
    }

    override fun renderText(text: String, modifier: Modifier) {
        createElement("span", modifier, { element ->
            element.textContent = text
        })
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

    override fun renderColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
        val columnModifier = Modifier(
            modifier.styles + mapOf(
                "display" to "flex",
                "flexDirection" to "column"
            )
        )

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
