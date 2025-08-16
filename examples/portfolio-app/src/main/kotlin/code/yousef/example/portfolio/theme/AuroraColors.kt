package code.yousef.example.portfolio.theme

/**
 * Simple Aurora color palette using string constants.
 * Basic color definitions for the portfolio theme.
 */
object AuroraColors {
    // Core aurora colors
    const val Primary = "#38B2AC"          // Teal
    const val Secondary = "#2C5282"        // Navy
    const val Accent = "#9B2C2C"           // Maroon
    const val White = "#FFFFFF"            // Pure white
    const val Black = "#000000"            // Pure black
    
    // Background colors
    const val BackgroundDark = "#0f0f23"   // Deep space
    const val BackgroundBlack = "#000000"  // Pure black
    
    // Glass morphism colors with alpha
    const val GlassBackground = "rgba(255, 255, 255, 0.05)"    // Very subtle white overlay
    const val GlassBorder = "rgba(255, 255, 255, 0.2)"        // Subtle white border
    const val GlassHover = "rgba(255, 255, 255, 0.08)"        // Slightly more visible on hover
    
    // Portal proximity glow variants
    const val PrimaryGlow = "rgba(56, 178, 172, 0.8)"         // Bright teal glow
    const val SecondaryGlow = "rgba(44, 82, 130, 0.7)"        // Navy glow
    const val AccentGlow = "rgba(155, 44, 44, 0.6)"           // Maroon glow
    
    // Text colors with proper opacity
    const val TextPrimary = "#FFFFFF"                          // Full opacity white text
    const val TextSecondary = "rgba(255, 255, 255, 0.8)"      // Secondary text
    const val TextMuted = "rgba(255, 255, 255, 0.6)"          // Muted text
    
    // Aurora-specific gradient colors for portal effects
    const val PortalLight = "#F0F9FF"                          // Light blue-white
    const val PortalTeal = Primary                             // Core teal
    const val PortalNavy = Secondary                           // Deep navy
    const val PortalMaroon = Accent                            // Deep maroon
}

/**
 * Simple aurora animation constants.
 */
object AuroraAnimations {
    const val PORTAL_PULSE = "4s"          // Main portal heartbeat
    const val ENERGY_FLOW = "3s"           // Energy flowing through connections
    const val SHARD_FLOAT = "6s"           // Gentle floating motion
    const val ORBITAL_DRIFT = "20s"        // Very slow orbital movement
    const val GLOW_BREATH = "8s"           // Breathing glow intensity
}

/**
 * Simple aurora shadow definitions using CSS strings.
 */
object AuroraShadows {
    const val PortalGlow = "0 0 20px rgba(56, 178, 172, 0.8)"
    const val GlassMorphism = "0 8px 32px rgba(56, 178, 172, 0.3)"
    const val InnerGlow = "inset 0 0 10px rgba(255, 255, 255, 0.1)"
    const val AuroraGlow = "0 0 20px rgba(240, 249, 255, 0.8), 0 0 40px rgba(56, 178, 172, 0.8), 0 0 60px rgba(255, 255, 255, 0.4)"
}

/**
 * Aurora gradient definitions for portal and energy effects.
 */
object AuroraGradients {
    const val PortalBeam = "linear-gradient(180deg, rgba(240, 249, 255, 0.9) 0%, rgba(56, 178, 172, 0.7) 25%, rgba(255, 255, 255, 1) 50%, rgba(44, 82, 130, 0.7) 75%, rgba(155, 44, 44, 0.6) 100%)"
    const val Primary = "linear-gradient(135deg, #38B2AC 0%, #2C5282 100%)"
    const val Secondary = "linear-gradient(135deg, #9B2C2C 0%, #2C5282 100%)"
    const val Background = "radial-gradient(ellipse at center, #0f0f23 0%, #000000 100%)"
}