package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable

/**
 * Gives [block] remembered-state and effect identity within its immediate parent group.
 * Use stable immutable keys, including account/route identity where appropriate. Explicit sibling
 * keys must be unique. Changing a key or omitting the group releases its owned effects and caches.
 * DOM identity remains a separate renderer modifier; this function does not add DOM attributes.
 * Without an active composer the block executes normally, for example in stateless SSR.
 * Ordinary calls inside the block remain positional; wrap conditional branches and list items.
 */
@Composable
fun <T> key(vararg keys: Any?, block: @Composable () -> T): T {
    require(keys.isNotEmpty()) { "A composition key requires at least one value" }
    val composer = CompositionLocal.currentComposer ?: return block()
    composer.startGroup(ExplicitCompositionKey(keys.toList()))
    return try { block() } finally { composer.endGroup() }
}

/** Internal component occurrences isolate local slots; callers key dynamic siblings explicitly. */
internal fun <T> compositionGroup(group: Any, block: () -> T): T {
    val composer = CompositionLocal.currentComposer ?: return block()
    composer.startGroup(group)
    return try { block() } finally { composer.endGroup() }
}
