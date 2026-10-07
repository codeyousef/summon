package codes.yousef.summon

import kotlinx.html.TagConsumer

/**
 * JS-specific implementation to check if a receiver is an HTML TagConsumer.
 */
actual fun <T> isHtmlReceiver(receiver: T): Boolean {
    return receiver is TagConsumer<*>
}
