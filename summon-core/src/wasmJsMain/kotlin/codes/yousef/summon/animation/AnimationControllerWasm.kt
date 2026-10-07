package codes.yousef.summon.animation

import codes.yousef.summon.runtime.wasmConsoleLog

/**
 * WASM implementation of AnimationStatus enum.
 */
actual enum class AnimationStatus {
    /** The idle animation status option. */
    IDLE,
    /** The running animation status option. */
    RUNNING,
    /** The paused animation status option. */
    PAUSED,
    /** The stopped animation status option. */
    STOPPED
}

/**
 * WASM implementation of AnimationController.
 */
actual object AnimationController {
    private var _status: AnimationStatus = AnimationStatus.IDLE
    private var _progress: Float = 0.0f

    /** Pauses the operation. */
    actual fun pause() {
        wasmConsoleLog("AnimationController.pause() - WASM stub")
        _status = AnimationStatus.PAUSED
    }

    /** Resumes the operation. */
    actual fun resume() {
        wasmConsoleLog("AnimationController.resume() - WASM stub")
        _status = AnimationStatus.RUNNING
    }

    /** Cancels the operation. */
    actual fun cancel() {
        wasmConsoleLog("AnimationController.cancel() - WASM stub")
        _status = AnimationStatus.IDLE
        _progress = 0.0f
    }

    /** Stops the operation. */
    actual fun stop() {
        wasmConsoleLog("AnimationController.stop() - WASM stub")
        _status = AnimationStatus.STOPPED
    }

    /** The property declaration value. */
    actual val status: AnimationStatus get() = _status

    /** The property declaration value. */
    actual val progress: Float get() = _progress
}