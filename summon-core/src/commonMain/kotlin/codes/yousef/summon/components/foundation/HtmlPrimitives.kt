package codes.yousef.summon.components.foundation

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContentCompat
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Cross-platform canvas element that maps directly to `<canvas>`.
 */
@Composable
fun Canvas(
    modifier: Modifier = Modifier(),
    id: String? = null,
    width: Int? = null,
    height: Int? = null,
    ariaLabel: String? = null,
    role: String? = null,
    dataAttributes: Map<String, String> = emptyMap(),
    content: @Composable FlowContentCompat.() -> Unit = {}
) {
    val renderer = LocalPlatformRenderer.current
    var resolvedModifier = modifier
        .dataAttributes(dataAttributes)

    if (!id.isNullOrBlank()) {
        resolvedModifier = resolvedModifier.id(id)
    }
    if (!ariaLabel.isNullOrBlank()) {
        resolvedModifier = resolvedModifier.ariaAttribute("label", ariaLabel)
    }
    if (!role.isNullOrBlank()) {
        resolvedModifier = resolvedModifier.role(role)
    }

    renderer.renderCanvas(
        modifier = resolvedModifier,
        width = width,
        height = height,
        content = content
    )
}

/**
 * Renders a `<script>` tag with optional external source or inline content.
 */
@Composable
fun ScriptTag(
    src: String? = null,
    modifier: Modifier = Modifier(),
    async: Boolean = false,
    defer: Boolean = false,
    type: String? = null,
    id: String? = null,
    dataAttributes: Map<String, String> = emptyMap(),
    inlineContent: String? = null
) {
    val renderer = LocalPlatformRenderer.current
    var resolvedModifier = modifier
        .dataAttributes(dataAttributes)

    if (!id.isNullOrBlank()) {
        resolvedModifier = resolvedModifier.id(id)
    }

    renderer.renderScriptTag(
        src = src,
        async = async,
        defer = defer,
        type = type,
        modifier = resolvedModifier,
        inlineContent = inlineContent
    )
}

/**
 * Simple builder scope that mimics kotlinx.html's unaryPlus for raw output.
 */
class RawHtmlScope internal constructor() {
    private val builder = StringBuilder()

    operator fun String.unaryPlus() {
        builder.append(this)
    }

    fun append(value: String) {
        builder.append(value)
    }

    internal fun build(): String = builder.toString()
}

/**
 * Emits trusted HTML without sanitization using a lightweight builder DSL.
 */
@Composable
fun RawHtml(
    modifier: Modifier = Modifier(),
    sanitize: Boolean = false,
    content: RawHtmlScope.() -> Unit
) {
    val scope = RawHtmlScope().apply(content)
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtml(scope.build(), modifier, sanitize)
}

/**
 * Emits trusted HTML from a pre-built string.
 */
@Composable
fun RawHtml(
    html: String,
    modifier: Modifier = Modifier(),
    sanitize: Boolean = false
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtml(html, modifier, sanitize)
}
