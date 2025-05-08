package code.yousef.summon.examples.js.core

import code.yousef.summon.annotation.Composable
import code.yousef.summon.i18n.Language
import code.yousef.summon.i18n.LayoutDirection
import code.yousef.summon.i18n.LocalLanguage
import code.yousef.summon.i18n.LocalLayoutDirection
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.PlatformRenderer
import org.w3c.dom.HTMLElement

/**
 * Custom implementation of renderComposable for the JavaScript platform.
 * This implementation avoids the NotImplementedError by directly calling the composable function.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
@JsName("renderComposable")
fun renderComposable(renderer: Any, composable: @Composable () -> Unit, container: HTMLElement) {
    console.log("[DEBUG] Using custom renderComposable implementation")
    console.log("[DEBUG] Container: " + container.tagName + "#" + container.id)

    // Clear the container first
    console.log("[DEBUG] Clearing container")
    container.innerHTML = ""

    // Store the previous parent
    console.log("[DEBUG] Storing previous parent")
    val previousParent = js("currentParent")
    console.log("[DEBUG] Previous parent: " + js("currentParent.tagName") + "#" + js("currentParent.id"))

    // Set the container as the current parent
    console.log("[DEBUG] Setting container as current parent")
    js("currentParent = container")
    console.log("[DEBUG] Current parent: " + js("currentParent.tagName") + "#" + js("currentParent.id"))

    try {
        // Just call the composable function directly
        // This is a fallback that doesn't use the renderer's renderComposable method
        // which might not be implemented and could cause NotImplementedError
        console.log("[DEBUG] Using direct composable call to avoid NotImplementedError")
        composable()
        console.log("[DEBUG] Composable function executed successfully")
    } catch (e: Exception) {
        console.error("[DEBUG] Error executing composable:", e)
        console.error("[DEBUG] Error message: " + e.message)
        console.error("[DEBUG] Error stack: " + e.stackTraceToString())
    } finally {
        // Restore the previous parent
        console.log("[DEBUG] Restoring previous parent")
        js("currentParent = previousParent")
        console.log("[DEBUG] Current parent restored to: " + js("currentParent.tagName") + "#" + js("currentParent.id"))
    }
}

/**
 * CompositionLocalProvider for JavaScript platform.
 * This is a simple implementation that ensures CompositionLocal values are properly provided.
 */
