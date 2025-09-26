package code.yousef.summon.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Tests for PlatformTarget sealed class.
 *
 * These tests verify that the PlatformTarget abstraction correctly
 * represents different compilation targets with type safety.
 */
class PlatformTargetTest {

    @Test
    fun `platform targets should be distinct sealed class instances`() {
        val jvm = PlatformTarget.JVM
        val js = PlatformTarget.JavaScript
        val wasm = PlatformTarget.WebAssembly

        // Each target should be distinct
        assertNotEquals<PlatformTarget>(jvm, js, "JVM and JavaScript targets should be different")
        assertNotEquals<PlatformTarget>(js, wasm, "JavaScript and WebAssembly targets should be different")
        assertNotEquals<PlatformTarget>(jvm, wasm, "JVM and WebAssembly targets should be different")
    }

    @Test
    fun `platform targets should have consistent identity`() {
        // Same target instances should be equal
        assertEquals(PlatformTarget.JVM, PlatformTarget.JVM, "JVM target should equal itself")
        assertEquals(PlatformTarget.JavaScript, PlatformTarget.JavaScript, "JS target should equal itself")
        assertEquals(PlatformTarget.WebAssembly, PlatformTarget.WebAssembly, "WASM target should equal itself")
    }

    @Test
    fun `platform targets should support when expressions`() {
        val targets = listOf(
            PlatformTarget.JVM,
            PlatformTarget.JavaScript,
            PlatformTarget.WebAssembly
        )

        targets.forEach { target ->
            val targetName = when (target) {
                is PlatformTarget.JVM -> "JVM"
                is PlatformTarget.JavaScript -> "JavaScript"
                is PlatformTarget.WebAssembly -> "WebAssembly"
            }

            assertTrue(targetName.isNotEmpty(), "Target name should not be empty")
        }
    }

    @Test
    fun `browser targets should be identifiable`() {
        val browserTargets = listOf(
            PlatformTarget.JavaScript,
            PlatformTarget.WebAssembly
        )

        browserTargets.forEach { target ->
            val isBrowserTarget = when (target) {
                is PlatformTarget.JVM -> false
                is PlatformTarget.JavaScript -> true
                is PlatformTarget.WebAssembly -> true
            }

            assertTrue(isBrowserTarget, "$target should be identified as browser target")
        }
    }

    @Test
    fun `server target should be identifiable`() {
        val serverTarget = PlatformTarget.JVM

        @Suppress("SENSELESS_COMPARISON", "USELESS_IS_CHECK")
        val isServerTarget = when (serverTarget) {
            is PlatformTarget.JVM -> true
            is PlatformTarget.JavaScript -> false
            is PlatformTarget.WebAssembly -> false
        }

        assertTrue(isServerTarget, "JVM should be identified as server target")
    }

    @Test
    fun `platform target capabilities should be deterministic`() {
        // Test DOM capabilities
        fun hasDOMCapabilities(target: PlatformTarget): Boolean = when (target) {
            is PlatformTarget.JVM -> false
            is PlatformTarget.JavaScript -> true
            is PlatformTarget.WebAssembly -> true
        }

        // Test SSR capabilities
        fun hasSSRCapabilities(target: PlatformTarget): Boolean = when (target) {
            is PlatformTarget.JVM -> true
            is PlatformTarget.JavaScript -> false
            is PlatformTarget.WebAssembly -> false
        }

        assertTrue(hasDOMCapabilities(PlatformTarget.JavaScript), "JS should have DOM capabilities")
        assertTrue(hasDOMCapabilities(PlatformTarget.WebAssembly), "WASM should have DOM capabilities")
        assertTrue(!hasDOMCapabilities(PlatformTarget.JVM), "JVM should not have DOM capabilities")

        assertTrue(hasSSRCapabilities(PlatformTarget.JVM), "JVM should have SSR capabilities")
        assertTrue(!hasSSRCapabilities(PlatformTarget.JavaScript), "JS should not have SSR capabilities")
        assertTrue(!hasSSRCapabilities(PlatformTarget.WebAssembly), "WASM should not have SSR capabilities")
    }

    @Test
    fun `platform target toString should be meaningful`() {
        val targets = listOf(
            PlatformTarget.JVM,
            PlatformTarget.JavaScript,
            PlatformTarget.WebAssembly
        )

        targets.forEach { target ->
            val stringRepresentation = target.toString()
            assertTrue(stringRepresentation.isNotEmpty(), "toString should not be empty")
            assertTrue(
                stringRepresentation.contains(target::class.simpleName ?: ""),
                "toString should contain class name"
            )
        }
    }
}

