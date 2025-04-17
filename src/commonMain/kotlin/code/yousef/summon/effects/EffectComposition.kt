package code.yousef.summon.effects

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.DisposableEffect
import code.yousef.summon.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.milliseconds

// Using IntervalControl and TimeoutControl interfaces from CommonEffects.kt

/**
 * Effect priority levels for ordering effects
 */
enum class EffectPriority {
    HIGH,
    NORMAL,
    LOW
}

/**
 * Effect scope for managing effect execution context
 */
class EffectScope {
    private val effects = mutableListOf<Pair<() -> Unit, EffectPriority>>()

    /**
     * Add an effect with a specific priority
     */
    fun addEffect(effect: () -> Unit, priority: EffectPriority = EffectPriority.NORMAL) {
        effects.add(effect to priority)
    }

    /**
     * Execute all effects in priority order
     */
    fun executeEffects() {
        effects.sortedBy { it.second.ordinal }.forEach { (effect, _) ->
            effect()
        }
    }

    /**
     * Clear all effects
     */
    fun clearEffects() {
        effects.clear()
    }
}

/**
 * Create a custom composable effect
 * @param setup The setup function that creates the effect value
 * @param callback The callback that handles the effect value and returns a cleanup function
 * @return A composable effect function that can be used in other composables
 */
fun <T> createEffect(
    setup: CompositionScope.() -> T,
    callback: (T) -> (() -> Unit)?
): CompositionScope.() -> Unit = {
    val value = setup()
    DisposableEffect {
        callback(value) ?: {}
    }
}

/**
 * Combine multiple effects into one with priority ordering
 * @param effects The effects to combine
 * @return A composable effect that runs all the provided effects in priority order
 */
fun combineEffects(
    vararg effects: Pair<CompositionScope.() -> Unit, EffectPriority>
): CompositionScope.() -> Unit = {
    val scope = EffectScope()

    // Add all effects to the scope with their priorities
    effects.forEach { (effect, priority) ->
        scope.addEffect({ effect(this) }, priority)
    }

    // Execute all effects in priority order
    scope.executeEffects()
}

/**
 * Combine multiple effects into one with default priority
 * @param effects The effects to combine
 * @return A composable effect that runs all the provided effects
 */
fun combineEffects(
    vararg effects: CompositionScope.() -> Unit
): CompositionScope.() -> Unit = {
    val scope = EffectScope()

    // Add all effects to the scope with default priority
    effects.forEach { effect ->
        scope.addEffect({ effect(this) })
    }

    // Execute all effects in priority order
    scope.executeEffects()
}

/**
 * Create a conditional effect that only runs when condition is true
 * @param condition The condition function to check
 * @param effect The effect to run when the condition is true
 * @param priority The priority of the effect
 * @return A composable effect that runs conditionally
 */
fun conditionalEffect(
    condition: () -> Boolean,
    effect: CompositionScope.() -> Unit,
    priority: EffectPriority = EffectPriority.NORMAL
): CompositionScope.() -> Unit = {
    val scope = EffectScope()

    if (condition()) {
        scope.addEffect({ effect(this) }, priority)
        scope.executeEffects()
    }
}

/**
 * Timer for debouncing/throttling.
 */
private class EffectTimer<T>(
    private val delayMs: Int,
    private val operation: (T) -> Unit
) {
    private var timeoutId: Job? = null
    private var lastValue: T? = null
    private var lastFireTime: Long = 0
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun debounce(value: T) {
        // Cancel any pending execution
        cancelTimeout()

        // Store the latest value
        lastValue = value

        // Schedule a new execution after delay
        timeoutId = scheduleFutureExecution {
            val valueToUse = lastValue
            if (valueToUse != null) {
                operation(valueToUse)
            }
        }
    }

    fun throttle(value: T) {
        val now = Clock.System.now().toEpochMilliseconds()

        // Store the latest value
        lastValue = value

        // If enough time has passed since last execution, run immediately
        if (now > lastFireTime + delayMs) {
            cancelTimeout()
            operation(value)
            lastFireTime = now
        } else if (timeoutId == null) {
            // Schedule execution for after the throttle period
            timeoutId = scheduleFutureExecution {
                val valueToUse = lastValue
                if (valueToUse != null) {
                    operation(valueToUse)
                    lastFireTime = Clock.System.now().toEpochMilliseconds()
                }
            }
        }
    }

    fun cancel() {
        cancelTimeout()
        coroutineScope.cancel()
    }

    private fun cancelTimeout() {
        timeoutId?.cancel()
        timeoutId = null
    }

    private fun scheduleFutureExecution(action: () -> Unit): Job {
        return coroutineScope.launch {
            delay(delayMs.milliseconds)
            try {
                action()
            } catch (e: Exception) {
                println("Exception in EffectTimer: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

/**
 * Create a debounced effect
 * @param delayMs The debounce delay in milliseconds
 * @param producer The producer function that creates the value
 * @param effect The effect function that handles the value
 * @return A composable effect that debounces the producer calls
 */
fun <T> debouncedEffect(
    delayMs: Int,
    producer: () -> T,
    effect: (T) -> Unit
): CompositionScope.() -> Unit = {
    val value = producer()

    LaunchedEffect(value) {
        delay(delayMs.milliseconds)
        effect(value)
    }
}

/**
 * Create a throttled effect
 * @param delayMs The throttle delay in milliseconds
 * @param producer The producer function that creates the value
 * @param effect The effect function that handles the value
 * @return A composable effect that throttles the producer calls
 */
fun <T> throttledEffect(
    delayMs: Int,
    producer: () -> T,
    effect: (T) -> Unit
): CompositionScope.() -> Unit = {
    val value = producer()

    var lastExecutionTime = 0L
    LaunchedEffect(value) {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val elapsed = currentTime - lastExecutionTime

        if (elapsed >= delayMs) {
            effect(value)
            lastExecutionTime = currentTime
        }
    }
}

/**
 * Remember function for effects implementation.
 * This is a temporary implementation until we integrate with the actual framework.
 */
@Composable
private fun <T> remember(calculation: () -> T): T {
    // In actual implementation, this would use the framework's remember function
    return calculation()
}

/**
 * Create an interval effect that runs at specified intervals
 * @param delayMs The interval delay in milliseconds
 * @param function The function to run at each interval
 * @return A composable effect with interval and control functions
 */
fun intervalEffect(
    delayMs: Int,
    function: () -> Unit
): CompositionScope.() -> IntervalControl = {
    var isActive = true
    var intervalDelay = delayMs

    val control = object : IntervalControl {
        override fun pause() {
            isActive = false
        }

        override fun resume() {
            isActive = true
        }

        override fun reset() {
            // No-op for interval
        }

        override fun setDelay(delayMs: Int) {
            intervalDelay = delayMs
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(intervalDelay.milliseconds)
            if (isActive) {
                function()
            }
        }
    }

    control
}

/**
 * Create a timeout effect that runs after a delay
 * @param delayMs The timeout delay in milliseconds
 * @param function The function to run after the timeout
 * @return A composable effect with timeout control functions
 */
fun timeoutEffect(
    delayMs: Int,
    function: () -> Unit
): CompositionScope.() -> TimeoutControl = {
    var isActive = true
    var timeoutDelay = delayMs

    val control = object : TimeoutControl {
        override fun cancel() {
            isActive = false
        }

        override fun reset() {
            isActive = true
        }

        override fun setDelay(delayMs: Int) {
            timeoutDelay = delayMs
        }
    }

    LaunchedEffect(Unit) {
        delay(timeoutDelay.milliseconds)
        if (isActive) {
            function()
        }
    }

    control
} 
