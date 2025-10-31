# GlobalStyle Components

The GlobalStyle components provide a way to inject global CSS styles, keyframe animations, and CSS variables into your
Summon application.

## Components

### GlobalStyle

Injects global CSS styles into the document head.

```kotlin
@Composable
fun GlobalStyle(
    css: String,
    modifier: Modifier = Modifier()
)
```

**Parameters:**

- `css` - The CSS content to inject
- `modifier` - The modifier to apply to this component

**Example:**

```kotlin
GlobalStyle(
    """
    body {
        margin: 0;
        padding: 0;
        font-family: 'Arial', sans-serif;
    }
    
    .container {
        max-width: 1200px;
        margin: 0 auto;
    }
"""
)
```

### GlobalKeyframes

Injects CSS keyframe animations into the document head.

```kotlin
@Composable
fun GlobalKeyframes(
    name: String,
    keyframes: String,
    modifier: Modifier = Modifier()
)
```

**Parameters:**

- `name` - The name of the keyframe animation
- `keyframes` - The keyframe definition (without @keyframes wrapper)
- `modifier` - The modifier to apply to this component

**Example:**

```kotlin
GlobalKeyframes(
    name = "fadeIn",
    keyframes = """
        0% { opacity: 0; transform: translateY(20px); }
        100% { opacity: 1; transform: translateY(0); }
    """
)

// Use in component styling
Box(
    modifier = Modifier()
        .style("animation", "fadeIn 0.3s ease-out")
) {
    Text("Animated content")
}
```

### CssVariables

Injects CSS custom properties (variables) into the document root.

```kotlin
@Composable
fun CssVariables(
    variables: Map<String, String>,
    modifier: Modifier = Modifier()
)
```

**Parameters:**

- `variables` - A map of CSS variable names to their values
- `modifier` - The modifier to apply to this component

**Example:**

```kotlin
CssVariables(
    mapOf(
        "--primary-color" to "#007bff",
        "--secondary-color" to "#6c757d",
        "--border-radius" to "8px",
        "--spacing-xs" to "0.25rem",
        "--spacing-sm" to "0.5rem",
        "--spacing-md" to "1rem",
        "--spacing-lg" to "1.5rem",
        "--spacing-xl" to "3rem"
    )
)

// Use variables in components
Button(
    modifier = Modifier()
        .style("background-color", "var(--primary-color)")
        .style("border-radius", "var(--border-radius)")
        .style("padding", "var(--spacing-sm) var(--spacing-md)")
) {
    Text("Themed Button")
}
```

### MediaQuery

Injects CSS media queries into the document head.

```kotlin
@Composable
fun MediaQuery(
    query: String,
    css: String,
    modifier: Modifier = Modifier()
)
```

**Parameters:**

- `query` - The media query (e.g., "@media (max-width: 768px)")
- `css` - The CSS content to apply within the media query
- `modifier` - The modifier to apply to this component

**Example:**

```kotlin
MediaQuery(
    query = "@media (max-width: 768px)",
    css = """
        .container {
            padding: 1rem;
            font-size: 14px;
        }
        
        .hero-title {
            font-size: 2rem;
        }
    """
)

MediaQuery(
    query = "@media (prefers-color-scheme: dark)",
    css = """
        body {
            background-color: #1a1a1a;
            color: #ffffff;
        }
    """
)
```

## Best Practices

### 1. CSS Variable Organization

Group related variables together and use consistent naming:

```kotlin
// Design tokens
CssVariables(
    mapOf(
        // Colors
        "--color-primary" to "#007bff",
        "--color-primary-hover" to "#0056b3",
        "--color-secondary" to "#6c757d",
        "--color-success" to "#28a745",
        "--color-danger" to "#dc3545",
        "--color-warning" to "#ffc107",

        // Spacing
        "--space-1" to "0.25rem",
        "--space-2" to "0.5rem",
        "--space-3" to "0.75rem",
        "--space-4" to "1rem",
        "--space-5" to "1.25rem",
        "--space-6" to "1.5rem",

        // Typography
        "--font-size-xs" to "0.75rem",
        "--font-size-sm" to "0.875rem",
        "--font-size-base" to "1rem",
        "--font-size-lg" to "1.125rem",
        "--font-size-xl" to "1.25rem",
        "--font-size-2xl" to "1.5rem",

        // Border radius
        "--radius-sm" to "0.125rem",
        "--radius-base" to "0.25rem",
        "--radius-md" to "0.375rem",
        "--radius-lg" to "0.5rem",
        "--radius-xl" to "0.75rem"
    )
)
```

