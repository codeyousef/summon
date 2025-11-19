
// Fix for "process.release is undefined" error in browser environment
// This error occurs when dependencies (like Kotlin coroutines or others) check for Node.js environment
// by accessing process.release.name.

const webpack = require('webpack');

config.plugins.push(new webpack.DefinePlugin({
    'process.release': JSON.stringify({ name: 'browser' }),
    'process.env.NODE_ENV': JSON.stringify('production'),
    'process.platform': JSON.stringify('browser'),
    'process.versions': JSON.stringify({ node: false })
}));

// Also ensure we don't pull in the huge node process polyfill if we can avoid it
config.resolve = config.resolve || {};
config.resolve.fallback = config.resolve.fallback || {};
config.resolve.fallback.process = false;
