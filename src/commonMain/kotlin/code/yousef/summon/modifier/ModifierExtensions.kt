package code.yousef.summon.modifier

import code.yousef.summon.components.ScrollableComponent
import code.yousef.summon.components.InputComponent
import code.yousef.summon.components.TextComponent
import code.yousef.summon.components.LayoutComponent
import code.yousef.summon.components.ClickableComponent
import code.yousef.summon.components.FocusableComponent
import code.yousef.summon.components.MediaComponent

/**
 * Extension functions for Modifier.
 * 
 * @deprecated These extensions are being moved to dedicated modifier packages for better organization.
 * For auto margin modifiers, please import from code.yousef.summon.modifier.AutoMarginModifiers directly.
 */

object ModifierExtensions {
    
    /**
     * Layout related modifiers
     */
    object Layout {
        /**
         * Sets the element to fill the width of its container.
         */
        fun Modifier.fillMaxWidth(): Modifier = style("width", "100%")
        
        /**
         * Sets the element to fill the height of its container.
         */
        fun Modifier.fillMaxHeight(): Modifier = style("height", "100%")
        
        /**
         * Sets the element to fill its container.
         */
        fun Modifier.fillMaxSize(): Modifier = fillMaxWidth().fillMaxHeight()
        
        /**
         * Sets the width of the element.
         */
        fun Modifier.width(value: String): Modifier = style("width", value)
        
        /**
         * Sets the height of the element.
         */
        fun Modifier.height(value: String): Modifier = style("height", value)
        
        /**
         * Sets the maximum width of the element.
         */
        fun Modifier.maxWidth(value: String): Modifier = style("max-width", value)
        
        /**
         * Sets the maximum height of the element.
         */
        fun Modifier.maxHeight(value: String): Modifier = style("max-height", value)
        
        /**
         * Sets the minimum width of the element.
         */
        fun Modifier.minWidth(value: String): Modifier = style("min-width", value)
        
        /**
         * Sets the minimum height of the element.
         */
        fun Modifier.minHeight(value: String): Modifier = style("min-height", value)
        
        /**
         * Sets both width and height at once.
         */
        fun Modifier.size(width: String, height: String): Modifier =
            this.width(width).height(height)
        
        /**
         * Sets equal width and height.
         */
        fun Modifier.size(value: String): Modifier = size(value, value)
        
        /**
         * Sets padding for the element.
         */
        fun Modifier.padding(value: String): Modifier = style("padding", value)
        
        /**
         * Sets padding for each side individually.
         */
        fun Modifier.padding(top: String, right: String, bottom: String, left: String): Modifier =
            style("padding", "$top $right $bottom $left")
        
        /**
         * Sets margin for the element.
         */
        fun Modifier.margin(value: String): Modifier = style("margin", value)
        
        /**
         * Sets margin for each side individually.
         */
        fun Modifier.margin(top: String, right: String, bottom: String, left: String): Modifier =
            style("margin", "$top $right $bottom $left")
            
        /**
         * Sets the margin at the top of the element.
         */
        fun Modifier.marginTop(value: String): Modifier = style("margin-top", value)
        
        /**
         * Sets the margin at the right of the element.
         */
        fun Modifier.marginRight(value: String): Modifier = style("margin-right", value)
        
        /**
         * Sets the margin at the bottom of the element.
         */
        fun Modifier.marginBottom(value: String): Modifier = style("margin-bottom", value)
        
        /**
         * Sets the margin at the left of the element.
         */
        fun Modifier.marginLeft(value: String): Modifier = style("margin-left", value)
        
        /**
         * Sets the element's position to absolute.
         */
        fun Modifier.absolutePosition(
            top: String? = null,
            right: String? = null,
            bottom: String? = null,
            left: String? = null
        ): Modifier {
            var result = style("position", "absolute")
            if (top != null) result = result.style("top", top)
            if (right != null) result = result.style("right", right)
            if (bottom != null) result = result.style("bottom", bottom)
            if (left != null) result = result.style("left", left)
            return result
        }
        
        /**
         * Sets the element's position.
         */
        fun Modifier.position(value: String): Modifier = style("position", value)
        
