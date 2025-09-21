package code.yousef.summon.seo

import code.yousef.summon.annotation.Composable

/**
 * High-level composable component that combines all SEO functionality.
 * Provides a single, convenient API for adding comprehensive SEO metadata to pages.
 *
 * @param title The page title for SEO and social media
 * @param description The page description for SEO and social media
 * @param keywords List of keywords for SEO
 * @param image The main image URL for social media previews
 * @param url The canonical URL of the page
 * @param type The type of content for OpenGraph
 * @param twitterCard The type of Twitter Card to use
 * @param structuredData Schema.org structured data for rich snippets
 * @param author The author of the content
 * @param siteName The name of the website
 * @param locale The locale of the content (e.g., "en_US")
 * @param themeColor Browser theme color for mobile
 * @param viewport Viewport settings for responsive design
 * @param robots Search engine crawling instructions
 * @param twitterSite The @username of the website for Twitter
 * @param twitterCreator The @username of the content creator for Twitter
 * @param customMetaTags Additional custom meta tags
 * @param customOpenGraphProperties Additional OpenGraph properties
 * @param customTwitterProperties Additional Twitter Card properties
 */
@Composable
fun SEO(
    title: String,
    description: String,
    keywords: List<String>? = null,
    image: String? = null,
    url: String? = null,
    type: OGType = OGType.Website,
    twitterCard: TwitterCardType = TwitterCardType.Summary,
    structuredData: StructuredDataSchema? = null,
    author: String? = null,
    siteName: String? = null,
    locale: String? = null,
    themeColor: String? = null,
    viewport: String = "width=device-width, initial-scale=1.0",
    robots: String? = null,
    twitterSite: String? = null,
    twitterCreator: String? = null,
    customMetaTags: Map<String, String> = emptyMap(),
    customOpenGraphProperties: Map<String, String> = emptyMap(),
    customTwitterProperties: Map<String, String> = emptyMap()
) {
    // Render standard meta tags
    MetaTags(
        title = title,
        description = description,
        keywords = keywords,
        author = author,
        viewport = viewport,
        robots = robots,
        canonical = url,
        themeColor = themeColor,
        customTags = customMetaTags
    )

    // Render OpenGraph tags
    OpenGraphTags(
        title = title,
        type = type,
        url = url,
        image = image,
        description = description,
        siteName = siteName,
        locale = locale,
        customProperties = customOpenGraphProperties
    )

    // Render Twitter Card tags
    TwitterCard(
        card = twitterCard,
        title = title,
        description = description,
        image = image,
        site = twitterSite,
        creator = twitterCreator ?: author,
        customProperties = customTwitterProperties
    )

    // Render structured data if provided
    structuredData?.let {
        StructuredData(it)
    }
}

/**
 * SEO component optimized for blog posts and articles.
 */
@Composable
fun ArticleSEO(
    title: String,
    description: String,
    author: String,
    publishedTime: String,
    modifiedTime: String? = null,
    image: String? = null,
    url: String? = null,
    keywords: List<String>? = null,
    section: String? = null,
    siteName: String? = null,
    twitterSite: String? = null
) {
    // Create article structured data
    val articleSchema = ArticleSchema(
        headline = title,
        description = description,
        author = ArticleSchema.Person(name = author),
        datePublished = publishedTime,
        dateModified = modifiedTime,
        image = image,
        mainEntityOfPage = url,
        keywords = keywords,
        articleSection = section,
        publisher = siteName?.let { OrganizationSchema(name = it) }
    )

    SEO(
        title = title,
        description = description,
        keywords = keywords,
        image = image,
        url = url,
        type = OGType.Article,
        twitterCard = if (image != null) TwitterCardType.SummaryLargeImage else TwitterCardType.Summary,
        structuredData = articleSchema,
        author = author,
        siteName = siteName,
        twitterSite = twitterSite,
        twitterCreator = "@$author",
        customOpenGraphProperties = buildMap {
            put("article:author", author)
            put("article:published_time", publishedTime)
            modifiedTime?.let { put("article:modified_time", it) }
            section?.let { put("article:section", it) }
        }
    )
}

/**
 * SEO component optimized for product pages.
 */
