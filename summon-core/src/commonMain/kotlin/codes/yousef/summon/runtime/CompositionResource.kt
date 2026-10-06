package codes.yousef.summon.runtime

/** A resource owned by a composition slot, released on replacement or removal. */
internal interface CompositionResource {
    fun dispose()
}
