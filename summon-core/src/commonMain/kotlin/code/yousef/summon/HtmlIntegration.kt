package codes.yousef.summon

// import codes.yousef.summon.runtime.PlatformRendererProvider // Remove unused import

/**
 * Expect declaration to check if a generic receiver is capable of handling HTML tags.
 * Actual implementations will determine this based on platform capabilities (e.g., kotlinx.html.TagConsumer).
 *
 * @param receiver The receiver object to check.
 * @return True if the receiver can handle HTML tags, false otherwise.
 */
expect fun <T> isHtmlReceiver(receiver: T): Boolean

/**
 * Expect declaration to add a client-side script placeholder to the output.
 * Actual implementations will insert the appropriate script tag or placeholder
 * based on the target platform (HTML, etc.).
 *
 * @param receiver The target receiver (e.g., a TagConsumer).
 * @param scriptId A unique ID for the script tag.
 * @param effectType A string describing the type of effect (used for logging/debugging in actual implementation).
 * @param withCleanup Indicates if a cleanup hook should be associated with the script.
 */
expect fun <T> addClientSideScript(
    receiver: T,
    scriptId: String,
    effectType: String,
    withCleanup: Boolean
) 
