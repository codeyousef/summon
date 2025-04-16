# Internationalization Implementation Example

This document provides a complete implementation example of internationalization in a Summon application.

## Project Setup

First, ensure your i18n folder structure is set up:

```
src/jsMain/resources/i18n/
  en.json
  ar.json
  fr.json
  ...
```

## Translation Files

Create translation files for each language:

### src/jsMain/resources/i18n/en.json

```json
{
  "common": {
    "welcome": "Welcome to our application",
    "language": "Language",
    "back": "Back",
    "next": "Next"
  },
  "nav": {
    "home": "Home",
    "about": "About Us",
    "contact": "Contact"
  },
  "home": {
    "title": "Welcome",
    "subtitle": "Explore our offerings",
    "cta": "Get Started"
  }
}
```

### src/jsMain/resources/i18n/ar.json

```json
{
  "common": {
    "welcome": "مرحبًا بكم في تطبيقنا",
    "language": "اللغة",
    "back": "رجوع",
    "next": "التالي"
  },
  "nav": {
    "home": "الرئيسية",
    "about": "من نحن",
    "contact": "اتصل بنا"
  },
  "home": {
    "title": "مرحباً",
    "subtitle": "استكشف خدماتنا",
    "cta": "ابدأ الآن"
  }
}
```

## Application Initialization

Configure i18n in your application's main entry point:

```kotlin
// src/jsMain/kotlin/code/yousef/myapp/Main.kt

import code.yousef.summon.i18n.I18nConfig
import code.yousef.summon.i18n.JsI18nImplementation
import code.yousef.summon.i18n.LayoutDirection
import kotlinx.browser.document

fun main() {
    // Configure supported languages
    I18nConfig.configure {
        language("en", "English")
        language("ar", "العربية", LayoutDirection.RTL)
        language("fr", "Français")
        
        // Set default language
        setDefault("en")
    }
    
    // Initialize i18n system
    JsI18nImplementation.init()
    
    // Load translations from resources
    JsI18nImplementation.loadLanguageResources("/i18n/")
    
    // Set up the application UI after i18n initialization
    document.addEventListener("DOMContentLoaded") {
        setupApp()
    }
}

private fun setupApp() {
    // Application setup code
    // ...
}
```

## Creating a Root Component with Language Switching

Create a root component that includes language selection:

