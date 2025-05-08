package code.yousef.summon.examples.js.components

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.i18n.I18nConfig
import code.yousef.summon.i18n.Language
import code.yousef.summon.i18n.changeLanguage
import code.yousef.summon.i18n.stringResource
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * Application header component with language selector and theme toggle.
 *
 * @param currentLanguage The current language
 * @param isDarkMode Whether dark mode is enabled
 * @param onThemeToggle Callback for when the theme toggle is clicked
 */
@Composable
fun Header(
    currentLanguage: Language,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    Column(
        modifier = Modifier()
    ) {
        // App title
        Text(
            text = stringResource("app.title"),
            modifier = Modifier()
        )

        // Controls row with language selector and theme toggle
        Row(
            modifier = Modifier()
        ) {
            // Language selector
            LanguageSelector(currentLanguage)

            // Theme toggle
            ThemeToggle(isDarkMode, onThemeToggle)
        }
    }
}

/**
 * Language selector component.
 *
 * @param currentLanguage The current language
 */
@Composable
private fun LanguageSelector(currentLanguage: Language) {
    Column {
        Text(
            text = stringResource("common.language"),
            modifier = Modifier()
        )

        Row(Modifier()) {
            I18nConfig.supportedLanguages.forEach { language ->
                Button(
                    onClick = { changeLanguage(language.code) },
                    label = language.name,
                    modifier = Modifier()
                )
            }
        }
    }
}

/**
 * Theme toggle component.
 *
 * @param isDarkMode Whether dark mode is enabled
 * @param onThemeToggle Callback for when the theme toggle is clicked
 */
@Composable
private fun ThemeToggle(
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    Column(
        modifier = Modifier()
    ) {
        Text(
            text = stringResource("theme.title"),
            modifier = Modifier()
        )

        Button(
            onClick = onThemeToggle,
            label = if (isDarkMode) stringResource("theme.light") else stringResource("theme.dark"),
            modifier = Modifier()
        )
    }
}
