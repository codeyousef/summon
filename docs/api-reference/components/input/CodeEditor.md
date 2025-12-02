# CodeEditor

The `CodeEditor` component provides a syntax-highlighted code editing experience. It is useful for scenarios where users need to input or edit code snippets.

## Usage

```kotlin
var code by remember { mutableStateOf("fun main() { println(\"Hello\") }") }

CodeEditor(
    value = code,
    onValueChange = { code = it },
    language = "kotlin",
    readOnly = false,
    modifier = Modifier().height(300.px).border(1.px, BorderStyle.Solid, "#ccc")
)
```

## Parameters

| Name | Type | Description |
| :--- | :--- | :--- |
| `value` | `String` | The current code content. |
| `onValueChange` | `(String) -> Unit` | Callback invoked when the code content changes. |
| `language` | `String` | The programming language for syntax highlighting (e.g., "kotlin", "javascript", "html"). |
| `readOnly` | `Boolean` | Whether the editor is read-only. Defaults to `false`. |
| `modifier` | `Modifier` | The modifier to apply to the editor container. |

## Platform Support

| Platform | Support | Notes |
| :--- | :--- | :--- |
| JVM (SSR) | Partial | Renders a static code block with syntax highlighting classes. Client-side hydration enables editing. |
| JS (Client) | Full | Renders an interactive code editor (e.g., using PrismJS or similar). |
| Wasm (Client) | Full | Renders an interactive code editor. |
