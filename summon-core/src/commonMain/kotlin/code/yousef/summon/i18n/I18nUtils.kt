package code.yousef.summon.i18n

import code.yousef.summon.runtime.Composable
import kotlin.math.abs

/**
 * Utility functions for internationalization
 */
object I18nUtils {
    /**
     * Format a message with placeholders
     *
     * @param message The message template with {placeholder} syntax
     * @param params Map of placeholder names to values
     * @return The formatted message
     */
    fun formatMessage(message: String, params: Map<String, Any>): String {
        var result = message

        // Replace each placeholder with its value
        params.forEach { (key, value) ->
            result = result.replace("{$key}", value.toString())
        }

        return result
    }

    /**
     * Get a plural-aware translation
     *
     * @param count The count to determine plural form
     * @param zero Translation for zero items (optional)
     * @param one Translation for one item
     * @param few Translation for few items (used in some languages)
     * @param many Translation for many items (used in some languages)
     * @param other Translation for other counts
     * @return The appropriate translation based on count
     */
    fun pluralString(
        count: Int,
        zero: String? = null,
        one: String,
        few: String? = null,
        many: String? = null,
        other: String
    ): String {
        return when {
            count == 0 && zero != null -> formatMessage(zero, mapOf("count" to count))
            count == 1 -> formatMessage(one, mapOf("count" to count))
            count in 2..4 && few != null -> formatMessage(few, mapOf("count" to count))
            count >= 5 && count <= 20 && many != null -> formatMessage(many, mapOf("count" to count))
            else -> formatMessage(other, mapOf("count" to count))
        }
    }

    /**
     * Get a gender-aware translation
     *
     * @param gender The gender ("male", "female", or other)
     * @param male Translation for male gender
     * @param female Translation for female gender
     * @param other Translation for other genders
     * @return The appropriate translation based on gender
     */
    fun genderString(
        gender: String,
        male: String,
        female: String,
        other: String
    ): String {
        return when (gender.lowercase()) {
            "male" -> male
            "female" -> female
            else -> other
        }
    }

    /**
     * Format a number based on the current locale
     *
     * @param number The number to format
     * @param minimumFractionDigits Minimum number of fraction digits
     * @param maximumFractionDigits Maximum number of fraction digits
     * @return The formatted number as a string
     */
    @Composable
    fun formatNumber(
        number: Number,
        minimumFractionDigits: Int = 0,
        maximumFractionDigits: Int = 3
    ): String {
        // Get the current language
        val language = LocalLanguage.current
        val currentLanguage = (language as Function0<Language>).invoke()

        // Format the number based on the language
        // This is a simplified implementation; in a real app, we would use
        // platform-specific number formatting
        return formatNumberForLanguage(number, currentLanguage.code, minimumFractionDigits, maximumFractionDigits)
    }

    /**
     * Format a date based on the current locale
     *
     * @param timestamp The timestamp to format (milliseconds since epoch)
     * @param format The date format (short, medium, long, full)
     * @return The formatted date as a string
     */
    @Composable
    fun formatDate(
        timestamp: Long,
        format: DateFormat = DateFormat.MEDIUM
    ): String {
        // Get the current language
        val language = LocalLanguage.current
        val currentLanguage = (language as Function0<Language>).invoke()

        // Format the date based on the language
        // This is a simplified implementation; in a real app, we would use
        // platform-specific date formatting
        return formatDateForLanguage(timestamp, currentLanguage.code, format)
    }

    /**
     * Platform-specific implementation of number formatting
     */
    private fun formatNumberForLanguage(
        number: Number,
        languageCode: String,
        minimumFractionDigits: Int,
        maximumFractionDigits: Int
    ): String {
        // This is a simplified implementation; in a real app, we would use
        // platform-specific number formatting
        val value = number.toDouble()
        val intPart = value.toLong()
        val fracPart = abs(value - intPart)

        // Format the integer part with thousands separators
        val formattedIntPart = when (languageCode) {
            "en", "fr", "de", "it", "es" -> intPart.toString().reversed().chunked(3).joinToString(",").reversed()
            "ar" -> intPart.toString().reversed().chunked(3).joinToString("٬").reversed()
            else -> intPart.toString()
        }

        // Format the fractional part if needed
        return if (fracPart > 0 || minimumFractionDigits > 0) {
            val fracString = fracPart.toString().substring(2)
            val paddedFracString = fracString.padEnd(minimumFractionDigits, '0')
            val truncatedFracString = paddedFracString.take(maximumFractionDigits)

            // Use the appropriate decimal separator
            val decimalSeparator = when (languageCode) {
                "en" -> "."
                "fr", "it", "es" -> ","
                "de" -> ","
                "ar" -> "٫"
                else -> "."
            }

            "$formattedIntPart$decimalSeparator$truncatedFracString"
        } else {
            formattedIntPart
        }
    }

