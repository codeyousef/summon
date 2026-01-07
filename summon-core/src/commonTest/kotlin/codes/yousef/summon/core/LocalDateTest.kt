package codes.yousef.summon.core

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlin.test.*

class LocalDateTest {

    @Test
    fun testFormat() {
        val date = LocalDate(2023, 4, 15)

        // Test full pattern
        assertEquals("2023-04-15", date.format("yyyy-MM-dd"), "Full pattern should format correctly")

        // Test individual components
        assertEquals("2023", date.format("yyyy"), "Year should format correctly")
        assertEquals("04", date.format("MM"), "Month with leading zero should format correctly")
        assertEquals("4", date.format("M"), "Month without leading zero should format correctly")
        assertEquals("15", date.format("dd"), "Day with leading zero should format correctly")
        assertEquals("15", date.format("d"), "Day without leading zero should format correctly")

        // Test mixed patterns
        assertEquals("15/04/2023", date.format("d/MM/yyyy"), "Mixed pattern should format correctly")
        // Note: The format function doesn't support text month formats like "MMM"

        // Test edge cases
        val edgeDate = LocalDate(2, 1, 1)
        assertEquals("0002-01-01", edgeDate.format("yyyy-MM-dd"), "Early year should be padded with zeros")

        val singleDigitDate = LocalDate(2023, 1, 2)
        assertEquals("2023-01-02", singleDigitDate.format("yyyy-MM-dd"), "Single digit month and day should be padded")
        assertEquals("2023-1-2", singleDigitDate.format("yyyy-M-d"), "Single digit month and day without padding")
    }

    @Test
    fun testFormatIso() {
        val date = LocalDate(2023, 4, 15)
        assertEquals("2023-04-15", date.formatIso(), "ISO format should match expected pattern")

        // Edge cases
        val edgeDate = LocalDate(2, 1, 1)
        assertEquals("0002-01-01", edgeDate.formatIso(), "Early year should be formatted correctly in ISO format")
    }

    @Test
    fun testParseLocalDate() {
        // Valid cases
        val date = parseLocalDate("2023-04-15")
        assertEquals(2023, date.year, "Year should be parsed correctly")
        assertEquals(4, date.month.number, "Month should be parsed correctly")
        assertEquals(15, date.day, "Day should be parsed correctly")

        // Edge cases
        val edgeDate = parseLocalDate("0002-01-01")
        assertEquals(2, edgeDate.year, "Early year should be parsed correctly")

        // Invalid cases
        assertFailsWith<IllegalArgumentException>("Invalid format should throw exception") {
            parseLocalDate("15/04/2023")
        }

        assertFailsWith<IllegalArgumentException>("Invalid date should throw exception") {
            parseLocalDate("2023-13-45")
        }
    }

    @Test
    fun testIsBefore() {
        val date1 = LocalDate(2023, 4, 15)
        val date2 = LocalDate(2023, 4, 16)
        val date3 = LocalDate(2023, 5, 1)
        val date4 = LocalDate(2024, 1, 1)
        val sameDate = LocalDate(2023, 4, 15)

        // Valid cases
        assertTrue(date1.isBefore(date2), "Earlier date should be before later date (same month)")
        assertTrue(date1.isBefore(date3), "Earlier date should be before later date (different month)")
        assertTrue(date1.isBefore(date4), "Earlier date should be before later date (different year)")

        // Invalid cases
        assertFalse(date2.isBefore(date1), "Later date should not be before earlier date")
        assertFalse(date1.isBefore(sameDate), "Same date should not be before itself")
    }

    @Test
    fun testIsAfter() {
        val date1 = LocalDate(2023, 4, 15)
        val date2 = LocalDate(2023, 4, 16)
        val date3 = LocalDate(2023, 5, 1)
        val date4 = LocalDate(2024, 1, 1)
        val sameDate = LocalDate(2023, 4, 15)

        // Valid cases
        assertTrue(date2.isAfter(date1), "Later date should be after earlier date (same month)")
        assertTrue(date3.isAfter(date1), "Later date should be after earlier date (different month)")
        assertTrue(date4.isAfter(date1), "Later date should be after earlier date (different year)")

        // Invalid cases
        assertFalse(date1.isAfter(date2), "Earlier date should not be after later date")
        assertFalse(date1.isAfter(sameDate), "Same date should not be after itself")
    }
}
