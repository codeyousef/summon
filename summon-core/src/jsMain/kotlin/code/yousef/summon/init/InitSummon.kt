package code.yousef.summon.init

import code.yousef.summon.routing.Router

/**
 * An annotation which identifies a function as one which will be called when the page opens, before nodes are laid out.
 * The function should take an [InitSummonContext] as its only parameter.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class InitSummon

/**
 * Various classes useful for methods that are called when a page is first loaded.
 */
class InitSummonContext(val config: MutableSummonConfig, val router: Router)

/**
 * Initialize Summon with the given router and initialization function.
 */
fun initSummon(router: Router, init: (InitSummonContext) -> Unit) {
    val config = MutableSummonConfig()
    init(InitSummonContext(config, router))
    MutableSummonConfigInstance = config
}

/**
 * Configuration for Summon applications.
 */
interface SummonConfig {
    companion object {
        val Instance: SummonConfig get() = MutableSummonConfigInstance
    }
}

/**
 * Mutable implementation of SummonConfig.
 */
class MutableSummonConfig : SummonConfig {
    // Add configuration properties as needed
}

/**
 * Global instance of MutableSummonConfig.
 */
internal var MutableSummonConfigInstance: MutableSummonConfig = MutableSummonConfig()