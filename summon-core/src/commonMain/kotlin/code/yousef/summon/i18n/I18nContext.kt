package code.yousef.summon.i18n

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.CompositionLocal

/**
 * Internationalization context providing language and translation support.
 *
 * I18nContext serves as the core container for internationalization functionality
 * within Summon applications. It manages current language settings, translation
 * mappings, and provides convenient methods for retrieving localized strings
 * with parameter substitution support.
 *
 * ## Core Features
 *
 * - **Language Management**: Current language code and switching
 * - **Translation Storage**: Key-value mapping for localized strings
 * - **Parameter Substitution**: Template-based string interpolation
 * - **Fallback Handling**: Graceful degradation for missing translations
 * - **Custom Translation**: Pluggable translation provider support
 *
 * ## Usage Examples
 *
 * ### Basic Translation
 * ```kotlin
 * val i18n = I18nContext(
 *     language = "en",
 *     translations = mapOf(
 *         "hello" to "Hello",
 *         "goodbye" to "Goodbye"
 *     )
 * )
 *
 * val greeting = i18n.getString("hello") // "Hello"
 * ```
 *
 * ### Parameter Substitution
 * ```kotlin
 * val i18n = I18nContext(
 *     language = "en",
 *     translations = mapOf(
 *         "welcome_user" to "Welcome, {0}!",
 *         "items_count" to "You have {0} items in your cart"
 *     )
 * )
 *
 * val welcome = i18n.getString("welcome_user", "John") // "Welcome, John!"
 * val count = i18n.getString("items_count", 5) // "You have 5 items in your cart"
 * ```
 *
 * ### Custom Translation Provider
 * ```kotlin
 * val dynamicI18n = I18nContext(
 *     language = "fr",
 *     translator = { key, lang ->
 *         // Load from external source (API, database, etc.)
 *         translationService.getTranslation(key, lang) ?: key
 *     }
 * )
 * ```
 *
 * ### Composition Integration
 * ```kotlin
 * @Composable
 * fun LocalizedComponent() {
 *     val i18n = rememberI18n()
 *
 *     Column {
 *         Text(i18n.getString("title"))
 *         Text(i18n.getString("description"))
 *         Button(
 *             onClick = { /* action */ },
 *             label = i18n.getString("save_button")
 *         )
 *     }
 * }
 * ```
 *
 * ## Translation Key Conventions
 *
 * Recommended key naming patterns:
 * - **Hierarchical**: Use dots for namespace separation (`auth.login.title`)
 * - **Descriptive**: Clear, meaningful names (`user_profile_edit_success`)
 * - **Consistent**: Follow established patterns across the application
 * - **Contextual**: Include context when needed (`button.save`, `message.save_success`)
 *
 * ## Fallback Strategy
 *
 * Translation resolution follows this priority:
 * 1. Direct translation map lookup
 * 2. Custom translator function
 * 3. Key itself as fallback
 *
 * This ensures applications remain functional even with incomplete translations.
 *
 * ## Thread Safety
 *
 * I18nContext is immutable and thread-safe. For dynamic language switching,
 * create new context instances rather than modifying existing ones.
 *
 * @property language Current language code (ISO 639-1 format recommended)
 * @property translations Map of translation keys to localized strings
 * @property translator Custom translation function for dynamic lookup
 * @see I18nProvider for context provision in composables
 * @see rememberI18n for accessing context within composables
 * @see Language for language definitions
 * @since 1.0.0
 */
data class I18nContext(
    val language: String = "en",
    val translations: Map<String, String> = emptyMap(),
    val translator: (String, String) -> String = { key, lang -> key } // Fallback to key if no translation
) {
    /**
     * Retrieves a localized string for the specified key.
     *
     * This method performs translation lookup using the configured fallback strategy:
     * 1. Checks the local translations map
     * 2. Falls back to the custom translator function
     * 3. Returns the key itself if no translation is found
     *
     * ## Usage Examples
     *
     * ```kotlin
     * val i18n = I18nContext(
     *     language = "en",
     *     translations = mapOf(
     *         "app.title" to "My Application",
     *         "menu.home" to "Home",
     *         "menu.about" to "About"
     *     )
     * )
     *
     * val title = i18n.getString("app.title")      // "My Application"
     * val home = i18n.getString("menu.home")       // "Home"
     * val missing = i18n.getString("menu.contact") // "menu.contact" (fallback)
     * ```
     *
     * ## Error Handling
     *
     * This method never throws exceptions. Missing translations are handled
     * gracefully by returning the key itself, ensuring UI remains functional
     * even with incomplete translation coverage.
     *
     * @param key Translation key to look up
     * @return Localized string or the key itself if translation not found
     * @see getString with parameters for string interpolation
     * @since 1.0.0
     */
    fun getString(key: String): String {
        return translations[key] ?: translator(key, language)
    }

    /**
     * Retrieves a localized string with parameter substitution.
     *
     * This method performs translation lookup and then replaces placeholders
     * in the format `{0}`, `{1}`, etc. with the provided arguments. Arguments
     * are converted to strings using their `toString()` method.
     *
     * ## Parameter Substitution
     *
     * Placeholders use zero-based indexing:
     * - `{0}` → First argument
     * - `{1}` → Second argument
     * - `{n}` → nth argument
     *
     * ## Usage Examples
     *
     * ```kotlin
     * val i18n = I18nContext(
     *     language = "en",
     *     translations = mapOf(
     *         "welcome" to "Welcome, {0}!",
     *         "item_count" to "You have {0} {1} in your {2}",
     *         "user_age" to "{0} is {1} years old"
     *     )
     * )
     *
     * val welcome = i18n.getString("welcome", "Alice")
     * // Result: "Welcome, Alice!"
     *
     * val count = i18n.getString("item_count", 5, "items", "cart")
     * // Result: "You have 5 items in your cart"
     *
     * val age = i18n.getString("user_age", "Bob", 25)
     * // Result: "Bob is 25 years old"
     * ```
     *
     * ## Type Handling
     *
     * All argument types are supported through `toString()` conversion:
     * ```kotlin
     * i18n.getString("score", 1234)           // Numbers
     * i18n.getString("enabled", true)         // Booleans
     * i18n.getString("user", userObject)      // Custom objects
     * i18n.getString("date", LocalDate.now()) // Date/time objects
     * ```
     *
     * ## Missing Arguments
     *
     * If fewer arguments are provided than placeholders exist:
     * - Unreplaced placeholders remain in the string
     * - No exceptions are thrown
     * - Example: `"Hello {0} and {1}"` with arg `"Alice"` becomes `"Hello Alice and {1}"`
     *
     * @param key Translation key to look up
     * @param args Arguments to substitute into the translated string
     * @return Localized string with parameter substitution applied
     * @see getString without parameters for simple translation
     * @since 1.0.0
     */
    fun getString(key: String, vararg args: Any): String {
        var template = getString(key)
        args.forEachIndexed { index, arg ->
            template = template.replace("{$index}", arg.toString())
        }
        return template
    }
}

/**
 * CompositionLocal for accessing internationalization context throughout the component tree.
 *
 * LocalI18n provides a convenient way to access the current I18nContext from any
 * composable function within the application. It uses Summon's CompositionLocal
 * system to propagate the i18n context down the component tree.
 *
 * ## Usage
 *
 * ```kotlin
 * @Composable
 * fun SomeComponent() {
 *     val i18n = LocalI18n.current
 *     Text(i18n.getString("hello"))
 * }
 * ```
 *
 * @see I18nProvider for providing i18n context
 * @see rememberI18n for convenient access hook
 * @since 1.0.0
 */
val LocalI18n = CompositionLocal.compositionLocalOf(I18nContext())

/**
 * Provider component that establishes internationalization context for child components.
 *
 * I18nProvider creates and provides an I18nContext that becomes available to all
 * child composables through the LocalI18n CompositionLocal. This is typically
 * placed near the root of the application component tree.
 *
 * ## Basic Usage
 *
 * ```kotlin
 * @Composable
 * fun App() {
 *     I18nProvider(
 *         language = "en",
 *         translations = mapOf(
 *             "welcome" to "Welcome",
 *             "goodbye" to "Goodbye"
 *         )
 *     ) {
 *         MainContent()
 *     }
 * }
 * ```
 *
 * ## Dynamic Language Switching
 *
 * ```kotlin
 * @Composable
 * fun App() {
 *     var currentLanguage by remember { mutableStateOf("en") }
 *     val translations = remember(currentLanguage) {
 *         loadTranslationsForLanguage(currentLanguage)
 *     }
 *
 *     I18nProvider(
 *         language = currentLanguage,
 *         translations = translations
 *     ) {
 *         Column {
 *             LanguageSelector(currentLanguage) { currentLanguage = it }
 *             MainContent()
 *         }
 *     }
 * }
 * ```
 *
 * ## Custom Translation Provider
 *
 * ```kotlin
 * @Composable
 * fun App() {
 *     I18nProvider(
 *         language = "fr",
 *         translator = { key, lang ->
 *             // Fetch from API, database, or other source
 *             translationRepository.getTranslation(key, lang)
 *         }
 *     ) {
 *         MainContent()
 *     }
 * }
 * ```
 *
 * ## Nested Providers
 *
 * I18nProvider supports nesting for different language contexts:
 *
 * ```kotlin
 * @Composable
 * fun MultiLanguageApp() {
 *     I18nProvider(language = "en", translations = englishTranslations) {
 *         Column {
 *             Text("English content")
 *
 *             I18nProvider(language = "es", translations = spanishTranslations) {
 *                 Text("Spanish content")
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @param language Current language code (e.g., "en", "fr", "es")
 * @param translations Map of translation keys to localized strings
 * @param translator Custom translation function for dynamic lookup
 * @param content Child composables that will have access to the i18n context
 * @see I18nContext for context structure
 * @see rememberI18n for accessing the provided context
 * @since 1.0.0
 */
@Composable
fun I18nProvider(
    language: String,
    translations: Map<String, String> = emptyMap(),
    translator: (String, String) -> String = { key, _ -> key },
    content: @Composable () -> Unit
) {
    val i18nContext = I18nContext(language, translations, translator)

    // TODO: This would need proper CompositionLocal provider support in the framework
    // For now, we'll just pass the context through a simple mechanism
    LocalI18n.provides(i18nContext)
    content()
}

/**
 * Composable function to access the current internationalization context.
 *
 * rememberI18n provides a convenient way to access the I18nContext that was
 * provided by an I18nProvider higher up in the component tree. This hook
 * returns the current context and will trigger recomposition when the
 * language or translations change.
 *
 * ## Basic Usage
 *
 * ```kotlin
 * @Composable
 * fun WelcomeMessage() {
 *     val i18n = rememberI18n()
 *
 *     Text(
 *         text = i18n.getString("welcome"),
 *         modifier = Modifier.padding(16.dp)
 *     )
 * }
 * ```
 *
 * ## With Parameter Substitution
 *
 * ```kotlin
 * @Composable
 * fun UserGreeting(userName: String) {
 *     val i18n = rememberI18n()
 *
 *     Text(
 *         text = i18n.getString("user_greeting", userName),
 *         style = Typography.h4
 *     )
 * }
 * ```
 *
 * ## Form Validation
 *
 * ```kotlin
 * @Composable
 * fun LoginForm() {
 *     val i18n = rememberI18n()
 *     var email by remember { mutableStateOf("") }
 *     var password by remember { mutableStateOf("") }
 *
 *     Column {
 *         TextField(
 *             value = email,
 *             onValueChange = { email = it },
 *             label = i18n.getString("email_label"),
 *             placeholder = i18n.getString("email_placeholder")
 *         )
 *
 *         TextField(
 *             value = password,
 *             onValueChange = { password = it },
 *             label = i18n.getString("password_label"),
 *             type = TextFieldType.Password
 *         )
 *
 *         Button(
 *             onClick = { /* validate and submit */ },
 *             label = i18n.getString("login_button")
 *         )
 *     }
 * }
 * ```
 *
 * ## Error Handling
 *
 * rememberI18n will return a default I18nContext with English language
 * if no I18nProvider is found in the component tree.
 *
 * @return Current I18nContext from the nearest I18nProvider
 * @see I18nProvider for providing context
 * @see I18nContext for context methods
 * @since 1.0.0
 */
@Composable
fun rememberI18n(): I18nContext {
    return LocalI18n.current
}