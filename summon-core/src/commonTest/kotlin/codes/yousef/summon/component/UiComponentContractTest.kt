package codes.yousef.summon.component

import codes.yousef.summon.action.UiAction
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UiComponentContractTest {
    @Test
    fun everyComponentAndActionVariantRoundTripsPolymorphically() {
        val payload = buildJsonObject { put("recordId", "opaque-1") }
        val actions = listOf<UiAction>(
            UiAction.Navigate("/records"),
            UiAction.ServerRpc("/api/action", payload),
            UiAction.ServerRpc("/api/action", payload, buildJsonObject { put("status", "pending") }),
            UiAction.ToggleVisibility("menu")
        )
        val components = listOf<UiComponent>(
            UiComponent.Box(listOf(UiComponent.Text("Title"))),
            UiComponent.Text("Body"),
            UiComponent.Button("Run", actions[1]),
            UiComponent.ExternalWidget("/assets/widget.js", payload)
        )
        actions.forEach { action ->
            assertEquals(action, Json.decodeFromString<UiAction>(Json.encodeToString(action)))
        }
        components.forEach { component ->
            assertEquals(component, Json.decodeFromString<UiComponent>(Json.encodeToString(component)))
        }
    }

    @Test
    fun componentAndActionIdentityIncludesConsumerVisibleFields() {
        assertNotEquals(UiAction.Navigate("/a"), UiAction.Navigate("/b"))
        assertNotEquals(UiAction.ToggleVisibility("a"), UiAction.ToggleVisibility("b"))
        assertNotEquals(UiComponent.Text("a"), UiComponent.Text("b"))
        assertNotEquals(
            UiComponent.Box(listOf(UiComponent.Text("a"))),
            UiComponent.Box(listOf(UiComponent.Text("b")))
        )
    }
}