        /**
         * Sets the display property of the element.
         */
        fun Modifier.display(value: String): Modifier = style("display", value)
        
        /**
         * Sets the element to be a flex container.
         */
        fun Modifier.flex(): Modifier = display("flex")
        
        /**
         * Sets the flex-direction property.
         */
        fun Modifier.flexDirection(value: String): Modifier = style("flex-direction", value)
        
        /**
         * Sets the flex-wrap property.
         */
        fun Modifier.flexWrap(value: String): Modifier = style("flex-wrap", value)
        
        /**
         * Sets the justify-content property.
         */
        fun Modifier.justifyContent(value: String): Modifier = style("justify-content", value)
        
        /**
         * Sets the align-items property.
         */
        fun Modifier.alignItems(value: String): Modifier = style("align-items", value)
        
        /**
         * Sets the flex-grow property.
         */
        fun Modifier.flexGrow(value: Int): Modifier = style("flex-grow", value.toString())
        
        /**
         * Sets the flex-shrink property.
         */
        fun Modifier.flexShrink(value: Int): Modifier = style("flex-shrink", value.toString())
        
        /**
         * Sets the flex-basis property.
         */
        fun Modifier.flexBasis(value: String): Modifier = style("flex-basis", value)
        
        /**
         * Sets the grid-template-columns property.
         */
        fun Modifier.gridTemplateColumns(value: String): Modifier = style("grid-template-columns", value)
        
        /**
         * Sets the grid-template-rows property.
         */
        fun Modifier.gridTemplateRows(value: String): Modifier = style("grid-template-rows", value)
        
        /**
         * Sets the grid-gap property.
         */
        fun Modifier.gridGap(value: String): Modifier = style("grid-gap", value)
    }
    
    /**
     * Typography related modifiers
     */
    object Typography {
        /**
         * Sets the font weight.
         */
        fun Modifier.fontWeight(value: String): Modifier = style("font-weight", value)
        
        /**
         * Sets the font size.
         */
        fun Modifier.fontSize(value: String): Modifier = style("font-size", value)
        
        /**
         * Sets the text color.
         */
        fun Modifier.color(value: String): Modifier = style("color", value)
        
        /**
         * Sets the text decoration.
         */
        fun Modifier.textDecoration(value: String): Modifier = style("text-decoration", value)
        
        /**
         * Sets the text alignment.
         */
        fun Modifier.textAlign(value: String): Modifier = style("text-align", value)
        
        /**
         * Sets the line height.
         */
        fun Modifier.lineHeight(value: String): Modifier = style("line-height", value)
        
        /**
         * Sets the font family.
         */
        fun Modifier.fontFamily(value: String): Modifier = style("font-family", value)
        
        /**
         * Sets the font style.
         */
        fun Modifier.fontStyle(value: String): Modifier = style("font-style", value)
        
        /**
         * Sets the text transform.
         */
        fun Modifier.textTransform(value: String): Modifier = style("text-transform", value)
        
        /**
         * Sets text truncation with ellipsis for overflow.
         */
        fun Modifier.ellipsis(): Modifier = styles(
            mapOf(
                "white-space" to "nowrap",
                "overflow" to "hidden",
                "text-overflow" to "ellipsis"
            )
        )
        
        /**
         * Controls text wrapping behavior.
         */
        fun Modifier.whiteSpace(value: String): Modifier = style("white-space", value)
    }
    
    /**
     * Appearance related modifiers
     */
    object Appearance {
        /**
         * Sets the background color.
         */
        fun Modifier.backgroundColor(color: String): Modifier = style("background-color", color)
        
        /**
         * Sets the background color (alias for backgroundColor).
         */
        fun Modifier.background(color: String): Modifier = backgroundColor(color)
        
        /**
         * Sets the opacity.
         */
        fun Modifier.opacity(value: Float): Modifier = style("opacity", value.toString())
        
        /**
         * Sets the opacity using a string value.
         */
        fun Modifier.opacity(value: String): Modifier = style("opacity", value)
        
