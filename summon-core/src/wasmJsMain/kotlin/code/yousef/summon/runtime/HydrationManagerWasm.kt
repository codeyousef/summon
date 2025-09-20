package code.yousef.summon.runtime

import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.runtime.wasmConsoleWarn
import code.yousef.summon.runtime.wasmConsoleError
import code.yousef.summon.annotation.Composable
import code.yousef.summon.state.MutableState
import code.yousef.summon.state.mutableStateOf

actual class HydrationManager actual constructor() {
    private val registeredComponents = mutableMapOf<String, HydrationInfo>()
    private var componentIdCounter = 0

    actual fun registerComponent(
        elementId: String,
        componentType: String,
        initialState: Map<String, Any?>,
        composable: @Composable () -> Unit
    ) {
        wasmConsoleLog("HydrationManager registerComponent: $elementId ($componentType) - WASM stub")
        registeredComponents[elementId] = HydrationInfo(elementId, componentType, initialState, composable)
    }

    actual fun hydrateAll() {
        wasmConsoleLog("HydrationManager hydrateAll (${registeredComponents.size} components) - WASM stub")
    }

    actual fun hydrateComponent(elementId: String): Boolean {
        wasmConsoleLog("HydrationManager hydrateComponent: $elementId - WASM stub")
        return registeredComponents.containsKey(elementId)
    }

    actual fun <T> restoreState(componentId: String, stateKey: String, initialValue: T): MutableState<T> {
        wasmConsoleLog("HydrationManager restoreState: $componentId.$stateKey - WASM stub")
        return mutableStateOf(initialValue)
    }

    actual fun generateComponentId(componentType: String): String {
        wasmConsoleLog("HydrationManager generateComponentId: $componentType - WASM stub")
        return "${componentType}_${++componentIdCounter}"
    }

    actual fun clear() {
        wasmConsoleLog("HydrationManager clear - WASM stub")
        registeredComponents.clear()
        componentIdCounter = 0
    }
}

