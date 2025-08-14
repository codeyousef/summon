package code.yousef.summon.i18n

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.layout.Div
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * Demonstration component showing i18n capabilities with full integration with Summon components
 */
@Composable
fun I18nDemo() {
    // Get current language and layout direction
    val languageProvider = LocalLanguage.current
    val layoutDirection = LocalLayoutDirection.current

    // Get the actual values
    val currentLanguage = (languageProvider as Function0<Language>).invoke()
    val actualDirection = (layoutDirection as Function0<LayoutDirection>).invoke()

    val isRtl = actualDirection == LayoutDirection.RTL

    // Create directional modifier based on layout direction
    val containerModifier = Modifier()
        .width("100%")
        .padding("16px")
        .style("direction", if (isRtl) "rtl" else "ltr")
        .style("text-align", if (isRtl) "right" else "left")

    // Get localized welcome message
    val welcomeText = StringResources.getString("common.welcome", currentLanguage.code)
    val directionText = if (isRtl) "RTL" else "LTR"
    val languageText = "${currentLanguage.name} (${currentLanguage.code})"

    // Create a container with the appropriate direction
    Div(modifier = containerModifier) {
        // Title with current language and direction
        Text(
            text = "I18n Demo",
            modifier = Modifier()
                .fontSize("24px")
                .fontWeight("bold")
                .margin("0 0 16px 0")
        )

        // Welcome message in the current language
        Text(
            text = welcomeText,
            modifier = Modifier()
                .fontSize("18px")
                .margin("0 0 8px 0")
        )

        // Display current language and direction
        Text(
            text = "Current Language: $languageText",
            modifier = Modifier().margin("0 0 4px 0")
        )

        Text(
            text = "Text Direction: $directionText",
            modifier = Modifier().margin("0 0 16px 0")
        )

        // Add language selector
        LanguageSelector()
    }
}

/**
 * Language selector component that displays buttons for each supported language
 */
@Composable
private fun LanguageSelector() {
    // Get current language
    val languageProvider = LocalLanguage.current
    val currentLanguage = (languageProvider as Function0<Language>).invoke()

    // Create a container for the language buttons
    Div(modifier = Modifier().margin("8px 0")) {
        // Title for the language selector
        Text(
            text = "Select Language:",
            modifier = Modifier()
                .fontWeight("bold")
                .margin("0 0 8px 0")
        )

        // Create a horizontal row of language buttons
        Div(modifier = Modifier().style("display", "flex").style("gap", "8px")) {
            // Create a button for each supported language
            for (language in I18nConfig.supportedLanguages) {
                // Determine if this is the current language
                val isCurrentLanguage = language.code == currentLanguage.code

                // Create a button with appropriate styling
                Button(
                    onClick = {
                        // Change the language when clicked
                        I18nConfig.changeLanguage(language.code)
                    },
                    label = language.name,
                    variant = if (isCurrentLanguage) ButtonVariant.PRIMARY else ButtonVariant.SECONDARY,
                    modifier = Modifier()
                        .margin("4px")
                        .style("min-width", "100px")
                )
            }
        }
    }
}
