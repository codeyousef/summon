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
            window.currentParent = document.body;
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
                    window.currentParent = document.body;
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