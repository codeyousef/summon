package codes.yousef.summon.runtime

/**
 * Initializes the JavaScript environment for Summon.
 * This code runs early to set up the necessary global variables and functions.
 */
private object SummonDomInitializer {
    init {
        js(
            """
        // Setup a global parent stack for compatibility with existing code
        if (typeof window.currentParent === 'undefined') {
            // For SSR hydration, prefer the SSR root container over document.body
            var ssrRoot = document.querySelector('[data-summon-hydration="root"]');
            if (ssrRoot) {
                console.log('Initialized currentParent to SSR root container');
                window.currentParent = ssrRoot;
            } else {
                console.log('Initialized currentParent to document.body (no SSR root found)');
                window.currentParent = document.body;
            }
            window._parentStack = [];
            
            // Add compatibility layer for existing code
            window.pushParent = function(element) {
                window._parentStack.push(window.currentParent);
                window.currentParent = element;
            };
            
            window.popParent = function() {
                if (window._parentStack.length > 0) {
                    window.currentParent = window._parentStack.pop();
                } else {
                    // Fallback to SSR root or document.body
                    var fallbackRoot = document.querySelector('[data-summon-hydration="root"]');
                    window.currentParent = fallbackRoot || document.body;
                }
            };
        }
        
        // Add a PlatformRenderer constructor globally
        window.PlatformRenderer = function() {
            return new codes.yousef.summon.runtime.PlatformRenderer();
        };
        """
        )
    }
}

// Force initialization
private val init = SummonDomInitializer