```kotlin
// src/commonMain/kotlin/code/yousef/myapp/App.kt

import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.navigation.Button
import code.yousef.summon.components.display.Text
import code.yousef.summon.i18n.*
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

@Composable
fun App() {
    // Provide language context to the entire application
    LanguageProvider {
        AppContent()
    }
}

@Composable
private fun AppContent() {
    // Get current language
    val currentLanguage = LocalLanguage.current
    
    Column(
        modifier = Modifier
            .padding("20px")
            .withDirection() // Apply direction attribute
    ) {
        // Language selector
        LanguageSelector(currentLanguage)
        
        // Main content
        MainContent()
    }
}

@Composable
private fun LanguageSelector(currentLanguage: Language) {
    Column(Modifier.padding("0 0 20px 0")) {
        Text(
            stringResource("common.language"),
            modifier = Modifier.padding("0 0 10px 0")
        )
        
        Row(Modifier.directionalRow()) {
            I18nConfig.supportedLanguages.forEach { language ->
                Button(
                    onClick = { changeLanguage(language.code) },
                    modifier = Modifier
                        .marginEnd("10px")
                        .padding("8px 16px")
                        .style(
                            "background-color", 
                            if (language.code == currentLanguage.code) "#007bff" else "#f8f9fa"
                        )
                        .style(
                            "color", 
                            if (language.code == currentLanguage.code) "white" else "black"
                        )
                ) {
                    Text(language.name)
                }
            }
        }
    }
}

@Composable
private fun MainContent() {
    // Direction-aware layout
    Column(
        modifier = Modifier
            .padding("20px")
            .style("border", "1px solid #eee")
            .style("border-radius", "5px")
    ) {
        // Welcome message (translated)
        Text(
            stringResource("common.welcome"),
            modifier = Modifier
                .style("font-size", "24px")
                .paddingStart("10px") // Direction-aware padding
                .paddingBottom("20px")
        )
        
        // Navigation with direction-aware layout
        NavigationBar()
        
        // Home section content
        HomeSection()
    }
}

@Composable
private fun NavigationBar() {
    Row(Modifier.directionalRow()) {
        NavItem("nav.home", "#home")
        NavItem("nav.about", "#about")
        NavItem("nav.contact", "#contact")
    }
}

@Composable
private fun NavItem(textKey: String, href: String) {
    Button(
        onClick = { /* navigate to href */ },
        modifier = Modifier
            .paddingStart("10px")
            .paddingEnd("10px")
            .padding("8px 0")
            .style("background", "transparent")
    ) {
        Text(stringResource(textKey))
    }
}

@Composable
private fun HomeSection() {
    Column(
        modifier = Modifier
            .padding("20px 0")
            .style("border-top", "1px solid #eee")
            .paddingTop("20px")
    ) {
        Text(
            stringResource("home.title"),
            modifier = Modifier
                .style("font-size", "20px")
                .style("font-weight", "bold")
                .textStart() // Direction-aware text alignment
                .paddingBottom("10px")
        )
        
        Text(
            stringResource("home.subtitle"),
            modifier = Modifier
                .style("font-size", "16px")
                .paddingBottom("10px")
        )
        
        // Directional button row
        Row(
            modifier = Modifier
                .directionalRow()
                .paddingTop("20px")
        ) {
            Button(
                onClick = { /* no-op */ },
                modifier = Modifier.marginEnd("10px")
            ) {
                Text(stringResource("common.back"))
            }
            
            Button(
                onClick = { /* no-op */ },
                modifier = Modifier
                    .marginStart("10px")
                    .style("background-color", "#007bff")
                    .style("color", "white")
            ) {
                Text(stringResource("common.next"))
            }
        }
    }
}
```

## Handling Forms with RTL Support

Forms also need to be designed with RTL support in mind:

```kotlin
@Composable
fun ContactForm() {
    Column(
        modifier = Modifier
            .padding("20px")
            .style("border", "1px solid #eee")
            .style("border-radius", "5px")
    ) {
        Text(
            stringResource("contact.title"),
            modifier = Modifier
                .style("font-size", "20px")
                .style("font-weight", "bold")
                .paddingBottom("20px")
        )
        
        // Form fields with directional layout
        FormField("contact.name")
        FormField("contact.email")
        FormField("contact.message", height = "100px")
        
        // Submit button with directional alignment
        Row(
            modifier = Modifier
                .paddingTop("20px")
                .style("justify-content", "flex-end")
        ) {
            Button(
                onClick = { /* Form submission logic */ },
                modifier = Modifier
                    .padding("8px 16px")
                    .style("background-color", "#007bff")
                    .style("color", "white")
            ) {
                Text(stringResource("contact.submit"))
            }
        }
    }
}

@Composable
private fun FormField(labelKey: String, height: String = "40px") {
    Column(Modifier.paddingBottom("15px")) {
        // Label with directional text alignment
        Text(
            stringResource(labelKey),
            modifier = Modifier
                .textStart()
                .paddingBottom("5px")
        )
        
        // Input field with directional padding
        TextInput(
            modifier = Modifier
                .style("width", "100%")
                .style("height", height)
                .paddingStart("10px") // Direction-aware padding
                .style("border", "1px solid #ddd")
                .style("border-radius", "4px")
        )
    }
}

@Composable
private fun TextInput(modifier: Modifier = Modifier()) {
    // TODO: Implement a real implementation
    // Text input implementation
    // In a real app, this would be a proper text input component
}
```

## Complete Application

The complete application combines the initialization code with the UI components above. The key points to note:

1. The application is configured with multiple languages, including RTL languages
2. All UI strings come from the translation system using `stringResource()`
3. Direction-aware modifiers are used instead of hardcoded left/right styles
4. The language can be switched at runtime
5. The HTML document direction attribute is automatically updated

This implementation provides a complete, user-friendly internationalization solution that handles both LTR and RTL languages with minimal developer effort. 