package code.yousef.summon.components.feedback

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Variants (styles) for Alert components.
 * These correspond to common alert/message types in UI design.
 */
enum class AlertVariant {
    /** Information message, usually blue */
    INFO,
    
    /** Success message, usually green */
    SUCCESS,
    
    /** Warning message, usually yellow or orange */
    WARNING,
    
    /** Error/danger message, usually red */
    ERROR,
    
    /** Default/neutral message, usually gray */
    DEFAULT;
    
    /**
     * Get the CSS color associated with this variant.
     * These are general default colors that can be overridden by themes.
     */
    fun getColor(): String {
        return when (this) {
            INFO -> "#0284c7"     // Blue
            SUCCESS -> "#16a34a"  // Green
            WARNING -> "#d97706"  // Orange
            ERROR -> "#dc2626"    // Red
            DEFAULT -> "#6b7280"  // Gray
        }
    }
    
    /**
     * Get the CSS background color associated with this variant.
     * These are lighter background colors that pair well with the main colors.
     */
    fun getBackgroundColor(): String {
        return when (this) {
            INFO -> "#e0f2fe"     // Light blue
            SUCCESS -> "#dcfce7"  // Light green
            WARNING -> "#fef3c7"  // Light yellow
            ERROR -> "#fee2e2"    // Light red
            DEFAULT -> "#f3f4f6"  // Light gray
        }
    }
} 
