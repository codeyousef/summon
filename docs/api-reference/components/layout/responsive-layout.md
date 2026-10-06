# ResponsiveLayout

`ResponsiveLayout` renders content selected by the browser viewport or by an explicit server-side screen size.

```kotlin
ResponsiveLayout(
    content = mapOf(
        ScreenSize.SMALL to { MobileNavigation() },
        ScreenSize.LARGE to { DesktopNavigation() }
    ),
    defaultContent = { DesktopNavigation() },
    modifier = Modifier().attribute("data-summon-id", "primary-navigation")
)
```

## Breakpoints

Client detection uses the framework breakpoints:

| Screen size | Viewport width |
| --- | --- |
| `SMALL` | below 600 px |
| `MEDIUM` | 600–959 px |
| `LARGE` | 960–1279 px |
| `XLARGE` | 1280 px and above |

With `detectScreenSizeClient = true`, the renderer updates the responsive container's `data-screen-size` attribute and matching `*-screen` class after mounting and on resize. With client detection disabled, only `content[serverSideScreenSize]` or `defaultContent` is rendered.

## Browser lifecycle

Each JS or WASM rendered responsive node owns one resize subscription. Recomposition reuses that subscription. Conditional removal, node replacement, failed or canceled mounting, and `MountedComposition.dispose()` detach it. A callback retained by host code after teardown is inert and cannot update the retired element.

Use a stable `data-summon-id` when the responsive node's identity must survive WASM recomposition. Disposing a mounted root does not affect responsive nodes owned by neighboring roots.

## Content security policy

The current browser renderer installs the shared `summon-responsive-styles` style element when needed. This lifecycle guarantee does not qualify the component for a strict policy that forbids inline styles; use the CSP guidance and release qualification for the target deployment.
