package codes.yousef.summon.core

import kotlin.math.E
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class WasmCompatContractTest {
    @Test
    fun stringAndCollectionCompatibilityMatchesCommonOperations() {
        assertEquals("abc-ä", "ABC-ä".toLowerCaseCompat())
        assertEquals(listOf(""), "".splitCompat(","))
        assertEquals(listOf("a", "", "b", ""), "a,,b,".splitCompat(","))
        assertEquals(listOf("a", "b,c"), "a,b,c".splitCompat(',', 2))
        assertEquals(listOf("a", "b", "c"), "a,b,c".splitCompat(',', 0))
        assertEquals(listOf("abc"), "abc".splitCompat(",", 1))
        assertFailsWith<IllegalArgumentException> { "abc".splitCompat("") }
        assertFailsWith<IllegalArgumentException> { "abc".splitCompat("", 2) }

        assertEquals(mapOf("a" to 2, "b" to 3), mapOfCompat(pairOf("a", 1), pairOf("a", 2), pairOf("b", 3)))
        assertEquals("x!7null", StringBuilderCompat().append("x").append('!').append(7).append(null).toString())
        assertEquals("one\n  two\n\nthree", "  one\n    two\n\n  three".trimIndentCompat())
        assertEquals("\n", " \n\t".trimIndentCompat())

        val seen = mutableListOf<Int>()
        listOf(1, 2, 3).forEachCompat { seen += it }
        assertEquals(listOf(1, 2, 3), seen)
        assertEquals(listOf(1, 2), seen.takeCompat(2))
        assertEquals(emptyList(), seen.takeCompat(0))
        assertEquals(listOf(3), seen.dropCompat(2))
        assertEquals(emptyList(), seen.dropCompat(4))
        assertEquals(listOf(2, 4, 6), seen.mapCompat { it * 2 })
        assertEquals(listOf(1, 3), seen.filterCompat { it % 2 == 1 })
    }

    @Test
    fun mathAndTimeCompatibilityCoversNumericTypesAndUnits() {
        assertTrue(kotlin.math.abs(MathCompat.exp(1.0) - E) < 0.000001)
        assertTrue(kotlin.math.abs(MathCompat.exp(1.0f) - E.toFloat()) < 0.000001f)
        assertEquals(2.0, MathCompat.abs(-2.0)); assertEquals(2.0, MathCompat.abs(2.0))
        assertEquals(2.0f, MathCompat.abs(-2.0f)); assertEquals(2.0f, MathCompat.abs(2.0f))
        assertEquals(2, MathCompat.abs(-2)); assertEquals(2, MathCompat.abs(2))
        assertEquals(2.0, MathCompat.max(2.0, 1.0)); assertEquals(2.0, MathCompat.max(1.0, 2.0))
        assertEquals(2.0f, MathCompat.max(2.0f, 1.0f)); assertEquals(2.0f, MathCompat.max(1.0f, 2.0f))
        assertEquals(2, MathCompat.max(2, 1)); assertEquals(2, MathCompat.max(1, 2))
        assertEquals(1.0, MathCompat.min(1.0, 2.0)); assertEquals(1.0, MathCompat.min(2.0, 1.0))
        assertEquals(1.0f, MathCompat.min(1.0f, 2.0f)); assertEquals(1.0f, MathCompat.min(2.0f, 1.0f))
        assertEquals(1, MathCompat.min(1, 2)); assertEquals(1, MathCompat.min(2, 1))

        assertEquals(1.5, TimeCompat.milliseconds(1_500).toSeconds())
        assertEquals(2.0, TimeCompat.seconds(120).toMinutes())
        assertEquals(2.0, TimeCompat.minutes(120).toHours())
        assertEquals(3_600_000L, TimeCompat.hours(1).milliseconds)
    }
}
