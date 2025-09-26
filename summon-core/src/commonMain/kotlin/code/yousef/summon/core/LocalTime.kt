package code.yousef.summon.core

import code.yousef.summon.core.error.ErrorHandler
import code.yousef.summon.core.error.requireInRange

// Commented out to avoid name conflict with the custom LocalTime class
// import kotlinx.datetime.LocalTime

/**
 * A simplified LocalTime implementation for multiplatform use.
 * This is a placeholder that can be replaced with kotlinx-datetime implementation
 * once project dependencies are properly set up.
 */
data class LocalTime(val hour: Int, val minute: Int, val second: Int = 0) {
    init {
        hour.requireInRange(0..23) { ErrorHandler.Messages.outOfRange("Hour", hour, 0, 23) }
        minute.requireInRange(0..59) { ErrorHandler.Messages.outOfRange("Minute", minute, 0, 59) }
        second.requireInRange(0..59) { ErrorHandler.Messages.outOfRange("Second", second, 0, 59) }
    }

    override fun toString(): String {
        val hourStr = if (hour < 10) "0$hour" else "$hour"
        val minuteStr = if (minute < 10) "0$minute" else "$minute"
        val secondStr = if (second < 10) "0$second" else "$second"
        return "$hourStr:$minuteStr:$secondStr"
    }

    companion object {
        /**
         * Parses a time from a string using ISO format (HH:mm:ss or HH:mm).
         */
        fun parse(value: String): LocalTime {
            val parts = value.splitCompat(":")
            parts.size.requireInRange(2..3) { "Invalid time format, expected HH:mm[:ss]" }
            return LocalTime(
                hour = parts[0].toInt(),
                minute = parts[1].toInt(),
                second = if (parts.size > 2) parts[2].toInt() else 0
            )
        }
    }

    /**
     * Formats this time as a String using ISO format (HH:mm:ss).
     */
    fun format(): String {
        val hourStr = if (hour < 10) "0$hour" else "$hour"
        val minuteStr = if (minute < 10) "0$minute" else "$minute"
        val secondStr = if (second < 10) "0$second" else "$second"
        return "$hourStr:$minuteStr:$secondStr"
    }

    /**
     * Formats this time as a String using the specified pattern.
     * Supported format specifiers:
     * - HH: hour of day (00-23)
     * - H: hour of day (0-23)
     * - mm: minute of hour (00-59)
     * - m: minute of hour (0-59)
     * - ss: second of minute (00-59)
     * - s: second of minute (0-59)
     */
    fun format(pattern: String): String {
        var result = pattern
        result = result.replace("HH", hour.toString().padStart(2, '0'))
        result = result.replace("H", hour.toString())
        result = result.replace("mm", minute.toString().padStart(2, '0'))
        result = result.replace("m", minute.toString())
        result = result.replace("ss", second.toString().padStart(2, '0'))
        result = result.replace("s", second.toString())
        return result
    }

    /**
     * Checks if this time is before the specified time.
     */
    fun isBefore(other: LocalTime): Boolean {
        if (hour < other.hour) return true
        if (hour > other.hour) return false
        if (minute < other.minute) return true
        if (minute > other.minute) return false
        return second < other.second
    }

    /**
     * Checks if this time is after the specified time.
     */
    fun isAfter(other: LocalTime): Boolean {
        if (hour > other.hour) return true
        if (hour < other.hour) return false
        if (minute > other.minute) return true
        if (minute < other.minute) return false
        return second > other.second
    }
} 
