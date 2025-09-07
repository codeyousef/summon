package code.yousef.summon.routing.seo

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import kotlinx.html.FlowContent

/**
 * SemanticHTML provides components for creating semantically meaningful HTML elements
 * These help search engines understand the structure of your content
 */

/**
 * Creates a semantic header element
 */
@Composable
fun Header(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = modifier
        .let { if (id != null) it.style("id", id) else it }
        .let { if (className != null) it.style("class", className) else it }
        .style("role", "banner")
    renderer.renderBlock(finalModifier, content)
}

/**
 * Creates a semantic main content element
 */
@Composable
fun Main(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = modifier
        .let { if (id != null) it.style("id", id) else it }
        .let { if (className != null) it.style("class", className) else it }
        .style("role", "main")
    renderer.renderBlock(finalModifier, content)
}

/**
 * Creates a semantic navigation element
 */
@Composable
fun Nav(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = modifier
        .let { if (id != null) it.style("id", id) else it }
        .let { if (className != null) it.style("class", className) else it }
        .style("role", "navigation")
    renderer.renderBlock(finalModifier, content)
}

/**
 * Creates a semantic article element for standalone content
 */
@Composable
fun Article(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = modifier
        .let { if (id != null) it.style("id", id) else it }
        .let { if (className != null) it.style("class", className) else it }
        .style("role", "article")
    renderer.renderBlock(finalModifier, content)
}

/**
 * Creates a semantic section element
 */
@Composable
fun Section(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = modifier
        .let { if (id != null) it.style("id", id) else it }
        .let { if (className != null) it.style("class", className) else it }
        .style("role", "region")
    renderer.renderBlock(finalModifier, content)
}

/**
 * Creates a semantic aside element for tangentially related content
 */
@Composable
fun Aside(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = modifier
        .let { if (id != null) it.style("id", id) else it }
        .let { if (className != null) it.style("class", className) else it }
        .style("role", "complementary")
    renderer.renderBlock(finalModifier, content)
}

/**
 * Creates a semantic footer element
 */
@Composable
fun Footer(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = modifier
        .let { if (id != null) it.style("id", id) else it }
        .let { if (className != null) it.style("class", className) else it }
        .style("role", "contentinfo")
    renderer.renderBlock(finalModifier, content)
}

/**
 * Creates a heading element with proper semantics
 */
@Composable
fun Heading(
    level: Int,
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = modifier
        .let { if (id != null) it.style("id", id) else it }
        .let { if (className != null) it.style("class", className) else it }
        .style("role", "heading")
        .style("aria-level", level.toString())
    renderer.renderBlock(finalModifier, content)
}

/** Represents the <figure> semantic HTML element. */
@Composable
fun Figure(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderBlock(modifier, content)
}

/** Represents the <figcaption> semantic HTML element. */
@Composable
fun FigCaption(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderInline(modifier, content)
}