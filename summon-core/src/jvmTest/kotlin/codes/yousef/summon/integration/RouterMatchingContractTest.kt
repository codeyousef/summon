package codes.yousef.summon.integration

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RouterMatchingContractTest {
    @Test
    fun basePathsNormalizeOnceAcrossEveryJvmServerIntegration() {
        mapOf(
            "" to "/",
            "   " to "/",
            "/" to "/",
            "app" to "/app",
            "/app/" to "/app",
            "///" to "/",
        ).forEach { (input, expected) -> assertEquals(expected, input.normalizeRouterBasePath()) }

        assertEquals("/", "".ensureRouterLeadingSlash())
        assertEquals("/", "   ".ensureRouterLeadingSlash())
        assertEquals("/ready", "/ready".ensureRouterLeadingSlash())
        assertEquals("/ready", "ready".ensureRouterLeadingSlash())
    }

    @Test
    fun routePatternsHandleRootStaticParametersAndCatchAllBoundaries() {
        assertTrue(routerPatternMatches("/", "/"))
        assertFalse(routerPatternMatches("/", "/other"))
        assertTrue(routerPatternMatches("users/:id", "/users/42"))
        assertFalse(routerPatternMatches("/users/:id", "/users"))
        assertFalse(routerPatternMatches("/users/:id", "/groups/42"))
        assertTrue(routerPatternMatches("/assets/*", "/assets"))
        assertTrue(routerPatternMatches("/assets/*", "/assets/js/app.js"))
        assertFalse(routerPatternMatches("/assets/private/*", "/assets"))
        assertFalse(routerPatternMatches("/assets/*", "/other/file"))
        assertFalse(routerPatternMatches("/single", "/single/extra"))
        assertTrue(routerPatternMatches("single", "/single"))
    }
}
