package code.yousef.summon.animation

import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

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
 * 
 * For JVM platform, this implementation uses a simple thread-based approach
 * to simulate animation progress. In a real-world scenario, this would likely
 * be integrated with a UI framework's animation system.
 */
actual object AnimationController {
    // Thread-safe animation state
    private val currentStatus = AtomicReference(AnimationStatus.IDLE)
    private val currentProgress = AtomicReference(0f)
    private var animationThread: Thread? = null
    private var animationDuration: Long = 300 // Default duration in ms
    private var startTime: Long = 0
    private var pauseTime: Long = 0

    /** Pauses the running animation. */
    actual fun pause() {
        if (currentStatus.get() == AnimationStatus.RUNNING) {
            pauseTime = System.currentTimeMillis()
            currentStatus.set(AnimationStatus.PAUSED)
        }
    }

    /** Resumes a paused animation. */
    actual fun resume() {
        if (currentStatus.get() == AnimationStatus.PAUSED) {
            // Adjust start time to account for pause duration
            val pauseDuration = System.currentTimeMillis() - pauseTime
            startTime += pauseDuration
            currentStatus.set(AnimationStatus.RUNNING)
        }
    }

    /** Cancels the animation, potentially resetting state. */
    actual fun cancel() {
        stopAnimation()
        currentProgress.set(0f)
        currentStatus.set(AnimationStatus.IDLE)
    }

    /** Stops the animation, holding its current state. */
    actual fun stop() {
        stopAnimation()
        currentStatus.set(AnimationStatus.STOPPED)
    }

    /** Gets the current status of the animation. */
    actual val status: AnimationStatus
        get() = currentStatus.get()

    /** Gets the current progress of the animation (typically 0.0 to 1.0). */
    actual val progress: Float
        get() = currentProgress.get()

    /**
     * Starts a new animation with the specified duration.
     * This is a JVM-specific method not in the common interface.
     */
    fun startAnimation(durationMs: Long = 300) {
        // Stop any existing animation
        stopAnimation()

        // Set up new animation
        animationDuration = durationMs
        startTime = System.currentTimeMillis()
        currentProgress.set(0f)
        currentStatus.set(AnimationStatus.RUNNING)

        // Create and start animation thread
        animationThread = thread(start = true, isDaemon = true) {
            try {
                while (currentStatus.get() == AnimationStatus.RUNNING) {
                    val currentTime = System.currentTimeMillis()
                    val elapsedTime = currentTime - startTime

                    if (elapsedTime >= animationDuration) {
                        // Animation complete
                        currentProgress.set(1f)
                        currentStatus.set(AnimationStatus.STOPPED)
                        break
                    } else if (currentStatus.get() == AnimationStatus.RUNNING) {
                        // Update progress
                        val newProgress = (elapsedTime.toFloat() / animationDuration).coerceIn(0f, 1f)
                        currentProgress.set(newProgress)
                    }

                    // Small delay to avoid excessive CPU usage
                    Thread.sleep(16) // ~60fps
                }
            } catch (e: InterruptedException) {
                // Thread was interrupted, stop animation
                currentStatus.set(AnimationStatus.STOPPED)
            }
        }
    }

    /**
     * Stops the animation thread if it's running.
     */
    private fun stopAnimation() {
        animationThread?.interrupt()
        animationThread = null
    }
} 
