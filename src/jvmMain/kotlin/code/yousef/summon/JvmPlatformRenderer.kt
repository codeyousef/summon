@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon

import code.yousef.summon.animation.AnimatedContent
import code.yousef.summon.animation.AnimatedVisibility
import code.yousef.summon.components.display.*
import code.yousef.summon.components.feedback.*
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.*
import code.yousef.summon.components.navigation.Link
import code.yousef.summon.components.navigation.TabLayout
import code.yousef.summon.core.Composable
import code.yousef.summon.core.PlatformRenderer
import code.yousef.summon.routing.Router
import code.yousef.summon.routing.RouterContext
import kotlinx.html.*

/**
 * JVM implementation of the PlatformRenderer interface.
 * Provides JVM-specific rendering for UI components.
 */
class JvmPlatformRenderer : PlatformRenderer {
    /**
     * Renders a Text component as a span element with the text content.
     */
    override fun <T> renderText(text: Text, consumer: TagConsumer<T>): T {
        consumer.span {
            // Apply the modifier styles and additional text-specific styles
            val additionalStyles = text.getAdditionalStyles()
            val combinedStyles = text.modifier.styles + additionalStyles
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Apply accessibility attributes
            text.getAccessibilityAttributes().forEach { (key, value) ->
                attributes[key] = value
            }

            // Add the text content
            +text.text
        }

        return consumer as T
    }

