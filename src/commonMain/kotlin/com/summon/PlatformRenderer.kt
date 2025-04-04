package com.summon

import com.summon.routing.Router
import kotlinx.html.TagConsumer

/**
 * Platform-specific renderer interface that serves as a common abstraction
 * for rendering UI components across platforms.
 *
 * Following section 8 of the guide: "Exploring Common Intermediate Representations or Abstraction Layers"
 */
interface PlatformRenderer {
    /**
     * Renders a Text component to the appropriate platform output.
     */
    fun <T> renderText(text: Text, consumer: TagConsumer<T>): T

    /**
     * Renders a Button component to the appropriate platform output.
     */
    fun <T> renderButton(button: Button, consumer: TagConsumer<T>): T

    /**
     * Renders a Row component to the appropriate platform output.
     */
    fun <T> renderRow(row: Row, consumer: TagConsumer<T>): T

    /**
     * Renders a Column component to the appropriate platform output.
     */
    fun <T> renderColumn(column: Column, consumer: TagConsumer<T>): T

    /**
     * Renders a Spacer component to the appropriate platform output.
     */
    fun <T> renderSpacer(spacer: Spacer, consumer: TagConsumer<T>): T

    /**
     * Renders a TextField component to the appropriate platform output.
     */
    fun <T> renderTextField(textField: TextField, consumer: TagConsumer<T>): T

    /**
     * Renders a Form component to the appropriate platform output.
     */
    fun <T> renderForm(form: Form, consumer: TagConsumer<T>): T

    /**
     * Renders a Card component to the appropriate platform output.
     */
    fun <T> renderCard(card: Card, consumer: TagConsumer<T>): T

    /**
     * Renders a Router component to the appropriate platform output.
     */
    fun <T> renderRouter(router: Router, consumer: TagConsumer<T>): T

    /**
     * Renders an Image component to the appropriate platform output.
     */
    fun <T> renderImage(image: Image, consumer: TagConsumer<T>): T

    /**
     * Renders a Divider component to the appropriate platform output.
     */
    fun <T> renderDivider(divider: Divider, consumer: TagConsumer<T>): T

    /**
     * Renders a 404 Not Found page.
     */
    fun <T> renderNotFound(consumer: TagConsumer<T>): T
} 