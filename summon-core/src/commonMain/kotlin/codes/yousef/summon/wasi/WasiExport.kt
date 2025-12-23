package codes.yousef.summon.wasi

import codes.yousef.summon.cbor.UiTree
import codes.yousef.summon.cbor.UiTreeSerializer
import kotlin.native.concurrent.ThreadLocal

/**
 * This object serves as the bridge between the Summon runtime and the WASI host environment.
 * It exports functions that the host can call to retrieve the UI tree and send events.
 */
@ThreadLocal
object WasiExport {
    private var currentTree: UiTree? = null
    
    /**
     * Updates the current UI tree. This should be called by the Summon runtime
     * whenever a recomposition occurs.
     */
    fun updateTree(tree: UiTree) {
        currentTree = tree
    }

    /**
     * Returns a pointer to the serialized UI tree.
     * The host is responsible for reading the memory and freeing it if necessary (though usually
     * in WASM linear memory, the guest manages it).
     * 
     * Note: In a real WASM export, we would return a pointer and length.
     * Since we are in common code, we simulate the logic.
     * The actual `@JsExport` or `@WasmExport` annotations would be platform specific.
     */
    fun getSerializedTree(): ByteArray {
        return currentTree?.let { UiTreeSerializer.serialize(it) } ?: ByteArray(0)
    }

    /**
     * Handles an event from the host.
     * 
     * @param eventType The type of event (e.g., "click", "input")
     * @param nodeId The ID of the target node
     * @param data JSON/CBOR encoded event data
     */
    fun handleEvent(eventType: String, nodeId: String, data: ByteArray) {
        // Dispatch event to Summon runtime
        println("WASI: Received event $eventType for node $nodeId")
    }
}

// These would be the actual exported symbols in a WASM-specific source set.
// For now, we define them here to satisfy the architecture requirement.

/**
 * Exported function to get the UI tree.
 * Returns a pointer to the buffer (simulated).
 */
fun summon_entry(): Int {
    val bytes = WasiExport.getSerializedTree()
    // In a real implementation, we would allocate memory, copy bytes, and return the pointer.
    return bytes.size // Placeholder
}

/**
 * Exported function to handle events.
 */
fun summon_event(typePtr: Int, typeLen: Int, idPtr: Int, idLen: Int, dataPtr: Int, dataLen: Int) {
    // In a real implementation, we would read strings/bytes from memory pointers
    // and call WasiExport.handleEvent
}
