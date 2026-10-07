package codes.yousef.summon.ssr

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.routing.RouteDefinition
import java.nio.file.Files
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class StaticSiteGeneratorContractTest {
    @Test
    fun generatesStaticAndEverySupportedDynamicRouteFamilyWithAssets() {
        val output = Files.createTempDirectory("summon-static-site-").toFile()
        try {
            val routes = listOf(
                route("/", "Home"),
                route("/about.html", "About"),
                route("/products/{id}", "Product"),
                route("/users/{userId}", "User"),
                route("/blog/{slug}", "Blog"),
                route("/categories/{category}", "Category")
            )
            StaticSiteGenerator.generateStaticSite(routes, output.absolutePath)

            listOf(
                "index.html",
                "about.html",
                "products/product-1.html",
                "products/product-5.html",
                "users/user-1.html",
                "users/user-3.html",
                "blog/first-post.html",
                "blog/advanced-techniques.html",
                "categories/sample-category-value.html",
                "assets/css/main.css",
                "assets/js/summon-hydration.js",
                "assets/img/placeholder.svg",
                "favicon.svg",
            ).forEach { relative -> assertTrue(output.resolve(relative).isFile, relative) }
            assertContains(output.resolve("products/product-1.html").toPath().readText(), "product-1")
            assertContains(output.resolve("index.html").toPath().readText(), "Home")
        } finally {
            output.deleteRecursively()
        }
    }

    private fun route(path: String, label: String) = RouteDefinition(
        path = path,
        title = label,
        description = "$label description",
        canonicalUrl = "https://example.test$path",
        content = { params -> Text("$label ${params.params.values.joinToString()}") }
    )
}
