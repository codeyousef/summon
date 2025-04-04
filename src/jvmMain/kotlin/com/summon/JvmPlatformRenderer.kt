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
        @Suppress("UNCHECKED_CAST")
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
        @Suppress("UNCHECKED_CAST")
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
        consumer.a {
            // Set the href attribute
            href = link.href

            // Apply the modifier styles
            val hoverClass = link.modifier.applyStyles(this)

            // Apply additional link-specific attributes
            link.getLinkAttributes().forEach { (key, value) ->
                attributes[key] = value
            }

            // Add hover styles to the stylesheet if present
            hoverClass?.addToStyleSheet()

            // Add the link text
            +link.text
        }
        @Suppress("UNCHECKED_CAST")
        return consumer as T
    }
} 