@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon

import code.yousef.summon.components.display.*
import code.yousef.summon.components.feedback.*
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.*
import code.yousef.summon.components.navigation.Link
import code.yousef.summon.core.PlatformRenderer
import kotlinx.html.*


/**
 * JS implementation of the PlatformRenderer interface.
 * Provides JS-specific rendering for UI components.
 */
class JsPlatformRenderer : PlatformRenderer {
    /**
     * Renders a Text component as a span element with the text content.
     */
    override fun <T> renderText(text: Text, consumer: TagConsumer<T>): T {
        // Collect all attributes first
        val attributes = mutableMapOf<String, String>()

        // Apply the modifier styles and additional text-specific styles
        val additionalStyles = text.getAdditionalStyles()
        val combinedStyles = text.modifier.styles + additionalStyles
        attributes["style"] = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

        // Apply accessibility attributes
        text.getAccessibilityAttributes().forEach { (key, value) ->
            attributes[key] = value
        }

        consumer.span {
            // Properly apply all collected attributes to the tag
            for ((key, value) in attributes) {
                this.attributes[key] = value
            }

            // Add the text content
            +text.text
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a Button component as a button element with appropriate attributes.
     */
    override fun <T> renderButton(button: Button, consumer: TagConsumer<T>): T {
        val buttonId = "btn-${button.hashCode()}"

        // Collect all attributes first
        val attributes = mutableMapOf<String, String>()

        // Set required attributes
        attributes["id"] = buttonId
        attributes["style"] = button.modifier.toStyleString()
        attributes["data-summon-click"] = "true"

        // Create the button element with all attributes
        consumer.button {
            // Properly apply all collected attributes to the tag
            for ((key, value) in attributes) {
                this.attributes[key] = value
            }

            // Add the button text
            +button.label
        }

        // Set up the click handler for this button
        setupJsClickHandler(buttonId, button)

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a Row component as a div with flex-direction: row.
     */
    override fun <T> renderRow(row: Row, consumer: TagConsumer<T>): T {
        consumer.div {
            val combinedStyles = row.modifier.styles + mapOf(
                "display" to "flex",
                "flex-direction" to "row"
            )
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Render each child
            row.content.forEach { child ->
                child.compose(this)
            }
        }
        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a Column component as a div with flex-direction: column.
     */
    override fun <T> renderColumn(column: Column, consumer: TagConsumer<T>): T {
        consumer.div {
            val combinedStyles = column.modifier.styles + mapOf(
                "display" to "flex",
                "flex-direction" to "column"
            )
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Render each child
            column.content.forEach { child ->
                child.compose(this)
            }
        }
        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a Spacer component as a div with appropriate dimension.
     */
    override fun <T> renderSpacer(spacer: Spacer, consumer: TagConsumer<T>): T {
        consumer.div {
            val styleProp = if (spacer.isVertical) "height" else "width"
            style = "$styleProp:${spacer.size};"
        }
        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a TextField component as an input field with a label.
     */
    override fun <T> renderTextField(textField: TextField, consumer: TagConsumer<T>): T {
        val fieldId = "field-${textField.hashCode()}"

        consumer.div {
            // Apply container styles
            style = "display: flex; flex-direction: column; margin-bottom: 16px;"

            // Add the label if provided
            textField.label?.let {
                label {
                    htmlFor = fieldId
                    style = "margin-bottom: 4px; font-weight: 500;"
                    +it
                }
            }

            // Render the input field
            input {
                id = fieldId
                name = fieldId
                // Convert the TextField type to HTML input type
                type = when (textField.type) {
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

                // Apply modifier styles
                style = textField.modifier.toStyleString()

                // Set current value
                value = textField.state.value

                // Add placeholder if provided
                textField.placeholder?.let {
                    placeholder = it
                }

                // Add a data attribute for input handling
                attributes["data-summon-input"] = fieldId
            }

            // Show validation errors if any
            val errors = textField.getValidationErrors()
            if (errors.isNotEmpty()) {
                div {
                    style = "color: #d32f2f; font-size: 12px; margin-top: 4px;"
                    errors.forEach { error ->
                        div {
                            +error
                        }
                    }
                }
            }
        }

        // Set up the input change handler
        setupJsInputHandler(fieldId, textField)

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a TextArea component as a textarea field with a label.
     */
    override fun <T> renderTextArea(textArea: TextArea, consumer: TagConsumer<T>): T {
        val fieldId = "textarea-${textArea.hashCode()}"

        consumer.div {
            // Apply container styles
            style = "display: flex; flex-direction: column; margin-bottom: 16px;"

            // Add the label if provided
            textArea.label?.let {
                label {
                    htmlFor = fieldId
                    style = "margin-bottom: 4px; font-weight: 500;"
                    +it
                }
            }

            // Render the textarea field
            textArea {
                id = fieldId
                name = fieldId
                rows = "${textArea.rows}"
                cols = "${textArea.columns}"

                // Apply modifier styles
                val baseStyles = textArea.modifier.toStyleString()
                val resizableStyle = if (!textArea.resizable) "; resize: none" else ""
                style = baseStyles + resizableStyle

                // Add maxlength if provided
                textArea.maxLength?.let {
                    maxLength = "$it"
                }

                // Add placeholder if provided
                textArea.placeholder?.let {
                    placeholder = it
                }

                // Set current value as content
                +textArea.state.value

                // Add a data attribute for textarea handling
                attributes["data-summon-textarea"] = fieldId
            }

            // Show validation errors if any
            val errors = textArea.getValidationErrors()
            if (errors.isNotEmpty()) {
                div {
                    style = "color: #d32f2f; font-size: 12px; margin-top: 4px;"
                    errors.forEach { error ->
                        div {
                            +error
                        }
                    }
                }
            }
        }

        // Set up the textarea change handler
        setupJsTextAreaHandler(fieldId, textArea)

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a Checkbox component as a checkbox input with a label.
     */
    override fun <T> renderCheckbox(checkbox: Checkbox, consumer: TagConsumer<T>): T {
        val checkboxId = "checkbox-${checkbox.hashCode()}"

        consumer.div {
            // Apply container styles
            style = "display: flex; align-items: center; margin-bottom: 16px;"

            // Render the checkbox input
            input {
                id = checkboxId
                name = checkboxId
                type = InputType.checkBox
                checked = checkbox.state.value

                // Apply indeterminate state if needed
                if (checkbox.isIndeterminate) {
                    attributes["indeterminate"] = "true"
                }

                // Apply modifier styles
                style = checkbox.modifier.toStyleString()

                // Add a data attribute for checkbox handling
                attributes["data-summon-checkbox"] = checkboxId
            }

            // Add the label if provided, placed next to the checkbox
            checkbox.label?.let {
                label {
                    htmlFor = checkboxId
                    style = "margin-left: 8px;"
                    +it
                }
            }
        }

        // Show validation errors if any
        val errors = checkbox.getValidationErrors()
        if (errors.isNotEmpty()) {
            consumer.div {
                style = "color: #d32f2f; font-size: 12px; margin-top: 4px;"
                errors.forEach { error ->
                    div {
                        +error
                    }
                }
            }
        }

        // Set up the checkbox change handler
        setupJsCheckboxHandler(checkboxId, checkbox)

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a RadioButton component as a radio input with a label.
     */
    override fun <T> renderRadioButton(radioButton: RadioButton<Any>, consumer: TagConsumer<T>): T {
        val radioId = "radio-${radioButton.hashCode()}"

        consumer.div {
            // Apply container styles
            style = "display: flex; align-items: center; margin-bottom: 8px;"

            // Render the radio input
            input {
                id = radioId
                name = radioButton.name
                type = InputType.radio
                checked = radioButton.selected

                if (radioButton.disabled) {
                    disabled = true
                }

                // Apply modifier styles
                style = radioButton.modifier.toStyleString()

                // Add a data attribute for radio handling
                attributes["data-summon-radio"] = radioId
            }

            // Add the label if provided, placed next to the radio button
            radioButton.label?.let {
                label {
                    htmlFor = radioId
                    style = "margin-left: 8px;"
                    +it
                }
            }
        }

        // Set up the radio click handler
        setupJsRadioHandler(radioId, radioButton)

        return consumer as T
    }

    /**
     * Renders a Form component as a form element with its contents.
     */
    override fun <T> renderForm(form: Form, consumer: TagConsumer<T>): T {
        val formId = "form-${form.hashCode()}"

        consumer.form {
            id = formId

            // Apply form styles
            style = form.modifier.toStyleString()

            // Add a submit handler attribute
            attributes["data-summon-form"] = "true"

            // Render all form content
            form.content.forEach { child ->
                // Register TextField components with the form
                if (child is TextField) {
                    form.registerField(child)
                }

                // Render the child component
                child.compose(this)
            }
        }

        // Set up the form submit handler
        setupJsFormHandler(formId, form)

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders the container for the Router.
     * The Composer, using RouterContext, is responsible for rendering the actual page content inside this container.
     */
    override fun <T> renderRouter(routerData: RouterData, consumer: TagConsumer<T>): T { // Changed Router to RouterData
        val routerContainerId = "router-container-${routerData.hashCode()}" // Use routerData

        consumer.div(classes = routerData.modifier.classes.joinToString(" ")) { // Apply modifier classes
            id = routerContainerId
            style = routerData.modifier.toStyleString() // Apply modifier styles

            // Set role=main? or let the page content handle semantic roles?
            // attributes["role"] = "main" 

            // The Composer will query RouterContext and render the appropriate component here.
            // It doesn't need direct access to routerData.routes etc.
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a 404 Not Found page.
     * This might be considered a specific page component rather than a generic renderer method.
     * For now, keep it simple. Refactor to use NotFoundData if it needs configuration.
     */
    override fun <T> renderNotFound(consumer: TagConsumer<T>): T {
        // Consider creating a NotFoundData class if customization (e.g., text, links) is needed.
        consumer.div {
            style = "padding: 20px; text-align: center;" // Basic styling
            h1 { +"404 - Page Not Found" }
            p { +"The page you are looking for doesn't exist or has been moved." }
            // Maybe render a Link component here? 
            // For now, a simple anchor tag.
            a(href = "/") { +"Go to Home Page" }
        }
        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a Card component as a div with appropriate styling for a card.
     */
    override fun <T> renderCard(card: Card, consumer: TagConsumer<T>): T {
        val cardId = if (card.onClick != null) "card-${card.hashCode()}" else null

        consumer.div {
            // Set id if we have a click handler
            cardId?.let { id = it }

            // Create base card styles
            val cardStyles = mapOf(
                "background-color" to "white",
                "border-radius" to card.borderRadius,
                "box-shadow" to "0 ${card.elevation} ${card.elevation} rgba(0, 0, 0, 0.1)",
                "padding" to "16px",
                "overflow" to "hidden"
            )

            // Combine with the user's custom styles
            val combinedStyles = card.modifier.styles + cardStyles
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Add click handling if provided
            if (card.onClick != null) {
                attributes["data-summon-click"] = "true"
                attributes["role"] = "button"
                attributes["tabindex"] = "0"
            }

            // Render each child component
            card.content.forEach { child ->
                child.compose(this)
            }
        }

        // Set up click handler if needed
        if (cardId != null && card.onClick != null) {
            setupJsCardClickHandler(cardId, card)
        }


        return consumer as T
    }

    /**
     * Renders an Image component as an img element with accessibility attributes.
     */
    override fun <T> renderImage(image: Image, consumer: TagConsumer<T>): T {
        val imageId = "img-${image.hashCode()}"

        // Collect all attributes first
        val attributes = mutableMapOf<String, String>()

        // Set required attributes
        attributes["id"] = imageId
        attributes["src"] = image.src
        attributes["alt"] = image.alt

        // Apply loading strategy
        if (image.loading != ImageLoading.AUTO) {
            attributes["loading"] = image.loading.value
        }

        // Apply optional attributes
        image.width?.let { attributes["width"] = it.toString() }
        image.height?.let { attributes["height"] = it.toString() }

        // Add detailed description if provided
        image.contentDescription?.let {
            attributes["aria-describedby"] = "img-desc-$imageId"
        }

        // Apply styles
        attributes["style"] = image.modifier.toStyleString()

        // Set up error handling for image loading
        attributes["onerror"] =
            "this.onerror=null; this.src='data:image/svg+xml;charset=utf-8,%3Csvg xmlns%3D%22http://www.w3.org/2000/svg%22 width%3D%22${image.width ?: "100"}%22 height%3D%22${image.height ?: "100"}%22 viewBox%3D%220 0 24 24%22%3E%3Cpath fill%3D%22%23ccc%22 d%3D%22M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z%22/%3E%3C/svg%3E';"

        // Create the img tag with all collected attributes
        consumer.img {
            // Properly apply all collected attributes to the tag
            for ((key, value) in attributes) {
                this.attributes[key] = value
            }
        }

        // Add the description in a hidden element if provided
        image.contentDescription?.let {
            consumer.div {
                id = "img-desc-$imageId"
                style = "position: absolute; height: 1px; width: 1px; overflow: hidden; clip: rect(1px, 1px, 1px, 1px);"
                +it
            }
        }

        return consumer as T
    }

    override fun <T> renderDivider(divider: Divider, consumer: TagConsumer<T>): T {
        consumer.apply {
            if (divider.isVertical) {
                div {
                    style = buildString {
                        append("display: inline-block;")
                        append("width: ${divider.thickness};")
                        append("height: ${divider.length};")
                        append("background-color: ${divider.color};")
                        append(divider.modifier.toStyleString())
                    }

                    // Apply hover styles and event listeners if any
                    divider.modifier.applyStyles(this)
                }
            } else {
                hr {
                    style = buildString {
                        append("border: none;")
                        append("height: ${divider.thickness};")
                        append("width: ${divider.length};")
                        append("background-color: ${divider.color};")
                        append(divider.modifier.toStyleString())
                    }

                    // Apply hover styles and event listeners if any
                    divider.modifier.applyStyles(this)
                }
            }
        }

        return consumer as T
    }

    /**
     * Renders a Link component as an anchor element with SEO-friendly attributes.
     */
    override fun <T> renderLink(link: Link, consumer: TagConsumer<T>): T {
        val linkId = "link-${link.hashCode()}"

        consumer.a {
            // Set the href attribute and ID
            href = link.href
            id = linkId

            // Apply the modifier styles
            style = link.modifier.toStyleString()

            // Apply additional link-specific attributes
            link.getLinkAttributes().forEach { (key, value) ->
                attributes[key] = value
            }

            // Add the link text
            +link.text

            // Add a data attribute for click handling if needed
            if (link.onClick != null) {
                attributes["data-summon-click"] = "true"
            }
        }

        // Set up the click handler for this link if there's an onClick function
        if (link.onClick != null) {
            setupJsLinkHandler(linkId, link)
        }

        return consumer as T
    }

    /**
     * Set up click handler for card components.
     */
    private fun setupJsCardClickHandler(cardId: String, card: Card) {
        card.setupJsClickHandler(cardId)
    }

    /**
     * Renders an Icon component based on its type (SVG, font, or image).
     */
    override fun <T> renderIcon(icon: Icon, consumer: TagConsumer<T>): T {
        val iconId = "icon-${icon.hashCode()}"

        when (icon.type) {
            IconType.SVG -> {
                consumer.span {
                    // Set id for JS interactions
                    id = iconId

                    // Apply icon styles and modifier
                    val styles = icon.getAdditionalStyles()
                    style = (icon.modifier.styles + styles).entries.joinToString(";") { (key, value) -> "$key:$value" }

                    // Apply accessibility attributes
                    icon.getAccessibilityAttributes().forEach { (key, value) ->
                        attributes[key] = value
                    }

                    // Add the SVG content
                    if (icon.svgContent != null) {
                        unsafe {
                            raw(icon.svgContent)
                        }
                    }

                    // Add click handling if needed
                    if (icon.onClick != null) {
                        attributes["data-summon-click"] = "true"
                    }
                }

                // Set up the click handler if needed
                if (icon.onClick != null) {
                    setupJsIconClickHandler(iconId, icon)
                }
            }

            IconType.FONT -> {
                consumer.span {
                    // Set id for JS interactions
                    id = iconId

                    // Apply icon styles and modifier
                    val styles = icon.getAdditionalStyles()
                    style = (icon.modifier.styles + styles).entries.joinToString(";") { (key, value) -> "$key:$value" }

                    // Apply accessibility attributes
                    icon.getAccessibilityAttributes().forEach { (key, value) ->
                        attributes[key] = value
                    }

                    // Add the icon content (font character)
                    +icon.name

                    // Add click handling if needed
                    if (icon.onClick != null) {
                        attributes["data-summon-click"] = "true"
                    }
                }

                // Set up the click handler if needed
                if (icon.onClick != null) {
                    setupJsIconClickHandler(iconId, icon)
                }
            }

            IconType.IMAGE -> {
                consumer.img {
                    // Set id for JS interactions
                    id = iconId

                    // Apply modifier styles
                    style = icon.modifier.toStyleString()

                    // Set image attributes
                    src = icon.name
                    alt = icon.ariaLabel ?: ""

                    // Set width and height
                    width = icon.size
                    height = icon.size

                    // Add click handling if needed
                    if (icon.onClick != null) {
                        attributes["data-summon-click"] = "true"
                        onClick = "return false;" // Prevent default action
                    }
                }

                // Set up the click handler if needed
                if (icon.onClick != null) {
                    setupJsIconClickHandler(iconId, icon)
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Set up click handler for icon components.
     */
    private fun setupJsIconClickHandler(iconId: String, icon: Icon) {
        // This will be implemented in IconExt.kt
        icon.setupJsClickHandler(iconId)
    }

    /**
     * Renders an Alert component for notifications and messages.
     */
    override fun <T> renderAlert(alert: Alert, consumer: TagConsumer<T>): T {
        val alertId = "alert-${alert.hashCode()}"
        // Define IDs for action and dismiss buttons upfront
        val actionId = if (alert.onAction != null) "$alertId-action" else null
        val dismissId = if (alert.isDismissible) "$alertId-dismiss" else null

        consumer.div {
            // Set id for JS interactions
            id = alertId

            // Combine type-specific styles with modifier
            val typeStyles = alert.getTypeStyles()
            val combinedStyles = alert.modifier.styles + typeStyles + mapOf(
                "position" to "relative",
                "padding" to "12px 16px",
                "margin" to "8px 0",
                "border" to "1px solid",
                "border-radius" to "4px",
                "display" to "flex",
                "align-items" to "center"
            )

            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Apply accessibility attributes
            alert.getAccessibilityAttributes().forEach { (key, value) ->
                attributes[key] = value
            }

            // Add icon if present
            alert.icon?.let {
                div {
                    style = "margin-right: 12px;"
                    it.compose(this)
                }
            }

            // Content container
            div {
                style = "flex: 1;"

                // Add title if present
                alert.title?.let {
                    div {
                        style = "font-weight: bold; margin-bottom: 4px;"
                        +it
                    }
                }

                // Add message
                div {
                    +alert.message
                }
            }

            // Add action button if present
            alert.actionText?.let {
                button {
                    // Set id for action button
                    actionId?.let { actionButtonId -> id = actionButtonId }

                    style =
                        "margin-left: 16px; background: none; border: none; cursor: pointer; font-weight: bold; color: inherit;"

                    // Add data attribute for action handling
                    attributes["data-summon-alert-action"] = "true"

                    +it
                }
            }

            // Add dismiss button if alert is dismissible
            if (alert.isDismissible) {
                button {
                    // Set id for dismiss button
                    dismissId?.let { dismissButtonId -> id = dismissButtonId }

                    style =
                        "background: none; border: none; cursor: pointer; position: absolute; top: 8px; right: 8px; padding: 4px; color: inherit; opacity: 0.6;"
                    attributes["aria-label"] = "Close"
                    attributes["data-summon-alert-dismiss"] = "true"

                    // Use a simple × character for the button
                    +"×"
                }
            }
        }

        // Set up action handler if needed
        if (alert.onAction != null && actionId != null) {
            setupJsAlertActionHandler(actionId, alert)
        }

        // Set up dismiss handler if needed
        if (alert.onDismiss != null && dismissId != null) {
            setupJsAlertDismissHandler(dismissId, alertId, alert)
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Set up action handler for alert components.
     */
    private fun setupJsAlertActionHandler(actionId: String, alert: Alert) {
        // This will be implemented in AlertExt.kt
        alert.setupJsActionHandler(actionId)
    }

    /**
     * Set up dismiss handler for alert components.
     */
    private fun setupJsAlertDismissHandler(dismissId: String, alertId: String, alert: Alert) {
        // This will be implemented in AlertExt.kt
        alert.setupJsDismissHandler(dismissId, alertId)
    }

    /**
     * Renders a Badge node (typically a span).
     */
    override fun <T> renderBadge(badgeData: BadgeData, consumer: TagConsumer<T>): T {
        val badgeId = "badge-${badgeData.hashCode()}"
        val isClickable = badgeData.onClick != null

        consumer.span(
            classes = (badgeData.modifier.classes + " summon-badge summon-badge-${badgeData.severity.name.lowercase()}").joinToString(
                " "
            )
        ) {
            id = badgeId

            // Combine base styles, severity styles, shape styles, size styles, and modifier styles
            val baseStyles = mapOf(
                "display" to "inline-flex",
                "align-items" to "center",
                "justify-content" to "center",
                "font-weight" to "500",
                "line-height" to "1",
                "white-space" to "nowrap"
            )
            val severityStyles = getBadgeSeverityStyles(badgeData.severity)
            val shapeStyles = getBadgeShapeStyles(badgeData.shape)
            val sizeStyles = getBadgeSizeStyles(badgeData.size)
            val clickableStyle = if (isClickable) mapOf("cursor" to "pointer") else emptyMap()

            val combinedStyles =
                baseStyles + severityStyles + shapeStyles + sizeStyles + clickableStyle + badgeData.modifier.styles
            style = combinedStyles.entries.joinToString(";") { "${it.key}:${it.value}" }

            // Apply accessibility attributes (role might depend on context/content)
            attributes["role"] = "status"

            // Add click handler attribute if clickable
            if (isClickable) {
                attributes["data-summon-click"] = "true"
                attributes["tabindex"] = "0"
            }

            // Add content unless it's a dot badge
            if (badgeData.shape != BadgeShape.DOT) {
                +badgeData.content
            }
        }

        // Set up click handler if needed
        if (isClickable) {
            setupJsBadgeClickHandler(badgeId, badgeData.onClick!!)
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Sets up a JS click handler for a badge
     */
    private fun setupJsBadgeClickHandler(elementId: String, onClickLambda: () -> Unit) {
        // Placeholder - Use the generic click handler logic
        println("TODO: setupJsBadgeClickHandler for ID $elementId needs ACTUAL JS IMPLEMENTATION (use generic click handler)")
    }

    /**
     * Renders a Tooltip node.
     * Sets up the container and the hidden tooltip content.
     * The actual trigger element is rendered by the Composer.
     * JS handlers manage visibility.
     */
    override fun <T> renderTooltip(tooltipData: TooltipData, consumer: TagConsumer<T>): T {
        val tooltipWrapperId = "tooltip-wrapper-${tooltipData.hashCode()}"
        val tooltipContentId = "tooltip-content-${tooltipData.hashCode()}"

        // Outer container for positioning context
        consumer.div(classes = tooltipData.modifier.classes.joinToString(" ")) {
            id = tooltipWrapperId
            // Basic style for positioning context
            style = "position: relative; display: inline-block;" + tooltipData.modifier.toStyleString()

            // The Composer will render the actual trigger element (tooltipData.triggerContent) here.
            // We need to ensure the trigger element gets the necessary ARIA attributes.
            // This might require passing attributes down or having the Composer apply them.
            // For now, assume the trigger element itself will have aria-describedby="tooltipContentId"

            // Tooltip content element (initially hidden)
            div(classes = "summon-tooltip-content") {
                id = tooltipContentId

                // Base styles for the tooltip popup
                val basePopupStyles = mapOf(
                    "position" to "absolute",
                    "background-color" to "#333", // Default dark background
                    "color" to "#fff", // Default light text
                    "padding" to "6px 10px",
                    "border-radius" to "4px",
                    "font-size" to "0.875rem",
                    "z-index" to "1000",
                    "pointer-events" to "none", // Prevent tooltip from interfering with mouse events
                    "opacity" to "0", // Start hidden
                    "transition" to "opacity 0.2s ease-in-out", // Fade animation
                    "max-width" to "250px",
                    "text-align" to "center"
                )
                // Placement styles (positioning relative to the trigger)
                val placementStyles = getTooltipPlacementStyles(tooltipData.placement)

                style = (basePopupStyles + placementStyles).entries.joinToString(";") { "${it.key}:${it.value}" }

                // ARIA role
                attributes["role"] = "tooltip"
                attributes["aria-hidden"] = "true" // Initially hidden from accessibility tree

                // Add tooltip text content
                +tooltipData.tooltipText

                // Optional arrow (styling depends heavily on placement)
                if (tooltipData.showArrow) {
                    div(classes = "summon-tooltip-arrow") {
                        val arrowStyles = getTooltipArrowStyles(tooltipData.placement)
                        style = arrowStyles.entries.joinToString(";") { "${it.key}:${it.value}" }
                    }
                }
            }
        }

        // Set up JS handlers to show/hide the tooltip on trigger hover/focus
        setupJsTooltipHandlers(tooltipWrapperId, tooltipContentId, tooltipData)

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /** Sets up JS event handlers for tooltip visibility */
    private fun setupJsTooltipHandlers(wrapperId: String, contentId: String, tooltipData: TooltipData) {
        // Placeholder - Need JS to:
        // 1. Find the trigger element(s) inside the wrapper (might be complex if trigger isn't the direct child).
        // 2. Find the tooltip content element by contentId.
        // 3. Add event listeners (e.g., mouseenter, mouseleave, focus, blur) to the trigger.
        // 4. On trigger enter/focus: Show tooltip (set opacity=1, aria-hidden=false).
        // 5. On trigger leave/blur: Hide tooltip (set opacity=0, aria-hidden=true).
        // Consider delays for hover (tooltipData.delayMillis).
        println("TODO: setupJsTooltipHandlers for wrapper ID $wrapperId (content ID $contentId) needs ACTUAL JS IMPLEMENTATION")
        // Example (conceptual - simplified, assumes wrapper IS the trigger):
        // js("const trigger = document.getElementById(wrapperId); const tooltip = document.getElementById(contentId); ... add listeners ...")
    }

    /**
     * Renders a Progress node for loading and progress indication.
     * Handles linear/circular and determinate/indeterminate types.
     */
    override fun <T> renderProgress(
        progressData: ProgressData,
        consumer: TagConsumer<T>
    ): T { // Changed Progress to ProgressData
        val progressId = "progress-${progressData.hashCode()}"
        val isIndeterminate = progressData.value == null // Use progressData.value == null for indeterminate

        // Container element with modifier
        consumer.div(classes = progressData.modifier.classes.joinToString(" ")) {
            id = progressId
            // Basic container styles + modifier
            style =
                "display: flex; flex-direction: column; align-items: center;" + progressData.modifier.toStyleString() // Use progressData.modifier

            // Optional Label
            progressData.label?.let { // Use progressData.label
                div(classes = "summon-progress-label") {
                    style = "margin-bottom: 4px; font-size: 0.875rem; color: #555;"
                    +it
                }
            }

            // Main progress visual container (track)
            div(classes = "summon-progress-track") {
                // Track styles based on type
                val trackStyles = getProgressTrackStyles(progressData) // Helper using ProgressData
                style = trackStyles.entries.joinToString(";") { "${it.key}:${it.value}" }

                // Apply ARIA attributes
                attributes["role"] = "progressbar"
                if (!isIndeterminate) {
                    attributes["aria-valuenow"] = progressData.value!!.toString()
                    attributes["aria-valuemin"] = "0" // Assuming 0 is always min
                    attributes["aria-valuemax"] = progressData.max.toString() // Use progressData.max
                } else {
                    // For indeterminate, specific ARIA attributes might not be needed,
                    // but role=progressbar is still important.
                    attributes["aria-busy"] = "true"
                }
                progressData.label?.let { attributes["aria-label"] = it } // Use label for accessibility if present

                // Render the actual progress indicator (bar or circle part)
                if (!isIndeterminate) {
                    // Determinate indicator
                    div(classes = "summon-progress-indicator") {
                        val indicatorStyles = getProgressIndicatorStyles(progressData) // Helper using ProgressData
                        style = indicatorStyles.entries.joinToString(";") { "${it.key}:${it.value}" }
                    }
                } else {
                    // Indeterminate animation elements (handled by CSS usually)
                    // Add elements/classes that CSS can target for animation
                    if (progressData.type == ProgressType.LINEAR) {
                        div(classes = "summon-progress-indeterminate-bar") { /* Styled via CSS */ }
                    }
                    // For circular indeterminate, the track div itself is often styled with borders and animation
                }
            }
            // Add CSS keyframes if needed for indeterminate animations (could be global CSS)
            // This might be better placed in a global stylesheet rather than inline per component.
            /*
            style {
                unsafe {
                    raw("""
                    @keyframes spin { ... }
                    @keyframes indeterminate-linear { ... }
                    """.trimIndent())
                }
            }
            */
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a Box component as a div with absolute positioning.
     */
    override fun <T> renderBox(boxData: BoxData, consumer: TagConsumer<T>): T { // Changed Box to BoxData
        consumer.div(classes = boxData.modifier.classes.joinToString(" ")) { // Apply modifier classes
            // Apply modifier styles directly
            // Box typically relies heavily on Modifier for layout (size, padding, alignment within parent etc.)
            style = boxData.modifier.toStyleString() // Use boxData.modifier

            // Add display: flex by default? Or let Modifier handle it?
            // Assuming Modifier controls display type if needed.
            // if (!style.contains("display:")) {
            //    style = "display: flex;" + style
            // }

            // Content is rendered by the Composer within this div.
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a Grid node (a div with display: grid).
     * The content is rendered by the Composer.
     */
    override fun <T> renderGrid(gridData: GridData, consumer: TagConsumer<T>): T { // Changed Grid to GridData
        consumer.div(classes = gridData.modifier.classes.joinToString(" ")) { // Apply modifier classes

            // Grid-specific styles from GridData
            val gridStyles = mutableMapOf<String, String>()
            gridStyles["display"] = "grid"
            gridData.columns?.let { gridStyles["grid-template-columns"] = it } // Use gridData.columns
            gridData.rows?.let { gridStyles["grid-template-rows"] = it } // Use gridData.rows
            gridData.gap?.let { gridStyles["gap"] = it } // Use gridData.gap
            gridData.areas?.let { gridStyles["grid-template-areas"] = it } // Use gridData.areas
            // Add other potential grid properties like justify-items, align-items etc. if needed in GridData

            // Combine grid styles with modifier styles
            val combinedStyles = gridStyles + gridData.modifier.styles // Use gridData.modifier
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Content is rendered by the Composer within this div.
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders an AspectRatio node (a div with the aspect-ratio CSS property).
     * The content is rendered by the Composer.
     */
    override fun <T> renderAspectRatio(
        aspectRatioData: AspectRatioData,
        consumer: TagConsumer<T>
    ): T { // Changed AspectRatio to AspectRatioData
        consumer.div(classes = aspectRatioData.modifier.classes.joinToString(" ")) { // Apply modifier classes

            // Combine aspect-ratio style with modifier styles
            val aspectRatioStyle =
                mapOf("aspect-ratio" to aspectRatioData.ratio.toString()) // Use aspectRatioData.ratio
            // Ensure overflow is handled if content might exceed the aspect ratio box
            val overflowStyle = if (style?.contains("overflow:") != true) mapOf("overflow" to "hidden") else emptyMap()

            val combinedStyles =
                aspectRatioStyle + overflowStyle + aspectRatioData.modifier.styles // Use aspectRatioData.modifier
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Content is rendered by the Composer within this div.
            // Ensure the content itself scales correctly within the aspect ratio container (e.g., width: 100%, height: 100% for an image).
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a ResponsiveLayout node.
     * Outputs container divs for content specific to different breakpoints.
     * Visibility is controlled by CSS media queries targeting classes like 'summon-show-on-[size]'.
     * The Composer renders the content within the appropriate div.
     */
    override fun <T> renderResponsiveLayout(
        responsiveData: ResponsiveLayoutData,
        consumer: TagConsumer<T>
    ): T { // Changed ResponsiveLayout to ResponsiveLayoutData
        consumer.div(classes = responsiveData.modifier.classes.joinToString(" ")) { // Apply modifier classes to the main container
            style = responsiveData.modifier.toStyleString() // Apply modifier styles

            // Output container for default/fallback content (visible if no specific breakpoint matches or as a base)
            div(classes = "summon-responsive-content summon-show-on-default") { // Base class + default visibility class
                // The Composer will call responsiveData.defaultContent.compose(this) here.
            }

            // Output containers for breakpoint-specific content
            responsiveData.content.forEach { (screenSize, contentLambda) -> // Use responsiveData.content
                val sizeClass = "summon-show-on-${screenSize.name.lowercase()}" // e.g., summon-show-on-small
                div(classes = "summon-responsive-content $sizeClass") {
                    // Apply specific styling if needed for this breakpoint container?
                    // The Composer will call contentLambda.compose(this) here.
                }
            }

            // It's assumed that global CSS defines rules like:
            // .summon-responsive-content { display: none; } // Hide all by default
            // @media (max-width: 599px) { .summon-show-on-small { display: block; } }
            // @media (min-width: 600px) and (max-width: 959px) { .summon-show-on-medium { display: block; } }
            // ... etc.
            // The default content might need rules like: .summon-show-on-default { display: block; }
            // and then breakpoint-specific rules would override/hide it if they match.
        }

        // No JS setup needed for CSS-driven approach
        // setupResponsiveLayout(layoutId, responsiveLayout) // Removed

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders the container for a LazyColumn.
     * Sets up a scrollable div.
     * Actual item rendering and virtualization must be handled by external JS/Composer logic.
     */
    override fun <T> renderLazyColumn(
        lazyColumnData: LazyColumnData<*>,
        consumer: TagConsumer<T>
    ): T { // Changed LazyColumn to LazyColumnData
        val columnId = "lazy-column-${lazyColumnData.hashCode()}"

        consumer.div(classes = lazyColumnData.modifier.classes.joinToString(" ")) { // Apply modifier classes
            id = columnId
            // Base styles for a scrollable column container + modifier styles
            val baseStyles = mapOf(
                // "display" to "flex", // Often applied by modifier or parent
                // "flex-direction" to "column",
                "overflow-y" to "auto", // Enable vertical scrolling
                "height" to "100%" // Default height, should be constrained by parent or modifier
                // Add overscroll-behavior: contain? to prevent scrolling chain
            )
            val combinedStyles = baseStyles + lazyColumnData.modifier.styles // Use lazyColumnData.modifier
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Content (items) are rendered dynamically by external logic (JS/Composer) inside this div.
            // The renderer only creates the scroll container.
        }

        // JS virtualization setup is removed from the renderer.
        // setupLazyColumnVirtualization(columnId, lazyColumn) // Removed

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders the container for a LazyRow.
     * Sets up a scrollable div.
     * Actual item rendering and virtualization must be handled by external JS/Composer logic.
     */
    override fun <T> renderLazyRow(
        lazyRowData: LazyRowData<*>,
        consumer: TagConsumer<T>
    ): T { // Changed LazyRow to LazyRowData
        val rowId = "lazy-row-${lazyRowData.hashCode()}"

        consumer.div(classes = lazyRowData.modifier.classes.joinToString(" ")) { // Apply modifier classes
            id = rowId
            // Base styles for a scrollable row container + modifier styles
            val baseStyles = mapOf(
                // "display" to "flex", // Often applied by modifier or parent
                "overflow-x" to "auto", // Enable horizontal scrolling
                "width" to "100%", // Default width, should be constrained by parent or modifier
                "display" to "flex", // Ensure items are laid out horizontally inside
                "flex-direction" to "row"
                // Add overscroll-behavior: contain?
            )
            val combinedStyles = baseStyles + lazyRowData.modifier.styles // Use lazyRowData.modifier
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Content (items) are rendered dynamically by external logic (JS/Composer) inside this div.
            // The renderer only creates the scroll container.
        }

        // JS virtualization setup is removed from the renderer.
        // setupLazyRowVirtualization(rowId, lazyRow) // Removed

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a TabLayout node.
     * Sets up containers for tab headers and content.
     * Composer renders headers and selected content.
     * JS handles tab switching and closing logic.
     */
    override fun <T> renderTabLayout(
        tabLayoutData: TabLayoutData,
        consumer: TagConsumer<T>
    ): T { // Changed TabLayout to TabLayoutData
        val tabLayoutId = "tab-layout-${tabLayoutData.hashCode()}"

        // Main container
        consumer.div(classes = tabLayoutData.modifier.classes.joinToString(" ")) { // Apply modifier classes
            id = tabLayoutId
            // Basic layout + modifier styles
            val baseStyles = mapOf("display" to "flex", "flex-direction" to "column")
            style =
                (baseStyles + tabLayoutData.modifier.styles).entries.joinToString(";") { "${it.key}:${it.value}" } // Use tabLayoutData.modifier

            // Tab headers container
            div(classes = "summon-tablayout-headers") {
                id = "$tabLayoutId-headers"
                style = "display: flex; flex-direction: row; border-bottom: 1px solid #ddd;" // Basic header styling
                attributes["role"] = "tablist" // Accessibility

                // Composer renders the individual tab headers (tabLayoutData.tabs.forEach { renderTabHeader(it, index) }) inside here.
                // Each rendered header should have role="tab", aria-controls="$tabLayoutId-content-$index", 
                // aria-selected=(index == tabLayoutData.selectedTabIndex).toString(),
                // and data attributes for index and closability for the JS handler.
            }

            // Tab content container
            div(classes = "summon-tablayout-content") {
                id = "$tabLayoutId-content"
                style = "padding: 15px 0;" // Basic content padding

                // Composer renders the selected tab's content (tabLayoutData.tabs[selectedTabIndex].content.compose(this)) inside here.
                // It should ideally have role="tabpanel" and aria-labelledby="$tabLayoutId-tab-$selectedTabIndex".
            }
        }

        // Set up JS tab switching/closing logic
        setupTabLayoutSwitching(tabLayoutId, tabLayoutData) // Pass TabLayoutData

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Set up JS for tab switching in TabLayout
     */
    private fun setupTabLayoutSwitching(tabLayoutId: String, tabLayoutData: TabLayoutData) {
        // Placeholder - Need JS to:
        // 1. Find the header container (#$tabLayoutId-headers).
        // 2. Add event listener (e.g., click) to the header container (event delegation).
        // 3. On click:
        //    a. Check if a tab header (element with role=tab and data-tab-index) was clicked.
        //    b. If yes, get the index from data-tab-index.
        //    c. Call tabLayoutData.onTabSelected(index).
        //    d. Check if the close button (element with data-tab-close) within the header was clicked.
        //    e. If yes, get the index and call tabLayoutData.onTabClose?.invoke(index).
        // Note: State changes (selectedTabIndex) trigger recomposition, which updates the UI (selected styles, content).
        println("TODO: setupTabLayoutSwitching for ID $tabLayoutId needs ACTUAL JS IMPLEMENTATION")
        // Example (conceptual):
        // js("const headers = document.getElementById('$tabLayoutId-headers'); headers.addEventListener('click', (event) => { ... check target, get index, call lambdas ... });")
    }

    /**
     * Renders an ExpansionPanel node.
     * Sets up the main container, header container, and content container.
     * Composer renders the header and content.
     * JS handles the toggling logic.
     */
    override fun <T> renderExpansionPanel(
        panelData: ExpansionPanelData,
        consumer: TagConsumer<T>
    ): T { // Changed ExpansionPanel to ExpansionPanelData
        val panelId = "panel-${panelData.hashCode()}"
        val headerId = "$panelId-header"
        val contentId = "$panelId-content"
        val isExpanded = panelData.isExpanded // Use panelData.isExpanded

        // Main container
        consumer.div(classes = panelData.modifier.classes.joinToString(" ")) { // Apply modifier classes
            id = panelId
            // Basic container styles + modifier
            val baseStyles = mapOf("border" to "1px solid #ddd", "border-radius" to "4px", "margin-bottom" to "10px")
            style =
                (baseStyles + panelData.modifier.styles).entries.joinToString(";") { "${it.key}:${it.value}" } // Use panelData.modifier

            // Header container (clickable)
            div(classes = "summon-expansion-header") {
                id = headerId
                // Basic header styling
                val headerStyles = mapOf(
                    "padding" to "10px 15px",
                    "background-color" to "#f5f5f5",
                    "cursor" to "pointer",
                    "display" to "flex",
                    "justify-content" to "space-between",
                    "align-items" to "center"
                )
                style = headerStyles.entries.joinToString(";") { "${it.key}:${it.value}" }
                attributes["data-expansion-header"] = "true" // Hook for JS handler
                // ARIA attributes for accessibility
                attributes["role"] = "button"
                attributes["aria-expanded"] = isExpanded.toString()
                attributes["aria-controls"] = contentId
                attributes["tabindex"] = "0" // Make header focusable

                // Composer renders the header content (panelData.header.compose(this)) here.
                // It should include the title and optionally an icon.
                // The expand/collapse indicator might also be part of the header content or rendered separately.

                // Example: Separate indicator (can be styled with CSS based on aria-expanded)
                // span(classes = "summon-expansion-indicator") { + if(isExpanded) "▲" else "▼" }
            }

            // Content container (collapsible)
            div(classes = "summon-expansion-content") {
                id = contentId
                // Basic content styling (visibility/height controlled by JS/CSS)
                val contentStyles = mapOf(
                    "overflow" to "hidden",
                    "transition" to "height 0.3s ease, padding 0.3s ease" // Animate height and padding
                    // Height and padding set dynamically or via CSS based on expansion state
                    // e.g., [aria-hidden="true"] { height: 0; padding-top: 0; padding-bottom: 0; }
                )
                style = contentStyles.entries.joinToString(";") { "${it.key}:${it.value}" }

                // ARIA attributes
                attributes["role"] = "region"
                attributes["aria-labelledby"] = headerId
                attributes["aria-hidden"] = (!isExpanded).toString()

                // Composer renders the content (panelData.content.compose(this)) here WHEN EXPANDED.
                // The renderer only sets up the container. Content visibility is tied to the isExpanded state.
            }
        }

        // Set up JS for expansion toggling
        setupExpansionPanelToggling(panelId, panelData) // Pass ExpansionPanelData

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    // --- Helper/Interop Functions --- 

    /** Sets up JS event handlers for expansion panel toggling */
    private fun setupExpansionPanelToggling(panelId: String, panelData: ExpansionPanelData) {
        // Placeholder - Need JS to:
        // 1. Find the header element (#$panelId-header).
        // 2. Add event listeners (click, keydown for Enter/Space) to the header.
        // 3. On activation:
        //    a. Call panelData.onToggle().
        // Note: State change (isExpanded) triggers recomposition, which updates ARIA attributes and potentially CSS classes/styles 
        // for the content visibility/animation.
        println("TODO: setupExpansionPanelToggling for ID $panelId needs ACTUAL JS IMPLEMENTATION")
        // Example (conceptual):
        // js("const header = document.getElementById('$panelId-header'); header.addEventListener('click', () => { panelData.onToggle(); }); header.addEventListener('keydown', ...) { panelData.onToggle(); } ")
    }

    /**
     * Renders a Select component as a dropdown with options.
     */
    override fun <T> renderSelect(select: Select<Any>, consumer: TagConsumer<T>): T {
        val selectId = "select-${select.hashCode()}"

        consumer.div {
            // Apply container styles
            style = "display: flex; flex-direction: column; margin-bottom: 16px;"

            // Add the label if provided
            select.label?.let {
                label {
                    htmlFor = selectId
                    style = "margin-bottom: 4px; font-weight: 500;"
                    +it
                }
            }

            // Render the select field
            select {
                id = selectId
                name = selectId

                if (select.multiple) {
                    multiple = true
                }

                if (select.disabled) {
                    disabled = true
                }

                if (select.size > 1) {
                    size = "${select.size}"
                }

                // Apply modifier styles
                style = select.modifier.toStyleString()

                // Add placeholder option if provided
                select.placeholder?.let {
                    option {
                        value = ""
                        selected = select.selectedValue.value == null
                        +it
                    }
                }

                // Add all options
                select.options.forEach { option ->
                    option {
                        value = option.value.toString()
                        selected = option.selected ||
                                select.selectedValue.value?.toString() == option.value.toString()

                        if (option.disabled) {
                            disabled = true
                        }

                        +option.label
                    }
                }

                // Add a data attribute for select handling
                attributes["data-summon-select"] = selectId
            }

            // Show validation errors if any
            val errors = select.getValidationErrors()
            if (errors.isNotEmpty()) {
                div {
                    style = "color: #d32f2f; font-size: 12px; margin-top: 4px;"
                    errors.forEach { error ->
                        div {
                            +error
                        }
                    }
                }
            }
        }

        // Set up the select change handler
        setupJsSelectHandler(selectId, select)

        return consumer as T
    }

    /**
     * Renders a FormField component that wraps form controls with labels and validation.
     */
    override fun <T> renderFormField(formField: FormField, consumer: TagConsumer<T>): T {
        consumer.div {
            // Apply container styles with modifier
            style = "display: flex; flex-direction: column; margin-bottom: 16px;" +
                    formField.modifier.toStyleString()

            // Render label if provided
            formField.label?.let {
                label {
                    style = "margin-bottom: 4px; font-weight: 500;" +
                            if (formField.required) " position: relative;" else ""

                    +it

                    // Add required indicator
                    if (formField.required) {
                        span {
                            style = "color: #d32f2f; margin-left: 4px;"
                            +"*"
                        }
                    }
                }
            }

            // Render the field content
            formField.fieldContent.compose(this)

            // Render helper text if provided
            formField.helper?.let {
                div {
                    style = "font-size: 12px; color: #666; margin-top: 4px;"
                    +it
                }
            }

            // Render error if provided
            formField.error?.let {
                div {
                    style = "font-size: 12px; color: #d32f2f; margin-top: 4px;"
                    +it
                }
            }
        }

        return consumer as T
    }

    /**
     * Renders a Switch component as a toggle switch with a label.
     */
    override fun <T> renderSwitch(switch: Switch, consumer: TagConsumer<T>): T {
        val switchId = "switch-${switch.hashCode()}"

        consumer.div {
            // Apply container styles
            style = "display: flex; align-items: center; margin-bottom: 16px;"

            // Create the visual switch control
            label {
                htmlFor = switchId
                style = "position: relative; display: inline-block; width: 40px; height: 24px; " +
                        "cursor: ${if (switch.disabled) "not-allowed" else "pointer"};"

                // Hidden checkbox input
                input {
                    id = switchId
                    type = InputType.checkBox
                    checked = switch.state.value

                    if (switch.disabled) {
                        disabled = true
                    }

                    style = "opacity: 0; width: 0; height: 0;" // Hide the checkbox
                    attributes["data-summon-switch"] = switchId
                }

                // Switch slider
                span {
                    style = "position: absolute; top: 0; left: 0; right: 0; bottom: 0; " +
                            "background-color: ${if (switch.state.value) "#2196F3" else "#ccc"}; " +
                            "border-radius: 12px; " +
                            "transition: .4s; " +
                            "opacity: ${if (switch.disabled) "0.6" else "1"};" +
                            switch.modifier.toStyleString()

                    // Switch knob/thumb
                    span {
                        style = "position: absolute; content: ''; height: 16px; width: 16px; " +
                                "left: 4px; bottom: 4px; background-color: white; " +
                                "border-radius: 50%; transition: .4s; " +
                                "transform: ${if (switch.state.value) "translateX(16px)" else "none"};"
                    }
                }
            }

            // Label text if provided
            switch.label?.let {
                label {
                    htmlFor = switchId
                    style = "margin-left: 12px;" +
                            if (switch.disabled) " opacity: 0.6;" else ""
                    +it
                }
            }
        }

        // Set up the switch change handler
        setupJsSwitchHandler(switchId, switch)

        return consumer as T
    }

    /**
     * Renders a FileUpload component for file selection.
     */
    override fun <T> renderFileUpload(fileUpload: FileUpload, consumer: TagConsumer<T>): T {
        val uploadId = "file-upload-${fileUpload.hashCode()}"

        consumer.div {
            // Apply container styles
            style = "display: flex; flex-direction: column; margin-bottom: 16px;" +
                    fileUpload.modifier.toStyleString()

            // Label if provided
            fileUpload.label?.let {
                label {
                    htmlFor = uploadId
                    style = "margin-bottom: 8px; font-weight: 500;"
                    +it
                }
            }

            // Custom file input wrapper
            div {
                style = "display: flex; align-items: center;"

                // Hidden file input
                input {
                    id = uploadId
                    type = InputType.file

                    // Set attributes based on component properties
                    fileUpload.accept?.let { accept = it }
                    if (fileUpload.multiple) multiple = true
                    if (fileUpload.disabled) disabled = true
                    fileUpload.capture?.let { attributes["capture"] = it }

                    // Apply basic styles
                    style = "position: absolute; opacity: 0; width: 0.1px; height: 0.1px; overflow: hidden;"
                    attributes["data-summon-file-upload"] = uploadId
                }

                // Custom styled button
                label {
                    htmlFor = uploadId
                    style = "display: inline-block; padding: 8px 16px; " +
                            "background-color: #f5f5f5; border: 1px solid #ddd; " +
                            "border-radius: 4px; cursor: pointer; " +
                            "transition: background-color 0.3s; " +
                            if (fileUpload.disabled) "opacity: 0.6; cursor: not-allowed;" else ""
                    +fileUpload.buttonLabel
                }

                // Display selected file name(s)
                span {
                    id = "$uploadId-selection"
                    style = "margin-left: 10px; color: #666; font-size: 14px;"
                    +"No file selected"
                }
            }
        }

        // Set up the file upload handler
        setupJsFileUploadHandler(uploadId, fileUpload)

        return consumer as T
    }

    override fun <T> renderSlider(slider: SliderData, consumer: TagConsumer<T>): T {
        TODO("Not yet implemented")
    }

    /**
     * Renders a RangeSlider component for range selection.
     */
    override fun <T> renderRangeSlider(rangeSlider: RangeSlider, consumer: TagConsumer<T>): T {
        val sliderId = "slider-${rangeSlider.hashCode()}"

        consumer.div {
            // Apply container styles
            style = "display: flex; flex-direction: column; margin-bottom: 16px;" +
                    rangeSlider.modifier.toStyleString()

            // Add label if provided
            rangeSlider.label?.let {
                label {
                    htmlFor = sliderId
                    style = "margin-bottom: 8px; display: flex; justify-content: space-between;"

                    // Label text
                    span { +it }

                    // Current value
                    if (rangeSlider.showTooltip) {
                        span {
                            id = "$sliderId-value"
                            +rangeSlider.valueFormat(rangeSlider.state.value)
                        }
                    }
                }
            }

            // Render the slider input
            input {
                id = sliderId
                type = InputType.range
                min = rangeSlider.min.toString()
                max = rangeSlider.max.toString()
                step = rangeSlider.step.toString()
                value = rangeSlider.state.value.toString()

                if (rangeSlider.disabled) {
                    disabled = true
                }

                // Apply styles
                style = "width: 100%; " +
                        if (rangeSlider.disabled) "opacity: 0.6;" else ""

                // Add data attribute for slider handling
                attributes["data-summon-slider"] = sliderId
            }
        }

        // Set up the slider handler
        setupJsSliderHandler(sliderId, rangeSlider)

        return consumer as T
    }

    /**
     * Renders a DatePicker component as an input field with a date type.
     */
    override fun <T> renderDatePicker(datePicker: DatePicker, consumer: TagConsumer<T>): T {
        val datePickerId = "date-picker-${datePicker.hashCode()}"

        consumer.div {
            // Apply container styles
            style = "display: flex; flex-direction: column; margin-bottom: 16px;"

            // Add the label if provided
            datePicker.label?.let {
                label {
                    htmlFor = datePickerId
                    style = "margin-bottom: 4px; font-weight: 500;"
                    +it
                }
            }

            // Render the date input field
            input {
                id = datePickerId
                name = datePickerId
                type = InputType.date

                // Apply modifier styles
                style = datePicker.modifier.toStyleString()

                // Set current value
                value = datePicker.state.value

                // Set min and max dates if provided
                datePicker.min?.let { min = it }
                datePicker.max?.let { max = it }

                // Add disabled state if needed
                if (datePicker.disabled) {
                    disabled = true
                }

                // Add placeholder if provided
                datePicker.placeholder?.let {
                    placeholder = it
                }

                // Add a data attribute for date picker handling
                attributes["data-summon-date-picker"] = datePickerId
            }

            // Show validation errors if any
            val errors = datePicker.getValidationErrors()
            if (errors.isNotEmpty()) {
                div {
                    style = "color: #d32f2f; font-size: 12px; margin-top: 4px;"
                    errors.forEach { error ->
                        div {
                            +error
                        }
                    }
                }
            }
        }

        // Set up the date picker handler
        setupJsDatePickerHandler(datePickerId, datePicker)

        return consumer as T
    }

    /**
     * Renders a TimePicker component as an input field with a time type.
     */
    override fun <T> renderTimePicker(timePicker: TimePicker, consumer: TagConsumer<T>): T {
        val timePickerId = "time-picker-${timePicker.hashCode()}"

        consumer.div {
            // Apply container styles
            style = "display: flex; flex-direction: column; margin-bottom: 16px;"

            // Add the label if provided
            timePicker.label?.let {
                label {
                    htmlFor = timePickerId
                    style = "margin-bottom: 4px; font-weight: 500;"
                    +it
                }
            }

            // Render the time input field
            input {
                id = timePickerId
                name = timePickerId
                type = InputType.time

                // Add step attribute for seconds if needed
                if (timePicker.showSeconds) {
                    attributes["step"] = "1"
                }

                // Apply modifier styles
                style = timePicker.modifier.toStyleString()

                // Set current value
                value = timePicker.state.value

                // Set min and max times if provided
                timePicker.min?.let { min = it }
                timePicker.max?.let { max = it }

                // Add disabled state if needed
                if (timePicker.disabled) {
                    disabled = true
                }

                // Add placeholder if provided
                timePicker.placeholder?.let {
                    placeholder = it
                }

                // Add a data attribute for time picker handling
                attributes["data-summon-time-picker"] = timePickerId
            }

            // Show validation errors if any
            val errors = timePicker.getValidationErrors()
            if (errors.isNotEmpty()) {
                div {
                    style = "color: #d32f2f; font-size: 12px; margin-top: 4px;"
                    errors.forEach { error ->
                        div {
                            +error
                        }
                    }
                }
            }
        }

        // Set up the time picker handler
        setupJsTimePickerHandler(timePickerId, timePicker)

        return consumer as T
    }

    /**
     * Helper function to set up the date picker handler.
     */
    private fun setupJsDatePickerHandler(datePickerId: String, datePicker: DatePicker) {
        // Direct implementation (without extension function)
        // This will be connected to browser DOM events when kotlinx-browser is available
        // For now, just handle the state to resolve the compilation error
        datePicker.onDateChange(datePicker.state.value)
    }

    /**
     * Helper function to set up the time picker handler.
     */
    private fun setupJsTimePickerHandler(timePickerId: String, timePicker: TimePicker) {
        // Direct implementation (without extension function)
        // This will be connected to browser DOM events when kotlinx-browser is available
        // For now, just handle the state to resolve the compilation error
        timePicker.onTimeChange(timePicker.state.value)
    }

    /**
     * Renders the container for AnimatedVisibility.
     * The container might have transition properties applied.
     * Actual content rendering and animation state are handled externally (Composer/JS).
     */
    override fun <T> renderAnimatedVisibility(
        animData: AnimatedVisibilityData,
        consumer: TagConsumer<T>
    ): T { // Changed AnimatedVisibility to AnimatedVisibilityData
        val containerId = "anim-visibility-${animData.hashCode()}"

        consumer.div(classes = animData.modifier.classes.joinToString(" ")) { // Apply modifier classes
            id = containerId
            // Apply modifier styles and potentially base transition styles
            // Transitions are complex and depend on the specific enter/exit animations defined in AnimatedVisibilityData.
            // This might involve applying CSS classes managed by external JS based on the visibility state.
            style = animData.modifier.toStyleString() // Use animData.modifier

            // Example data attribute for external JS hook:
            // attributes["data-summon-anim-visibility"] = animData.visible.toString()

            // Content is rendered by the Composer within this div based on animData.visible state.
            // The Composer/JS needs to handle adding/removing content and applying animation classes/styles.
        }

        // Complex JS animation logic removed from renderer

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders the container for AnimatedContent.
     * The container might have transition properties for content switching.
     * Actual content rendering and animation state are handled externally (Composer/JS).
     */
    override fun <T> renderAnimatedContent(
        animData: AnimatedContentData<*>,
        consumer: TagConsumer<T>
    ): T { // Changed AnimatedContent to AnimatedContentData
        val containerId = "anim-content-${animData.hashCode()}"

        consumer.div(classes = animData.modifier.classes.joinToString(" ")) { // Apply modifier classes
            id = containerId
            // Apply modifier styles and potentially base transition styles for content switching.
            // Needs to handle positioning (e.g., position: relative) if animations involve absolute positioning.
            style = "position: relative; overflow: hidden;" + animData.modifier.toStyleString() // Use animData.modifier

            // Example data attribute for external JS hook:
            // attributes["data-summon-anim-content-key"] = animData.targetState.hashCode().toString()

            // Content for the current state (animData.targetState) is rendered by the Composer within this div.
            // The Composer/JS needs to handle animating out old content and animating in new content based on state changes.
        }

        // Complex JS animation logic removed from renderer

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    // --- Temporary Style Helpers (Move to BadgeData or Utils) ---
    private fun getBadgeSeverityStyles(severity: BadgeSeverity): Map<String, String> {
        return when (severity) {
            BadgeSeverity.PRIMARY -> mapOf("background-color" to "#1976d2", "color" to "#fff")
            BadgeSeverity.SECONDARY -> mapOf("background-color" to "#9c27b0", "color" to "#fff")
            BadgeSeverity.SUCCESS -> mapOf("background-color" to "#2e7d32", "color" to "#fff")
            BadgeSeverity.WARNING -> mapOf("background-color" to "#ed6c02", "color" to "#fff")
            BadgeSeverity.ERROR -> mapOf("background-color" to "#d32f2f", "color" to "#fff")
            BadgeSeverity.INFO -> mapOf("background-color" to "#0288d1", "color" to "#fff")
            BadgeSeverity.DEFAULT -> mapOf("background-color" to "#e0e0e0", "color" to "#000")
        }
    }

    private fun getBadgeShapeStyles(shape: BadgeShape): Map<String, String> {
        return when (shape) {
            BadgeShape.RECTANGLE -> mapOf("border-radius" to "4px")
            BadgeShape.PILL -> mapOf("border-radius" to "12px")
            BadgeShape.CIRCLE, BadgeShape.DOT -> mapOf("border-radius" to "50%")
        }
    }

    private fun getBadgeSizeStyles(size: BadgeSize): Map<String, String> {
        return when (size) {
            BadgeSize.SMALL -> mapOf("padding" to "2px 6px", "font-size" to "0.75rem")
            BadgeSize.MEDIUM -> mapOf("padding" to "4px 8px", "font-size" to "0.875rem")
            BadgeSize.LARGE -> mapOf("padding" to "6px 12px", "font-size" to "1rem")
        } + if (size == BadgeSize.DOT) mapOf(
            "width" to "8px",
            "height" to "8px",
            "padding" to "0"
        ) else emptyMap() // Special handling for dot
    }

    // --- Helper/Interop Functions ---

    /**
     * Helper function to set up the click handler for a button using the existing extension function.
     */
    private fun setupJsClickHandler(buttonId: String, button: Button) {
        button.setupJsClickHandler(buttonId)
    }

    /**
     * Helper function to set up the input handler for a text field using the existing extension function.
     */
    private fun setupJsInputHandler(fieldId: String, textField: TextField) {
        textField.setupJsInputHandler(fieldId)
    }

    /**
     * Helper function to set up the form handler using the existing extension function.
     */
    private fun setupJsFormHandler(formId: String, form: Form) {
        form.setupJsFormHandler(formId)
    }

    /**
     * Helper function to set up the link handler using the existing extension function.
     */
    private fun setupJsLinkHandler(linkId: String, link: Link) {
        link.setupJsClickHandler(linkId)
    }

    /**
     * Helper function to set up the radio button handler using the existing extension function.
     */
    private fun setupJsRadioHandler(radioId: String, radioButton: RadioButton<Any>) {
        // Direct implementation (without extension function)
        // This will be connected to browser DOM events when kotlinx-browser is available
        if (radioButton.selected) {
            radioButton.onClick.invoke()
        }
    }

    /**
     * Helper function to set up the select handler using the existing extension function.
     */
    private fun setupJsSelectHandler(selectId: String, select: Select<Any>) {
        // Direct implementation (without extension function)
        // This will be connected to browser DOM events when kotlinx-browser is available
        select.onSelectedChange.invoke(select.selectedValue.value)
    }

    /**
     * Helper function to set up the switch handler using the existing extension function.
     */
    private fun setupJsSwitchHandler(switchId: String, switch: Switch) {
        // Direct implementation (without extension function)
        // This will be connected to browser DOM events when kotlinx-browser is available
        switch.onValueChange.invoke(switch.state.value)
    }

    /**
     * Helper function to set up the file upload handler using the existing extension function.
     */
    private fun setupJsFileUploadHandler(uploadId: String, fileUpload: FileUpload) {
        // Direct implementation (without extension function)
        // This will be connected to browser DOM events when kotlinx-browser is available
        // For now, just provide a minimal implementation to resolve the compilation error
        fileUpload.onFilesSelected(emptyList())
    }

    /**
     * Helper function to set up the slider handler using the existing extension function.
     */
    private fun setupJsSliderHandler(sliderId: String, rangeSlider: RangeSlider) {
        // Direct implementation (without extension function)
        // This will be connected to browser DOM events when kotlinx-browser is available
        // For now, just handle the state to resolve the compilation error
        rangeSlider.onValueChange(rangeSlider.state.value)
    }

    // --- Temporary Style Helpers (Move to TooltipData or Utils) ---
    private fun getTooltipPlacementStyles(placement: TooltipPlacement): Map<String, String> {
        return when (placement) {
            TooltipPlacement.TOP -> mapOf(
                "bottom" to "calc(100% + 5px)",
                "left" to "50%",
                "transform" to "translateX(-50%)"
            )

            TooltipPlacement.BOTTOM -> mapOf(
                "top" to "calc(100% + 5px)",
                "left" to "50%",
                "transform" to "translateX(-50%)"
            )

            TooltipPlacement.LEFT -> mapOf(
                "right" to "calc(100% + 5px)",
                "top" to "50%",
                "transform" to "translateY(-50%)"
            )

            TooltipPlacement.RIGHT -> mapOf(
                "left" to "calc(100% + 5px)",
                "top" to "50%",
                "transform" to "translateY(-50%)"
            )
        }
    }

    private fun getTooltipArrowStyles(placement: TooltipPlacement): Map<String, String> {
        val base = mapOf(
            "position" to "absolute", "width" to "0", "height" to "0",
            "border-style" to "solid", "border-color" to "transparent"
        )
        val arrowSize = "5px"
        val arrowColor = "#333" // Match tooltip background
        return base + when (placement) {
            TooltipPlacement.TOP -> mapOf(
                "left" to "50%",
                "bottom" to "-{$arrowSize}",
                "transform" to "translateX(-50%)",
                "border-width" to "$arrowSize $arrowSize 0",
                "border-top-color" to arrowColor
            )

            TooltipPlacement.BOTTOM -> mapOf(
                "left" to "50%",
                "top" to "-{$arrowSize}",
                "transform" to "translateX(-50%)",
                "border-width" to "0 $arrowSize $arrowSize",
                "border-bottom-color" to arrowColor
            )

            TooltipPlacement.LEFT -> mapOf(
                "top" to "50%",
                "right" to "-{$arrowSize}",
                "transform" to "translateY(-50%)",
                "border-width" to "$arrowSize 0 $arrowSize $arrowSize",
                "border-left-color" to arrowColor
            )

            TooltipPlacement.RIGHT -> mapOf(
                "top" to "50%",
                "left" to "-{$arrowSize}",
                "transform" to "translateY(-50%)",
                "border-width" to "$arrowSize $arrowSize $arrowSize 0",
                "border-right-color" to arrowColor
            )
        }
    }
}

/**
 * Helper function to set up the click handler for a button using the existing extension function.
 */
private fun setupJsClickHandler(buttonId: String, button: Button) {
    button.setupJsClickHandler(buttonId)
}