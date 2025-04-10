package code.yousef.summon.platform

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.input.TextFieldType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.core.AnimationController
import code.yousef.summon.core.AnimationStatus
import code.yousef.summon.core.style.Color
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.attribute
import code.yousef.summon.runtime.MigratedPlatformRenderer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.style

/**
 * JVM implementation of PlatformRenderer
 */
class JvmPlatformRenderer : MigratedPlatformRenderer {
    // Store head elements that should be added to the page
    private val headElements = mutableListOf<String>()
    
    // Callbacks for time picker changes
    private val timePickerCallbacks = mutableMapOf<String, (Int, Int, Int) -> Unit>()

    /**
     * Add an HTML element to the document head.
     * This is used by SEO components like MetaTags, CanonicalLinks, etc.
     *
     * @param content The HTML content to add to the head
     */
    override fun addHeadElement(content: String) {
        headElements.add(content)
    }

    /**
     * Get all head elements that have been added
     *
     * @return List of head element HTML strings
     */
    fun getHeadElements(): List<String> {
        return headElements.toList()
    }

    // Core PlatformRenderer methods
    override fun renderText(value: String, modifier: Modifier) {
        // Implementation
    }

    override fun renderText(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {
        // Implementation
    }

    override fun renderButton(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderRow(modifier: Modifier) {
        // Implementation
    }

    override fun renderRow(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderColumn(modifier: Modifier) {
        // Implementation
    }

    override fun renderColumn(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderSpacer(modifier: Modifier) {
        // Implementation
    }

    override fun renderDiv(modifier: Modifier) {
        // Implementation
    }

    override fun renderSpan(modifier: Modifier) {
        // Implementation
    }

    // We can't have two methods with the same signature, so we'll override only the one with content
    override fun renderBox(modifier: Modifier, content: @Composable () -> Unit) {
        // Render a basic box (div) with the styles from the modifier
        // Updated: This implementation needs to be replaced with a proper one using FlowContent
    }

    override fun renderBox(modifier: Modifier) {
        // Render a basic box (div) with the styles from the modifier
        // Updated: This implementation needs to be replaced with a proper one using FlowContent
    }

    // MigratedPlatformRenderer methods
    override fun <T> renderComposable(composable: @Composable () -> Unit, consumer: T) {
        // Implementation for rendering composables
    }

    override fun renderGrid(modifier: Modifier) {
        // Implementation
    }

    override fun renderGrid(modifier: Modifier, columns: Int, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderAspectRatio(modifier: Modifier) {
        // Implementation
    }

    override fun renderAspectRatio(modifier: Modifier, ratio: Float, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderResponsiveLayout(modifier: Modifier) {
        // Implementation
    }

    override fun renderResponsiveLayout(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderExpansionPanel(modifier: Modifier) {
        // Implementation
    }

    override fun renderExpansionPanel(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderLazyColumn(modifier: Modifier) {
        // Implementation
    }

    override fun renderLazyColumn(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderLazyRow(modifier: Modifier) {
        // Implementation
    }

    override fun renderLazyRow(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderDivider(modifier: Modifier) {
        // Implementation
    }

    override fun renderDivider(modifier: Modifier, color: Color, thickness: Float, vertical: Boolean) {
        // Implementation for migrated version
    }

    override fun renderCard(modifier: Modifier) {
        // Implementation
    }

    override fun renderCard(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderLink(href: String, modifier: Modifier) {
        // Implementation
    }

    override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    /**
     * Render a hyperlink with enhanced accessibility attributes.
     * This implementation supports additional attributes like target, title, and ARIA attributes.
     */
    override fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier
    ) {
        // Create a new modifier that includes all the accessibility attributes
        val finalModifier = modifier.let { mod ->
            var result = mod
            target?.let { result = result.attribute("target", it) }
            title?.let { result = result.attribute("title", it) }
            ariaLabel?.let { result = result.attribute("aria-label", it) }
            ariaDescribedBy?.let { result = result.attribute("aria-describedby", it) }
            result
        }

        // Use the existing renderLink method with the enhanced modifier
        renderLink(href, finalModifier)
    }

    override fun renderIcon(name: String, modifier: Modifier) {
        // Implementation
    }

    override fun renderIcon(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderImage(src: String, alt: String, modifier: Modifier) {
        // Implementation
    }

    override fun renderImage(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    ) {
        // Implementation
    }

    override fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderTabLayout(
        tabs: List<String>, selectedTab: String, onTabSelected: (String) -> Unit,
        modifier: Modifier, content: () -> Unit
    ) {
        // Implementation for migrated version
    }

    override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        // Implementation
    }

    override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for migrated version
    }

    override fun renderAnimatedContent(modifier: Modifier) {
        // Implementation for basic version
    }

    override fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit) {
        // Implementation for animated content
    }

    // Other implementation methods for MigratedPlatformRenderer
    override fun renderTextField(
        value: String, onValueChange: (String) -> Unit, modifier: Modifier,
        placeholder: String, isError: Boolean, type: String
    ) {
        // Implementation for migrated version
    }

    override fun renderTextArea(
        value: String, onValueChange: (String) -> Unit, modifier: Modifier,
        placeholder: String, maxLines: Int
    ) {
        // Implementation for migrated version
    }

    override fun renderCheckbox(
        checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier,
        enabled: Boolean
    ) {
        // Implementation for migrated version
    }

    override fun renderRadioButton(
        selected: Boolean, onClick: () -> Unit, modifier: Modifier,
        enabled: Boolean
    ) {
        // Implementation for migrated version
    }

    override fun renderSelect(
        value: String, onValueChange: (String) -> Unit, options: List<String>,
        modifier: Modifier, placeholder: String
    ) {
        // Implementation for migrated version
    }

    override fun renderSwitch(
        checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier,
        enabled: Boolean
    ) {
        // Implementation for migrated version
    }

    override fun renderDatePicker(date: LocalDate?, onDateChange: (LocalDate) -> Unit, modifier: Modifier) {
        // Implementation for migrated version
    }

    override fun renderTimePicker(
        time: code.yousef.summon.core.LocalTime?,
        onTimeChange: (code.yousef.summon.core.LocalTime) -> Unit,
        modifier: Modifier,
        is24Hour: Boolean
    ) {
        // Simplified implementation for time picker
        // The actual rendering will be handled by the platform-specific implementation
        // This is a placeholder implementation for the JVM platform
        
        // In a real implementation, we would render a time input element
        // and handle changes via JavaScript bridge
        
        // Register callback for time changes
        val id = "time-picker-${System.currentTimeMillis()}"
        timePickerCallbacks[id] = { h, m, s ->
            val newTime = code.yousef.summon.core.LocalTime(h, m, s)
            onTimeChange(newTime)
        }
    }

    override fun renderFileUpload(
        onFileSelected: (List<Any>) -> Unit, modifier: Modifier, multiple: Boolean,
        acceptedFileTypes: List<String>
    ) {
        // Implementation for migrated version
    }

    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>, onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        modifier: Modifier, valueRange: ClosedFloatingPointRange<Float>
    ) {
        // Implementation for migrated version
    }

    override fun renderAlertContainer(variant: AlertVariant?, modifier: Modifier) {
        // Implementation
    }

    override fun renderBadge(modifier: Modifier) {
        // Implementation
    }

    override fun renderTooltipContainer(modifier: Modifier) {
        // Implementation
    }

    override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
        // Implementation
    }

    override fun renderHtmlTag(tag: String, attrs: Map<String, String>, content: () -> Unit) {
        // Implementation for migrated version
    }

    // Additional PlatformRenderer methods
    override fun renderTextField(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        type: TextFieldType,
        placeholder: String?,
        modifier: Modifier
    ) {
        // Implementation
    }

    override fun renderTextArea(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        rows: Int?,
        maxLength: Int?,
        placeholder: String?,
        modifier: Modifier
    ) {
        // Implementation
    }

    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Implementation
    }

    override fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Implementation
    }

    override fun <T> renderSelect(
        value: T?,
        onValueChange: (T?) -> Unit,
        options: List<SelectOption<T>>,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Implementation
    }

    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Implementation
    }

    override fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        // Implementation
        return {}
    }

    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Implementation
    }

    override fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Implementation using a single-value slider directly
        // Uses the range-slider implementation under the hood for consistency
        val rangeValue = value..value
        val rangeChange: (ClosedFloatingPointRange<Float>) -> Unit = { newRange ->
            if (enabled) onValueChange(newRange.start)
        }

        renderRangeSlider(
            value = rangeValue,
            onValueChange = rangeChange,
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
            modifier = modifier
        )
    }

