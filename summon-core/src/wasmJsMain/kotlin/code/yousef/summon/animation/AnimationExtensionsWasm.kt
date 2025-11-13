package codes.yousef.summon.animation

import codes.yousef.summon.runtime.wasmConsoleLog
import kotlinx.coroutines.delay as kotlinxDelay

actual fun AnimationController.startAnimation(durationMs: Int) {
    wasmConsoleLog("AnimationController.startAnimation: ${durationMs}ms - WASM stub")
}

actual suspend fun delay(timeMillis: Long) {
    wasmConsoleLog("Animation delay: ${timeMillis}ms - WASM stub")
    kotlinxDelay(timeMillis)
}