package code.yousef.summon.runtime

import kotlinx.browser.window

/**
 * JavaScript implementation of the RecompositionScheduler.
 * Uses requestAnimationFrame for optimal rendering performance.
 */
class JsRecompositionScheduler : RecompositionScheduler {
    private var scheduledWork: (() -> Unit)? = null
    private var animationFrameId: Int? = null

    override fun scheduleRecomposition(work: () -> Unit) {
        // Cancel any previously scheduled work
        animationFrameId?.let { window.cancelAnimationFrame(it) }

        // Schedule new work
        scheduledWork = work
        animationFrameId = window.requestAnimationFrame {
            scheduledWork?.invoke()
            scheduledWork = null
            animationFrameId = null
        }
    }
}

/**
 * Alternative scheduler using microtasks for faster execution.
 * This can be useful for state changes that need immediate response.
 */
class MicrotaskScheduler : RecompositionScheduler {
    private var scheduledWork: (() -> Unit)? = null

    override fun scheduleRecomposition(work: () -> Unit) {
        if (scheduledWork == null) {
            scheduledWork = work
            // Use Promise.resolve().then() to schedule a microtask
            js(
                """
                var that = this;
                Promise.resolve().then(function() { 
                    var work = that.scheduledWork;
                    if (work) {
                        work();
                        that.scheduledWork = null;
                    }
                })
            """
            )
        }
    }
}

/**
 * Creates the default scheduler for the JavaScript platform.
 * Uses requestAnimationFrame for smooth animations.
 */
actual fun createDefaultScheduler(): RecompositionScheduler = JsRecompositionScheduler()