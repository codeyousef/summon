package com.summon

import com.summon.routing.Router
import com.summon.routing.RouterContext
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
        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders a Button component as a button element with appropriate attributes.
     */
    override fun <T> renderButton(button: Button, consumer: TagConsumer<T>): T {
        val buttonId = "btn-${button.hashCode()}"
        consumer.button {
            style = button.modifier.toStyleString()
            id = buttonId

            // Add the button text
            +button.label

            // Add a data attribute for click handling
            attributes["data-summon-click"] = "true"
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
     * Renders a TextField component as an input element with appropriate attributes.
     */
    override fun <T> renderTextField(textField: TextField, consumer: TagConsumer<T>): T {
        val fieldId = "tf-${textField.hashCode()}"

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

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Renders an Image component as an img element with accessibility attributes.
     */
    override fun <T> renderImage(image: Image, consumer: TagConsumer<T>): T {
        val imageId = "img-${image.hashCode()}"

        consumer.img {
            // Set id for potential JS interactions
            id = imageId

            // Set required attributes
            src = image.src
            alt = image.alt

            // Apply loading strategy
            if (image.loading != ImageLoading.AUTO) {
                attributes["loading"] = image.loading.value
            }

            // Apply optional attributes
            image.width?.let { width = it }
            image.height?.let { height = it }

            // Add detailed description if provided
            image.contentDescription?.let {
                attributes["aria-describedby"] = "img-desc-$imageId"
            }

            // Apply modifier styles
            style = image.modifier.toStyleString()

            // Set up error handling for image loading
            onError =
                "this.onerror=null; this.src='data:image/svg+xml;charset=utf-8,%3Csvg xmlns%3D%22http://www.w3.org/2000/svg%22 width%3D%22${image.width ?: "100"}%22 height%3D%22${image.height ?: "100"}%22 viewBox%3D%220 0 24 24%22%3E%3Cpath fill%3D%22%23ccc%22 d%3D%22M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z%22/%3E%3C/svg%3E';"
        }

        // Add the description in a hidden element if provided
        image.contentDescription?.let {
            consumer.div {
                id = "img-desc-$imageId"
                style = "position: absolute; height: 1px; width: 1px; overflow: hidden; clip: rect(1px, 1px, 1px, 1px);"
                +it
            }
        }

        @Suppress("UNCHECKED_CAST")
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
        @Suppress("UNCHECKED_CAST")
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

        @Suppress("UNCHECKED_CAST")
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
     * Renders a Badge component for status indicators and labels.
     */
    override fun <T> renderBadge(badge: Badge, consumer: TagConsumer<T>): T {
        val badgeId = if (badge.onClick != null) "badge-${badge.hashCode()}" else null

        consumer.span {
            // Set id if we have a click handler
            badgeId?.let { id = it }

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

        // Set up click handler if needed
        if (badge.onClick != null && badgeId != null) {
            setupJsBadgeClickHandler(badgeId, badge)
        }

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Set up click handler for badge components.
     */
    private fun setupJsBadgeClickHandler(badgeId: String, badge: Badge) {
        // This will be implemented in BadgeExt.kt
        badge.setupJsClickHandler(badgeId)
    }

    /**
     * Renders a Tooltip component that shows information on hover.
     */
    override fun <T> renderTooltip(tooltip: Tooltip, consumer: TagConsumer<T>): T {
        val tooltipId = "tooltip-${tooltip.hashCode()}"
        val contentId = "$tooltipId-content"

        consumer.div {
            // Set id for the tooltip wrapper
            id = tooltipId

            // Create a wrapper with relative positioning
            style = "position: relative; display: inline-block;" + tooltip.modifier.toStyleString()

            // Add tooltip trigger attributes with updated tooltip content id
            val triggerAttributes = tooltip.getTriggerAttributes().toMutableMap()
            triggerAttributes["aria-describedby"] = contentId

            triggerAttributes.forEach { (key, value) ->
                attributes[key] = value
            }

            // Add the trigger component
            tooltip.trigger.compose(this)

            // Add the tooltip content
            div {
                id = contentId

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

        // Set up tooltip behavior
        setupJsTooltipHandlers(tooltipId, contentId, tooltip)

        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }

    /**
     * Set up event handlers for tooltip components.
     */
    private fun setupJsTooltipHandlers(tooltipId: String, contentId: String, tooltip: Tooltip) {
        // This will be implemented in TooltipExt.kt
        tooltip.setupJsHandlers(tooltipId, contentId)
    }

    /**
     * Renders a Progress component for loading and progress indication.
     */
    override fun <T> renderProgress(progress: Progress, consumer: TagConsumer<T>): T {
        val progressId = "progress-${progress.hashCode()}"

        // Container element
        consumer.div {
            id = progressId
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
}

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