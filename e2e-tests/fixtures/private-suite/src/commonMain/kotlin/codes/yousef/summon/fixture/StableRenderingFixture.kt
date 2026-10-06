package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.input.TextField
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.runtime.key
import codes.yousef.summon.state.mutableStateOf

private data class PendingRenderRequest(val route: String, val value: String)

class StableRenderingFixture {
    private val route = mutableStateOf("A")
    private val callbackModel = mutableStateOf("model-A")
    private val callbackResult = mutableStateOf("not invoked")
    private val requestResult = mutableStateOf("Unavailable")
    private val items = mutableStateOf(listOf("one", "two", "three"))
    private val itemValues = mapOf(
        "one" to mutableStateOf("one"),
        "two" to mutableStateOf("two"),
        "three" to mutableStateOf("three")
    )
    private val itemCallbacks = mutableStateOf(0)
    private val activeRouteEffects = mutableStateOf(0)
    private val disposedRouteEffects = mutableStateOf(0)
    private var oldRequest: PendingRenderRequest? = null
    private var newRequest: PendingRenderRequest? = null

    private fun complete(request: PendingRenderRequest?) {
        if (request?.route == route.value) {
            requestResult.value = request.value
        }
    }
    private fun fail(request: PendingRenderRequest?) {
        if (request?.route == route.value) {
            requestResult.value = "Unavailable"
        }
    }

    @Composable
    fun Content() {
        Column {
            Text("Stable rendering fixture", Modifier().attribute("data-testid", "identity-title"))
            Text("Route ${route.value}", Modifier().attribute("data-testid", "identity-route"))
            Text(
                "Active: ${activeRouteEffects.value}; Disposed: ${disposedRouteEffects.value}",
                Modifier().attribute("data-testid", "route-effect-stats")
            )
            Button(
                onClick = { route.value = if (route.value == "A") "B" else "A" },
                label = "Swap route",
                modifier = Modifier().attribute("key", "swap-route")
            )

            key("route", route.value) {
                DisposableEffect(route.value) {
                    activeRouteEffects.value++
                    return@DisposableEffect {
                        activeRouteEffects.value--
                        disposedRouteEffects.value++
                    }
                }
                Text("Private route ${route.value}", Modifier().attribute("data-testid", "private-route-content"))
            }

            Text(callbackModel.value, Modifier().attribute("data-testid", "callback-model"))
            Text(callbackResult.value, Modifier().attribute("data-testid", "callback-result"))
            Button(
                onClick = { callbackModel.value = if (callbackModel.value == "model-A") "model-B" else "model-A" },
                label = "Change callback model",
                modifier = Modifier().attribute("key", "change-callback-model")
            )
            Button(
                onClick = { callbackResult.value = callbackModel.value },
                label = "Invoke current callback",
                modifier = Modifier().attribute("key", "invoke-current-callback")
            )

            Text(requestResult.value, Modifier().attribute("data-testid", "request-result"))
            Button(
                onClick = { oldRequest = PendingRenderRequest(route.value, "old ${route.value}") },
                label = "Start old request"
            )
            Button(
                onClick = { newRequest = PendingRenderRequest(route.value, "new ${route.value}") },
                label = "Start new request"
            )
            Button(onClick = { complete(oldRequest) }, label = "Complete old request")
            Button(onClick = { complete(newRequest) }, label = "Complete new request")
            Button(onClick = { fail(newRequest) }, label = "Fail new request")

            items.value.forEach { item ->
                key("identity-item", item) {
                    val itemState = itemValues.getValue(item)
                    TextField(
                        value = itemState.value,
                        onValueChange = { itemState.value = it },
                        label = "Item $item",
                        modifier = Modifier()
                            .attribute("key", "identity-input-$item")
                            .attribute("data-testid", "identity-input-$item")
                    )
                    Button(
                        onClick = { itemCallbacks.value++ },
                        label = "Invoke item $item",
                        modifier = Modifier()
                            .attribute("key", "identity-button-$item")
                            .attribute("data-testid", "identity-button-$item")
                    )
                }
            }
            Text("Item callbacks: ${itemCallbacks.value}", Modifier().attribute("data-testid", "item-callbacks"))
            Button(onClick = { items.value = items.value.reversed() }, label = "Reverse identity items")
            Button(onClick = { items.value = items.value.filter { it != "two" } }, label = "Delete identity item two")
        }
    }
}
