package code.yousef.summon.animation

import code.yousef.summon.runtime.wasmConsoleLog

/**
 * WASM implementation of AnimationStatus enum.
 */
actual enum class AnimationStatus {
    IDLE,
    RUNNING,
    PAUSED,
    STOPPED
}

/**
 * WASM implementation of AnimationController.
 */
actual object AnimationController {
    private var _status: AnimationStatus = AnimationStatus.IDLE
    private var _progress: Float = 0.0f

    actual fun pause() {
        wasmConsoleLog("AnimationController.pause() - WASM stub")
        _status = AnimationStatus.PAUSED
    }

    actual fun resume() {
        wasmConsoleLog("AnimationController.resume() - WASM stub")
        _status = AnimationStatus.RUNNING
    }

    actual fun cancel() {
        wasmConsoleLog("AnimationController.cancel() - WASM stub")
        _status = AnimationStatus.IDLE
        _progress = 0.0f
    }

    actual fun stop() {
        wasmConsoleLog("AnimationController.stop() - WASM stub")
        _status = AnimationStatus.STOPPED
    }

    actual val status: AnimationStatus get() = _status

    actual val progress: Float get() = _progress
}