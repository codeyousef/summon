@file:JvmName("DragCoordinatorJvm")

package codes.yousef.summon.desktop.communication

/**
 * JVM implementation of DragCoordinator.
 * Cross-window drag is not applicable on server-side JVM.
 */
actual class DragCoordinator actual constructor(channelName: String) {
    private val listeners = mutableListOf<DragEventListener>()

    /**
     * Starts drag.
     *
     * @param data The data value.
     */
    actual fun startDrag(data: DragData) {
        // No-op on JVM - cross-window drag not supported
        println("DragCoordinator.startDrag is not supported on JVM")
    }

    /**
     * Updates drag position.
     *
     * @param dragId The drag id value.
     * @param x The x value.
     * @param y The y value.
     */
    actual fun updateDragPosition(dragId: String, x: Double, y: Double) {
        // No-op on JVM
    }

    /**
     * Executes the end drag operation.
     *
     * @param dragId The drag id value.
     * @param cancelled The cancelled value.
     */
    actual fun endDrag(dragId: String, cancelled: Boolean) {
        // No-op on JVM
    }

    /**
     * Executes the accept drop operation.
     *
     * @param dragId The drag id value.
     */
    actual fun acceptDrop(dragId: String) {
        // No-op on JVM
    }

    /**
     * Adds listener.
     *
     * @param listener The listener value.
     */
    actual fun addListener(listener: DragEventListener) {
        listeners.add(listener)
    }

    /**
     * Removes listener.
     *
     * @param listener The listener value.
     */
    actual fun removeListener(listener: DragEventListener) {
        listeners.remove(listener)
    }

    /** Closes the operation. */
    actual fun close() {
        listeners.clear()
    }
}
