package codes.yousef.summon.components.html

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Table-related HTML5 elements for creating accessible data tables.
 *
 * These components render actual HTML table elements (`<table>`, `<tr>`, `<td>`, etc.)
 * providing proper data table structure and accessibility.
 */

// ============================================
// Main Table Element
// ============================================

/**
 * Renders an HTML `<table>` element - a data table.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content containing Caption, Thead, Tbody, Tfoot, and/or Tr elements
 */
@Composable
fun Table(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("table", modifier, content)
}

// ============================================
// Table Sections
// ============================================

/**
 * Renders an HTML `<thead>` element - the header section of a table.
 *
 * Contains header row(s) with Th elements.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content containing Tr elements
 */
@Composable
fun Thead(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("thead", modifier, content)
}

/**
 * Renders an HTML `<tbody>` element - the body section of a table.
 *
 * Contains data row(s) with Td elements.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content containing Tr elements
 */
@Composable
fun Tbody(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("tbody", modifier, content)
}

/**
 * Renders an HTML `<tfoot>` element - the footer section of a table.
 *
 * Contains footer row(s), typically with summary data.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content containing Tr elements
 */
@Composable
fun Tfoot(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("tfoot", modifier, content)
}

// ============================================
// Table Rows and Cells
// ============================================

/**
 * Renders an HTML `<tr>` element - a table row.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content containing Th or Td elements
 */
@Composable
fun Tr(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("tr", modifier, content)
}

/**
 * Renders an HTML `<th>` element - a table header cell.
 *
 * @param scope Optional scope attribute: "row", "col", "rowgroup", "colgroup"
 * @param colspan Number of columns this cell spans
 * @param rowspan Number of rows this cell spans
 * @param headers Space-separated list of header cell IDs this cell is associated with
 * @param abbr Abbreviated version of the cell content for accessibility
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the header cell
 */
@Composable
fun Th(
    scope: String? = null,
    colspan: Int? = null,
    rowspan: Int? = null,
    headers: String? = null,
    abbr: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier
    if (scope != null) finalModifier = finalModifier.attribute("scope", scope)
    if (colspan != null) finalModifier = finalModifier.attribute("colspan", colspan.toString())
    if (rowspan != null) finalModifier = finalModifier.attribute("rowspan", rowspan.toString())
    if (headers != null) finalModifier = finalModifier.attribute("headers", headers)
    if (abbr != null) finalModifier = finalModifier.attribute("abbr", abbr)
    renderer.renderHtmlTag("th", finalModifier, content)
}

/**
 * Renders an HTML `<td>` element - a table data cell.
 *
 * @param colspan Number of columns this cell spans
 * @param rowspan Number of rows this cell spans
 * @param headers Space-separated list of header cell IDs this cell is associated with
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render inside the data cell
 */
@Composable
fun Td(
    colspan: Int? = null,
    rowspan: Int? = null,
    headers: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    var finalModifier = modifier
    if (colspan != null) finalModifier = finalModifier.attribute("colspan", colspan.toString())
    if (rowspan != null) finalModifier = finalModifier.attribute("rowspan", rowspan.toString())
    if (headers != null) finalModifier = finalModifier.attribute("headers", headers)
    renderer.renderHtmlTag("td", finalModifier, content)
}

// ============================================
// Table Caption and Column Groups
// ============================================

/**
 * Renders an HTML `<caption>` element - a table caption/title.
 *
 * Must be the first child of a Table element.
 *
 * @param modifier Styling and attributes to apply
 * @param content Composable content to render as the caption
 */
@Composable
fun Caption(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderHtmlTag("caption", modifier, content)
}

/**
 * Renders an HTML `<colgroup>` element - a group of columns.
 *
 * Used to apply styles to entire columns.
 *
 * @param span Number of consecutive columns the group spans
 * @param modifier Styling and attributes to apply
 * @param content Optional composable content containing Col elements
 */
@Composable
fun Colgroup(
    span: Int? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit = {}
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = if (span != null) {
        modifier.attribute("span", span.toString())
    } else {
        modifier
    }
    renderer.renderHtmlTag("colgroup", finalModifier, content)
}

/**
 * Renders an HTML `<col>` element - a table column.
 *
 * Used inside Colgroup to define column properties.
 *
 * @param span Number of consecutive columns this element spans
 * @param modifier Styling and attributes to apply
 */
@Composable
fun Col(
    span: Int? = null,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    val finalModifier = if (span != null) {
        modifier.attribute("span", span.toString())
    } else {
        modifier
    }
    renderer.renderHtmlTag("col", finalModifier) {}
}

// Helper extension function
private fun Modifier.attribute(name: String, value: String): Modifier {
    return Modifier(
        styles = this.styles,
        attributes = this.attributes + (name to value),
        eventHandlers = this.eventHandlers
    )
}
