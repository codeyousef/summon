package codes.yousef.summon.core

// Use the LocalTime class from this package, not from kotlinx.datetime
// import kotlinx.datetime.LocalTime

import kotlin.test.*

class LocalTimeTest {

    @Test
    fun testConstructorAndValidation() {
        // Valid cases
        val time1 = LocalTime(12, 30, 45)
        assertEquals(12, time1.hour, "Hour should be set correctly")
        assertEquals(30, time1.minute, "Minute should be set correctly")
        assertEquals(45, time1.second, "Second should be set correctly")

        val time2 = LocalTime(0, 0, 0)
        assertEquals(0, time2.hour, "Minimum values should be accepted")

        val time3 = LocalTime(23, 59, 59)
        assertEquals(23, time3.hour, "Maximum values should be accepted")

        val time4 = LocalTime(12, 30) // Default second
        assertEquals(0, time4.second, "Second should default to 0")

        // Invalid cases
        assertFailsWith<IllegalArgumentException>("Hour below range should throw exception") {
            LocalTime(-1, 30, 45)
        }

        assertFailsWith<IllegalArgumentException>("Hour above range should throw exception") {
            LocalTime(24, 30, 45)
        }

        assertFailsWith<IllegalArgumentException>("Minute below range should throw exception") {
            LocalTime(12, -1, 45)
        }

        assertFailsWith<IllegalArgumentException>("Minute above range should throw exception") {
            LocalTime(12, 60, 45)
        }

        assertFailsWith<IllegalArgumentException>("Second below range should throw exception") {
            LocalTime(12, 30, -1)
        }

        assertFailsWith<IllegalArgumentException>("Second above range should throw exception") {
            LocalTime(12, 30, 60)
        }
    }

    @Test
    fun testToString() {
        // Test standard time
        val time1 = LocalTime(12, 30, 45)
        assertEquals("12:30:45", time1.toString(), "Standard time should format correctly")

        // Test padding with leading zeros
        val time2 = LocalTime(1, 2, 3)
        assertEquals("01:02:03", time2.toString(), "Time should be padded with leading zeros")

        // Test edge cases
        val time3 = LocalTime(0, 0, 0)
        assertEquals("00:00:00", time3.toString(), "Midnight should format correctly")

        val time4 = LocalTime(23, 59, 59)
        assertEquals("23:59:59", time4.toString(), "End of day should format correctly")
    }

    @Test
    fun testParse() {
        // Test standard time with seconds
        val time1 = LocalTime.parse("12:30:45")
        assertEquals(12, time1.hour, "Hour should be parsed correctly")
        assertEquals(30, time1.minute, "Minute should be parsed correctly")
        assertEquals(45, time1.second, "Second should be parsed correctly")

        // Test time without seconds
        val time2 = LocalTime.parse("12:30")
        assertEquals(12, time2.hour, "Hour should be parsed correctly")
        assertEquals(30, time2.minute, "Minute should be parsed correctly")
        assertEquals(0, time2.second, "Second should default to 0")

        // Test edge cases
        val time3 = LocalTime.parse("00:00:00")
        assertEquals(0, time3.hour, "Midnight should parse correctly")

        val time4 = LocalTime.parse("23:59:59")
        assertEquals(23, time4.hour, "End of day should parse correctly")

        // Invalid cases
        assertFailsWith<IllegalArgumentException>("Invalid format should throw exception") {
            LocalTime.parse("12-30-45")
        }

        assertFailsWith<IllegalArgumentException>("Too few parts should throw exception") {
            LocalTime.parse("12")
        }

        assertFailsWith<IllegalArgumentException>("Too many parts should throw exception") {
            LocalTime.parse("12:30:45:00")
        }

        assertFailsWith<NumberFormatException>("Non-numeric values should throw exception") {
            LocalTime.parse("ab:cd:ef")
        }

        assertFailsWith<IllegalArgumentException>("Out of range values should throw exception") {
            LocalTime.parse("24:00:00")
        }
    }

    @Test
    fun testFormatNoArgs() {
        // Test standard time
        val time1 = LocalTime(12, 30, 45)
        assertEquals("12:30:45", time1.format(), "Standard time should format correctly")

        // Test padding with leading zeros
        val time2 = LocalTime(1, 2, 3)
        assertEquals("01:02:03", time2.format(), "Time should be padded with leading zeros")

        // Test edge cases
        val time3 = LocalTime(0, 0, 0)
        assertEquals("00:00:00", time3.format(), "Midnight should format correctly")

        val time4 = LocalTime(23, 59, 59)
        assertEquals("23:59:59", time4.format(), "End of day should format correctly")
    }

    @Test
    fun testFormatWithPattern() {
        val time = LocalTime(12, 30, 45)

        // Test full pattern
        assertEquals("12:30:45", time.format("HH:mm:ss"), "Full pattern should format correctly")

        // Test individual components
        assertEquals("12", time.format("HH"), "Hour with leading zero should format correctly")
        assertEquals("12", time.format("H"), "Hour without leading zero should format correctly")
        assertEquals("30", time.format("mm"), "Minute with leading zero should format correctly")
        assertEquals("30", time.format("m"), "Minute without leading zero should format correctly")
        assertEquals("45", time.format("ss"), "Second with leading zero should format correctly")
        assertEquals("45", time.format("s"), "Second without leading zero should format correctly")

        // Test simple mixed pattern
        assertEquals("12:30:45", time.format("HH:mm:ss"), "Simple mixed pattern should format correctly")

        // Test edge cases
        val edgeTime = LocalTime(1, 2, 3)
        assertEquals("01:02:03", edgeTime.format("HH:mm:ss"), "Single digit values should be padded with zeros")
        assertEquals("1:2:3", edgeTime.format("H:m:s"), "Single digit values without padding")
    }

    @Test
    fun testIsBefore() {
        val time1 = LocalTime(12, 30, 45)
        val time2 = LocalTime(12, 30, 46)
        val time3 = LocalTime(12, 31, 45)
        val time4 = LocalTime(13, 30, 45)
        val sameTime = LocalTime(12, 30, 45)

        // Valid cases
        assertTrue(time1.isBefore(time2), "Earlier time should be before later time (same hour, minute)")
        assertTrue(time1.isBefore(time3), "Earlier time should be before later time (same hour)")
        assertTrue(time1.isBefore(time4), "Earlier time should be before later time (different hour)")

        // Invalid cases
        assertFalse(time2.isBefore(time1), "Later time should not be before earlier time")
        assertFalse(time1.isBefore(sameTime), "Same time should not be before itself")
    }

    @Test
    fun testIsAfter() {
        val time1 = LocalTime(12, 30, 45)
        val time2 = LocalTime(12, 30, 46)
        val time3 = LocalTime(12, 31, 45)
        val time4 = LocalTime(13, 30, 45)
        val sameTime = LocalTime(12, 30, 45)

        // Valid cases
        assertTrue(time2.isAfter(time1), "Later time should be after earlier time (same hour, minute)")
        assertTrue(time3.isAfter(time1), "Later time should be after earlier time (same hour)")
        assertTrue(time4.isAfter(time1), "Later time should be after earlier time (different hour)")

        // Invalid cases
        assertFalse(time1.isAfter(time2), "Earlier time should not be after later time")
        assertFalse(time1.isAfter(sameTime), "Same time should not be after itself")
    }
}
