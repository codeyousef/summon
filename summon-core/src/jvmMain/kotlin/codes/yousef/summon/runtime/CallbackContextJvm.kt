package codes.yousef.summon.runtime

internal actual fun callbackContextKey(): Long = Thread.currentThread().id
