package code.yousef.summon.core


/**
 * A time without a time-zone in the ISO-8601 calendar system, such as 10:15:30.
 */
data class LocalTime(val hour: Int, val minute: Int, val second: Int = 0) {
    init {
        require(hour in 0..23) { "Hour must be between 0 and 23" }
        require(minute in 0..59) { "Minute must be between 0 and 59" }
        require(second in 0..59) { "Second must be between 0 and 59" }
    }

    companion object {
        /**
         * Obtains the current time from the system clock in the default time-zone.
         */
        fun now(): LocalTime {
            val date = Date()
            return LocalTime(
                hour = date.getHours(),
                minute = date.getMinutes(),
                second = date.getSeconds()
            )
        }

        /**
         * Parses a time from a string using 24-hour format (HH:mm:ss or HH:mm).
         */
        fun parse(timeString: String): LocalTime {
            val parts = timeString.split(":")
            require(parts.size in 2..3) { "Time string must be in format HH:mm or HH:mm:ss" }
            
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            val second = if (parts.size > 2) parts[2].toInt() else 0
            
            return LocalTime(hour, minute, second)
        }
    }

    /**
     * Formats this time as a String using 24-hour format (HH:mm:ss).
     */
    fun format24Hour(): String {
        return String.format("%02d:%02d:%02d", hour, minute, second)
    }

    /**
     * Formats this time as a String using 12-hour format (hh:mm:ss a).
     */
    fun format12Hour(): String {
        val h = if (hour % 12 == 0) 12 else hour % 12
        val ampm = if (hour < 12) "AM" else "PM"
        return String.format("%02d:%02d:%02d %s", h, minute, second, ampm)
    }

    /**
     * Returns a LocalTime with the same hour and minute as this time,
     * but with the seconds set to the specified value.
     */
    fun withSecond(second: Int): LocalTime {
        return copy(second = second)
    }

    /**
     * Returns a LocalTime with the same hour and second as this time,
     * but with the minutes set to the specified value.
     */
    fun withMinute(minute: Int): LocalTime {
        return copy(minute = minute)
    }

    /**
     * Returns a LocalTime with the same minute and second as this time,
     * but with the hours set to the specified value.
     */
    fun withHour(hour: Int): LocalTime {
        return copy(hour = hour)
    }
} 