package codes.yousef.summon.animation

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JvmAnimationControllerTest {
    @AfterTest
    fun reset() = AnimationController.cancel()

    @Test
    fun pauseResumeCompletionStopAndCancelPreserveDocumentedStates() {
        AnimationController.cancel()
        AnimationController.pause()
        AnimationController.resume()
        assertEquals(AnimationStatus.IDLE, AnimationController.status)

        AnimationController.startAnimation(120)
        assertEquals(AnimationStatus.RUNNING, AnimationController.status)
        Thread.sleep(25)
        AnimationController.pause()
        assertEquals(AnimationStatus.PAUSED, AnimationController.status)
        val paused = AnimationController.progress
        Thread.sleep(25)
        assertEquals(paused, AnimationController.progress)

        AnimationController.resume()
        assertEquals(AnimationStatus.RUNNING, AnimationController.status)
        awaitStopped()
        assertEquals(1f, AnimationController.progress)

        AnimationController.startAnimation(10_000)
        Thread.sleep(20)
        AnimationController.stop()
        assertEquals(AnimationStatus.STOPPED, AnimationController.status)
        assertTrue(AnimationController.progress in 0f..1f)

        AnimationController.cancel()
        assertEquals(AnimationStatus.IDLE, AnimationController.status)
        assertEquals(0f, AnimationController.progress)
    }

    private fun awaitStopped() {
        val deadline = System.nanoTime() + 2_000_000_000L
        while (AnimationController.status != AnimationStatus.STOPPED && System.nanoTime() < deadline) {
            Thread.sleep(5)
        }
        assertEquals(AnimationStatus.STOPPED, AnimationController.status)
    }
}
