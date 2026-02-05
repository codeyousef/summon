# HTML DSL API Reference

Complete HTML5 semantic elements DSL for building accessible, SEO-friendly web pages.

**Package**: `codes.yousef.summon.components.html`
**Since**: 0.7.0

---

## Overview

The HTML DSL provides composable functions that render actual HTML5 semantic elements rather than generic `<div>`
elements with ARIA roles. This provides better:

- **Screen reader support** - Native semantic meaning
- **Search engine optimization** - Proper document structure
- **Document outline structure** - Hierarchical headings and sections
- **Browser default styling** - Consistent baseline appearance

---

## Structural Elements

### Header

Renders an HTML `<header>` element for introductory content.

```kotlin
@Composable
fun Header(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

**Example:**

```kotlin
Header(modifier = Modifier().padding(16.px)) {
    H1 { Text("Welcome to My Site") }
    Nav {
        A(href = "/home") { Text("Home") }
        A(href = "/about") { Text("About") }
    }
}
```

### Nav

Renders an HTML `<nav>` element for navigation links.

```kotlin
@Composable
fun Nav(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Main

Renders an HTML `<main>` element for the dominant content. There should be only one visible `<main>` per page.

```kotlin
@Composable
fun Main(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Footer

Renders an HTML `<footer>` element for footer content (copyright, author info, related links).

```kotlin
@Composable
fun Footer(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Section

Renders an HTML `<section>` element for thematic grouping of content.

```kotlin
@Composable
fun Section(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Article

Renders an HTML `<article>` element for self-contained compositions (blog posts, news articles, comments).

```kotlin
@Composable
fun Article(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Aside

Renders an HTML `<aside>` element for tangentially related content (sidebars, pull quotes).

```kotlin
@Composable
fun Aside(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Address

Renders an HTML `<address>` element for contact information.

```kotlin
@Composable
fun Address(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Hgroup

Renders an HTML `<hgroup>` element for heading groups with subheadings.

```kotlin
@Composable
fun Hgroup(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Search

Renders an HTML `<search>` element for search form containers.

```kotlin
@Composable
fun Search(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

---

## Text Elements

### Headings (H1-H6)

```kotlin
@Composable fun H1(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun H2(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun H3(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun H4(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun H5(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun H6(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
```

**Example:**

```kotlin
Article {
    H1 { Text("Main Title") }
    H2 { Text("Subtitle") }
    P { Text("Content paragraph...") }
}
```

### Paragraph (P)

```kotlin
@Composable
fun P(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Blockquote

Extended quotation with optional citation URL.

```kotlin
@Composable
fun Blockquote(
    cite: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

**Example:**

```kotlin
Blockquote(cite = "https://example.com/source") {
    P { Text("To be or not to be, that is the question.") }
}
```

### Pre (Preformatted Text)

Preserves whitespace and line breaks.

```kotlin
@Composable
fun Pre(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Code

Inline computer code. For code blocks, wrap inside `Pre`.

```kotlin
@Composable
fun Code(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

**Example:**

```kotlin
Pre {
    Code {
        Text("fun main() {\n    println(\"Hello, World!\")\n}")
    }
}
```

### Text Formatting Elements

| Function | Element    | Purpose                            |
|----------|------------|------------------------------------|
| `Strong` | `<strong>` | Strong importance (bold)           |
| `Em`     | `<em>`     | Emphatic stress (italic)           |
| `Small`  | `<small>`  | Side comments, fine print          |
| `Mark`   | `<mark>`   | Highlighted text                   |
| `Del`    | `<del>`    | Deleted text                       |
| `Ins`    | `<ins>`    | Inserted text                      |
| `Sub`    | `<sub>`    | Subscript                          |
| `Sup`    | `<sup>`    | Superscript                        |
| `S`      | `<s>`      | Strikethrough (no longer relevant) |
| `U`      | `<u>`      | Unarticulated annotation           |
| `B`      | `<b>`      | Bring attention (bold style)       |
| `I`      | `<i>`      | Idiomatic text (italic style)      |

**Del and Ins with metadata:**

```kotlin
@Composable
fun Del(
    cite: String? = null,
    datetime: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)

@Composable
fun Ins(
    cite: String? = null,
    datetime: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

---

## List Elements

### Unordered List (Ul)

```kotlin
@Composable
fun Ul(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Ordered List (Ol)

```kotlin
@Composable
fun Ol(
    start: Int? = null,
    reversed: Boolean = false,
    type: String? = null,  // "1", "a", "A", "i", "I"
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### List Item (Li)

```kotlin
@Composable
fun Li(
    value: Int? = null,  // For ordered lists
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

**Example:**

```kotlin
Ul(modifier = Modifier().listStyleType("disc")) {
    Li { Text("First item") }
    Li { Text("Second item") }
    Li { Text("Third item") }
}

Ol(start = 5, type = "a") {
    Li { Text("Item e") }
    Li { Text("Item f") }
}
```

### Description List (Dl, Dt, Dd)

```kotlin
@Composable fun Dl(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun Dt(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun Dd(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
```

**Example:**

```kotlin
Dl {
    Dt { Text("HTML") }
    Dd { Text("HyperText Markup Language") }

    Dt { Text("CSS") }
    Dd { Text("Cascading Style Sheets") }
}
```

### Menu

Semantic list for interactive commands.

```kotlin
@Composable
fun Menu(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

---

## Table Elements

### Table Structure

```kotlin
@Composable fun Table(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun Thead(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun Tbody(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun Tfoot(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun Tr(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
```

### Table Cells

```kotlin
@Composable
fun Th(
    scope: String? = null,  // "row", "col", "rowgroup", "colgroup"
    colspan: Int? = null,
    rowspan: Int? = null,
    headers: String? = null,
    abbr: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)

@Composable
fun Td(
    colspan: Int? = null,
    rowspan: Int? = null,
    headers: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Caption and Column Groups

```kotlin
@Composable fun Caption(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun Colgroup(span: Int? = null, modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit = {})
@Composable fun Col(span: Int? = null, modifier: Modifier = Modifier())
```

**Complete Table Example:**

```kotlin
Table(modifier = Modifier().width("100%").borderCollapse("collapse")) {
    Caption { Text("Monthly Sales") }
    Thead {
        Tr {
            Th(scope = "col") { Text("Month") }
            Th(scope = "col") { Text("Sales") }
        }
    }
    Tbody {
        Tr {
            Td { Text("January") }
            Td { Text("$10,000") }
        }
        Tr {
            Td { Text("February") }
            Td { Text("$12,000") }
        }
    }
    Tfoot {
        Tr {
            Th(scope = "row") { Text("Total") }
            Td { Text("$22,000") }
        }
    }
}
```

---

## Interactive Elements

### Details/Summary

Native collapsible content without JavaScript.

```kotlin
@Composable
fun Details(
    open: Boolean = false,
    onToggle: ((Boolean) -> Unit)? = null,
    name: String? = null,  // For accordion behavior
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)

@Composable
fun Summary(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

**Example:**

```kotlin
// Uncontrolled (browser manages state)
Details {
    Summary { Text("Click to expand") }
    P { Text("Hidden content revealed when expanded") }
}

// Controlled (you manage state)
var isExpanded by remember { mutableStateOf(false) }
Details(open = isExpanded, onToggle = { isExpanded = it }) {
    Summary { Text("FAQ Item") }
    P { Text("Answer to the question...") }
}

// Accordion (only one open at a time with same name)
Details(name = "faq") {
    Summary { Text("Question 1") }
    P { Text("Answer 1") }
}
Details(name = "faq") {
    Summary { Text("Question 2") }
    P { Text("Answer 2") }
}
```

### Dialog

Native dialog/modal element.

```kotlin
@Composable
fun Dialog(
    open: Boolean = false,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

---

## Inline Elements

### Links (A)

```kotlin
@Composable
fun A(
    href: String,
    target: String? = null,  // "_blank", "_self", "_parent", "_top"
    rel: String? = null,
    download: String? = null,
    hreflang: String? = null,
    type: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Span

Generic inline container.

```kotlin
@Composable
fun Span(
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

### Semantic Inline Elements

| Function | Element  | Purpose                                    |
|----------|----------|--------------------------------------------|
| `Time`   | `<time>` | Date/time with machine-readable `datetime` |
| `Abbr`   | `<abbr>` | Abbreviation with `title` expansion        |
| `Cite`   | `<cite>` | Citation/reference (titles of works)       |
| `Q`      | `<q>`    | Short inline quotation                     |
| `Kbd`    | `<kbd>`  | Keyboard input                             |
| `Samp`   | `<samp>` | Sample output                              |
| `Var`    | `<var>`  | Variable (math/programming)                |
| `Dfn`    | `<dfn>`  | Definition term                            |
| `Data`   | `<data>` | Machine-readable value                     |
| `Bdi`    | `<bdi>`  | Bidirectional isolation                    |
| `Bdo`    | `<bdo>`  | Bidirectional override                     |

**Examples:**

```kotlin
Time(datetime = "2026-02-05") { Text("February 5, 2026") }

Abbr(title = "HyperText Markup Language") { Text("HTML") }

Q(cite = "https://example.com") { Text("Quoted text") }

Kbd { Text("Ctrl") } + Kbd { Text("C") }
```

### Ruby Annotations (Ruby, Rt, Rp)

For East Asian character pronunciation guides.

```kotlin
Ruby {
    Text("漢")
    Rp { Text("(") }
    Rt { Text("かん") }
    Rp { Text(")") }
}
```

### Line Breaks

```kotlin
@Composable fun Br(modifier: Modifier = Modifier())
@Composable fun Wbr(modifier: Modifier = Modifier())  // Word break opportunity
```

---

## Media Elements

### Figure/Figcaption

```kotlin
@Composable fun Figure(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
@Composable fun Figcaption(modifier: Modifier = Modifier(), content: @Composable FlowContent.() -> Unit)
```

**Example:**

```kotlin
Figure {
    Image(src = "/chart.png", alt = "Sales chart")
    Figcaption { Text("Figure 1: Quarterly sales comparison") }
}
```

### Embedded Content

```kotlin
@Composable
fun Iframe(
    src: String,
    title: String,  // Required for accessibility
    width: String? = null,
    height: String? = null,
    sandbox: String? = null,
    allow: String? = null,
    loading: String? = null,
    referrerpolicy: String? = null,
    modifier: Modifier = Modifier()
)

@Composable
fun Embed(
    src: String,
    type: String,
    width: String? = null,
    height: String? = null,
    modifier: Modifier = Modifier()
)

@Composable
fun ObjectTag(
    data: String? = null,
    type: String? = null,
    width: String? = null,
    height: String? = null,
    name: String? = null,
    form: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit = {}
)
```

### Audio/Video Sources

```kotlin
@Composable
fun Source(
    src: String? = null,
    type: String? = null,
    srcset: String? = null,
    sizes: String? = null,
    media: String? = null,
    modifier: Modifier = Modifier()
)

@Composable
fun Track(
    src: String,
    kind: String = "subtitles",
    srclang: String? = null,
    label: String? = null,
    default: Boolean = false,
    modifier: Modifier = Modifier()
)

@Composable
fun Audio(
    src: String? = null,
    controls: Boolean = true,
    autoplay: Boolean = false,
    loop: Boolean = false,
    muted: Boolean = false,
    preload: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit = {}
)
```

### Meter

Scalar measurement within a known range.

```kotlin
@Composable
fun Meter(
    value: Double,
    min: Double = 0.0,
    max: Double = 1.0,
    low: Double? = null,
    high: Double? = null,
    optimum: Double? = null,
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit = {}
)
```

**Example:**

```kotlin
Meter(value = 0.7, min = 0.0, max = 1.0, low = 0.3, high = 0.8, optimum = 1.0) {
    Text("70%")
}
```

---

## Migration from SemanticHTML

The `components/html/` package replaces the deprecated `seo/routes/SemanticHTML.kt` functions:

| Old (SemanticHTML.kt)    | New (components/html/) |
|--------------------------|------------------------|
| `Header { }`             | `Header { }`           |
| `Main { }`               | `Main { }`             |
| `Nav { }`                | `Nav { }`              |
| `Article { }`            | `Article { }`          |
| `Section { }`            | `Section { }`          |
| `Aside { }`              | `Aside { }`            |
| `Footer { }`             | `Footer { }`           |
| `Heading(level = 1) { }` | `H1 { }`               |
| `Figure { }`             | `Figure { }`           |
| `FigCaption { }`         | `Figcaption { }`       |

---

## See Also

- [Modifier API](modifier.md) - Styling components
- [Components API](components.md) - All UI components
- [Accessibility API](accessibility.md) - Accessibility features
- [SEO API](seo.md) - SEO features
