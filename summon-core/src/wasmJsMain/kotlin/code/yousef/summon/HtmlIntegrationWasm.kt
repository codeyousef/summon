package codes.yousef.summon

/**
 * WASM implementation of HTML integration functions.
 */
actual fun <T> isHtmlReceiver(receiver: T): Boolean = false

actual fun <T> addClientSideScript(
    receiver: T,
    scriptId: String,
    effectType: String,
    withCleanup: Boolean
) = Unit