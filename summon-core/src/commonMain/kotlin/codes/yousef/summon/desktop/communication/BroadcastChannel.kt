/**
 * # Broadcast Channel
 *
 * Cross-window/cross-tab communication channel using the BroadcastChannel API.
 * Enables real-time message passing between browser contexts (tabs, windows, iframes)
 * that share the same origin.
 *
 * ## Features
 *
 * - **Cross-Tab Messaging**: Send messages that are received by all other tabs
 * - **Type-Safe**: Generic type parameter with serialization support
 * - **Simple API**: Post messages and register handlers
 * - **Auto-Cleanup**: Close channels when no longer needed
 *
 * ## Usage Examples
 *
 * ### Basic Usage
 *
 * ```kotlin
 * // Create a channel
 * val channel = SummonBroadcastChannel<String>("my-channel")
 *
 * // Listen for messages
 * channel.onMessage { message ->
 *     println("Received: $message")
 * }
 *
 * // Send a message to all other tabs
 * channel.postMessage("Hello from this tab!")
 *
 * // Clean up when done
 * channel.close()
 * ```
 *
 * ### Typed Messages
 *
 * ```kotlin
 * @Serializable
 * data class UserAction(val type: String, val userId: String)
 *
 * val channel = createTypedBroadcastChannel<UserAction>(
 *     name = "user-actions",
 *     serializer = { Json.encodeToString(it) },
 *     deserializer = { Json.decodeFromString(it) }
 * )
 *
 * channel.onMessage { action ->
 *     when (action.type) {
 *         "login" -> handleLogin(action.userId)
 *         "logout" -> handleLogout(action.userId)
 *     }
 * }
 *
 * channel.postMessage(UserAction("login", "user123"))
 * ```
 *
 * ### Synchronizing State
 *
 * ```kotlin
 * // Leader election / tab coordination
 * val leaderChannel = SummonBroadcastChannel<String>("leader-election")
 *
 * leaderChannel.onMessage { message ->
 *     if (message == "leader-request") {
 *         // Respond if we're the leader
 *         if (isLeader) {
 *             leaderChannel.postMessage("leader-response:$tabId")
 *         }
 *     }
 * }
 * ```
 *
 * ## Platform Support
 *
 * | Platform | Implementation |
 * |----------|---------------|
 * | JS       | BroadcastChannel API |
 * | WASM     | BroadcastChannel API |
 * | JVM      | In-process event bus (no cross-process support) |
 *
 * @since 0.7.0
 */
package codes.yousef.summon.desktop.communication

/**
 * A broadcast channel for cross-tab/cross-window communication.
 *
 * Messages posted to the channel are received by all other tabs/windows
 * with a channel of the same name on the same origin.
 *
 * Note: The sender does NOT receive their own messages.
 *
 * @param T The type of messages to send and receive
 */
interface SummonBroadcastChannel<T> {
    /**
     * The name of this channel.
     */
    val name: String

    /**
     * Posts a message to the channel.
     *
     * The message will be delivered to all other tabs/windows listening
     * to a channel with the same name.
     *
     * @param message The message to send
     */
    fun postMessage(message: T)

    /**
     * Registers a handler to receive messages from the channel.
     *
     * Multiple handlers can be registered. Each handler will receive
     * a copy of each message.
     *
     * @param handler Callback invoked when a message is received
     * @return A function to unregister the handler
     */
    fun onMessage(handler: (T) -> Unit): () -> Unit

    /**
     * Closes the channel and releases resources.
     *
     * After closing, no more messages can be sent or received.
     * All registered handlers will be removed.
     */
    fun close()

    /**
     * Checks if the channel is still open.
     */
    fun isOpen(): Boolean
}

/**
 * Creates a broadcast channel for string messages.
 *
 * @param name The channel name (must be the same across tabs to communicate)
 * @return A new SummonBroadcastChannel for string messages
 */
expect fun createBroadcastChannel(name: String): SummonBroadcastChannel<String>

/**
 * Creates a typed broadcast channel with custom serialization.
 *
 * @param name The channel name
 * @param serializer Function to convert messages to strings
 * @param deserializer Function to convert strings back to messages
 * @return A new typed SummonBroadcastChannel
 */
fun <T> createTypedBroadcastChannel(
    name: String,
    serializer: (T) -> String,
    deserializer: (String) -> T
): SummonBroadcastChannel<T> {
    val stringChannel = createBroadcastChannel(name)

    return object : SummonBroadcastChannel<T> {
        override val name: String = stringChannel.name

        override fun postMessage(message: T) {
            stringChannel.postMessage(serializer(message))
        }

        override fun onMessage(handler: (T) -> Unit): () -> Unit {
            return stringChannel.onMessage { serialized ->
                try {
                    val message = deserializer(serialized)
                    handler(message)
                } catch (e: Exception) {
                    // Log deserialization error but don't crash
                    println("BroadcastChannel: Failed to deserialize message: ${e.message}")
                }
            }
        }

        override fun close() = stringChannel.close()
        override fun isOpen() = stringChannel.isOpen()
    }
}

/**
 * Creates a typed broadcast channel with custom serialization (explicit name).
 */
fun <T> typedBroadcastChannel(
    name: String,
    serializer: (T) -> String,
    deserializer: (String) -> T
): SummonBroadcastChannel<T> = createTypedBroadcastChannel(name, serializer, deserializer)
