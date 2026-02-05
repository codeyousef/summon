package codes.yousef.summon.seo.routes

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.style
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * SemanticHTML provides components for creating semantically meaningful HTML elements
 * These help search engines understand the structure of your content.
 *
 * @deprecated Use the new HTML DSL components in `codes.yousef.summon.components.html` instead.
 * The new package provides proper semantic HTML elements with correct tag rendering.
 */

/**
 * Creates a semantic header element.
 *
 * @deprecated Use `codes.yousef.summon.components.html.Header` instead for proper `<header>` tag rendering.
 */
@Deprecated(
    message = "Use codes.yousef.summon.components.html.Header instead for proper <header> tag rendering",
    replaceWith = ReplaceWith(
        "Header(modifier, content)",
        "codes.yousef.summon.components.html.Header"
    ),
    level = DeprecationLevel.WARNING
)
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
 * Creates a semantic main content element.
 *
 * @deprecated Use `codes.yousef.summon.components.html.Main` instead for proper `<main>` tag rendering.
 */
@Deprecated(
    message = "Use codes.yousef.summon.components.html.Main instead for proper <main> tag rendering",
    replaceWith = ReplaceWith(
        "Main(modifier, content)",
        "codes.yousef.summon.components.html.Main"
    ),
    level = DeprecationLevel.WARNING
)
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
 * Creates a semantic navigation element.
 *
 * @deprecated Use `codes.yousef.summon.components.html.Nav` instead for proper `<nav>` tag rendering.
 */
@Deprecated(
    message = "Use codes.yousef.summon.components.html.Nav instead for proper <nav> tag rendering",
    replaceWith = ReplaceWith(
        "Nav(modifier, content)",
        "codes.yousef.summon.components.html.Nav"
    ),
    level = DeprecationLevel.WARNING
)
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
 * Creates a semantic article element for standalone content.
 *
 * @deprecated Use `codes.yousef.summon.components.html.Article` instead for proper `<article>` tag rendering.
 */
@Deprecated(
    message = "Use codes.yousef.summon.components.html.Article instead for proper <article> tag rendering",
    replaceWith = ReplaceWith(
        "Article(modifier, content)",
        "codes.yousef.summon.components.html.Article"
    ),
    level = DeprecationLevel.WARNING
)
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
 * Creates a semantic section element.
 *
 * @deprecated Use `codes.yousef.summon.components.html.Section` instead for proper `<section>` tag rendering.
 */
@Deprecated(
    message = "Use codes.yousef.summon.components.html.Section instead for proper <section> tag rendering",
    replaceWith = ReplaceWith(
        "Section(modifier, content)",
        "codes.yousef.summon.components.html.Section"
    ),
    level = DeprecationLevel.WARNING
)
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
 * Creates a semantic aside element for tangentially related content.
 *
 * @deprecated Use `codes.yousef.summon.components.html.Aside` instead for proper `<aside>` tag rendering.
 */
@Deprecated(
    message = "Use codes.yousef.summon.components.html.Aside instead for proper <aside> tag rendering",
    replaceWith = ReplaceWith(
        "Aside(modifier, content)",
        "codes.yousef.summon.components.html.Aside"
    ),
    level = DeprecationLevel.WARNING
)
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
 * Creates a semantic footer element.
 *
 * @deprecated Use `codes.yousef.summon.components.html.Footer` instead for proper `<footer>` tag rendering.
 */
@Deprecated(
    message = "Use codes.yousef.summon.components.html.Footer instead for proper <footer> tag rendering",
    replaceWith = ReplaceWith(
        "Footer(modifier, content)",
        "codes.yousef.summon.components.html.Footer"
    ),
    level = DeprecationLevel.WARNING
)
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
 * Creates a heading element with proper semantics.
 *
 * @deprecated Use `codes.yousef.summon.components.html.H1` through `H6` instead for proper heading tag rendering.
 */
@Deprecated(
    message = "Use codes.yousef.summon.components.html.H1-H6 instead for proper heading tag rendering",
    replaceWith = ReplaceWith(
        "H1(modifier, content)", // or H2, H3, etc. based on level
        "codes.yousef.summon.components.html.H1"
    ),
    level = DeprecationLevel.WARNING
)
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

/**
 * Represents the `<figure>` semantic HTML element.
 *
 * @deprecated Use `codes.yousef.summon.components.html.Figure` instead for proper `<figure>` tag rendering.
 */
@Deprecated(
    message = "Use codes.yousef.summon.components.html.Figure instead for proper <figure> tag rendering",
    replaceWith = ReplaceWith(
        "Figure(modifier, content)",
        "codes.yousef.summon.components.html.Figure"
    ),
    level = DeprecationLevel.WARNING
)
@Composable
fun Figure(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderBlock(modifier, content)
}

/**
 * Represents the `<figcaption>` semantic HTML element.
 *
 * @deprecated Use `codes.yousef.summon.components.html.Figcaption` instead for proper `<figcaption>` tag rendering.
 */
@Deprecated(
    message = "Use codes.yousef.summon.components.html.Figcaption instead for proper <figcaption> tag rendering",
    replaceWith = ReplaceWith(
        "Figcaption(modifier, content)",
        "codes.yousef.summon.components.html.Figcaption"
    ),
    level = DeprecationLevel.WARNING
)
@Composable
fun FigCaption(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderInline(modifier, content)
}
