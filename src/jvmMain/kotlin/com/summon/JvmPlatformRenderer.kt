package com.summon

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.span
import kotlinx.html.button
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.style
import kotlinx.html.id
import kotlinx.html.InputType
import kotlinx.html.form
import kotlinx.html.CommonAttributeGroupFacade

import com.summon.Text
import com.summon.Button
import com.summon.Row
import com.summon.Column
import com.summon.Spacer
import com.summon.TextField
import com.summon.Form
import com.summon.PlatformRenderer

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
            // Apply the modifier styles
            val styleString = text.modifier.toStyleString()
            style = styleString
            
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
            
            // For JVM, we can't actually handle the click events
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
} 