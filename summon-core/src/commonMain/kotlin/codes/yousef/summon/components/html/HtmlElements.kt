package codes.yousef.summon.components.html

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Structural HTML5 semantic elements for building accessible, SEO-friendly pages.
 *
 * These components render actual HTML5 semantic elements (`<header>`, `<nav>`, `<main>`, etc.)
 * rather than generic `<div>` elements with ARIA roles. This provides better:
 * - Screen reader support
 * - Search engine optimization
 * - Document outline structure
 * - Browser default styling
 */

/**
 * Renders an HTML `<header>` element.
 *
 * The header element represents introductory content, typically containing navigation aids,
 * logos, search forms, or author information.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the header
 */
@Composable
fun Header(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("header", modifier, content)
}

/**
 * Renders an HTML `<nav>` element.
 *
 * The nav element represents a section containing navigation links to other pages
 * or to parts within the page.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the nav
 */
@Composable
fun Nav(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("nav", modifier, content)
}

/**
 * Renders an HTML `<main>` element.
 *
 * The main element represents the dominant content of the document body.
 * There should be only one visible main element per page.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the main
 */
@Composable
fun Main(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("main", modifier, content)
}

/**
 * Renders an HTML `<footer>` element.
 *
 * The footer element represents a footer for its nearest sectioning content or
 * sectioning root element. Typically contains author information, copyright, or related links.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the footer
 */
@Composable
fun Footer(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("footer", modifier, content)
}

/**
 * Renders an HTML `<section>` element.
 *
 * The section element represents a standalone section of content that has its own heading.
 * Use for thematic grouping of content.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the section
 */
@Composable
fun Section(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("section", modifier, content)
}

/**
 * Renders an HTML `<article>` element.
 *
 * The article element represents a self-contained composition that is intended to be
 * independently distributable or reusable (e.g., a blog post, news article, comment).
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the article
 */
@Composable
fun Article(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("article", modifier, content)
}

/**
 * Renders an HTML `<aside>` element.
 *
 * The aside element represents content tangentially related to the surrounding content,
 * such as sidebars, pull quotes, or advertising.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the aside
 */
@Composable
fun Aside(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("aside", modifier, content)
}

/**
 * Renders an HTML `<address>` element.
 *
 * The address element represents contact information for the nearest article or body ancestor.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the address
 */
@Composable
fun Address(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("address", modifier, content)
}

/**
 * Renders an HTML `<hgroup>` element.
 *
 * The hgroup element represents a heading grouped with secondary content such as
 * subheadings, alternative titles, or taglines.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the hgroup
 */
@Composable
fun Hgroup(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("hgroup", modifier, content)
}

/**
 * Renders an HTML `<search>` element.
 *
 * The search element represents a section containing form controls for searching.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the search
 */
@Composable
fun Search(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("search", modifier, content)
}
