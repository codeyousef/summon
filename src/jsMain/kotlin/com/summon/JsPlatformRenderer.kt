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
 * Helper function to set up the click handler for a card using the existing extension function.
 */
private fun setupJsCardClickHandler(cardId: String, card: Card) {
    card.setupJsClickHandler(cardId)
}

/**
 * Helper function to set up the link handler using the existing extension function.
 */
private fun setupJsLinkHandler(linkId: String, link: Link) {
    link.setupJsClickHandler(linkId)
} 