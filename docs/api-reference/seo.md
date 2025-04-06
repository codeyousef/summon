# SEO API Reference

This document provides detailed API documentation for Summon's SEO features.

## Meta Tags

### MetaTags

```kotlin
class MetaTags(
    private val title: String? = null,
    private val description: String? = null,
    private val keywords: String? = null,
    private val author: String? = null,
    private val viewport: String = "width=device-width, initial-scale=1.0",
    private val charset: String = "UTF-8",
    private val extraTags: Map<String, String> = emptyMap()
) : Composable
```

The `MetaTags` component configures HTML meta tags for search engines.

### OpenGraphTags

```kotlin
class OpenGraphTags(
    private val title: String,
    private val type: String = "website",
    private val url: String,
    private val image: String? = null,
    private val description: String? = null,
    private val siteName: String? = null,
    private val locale: String? = null,
    private val extraTags: Map<String, String> = emptyMap()
) : Composable

companion object {
    fun article(
        title: String,
        url: String,
        image: String,
        description: String,
        publishedTime: String? = null,
        author: String? = null,
        siteName: String? = null
    ): OpenGraphTags
}
```

The `OpenGraphTags` component adds Open Graph meta tags for better social media sharing.

### TwitterCards

```kotlin
class TwitterCards(
    private val card: CardType = CardType.SUMMARY,
    private val title: String? = null,
    private val description: String? = null,
    private val image: String? = null,
    private val imageAlt: String? = null,
    private val site: String? = null,
    private val creator: String? = null,
    private val extraTags: Map<String, String> = emptyMap()
) : Composable

enum class CardType(val value: String) {
    SUMMARY("summary"),
    SUMMARY_LARGE_IMAGE("summary_large_image"),
    APP("app"),
    PLAYER("player")
}
```

The `TwitterCards` component adds Twitter Card meta tags for better Twitter sharing.

### CanonicalLinks

```kotlin
class CanonicalLinks(
    private val url: String,
    private val alternateLanguages: Map<String, String> = emptyMap(),
    private val ampUrl: String? = null
) : Composable

companion object {
    fun simple(url: String): CanonicalLinks
}
```

The `CanonicalLinks` component manages canonical URLs and alternate language versions.

## Structured Data

### StructuredData

```kotlin
object StructuredData {
    fun webPage(
        name: String,
        description: String,
        url: String
    ): Composable

    fun organization(
        name: String,
        url: String,
        logo: String
    ): Composable

    fun product(
        name: String,
        description: String,
        image: String,
        price: String,
        currency: String
    ): Composable
}
```

The `StructuredData` object provides utilities for generating JSON-LD structured data.

## Deep Linking

### DeepLinking

```kotlin
class DeepLinking private constructor() {
    fun generateMetaTags(
        path: String,
        title: String,
        description: String,
        imageUrl: String? = null,
        type: String = "website"
    ): Composable
}
```

The `DeepLinking` class provides support for deep linking with SEO-friendly URLs.

## SEO Prerendering

### SEOPrerenderer

```kotlin
class SEOPrerenderer {
    fun isSearchEngineCrawler(userAgent: String): Boolean
    fun enrichSeoMetadata(metadata: SeoMetadata): SeoMetadata
}

object SEOPrerender {
    fun isSearchEngineCrawler(userAgent: String): Boolean
}
```

The `SEOPrerenderer` class and `SEOPrerender` object provide utilities for SEO pre-rendering and detecting search engine crawlers.

## Examples

### Basic Meta Tags

```kotlin
MetaTags(
    title = "My Website",
    description = "A description of my website",
    keywords = "summon, ui, web framework",
    author = "Your Name"
).compose(head)
```

### Open Graph Tags

```kotlin
OpenGraphTags(
    title = "My Website",
    type = "website",
    url = "https://example.com",
    image = "https://example.com/image.jpg",
    description = "A description for social media",
    siteName = "My Site"
).compose(head)
```

### Twitter Cards

```kotlin
TwitterCards(
    card = TwitterCards.CardType.SUMMARY_LARGE_IMAGE,
    title = "My Website",
    description = "A description for Twitter",
    image = "https://example.com/image.jpg",
    site = "@myhandle"
).compose(head)
```

### Canonical Links

```kotlin
CanonicalLinks(
    url = "https://example.com/page",
    alternateLanguages = mapOf(
        "en" to "https://example.com/page",
        "es" to "https://example.com/es/page"
    )
).compose(head)
```

### Structured Data

```kotlin
StructuredData.webPage(
    name = "Product Page",
    description = "Our best product details",
    url = "https://example.com/products/1"
).compose(head)

StructuredData.organization(
    name = "Example Company",
    url = "https://example.com",
    logo = "https://example.com/logo.png"
).compose(head)

StructuredData.product(
    name = "Example Product",
    description = "This is our featured product",
    image = "https://example.com/product1.jpg",
    price = "99.99",
    currency = "USD"
).compose(head)
```

### Deep Linking

```kotlin
DeepLinking.generateMetaTags(
    path = "/products/1",
    title = "Product Details",
    description = "View our featured product",
    imageUrl = "https://example.com/product1.jpg"
).compose(head)
``` 