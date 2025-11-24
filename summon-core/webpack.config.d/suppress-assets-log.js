
// Disable Webpack caching to prevent "assets by status ... [cached]" output
// which causes a parsing error in Kotlin Gradle Plugin 2.2.21.
config.cache = false;

// Also try to disable filesystem caching explicitly if it was an object
if (typeof config.cache === 'object') {
    config.cache = false;
}

// Hide cached assets from stats output to avoid the parser error
if (!config.stats) {
    config.stats = {};
}

if (typeof config.stats === 'string') {
    config.stats = {
        preset: config.stats,
        assets: false, // Hide assets completely to avoid the [cached] group header
        cachedAssets: false,
        cached: false,
        cachedModules: false,
        groupAssetsByEmitStatus: false
    };
} else if (typeof config.stats === 'object') {
    config.stats.assets = false; // Hide assets completely to avoid the [cached] group header
    config.stats.cachedAssets = false;
    config.stats.cached = false;
    config.stats.cachedModules = false;
    config.stats.groupAssetsByEmitStatus = false;
}
