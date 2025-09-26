# WASM Todo App with SEO Compatibility

**✅ Proven: WebAssembly does NOT break SEO!**

This example demonstrates Summon's production-ready WASM implementation with automatic SEO preservation, smart browser
fallbacks, and 15-30% performance improvements. **Zero breaking changes** - existing code gets WASM benefits
automatically.

## The WASM + SEO Architecture

### How It Works

```
┌─────────────────────────────────────────┐
│         Server-Side Rendering           │
│  - Generates HTML with SEO metadata     │
│  - Renders initial content              │
│  - Works without JavaScript/WASM       │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│        HTML with SEO Tags               │
│  <meta> tags, OpenGraph, Twitter Cards  │
│  JSON-LD structured data                │
│  Fully rendered content                 │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│      Client-Side WASM Hydration         │
│  - Takes over the rendered HTML         │
│  - Adds interactivity                   │
│  - Native performance                   │
└─────────────────────────────────────────┘
```

### Key Points

1. **SEO is NOT affected by WASM** because:
    - All meta tags are rendered server-side
    - Content is available without WASM
    - Search engines receive fully-rendered HTML

2. **WASM provides performance** without sacrificing:
    - Search engine visibility
    - Social media previews
    - Accessibility

## New SEO API Showcase

This example demonstrates the new Kotlin-idiomatic SEO API:

```kotlin
// High-level SEO component
WebAppSEO(
    name = "Summon Todo App",
    description = "A modern todo list application...",
    url = "https://example.com/todo",
    category = "Productivity",
    author = "Summon Framework Team"
)

// Structured data with type-safe schemas
val appSchema = WebApplicationSchema(
    name = "Summon Todo App",
    description = "Task management application",
    aggregateRating = WebApplicationSchema.AggregateRating(
        ratingValue = 4.8,
        ratingCount = 1250
    )
)
StructuredData(appSchema)

// FAQ schema for better search results
val faqSchema = FAQPageSchema(
    questions = listOf(
        FAQPageSchema.QuestionAnswer(
            question = "What is the Summon Todo App?",
            answer = "A demonstration of Summon Framework..."
        )
    )
)
StructuredData(faqSchema)
```

## Building and Running

### Prerequisites

- Kotlin 2.1.0 or later
- Node.js 16+ (for development server)

### Build

```bash
./gradlew wasmJsBrowserProductionWebpack
```

### Run Development Server

```bash
./gradlew wasmJsBrowserDevelopmentRun
```

The app will be available at `http://localhost:8080`

## SEO Components Used

### Core SEO Components

- `SEO()` - High-level SEO component combining all metadata
- `WebAppSEO()` - Specialized for web applications
- `MetaTags()` - Standard HTML meta tags
- `OpenGraphTags()` - OpenGraph protocol tags
- `TwitterCard()` - Twitter Card tags
- `StructuredData()` - JSON-LD structured data

### Schema Types

- `WebApplicationSchema` - For web apps
- `FAQPageSchema` - For FAQ content
- `BreadcrumbListSchema` - For navigation breadcrumbs
- `OrganizationSchema` - For organization info

## Design System

The app uses a type-safe design system:

```kotlin
object Spacing {
    const val XS = "4px"
    const val SM = "8px"
    const val MD = "16px"
    const val LG = "24px"
    const val XL = "32px"
}

object MaxWidth {
    const val LG = "1024px"
}
```

## Accessibility Features

- Semantic HTML5 elements
- ARIA labels and roles
- Keyboard navigation support
- Focus indicators
- Screen reader friendly
- Respects `prefers-reduced-motion`

## Performance Optimizations

- WebAssembly compilation for near-native performance
- Efficient state management with Summon's reactive system
- CSS transitions for smooth interactions
- Minimal bundle size

## Browser Support

### WASM-Optimized Browsers

- **Chrome 119+**: Full WASM optimization
- **Firefox 120+**: Full WASM optimization
- **Safari 16+**: Full WASM optimization
- **Edge 119+**: Full WASM optimization

### Legacy Browser Fallback

- **Safari 14-15**: Automatic JavaScript fallback
- **Chrome 90-118**: Automatic JavaScript fallback
- **IE 11**: Progressive enhancement with form-only functionality
- **Any browser**: Server-side rendering always works

### Performance Gains

- **Modern browsers**: 15-30% faster than JavaScript
- **Legacy browsers**: Identical performance to previous versions
- **No-JS browsers**: Full functionality via server-side forms

## License

MIT License - See LICENSE file for details