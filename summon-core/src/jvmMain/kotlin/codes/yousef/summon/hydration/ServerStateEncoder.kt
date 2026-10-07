package codes.yousef.summon.hydration

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.util.Base64

/** Provides server state encoder operations. */
object ServerStateEncoder {
    /** The property declaration value. */
    val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        classDiscriminator = "type"
    }

    /**
     * Executes the encode operation.
     *
     * @param state The state value.
     * @return The resulting value.
     */
    inline fun <reified T> encode(state: T): String {
        val jsonString = json.encodeToString(state)
        return Base64.getEncoder().encodeToString(jsonString.toByteArray())
    }
}
