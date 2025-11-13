package codes.yousef.summon.web

import kotlinx.browser.document
import kotlinx.browser.window

/**
 * JavaScript-specific implementations of error boundary utilities.
 */

actual fun enableStaticFormFallbacksPlatform() {
    console.log("Enabling static form fallbacks for JavaScript environment")

    // Make sure all interactive elements are accessible
    val interactiveElements = document.querySelectorAll("[data-onclick-action], [data-onchange-action]")
    for (i in 0 until interactiveElements.length) {
        val element = interactiveElements.item(i)
        if (element != null) {
            js(
                """
                element.style.opacity = '1';
                element.style.pointerEvents = 'auto';
                element.removeAttribute('disabled');
            """
            )
        }
    }

    // Add basic form validation
    val forms = document.querySelectorAll("form")
    for (i in 0 until forms.length) {
        val form = forms.item(i)
        if (form != null) {
            js(
                """
                form.addEventListener('submit', function(e) {
                    var requiredFields = form.querySelectorAll('[required]');
                    for (var j = 0; j < requiredFields.length; j++) {
                        var field = requiredFields[j];
                        if (!field.value.trim()) {
                            e.preventDefault();
                            field.focus();
                            alert('Please fill in all required fields');
                            return;
                        }
                    }
                });
            """
            )
        }
    }
}

actual fun clearWasmCachePlatform(): Boolean {
    return try {
        // Clear any cached WASM modules
        js(
            """
            if ('caches' in window) {
                caches.keys().then(function(names) {
                    names.forEach(function(name) {
                        if (name.includes('wasm') || name.includes('summon')) {
                            caches.delete(name);
                        }
                    });
                });
            }
        """
        )
        true
    } catch (e: Exception) {
        console.error("Failed to clear WASM cache:", e)
        false
    }
}

actual fun verifyJSFallbackPlatform(): Boolean {
    return try {
        // Verify that JavaScript fallback is available
        val hasBasicJS = js("typeof document !== 'undefined' && typeof window !== 'undefined'") as Boolean
        val hasFetch = js("typeof fetch !== 'undefined'") as Boolean
        val hasPromise = js("typeof Promise !== 'undefined'") as Boolean

        hasBasicJS && (hasFetch || hasPromise)
    } catch (e: Exception) {
        console.error("JavaScript fallback verification failed:", e)
        false
    }
}

actual fun clearModuleCachePlatform(): Boolean {
    return try {
        // Clear ES module cache
        js(
            """
            if ('caches' in window) {
                caches.keys().then(function(names) {
                    names.forEach(function(name) {
                        if (name.includes('module') || name.includes('js')) {
                            caches.delete(name);
                        }
                    });
                });
            }

            // Also clear any dynamic imports cache if possible
            if (window.summonModuleCache) {
                window.summonModuleCache.clear();
            }
        """
        )
        true
    } catch (e: Exception) {
        console.error("Failed to clear module cache:", e)
        false
    }
}

actual fun loadCompatibilityShimsPlatform(): Boolean {
    return try {
        // Load polyfills for missing features
        val polyfills = mutableListOf<String>()

        // Check for fetch API
        if (js("typeof fetch === 'undefined'") as Boolean) {
            polyfills.add("https://polyfill.io/v3/polyfill.min.js?features=fetch")
        }

        // Check for Promise support
        if (js("typeof Promise === 'undefined'") as Boolean) {
            polyfills.add("https://polyfill.io/v3/polyfill.min.js?features=Promise")
        }

        // Load polyfills asynchronously
        polyfills.forEach { url ->
            val script = document.createElement("script")
            js("script.src = url; script.async = true; document.head.appendChild(script);")
        }

        true
    } catch (e: Exception) {
        console.error("Failed to load compatibility shims:", e)
        false
    }
}

actual fun checkNetworkConnectivityPlatform(): Boolean {
    return try {
        // Check navigator.onLine
        val isOnline = js("navigator.onLine") as Boolean

        // Additional check with a lightweight network request
        if (isOnline) {
            js(
                """
                fetch('/favicon.ico', {
                    method: 'HEAD',
                    cache: 'no-cache',
                    mode: 'no-cors'
                }).catch(function() {
                    // Network is likely down
                    return false;
                });
            """
            )
        }

        isOnline
    } catch (e: Exception) {
        console.warn("Network connectivity check failed:", e)
        false
    }
}

