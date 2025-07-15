package code.yousef.summon.ssr

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
                append("\"$key\":")
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
                        append("\"${entry.key}\":")
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
    fun escapeJsonString(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            .replace("\b", "\\b")
            .replace("\u000C", "\\f")
    }
}