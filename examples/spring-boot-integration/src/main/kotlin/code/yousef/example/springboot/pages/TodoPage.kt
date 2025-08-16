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
fun TodoPage(
    language: Language = Language.ENGLISH,
    theme: Theme = Theme.LIGHT,
    username: String = ""
) {
    val colors = if (theme == Theme.DARK) MaterialColors.darkColors() else MaterialColors.lightColors()
    
    ThemeProvider(colors = colors) {
        Column(
            modifier = Modifier
                .background(LocalColors.current.background)
                .padding("0")
                .width("100%")
        ) {
            // Header
            Box(
                modifier = Modifier
                    .width("100%")
                    .background(LocalColors.current.primary)
                    .padding("16px 24px")
            ) {
                Row(
                    modifier = Modifier
                        .maxWidth("1200px")
                        .margin("0 auto")
                        .width("100%")
                ) {
                    Column(modifier = Modifier.padding("0 16px 0 0")) {
                        Text(
                            text = Translations.get("app.title", language),
                            modifier = Modifier
                                .fontSize("24px")
                                .fontWeight("bold")
                                .color(LocalColors.current.onPrimary)
                                .margin("0 0 4px 0")
                        )
                        
                        Text(
                            text = "${Translations.get("auth.welcome", language)}, $username",
                            modifier = Modifier
                                .fontSize("14px")
                                .color(LocalColors.current.onPrimary)
                        )
                    }
                    
                    Row(modifier = Modifier.margin("0 0 0 auto")) {
                        // Language Selector
                        Button(
                            text = "${Translations.get("language.select", language)}: ${language.name}",
                            modifier = Modifier
                                .margin("0 8px 0 0")
                                .padding("8px 16px")
                                .borderRadius("4px")
                                .background(LocalColors.current.surface)
                                .color(LocalColors.current.onSurface)
                                .fontSize("12px"),
                            onClick = "toggleLanguage()"
                        )
                        
                        // Theme Toggle
                        Button(
                            text = Translations.get("theme.toggle", language),
                            modifier = Modifier
                                .margin("0 8px 0 0")
                                .padding("8px 16px")
                                .borderRadius("4px")
                                .background(LocalColors.current.surface)
                                .color(LocalColors.current.onSurface)
                                .fontSize("12px"),
                            onClick = "toggleTheme()"
                        )
                        
                        // Logout
                        Button(
                            text = Translations.get("auth.logout", language),
                            modifier = Modifier
                                .padding("8px 16px")
                                .borderRadius("4px")
                                .background(LocalColors.current.error)
                                .color(LocalColors.current.onError)
                                .fontSize("12px"),
                            onClick = "logout()"
                        )
                    }
                }
            }
            
            // Main content
            Box(
                modifier = Modifier
                    .maxWidth("800px")
                    .margin("0 auto")
                    .padding("32px 24px")
                    .width("100%")
            ) {
                Column {
                    // Add todo section
                    Row(
                        modifier = Modifier
                            .width("100%")
                            .margin("0 0 32px 0")
                            .padding("20px")
                            .background(LocalColors.current.surface)
                            .borderRadius("12px")
                            .border("1px solid ${LocalColors.current.outline}")
                    ) {
                        TextField(
                            placeholder = Translations.get("todo.add_placeholder", language),
                            name = "newTodo",
                            modifier = Modifier
                                .width("100%")
                                .margin("0 12px 0 0")
                                .padding("12px")
                                .borderRadius("8px")
                                .border("1px solid ${LocalColors.current.outline}")
                                .background(LocalColors.current.background)
                                .color(LocalColors.current.onBackground)
                        )
                        
                        Button(
                            text = Translations.get("todo.add_button", language),
                            modifier = Modifier
                                .padding("12px 24px")
                                .borderRadius("8px")
                                .background(LocalColors.current.primary)
                                .color(LocalColors.current.onPrimary)
                                .fontWeight("600"),
                            onClick = "addTodo()"
                        )
                    }
                    
                    // Filter buttons
                    Row(
                        modifier = Modifier
                            .width("100%")
                            .margin("0 0 24px 0")
                            .textAlign("center")
                    ) {
                        Button(
                            text = Translations.get("filter.all", language),
                            modifier = Modifier
                                .margin("0 8px 0 0")
                                .padding("8px 16px")
                                .borderRadius("20px")
                                .background(LocalColors.current.primaryContainer)
                                .color(LocalColors.current.onPrimaryContainer)
                                .fontSize("14px"),
                            onClick = "setFilter('ALL')"
                        )
                        
                        Button(
                            text = Translations.get("filter.active", language),
                            modifier = Modifier
                                .margin("0 8px 0 0")
                                .padding("8px 16px")
                                .borderRadius("20px")
                                .background(LocalColors.current.surface)
                                .color(LocalColors.current.onSurface)
                                .border("1px solid ${LocalColors.current.outline}")
                                .fontSize("14px"),
                            onClick = "setFilter('ACTIVE')"
                        )
                        
                        Button(
                            text = Translations.get("filter.completed", language),
                            modifier = Modifier
                                .padding("8px 16px")
                                .borderRadius("20px")
                                .background(LocalColors.current.surface)
                                .color(LocalColors.current.onSurface)
                                .border("1px solid ${LocalColors.current.outline}")
                                .fontSize("14px"),
                            onClick = "setFilter('COMPLETED')"
                        )
                    }
                    
                    // Todo list placeholder
                    Box(
                        modifier = Modifier
                            .width("100%")
                            .padding("40px")
                            .background(LocalColors.current.surface)
                            .borderRadius("12px")
                            .border("1px solid ${LocalColors.current.outline}")
                            .textAlign("center")
                    ) {
                        Column {
                            Text(
                                text = Translations.get("message.no_todos", language),
                                modifier = Modifier
                                    .fontSize("16px")
                                    .color(LocalColors.current.onSurfaceVariant)
                                    .margin("0 0 16px 0")
                            )
                            
                            Text(
                                text = Translations.get("todo.jwt_auth", language),
                                modifier = Modifier
                                    .fontSize("12px")
                                    .color(LocalColors.current.primary)
                                    .fontWeight("500")
                            )
                        }
                    }
                    
                    // Footer actions
                    Row(
                        modifier = Modifier
                            .width("100%")
                            .margin("24px 0 0 0")
                            .textAlign("center")
                    ) {
                        Text(
                            text = "0 ${Translations.get("todo.items_left", language)}",
                            modifier = Modifier
                                .fontSize("14px")
                                .color(LocalColors.current.onSurfaceVariant)
                                .margin("0 16px 0 0")
                        )
                        
                        Button(
                            text = Translations.get("todo.clear_completed", language),
                            modifier = Modifier
                                .padding("8px 16px")
                                .borderRadius("4px")
                                .background("transparent")
                                .color(LocalColors.current.error)
                                .border("1px solid ${LocalColors.current.error}")
                                .fontSize("12px"),
                            onClick = "clearCompleted()"
                        )
                    }
                    
                    // Powered by
                    Text(
                        text = Translations.get("app.powered_by", language),
                        modifier = Modifier
                            .fontSize("12px")
                            .color(LocalColors.current.onSurfaceVariant)
                            .margin("40px 0 0 0")
                            .textAlign("center")
                            .width("100%")
                    )
                }
            }
        }
    }
}