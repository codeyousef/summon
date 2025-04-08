package code.yousef.summon.core

/**
 * A simplified LocalDate implementation for multiplatform use.
 * This is a placeholder that can be replaced with kotlinx-datetime implementation
 * once project dependencies are properly set up.
 */
data class LocalDate(val year: Int, val month: Int, val day: Int) {
    init {
        require(year >= 0) { "Year must be non-negative" }
        require(month in 1..12) { "Month must be between 1 and 12" }
        require(day in 1..31) { "Day must be between 1 and 31" }
    }

    override fun toString(): String {
        val monthStr = if (month < 10) "0$month" else "$month"
        val dayStr = if (day < 10) "0$day" else "$day"
        return "$year-$monthStr-$dayStr"
    }

    companion object {
        /**
         * Parses a date from a string using ISO format (yyyy-MM-dd).
         */
        fun parse(value: String): LocalDate {
            val parts = value.split("-")
            require(parts.size == 3) { "Invalid date format, expected yyyy-MM-dd" }
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
        val monthStr = if (month < 10) "0$month" else "$month"
        val dayStr = if (day < 10) "0$day" else "$day"
        return "$year-$monthStr-$dayStr"
    }

    /**
     * Formats this date as a String using the specified pattern.
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