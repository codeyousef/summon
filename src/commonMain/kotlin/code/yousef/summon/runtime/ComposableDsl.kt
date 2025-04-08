package runtime

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Marks a class as part of the Composable DSL.
 * 
 * This annotation is used to provide type safety and IDE support for receiver scopes
 * in composable functions. It helps prevent accidental use of extension functions from
 * different scopes than intended.
 */
@DslMarker
annotation class ComposableDsl 