    /**
     * Platform-specific implementation of date formatting
     */
    private fun formatDateForLanguage(
        timestamp: Long,
        languageCode: String,
        format: DateFormat
    ): String {
        // This is a simplified implementation; in a real app, we would use
        // platform-specific date formatting
        // For now, we'll just return a simple ISO-like format using basic math

        // Calculate date components from timestamp
        val secondsTotal = timestamp / 1000
        val minutesTotal = secondsTotal / 60
        val hoursTotal = minutesTotal / 60
        val daysTotal = hoursTotal / 24

        // Start from Unix epoch (January 1, 1970)
        var year = 1970
        var month = 1
        var day = 1

        // Simple algorithm to calculate date (not accounting for leap years properly)
        var remainingDays = daysTotal.toInt()

        // Advance years
        while (remainingDays >= 365) {
            val daysInYear = if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 366 else 365
            if (remainingDays >= daysInYear) {
                year++
                remainingDays -= daysInYear
            } else {
                break
            }
        }

        // Advance months
        val daysInMonth = arrayOf(31, if (year % 4 == 0) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        while (month <= 12) {
            if (remainingDays >= daysInMonth[month - 1]) {
                remainingDays -= daysInMonth[month - 1]
                month++
            } else {
                break
            }
        }

        // Remaining days
        day += remainingDays

        // Calculate time components
        val hours = (hoursTotal % 24).toInt()
        val minutes = (minutesTotal % 60).toInt()
        val seconds = (secondsTotal % 60).toInt()

        // Format date and time parts
        val datePart = "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
        val timePart = "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${
            seconds.toString().padStart(2, '0')
        }"

        return when (format) {
            DateFormat.SHORT -> datePart
            DateFormat.MEDIUM -> "$datePart $timePart"
            DateFormat.LONG -> "$datePart $timePart UTC"
            DateFormat.FULL -> "$datePart $timePart UTC (${languageCode.uppercase()})"
        }
    }
}

/**
 * Date format options
 */
enum class DateFormat {
    SHORT, MEDIUM, LONG, FULL
}

/**
 * Utility function to format a message with placeholders
 *
 * @param key The translation key
 * @param params Map of placeholder names to values
 * @return The formatted message
 */
@Composable
fun stringResourceWithParams(key: String, params: Map<String, Any>): String {
    val baseString = stringResource(key)
    return I18nUtils.formatMessage(baseString, params)
}

/**
 * Utility function to get a plural-aware translation
 *
 * @param keyZero Translation key for zero items (optional)
 * @param keyOne Translation key for one item
 * @param keyFew Translation key for few items (optional)
 * @param keyMany Translation key for many items (optional)
 * @param keyOther Translation key for other counts
 * @param count The count to determine plural form
 * @return The appropriate translation based on count
 */
@Composable
fun pluralStringResource(
    keyZero: String? = null,
    keyOne: String,
    keyFew: String? = null,
    keyMany: String? = null,
    keyOther: String,
    count: Int
): String {
    val zero = keyZero?.let { stringResource(it) }
    val one = stringResource(keyOne)
    val few = keyFew?.let { stringResource(it) }
    val many = keyMany?.let { stringResource(it) }
    val other = stringResource(keyOther)

    return I18nUtils.pluralString(count, zero, one, few, many, other)
}

/**
 * Utility function to get a gender-aware translation
 *
 * @param keyMale Translation key for male gender
 * @param keyFemale Translation key for female gender
 * @param keyOther Translation key for other genders
 * @param gender The gender ("male", "female", or other)
 * @return The appropriate translation based on gender
 */
@Composable
fun genderStringResource(
    keyMale: String,
    keyFemale: String,
    keyOther: String,
    gender: String
): String {
    val male = stringResource(keyMale)
    val female = stringResource(keyFemale)
    val other = stringResource(keyOther)

    return I18nUtils.genderString(gender, male, female, other)
}

/**
 * Utility function to format a number based on the current locale
 *
 * @param number The number to format
 * @param minimumFractionDigits Minimum number of fraction digits
 * @param maximumFractionDigits Maximum number of fraction digits
 * @return The formatted number as a string
 */
@Composable
fun formatNumberResource(
    number: Number,
    minimumFractionDigits: Int = 0,
    maximumFractionDigits: Int = 3
): String {
    return I18nUtils.formatNumber(number, minimumFractionDigits, maximumFractionDigits)
}

/**
 * Utility function to format a date based on the current locale
 *
 * @param timestamp The timestamp to format (milliseconds since epoch)
 * @param format The date format (short, medium, long, full)
 * @return The formatted date as a string
 */
@Composable
fun formatDateResource(
    timestamp: Long,
    format: DateFormat = DateFormat.MEDIUM
): String {
    return I18nUtils.formatDate(timestamp, format)
}
