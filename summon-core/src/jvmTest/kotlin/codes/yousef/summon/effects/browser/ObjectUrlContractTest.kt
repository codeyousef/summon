package codes.yousef.summon.effects.browser

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class ObjectUrlContractTest {
    @Test
    fun validationCapabilityAndIntegrityPrecedePlatformAllocation() = runTest {
        val bytes = byteArrayOf(1, 2, 3)
        listOf("", "x".repeat(128), "image/png\nprivate").forEach { mime ->
            assertFailsWith<IllegalArgumentException> {
                createVerifiedObjectUrl(bytes, mime, verifyIntegrity = { true })
            }
        }

        var integrityChecks = 0
        val unsupported = createVerifiedObjectUrl(
            bytes,
            "video/unknown",
            isMimeTypeSupported = { false },
            verifyIntegrity = { integrityChecks++; true }
        )
        assertEquals(MediaCapabilityState.Unsupported("video/unknown"), unsupported)
        assertEquals(0, integrityChecks)

        val failed = createVerifiedObjectUrl(bytes, "image/png", verifyIntegrity = { false })
        assertIs<MediaCapabilityState.IntegrityFailed>(failed)

        assertFailsWith<ObjectUrlException> {
            createVerifiedObjectUrl(bytes, "image/png", verifyIntegrity = { true })
        }
    }
}
