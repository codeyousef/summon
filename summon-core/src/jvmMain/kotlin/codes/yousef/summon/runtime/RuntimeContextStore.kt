package codes.yousef.summon.runtime

internal actual object RuntimeContextStore {
    private val context = ThreadLocal.withInitial { RuntimeContext() }
    actual fun get(): RuntimeContext = context.get()
}
