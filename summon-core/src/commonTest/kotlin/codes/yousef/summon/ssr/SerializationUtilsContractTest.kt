package codes.yousef.summon.ssr

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SerializationUtilsContractTest {
    @Test
    fun nestedInitialStateProducesValidScriptSafeJson() {
        val state = linkedMapOf<String, Any?>(
            "quoted\"key" to "</script>\n\t\b\u000c\u0001\u2028\u2029\\\"",
            "null" to null,
            "boolean" to true,
            "number" to 42,
            "map" to linkedMapOf<Any, Any?>("nested\"key" to listOf(1, "two", false)),
            "object" to object { override fun toString() = "custom" }
        )
        val serialized = SerializationUtils.serializeInitialState(state)
        Json.parseToJsonElement(serialized)
        assertFalse(serialized.contains("</script>"))
        assertEquals("{}", SerializationUtils.serializeInitialState(emptyMap()))
        assertEquals("\"custom\"", SerializationUtils.serializeValue(state.getValue("object")))
    }

    @Test
    fun scalarAndCollectionValuesCoverEverySupportedKind() {
        assertEquals("null", SerializationUtils.serializeValue(null))
        assertEquals("true", SerializationUtils.serializeValue(true))
        assertEquals("1.5", SerializationUtils.serializeValue(1.5))
        assertEquals("[1,\"two\"]", SerializationUtils.serializeValue(listOf(1, "two")))
        assertEquals("{\"1\":\"one\"}", SerializationUtils.serializeValue(mapOf(1 to "one")))
        assertEquals("\\\"\\\\\\n\\r\\t\\b\\f\\u003c\\u0001\\u2028\\u2029", SerializationUtils.escapeJsonString("\"\\\n\r\t\b\u000c<\u0001\u2028\u2029"))
    }
}
