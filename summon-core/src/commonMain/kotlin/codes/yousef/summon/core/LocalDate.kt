package codes.yousef.summon.core

// Import kotlinx.datetime for LocalDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * Extension functions for kotlinx.datetime.LocalDate
 * These provide additional functionality on top of the standard LocalDate.
 */

/**
 * Formats this date as a String using the specified pattern.
 * Supported format specifiers:
 * - yyyy: year
 * - MM: month (2 digits)
 * - M: month (1-2 digits)
 * - dd: day (2 digits)
 * - d: day (1-2 digits)
 */
fun LocalDate.format(pattern: String): String {
    var result = pattern
    result = result.replace("yyyy", year.toString().padStart(4, '0'))
    result = result.replace("MM", month.number.toString().padStart(2, '0'))
    result = result.replace("M", month.number.toString())
    result = result.replace("dd", day.toString().padStart(2, '0'))
    result = result.replace("d", day.toString())
    return result
}

/**
 * Formats this date as a String using ISO format (yyyy-MM-dd).
 */
fun LocalDate.formatIso(): String {
    return toString() // Already in ISO format
}

/**
 * Parses a date from a string using ISO format (yyyy-MM-dd).
 */
fun parseLocalDate(value: String): LocalDate {
    return LocalDate.parse(value)
}

/**
 * Checks if this date is before the specified date.
 */
fun LocalDate.isBefore(other: LocalDate): Boolean {
    return compareTo(other) < 0
}

/**
 * Checks if this date is after the specified date.
 */
fun LocalDate.isAfter(other: LocalDate): Boolean {
    return compareTo(other) > 0
} 