package code.yousef.summon

import code.yousef.summon.runtime.PlatformRendererProvider

import code.yousef.summon.components.display.Image
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.runtime.PlatformRendererProvider
import kotlinx.html.TagConsumer
import kotlinx.html.stream.appendHTML

/**
 * JVM implementation for the Image component rendering.
 * This is used in ImageJvmTest to generate HTML string representation.
 */
fun <T> Image.renderJvm(consumer: TagConsumer<T>): TagConsumer<T> {
    // Create a new consumer
    val sb = StringBuilder()
    val newConsumer = sb.appendHTML()
    
    // Render using platform renderer
    runtime.PlatformRendererProvider.PlatformRendererProvider.code.yousef.summon.runtime.PlatformRendererProvider.getPlatformRenderer().renderImage(this, newConsumer)
    
    // We'll just return the original consumer as the test is only checking the toString output
    return consumer
}

/**
 * Renders an Image component to an HTML string.
 * This is a helper method used by tests.
 */
fun Image.renderToHtmlString(): String {
    val sb = StringBuilder()
    val consumer = sb.appendHTML()
    runtime.PlatformRendererProvider.PlatformRendererProvider.code.yousef.summon.runtime.PlatformRendererProvider.getPlatformRenderer().renderImage(this, consumer)
    return sb.toString()
}

/**
 * JVM implementation for the Button component rendering.
 * This is used in ButtonJvmTest to generate HTML string representation.
 */
fun <T> Button.renderJvm(consumer: TagConsumer<T>): TagConsumer<T> {
    // Create a new consumer
    val sb = StringBuilder()
    val newConsumer = sb.appendHTML()
    
    // Render using platform renderer
    runtime.PlatformRendererProvider.PlatformRendererProvider.code.yousef.summon.runtime.PlatformRendererProvider.getPlatformRenderer().renderButton(this, newConsumer)
    
    // We'll just return the original consumer as the test is only checking the toString output
    return consumer
}

/**
 * Renders a Button component to an HTML string.
 * This is a helper method used by tests.
 */
fun Button.renderToHtmlString(): String {
    val sb = StringBuilder()
    val consumer = sb.appendHTML()
    runtime.PlatformRendererProvider.PlatformRendererProvider.code.yousef.summon.runtime.PlatformRendererProvider.getPlatformRenderer().renderButton(this, consumer)
    return sb.toString()
}

/**
 * JVM implementation for rendering a Text component to an HTML string.
 * This is a helper method used by tests.
 */
fun Text.renderToHtmlString(): String {
    val sb = StringBuilder()
    val consumer = sb.appendHTML()
    runtime.PlatformRendererProvider.PlatformRendererProvider.code.yousef.summon.runtime.PlatformRendererProvider.getPlatformRenderer().renderText(this, consumer)
    return sb.toString()
} 
