package code.yousef.summon

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.unsafe

/**
 * JVM-specific implementation to check if a receiver is an HTML TagConsumer.
 */
actual fun <T> isHtmlReceiver(receiver: T): Boolean {
    return receiver is TagConsumer<*>
}

/**
 * JVM-specific implementation to add a client-side script to the HTML output.
 */
actual fun <T> addClientSideScript(
    receiver: T,
    scriptId: String,
    effectType: String,
    withCleanup: Boolean
) {
    if (receiver !is TagConsumer<*>) return

    // Use a div container with unsafe HTML to insert the script
    receiver.div {
        unsafe {
            val cleanupAttr = if (withCleanup)
                """data-cleanup="() => { /* $effectType cleanup would execute here */ }" """
            else
                ""

            +"""<script id="$scriptId" onload="() => { /* $effectType would execute here */ }" $cleanupAttr></script>"""
        }
    }
} 
