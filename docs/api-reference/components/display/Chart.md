# Chart

The `Chart` component renders interactive charts using Chart.js.

## Usage

```kotlin
Chart(
    type = "bar",
    data = """{
        "labels": ["Red", "Blue", "Yellow", "Green", "Purple", "Orange"],
        "datasets": [{
            "label": "# of Votes",
            "data": [12, 19, 3, 5, 2, 3],
            "backgroundColor": [
                "rgba(255, 99, 132, 0.2)",
                "rgba(54, 162, 235, 0.2)",
                "rgba(255, 206, 86, 0.2)",
                "rgba(75, 192, 192, 0.2)",
                "rgba(153, 102, 255, 0.2)",
                "rgba(255, 159, 64, 0.2)"
            ],
            "borderColor": [
                "rgba(255, 99, 132, 1)",
                "rgba(54, 162, 235, 1)",
                "rgba(255, 206, 86, 1)",
                "rgba(75, 192, 192, 1)",
                "rgba(153, 102, 255, 1)",
                "rgba(255, 159, 64, 1)"
            ],
            "borderWidth": 1
        }]
    }""",
    options = """{
        "scales": {
            "y": {
                "beginAtZero": true
            }
        }
    }""",
    modifier = Modifier().width(400.px).height(300.px)
)
```

## Parameters

| Name | Type | Description |
| :--- | :--- | :--- |
| `type` | `String` | The type of chart to render (e.g., "bar", "line", "pie", "doughnut", "radar", "polarArea", "bubble", "scatter"). |
| `data` | `String` | A JSON string representing the data for the chart. |
| `options` | `String?` | An optional JSON string representing the configuration options for the chart. |
| `modifier` | `Modifier` | The modifier to apply to the chart container. |

## Platform Support

| Platform | Support | Notes |
| :--- | :--- | :--- |
| JVM (SSR) | Partial | Renders a canvas element with data attributes. Client-side hydration is required for interactivity. |
| JS (Client) | Full | Renders an interactive Chart.js chart. |
| Wasm (Client) | Full | Renders an interactive Chart.js chart. |
