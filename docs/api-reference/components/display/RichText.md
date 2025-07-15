# RichText Components

The RichText components provide safe HTML rendering with built-in XSS protection and Markdown support.

## Components

### RichText

Renders rich HTML content with optional sanitization for XSS protection.

```kotlin
@Composable
fun RichText(
    htmlContent: String,
    modifier: Modifier = Modifier(),
    sanitize: Boolean = true
)
```

**Parameters:**
- `htmlContent` - The HTML content to render
- `modifier` - The modifier to apply to this component
- `sanitize` - Whether to sanitize the HTML content (default: true)

**Example:**
```kotlin
RichText(
    htmlContent = """
        <h1>Welcome to Our App</h1>
        <p>This is <strong>bold</strong> and <em>italic</em> text.</p>
        <ul>
            <li>Feature 1</li>
            <li>Feature 2</li>
            <li>Feature 3</li>
        </ul>
    """,
    sanitize = true
)
```

### Html

Renders trusted HTML content with minimal sanitization. Use this for content you control and trust.

```kotlin
@Composable
fun Html(
    htmlContent: String,
    modifier: Modifier = Modifier()
)
```

**Parameters:**
- `htmlContent` - The trusted HTML content to render
- `modifier` - The modifier to apply to this component

**Example:**
```kotlin
Html(
    htmlContent = """
        <div class="custom-widget">
            <script src="https://trusted-widget.com/widget.js"></script>
            <div id="widget-container"></div>
        </div>
    """
)
```

### Markdown

Renders Markdown content by converting it to safe HTML.

```kotlin
@Composable
fun Markdown(
    markdownContent: String,
    modifier: Modifier = Modifier()
)
```

**Parameters:**
- `markdownContent` - The Markdown content to render
- `modifier` - The modifier to apply to this component

**Example:**
```kotlin
Markdown(
    markdownContent = """
        # Getting Started
        
        Welcome to our **amazing** application!
        
        ## Features
        
        - Easy to use
        - *Powerful* functionality
        - Great performance
        
        [Learn more](https://example.com)
        
        ```kotlin
        fun hello() {
            println("Hello, World!")
        }
        ```
    """
)
```

## HTML Sanitization

The RichText component includes built-in XSS protection when `sanitize = true` (default).

### Allowed Elements

The following HTML elements are preserved during sanitization:

- **Text formatting**: `<p>`, `<span>`, `<div>`, `<br>`
- **Headers**: `<h1>`, `<h2>`, `<h3>`, `<h4>`, `<h5>`, `<h6>`
- **Emphasis**: `<strong>`, `<b>`, `<em>`, `<i>`, `<u>`
- **Lists**: `<ul>`, `<ol>`, `<li>`
- **Links**: `<a>` (with safe href attributes)
- **Code**: `<code>`, `<pre>`
- **Quotes**: `<blockquote>`
- **Tables**: `<table>`, `<thead>`, `<tbody>`, `<tr>`, `<th>`, `<td>`

### Removed Elements

The following dangerous elements are removed:

- **Scripts**: `<script>`, `<noscript>`
- **Forms**: `<form>`, `<input>`, `<button>`, `<textarea>`, `<select>`
- **Objects**: `<object>`, `<embed>`, `<applet>`, `<iframe>`
- **Meta**: `<meta>`, `<link>`, `<style>` (use GlobalStyle instead)

### Removed Attributes

The following dangerous attributes are removed:

- **Event handlers**: `onclick`, `onload`, `onerror`, `onmouseover`, etc.
- **JavaScript URLs**: `href="javascript:..."`, `src="javascript:..."`
- **Data URLs**: Potentially dangerous data: URLs

## Markdown Support

The Markdown component supports a subset of CommonMark:

### Headers
```markdown
# H1 Header
## H2 Header
### H3 Header
#### H4 Header
##### H5 Header
###### H6 Header
```

### Text Formatting
```markdown
**Bold text**
*Italic text*
`Inline code`
```

### Lists
```markdown
- Unordered list item 1
- Unordered list item 2

1. Ordered list item 1
2. Ordered list item 2
```

### Links
```markdown
[Link text](https://example.com)
[Link with title](https://example.com "Link title")
```

### Code Blocks
```markdown
```kotlin
fun hello() {
    println("Hello, World!")
}
```
```

### Blockquotes
```markdown
> This is a blockquote
> It can span multiple lines
```

## Security Best Practices

### 1. Always Sanitize User Content

```kotlin
@Composable
fun UserComment(comment: UserCommentData) {
    RichText(
        htmlContent = comment.htmlContent,
        sanitize = true // Always true for user content
    )
}
```

### 2. Use Html Only for Trusted Content

