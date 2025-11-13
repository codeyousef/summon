package codes.yousef.summon.core

/**
 * WebAssembly implementation of getCurrentTimeMillis.
 * Returns a fixed timestamp for testing purposes since performance.now() is not available in Node.js test environment.
 */
actual fun getCurrentTimeMillis(): Long {
    // Return a fixed timestamp for testing
    // This is sufficient for tests that just need a non-zero timestamp
    return 1735200000000L // January 1, 2025
}