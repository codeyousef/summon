package codes.yousef.summon

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer
import kotlinx.html.TagConsumer
import kotlinx.html.stream.appendHTML
import codes.yousef.summon.test.Button as TestButton
import codes.yousef.summon.test.Image as TestImage
import codes.yousef.summon.test.Text as TestText

fun <T> TestImage.renderJvm(consumer: TagConsumer<T>): TagConsumer<T> {
    val sb = StringBuilder()
    sb.appendHTML()
    LocalPlatformRenderer.current.renderImage(src, alt, Modifier())
    return consumer
}

fun TestImage.renderToHtmlString(): String {
    val sb = StringBuilder()
    sb.appendHTML()
    LocalPlatformRenderer.current.renderImage(src, alt, Modifier())
    return sb.toString()
}

fun <T> TestButton.renderJvm(consumer: TagConsumer<T>): TagConsumer<T> {
    val sb = StringBuilder()
    sb.appendHTML()
    LocalPlatformRenderer.current.renderButton(
        onClick = { onClick(Unit) },
        modifier = Modifier(),
        content = {}
    )
    return consumer
}

fun TestButton.renderToHtmlString(): String {
    val sb = StringBuilder()
    sb.appendHTML()
    LocalPlatformRenderer.current.renderButton(
        onClick = { onClick(Unit) },
        modifier = Modifier(),
        content = {}
    )
    return sb.toString()
}

fun TestText.renderToHtmlString(): String {
    val sb = StringBuilder()
    sb.appendHTML()
    LocalPlatformRenderer.current.renderText(text, Modifier())
    return sb.toString()
}
