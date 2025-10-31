# ThemeProvider

Enhanced theme system that provides design tokens, CSS variables, and theme composition support.

## Components

### ThemeProvider

Provides theme configuration to child components and optionally injects CSS variables.

```kotlin
@Composable
fun ThemeProvider(
    theme: EnhancedThemeConfig = EnhancedThemeConfig(),
    content: @Composable () -> Unit
)
```

**Parameters:**

- `theme` - The theme configuration to provide
- `content` - The content to render with the theme

### useTheme

Hook to access the current theme configuration.

```kotlin
@Composable
fun useTheme(): EnhancedThemeConfig
```

**Returns:** The current theme configuration

## EnhancedThemeConfig

Enhanced theme configuration that extends the base theme with additional design tokens.

```kotlin
data class EnhancedThemeConfig(
    val primaryColor: String = "#007bff",
    val secondaryColor: String = "#6c757d",
    val backgroundColor: String = "#ffffff",
    val textColor: String = "#212529",
    val borderColor: String = "#dee2e6",
    val isDarkMode: Boolean? = null,
    val designTokens: Map<String, String>? = null,
    val breakpoints: Map<String, String>? = null,
    val typography: Map<String, String>? = null
)
```

**Properties:**

- `primaryColor` - Primary brand color
- `secondaryColor` - Secondary brand color
- `backgroundColor` - Default background color
- `textColor` - Default text color
- `borderColor` - Default border color
- `isDarkMode` - Whether this is a dark mode theme
- `designTokens` - CSS custom properties to inject
- `breakpoints` - Responsive breakpoint definitions
- `typography` - Typography scale definitions

## Basic Usage

### Simple Theme

```kotlin
@Composable
fun App() {
    val theme = EnhancedThemeConfig(
        primaryColor = "#007bff",
        secondaryColor = "#6c757d",
        backgroundColor = "#ffffff",
        textColor = "#333333"
    )

    ThemeProvider(theme = theme) {
        // Your app content
        HomePage()
    }
}

@Composable
fun HomePage() {
    val theme = useTheme()

    Box(
        modifier = Modifier()
            .style("background-color", theme.backgroundColor)
            .style("color", theme.textColor)
    ) {
        Text("Welcome to our app!")
    }
}
```

### Theme with Design Tokens

```kotlin
@Composable
fun App() {
    val theme = EnhancedThemeConfig(
        primaryColor = "var(--color-primary)",
        secondaryColor = "var(--color-secondary)",
        backgroundColor = "var(--color-background)",
        designTokens = mapOf(
            // Colors
            "--color-primary" to "#007bff",
            "--color-primary-hover" to "#0056b3",
            "--color-secondary" to "#6c757d",
            "--color-background" to "#ffffff",
            "--color-surface" to "#f8f9fa",
            "--color-text" to "#333333",
            "--color-text-muted" to "#6c757d",

            // Spacing
            "--space-1" to "0.25rem",
            "--space-2" to "0.5rem",
            "--space-3" to "0.75rem",
            "--space-4" to "1rem",
            "--space-5" to "1.25rem",
            "--space-6" to "1.5rem",
            "--space-8" to "2rem",
            "--space-10" to "2.5rem",
            "--space-12" to "3rem",

            // Typography
            "--font-size-xs" to "0.75rem",
            "--font-size-sm" to "0.875rem",
            "--font-size-base" to "1rem",
            "--font-size-lg" to "1.125rem",
            "--font-size-xl" to "1.25rem",
            "--font-size-2xl" to "1.5rem",
            "--font-size-3xl" to "1.875rem",
            "--font-size-4xl" to "2.25rem",

            // Border radius
            "--radius-sm" to "0.125rem",
            "--radius-base" to "0.25rem",
            "--radius-md" to "0.375rem",
            "--radius-lg" to "0.5rem",
            "--radius-xl" to "0.75rem",
            "--radius-2xl" to "1rem",
            "--radius-full" to "9999px",

            // Shadows
            "--shadow-sm" to "0 1px 2px 0 rgba(0, 0, 0, 0.05)",
            "--shadow-base" to "0 1px 3px 0 rgba(0, 0, 0, 0.1)",
            "--shadow-md" to "0 4px 6px -1px rgba(0, 0, 0, 0.1)",
            "--shadow-lg" to "0 10px 15px -3px rgba(0, 0, 0, 0.1)",
            "--shadow-xl" to "0 20px 25px -5px rgba(0, 0, 0, 0.1)"
        )
    )

    ThemeProvider(theme = theme) {
        MainApp()
    }
}
```

