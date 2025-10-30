package code.yousef.summon.runtime

internal actual class CallbackRegistryLock

internal actual fun <T> withCallbackRegistryLock(lock: CallbackRegistryLock, block: () -> T): T = block()
