package codes.yousef.summon.runtime

internal actual object RuntimeContextStore {
    private val context = RuntimeContext()
    actual fun get(): RuntimeContext = context
}
