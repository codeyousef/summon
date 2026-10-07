package codes.yousef.summon.core

/**
 * Compatibility utilities for WASM platform limitations.
 * WASM has limited standard library support, so we provide alternatives here.
 */

/**
 * Convert string to lowercase.
 * Works around missing String.toLowerCaseCompat() in WASM.
 */
fun String.toLowerCaseCompat(): String {
    val result = CharArray(this.length)
    for (i in this.indices) {
        val ch = this[i]
        result[i] = when (ch) {
            in 'A'..'Z' -> (ch.code + 32).toChar()
            else -> ch
        }
    }
    return result.concatToString()
}

/**
 * Split string by delimiter.
 * Works around missing String.splitCompat() in WASM.
 */
fun String.splitCompat(delimiter: String): List<String> {
    require(delimiter.isNotEmpty()) { "Delimiter must not be empty" }
    val result = mutableListOf<String>()
    var currentIndex = 0

    while (true) {
        val nextIndex = indexOf(delimiter, currentIndex)
        if (nextIndex == -1) {
            result.add(substring(currentIndex))
            return result
        }
        result.add(substring(currentIndex, nextIndex))
        currentIndex = nextIndex + delimiter.length
    }
}

/**
 * Split string by delimiter with limit.
 * Works around missing String.split() with limit in WASM.
 */
fun String.splitCompat(delimiter: String, limit: Int): List<String> {
    require(delimiter.isNotEmpty()) { "Delimiter must not be empty" }
    if (limit <= 0) return splitCompat(delimiter)

    val result = mutableListOf<String>()
    var currentIndex = 0
    while (result.size < limit - 1) {
        val nextIndex = indexOf(delimiter, currentIndex)
        if (nextIndex == -1) break
        result.add(substring(currentIndex, nextIndex))
        currentIndex = nextIndex + delimiter.length
    }
    result.add(substring(currentIndex))
    return result
}

/**
 * Split string by character delimiter.
 * Works around missing String.split(Char) in WASM.
 */
fun String.splitCompat(delimiter: Char): List<String> {
    return splitCompat(delimiter.toString())
}

/**
 * Split string by character delimiter with limit.
 * Works around missing String.split(Char, limit) in WASM.
 */
fun String.splitCompat(delimiter: Char, limit: Int): List<String> {
    return splitCompat(delimiter.toString(), limit)
}

/**
 * Create a map from pairs.
 * Works around missing mapOfCompat() in WASM.
 */
fun <K, V> mapOfCompat(vararg pairs: Pair<K, V>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    for (pair in pairs) {
        map[pair.first] = pair.second
    }
    return map
}

/**
 * Create a pair.
 * Works around missing 'to' infix function in WASM.
 */
fun <A, B> pairOf(first: A, second: B): Pair<A, B> = Pair(first, second)

/**
 * StringBuilder alternative for WASM.
 */
class StringBuilderCompat {
    private val parts = mutableListOf<String>()

    /** Appends [value] without intermediate concatenation. */
    fun append(value: String): StringBuilderCompat {
        parts.add(value)
        return this
    }

    /** Appends [value] without intermediate concatenation. */
    fun append(value: Char): StringBuilderCompat {
        parts.add(value.toString())
        return this
    }

    /** Appends the string representation of [value], or `null`. */
    fun append(value: Any?): StringBuilderCompat {
        parts.add(value?.toString() ?: "null")
        return this
    }

    /** Joins all appended values. */
    override fun toString(): String {
        return parts.joinToString("")
    }
}

/**
 * Trim indentation from multiline string.
 * Works around missing trimIndent() in WASM.
 */
fun String.trimIndentCompat(): String {
    val lines = this.splitCompat("\n")

    // Find minimum indentation (excluding empty lines)
    var minIndent = Int.MAX_VALUE
    for (line in lines) {
        if (line.isNotBlank()) {
            var indent = 0
            for (ch in line) {
                if (ch == ' ' || ch == '\t') {
                    indent++
                } else {
                    break
                }
            }
            if (indent < minIndent) {
                minIndent = indent
            }
        }
    }

    if (minIndent == Int.MAX_VALUE) {
        minIndent = 0
    }

    // Remove the minimum indentation from each line
    val result = mutableListOf<String>()
    for (line in lines) {
        if (line.isBlank()) {
            result.add("")
        } else if (line.length > minIndent) {
            result.add(line.substring(minIndent))
        } else {
            result.add(line)
        }
    }

    return result.joinToString("\n")
}

