package codes.yousef.summon.core.rendering

import codes.yousef.summon.core.splitCompat

import codes.yousef.summon.modifier.Modifier

/**
 * Common patterns and utilities for platform-specific rendering.
 * This provides abstraction over common rendering operations to reduce code duplication.
 */

/**
 * Common data structure for component rendering that includes standard properties
 * most components need.
 */
data class ComponentRenderData(
    val modifier: Modifier,
    val additionalStyles: Map<String, String> = emptyMap(),
    val accessibilityAttributes: Map<String, String> = emptyMap(),
    val customAttributes: Map<String, String> = emptyMap()
) {
    /**
     * Combines modifier styles with additional styles into a single map.
     */
    fun getCombinedStyles(): Map<String, String> {
        return modifier.styles + additionalStyles
    }

    /**
     * Gets all attributes including accessibility and custom attributes.
     */
    fun getAllAttributes(): Map<String, String> {
        return accessibilityAttributes + customAttributes
    }

    /**
     * Creates a CSS style string from combined styles.
     */
    fun getStyleString(): String {
        val combinedStyles = getCombinedStyles()
        return if (combinedStyles.isNotEmpty()) {
            combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
        } else {
            ""
        }
    }
}

/**
 * Utility functions for common rendering patterns.
 */
object RenderingUtils {
    /**
     * Converts a CSS property name to kebab-case (for HTML style attributes).
     * Examples: backgroundColor -> background-color, fontSize -> font-size
     */
    fun toKebabCase(camelCase: String): String {
        return camelCase.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
    }

    /**
     * Converts a CSS property name to camelCase (for JavaScript DOM style properties).
     * Examples: background-color -> backgroundColor, font-size -> fontSize
     */
    fun toCamelCase(kebabCase: String): String {
        return kebabCase.splitCompat("-").mapIndexed { index, part ->
            if (index == 0) part else part.replaceFirstChar { it.uppercase() }
        }.joinToString("")
    }

    /**
     * Normalizes CSS property names to ensure consistent formatting.
     * If the property already contains hyphens, returns as-is.
     * Otherwise, converts camelCase to kebab-case.
     */
    fun normalizeCssProperty(property: String): String {
        return if (property.contains('-')) {
            property
        } else {
            toKebabCase(property)
        }
    }

    /**
     * Creates a CSS style string from a map of properties.
     * Automatically normalizes property names.
     */
    fun createStyleString(styles: Map<String, String>): String {
        return styles.entries.joinToString(
            separator = "; ",
            postfix = if (styles.isNotEmpty()) ";" else ""
        ) { (key, value) ->
            "${normalizeCssProperty(key)}: $value"
        }
    }

    /**
     * Merges multiple style maps with later maps taking precedence.
     */
    fun mergeStyles(vararg styleMaps: Map<String, String>): Map<String, String> {
        return styleMaps.fold(emptyMap()) { acc, styles ->
            acc + styles
        }
    }

    /**
     * Creates a component render data instance with sensible defaults.
     */
    fun createRenderData(
        modifier: Modifier,
        additionalStyles: Map<String, String> = emptyMap(),
        accessibilityAttributes: Map<String, String> = emptyMap(),
        customAttributes: Map<String, String> = emptyMap()
    ): ComponentRenderData {
        return ComponentRenderData(
            modifier = modifier,
            additionalStyles = additionalStyles,
            accessibilityAttributes = accessibilityAttributes,
            customAttributes = customAttributes
        )
    }
}

/**
 * Extension functions for common modifier operations.
 */

/**
 * Extracts event handlers from a modifier.
 * This is a placeholder for platform-specific event handler extraction.
 */
fun Modifier.extractEventHandlers(): Map<String, Any> {
    // This would be implemented differently on each platform
    // For now, return empty map as a placeholder
    return emptyMap()
}

/**
 * Gets a normalized style string that can be used across platforms.
 */
fun Modifier.getNormalizedStyleString(): String {
    return RenderingUtils.createStyleString(this.styles)
}

/**
 * Combines this modifier with additional rendering data.
 */
fun Modifier.withRenderData(
    additionalStyles: Map<String, String> = emptyMap(),
    accessibilityAttributes: Map<String, String> = emptyMap(),
    customAttributes: Map<String, String> = emptyMap()
): ComponentRenderData {
    return RenderingUtils.createRenderData(
        modifier = this,
        additionalStyles = additionalStyles,
        accessibilityAttributes = accessibilityAttributes,
        customAttributes = customAttributes
    )
}