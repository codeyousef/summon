package codes.yousef.summon.action

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class UiActionContractTest {
    private val json = Json { classDiscriminator = "type"; encodeDefaults = true }

    @Test
    fun everyActionRoundTripsWithStableDiscriminator() {
        val actions = listOf<UiAction>(
            UiAction.Navigate("/next"),
            UiAction.ServerRpc("/rpc", JsonPrimitive("payload")),
            UiAction.ServerRpc("/rpc", JsonPrimitive(1), JsonPrimitive("optimistic")),
            UiAction.ToggleVisibility("menu")
        )
        actions.forEach { action ->
            val encoded = json.encodeToString(action)
            assertEquals(action, json.decodeFromString<UiAction>(encoded))
            assertContains(encoded, "\"type\"")
        }
        assertContains(json.encodeToString<UiAction>(actions[0]), "\"nav\"")
        assertContains(json.encodeToString<UiAction>(actions[1]), "\"rpc\"")
        assertContains(json.encodeToString<UiAction>(actions[3]), "\"toggle\"")
    }
}
