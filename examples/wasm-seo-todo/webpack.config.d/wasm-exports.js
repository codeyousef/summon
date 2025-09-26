// Webpack configuration to expose WASM exports globally
const webpack = require('webpack');

// Add a define plugin to mark that we need to expose WASM exports
config.plugins = config.plugins || [];
config.plugins.push(
    new webpack.DefinePlugin({
        'process.env.EXPOSE_WASM_EXPORTS': JSON.stringify(true)
    })
);

// Ensure the WASM module exports are available globally
config.optimization = config.optimization || {};
config.optimization.runtimeChunk = 'single';