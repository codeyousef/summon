package code.yousef.summon.core


/**
 * A date without a time-zone in the ISO-8601 calendar system, such as 2023-08-15.
 */
data class LocalDate(val year: Int, val month: Int, val day: Int) {
    init {
        require(month in 1..12) { "Month must be between 1 and 12" }
        require(day in 1..31) { "Day must be between 1 and 31" }
    }

    companion object {
        /**
         * Obtains the current date from the system clock in the default time-zone.
         */
        fun now(): LocalDate {
            val date = Date()
            return LocalDate(
                year = date.getFullYear(),
                month = date.getMonth() + 1, // JS months are 0-based
                day = date.getDate()
            )
        }

        /**
         * Parses a date from a string using ISO format (yyyy-MM-dd).
         */
        fun parse(dateString: String): LocalDate {
            val parts = dateString.split("-")
            require(parts.size == 3) { "Date string must be in format yyyy-MM-dd" }
            return LocalDate(
                year = parts[0].toInt(),
                month = parts[1].toInt(),
                day = parts[2].toInt()
            )
        }
    }

    /**
     * Formats this date as a String using ISO format (yyyy-MM-dd).
     */
    fun format(): String {
        return String.format("%04d-%02d-%02d", year, month, day)
    }

    /**
     * Formats this date as a String using the specified format.
     * Supported format specifiers:
     * - yyyy: year
     * - MM: month (2 digits)
     * - M: month (1-2 digits)
     * - dd: day (2 digits)
     * - d: day (1-2 digits)
     */
    fun format(pattern: String): String {
        var result = pattern
        result = result.replace("yyyy", year.toString().padStart(4, '0'))
        result = result.replace("MM", month.toString().padStart(2, '0'))
        result = result.replace("M", month.toString())
        result = result.replace("dd", day.toString().padStart(2, '0'))
        result = result.replace("d", day.toString())
        return result
    }

    /**
     * Checks if this date is before the specified date.
     */
    fun isBefore(other: LocalDate): Boolean {
        if (year < other.year) return true
        if (year > other.year) return false
        if (month < other.month) return true
        if (month > other.month) return false
        return day < other.day
    }

    /**
     * Checks if this date is after the specified date.
     */
    fun isAfter(other: LocalDate): Boolean {
        if (year > other.year) return true
        if (year < other.year) return false
        if (month > other.month) return true
        if (month < other.month) return false
        return day > other.day
    }
} 