package routing.seo

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

import kotlinx.html.FlowContent
import kotlinx.html.header
import kotlinx.html.main
import kotlinx.html.nav
import kotlinx.html.article
import kotlinx.html.section
import kotlinx.html.aside
import kotlinx.html.footer
import kotlinx.html.h1
import kotlinx.html.h2


/**
 * SemanticHTML provides components for creating semantically meaningful HTML elements
 * These help search engines understand the structure of your content
 */
object SemanticHTML {
    /**
     * Creates a semantic header element
     */
    class Header(
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable {

        @Suppress("UNCHECKED_CAST")
        override fun <T> compose(receiver: T): T {
            if (receiver is FlowContent) {
                receiver.header {
                    id?.let { this.attributes["id"] = it }
                    className?.let { this.attributes["class"] = it }
                    content()
                }
            }

            return receiver
        }
    }

    /**
     * Creates a semantic main content element
     */
    class Main(
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable {

        @Suppress("UNCHECKED_CAST")
        override fun <T> compose(receiver: T): T {
            if (receiver is FlowContent) {
                receiver.main {
                    id?.let { this.attributes["id"] = it }
                    className?.let { this.attributes["class"] = it }
                    content()
                }
            }

            return receiver
        }
    }

    /**
     * Creates a semantic navigation element
     */
    class Nav(
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable {

        @Suppress("UNCHECKED_CAST")
        override fun <T> compose(receiver: T): T {
            if (receiver is FlowContent) {
                receiver.nav {
                    id?.let { this.attributes["id"] = it }
                    className?.let { this.attributes["class"] = it }
                    content()
                }
            }

            return receiver
        }
    }

    /**
     * Creates a semantic article element for standalone content
     */
    class Article(
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable {

        @Suppress("UNCHECKED_CAST")
        override fun <T> compose(receiver: T): T {
            if (receiver is FlowContent) {
                receiver.article {
                    id?.let { this.attributes["id"] = it }
                    className?.let { this.attributes["class"] = it }
                    content()
                }
            }

            return receiver
        }
    }

    /**
     * Creates a semantic section element
     */
    class Section(
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable {

        @Suppress("UNCHECKED_CAST")
        override fun <T> compose(receiver: T): T {
            if (receiver is FlowContent) {
                receiver.section {
                    id?.let { this.attributes["id"] = it }
                    className?.let { this.attributes["class"] = it }
                    content()
                }
            }

            return receiver
        }
    }

    /**
     * Creates a semantic aside element for tangentially related content
     */
    class Aside(
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable {

        @Suppress("UNCHECKED_CAST")
        override fun <T> compose(receiver: T): T {
            if (receiver is FlowContent) {
                receiver.aside {
                    id?.let { this.attributes["id"] = it }
                    className?.let { this.attributes["class"] = it }
                    content()
                }
            }

            return receiver
        }
    }

    /**
     * Creates a semantic footer element
     */
    class Footer(
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable {

        @Suppress("UNCHECKED_CAST")
        override fun <T> compose(receiver: T): T {
            if (receiver is FlowContent) {
                receiver.footer {
                    id?.let { this.attributes["id"] = it }
                    className?.let { this.attributes["class"] = it }
                    content()
                }
            }

            return receiver
        }
    }

    /**
     * Creates a heading element with proper semantics
     */
    class Heading(
        private val level: Int = 1,
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable {

        @Suppress("UNCHECKED_CAST")
        override fun <T> compose(receiver: T): T {
            if (receiver is FlowContent) {
                when (level) {
                    1 -> receiver.h1 {
                        id?.let { this.attributes["id"] = it }
                        className?.let { this.attributes["class"] = it }
                        content()
                    }

                    2 -> receiver.h2 {
                        id?.let { this.attributes["id"] = it }
                        className?.let { this.attributes["class"] = it }
                        content()
                    }
                    // Add h3-h6 here if needed
                    else -> receiver.h1 {
                        id?.let { this.attributes["id"] = it }
                        className?.let { this.attributes["class"] = it }
                        content()
                    }
                }
            }

            return receiver
        }
    }
}

/**
 * Renders content within an HTML `<article>` tag.
 */
@Composable
fun Article(modifier: Modifier = Modifier(), content: @Composable () -> Unit) {
    HtmlTag("article", modifier, content)
}

/**
 * Renders content within an HTML `<aside>` tag.
 */
@Composable
fun Aside(modifier: Modifier = Modifier(), content: @Composable () -> Unit) {
    HtmlTag("aside", modifier, content)
}

/**
 * Renders content within an HTML `<nav>` tag.
 */
@Composable
fun Nav(modifier: Modifier = Modifier(), content: @Composable () -> Unit) {
    HtmlTag("nav", modifier, content)
}

/**
 * Renders content within an HTML `<section>` tag.
 */
@Composable
fun Section(modifier: Modifier = Modifier(), content: @Composable () -> Unit) {
    HtmlTag("section", modifier, content)
}

/**
 * Renders content within an HTML `<header>` tag.
 */
@Composable
fun Header(modifier: Modifier = Modifier(), content: @Composable () -> Unit) {
    HtmlTag("header", modifier, content)
}

/**
 * Renders content within an HTML `<footer>` tag.
 */
@Composable
fun Footer(modifier: Modifier = Modifier(), content: @Composable () -> Unit) {
    HtmlTag("footer", modifier, content)
}

/**
 * Renders content within an HTML `<main>` tag.
 */
@Composable
fun Main(modifier: Modifier = Modifier(), content: @Composable () -> Unit) {
    HtmlTag("main", modifier, content)
}

/**
 * Renders content within an HTML `<figure>` tag.
 */
@Composable
fun Figure(modifier: Modifier = Modifier(), content: @Composable () -> Unit) {
    HtmlTag("figure", modifier, content)
}

/**
 * Renders content within an HTML `<figcaption>` tag.
 */
@Composable
fun FigCaption(modifier: Modifier = Modifier(), content: @Composable () -> Unit) {
    HtmlTag("figcaption", modifier, content)
}

// --- Helper for generic HTML tag rendering ---
@Composable
private fun HtmlTag(tag: String, modifier: Modifier, content: @Composable () -> Unit) {
    val composer = CompositionLocal.currentComposer
    composer?.startNode() // Start node for the specific semantic tag
    if (composer?.inserting == true) {
        PlatformRendererProvider.getPlatformRenderer().renderHtmlTag(tag, modifier) // Assumes renderer method exists
    }
    content() // Compose children
    composer?.endNode() // End node
} 
