package codes.yousef.summon.runtime

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.seo.MetaTags
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class JvmHeadRenderingTest {

    @Test
    fun customMetadataReplacesDefaultsAndIsEscaped() {
        val renderer = PlatformRenderer()
        renderer.renderHeadElements {
            title("Packages <Seen> & friends")
            meta(name = "description", content = "A \"safe\" <catalog> & index")
            meta(property = "og:title", content = "Packages <Seen>")
        }

        val html = renderer.renderComposableRootWithHydration("en", "ltr") {
            Text("Catalog")
        }

        assertEquals(1, Regex("<title>").findAll(html).count())
        assertEquals(1, Regex("name=\"description\"").findAll(html).count())
        assertEquals(1, Regex("property=\"og:title\"").findAll(html).count())
        assertEquals(1, Regex("name=\"viewport\"").findAll(html).count())
        assertContains(html, "<title>Packages &lt;Seen&gt; &amp; friends</title>")
        assertContains(html, "content=\"A &quot;safe&quot; &lt;catalog&gt; &amp; index\"")
        assertFalse(html.substringAfter("<body>").contains("<meta"))
    }

    @Test
    fun metadataAndGeneratedHeadStylesDoNotLeakAcrossRendererReuse() {
        val renderer = PlatformRenderer()

        val first = renderer.renderComposableRoot {
            MetaTags(title = "First page", description = "First description")
            Text("First")
        }
        val second = renderer.renderComposableRoot {
            Text("Second")
        }

        assertContains(first, "<title>First page</title>")
        assertContains(first, "First description")
        assertFalse(second.contains("First page"))
        assertFalse(second.contains("First description"))
        assertContains(second, "<title>Summon App</title>")
    }

    @Test
    fun languageAndDirectionAttributesAreEscaped() {
        val renderer = PlatformRenderer()

        val html = renderer.renderComposableRootWithHydration(
            lang = "en\" data-hostile=\"true",
            dir = "ltr<unsafe",
        ) {
            Text("Content")
        }

        assertContains(html, "lang=\"en&quot; data-hostile=&quot;true\"")
        assertContains(html, "dir=\"ltr&lt;unsafe\"")
        assertFalse(html.contains("data-hostile=\"true\""))
    }
}
