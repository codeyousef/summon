package codes.yousef.summon.routing

/**
 * A validated same-origin route path. Query strings and fragments are deliberately excluded so
 * private application state cannot be copied into browser history through router navigation.
 */
class InternalRoutePath private constructor(
    val encodedPath: String,
    val segments: List<String>
) {
    companion object {
        const val MAX_PATH_BYTES: Int = 2_048
        const val MAX_SEGMENT_BYTES: Int = 256

        fun parse(value: String): InternalRoutePath? {
            if (!value.startsWith('/') || value.startsWith("//")) return null
            if ('?' in value || '#' in value || '\\' in value || value.encodeToByteArray().size > MAX_PATH_BYTES) {
                return null
            }
            if (value.any { it.code < 0x20 || it.code == 0x7f }) return null

            val encodedSegments = value.split('/').drop(1)
            val decodedSegments = ArrayList<String>(encodedSegments.size)
            for (encoded in encodedSegments) {
                if (encoded.encodeToByteArray().size > MAX_SEGMENT_BYTES) return null
                val decoded = decodeSegment(encoded) ?: return null
                if (decoded == "." || decoded == ".." || '/' in decoded || '\\' in decoded || '\u0000' in decoded) {
                    return null
                }
                decodedSegments += decoded
            }
            return InternalRoutePath(value, decodedSegments)
        }

        private fun decodeSegment(value: String): String? {
            if ('%' !in value) return value
            val bytes = ArrayList<Byte>(value.length)
            var index = 0
            while (index < value.length) {
                val character = value[index]
                if (character == '%') {
                    if (index + 2 >= value.length) return null
                    val high = value[index + 1].digitToIntOrNull(16) ?: return null
                    val low = value[index + 2].digitToIntOrNull(16) ?: return null
                    bytes += ((high shl 4) or low).toByte()
                    index += 3
                } else {
                    val encoded = character.toString().encodeToByteArray()
                    for (byte in encoded) bytes += byte
                    index++
                }
            }
            return try {
                bytes.toByteArray().decodeToString(throwOnInvalidSequence = true)
            } catch (_: CharacterCodingException) {
                null
            }
        }
    }
}

internal fun matchInternalRoute(pattern: String, path: InternalRoutePath): Map<String, String>? {
    val patternPath = InternalRoutePath.parse(pattern) ?: return null
    val patternSegments = patternPath.segments
    val pathSegments = path.segments

    val wildcardIndex = patternSegments.indexOfFirst { it == "*" }
    if (wildcardIndex >= 0) {
        if (wildcardIndex != patternSegments.lastIndex || pathSegments.size < wildcardIndex) return null
    } else if (patternSegments.size != pathSegments.size) {
        return null
    }

    val params = mutableMapOf<String, String>()
    for (index in patternSegments.indices) {
        val patternSegment = patternSegments[index]
        if (patternSegment == "*") break
        val pathSegment = pathSegments.getOrNull(index) ?: return null
        when {
            patternSegment.startsWith(':') && patternSegment.length > 1 -> {
                params[patternSegment.substring(1)] = pathSegment
            }
            patternSegment.startsWith('{') && patternSegment.endsWith('}') && patternSegment.length > 2 -> {
                params[patternSegment.substring(1, patternSegment.lastIndex)] = pathSegment
            }
            patternSegment != pathSegment -> return null
        }
    }
    return params
}
