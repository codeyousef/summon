package codes.yousef.summon.desktop.communication

import codes.yousef.summon.desktop.window.WindowManager

/**
 * JS implementation of DragCoordinator using BroadcastChannel.
 */
actual class DragCoordinator actual constructor(channelName: String) {
    private val channel = createBroadcastChannel(channelName)
    private val listeners = mutableListOf<DragEventListener>()
    private var currentDragId: String? = null
    private var unsubscribe: (() -> Unit)? = null

    init {
        unsubscribe = channel.onMessage { json ->
            try {
                val message = deserializeDragMessage(json)
                notifyListeners(message)
            } catch (e: Exception) {
                // Ignore malformed messages
            }
        }
    }

    actual fun startDrag(data: DragData) {
        currentDragId = data.dragId
        val message = DragMessage.DragStart(data)
        channel.postMessage(message.serialize())
    }

    actual fun updateDragPosition(dragId: String, x: Double, y: Double) {
        val message = DragMessage.DragMove(dragId, x, y)
        channel.postMessage(message.serialize())
    }

    actual fun endDrag(dragId: String, cancelled: Boolean) {
        val message = DragMessage.DragEnd(dragId, cancelled)
        channel.postMessage(message.serialize())
        if (currentDragId == dragId) {
            currentDragId = null
        }
    }

    actual fun acceptDrop(dragId: String) {
        val windowId = WindowManager.currentWindowId ?: "unknown"
        val message = DragMessage.DropAccepted(dragId, windowId)
        channel.postMessage(message.serialize())
    }

    actual fun addListener(listener: DragEventListener) {
        listeners.add(listener)
    }

    actual fun removeListener(listener: DragEventListener) {
        listeners.remove(listener)
    }

    actual fun close() {
        unsubscribe?.invoke()
        channel.close()
        listeners.clear()
    }

    private fun notifyListeners(message: DragMessage) {
        listeners.forEach { listener ->
            when (message) {
                is DragMessage.DragStart -> listener.onDragStart(message.data)
                is DragMessage.DragMove -> listener.onDragMove(message.dragId, message.x, message.y)
                is DragMessage.DragEnd -> listener.onDragEnd(message.dragId, message.cancelled)
                is DragMessage.DropAccepted -> listener.onDropAccepted(message.dragId, message.targetWindow)
            }
        }
    }
}