    /**
     * Renders a Button component as a button element with appropriate attributes.
     */
    override fun <T> renderButton(button: Button, consumer: TagConsumer<T>): T {
        // Collect all attributes before creating the tag
        val attributesMap = mutableMapOf<String, String>()
        
        // Add data attribute for click events
        attributesMap["data-summon-click"] = "true"
        
        // Add disabled attribute if button is disabled
        if (button.disabled) {
            attributesMap["disabled"] = "disabled"
        }
        
        // Apply styles from modifier
        val styleString = button.modifier.toStyleString()
        
        consumer.button {
            // Apply collected attributes
            attributesMap.forEach { (key, value) ->
                attributes[key] = value
            }
            
            // Apply styles
            style = styleString
            
            // Add the button text
            +button.label
        }

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

                // For JVM, we can't handle change events directly
                // but add a data attribute that could be used with JS later
                attributes["data-summon-input"] = "true"
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

                // For JVM, we can't handle change events directly
                // but add a data attribute that could be used with JS later
                attributes["data-summon-textarea"] = "true"
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

                // For JVM, we can't handle change events directly
                // but add a data attribute that could be used with JS later
                attributes["data-summon-checkbox"] = "true"
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

                // For JVM, we can't handle change events directly
                attributes["data-summon-radio"] = "true"
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

        @Suppress("UNCHECKED_CAST")
        return consumer as T
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

                // For JVM, we can't handle change events directly
                attributes["data-summon-select"] = "true"
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

        @Suppress("UNCHECKED_CAST")
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

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a Router component to the appropriate platform output.
     */
    override fun <T> renderRouter(router: Router, consumer: TagConsumer<T>): T {
        return consumer.div {
            // Set the router as the current context
            val component = RouterContext.withRouter(router) {
                router.getCurrentComponent()
            }

            // Render the current component
            component.compose(this)
        }
    }

    /**
     * Renders a 404 Not Found page.
     */
    override fun <T> renderNotFound(consumer: TagConsumer<T>): T {
        return consumer.div {
            attributes["style"] = "padding: 20px; text-align: center;"
            h1 {
                +"404 - Page Not Found"
            }
            p {
                +"The page you are looking for doesn't exist or has been moved."
            }
            a(href = "/") {
                +"Go to Home Page"
            }
        }
    }

    /**
     * Renders a Card component as a div with appropriate styling for a card.
     */
    override fun <T> renderCard(card: Card, consumer: TagConsumer<T>): T {
        consumer.div {
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

        return consumer as T
    }

    /**
     * Renders an Image component as an img element with appropriate attributes.
     */
    override fun <T> renderImage(image: Image, consumer: TagConsumer<T>): T {
        // Collect all attributes before creating the tag
        val attributesMap = mutableMapOf<String, String>()
        
        // Set required attributes
        attributesMap["src"] = image.src
        attributesMap["alt"] = image.alt
        
        // Apply loading strategy
        if (image.loading != ImageLoading.AUTO) {
            attributesMap["loading"] = image.loading.value
        }
        
        // Apply optional attributes
        image.width?.let { attributesMap["width"] = it }
        image.height?.let { attributesMap["height"] = it }
        
        // Add detailed description if provided
        image.contentDescription?.let {
            attributesMap["aria-describedby"] = "img-desc-${image.hashCode()}"
        }
        
        // Get modifier styles
        val styleString = image.modifier.toStyleString()
        
        consumer.img {
            // Apply collected attributes
            attributesMap.forEach { (key, value) ->
                attributes[key] = value
            }
            
            // Apply modifier styles
            style = styleString
        }
        
        // Add the description in a hidden element if provided
        image.contentDescription?.let {
            consumer.div {
                id = "img-desc-${image.hashCode()}"
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
        consumer.a {
            // Set the href attribute
            href = link.href

            // Apply the modifier styles
            style = link.modifier.toStyleString()

            // Apply additional link-specific attributes
            link.getLinkAttributes().forEach { (key, value) ->
                attributes[key] = value
            }

            // Add the link text
            +link.text
        }
        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders an Icon component based on its type (SVG, font, or image).
     */
    override fun <T> renderIcon(icon: Icon, consumer: TagConsumer<T>): T {
        when (icon.type) {
            IconType.SVG -> {
                consumer.span {
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
                }
            }

            IconType.FONT -> {
                consumer.span {
                    // Apply icon styles and modifier
                    val styles = icon.getAdditionalStyles()
                    style = (icon.modifier.styles + styles).entries.joinToString(";") { (key, value) -> "$key:$value" }

                    // Apply accessibility attributes
                    icon.getAccessibilityAttributes().forEach { (key, value) ->
                        attributes[key] = value
                    }

                    // Add the icon content (font character)
                    +icon.name
                }
            }

            IconType.IMAGE -> {
                consumer.img {
                    // Apply modifier styles
                    style = icon.modifier.toStyleString()

                    // Set image attributes
                    src = icon.name
                    alt = icon.ariaLabel ?: ""

                    // Set width and height
                    width = icon.size
                    height = icon.size
                }
            }
        }

        return consumer as T
    }

    /**
     * Renders an Alert component for notifications and messages.
     */
    override fun <T> renderAlert(alert: Alert, consumer: TagConsumer<T>): T {
        consumer.div {
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
                    style =
                        "background: none; border: none; cursor: pointer; position: absolute; top: 8px; right: 8px; padding: 4px; color: inherit; opacity: 0.6;"
                    attributes["aria-label"] = "Close"
                    attributes["data-summon-alert-dismiss"] = "true"

                    // Use a simple × character for the button
                    +"×"
                }
            }
        }

        return consumer as T
    }

    /**
     * Renders a Badge component for status indicators and labels.
     */
    override fun <T> renderBadge(badge: Badge, consumer: TagConsumer<T>): T {
        consumer.span {
            // Combine all style categories
            val typeStyles = badge.getTypeStyles()
            val shapeStyles = badge.getShapeStyles()
            val sizeStyles = badge.getSizeStyles()

            val combinedStyles = badge.modifier.styles + typeStyles + shapeStyles + sizeStyles + mapOf(
                "display" to "inline-flex",
                "align-items" to "center",
                "justify-content" to "center",
                "font-weight" to "500",
                "line-height" to "1",
                "white-space" to "nowrap"
            )

            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Apply accessibility attributes
            badge.getAccessibilityAttributes().forEach { (key, value) ->
                attributes[key] = value
            }

            // Add click handling if provided
            if (badge.onClick != null) {
                attributes["data-summon-click"] = "true"
            }

            // Add content if not a dot badge
            if (badge.shape != BadgeShape.DOT) {
                +badge.content
            }
        }

        return consumer as T
    }

    /**
     * Renders a Tooltip component that shows information on hover.
     */
    override fun <T> renderTooltip(tooltip: Tooltip, consumer: TagConsumer<T>): T {
        consumer.div {
            // Create a wrapper with relative positioning
            style = "position: relative; display: inline-block;" + tooltip.modifier.toStyleString()

            // Add tooltip trigger attributes
            tooltip.getTriggerAttributes().forEach { (key, value) ->
                attributes[key] = value
            }

            // Add the trigger component
            tooltip.trigger.compose(this)

            // Add the tooltip content
            div {
                id = "tooltip-content"

                // Add tooltip styling
                val placementStyles = tooltip.getPlacementStyles()
                style = placementStyles.entries.joinToString(";") { (key, value) -> "$key:$value" } +
                        ";position: absolute; background-color: #333; color: white; padding: 6px 10px; " +
                        "border-radius: 4px; font-size: 14px; z-index: 1000; pointer-events: none; " +
                        "opacity: 0; transition: opacity 0.3s; max-width: 250px; text-align: center;"

                // Apply accessibility attributes
                tooltip.getAccessibilityAttributes().forEach { (key, value) ->
                    attributes[key] = value
                }

                // Add content
                +tooltip.content

                // Add arrow if needed
                if (tooltip.showArrow) {
                    div {
                        val arrowStyles = tooltip.getArrowStyles()
                        style = arrowStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
                    }
                }
            }
        }

        return consumer as T
    }

    /**
     * Renders a Progress component for loading and progress indication.
     */
    override fun <T> renderProgress(progress: Progress, consumer: TagConsumer<T>): T {
        // Container element
        consumer.div {
            style = "display: flex; flex-direction: column;" + progress.modifier.toStyleString()

            // Add label if present
            progress.label?.let {
                div {
                    style = "margin-bottom: 4px; font-size: 14px;"
                    +it
                }
            }

            // Main progress container
            div {
                // Add basic styles for track
                val typeStyles = progress.getTypeStyles()
                style = typeStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

                // Add accessibility attributes
                progress.getAccessibilityAttributes().forEach { (key, value) ->
                    attributes[key] = value
                }

                // For determinate progress, add the value indicator
                if (!progress.isIndeterminate()) {
                    // Progress indicator
                    div {
                        // Calculate width percentage for linear or appropriate styles for circular
                        val percentage = progress.getPercentage()

                        val styles = when (progress.type) {
                            ProgressType.LINEAR -> mapOf(
                                "width" to "$percentage%",
                                "height" to "100%",
                                "background-color" to progress.color
                            )

                            ProgressType.CIRCULAR -> mapOf(
                                "width" to "100%",
                                "height" to "100%",
                                "border-radius" to "50%",
                                "background" to "conic-gradient(${progress.color} ${percentage * 3.6}deg, transparent 0deg)",
                                "transform" to "rotate(-90deg)"
                            )

                            else -> mapOf(
                                "width" to "100%",
                                "height" to "100%"
                            )
                        }

                        // Add animation styles
                        val animationStyles = progress.getAnimationStyles()
                        val combinedStyles = styles + animationStyles

                        style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
                    }
                } else {
                    // For indeterminate progress, render animation
                    val indeterminateStyles = when (progress.type) {
                        ProgressType.CIRCULAR -> mapOf(
                            "border" to "${progress.thickness} solid ${progress.trackColor}",
                            "border-top" to "${progress.thickness} solid ${progress.color}",
                            "border-radius" to "50%",
                            "width" to (progress.getTypeStyles()["width"]?.toString() ?: progress.size),
                            "height" to (progress.getTypeStyles()["height"]?.toString() ?: progress.size),
                            "animation" to "spin 1s linear infinite"
                        )

                        else -> mapOf(
                            "position" to "relative",
                            "overflow" to "hidden",
                            "height" to (progress.getTypeStyles()["height"]?.toString() ?: progress.thickness)
                        )
                    }

                    style = indeterminateStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

                    // For linear indeterminate, add the animation element
                    if (progress.type != ProgressType.CIRCULAR) {
                        div {
                            style =
                                "position: absolute; height: 100%; width: 30%; background-color: ${progress.color};" +
                                        "animation: indeterminate 2s ease-in-out infinite;"
                        }
                    }

                    // Add animation keyframes
                    style {
                        unsafe {
                            if (progress.type == ProgressType.CIRCULAR) {
                                raw(
                                    """
                                    @keyframes spin {
                                        0% { transform: rotate(0deg); }
                                        100% { transform: rotate(360deg); }
                                    }
                                """.trimIndent()
                                )
                            } else {
                                raw(
                                    """
                                    @keyframes indeterminate {
                                        0% { left: -30%; }
                                        100% { left: 100%; }
                                    }
                                """.trimIndent()
                                )
                            }

                            // Add any custom animation keyframes from the progress component
                            progress.getAnimationKeyframes()?.let { raw(it) }
                        }
                    }
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a Box component as a div with absolute positioning.
     */
    override fun <T> renderBox(box: Box, consumer: TagConsumer<T>): T {
        consumer.div {
            val combinedStyles = box.modifier.styles + mapOf(
                "position" to "relative",
                "display" to "flex"
            )
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Render each child
            box.content.forEach { child ->
                child.compose(this)
            }
        }

        return consumer as T
    }

    /**
     * Renders a Grid component as a div with CSS Grid properties.
     */
    override fun <T> renderGrid(grid: Grid, consumer: TagConsumer<T>): T {
        consumer.div {
            val gridStyles = mapOf(
                "display" to "grid",
                "grid-template-columns" to grid.columns,
                "grid-template-rows" to grid.rows,
                "gap" to grid.gap
            ) + if (grid.areas != null) {
                mapOf("grid-template-areas" to grid.areas)
            } else {
                emptyMap()
            }

            val combinedStyles = grid.modifier.styles + gridStyles
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Render each child
            grid.content.forEach { child ->
                child.compose(this)
            }
        }

        return consumer as T
    }

    /**
     * Renders an AspectRatio component as a div with padding-bottom trick for aspect ratio.
     */
    override fun <T> renderAspectRatio(aspectRatio: AspectRatio, consumer: TagConsumer<T>): T {
        // The padding-bottom technique is used to maintain aspect ratio
        // padding-bottom = (height / width) * 100%
        val paddingBottom = "${(1 / aspectRatio.ratio) * 100}%"

        consumer.div {
            val outerStyles = aspectRatio.modifier.styles + mapOf(
                "position" to "relative",
                "width" to "100%",
                "padding-bottom" to paddingBottom
            )
            style = outerStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            div {
                // This inner div will contain the actual content
                val innerStyles = mapOf(
                    "position" to "absolute",
                    "top" to "0",
                    "left" to "0",
                    "width" to "100%",
                    "height" to "100%"
                )
                style = innerStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

                // Render the content
                aspectRatio.content.compose(this)
            }
        }

        return consumer as T
    }

    /**
     * Renders a ResponsiveLayout component with media queries for different screen sizes.
     */
    override fun <T> renderResponsiveLayout(responsiveLayout: ResponsiveLayout, consumer: TagConsumer<T>): T {
        consumer.div {
            style = responsiveLayout.modifier.toStyleString()

            // For server-side rendering, we'll render all versions and use CSS to show/hide
            // Note: This is not the most efficient, but works for SSR without JS

            // Default content first (will be overridden by media queries)
            div {
                style = "display: block;"
                responsiveLayout.defaultContent.compose(this)
            }

            // Media query specific content
            val breakpoints = mapOf(
                ScreenSize.SMALL to "max-width: 599px",
                ScreenSize.MEDIUM to "min-width: 600px and max-width: 959px",
                ScreenSize.LARGE to "min-width: 960px and max-width: 1279px",
                ScreenSize.XLARGE to "min-width: 1280px"
            )

            responsiveLayout.content.forEach { (size, content) ->
                val mediaQuery = breakpoints[size]
                div {
                    // In server-side rendering, we use a data attribute that will be used by JS
                    // to show/hide based on media query on the client
                    attributes["data-media-query"] = mediaQuery ?: ""
                    style = "display: none;" // Hidden by default, JS will show when appropriate
                    content.compose(this)
                }
            }
        }

        return consumer as T
    }

    /**
     * Helper function to safely access the itemContent with wildcard generics
     */
    private fun <T, I> getComposable(lazyList: LazyColumn<*>, item: Any?): Composable {
        // This is a workaround for type erasure and wildcard generics
        @Suppress("UNCHECKED_CAST")
        val typedList = lazyList as LazyColumn<I>
        @Suppress("UNCHECKED_CAST")
        return typedList.itemContent(item as I)
    }

    /**
     * Renders a LazyColumn component as a div with vertical scrolling.
     */
    override fun <T> renderLazyColumn(lazyColumn: LazyColumn<*>, consumer: TagConsumer<T>): T {
        consumer.div {
            val combinedStyles = lazyColumn.modifier.styles + mapOf(
                "display" to "flex",
                "flex-direction" to "column",
                "overflow-y" to "auto"
            )
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // In server-side rendering, we'll render all items
            // On client-side, JS will handle virtualization
            lazyColumn.items.forEachIndexed { index, item ->
                @Suppress("UNCHECKED_CAST")
                val composable = getComposable<T, Any>(lazyColumn, item)
                div {
                    attributes["data-lazy-index"] = index.toString()
                    composable.compose(this)
                }
            }
        }

        return consumer as T
    }

    /**
     * Helper function to safely access the itemContent with wildcard generics
     */
    private fun <T, I> getComposable(lazyList: LazyRow<*>, item: Any?): Composable {
        // This is a workaround for type erasure and wildcard generics
        @Suppress("UNCHECKED_CAST")
        val typedList = lazyList as LazyRow<I>
        @Suppress("UNCHECKED_CAST")
        return typedList.itemContent(item as I)
    }

    /**
     * Renders a LazyRow component as a div with horizontal scrolling.
     */
    override fun <T> renderLazyRow(lazyRow: LazyRow<*>, consumer: TagConsumer<T>): T {
        consumer.div {
            val combinedStyles = lazyRow.modifier.styles + mapOf(
                "display" to "flex",
                "flex-direction" to "row",
                "overflow-x" to "auto"
            )
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // In server-side rendering, we'll render all items
            // On client-side, JS will handle virtualization
            lazyRow.items.forEachIndexed { index, item ->
                @Suppress("UNCHECKED_CAST")
                val composable = getComposable<T, Any>(lazyRow, item)
                div {
                    attributes["data-lazy-index"] = index.toString()
                    composable.compose(this)
                }
            }
        }

        return consumer as T
    }

    /**
     * Renders a TabLayout component as a tabbed interface.
     */
    override fun <T> renderTabLayout(tabLayout: TabLayout, consumer: TagConsumer<T>): T {
        consumer.div {
            val combinedStyles = tabLayout.modifier.styles + mapOf(
                "display" to "flex",
                "flex-direction" to "column"
            )
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Tab headers
            div {
                style = "display: flex; flex-direction: row; border-bottom: 1px solid #ddd;"

                tabLayout.tabs.forEachIndexed { index, tab ->
                    val isSelected = index == tabLayout.selectedTabIndex
                    div {
                        val tabStyles = mapOf(
                            "padding" to "10px 15px",
                            "cursor" to "pointer",
                            "border-bottom" to if (isSelected) "2px solid #1976d2" else "none",
                            "color" to if (isSelected) "#1976d2" else "inherit",
                            "font-weight" to if (isSelected) "bold" else "normal"
                        )
                        style = tabStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
                        attributes["data-tab-index"] = index.toString()

                        // Render icon if present
                        tab.icon?.compose(this)

                        // Render title
                        +tab.title

                        // Render close button if closable
                        if (tab.isClosable) {
                            span {
                                style = "margin-left: 5px; font-size: 12px;"
                                +"✕"
                                // In real implementation, would add data attributes for JS handling
                            }
                        }
                    }
                }
            }

            // Tab content
            div {
                style = "padding: 15px 0;"
                if (tabLayout.selectedTabIndex in tabLayout.tabs.indices) {
                    tabLayout.tabs[tabLayout.selectedTabIndex].content.compose(this)
                }
            }
        }

        return consumer as T
    }

    /**
     * Renders an ExpansionPanel component as a collapsible section.
     */
    override fun <T> renderExpansionPanel(expansionPanel: ExpansionPanel, consumer: TagConsumer<T>): T {
        val panelId = "panel-${expansionPanel.hashCode()}"

        consumer.div {
            val combinedStyles = expansionPanel.modifier.styles + mapOf(
                "border" to "1px solid #ddd",
                "border-radius" to "4px",
                "margin-bottom" to "10px"
            )
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
            id = panelId

            // Header
            div {
                val headerStyles = mapOf(
                    "padding" to "10px 15px",
                    "background-color" to "#f5f5f5",
                    "cursor" to "pointer",
                    "display" to "flex",
                    "justify-content" to "space-between",
                    "align-items" to "center"
                )
                style = headerStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
                attributes["data-expansion-header"] = "true"

                // Title and optional icon
                div {
                    style = "display: flex; align-items: center;"

                    // Render icon if present
                    expansionPanel.icon?.compose(this)

                    // Render title
                    +expansionPanel.title
                }

                // Expansion icon
                div {
                    +(if (expansionPanel.isExpanded) "▲" else "▼")
                }
            }

            // Content
            div {
                val contentStyles = mapOf(
                    "padding" to if (expansionPanel.isExpanded) "15px" else "0",
                    "height" to if (expansionPanel.isExpanded) "auto" else "0",
                    "overflow" to "hidden",
                    "transition" to "all 0.3s ease"
                )
                style = contentStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
                attributes["data-expansion-content"] = "true"

                if (expansionPanel.isExpanded) {
                    expansionPanel.content.compose(this)
                }
            }
        }

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

        @Suppress("UNCHECKED_CAST")
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
                    attributes["data-summon-switch"] = "true"
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

        @Suppress("UNCHECKED_CAST")
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
                    attributes["data-summon-file-upload"] = "true"
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

        @Suppress("UNCHECKED_CAST")
        return consumer as T
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

                // For JVM, we can't handle change events directly
                attributes["data-summon-slider"] = "true"
            }
        }

        @Suppress("UNCHECKED_CAST")
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

                // For JVM, we can't handle change events directly
                // but add a data attribute that could be used with JS later
                attributes["data-summon-date-picker"] = "true"
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

                // For JVM, we can't handle change events directly
                // but add a data attribute that could be used with JS later
                attributes["data-summon-time-picker"] = "true"
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

        return consumer as T
    }

    /**
     * Renders an AnimatedVisibility component as a div with CSS transitions.
     */
    override fun <T> renderAnimatedVisibility(animatedVisibility: AnimatedVisibility, consumer: TagConsumer<T>): T {
        consumer.div {
            // Combine base styles with animation and component modifiers
            val baseStyles = if (animatedVisibility.visible) {
                animatedVisibility.getAnimationStyles()
            } else {
                animatedVisibility.getInitialStyles()
            }

            val combinedStyles = animatedVisibility.modifier.styles + baseStyles
            style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Only render content if visible (for initial load) or during animation
            if (animatedVisibility.visible) {
                // Render each child in the content list
                animatedVisibility.content.forEach { child ->
                    child.compose(this)
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders an AnimatedContent component with transitions between different content states.
     */
    override fun <T> renderAnimatedContent(animatedContent: AnimatedContent<*>, consumer: TagConsumer<T>): T {
        consumer.div {
            // Apply container styles
            val containerStyles = animatedContent.getContainerStyles() + animatedContent.modifier.styles
            style = containerStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

            // Update content based on current state
            animatedContent.updateContent()

            // Render current content with animation styles
            div {
                val currentStyles = animatedContent.getCurrentContentStyles()
                style = currentStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

                // Render each child in the current content - use unchecked cast to handle wildcard type
                @Suppress("UNCHECKED_CAST")
                val value = animatedContent.targetState.value
                val factory = animatedContent.contentFactory as (Any?) -> List<Composable>
                val content = factory(value)
                content.forEach { child ->
                    child.compose(this)
                }
            }

            // Render previous content during transition (if available)
            animatedContent.getPreviousContentStyles().let { prevStyles ->
                div {
                    style = prevStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }

                    // We don't actually render the previous content here, as it would be
                    // expensive and unnecessary. Just having the div with transition
                    // styles is enough for the animation effect.
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }
} 