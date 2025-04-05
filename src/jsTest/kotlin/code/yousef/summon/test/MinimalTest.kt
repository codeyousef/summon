package code.yousef.summon.test

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A minimal test that doesn't involve any component rendering,
 * to test if the basic testing infrastructure works
 */
class MinimalTest {
    @Test
    fun testBasicString() {
        val expected = "test"
        val actual = "test"
        assertEquals(expected, actual)
    }

    @Test
    fun testBasicNumber() {
        val expected = 42
        val actual = 42
        assertEquals(expected, actual)
    }
} 