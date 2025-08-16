package code.yousef.example.portfolio.theme

/**
 * Catppuccin Mocha color palette for the futuristic portfolio
 * https://github.com/catppuccin/catppuccin
 */
object CatppuccinMocha {
    // Base colors
    const val ROSEWATER = "#f5e0dc"
    const val FLAMINGO = "#f2cdcd"
    const val PINK = "#f5c2e7"
    const val MAUVE = "#cba6f7"
    const val RED = "#f38ba8"
    const val MAROON = "#eba0ac"
    const val PEACH = "#fab387"
    const val YELLOW = "#f9e2af"
    const val GREEN = "#a6e3a1"
    const val TEAL = "#94e2d5"
    const val SKY = "#89dceb"
    const val SAPPHIRE = "#74c7ec"
    const val BLUE = "#89b4fa"
    const val LAVENDER = "#b4befe"
    
    // Neutral colors
    const val TEXT = "#cdd6f4"
    const val SUBTEXT1 = "#bac2de"
    const val SUBTEXT0 = "#a6adc8"
    const val OVERLAY2 = "#9399b2"
    const val OVERLAY1 = "#7f849c"
    const val OVERLAY0 = "#6c7086"
    const val SURFACE2 = "#585b70"
    const val SURFACE1 = "#45475a"
    const val SURFACE0 = "#313244"
    const val BASE = "#1e1e2e"
    const val MANTLE = "#181825"
    const val CRUST = "#11111b"
    
    // Custom deep space black for background
    const val DEEP_SPACE = "#0a0a0f"
    
    // Neon accent colors for cyberpunk effect
    const val NEON_CYAN = "#00ffff"
    const val NEON_PINK = "#ff00ff"
    const val NEON_BLUE = "#00bbff"
    const val NEON_GREEN = "#00ff88"
    
    // Additional glassmorphism colors
    const val GLASS_SURFACE = "rgba(49, 50, 68, 0.6)"
    const val GLASS_BORDER = "rgba(137, 180, 250, 0.3)"
    const val GLASS_GLOW = "rgba(0, 255, 255, 0.1)"
}

/**
 * Futuristic theme configuration
 */
object FuturisticTheme {
    // Background gradient colors
    val backgroundGradient = listOf(
        CatppuccinMocha.DEEP_SPACE,
        CatppuccinMocha.CRUST,
        CatppuccinMocha.BASE
    )
    
    // Glass morphism settings
    const val GLASS_BLUR = "12px"
    const val GLASS_OPACITY = 0.08
    const val GLASS_BORDER_OPACITY = 0.2
    
    // Glow effects
    val glowColors = mapOf(
        "summon" to CatppuccinMocha.BLUE,
        "horizonos" to CatppuccinMocha.TEAL,
        "seenlang" to CatppuccinMocha.MAUVE,
        "default" to CatppuccinMocha.SAPPHIRE
    )
    
    // Animation durations
    const val ANIMATION_FAST = "0.3s"
    const val ANIMATION_NORMAL = "0.6s"
    const val ANIMATION_SLOW = "1.2s"
    
    // Z-index layers
    const val Z_BACKGROUND = 0
    const val Z_PARTICLES = 1
    const val Z_GRID = 2
    const val Z_CONTENT = 10
    const val Z_NODES = 20
    const val Z_CONNECTIONS = 15
    const val Z_NAVIGATION = 100
    const val Z_MODAL = 1000
    
    // CSS Animation keyframes
    fun getCSSKeyframes(): String = """
        <style>
        @keyframes float {
            0%, 100% { transform: translateY(0px) rotate(0deg); }
            33% { transform: translateY(-10px) rotate(1deg); }
            66% { transform: translateY(-5px) rotate(-1deg); }
        }
        
        @keyframes pulse {
            0%, 100% { box-shadow: 0 12px 40px rgba(137, 180, 250, 0.2); }
            50% { box-shadow: 0 12px 40px rgba(137, 180, 250, 0.4); }
        }
        
        @keyframes flow {
            0% { transform: translateX(-100px); opacity: 0; }
            50% { opacity: 1; }
            100% { transform: translateX(100px); opacity: 0; }
        }
        
        @keyframes mesh {
            0%, 100% { 
                transform: scale(1) translate(0, 0);
                opacity: 1;
            }
            25% { 
                transform: scale(1.05) translate(-2%, -1%);
                opacity: 0.9;
            }
            50% { 
                transform: scale(1.02) translate(1%, -2%);
                opacity: 0.95;
            }
            75% { 
                transform: scale(1.08) translate(2%, 1%);
                opacity: 0.85;
            }
        }
        
        @keyframes particles {
            0% { 
                transform: translateY(100vh) translateX(0) scale(1);
                opacity: 0;
            }
            10% {
                opacity: 0.6;
            }
            90% {
                opacity: 0.6;
            }
            100% { 
                transform: translateY(-100px) translateX(30px) scale(0.8);
                opacity: 0;
            }
        }
        
        @keyframes particlesAlt {
            0% { 
                transform: translateY(100vh) translateX(0) scale(1.2);
                opacity: 0;
            }
            10% {
                opacity: 0.4;
            }
            90% {
                opacity: 0.4;
            }
            100% { 
                transform: translateY(-100px) translateX(-40px) scale(0.6);
                opacity: 0;
            }
        }
        
        @keyframes typing {
            from { width: 0; }
            to { width: 100%; }
        }
        
        @keyframes glow {
            0%, 100% { text-shadow: 0 0 10px rgba(0, 255, 255, 0.5); }
            50% { text-shadow: 0 0 20px rgba(0, 255, 255, 0.8); }
        }
        
        @keyframes nodeHover {
            0% { transform: translateY(0) scale(1); }
            100% { transform: translateY(-5px) scale(1.05); }
        }
        </style>
    """.trimIndent()
}