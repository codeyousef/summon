/**
 * # Summon Modifier Package
 *
 * This package provides the type-safe styling system for Summon UI components.
 *
 * ## Overview
 *
 * The modifier package implements a Jetpack Compose-inspired styling system that enables:
 *
 * - **Type-Safe Styling**: Chainable modifier functions with compile-time safety
 * - **CSS Integration**: Direct mapping to CSS properties and HTML attributes
 * - **Cross-Platform Support**: Works consistently across Browser and JVM platforms
 * - **JavaScript Interop**: Full export support for JavaScript environments
 * - **Performance**: Efficient style application and minimal runtime overhead
 *
 * ## Key Components
 *
 * - [Modifier] - Core modifier class for styling and attributes
 * - **Styling Functions** - Type-safe functions for CSS properties
 * - **Layout Modifiers** - Padding, margin, sizing, and positioning
 * - **Accessibility Modifiers** - ARIA attributes and semantic markup
 * - **Interaction Modifiers** - Hover states, focus, and event handling
 *
 * ## Usage Patterns
 *
 * ```kotlin
 * // Basic styling
 * val buttonModifier = Modifier()
 *     .padding("16px")
 *     .backgroundColor("#007bff")
 *     .color("white")
 *     .borderRadius("8px")
 *
 * // Chaining and composition
 * val complexModifier = Modifier()
 *     .fillMaxWidth()
 *     .height("200px")
 *     .margin("16px")
 *     .shadow("0px", "2px", "4px", "rgba(0,0,0,0.1)")
 *     .hover(mapOfCompat("transform" to "translateY(-2px)"))
 *
 * // Accessibility
 * val accessibleModifier = Modifier()
 *     .role("button")
 *     .ariaAttribute("label", "Close dialog")
 *     .tabIndex(0)
 * ```
 *
 * @since 1.0.0
 */
package codes.yousef.summon.modifier

import codes.yousef.summon.core.splitCompat
import kotlin.js.JsName

