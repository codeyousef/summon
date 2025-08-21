package code.yousef.example.springboot

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.FontWeight
import code.yousef.summon.modifier.Display

// Extension to convert FontWeight enum to string for use with modifiers
fun Modifier.fontWeight(weight: FontWeight): Modifier = this.fontWeight(weight.value)
fun Modifier.fontWeight(weight: String): Modifier = this.attribute("style", "font-weight: $weight")

/**
 * Extension functions for missing modifiers in the Spring Boot example.
 * These provide compatibility shims for commonly used CSS properties.
 */

// Layout modifiers
fun Modifier.flex(value: Int): Modifier = this.attribute("style", "flex: $value")
fun Modifier.flex(value: String): Modifier = this.attribute("style", "flex: $value")
fun Modifier.alignItems(value: String): Modifier = this.attribute("style", "align-items: $value")
fun Modifier.justifyContent(value: String): Modifier = this.attribute("style", "justify-content: $value")
fun Modifier.gap(value: String): Modifier = this.attribute("style", "gap: $value")
fun Modifier.minHeight(value: String): Modifier = this.attribute("style", "min-height: $value")
fun Modifier.maxHeight(value: String): Modifier = this.attribute("style", "max-height: $value")
fun Modifier.textAlign(value: String): Modifier = this.attribute("style", "text-align: $value")

// Border modifiers
fun Modifier.borderBottom(width: String, style: String, color: String): Modifier = 
    this.attribute("style", "border-bottom: $width $style $color")
fun Modifier.borderTop(width: String, style: String, color: String): Modifier = 
    this.attribute("style", "border-top: $width $style $color")

// Visual effect modifiers
fun Modifier.boxShadow(value: String): Modifier = this.attribute("style", "box-shadow: $value")
fun Modifier.backgroundColor(value: String): Modifier = this.attribute("style", "background-color: $value")

// Text modifiers
fun Modifier.textDecoration(value: String): Modifier = this.attribute("style", "text-decoration: $value")

// Event modifiers (simplified - in real usage these would connect to the runtime)
fun Modifier.onClick(handler: () -> Unit): Modifier = this

// Pseudo-class modifiers (simplified - these would need runtime support)
fun Modifier.hover(block: Modifier.() -> Modifier): Modifier = this

// Additional missing modifiers
fun Modifier.borderRadius(value: String): Modifier = this.attribute("style", "border-radius: $value")
fun Modifier.overflowY(value: String): Modifier = this.attribute("style", "overflow-y: $value")
fun Modifier.display(value: String): Modifier = this.attribute("style", "display: $value")
fun Modifier.display(value: Display): Modifier = this.attribute("style", "display: ${value.value}")
fun Modifier.opacity(value: Double): Modifier = this.attribute("style", "opacity: $value")
fun Modifier.opacity(value: Float): Modifier = this.attribute("style", "opacity: $value")