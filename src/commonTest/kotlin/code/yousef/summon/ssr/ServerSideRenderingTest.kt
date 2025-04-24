package code.yousef.summon.ssr

import kotlin.test.*

/**
 * Tests for the server-side rendering module.
 */
class ServerSideRenderingTest {

    /**
     * Test the HtmlBuilder interface and SimpleHtmlBuilder implementation.
     */
    @Test
    fun testHtmlBuilder() {
        // Create a new HTML builder
        val builder = createHTML()

        // Verify it's a SimpleHtmlBuilder
        assertTrue(builder is SimpleHtmlBuilder)

        // Test appending content
        builder.append("<div>")
        builder.append("Hello, World!")
        builder.append("</div>")

        // Test finalizing the HTML
        val html = builder.finalize()
        assertEquals("<div>Hello, World!</div>", html)
    }

    /**
     * Test the RenderContext class.
     */
    @Test
    fun testRenderContext() {
        // Test default values
        val defaultContext = RenderContext()
        assertFalse(defaultContext.enableHydration)
        assertEquals("summon-", defaultContext.hydrationIdPrefix)
        assertTrue(defaultContext.metadata.isEmpty())
        assertFalse(defaultContext.debug)
        assertTrue(defaultContext.initialState.isEmpty())
        assertTrue(defaultContext.headElements.isEmpty())

        // Test custom values
        val customContext = RenderContext(
            enableHydration = true,
            hydrationIdPrefix = "custom-",
            metadata = mapOf("key" to "value"),
            debug = true,
            initialState = mapOf("state" to "value")
        )
        assertTrue(customContext.enableHydration)
        assertEquals("custom-", customContext.hydrationIdPrefix)
        assertEquals(mapOf("key" to "value"), customContext.metadata)
        assertTrue(customContext.debug)
        assertEquals(mapOf("state" to "value"), customContext.initialState)
    }

    /**
     * Test the SeoMetadata class.
     */
    @Test
    fun testSeoMetadata() {
        // Test default values
        val defaultSeo = SeoMetadata()
        assertEquals("", defaultSeo.title)
        assertEquals("", defaultSeo.description)
        assertTrue(defaultSeo.keywords.isEmpty())
        assertEquals("", defaultSeo.canonical)
        assertEquals("index, follow", defaultSeo.robots)
        assertTrue(defaultSeo.customMetaTags.isEmpty())

        // Test custom values
        val customSeo = SeoMetadata(
            title = "Test Title",
            description = "Test Description",
            keywords = listOf("test", "summon", "ssr"),
            canonical = "https://example.com",
            robots = "noindex, nofollow",
            customMetaTags = mapOf("custom" to "value")
        )
        assertEquals("Test Title", customSeo.title)
        assertEquals("Test Description", customSeo.description)
        assertEquals(listOf("test", "summon", "ssr"), customSeo.keywords)
        assertEquals("https://example.com", customSeo.canonical)
        assertEquals("noindex, nofollow", customSeo.robots)
        assertEquals(mapOf("custom" to "value"), customSeo.customMetaTags)
    }

    /**
     * Test the OpenGraphMetadata class.
     */
    @Test
    fun testOpenGraphMetadata() {
        // Test default values
        val defaultOg = OpenGraphMetadata()
        assertEquals("", defaultOg.title)
        assertEquals("", defaultOg.description)
        assertEquals("website", defaultOg.type)
        assertEquals("", defaultOg.url)
        assertEquals("", defaultOg.image)
        assertEquals("", defaultOg.siteName)

        // Test custom values
        val customOg = OpenGraphMetadata(
            title = "OG Title",
            description = "OG Description",
            type = "article",
            url = "https://example.com",
            image = "https://example.com/image.jpg",
            siteName = "Example Site"
        )
        assertEquals("OG Title", customOg.title)
        assertEquals("OG Description", customOg.description)
        assertEquals("article", customOg.type)
        assertEquals("https://example.com", customOg.url)
        assertEquals("https://example.com/image.jpg", customOg.image)
        assertEquals("Example Site", customOg.siteName)
    }

    /**
     * Test the TwitterCardMetadata class.
     */
    @Test
    fun testTwitterCardMetadata() {
        // Test default values
        val defaultTwitter = TwitterCardMetadata()
        assertEquals("summary", defaultTwitter.card)
        assertEquals("", defaultTwitter.site)
        assertEquals("", defaultTwitter.creator)
        assertEquals("", defaultTwitter.title)
        assertEquals("", defaultTwitter.description)
        assertEquals("", defaultTwitter.image)

        // Test custom values
        val customTwitter = TwitterCardMetadata(
            card = "summary_large_image",
            site = "@example",
            creator = "@user",
            title = "Twitter Title",
            description = "Twitter Description",
            image = "https://example.com/image.jpg"
        )
        assertEquals("summary_large_image", customTwitter.card)
        assertEquals("@example", customTwitter.site)
        assertEquals("@user", customTwitter.creator)
        assertEquals("Twitter Title", customTwitter.title)
        assertEquals("Twitter Description", customTwitter.description)
        assertEquals("https://example.com/image.jpg", customTwitter.image)
    }

    /**
     * Test the HydrationStrategy enum.
     */
    @Test
    fun testHydrationStrategy() {
        // Test that all expected enum values exist
        assertEquals(4, HydrationStrategy.entries.size)
        assertNotNull(HydrationStrategy.NONE)
        assertNotNull(HydrationStrategy.FULL)
        assertNotNull(HydrationStrategy.PARTIAL)
        assertNotNull(HydrationStrategy.PROGRESSIVE)
    }

    /**
     * Test the ServerSideRenderUtils object.
     * This test is limited since it depends on platform-specific rendering.
     */
    @Test
    fun testServerSideRenderUtils() {
        // Since ServerSideRenderUtils.renderPageToString depends on platform-specific rendering,
        // we can only verify that the object exists and doesn't throw exceptions when accessed
        assertNotNull(ServerSideRenderUtils)

        // In a real implementation, we would test the renderPageToString method with mock composables
        // For now, we just verify the object structure
    }
}
