package codes.yousef.summon.cli.util

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersionReaderContractTest {
    @AfterTest
    fun clearCache() = VersionReader.clearCache()

    @Test
    fun packagedVersionIsSemanticAndStableAcrossCachedReads() {
        VersionReader.clearCache()
        val first = VersionReader.readVersion()
        assertTrue(VersionReader.isValidSemver(first))
        assertEquals(first, VersionReader.readVersion())
    }

    @Test
    fun semanticVersionValidationCoversReleaseQualifierAndInvalidShapes() {
        assertTrue(VersionReader.isValidSemver("1.2.3"))
        assertTrue(VersionReader.isValidSemver("1.2.3.4"))
        assertTrue(VersionReader.isValidSemver("1.2.3-rc.1"))
        assertFalse(VersionReader.isValidSemver("1.2"))
        assertFalse(VersionReader.isValidSemver("v1.2.3"))
        assertFalse(VersionReader.isValidSemver("1.2.3-"))
    }
}