/**
 * Type-safe styling system for Summon UI components.
 *
 * Modifier provides a declarative, chainable API for applying styles and attributes to UI components.
 * Inspired by Jetpack Compose, it enables type-safe styling with excellent IDE support and compile-time
 * checking while maintaining direct compatibility with CSS and HTML.
 *
 * ## Architecture
 *
 * The Modifier system is built around immutable data classes that chain together styling operations:
 * - Each modifier function returns a new instance with additional styling
 * - Styles are stored as CSS property-value pairs
 * - HTML attributes are maintained separately for semantic markup
 * - Cross-platform rendering converts modifiers to platform-specific output
 *
 * ## Core Features
 *
 * ### CSS Property Support
 * - **Layout**: [padding], [margin], [width], [height], [fillMaxWidth], [fillMaxHeight]
 * - **Appearance**: [backgroundColor], [color], [borderRadius], [shadow], [opacity]
 * - **Typography**: [fontSize], [fontWeight]
 * - **Positioning**: [absolutePosition], [zIndex]
 * - **Interaction**: [cursor], [hover] effects
 *
 * ### HTML Attributes
 * - **Identity**: [id], [className], [addClass]
 * - **Accessibility**: [role], [ariaAttribute], [tabIndex]
 * - **Data**: [dataAttribute] for custom data attributes
 *
 * ### JavaScript Interoperability
 * The entire Modifier API is exported for JavaScript use with appropriate name mappings:
 * - Constructor and methods are directly callable from JS
 * - Property names are mapped to avoid conflicts (e.g., `htmlAttributes`)
 * - Factory functions provide convenient creation patterns
 *
 * ## Usage Examples
 *
 * ### Basic Styling
 * ```kotlin
 * Button(
 *     onClick = { /* action */ },
 *     label = "Save",
 *     modifier = Modifier()
 *         .padding("12px 24px")
 *         .backgroundColor("#007bff")
 *         .color("white")
 *         .borderRadius("6px")
 * )
 * ```
 *
 * ### Layout and Sizing
 * ```kotlin
 * Column(
 *     modifier = Modifier()
 *         .fillMaxWidth()
 *         .height("400px")
 *         .margin("16px")
 *         .padding("20px")
 * ) {
 *     // Content
 * }
 * ```
 *
 * ### Accessibility
 * ```kotlin
 * Icon(
 *     name = "close",
 *     modifier = Modifier()
 *         .role("button")
 *         .ariaAttribute("label", "Close dialog")
 *         .tabIndex(0)
 *         .cursor("pointer")
 * )
 * ```
 *
 * ### Complex Compositions
 * ```kotlin
 * val cardModifier = Modifier()
 *     .fillMaxWidth()
 *     .backgroundColor("white")
 *     .borderRadius("12px")
 *     .shadow("0px", "4px", "6px", "rgba(0, 0, 0, 0.1)")
 *     .padding("24px")
 *     .hover(mapOfCompat(
 *         "transform" to "translateY(-2px)",
 *         "box-shadow" to "0px 8px 12px rgba(0, 0, 0, 0.15)"
 *     ))
 * ```
 *
 * ### JavaScript Usage
 * ```javascript
 * // Create modifier from JavaScript
 * const modifier = new summon.modifier.Modifier(
 *     { "color": "blue", "padding": "10px" },
 *     { "id": "my-element" }
 * );
 *
 * // Use factory functions
 * const styled = summon.modifier.createModifierWithStyles({
 *     "background-color": "red",
 *     "border-radius": "5px"
 * });
 * ```
 *
 * ## Platform Integration
 *
 * ### Browser/JavaScript Platform
 * - Styles are applied directly to DOM elements via the `style` attribute
 * - Attributes become HTML element attributes
 * - Hover effects generate CSS classes or use JavaScript event handlers
 *
 * ### JVM/Server Platform
 * - Styles are serialized to CSS inline styles in generated HTML
 * - Attributes are included in HTML element markup
 * - Server-side rendering maintains full styling information
 *
 * ## Performance Considerations
 *
 * - Modifiers are immutable, enabling safe sharing and caching
 * - Style maps use efficient Kotlin collections
 * - CSS generation is lazy and cached where possible
 * - Platform renderers optimize style application
 *
 * ## Extension and Customization
 *
 * Modifiers can be extended with domain-specific styling functions:
 *
 * ```kotlin
 * fun Modifier.brandPrimary() = this
 *     .backgroundColor("#007bff")
 *     .color("white")
 *     .fontWeight("600")
 *
 * fun Modifier.cardShadow() = this
 *     .shadow("0px", "2px", "8px", "rgba(0, 0, 0, 0.12)")
 * ```
 *
 * @property styles Map of CSS property names to values
 * @property attributes Map of HTML attribute names to values (renamed to `htmlAttributes` in JS)
 * @constructor Creates a new Modifier with the specified styles and attributes
 * @see codes.yousef.summon.runtime.PlatformRenderer
 * @see codes.yousef.summon.components
 * @since 1.0.0
 */
interface Modifier {
    val styles: Map<String, String> get() = emptyMap()
    val attributes: Map<String, String> get() = emptyMap()
    val eventHandlers: Map<String, () -> Unit> get() = emptyMap()
    val complexEventHandlers: Map<String, (Any) -> Unit> get() = emptyMap()
    val pseudoElements: List<PseudoElementDefinition> get() = emptyList()

    /**
     * Combines this modifier with another modifier.
     * Styles and attributes from the `other` modifier will override those
     * with the same keys in `this` modifier.
     * @param other The Modifier to combine with.
     * @return A new Modifier that is the result of the combination.
     */
    infix fun then(other: Modifier): Modifier

    companion object : Modifier {
        override infix fun then(other: Modifier): Modifier = other
        override fun toString() = "Modifier"
    }
}

/**
 * Internal implementation of Modifier that holds the actual state.
 */
data class ModifierImpl(
    override val styles: Map<String, String> = emptyMap(),
    override val attributes: Map<String, String> = emptyMap(),
    override val eventHandlers: Map<String, () -> Unit> = emptyMap(),
    override val complexEventHandlers: Map<String, (Any) -> Unit> = emptyMap(),
    override val pseudoElements: List<PseudoElementDefinition> = emptyList()
) : Modifier {
    override infix fun then(other: Modifier): Modifier {
        return if (other === Modifier) this
        else if (other !is ModifierImpl) this
        else {
            ModifierImpl(
                styles = this.styles + other.styles,
                attributes = this.attributes + other.attributes,
                eventHandlers = this.eventHandlers + other.eventHandlers,
                complexEventHandlers = this.complexEventHandlers + other.complexEventHandlers,
                pseudoElements = this.pseudoElements + other.pseudoElements
            )
        }
    }
}

/**
 * Factory function to create a new Modifier with optional initial values.
 *
 * @param styles Initial map of CSS styles
 * @param attributes Initial map of HTML attributes
 * @param eventHandlers Initial map of event handlers
 * @param pseudoElements Initial list of pseudo-element definitions
 * @return A new ModifierImpl instance
 */
@Suppress("FunctionName")
fun Modifier(
    styles: Map<String, String> = emptyMap(),
    attributes: Map<String, String> = emptyMap(),
    eventHandlers: Map<String, () -> Unit> = emptyMap(),
    pseudoElements: List<PseudoElementDefinition> = emptyList()
): Modifier = ModifierImpl(styles, attributes, eventHandlers, emptyMap(), pseudoElements)

/**
 * Generic style method for adding any CSS property.
 *
 * @param propertyName The CSS property name (e.g., "color", "font-size").
 * @param value The property value (e.g., "red", "12px").
 * @return A new Modifier with the added style.
 */
fun Modifier.style(propertyName: String, value: String): Modifier =
    when (this) {
        is ModifierImpl -> copy(styles = styles + (propertyName to value))
        else -> ModifierImpl(styles = mapOf(propertyName to value))
    }

/**
 * Sets multiple style properties at once.
 *
 * @param properties A map where keys are CSS property names and values are their corresponding values.
 * @return A new Modifier with the added styles.
 */
fun Modifier.withStyles(properties: Map<String, String>): Modifier =
    when (this) {
        is ModifierImpl -> copy(styles = styles + properties)
        else -> ModifierImpl(styles = properties)
    }

/**
 * Sets the background color of the element.
 * @param color The CSS color value (e.g., "red", "#FF0000", "rgb(255,0,0)").
 * @return A new Modifier with the background-color style.
 */
fun Modifier.background(color: String): Modifier =
    style("background-color", color)

/**
 * Alias for background() to match CSS property name.
 * Sets the background color of the element.
 * @param color The CSS color value.
 * @return A new Modifier with the background-color style.
 */
fun Modifier.backgroundColor(color: String): Modifier =
    background(color)

/**
 * Sets the padding of the element using a single value for all sides.
 * @param value The CSS padding value (e.g., "10px", "1rem").
 * @return A new Modifier with the padding style.
 */
@JsName("paddingUniform")
fun Modifier.padding(value: String): Modifier =
    style("padding", value)

/**
 * Adds a custom HTML attribute to the element.
 *
 * @param name The attribute name (e.g., "id", "role", "aria-label").
 * @param value The attribute value.
 * @return A new Modifier with the added attribute.
 */
fun Modifier.attribute(name: String, value: String): Modifier =
    when (this) {
        is ModifierImpl -> copy(attributes = attributes + (name to value))
        else -> ModifierImpl(attributes = mapOf(name to value))
    }

/**
 * Sets padding for each side individually.
 * @param top Padding for the top side.
 * @param right Padding for the right side.
 * @param bottom Padding for the bottom side.
 * @param left Padding for the left side.
 * @return A new Modifier with the specified padding styles.
 */
@JsName("paddingSides")
fun Modifier.padding(top: String, right: String, bottom: String, left: String): Modifier =
    style("padding", "$top $right $bottom $left")

/**
 * Sets the width of the element.
 * @param value The CSS width value (e.g., "100px", "50%").
 * @return A new Modifier with the width style.
 */
fun Modifier.width(value: String): Modifier =
    style("width", value)

/**
 * Sets the maximum width of the element.
 * @param value The CSS max-width value.
 * @return A new Modifier with the max-width style.
 */
fun Modifier.maxWidth(value: String): Modifier =
    style("max-width", value)

/**
 * Sets the height of the element.
 * @param value The CSS height value (e.g., "100px", "50vh").
 * @return A new Modifier with the height style.
 */
fun Modifier.height(value: String): Modifier =
    style("height", value)

/**
 * Sets both width and height of the element.
 * @param width The CSS width value.
 * @param height The CSS height value.
 * @return A new Modifier with both width and height styles.
 */
@JsName("sizeDetailed")
fun Modifier.size(width: String, height: String): Modifier =
    this.width(width).height(height)

/**
 * Sets equal width and height for the element.
 * @param value The CSS value for both width and height.
 * @return A new Modifier with equal width and height styles.
 */
@JsName("sizeUniform")
fun Modifier.size(value: String): Modifier =
    size(value, value)

/**
 * Adds a border to the element.
 * @param width The border width (e.g., "1px").
 * @param style The border style (e.g., "solid", "dashed").
 * @param color The border color (e.g., "black").
 * @return A new Modifier with the border style.
 */
fun Modifier.border(width: String, style: String, color: String): Modifier =
    this.style("border", "$width $style $color")

/**
 * Sets the border radius for rounded corners.
 * @param value The CSS border-radius value (e.g., "5px", "50%").
 * @return A new Modifier with the border-radius style.
 */
fun Modifier.borderRadius(value: String): Modifier =
    style("border-radius", value)

/**
 * Sets the text color.
 * @param value The CSS color value.
 * @return A new Modifier with the color style.
 */
fun Modifier.color(value: String): Modifier =
    style("color", value)

/**
 * Sets the font size.
 * @param value The CSS font-size value (e.g., "16px", "1.2em").
 * @return A new Modifier with the font-size style.
 */
fun Modifier.fontSize(value: String): Modifier =
    style("font-size", value)

/**
 * Sets the font weight.
 * @param value The CSS font-weight value (e.g., "bold", "normal", "700").
 * @param component (Optional) Placeholder for potential component context, currently unused.
 * @return A new Modifier with the font-weight style.
 */
fun Modifier.fontWeight(value: String, component: Any? = null): Modifier =
    style("font-weight", value)

/**
 * Sets margins around the element using a single value for all sides.
 * @param value The CSS margin value.
 * @return A new Modifier with the margin style.
 */
@JsName("marginUniform")
fun Modifier.margin(value: String): Modifier =
    style("margin", value)



/**
 * Sets the object-fit property, typically for images or videos.
 * @param value The CSS object-fit value (e.g., "fill", "contain", "cover").
 * @param component (Optional) Placeholder for potential component context, currently unused.
 * @return A new Modifier with the object-fit style.
 */
fun Modifier.objectFit(value: String, component: Any? = null): Modifier =
    style("object-fit", value)

/**
 * Sets the element to fill its container's width and height.
 * Equivalent to width("100%") and height("100%").
 * @return A new Modifier making the element fill its allocated space.
 */
fun Modifier.fillMaxSize(): Modifier =
    this.fillMaxWidth().fillMaxHeight()

/**
 * Sets the element to fill the width of its container.
 * Equivalent to width("100%").
 * @return A new Modifier with width set to 100%.
 */
fun Modifier.fillMaxWidth(): Modifier =
    style("width", "100%")

/**
 * Sets the element to fill the height of its container.
 * Equivalent to height("100%").
 * @return A new Modifier with height set to 100%.
 */
fun Modifier.fillMaxHeight(): Modifier =
    style("height", "100%")

/**
 * Sets box shadow for the element.
 * @param offsetX The horizontal offset of the shadow.
 * @param offsetY The vertical offset of the shadow.
 * @param blurRadius The blur radius of the shadow.
 * @param color The color of the shadow.
 * @return A new Modifier with the box-shadow style.
 */
@JsName("shadowDetailed")
fun Modifier.shadow(offsetX: String, offsetY: String, blurRadius: String, color: String): Modifier =
    style("box-shadow", "$offsetX $offsetY $blurRadius $color")

/**
 * Adds a simple shadow with common default values (e.g., a subtle gray shadow).
 * @return A new Modifier with a default box-shadow style.
 */
@JsName("shadowDefault")
fun Modifier.shadow(): Modifier =
    shadow("0px", "2px", "4px", "rgba(0,0,0,0.1)")

/**
 * Sets the element's position to absolute and allows specifying offsets.
 * @param top (Optional) Top offset (e.g., "10px").
 * @param right (Optional) Right offset.
 * @param bottom (Optional) Bottom offset.
 * @param left (Optional) Left offset.
 * @return A new Modifier with absolute positioning and specified offsets.
 */
fun Modifier.absolutePosition(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier {
    var mod: Modifier = style("position", "absolute")
    top?.let { mod = mod.style("top", it) }
    right?.let { mod = mod.style("right", it) }
    bottom?.let { mod = mod.style("bottom", it) }
    left?.let { mod = mod.style("left", it) }
    return mod
}

/**
 * Sets the element's opacity.
 * @param value Opacity value between 0.0 (fully transparent) and 1.0 (fully opaque).
 * @return A new Modifier with the opacity style.
 */
fun Modifier.opacity(value: Float): Modifier =
    style("opacity", value.toString().takeIf { value >= 0.0f && value <= 1.0f } ?: "1.0")

/**
 * Sets the element's opacity using a string value.
 * @param value Opacity value as a string (e.g. "0.5", "50%").
 * @return A new Modifier with the opacity style.
 */
fun Modifier.opacity(value: String): Modifier =
    style("opacity", value)


/**
 * Sets multiple HTML attributes at once.
 * @param attrs A map where keys are attribute names and values are their corresponding values.
 * @return A new Modifier with the added attributes.
 */
@JsName("withAttributes")
fun Modifier.attributes(attrs: Map<String, String>): Modifier =
    when (this) {
        is ModifierImpl -> copy(attributes = attributes + attrs)
        else -> ModifierImpl(attributes = attrs)
    }

/**
 * Adds a hover effect by specifying styles to apply on hover.
 * @param hoverStyles A map of CSS property names to values to apply on hover.
 * @return A new Modifier that includes hover style information.
 */
fun Modifier.hover(hoverStyles: Map<String, String>): Modifier {
    val currentAttributes = (this as? ModifierImpl)?.attributes ?: emptyMap()
    val currentHover = currentAttributes["data-hover-styles"]?.let { prev ->
        prev.splitCompat(';').associate {
            val parts = it.splitCompat(':')
            parts[0].trim() to parts[1].trim()
        } + hoverStyles
    } ?: hoverStyles
    val hoverString = currentHover.entries.joinToString(";") { "${it.key}:${it.value}" }
    return attribute("data-hover-styles", hoverString)
}

/**
 * Converts the styles map to a CSS inline style string.
 * Example: "color: red; font-size: 12px;"
 * @return A string representation of styles suitable for inline CSS.
 */
fun Modifier.toStyleString(): String {
    val styles = (this as? ModifierImpl)?.styles ?: return ""
    return if (styles.isEmpty()) "" else styles.entries.joinToString(
        separator = "; ",
        postfix = ";"
    ) { "${it.key}: ${it.value}" }
}

/**
 * Converts the styles map to a CSS inline style string with camelCase property names
 * transformed to kebab-case (e.g., "fontSize" becomes "font-size").
 * @return A string representation of styles suitable for inline CSS, with kebab-case keys.
 */
fun Modifier.toStyleStringKebabCase(): String {
    val styles = (this as? ModifierImpl)?.styles ?: return ""
    return styles.entries.joinToString(separator = ";") { (key, value) ->
        val kebabKey = key.replace(Regex("([a-z])([A-Z])")) {
            "${it.groupValues[1]}-${it.groupValues[2].lowercase()}"
        }.lowercase()
        "$kebabKey:$value"
    }.ifEmpty { "" }
}

/**
 * Checks if this modifier has a specific style with the given key and value.
 * @param key The style key to check
 * @param value The expected value for the style
 * @return true if the style exists with the given value, false otherwise
 */
fun Modifier.hasStyle(key: String, value: String): Boolean {
    val styles = (this as? ModifierImpl)?.styles ?: return false
    return styles[key] == value
}

// --- Common HTML Attributes as methods ---

/**
 * Sets the `id` attribute of the element.
 * @param value The ID value.
 * @return A new Modifier with the id attribute.
 */
fun Modifier.id(value: String): Modifier =
    attribute("id", value)

/**
 * Sets the `class` attribute of the element.
 * Note: This will overwrite any existing classes. Use `addClass` to append.
 * @param value The class name(s).
 * @return A new Modifier with the class attribute.
 */
fun Modifier.className(value: String): Modifier =
    attribute("class", value)

/**
 * Adds a class to the element's class list.
 * If the element already has classes, this will append the new class, ensuring no duplicates.
 * @param value The class name to add.
 * @return A new Modifier with the updated class attribute.
 */
fun Modifier.addClass(value: String): Modifier {
    val currentAttributes = (this as? ModifierImpl)?.attributes ?: emptyMap()
    val existingClasses = currentAttributes["class"]?.splitCompat(' ')?.toSet() ?: emptySet()
    val newClasses = (existingClasses + value.trim()).filter { it.isNotEmpty() }.joinToString(" ")
    return attribute("class", newClasses)
}

/**
 * Sets a `data-*` attribute of the element.
 * @param name The name of the data attribute (without the "data-" prefix).
 * @param value The value of the data attribute.
 * @return A new Modifier with the data attribute.
 */
fun Modifier.dataAttribute(name: String, value: String): Modifier =
    attribute("data-$name", value)

/**
 * Applies a map of data attributes in a single call.
 * Keys may be provided with or without the `data-` prefix.
 * Blank keys are ignored.
 * @param values Map of attribute names to values.
 */
fun Modifier.dataAttributes(values: Map<String, String>): Modifier {
    var updated = this
    values.forEach { (key, value) ->
        val trimmedKey = key.trim()
        if (trimmedKey.isEmpty()) return@forEach
        updated = if (trimmedKey.startsWith("data-")) {
            updated.attribute(trimmedKey, value)
        } else {
            updated.dataAttribute(trimmedKey, value)
        }
    }
    return updated
}

/**
 * Sets an `aria-*` attribute of the element for accessibility.
 * @param name The name of the ARIA attribute (without the "aria-" prefix).
 * @param value The value of the ARIA attribute.
 * @return A new Modifier with the ARIA attribute.
 */
fun Modifier.ariaAttribute(name: String, value: String): Modifier =
    attribute("aria-$name", value)

/**
 * Sets the `role` attribute of the element for accessibility.
 * @param value The role value.
 * @return A new Modifier with the role attribute.
 */
fun Modifier.role(value: String): Modifier =
    attribute("role", value)

/**
 * Sets the `tabindex` attribute of the element, affecting focusability.
 * @param value The tabindex value.
 * @return A new Modifier with the tabindex attribute.
 */
fun Modifier.tabIndex(value: Int): Modifier =
    attribute("tabindex", value.toString())

/**
 * Sets the margin at the bottom of the element.
 * @param value The CSS margin-bottom value.
 * @return A new Modifier with the margin-bottom style.
 */
fun Modifier.marginBottom(value: String): Modifier =
    style("margin-bottom", value)

/**
 * Sets the margin at the top of the element.
 * @param value The CSS margin-top value.
 * @return A new Modifier with the margin-top style.
 */
fun Modifier.marginTop(value: String): Modifier =
    style("margin-top", value)

/**
 * Sets the margin at the left of the element.
 * @param value The CSS margin-left value.
 * @return A new Modifier with the margin-left style.
 */
fun Modifier.marginLeft(value: String): Modifier =
    style("margin-left", value)

/**
 * Sets the margin at the right of the element.
 * @param value The CSS margin-right value.
 * @return A new Modifier with the margin-right style.
 */
fun Modifier.marginRight(value: String): Modifier =
    style("margin-right", value)

/**
 * Sets the CSS `cursor` property.
 * @param value The CSS cursor value (e.g., "pointer", "default", "text").
 * @return A new Modifier with the cursor style.
 */
fun Modifier.cursor(value: String): Modifier =
    style("cursor", value)



/**
 * Sets the `z-index` of the element, affecting stacking order.
 * @param value The z-index value.
 * @return A new Modifier with the z-index style.
 */
fun Modifier.zIndex(value: Int): Modifier =
    style("z-index", value.toString())

// ========================================
// Hydration Priority
// ========================================

/**
 * Sets the hydration priority for this element.
 * @param priority The hydration priority level
 * @return A new Modifier with the hydration-priority data attribute
 */
fun Modifier.hydrationPriority(priority: String): Modifier =
    dataAttribute("hydration-priority", priority)

/**
 * Marks this element for critical hydration (highest priority).
 * @return A new Modifier with critical hydration priority
 */
fun Modifier.hydrationCritical(): Modifier =
    hydrationPriority("critical")

/**
 * Marks this element for deferred hydration (lowest priority).
 * @return A new Modifier with deferred hydration priority
 */
fun Modifier.hydrationDeferred(): Modifier =
    hydrationPriority("deferred")

/**
 * Factory function to create a Modifier with initial styles.
 * @param styles A map of style properties.
 * @return A new Modifier with the specified styles.
 */
fun createModifierWithStyles(styles: Map<String, String>): Modifier {
    return ModifierImpl(styles = styles)
}

/**
 * Factory function to create a Modifier with initial attributes.
 * @param attributes A map of attributes.
 * @return A new Modifier with the specified attributes.
 */
fun createModifierWithAttributes(attributes: Map<String, String>): Modifier {
    return ModifierImpl(attributes = attributes)
}