### 2. Animation Best Practices

Create reusable keyframe animations:

```kotlin
// Common animations
GlobalKeyframes("fadeIn", "from { opacity: 0; } to { opacity: 1; }")
GlobalKeyframes("slideUp", "from { transform: translateY(100%); } to { transform: translateY(0); }")
GlobalKeyframes("slideDown", "from { transform: translateY(-100%); } to { transform: translateY(0); }")
GlobalKeyframes("scaleIn", "from { transform: scale(0.95); opacity: 0; } to { transform: scale(1); opacity: 1; }")
GlobalKeyframes("spin", "from { transform: rotate(0deg); } to { transform: rotate(360deg); }")

// Micro-interactions
GlobalKeyframes(
    "buttonPress", """
    0% { transform: scale(1); }
    50% { transform: scale(0.98); }
    100% { transform: scale(1); }
"""
)

GlobalKeyframes(
    "pulse", """
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
"""
)
```

### 3. Responsive Design

Use media queries for responsive layouts:

```kotlin
@Composable
fun ResponsiveStyles() {
    // Mobile first approach
    GlobalStyle(
        """
        .container {
            padding: 1rem;
            max-width: 100%;
        }
    """
    )

    // Tablet
    MediaQuery(
        query = "@media (min-width: 768px)",
        css = """
            .container {
                padding: 1.5rem;
                max-width: 768px;
                margin: 0 auto;
            }
        """
    )

    // Desktop
    MediaQuery(
        query = "@media (min-width: 1024px)",
        css = """
            .container {
                padding: 2rem;
                max-width: 1024px;
            }
        """
    )

    // Large desktop
    MediaQuery(
        query = "@media (min-width: 1200px)",
        css = """
            .container {
                max-width: 1200px;
            }
        """
    )
}
```

### 4. Dark Mode Support

```kotlin
@Composable
fun DarkModeStyles() {
    // Light mode (default)
    CssVariables(
        mapOf(
            "--bg-primary" to "#ffffff",
            "--bg-secondary" to "#f8f9fa",
            "--text-primary" to "#212529",
            "--text-secondary" to "#6c757d",
            "--border-color" to "#dee2e6"
        )
    )

    // Dark mode
    MediaQuery(
        query = "@media (prefers-color-scheme: dark)",
        css = """
            :root {
                --bg-primary: #1a1a1a;
                --bg-secondary: #2d2d2d;
                --text-primary: #ffffff;
                --text-secondary: #cccccc;
                --border-color: #404040;
            }
        """
    )
}
```

## Platform Support

- **JVM**: Injects styles into HTML `<head>` section
- **JS**: Dynamically creates `<style>` elements in the DOM
- **Common**: Provides type-safe API for all platforms

## Integration with ThemeProvider

GlobalStyle components work seamlessly with the enhanced ThemeProvider:

```kotlin
@Composable
fun App() {
    ThemeProvider(
        theme = EnhancedThemeConfig(
            designTokens = mapOf(
                "--primary" to "#007bff",
                "--radius" to "8px"
            )
        )
    ) {
        // CSS variables from theme are automatically injected

        GlobalStyle(
            """
            .button-primary {
                background: var(--primary);
                border-radius: var(--radius);
                transition: all 0.2s ease;
            }
            
            .button-primary:hover {
                filter: brightness(1.1);
            }
        """
        )

        GlobalKeyframes(
            "wiggle", """
            0%, 7% { transform: rotateZ(0); }
            15% { transform: rotateZ(-15deg); }
            20% { transform: rotateZ(10deg); }
            25% { transform: rotateZ(-10deg); }
            30% { transform: rotateZ(6deg); }
            35% { transform: rotateZ(-4deg); }
            40%, 100% { transform: rotateZ(0); }
        """
        )

        // Your app content
    }
}
```