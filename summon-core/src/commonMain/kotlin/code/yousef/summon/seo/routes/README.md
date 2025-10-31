# Summon SEO Components

This package provides SEO enhancements for your Summon UI applications.

## Components

### MetaTags

Easy configuration of HTML meta tags for search engines.

```kotlin
// Basic usage
MetaTags(
    title = "My Website",
    description = "A description of my website for search engines",
    keywords = "summon, ui, web framework",
    author = "Your Name"
).compose(head)

// Using the standard helper
MetaTags.standard(
    title = "My Website",
    description = "A description of my website"
).compose(head)
```

### StructuredData

JSON-LD generation helpers for semantic web data.

```kotlin
// Create web page structured data
StructuredData.webPage(
    name = "Product Page",
    description = "Our best product details",
    url = "https://example.com/products/1"
).compose(head)

// Create organization structured data
StructuredData.organization(
    name = "Example Company",
    url = "https://example.com",
    logo = "https://example.com/logo.png"
).compose(head)

// Create product structured data
StructuredData.product(
    name = "Example Product",
    description = "This is our featured product",
    image = "https://example.com/product1.jpg",
    price = "99.99",
    currency = "USD"
).compose(head)
```

### SemanticHTML

Semantic HTML element helpers for better content structure.

```kotlin
// Create semantic elements
SemanticHTML.Header(className = "site-header") {
    // Header content here
}.compose(this)

SemanticHTML.Main(className = "content") {
    SemanticHTML.Section(className = "product-details") {
        SemanticHTML.Heading(level = 1) { +"Product Name" }
        // Section content
    }.compose(this)

    SemanticHTML.Article(className = "product-description") {
        // Article content
    }.compose(this)
}.compose(this)

SemanticHTML.Footer(className = "site-footer") {
    // Footer content
}.compose(this)
```

### SitemapGeneration

Sitemap creation utilities to help search engines discover your content.

```kotlin
// Generate sitemap from routes
val sitemap = SitemapGeneration.fromRoutes(
    routes = myRoutes,
    baseUrl = "https://example.com"
)

// Use within a route handler
Route("/sitemap.xml") { _ ->
    return@Route object : Composable {
        override fun <T> compose(receiver: T): T {
            if (receiver is TagConsumer<*>) {
                receiver.onTagContent(sitemap.getXmlString())
            }
            return receiver
        }
    }
}

// Generate robots.txt content
val robotsTxt = SitemapGeneration.robotsTxt("https://example.com/sitemap.xml")
```

### CanonicalLinks

Management of canonical URLs for handling duplicate content issues.

```kotlin
// Basic canonical link
CanonicalLinks.simple("https://example.com/products/1").compose(head)

// Canonical with language alternates
CanonicalLinks.withLanguages(
    defaultUrl = "https://example.com/products/1",
    defaultLanguage = "en",
    alternates = mapOf(
        "es" to "https://example.com/es/products/1",
        "fr" to "https://example.com/fr/products/1"
    )
).compose(head)

// Canonical with AMP version
CanonicalLinks.withAmp(
    url = "https://example.com/products/1",
    ampUrl = "https://example.com/amp/products/1"
).compose(head)
```

### OpenGraphTags

Social media metadata support for rich sharing experiences.

```kotlin
// Basic Open Graph tags
OpenGraphTags(
    title = "My Page Title",
    url = "https://example.com/page",
    image = "https://example.com/image.jpg",
    description = "Description for social media"
).compose(head)

// Article specific Open Graph
OpenGraphTags.article(
    title = "Article Title",
    url = "https://example.com/article/1",
    image = "https://example.com/article-image.jpg",
    description = "Article description",
    publishedTime = "2023-05-15T14:30:00Z",
    author = "Author Name"
).compose(head)
```

### TwitterCards

Twitter-specific metadata for rich previews when sharing.

```kotlin
// Basic Twitter Card
TwitterCards(
    card = TwitterCards.CardType.SUMMARY,
    title = "My Page Title",
    description = "Description for Twitter",
    image = "https://example.com/image.jpg",
    site = "@twitterhandle"
).compose(head)

// Article Twitter Card with large image
TwitterCards.article(
    title = "Article Title",
    description = "Article description for Twitter",
    image = "https://example.com/article-image.jpg",
    site = "@twitterhandle",
    creator = "@authorhandle"
).compose(head)
```

## Integration Example

```kotlin
class SeoExample : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is HEAD) {
            // Add SEO components to the HEAD
            MetaTags.standard(
                title = "My Product Page",
                description = "The best product ever"
            ).compose(receiver)
            
            OpenGraphTags(
                title = "My Product Page",
                url = "https://example.com/products/1",
                image = "https://example.com/product1.jpg",
                description = "Check out this amazing product"
            ).compose(receiver)
            
            TwitterCards(
                card = TwitterCards.CardType.SUMMARY_LARGE_IMAGE,
                title = "My Product Page",
                description = "The best product ever",
                image = "https://example.com/product1.jpg"
            ).compose(receiver)
            
            CanonicalLinks.simple("https://example.com/products/1").compose(receiver)
            
            StructuredData.product(
                name = "Amazing Product",
                description = "This product will change your life",
                image = "https://example.com/product1.jpg",
                price = "99.99"
            ).compose(receiver)
        }
        
        return receiver
    }
} 