package code.yousef.summon

import code.yousef.summon.LifecycleOwner

/**
 * JavaScript implementation of currentLifecycleOwner.
 * Returns a global instance of LifecycleOwner for the JS environment.
 */
actual fun currentLifecycleOwner(): LifecycleOwner {
    return JsLifecycleOwnerInstance
}

/**
 * Singleton instance of LifecycleOwner for the JS environment.
 */
private val JsLifecycleOwnerInstance = LifecycleOwner() 