        /**
         * Sets the border.
         */
        fun Modifier.border(width: String, style: String, color: String): Modifier =
            this.style("border", "$width $style $color")
        
        /**
         * Sets the border radius.
         */
        fun Modifier.borderRadius(value: String): Modifier = style("border-radius", value)
        
        /**
         * Sets box shadow.
         */
        fun Modifier.shadow(offsetX: String, offsetY: String, blurRadius: String, color: String): Modifier =
            style("box-shadow", "$offsetX $offsetY $blurRadius $color")
        
        /**
         * Adds a simple shadow with default values.
         */
        fun Modifier.shadow(): Modifier = shadow("0px", "2px", "4px", "rgba(0,0,0,0.2)")
        
        /**
         * Sets box shadow using a CSS value.
         */
        fun Modifier.boxShadow(value: String): Modifier = style("box-shadow", value)
        
        /**
         * Sets the cursor style.
         */
        fun Modifier.cursor(value: String): Modifier = style("cursor", value)
        
        /**
         * Sets CSS transitions for animating property changes.
         */
        fun Modifier.transitions(value: String): Modifier = style("transition", value)
        
        /**
         * Sets the visibility property.
         */
        fun Modifier.visibility(value: String): Modifier = style("visibility", value)
        
        /**
         * Makes the element invisible while preserving its space.
         */
        fun Modifier.invisible(): Modifier = visibility("hidden")
    }
    
    /**
     * Interactivity related modifiers
     */
    object Interactivity {
        /**
         * Adds hover effect styles.
         */
        fun Modifier.hover(hoverStyles: Map<String, String>): Modifier =
            style("__hover", hoverStyles.entries.joinToString(";") { (k, v) -> "$k:$v" })
        
        /**
         * Adds hover effect with a Modifier.
         */
        fun Modifier.hover(hoverModifier: Modifier): Modifier = hover(hoverModifier.styles)
        
        /**
         * Makes the element disabled.
         */
        fun Modifier.disabled(): Modifier = styles(
            mapOf(
                "opacity" to "0.6",
                "cursor" to "not-allowed",
                "__disabled" to "true"
            )
        )
        
        /**
         * Sets styles for when the element is focused.
         */
        fun Modifier.focus(focusStyles: Map<String, String>): Modifier =
            style("__focus", focusStyles.entries.joinToString(";") { (k, v) -> "$k:$v" })
        
        /**
         * Sets cursor to pointer (for clickable elements).
         */
        fun Modifier.clickable(): Modifier = cursor("pointer")
    }
    
    /**
     * Scrollable component specific modifiers
     */
    object Scroll {
        /**
         * Sets the overflow behavior.
         */
        fun Modifier.overflow(value: String): Modifier = style("overflow", value)
        
        /**
         * Enables vertical scrolling with automatic scrollbars.
         */
        fun Modifier.verticalScroll(): Modifier = styles(
            mapOf(
                "overflow" to "auto",
                "overflow-y" to "auto",
                "overflow-x" to "hidden"
            )
        )
        
        /**
         * Enables horizontal scrolling with automatic scrollbars.
         */
        fun Modifier.horizontalScroll(): Modifier = styles(
            mapOf(
                "overflow" to "auto",
                "overflow-x" to "auto",
                "overflow-y" to "hidden"
            )
        )
        
        /**
         * Sets the scroll behavior.
         */
        fun Modifier.scrollBehavior(value: String): Modifier = style("scroll-behavior", value)
    }
}

// Legacy adapter functions to keep backward compatibility
// These are placed in the base package to avoid breaking existing code

/**
 * Creates a ScrollableModifier for use with ScrollableComponent.
 */
fun Modifier.scrollable(): ScrollableModifier = ScrollableModifier(this)

/**
 * Creates an InputModifier for use with InputComponent.
 */
fun Modifier.input(): InputModifier = InputModifier(this)

/**
 * Creates a TextModifier for use with TextComponent.
 */
fun Modifier.text(): TextModifier = TextModifier(this)

/**
 * Creates a LayoutModifier for use with LayoutComponent.
 */
fun Modifier.layout(): LayoutModifier = LayoutModifier(this)