## Advanced Features

### Responsive Breakpoints

```kotlin
@Composable
fun ResponsiveTheme() {
    val theme = EnhancedThemeConfig(
        breakpoints = mapOf(
            "sm" to "640px",
            "md" to "768px",
            "lg" to "1024px",
            "xl" to "1280px",
            "2xl" to "1536px"
        ),
        designTokens = mapOf(
            "--breakpoint-sm" to "640px",
            "--breakpoint-md" to "768px",
            "--breakpoint-lg" to "1024px",
            "--breakpoint-xl" to "1280px",
            "--breakpoint-2xl" to "1536px"
        )
    )

    ThemeProvider(theme = theme) {
        GlobalStyle(
            """
            .container {
                width: 100%;
                padding: 0 var(--space-4);
            }
            
            @media (min-width: var(--breakpoint-sm)) {
                .container { max-width: 640px; margin: 0 auto; }
            }
            
            @media (min-width: var(--breakpoint-md)) {
                .container { max-width: 768px; }
            }
            
            @media (min-width: var(--breakpoint-lg)) {
                .container { max-width: 1024px; }
            }
        """
        )

        YourApp()
    }
}
```

### Typography System

```kotlin
@Composable
fun TypographyTheme() {
    val theme = EnhancedThemeConfig(
        typography = mapOf(
            "fontFamily" to "'Inter', -apple-system, BlinkMacSystemFont, sans-serif",
            "fontFamilyMono" to "'JetBrains Mono', 'Monaco', monospace",
            "fontWeight-light" to "300",
            "fontWeight-normal" to "400",
            "fontWeight-medium" to "500",
            "fontWeight-semibold" to "600",
            "fontWeight-bold" to "700",
            "lineHeight-tight" to "1.25",
            "lineHeight-normal" to "1.5",
            "lineHeight-relaxed" to "1.75"
        ),
        designTokens = mapOf(
            "--font-family" to "'Inter', -apple-system, BlinkMacSystemFont, sans-serif",
            "--font-family-mono" to "'JetBrains Mono', 'Monaco', monospace",
            "--font-weight-light" to "300",
            "--font-weight-normal" to "400",
            "--font-weight-medium" to "500",
            "--font-weight-semibold" to "600",
            "--font-weight-bold" to "700",
            "--line-height-tight" to "1.25",
            "--line-height-normal" to "1.5",
            "--line-height-relaxed" to "1.75"
        )
    )

    ThemeProvider(theme = theme) {
        GlobalStyle(
            """
            body {
                font-family: var(--font-family);
                line-height: var(--line-height-normal);
            }
            
            .text-xs { font-size: var(--font-size-xs); }
            .text-sm { font-size: var(--font-size-sm); }
            .text-base { font-size: var(--font-size-base); }
            .text-lg { font-size: var(--font-size-lg); }
            .text-xl { font-size: var(--font-size-xl); }
            .text-2xl { font-size: var(--font-size-2xl); }
            
            .font-light { font-weight: var(--font-weight-light); }
            .font-normal { font-weight: var(--font-weight-normal); }
            .font-medium { font-weight: var(--font-weight-medium); }
            .font-semibold { font-weight: var(--font-weight-semibold); }
            .font-bold { font-weight: var(--font-weight-bold); }
        """
        )

        YourApp()
    }
}
```

### Dark Mode Support

