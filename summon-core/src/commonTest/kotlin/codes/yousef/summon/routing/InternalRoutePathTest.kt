package codes.yousef.summon.routing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InternalRoutePathTest {
    @Test
    fun acceptsSuiteRouteFamiliesAndDecodesOpaqueParameters() {
        val patterns = listOf(
            "/mail",
            "/mail/thread/:id",
            "/mail/compose",
            "/calendar",
            "/calendar/event/:id",
            "/aliases",
            "/aliases/:id",
            "/security",
            "/security/devices",
            "/security/recovery",
            "/drive/*",
            "/attention",
            "/connectors/*",
            "/feed",
            "/people/:handle",
            "/communities/:id"
        )
        patterns.forEach { assertNotNull(InternalRoutePath.parse(it), it) }

        val route = Route("/people/:handle") { { } }
        assertEquals("مستخدم", route.extractParams("/people/%D9%85%D8%B3%D8%AA%D8%AE%D8%AF%D9%85")?.get("handle"))
        assertTrue(Route("/drive/*") { { } }.matches("/drive/folder/object"))
    }

    @Test
    fun rejectsExternalAmbiguousAndTraversalPaths() {
        listOf(
            "https://example.test/mail",
            "//example.test/mail",
            "/mail?body=private",
            "/mail#private",
            "/mail/../security",
            "/mail/%2e%2e/security",
            "/mail/%2Fsecurity",
            "/mail/%5csecurity",
            "/mail/%00security",
            "/mail/%",
            "/mail/%GG",
            "/mail/\u0000private"
        ).forEach { unsafe -> assertNull(InternalRoutePath.parse(unsafe), "accepted unsafe path category") }
    }

    @Test
    fun enforcesByteBoundsUtf8AndPatternStructureAtEveryBoundary() {
        assertNotNull(InternalRoutePath.parse("/"))
        assertNotNull(InternalRoutePath.parse("/${"a".repeat(256)}"))
        assertNull(InternalRoutePath.parse("/${"a".repeat(257)}"))
        assertNull(InternalRoutePath.parse("/${"a".repeat(2_048)}"))
        assertNull(InternalRoutePath.parse("/mail/\u007fprivate"))
        assertNull(InternalRoutePath.parse("/mail/%A"))
        assertNull(InternalRoutePath.parse("/mail/%FF"))

        val path = assertNotNull(InternalRoutePath.parse("/users/alice/files/document"))
        assertEquals(mapOf("name" to "alice"), matchInternalRoute("/users/{name}/files/*", path))
        assertNull(matchInternalRoute("not-a-path", path))
        assertNull(matchInternalRoute("/users/*/files", path))
        assertNull(matchInternalRoute("/users/:name", path))
        assertNull(matchInternalRoute("/users/bob/files/*", path))
        assertEquals(
            emptyMap(),
            matchInternalRoute("/users/:/files/*", assertNotNull(InternalRoutePath.parse("/users/:/files/document")))
        )
    }

    @Test
    fun routeMatchingDoesNotTreatPrefixesAsAuthorizedRoutes() {
        val aliases = Route("/aliases") { { } }
        assertTrue(aliases.matches("/aliases"))
        assertFalse(aliases.matches("/aliases/private"))
        assertFalse(aliases.matches("/aliases-malicious"))
    }

    @Test
    fun guardStatesRemainExplicitAndPrivacySafe() {
        val results = listOf(
            GuardResult.Loading,
            GuardResult.Locked,
            GuardResult.FeatureDisabled,
            GuardResult.PermissionDenied,
            GuardResult.Deny
        )
        assertEquals(
            listOf("loading", "locked", "feature unavailable", "permission denied", "permission denied"),
            results.map { it.safeReason }
        )
        assertFalse(results.any { it.safeReason.contains("private-object") })
    }
}
