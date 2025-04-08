package components.feedback

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Defines alert variants for different semantic meanings
 */
enum class AlertVariant {
    INFO,       // Informational messages
    SUCCESS,    // Success messages
    WARNING,    // Warning messages
    DANGER      // Error/danger messages
} 
