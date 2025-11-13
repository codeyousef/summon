package codes.yousef.summon.util

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.routing.RouteParams
import codes.yousef.summon.theme.ColorSystem
import codes.yousef.summon.theme.Theme
import codes.yousef.summon.validation.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Test implementation of FileInfo for use in common tests.
 * This is a simple data class that can be used across all platforms.
 */
data class TestFileInfo(
    val name: String,
    val size: Long,
    val type: String
)

/**
 * Test fixtures providing common test data and scenarios.
 * This centralizes test data to ensure consistency across tests.
 */
object TestFixtures {
    
    /**
     * Common test strings for various scenarios
     */
    object Strings {
        const val EMPTY = ""
        const val WHITESPACE = "   "
        const val SIMPLE = "Hello World"
        const val LONG = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
        const val WITH_SPECIAL_CHARS = "Test@123#Special$%^&*()"
        const val WITH_HTML = "<script>alert('XSS')</script>"
        const val WITH_UNICODE = "Hello 世界 🌍"
        const val WITH_NEWLINES = "Line 1\nLine 2\nLine 3"
        
        val ALL = listOf(EMPTY, WHITESPACE, SIMPLE, LONG, WITH_SPECIAL_CHARS, WITH_HTML, WITH_UNICODE, WITH_NEWLINES)
    }
    
    /**
     * Common test numbers
     */
    object Numbers {
        const val ZERO = 0
        const val NEGATIVE = -42
        const val POSITIVE = 42
        const val LARGE = Int.MAX_VALUE
        const val SMALL = Int.MIN_VALUE
        const val DECIMAL = 3.14159
        
        val INTEGERS = listOf(ZERO, NEGATIVE, POSITIVE, LARGE, SMALL)
        val ALL = listOf(ZERO, NEGATIVE, POSITIVE, LARGE, SMALL, DECIMAL.toInt())
    }
    
    /**
     * Common test modifiers
     */
    object Modifiers {
        val EMPTY = Modifier()
        val WITH_BACKGROUND = Modifier().background("#ff0000")
        val WITH_PADDING = Modifier().padding("10px")
        val WITH_MARGIN = Modifier().margin("5px")
        val WITH_SIZE = Modifier().width("100px").height("50px")
        val COMPLEX = Modifier()
            .background("#ff0000")
            .color("#ffffff")
            .padding("10px")
            .margin("5px")
            .borderRadius("4px")
            .fontSize("16px")
        
        val ALL = listOf(EMPTY, WITH_BACKGROUND, WITH_PADDING, WITH_MARGIN, WITH_SIZE, COMPLEX)
    }
    
    /**
     * Common test colors
     */
    object Colors {
        const val RED = "#ff0000"
        const val GREEN = "#00ff00"
        const val BLUE = "#0000ff"
        const val WHITE = "#ffffff"
        const val BLACK = "#000000"
        const val TRANSPARENT = "transparent"
        
        val PRIMARY_COLORS = listOf(RED, GREEN, BLUE)
        val ALL = listOf(RED, GREEN, BLUE, WHITE, BLACK, TRANSPARENT)
    }
    
    /**
     * Common test validators
     */
    object Validators {
        val REQUIRED = RequiredValidator()
        val EMAIL = EmailValidator()
        val MIN_LENGTH_5 = MinLengthValidator(5)
        val MAX_LENGTH_10 = MaxLengthValidator(10)
        val PHONE_PATTERN = PatternValidator(Regex("\\d{3}-\\d{3}-\\d{4}"))
        val ALWAYS_VALID = CustomValidator({ true }, "Always valid")
        val ALWAYS_INVALID = CustomValidator({ false }, "Always invalid")
        
        val ALL = listOf(REQUIRED, EMAIL, MIN_LENGTH_5, MAX_LENGTH_10, PHONE_PATTERN)
    }
    
