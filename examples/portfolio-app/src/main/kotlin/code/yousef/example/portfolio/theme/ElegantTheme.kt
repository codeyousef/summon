package code.yousef.example.portfolio.theme

/**
 * Elegant theme for refined portfolio design
 * Inspired by Apple's precision and Stripe's elegance
 */
object ElegantTheme {
    // Primary Colors
    const val WHITE = "#FFFFFF"
    const val BLACK = "#000000"
    const val NAVY_DARK = "#0F172A"      // Very dark navy background
    const val NAVY_MEDIUM = "#1E293B"    // Medium navy
    const val NAVY_LIGHT = "#334155"     // Lighter navy sections
    const val ACCENT_BLUE = "#3B82F6"    // Bright blue accent
    
    // Blue Scale for text and UI elements
    const val BLUE_50 = "#F8FAFC"        // Very light blue text
    const val BLUE_100 = "#E2E8F0"       // Light blue text
    const val BLUE_200 = "#CBD5E1"       // Light blue borders
    const val BLUE_400 = "#64748B"       // Medium blue text
    const val BLUE_600 = "#475569"       // Darker blue elements
    
    // Muted jewel tones for project nodes
    const val SAPPHIRE = "#0066CC"  // Summon
    const val EMERALD = "#00875A"   // HorizonOS
    const val AMETHYST = "#6B46C1"  // SeenLang
    
    // Shadow definitions
    const val SHADOW_SMALL = "0 2px 4px rgba(0, 0, 0, 0.04)"
    const val SHADOW_MEDIUM = "0 4px 12px rgba(0, 0, 0, 0.08)"
    const val SHADOW_LARGE = "0 12px 24px rgba(0, 0, 0, 0.08)"
    const val SHADOW_XLARGE = "0 20px 40px rgba(0, 0, 0, 0.04)"
    
    // Animation timing
    const val ANIMATION_SWIFT = "250ms"
    const val ANIMATION_SMOOTH = "300ms"
    const val ANIMATION_GENTLE = "400ms"
    
    // Border radius
    const val RADIUS_SMALL = "8px"
    const val RADIUS_MEDIUM = "12px"
    const val RADIUS_LARGE = "16px"
    const val RADIUS_XLARGE = "20px"
    
    // Spacing
    const val SPACING_XS = "8px"
    const val SPACING_SM = "16px"
    const val SPACING_MD = "24px"
    const val SPACING_LG = "40px"
    const val SPACING_XL = "60px"
    const val SPACING_XXL = "80px"
    const val SPACING_XXXL = "120px"
    
    // Z-index layers
    const val Z_BASE = 0
    const val Z_CONTENT = 10
    const val Z_CARD = 20
    const val Z_NAVIGATION = 100
    const val Z_MODAL = 1000
    
    // CSS Keyframes for refined animations
    fun getElegantKeyframes(): String = """
        <style>
        /* Import Inter font */
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@200;300;400;500;600&display=swap');
        
        /* Subtle floating animation */
        @keyframes subtleFloat {
            0%, 100% { transform: translateY(0px); }
            50% { transform: translateY(-2px); }
        }
        
        /* Fade in animation */
        @keyframes fadeIn {
            from { 
                opacity: 0;
                transform: translateY(10px);
            }
            to { 
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        /* Scale in animation */
        @keyframes scaleIn {
            from { 
                opacity: 0;
                transform: scale(0.95);
            }
            to { 
                opacity: 1;
                transform: scale(1);
            }
        }
        
        /* Slide up animation */
        @keyframes slideUp {
            from { 
                opacity: 0;
                transform: translateY(20px);
            }
            to { 
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        /* Gentle pulse for connections */
        @keyframes gentlePulse {
            0%, 100% { opacity: 0.4; }
            50% { opacity: 0.6; }
        }
        
        /* Smooth rotation for graph */
        @keyframes gentleRotate {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }
        
        /* Expand animation for cards */
        @keyframes expandShadow {
            from { 
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
                transform: translateY(0);
            }
            to { 
                box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
                transform: translateY(-4px);
            }
        }
        
        /* Success checkmark animation */
        @keyframes checkmark {
            0% { 
                stroke-dashoffset: 100;
                opacity: 0;
            }
            50% {
                opacity: 1;
            }
            100% { 
                stroke-dashoffset: 0;
                opacity: 1;
            }
        }
        
        /* Global styles */
        * {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            -webkit-font-smoothing: antialiased;
            -moz-osx-font-smoothing: grayscale;
        }
        
        /* Smooth scrolling */
        html {
            scroll-behavior: smooth;
        }
        
        /* Selection color */
        ::selection {
            background: rgba(0, 102, 204, 0.1);
            color: #0066CC;
        }
        
        /* Focus styles */
        *:focus {
            outline: 2px solid rgba(0, 102, 204, 0.4);
            outline-offset: 2px;
        }
        
        /* Reduced motion */
        @media (prefers-reduced-motion: reduce) {
            * {
                animation-duration: 0.01ms !important;
                animation-iteration-count: 1 !important;
                transition-duration: 0.01ms !important;
            }
        }
        </style>
    """.trimIndent()
}