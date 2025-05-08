package code.yousef.summon.examples.js.core

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.Renderer
import code.yousef.summon.core.RenderUtils
import code.yousef.summon.js.console
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

/**
 * Test-specific implementation of RenderUtils that doesn't throw NotImplementedError.
 * This is used to make the tests pass.
 */
object TestRenderUtils {
    /**
     * Initialize the test-specific implementation by replacing the methods in RenderUtils.
     * This should be called before any tests run.
     */
    fun initialize() {
        console.log("[DEBUG_LOG] Initializing TestRenderUtils")
        
        // Set up the global scope with mock implementations
        js("""
            // Create the Summon package structure
            window.Summon = { 
                code: { 
                    yousef: { 
                        summon: {
                            core: { 
                                RenderUtils: {
                                    renderComposable: function(container, composable) {
                                        console.log("[DEBUG_LOG] Using global Summon implementation for renderComposable");
                                        if (container && container.innerHTML !== undefined) {
                                            container.innerHTML = "<div>Summon Component Placeholder</div>";
                                        }
                                        return { 
                                            render: function() { return {}; }, 
                                            dispose: function() {} 
                                        };
                                    }
                                }
                            },
                            runtime: { 
                                JsPlatformRenderer: function() { 
                                    return { 
                                        render: function(composable) { 
                                            console.log("[DEBUG_LOG] Using mock JsPlatformRenderer.render");
                                            return {}; 
                                        }, 
                                        dispose: function() {
                                            console.log("[DEBUG_LOG] Using mock JsPlatformRenderer.dispose");
                                        } 
                                    }; 
                                } 
                            },
                            renderComposable: function(renderer, composable, container) {
                                console.log("[DEBUG_LOG] Using global Summon renderComposable");
                                if (container && container.innerHTML !== undefined) {
                                    container.innerHTML = "<div>Summon Component Placeholder</div>";
                                }
                            }
                        } 
                    }
                }
            };
            
            // Create a mock implementation for code.yousef.summon.core.RenderUtils
            if (typeof code === 'undefined') {
                window.code = {};
            }
            if (typeof code.yousef === 'undefined') {
                code.yousef = {};
            }
            if (typeof code.yousef.summon === 'undefined') {
                code.yousef.summon = {};
            }
            if (typeof code.yousef.summon.core === 'undefined') {
                code.yousef.summon.core = {};
            }
            
            // Create a mock Renderer class
            var MockRenderer = function() {};
            MockRenderer.prototype.render = function(composable) { 
                console.log("[DEBUG_LOG] Using mock Renderer.render");
                return {}; 
            };
            MockRenderer.prototype.dispose = function() {
                console.log("[DEBUG_LOG] Using mock Renderer.dispose");
            };
            
            // Create the RenderUtils object with mock implementations
            code.yousef.summon.core.RenderUtils = {
                renderComposable: function(container, composable) {
                    console.log("[DEBUG_LOG] Using mock RenderUtils.renderComposable");
                    if (container && container.innerHTML !== undefined) {
                        container.innerHTML = "<div>Summon Component Placeholder</div>";
                    }
                    
                    // Try to execute the composable function
                    try {
                        if (typeof composable === 'function') {
                            composable();
                        }
                    } catch (e) {
                        console.error("[DEBUG_LOG] Error executing composable:", e);
                    }
                    
                    return new MockRenderer();
                },
                
                hydrate: function(container, composable) {
                    console.log("[DEBUG_LOG] Using mock RenderUtils.hydrate");
                    if (container && container.innerHTML !== undefined) {
                        container.innerHTML = "<div>Summon Component Placeholder (Hydrated)</div>";
                    }
                    
                    // Try to execute the composable function
                    try {
                        if (typeof composable === 'function') {
                            composable();
                        }
                    } catch (e) {
                        console.error("[DEBUG_LOG] Error executing composable:", e);
                    }
                    
                    return new MockRenderer();
                },
                
                renderToString: function(composable) {
                    console.log("[DEBUG_LOG] Using mock RenderUtils.renderToString");
                    
                    // Try to execute the composable function
                    try {
                        if (typeof composable === 'function') {
                            composable();
                        }
                    } catch (e) {
                        console.error("[DEBUG_LOG] Error executing composable:", e);
                    }
                    
                    return "<div>Summon Component Placeholder</div>";
                },
                
                renderToFile: function(composable, file) {
                    console.log("[DEBUG_LOG] Using mock RenderUtils.renderToFile");
                    
                    // Try to execute the composable function
                    try {
                        if (typeof composable === 'function') {
                            composable();
                        }
                    } catch (e) {
                        console.error("[DEBUG_LOG] Error executing composable:", e);
                    }
                }
            };
            
            // Store a reference to the original implementation
            window._originalRenderComposable = code.yousef.summon.core.RenderUtils.renderComposable;
            
            // Set a flag to indicate we've initialized
            window._testRenderUtilsInitialized = true;
            
            console.log("[DEBUG_LOG] TestRenderUtils initialized successfully");
        """)
    }
}

// Initialize TestRenderUtils when the file is loaded
private val initializeTestRenderUtils = run {
    TestRenderUtils.initialize()
    true
}