    /**
     * Common test dates and times
     */
    object DateTimes {
        val TODAY = LocalDate(2024, 1, 1)
        val TOMORROW = LocalDate(2024, 1, 2)
        val YESTERDAY = LocalDate(2023, 12, 31)
        val NEXT_MONTH = LocalDate(2024, 2, 1)
        val LAST_MONTH = LocalDate(2023, 12, 1)
        
        val NOON = LocalTime(12, 0, 0)
        val MIDNIGHT = LocalTime(0, 0, 0)
        val MORNING = LocalTime(9, 30, 0)
        val EVENING = LocalTime(18, 45, 30)
    }
    
    /**
     * Common test route parameters
     */
    object Routes {
        val EMPTY_PARAMS = RouteParams(emptyMap())
        val WITH_ID = RouteParams(mapOf("id" to "123"))
        val WITH_MULTIPLE = RouteParams(mapOf(
            "id" to "123",
            "name" to "test",
            "active" to "true"
        ))
        val WITH_SPECIAL_CHARS = RouteParams(mapOf(
            "query" to "hello+world",
            "filter" to "status%3Dactive"
        ))
    }
    
    /**
     * Common test themes
     */
    object Themes {
        val LIGHT = Theme.ThemeConfig(
            colorPalette = ColorSystem.default,
            typographyTheme = Theme.TypographyTheme(),
            spacingTheme = Theme.SpacingTheme()
        )
        
        val DARK = Theme.ThemeConfig(
            colorPalette = ColorSystem.default,
            typographyTheme = Theme.TypographyTheme(),
            spacingTheme = Theme.SpacingTheme()
        )
        
        val CUSTOM = Theme.ThemeConfig(
            colorPalette = ColorSystem.ColorPalette(
                light = mapOf(
                    "primary" to "#ff6b6b",
                    "secondary" to "#4ecdc4",
                    "background" to "#f7f7f7",
                    "text" to "#2d3436"
                ),
                dark = mapOf(
                    "primary" to "#ff6b6b",
                    "secondary" to "#4ecdc4",
                    "background" to "#2d3436",
                    "text" to "#f7f7f7"
                )
            ),
            typographyTheme = Theme.TypographyTheme(
                body = Theme.TextStyle.create(
                    fontFamily = "Arial, sans-serif",
                    fontSize = 16,
                    fontSizeUnit = "px"
                )
            ),
            spacingTheme = Theme.SpacingTheme()
        )
    }
    
    /**
     * Common test event handlers
     */
    object EventHandlers {
        val NOOP: () -> Unit = { }
        val LOGGING: () -> Unit = { println("Event triggered") }
        val THROWING: () -> Unit = { throw RuntimeException("Test exception") }
        
        var clickCount = 0
        val COUNTING: () -> Unit = { clickCount++ }
        
        fun resetCounters() {
            clickCount = 0
        }
    }
    
    /**
     * Common test IDs and keys
     */
    object Ids {
        const val TEST_ID = "test-id"
        const val COMPONENT_ID = "component-123"
        const val USER_ID = "user-456"
        const val SESSION_ID = "session-789"
        
        val ALL = listOf(TEST_ID, COMPONENT_ID, USER_ID, SESSION_ID)
    }
    
    /**
     * Test data for form inputs
     */
    object FormInputs {
        const val VALID_EMAIL = "test@example.com"
        const val INVALID_EMAIL = "not-an-email"
        const val VALID_PHONE = "123-456-7890"
        const val INVALID_PHONE = "123456"
        const val VALID_PASSWORD = "SecurePass123!"
        const val WEAK_PASSWORD = "123"
        
        val VALID_INPUTS = mapOf(
            "email" to VALID_EMAIL,
            "phone" to VALID_PHONE,
            "password" to VALID_PASSWORD
        )
        
        val INVALID_INPUTS = mapOf(
            "email" to INVALID_EMAIL,
            "phone" to INVALID_PHONE,
            "password" to WEAK_PASSWORD
        )
    }
}