package code.yousef.summon.animation

/**
 * Represents the status of an animation.
 */
actual enum class AnimationStatus {
    IDLE,    // Animation has not started or has been reset.
    RUNNING, // Animation is currently playing.
    PAUSED,  // Animation is paused.
    STOPPED  // Animation has finished or been explicitly stopped.
}

/**
 * Controller for managing animations started by the platform renderer.
 * Allows pausing, resuming, stopping, and querying the state of an animation.
 */
actual object AnimationController { // Using object for simplicity, could be interface/class if needed
    /** Pauses the running animation. */
    actual fun pause() {
        // Implementation for JS platform
        js("if (window.animationController) window.animationController.pause()")
    }

    /** Resumes a paused animation. */
    actual fun resume() {
        // Implementation for JS platform
        js("if (window.animationController) window.animationController.resume()")
    }

    /** Cancels the animation, potentially resetting state. */
    actual fun cancel() {
        // Implementation for JS platform
        js("if (window.animationController) window.animationController.cancel()")
    }

    /** Stops the animation, holding its current state. */
    actual fun stop() {
        // Implementation for JS platform
        js("if (window.animationController) window.animationController.stop()")
    }

    /** Gets the current status of the animation. */
    actual val status: AnimationStatus
        get() {
            // Implementation for JS platform - get status from JS
            val statusStr = js("window.animationController ? window.animationController.status : 'idle'") as String
            return when (statusStr.lowercase()) {
                "running" -> AnimationStatus.RUNNING
                "paused" -> AnimationStatus.PAUSED
                "stopped" -> AnimationStatus.STOPPED
                else -> AnimationStatus.IDLE
            }
        }

    /** Gets the current progress of the animation (typically 0.0 to 1.0). */
    actual val progress: Float
        get() {
            // Implementation for JS platform - get progress from JS
            return js("window.animationController ? window.animationController.progress : 0.0") as Float
        }
} 