actual fun retryNetworkOperationPlatform(): Boolean {
    return try {
        // Retry any failed network operations
        js(
            """
            if (window.summonFailedRequests && window.summonFailedRequests.length > 0) {
                window.summonFailedRequests.forEach(function(request) {
                    fetch(request.url, request.options)
                        .then(function(response) {
                            if (response.ok && request.callback) {
                                request.callback(response);
                            }
                        })
                        .catch(function(error) {
                            console.warn('Retry failed for:', request.url, error);
                        });
                });
                window.summonFailedRequests = [];
                return true;
            }
        """
        )
        true
    } catch (e: Exception) {
        console.error("Failed to retry network operations:", e)
        false
    }
}

actual fun enableOfflineModePlatform(): Boolean {
    return try {
        // Enable offline mode by using cached resources
        js(
            """
            if ('serviceWorker' in navigator && 'caches' in window) {
                // Use service worker for offline functionality
                navigator.serviceWorker.ready.then(function(registration) {
                    registration.active.postMessage({
                        command: 'ENABLE_OFFLINE_MODE'
                    });
                });
            } else {
                // Fallback: disable non-essential features
                var nonEssential = document.querySelectorAll('[data-non-essential]');
                nonEssential.forEach(function(element) {
                    element.style.display = 'none';
                });

                // Show offline indicator
                var offlineIndicator = document.createElement('div');
                offlineIndicator.innerHTML = 'Offline Mode';
                offlineIndicator.style.cssText = 'position:fixed;top:0;left:0;right:0;background:#ff6b6b;color:white;text-align:center;padding:8px;z-index:10000;';
                document.body.insertBefore(offlineIndicator, document.body.firstChild);
            }
        """
        )
        true
    } catch (e: Exception) {
        console.error("Failed to enable offline mode:", e)
        false
    }
}

actual fun clearAllCachesPlatform(): Boolean {
    return try {
        js(
            """
            // Clear all browser caches
            if ('caches' in window) {
                caches.keys().then(function(names) {
                    return Promise.all(names.map(function(name) {
                        return caches.delete(name);
                    }));
                });
            }

            // Clear localStorage if possible
            try {
                localStorage.clear();
            } catch (e) {}

            // Clear sessionStorage if possible
            try {
                sessionStorage.clear();
            } catch (e) {}

            // Clear any application-specific caches
            if (window.summonCache) {
                window.summonCache.clear();
            }
        """
        )
        true
    } catch (e: Exception) {
        console.error("Failed to clear all caches:", e)
        false
    }
}

actual fun resetToKnownStatePlatform(): Boolean {
    return try {
        // Reset application to a known good state
        js(
            """
            // Reset any global application state
            if (window.summonState) {
                window.summonState = {};
            }

            // Remove any error indicators
            var errorElements = document.querySelectorAll('[data-error-state]');
            errorElements.forEach(function(element) {
                element.removeAttribute('data-error-state');
            });

            // Reset form states
            var forms = document.querySelectorAll('form');
            forms.forEach(function(form) {
                try {
                    form.reset();
                } catch (e) {}
            });

            // Clear any dynamic content that might be causing issues
            var dynamicElements = document.querySelectorAll('[data-dynamic]');
            dynamicElements.forEach(function(element) {
                element.innerHTML = '';
            });
        """
        )
        true
    } catch (e: Exception) {
        console.error("Failed to reset to known state:", e)
        false
    }
}

actual fun verifyBasicFunctionalityPlatform(): Boolean {
    return try {
        // Verify that basic functionality is working
        val hasDocument = js("typeof document !== 'undefined'") as Boolean
        val hasWindow = js("typeof window !== 'undefined'") as Boolean
        val canCreateElement = try {
            document.createElement("div")
            true
        } catch (e: Exception) {
            false
        }

        val canAccessDOM = try {
            document.body != null
        } catch (e: Exception) {
            false
        }

        val hasEventListeners = try {
            js("typeof document.addEventListener === 'function'") as Boolean
        } catch (e: Exception) {
            false
        }

        hasDocument && hasWindow && canCreateElement && canAccessDOM && hasEventListeners
    } catch (e: Exception) {
        console.error("Basic functionality verification failed:", e)
        false
    }
}

/**
 * JavaScript-specific delay implementation.
 */
actual fun delayPlatform(ms: Int) {
    // In JS environment, we use setTimeout for delay
    // This is a blocking call for the purposes of our error boundary
    js("setTimeout(function(){}, ms)")
}

/**
 * Async delay implementation for JavaScript.
 */
fun delay(ms: Int) {
    js(
        """
        return new Promise(function(resolve) {
            setTimeout(resolve, ms);
        });
    """
    )
}

/**
 * Enhanced error boundary for JavaScript environment.
 */
