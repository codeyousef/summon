# Accessibility and SEO Features

Summon provides comprehensive support for building accessible web applications and optimizing them for search engines. This guide covers the built-in accessibility and SEO features.

## Accessibility Features

### ARIA Support

Summon provides extensive ARIA (Accessible Rich Internet Applications) support through modifiers:

```kotlin
Text(
    text = "Accessible Text",
    modifier = Modifier()
        .role("heading")
        .ariaLabel("Section heading")
        .ariaDescribedBy("text-description")
)
```

Available ARIA modifiers include:
- `role()` - Sets the ARIA role
- `ariaLabel()` - Sets the accessible name
- `ariaLabelledBy()` - References an element that labels this element
- `ariaDescribedBy()` - References an element that describes this element
- `ariaHidden()` - Hides content from screen readers
- `ariaExpanded()` - Indicates if a control is expanded
- `ariaPressed()` - Indicates if a button is pressed
- `ariaChecked()` - Indicates if a checkbox/radio is checked
- `ariaSelected()` - Indicates if an option is selected
- `ariaDisabled()` - Indicates if an element is disabled
- `ariaInvalid()` - Indicates if an input is invalid
- `ariaRequired()` - Indicates if an input is required
- `ariaCurrent()` - Indicates the current item in a set
- `ariaLive()` - Sets the live region behavior
- `ariaControls()` - Indicates which element is controlled by the current element
- `ariaHasPopup()` - Indicates whether the element has a popup
- `ariaBusy()` - Indicates that an element is being modified

### Focus Management

Summon includes utilities for managing keyboard focus:

```kotlin
// Make an element focusable but not in tab order
modifier.focusable()

// Make an element tabbable
modifier.tabbable()

// Disable focus
modifier.disabled()

// Auto-focus an element
modifier.autoFocus()
```

### AccessibleElement Component

Summon provides an `AccessibleElement` wrapper component for easily adding accessibility attributes:

```kotlin
AccessibleElement(
    role = AccessibilityUtils.NodeRole.ALERT,
    label = "Important alert message",
    relations = mapOf("describedby" to "alert-description"),
    modifier = Modifier.color("#c62828")
) {
    Text("Warning: This action cannot be undone")
}
```

### Accessibility Utilities

The `AccessibilityUtils` object provides helpful functions to create and inspect accessibility attributes:

```kotlin
// Create a modifier with a specific role
val buttonModifier = AccessibilityUtils.createRoleModifier(AccessibilityUtils.NodeRole.BUTTON)

// Create a modifier with an accessible label
val labelModifier = AccessibilityUtils.createLabelModifier("Close dialog")

// Create a modifier with relationship to another element
val relationModifier = AccessibilityUtils.createRelationshipModifier("describedby", "description-id")

// Inspect accessibility attributes on a modifier
val modifier = Modifier()
    .role("button")
    .ariaLabel("Close")
    
val accessibilityAttrs = modifier.inspectAccessibility()
// Result: {"role": "button", "aria-label": "Close"}
```

### Semantic HTML

Summon provides semantic HTML components for better accessibility:

```kotlin
Header {
    Heading(level = 1) {
        Text("Site Title")
    }
}

Main {
    Section {
        Heading(level = 2) {
            Text("About Us")
        }
        Text("Company information goes here")
    }
    
    Article {
        Heading(level = 2) {
            Text("Latest News")
        }
        Text("News content goes here")
    }
}

Footer {
    Text("© 2023 My Company")
}
```

## SEO Features

### Meta Tags

Summon provides components for managing meta tags:

```kotlin
MetaTags(
    title = "My Website",
    description = "A description of my website",
    keywords = "summon, ui, web framework",
    author = "Your Name"
)
```

### Open Graph Tags

For better social media sharing:

```kotlin
OpenGraphTags(
    title = "My Website",
    type = "website",
    url = "https://example.com",
    image = "https://example.com/image.jpg",
    description = "A description for social media",
    siteName = "My Site"
)
```

### Twitter Cards

For Twitter-specific metadata:

```kotlin
TwitterCards(
    card = TwitterCards.CardType.SUMMARY_LARGE_IMAGE,
    title = "My Website",
    description = "A description for Twitter",
    image = "https://example.com/image.jpg",
    site = "@myhandle"
)
```

### Canonical Links

For managing duplicate content:

```kotlin
CanonicalLinks(
    url = "https://example.com/page",
    alternateLanguages = mapOf(
        "en" to "https://example.com/page",
        "es" to "https://example.com/es/page"
    )
)
```

### Structured Data

For rich search results:

```kotlin
StructuredData.webPage(
    name = "Product Page",
    description = "Our best product details",
    url = "https://example.com/products/1"
)

StructuredData.organization(
    name = "Example Company",
    url = "https://example.com",
    logo = "https://example.com/logo.png"
)

StructuredData.product(
    name = "Example Product",
    description = "This is our featured product",
    image = "https://example.com/product1.jpg",
    price = "99.99",
    currency = "USD"
)
```

### Deep Linking

Summon supports deep linking with SEO-friendly URLs:

```kotlin
DeepLinking.generateMetaTags(
    path = "/products/1",
    title = "Product Details",
    description = "View our featured product",
    imageUrl = "https://example.com/product1.jpg"
)
```

## Best Practices

1. **Accessibility**:
   - Use semantic HTML elements when possible
   - Provide ARIA labels for interactive elements
   - Ensure proper heading hierarchy
   - Make sure all interactive elements are keyboard accessible
   - Test with screen readers
   - Use the AccessibleElement component for complex interactive elements
   - Ensure proper focus management throughout your application

2. **SEO**:
   - Use descriptive meta titles and descriptions
   - Implement structured data for rich search results
   - Provide canonical URLs for duplicate content
   - Include Open Graph and Twitter Card metadata
   - Use semantic HTML structure
   - Ensure proper heading hierarchy
   - Make content accessible to search engines 