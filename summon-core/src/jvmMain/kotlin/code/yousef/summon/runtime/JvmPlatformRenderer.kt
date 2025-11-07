package code.yousef.summon.runtime

// Missing imports for runtime components
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.core.FlowContentCompat
import code.yousef.summon.core.asFlowContentCompat
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.overflowX
import code.yousef.summon.modifier.overflowY
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

// Interface defined in PlatformRenderer.kt commonMain
// interface FormContent : FlowContent

// PlatformRenderer implementation
@OptIn(ExperimentalUuidApi::class)
actual open class PlatformRenderer {

    private val headElements = mutableListOf<String>()

    // Fix 1: Remove ThreadLocal, use instance variable.
    private var currentBuilder: FlowContent? = null

    // Initialize renderer - register this instance globally
    init {
        setPlatformRenderer(this)
    }

    private fun cssPropertyName(key: String): String =
        if (key.contains('-')) {
            key
        } else {
            key.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
        }

    // Apply Modifier - handles both styles and attributes
    // This extension function applies to any FlowOrMetaDataContent
    private fun FlowOrMetaDataContent.applyModifier(modifier: Modifier) {
        // Apply CSS styles
        if (modifier.styles.isNotEmpty()) {
            val styleString = modifier.styles
                .map { (key, value) ->
                    "${cssPropertyName(key)}: $value"
                }
                .joinToString(separator = "; ", postfix = ";")

            if (styleString.isNotBlank()) {
                (this as? CommonAttributeGroupFacade)?.style = styleString
            }
        }

        // Apply HTML attributes
        if (modifier.attributes.isNotEmpty() && this is CommonAttributeGroupFacade) {
            modifier.attributes.forEach { (name, value) ->
                this.attributes[name] = value
            }
        }

        // Add hydration marker for SSR compatibility
        if (this is CommonAttributeGroupFacade) {
            val existingId = this.attributes["data-summon-id"] ?: modifier.attributes["data-summon-id"]
            if (existingId == null) {
                // Generate unique hydration ID for each element
                val hydrationId = "summon-${UUID.randomUUID().toString().take(8)}"
                this.attributes["data-summon-id"] = hydrationId
            }

            if (modifier.eventHandlers.isNotEmpty()) {
                val isDisabled = modifier.attributes.containsKey("disabled")
                if (!isDisabled) {
                    modifier.eventHandlers.forEach { (eventName, handler) ->
                        val callbackId = CallbackRegistry.registerCallback(handler)
                        when (eventName.lowercase()) {
                            "click" -> {
                                if (!this.attributes.containsKey("data-onclick-id")) {
                                    this.attributes["data-onclick-id"] = callbackId
                                }
                                this.attributes["data-onclick-action"] = "true"
                            }

                            else -> {
                                this.attributes["data-summon-event-$eventName-id"] = callbackId
                                this.attributes["data-summon-event-$eventName-action"] = "true"
                            }
                        }
                    }
                }
            }
        }

        if (modifier.pseudoElements.isNotEmpty() && this is CommonAttributeGroupFacade) {
            val hostId = this.attributes["data-summon-id"]
            if (hostId != null) {
                modifier.pseudoElements.forEach { pseudo ->
                    val cssBody = pseudo.styles.entries.joinToString("; ") {
                        "${cssPropertyName(it.key)}: ${it.value};"
                    }
                    val css = """
                        [data-summon-id="$hostId"]${pseudo.element.selector} {
                            content: ${pseudo.content};
                            $cssBody
                        }
                    """.trimIndent()
                    addHeadElement("<style>$css</style>")
                }
            }
        }
    }

    // Helper to render composable content with FlowContent receiver
    private fun <T : FlowContent> T.renderContent(content: @Composable FlowContentCompat.() -> Unit) {
        // Create a bridge from kotlinx.html.FlowContent to FlowContentCompat
        val flowContentCompat = this.asFlowContentCompat()
        flowContentCompat.content()
    }

    // Helper to render composable content without FlowContent receiver
    private fun renderContent(content: @Composable () -> Unit) {
        requireBuilder() // Ensure context exists before calling content

        // Create and use proper composition context
        val composer = RecomposerHolder.createComposer()
        val previousComposer = CompositionLocal.currentComposer
        
        try {
            // Set up composition context
            CompositionLocal.setCurrentComposer(composer)
            LocalPlatformRenderer.provides(this)
            
            // Execute composable with composition context
            composer.compose {
                content()
            }
        } finally {
            // Restore previous composer
            CompositionLocal.setCurrentComposer(previousComposer)
        }
    }

    // Get the current builder context
    private fun requireBuilder(): FlowContent {
        return currentBuilder ?: error("Rendering function called outside of renderComposableRoot scope")
    }

    actual open fun renderText(text: String, modifier: Modifier) {
        requireBuilder().span {
            applyModifier(modifier)
            +text
        }
    }

    actual open fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
        requireBuilder().label {
            applyModifier(modifier)
            if (forElement != null) {
                htmlFor = forElement // Changed from htmlFor
            }
            +text
        }
    }

    actual open fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        requireBuilder().button {
            applyModifier(modifier)

            // Add proper button type to prevent form submission issues
            // Only set type="button" if not already specified in modifier
            if (!attributes.containsKey("type")) {
                attributes["type"] = "button"
            }

            val isDisabled = modifier.attributes.containsKey("disabled")
            if (!isDisabled) {
                if (!attributes.containsKey("data-onclick-id")) {
                    val callbackId = CallbackRegistry.registerCallback(onClick)
                    attributes["data-onclick-id"] = callbackId
                }
                attributes["data-onclick-action"] = "true"
            }
            
            renderContent(content)
        }
    }

    actual open fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier,
        type: String
    ) {
        requireBuilder().input(type = InputType.text) {
            applyModifier(modifier)
            this.value = value

            // Check if name attribute was provided in modifier
            val customName = modifier.attributes?.get("name")
            if (customName != null) {
                // Use the provided name
                name = customName
                // If no id was provided, use the name as id
                if (modifier.attributes?.get("id") == null) {
                    id = customName
                }
            } else {
                // Fall back to generated UUID for both id and name
                id = "input-${UUID.randomUUID()}"
                name = id
            }

            attributes["data-onchange-action"] = "true"
            comment(" onValueChange handler needed (JS) ")
        }
    }

    actual open fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<code.yousef.summon.runtime.SelectOption<T>>,
        modifier: Modifier
    ) {
        requireBuilder().select {
            applyModifier(modifier)

            // Check if name attribute was provided in modifier
            val customName = modifier.attributes?.get("name")
            if (customName != null) {
                // Use the provided name
                name = customName
                // If no id was provided, use the name as id
                if (modifier.attributes?.get("id") == null) {
                    id = customName
                }
            } else {
                // Fall back to generated UUID for both id and name
                id = "select-${UUID.randomUUID()}"
                name = id
            }

            attributes["data-onchange-action"] = "true"
            comment(" onSelectedChange handler needed (JS) ")

            options.forEach { optionData ->
                option {
                    this.value = optionData.value.toString()
                    if (optionData.value == selectedValue) this.selected = true
                    this.disabled = optionData.disabled
                    +optionData.label
                }
            }
        }
    }

    actual open fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        min: LocalDate?,
        max: LocalDate?,
        modifier: Modifier
    ) {
        requireBuilder().input(type = InputType.date) {
            applyModifier(modifier)
            if (value != null) this.value = value.toString()
            if (min != null) this.min = min.toString()
            if (max != null) this.max = max.toString()
            this.disabled = !enabled
            id = "date-${UUID.randomUUID()}"
            name = id
            attributes["data-onchange-action"] = "true"
            comment(" onValueChange handler needed (JS) ")
        }
    }

    actual open fun renderTextArea(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        rows: Int?,
        maxLength: Int?,
        placeholder: String?,
        modifier: Modifier
    ) {
        requireBuilder().textArea {
            applyModifier(modifier)
            if (rows != null) this.rows = rows.toString()
            if (maxLength != null) this.maxLength = maxLength.toString()
            if (placeholder != null) this.placeholder = placeholder
            this.disabled = !enabled
            this.readonly = readOnly

            // Check if name attribute was provided in modifier
            val customName = modifier.attributes?.get("name")
            if (customName != null) {
                // Use the provided name
                name = customName
                // If no id was provided, use the name as id
                if (modifier.attributes?.get("id") == null) {
                    id = customName
                }
            } else {
                // Fall back to generated UUID for both id and name
                id = "textarea-${UUID.randomUUID()}"
                name = id
            }

            attributes["data-onchange-action"] = "true"
            comment(" onValueChange handler needed (JS) ")
            +value
        }
    }

    actual open fun addHeadElement(content: String) {
        headElements.add(content)
    }

    actual open fun getHeadElements(): List<String> {
        return headElements.toList()
    }

    actual open fun renderHeadElements(builder: code.yousef.summon.seo.HeadScope.() -> Unit) {
        val headScope = code.yousef.summon.seo.DefaultHeadScope { element ->
            addHeadElement(element)
        }
        headScope.builder()
    }

    // Renders a composable component in the current context
