package codes.yousef.summon.devtools

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put

/** Strict, non-executable JSON interchange for [StateTimeline]. */
object DebugSessionCodec {
    /** The property declaration value. */
    const val FORMAT_VERSION: Int = 1
    /** The property declaration value. */
    const val MAX_JSON_UTF8_BYTES: Int = 1_048_576

    private val json = Json {
        isLenient = false
        ignoreUnknownKeys = false
        explicitNulls = false
    }

    /**
     * Executes the encode operation.
     *
     * @param plan The plan value.
     * @return The resulting value.
     */
    fun encode(plan: DebugSessionPlan): String {
        val document = buildJsonObject {
            put("formatVersion", FORMAT_VERSION)
            put("entries", buildJsonArray { plan.entries.forEach { add(it.toJson()) } })
        }
        return document.toString().also(::requireBoundedJson)
    }

    internal fun decode(source: String, access: TimelineFieldAccess): DebugSessionPlan {
        requireBoundedJson(source)
        val root = try {
            json.parseToJsonElement(source).jsonObject
        } catch (error: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid debug session JSON", error)
        }
        root.requireKeys(setOf("formatVersion", "entries"), "session")
        require(root.requiredInt("formatVersion") == FORMAT_VERSION) { "Unsupported debug session formatVersion" }
        val entriesElement = root["entries"] ?: throw IllegalArgumentException("Missing session entries")
        val entries = try {
            entriesElement.jsonArray
        } catch (error: IllegalArgumentException) {
            throw IllegalArgumentException("Session entries must be an array", error)
        }
        require(entries.size <= StateTimeline.MAX_CAPACITY) {
            "Imported session exceeds ${StateTimeline.MAX_CAPACITY} entries"
        }
        val parsed = entries.mapIndexed { index, element -> element.toMutation(index) }
        require(parsed.map { it.sequence }.toSet().size == parsed.size) { "Duplicate mutation sequence" }
        require(parsed.zipWithNext().all { (first, second) -> first.sequence < second.sequence }) {
            "Mutation sequences must be strictly increasing"
        }
        val plan = DebugSessionPlan(parsed)
        validatePlanAgainstLiveSession(plan, access)
        return plan
    }

    private fun StateMutation.toJson(): JsonObject = buildJsonObject {
        put("sequence", sequence.toString())
        put("field", buildJsonObject {
            put("nodeId", fieldId.nodeId.toString())
            put("name", fieldId.name)
            put("codec", fieldId.typeName)
        })
        put("before", before.toJson())
        put("after", after.toJson())
        actionId?.let { put("actionId", it) }
    }

    private fun DebugValue.toJson(): JsonObject = buildJsonObject {
        when (this@toJson) {
            DebugValue.Null -> put("kind", "null")
            is DebugValue.BooleanValue -> {
                put("kind", "boolean")
                put("value", value)
            }
            is DebugValue.StringValue -> {
                validateDebugValue(this@toJson)
                put("kind", "string")
                put("value", value)
            }
            is DebugValue.LongValue -> {
                put("kind", "long")
                put("value", value.toString())
            }
            is DebugValue.DoubleValue -> {
                validateDebugValue(this@toJson)
                put("kind", "double")
                put("value", value)
            }
        }
    }

    private fun JsonElement.toMutation(index: Int): StateMutation {
        val entry = asObject("entry $index")
        entry.requireKeys(setOf("sequence", "field", "before", "after", "actionId"), "entry $index")
        val sequence = entry.requiredString("sequence").toLongOrNull()
            ?: throw IllegalArgumentException("Invalid mutation sequence")
        val field = (entry["field"] ?: throw IllegalArgumentException("Missing mutation field"))
            .asObject("field")
        field.requireKeys(setOf("nodeId", "name", "codec"), "field")
        val nodeId = field.requiredString("nodeId").toLongOrNull()
            ?: throw IllegalArgumentException("Invalid field nodeId")
        require(sequence < Long.MAX_VALUE) { "Mutation sequence is too large" }
        val actionId = entry["actionId"]?.let {
            if (it is JsonNull) null else it.asPrimitive("actionId").content.also { id ->
                requireDebugIdentifier(id, "Action")
            }
        }
        return StateMutation(
            sequence = sequence,
            fieldId = DebugFieldId(nodeId, field.requiredString("name"), field.requiredString("codec")),
            before = (entry["before"] ?: throw IllegalArgumentException("Missing before value")).toDebugValue(),
            after = (entry["after"] ?: throw IllegalArgumentException("Missing after value")).toDebugValue(),
            actionId = actionId
        )
    }

    private fun JsonElement.toDebugValue(): DebugValue {
        val value = asObject("debug value")
        value.requireKeys(setOf("kind", "value"), "debug value")
        val decoded = when (val kind = value.requiredString("kind")) {
            "null" -> {
                require("value" !in value) { "Null debug values cannot carry data" }
                DebugValue.Null
            }
            "boolean" -> DebugValue.BooleanValue(
                value.requiredPrimitive("value").booleanOrNull
                    ?: throw IllegalArgumentException("Invalid boolean debug value")
            )
            "string" -> DebugValue.StringValue(value.requiredString("value"))
            "long" -> DebugValue.LongValue(
                value.requiredString("value").toLongOrNull()
                    ?: throw IllegalArgumentException("Invalid integral debug value")
            )
            "double" -> DebugValue.DoubleValue(
                value.requiredPrimitive("value").doubleOrNull
                    ?: throw IllegalArgumentException("Invalid floating-point debug value")
            )
            else -> throw IllegalArgumentException("Unknown debug value kind '$kind'")
        }
        validateDebugValue(decoded)
        return decoded
    }

    private fun JsonElement.asObject(label: String): JsonObject = try {
        jsonObject
    } catch (error: IllegalArgumentException) {
        throw IllegalArgumentException("$label must be an object", error)
    }

    private fun JsonElement.asPrimitive(label: String): JsonPrimitive = try {
        jsonPrimitive
    } catch (error: IllegalArgumentException) {
        throw IllegalArgumentException("$label must be a primitive", error)
    }

    private fun JsonObject.requiredPrimitive(name: String): JsonPrimitive =
        (this[name] ?: throw IllegalArgumentException("Missing '$name'" )).asPrimitive(name)

    private fun JsonObject.requiredString(name: String): String {
        val primitive = requiredPrimitive(name)
        require(primitive.isString) { "'$name' must be a string" }
        return primitive.content
    }

    private fun JsonObject.requiredInt(name: String): Int {
        val primitive = requiredPrimitive(name)
        require(!primitive.isString) { "'$name' must be a number" }
        return primitive.longOrNull?.takeIf { it in Int.MIN_VALUE..Int.MAX_VALUE }?.toInt()
            ?: throw IllegalArgumentException("'$name' must be an integer")
    }

    private fun JsonObject.requireKeys(allowed: Set<String>, label: String) {
        val unknown = keys - allowed
        require(unknown.isEmpty()) { "Unknown $label properties: ${unknown.sorted().joinToString()}" }
    }

    private fun requireBoundedJson(source: String) {
        require(source.encodeToByteArray().size <= MAX_JSON_UTF8_BYTES) {
            "Debug session JSON must not exceed $MAX_JSON_UTF8_BYTES UTF-8 bytes"
        }
    }
}