    override fun renderForm(onSubmit: () -> Unit, modifier: Modifier) {
        // Implementation
    }

    override fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Implementation
    }

    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        // Implementation
    }

    override fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable () -> Unit
    ) {
        // Not yet implemented for JVM
        throw NotImplementedError("FormField rendering is not yet implemented for JVM")
    }

    /**
     * Renders an icon with enhanced functionality
     */
    override fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: code.yousef.summon.components.display.IconType
    ) {
        throw NotImplementedError("Enhanced icon rendering is not yet implemented for JVM")
    }

    private fun applyModifier(tag: CommonAttributeGroupFacade, modifier: Modifier) {
        // Apply styles from the modifier directly to the HTML tag
        tag.style = modifier.toStyleString()

        // Apply any attributes from the modifier
        modifier.styles.entries.forEach { (key, value) ->
            if (key.startsWith("__attr:")) {
                val attributeName = key.substring("__attr:".length)
                tag.attributes[attributeName] = value
            }
        }
    }

    private fun Modifier.toStyleString(): String {
        return this.styles.entries
            .filter { !it.key.startsWith("__attr:") }
            .joinToString(";") { (key, value) -> "$key:$value" }
    }

    /**
     * Applies focus to an element with the given ID.
     * For JVM platform, we'll need to use a JavaScript snippet to apply focus.
     */
    override fun applyFocus(elementId: String): Boolean {
        // For JVM platform, we need to add a script to focus the element on the client
        val script = """
            document.getElementById('$elementId')?.focus();
        """.trimIndent()
        
        // Add the script to be executed when the page loads
        addHeadElement("<script>document.addEventListener('DOMContentLoaded', function() { $script });</script>")
        
        // We can't know if focus was successful on server-side, so return true
        return true
    }
    
    /**
     * Registers an animation with the platform renderer.
     */
    override fun registerAnimation(
        animationId: String,
        animationProps: Map<String, Any>,
        targetElementId: String?
    ) {
        // Create the CSS animation definition
        val keyframesName = "animation_$animationId"
        val duration = animationProps["duration"]?.toString() ?: "1s"
        val easing = animationProps["easing"]?.toString() ?: "ease"
        val delay = animationProps["delay"]?.toString() ?: "0s"
        
        // Process keyframes if they're provided
        val keyframes = animationProps["keyframes"]
        var keyframesCSS = ""
        
        if (keyframes != null) {
            keyframesCSS = "@keyframes $keyframesName {\n"
            
            when (keyframes) {
                is Map<*, *> -> {
                    // Process keyframes map
                    keyframes.forEach { (key, value) ->
                        keyframesCSS += "    $key {\n"
                        if (value is Map<*, *>) {
                            value.forEach { (prop, propValue) ->
                                keyframesCSS += "        $prop: $propValue;\n"
                            }
                        }
                        keyframesCSS += "    }\n"
                    }
                }
                is String -> {
                    // Direct string keyframes definition
                    keyframesCSS += keyframes
                }
            }
            
            keyframesCSS += "}\n"
        } else {
            // Default keyframes if none provided
            keyframesCSS = """
                @keyframes $keyframesName {
                    0% { transform: scale(0.95); opacity: 0.7; }
                    50% { transform: scale(1.05); opacity: 0.9; }
                    100% { transform: scale(1.0); opacity: 1.0; }
                }
            """.trimIndent()
        }
        
        // Create the animation class
        val animationCSS = """
            $keyframesCSS
            .$keyframesName {
                animation-name: $keyframesName;
                animation-duration: $duration;
                animation-timing-function: $easing;
                animation-delay: $delay;
                animation-fill-mode: both;
            }
        """.trimIndent()
        
        // Add the CSS to the head
        addHeadElement("<style>$animationCSS</style>")
    }
    
    /**
     * Starts a previously registered animation.
     */
    override fun startAnimation(
        animationId: String,
        options: Map<String, Any>
    ): AnimationController {
        // For JVM platform, we'll add JS to start the animation
        val targetId = options["targetElementId"]?.toString() ?: ""
        val animationClass = "animation_$animationId"
        val controllerId = "animation_controller_${System.currentTimeMillis()}"
        
        // Create JavaScript to start the animation and provide control functions
        val script = """
            (function() {
                // Create a controller object in the window namespace
                window.${controllerId} = {
                    status: 'RUNNING',
                    progress: 0,
                    element: null,
                    interval: null,
                    
                    // Initialize the animation
                    init: function() {
                        const element = document.getElementById('$targetId');
                        if (!element) return;
                        
                        this.element = element;
                        element.classList.add('$animationClass');
                        
                        // Set up event listeners
                        element.addEventListener('animationstart', () => {
                            this.status = 'RUNNING';
                            this.progress = 0;
                        });
                        
                        element.addEventListener('animationend', () => {
                            this.status = 'COMPLETED';
                            this.progress = 1;
                        });
                        
                        element.addEventListener('animationcancel', () => {
                            this.status = 'CANCELLED';
                        });
                        
                        // Update progress periodically
                        this.interval = setInterval(() => {
                            if (this.status === 'RUNNING') {
                                this.progress = Math.min(this.progress + 0.01, 0.99);
                            }
                        }, 100);
                    },
                    
                    // Control methods
                    pause: function() {
                        if (this.status === 'RUNNING' && this.element) {
                            this.element.style.animationPlayState = 'paused';
                            this.status = 'PAUSED';
                        }
                    },
                    
                    resume: function() {
                        if (this.status === 'PAUSED' && this.element) {
                            this.element.style.animationPlayState = 'running';
                            this.status = 'RUNNING';
                        }
                    },
                    
                    cancel: function() {
                        if (this.element) {
                            this.element.classList.remove('$animationClass');
                            this.status = 'CANCELLED';
                        }
                    },
                    
                    stop: function() {
                        if (this.element) {
                            this.element.style.animationPlayState = 'paused';
                            this.element.style.animationFillMode = 'forwards';
                            this.status = 'COMPLETED';
                        }
                    },
                    
                    // Clean up resources
                    cleanup: function() {
                        if (this.interval) {
                            clearInterval(this.interval);
                        }
                    }
                };
                
                // Initialize on DOM load
                if (document.readyState !== 'loading') {
                    window.${controllerId}.init();
                } else {
                    document.addEventListener('DOMContentLoaded', function() {
                        window.${controllerId}.init();
                    });
                }
            })();
        """.trimIndent()
        
        // Add the script to the head
        addHeadElement("<script>$script</script>")
        
        // Return a controller that maps client-side controls to server-side functions
        return object : AnimationController {
            override fun pause() {
                // Add JavaScript to call the pause function
                addHeadElement("""
                    <script>
                        document.addEventListener('DOMContentLoaded', function() {
                            if (window.${controllerId}) window.${controllerId}.pause();
                        });
                    </script>
                """.trimIndent())
            }
            
            override fun resume() {
                // Add JavaScript to call the resume function
                addHeadElement("""
                    <script>
                        document.addEventListener('DOMContentLoaded', function() {
                            if (window.${controllerId}) window.${controllerId}.resume();
                        });
                    </script>
                """.trimIndent())
            }
            
            override fun cancel() {
                // Add JavaScript to call the cancel function
                addHeadElement("""
                    <script>
                        document.addEventListener('DOMContentLoaded', function() {
                            if (window.${controllerId}) window.${controllerId}.cancel();
                        });
                    </script>
                """.trimIndent())
            }
            
            override fun stop() {
                // Add JavaScript to call the stop function
                addHeadElement("""
                    <script>
                        document.addEventListener('DOMContentLoaded', function() {
                            if (window.${controllerId}) window.${controllerId}.stop();
                        });
                    </script>
                """.trimIndent())
            }
            
            // Status and progress can't be read from server side,
            // so we return default values
            override val status: AnimationStatus = AnimationStatus.RUNNING
            override val progress: Float = 0f
        }
    }
} 