@Composable
fun ProductSEO(
    name: String,
    description: String,
    image: String,
    price: Double,
    currency: String = "USD",
    brand: String,
    sku: String? = null,
    availability: String = "https://schema.org/InStock",
    rating: ProductSchema.Rating? = null,
    url: String? = null,
    siteName: String? = null,
    twitterSite: String? = null
) {
    // Create product structured data
    val productSchema = ProductSchema(
        name = name,
        description = description,
        image = image,
        brand = brand,
        sku = sku,
        offers = listOf(
            ProductSchema.OfferDetail(
                price = price,
                priceCurrency = currency,
                availability = availability
            )
        ),
        aggregateRating = rating
    )

    SEO(
        title = "$name - $brand",
        description = description,
        image = image,
        url = url,
        type = OGType.Product,
        twitterCard = TwitterCardType.SummaryLargeImage,
        structuredData = productSchema,
        siteName = siteName,
        twitterSite = twitterSite,
        customOpenGraphProperties = buildMap {
            put("product:price:amount", price.toString())
            put("product:price:currency", currency)
            put("product:availability", availability)
            put("product:brand", brand)
        },
        customTwitterProperties = buildMap {
            put("label1", "Price")
            put("data1", "$currency$price")
            put("label2", "Brand")
            put("data2", brand)
        }
    )
}

/**
 * SEO component optimized for web applications.
 */
@Composable
fun WebAppSEO(
    name: String,
    description: String,
    url: String,
    image: String? = null,
    category: String? = null,
    author: String? = null,
    siteName: String? = null,
    twitterSite: String? = null,
    themeColor: String? = null,
    manifestUrl: String? = null
) {
    // Create web application structured data
    val webAppSchema = WebApplicationSchema(
        name = name,
        description = description,
        url = url,
        applicationCategory = category,
        author = author?.let { OrganizationSchema(name = it) }
    )

    // Base SEO
    SEO(
        title = name,
        description = description,
        image = image,
        url = url,
        type = OGType.Website,
        twitterCard = if (image != null) TwitterCardType.SummaryLargeImage else TwitterCardType.Summary,
        structuredData = webAppSchema,
        author = author,
        siteName = siteName ?: name,
        twitterSite = twitterSite,
        themeColor = themeColor
    )

    // Add PWA-specific tags if manifest is provided
    manifestUrl?.let {
        PWAMetaTags(
            title = name,
            description = description,
            themeColor = themeColor ?: "#000000",
            manifestUrl = it,
            appleTouchIcon = image
        )
    }
}

/**
 * SEO component for video content.
 */
@Composable
fun VideoSEO(
    title: String,
    description: String,
    videoUrl: String,
    thumbnailImage: String,
    duration: Int? = null, // in seconds
    uploadDate: String? = null,
    url: String? = null,
    siteName: String? = null,
    creator: String? = null,
    twitterSite: String? = null,
    playerWidth: Int = 1280,
    playerHeight: Int = 720
) {
    SEO(
        title = title,
        description = description,
        image = thumbnailImage,
        url = url,
        type = OGType.Video,
        twitterCard = TwitterCardType.Player,
        siteName = siteName,
        twitterSite = twitterSite,
        twitterCreator = creator,
        customOpenGraphProperties = buildMap {
            put("video", videoUrl)
            duration?.let { put("video:duration", it.toString()) }
            uploadDate?.let { put("video:release_date", it) }
        },
        customTwitterProperties = buildMap {
            put("player", videoUrl)
            put("player:width", playerWidth.toString())
            put("player:height", playerHeight.toString())
            duration?.let {
                val minutes = it / 60
                val seconds = it % 60
                put("duration", "$minutes:${seconds.toString().padStart(2, '0')}")
            }
        }
    )
}

/**
 * SEO component with breadcrumb navigation.
 */
@Composable
fun PageWithBreadcrumbsSEO(
    title: String,
    description: String,
    breadcrumbs: List<BreadcrumbListSchema.BreadcrumbItem>,
    image: String? = null,
    url: String? = null,
    siteName: String? = null,
    twitterSite: String? = null
) {
    // Create breadcrumb structured data
    val breadcrumbSchema = BreadcrumbListSchema(items = breadcrumbs)

    SEO(
        title = title,
        description = description,
        image = image,
        url = url,
        structuredData = breadcrumbSchema,
        siteName = siteName,
        twitterSite = twitterSite
    )
}

/**
 * SEO component for FAQ pages.
 */
@Composable
fun FAQSEO(
    title: String,
    description: String,
    questions: List<FAQPageSchema.QuestionAnswer>,
    url: String? = null,
    siteName: String? = null,
    twitterSite: String? = null
) {
    // Create FAQ structured data
    val faqSchema = FAQPageSchema(questions = questions)

    SEO(
        title = title,
        description = description,
        url = url,
        structuredData = faqSchema,
        siteName = siteName,
        twitterSite = twitterSite
    )
}