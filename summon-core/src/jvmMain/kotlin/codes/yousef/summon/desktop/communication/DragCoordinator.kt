@file:JvmName("DragCoordinatorJvm")

package codes.yousef.summon.desktop.communication

/**
 * JVM implementation of DragCoordinator.
 * Cross-window drag is not applicable on server-side JVM.
 */
actual class DragCoordinator actual constructor(channelName: String) {
    private val listeners = mutableListOf<DragEventListener>()

    actual fun startDrag(data: DragData) {
        // No-op on JVM - cross-window drag not supported
        println("DragCoordinator.startDrag is not supported on JVM")
    }

    actual fun updateDragPosition(dragId: String, x: Double, y: Double) {
        // No-op on JVM
    }

    actual fun endDrag(dragId: String, cancelled: Boolean) {
        // No-op on JVM
    }

    actual fun acceptDrop(dragId: String) {
        // No-op on JVM
    }

    actual fun addListener(listener: DragEventListener) {
        listeners.add(listener)
    }

    actual fun removeListener(listener: DragEventListener) {
        listeners.remove(listener)
    }

    actual fun close() {
        listeners.clear()
    }
}