/**
 * Legacy implementation of specialized modifiers to maintain compatibility
 */
class ScrollableModifier internal constructor(private val baseModifier: Modifier) {
    fun overflow(value: String): ScrollableModifier {
        return ScrollableModifier(baseModifier.style("overflow", value))
    }

    fun verticalScroll(): ScrollableModifier {
        return overflow("auto").then(
            Modifier(mapOf("overflow-y" to "auto", "overflow-x" to "hidden"))
        )
    }

    fun horizontalScroll(): ScrollableModifier {
        return overflow("auto").then(
            Modifier(mapOf("overflow-x" to "auto", "overflow-y" to "hidden"))
        )
    }

    fun scrollBehavior(value: String): ScrollableModifier {
        return ScrollableModifier(baseModifier.style("scroll-behavior", value))
    }

    fun then(other: Modifier): ScrollableModifier {
        return ScrollableModifier(baseModifier.then(other))
    }

    fun toModifier(): Modifier {
        return baseModifier
    }
}

class InputModifier internal constructor(private val baseModifier: Modifier) {
    fun placeholderColor(color: String): InputModifier {
        val placeholderStyles = mapOf(
            "__placeholder" to "color:$color"
        )
        return InputModifier(baseModifier.then(Modifier(placeholderStyles)))
    }

    fun focusBorderColor(color: String): InputModifier {
        val focusStyles = mapOf(
            "__focus" to "border-color:$color;outline-color:$color"
        )
        return InputModifier(baseModifier.then(Modifier(focusStyles)))
    }

    fun disabled(): InputModifier {
        return InputModifier(
            baseModifier.then(
                Modifier(
                    mapOf(
                        "opacity" to "0.6",
                        "cursor" to "not-allowed",
                        "__disabled" to "true"
                    )
                )
            )
        )
    }

    fun then(other: Modifier): InputModifier {
        return InputModifier(baseModifier.then(other))
    }

    fun toModifier(): Modifier {
        return baseModifier
    }
}

class TextModifier internal constructor(private val baseModifier: Modifier) {
    fun ellipsis(): TextModifier {
        return TextModifier(
            baseModifier.then(
                Modifier(
                    mapOf(
                        "white-space" to "nowrap",
                        "overflow" to "hidden",
                        "text-overflow" to "ellipsis"
                    )
                )
            )
        )
    }

    fun whiteSpace(value: String): TextModifier {
        return TextModifier(baseModifier.style("white-space", value))
    }

    fun textAlign(value: String): TextModifier {
        return TextModifier(baseModifier.style("text-align", value))
    }

    fun textTransform(value: String): TextModifier {
        return TextModifier(baseModifier.style("text-transform", value))
    }

    fun lineHeight(value: String): TextModifier {
        return TextModifier(baseModifier.style("line-height", value))
    }

    fun then(other: Modifier): TextModifier {
        return TextModifier(baseModifier.then(other))
    }

    fun toModifier(): Modifier {
        return baseModifier
    }
}

class LayoutModifier internal constructor(private val baseModifier: Modifier) {
    fun flexGrow(value: Int): LayoutModifier {
        return LayoutModifier(baseModifier.style("flex-grow", value.toString()))
    }

    fun flexShrink(value: Int): LayoutModifier {
        return LayoutModifier(baseModifier.style("flex-shrink", value.toString()))
    }

    fun flexBasis(value: String): LayoutModifier {
        return LayoutModifier(baseModifier.style("flex-basis", value))
    }

    fun gridTemplateColumns(value: String): LayoutModifier {
        return LayoutModifier(baseModifier.style("grid-template-columns", value))
    }

    fun gridTemplateRows(value: String): LayoutModifier {
        return LayoutModifier(baseModifier.style("grid-template-rows", value))
    }

    fun gridGap(value: String): LayoutModifier {
        return LayoutModifier(baseModifier.style("grid-gap", value))
    }

    fun then(other: Modifier): LayoutModifier {
        return LayoutModifier(baseModifier.then(other))
    }

    fun toModifier(): Modifier {
        return baseModifier
    }
} 