package codes.yousef.summon.ssr

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DynamicRouteDataContractTest {
    @Test
    fun everyRouteFamilyReturnsItsTypedPayload() = runTest {
        val product = DynamicRendering.fetchDataForRoute("product-42")
        assertEquals("42", product["id"])
        assertTrue(product.keys.containsAll(listOf("price", "rating", "categories", "metadata", "timestamp")))

        val user = DynamicRendering.fetchDataForRoute("user-7")
        assertEquals("user7@example.com", user["email"])
        assertIs<Map<*, *>>(user["preferences"])

        val blog = DynamicRendering.fetchDataForRoute("blog-post")
        assertEquals(3, assertIs<List<*>>(blog["comments"]).size)
        assertIs<List<*>>(blog["tags"])

        val dashboard = DynamicRendering.fetchDataForRoute("dashboard-main")
        assertIs<Map<*, *>>(dashboard["metrics"])
        assertEquals(2, assertIs<List<*>>(dashboard["charts"]).size)

        val generic = DynamicRendering.fetchDataForRoute("about")
        assertEquals("Content for about", generic["title"])
        assertEquals("generic", assertIs<Map<*, *>>(generic["metadata"])["type"])
    }
}
