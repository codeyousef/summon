package codes.yousef.summon.runtime

import kotlinx.browser.window
import kotlin.js.Promise

/**
 * JavaScript implementation of the RecompositionScheduler.
 * Uses requestAnimationFrame for optimal rendering performance.
 */
class JsRecompositionScheduler : RecompositionScheduler {
    private var scheduledWork: (() -> Unit)? = null
    private var animationFrameId: Int? = null

    override fun cancelPendingRecomposition() {
        animationFrameId?.let { window.cancelAnimationFrame(it) }
        animationFrameId = null
        scheduledWork = null
    }

    override fun scheduleRecomposition(work: () -> Unit) {
        // Cancel any previously scheduled work
        animationFrameId?.let { window.cancelAnimationFrame(it) }

        // Schedule new work
        scheduledWork = work
        animationFrameId = window.requestAnimationFrame {
            val next = scheduledWork
            scheduledWork = null
            animationFrameId = null
            next?.invoke()
        }
    }
}

/**
 * Alternative scheduler using microtasks for faster execution.
 * This can be useful for state changes that need immediate response.
 */
class MicrotaskScheduler : RecompositionScheduler {
    private var scheduledWork: (() -> Unit)? = null
    private var generation = 0L

    override fun cancelPendingRecomposition() {
        generation++
        scheduledWork = null
    }

    override fun scheduleRecomposition(work: () -> Unit) {
        if (scheduledWork == null) {
            scheduledWork = work
            val ticket = ++generation
            // Capture Kotlin fields directly; raw JS property names are unstable
            // under IR mangling. Clear before execution so reentrant work survives.
            Promise.resolve(Unit).then {
                if (ticket != generation) return@then Unit
                val next = scheduledWork
                scheduledWork = null
                next?.invoke()
            }
        }
    }
}

/**
 * Creates the default scheduler for the JavaScript platform.
 * Uses requestAnimationFrame for smooth animations.
 */
actual fun createDefaultScheduler(): RecompositionScheduler = JsRecompositionScheduler()
