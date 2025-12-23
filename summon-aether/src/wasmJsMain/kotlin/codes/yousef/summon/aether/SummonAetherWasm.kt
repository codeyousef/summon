package codes.yousef.summon.aether

internal actual suspend fun <T> withRenderingContext(block: suspend () -> T): T {
    return block()
}
