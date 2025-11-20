package codes.yousef.summon.hydration

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.util.Base64

object ServerStateEncoder {
    val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        classDiscriminator = "type"
    }

    inline fun <reified T> encode(state: T): String {
        val jsonString = json.encodeToString(state)
        return Base64.getEncoder().encodeToString(jsonString.toByteArray())
    }
}
