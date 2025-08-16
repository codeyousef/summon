package code.yousef.example.quarkus.ui.theme

import code.yousef.summon.core.style.Color
import code.yousef.summon.theme.*
import code.yousef.summon.modifier.FontWeight

/**
 * Application theme definitions
 */
object AppThemes {
    
    /**
     * Light theme configuration
     */
    val lightTheme = AppTheme(
        name = "light",
        colors = AppColors(
            primary = "#1976D2",
            primaryVariant = "#1565C0",
            secondary = "#03DAC6",
            secondaryVariant = "#018786",
            background = "#FFFFFF",
            surface = "#F5F5F5",
            error = "#B00020",
            onPrimary = "#FFFFFF",
            onSecondary = "#000000",
            onBackground = "#000000",
            onSurface = "#000000",
            onError = "#FFFFFF",
            cardBackground = "#FFFFFF",
            border = "#E0E0E0",
            textPrimary = "#212121",
            textSecondary = "#757575",
            textHint = "#9E9E9E"
        ),
        typography = AppTypography(
            h1 = Theme.TextStyle.create(fontSize = 32, fontWeight = FontWeight.Bold, color = Color.fromHex("#212121")),
            h2 = Theme.TextStyle.create(fontSize = 24, fontWeight = FontWeight.Bold, color = Color.fromHex("#212121")),
            h3 = Theme.TextStyle.create(fontSize = 20, fontWeight = FontWeight.Bold, color = Color.fromHex("#212121")),
            body1 = Theme.TextStyle.create(fontSize = 16, fontWeight = FontWeight.Normal, color = Color.fromHex("#212121")),
            body2 = Theme.TextStyle.create(fontSize = 14, fontWeight = FontWeight.Normal, color = Color.fromHex("#757575")),
            caption = Theme.TextStyle.create(fontSize = 12, fontWeight = FontWeight.Normal, color = Color.fromHex("#9E9E9E")),
            button = Theme.TextStyle.create(fontSize = 14, fontWeight = FontWeight.Medium, color = Color.fromHex("#FFFFFF"))
        ),
        spacing = AppSpacing()
    )
    
    /**
     * Dark theme configuration
     */
    val darkTheme = AppTheme(
        name = "dark",
        colors = AppColors(
            primary = "#BB86FC",
            primaryVariant = "#6200EE",
            secondary = "#03DAC6",
            secondaryVariant = "#03DAC6",
            background = "#121212",
            surface = "#1E1E1E",
            error = "#CF6679",
            onPrimary = "#000000",
            onSecondary = "#000000",
            onBackground = "#FFFFFF",
            onSurface = "#FFFFFF",
            onError = "#000000",
            cardBackground = "#2D2D2D",
            border = "#373737",
            textPrimary = "#FFFFFF",
            textSecondary = "#B3B3B3",
            textHint = "#666666"
        ),
        typography = AppTypography(
            h1 = Theme.TextStyle.create(fontSize = 32, fontWeight = FontWeight.Bold, color = Color.fromHex("#FFFFFF")),
            h2 = Theme.TextStyle.create(fontSize = 24, fontWeight = FontWeight.Bold, color = Color.fromHex("#FFFFFF")),
            h3 = Theme.TextStyle.create(fontSize = 20, fontWeight = FontWeight.Bold, color = Color.fromHex("#FFFFFF")),
            body1 = Theme.TextStyle.create(fontSize = 16, fontWeight = FontWeight.Normal, color = Color.fromHex("#FFFFFF")),
            body2 = Theme.TextStyle.create(fontSize = 14, fontWeight = FontWeight.Normal, color = Color.fromHex("#B3B3B3")),
            caption = Theme.TextStyle.create(fontSize = 12, fontWeight = FontWeight.Normal, color = Color.fromHex("#666666")),
            button = Theme.TextStyle.create(fontSize = 14, fontWeight = FontWeight.Medium, color = Color.fromHex("#000000"))
        ),
        spacing = AppSpacing()
    )
}

/**
 * Application theme data class
 */
data class AppTheme(
    val name: String,
    val colors: AppColors,
    val typography: AppTypography,
    val spacing: AppSpacing
)

/**
 * Application color scheme
 */
data class AppColors(
    val primary: String,
    val primaryVariant: String,
    val secondary: String,
    val secondaryVariant: String,
    val background: String,
    val surface: String,
    val error: String,
    val onPrimary: String,
    val onSecondary: String,
    val onBackground: String,
    val onSurface: String,
    val onError: String,
    val cardBackground: String,
    val border: String,
    val textPrimary: String,
    val textSecondary: String,
    val textHint: String
)

/**
 * Application typography
 */
data class AppTypography(
    val h1: Theme.TextStyle,
    val h2: Theme.TextStyle,
    val h3: Theme.TextStyle,
    val body1: Theme.TextStyle,
    val body2: Theme.TextStyle,
    val caption: Theme.TextStyle,
    val button: Theme.TextStyle
)

/**
 * Application spacing
 */
data class AppSpacing(
    val xs: String = "4px",
    val sm: String = "8px",
    val md: String = "16px",
    val lg: String = "24px",
    val xl: String = "32px",
    val xxl: String = "48px"
)

