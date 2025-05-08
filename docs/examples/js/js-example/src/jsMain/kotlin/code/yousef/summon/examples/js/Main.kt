package code.yousef.summon.examples.js

import code.yousef.summon.core.RenderUtils
import code.yousef.summon.core.RenderUtilsJs
import code.yousef.summon.examples.js.core.initializeCustomRenderUtils
import code.yousef.summon.i18n.I18nConfig
import code.yousef.summon.i18n.JsI18nImplementation
import code.yousef.summon.i18n.LayoutDirection
import code.yousef.summon.examples.js.theme.ThemeManager
import code.yousef.summon.init.InitSummon
import code.yousef.summon.init.InitSummonContext
import code.yousef.summon.js.console
import code.yousef.summon.modifier.initializeModifierJs
import code.yousef.summon.initializeModifierJsModule
import code.yousef.summon.style.SummonStyleSheet
import code.yousef.summon.setupHoverEffects
import code.yousef.summon.applyClassesToElements
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event

/**
 * Entry point for the Summon JavaScript example application.
 * This sets up the application, configures internationalization,
 * and renders the main App component.
 * 
 * This implementation follows the Kobweb-inspired approach.
 */
fun main() {
    // Check if RenderUtils and protoOf are defined
    js("""
    if (typeof RenderUtils === 'undefined') {
        console.error("RenderUtils is not defined in main, this should have been defined in index.html");
    }

    if (typeof protoOf === 'undefined') {
        console.error("protoOf is not defined in main, this should have been defined in index.html");
    }
    """)

    // Initialize JavaScript-specific implementations
    RenderUtilsJs.initialize()

    // Initialize custom renderComposable implementation
    initializeCustomRenderUtils()

    // Initialize ModifierJs to expose it to JavaScript
    initializeModifierJs()

    // Initialize SummonStyleSheet before rendering
    try {
        // First, try to initialize SummonStyleSheet directly from Kotlin
        try {
            console.log("[DEBUG] Initializing SummonStyleSheet directly from Kotlin")
            SummonStyleSheet.initialize()
            console.log("[DEBUG] SummonStyleSheet initialized successfully from Kotlin")
        } catch (e: Throwable) {
            console.error("[DEBUG] Error initializing SummonStyleSheet from Kotlin: ${e.message}")

            // Fallback to JavaScript initialization
            js("""
                // Initialize SummonStyleSheet directly
                if (typeof code !== 'undefined' && 
                    typeof code.yousef !== 'undefined' && 
                    typeof code.yousef.summon !== 'undefined' && 
                    typeof code.yousef.summon.style !== 'undefined' && 
                    typeof code.yousef.summon.style.SummonStyleSheet !== 'undefined') {
                    console.log("[DEBUG] Initializing SummonStyleSheet directly from JS");
                    code.yousef.summon.style.SummonStyleSheet.initialize();
                    console.log("[DEBUG] SummonStyleSheet initialized successfully from JS");
                } else {
                    console.error("[DEBUG] SummonStyleSheet not found");
                }
            """)
        }
    } catch (e: Throwable) {
        console.error("[DEBUG] Error initializing SummonStyleSheet: ${e.message}")
    }

    // Initialize ModifierJs module to expose additional functions
    initializeModifierJsModule()

    // Add logging to track modifier usage
//    js("""
//    // Monkey patch Modifier.toStyleString to add logging
//    if (typeof code !== 'undefined' &&
//        typeof code.yousef !== 'undefined' &&
//        typeof code.yousef.summon !== 'undefined' &&
//        typeof code.yousef.summon.modifier !== 'undefined' &&
//        typeof code.yousef.summon.modifier.Modifier !== 'undefined') {
//
//        var originalToStyleString = code.yousef.summon.modifier.Modifier.prototype.toStyleString;
//
//        code.yousef.summon.modifier.Modifier.prototype.toStyleString = function() {
//            var result = originalToStyleString.call(this);
//            console.log("[DEBUG] Modifier.toStyleString called, styles: ", this.styles, " result: ", result);
//            return result;
//        };
//
//        console.log("[DEBUG] Monkey patched Modifier.toStyleString");
//    } else {
//        console.error("[DEBUG] Could not find Modifier class to monkey patch");
//    }
//    """)

    // Initialize LocalPlatformRenderer with a JsPlatformRenderer instance
    try {
        // Create a JsPlatformRenderer instance
        val renderer = js("new code.yousef.summon.runtime.JsPlatformRenderer()")

        // Provide it to LocalPlatformRenderer using the Kobweb approach
        js("""
            try {
                if (typeof code.yousef.summon.runtime.LocalPlatformRenderer !== 'undefined' && 
                    typeof code.yousef.summon.runtime.LocalPlatformRenderer.provides === 'function') {
                    console.log("[DEBUG] Using LocalPlatformRenderer.provides function");
                    console.log("[DEBUG] Providing renderer directly");
                    code.yousef.summon.runtime.LocalPlatformRenderer.provides(renderer);
                } else {
                    console.error("[DEBUG] LocalPlatformRenderer or provides function not found");
                }
            } catch (e) {
                console.error("[DEBUG] Error in JS providing renderer: " + e);
            }
        """)

        console.log("[DEBUG] LocalPlatformRenderer initialized with JsPlatformRenderer instance")
    } catch (e: Throwable) {
        console.error("[DEBUG] Error initializing LocalPlatformRenderer: " + e.message)
        console.error("[DEBUG] Error stack: " + e.stackTraceToString())
    }

    // Configure supported languages
    I18nConfig.configure {
        language("en", "English")
        language("ar", "العربية", LayoutDirection.RTL)
        language("fr", "Français")

        // Set default language
        setDefault("en")
    }

    // Initialize i18n system
    JsI18nImplementation.init()

    // Define the internal function to handle the actual rendering
    fun renderAppInternal() {
        try {
            console.log("[DEBUG] DOM content loaded, attempting to render app")

            // Add logging to track element creation and styling
            js("""
            // Monkey patch createElement to add logging
            var originalCreateElement = document.createElement;
            document.createElement = function(tagName) {
                var element = originalCreateElement.call(document, tagName);
                console.log("[DEBUG] Created element: " + tagName);

                // Add a class to track elements created by our code
                element.classList.add('summon-element');

                // Store the original classList.add method
                var originalClassListAdd = element.classList.add;

                // Override classList.add to log class additions
                element.classList.add = function() {
                    console.log("[DEBUG] Adding classes to " + tagName + " element: ", arguments);
                    return originalClassListAdd.apply(this, arguments);
                };

                // Store the original setAttribute method
                var originalSetAttribute = element.setAttribute;

                // Override setAttribute to log style changes
                element.setAttribute = function(name, value) {
                    if (name === 'style') {
                        console.log("[DEBUG] Setting style attribute on " + tagName + " element: ", value);
                    } else if (name === 'class') {
                        console.log("[DEBUG] Setting class attribute on " + tagName + " element: ", value);
                    }
                    return originalSetAttribute.call(this, name, value);
                };

                // Monitor style property changes
                var originalStyleDescriptor = Object.getOwnPropertyDescriptor(HTMLElement.prototype, 'style');
                if (originalStyleDescriptor && originalStyleDescriptor.set) {
                    try {
                        // Create a proxy for the style object
                        var originalStyle = element.style;
                        var styleProxy = new Proxy(originalStyle, {
                            set: function(target, prop, value) {
                                console.log("[DEBUG] Setting style property on " + tagName + " element: " + prop + " = " + value);
                                target[prop] = value;
                                return true;
                            }
                        });

                        // Replace the style object with our proxy
                        Object.defineProperty(element, 'style', {
                            get: function() { return styleProxy; },
                            set: originalStyleDescriptor.set,
                            enumerable: true,
                            configurable: true
                        });
                    } catch (e) {
                        console.error("[DEBUG] Error setting up style proxy: ", e);
                    }
                }

                return element;
            };
            console.log("[DEBUG] Monkey patched document.createElement");
            """)

            // Get the root element
            val rootElement = document.getElementById("root")
            console.log("[DEBUG] Root element found: " + (rootElement != null))

            if (rootElement == null) {
                console.error("[DEBUG] Root element not found. Make sure there's a div with id 'root' in your HTML.")
                throw IllegalStateException("Root element not found. Make sure there's a div with id 'root' in your HTML.")
            }

            // Cast to HTMLElement
            val htmlElement = rootElement as HTMLElement

            // Clear the root element
            console.log("[DEBUG] Clearing root element")
            htmlElement.innerHTML = ""

            // Explicitly render the App component
            console.log("[DEBUG] Attempting to render App component")
            try {
                RenderUtils.renderComposable(htmlElement) {
                    console.log("[DEBUG] Inside renderComposable block")
                    App {
                        console.log("[DEBUG] Inside App component")
                        // This is where router content would go in a real app
                    }
                }
                console.log("[DEBUG] App rendered successfully")

                // Apply styles after rendering
                try {
                    // First, try to initialize SummonStyleSheet directly from Kotlin
                    try {
                        console.log("[DEBUG] Initializing SummonStyleSheet before applying styles")
                        SummonStyleSheet.initialize()
                        console.log("[DEBUG] SummonStyleSheet initialized successfully before applying styles")
                    } catch (e: Throwable) {
                        console.error("[DEBUG] Error initializing SummonStyleSheet before applying styles: ${e.message}")
                    }

                    // Try to call setupHoverEffects and applyClassesToElements directly from Kotlin
                    try {
                        console.log("[DEBUG] Applying styles directly from Kotlin")
                        setupHoverEffects()
                        applyClassesToElements()
                        console.log("[DEBUG] Styles applied successfully from Kotlin")
                    } catch (e: Throwable) {
                        console.error("[DEBUG] Error applying styles from Kotlin: ${e.message}")

                        // Fallback to JavaScript
                        js("""
                        try {
                            console.log("[DEBUG] Applying styles after rendering");

                            // First, ensure SummonStyleSheet is initialized
                            if (typeof code !== 'undefined' && 
                                typeof code.yousef !== 'undefined' && 
                                typeof code.yousef.summon !== 'undefined' && 
                                typeof code.yousef.summon.style !== 'undefined' && 
                                typeof code.yousef.summon.style.SummonStyleSheet !== 'undefined' &&
                                typeof code.yousef.summon.style.SummonStyleSheet.initialize === 'function') {
                                console.log("[DEBUG] Initializing SummonStyleSheet before applying styles");
                                code.yousef.summon.style.SummonStyleSheet.initialize();
                                console.log("[DEBUG] SummonStyleSheet initialized successfully");
                            } else {
                                console.warn("[DEBUG] SummonStyleSheet not found or initialize method not available");
                            }

                            // Call our global function to apply styles
                            if (typeof window.applySummonStyles === 'function') {
                                console.log("[DEBUG] Calling applySummonStyles");
                                window.applySummonStyles();
                            } else {
                                console.warn("[DEBUG] applySummonStyles function not found");
                            }

                            // Try to call setupHoverEffects from the global namespace
                            if (typeof window.setupHoverEffects === 'function') {
                                console.log("[DEBUG] Calling setupHoverEffects from window");
                                window.setupHoverEffects();
                            } else if (typeof code !== 'undefined' && 
                                typeof code.yousef !== 'undefined' && 
                                typeof code.yousef.summon !== 'undefined' && 
                                typeof code.yousef.summon.setupHoverEffects === 'function') {
                                console.log("[DEBUG] Calling setupHoverEffects from code.yousef.summon");
                                code.yousef.summon.setupHoverEffects();
                            } else {
                                console.warn("[DEBUG] setupHoverEffects function not found in any namespace");
                            }

                            // Try to call applyClassesToElements from the global namespace
                            if (typeof window.applyClassesToElements === 'function') {
                                console.log("[DEBUG] Calling applyClassesToElements from window");
                                window.applyClassesToElements();
                            } else if (typeof code !== 'undefined' && 
                                typeof code.yousef !== 'undefined' && 
                                typeof code.yousef.summon !== 'undefined' && 
                                typeof code.yousef.summon.applyClassesToElements === 'function') {
                                console.log("[DEBUG] Calling applyClassesToElements from code.yousef.summon");
                                code.yousef.summon.applyClassesToElements();
                            } else {
                                console.warn("[DEBUG] applyClassesToElements function not found in any namespace");

                                // Apply basic classes based on tag names as a fallback
                                console.warn("[DEBUG] Using fallback approach to apply styles");
                                var elements = document.querySelectorAll('*');
                                for (var i = 0; i < elements.length; i++) {
                                    var element = elements[i];
                                    var tagName = element.tagName.toLowerCase();

                                    if (tagName === 'button' && !element.classList.contains('summon-button')) {
                                        console.log("[DEBUG] Adding summon-button class to button element");
                                        element.classList.add('summon-button');
                                    } 
                                    else if ((tagName === 'span' || tagName === 'p' || tagName === 'h1' || 
                                             tagName === 'h2' || tagName === 'h3' || tagName === 'h4') && 
                                             !element.classList.contains('summon-text')) {
                                        console.log("[DEBUG] Adding summon-text class to text element");
                                        element.classList.add('summon-text');
                                    }
                                }

                                // Apply layout classes based on computed styles
                                var divs = document.querySelectorAll('div');
                                for (var i = 0; i < divs.length; i++) {
                                    var div = divs[i];
                                    var style = window.getComputedStyle(div);

                                    if (style.display === 'flex') {
                                        if (style.flexDirection === 'row' && !div.classList.contains('summon-row')) {
                                            console.log("[DEBUG] Adding summon-row class to row element");
                                            div.classList.add('summon-row');
                                        } else if (style.flexDirection === 'column' && !div.classList.contains('summon-column')) {
                                            console.log("[DEBUG] Adding summon-column class to column element");
                                            div.classList.add('summon-column');
                                        }
                                    }
                                }
                            }

                            console.log("[DEBUG] Style application completed");
                        } catch (e) {
                            console.error("[DEBUG] JavaScript error applying styles: " + e);
                        }
                        """)
                    }
                    console.log("[DEBUG] Style application attempt completed")
                } catch (e: Throwable) {
                    console.error("[DEBUG] Kotlin error applying styles: " + e.message)
                    console.error("[DEBUG] Error stack: " + e.stackTraceToString())
                }
            } catch (e: Throwable) {
                console.error("[DEBUG] Error rendering App: " + e.message)
                console.error("[DEBUG] Error stack: " + e.stackTraceToString())
            }

            console.log("[DEBUG] App rendering process completed")
        } catch (e: Throwable) {
            console.error("[DEBUG] Uncaught error in renderAppInternal: " + e.message)
            console.error("[DEBUG] Error stack: " + e.stackTraceToString())
        }
    }

    // Function to render the app after resources are loaded
    fun renderApp() {
        // Wait for the DOM to be loaded
        if (document.readyState.toString() == "loading") {
            document.addEventListener("DOMContentLoaded", { _: Event ->
                renderAppInternal()
            })
        } else {
            // DOM already loaded, render immediately
            renderAppInternal()
        }
    }

    // Load translations from resources with a callback to render the app
    JsI18nImplementation.loadLanguageResources("i18n/") {
        console.log("[DEBUG] Language resources loaded, now rendering the app")
        renderApp()
    }

    // Initialize theme system with default theme
    ThemeManager.initialize(isDarkMode = window.matchMedia("(prefers-color-scheme: dark)").matches)
}

/**
 * Initialize Summon with the router.
 * This is called automatically by the Summon framework.
 */
@InitSummon
fun initializeApp(ctx: InitSummonContext) {
    // Register routes if needed
    // In this example, we're using a single-page application
    // so we don't need to register any routes
}

// The App function is now implemented directly in App.kt and annotated with @App
