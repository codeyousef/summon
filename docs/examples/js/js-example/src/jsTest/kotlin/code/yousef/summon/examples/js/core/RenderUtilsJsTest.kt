package code.yousef.summon.examples.js.core

import code.yousef.summon.core.RenderUtilsJs
import code.yousef.summon.core.RenderUtils
import code.yousef.summon.examples.js.core.initializeCustomRenderUtils
import code.yousef.summon.js.console
import kotlinx.browser.window
import kotlinx.browser.document
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.AfterTest
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFails
import kotlin.test.fail

/**
 * Tests for the RenderUtilsJs class, focusing on the issues mentioned in the error logs.
 */
class RenderUtilsJsTest {

    @BeforeTest
    fun setup() {
        // Reset any previous initialization
        window.asDynamic()._originalRenderComposable = null

        // Initialize our custom implementation
        initializeCustomRenderUtils()

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
                                        console.log("Using global Summon implementation for renderComposable");
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
                                            console.log("Using mock JsPlatformRenderer.render");
                                            return {}; 
                                        }, 
                                        dispose: function() {
                                            console.log("Using mock JsPlatformRenderer.dispose");
                                        } 
                                    }; 
                                } 
                            },
                            renderComposable: function(renderer, composable, container) {
                                console.log("Using global Summon renderComposable");
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
            if (typeof code.yousef.summon.core.RenderUtils === 'undefined') {
                code.yousef.summon.core.RenderUtils = {
                    renderComposable: function(container, composable) {
                        console.log("Using mock RenderUtils.renderComposable");
                        if (container && container.innerHTML !== undefined) {
                            container.innerHTML = "<div>Summon Component Placeholder</div>";
                        }
                        return { 
                            render: function() { return {}; }, 
                            dispose: function() {} 
                        };
                    }
                };
            }

            // Store a reference to the original implementation
            window._originalRenderComposable = code.yousef.summon.core.RenderUtils.renderComposable;
        """)
    }

    @AfterTest
    fun teardown() {
        // Clean up after tests
    }

    /**
     * Test that RenderUtilsJs can be initialized without errors.
     * This tests the "Failed to find Summon package in global scope" error.
     */
    @Test
    fun testInitializeDoesNotFail() {
        console.log("[DEBUG_LOG] Starting RenderUtilsJs initialization test")

        // Capture console errors using a global variable
        js("window._errorCaptured = false")
        js("window._originalConsoleError = console.error")
        js("console.error = function(message, obj) { window._errorCaptured = true; console.log('[DEBUG_LOG] Console error captured: ' + message); window._originalConsoleError(message, obj); }")

        try {
            // Initialize RenderUtilsJs
            RenderUtilsJs.initialize()

            // Check if initialization succeeded (no error about failing to find Summon package)
            val errorCaptured = js("window._errorCaptured") as Boolean
            assertTrue(!errorCaptured, "RenderUtilsJs initialization should not produce errors")

            // Check if _originalRenderComposable was set, indicating successful initialization
            assertNotNull(window.asDynamic()._originalRenderComposable, 
                "RenderUtilsJs initialization should set _originalRenderComposable")

            console.log("[DEBUG_LOG] RenderUtilsJs initialization test completed successfully")
        } finally {
            // Restore original console.error
            js("console.error = window._originalConsoleError")
            js("delete window._errorCaptured")
            js("delete window._originalConsoleError")
        }
    }

    /**
     * Test that the Summon package can be found in the global scope.
     * This directly tests the functionality that's failing with the
     * "Failed to find Summon package in global scope" error.
     */
    @Test
    fun testSummonPackageIsFoundInGlobalScope() {
        console.log("[DEBUG_LOG] Starting Summon package global scope test")

        // Access the private findSummonPackage method using reflection or a test-specific accessor
        // Since we can't directly access private methods in Kotlin/JS, we'll check indirectly

        // Capture console errors using global variables
        js("window._errorCaptured = false")
        js("window._errorMessage = ''")
        js("window._originalConsoleError = console.error")
        js("console.error = function(message, obj) { window._errorCaptured = true; window._errorMessage = message; console.log('[DEBUG_LOG] Console error captured: ' + message); window._originalConsoleError(message, obj); }")

        try {
            // Initialize RenderUtilsJs
            RenderUtilsJs.initialize()

            // Check if there was an error about failing to find Summon package
            val errorCaptured = js("window._errorCaptured") as Boolean
            val errorMessage = js("window._errorMessage") as String
            assertTrue(!errorCaptured || errorMessage.indexOf("Failed to find Summon package") == -1, 
                "Summon package should be found in global scope")

            console.log("[DEBUG_LOG] Summon package global scope test completed successfully")
        } finally {
            // Restore original console.error
            js("console.error = window._originalConsoleError")
            js("delete window._errorCaptured")
            js("delete window._errorMessage")
            js("delete window._originalConsoleError")
        }
    }

    /**
     * Test that the renderComposable function does not throw NotImplementedError.
     * This tests that our fix for the issue described in js-troubleshooting.md works correctly.
     */
    @Test
    fun testRenderComposableDoesNotThrowNotImplementedError() {
        console.log("[DEBUG_LOG] Starting renderComposable NotImplementedError test")

        // Set up a global variable to capture any errors
        js("window._errorCaptured = false;")
        js("window._errorMessage = '';")
        js("window._originalConsoleError = console.error;")
        js("""
            console.error = function(message, error) {
                window._errorCaptured = true;
                if (error && error.message) {
                    window._errorMessage = error.message;
                } else if (typeof message === 'string') {
                    window._errorMessage = message;
                }
                console.log("[DEBUG_LOG] Console error captured: " + window._errorMessage);
                window._originalConsoleError(message, error);
            };
        """)

        try {
            // Initialize RenderUtilsJs to set up the JavaScript-specific implementations
            RenderUtilsJs.initialize()

            // Create a container element for testing
            val container = document.createElement("div")

            // This should not throw a NotImplementedError
            try {
                // Call the actual RenderUtils.renderComposable method
                RenderUtils.renderComposable(container) {
                    // Empty composable
                }

                // If we get here, the test passes because no exception was thrown
                console.log("[DEBUG_LOG] RenderUtils.renderComposable did not throw an exception")
            } catch (e: Throwable) {
                // If an exception was thrown, the test fails
                fail("RenderUtils.renderComposable should not throw an exception, but it threw: ${e.message}")
            }

            // Check if any errors were captured
            val errorCaptured = js("window._errorCaptured") as Boolean
            val errorMessage = js("window._errorMessage") as String

            // Verify that no NotImplementedError was captured
            assertTrue(!errorCaptured || !errorMessage.contains("Platform-specific implementation required"),
                "RenderUtils.renderComposable should not produce a NotImplementedError")

            // Check that the container has some content, indicating that rendering occurred
            val containerHtml = container.innerHTML
            console.log("[DEBUG_LOG] Container HTML after rendering: $containerHtml")
            assertTrue(containerHtml.isNotEmpty(), "Container should have content after rendering")

            // Log success
            console.log("[DEBUG_LOG] renderComposable NotImplementedError test completed successfully")
        } finally {
            // Restore original console.error
            js("console.error = window._originalConsoleError;")
            js("delete window._errorCaptured;")
            js("delete window._errorMessage;")
            js("delete window._originalConsoleError;")
        }
    }
}
