package codes.yousef.summon.core

import kotlinx.html.FlowContent as KotlinxFlowContent

/**
 * JS implementation of FlowContentCompat that bridges to kotlinx.HTML.FlowContent.
 * This allows JS platform code to work with the existing kotlinx.HTML ecosystem
 * while providing a cross-platform abstraction for WASM compatibility.
 *
 * @property kotlinxFlowContent underlying JS HTML builder
 */
class FlowContentJS(val kotlinxFlowContent: KotlinxFlowContent) : FlowContentCompat() {
    // This class serves as a bridge between our abstraction and kotlinx.html
    // Platform-specific renderers will use this to access the underlying kotlinx.html.FlowContent
}

/**
 * Extension function to get the underlying kotlinx.HTML.FlowContent from our abstraction.
 * This is used by JS platform renderers to access kotlinx.HTML functionality.
 */
fun FlowContentCompat.asKotlinxFlowContent(): KotlinxFlowContent? {
    return (this as? FlowContentJS)?.kotlinxFlowContent
}

/**
 * Helper function to create a FlowContentCompat wrapper around kotlinx.HTML.FlowContent.
 */
fun KotlinxFlowContent.asFlowContentCompat(): FlowContentCompat {
    return FlowContentJS(this)
}