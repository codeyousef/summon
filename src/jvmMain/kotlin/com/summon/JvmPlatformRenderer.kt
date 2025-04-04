@file:Suppress("UNCHECKED_CAST")

package com.summon

import com.summon.routing.Router
import com.summon.routing.RouterContext
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
        consumer.button {
            // Apply the modifier styles
            val styleString = button.modifier.toStyleString()
            style = styleString

            // Add the button text
            +button.label

            // For JVM, we can't actually handle the click events,
            // but we'll add a data attribute that could be used with JS later
            attributes["data-summon-click"] = "true"
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
     * Renders an Image component as an img element with accessibility attributes.
     */
    override fun <T> renderImage(image: Image, consumer: TagConsumer<T>): T {
        consumer.img {
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
                attributes["aria-describedby"] = "img-desc-${image.hashCode()}"
            }

            // Apply modifier styles
            style = image.modifier.toStyleString()
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
} 