package codes.yousef.summon.runtime

/** Synchronous composition context; JVM contexts belong to the executing thread. */
internal class RuntimeContext {
    var composer: Composer? = null
    var recomposer: Recomposer? = null
    val localValues = mutableMapOf<Any, Any?>()
}

internal expect object RuntimeContextStore {
    fun get(): RuntimeContext
}

internal fun <T, R> withCompositionLocal(
    provider: CompositionLocalProvider<T>,
    value: T,
    block: () -> R
): R {
    val values = RuntimeContextStore.get().localValues
    val present = values.containsKey(provider)
    val previous = values[provider]
    values[provider] = value
    return try { block() } finally {
        if (present) values[provider] = previous else values.remove(provider)
    }
}

internal fun <R> withPlatformRenderer(renderer: PlatformRenderer?, block: () -> R): R {
    val previous = PlatformRendererStore.get()
    PlatformRendererStore.set(renderer)
    return try {
        if (renderer == null) block() else withCompositionLocal(LocalPlatformRenderer, renderer, block)
    } finally { PlatformRendererStore.set(previous) }
}