```kotlin
@Composable
fun DarkModeTheme() {
    val lightTheme = EnhancedThemeConfig(
        primaryColor = "#3b82f6",
        backgroundColor = "#ffffff",
        textColor = "#1f2937",
        isDarkMode = false,
        designTokens = mapOf(
            "--color-primary" to "#3b82f6",
            "--color-background" to "#ffffff",
            "--color-surface" to "#f9fafb",
            "--color-text" to "#1f2937",
            "--color-text-muted" to "#6b7280",
            "--color-border" to "#e5e7eb"
        )
    )

    val darkTheme = EnhancedThemeConfig(
        primaryColor = "#60a5fa",
        backgroundColor = "#111827",
        textColor = "#f9fafb",
        isDarkMode = true,
        designTokens = mapOf(
            "--color-primary" to "#60a5fa",
            "--color-background" to "#111827",
            "--color-surface" to "#1f2937",
            "--color-text" to "#f9fafb",
            "--color-text-muted" to "#d1d5db",
            "--color-border" to "#374151"
        )
    )

    // Detect system preference or use user setting
    val isDarkMode = remember { /* detect dark mode */ false }
    val currentTheme = if (isDarkMode) darkTheme else lightTheme

    ThemeProvider(theme = currentTheme) {
        GlobalStyle(
            """
            body {
                background-color: var(--color-background);
                color: var(--color-text);
                transition: background-color 0.2s ease, color 0.2s ease;
            }
        """
        )

        YourApp()
    }
}
```

## Theme Composition and Nesting

### Nested Themes

```kotlin
@Composable
fun NestedThemeExample() {
    val mainTheme = EnhancedThemeConfig(primaryColor = "#007bff")

    ThemeProvider(theme = mainTheme) {
        Column {
            Text("Main theme color: ${useTheme().primaryColor}")

            // Override theme for specific section
            val cardTheme = EnhancedThemeConfig(primaryColor = "#28a745")
            ThemeProvider(theme = cardTheme) {
                Card {
                    Text("Card theme color: ${useTheme().primaryColor}")
                }
            }

            // Main theme is restored
            Text("Back to main theme: ${useTheme().primaryColor}")
        }
    }
}
```

### Extending Themes

```kotlin
@Composable
fun ExtendedTheme() {
    val baseTheme = EnhancedThemeConfig(
        primaryColor = "#007bff",
        designTokens = mapOf(
            "--color-primary" to "#007bff",
            "--space-4" to "1rem"
        )
    )

    ThemeProvider(theme = baseTheme) {
        // Extend with additional tokens
        val extendedTheme = useTheme().copy(
            designTokens = useTheme().designTokens.orEmpty() + mapOf(
                "--color-accent" to "#ff6b6b",
                "--space-custom" to "1.25rem"
            )
        )

        ThemeProvider(theme = extendedTheme) {
            // Use extended theme
            YourComponent()
        }
    }
}
```

## Theme-Aware Modifiers

```kotlin
@Composable
fun ThemedButton(text: String) {
    Button(
        modifier = Modifier().themeColor("primary").themeBorder(),
        onClick = { /* handle click */ }
    ) {
        Text(text)
    }
}

@Composable
fun ThemedCard() {
    val theme = useTheme()

    Card(
        modifier = Modifier()
            .style("background-color", theme.backgroundColor)
            .style("border", "1px solid ${theme.borderColor}")
            .themeSpacing("md")
    ) {
        Text(
            "Card content",
            modifier = Modifier().style("color", theme.textColor)
        )
    }
}
```

## Integration with GlobalStyle

