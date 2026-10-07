package codes.yousef.summon

// import codes.yousef.summon.runtime.PlatformRendererProvider // Remove unused import

/**
 * Expect declaration to check if a generic receiver is capable of handling HTML tags.
 * Actual implementations will determine this based on platform capabilities (e.g., kotlinx.HTML.TagConsumer).
 *
 * @param receiver The receiver object to check.
 * @return True if the receiver can handle HTML tags, false otherwise.
 */
expect fun <T> isHtmlReceiver(receiver: T): Boolean
