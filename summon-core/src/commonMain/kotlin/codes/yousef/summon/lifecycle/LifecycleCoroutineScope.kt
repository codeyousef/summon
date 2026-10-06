package codes.yousef.summon.lifecycle

import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * The single coroutine scope owned by a [LifecycleOwner]. The scope is canceled when the owner
 * reaches [LifecycleState.DESTROYED]. Disposing it does not destroy the lifecycle owner.
 */
interface LifecycleCoroutineScope : CoroutineScope {
    val lifecycleOwner: LifecycleOwner
    val isDisposed: Boolean

    /** Cancels this scope. Repeated calls have no effect. */
    fun dispose()
}

/**
 * Returns the lifecycle owner's current scope. Repeated reads return the same scope. A destroyed
 * owner always returns its already-canceled scope and never creates fresh work ownership.
 */
val LifecycleOwner.lifecycleScope: LifecycleCoroutineScope
    get() = lifecycleScope(Dispatchers.Default)

/**
 * Creates the owner's scope with [context] on first access. Later calls return that same scope.
 * Supplying a context is useful when the caller owns a parent job or deterministic scheduler.
 */
fun LifecycleOwner.lifecycleScope(context: CoroutineContext): LifecycleCoroutineScope {
    var candidate: LifecycleCoroutineScopeImpl? = null
    val selected = lifecycleScopeOrCreate {
        LifecycleCoroutineScopeImpl(this, context).also { candidate = it }
    }
    candidate?.start()
    return selected
}

private class LifecycleCoroutineScopeImpl(
    override val lifecycleOwner: LifecycleOwner,
    context: CoroutineContext
) : LifecycleCoroutineScope, LifecycleObserver {
    private val lock = ReentrantLock()
    private val job = SupervisorJob(context[Job])
    private var disposed = false
    private var registered = false

    override val coroutineContext: CoroutineContext = context.minusKey(Job) + job
    override val isDisposed: Boolean get() = lock.withLock { disposed }

    init {
        job.invokeOnCompletion { releaseRegistration() }
    }

    fun start() {
        val shouldRegister = lock.withLock {
            if (disposed || registered || !job.isActive) false else {
                registered = true
                true
            }
        }
        if (shouldRegister) lifecycleOwner.addObserver(this)
    }


    override fun dispose() {
        releaseRegistration()
        if (lifecycleOwner.currentState != LifecycleState.DESTROYED) {
            lifecycleOwner.clearLifecycleScope(this)
        }
        job.cancel()
    }

    private fun releaseRegistration() {
        val wasRegistered = lock.withLock {
            if (disposed) return
            disposed = true
            val value = registered
            registered = false
            value
        }
        if (wasRegistered) lifecycleOwner.removeObserver(this)
    }

    override fun onCreate() = Unit
    override fun onStart() = Unit
    override fun onResume() = Unit
    override fun onPause() = Unit
    override fun onStop() = Unit

    override fun onDestroy() {
        releaseRegistration()
        job.cancel()
    }
}
