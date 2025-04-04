package com.summon

import kotlinx.html.TagConsumer
import com.summon.Text
import com.summon.Button
import com.summon.Row
import com.summon.Column
import com.summon.Spacer
import com.summon.TextField
import com.summon.Form

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
} 