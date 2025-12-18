package portfolio.theme

import codes.yousef.summon.extensions.px

/**
 * Theme configuration for the portfolio.
 * Adapted from the main portfolio project.
 */
object PortfolioTheme {
    object Colors {
        const val BACKGROUND = "#001a2c"
        const val BACKGROUND_ALT = "#05294a"
        const val SURFACE = "rgba(255,255,255,0.12)"
        const val SURFACE_STRONG = "rgba(255,255,255,0.18)"
        const val GLASS = "rgba(255,255,255,0.15)"
        const val BORDER = "rgba(255,255,255,0.15)"
        const val BORDER_STRONG = "rgba(255,255,255,0.25)"
        const val TEXT_PRIMARY = "#ffffff"
        const val TEXT_SECONDARY = "#d0dae8"
        const val ACCENT = "#ff4668"
        const val ACCENT_ALT = "#ff89b0"
        const val ACCENT_HOVER = "#ff5f7f"
        const val LINK = "#6ad7ff"
        const val LINK_HOVER = "#8fe3ff"
        const val SUCCESS = "#3dd598"
        const val WARNING = "#fbbf24"
        const val DANGER = "#ff4d4d"
    }

    object Spacing {
        val xxs = 2.px
        val xs = 4.px
        val sm = 8.px
        val md = 16.px
        val lg = 24.px
        val xl = 32.px
        val xxl = 48.px

        fun scale(multiplier: Int): String = (multiplier * 4).px
    }

    object Radii {
        val sm = 8.px
        val md = 16.px
        val lg = 24.px
        val pill = 999.px
    }

    object Shadows {
        const val LOW = "0 10px 40px rgba(0,0,0,.45), 0 2px 10px rgba(0,0,0,.35)"
        const val MEDIUM = "0 20px 60px rgba(0,0,0,.55)"
        const val HIGH = "0 30px 120px rgba(0,0,0,.65)"
    }

    object Motion {
        const val DEFAULT = "200ms ease"
        const val EMPHASIZED = "300ms cubic-bezier(0.4, 0, 0.2, 1)"
        const val BOUNCE = "400ms cubic-bezier(0.34, 1.56, 0.64, 1)"
    }

    object Typography {
        const val FONT_SANS = "\"Inter\", \"SF Pro Display\", system-ui, -apple-system, BlinkMacSystemFont, \"Segoe UI\", sans-serif"
        const val FONT_SERIF = "\"Playfair Display\", \"Times New Roman\", serif"
        const val FONT_MONO = "\"JetBrains Mono\", \"Fira Code\", ui-monospace, SFMono-Regular, Menlo, monospace"
        const val HERO_TRACKING = "-0.025em"
    }

    object Gradients {
        const val HERO =
            "radial-gradient(circle at 20% 30%, rgba(255,70,104,0.2) 0%, transparent 60%)," +
                "radial-gradient(circle at 80% 70%, rgba(106,215,255,0.15) 0%, transparent 60%)," +
                "linear-gradient(180deg, #001a2c 0%, #05294a 100%)"
        const val ACCENT = "linear-gradient(120deg, ${Colors.ACCENT}, ${Colors.ACCENT_ALT})"
        const val CARD = "linear-gradient(180deg, rgba(255,255,255,0.15), rgba(255,255,255,0.06))"
        const val GLASS = "linear-gradient(180deg, rgba(255,255,255,0.20), rgba(255,255,255,0.10))"
    }
}