class JSEnhancedErrorBoundary : ErrorBoundary {

    private val errorHistory = mutableListOf<ErrorRecord>()
    private var consecutiveErrors = 0
    private var lastErrorTime = 0L

    override fun handleError(error: Throwable, context: String): ErrorAction {
        val currentTime = js("Date.now()") as Long
        val errorRecord = ErrorRecord(error, context, currentTime)
        errorHistory.add(errorRecord)

        // Increment consecutive error count if errors are happening rapidly
        if (currentTime - lastErrorTime < 5000) { // Within 5 seconds
            consecutiveErrors++
        } else {
            consecutiveErrors = 1
        }
        lastErrorTime = currentTime

        // Apply escalating error handling
        return when {
            consecutiveErrors >= 5 -> {
                console.error("Too many consecutive errors, enabling emergency fallback")
                ErrorAction.Fallback(RenderingStrategy.STATIC_FALLBACK)
            }

            error is RuntimeException && error.message?.contains("OutOfMemory", ignoreCase = true) == true -> {
                console.error("Out of memory error, reducing functionality")
                ErrorAction.Fallback(RenderingStrategy.JS_COMPATIBLE)
            }

            error is RuntimeException && error.message?.contains("security", ignoreCase = true) == true -> {
                console.error("Security error, using safe fallback")
                ErrorAction.Graceful("Security restrictions prevent this action")
            }

            canRecover(error) -> {
                console.warn("Recoverable error, retrying:", error.message)
                ErrorAction.Retry
            }

            else -> {
                console.error("Unrecoverable error, falling back:", error.message)
                ErrorAction.Fallback(RenderingStrategy.JS_COMPATIBLE)
            }
        }
    }

    override fun reportError(error: Throwable, context: String, metadata: Map<String, Any>) {
        val errorReport = buildMap {
            put("error", error.message ?: "Unknown error")
            put("stack", error.stackTraceToString())
            put("context", context)
            put("timestamp", js("Date.now()"))
            put("userAgent", js("navigator.userAgent"))
            put("url", window.location.href)
            put("consecutiveErrors", consecutiveErrors)
            putAll(metadata)
        }

        // Log to console
        console.error("Summon Error Report:", errorReport)

        // Send to error reporting service if available
        try {
            js(
                """
                if (window.summonErrorReporter) {
                    window.summonErrorReporter.report(errorReport);
                }
            """
            )
        } catch (e: Exception) {
            console.warn("Failed to send error report:", e)
        }

        // Store locally for debugging
        try {
            val existingErrors = js("JSON.parse(localStorage.getItem('summon-errors') || '[]')") as Array<*>
            val updatedErrors = existingErrors.toMutableList()
            updatedErrors.add(errorReport)

            // Keep only last 50 errors
            if (updatedErrors.size > 50) {
                updatedErrors.removeAt(0)
            }

            js("localStorage.setItem('summon-errors', JSON.stringify(updatedErrors))")
        } catch (e: Exception) {
            // localStorage might not be available
        }
    }

    override fun canRecover(error: Throwable): Boolean {
        return when (error) {
            is RuntimeException -> when {
                error.message?.contains("OutOfMemory", ignoreCase = true) == true -> false
                error.message?.contains("security", ignoreCase = true) == true -> false
                else -> consecutiveErrors < 3
            }

            else -> consecutiveErrors < 3 // Allow recovery if not too many consecutive errors
        }
    }

    /**
     * Gets recent error history for debugging.
     */
    fun getErrorHistory(): List<ErrorRecord> {
        return errorHistory.takeLast(20) // Return last 20 errors
    }

    /**
     * Clears error history and resets counters.
     */
    fun clearErrorHistory() {
        errorHistory.clear()
        consecutiveErrors = 0
        lastErrorTime = 0L
    }
}

/**
 * Record of an error occurrence.
 */
data class ErrorRecord(
    val error: Throwable,
    val context: String,
    val timestamp: Long
)

/**
 * Global error handler setup for JavaScript.
 */
fun setupGlobalErrorHandling() {
    // Set up global error handlers
    js(
        """
        window.addEventListener('error', function(event) {
            console.error('Global error:', event.error);
            if (window.SummonErrorBoundary) {
                window.SummonErrorBoundary.handleError(event.error, 'global-error');
            }
        });

        window.addEventListener('unhandledrejection', function(event) {
            console.error('Unhandled promise rejection:', event.reason);
            if (window.SummonErrorBoundary) {
                window.SummonErrorBoundary.handleError(event.reason, 'unhandled-promise');
            }
        });
    """
    )
}