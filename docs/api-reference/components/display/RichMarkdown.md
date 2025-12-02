# RichMarkdown

The `RichMarkdown` component renders Markdown content as HTML. It supports standard Markdown syntax including headers, lists, links, bold, italic, and more.

## Usage

```kotlin
RichMarkdown(
    markdown = """
        # Hello World
        This is **bold** and *italic* text.
        
        - List item 1
        - List item 2
        
        [Link to Google](https://google.com)
    """.trimIndent(),
    modifier = Modifier().padding(16.px)
)
```

## Parameters

| Name | Type | Description |
| :--- | :--- | :--- |
| `markdown` | `String` | The Markdown content to render. |
| `modifier` | `Modifier` | The modifier to apply to the container. |

## Platform Support

| Platform | Support | Notes |
| :--- | :--- | :--- |
| JVM (SSR) | Full | Renders HTML on the server using Flexmark. |
| JS (Client) | Full | Renders HTML on the client using a Markdown library. |
| Wasm (Client) | Full | Renders HTML on the client. |
