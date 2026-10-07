package codes.yousef.summon.devtools

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ErrorOverlayModelTest {
    private val sourceMap = """{"version":3,"sources":["webpack://fixture/../../src/App.kt"],"names":[],"mappings":"AAyCM;AAAA"}"""

    @Test
    fun stackParsingIsBoundedAndIgnoresInvalidLocations() {
        val stack = buildString {
            appendLine("at hostile (https://evil.example/x.js:0:1)")
            repeat(150) { appendLine("at frame (https://app.test/fixture.js:${it + 1}:2)") }
        }
        val frames = DevelopmentStackParser.parse(stack)
        assertEquals(DevelopmentStackParser.MAX_FRAMES, frames.size)
        assertEquals(GeneratedSourceLocation("https://app.test/fixture.js", 1, 2), frames.first())
        assertTrue(frames.none { it.assetUrl.contains("evil") })
    }

    @Test
    fun verifiedSourceMapIdentifiesExactKotlinLineAndLeavesOtherAssetsUnmapped() {
        val catalog = VerifiedSourceMapCatalog.parse(
            "build-123",
            listOf(VerifiedSourceMapAsset("https://app.test/fixture.js", "build-123", sourceMap))
        )
        assertEquals(
            MappedSourceLocation("src/App.kt", 42, 7, "build-123"),
            catalog.map(GeneratedSourceLocation("https://app.test/fixture.js", 1, 1))
        )
        assertEquals(
            MappedSourceLocation("src/App.kt", 42, 1, "build-123"),
            catalog.map(GeneratedSourceLocation("https://app.test/fixture.js", 2, 1))
        )
        assertNull(catalog.map(GeneratedSourceLocation("https://evil.example/fixture.js", 1, 1)))
        assertFailsWith<IllegalArgumentException> {
            VerifiedSourceMapCatalog.parse(
                "build-123",
                listOf(VerifiedSourceMapAsset("https://app.test/fixture.js", "other-build", sourceMap))
            )
        }
    }

    @Test
    fun editorAndViewerLinksRequireAllowedRootsSchemesAndExactIdentity() {
        val location = MappedSourceLocation("src/مرحبا App.kt", 42, 7, "build-123")
        val policy = DevelopmentSourceLinkPolicy(
            editorProtocol = "vscode",
            workspaceRoot = "/workspace/project",
            viewerOrigin = "https://source.example",
            sourceRevision = "build-123"
        )
        assertEquals(
            "vscode://file/workspace/project/src/%D9%85%D8%B1%D8%AD%D8%A8%D8%A7%20App.kt:42:7",
            policy.editorLink(location)
        )
        assertEquals(
            "https://source.example/source/build-123/src/%D9%85%D8%B1%D8%AD%D8%A8%D8%A7%20App.kt#L42:C7",
            policy.viewerLink(location)
        )
        assertNull(policy.copy(editorProtocol = "javascript").editorLink(location))
        assertNull(policy.copy(workspaceRoot = "/workspace/../private").editorLink(location))
        assertNull(policy.copy(viewerOrigin = "https://user@source.example").viewerLink(location))
        assertNull(policy.copy(viewerOrigin = "https://source.example:99999").viewerLink(location))
        assertNull(policy.copy(viewerOrigin = "https://bad..example").viewerLink(location))
        assertNull(policy.viewerLink(location.copy(source = "../private.kt")))
        assertNull(policy.editorLink(location.copy(buildId = "other")))
    }

    @Test
    fun messagesAreGenericUnlessSyntheticPublicTextIsExplicitlyAllowedAndBounded() {
        assertEquals(
            "Unhandled promise rejection",
            DevelopmentStackParser.displayText(
                DevelopmentErrorCategory.UNHANDLED_REJECTION,
                "private token",
                DevelopmentErrorTextPolicy.GENERIC_ONLY
            )
        )
        val text = "🙂".repeat(DevelopmentStackParser.MAX_TEXT_UTF8_BYTES)
        val displayed = DevelopmentStackParser.displayText(
            DevelopmentErrorCategory.ERROR_BOUNDARY,
            text,
            DevelopmentErrorTextPolicy.SYNTHETIC_PUBLIC
        )
        assertTrue(displayed.encodeToByteArray().size <= DevelopmentStackParser.MAX_TEXT_UTF8_BYTES)
        assertTrue(displayed.endsWith("🙂"))
    }

    @Test
    fun unsafeSourcePathsAndMalformedMappingsAreRejected() {
        assertFailsWith<IllegalArgumentException> {
            VerifiedSourceMapCatalog.parse(
                "build-123",
                listOf(
                    VerifiedSourceMapAsset(
                        "https://app.test/fixture.js",
                        "build-123",
                        """{"version":3,"sources":["../secret.kt"],"names":[],"mappings":"AAAA"}"""
                    )
                )
            )
        }
        assertFailsWith<IllegalArgumentException> {
            VerifiedSourceMapCatalog.parse(
                "build-123",
                listOf(VerifiedSourceMapAsset("https://app.test/fixture.js", "build-123", "{}"))
            )
        }
    }
}
