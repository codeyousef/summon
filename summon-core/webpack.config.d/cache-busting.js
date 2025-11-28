// Performance optimization: Cache busting with content hashes
// Enables long-term caching by including content hash in filenames

if (config.mode === 'production') {
    config.output = config.output || {};

    // Add content hash to output filenames for cache busting
    // Note: The main output filename is controlled by KotlinWebpack task,
    // but we can configure chunk filenames here
    config.output.chunkFilename = '[name].[contenthash:8].bundle.js';

    // Ensure clean output directory on each build
    config.output.clean = true;
}