/**
 * Math utilities for WASM.
 */
object MathCompat {
    /** Approximates $e^x$ with twenty Taylor-series terms. */
    fun exp(x: Double): Double {
        // Simple Taylor series approximation for e^x
        var sum = 1.0
        var term = 1.0
        for (i in 1..20) {
            term *= x / i
            sum += term
        }
        return sum
    }

    /** Float variant of [exp]. */
    fun exp(x: Float): Float = exp(x.toDouble()).toFloat()

    /** Absolute value of [x]. */
    fun abs(x: Double): Double = if (x < 0.0) -x else x
    /** Absolute value of [x]. */
    fun abs(x: Float): Float = if (x < 0.0f) -x else x
    /** Absolute value of [x]. */
    fun abs(x: Int): Int = if (x < 0) -x else x

    /** Greater of [a] and [b]. */
    fun max(a: Double, b: Double): Double = if (a > b) a else b
    /** Greater of [a] and [b]. */
    fun max(a: Float, b: Float): Float = if (a > b) a else b
    /** Greater of [a] and [b]. */
    fun max(a: Int, b: Int): Int = if (a > b) a else b

    /** Lesser of [a] and [b]. */
    fun min(a: Double, b: Double): Double = if (a < b) a else b
    /** Lesser of [a] and [b]. */
    fun min(a: Float, b: Float): Float = if (a < b) a else b
    /** Lesser of [a] and [b]. */
    fun min(a: Int, b: Int): Int = if (a < b) a else b
}

/**
 * Collection extensions for WASM.
 */
fun <T> List<T>.forEachCompat(action: (T) -> Unit) {
    for (element in this) {
        action(element)
    }
}

/** Returns at most the first [n] values. */
fun <T> List<T>.takeCompat(n: Int): List<T> {
    val result = mutableListOf<T>()
    var count = 0
    for (element in this) {
        if (count >= n) break
        result.add(element)
        count++
    }
    return result
}

/** Returns values after the first [n]. */
fun <T> List<T>.dropCompat(n: Int): List<T> {
    val result = mutableListOf<T>()
    var count = 0
    for (element in this) {
        if (count >= n) {
            result.add(element)
        }
        count++
    }
    return result
}

/** Maps every value with [transform]. */
fun <T, R> List<T>.mapCompat(transform: (T) -> R): List<R> {
    val result = mutableListOf<R>()
    for (element in this) {
        result.add(transform(element))
    }
    return result
}

/** Retains values accepted by [predicate]. */
fun <T> List<T>.filterCompat(predicate: (T) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    for (element in this) {
        if (predicate(element)) {
            result.add(element)
        }
    }
    return result
}

/** Millisecond-based duration helpers used by common code. */
object TimeCompat {
    /**
     * Duration represented in milliseconds.
     *
     * @property milliseconds signed duration value
     */
    data class DurationCompat(val milliseconds: Long) {
        /** Converts this duration to fractional seconds. */
        fun toSeconds(): Double = milliseconds / 1000.0
        /** Converts this duration to fractional minutes. */
        fun toMinutes(): Double = milliseconds / 60000.0
        /** Converts this duration to fractional hours. */
        fun toHours(): Double = milliseconds / 3600000.0
    }

    /** Creates a duration from [value] milliseconds. */
    fun milliseconds(value: Long): DurationCompat = DurationCompat(value)
    /** Creates a duration from [value] seconds. */
    fun seconds(value: Long): DurationCompat = DurationCompat(value * 1000)
    /** Creates a duration from [value] minutes. */
    fun minutes(value: Long): DurationCompat = DurationCompat(value * 60000)
    /** Creates a duration from [value] hours. */
    fun hours(value: Long): DurationCompat = DurationCompat(value * 3600000)
}