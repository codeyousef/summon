package code.yousef.example.springboot.pages

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.core.ThemeProvider
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.example.springboot.i18n.Translations
import code.yousef.example.springboot.models.Language
import code.yousef.example.springboot.models.Theme
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.background
import code.yousef.summon.modifier.border
import code.yousef.summon.modifier.borderRadius
import code.yousef.summon.modifier.color
import code.yousef.summon.modifier.fontSize
import code.yousef.summon.modifier.fontWeight
import code.yousef.summon.modifier.margin
import code.yousef.summon.modifier.maxWidth
import code.yousef.summon.modifier.padding
import code.yousef.summon.modifier.textAlign
import code.yousef.summon.modifier.width
import code.yousef.summon.theme.LocalColors
import code.yousef.summon.theme.MaterialColors

@Composable
fun AuthPage(
    language: Language = Language.ENGLISH,
    theme: Theme = Theme.LIGHT,
    isLogin: Boolean = true
) {
    val colors = if (theme == Theme.DARK) MaterialColors.darkColors() else MaterialColors.lightColors()
    
    ThemeProvider(colors = colors) {
        Box(
            modifier = Modifier
                .background(LocalColors.current.background)
                .padding("20px")
                .textAlign("center")
        ) {
            Column(
                modifier = Modifier
                    .maxWidth("400px")
                    .margin("0 auto")
                    .padding("40px")
                    .background(LocalColors.current.surface)
                    .borderRadius("12px")
                    .border("1px solid ${LocalColors.current.outline}")
            ) {
                // App Title
                Text(
                    text = Translations.get("app.title", language),
                    modifier = Modifier
                        .fontSize("28px")
                        .fontWeight("bold")
                        .color(LocalColors.current.onSurface)
                        .margin("0 0 8px 0")
                )
                
                Text(
                    text = Translations.get("app.subtitle", language),
                    modifier = Modifier
                        .fontSize("16px")
                        .color(LocalColors.current.onSurfaceVariant)
                        .margin("0 0 32px 0")
                )
                
                // Auth Form Title
                Text(
                    text = if (isLogin) 
                        Translations.get("auth.login", language) 
                    else 
                        Translations.get("auth.register", language),
                    modifier = Modifier
                        .fontSize("24px")
                        .fontWeight("600")
                        .color(LocalColors.current.onSurface)
                        .margin("0 0 24px 0")
                )
                
                // Form Fields
                if (!isLogin) {
                    TextField(
                        placeholder = Translations.get("auth.email", language),
                        name = "email",
                        type = "email",
                        modifier = Modifier
                            .width("100%")
                            .margin("0 0 16px 0")
                            .padding("12px")
                            .borderRadius("8px")
                            .border("1px solid ${LocalColors.current.outline}")
                            .background(LocalColors.current.background)
                            .color(LocalColors.current.onBackground)
                    )
                }
                
                TextField(
                    placeholder = Translations.get("auth.username", language),
                    name = "username",
                    modifier = Modifier
                        .width("100%")
                        .margin("0 0 16px 0")
                        .padding("12px")
                        .borderRadius("8px")
                        .border("1px solid ${LocalColors.current.outline}")
                        .background(LocalColors.current.background)
                        .color(LocalColors.current.onBackground)
                )
                
                TextField(
                    placeholder = Translations.get("auth.password", language),
                    name = "password",
                    type = "password",
                    modifier = Modifier
                        .width("100%")
                        .margin("0 0 24px 0")
                        .padding("12px")
                        .borderRadius("8px")
                        .border("1px solid ${LocalColors.current.outline}")
                        .background(LocalColors.current.background)
                        .color(LocalColors.current.onBackground)
                )
                
                // Submit Button
                Button(
                    text = if (isLogin) 
                        Translations.get("auth.login", language) 
                    else 
                        Translations.get("auth.register", language),
                    modifier = Modifier
                        .width("100%")
                        .padding("12px 24px")
                        .borderRadius("8px")
                        .background(LocalColors.current.primary)
                        .color(LocalColors.current.onPrimary)
                        .fontWeight("600")
                        .margin("0 0 16px 0"),
                    onClick = "submitAuth()"
                )
                
                // Toggle between login/register
                Row(
                    modifier = Modifier
                        .margin("16px 0 0 0")
                        .textAlign("center")
                ) {
                    Text(
                        text = if (isLogin) "Don't have an account? " else "Already have an account? ",
                        modifier = Modifier
                            .color(LocalColors.current.onSurfaceVariant)
                            .fontSize("14px")
                    )
                    
                    Button(
                        text = if (isLogin) 
                            Translations.get("auth.register", language) 
                        else 
                            Translations.get("auth.login", language),
                        modifier = Modifier
                            .color(LocalColors.current.primary)
                            .fontSize("14px")
                            .fontWeight("600")
                            .background("transparent")
                            .border("none")
                            .padding("0"),
                        onClick = "toggleAuthMode()"
                    )
                }
                
                // JWT info
                Text(
                    text = Translations.get("todo.jwt_auth", language),
                    modifier = Modifier
                        .fontSize("12px")
                        .color(LocalColors.current.primary)
                        .margin("24px 0 0 0")
                        .textAlign("center")
                )
                
                // Powered by
                Text(
                    text = Translations.get("app.powered_by", language),
                    modifier = Modifier
                        .fontSize("12px")
                        .color(LocalColors.current.onSurfaceVariant)
                        .margin("16px 0 0 0")
                        .textAlign("center")
                )
            }
        }
    }
}