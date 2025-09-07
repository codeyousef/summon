# Summon Internationalization (i18n) Framework

A flexible, easy-to-use internationalization framework for Summon applications that supports multiple languages, including right-to-left (RTL) layouts.

## Features

- **Multiple Language Support**: Define and use any number of languages in your application
- **RTL Support**: First-class support for right-to-left languages like Arabic and Hebrew
- **Composable API**: Seamless integration with Summon's composable UI framework
- **Direction-Aware Styling**: Modifiers that automatically adapt to the current language direction
- **JSON-Based Translations**: Simple, hierarchical JSON translation files
- **Runtime Language Switching**: Change languages without page reload
- **Fallback Support**: Automatically fall back to default language for missing translations

## Getting Started

### 1. Set Up Language Configuration

Configure the languages your application will support:

```kotlin
// Configure languages in your app initialization
I18nConfig.configure {
    language("en", "English")
    language("fr", "Français") 
    language("ar", "العربية", LayoutDirection.RTL)
    
    // Set default language
    setDefault("en")
}
```

### 2. Create Translation Files

Create JSON files for each language with the same structure:

```
src/jsMain/resources/i18n/
  en.json
  fr.json
  ar.json
```

Example translation file (en.json):

```json
{
  "common": {
    "welcome": "Welcome to our application",
    "language": "Language"
  },
  "nav": {
    "home": "Home",
    "about": "About"
  }
}
```

### 3. Initialize and Load Translations

For JavaScript/browser applications:

```kotlin
// Initialize i18n system
JsI18nImplementation.init()

// Load translations from resources
JsI18nImplementation.loadLanguageResources("/i18n/")
```

### 4. Set Up Language Provider

Wrap your application content with the LanguageProvider:

```kotlin
@Composable
fun App() {
    LanguageProvider {
        // Your app content here
        MainContent()
    }
}
```

### 5. Use Translations in UI

Access translations with the `stringResource` composable function:

```kotlin
@Composable
fun WelcomeScreen() {
    Text(
        text = stringResource("common.welcome"),
        modifier = Modifier.padding("20px")
    )
}
```

### 6. Use Direction-Aware Modifiers

Replace direction-specific modifiers with direction-aware versions:

```kotlin
// Instead of paddingLeft/paddingRight
.paddingStart("10px")  // Left in LTR, Right in RTL
.paddingEnd("10px")    // Right in LTR, Left in RTL

// For directional rows that reverse in RTL
Row(Modifier.directionalRow()) {
    // Content will be reversed in RTL languages
}
```

### 7. Add Language Switching

Allow users to change languages:

```kotlin
Button(onClick = { changeLanguage("ar") }) {
    Text("العربية")
}
```

## Documentation

For more detailed documentation, please refer to these resources:

- [Internationalization Guide](i18n.md) - Complete guide to internationalization
- [API Reference](api/i18n-api.md) - Detailed API documentation

## Best Practices

1. Use direction-aware modifiers (paddingStart, marginEnd, etc.) instead of left/right-specific versions
2. Always use `stringResource()` for user-visible text
3. Test your UI in both LTR and RTL modes regularly
4. Design with text expansion in mind (translations may be longer than original text)
5. Mirror directional icons in RTL mode with the `mirrorInRtl()` modifier

## Contributing

Contributions to improve the internationalization framework are welcome. Please follow the project's contribution guidelines. 