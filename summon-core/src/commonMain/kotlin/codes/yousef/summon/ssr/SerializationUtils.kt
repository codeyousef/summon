package codes.yousef.summon.ssr

/**
 * Utility functions for serializing data to JSON format.
 * Consolidates common serialization logic used across SSR implementations.
 */
object SerializationUtils {

    /**
     * Serialize the initial state to JSON
     */
    fun serializeInitialState(state: Map<String, Any?>): String {
        if (state.isEmpty()) return "{}"

        return buildString {
            append("{")
            state.entries.forEachIndexed { index, (key, value) ->
                if (index > 0) append(",")
                append("\"${escapeJsonString(key)}\":")
                append(serializeValue(value))
            }
            append("}")
        }
    }

    /**
     * Serialize a value to JSON
     */
    fun serializeValue(value: Any?): String {
        return when (value) {
            null -> "null"
            is String -> "\"${escapeJsonString(value)}\""
            is Number, is Boolean -> value.toString()
            is Map<*, *> -> {
                buildString {
                    append("{")
                    value.entries.forEachIndexed { index, entry ->
                        if (index > 0) append(",")
                        append("\"${escapeJsonString(entry.key.toString())}\":")
                        append(serializeValue(entry.value))
                    }
                    append("}")
                }
            }

            is List<*> -> {
                buildString {
                    append("[")
                    value.forEachIndexed { index, item ->
                        if (index > 0) append(",")
                        append(serializeValue(item))
                    }
                    append("]")
                }
            }

            else -> "\"${escapeJsonString(value.toString())}\""
        }
    }

    /**
     * Escape a string for JSON
     */
    fun escapeJsonString(value: String): String = buildString(value.length) {
        value.forEach { character ->
            when (character) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
                '<' -> append("\\u003c")
                '\u2028' -> append("\\u2028")
                '\u2029' -> append("\\u2029")
                else -> if (character.code < 0x20) {
                    append("\\u").append(character.code.toString(16).padStart(4, '0'))
                } else {
                    append(character)
                }
            }
        }
    }
}