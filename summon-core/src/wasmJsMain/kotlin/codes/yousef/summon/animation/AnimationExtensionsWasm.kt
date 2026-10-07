package codes.yousef.summon.animation

import codes.yousef.summon.runtime.wasmConsoleLog
import kotlinx.coroutines.delay as kotlinxDelay

/**
 * Starts animation.
 *
 * @param durationMs The duration ms value.
 */
actual fun AnimationController.startAnimation(durationMs: Int) {
    wasmConsoleLog("AnimationController.startAnimation: ${durationMs}ms - WASM stub")
}

/**
 * Executes the delay operation.
 *
 * @param timeMillis The time millis value.
 */
actual suspend fun delay(timeMillis: Long) {
    wasmConsoleLog("Animation delay: ${timeMillis}ms - WASM stub")
    kotlinxDelay(timeMillis)
}