package codes.yousef.summon.runtime

// Missing imports for runtime components
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.IconType
import codes.yousef.summon.components.foundation.TrustedCss
import codes.yousef.summon.components.foundation.TrustedHtml
import codes.yousef.summon.components.foundation.TrustedSvg
import codes.yousef.summon.components.feedback.AlertVariant
import codes.yousef.summon.components.feedback.ProgressType
import codes.yousef.summon.components.input.FileInfo
import codes.yousef.summon.components.navigation.Tab
import codes.yousef.summon.core.FlowContentCompat
import codes.yousef.summon.core.asFlowContentCompat
import codes.yousef.summon.hydration.SummonTagConsumer
import codes.yousef.summon.modifier.ConditionalStyleDefinition
import codes.yousef.summon.modifier.ConditionalStyleState
import codes.yousef.summon.modifier.MediaStyleDefinition
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ScopedStyleDefinition
import codes.yousef.summon.modifier.StateStyleDefinition
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.modifier.overflowX
import codes.yousef.summon.modifier.overflowY
import codes.yousef.summon.modifier.style
import codes.yousef.summon.security.CspDocument
import codes.yousef.summon.security.PrivateShellContentSecurityPolicy
import codes.yousef.summon.security.PublicHydrationState
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
    private val lastRenderedHeadElements = mutableListOf<String>()
    private val conditionalStyleRules = linkedSetOf<String>()
    private var conditionalStyleHostCounter = 0
    private var activeStyleNonce: String? = null

    // Fix 1: Remove ThreadLocal, use instance variable.
    private var currentBuilder: FlowContent? = null


    actual open fun startRecomposition() {}
    actual open fun endRecomposition() {}

    private fun cssPropertyName(key: String): String =
        if (key.contains('-')) {
            key
        } else {
            key.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
        }

    private fun beginConditionalStyleRender() {
        conditionalStyleRules.clear()
        conditionalStyleHostCounter = 0
    }

    private fun endConditionalStyleRender() {
        conditionalStyleRules.clear()
        conditionalStyleHostCounter = 0
    }

    private fun headElementSnapshot(): List<String> =
        synchronized(headElements) { headElements.toList() }

    private fun clearPendingHeadElements() {
        synchronized(headElements) { headElements.clear() }
    }

    private fun rememberRenderedHeadElements(elements: List<String>) {
        synchronized(lastRenderedHeadElements) {
            lastRenderedHeadElements.clear()
            lastRenderedHeadElements.addAll(elements)
        }
    }

    private fun List<String>.containsTag(tagName: String): Boolean {
        val tag = Regex("""<\s*${Regex.escape(tagName)}(?:\s|>)""", RegexOption.IGNORE_CASE)
        return any(tag::containsMatchIn)
    }

    private fun List<String>.containsMetaAttribute(attribute: String, value: String): Boolean {
        val meta = Regex(
            """<\s*meta\b[^>]*\b${Regex.escape(attribute)}\s*=\s*([\"'])${Regex.escape(value)}\1""",
            RegexOption.IGNORE_CASE
        )
        return any(meta::containsMatchIn)
    }

    private fun List<String>.containsCharsetMeta(): Boolean {
        val meta = Regex("""<\s*meta\b[^>]*\bcharset\s*=""", RegexOption.IGNORE_CASE)
        return any(meta::containsMatchIn)
    }

    private fun defaultAwareHeadElements(customElements: List<String>): List<String> = buildList {
        if (!customElements.containsCharsetMeta()) {
            add("<meta charset=\"UTF-8\">")
        }
        if (!customElements.containsMetaAttribute("name", "viewport")) {
            add("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
        }
        if (!customElements.containsTag("title")) {
            add("<title>Summon App</title>")
        }
        if (!customElements.containsMetaAttribute("name", "description")) {
            add("<meta name=\"description\" content=\"Summon Framework Application\">")
        }
        if (!customElements.containsMetaAttribute("name", "robots")) {
            add("<meta name=\"robots\" content=\"index, follow\">")
        }
        if (!customElements.containsMetaAttribute("property", "og:type")) {
            add("<meta property=\"og:type\" content=\"website\">")
        }
        if (
            !customElements.containsMetaAttribute("property", "og:title") &&
            !customElements.containsTag("title")
        ) {
            add("<meta property=\"og:title\" content=\"Summon App\">")
        }
        addAll(customElements)
    }

    private fun escapeHtmlAttribute(value: String): String = buildString(value.length) {
        value.forEach { character ->
            when (character) {
                '&' -> append("&amp;")
                '<' -> append("&lt;")
                '>' -> append("&gt;")
                '"' -> append("&quot;")
                '\'' -> append("&#39;")
                else -> append(character)
            }
        }
    }

    private fun injectHeadElements(document: String, elements: List<String>): String {
        if (elements.isEmpty()) return document
        val headEnd = document.indexOf("</head>")
        if (headEnd < 0) return document
        return document.substring(0, headEnd) + elements.joinToString("\n") + document.substring(headEnd)
    }

    private fun removeSupersededDefaultHeadElements(
        document: String,
        customElements: List<String>
    ): String {
        var updated = document
        if (customElements.containsCharsetMeta()) {
            updated = updated.replace("<meta charset=\"UTF-8\">", "")
        }
        if (customElements.containsMetaAttribute("name", "viewport")) {
            updated = updated.replace(
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">",
                ""
            )
        }
        if (customElements.containsTag("title")) {
            updated = updated.replace("<title>Summon App</title>", "")
        }
        if (customElements.containsMetaAttribute("name", "description")) {
            updated = updated.replace(
                "<meta name=\"description\" content=\"Summon Framework Application\">",
                ""
            )
        }
        if (customElements.containsMetaAttribute("name", "robots")) {
            updated = updated.replace("<meta name=\"robots\" content=\"index, follow\">", "")
        }
        if (customElements.containsMetaAttribute("property", "og:type")) {
            updated = updated.replace("<meta property=\"og:type\" content=\"website\">", "")
        }
        if (
            customElements.containsMetaAttribute("property", "og:title") ||
            customElements.containsTag("title")
        ) {
            updated = updated.replace("<meta property=\"og:title\" content=\"Summon App\">", "")
        }
        return updated
    }

    private fun stateSelector(definition: StateStyleDefinition): String =
        when (definition.state) {
            ConditionalStyleState.HOVER -> ":hover"
            ConditionalStyleState.FOCUS -> ":focus"
            ConditionalStyleState.FOCUS_VISIBLE -> ":focus-visible"
            ConditionalStyleState.ACTIVE -> ":active"
            ConditionalStyleState.FOCUS_WITHIN -> ":focus-within"
            ConditionalStyleState.FIRST_CHILD -> ":first-child"
            ConditionalStyleState.LAST_CHILD -> ":last-child"
            ConditionalStyleState.NTH_CHILD -> ":nth-child(${definition.argument})"
            ConditionalStyleState.ONLY_CHILD -> ":only-child"
            ConditionalStyleState.VISITED -> ":visited"
            ConditionalStyleState.DISABLED -> ":disabled"
            ConditionalStyleState.CHECKED -> ":checked"
        }

    private fun cssDeclarations(styles: Map<String, String>, indent: String): String =
        styles.entries.joinToString("\n") { (property, value) ->
            val importantValue = if (value.contains("!important", ignoreCase = true)) {
                value
            } else {
                "$value !important"
            }
            "$indent${cssPropertyName(property)}: $importantValue;"
        }

    private fun renderConditionalStyleRule(
        hostSelector: String,
        definition: ConditionalStyleDefinition
    ): String = when (definition) {
        is StateStyleDefinition -> buildString {
            append(hostSelector)
            append(stateSelector(definition))
            append(" {\n")
            append(cssDeclarations(definition.styles, "  "))
            append("\n}")
        }

        is MediaStyleDefinition -> buildString {
            append("@media ")
            append(definition.query)
            append(" {\n  ")
            append(hostSelector)
            append(" {\n")
            append(cssDeclarations(definition.styles, "    "))
            append("\n  }\n}")
        }

        is ScopedStyleDefinition -> buildString {
            append(hostSelector)
            append(definition.selectorType.combinator)
            append(definition.selector)
            append(" {\n")
            append(cssDeclarations(definition.styles, "  "))
            append("\n}")
        }
    }

    private fun registerConditionalStyles(
        hostId: String,
        definitions: List<ConditionalStyleDefinition>
    ) {
        val hostSelector = "[data-summon-style-id=\"$hostId\"]"
        definitions
            .filter { it.styles.isNotEmpty() }
            .mapTo(conditionalStyleRules) { renderConditionalStyleRule(hostSelector, it) }
    }

    private fun injectConditionalStyleSheet(document: String): String {
        if (conditionalStyleRules.isEmpty()) return document

        val headEnd = document.indexOf("</head>")
        if (headEnd < 0) return document

        val styleElement = buildString {
            append("<style")
            append(styleNonceAttribute())
            append(" data-summon-conditional-styles=\"true\">\n")
            append(conditionalStyleRules.joinToString("\n"))
            append("\n</style>")
        }
        return document.substring(0, headEnd) + styleElement + document.substring(headEnd)
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

            if (modifier.conditionalStyles.isNotEmpty()) {
                val styleHostId = "summon-style-${conditionalStyleHostCounter++}"
                this.attributes["data-summon-style-id"] = styleHostId
                registerConditionalStyles(styleHostId, modifier.conditionalStyles)
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
                    addHeadElement("<style${styleNonceAttribute()}>$css</style>")
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
        if (currentBuilder == null) {
            error("Rendering function called outside of renderComposableRoot scope")
        }
        return currentBuilder!!
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

    actual open fun renderRawHtml(html: TrustedHtml) {
        requireBuilder().consumer.onTagContentUnsafe {
            +html.value
        }
    }

    actual open fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        requireBuilder().button {
            applyModifier(modifier)
            // Ensure type="button" if not present to prevent form submission
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
            val customName = modifier.attributes["name"]
            if (customName != null) {
                // Use the provided name
                name = customName
                // If no id was provided, use the name as id
                if (modifier.attributes["id"] == null) {
                    id = customName
                }
            } else {
                // Fall back to generated UUID for both id and name
                id = "input-${UUID.randomUUID()}"
                name = id
            }

            attributes["data-onchange-action"] = "true"
            // onValueChange handler will be attached by client-side JS
        }
    }

    actual open fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<codes.yousef.summon.runtime.SelectOption<T>>,
        modifier: Modifier
    ) {
        requireBuilder().select {
            applyModifier(modifier)

            // Check if name attribute was provided in modifier
            val customName = modifier.attributes["name"]
            if (customName != null) {
                // Use the provided name
                name = customName
                // If no id was provided, use the name as id
                if (modifier.attributes["id"] == null) {
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
            attributes["data-onchange-action"] = "true"
            // onValueChange handler will be attached by client-side JS
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
            val customName = modifier.attributes["name"]
            if (customName != null) {
                // Use the provided name
                name = customName
                // If no id was provided, use the name as id
                if (modifier.attributes["id"] == null) {
                    id = customName
                }
            } else {
                // Fall back to generated UUID for both id and name
                id = "textarea-${UUID.randomUUID()}"
                name = id
            }

            attributes["data-onchange-action"] = "true"
            // onValueChange handler will be attached by client-side JS
            +value
        }
    }

    /** Adds trusted markup to the current document head. */
    actual open fun addHeadElement(content: String) {
        headElements.add(content)
    }

    /** Returns the head markup registered for the current render. */
    actual open fun getHeadElements(): List<String> {
        val pending = headElementSnapshot()
        return if (pending.isNotEmpty()) {
            pending
        } else {
            synchronized(lastRenderedHeadElements) { lastRenderedHeadElements.toList() }
        }
    }

    actual open fun renderHeadElements(builder: codes.yousef.summon.seo.HeadScope.() -> Unit) {
        val headScope = codes.yousef.summon.seo.DefaultHeadScope { element ->
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

    /** Renders composable content as a complete root. */
    actual open fun renderComposableRoot(composable: @Composable (() -> Unit)): String {
        beginConditionalStyleRender()
        val initialHeadElements = headElementSnapshot()
        CallbackRegistry.beginRender()
        val result = StringBuilder()
        try {
            result.appendHTML(prettyPrint = false).html {
                head {
                    defaultAwareHeadElements(initialHeadElements).forEach { element ->
                        unsafe { raw(element) }
                    }
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
            val completeHeadElements = headElementSnapshot()
            rememberRenderedHeadElements(completeHeadElements)
            val lateHeadElements = completeHeadElements.drop(initialHeadElements.size)
            val defaultAwareDocument = removeSupersededDefaultHeadElements(
                result.toString(),
                lateHeadElements
            )
            return injectConditionalStyleSheet(
                injectHeadElements(defaultAwareDocument, lateHeadElements)
            )
        } finally {
            if (currentBuilder != null) { // Double-check clearance on exception
                currentBuilder = null
            }
            CallbackRegistry.abandonRenderContext()
            endConditionalStyleRender()
            clearPendingHeadElements()
        }
    }


    /**
     * Renders a locked/private shell and returns the CSP header bound to its style nonce.
     */
    open fun renderPrivateShell(
        publicState: PublicHydrationState? = null,
        lang: String = "en",
        dir: String = "ltr",
        composable: @Composable () -> Unit
    ): CspDocument {
        check(activeStyleNonce == null) { "Nested private-shell rendering is not supported" }
        val nonce = generateCallbackCapability()
        activeStyleNonce = nonce
        return try {
            CspDocument(
                html = renderComposableRootWithHydration(publicState?.json, lang, dir, composable),
                contentSecurityPolicy = PrivateShellContentSecurityPolicy.headerValue(nonce)
            )
        } finally {
            activeStyleNonce = null
        }
    }

    /** Renders a root with client hydration metadata. */
    actual open fun renderComposableRootWithHydration(composable: @Composable () -> Unit): String {
        return renderComposableRootWithHydration(null, "en", "ltr", composable)
    }

    /**
     * Renders a composable to a full HTML document with hydration support and i18n attributes.
     *
     * @param lang The language code for the HTML document (e.g., "en", "ar", "he")
     * @param dir The text direction for the HTML document ("ltr" or "rtl")
     * @param composable The composable content to render
     * @return A complete HTML document string with hydration data
     */
    open fun renderComposableRootWithHydration(
        lang: String = "en",
        dir: String = "ltr",
        composable: @Composable () -> Unit
    ): String {
        return renderComposableRootWithHydration(null, lang, dir, composable)
    }

    /** Renders a root with client hydration metadata. */
    actual open fun renderComposableRootWithHydration(state: Any?, composable: @Composable () -> Unit): String {
        return renderComposableRootWithHydration(state, "en", "ltr", composable)
    }

    /**
     * Renders a composable to a full HTML document with hydration support, state, and i18n attributes.
     *
     * @param state Optional state to embed in the document for client-side hydration
     * @param lang The language code for the HTML document (e.g., "en", "ar", "he")
     * @param dir The text direction for the HTML document ("ltr" or "rtl")
     * @param composable The composable content to render
     * @return A complete HTML document string with hydration data
     */
    open fun renderComposableRootWithHydration(
        state: Any?,
        lang: String = "en",
        dir: String = "ltr",
        composable: @Composable () -> Unit
    ): String {
        beginConditionalStyleRender()
        val debugEnabled = System.getProperty("summon.debug.callbacks", "false").toBoolean()

        if (debugEnabled) {
            val contextKeyBefore = callbackContextKey()
            System.err.println("[Summon][SSR] Starting render with context key: $contextKeyBefore")
        }

        CallbackRegistry.beginRender()

        if (debugEnabled) {
            val contextKeyAfterBegin = callbackContextKey()
            System.err.println("[Summon][SSR] After beginRender, context key: $contextKeyAfterBegin")
        }

        return try {
            val bodyContent = renderComposableContent(composable)

            if (debugEnabled) {
                System.err.println("[Summon][SSR] Body content rendered (${bodyContent.length} chars)")
                val contextKeyBeforeCollect = callbackContextKey()
                System.err.println("[Summon][SSR] Before collecting callbacks, context key: $contextKeyBeforeCollect")
            }

            val callbackContext = CallbackRegistry.finishRenderAndCollectCallbacks()

            if (debugEnabled) {
                System.err.println("[Summon][SSR] Collected ${callbackContext.callbackIds.size} callback IDs")
            }

            val hydrationData = generateHydrationData(callbackContext)
            rememberRenderedHeadElements(headElementSnapshot())
            val fullDoc = injectConditionalStyleSheet(
                createHydratedDocument(bodyContent, hydrationData, state, lang, dir)
            )

            if (debugEnabled) {
                System.err.println("[Summon][SSR] Hydration document created")
            }

            fullDoc
        } finally {
            CallbackRegistry.abandonRenderContext()
            endConditionalStyleRender()
            clearPendingHeadElements()
        }
    }

    private fun renderComposableContent(composable: @Composable () -> Unit): String {
        val result = StringBuilder()
        try {
            val consumer = result.appendHTML(prettyPrint = false)
            val summonConsumer = SummonTagConsumer(consumer)
            summonConsumer.div {
                currentBuilder = this // Set context
                try {
                    renderContent(composable)
                } finally {
                    currentBuilder = null // Ensure context is cleared
                }
            }
        } finally {
            if (currentBuilder != null) { // Double-check clearance on exception
                currentBuilder = null
            }
        }
        return result.toString()
    }

    private fun generateHydrationData(callbackContext: CallbackRenderContext): String {
        return buildString {
            append("{")
            append("\"version\":1,")
            append("\"callbacks\":[")
            callbackContext.callbackIds.forEachIndexed { index, callback ->
                append('"').append(callback).append('"')
                if (index < callbackContext.callbackIds.size - 1) append(',')
            }
            append("],")
            append("\"callbackContext\":\"").append(callbackContext.capability).append("\",")
            append("\"timestamp\":").append(System.currentTimeMillis()).append(',')
            append("\"renderer\":\"jvm\",")
            append("\"hydrationMarkers\":true,")
            append("\"seoCompatible\":true")
            append("}")
        }
    }

    private fun createHydratedDocument(
        bodyContent: String,
        hydrationData: String,
        state: Any? = null,
        lang: String = "en",
        dir: String = "ltr"
    ): String {
        val stateScript = if (state is String) {
            val encoded = Base64.getEncoder().encodeToString(state.toByteArray())
            """<script id="summon-state" type="application/json+summon">$encoded</script>"""
        } else {
            ""
        }

        val documentHead = defaultAwareHeadElements(headElementSnapshot()).joinToString("\n    ")
        val documentLanguage = escapeHtmlAttribute(lang)
        val documentDirection = escapeHtmlAttribute(dir)
        val styleNonceMeta = activeStyleNonce?.let {
            """<meta name="summon-style-nonce" content="${escapeHtmlAttribute(it)}">"""
        }.orEmpty()

        return """<!DOCTYPE html>
<html lang="$documentLanguage" dir="$documentDirection">
<head>
    $documentHead
    $styleNonceMeta
    $stateScript
    <link rel="preload" href="/summon-hydration.js" as="script">
</head>
<body>
    <div id="${SummonConstants.DEFAULT_ROOT_ELEMENT_ID}" data-ssr="true" data-hydration-ready="false" data-summon-hydration="root">
        $bodyContent
    </div>
    <script type="application/json" id="summon-hydration-data">$hydrationData</script>
    <script src="/summon-bootloader.js" defer></script>
</body>
</html>
        """.trimIndent()
    }

    /** Hydrates an existing browser root. */
    actual open fun hydrateComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
        // Hydration on JVM/server-side doesn't make sense in the same way as client-side
        // This is typically a no-op for server-side rendering
        // In a full implementation, this might generate hydration instructions for the client
        // For now, we'll just log a warning
        System.err.println("Warning: hydrateComposableRoot called on JVM platform. This is typically a client-side operation.")
    }

    /** Renders modal. */
    actual open fun renderModal(
        onDismiss: () -> Unit,
        modifier: Modifier,
        variant: codes.yousef.summon.components.feedback.ModalVariant,
        size: codes.yousef.summon.components.feedback.ModalSize,
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
                    .attribute("data-summon-modal-overlay", "true")
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
                        .attribute("role", "dialog")
                        .attribute("aria-modal", "true")
                        .attribute("tabindex", "-1")
                        .attribute("data-summon-modal-dialog", "true")
                        .let { base ->
                            modifier.attributes["data-summon-modal-label"]?.let { base.attribute("aria-label", it) }
                                ?: base
                        }
                        .style("background-color", "#ffffff")
                        .style("border-radius", "8px")
                        .style("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.3)")
                        .style(
                            "max-width", when (size) {
                                codes.yousef.summon.components.feedback.ModalSize.SMALL -> "400px"
                                codes.yousef.summon.components.feedback.ModalSize.MEDIUM -> "600px"
                                codes.yousef.summon.components.feedback.ModalSize.LARGE -> "800px"
                                codes.yousef.summon.components.feedback.ModalSize.EXTRA_LARGE -> "1000px"
                            }
                        )
                        .style("max-height", "90vh")
                        .style("overflow", "auto")
                        .then(
                            when (variant) {
                                codes.yousef.summon.components.feedback.ModalVariant.ALERT ->
                                    Modifier().style("border", "2px solid #ff6b6b")

                                codes.yousef.summon.components.feedback.ModalVariant.CONFIRMATION ->
                                    Modifier().style("border", "2px solid #4ecdc4")

                                codes.yousef.summon.components.feedback.ModalVariant.FULLSCREEN ->
                                    Modifier().style("width", "100vw").style("height", "100vh")
                                        .style("border-radius", "0")

                                else -> Modifier()
                            }
                        )
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
                                        .attribute("type", "button")
                                        .attribute("aria-label", "Close dialog")
                                        .style("background-color", "transparent")
                                        .style("border", "none")
                                        .style("font-size", "24px")
                                        .style("cursor", "pointer")
                                        .style("min-width", "44px")
                                        .style("min-height", "44px")
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

    /** Renders row. */
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

    /** Renders column. */
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

    /** Renders box. */
    actual open fun renderBox(
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    /** Renders box container. */
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
        svgContent: TrustedSvg?,
        type: IconType
    ) {
        requireBuilder().span {
            applyModifier(modifier)
            if (onClick != null) {
                attributes["data-onclick-action"] = "true"
                val currentStyle = attributes["style"] ?: ""
                if ("cursor" !in currentStyle) attributes["style"] = "$currentStyle;cursor:pointer;".trimStart(';')
            }
            if (svgContent != null) {
                unsafe { raw(svgContent.value) }
            } else {
                // Handle Font Icons
                val classes = modifier.attributes["class"] ?: ""
                if (classes.contains("material-icons")) {
                    // Material Icons use ligatures - the name is the text content
                    +name
                } else {
                    // Other icon sets (like FontAwesome) typically use the name as a CSS class
                    // We render an inner <i> tag with the name as the class
                    i(classes = name)
                }
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

    /** Renders checkbox. */
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
            // onValueChange handler will be attached by client-side JS
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
            // onValueChange handlers will be attached by client-side JS to coordinate sliders
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
            // 12/24 hour display depends on browser/locale
            id = "time-${UUID.randomUUID()}"
            name = id
            attributes["data-onchange-action"] = "true"
            // onValueChange handler will be attached by client-side JS
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

        when (tagName.lowercase()) {
            // ============================================
            // Interactive Elements
            // ============================================
            "button" -> {
                builder.button {
                    applyModifier(modifier)
                    if (!attributes.containsKey("type")) {
                        attributes["type"] = "button"
                    }
                    renderContent(content)
                }
            }

            // ============================================
            // Structural/Sectioning Elements
            // ============================================
            "div" -> builder.div { applyModifier(modifier); renderContent(content) }
            "header" -> builder.header { applyModifier(modifier); renderContent(content) }
            "nav" -> builder.nav { applyModifier(modifier); renderContent(content) }
            "main" -> builder.main { applyModifier(modifier); renderContent(content) }
            "footer" -> builder.footer { applyModifier(modifier); renderContent(content) }
            "section" -> builder.section { applyModifier(modifier); renderContent(content) }
            "article" -> builder.article { applyModifier(modifier); renderContent(content) }
            "aside" -> builder.aside { applyModifier(modifier); renderContent(content) }
            "address" -> builder.address { applyModifier(modifier); renderContent(content) }
            "hgroup" -> builder.hGroup { applyModifier(modifier); renderContent(content) }
            "search" -> {
                // <search> is HTML5.2+, render with unsafe for broad support
                renderGenericHtmlTag(builder, "search", modifier, content)
            }

            // ============================================
            // Heading Elements
            // ============================================
            "h1" -> builder.h1 { applyModifier(modifier); renderContent(content) }
            "h2" -> builder.h2 { applyModifier(modifier); renderContent(content) }
            "h3" -> builder.h3 { applyModifier(modifier); renderContent(content) }
            "h4" -> builder.h4 { applyModifier(modifier); renderContent(content) }
            "h5" -> builder.h5 { applyModifier(modifier); renderContent(content) }
            "h6" -> builder.h6 { applyModifier(modifier); renderContent(content) }

            // ============================================
            // Text Content Elements
            // ============================================
            "p" -> builder.p { applyModifier(modifier); renderContent(content) }
            "blockquote" -> builder.blockQuote { applyModifier(modifier); renderContent(content) }
            "pre" -> builder.pre { applyModifier(modifier); renderContent(content) }
            "code" -> builder.code { applyModifier(modifier); renderContent(content) }
            "hr" -> builder.hr { applyModifier(modifier) }

            // ============================================
            // Inline Text Semantics
            // ============================================
            "span" -> builder.span { applyModifier(modifier); renderContent(content) }
            "a" -> builder.a { applyModifier(modifier); renderContent(content) }
            "strong" -> builder.strong { applyModifier(modifier); renderContent(content) }
            "em" -> builder.em { applyModifier(modifier); renderContent(content) }
            "small" -> builder.small { applyModifier(modifier); renderContent(content) }
            "mark" -> builder.mark { applyModifier(modifier); renderContent(content) }
            "del" -> builder.del { applyModifier(modifier); renderContent(content) }
            "ins" -> builder.ins { applyModifier(modifier); renderContent(content) }
            "sub" -> builder.sub { applyModifier(modifier); renderContent(content) }
            "sup" -> builder.sup { applyModifier(modifier); renderContent(content) }
            "s" -> builder.s { applyModifier(modifier); renderContent(content) }
            "u" -> builder.u { applyModifier(modifier); renderContent(content) }
            "b" -> builder.b { applyModifier(modifier); renderContent(content) }
            "i" -> builder.i { applyModifier(modifier); renderContent(content) }
            "abbr" -> builder.abbr { applyModifier(modifier); renderContent(content) }
            "cite" -> builder.cite { applyModifier(modifier); renderContent(content) }
            "q" -> builder.q { applyModifier(modifier); renderContent(content) }
            "kbd" -> builder.kbd { applyModifier(modifier); renderContent(content) }
            "samp" -> builder.samp { applyModifier(modifier); renderContent(content) }
            "var" -> builder.htmlVar { applyModifier(modifier); renderContent(content) }
            "dfn" -> builder.dfn { applyModifier(modifier); renderContent(content) }
            "time" -> builder.time { applyModifier(modifier); renderContent(content) }
            "bdi" -> builder.bdi { applyModifier(modifier); renderContent(content) }
            "bdo" -> builder.bdo { applyModifier(modifier); renderContent(content) }
            "br" -> builder.br { applyModifier(modifier) }

            // Elements that need generic rendering due to kotlinx.html constraints
            "data" -> renderGenericHtmlTag(builder, "data", modifier, content)
            "ruby" -> renderGenericHtmlTag(builder, "ruby", modifier, content)
            "rt" -> renderGenericHtmlTag(builder, "rt", modifier, content)
            "rp" -> renderGenericHtmlTag(builder, "rp", modifier, content)
            "wbr" -> renderGenericHtmlTag(builder, "wbr", modifier, content)

            // ============================================
            // List Elements
            // ============================================
            "ul" -> builder.ul { applyModifier(modifier); renderContent(content) }
            "ol" -> builder.ol { applyModifier(modifier); renderContent(content) }
            "dl" -> builder.dl { applyModifier(modifier); renderContent(content) }

            // List items need generic rendering as they require specific parent context in kotlinx.html
            "li" -> renderGenericHtmlTag(builder, "li", modifier, content)
            "dt" -> renderGenericHtmlTag(builder, "dt", modifier, content)
            "dd" -> renderGenericHtmlTag(builder, "dd", modifier, content)
            "menu" -> renderGenericHtmlTag(builder, "menu", modifier, content)

            // ============================================
            // Table Elements
            // ============================================
            "table" -> builder.table { applyModifier(modifier); renderContent(content) }

            // Table sections and cells need generic rendering due to parent context requirements
            "caption" -> renderGenericHtmlTag(builder, "caption", modifier, content)
            "colgroup" -> renderGenericHtmlTag(builder, "colgroup", modifier, content)
            "col" -> renderGenericHtmlTag(builder, "col", modifier, content)
            "thead" -> renderGenericHtmlTag(builder, "thead", modifier, content)
            "tbody" -> renderGenericHtmlTag(builder, "tbody", modifier, content)
            "tfoot" -> renderGenericHtmlTag(builder, "tfoot", modifier, content)
            "tr" -> renderGenericHtmlTag(builder, "tr", modifier, content)
            "th" -> renderGenericHtmlTag(builder, "th", modifier, content)
            "td" -> renderGenericHtmlTag(builder, "td", modifier, content)

            // ============================================
            // Media Elements
            // ============================================
            "figure" -> builder.figure { applyModifier(modifier); renderContent(content) }
            "figcaption" -> renderGenericHtmlTag(builder, "figcaption", modifier, content)
            "iframe" -> builder.iframe { applyModifier(modifier); renderContent(content) }
            "embed" -> builder.embed { applyModifier(modifier) }
            "object" -> builder.htmlObject { applyModifier(modifier); renderContent(content) }
            "param" -> renderGenericHtmlTag(builder, "param", modifier, content)
            "source" -> renderGenericHtmlTag(builder, "source", modifier, content)
            "track" -> renderGenericHtmlTag(builder, "track", modifier, content)
            "audio" -> builder.audio { applyModifier(modifier); renderContent(content) }
            "video" -> builder.video { applyModifier(modifier); renderContent(content) }
            "picture" -> builder.picture { applyModifier(modifier); renderContent(content) }
            "img" -> builder.img { applyModifier(modifier) }
            "map" -> builder.map { applyModifier(modifier); renderContent(content) }
            "area" -> renderGenericHtmlTag(builder, "area", modifier, content)
            "meter" -> renderGenericHtmlTag(builder, "meter", modifier, content)
            "progress" -> renderGenericHtmlTag(builder, "progress", modifier, content)

            // ============================================
            // Interactive Elements
            // ============================================
            "details" -> builder.details { applyModifier(modifier); renderContent(content) }
            "summary" -> builder.summary { applyModifier(modifier); renderContent(content) }
            "dialog" -> builder.dialog { applyModifier(modifier); renderContent(content) }

            // ============================================
            // Form Elements
            // ============================================
            "form" -> builder.form { applyModifier(modifier); renderContent(content) }
            "input" -> builder.input { applyModifier(modifier) }
            "textarea" -> builder.textArea { applyModifier(modifier); renderContent(content) }
            "select" -> builder.select { applyModifier(modifier); renderContent(content) }
            "option" -> renderGenericHtmlTag(builder, "option", modifier, content)
            "optgroup" -> renderGenericHtmlTag(builder, "optgroup", modifier, content)
            "label" -> builder.label { applyModifier(modifier); renderContent(content) }
            "fieldset" -> builder.fieldSet { applyModifier(modifier); renderContent(content) }
            "legend" -> renderGenericHtmlTag(builder, "legend", modifier, content)
            "datalist" -> builder.dataList { applyModifier(modifier); renderContent(content) }
            "output" -> builder.output { applyModifier(modifier); renderContent(content) }

            // ============================================
            // Fallback for any other tags
            // ============================================
            else -> renderGenericHtmlTag(builder, tagName, modifier, content)
        }
    }

    /**
     * Renders a generic HTML tag using unsafe raw HTML output.
     * This is used for tags that kotlinx.HTML doesn't support directly at flow content level
     * or that have parent context requirements (like li, td, etc).
     */
    private fun renderGenericHtmlTag(
        builder: FlowContent,
        tagName: String,
        modifier: Modifier,
        content: @Composable (FlowContentCompat.() -> Unit)
    ) {
        // Build opening tag with attributes
        val attrs = buildGenericAttributes(modifier)
        val openTag = "<$tagName$attrs>"
        val closeTag = "</$tagName>"

        // For void elements (self-closing), don't render content or close tag
        val voidElements = setOf(
            "area", "base", "br", "col", "embed", "hr", "img", "input",
            "link", "meta", "param", "source", "track", "wbr"
        )

        // Render content to a string first
        val contentHtml = if (tagName.lowercase() in voidElements) {
            ""
        } else {
            val contentBuilder = StringBuilder()
            // Use a sub-renderer to capture the content
            try {
                val savedBuilder = currentBuilder
                // Create a temporary HTML builder to capture content
                contentBuilder.appendHTML().div {
                    currentBuilder = this
                    val flowContentCompat = this.asFlowContentCompat()
                    flowContentCompat.content()
                }
                currentBuilder = savedBuilder
            } catch (e: Exception) {
                // If content rendering fails, just use empty content
            }
            // Extract inner content from the wrapper div
            val html = contentBuilder.toString()
            html.substringAfter("<div>", "").substringBeforeLast("</div>", html)
        }

        // Use a span to access unsafe{} and output the raw HTML
        builder.span {
            // Make the span invisible/inline
            attributes["style"] = "display: contents;"
            if (tagName.lowercase() in voidElements) {
                unsafe { raw("<$tagName$attrs>") }
            } else {
                unsafe { raw("$openTag$contentHtml$closeTag") }
            }
        }
    }

    /**
     * Builds HTML attributes string from a Modifier for use in generic tag rendering.
     */
    private fun buildGenericAttributes(modifier: Modifier): String {
        val attrs = mutableListOf<String>()

        // Add styles
        if (modifier.styles.isNotEmpty()) {
            val styleString = modifier.styles.entries.joinToString("; ") { (key, value) ->
                "${cssPropertyName(key)}: $value"
            }
            attrs.add("style=\"${escapeHtmlAttribute(styleString)}\"")
        }

        // Add regular attributes
        modifier.attributes.forEach { (name, value) ->
            attrs.add("$name=\"${escapeHtmlAttribute(value)}\"")
        }

        // Add hydration marker
        if (!modifier.attributes.containsKey("data-summon-id")) {
            val hydrationId = "summon-${UUID.randomUUID().toString().take(8)}"
            attrs.add("data-summon-id=\"$hydrationId\"")
        }

        return if (attrs.isNotEmpty()) " ${attrs.joinToString(" ")}" else ""
    }

    actual open fun renderCanvas(
        modifier: Modifier,
        width: Int?,
        height: Int?,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        requireBuilder().canvas {
            applyModifier(modifier)
            width?.let { attributes["width"] = it.toString() }
            height?.let { attributes["height"] = it.toString() }
            renderContent(content)
        }
    }

    actual open fun renderScriptTag(
        src: String,
        async: Boolean,
        defer: Boolean,
        type: String?,
        modifier: Modifier
    ) {
        requireBuilder().script(type = type, src = src) {
            applyModifier(modifier)
            if (async) {
                attributes["async"] = "async"
            }
            if (defer) {
                attributes["defer"] = "defer"
            }
        }
    }

    /** Renders span. */
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

    actual open fun renderLazyColumn(
        modifier: Modifier,
        onScroll: (scrollPosition: Float, containerSize: Float) -> Unit,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        renderLazyColumn(modifier, content)
    }

    actual open fun renderLazyColumn(
        modifier: Modifier,
        scrollPosition: Float,
        scrollRevision: Int,
        onViewportChanged: (scrollPosition: Float, containerSize: Float) -> Unit,
        onItemMeasured: (index: Int, size: Float) -> Unit,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        renderLazyColumn(modifier, content)
    }

    actual open fun renderLazyRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        // JVM equivalent: scrollable div
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().overflowX("auto")))
            renderContent(content)
        }
    }

    actual open fun renderLazyRow(
        modifier: Modifier,
        onScroll: (scrollPosition: Float, containerSize: Float) -> Unit,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        renderLazyRow(modifier, content)
    }

    actual open fun renderLazyRow(
        modifier: Modifier,
        scrollPosition: Float,
        scrollRevision: Int,
        onViewportChanged: (scrollPosition: Float, containerSize: Float) -> Unit,
        onItemMeasured: (index: Int, size: Float) -> Unit,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        renderLazyRow(modifier, content)
    }

    actual open fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        // Basic div, actual responsiveness will depend on CSS within modifier and content
        requireBuilder().div {
            applyModifier(modifier)
            comment(" ResponsiveLayout: Ensure CSS handles different screen sizes ")

            style {
                activeStyleNonce?.let { attributes["nonce"] = it }
                unsafe {
                    raw(
                        """
                        [data-screen-size="SMALL"] .small-content { display: block !important; }
                        [data-screen-size="MEDIUM"] .medium-content { display: block !important; }
                        [data-screen-size="LARGE"] .large-content { display: block !important; }
                        [data-screen-size="XLARGE"] .xlarge-content { display: block !important; }
                        """.trimIndent()
                    )
                }
            }
            renderContent(content)
        }
    }

    /** Renders snackbar. */
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

    /** Renders dropdown menu. */
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
                attributes["data-onclick-dismiss"] = "true"
                renderContent(content)
            }
        }
    }

    /** Renders tooltip. */
    actual open fun renderTooltip(text: String, modifier: Modifier, content: @Composable (() -> Unit)) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().style("position", "relative")))
            attributes["title"] = text // Basic browser tooltip
            comment(" Tooltip: For custom styling, JS/CSS solution is better ")
            renderContent(content)
        }
    }

    /** Renders modal. */
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

    /** Renders screen. */
    actual open fun renderScreen(modifier: Modifier, content: @Composable (FlowContentCompat.() -> Unit)) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().style("width", "100%").style("height", "100vh")))
            renderContent(content)
        }
    }

    actual open fun renderHtml(htmlContent: TrustedHtml, modifier: Modifier) {
        requireBuilder().div {
            applyModifier(modifier)
            unsafe { raw(htmlContent.value) }
        }
    }

    /** Renders surface. */
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

    /** Renders swipe to dismiss. */
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

    /** Renders vertical pager. */
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

    /** Renders horizontal pager. */
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

    /** Renders aspect ratio container. */
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

    /** Renders file picker. */
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
            attributes["data-onfilesselected-action"] = "true"
            if (actions != null) {
                comment(" FilePicker custom action content requires a browser trigger ")
            }
        }
    }

    /** Renders alert. */
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
            applyModifier(modifier)
            attributes["style"] = (attributes["style"] ?: "") +
                "padding: 15px; margin-bottom: 20px; border: 1px solid transparent; border-radius: 4px; background-color: $alertColor;"
            if (icon != null) {
                span { style = "margin-right: 10px;"; renderContent(icon) }
            }
            if (title != null) {
                strong {
                    style = "display: block; margin-bottom: 5px;"
                    +title
                }
            }
            +message
            if (actions != null) {
                div { style = "margin-top: 10px;"; renderContent(actions) }
            }
        }
    }

    /** Renders card. */
    actual open fun renderCard(modifier: Modifier, elevation: Int, content: @Composable (() -> Unit)) {
        // Re-use renderSurface for card appearance
        renderSurface(modifier.then(Modifier().style("border", "1px solid #ddd")), elevation, content)
    }

    /** Renders linear progress indicator. */
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

    /** Renders circular progress indicator. */
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

    /** Renders modal bottom sheet. */
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
                applyModifier(modifier)
                attributes["style"] = (attributes["style"] ?: "") +
                    "background-color: white; padding: 20px; border-top-left-radius: 8px; border-top-right-radius: 8px; width: 100%; max-width: 600px; box-shadow: 0 -2px 10px rgba(0,0,0,0.1);"
                renderContent(content)
            }
            comment(" ModalBottomSheet: JS for onDismissRequest and animations needed ")
        }
    }

    /** Renders alert dialog. */
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
                applyModifier(modifier)
                attributes["style"] = (attributes["style"] ?: "") +
                    "background-color: white; padding: 25px; border-radius: 8px; min-width: 280px; max-width: 560px; box-shadow: 0 4px 20px rgba(0,0,0,0.2);"
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

    /** Renders radio button. */
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
                    style = "margin-left: 8px;"
                    +label
                }
            }
        }
    }

    /** Renders checkbox. */
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
                    style = "margin-left: 8px;"
                    +label
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

    actual open fun renderNativeInput(
        type: String,
        modifier: Modifier,
        value: String?,
        isChecked: Boolean?
    ) {
        requireBuilder().input {
            applyModifier(modifier)
            attributes["type"] = type
            value?.let { this.value = it }
            if (isChecked != null) {
                this.checked = isChecked
            }
        }
    }

    actual open fun renderNativeTextarea(
        modifier: Modifier,
        value: String?
    ) {
        requireBuilder().textArea {
            applyModifier(modifier)
            value?.let { +it }
        }
    }

    actual open fun renderNativeSelect(
        modifier: Modifier,
        options: List<NativeSelectOption>
    ) {
        requireBuilder().select {
            applyModifier(modifier)
            options.forEach { optionConfig ->
                option {
                    attributes["value"] = optionConfig.value
                    if (optionConfig.isDisabled) {
                        disabled = true
                    }
                    if (optionConfig.isPlaceholder) {
                        attributes["hidden"] = "hidden"
                        attributes["disabled"] = "disabled"
                    }
                    if (optionConfig.isSelected) {
                        selected = true
                    }
                    +optionConfig.label
                }
            }
        }
    }

    actual open fun renderNativeButton(
        type: String,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
        requireBuilder().button {
            applyModifier(modifier)
            attributes["type"] = type
            renderContent(content)
        }
    }

    /** Renders radio button. */
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

    /** Renders card. */
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
        modifier: Modifier,
        fallbackText: String?
    ) {
        requireBuilder().a(href = href) {
            applyModifier(modifier)
            if (target != null) this.target = target
            if (title != null) this.title = title
            if (ariaLabel != null) attributes["aria-label"] = ariaLabel
            if (ariaDescribedBy != null) attributes["aria-describedby"] = ariaDescribedBy
            if (!fallbackText.isNullOrBlank()) {
                +fallbackText
            }
        }
    }

    actual open fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        requireBuilder().a(href = href) {
            applyModifier(modifier)
            if (target != null) this.target = target
            if (title != null) this.title = title
            if (ariaLabel != null) attributes["aria-label"] = ariaLabel
            if (ariaDescribedBy != null) attributes["aria-describedby"] = ariaDescribedBy
            renderContent(content)
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
                    +tab.title
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

    /** Renders block. */
    actual open fun renderBlock(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().style("display", "block")))
            renderContent(content)
        }
    }

    /** Renders inline. */
    actual open fun renderInline(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        requireBuilder().span {
            applyModifier(modifier.then(Modifier().style("display", "inline")))
            renderContent(content)
        }
    }

    /** Renders div. */
    actual open fun renderDiv(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
        requireBuilder().div {
            applyModifier(modifier)
            renderContent(content)
        }
    }

    actual open fun renderGlobalStyle(css: TrustedCss) {
        addHeadElement("<style${styleNonceAttribute()}>${css.value}</style>")
    }


    actual open fun renderLoading(
        modifier: Modifier,
        variant: codes.yousef.summon.components.feedback.LoadingVariant,
        size: codes.yousef.summon.components.feedback.LoadingSize,
        text: String?,
        textModifier: Modifier
    ) {
        val sizeValue = when (size) {
            codes.yousef.summon.components.feedback.LoadingSize.SMALL -> "16px"
            codes.yousef.summon.components.feedback.LoadingSize.MEDIUM -> "24px"
            codes.yousef.summon.components.feedback.LoadingSize.LARGE -> "32px"
            codes.yousef.summon.components.feedback.LoadingSize.EXTRA_LARGE -> "48px"
        }

        requireBuilder().div {
            applyModifier(
                modifier.style("display", "flex").style("flex-direction", "column").style("align-items", "center")
                    .style("gap", "8px")
            )

            when (variant) {
                codes.yousef.summon.components.feedback.LoadingVariant.SPINNER -> {
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

                codes.yousef.summon.components.feedback.LoadingVariant.DOTS -> {
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

                codes.yousef.summon.components.feedback.LoadingVariant.LINEAR -> {
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

                codes.yousef.summon.components.feedback.LoadingVariant.CIRCULAR -> {
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
                    applyModifier(
                        textModifier.style("font-size", "14px").style("color", "#666").style("text-align", "center")
                    )
                    +it
                }
            }
        }

        // Add CSS animations to head if not already added
        addHeadElement(
            """
            <style${styleNonceAttribute()}>
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
        """.trimIndent()
        )
    }

    actual open fun renderToast(
        toast: codes.yousef.summon.components.feedback.ToastData,
        onDismiss: () -> Unit,
        modifier: Modifier
    ) {
        val (bgColor, borderColor, textColor) = when (toast.variant) {
            codes.yousef.summon.components.feedback.ToastVariant.INFO -> Triple("#e3f2fd", "#2196f3", "#0d47a1")
            codes.yousef.summon.components.feedback.ToastVariant.SUCCESS -> Triple("#e8f5e8", "#4caf50", "#1b5e20")
            codes.yousef.summon.components.feedback.ToastVariant.WARNING -> Triple("#fff3e0", "#ff9800", "#6d3b00")
            codes.yousef.summon.components.feedback.ToastVariant.ERROR -> Triple("#ffebee", "#f44336", "#b71c1c")
        }

        requireBuilder().div {
            applyModifier(
                modifier
                    .attribute("role", if (toast.variant == codes.yousef.summon.components.feedback.ToastVariant.ERROR) "alert" else "status")
                    .attribute("aria-live", if (toast.variant == codes.yousef.summon.components.feedback.ToastVariant.ERROR) "assertive" else "polite")
                    .attribute("aria-atomic", "true")
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
                        attributes["type"] = "button"
                        attributes["aria-label"] = action.label
                        style = """
                            background: transparent;
                            border: 1px solid $borderColor;
                            color: $textColor;
                            padding: 4px 8px;
                            border-radius: 4px;
                            min-width: 44px;
                            min-height: 44px;
                            font-size: 12px;
                            cursor: pointer;
                            transition: background-color 0.2s;
                        """.trimIndent()
                        val callbackId = CallbackRegistry.registerCallback(action.onClick)
                        attributes["data-onclick-id"] = callbackId
                        attributes["data-onclick-action"] = "true"
                        +action.label
                    }
                }

                // Dismiss button if dismissible
                if (toast.dismissible) {
                    button {
                        attributes["type"] = "button"
                        attributes["aria-label"] = "Dismiss notification"
                        style = """
                            background: transparent;
                            border: none;
                            color: $textColor;
                            font-size: 16px;
                            cursor: pointer;
                            padding: 0;
                            min-width: 44px;
                            min-height: 44px;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            opacity: 0.7;
                            transition: opacity 0.2s;
                        """.trimIndent()
                        val callbackId = CallbackRegistry.registerCallback(onDismiss)
                        attributes["data-onclick-id"] = callbackId
                        attributes["data-onclick-action"] = "true"
                        +"×"
                    }
                }
            }
        }

        // Add toast animations CSS if not already added
        addHeadElement(
            """
            <style${styleNonceAttribute()}>
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
        """.trimIndent()
        )
    }

    actual open fun renderRichMarkdown(markdown: String, modifier: Modifier) {
        requireBuilder().div {
            applyModifier(modifier)
            +markdown
        }
    }

    actual open fun renderCodeEditor(
        value: String,
        onValueChange: (String) -> Unit,
        language: String,
        readOnly: Boolean,
        modifier: Modifier
    ) {
        requireBuilder().div {
            applyModifier(modifier)
            attributes["data-summon-component"] = "code-editor"
            attributes["data-language"] = language

            // SSR Fallback: Render as read-only code block
            pre {
                code {
                    classes = setOf("language-$language")
                    +value
                }
            }
        }
    }

    actual open fun renderChart(
        type: String,
        dataJson: String,
        optionsJson: String?,
        modifier: Modifier
    ) {
        requireBuilder().canvas {
            applyModifier(modifier)
            attributes["data-summon-component"] = "chart"
            attributes["data-chart-type"] = type
            attributes["data-chart-data"] = dataJson
            if (optionsJson != null) {
                attributes["data-chart-options"] = optionsJson
            }
            // Fallback content
            +"Chart: $type"
        }
    }

    actual open fun renderSplitPane(
        orientation: String,
        modifier: Modifier,
        first: @Composable () -> Unit,
        second: @Composable () -> Unit
    ) {
        requireBuilder().div {
            applyModifier(modifier.then(Modifier().style("display", "flex").style("flex-direction", if (orientation == "vertical") "column" else "row")))
            attributes["data-summon-component"] = "split-pane"
            attributes["data-orientation"] = orientation

            div {
                attributes["data-pane"] = "first"
                style = "flex: 1; overflow: auto;"
                renderContent(first)
            }

            div {
                attributes["data-pane"] = "divider"
                style = if (orientation == "vertical") "height: 5px; cursor: row-resize; background: #ccc;" else "width: 5px; cursor: col-resize; background: #ccc;"
            }

            div {
                attributes["data-pane"] = "second"
                style = "flex: 1; overflow: auto;"
                renderContent(second)
            }
        }
    }

    /**
     * Renders a menu bar as a horizontal navigation component.
     * On the server (JVM), this generates semantic HTML with dropdown menus.
     */
    actual open fun renderMenuBar(
        menus: List<codes.yousef.summon.desktop.menu.Menu>,
        modifier: Modifier
    ) {
        requireBuilder().nav {
            applyModifier(
                modifier.then(
                    Modifier()
                        .style("display", "flex")
                        .style("gap", "0")
                        .style("background-color", "#f8f9fa")
                        .style("padding", "0 8px")
                        .style("border-bottom", "1px solid #dee2e6")
                )
            )
            attributes["data-summon-component"] = "menu-bar"
            attributes["role"] = "menubar"

            menus.forEach { menu ->
                div {
                    style = "position: relative;"
                    classes = classes + "summon-menu"
                    attributes["role"] = "none"

                    // Menu button
                    button {
                        attributes["type"] = "button"
                        attributes["role"] = "menuitem"
                        attributes["aria-haspopup"] = "true"
                        attributes["aria-expanded"] = "false"
                        if (menu.disabled) {
                            attributes["disabled"] = "disabled"
                        }
                        style = "padding: 8px 12px; background: none; border: none; cursor: pointer; font-size: 14px;"
                        +menu.label
                    }

                    // Dropdown menu
                    ul {
                        classes = classes + "summon-menu-dropdown"
                        attributes["role"] = "menu"
                        style =
                            "display: none; position: absolute; top: 100%; left: 0; min-width: 160px; background: white; border: 1px solid #dee2e6; border-radius: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.15); padding: 4px 0; margin: 0; list-style: none; z-index: 1000;"

                        menu.items.forEach { item ->
                            renderMenuItem(this, item)
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper to render a single menu item recursively.
     */
    private fun renderMenuItem(ul: UL, item: codes.yousef.summon.desktop.menu.MenuItem) {
        ul.li {
            attributes["role"] = "none"

            if (item.isSeparator) {
                hr {
                    style = "margin: 4px 0; border: none; border-top: 1px solid #dee2e6;"
                }
            } else {
                if (item.submenu != null) {
                    // Submenu
                    div {
                        style = "position: relative;"

                        button {
                            attributes["type"] = "button"
                            attributes["role"] = "menuitem"
                            attributes["aria-haspopup"] = "true"
                            attributes["aria-expanded"] = "false"
                            if (item.disabled) {
                                attributes["disabled"] = "disabled"
                            }
                            style =
                                "display: flex; justify-content: space-between; width: 100%; padding: 8px 12px; background: none; border: none; cursor: pointer; text-align: left; font-size: 14px;"
                            span { +item.label }
                            span { +"▶" }
                        }

                        ul {
                            classes = classes + "summon-submenu"
                            attributes["role"] = "menu"
                            style =
                                "display: none; position: absolute; left: 100%; top: 0; min-width: 160px; background: white; border: 1px solid #dee2e6; border-radius: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.15); padding: 4px 0; margin: 0; list-style: none; z-index: 1001;"

                            item.submenu.forEach { subItem ->
                                renderMenuItem(this, subItem)
                            }
                        }
                    }
                } else {
                    // Regular item
                    button {
                        attributes["type"] = "button"
                        attributes["role"] = "menuitem"
                        if (item.disabled) {
                            attributes["disabled"] = "disabled"
                        }
                        if (item.checked != null) {
                            attributes["aria-checked"] = item.checked.toString()
                        }
                        style =
                            "display: flex; justify-content: space-between; width: 100%; padding: 8px 12px; background: none; border: none; cursor: pointer; text-align: left; font-size: 14px;"

                        span {
                            if (item.checked == true) {
                                +"✓ "
                            }
                            item.icon?.let {
                                span {
                                    style = "margin-right: 8px;"
                                    +it
                                }
                            }
                            +item.label
                        }

                        item.shortcut?.let { shortcut ->
                            span {
                                style = "margin-left: 24px; color: #6c757d; font-size: 12px;"
                                +shortcut.toDisplayString()
                            }
                        }
                    }
                }
            }
        }
    }
    private fun styleNonceAttribute(): String =
        activeStyleNonce?.let { """ nonce="${escapeHtmlAttribute(it)}"""" }.orEmpty()

}