@Composable
fun CompositionLocalProvider(
    content: @Composable () -> Unit,
    vararg providers: Pair<Any, Any?>
) {
    console.log("[DEBUG] Using CompositionLocalProvider with " + providers.size + " providers")

    // Store previous values
    val prevValues = mutableMapOf<Any, Any?>()

    try {
        // Set new values
        providers.forEach { (provider, value) ->
            try {
                console.log("[DEBUG] Setting provider: " + provider)

                when (provider) {
                    LocalLanguage -> {
                        prevValues[LocalLanguage] = LocalLanguage.current
                        if (value is Function0<*>) {
                            @Suppress("UNCHECKED_CAST")
                            LocalLanguage.provides(value as () -> Language)
                        } else if (value is Language) {
                            LocalLanguage.provides { value }
                        }
                    }
                    LocalLayoutDirection -> {
                        prevValues[LocalLayoutDirection] = LocalLayoutDirection.current
                        if (value is Function0<*>) {
                            @Suppress("UNCHECKED_CAST")
                            LocalLayoutDirection.provides(value as () -> LayoutDirection)
                        } else if (value is LayoutDirection) {
                            LocalLayoutDirection.provides { value }
                        }
                    }
                    LocalPlatformRenderer -> {
                        try {
                            // Store the current value if possible
                            try {
                                prevValues[LocalPlatformRenderer] = LocalPlatformRenderer.current
                            } catch (e: Throwable) {
                                // Ignore if current value is not available
                            }

                            // Set the new value
                            if (value is Function0<*>) {
                                @Suppress("UNCHECKED_CAST")
                                val renderer = (value as () -> PlatformRenderer).invoke()
                                LocalPlatformRenderer.provides(renderer)
                            } else if (value is PlatformRenderer) {
                                LocalPlatformRenderer.provides(value)
                            }
                        } catch (e: Throwable) {
                            console.error("[DEBUG] Error setting LocalPlatformRenderer: " + e)
                            console.error("[DEBUG] Error stack: " + e.stackTraceToString())
                        }
                    }
                    else -> {
                        // For other CompositionLocal providers, try to use the provides method
                        try {
                            // Store the current value if possible
                            val currentValue = js("provider.current")
                            if (currentValue != null) {
                                prevValues[provider] = currentValue
                            }

                            // Set the new value
                            if (value is Function0<*>) {
                                js("provider.provides(value)")
                            } else {
                                js("provider.provides(function() { return value; })")
                            }
                        } catch (e: Throwable) {
                            console.error("[DEBUG] Error setting provider: " + e)
                            console.error("[DEBUG] Error stack: " + e.stackTraceToString())
                        }
                    }
                }
            } catch (e: Throwable) {
                console.error("[DEBUG] Error processing provider: " + e)
                console.error("[DEBUG] Error stack: " + e.stackTraceToString())
            }
        }

        // Call content with new values
        content()
    } finally {
        // Restore previous values
        prevValues.forEach { (provider, value) ->
            try {
                console.log("[DEBUG] Restoring provider: $provider")

                when (provider) {
                    LocalLanguage -> {
                        if (value is Function0<*>) {
                            @Suppress("UNCHECKED_CAST")
                            LocalLanguage.provides(value as () -> Language)
                        } else if (value is Language) {
                            LocalLanguage.provides { value }
                        }
                    }
                    LocalLayoutDirection -> {
                        if (value is Function0<*>) {
                            @Suppress("UNCHECKED_CAST")
                            LocalLayoutDirection.provides(value as () -> LayoutDirection)
                        } else if (value is LayoutDirection) {
                            LocalLayoutDirection.provides { value }
                        }
                    }
                    LocalPlatformRenderer -> {
                        try {
                            if (value is Function0<*>) {
                                @Suppress("UNCHECKED_CAST")
                                val renderer = (value as () -> PlatformRenderer).invoke()
                                LocalPlatformRenderer.provides(renderer)
                            } else if (value is PlatformRenderer) {
                                LocalPlatformRenderer.provides(value)
                            }
                        } catch (e: Throwable) {
                            console.error("[DEBUG] Error restoring LocalPlatformRenderer: " + e)
                            console.error("[DEBUG] Error stack: " + e.stackTraceToString())
                        }
                    }
                    else -> {
                        // For other CompositionLocal providers, try to use the provides method
                        try {
                            if (value is Function0<*>) {
                                js("provider.provides(value)")
                            } else {
                                js("provider.provides(function() { return value; })")
                            }
                        } catch (e: Throwable) {
                            console.error("[DEBUG] Error restoring provider: $e")
                            console.error("[DEBUG] Error stack: " + e.stackTraceToString())
                        }
                    }
                }
            } catch (e: Throwable) {
                console.error("[DEBUG] Error restoring provider: $e")
                console.error("[DEBUG] Error stack: " + e.stackTraceToString())
            }
        }
    }
}


/**
 * Initialize the custom renderComposable implementation.
 * This should be called early in the application lifecycle.
 */
