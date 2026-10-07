package codes.yousef.summon.devtools

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
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

    @Test
    fun invalidCatalogInputsAndLinkPoliciesFailClosed() {
        assertEquals(emptyList(), DevelopmentStackParser.parse(null))
        assertEquals(
            "Unhandled script error",
            DevelopmentStackParser.displayText(
                DevelopmentErrorCategory.SCRIPT_ERROR,
                null,
                DevelopmentErrorTextPolicy.SYNTHETIC_PUBLIC
            )
        )
        assertEquals(
            "Component error boundary",
            DevelopmentStackParser.displayText(
                DevelopmentErrorCategory.ERROR_BOUNDARY,
                "ignored",
                DevelopmentErrorTextPolicy.GENERIC_ONLY
            )
        )
        assertFailsWith<IllegalArgumentException> { VerifiedSourceMapCatalog.parse("bad identity!", emptyList()) }
        assertFailsWith<IllegalArgumentException> {
            VerifiedSourceMapCatalog.parse(
                "build",
                List(129) { VerifiedSourceMapAsset("https://app.test/$it.js", "build", sourceMap) }
            )
        }
        assertFailsWith<IllegalArgumentException> {
            VerifiedSourceMapCatalog.parse(
                "build",
                listOf(VerifiedSourceMapAsset("file:///tmp/app.js", "build", sourceMap))
            )
        }
        assertFailsWith<IllegalArgumentException> {
            val asset = VerifiedSourceMapAsset("https://app.test/app.js", "build", sourceMap)
            VerifiedSourceMapCatalog.parse("build", listOf(asset, asset))
        }
        listOf(
            """{"version":2,"sources":["App.kt"],"mappings":"AAAA"}""",
            """{"version":3,"sources":["App.kt"],"mappings":"!"}""",
            """{"version":3,"sources":["App.kt"],"mappings":"g"}""",
            """{"version":3,"sources":["App.kt"],"mappings":"AAAAAA"}""",
        ).forEach { malformed ->
            assertFailsWith<IllegalArgumentException> {
                VerifiedSourceMapCatalog.parse(
                    "build",
                    listOf(VerifiedSourceMapAsset("https://app.test/app.js", "build", malformed))
                )
            }
        }

        val location = MappedSourceLocation("src/App.kt", 1, 1, "build")
        val valid = DevelopmentSourceLinkPolicy("idea", "/workspace", "https://viewer.test:443", "build")
        assertTrue(valid.viewerLink(location)!!.startsWith("https://viewer.test:443/source/"))
        assertNull(valid.copy(editorProtocol = null).editorLink(location))
        assertNull(valid.copy(editorProtocol = "9invalid").editorLink(location))
        assertNull(valid.copy(editorProtocol = "https").editorLink(location))
        assertNull(valid.copy(workspaceRoot = null).editorLink(location))
        assertNull(valid.editorLink(location.copy(source = "bad\\path")))
        assertNull(valid.editorLink(location.copy(line = 0)))
        assertNull(valid.copy(sourceRevision = "other").viewerLink(location))
        assertNull(valid.copy(viewerOrigin = null).viewerLink(location))
        assertNull(valid.viewerLink(location.copy(source = "bad:path")))
        assertNull(valid.viewerLink(location.copy(column = 0)))
    }

    @Test
    fun publicErrorModelsIncludeEveryFieldInIdentity() {
        val generated = GeneratedSourceLocation("https://app.test/app.js", 1, 2)
        assertEquals(generated, generated.copy())
        listOf(
            generated.copy(assetUrl = "https://app.test/other.js"),
            generated.copy(line = 2),
            generated.copy(column = 3),
        ).forEach { assertNotEquals(generated, it) }

        val mapped = MappedSourceLocation("src/App.kt", 3, 4, "build")
        assertEquals(mapped, mapped.copy())
        listOf(
            mapped.copy(source = "src/Other.kt"),
            mapped.copy(line = 4),
            mapped.copy(column = 5),
            mapped.copy(buildId = "other"),
        ).forEach { assertNotEquals(mapped, it) }

        val frame = DevelopmentErrorFrame(generated, mapped)
        assertEquals(frame, frame.copy())
        assertNotEquals(frame, frame.copy(generated = generated.copy(line = 9)))
        assertNotEquals(frame, frame.copy(mapped = null))

        val error = DevelopmentError(DevelopmentErrorCategory.SCRIPT_ERROR, "public", listOf(frame))
        assertEquals(error, error.copy())
        assertNotEquals(error, error.copy(category = DevelopmentErrorCategory.ERROR_BOUNDARY))
        assertNotEquals(error, error.copy(text = "other"))
        assertNotEquals(error, error.copy(frames = emptyList()))

        val asset = VerifiedSourceMapAsset("https://app.test/app.js", "build", sourceMap)
        assertEquals(asset, asset.copy())
        assertNotEquals(asset, asset.copy(generatedAssetUrl = "https://app.test/other.js"))
        assertNotEquals(asset, asset.copy(buildId = "other"))
        assertNotEquals(asset, asset.copy(json = "{}"))

        val policy = DevelopmentSourceLinkPolicy("idea", "/workspace", "https://viewer.test", "build")
        assertEquals(policy, policy.copy())
        listOf(
            policy.copy(editorProtocol = null),
            policy.copy(workspaceRoot = null),
            policy.copy(viewerOrigin = null),
            policy.copy(sourceRevision = null),
        ).forEach { assertNotEquals(policy, it) }
    }

    @Test
    fun sourceMapAndLinkBoundaryVariantsFailClosedWithoutFetching() {
        val rooted = """{"version":3,"sourceRoot":"src","sources":["App.kt"],"mappings":"AAAA"}"""
        val catalog = VerifiedSourceMapCatalog.parse(
            "build",
            listOf(VerifiedSourceMapAsset("https://app.test/rooted.js", "build", rooted))
        )
        assertEquals(
            MappedSourceLocation("src/App.kt", 1, 1, "build"),
            catalog.map(GeneratedSourceLocation("https://app.test/rooted.js", 1, 1))
        )
        assertNull(catalog.map(GeneratedSourceLocation("https://app.test/rooted.js", 2, 1)))

        val generatedOnly = """{"version":3,"sources":["App.kt"],"mappings":"A"}"""
        assertNull(
            VerifiedSourceMapCatalog.parse(
                "build",
                listOf(VerifiedSourceMapAsset("https://app.test/generated.js", "build", generatedOnly))
            ).map(GeneratedSourceLocation("https://app.test/generated.js", 1, 1))
        )
        listOf(
            """{"version":3,"sources":["../secret.kt"],"mappings":"AAAA"}""",
            """{"version":3,"sources":["App.kt"],"mappings":"D"}""",
            """{"version":3,"sources":["App.kt"],"mappings":"ACAA"}""",
        ).forEach { malformed ->
            assertFailsWith<IllegalArgumentException> {
                VerifiedSourceMapCatalog.parse(
                    "build",
                    listOf(VerifiedSourceMapAsset("https://app.test/bad.js", "build", malformed))
                )
            }
        }

        val location = MappedSourceLocation("src/file name-µ.kt", 2, 3, "build")
        val policy = DevelopmentSourceLinkPolicy("idea", "/work space", "https://viewer.test", "build")
        assertTrue(policy.editorLink(location)!!.contains("%20"))
        assertTrue(policy.viewerLink(location)!!.contains("%C2%B5"))
        listOf(
            "https://-bad.test",
            "https://bad-.test",
            "https://bad..test",
            "https://viewer.test:0",
            "https://viewer.test:65536",
            "https://user@viewer.test",
            "http://viewer.test",
        ).forEach { origin ->
            assertNull(policy.copy(viewerOrigin = origin).viewerLink(location))
        }
        listOf("/workspace//bad", "/workspace/./bad", "/workspace/../bad", "relative", "/bad:path").forEach { root ->
            assertNull(policy.copy(workspaceRoot = root).editorLink(location))
        }

        val unicode = "a".repeat(DevelopmentStackParser.MAX_TEXT_UTF8_BYTES) + "é€😀"
        assertEquals(
            DevelopmentStackParser.MAX_TEXT_UTF8_BYTES,
            DevelopmentStackParser.displayText(
                DevelopmentErrorCategory.SCRIPT_ERROR,
                unicode,
                DevelopmentErrorTextPolicy.SYNTHETIC_PUBLIC
            ).encodeToByteArray().size
        )
    }
    @Test
    fun stackCatalogAndLinkPoliciesCoverEveryPublicFailClosedBoundary() {
        assertTrue(DevelopmentStackParser.parse(null).isEmpty())
        assertTrue(DevelopmentStackParser.parse("not a frame").isEmpty())
        assertTrue(DevelopmentStackParser.parse("at x (https://app.test/a.js:0:1)").isEmpty())
        assertTrue(DevelopmentStackParser.parse("at x (https://app.test/a.js:1:0)").isEmpty())
        assertTrue(DevelopmentStackParser.parse("at x (https://app.test/a.js:10000001:1)").isEmpty())
        val oversizedStack = (1..150).joinToString("\n") { "at f$it (https://app.test/a.js:$it:1)" }
        assertEquals(DevelopmentStackParser.MAX_FRAMES, DevelopmentStackParser.parse(oversizedStack).size)
        DevelopmentErrorCategory.entries.forEach { category ->
            assertTrue(
                DevelopmentStackParser.displayText(category, "private", DevelopmentErrorTextPolicy.GENERIC_ONLY) != "private"
            )
            assertTrue(
                DevelopmentStackParser.displayText(category, null, DevelopmentErrorTextPolicy.SYNTHETIC_PUBLIC).isNotBlank()
            )
        }

        assertFailsWith<IllegalArgumentException> { VerifiedSourceMapCatalog.parse("bad id", emptyList()) }
        assertFailsWith<IllegalArgumentException> {
            VerifiedSourceMapCatalog.parse(
                "build",
                List(129) { VerifiedSourceMapAsset("https://app.test/$it.js", "build", "{}") },
            )
        }
        val validMap = """{"version":3,"sources":["App.kt"],"mappings":"AAAA"}"""
        assertFailsWith<IllegalArgumentException> {
            VerifiedSourceMapCatalog.parse("build", listOf(VerifiedSourceMapAsset("https://app.test/a.js", "other", validMap)))
        }
        assertFailsWith<IllegalArgumentException> {
            VerifiedSourceMapCatalog.parse("build", listOf(VerifiedSourceMapAsset("file:///a.js", "build", validMap)))
        }
        assertFailsWith<IllegalArgumentException> {
            VerifiedSourceMapCatalog.parse(
                "build",
                listOf(
                    VerifiedSourceMapAsset("https://app.test/a.js", "build", validMap),
                    VerifiedSourceMapAsset("https://app.test/a.js", "build", validMap),
                ),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            VerifiedSourceMapCatalog.parse(
                "build",
                listOf(
                    VerifiedSourceMapAsset(
                        "https://app.test/a.js",
                        "build",
                        "x".repeat(VerifiedSourceMapCatalog.MAX_SOURCE_MAP_UTF8_BYTES + 1),
                    )
                ),
            )
        }
        val optionalNameMap = """{"version":3,"sources":["App.kt"],"mappings":"AAAAA"}"""
        assertEquals(
            1,
            VerifiedSourceMapCatalog.parse(
                "build",
                listOf(VerifiedSourceMapAsset("http://app.test/named.js", "build", optionalNameMap)),
            ).map(GeneratedSourceLocation("http://app.test/named.js", 1, 1))?.line,
        )
        val negativeOriginal = """{"version":3,"sources":["App.kt"],"mappings":"AADA"}"""
        assertNull(
            VerifiedSourceMapCatalog.parse(
                "build",
                listOf(VerifiedSourceMapAsset("https://app.test/negative.js", "build", negativeOriginal)),
            ).map(GeneratedSourceLocation("https://app.test/negative.js", 1, 1))
        )

        val location = MappedSourceLocation("src/App.kt", 1, 1, "build")
        val policy = DevelopmentSourceLinkPolicy("idea", "/workspace", "https://viewer.test", "build")
        assertNull(policy.editorLink(location.copy(buildId = "other")))
        assertNull(policy.viewerLink(location.copy(buildId = "other")))
        assertNull(policy.copy(editorProtocol = null).editorLink(location))
        listOf("", "1idea", "a".repeat(33), "http", "https", "javascript", "data", "file").forEach {
            assertNull(policy.copy(editorProtocol = it).editorLink(location))
        }
        assertNull(policy.copy(workspaceRoot = null).editorLink(location))
        assertNull(policy.copy(viewerOrigin = null).viewerLink(location))
        listOf(0, 10_000_001).forEach { invalid ->
            assertNull(policy.editorLink(location.copy(line = invalid)))
            assertNull(policy.viewerLink(location.copy(column = invalid)))
        }
        listOf("", "bad\\path", "bad:path", "bad?path", "bad#path", "a//b", "../b").forEach { source ->
            assertNull(policy.editorLink(location.copy(source = source)))
            assertNull(policy.viewerLink(location.copy(source = source)))
        }
    }

}