// This is a convenience method for rendering a composable without directly accessing FlowContent
    actual open fun renderComposable(composable: @Composable () -> Unit) {
        currentBuilder?.let { builder ->
            // Create composition context for this composable
            val composer = RecomposerHolder.createComposer()
            val previousComposer = CompositionLocal.currentComposer
            
            try {
                // Set up composition context
                CompositionLocal.setCurrentComposer(composer)
                LocalPlatformRenderer.provides(this)
                
                // Execute composable with composition context
                composer.compose {
                    composable()
                }
            } finally {
                // Restore previous composer
                CompositionLocal.setCurrentComposer(previousComposer)
            }
        }
            ?: error("renderComposable called without an active FlowContent builder. Ensure it's within renderComposableRoot or a parent Composable.")
    }

    actual open fun renderComposableRoot(composable: @Composable (() -> Unit)): String {
        CallbackRegistry.beginRender()
        val result = StringBuilder()
        try {
            result.appendHTML(prettyPrint = false).html {
                head {
                    meta(charset = "UTF-8")
                    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                    title("Summon App")
                    synchronized(headElements) { headElements.forEach { unsafe { raw(it) } } }
                }
                body {
                    currentBuilder = this // Set context
                    try {
                        renderContent(composable)
                    } finally {
                        currentBuilder = null // Ensure context is cleared
                    }
                }
            }
        } finally {
            if (currentBuilder != null) { // Double-check clearance on exception
                System.err.println("Warning: currentBuilder not cleared properly in renderComposableRoot.")
                currentBuilder = null
            }
            CallbackRegistry.abandonRenderContext()
        }
        return result.toString()
    }

    actual open fun renderComposableRootWithHydration(composable: @Composable () -> Unit): String {
        CallbackRegistry.beginRender()
        return try {
            val bodyContent = renderComposableContent(composable)
            val callbackIds = CallbackRegistry.finishRenderAndCollectCallbackIds()
            val hydrationData = generateHydrationData(callbackIds)
            createHydratedDocument(bodyContent, hydrationData)
        } finally {
            CallbackRegistry.abandonRenderContext()
        }
    }
    
    private fun renderComposableContent(composable: @Composable () -> Unit): String {
        val result = StringBuilder()
        try {
            result.appendHTML(prettyPrint = false).div {
                currentBuilder = this // Set context
                try {
                    renderContent(composable)
                } finally {
                    currentBuilder = null // Ensure context is cleared
                }
            }
        } finally {
            if (currentBuilder != null) { // Double-check clearance on exception
                System.err.println("Warning: currentBuilder not cleared properly in renderComposableContent.")
                currentBuilder = null
            }
        }
        return result.toString()
    }

    private fun generateHydrationData(callbackIds: Set<String>): String {
        // Enhanced hydration data for WASM compatibility
        val hydrationData = mapOf(
            "version" to 1,
            "callbacks" to callbackIds.toList(),
            "timestamp" to System.currentTimeMillis(),
            "renderer" to "jvm",
            "hydrationMarkers" to true,
            "seoCompatible" to true
        )

        // Serialize to JSON for the client
        val jsonData = buildString {
            append("{")
            append("\"version\":${hydrationData["version"]},")
            append("\"callbacks\":[")
            @Suppress("UNCHECKED_CAST")
            val callbacks = hydrationData["callbacks"] as List<String>
            callbacks.forEachIndexed { index, callback ->
                append("\"$callback\"")
                if (index < callbacks.size - 1) append(",")
            }
            append("],")
            append("\"timestamp\":${hydrationData["timestamp"]},")
            append("\"renderer\":\"${hydrationData["renderer"]}\",")
            append("\"hydrationMarkers\":${hydrationData["hydrationMarkers"]},")
            append("\"seoCompatible\":${hydrationData["seoCompatible"]}")
            append("}")
        }

        return jsonData
    }
    
    private fun createHydratedDocument(bodyContent: String, hydrationData: String): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Summon App</title>

                <!-- SEO-critical metadata is preserved for search engines -->
                <meta name="description" content="Summon Framework Application">
                <meta name="robots" content="index, follow">
                <meta property="og:type" content="website">
                <meta property="og:title" content="Summon App">

                ${headElements.joinToString("\n                ")}

                <!-- Preload hydration resources for better performance -->
                <link rel="preload" href="/summon-hydration.js" as="script">
                <link rel="preload" href="/summon-hydration.wasm" as="fetch" type="application/wasm" crossorigin>
            </head>
            <body>
                <!-- Server-rendered content with hydration markers -->
                <div id="summon-app" data-ssr="true" data-hydration-ready="false">
                    $bodyContent
                </div>

                <!-- Hydration data for Summon components -->
                <script type="application/json" id="summon-hydration-data">
                    $hydrationData
                </script>

                <!-- Progressive enhancement: Forms work without JS -->
                <noscript>
                    <style>
                        [data-onclick-action], [data-onchange-action] {
                            /* Ensure form elements are visible and functional without JS */
                            opacity: 1 !important;
                            pointer-events: auto !important;
                        }
                    </style>
                </noscript>

                <!-- Phase 5: Performance optimization meta tags and resource hints -->
                <meta name="viewport" content="width=device-width, initial-scale=1.0">

                <!-- Preconnect to optimize external resources -->
                <link rel="preconnect" href="https://fonts.gstatic.com">
                <link rel="dns-prefetch" href="//fonts.googleapis.com">

                <!-- Phase 5: Resource hints for optimal loading -->
                <script>
                    // Preload critical resources based on browser capabilities
                    (function() {
                        const userAgent = navigator.userAgent;
                        const isModernBrowser = 'noModule' in HTMLScriptElement.prototype;

                        // Preload appropriate bundle based on browser support
                        if (typeof WebAssembly === 'object' && WebAssembly.instantiate && isModernBrowser) {
                            // Modern browser with WASM support - preload WASM bundle
                            const wasmPreload = document.createElement('link');
                            wasmPreload.rel = 'modulepreload';
                            wasmPreload.href = '/summon-hydration.wasm.js';
                            wasmPreload.as = 'script';
                            document.head.appendChild(wasmPreload);

                            // Preload the WASM file itself
                            const wasmFilePreload = document.createElement('link');
                            wasmFilePreload.rel = 'preload';
                            wasmFilePreload.href = '/summon-hydration.wasm';
                            wasmFilePreload.as = 'fetch';
                            wasmFilePreload.type = 'application/wasm';
                            wasmFilePreload.crossOrigin = 'anonymous';
                            document.head.appendChild(wasmFilePreload);
                        } else {
                            // Legacy browser - preload JS fallback
                            const jsPreload = document.createElement('link');
                            jsPreload.rel = 'preload';
                            jsPreload.href = '/summon-hydration.js';
                            jsPreload.as = 'script';
                            document.head.appendChild(jsPreload);
                        }

                        // Prefetch non-critical resources for future navigation
                        setTimeout(() => {
                            const prefetchResources = [
                                '/static/summon-core.js',
                                '/static/vendors.js'
                            ];

                            prefetchResources.forEach(resource => {
                                const link = document.createElement('link');
                                link.rel = 'prefetch';
                                link.href = resource;
                                link.as = 'script';
                                document.head.appendChild(link);
                            });
                        }, 100);
                    })();
                </script>

                <!-- Advanced WASM detection and browser compatibility layer with Phase 5 optimizations -->
                <script>
                    (function() {
                        // Phase 5: Performance monitoring for optimization
                        const PerformanceTracker = {
                            marks: new Map(),
                            measures: new Map(),

                            mark: function(name) {
                                if (window.performance && performance.mark) {
                                    performance.mark(name);
                                    this.marks.set(name, performance.now());
                                }
                            },

                            measure: function(name, startMark, endMark) {
                                if (window.performance && performance.measure) {
                                    try {
                                        performance.measure(name, startMark, endMark);
                                        const startTime = this.marks.get(startMark) || 0;
                                        const endTime = endMark ? this.marks.get(endMark) : performance.now();
                                        this.measures.set(name, endTime - startTime);
                                        return endTime - startTime;
                                    } catch (e) {
                                        console.debug('Performance measurement failed:', e);
                                        return 0;
                                    }
                                }
                                return 0;
                            },

                            getMetrics: function() {
                                return {
                                    marks: Object.fromEntries(this.marks),
                                    measures: Object.fromEntries(this.measures),
                                    navigation: window.performance?.timing ? {
                                        domContentLoaded: performance.timing.domContentLoadedEventEnd - performance.timing.navigationStart,
                                        loadComplete: performance.timing.loadEventEnd - performance.timing.navigationStart
                                    } : null
                                };
                            }
                        };

                        // Enhanced browser capability detection with Phase 5 optimizations
                        const BrowserSupport = {
                            wasm: false,
                            wasmVersion: null,
                            jsModules: false,
                            serviceWorker: false,
                            webgl: false,
                            browserName: 'unknown',
                            browserVersion: null,

                            detect: function() {
                                this.detectBrowser();
                                this.detectWasm();
                                this.detectFeatures();
                                this.logSupport();
                            },

                            detectBrowser: function() {
                                const ua = navigator.userAgent;
                                if (ua.includes('Chrome')) {
                                    this.browserName = 'chrome';
                                    const match = ua.match(/Chrome\/(\d+)/);
                                    this.browserVersion = match ? parseInt(match[1]) : null;
                                } else if (ua.includes('Safari') && !ua.includes('Chrome')) {
                                    this.browserName = 'safari';
                                    const match = ua.match(/Safari\/(\d+)/);
                                    this.browserVersion = match ? parseInt(match[1]) : null;
                                } else if (ua.includes('Firefox')) {
                                    this.browserName = 'firefox';
                                    const match = ua.match(/Firefox\/(\d+)/);
                                    this.browserVersion = match ? parseInt(match[1]) : null;
                                } else if (ua.includes('Edge')) {
                                    this.browserName = 'edge';
                                    const match = ua.match(/Edge\/(\d+)/);
                                    this.browserVersion = match ? parseInt(match[1]) : null;
                                }
                            },

                            detectWasm: function() {
                                try {
                                    if (typeof WebAssembly === "object" && typeof WebAssembly.instantiate === "function") {
                                        // Test basic WASM support
                                        const module = new WebAssembly.Module(Uint8Array.of(0x0, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00));
                                        if (module instanceof WebAssembly.Module) {
                                            const instance = new WebAssembly.Instance(module);
                                            if (instance instanceof WebAssembly.Instance) {
                                                this.wasm = true;
                                                this.wasmVersion = 'mvp'; // Minimum Viable Product

                                                // Check for advanced WASM features
                                                if (typeof WebAssembly.Memory !== 'undefined') {
                                                    this.wasmVersion = 'advanced';
                                                }

                                                // Browser-specific WASM version checks
                                                if (this.browserName === 'chrome' && this.browserVersion >= 119) {
                                                    this.wasmVersion = 'optimized';
                                                } else if (this.browserName === 'safari' && this.browserVersion < 15) {
                                                    this.wasm = false; // Safari < 15 has WASM issues
                                                }
                                            }
                                        }
                                    }
                                } catch (e) {
                                    this.wasm = false;
                                    console.debug('WASM detection failed:', e.message);
                                }
                            },

                            detectFeatures: function() {
                                this.jsModules = 'noModule' in HTMLScriptElement.prototype;
                                this.serviceWorker = 'serviceWorker' in navigator;

                                // WebGL detection for performance context
                                try {
                                    const canvas = document.createElement('canvas');
                                    this.webgl = !!(canvas.getContext('webgl') || canvas.getContext('experimental-webgl'));
                                } catch (e) {
                                    this.webgl = false;
                                }
                            },

                            logSupport: function() {
                                console.info('Browser compatibility detected:', {
                                    browser: this.browserName + ' ' + (this.browserVersion || 'unknown'),
                                    wasm: this.wasm,
                                    wasmVersion: this.wasmVersion,
                                    jsModules: this.jsModules,
                                    serviceWorker: this.serviceWorker,
                                    webgl: this.webgl
                                });
                            },

                            shouldUseWasm: function() {
                                return this.wasm && this.wasmVersion !== null;
                            }
                        };

                        // Phase 5: Enhanced script loading with lazy loading and optimization
                        const ScriptLoader = {
                            attempts: 0,
                            maxRetries: 3,
                            fallbackUsed: false,
                            lazyLoadingEnabled: true,
                            preloadedResources: new Set(),

                            loadScript: function(src, options = {}) {
                                return new Promise((resolve, reject) => {
                                    const script = document.createElement('script');
                                    script.src = src;

                                    if (options.type) script.type = options.type;
                                    if (options.async !== undefined) script.async = options.async;
                                    if (options.defer !== undefined) script.defer = options.defer;

                                    script.onload = () => resolve(script);
                                    script.onerror = () => reject(new Error('Failed to load script: ' + src));

                                    document.head.appendChild(script);
                                });
                            },

                            async loadWithRetry(src, options = {}) {
                                for (let attempt = 0; attempt < this.maxRetries; attempt++) {
                                    try {
                                        const script = await this.loadScript(src, options);
                                        console.log('Successfully loaded:', src, 'on attempt', attempt + 1);
                                        return script;
                                    } catch (error) {
                                        console.warn('Script load attempt', attempt + 1, 'failed for:', src, error.message);
                                        if (attempt < this.maxRetries - 1) {
                                            // Wait before retry (exponential backoff)
                                            await new Promise(resolve => setTimeout(resolve, Math.pow(2, attempt) * 1000));
                                        }
                                    }
                                }
                                throw new Error('All attempts failed for: ' + src);
                            },

                            // Phase 5: Lazy loading with intersection observer
                            setupLazyLoading: function() {
                                if (!this.lazyLoadingEnabled || !('IntersectionObserver' in window)) {
                                    return this.loadHydrationScriptImmediate();
                                }

                                const app = document.getElementById('summon-app');
                                if (!app) return;

                                // Start performance tracking
                                PerformanceTracker.mark('lazy-loading-start');

                                const observer = new IntersectionObserver((entries) => {
                                    entries.forEach(entry => {
                                        if (entry.isIntersecting) {
                                            PerformanceTracker.mark('viewport-intersection');
                                            observer.unobserve(entry.target);
                                            this.loadHydrationScriptImmediate();
                                        }
                                    });
                                }, {
                                    rootMargin: '50px', // Start loading 50px before element comes into view
                                    threshold: 0.1
                                });

                                observer.observe(app);

                                // Fallback: Load after 3 seconds regardless
                                setTimeout(() => {
                                    if (!app.hasAttribute('data-hydration-loading')) {
                                        console.log('Lazy loading timeout reached, loading hydration script');
                                        this.loadHydrationScriptImmediate();
                                    }
                                }, 3000);
                            },

                            // Phase 5: Dynamic bundle selection with optimization
                            getBundleStrategy: function() {
                                const strategy = {
                                    useWasm: BrowserSupport.shouldUseWasm(),
                                    bundlePath: '',
                                    options: {},
                                    fallbacks: []
                                };

                                if (strategy.useWasm) {
                                    // Modern WASM bundle with ES modules
                                    strategy.bundlePath = '/summon-hydration.wasm.js';
                                    strategy.options = {
                                        type: 'module',
                                        async: true
                                    };
                                    strategy.fallbacks = [
                                        { path: '/summon-hydration.js', options: { async: true } }
                                    ];
                                } else {
                                    // Legacy JavaScript bundle
                                    strategy.bundlePath = '/summon-hydration.js';
                                    strategy.options = { async: true };
                                    strategy.fallbacks = [];
                                }

                                return strategy;
                            },

                            async loadHydrationScriptImmediate() {
                                const app = document.getElementById('summon-app');
                                if (!app) {
                                    console.error('Summon app container not found');
                                    return;
                                }

                                PerformanceTracker.mark('hydration-loading-start');
                                app.setAttribute('data-hydration-loading', 'true');

                                try {
                                    const strategy = this.getBundleStrategy();
                                    let script;

                                    console.log('Loading ' + (strategy.useWasm ? 'WASM' : 'JS') + ' hydration client for ' + BrowserSupport.browserName);

                                    try {
                                        script = await this.loadWithRetry(strategy.bundlePath, strategy.options);
                                        PerformanceTracker.mark('primary-script-loaded');
                                    } catch (error) {
                                        console.warn(`Primary script failed, trying fallbacks:`, error.message);

                                        // Try fallback strategies
                                        for (const fallback of strategy.fallbacks) {
                                            try {
                                                script = await this.loadWithRetry(fallback.path, fallback.options);
                                                this.fallbackUsed = true;
                                                PerformanceTracker.mark('fallback-script-loaded');
                                                break;
                                            } catch (fallbackError) {
                                                console.warn('Fallback ' + fallback.path + ' failed:', fallbackError.message);
                                            }
                                        }

                                        if (!script) {
                                            throw new Error('All script loading strategies failed');
                                        }
                                    }

                                    // Mark hydration as ready
                                    const hydrationMode = this.fallbackUsed ? 'js-fallback' : (strategy.useWasm ? 'wasm' : 'js');
                                    app.setAttribute('data-hydration-ready', 'true');
                                    app.setAttribute('data-hydration-mode', hydrationMode);
                                    app.removeAttribute('data-hydration-loading');

                                    PerformanceTracker.mark('hydration-ready');
                                    const loadTime = PerformanceTracker.measure('total-hydration-time', 'hydration-loading-start', 'hydration-ready');

                                    // Dispatch hydration ready event with performance metrics
                                    const event = new CustomEvent('summon:hydration-ready', {
                                        detail: {
                                            mode: hydrationMode,
                                            fallbackUsed: this.fallbackUsed,
                                            browserSupport: BrowserSupport,
                                            performance: {
                                                loadTime: loadTime,
                                                metrics: PerformanceTracker.getMetrics()
                                            }
                                        }
                                    });
                                    document.dispatchEvent(event);

                                    // Log performance metrics
                                    console.info('Hydration performance:', {
                                        mode: hydrationMode,
                                        loadTime: loadTime.toFixed(2) + 'ms',
                                        bundleSize: script ? (script.src.length || 'unknown') : 'unknown'
                                    });

                                } catch (finalError) {
                                    console.error('All hydration attempts failed:', finalError.message);
                                    app.setAttribute('data-hydration-failed', 'true');
                                    app.removeAttribute('data-hydration-loading');

                                    PerformanceTracker.mark('hydration-failed');

                                    // Dispatch hydration failed event
                                    const event = new CustomEvent('summon:hydration-failed', {
                                        detail: { error: finalError.message }
                                    });
                                    document.dispatchEvent(event);

                                    // Graceful degradation - ensure forms still work
                                    this.enableStaticFormFallbacks();
                                }
                            },

                            enableStaticFormFallbacks: function() {
                                console.log('Enabling static form fallbacks');

                                // Make sure all interactive elements are accessible
                                const interactiveElements = document.querySelectorAll('[data-onclick-action], [data-onchange-action]');
                                interactiveElements.forEach(element => {
                                    element.style.opacity = '1';
                                    element.style.pointerEvents = 'auto';
                                    element.removeAttribute('disabled');
                                });

                                // Add basic form validation
                                const forms = document.querySelectorAll('form');
                                forms.forEach(form => {
                                    form.addEventListener('submit', function(e) {
                                        const requiredFields = form.querySelectorAll('[required]');
                                        for (const field of requiredFields) {
                                            if (!field.value.trim()) {
                                                e.preventDefault();
                                                field.focus();
                                                alert('Please fill in all required fields');
                                                return;
                                            }
                                        }
                                    });
                                });
                            }
                        };

                        // Phase 5: Initialize with performance tracking and lazy loading
                        PerformanceTracker.mark('summon-init-start');

                        function initializeOptimized() {
                            BrowserSupport.detect();
                            PerformanceTracker.mark('browser-detection-complete');

                            // Check if immediate loading is needed (critical above-the-fold content)
                            const app = document.getElementById('summon-app');
                            const isAboveTheFold = app && (app.getBoundingClientRect().top < window.innerHeight);

                            if (isAboveTheFold || !ScriptLoader.lazyLoadingEnabled) {
                                console.log('Loading hydration immediately (above-the-fold or lazy loading disabled)');
                                ScriptLoader.loadHydrationScriptImmediate();
                            } else {
                                console.log('Setting up lazy loading for hydration script');
                                ScriptLoader.setupLazyLoading();
                            }

                            PerformanceTracker.mark('init-complete');
                            const initTime = PerformanceTracker.measure('total-init-time', 'summon-init-start', 'init-complete');
                            console.debug('Summon initialization completed in ' + initTime.toFixed(2) + 'ms');
                        }

                        if (document.readyState === 'loading') {
                            document.addEventListener('DOMContentLoaded', initializeOptimized);
                        } else {
                            initializeOptimized();
                        }

                        // Global error handling for hydration
                        window.addEventListener('error', function(event) {
                            if (event.filename && (event.filename.includes('summon-hydration') || event.filename.includes('wasm'))) {
                                console.error('Hydration script error:', event.error);
                                const app = document.getElementById('summon-app');
                                if (app && !app.hasAttribute('data-hydration-ready')) {
                                    app.setAttribute('data-hydration-failed', 'true');
                                    ScriptLoader.enableStaticFormFallbacks();
                                }
                            }
                        });

                        // Phase 5: Service Worker registration for caching (if supported)
                        if ('serviceWorker' in navigator && BrowserSupport.serviceWorker) {
                            window.addEventListener('load', function() {
                                navigator.serviceWorker.register('/sw.js')
                                    .then(function(registration) {
                                        console.log('Service Worker registered:', registration.scope);
                                    })
                                    .catch(function(error) {
                                        console.debug('Service Worker registration failed:', error);
                                    });
                            });
                        }

                        // Phase 5: Performance monitoring and analytics
                        window.addEventListener('load', function() {
                            setTimeout(function() {
                                const metrics = PerformanceTracker.getMetrics();

                                // Log comprehensive performance metrics
                                console.group('🚀 Summon Performance Metrics');
                                console.log('Initialization time:', metrics.measures['total-init-time'] || 'N/A');
                                console.log('Hydration time:', metrics.measures['total-hydration-time'] || 'N/A');
                                console.log('Browser detection:', BrowserSupport.browserName, BrowserSupport.browserVersion);
                                console.log('WASM support:', BrowserSupport.wasm, BrowserSupport.wasmVersion);
                                console.log('Module support:', BrowserSupport.jsModules);
                                console.log('Navigation timing:', metrics.navigation);
                                console.groupEnd();

                                // Dispatch metrics event for external monitoring
                                const metricsEvent = new CustomEvent('summon:performance-metrics', {
                                    detail: {
                                        ...metrics,
                                        browserSupport: BrowserSupport,
                                        timestamp: Date.now()
                                    }
                                });
                                document.dispatchEvent(metricsEvent);

                                // Check Phase 5 verification criteria
                                const timeToInteractive = metrics.navigation?.domContentLoaded || 0;
                                if (timeToInteractive > 0) {
                                    console.log('⏱️ Time to Interactive: ' + timeToInteractive + 'ms ' + (timeToInteractive < 3000 ? '✅' : '❌') + ' (target: <3000ms)');
                                }
                            }, 1000); // Wait 1 second for all metrics to settle
                        });

                        // Export for debugging and external monitoring
                        window.SummonBrowserSupport = BrowserSupport;
                        window.SummonScriptLoader = ScriptLoader;
                        window.SummonPerformanceTracker = PerformanceTracker;
                    })();
                </script>
            </body>
            </html>
        """.trimIndent()
    }

    actual open fun hydrateComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
        // Hydration on JVM/server-side doesn't make sense in the same way as client-side
        // This is typically a no-op for server-side rendering
        // In a full implementation, this might generate hydration instructions for the client
        // For now, we'll just log a warning
        System.err.println("Warning: hydrateComposableRoot called on JVM platform. This is typically a client-side operation.")
    }

    actual open fun renderModal(
        onDismiss: () -> Unit,
        modifier: Modifier,
        variant: code.yousef.summon.components.feedback.ModalVariant,
        size: code.yousef.summon.components.feedback.ModalSize,
        dismissOnBackdropClick: Boolean,
        showCloseButton: Boolean,
        header: (@Composable () -> Unit)?,
        footer: (@Composable () -> Unit)?,
        content: @Composable () -> Unit
    ) {
        requireBuilder().div {
            // Modal overlay
            applyModifier(
                Modifier()
                    .style("position", "fixed")
                    .style("top", "0")
                    .style("left", "0")
                    .style("width", "100%")
                    .style("height", "100%")
                    .style("background-color", "rgba(0, 0, 0, 0.5)")
                    .style("z-index", "1000")
                    .style("display", "flex")
                    .style("align-items", "center")
                    .style("justify-content", "center")
                    .then(modifier)
            )
            
            // Register dismiss callback for backdrop click
            if (dismissOnBackdropClick) {
                val dismissCallbackId = CallbackRegistry.registerCallback(onDismiss)
                attributes["data-onclick-id"] = dismissCallbackId
                attributes["data-onclick-action"] = "true"
                attributes["data-backdrop-dismiss"] = "true"
            }
            
            // Modal dialog
            div {
                applyModifier(
                    Modifier()
                        .style("background-color", "#ffffff")
                        .style("border-radius", "8px")
                        .style("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.3)")
                        .style("max-width", when (size) {
                            code.yousef.summon.components.feedback.ModalSize.SMALL -> "400px"
                            code.yousef.summon.components.feedback.ModalSize.MEDIUM -> "600px"
                            code.yousef.summon.components.feedback.ModalSize.LARGE -> "800px"
                            code.yousef.summon.components.feedback.ModalSize.EXTRA_LARGE -> "1000px"
                        })
                        .style("max-height", "90vh")
                        .style("overflow", "auto")
                        .then(when (variant) {
                            code.yousef.summon.components.feedback.ModalVariant.ALERT -> 
                                Modifier().style("border", "2px solid #ff6b6b")
                            code.yousef.summon.components.feedback.ModalVariant.CONFIRMATION -> 
                                Modifier().style("border", "2px solid #4ecdc4")
                            code.yousef.summon.components.feedback.ModalVariant.FULLSCREEN -> 
                                Modifier().style("width", "100vw").style("height", "100vh").style("border-radius", "0")
                            else -> Modifier()
                        })
                )
                
                // Prevent event bubbling to backdrop
                attributes["onclick"] = "event.stopPropagation()"
                
                // Modal header
                header?.let { headerContent ->
                    div {
                        applyModifier(
                            Modifier()
                                .style("border-bottom", "1px solid #e0e0e0")
                                .style("display", "flex")
                                .style("justify-content", "space-between")
                                .style("align-items", "center")
                        )
                        
                        renderContent(headerContent)
                        
                        // Close button
                        if (showCloseButton) {
                            button {
                                val closeCallbackId = CallbackRegistry.registerCallback(onDismiss)
                                attributes["data-onclick-id"] = closeCallbackId
                                attributes["data-onclick-action"] = "true"
                                
                                applyModifier(
                                    Modifier()
                                        .style("background-color", "transparent")
                                        .style("border", "none")
                                        .style("font-size", "24px")
                                        .style("cursor", "pointer")
                                        .style("padding", "8px")
                                )
                                
                                +"×"
                            }
                        }
                    }
                }
                
                // Modal content
                div {
                    renderContent(content)
                }
                
                // Modal footer
                footer?.let { footerContent ->
                    div {
                        applyModifier(
                            Modifier()
                                .style("border-top", "1px solid #e0e0e0")
                        )
                        renderContent(footerContent)
                    }
                }
            }
        }
    }

    actual open fun renderRow(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Row styling should be in Modifier ")
            renderContent(content)
        }
    }

    actual open fun renderColumn(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Column styling should be in Modifier ")
            renderContent(content)
        }
    }

    actual open fun renderBox(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    actual open fun renderBoxContainer(modifier: Modifier, content: @Composable () -> Unit) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().style("display", "block"))) // Typically a div
            renderContent(content)
        }
    }

    actual open fun renderImage(src: String, alt: String?, modifier: Modifier) {
        requireBuilder().img {
            this.src = src
            this.alt = alt ?: ""
            applyModifier(modifier)
        }
    }

    actual open fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    ) {
        requireBuilder().span {
            applyModifier(modifier)
            if (onClick != null) {
                attributes["data-onclick-action"] = "true"
                comment(" JS Hook needed for onClick ")
                val currentStyle = attributes["style"] ?: ""
                if ("cursor" !in currentStyle) attributes["style"] = "$currentStyle;cursor:pointer;".trimStart(';')
            }
            if (svgContent != null) {
                unsafe { raw(svgContent) }
            } else {
                i { comment(" Icon font class for '$name' needed via CSS/Modifier ") }
            }
        }
    }

    actual open fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        requireBuilder().div {
            attributes["role"] = "alert"
            applyModifier(modifier)
            comment(" Alert styling for variant '${variant?.name}' needed via CSS/Modifier ")
            renderContent(content)
        }
    }

    actual open fun renderBadge(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        requireBuilder().span {
            applyModifier(modifier)
            comment(" Badge styling via Modifier/CSS needed ")
            renderContent(content)
        }
    }

    actual open fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().input(type = InputType.checkBox) {
            applyModifier(modifier)
            this.checked = checked
            this.disabled = !enabled
            id = "checkbox-${UUID.randomUUID()}"
            name = id
            attributes["data-onchange-action"] = "true"
            comment(" onCheckedChange handler needed (JS) ")
        }
    }

    actual open fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().input(type = InputType.range) {
            applyModifier(modifier)
            min = valueRange.start.toString()
            max = valueRange.endInclusive.toString()
            if (steps > 0) step = ((valueRange.endInclusive - valueRange.start) / steps).toString()
            this.value = value.toString()
            this.disabled = !enabled
            id = "slider-${UUID.randomUUID()}"
            name = id
            comment(" onValueChange handler needed (JS) ")
            attributes["data-onchange-action"] = "true"
        }
    }

    actual open fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().div { // Requires JS library
            applyModifier(modifier)
            comment(" Range Slider requires JS library. Rendering basic inputs fallback. ")
            val stepValue = if (steps > 0) ((valueRange.endInclusive - valueRange.start) / steps) else null

            label {
                +"Start: "; input(type = InputType.range) {
                id = "range-start-${UUID.randomUUID()}"
                min = valueRange.start.toString()
                max = valueRange.endInclusive.toString()
                stepValue?.let { this.step = it.toString() }
                this.value = value.start.toString()
                this.disabled = !enabled
                attributes["data-onchange-action"] = "true"
            }
            }
            br()
            label {
                +"End: "; input(type = InputType.range) {
                id = "range-end-${UUID.randomUUID()}"
                min = valueRange.start.toString()
                max = valueRange.endInclusive.toString()
                stepValue?.let { this.step = it.toString() }
                this.value = value.endInclusive.toString()
                this.disabled = !enabled
                attributes["data-onchange-action"] = "true"
            }
            }
            comment(" onValueChange handler needed (JS) to coordinate sliders ")
        }
    }

    actual open fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().input(type = InputType.time) {
            applyModifier(modifier)
            if (value != null) this.value = value.toString().take(8) // HH:MM:SS
            this.disabled = !enabled
            comment(" 12/24 hour display depends on browser/locale. ")
            id = "time-${UUID.randomUUID()}"
            name = id
            attributes["data-onchange-action"] = "true"
            comment(" onValueChange handler needed (JS) ")
        }
    }

    actual open fun renderLink(href: String, modifier: Modifier) {
        requireBuilder().a(href = href) {
            applyModifier(modifier)
            +href
        }
    }

    actual open fun renderLink(
        modifier: Modifier,
        href: String,
        content: @Composable () -> Unit
    ) {
        requireBuilder().a(href = href) {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    actual open fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        val inputId = "file-${UUID.randomUUID()}"
        requireBuilder().input(type = InputType.file) {
            applyModifier(modifier)
            this.disabled = !enabled
            if (accept != null) this.accept = accept
            this.multiple = multiple
            if (capture != null) attributes["capture"] = capture
            id = inputId
            name = id
            comment(" onFilesSelected handler needed (JS) ")
            attributes["data-onchange-action"] = "true"
        }
        return { System.err.println("Programmatic file upload trigger not available server-side.") }
    }

    actual open fun renderForm(
        onSubmit: (() -> Unit)?,
        modifier: Modifier,
        content: @Composable FormContent.() -> Unit
    ) {
        requireBuilder().form {
            applyModifier(modifier)
            if (onSubmit != null) {
                attributes["data-onsubmit-action"] = "true"
                comment(" onSubmit handler needed (JS). Form action/method? ")
            }
            // Create FormContent and wrap it in FlowContentCompat
            val formContentImpl = this
            content(formContentImpl.asFlowContentCompat())
        }
    }

    // NOTE: Commented out because no corresponding expect declaration
    /*
    actual open fun renderTabs(
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier,
        tabs: List<Tab>
    ) {
        val builder = requireBuilder() // Get the builder context once
        builder.div { // Outer container
            this.applyModifier(modifier) // Apply modifier to the outer div
            comment(" Tab layout requires JS for interaction ")

            // Tab Headers
            div(classes = "tab-list") {
                attributes["role"] = "tablist"
                tabs.forEachIndexed { index, tab ->
                    val tabId = "tab-${tab.id}"
                    val panelId = "panel-${tab.id}"
                    // Fixed: Use standard kotlinx.html button()
                    button(classes = "tab-item ${if (index == selectedTabIndex) "selected" else ""}") {
                        attributes["role"] = "tab"
                        attributes["aria-controls"] = panelId
                        attributes["aria-selected"] = (index == selectedTabIndex).toString()
                        attributes["data-tab-index"] = index.toString()
                        attributes["data-onclick-action"] = "true"
                        attributes["id"] = tabId
                        comment(" onTabSelected handler needed (JS) ")
                        comment(" CSS needed for selected state ")
                        +(tab.title ?: "Tab ${index + 1}")
                    }
                }
            }

            // Tab Panels
            div(classes = "tab-panels") {
                tabs.forEachIndexed { index, tab ->
                    val panelId = "panel-${tab.id}"
                    val tabId = "tab-${tab.id}" // Match ID generation
                    // Fixed: Use standard kotlinx.html div()
                    div(classes = "tab-panel ${if (index == selectedTabIndex) "active" else ""}") {
                        attributes["role"] = "tabpanel"
                        attributes["aria-labelledby"] = tabId
                        attributes["id"] = panelId
                        if (index != selectedTabIndex) {
                            comment(" Inactive panel should be hidden via CSS/JS ")
                        }
                        // Fixed: Handle composable content
                        val savedBuilder = currentBuilder
                        currentBuilder = this
                        try {
                            // Handle imported Tab.content as a @Composable function
                            tab.content()
                        } finally {
                            currentBuilder = savedBuilder
                        }
                        comment(" CSS needed for active state ")
                    }
                }
            }
        }
    }
    */

    // NOTE: Commented out because no corresponding expect declaration
    /*
    actual open fun renderTab(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Tab content should be wrapped in a tab panel ")
            renderContent(content)
        }
    }
    */

    actual open fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().label { // Requires specific CSS
            applyModifier(modifier) // Style label, CSS targets input/span
            comment(" Switch requires CSS styling ")
            input(type = InputType.checkBox) {
                this.checked = checked
                this.disabled = !enabled
                attributes["style"] =
                    "position:absolute; opacity:0; pointer-events:none; width:0; height:0;" // Hide visually
                attributes["data-onchange-action"] = "true"
                comment(" onCheckedChange handler needed (JS) ")
            }
            span { comment(" CSS needed for switch slider appearance ") }
        }
    }

    actual open fun renderHtmlTag(
        tagName: String,
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        val builder = requireBuilder()

        // Create a div container to hold our custom tag content
        builder.div {
            // Apply the modifier to the div for now
            applyModifier(modifier)

            // Add a comment indicating this is a custom tag wrapper
            comment(" Custom tag '$tagName' wrapped in div. JS needed to transform DOM. ")

            // Add data attribute to help JS transform this element
            attributes["data-custom-tag"] = tagName

            // Render the content in the div
            renderContent(content)
        }

        // Add a comment about the limitation
        builder.comment(" Note: Direct custom tag rendering requires JS DOM manipulation ")
    }

    actual open fun renderSpan(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        requireBuilder().span {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    actual open fun renderDivider(modifier: Modifier) {
        requireBuilder().hr {
            applyModifier(modifier)
        }
    }

    actual open fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        // Basic div implementation for JVM, actual expansion behavior might need JS
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().style("border", "1px solid grey")))
            comment(" ExpansionPanel: JS might be needed for interactive expand/collapse ")
            renderContent(content)
        }
    }

    actual open fun renderGrid(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().style("display", "grid")))
            renderContent(content)
        }
    }

    actual open fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        // JVM equivalent: scrollable div
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().overflowY("auto")))
            renderContent(content)
        }
    }

    actual open fun renderLazyRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        // JVM equivalent: scrollable div
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().overflowX("auto")))
            renderContent(content)
        }
    }

    actual open fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        // Basic div, actual responsiveness will depend on CSS within modifier and content
        requireBuilder().div {
            applyModifier(modifier)
            comment(" ResponsiveLayout: Ensure CSS handles different screen sizes ")
            // Add screen size detection script (example from previous JvmPlatformRenderer)
            script(type = "text/javascript") {
                unsafe {
                    raw(
                        """
                    (function() {
                        const BREAKPOINTS = { SMALL: 600, MEDIUM: 960, LARGE: 1280 };
                        function determineScreenSize() {
                            const width = window.innerWidth;
                            if (width < BREAKPOINTS.SMALL) return 'SMALL';
                            else if (width < BREAKPOINTS.MEDIUM) return 'MEDIUM';
                            else if (width < BREAKPOINTS.LARGE) return 'LARGE';
                            else return 'XLARGE';
                        }
                        function updateLayout() {
                            const layout = document.currentScript ? document.currentScript.parentElement : document.body;
                            const screenSize = determineScreenSize();
                            layout.classList.remove('small-screen', 'medium-screen', 'large-screen', 'xlarge-screen');
                            layout.classList.add(screenSize.toLowerCase() + '-screen');
                            layout.setAttribute('data-screen-size', screenSize);
                            const event = new CustomEvent('screenSizeChanged', { detail: { screenSize: screenSize } });
                            layout.dispatchEvent(event);
                        }
                        if (document.readyState === 'loading') { document.addEventListener('DOMContentLoaded', updateLayout); }
                        else { updateLayout(); }
                        window.addEventListener('resize', updateLayout);
                    })();
                    """
                    )
                }
            }
            renderContent(content)
        }
    }

    actual open fun renderSnackbar(message: String, actionLabel: String?, onAction: (() -> Unit)?) {
        // JVM: Simple text representation, or a div styled to look like a snackbar.
        // Actual snackbar behavior (timing, dismissal) is typically JS-driven.
        requireBuilder().div {
            style =
                "position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%); background-color: #333; color: white; padding: 10px 20px; border-radius: 4px; z-index: 1000;"
            +message
            if (actionLabel != null) {
                button {
                    style = "margin-left: 15px; background: none; border: none; color: #80deea; cursor: pointer;"
                    +actionLabel
                    if (onAction != null) {
                        comment(" JS hook for onAction on snackbar button needed ")
                        // attributes["data-onclick-action"] = createJsAction(onAction) // Placeholder for JS hookup
                    }
                }
            }
            comment(" Snackbar: JS needed for timeout and dismissal ")
        }
    }

    actual open fun renderDropdownMenu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable (() -> Unit)
    ) {
        if (expanded) {
            requireBuilder().div {
                applyModifier(
                    modifier.then(
                        Modifier().style("position", "absolute").style("border", "1px solid #ccc")
                            .style("background-color", "white").style("z-index", "100")
                    )
                )
                comment(" DropdownMenu: JS for positioning and dismissal (onDismissRequest) needed ")
                attributes["data-onclick-dismiss"] = "true" // Example for JS hook
                renderContent(content)
            }
        }
    }

    actual open fun renderTooltip(text: String, modifier: Modifier, content: @Composable (() -> Unit)) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().style("position", "relative")))
            attributes["title"] = text // Basic browser tooltip
            comment(" Tooltip: For custom styling, JS/CSS solution is better ")
            renderContent(content)
        }
    }

    actual open fun renderModal(
        visible: Boolean,
        onDismissRequest: () -> Unit,
        title: String?,
        content: @Composable (() -> Unit),
        actions: @Composable (() -> Unit)?
    ) {
        if (visible) {
            requireBuilder().div { // Modal overlay
                style =
                    "position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 2000;"
                attributes["data-onclick-dismiss-modal"] = "true" // Hook for closing by clicking overlay

                div { // Modal content
                    style =
                        "background-color: white; padding: 20px; border-radius: 5px; min-width: 300px; max-width: 80%;"
                    if (title != null) {
                        h3 { +title }
                    }
                    renderContent(content)
                    if (actions != null) {
                        div { style = "margin-top: 10px;"; renderContent(actions) }
                    }
                }
                comment(" Modal: JS for onDismissRequest and complex interactions needed ")
            }
        }
    }

    actual open fun renderScreen(modifier: Modifier, content: @Composable (FlowContentCompat.() -> Unit)) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().style("width", "100%").style("height", "100vh")))
            renderContent(content)
        }
    }

    actual open fun renderHtml(htmlContent: String, modifier: Modifier) {
        requireBuilder().div { // Wrapper div to apply modifier
            applyModifier(modifier)
            unsafe { raw(htmlContent) }
        }
    }

    actual open fun renderSurface(modifier: Modifier, elevation: Int, content: @Composable (() -> Unit)) {
        // Elevation can be simulated with box-shadow
        val elevationStyle = when (elevation) {
            1 -> "box-shadow: 0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24);"
            2 -> "box-shadow: 0 3px 6px rgba(0,0,0,0.16), 0 3px 6px rgba(0,0,0,0.23);"
            // Add more levels as needed
            else -> ""
        }
        requireBuilder().div {
            applyModifier(modifier)
            if (elevationStyle.isNotBlank()) {
                attributes["style"] = (attributes["style"] ?: "") + elevationStyle
            }
            renderContent(content)
        }
    }

    actual open fun renderSwipeToDismiss(
        state: Any, // State object, likely for JS interop
        background: @Composable (() -> Unit),
        modifier: Modifier,
        content: @Composable (() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" SwipeToDismiss: Full functionality requires JS. This is a static representation. ")
            div { // Background placeholder
                style = "position: absolute; top: 0; left: 0; right: 0; bottom: 0; z-index: 0;"
                renderContent(background)
            }
            div { // Content placeholder
                style = "position: relative; z-index: 1; background-color: white;" // Ensure content is above background
                renderContent(content)
            }
        }
    }

    actual open fun renderVerticalPager(
        count: Int,
        state: Any,
        modifier: Modifier,
        content: @Composable ((Int) -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" VerticalPager: JS for pagination logic needed. Displaying first page. ")
            if (count > 0) {
                renderContent { content(0) } // Render first page as placeholder
            }
        }
    }

    actual open fun renderHorizontalPager(
        count: Int,
        state: Any,
        modifier: Modifier,
        content: @Composable ((Int) -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" HorizontalPager: JS for pagination logic needed. Displaying first page. ")
            if (count > 0) {
                renderContent { content(0) } // Render first page as placeholder
            }
        }
    }

    actual open fun renderAspectRatioContainer(ratio: Float, modifier: Modifier, content: @Composable (() -> Unit)) {
        requireBuilder().div {
            // CSS trick for aspect ratio box
            val paddingBottom = "${(1f / ratio) * 100}%"
            val outerStyle = "position: relative; width: 100%; height: 0; padding-bottom: $paddingBottom;"
            val innerStyle = "position: absolute; top: 0; left: 0; width: 100%; height: 100%;"

            attributes["style"] = (attributes["style"] ?: "") + outerStyle
            applyModifier(modifier) // Apply user modifier to outer div

            div { // Inner div for content
                attributes["style"] = innerStyle
                renderContent(content)
            }
        }
    }

    actual open fun renderFilePicker(
        onFilesSelected: (List<FileInfo>) -> Unit,
        enabled: Boolean,
        multiple: Boolean,
        accept: String?,
        modifier: Modifier,
        actions: @Composable (() -> Unit)? // Content for the button itself
    ) {
        requireBuilder().input(type = InputType.file) {
            applyModifier(modifier)
            this.multiple = multiple
            if (accept != null) this.accept = accept
            this.disabled = !enabled
            // JS needed to handle onFilesSelected and map to FileInfo
            comment(" FilePicker: JS needed for onFilesSelected callback and FileInfo creation ")
            attributes["data-onfilesselected-action"] = "true"
            // 'actions' would typically be the content of a button that triggers this input if it's hidden
            // For a direct input, 'actions' might not be directly applicable this way.
            // If 'actions' represents the button text/content for a styled picker:
            if (actions != null) {
                comment(" FilePicker 'actions' provided, but direct input shown. Consider custom styling with a label/button. ")
                // This actual implementation shows a raw file input. To use `actions` as the clickable element,
                // this input would need to be hidden and triggered by a button rendered via `actions`.
            }
        }
    }

    actual open fun renderAlert(
        message: String,
        variant: AlertVariant,
        modifier: Modifier,
        title: String?,
        icon: @Composable (() -> Unit)?,
        actions: @Composable (() -> Unit)?
    ) {
        val alertColor = when (variant) {
            AlertVariant.INFO -> "#e0f7fa"
            AlertVariant.SUCCESS -> "#e8f5e9"
            AlertVariant.WARNING -> "#fff3e0"
            AlertVariant.ERROR -> "#ffebee"
            AlertVariant.NEUTRAL -> "#f5f5f5"
        }
        requireBuilder().div {
            style =
                "padding: 15px; margin-bottom: 20px; border: 1px solid transparent; border-radius: 4px; background-color: $alertColor;"
            applyModifier(modifier)
            if (icon != null) {
                span { style = "margin-right: 10px;"; renderContent(icon) }
            }
            if (title != null) {
                strong { +title; style = "display: block; margin-bottom: 5px;" }
            }
            +message
            if (actions != null) {
                div { style = "margin-top: 10px;"; renderContent(actions) }
            }
        }
    }

    actual open fun renderCard(modifier: Modifier, elevation: Int, content: @Composable (() -> Unit)) {
        // Re-use renderSurface for card appearance
        renderSurface(modifier.then(Modifier().style("border", "1px solid #ddd")), elevation, content)
    }

    actual open fun renderLinearProgressIndicator(progress: Float?, modifier: Modifier, type: ProgressType) {
        requireBuilder().progress {
            applyModifier(modifier)
            if (type != ProgressType.INDETERMINATE && progress != null) {
                this.value = progress.toString()
                max = "1"
            } else {
                comment(" Indeterminate LinearProgressIndicator ")
                // No value attribute for indeterminate HTML progress, styling might be needed
            }
        }
    }

    actual open fun renderCircularProgressIndicator(progress: Float?, modifier: Modifier, type: ProgressType) {
        // HTML doesn't have a native circular progress. Simulate with text or requires SVG/JS.
        requireBuilder().div {
            applyModifier(modifier)
            if (type != ProgressType.INDETERMINATE && progress != null) {
                +"Progress: ${(progress * 100).toInt()}% (Circular - requires SVG/JS for visual)"
            } else {
                +"Loading... (Circular - requires SVG/JS for visual)"
            }
            comment(" CircularProgressIndicator: Visual representation requires SVG/CSS or JS library. ")
        }
    }

    actual open fun renderModalBottomSheet(
        onDismissRequest: () -> Unit,
        modifier: Modifier,
        content: @Composable (() -> Unit)
    ) {
        // Simplified modal for JVM, true bottom sheet behavior is JS/CSS driven.
        requireBuilder().div { // Overlay
            style =
                "position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.3); display: flex; align-items: flex-end; justify-content: center; z-index: 1500;"
            attributes["data-onclick-dismiss-modal-sheet"] = "true"

            div { // Sheet content
                style =
                    "background-color: white; padding: 20px; border-top-left-radius: 8px; border-top-right-radius: 8px; width: 100%; max-width: 600px; box-shadow: 0 -2px 10px rgba(0,0,0,0.1);"
                applyModifier(modifier)
                renderContent(content)
            }
            comment(" ModalBottomSheet: JS for onDismissRequest and animations needed ")
        }
    }

    actual open fun renderAlertDialog(
        onDismissRequest: () -> Unit,
        confirmButton: @Composable (() -> Unit),
        modifier: Modifier,
        dismissButton: @Composable (() -> Unit)?,
        icon: @Composable (() -> Unit)?,
        title: @Composable (() -> Unit)?,
        text: @Composable (() -> Unit)?
    ) {
        requireBuilder().div { // Overlay
            style =
                "position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 2500;"

            div { // Dialog box
                style =
                    "background-color: white; padding: 25px; border-radius: 8px; min-width: 280px; max-width: 560px; box-shadow: 0 4px 20px rgba(0,0,0,0.2);"
                applyModifier(modifier)
                if (icon != null) {
                    div { style = "text-align: center; margin-bottom: 15px;"; renderContent(icon) }
                }
                if (title != null) {
                    div { style = "font-size: 1.25em; font-weight: bold; margin-bottom: 10px;"; renderContent(title) }
                }
                if (text != null) {
                    div { style = "margin-bottom: 20px;"; renderContent(text) }
                }
                div { // Buttons
                    style = "display: flex; justify-content: flex-end; gap: 10px;"
                    if (dismissButton != null) {
                        renderContent(dismissButton)
                    }
                    renderContent(confirmButton)
                }
                comment(" AlertDialog: JS for onDismissRequest and button actions needed ")
            }
        }
    }

    actual open fun renderRadioButton(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        label: String?,
        enabled: Boolean,
        modifier: Modifier
    ) {
        val radioId = "radio-${UUID.randomUUID()}"
        requireBuilder().span { // Wrapper for radio and label
            applyModifier(modifier)
            input(
                type = InputType.radio,
                name = "radio-group-${hashCode()}"
            ) { // Ensure unique name for groups if needed
                id = radioId
                this.checked = checked
                this.disabled = !enabled
                attributes["data-onchange-action"] = "true"
                comment(" onCheckedChange JS hook needed ")
            }
            if (label != null) {
                label {
                    htmlFor = radioId
                    +label
                    style = "margin-left: 8px;"
                }
            }
        }
    }

    actual open fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        label: String?,
        modifier: Modifier
    ) {
        val checkboxId = "checkbox-${UUID.randomUUID()}"
        requireBuilder().span { // Wrapper for checkbox and label
            applyModifier(modifier)
            input(type = InputType.checkBox) {
                id = checkboxId
                this.checked = checked
                this.disabled = !enabled
                attributes["data-onchange-action"] = "true"
                comment(" onCheckedChange JS hook needed ")
            }
            if (label != null) {
                label {
                    htmlFor = checkboxId
                    +label
                    style = "margin-left: 8px;"
                }
            }
        }
    }

    // Additional methods required by expect declarations
    actual open fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
        renderLinearProgressIndicator(value, modifier, type)
    }

    actual open fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            attributes["role"] = "group"
            if (labelId != null) attributes["aria-labelledby"] = labelId
            if (errorMessageId != null && isError) attributes["aria-describedby"] = errorMessageId
            if (isRequired) attributes["aria-required"] = "true"
            if (isError) attributes["aria-invalid"] = "true"
            renderContent(content)
        }
    }

    actual open fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().input(type = InputType.radio) {
            applyModifier(modifier)
            this.checked = selected
            this.disabled = !enabled
            attributes["data-onclick-action"] = "true"
            comment(" onClick JS hook needed ")
        }
    }

    actual open fun renderSpacer(modifier: Modifier) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" Spacer element ")
        }
    }

    actual open fun renderAspectRatio(
        ratio: Float,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        renderAspectRatioContainer(ratio, modifier) {
            requireBuilder().renderContent(content)
        }
    }

    actual open fun renderCard(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        renderCard(modifier, 1) {
            requireBuilder().renderContent(content)
        }
    }

    actual open fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier
    ) {
        requireBuilder().a(href = href) {
            applyModifier(modifier)
            if (target != null) this.target = target
            if (title != null) this.title = title
            if (ariaLabel != null) attributes["aria-label"] = ariaLabel
            if (ariaDescribedBy != null) attributes["aria-describedby"] = ariaDescribedBy
            +href
        }
    }

    actual open fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    ) {
        // Delegate to the commented out renderTabs implementation
        requireBuilder().div {
            applyModifier(modifier)
            comment(" TabLayout not fully implemented - requires JS ")
            tabs.forEachIndexed { index, tab ->
                button {
                    if (index == selectedTabIndex) {
                        classes = setOf("selected")
                    }
                    attributes["data-tab-index"] = index.toString()
                    +(tab.title ?: "Tab ${index + 1}")
                }
            }
        }
    }

    actual open fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit) {
        requireBuilder().div {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    actual open fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" String-based TabLayout ")
            tabs.forEach { tab ->
                button {
                    if (tab == selectedTab) {
                        classes = setOf("selected")
                    }
                    attributes["data-tab-name"] = tab
                    +tab
                }
            }
            content()
        }
    }

    actual open fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        if (visible) {
            requireBuilder().div {
                applyModifier(modifier)
                comment(" AnimatedVisibility placeholder ")
            }
        }
    }

    actual open fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit) {
        requireBuilder().div {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    actual open fun renderAnimatedContent(modifier: Modifier) {
        requireBuilder().div {
            applyModifier(modifier)
            comment(" AnimatedContent placeholder ")
        }
    }

    actual open fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit) {
        requireBuilder().div {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    actual open fun renderBlock(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().style("display", "block")))
            renderContent(content)
        }
    }

    actual open fun renderInline(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        requireBuilder().span {
            applyModifier(modifier.then(Modifier().style("display", "inline")))
            renderContent(content)
        }
    }

    actual open fun renderDiv(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        requireBuilder().div {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    actual open fun renderHtml(htmlContent: String, modifier: Modifier, sanitize: Boolean) {
        val safeContent = if (sanitize) sanitizeHtml(htmlContent) else htmlContent
        requireBuilder().div {
            applyModifier(modifier)
            unsafe {
                +safeContent
            }
        }
    }

    actual open fun renderGlobalStyle(css: String) {
        addHeadElement("<style>$css</style>")
    }

    private fun sanitizeHtml(htmlContent: String): String {
        // Basic HTML sanitization - remove dangerous elements and attributes
        var sanitized = htmlContent

        // Remove script tags
        sanitized = sanitized.replace(
            Regex(
                "<script[^>]*>.*?</script>",
                setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
            ), ""
        )

        // Remove dangerous attributes
        sanitized = sanitized.replace(Regex("\\s(on\\w+)=[\"'][^\"']*[\"']", RegexOption.IGNORE_CASE), "")
        sanitized = sanitized.replace(Regex("\\shref=[\"']javascript:[^\"']*[\"']", RegexOption.IGNORE_CASE), "")

        // Remove dangerous tags but keep content
        val dangerousTags = listOf("object", "embed", "applet", "iframe", "form", "input", "button")
        for (tag in dangerousTags) {
            sanitized = sanitized.replace(Regex("</?$tag[^>]*>", RegexOption.IGNORE_CASE), "")
        }

        return sanitized
    }

    actual open fun renderLoading(
        modifier: Modifier,
        variant: code.yousef.summon.components.feedback.LoadingVariant,
        size: code.yousef.summon.components.feedback.LoadingSize,
        text: String?,
        textModifier: Modifier
    ) {
        val sizeValue = when (size) {
            code.yousef.summon.components.feedback.LoadingSize.SMALL -> "16px"
            code.yousef.summon.components.feedback.LoadingSize.MEDIUM -> "24px"
            code.yousef.summon.components.feedback.LoadingSize.LARGE -> "32px"
            code.yousef.summon.components.feedback.LoadingSize.EXTRA_LARGE -> "48px"
        }

        requireBuilder().div {
            applyModifier(modifier.style("display", "flex").style("flex-direction", "column").style("align-items", "center").style("gap", "8px"))
            
            when (variant) {
                code.yousef.summon.components.feedback.LoadingVariant.SPINNER -> {
                    div {
                        classes = setOf("summon-spinner")
                        style = """
                            width: $sizeValue;
                            height: $sizeValue;
                            border: 2px solid #f3f3f3;
                            border-top: 2px solid #3498db;
                            border-radius: 50%;
                            animation: summon-spin 2s linear infinite;
                        """.trimIndent()
                    }
                }
                code.yousef.summon.components.feedback.LoadingVariant.DOTS -> {
                    div {
                        classes = setOf("summon-dots")
                        style = """
                            display: flex;
                            align-items: center;
                            gap: 4px;
                        """.trimIndent()
                        repeat(3) { index ->
                            div {
                                style = """
                                    width: calc($sizeValue / 3);
                                    height: calc($sizeValue / 3);
                                    background-color: #3498db;
                                    border-radius: 50%;
                                    animation: summon-dot-pulse 1.4s ease-in-out ${index * 0.16}s infinite both;
                                """.trimIndent()
                            }
                        }
                    }
                }
                code.yousef.summon.components.feedback.LoadingVariant.LINEAR -> {
                    div {
                        classes = setOf("summon-linear")
                        style = """
                            width: $sizeValue;
                            height: 4px;
                            background-color: #f3f3f3;
                            border-radius: 2px;
                            overflow: hidden;
                            position: relative;
                        """.trimIndent()
                        div {
                            style = """
                                position: absolute;
                                top: 0;
                                left: -100%;
                                width: 100%;
                                height: 100%;
                                background-color: #3498db;
                                animation: summon-linear-progress 2s linear infinite;
                            """.trimIndent()
                        }
                    }
                }
                code.yousef.summon.components.feedback.LoadingVariant.CIRCULAR -> {
                    div {
                        classes = setOf("summon-circular")
                        style = """
                            width: $sizeValue;
                            height: $sizeValue;
                            position: relative;
                        """.trimIndent()
                        div {
                            style = """
                                position: absolute;
                                width: 100%;
                                height: 100%;
                                border: 2px solid #f3f3f3;
                                border-radius: 50%;
                            """.trimIndent()
                        }
                        div {
                            style = """
                                position: absolute;
                                width: 100%;
                                height: 100%;
                                border: 2px solid transparent;
                                border-top: 2px solid #3498db;
                                border-radius: 50%;
                                animation: summon-circular-progress 1.5s linear infinite;
                            """.trimIndent()
                        }
                    }
                }
            }
            
            text?.let {
                div {
                    applyModifier(textModifier.style("font-size", "14px").style("color", "#666").style("text-align", "center"))
                    +it
                }
            }
        }

        // Add CSS animations to head if not already added
        addHeadElement("""
            <style>
            @keyframes summon-spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
            
            @keyframes summon-dot-pulse {
                0%, 80%, 100% {
                    transform: scale(0);
                    opacity: 0.5;
                }
                40% {
                    transform: scale(1);
                    opacity: 1;
                }
            }
            
            @keyframes summon-linear-progress {
                0% { left: -100%; }
                100% { left: 100%; }
            }
            
            @keyframes summon-circular-progress {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
            </style>
        """.trimIndent())
    }

    actual open fun renderToast(
        toast: code.yousef.summon.components.feedback.ToastData,
        onDismiss: () -> Unit,
        modifier: Modifier
    ) {
        val (bgColor, borderColor, textColor) = when (toast.variant) {
            code.yousef.summon.components.feedback.ToastVariant.INFO -> Triple("#e3f2fd", "#2196f3", "#1976d2")
            code.yousef.summon.components.feedback.ToastVariant.SUCCESS -> Triple("#e8f5e8", "#4caf50", "#388e3c")
            code.yousef.summon.components.feedback.ToastVariant.WARNING -> Triple("#fff3e0", "#ff9800", "#f57c00")
            code.yousef.summon.components.feedback.ToastVariant.ERROR -> Triple("#ffebee", "#f44336", "#d32f2f")
        }

        requireBuilder().div {
            applyModifier(
                modifier
                    .style("background-color", bgColor)
                    .style("border", "1px solid $borderColor")
                    .style("border-radius", "6px")
                    .style("padding", "12px 16px")
                    .style("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                    .style("display", "flex")
                    .style("align-items", "center")
                    .style("justify-content", "space-between")
                    .style("gap", "12px")
                    .style("min-width", "300px")
                    .style("max-width", "400px")
                    .style("animation", "summon-toast-slide-in 0.3s ease-out")
            )
            
            // Toast content
            div {
                style = "flex: 1; color: $textColor; font-size: 14px; line-height: 1.4;"
                +toast.message
            }
            
            // Action button and dismiss button container
            div {
                style = "display: flex; align-items: center; gap: 8px;"
                
                // Action button if provided
                toast.action?.let { action ->
                    button {
                        style = """
                            background: transparent;
                            border: 1px solid $borderColor;
                            color: $textColor;
                            padding: 4px 8px;
                            border-radius: 4px;
                            font-size: 12px;
                            cursor: pointer;
                            transition: background-color 0.2s;
                        """.trimIndent()
                        +action.label
                        val callbackId = CallbackRegistry.registerCallback(action.onClick)
                        attributes["data-onclick-id"] = callbackId
                        attributes["data-onclick-action"] = "true"
                    }
                }
                
                // Dismiss button if dismissible
                if (toast.dismissible) {
                    button {
                        style = """
                            background: transparent;
                            border: none;
                            color: $textColor;
                            font-size: 16px;
                            cursor: pointer;
                            padding: 0;
                            width: 20px;
                            height: 20px;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            opacity: 0.7;
                            transition: opacity 0.2s;
                        """.trimIndent()
                        +"×"
                        val callbackId = CallbackRegistry.registerCallback(onDismiss)
                        attributes["data-onclick-id"] = callbackId
                        attributes["data-onclick-action"] = "true"
                    }
                }
            }
        }

        // Add toast animations CSS if not already added
        addHeadElement("""
            <style>
            @keyframes summon-toast-slide-in {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            
            @keyframes summon-toast-slide-out {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(100%);
                    opacity: 0;
                }
            }
            </style>
        """.trimIndent())
    }
}
