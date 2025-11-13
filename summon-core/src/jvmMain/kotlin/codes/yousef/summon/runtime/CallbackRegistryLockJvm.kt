package codes.yousef.summon.runtime

internal actual class CallbackRegistryLock {
    val monitor = Any()
}

internal actual fun <T> withCallbackRegistryLock(lock: CallbackRegistryLock, block: () -> T): T =
    synchronized(lock.monitor) { block() }
