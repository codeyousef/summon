/**
 * # Summon HTML DSL
 *
 * This package provides a comprehensive set of HTML5 semantic elements as composable functions,
 * enabling type-safe, declarative construction of semantically meaningful HTML content.
 *
 * ## Overview
 *
 * Unlike generic container components like `Div` and `Span`, these components render actual
 * HTML5 semantic elements, providing:
 *
 * - **Better Accessibility**: Screen readers understand the document structure
 * - **SEO Benefits**: Search engines can better index your content
 * - **Browser Defaults**: Native styling and behavior where applicable
 * - **Document Outline**: Proper heading structure for navigation
 *
 * ## Package Organization
 *
 * The HTML DSL is organized into logical categories:
 *
 * ### Structural Elements (`HtmlElements.kt`)
 * Page structure components: [Header], [Nav], [Main], [Footer], [Section], [Article], [Aside]
 *
 * ### Text Elements (`TextElements.kt`)
 * Headings and text formatting: [H1]-[H6], [P], [Blockquote], [Pre], [Code], [Strong], [Em], etc.
 *
 * ### List Elements (`ListElements.kt`)
 * All list types: [Ul], [Ol], [Li], [Dl], [Dt], [Dd], [Menu]
 *
 * ### Table Elements (`TableElements.kt`)
 * Full table support: [Table], [Thead], [Tbody], [Tfoot], [Tr], [Th], [Td], [Caption], [Colgroup], [Col]
 *
 * ### Inline Elements (`InlineElements.kt`)
 * Links and inline content: [A], [Span], [Time], [Abbr], [Cite], [Q], [Kbd], etc.
 *
 * ### Media Elements (`MediaElements.kt`)
 * Figures and embedded content: [Figure], [Figcaption], [Iframe], [Embed], [Audio], etc.
 *
 * ### Details Elements (`DetailsElements.kt`)
 * Interactive disclosure: [Details], [Summary], [Dialog]
 *
 * ## Usage Examples
 *
 * ### Basic Page Structure
 *
 * ```kotlin
 * @Composable
 * fun PageLayout() {
 *     Header {
 *         Nav {
 *             Ul {
 *                 Li { A(href = "/") { Text("Home") } }
 *                 Li { A(href = "/about") { Text("About") } }
 *             }
 *         }
 *     }
 *
 *     Main {
 *         Article {
 *             H1 { Text("Article Title") }
 *             P { Text("Article content...") }
 *         }
 *     }
 *
 *     Footer {
 *         P { Text("Copyright 2024") }
 *     }
 * }
 * ```
 *
 * ### Collapsible Content
 *
 * ```kotlin
 * @Composable
 * fun FAQ() {
 *     Details(open = false) {
 *         Summary { Text("What is Summon?") }
 *         P { Text("Summon is a Kotlin Multiplatform UI framework...") }
 *     }
 * }
 * ```
 *
 * ### Data Tables
 *
 * ```kotlin
 * @Composable
 * fun DataTable(items: List<Item>) {
 *     Table {
 *         Caption { Text("Product List") }
 *         Thead {
 *             Tr {
 *                 Th(scope = "col") { Text("Name") }
 *                 Th(scope = "col") { Text("Price") }
 *             }
 *         }
 *         Tbody {
 *             items.forEach { item ->
 *                 Tr {
 *                     Td { Text(item.name) }
 *                     Td { Text(item.price.toString()) }
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @since 0.7.0
 */
package codes.yousef.summon.components.html
