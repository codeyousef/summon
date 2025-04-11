# Internationalization API Reference

This document provides detailed API reference for the internationalization framework in Summon.

## Core Classes and Objects

### `Language`

```kotlin
data class Language(
    val code: String,
    val name: String,
    val direction: LayoutDirection
)
```

Represents a supported language in the application.

**Properties:**
- `code`: ISO language code (e.g., "en", "fr")
- `name`: Display name of the language
- `direction`: Text direction for this language

### `LayoutDirection`

```kotlin
enum class LayoutDirection {
    LTR, RTL
}
```

Enum representing text and UI element direction.

**Values:**
- `LTR`: Left-to-right (default for most Western languages)
- `RTL`: Right-to-left (for languages like Arabic, Hebrew)

### `I18nConfig`

```kotlin
object I18nConfig
```

Configuration object for internationalization settings.

**Properties:**
- `supportedLanguages`: List of supported languages
- `defaultLanguage`: The default language (nullable)

**Methods:**
- `configure(block: I18nConfigBuilder.() -> Unit)`: Configure i18n settings using the DSL builder

### `I18nConfigBuilder`

```kotlin
class I18nConfigBuilder
```

Builder class for i18n configuration DSL.

**Methods:**
- `language(code: String, name: String, direction: LayoutDirection = LayoutDirection.LTR)`: Add a supported language
- `setDefault(code: String)`: Set the default language by language code

### `StringResources`

```kotlin
object StringResources
```

Manager for string translation resources.

**Methods:**
- `loadTranslations(languageCode: String, jsonContent: String)`: Load translations for a specific language
- `getString(key: String, languageCode: String, fallbackLanguageCode: String? = null)`: Get a translated string
- `clearTranslations()`: Clear all loaded translations
- `getKeysForLanguage(languageCode: String)`: Get all keys for a specific language

## Composition Locals

```kotlin
val LocalLanguage: CompositionLocal<Language>
val LocalLayoutDirection: CompositionLocal<LayoutDirection>
```

Composition locals that provide the current language and layout direction in the composition.

## Composable Functions

### `LanguageProvider`

```kotlin
@Composable
fun LanguageProvider(
    initialLanguage: Language = I18nConfig.defaultLanguage ?: Language("en", "English", LayoutDirection.LTR),
    content: @Composable () -> Unit
)
```

Provides language context to the application.

**Parameters:**
- `initialLanguage`: The initial language to use (defaults to the configured default language)
- `content`: The content to be provided with language context

### `stringResource`

```kotlin
@Composable
fun stringResource(key: String): String
```

Utility function to get a localized string.

**Parameters:**
- `key`: The translation key (e.g., "common.welcome")

**Returns:**
- The translated string for the current language

## Directional Modifiers

### Padding and Margin

```kotlin
@Composable
fun Modifier.paddingStart(value: String): Modifier
@Composable
fun Modifier.paddingEnd(value: String): Modifier
@Composable
fun Modifier.marginStart(value: String): Modifier
@Composable
fun Modifier.marginEnd(value: String): Modifier
```

Creates padding/margin that adapts to the current layout direction.

**Parameters:**
- `value`: The padding/margin value (e.g., "8px")

### Border

```kotlin
@Composable
fun Modifier.borderStart(width: String, style: String, color: String): Modifier
@Composable
fun Modifier.borderEnd(width: String, style: String, color: String): Modifier
```

Adds a border to the start/end edge of an element.

**Parameters:**
- `width`: Border width
- `style`: Border style
- `color`: Border color

### Text Alignment

```kotlin
@Composable
fun Modifier.textStart(): Modifier
@Composable
fun Modifier.textEnd(): Modifier
```

Set the text alignment to the start/end edge.

### Layout Direction

```kotlin
@Composable
fun Modifier.flexRow(): Modifier
@Composable
fun Modifier.directionalRow(): Modifier
```

Adds flexbox direction that respects the current layout direction.

### Mirroring

```kotlin
@Composable
fun Modifier.mirrorInRtl(): Modifier
```

Applies CSS transform to mirror an element in RTL mode.

### Direction Attribute

```kotlin
@Composable
fun Modifier.withDirection(): Modifier
```

Applies the direction attribute to an element.

### Directional Padding and Margin

```kotlin
@Composable
fun Modifier.directionalPadding(
    start: String,
    top: String,
    end: String,
    bottom: String
): Modifier

@Composable
fun Modifier.directionalMargin(
    start: String,
    top: String,
    end: String,
    bottom: String
): Modifier
```

Extension functions to apply directional padding/margin to all sides.

## Utility Functions

### `changeLanguage`

```kotlin
fun changeLanguage(languageCode: String): Boolean
```

Change the application language.

**Parameters:**
- `languageCode`: The language code to switch to

**Returns:**
- `true` if the language was changed, `false` if not found

### `RtlUtils`

```kotlin
object RtlUtils
```

Utility class for RTL layout support.

**Methods:**
- `isRtl(): Boolean`: Check if the current layout direction is RTL
- `directionalValue(ltrValue: T, rtlValue: T): T`: Mirror a set of values based on the current layout direction

## Platform-Specific Implementations

### JavaScript

```kotlin
object JsI18nImplementation
```

JavaScript-specific implementation for loading i18n translation files.

**Methods:**
- `init()`: Initialize i18n for the JS platform
- `loadLanguageResources(basePath: String = "/i18n/")`: Load language resources from the specified base path
- `getCurrentLanguage(): Language`: Get the current language for JS platform 