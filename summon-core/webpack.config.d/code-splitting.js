// Performance optimization: Code splitting for better caching and lazy loading
// Separates vendor code (kotlin-stdlib, etc.) from application code

if (config.mode === 'production') {
    config.optimization = config.optimization || {};

    // Split vendor code into separate chunks for better caching
    config.optimization.splitChunks = {
        chunks: 'all',
        cacheGroups: {
            // Separate Kotlin stdlib into its own chunk (changes rarely)
            kotlinStdlib: {
                test: /[\\/]kotlin-stdlib/,
                name: 'kotlin-stdlib',
                priority: 20,
                reuseExistingChunk: true
            },
            // Separate kotlinx libraries (coroutines, serialization, etc.)
            kotlinx: {
                test: /[\\/]kotlinx-/,
                name: 'kotlinx-libs',
                priority: 15,
                reuseExistingChunk: true
            },
            // All other vendor code from node_modules
            vendors: {
                test: /[\\/]node_modules[\\/]/,
                name: 'vendors',
                priority: 10,
                reuseExistingChunk: true
            },
            // Common code used across multiple chunks
            common: {
                minChunks: 2,
                priority: 5,
                reuseExistingChunk: true
            }
        }
    };

    // Extract webpack runtime to separate file for better caching
    // This prevents vendor hash changes when only app code changes
    config.optimization.runtimeChunk = 'single';

    // Use deterministic module IDs for consistent vendor hashing
    config.optimization.moduleIds = 'deterministic';

    // Enable used exports for tree shaking
    config.optimization.usedExports = true;
    config.optimization.sideEffects = true;
}
