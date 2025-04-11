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

companion object {
    fun standard(
        title: String,
        description: String,
        keywords: String? = null,
        author: String? = null
    ): MetaTags
}
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
    @Composable
    fun generateMetaTags(
        path: String,
        title: String,
        description: String,
        imageUrl: String? = null,
        type: String = "website"
    )
    
    fun createDeepLink(
        path: String,
        queryParams: Map<String, String> = emptyMap(),
        fragment: String? = null
    ): String
    
    fun parseDeepLink(url: String): DeepLinkInfo

    data class DeepLinkInfo(
        val path: String,
        val queryParams: Map<String, String>,
        val fragment: String?
    )
    
    companion object {
        val current: DeepLinking
        fun encodeURIComponent(value: String): String
        fun decodeURIComponent(value: String): String
    }
}
```

The `DeepLinking` class provides support for deep linking with SEO-friendly URLs, creating and parsing deep links, and generating appropriate meta tags.

## SEO Prerendering

### SEOPrerenderer

```kotlin
class SEOPrerenderer {
    fun isSearchEngineCrawler(userAgent: String): Boolean
    fun enrichSeoMetadata(metadata: SeoMetadata): SeoMetadata
    fun createOpenGraphMetadata(
        seoMetadata: SeoMetadata,
        url: String,
        imageUrl: String = "",
        siteName: String = ""
    ): OpenGraphMetadata
}

object SEOPrerender {
    fun isSearchEngineCrawler(userAgent: String): Boolean
    fun enrichSeoMetadata(metadata: SeoMetadata): SeoMetadata
    fun createOpenGraphMetadata(
        seoMetadata: SeoMetadata,
        url: String,
        imageUrl: String = "",
        siteName: String = ""
    ): OpenGraphMetadata
}
```

The `SEOPrerenderer` class and `SEOPrerender` object provide utilities for SEO pre-rendering, detecting search engine crawlers, and enhancing metadata for better search engine visibility.

## Examples

### Basic Meta Tags

```kotlin
MetaTags(
    title = "My Website",
    description = "A description of my website",
    keywords = "summon, ui, web framework",
    author = "Your Name"
).compose(head)

// Or using the standard helper
MetaTags.standard(
    title = "My Website",
    description = "A description of my website"
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

### Deep Linking

```kotlin
// Generate meta tags for deep linking
DeepLinking.current.generateMetaTags(
    path = "/products/1",
    title = "Product Details",
    description = "View our featured product",
    imageUrl = "https://example.com/product1.jpg"
)

// Create a deep link URL
val url = DeepLinking.current.createDeepLink(
    path = "/products/1",
    queryParams = mapOf("color" to "red", "size" to "large"),
    fragment = "details"
)
// Result: "/products/1?color=red&size=large#details"

// Parse a deep link URL
val deepLinkInfo = DeepLinking.current.parseDeepLink("/products/1?color=red&size=large#details")
// Result: DeepLinkInfo(path="/products/1", queryParams={"color": "red", "size": "large"}, fragment="details")
```

### SEO Prerendering

```kotlin
// Check if request is from a search engine
val isBot = SEOPrerender.isSearchEngineCrawler(userAgent)

// Enhance metadata for better SEO
val enhancedMetadata = SEOPrerender.enrichSeoMetadata(basicMetadata)

// Generate OpenGraph metadata
val ogMetadata = SEOPrerender.createOpenGraphMetadata(
    seoMetadata = basicMetadata,
    url = "https://example.com/page",
    imageUrl = "https://example.com/image.jpg",
    siteName = "My Website"
)
``` 