fun initializeCustomRenderUtils() {
    console.log("[DEBUG] Initializing custom renderComposable implementation")

    // Use Kotlin/JS interop to define the renderComposable function in the global scope
    js("""
        (function() {
    // Initialize currentParent globally to avoid undefined errors
    if (typeof window.currentParent === 'undefined' || window.currentParent === null) {
        window.currentParent = document.body;
        console.log("[DEBUG] Initialized global currentParent to document.body");
    }

    try {
        // Define the renderComposable function
        if (typeof window.code === 'undefined') { // Ensure window.code is used for global namespace
            window.code = {};
        }
        if (typeof window.code.yousef === 'undefined') {
            window.code.yousef = {};
        }
        if (typeof window.code.yousef.summon === 'undefined') {
            window.code.yousef.summon = {};
        }

        // Define the renderComposable function
        window.code.yousef.summon.renderComposable = function(renderer, composable, container) {
            console.log("Using global renderComposable function");
            if (container && container.innerHTML !== undefined) {
                // Clear the container first
                container.innerHTML = "";

                // Store the previous parent
                var previousParent = window.currentParent; // Ensure window.currentParent is used

                // Set the container as the current parent
                window.currentParent = container; // Ensure window.currentParent is used

                try {
                    // Just call the composable function directly
                    console.log("Using direct composable call");
                    if (typeof composable === 'function') {
                        composable();
                    }
                } catch (e) {
                    console.error("Error executing composable:", e);
                } finally {
                    // Restore the previous parent
                    window.currentParent = previousParent; // Ensure window.currentParent is used
                }
            }
        };

        // Create the JsPlatformRenderer constructor if it doesn't exist
        if (typeof window.code.yousef.summon.runtime === 'undefined') {
            window.code.yousef.summon.runtime = {};
        }

        // Define the LocalPlatformRenderer with proper provides method
        if (typeof window.code.yousef.summon.runtime.LocalPlatformRenderer === 'undefined') {
            var currentRenderer = null;
            window.code.yousef.summon.runtime.LocalPlatformRenderer = {
                "current": function() {
                    if (currentRenderer === null) {
                        throw new Error("CompositionLocal not provided");
                    }
                    return currentRenderer;
                },
                "provides": function(valueOrFn) {
                    console.log("[DEBUG] LocalPlatformRenderer.provides called");
                    if (typeof valueOrFn === 'function') {
                        currentRenderer = valueOrFn();
                    } else {
                        currentRenderer = valueOrFn;
                    }
                    return this;
                }
            };
        }

        // Define the JsPlatformRenderer constructor
        window.code.yousef.summon.runtime.JsPlatformRenderer = function() {
            return {
                "renderComposable": function(composable) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderComposable");
                    try {
                        if (typeof composable === 'function') {
                            composable();
                        }
                    } catch (e) {
                        console.error("[DEBUG] Error executing composable:", e);
                    }
                },
                "renderComposable_udbimr": function(composable) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderComposable_udbimr");
                    try {
                        if (typeof composable === 'function') {
                            composable();
                        }
                    } catch (e) {
                        console.error("[DEBUG] Error executing composable in renderComposable_udbimr:", e);
                    }
                },
                "renderComposable_udbimr_k${'$'}": function(composable) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderComposable_udbimr_k${'$'}");
                    try {
                        if (typeof composable === 'function') {
                            composable();
                        }
                    } catch (e) {
                        console.error("[DEBUG] Error executing composable in renderComposable_udbimr_k${'$'}:", e);
                    }
                },
                "renderComposableRoot": function(composable) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderComposableRoot");
                    try {
                        if (typeof composable === 'function') {
                            composable();
                        }
                    } catch (e) {
                        console.error("[DEBUG] Error executing composable:", e);
                    }
                    return "<div>Summon Component Root</div>";
                },
                "render": function(composable) {
                    console.log("[DEBUG] Using JsPlatformRenderer.render");
                    return {};
                },
                "dispose": function() {
                    console.log("[DEBUG] Using JsPlatformRenderer.dispose");
                },

                // Add methods needed by components like Column
                "renderText": function(text, modifier) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderText: " + text);
                    console.log("[DEBUG] Modifier for text: ", modifier);

                    // Check if the text looks like an i18n key (contains dots and no spaces)
                    if (text && text.indexOf('.') !== -1 && text.indexOf(' ') === -1) {
                        console.log("[DEBUG] Text appears to be an i18n key: " + text);

                        // Try to look up the translation directly
                        try { // Outer try for translation attempts
                            // Check if StringResources is available
                            if (typeof window.code !== 'undefined' &&
                                typeof window.code.yousef !== 'undefined' &&
                                typeof window.code.yousef.summon !== 'undefined' &&
                                typeof window.code.yousef.summon.i18n !== 'undefined' &&
                                typeof window.code.yousef.summon.i18n.StringResources !== 'undefined') {

                                console.log("[DEBUG] StringResources is available, attempting to get translation");

                                // Get the current language code
                                var languageCode = "en"; // Default to English
                                if (typeof window.code.yousef.summon.i18n.getCurrentLanguage === 'function') {
                                    try {
                                        var currentLanguage = window.code.yousef.summon.i18n.getCurrentLanguage();
                                        if (currentLanguage && currentLanguage.code) {
                                            languageCode = currentLanguage.code;
                                            console.log("[DEBUG] Current language code: " + languageCode);
                                        }
                                    } catch (e) {
                                        console.error("[DEBUG] Error getting current language: " + e);
                                    }
                                }
                                
                                var translation; // Declare translation here to be accessible

                                // Try to get the translation
                                if (typeof window.code.yousef.summon.i18n.StringResources.getString === 'function') {
                                    try {
                                        translation = window.code.yousef.summon.i18n.StringResources.getString(text, languageCode);
                                        console.log("[DEBUG] Translation for " + text + " in " + languageCode + ": " + translation);
                                    } catch (e) {
                                        console.error("[DEBUG] Error using getString: " + e);
                                        // Try with the mangled name
                                        if (typeof window.code.yousef.summon.i18n.StringResources.getString_8ym3z0 === 'function') {
                                            try {
                                                console.log("[DEBUG] Trying with mangled name getString_8ym3z0");
                                                translation = window.code.yousef.summon.i18n.StringResources.getString_8ym3z0(text, languageCode, null);
                                                console.log("[DEBUG] Translation using mangled name: " + translation);
                                            } catch (e2) {
                                                console.error("[DEBUG] Error using mangled name: " + e2);
                                            }
                                        }
                                    }

                                    // Use the translation if it's different from the key
                                    if (translation && translation !== text) {
                                        console.log("[DEBUG] Using translation instead of key");
                                        text = translation;
                                    } else {
                                        console.log("[DEBUG] No translation found or translation is same as key via StringResources.getString. Trying direct JSON load.");
                                        // Try to load the translation directly from the JSON files
                                        try {
                                            var languageFile = "i18n/" + languageCode + ".json";
                                            console.log("[DEBUG] Attempting to load language file directly: " + languageFile);

                                            var xhr = new XMLHttpRequest();
                                            xhr.open("GET", languageFile, false); // false makes it synchronous
                                            xhr.send(null); // Pass null for GET requests

                                            if (xhr.status === 200) {
                                                var jsonData = JSON.parse(xhr.responseText);
                                                console.log("[DEBUG] Successfully loaded language file directly");

                                                var keyParts = text.split(".");
                                                var value = jsonData;

                                                for (var i = 0; i < keyParts.length; i++) {
                                                    if (value && typeof value === 'object' && value.hasOwnProperty(keyParts[i])) {
                                                        value = value[keyParts[i]];
                                                    } else {
                                                        value = null;
                                                        break;
                                                    }
                                                }

                                                if (value && typeof value === "string") {
                                                    console.log("[DEBUG] Found translation directly in JSON: " + value);
                                                    text = value;
                                                }
                                            } else {
                                                console.warn("[DEBUG] Failed to load language file directly: " + languageFile + ", Status: " + xhr.status);
                                            }
                                        } catch (e_xhr) {
                                            console.error("[DEBUG] Error loading translation directly: " + e_xhr);
                                        }
                                    }
                                } else {
                                     console.log("[DEBUG] StringResources.getString is not a function. Attempting direct JSON load.");
                                     // Fallback to direct JSON load if getString function is not available
                                     try {
                                        var languageFile_alt = "i18n/" + languageCode + ".json";
                                        console.log("[DEBUG] Attempting to load language file directly (fallback): " + languageFile_alt);
                                        var xhr_alt = new XMLHttpRequest();
                                        xhr_alt.open("GET", languageFile_alt, false); 
                                        xhr_alt.send(null);
                                        if (xhr_alt.status === 200) {
                                            var jsonData_alt = JSON.parse(xhr_alt.responseText);
                                            var keyParts_alt = text.split(".");
                                            var value_alt = jsonData_alt;
                                            for (var j = 0; j < keyParts_alt.length; j++) {
                                                if (value_alt && typeof value_alt === 'object' && value_alt.hasOwnProperty(keyParts_alt[j])) {
                                                    value_alt = value_alt[keyParts_alt[j]];
                                                } else {
                                                    value_alt = null;
                                                    break;
                                                }
                                            }
                                            if (value_alt && typeof value_alt === "string") {
                                                console.log("[DEBUG] Found translation directly in JSON (fallback): " + value_alt);
                                                text = value_alt;
                                            }
                                        } else {
                                             console.warn("[DEBUG] Failed to load language file directly (fallback): " + languageFile_alt + ", Status: " + xhr_alt.status);
                                        }
                                     } catch (e_xhr_alt) {
                                         console.error("[DEBUG] Error loading translation directly (fallback): " + e_xhr_alt);
                                     }
                                }
                                // *** SYNTAX ERROR FIX: The orphaned 'catch' block that was here has been removed. ***
                            } else {
                                console.log("[DEBUG] StringResources object not found. Skipping translation attempt.");
                            }
                        } catch (e_outer_translation) {
                            console.error("[DEBUG] Error during overall translation checking process: " + e_outer_translation);
                        }
                    } // End of i18n key check

                    var element = document.createElement('span');
                    element.textContent = text;

                    element.className = 'summon-text';
                    console.log("[DEBUG] Created text element with class: " + element.className);

                    if (typeof window.currentParent !== 'undefined' && window.currentParent) {
                        if (window.currentParent.classList &&
                            (window.currentParent.classList.contains('task-item') ||
                             window.currentParent.classList.contains('task-content'))) {
                            element.classList.add('task-title');
                            console.log("[DEBUG] Added task-title class to text element");
                        }
                    }

                    if (modifier && modifier.styles) {
                        console.log("[DEBUG] Text modifier styles: ", modifier.styles);
                        if (modifier.styles['font-size'] === '24px' && modifier.styles['font-weight'] === 'bold') element.classList.add('app-title');
                        else if (modifier.styles['font-size'] === '18px' && modifier.styles['font-weight'] === 'bold') element.classList.add('section-title');
                        else if (modifier.styles['font-size'] === '20px' && modifier.styles['font-weight'] === 'bold') element.classList.add('list-title');
                        if (modifier.styles.color === '#ff4d4d') element.classList.add('error-message');
                        if (modifier.styles['font-style'] === 'italic') element.classList.add('font-italic');
                        if (modifier.styles['text-decoration'] === 'line-through') element.classList.add('completed-text');
                        if (modifier.styles['text-align'] === 'center') element.classList.add('text-center');
                        if (modifier.styles['text-align'] === 'right') element.classList.add('text-right');
                        if (modifier.styles['font-weight'] === 'bold') element.classList.add('font-bold');

                        var styleString = "";
                        for (var prop in modifier.styles) {
                            if (modifier.styles.hasOwnProperty(prop)) {
                                element.style[prop] = modifier.styles[prop];
                                styleString += prop + ":" + modifier.styles[prop] + ";";
                            }
                        }
                        console.log("[DEBUG] Applied text style string: " + styleString);
                    }

                    if (typeof window.currentParent !== 'undefined' && window.currentParent && typeof window.currentParent.appendChild === 'function') {
                        window.currentParent.appendChild(element);
                    } else {
                        console.error("[DEBUG] currentParent is undefined or not an element in renderText, appending to body as fallback.");
                        document.body.appendChild(element);
                    }

                    return element;
                },

                "renderText_wa0k6q_k${'$'}": function(text, modifier) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderText_wa0k6q_k${'$'}");
                    return this.renderText(text, modifier);
                },

                "renderDiv": function(modifier, content) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderDiv");
                    var element = document.createElement('div');
                    element.className = 'summon-div';

                    if (typeof window.currentParent !== 'undefined' && window.currentParent) {
                        if (window.currentParent.classList && window.currentParent.classList.contains('app-header')) {
                            element.classList.add('header-content');
                        }
                    }

                    if (modifier && modifier.styles) {
                        if (modifier.styles.padding === '10px' && modifier.styles.margin === '0 20px 0 0' && modifier.styles['background-color'] === '#f0f0f0') element.classList.add('language-selector');
                        else if (modifier.styles.padding === '10px' && modifier.styles['background-color'] === '#f0f0f0') element.classList.add('theme-toggle');
                        if (modifier.styles.padding === '20px' && modifier.styles.margin === '10px 0' && modifier.styles['background-color'] === '#f9f9f9') element.classList.add('empty-state');
                        if (modifier.styles.width === '100%') element.classList.add('w-100');
                        if (modifier.styles['text-align'] === 'center') element.classList.add('text-center');
                        if (modifier.styles['text-align'] === 'right') element.classList.add('text-right');
                        for (var prop in modifier.styles) if (modifier.styles.hasOwnProperty(prop)) element.style[prop] = modifier.styles[prop];
                    }

                    var prevParent = (typeof window.currentParent !== 'undefined' && window.currentParent) ? window.currentParent : document.body;
                    window.currentParent = element;
                    try { if (typeof content === 'function') content(); } catch (e) { console.error("[DEBUG] Error executing div content:", e); }
                    window.currentParent = prevParent;
                    if (prevParent && typeof prevParent.appendChild === 'function') prevParent.appendChild(element); else document.body.appendChild(element);
                    return element;
                },

                "renderColumn": function(modifier, content) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderColumn");
                    var element = document.createElement('div');
                    element.style.display = 'flex';
                    element.style.flexDirection = 'column';
                    element.className = 'summon-column flex-column';

                    if (modifier && modifier.styles) {
                        if (modifier.styles.padding === '20px' && modifier.styles['background-color'] === '#ffffff' && modifier.styles['border-radius'] === '8px' && modifier.styles.margin === '0 0 20px 0') element.classList.add('task-form');
                        if (modifier.styles.padding === '15px' && modifier.styles['background-color'] === '#ffffff' && modifier.styles['border-radius'] === '8px' && modifier.styles.margin === '0 0 20px 0') element.classList.add('filter-controls');
                        if (modifier.styles.padding === '20px' && modifier.styles['background-color'] === '#ffffff' && modifier.styles['border-radius'] === '8px' && modifier.styles['max-height'] === '400px') element.classList.add('task-list');
                        if (modifier.styles.padding === '20px' && modifier.styles['background-color'] === '#f5f5f5' && modifier.styles['max-width'] === '1200px') element.classList.add('app-container');
                        if (modifier.styles.padding === '20px' && modifier.styles.margin === '10px 0' && modifier.styles['background-color'] === '#f9f9f9') element.classList.add('empty-state');
                        if (modifier.styles.width === '100%') element.classList.add('w-100');
                        if (modifier.styles['text-align'] === 'center') element.classList.add('text-center');
                        if (modifier.styles['text-align'] === 'right') element.classList.add('text-right');
                        if (modifier.styles['font-weight'] === 'bold') element.classList.add('font-bold');
                        if (modifier.styles['font-style'] === 'italic') element.classList.add('font-italic');
                        for (var prop in modifier.styles) if (modifier.styles.hasOwnProperty(prop)) element.style[prop] = modifier.styles[prop];
                    }

                    var prevParent = (typeof window.currentParent !== 'undefined' && window.currentParent) ? window.currentParent : document.body;
                    window.currentParent = element;
                    try { if (typeof content === 'function') content(); } catch (e) { console.error("[DEBUG] Error executing column content:", e); }
                    window.currentParent = prevParent;
                    if (prevParent && typeof prevParent.appendChild === 'function') prevParent.appendChild(element); else document.body.appendChild(element);
                    return element;
                },
                "renderColumn_aiouiv_k${'$'}": function(modifier, content) { return this.renderColumn(modifier, content); },

                "renderBlock": function(modifier, content) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderBlock");
                    var element = document.createElement('div');
                    element.style.display = 'block';
                    element.className = 'summon-block';

                    if (modifier && modifier.className === 'app-header') element.classList.add('app-header');
                    if (modifier && modifier.styles) {
                        if (modifier.styles.padding === '20px' && modifier.styles['background-color'] === '#ffffff' && modifier.styles['border-radius'] === '8px' && modifier.styles.margin === '0 0 20px 0') element.classList.add('app-header');
                        if (modifier.styles['box-shadow']) element.classList.add('shadow');
                        for (var prop in modifier.styles) if (modifier.styles.hasOwnProperty(prop)) element.style[prop] = modifier.styles[prop];
                    }

                    var prevParent = (typeof window.currentParent !== 'undefined' && window.currentParent) ? window.currentParent : document.body;
                    window.currentParent = element;
                    try { if (typeof content === 'function') content(); } catch (e) { console.error("[DEBUG] Error executing block content:", e); }
                    window.currentParent = prevParent;
                    if (prevParent && typeof prevParent.appendChild === 'function') prevParent.appendChild(element); else document.body.appendChild(element);
                    return element;
                },
                "renderBlock_bncpua_k${'$'}": function(modifier, content) { return this.renderBlock(modifier, content); },

                "renderRow": function(modifier, content) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderRow");
                    var element = document.createElement('div');
                    element.style.display = 'flex';
                    element.style.flexDirection = 'row';
                    element.className = 'summon-row flex-row';

                    if (typeof window.currentParent !== 'undefined' && window.currentParent) {
                        if (window.currentParent.classList) {
                            if (window.currentParent.classList.contains('filter-controls')) element.classList.add('filter-buttons-container');
                            if (window.currentParent.classList.contains('task-item')) element.classList.add('task-content');
                        }
                    }

                    if (modifier && modifier.styles) {
                        if (modifier.styles['justify-content'] === 'space-between') element.classList.add('justify-between');
                        if (modifier.styles['align-items'] === 'center') element.classList.add('align-center');
                        if (modifier.styles.width === '100%') element.classList.add('w-100');
                        if (modifier.styles.padding === '10px' && modifier.styles.margin === '5px 0' && modifier.styles['border-radius'] === '4px') {
                            element.classList.add('task-item');
                            if (modifier.styles['background-color'] === '#f0f8ff') element.classList.add('completed');
                        }
                        for (var prop in modifier.styles) if (modifier.styles.hasOwnProperty(prop)) element.style[prop] = modifier.styles[prop];
                    }

                    var prevParent = (typeof window.currentParent !== 'undefined' && window.currentParent) ? window.currentParent : document.body;
                    window.currentParent = element;
                    try { if (typeof content === 'function') content(); } catch (e) { console.error("[DEBUG] Error executing row content:", e); }
                    window.currentParent = prevParent;
                    if (prevParent && typeof prevParent.appendChild === 'function') prevParent.appendChild(element); else document.body.appendChild(element);
                    return element;
                },
                "renderRow_e96qap_k${'$'}": function(modifier, content) { return this.renderRow(modifier, content); },

                "renderButton": function(onClick, modifier, content) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderButton");
                    var element = document.createElement('button');
                    element.className = 'summon-button';

                    if (modifier && modifier.styles) {
                        if (modifier.styles['background-color'] === '#4285f4' && modifier.styles.padding === '10px 20px') element.classList.add('add-task-button');
                        if (modifier.styles['background-color'] === '#ff4d4d') element.classList.add('delete-task-button');
                        if (modifier.styles.margin === '0 5px' && modifier.styles.padding === '8px 12px') {
                            element.classList.add('filter-button');
                            if (modifier.styles['background-color'] === '#4285f4') element.classList.add('selected');
                        }
                    }
                    if (typeof window.currentParent !== 'undefined' && window.currentParent && window.currentParent.classList) {
                        if (window.currentParent.classList.contains('task-item')) element.classList.add('delete-task-button');
                        if (window.currentParent.classList.contains('task-form')) element.classList.add('add-task-button');
                        if (window.currentParent.classList.contains('filter-controls') || window.currentParent.classList.contains('filter-buttons-container')) element.classList.add('filter-button');
                    }
                     if (modifier && modifier.styles) { // Apply general styles again to ensure they are not overridden
                        for (var prop in modifier.styles) if (modifier.styles.hasOwnProperty(prop)) element.style[prop] = modifier.styles[prop];
                    }

                    element.addEventListener('click', function(event) { if (typeof onClick === 'function') onClick(); });

                    var prevParent = (typeof window.currentParent !== 'undefined' && window.currentParent) ? window.currentParent : document.body;
                    window.currentParent = element; // Button content (like text) should be appended to the button itself
                    try { if (typeof content === 'function') content(); } catch (e) { console.error("[DEBUG] Error executing button content:", e); }
                    window.currentParent = prevParent; // Restore parent for subsequent elements
                    if (prevParent && typeof prevParent.appendChild === 'function') prevParent.appendChild(element); else document.body.appendChild(element);
                    return element;
                },
                "renderButton_ahh62j_k${'$'}": function(onClick, modifier, content) { return this.renderButton(onClick, modifier, content); },

                "renderTextField": function(value, onValueChange, modifier, type) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderTextField");
                    var element = document.createElement('input');
                    element.setAttribute("type", type || "text");
                    element.setAttribute("value", value || "");
                    element.className = 'summon-input';

                    if (typeof window.currentParent !== 'undefined' && window.currentParent && window.currentParent.classList && window.currentParent.classList.contains('task-form')) {
                        element.classList.add('task-input');
                        element.setAttribute("placeholder", "Enter a new task...");
                    }
                    if (modifier && modifier.styles) {
                        if (modifier.styles.border && modifier.styles.border.includes('#ff4d4d')) element.classList.add('input-error');
                        for (var prop in modifier.styles) if (modifier.styles.hasOwnProperty(prop)) element.style[prop] = modifier.styles[prop];
                    }
                    element.addEventListener('input', function(event) { if (typeof onValueChange === 'function') onValueChange(event.target.value); });
                    
                    if (typeof window.currentParent !== 'undefined' && window.currentParent && typeof window.currentParent.appendChild === 'function') {
                        window.currentParent.appendChild(element);
                    } else {
                        console.error("[DEBUG] currentParent is undefined in renderTextField, appending to body.");
                        document.body.appendChild(element);
                    }
                    return element;
                },
                "renderTextField_w72mnb_k${'$'}": function(value, onValueChange, modifier, type) { return this.renderTextField(value, onValueChange, modifier, type); },

                "renderCheckbox": function(checked, onCheckedChange, enabled, modifier) {
                    console.log("[DEBUG] Using JsPlatformRenderer.renderCheckbox");
                    var element = document.createElement('input');
                    element.setAttribute("type", "checkbox");
                    element.checked = checked;
                    element.className = 'summon-checkbox';

                    if (!enabled) { element.disabled = true; element.classList.add('disabled'); }
                    if (checked) element.classList.add('checked'); // Add initially if checked

                    if (typeof window.currentParent !== 'undefined' && window.currentParent && window.currentParent.classList &&
                        (window.currentParent.classList.contains('task-item') || window.currentParent.classList.contains('task-content'))) {
                        element.classList.add('task-checkbox');
                    }
                    if (modifier && modifier.styles) {
                        if (modifier.styles.margin === '0 10px 0 0') element.classList.add('checkbox-with-margin');
                        for (var prop in modifier.styles) if (modifier.styles.hasOwnProperty(prop)) element.style[prop] = modifier.styles[prop];
                    }
                    element.addEventListener('change', function(event) {
                        if (typeof onCheckedChange === 'function') onCheckedChange(element.checked);
                        if (element.checked) element.classList.add('checked'); else element.classList.remove('checked');
                    });

                    if (typeof window.currentParent !== 'undefined' && window.currentParent && typeof window.currentParent.appendChild === 'function') {
                        window.currentParent.appendChild(element);
                    } else {
                        console.error("[DEBUG] currentParent is undefined in renderCheckbox, appending to body.");
                        document.body.appendChild(element);
                    }
                    return element;
                },
                "renderCheckbox_1iv0k1_k${'$'}": function(checked, onCheckedChange, enabled, modifier) { return this.renderCheckbox(checked, onCheckedChange, enabled, modifier); }
            };
        };

        // Check if RenderUtils and protoOf are defined (assuming they are globally available)
        if (typeof RenderUtils === 'undefined') {
            console.error("RenderUtils is not defined. This should have been defined in index.html or similar.");
        }
        if (typeof protoOf === 'undefined') {
            console.error("protoOf is not defined. This should have been defined in index.html or similar.");
        }

        if (typeof RenderUtils !== 'undefined' && typeof protoOf !== 'undefined') {
            var renderUtilsProto = protoOf(RenderUtils);
            try {
                var customRenderComposable = function(composable) {
                    console.log('Using custom renderComposable_udbimr_k${'$'} implementation');
                    var container = document.createElement('div');
                    container.className = 'summon-composable-container';

                    if (typeof window.currentParent === 'undefined' || window.currentParent === null) {
                        window.currentParent = document.body;
                    }
                    var previousParent = window.currentParent;
                    window.currentParent = container;
                    if (previousParent && typeof previousParent.appendChild === 'function') {
                         previousParent.appendChild(container);
                    } else {
                        console.warn("[DEBUG] previousParent in customRenderComposable was not an element, appending container to body.");
                        document.body.appendChild(container);
                    }

                    try {
                        if (typeof composable === 'function') composable();
                    } catch (e) {
                        console.error("Error calling composable directly in custom renderComposable_udbimr_k${'$'}:", e);
                    } finally {
                        window.currentParent = previousParent;
                    }
                    return { "render": function(c) { if (typeof c === 'function') c(); return {}; }, "dispose": function() {} };
                };
                renderUtilsProto.renderComposable_udbimr_k${'$'} = customRenderComposable;
                if (typeof window.renderComposable_udbimr_k${'$'} !== 'undefined') {
                    window.renderComposable_udbimr_k${'$'} = customRenderComposable;
                }
            } catch (e) {
                console.error("Could not redefine renderComposable_udbimr_k${'$'}:", e);
            }
        } else {
            console.warn("RenderUtils or protoOf not defined, skipping redefinition of renderComposable_udbimr_k${'$'}.")
        }

    } catch (e) {
        console.error("Error in custom renderComposable IIFE setup:", e);
    }
})();

    """
    )

    console.log("Custom renderComposable implementation initialized")
}

// External interface for Console
external interface Console {
    fun log(message: String)
    fun log(message: String, obj: dynamic)
    fun error(message: String)
    fun error(message: String, obj: dynamic)
}

// External interface for accessing the console
external val console: Console
