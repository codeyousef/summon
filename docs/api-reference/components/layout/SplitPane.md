# SplitPane

The `SplitPane` component creates a resizable split view, allowing users to adjust the size of two adjacent panels.

## Usage

```kotlin
SplitPane(
    orientation = "horizontal",
    modifier = Modifier().height(400.px),
    first = {
        Box(modifier = Modifier().backgroundColor("#eee").fillMaxSize()) {
            Text("Left Pane")
        }
    },
    second = {
        Box(modifier = Modifier().backgroundColor("#ddd").fillMaxSize()) {
            Text("Right Pane")
        }
    }
)
```

## Parameters

| Name | Type | Description |
| :--- | :--- | :--- |
| `orientation` | `String` | The orientation of the split ("horizontal" or "vertical"). |
| `modifier` | `Modifier` | The modifier to apply to the container. |
| `first` | `@Composable () -> Unit` | The content of the first (left or top) pane. |
| `second` | `@Composable () -> Unit` | The content of the second (right or bottom) pane. |

## Platform Support

| Platform | Support | Notes |
| :--- | :--- | :--- |
| JVM (SSR) | Partial | Renders the initial layout. Resizing functionality requires client-side JavaScript. |
| JS (Client) | Full | Renders a fully interactive resizable split pane. |
| Wasm (Client) | Full | Renders a fully interactive resizable split pane. |
