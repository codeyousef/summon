package code.yousef.summon.routing.seo

import code.yousef.summon.annotation.Composable
import kotlinx.html.*

/**
 * Composable function for creating a semantic HTML `<header>` element.
 * @param id Optional HTML ID attribute.
 * @param className Optional HTML class attribute.
 * @param content Lambda providing the content for the header.
 */
@Composable
fun Header(
    id: String? = null,
    className: String? = null,
    content: @Composable FlowContent.() -> Unit
) {
    header(classes = className) {
        id?.let { this.id = it }
        content()
    }
}

/**
 * Composable function for creating a semantic HTML `<main>` element.
 * @param id Optional HTML ID attribute.
 * @param className Optional HTML class attribute.
 * @param content Lambda providing the content for the main element.
 */
@Composable
fun Main(
    id: String? = null,
    className: String? = null,
    content: @Composable FlowContent.() -> Unit
) {
    main(classes = className) {
        id?.let { this.id = it }
        content()
    }
}

/**
 * Composable function for creating a semantic HTML `<nav>` element.
 * @param id Optional HTML ID attribute.
 * @param className Optional HTML class attribute.
 * @param content Lambda providing the content for the nav element.
 */
@Composable
fun Nav(
    id: String? = null,
    className: String? = null,
    content: @Composable FlowContent.() -> Unit
) {
    nav(classes = className) {
        id?.let { this.id = it }
        content()
    }
}

/**
 * Composable function for creating a semantic HTML `<article>` element.
 * @param id Optional HTML ID attribute.
 * @param className Optional HTML class attribute.
 * @param content Lambda providing the content for the article element.
 */
@Composable
fun Article(
    id: String? = null,
    className: String? = null,
    content: @Composable FlowContent.() -> Unit
) {
    article(classes = className) {
        id?.let { this.id = it }
        content()
    }
}

/**
 * Composable function for creating a semantic HTML `<section>` element.
 * @param id Optional HTML ID attribute.
 * @param className Optional HTML class attribute.
 * @param content Lambda providing the content for the section element.
 */
@Composable
fun Section(
    id: String? = null,
    className: String? = null,
    content: @Composable FlowContent.() -> Unit
) {
    section(classes = className) {
        id?.let { this.id = it }
        content()
    }
}

/**
 * Composable function for creating a semantic HTML `<aside>` element.
 * @param id Optional HTML ID attribute.
 * @param className Optional HTML class attribute.
 * @param content Lambda providing the content for the aside element.
 */
@Composable
fun Aside(
    id: String? = null,
    className: String? = null,
    content: @Composable FlowContent.() -> Unit
) {
    aside(classes = className) {
        id?.let { this.id = it }
        content()
    }
}

/**
 * Composable function for creating a semantic HTML `<footer>` element.
 * @param id Optional HTML ID attribute.
 * @param className Optional HTML class attribute.
 * @param content Lambda providing the content for the footer element.
 */
@Composable
fun Footer(
    id: String? = null,
    className: String? = null,
    content: @Composable FlowContent.() -> Unit
) {
    footer(classes = className) {
        id?.let { this.id = it }
        content()
    }
}

/**
 * Composable function for creating semantic HTML heading elements (`<h1>` to `<h6>`).
 * @param level The heading level (1 to 6).
 * @param id Optional HTML ID attribute.
 * @param className Optional HTML class attribute.
 * @param content Lambda providing the content for the heading element.
 */
@Composable
fun Heading(
    level: Int = 1,
    id: String? = null,
    className: String? = null,
    content: @Composable FlowContent.() -> Unit
) {
    when (level) {
        1 -> h1(classes = className) { id?.let { this.id = it }; content() }
        2 -> h2(classes = className) { id?.let { this.id = it }; content() }
        3 -> h3(classes = className) { id?.let { this.id = it }; content() }
        4 -> h4(classes = className) { id?.let { this.id = it }; content() }
        5 -> h5(classes = className) { id?.let { this.id = it }; content() }
        6 -> h6(classes = className) { id?.let { this.id = it }; content() }
        else -> h1(classes = className) {
            id?.let { this.id = it }
            content()
        }
    }
} 