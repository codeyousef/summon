package code.yousef.summon.examples.js.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.examples.js.i18n.Translations
import code.yousef.summon.examples.js.state.AppState
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf
import kotlinx.browser.window
import org.w3c.dom.events.KeyboardEvent

/**
 * Login form component demonstrating form handling, validation, and reactive UI
 */
@Composable
fun LoginForm(appState: AppState) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val currentLanguage = appState.language.value
    
    // Center the login form on the page
    Column(
        modifier = Modifier()
            .style("min-height", "100vh")
            .style("display", "flex")
            .style("align-items", "center")
            .style("justify-content", "center")
            .style("padding", "20px")
            .style("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
    ) {
        Card(
            modifier = Modifier()
                .style("max-width", "400px")
                .style("width", "100%")
                .style("padding", "32px")
                .style("border-radius", "16px")
                .style("box-shadow", "0 10px 25px rgba(0,0,0,0.1)")
                .style("background-color", "rgba(255,255,255,0.95)")
                .style("backdrop-filter", "blur(10px)")
        ) {
            Column(
                modifier = Modifier()
                    .style("gap", "24px")
            ) {
                // App Title
                Column(
                    modifier = Modifier()
                        .style("text-align", "center")
                        .style("gap", "8px")
                ) {
                    Text(
                        text = Translations.getString("app.title", currentLanguage),
                        modifier = Modifier()
                            .style("font-size", "28px")
                            .style("font-weight", "700")
                            .style("color", "#2d3748")
                            .style("margin-bottom", "0")
                    )
                    
                    Text(
                        text = Translations.getString("app.subtitle", currentLanguage),
                        modifier = Modifier()
                            .style("font-size", "14px")
                            .style("color", "#718096")
                            .style("line-height", "1.5")
                    )
                }
                
                // Welcome Message
                Text(
                    text = Translations.getString("auth.welcome.back", currentLanguage),
                    modifier = Modifier()
                        .style("font-size", "18px")
                        .style("font-weight", "600")
                        .style("color", "#4a5568")
                        .style("text-align", "center")
                )
                
                // Demo Hint
                Text(
                    text = Translations.getString("auth.demo.hint", currentLanguage),
                    modifier = Modifier()
                        .style("font-size", "12px")
                        .style("color", "#a0aec0")
                        .style("text-align", "center")
                        .style("padding", "8px")
                        .style("background-color", "#f7fafc")
                        .style("border-radius", "6px")
                        .style("border-left", "3px solid #4299e1")
                )
                
                // Login Form
                Column(
                    modifier = Modifier()
                        .style("gap", "16px")
                ) {
                    // Username Field
                    TextField(
                        value = username.value,
                        onValueChange = { username.value = it },
                        placeholder = Translations.getString("auth.username.placeholder", currentLanguage),
                        modifier = Modifier()
                            .style("width", "100%")
                            .style("padding", "12px 16px")
                            .style("border", "2px solid #e2e8f0")
                            .style("border-radius", "8px")
                            .style("font-size", "16px")
                            .style("transition", "border-color 0.2s")
                            .style("background-color", "#ffffff")
                            .attribute("data-hover-styles", "border-color: #4299e1")
                    )
                    
                    // Password Field
                    TextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        placeholder = Translations.getString("auth.password.placeholder", currentLanguage),
                        modifier = Modifier()
                            .style("width", "100%")
                            .style("padding", "12px 16px")
                            .style("border", "2px solid #e2e8f0")
                            .style("border-radius", "8px")
                            .style("font-size", "16px")
                            .style("transition", "border-color 0.2s")
                            .style("background-color", "#ffffff")
                            .attribute("type", "password")
                            .attribute("data-hover-styles", "border-color: #4299e1")
                    )
                    
                    // Error Message
                    appState.loginError.value?.let { error ->
                        Text(
                            text = error,
                            modifier = Modifier()
                                .style("color", "#e53e3e")
                                .style("font-size", "14px")
                                .style("text-align", "center")
                                .style("padding", "8px")
                                .style("background-color", "#fed7d7")
                                .style("border-radius", "6px")
                                .style("border", "1px solid #feb2b2")
                        )
                    }
                    
                    // Login Button
                    if (appState.isLoading.value) {
                        Text(
                            text = "Loading...",
                            modifier = Modifier()
                                .style("text-align", "center")
                                .style("padding", "12px")
                                .style("color", "var(--primary-color)")
                                .style("font-weight", "500")
                        )
                    } else {
                        Button(
                            onClick = { 
                                appState.login(username.value, password.value)
                            },
                            label = Translations.getString("auth.login.button", currentLanguage),
                            variant = ButtonVariant.PRIMARY,
                            modifier = Modifier()
                                .style("width", "100%")
                                .style("padding", "12px")
                                .style("font-size", "16px")
                                .style("font-weight", "600")
                                .style("border-radius", "8px")
                                .style("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                                .style("color", "white")
                                .style("border", "none")
                                .style("cursor", "pointer")
                                .style("transition", "transform 0.2s, box-shadow 0.2s")
                                .attribute("data-hover-styles", "transform: translateY(-1px); box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4)")
                        )
                    }
                }
                
                // Sample Credentials
                Column(
                    modifier = Modifier()
                        .style("margin-top", "16px")
                        .style("padding", "16px")
                        .style("background-color", "#f0fff4")
                        .style("border-radius", "8px")
                        .style("border", "1px solid #9ae6b4")
                        .style("gap", "12px")
                ) {
                    Text(
                        text = "💡 Try these sample credentials:",
                        modifier = Modifier()
                            .style("font-size", "12px")
                            .style("font-weight", "600")
                            .style("color", "#2f855a")
                            .style("margin-bottom", "8px")
                    )
                    
                    // Demo credentials button
                    Button(
                        onClick = { 
                            println("Demo button clicked - setting username and password")
                            username.value = "demo"
                            password.value = "demo123"
                            println("State updated - username: ${username.value}, password: ${password.value}")
                        },
                        label = "Use Demo Account (demo/demo123)",
                        variant = ButtonVariant.SECONDARY,
                        modifier = Modifier()
                            .style("width", "100%")
                            .style("padding", "8px 12px")
                            .style("font-size", "11px")
                            .style("background-color", "#f7fafc")
                            .style("color", "#38a169")
                            .style("border", "1px solid #9ae6b4")
                            .style("border-radius", "6px")
                            .style("cursor", "pointer")
                            .style("font-family", "monospace")
                            .style("transition", "all 0.2s ease")
                            .attribute("data-hover-styles", "background-color: #edf2f7; transform: translateY(-1px)")
                    )
                    
                    // Alice credentials button
                    Button(
                        onClick = { 
                            println("Alice button clicked - setting username and password")
                            username.value = "alice"
                            password.value = "alice456"
                            println("State updated - username: ${username.value}, password: ${password.value}")
                        },
                        label = "Use Alice Account (alice/alice456)",
                        variant = ButtonVariant.SECONDARY,
                        modifier = Modifier()
                            .style("width", "100%")
                            .style("padding", "8px 12px")
                            .style("font-size", "11px")
                            .style("background-color", "#f7fafc")
                            .style("color", "#38a169")
                            .style("border", "1px solid #9ae6b4")
                            .style("border-radius", "6px")
                            .style("cursor", "pointer")
                            .style("font-family", "monospace")
                            .style("transition", "all 0.2s ease")
                            .attribute("data-hover-styles", "background-color: #edf2f7; transform: translateY(-1px)")
                    )
                }
            }
        }
    }
}