```kotlin
@Composable
fun AdminDashboard() {
    // Only use Html for content you control
    Html(
        htmlContent = """
            <div class="admin-widget">
                <iframe src="/admin/analytics" width="100%" height="400"></iframe>
            </div>
        """
    )
}
```

### 3. Validate External Links

```kotlin
@Composable
fun SafeRichText(content: String) {
    val sanitizedContent = content
        .replace(Regex("href=[\"'](?!https?://)[^\"']*[\"']"), "href=\"#\"")
    
    RichText(
        htmlContent = sanitizedContent,
        sanitize = true
    )
}
```

## Styling Rich Content

### Using CSS Classes

```kotlin
@Composable
fun StyledArticle() {
    GlobalStyle("""
        .article-content {
            font-family: Georgia, serif;
            line-height: 1.6;
            color: #333;
        }
        
        .article-content h1 {
            color: #2c3e50;
            border-bottom: 2px solid #3498db;
            padding-bottom: 0.5rem;
        }
        
        .article-content h2 {
            color: #34495e;
            margin-top: 2rem;
        }
        
        .article-content p {
            margin-bottom: 1rem;
        }
        
        .article-content code {
            background: #f8f9fa;
            padding: 0.125rem 0.25rem;
            border-radius: 0.25rem;
            font-family: 'Monaco', monospace;
        }
        
        .article-content blockquote {
            border-left: 4px solid #3498db;
            padding-left: 1rem;
            margin: 1rem 0;
            font-style: italic;
            color: #7f8c8d;
        }
    """)
    
    RichText(
        htmlContent = """
            <div class="article-content">
                <h1>Article Title</h1>
                <p>This is the article content with <code>inline code</code>.</p>
                <blockquote>
                    This is a quote from someone important.
                </blockquote>
                <h2>Section Title</h2>
                <p>More content here...</p>
            </div>
        """,
        sanitize = true
    )
}
```

### Using Modifiers

```kotlin
@Composable
fun ModifiedRichText() {
    RichText(
        htmlContent = "<p>Styled content</p>",
        modifier = Modifier()
            .style("max-width", "800px")
            .style("margin", "0 auto")
            .style("padding", "2rem")
            .style("background", "#f8f9fa")
            .style("border-radius", "8px")
            .style("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
    )
}
```

## Integration Examples

### Blog Post Component

```kotlin
@Composable
fun BlogPost(post: BlogPostData) {
    Column {
        // Title
        Text(
            text = post.title,
            modifier = Modifier().style("font-size", "2rem").style("font-weight", "bold")
        )
        
        // Metadata
        Text(
            text = "By ${post.author} on ${post.date}",
            modifier = Modifier().style("color", "#666").style("margin-bottom", "1rem")
        )
        
        // Content
        RichText(
            htmlContent = post.htmlContent,
            sanitize = true,
            modifier = Modifier().style("line-height", "1.6")
        )
        
        // Tags
        Row {
            post.tags.forEach { tag ->
                Text(
                    text = "#$tag",
                    modifier = Modifier()
                        .style("background", "#e9ecef")
                        .style("padding", "0.25rem 0.5rem")
                        .style("border-radius", "1rem")
                        .style("font-size", "0.875rem")
                        .style("margin-right", "0.5rem")
                )
            }
        }
    }
}
```

### Documentation Viewer

```kotlin
@Composable
fun DocumentationViewer(markdownContent: String) {
    GlobalStyle("""
        .docs {
            max-width: 800px;
            margin: 0 auto;
            padding: 2rem;
        }
        
        .docs h1, .docs h2, .docs h3 {
            margin-top: 2rem;
            margin-bottom: 1rem;
        }
        
        .docs pre {
            background: #f6f8fa;
            padding: 1rem;
            border-radius: 6px;
            overflow-x: auto;
        }
        
        .docs code {
            background: #f6f8fa;
            padding: 0.125rem 0.25rem;
            border-radius: 3px;
            font-size: 0.875rem;
        }
    """)
    
    Markdown(
        markdownContent = markdownContent,
        modifier = Modifier().style("class", "docs")
    )
}
```

## Platform Support

- **JVM**: Renders HTML using kotlinx.html with server-side sanitization
- **JS**: Uses DOM innerHTML with client-side sanitization
- **Common**: Provides type-safe API and basic Markdown parsing

## Performance Considerations

1. **Sanitization**: HTML sanitization adds processing overhead. Disable for trusted content.
2. **Large Content**: Consider lazy loading or pagination for large HTML content.
3. **Caching**: Cache sanitized content when possible to avoid re-processing.

```kotlin
@Composable
fun OptimizedRichText(content: String) {
    val sanitizedContent = remember(content) {
        // Cache sanitized content
        sanitizeHtml(content)
    }
    
    Html(htmlContent = sanitizedContent)
}
```