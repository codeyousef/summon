// Performance optimization: TerserPlugin for JavaScript minification
// This configuration enables aggressive minification for production builds

const TerserPlugin = require('terser-webpack-plugin');

if (config.mode === 'production') {
    config.optimization = config.optimization || {};
    config.optimization.minimize = true;
    config.optimization.minimizer = [
        new TerserPlugin({
            terserOptions: {
                compress: {
                    // Remove console.* calls in production
                    drop_console: true,
                    // Remove debugger statements
                    drop_debugger: true,
                    // Apply multiple optimization passes for better results
                    passes: 2,
                    // Remove dead code
                    dead_code: true,
                    // Inline functions for better performance
                    inline: 2,
                    // Evaluate constant expressions
                    evaluate: true,
                    // Join consecutive variable declarations
                    join_vars: true,
                    // Optimize loops
                    loops: true,
                    // Enable pure_getters for better optimization
                    pure_getters: true,
                    // Collapse single-use variables
                    collapse_vars: true,
                    // Remove unused code
                    unused: true
                },
                mangle: {
                    // Mangle variable names for smaller output
                    toplevel: true
                },
                format: {
                    // Remove comments for smaller output
                    comments: false,
                    // Minimize whitespace
                    beautify: false
                }
            },
            // Enable parallel compression for faster builds
            parallel: true,
            // Don't extract comments to separate file
            extractComments: false
        })
    ];

    // Disable source maps in production for smaller bundles
    // Comment this line and set devtool = 'source-map' if debugging is needed
    config.devtool = false;
}
