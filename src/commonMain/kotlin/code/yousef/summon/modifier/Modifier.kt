@file:OptIn(ExperimentalJsExport::class) // Required for using @JsExport

package code.yousef.summon.modifier

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

// Assuming BorderStyle and Cursor enums are defined elsewhere or are simple enough to infer
// If they are complex or specific to a platform, they might need their own @JsExport handling
// or be simplified/stringified for JS interop.

// For example, if BorderStyle is an enum:
// enum class BorderStyle { SOLID, DASHED, DOTTED /* ... */ }
// enum class Cursor { POINTER, DEFAULT, TEXT /* ... */ }


/**
 * A Modifier is used to add styling information and attributes to a composable.
 * It holds a map of CSS property names to their values and a map of HTML attributes.
 * This class and its core functionalities are exported for JavaScript usage.
 */
@JsExport // Export the Modifier class to JavaScript
data class Modifier(
    val styles: Map<String, String> = emptyMap(),
    @JsName("htmlAttributes") // Renamed for JS to avoid clash with attributes() function
    val attributes: Map<String, String> = emptyMap()
) {

    // Note: The companion object 'create' method won't be directly on the Modifier class in JS
    // like `Modifier.create()`. It's better to use top-level @JsExport-ed factory functions.
    // The primary constructor of the data class IS available in JS.
    // e.g. new package.path.Modifier({ "color": "red" }, { "id": "myElement" })

    /**
     * Generic style method for adding any CSS property.
     *
     * @param propertyName The CSS property name (e.g., "color", "font-size").
     * @param value The property value (e.g., "red", "12px").
     * @return A new Modifier with the added style.
     */
    fun style(propertyName: String, value: String): Modifier =
        copy(styles = this.styles + (propertyName to value))

    /**
     * Sets multiple style properties at once.
     *
     * @param properties A map where keys are CSS property names and values are their corresponding values.
     *                   In JavaScript, this can be a simple object like { "color": "blue", "margin": "10px" }.
     * @return A new Modifier with the added styles.
     */
    fun withStyles(properties: Map<String, String>): Modifier =
        copy(styles = this.styles + properties)

    /**
     * Sets the background color of the element.
     * @param color The CSS color value (e.g., "red", "#FF0000", "rgb(255,0,0)").
     * @return A new Modifier with the background-color style.
     */
    fun background(color: String): Modifier =
        style("background-color", color)

    /**
     * Alias for background() to match CSS property name.
     * Sets the background color of the element.
     * @param color The CSS color value.
     * @return A new Modifier with the background-color style.
     */
    fun backgroundColor(color: String): Modifier =
        background(color)

    /**
     * Sets the padding of the element using a single value for all sides.
     * @param value The CSS padding value (e.g., "10px", "1rem").
     * @return A new Modifier with the padding style.
     */
    @JsName("paddingUniform")
    fun padding(value: String): Modifier =
        style("padding", value)

    /**
     * Sets padding for each side individually.
     * @param top Padding for the top side.
     * @param right Padding for the right side.
     * @param bottom Padding for the bottom side.
     * @param left Padding for the left side.
     * @return A new Modifier with the specified padding styles.
     */
    @JsName("paddingSides")
    fun padding(top: String, right: String, bottom: String, left: String): Modifier =
        style("padding", "$top $right $bottom $left") // Or apply individually if preferred

    /**
     * Sets the width of the element.
     * @param value The CSS width value (e.g., "100px", "50%").
     * @return A new Modifier with the width style.
     */
    fun width(value: String): Modifier =
        style("width", value)

    /**
     * Sets the maximum width of the element.
     * @param value The CSS max-width value.
     * @return A new Modifier with the max-width style.
     */
    fun maxWidth(value: String): Modifier =
        style("max-width", value)

    /**
     * Sets the height of the element.
     * @param value The CSS height value (e.g., "100px", "50vh").
     * @return A new Modifier with the height style.
     */
    fun height(value: String): Modifier =
        style("height", value)

    /**
     * Sets both width and height of the element.
     * @param width The CSS width value.
     * @param height The CSS height value.
     * @return A new Modifier with both width and height styles.
     */
    @JsName("sizeDetailed")
    fun size(width: String, height: String): Modifier =
        this.width(width).height(height)

    /**
     * Sets equal width and height for the element.
     * @param value The CSS value for both width and height.
     * @return A new Modifier with equal width and height styles.
     */
    @JsName("sizeUniform")
    fun size(value: String): Modifier =
        size(value, value) // Calls the @JsName("sizeDetailed") in Kotlin

    /**
     * Adds a border to the element.
     * @param width The border width (e.g., "1px").
     * @param style The border style (e.g., "solid", "dashed").
     * @param color The border color (e.g., "black").
     * @return A new Modifier with the border style.
     */
    fun border(width: String, style: String, color: String): Modifier =
        this.style("border-width", width)
            .style("border-style", style)
            .style("border-color", color)

    /**
     * Adds a border to the element using a BorderStyle enum (if defined and convertible to string).
     * @param width The border width.
     * @param style The BorderStyle enum value.
     * @param color The border color.
     * @return A new Modifier with the border style.
     */
    // fun border(width: String, style: BorderStyle, color: String): Modifier =
    // border(width, style.name.lowercase(), color) // Example if BorderStyle is an enum

    /**
     * Sets the border radius for rounded corners.
     * @param value The CSS border-radius value (e.g., "5px", "50%").
     * @return A new Modifier with the border-radius style.
     */
    fun borderRadius(value: String): Modifier =
        style("border-radius", value)

    /**
     * Sets the text color.
     * @param value The CSS color value.
     * @return A new Modifier with the color style.
     */
    fun color(value: String): Modifier =
        style("color", value)

    /**
     * Sets the font size.
     * @param value The CSS font-size value (e.g., "16px", "1.2em").
     * @return A new Modifier with the font-size style.
     */
    fun fontSize(value: String): Modifier =
        style("font-size", value)

    /**
     * Sets the font weight.
     * @param value The CSS font-weight value (e.g., "bold", "normal", "700").
     * @param component (Optional) Placeholder for potential component context, currently unused.
     * @return A new Modifier with the font-weight style.
     */
    fun fontWeight(value: String, component: Any? = null): Modifier = // Added component parameter for consistency if other deprecated methods had it
        style("font-weight", value)

    /**
     * Sets margins around the element using a single value for all sides.
     * @param value The CSS margin value.
     * @return A new Modifier with the margin style.
     */
    @JsName("marginUniform")
    fun margin(value: String): Modifier =
        style("margin", value)

    /**
     * Sets margins for each side individually.
     * @param top Margin for the top side.
     * @param right Margin for the right side.
     * @param bottom Margin for the bottom side.
     * @param left Margin for the left side.
     * @return A new Modifier with the specified margin styles.
     */
    @JsName("marginSides")
    fun margin(top: String, right: String, bottom: String, left: String): Modifier =
        style("margin", "$top $right $bottom $left") // Or apply individually

    /**
     * Sets the object-fit property, typically for images or videos.
     * @param value The CSS object-fit value (e.g., "fill", "contain", "cover").
     * @param component (Optional) Placeholder for potential component context, currently unused.
     * @return A new Modifier with the object-fit style.
     */
    fun objectFit(value: String, component: Any? = null): Modifier = // Added component parameter
        style("object-fit", value)

    /**
     * Sets the element to fill its container's width and height.
     * Equivalent to width("100%") and height("100%").
     * @return A new Modifier making the element fill its allocated space.
     */
    fun fillMaxSize(): Modifier =
        this.fillMaxWidth().fillMaxHeight()

    /**
     * Sets the element to fill the width of its container.
     * Equivalent to width("100%").
     * @return A new Modifier with width set to 100%.
     */
    fun fillMaxWidth(): Modifier =
        style("width", "100%")

    /**
     * Sets the element to fill the height of its container.
     * Equivalent to height("100%").
     * @return A new Modifier with height set to 100%.
     */
    fun fillMaxHeight(): Modifier =
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
    fun shadow(offsetX: String, offsetY: String, blurRadius: String, color: String): Modifier =
        style("box-shadow", "$offsetX $offsetY $blurRadius $color")

    /**
     * Adds a simple shadow with common default values (e.g., a subtle gray shadow).
     * You might want to define specific defaults here.
     * @return A new Modifier with a default box-shadow style.
     */
    @JsName("shadowDefault")
    fun shadow(): Modifier =
        shadow("0px", "2px", "4px", "rgba(0,0,0,0.1)") // Calls the @JsName("shadowDetailed") in Kotlin

    /**
     * Sets the element's position to absolute and allows specifying offsets.
     * @param top (Optional) Top offset (e.g., "10px").
     * @param right (Optional) Right offset.
     * @param bottom (Optional) Bottom offset.
     * @param left (Optional) Left offset.
     * @return A new Modifier with absolute positioning and specified offsets.
     */
    fun absolutePosition(
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
    fun opacity(value: Float): Modifier =
        style("opacity", value.toString().takeIf { value >= 0.0f && value <= 1.0f } ?: "1.0")


    /**
     * Adds a hover effect by specifying styles to apply on hover.
     * Note: For pure JS/CSS, this typically requires generating CSS rules or using JS event listeners.
     * A simple Modifier might just store these intended hover styles, and the rendering system
     * would be responsible for applying them (e.g., by generating dynamic CSS classes).
     * This example just merges them into a special 'attributes' key for simplicity,
     * which isn't standard CSS practice for inline styles.
     * A more robust solution would involve CSS classes or a dedicated styling engine.
     *
     * @param hoverStyles A map of CSS property names to values to apply on hover.
     * @return A new Modifier that includes hover style information.
     */
    fun hover(hoverStyles: Map<String, String>): Modifier {
        // This is a simplified representation. Actual hover effects in web
        // are usually handled by CSS pseudo-classes (:hover) or JavaScript event listeners.
        // Storing it this way would require a custom rendering logic to interpret.
        // For demonstration, let's assume it adds a custom attribute.
        val currentHover = attributes["data-hover-styles"]?.let { prev -> // Uses the JS renamed `htmlAttributes` in Kotlin via `attributes`
            // Crude merge, could be improved
            prev.split(';').associate {
                val parts = it.split(':')
                parts[0].trim() to parts[1].trim()
            } + hoverStyles
        } ?: hoverStyles
        val hoverString = currentHover.entries.joinToString(";") { "${it.key}:${it.value}" }
        return attribute("data-hover-styles", hoverString)
    }


    /**
     * Sets an HTML attribute on the element.
     * @param name The attribute name (e.g., "id", "data-custom").
     * @param value The attribute value.
     * @return A new Modifier with the added attribute.
     */
    fun attribute(name: String, value: String): Modifier =
        copy(attributes = this.attributes + (name to value)) // Accesses the Kotlin property `attributes`

    /**
     * Sets multiple HTML attributes at once.
     * @param attrs A map where keys are attribute names and values are their corresponding values.
     *              In JavaScript, this can be a simple object.
     * @return A new Modifier with the added attributes.
     */
    @JsName("withAttributes") // Renamed for JS to avoid clash with the 'attributes' property
    fun attributes(attrs: Map<String, String>): Modifier =
        copy(attributes = this.attributes + attrs)  // Accesses the Kotlin property `attributes`

    /**
     * Combines this modifier with another modifier.
     * Styles and attributes from the `other` modifier will override those
     * with the same keys in `this` modifier.
     * @param other The Modifier to combine with.
     * @return A new Modifier that is the result of the combination.
     */
    fun then(other: Modifier): Modifier =
        Modifier(
            styles = this.styles + other.styles,
            attributes = this.attributes + other.attributes // Accesses the Kotlin property `attributes`
        )

    /**
     * Converts the styles map to a CSS inline style string.
     * Example: "color:red;font-size:12px"
     * @return A string representation of styles suitable for inline CSS.
     */
    fun toStyleString(): String =
        styles.entries.joinToString(separator = ";") { "${it.key}:${it.value}" }.ifEmpty { "" }


    /**
     * Converts the styles map to a CSS inline style string with camelCase property names
     * transformed to kebab-case (e.g., "fontSize" becomes "font-size").
     * This is primarily for applying to actual CSS style properties.
     * @return A string representation of styles suitable for inline CSS, with kebab-case keys.
     */
    fun toStyleStringKebabCase(): String {
        return styles.entries.joinToString(separator = ";") { (key, value) ->
            val kebabKey = key.replace(Regex("([a-z])([A-Z])")) {
                "${it.groupValues[1]}-${it.groupValues[2].lowercase()}"
            }.lowercase() // Ensure fully kebab, e.g. backgroundColor -> background-color
            "$kebabKey:$value"
        }.ifEmpty { "" }
    }

    // --- Common HTML Attributes as methods ---

    /**
     * Sets the `id` attribute of the element.
     * @param value The ID value.
     * @return A new Modifier with the id attribute.
     */
    fun id(value: String): Modifier =
        attribute("id", value)

    /**
     * Sets the `class` attribute of the element.
     * Note: This will overwrite any existing classes. Use `addClass` to append.
     * @param value The class name(s).
     * @return A new Modifier with the class attribute.
     */
    fun className(value: String): Modifier =
        attribute("class", value)

    /**
     * Adds a class to the element's class list.
     * If the element already has classes, this will append the new class, ensuring no duplicates.
     * @param value The class name to add.
     * @return A new Modifier with the updated class attribute.
     */
    fun addClass(value: String): Modifier {
        val existingClasses = attributes["class"]?.split(' ')?.toSet() ?: emptySet() // Accesses the Kotlin property `attributes`
        val newClasses = (existingClasses + value.trim()).filter { it.isNotEmpty() }.joinToString(" ")
        return attribute("class", newClasses)
    }

    /**
     * Sets a `data-*` attribute of the element.
     * @param name The name of the data attribute (without the "data-" prefix).
     * @param value The value of the data attribute.
     * @return A new Modifier with the data attribute.
     */
    fun dataAttribute(name: String, value: String): Modifier =
        attribute("data-$name", value)

    /**
     * Sets an `aria-*` attribute of the element for accessibility.
     * @param name The name of the ARIA attribute (without the "aria-" prefix).
     * @param value The value of the ARIA attribute.
     * @return A new Modifier with the ARIA attribute.
     */
    fun ariaAttribute(name: String, value: String): Modifier =
        attribute("aria-$name", value)

    /**
     * Sets the `role` attribute of the element for accessibility.
     * @param value The role value.
     * @return A new Modifier with the role attribute.
     */
    fun role(value: String): Modifier =
        attribute("role", value)

    /**
     * Sets the `tabindex` attribute of the element, affecting focusability.
     * @param value The tabindex value.
     * @return A new Modifier with the tabindex attribute.
     */
    fun tabIndex(value: Int): Modifier =
        attribute("tabindex", value.toString())

    /**
     * Sets the margin at the bottom of the element.
     * @param value The CSS margin-bottom value.
     * @return A new Modifier with the margin-bottom style.
     */
    fun marginBottom(value: String): Modifier =
        style("margin-bottom", value)

    /**
     * Sets the margin at the top of the element.
     * @param value The CSS margin-top value.
     * @return A new Modifier with the margin-top style.
     */
    fun marginTop(value: String): Modifier =
        style("margin-top", value)

    /**
     * Sets the margin at the left of the element.
     * @param value The CSS margin-left value.
     * @return A new Modifier with the margin-left style.
     */
    fun marginLeft(value: String): Modifier =
        style("margin-left", value)

    /**
     * Sets the margin at the right of the element.
     * @param value The CSS margin-right value.
     * @return A new Modifier with the margin-right style.
     */
    fun marginRight(value: String): Modifier =
        style("margin-right", value)

    /**
     * Sets the CSS `cursor` property.
     * @param value The CSS cursor value (e.g., "pointer", "default", "text").
     * @return A new Modifier with the cursor style.
     */
    fun cursor(value: String): Modifier = // Kept for direct string values
        style("cursor", value)

    /**
     * Sets the CSS `cursor` property using a Cursor enum (if defined and convertible to string).
     * @param value The Cursor enum value.
     * @return A new Modifier with the cursor style.
     */
    // fun cursor(value: Cursor): Modifier =
    //    style("cursor", value.name.lowercase()) // Example if Cursor is an enum

    /**
     * Sets the `z-index` of the element, affecting stacking order.
     * @param value The z-index value.
     * @return A new Modifier with the z-index style.
     */
    fun zIndex(value: Int): Modifier =
        style("z-index", value.toString())
}

/**
 * Factory function to create an empty Modifier.
 * Exported to JavaScript with the name "createModifier".
 * In JS: `yourModuleName.code.yousef.summon.modifier.createModifier()`
 */
@JsExport
@JsName("createModifier") // Explicit JS name
fun createEmptyModifier(): Modifier = Modifier()

/**
 * Factory function to create a Modifier with initial styles.
 * Exported to JavaScript with the name "createModifierWithStyles".
 * In JS: `yourModuleName.code.yousef.summon.modifier.createModifierWithStyles({ color: 'blue' })`
 * @param styles A map of style properties. Can be a JS object when called from JS.
 * @return A new Modifier with the specified styles.
 */
@JsExport
@JsName("createModifierWithStyles")
fun createModifierWithStyles(styles: Map<String, String>): Modifier {
    // In JS, to pass an empty map for attributes: new Modifier(styles, undefined) or new Modifier(styles, {})
    return Modifier(styles = styles)
}

/**
 * Factory function to create a Modifier with initial attributes.
 * Exported to JavaScript with the name "createModifierWithAttributes".
 * In JS: `yourModuleName.code.yousef.summon.modifier.createModifierWithAttributes({ id: 'myElement' })`
 * @param attributes A map of attributes. Can be a JS object when called from JS.
 * @return A new Modifier with the specified attributes.
 */
@JsExport
@JsName("createModifierWithAttributes")
fun createModifierWithAttributes(attributes: Map<String, String>): Modifier {
    // In JS, to pass an empty map for styles: new Modifier(undefined, attributes) or new Modifier({}, attributes)
    return Modifier(attributes = attributes)
}