```kotlin
@Composable
fun ThemedApp() {
    val theme = EnhancedThemeConfig(
        designTokens = mapOf(
            "--primary" to "#007bff",
            "--secondary" to "#6c757d",
            "--radius" to "8px",
            "--space-4" to "1rem"
        )
    )

    ThemeProvider(theme = theme) {
        // Design tokens are automatically injected as CSS variables

        GlobalStyle(
            """
            .btn {
                background: var(--primary);
                border-radius: var(--radius);
                padding: var(--space-4);
                border: none;
                color: white;
                cursor: pointer;
                transition: all 0.2s ease;
            }
            
            .btn:hover {
                filter: brightness(1.1);
            }
            
            .btn-secondary {
                background: var(--secondary);
            }
        """
        )

        // Use themed styles
        Button(
            modifier = Modifier().style("class", "btn"),
            onClick = { /* action */ }
        ) {
            Text("Primary Button")
        }

        Button(
            modifier = Modifier().style("class", "btn btn-secondary"),
            onClick = { /* action */ }
        ) {
            Text("Secondary Button")
        }
    }
}
```

## Best Practices

### 1. Design System Organization

```kotlin
object DesignTokens {
    // Colors
    val colors = mapOf(
        "--color-primary-50" to "#eff6ff",
        "--color-primary-100" to "#dbeafe",
        "--color-primary-500" to "#3b82f6",
        "--color-primary-600" to "#2563eb",
        "--color-primary-900" to "#1e3a8a"
    )

    // Spacing scale
    val spacing = mapOf(
        "--space-0" to "0",
        "--space-px" to "1px",
        "--space-0_5" to "0.125rem",
        "--space-1" to "0.25rem",
        "--space-2" to "0.5rem",
        "--space-3" to "0.75rem",
        "--space-4" to "1rem"
    )

    // Typography scale
    val typography = mapOf(
        "--text-xs" to "0.75rem",
        "--text-sm" to "0.875rem",
        "--text-base" to "1rem",
        "--text-lg" to "1.125rem",
        "--text-xl" to "1.25rem"
    )

    val all = colors + spacing + typography
}

@Composable
fun DesignSystemTheme(content: @Composable () -> Unit) {
    val theme = EnhancedThemeConfig(
        designTokens = DesignTokens.all
    )

    ThemeProvider(theme = theme) {
        content()
    }
}
```

### 2. Consistent Color Usage

```kotlin
@Composable
fun ConsistentColors() {
    val theme = useTheme()

    // Good: Use theme colors consistently
    Box(
        modifier = Modifier()
            .style("background-color", theme.primaryColor)
            .style("color", theme.backgroundColor)
    ) {
        Text("Consistent theming")
    }

    // Avoid: Hardcoded colors
    // Box(modifier = Modifier().style("background-color", "#007bff"))
}
```

### 3. Responsive Design with Themes

```kotlin
@Composable
fun ResponsiveComponent() {
    ThemeProvider(
        theme = EnhancedThemeConfig(
            breakpoints = mapOf("md" to "768px"),
            designTokens = mapOf("--breakpoint-md" to "768px")
        )
    ) {
        GlobalStyle(
            """
            .responsive-text {
                font-size: 1rem;
            }
            
            @media (min-width: var(--breakpoint-md)) {
                .responsive-text {
                    font-size: 1.25rem;
                }
            }
        """
        )

        Text(
            "Responsive text",
            modifier = Modifier().style("class", "responsive-text")
        )
    }
}
```

## Platform Support

- **Common**: Type-safe theme configuration and composition
- **JVM**: CSS variable injection into HTML head
- **JS**: Dynamic CSS variable updates in the DOM

## Migration from Legacy Theme

```kotlin
// Legacy Theme usage
val legacyTheme = Theme.ThemeConfig(
    primaryColor = "#007bff",
    secondaryColor = "#6c757d"
)
Theme.setTheme(legacyTheme)

// New Enhanced Theme usage  
val enhancedTheme = EnhancedThemeConfig(
    primaryColor = "#007bff",
    secondaryColor = "#6c757d",
    designTokens = mapOf(
        "--color-primary" to "#007bff",
        "--color-secondary" to "#6c757d"
    )
)

ThemeProvider(theme = enhancedTheme) {
    // Components automatically have access to both
    // legacy Theme.getTheme() and useTheme()
}
```