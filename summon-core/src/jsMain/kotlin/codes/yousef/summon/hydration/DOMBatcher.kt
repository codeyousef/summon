package codes.yousef.summon.hydration

import kotlinx.browser.window

/**
 * DOM operation batcher that implements read-write phase separation.
 *
 * This class batches DOM operations to avoid layout thrashing by ensuring
 * all reads happen before any writes. This is critical for hydration
 * performance as interleaved reads and writes force synchronous layout
 * calculations.
 *
 * ## Key Features
 *
 * - **Read-Write Separation**: All queued reads execute before writes (FR-003)
 * - **RAF Scheduling**: Operations execute in requestAnimationFrame for optimal timing
 * - **Automatic Flushing**: Batches flush automatically on next animation frame
 * - **Error Isolation**: Individual operation failures don't affect others
 *
 * ## Usage
 *
 * ```kotlin
 * val batcher = DOMBatcher.instance
 *
 * // Queue a read operation
 * batcher.read {
 *     val height = element.offsetHeight
 *     // Use height...
 * }
 *
 * // Queue a write operation
 * batcher.write {
 *     element.style.display = "block"
 * }
 *
 * // Operations will execute in order: all reads, then all writes
 * ```
 *
 * ## Layout Thrashing Prevention
 *
 * Without batching:
 * ```kotlin
 * // BAD: Causes layout thrashing
 * val h1 = el1.offsetHeight  // Read (triggers layout)
 * el1.style.height = "100px" // Write (invalidates layout)
 * val h2 = el2.offsetHeight  // Read (triggers layout AGAIN)
 * el2.style.height = "100px" // Write (invalidates layout)
 * ```
 *
 * With batching:
 * ```kotlin
 * // GOOD: Single layout calculation
 * batcher.read { h1 = el1.offsetHeight }  // Queued
 * batcher.write { el1.style.height = "100px" }  // Queued
 * batcher.read { h2 = el2.offsetHeight }  // Queued
 * batcher.write { el2.style.height = "100px" }  // Queued
 * // On RAF: Read h1, Read h2, Write el1, Write el2
 * ```
 */
class DOMBatcher private constructor() {

    private val readQueue = ArrayDeque<() -> Unit>()
    private val writeQueue = ArrayDeque<() -> Unit>()
    private var rafHandle: Int? = null
    private var isFlushing = false

    /**
     * Enable/disable performance logging.
     */
    var enableLogging = false

    /**
     * Callback invoked when a batch flush completes.
     */
    var onFlushComplete: ((readCount: Int, writeCount: Int, elapsedMs: Double) -> Unit)? = null

    /**
     * Queue a DOM read operation.
     *
     * Read operations are executed first, before any write operations.
     * This ensures that reading layout properties doesn't trigger forced
     * synchronous layout due to pending writes.
     *
     * @param operation The read operation to queue
     */
    fun read(operation: () -> Unit) {
        readQueue.addLast(operation)
        scheduleFlush()
    }

    /**
     * Queue a DOM write operation.
     *
     * Write operations are executed after all read operations complete.
     * This ensures that writes don't invalidate layout during the read phase.
     *
     * @param operation The write operation to queue
     */
    fun write(operation: () -> Unit) {
        writeQueue.addLast(operation)
        scheduleFlush()
    }

    /**
     * Force immediate flush of all queued operations.
     *
     * Use sparingly - the automatic RAF-based flushing is preferred
     * for optimal performance.
     */
    fun flush() {
        rafHandle?.let { cancelAnimationFrame(it) }
        rafHandle = null
        executeFlush()
    }

    /**
     * Clear all queued operations without executing them.
     */
    fun clear() {
        rafHandle?.let { cancelAnimationFrame(it) }
        rafHandle = null
        readQueue.clear()
        writeQueue.clear()
    }

    /**
     * Check if there are pending operations.
     */
    fun hasPendingOperations(): Boolean = readQueue.isNotEmpty() || writeQueue.isNotEmpty()

    /**
     * Get counts of pending operations.
     */
    fun pendingCounts(): Pair<Int, Int> = readQueue.size to writeQueue.size

    private fun scheduleFlush() {
        if (rafHandle != null || isFlushing) return

        rafHandle = requestAnimationFrame { executeFlush() }
    }

    private fun executeFlush() {
        if (isFlushing) return
        isFlushing = true
        rafHandle = null

        val startTime = performanceNow()
        val readCount = readQueue.size
        val writeCount = writeQueue.size

        if (enableLogging && (readCount > 0 || writeCount > 0)) {
            console.log("[DOMBatcher] Flushing: $readCount reads, $writeCount writes")
        }

        // Phase 1: Execute all reads
        while (readQueue.isNotEmpty()) {
            val operation = readQueue.removeFirst()
            try {
                operation()
            } catch (e: Throwable) {
                console.error("[DOMBatcher] Read operation failed: ${e.message}")
            }
        }

        // Phase 2: Execute all writes
        while (writeQueue.isNotEmpty()) {
            val operation = writeQueue.removeFirst()
            try {
                operation()
            } catch (e: Throwable) {
                console.error("[DOMBatcher] Write operation failed: ${e.message}")
            }
        }

        val elapsed = performanceNow() - startTime
        isFlushing = false

        if (enableLogging && (readCount > 0 || writeCount > 0)) {
            console.log("[DOMBatcher] Flush complete: ${elapsed.toInt()}ms")
        }

        onFlushComplete?.invoke(readCount, writeCount, elapsed)

        // If new operations were queued during flush, schedule another
        if (hasPendingOperations()) {
            scheduleFlush()
        }
    }

    companion object {
        /**
         * Singleton instance of the DOMBatcher.
         */
        val instance: DOMBatcher by lazy { DOMBatcher() }

        /**
         * Create a new batcher instance (useful for testing).
         */
        fun create(): DOMBatcher = DOMBatcher()
    }
}

/**
 * External console for logging.
 */
private external val console: DOMBatcherConsole

private external interface DOMBatcherConsole {
    fun log(message: String)
    fun error(message: String)
}
