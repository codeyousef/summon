package code.yousef.summon.routing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.collections.emptyMap
import kotlin.test.assertNotNull
import code.yousef.summon.runtime.Composable

class RouterTest {

    @Test
    fun testRouteParamsGetters() {
        val paramsMap = mapOf("userId" to "123", "status" to "active", "count" to "42")
        val routeParams = RouteParams(paramsMap)

        // Test basic get
        assertEquals("123", routeParams["userId"])
        assertEquals("active", routeParams["status"])
        assertNull(routeParams["nonExistent"])

        // Test getOrDefault
        assertEquals("123", routeParams.getOrDefault("userId", "default"))
        assertEquals("default", routeParams.getOrDefault("nonExistent", "default"))

        // Test asMap
        assertEquals(paramsMap, routeParams.asMap())
    }

    @Test
    fun testRouteParamsTypeConversions() {
        val paramsMap = mapOf(
            "id" to "999",
            "isActive" to "true",
            "score" to "123.45",
            "ratio" to "0.5",
            "name" to "test",
            "invalidNum" to "abc",
            "invalidBool" to "yes"
        )
        val routeParams = RouteParams(paramsMap)

        assertEquals(999, routeParams.getInt("id"))
        assertNull(routeParams.getInt("name"))
        assertNull(routeParams.getInt("invalidNum"))
        assertNull(routeParams.getInt("missing"))

        assertEquals(999L, routeParams.getLong("id"))
        assertNull(routeParams.getLong("invalidNum"))

        assertEquals(true, routeParams.getBoolean("isActive"))
        assertNull(routeParams.getBoolean("name"))
        assertNull(routeParams.getBoolean("invalidBool")) // Only "true" (case-insensitive) is valid
        assertNull(routeParams.getBoolean("missing"))

        assertEquals(123.45f, routeParams.getFloat("score"))
        assertNull(routeParams.getFloat("invalidNum"))

        assertEquals(0.5, routeParams.getDouble("ratio"))
        assertNull(routeParams.getDouble("invalidNum"))
    }

    @Test
    fun testEmptyRouteParams() {
        val emptyParams = RouteParams(emptyMap())

        assertNull(emptyParams["anyKey"])
        assertEquals("default", emptyParams.getOrDefault("anyKey", "default"))
        assertNull(emptyParams.getInt("anyKey"))
        assertNull(emptyParams.getBoolean("anyKey"))
        assertEquals(emptyMap(), emptyParams.asMap())
    }

    @Test
    fun testRouterBuilderImpl() {
        val builder = RouterBuilderImpl()

        // Test adding routes
        val homeContent: @Composable (RouteParams) -> Unit = {}
        val userContent: @Composable (RouteParams) -> Unit = {}

        builder.route("/", homeContent)
        builder.route("/users/{id}", userContent, title = "User Profile")

        assertEquals(2, builder.routes.size, "Should have 2 routes defined")
        assertEquals("/", builder.routes[0].path)
        assertEquals(homeContent, builder.routes[0].content)
        assertNull(builder.routes[0].title)

        assertEquals("/users/{id}", builder.routes[1].path)
        assertEquals(userContent, builder.routes[1].content)
        assertEquals("User Profile", builder.routes[1].title)
        assertNull(builder.routes[1].description)
        assertNull(builder.routes[1].canonicalUrl)

        // Test default not found page (check existence, not rendering)
        assertNotNull(builder.notFoundPage, "Default notFoundPage should exist")

        // Test setting custom not found page
        val customNotFound: @Composable (RouteParams) -> Unit = {}
        builder.setNotFound(customNotFound)
        assertEquals(customNotFound, builder.notFoundPage, "Custom notFoundPage should be set")
    }
} 