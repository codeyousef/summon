package code.yousef.example.springboot.controller

import code.yousef.example.springboot.i18n.Translations
import code.yousef.example.springboot.models.Language
import code.yousef.example.springboot.models.Theme
import code.yousef.example.springboot.pages.AuthPage
import code.yousef.example.springboot.pages.TodoPage
import code.yousef.summon.runtime.jvm.SpringBootRenderer
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import jakarta.servlet.http.HttpServletRequest

@Controller
class WebController {
    
    @GetMapping("/")
    fun home(
        @RequestParam(defaultValue = "en") lang: String,
        @RequestParam(defaultValue = "light") theme: String,
        request: HttpServletRequest
    ): String {
        // Check if user has JWT token in Authorization header or as a parameter
        val authHeader = request.getHeader("Authorization")
        val hasToken = authHeader?.startsWith("Bearer ") == true
        
        return if (hasToken) "redirect:/todos" else "redirect:/auth"
    }
    
    @GetMapping("/auth")
    fun auth(
        @RequestParam(defaultValue = "en") lang: String,
        @RequestParam(defaultValue = "light") theme: String,
        @RequestParam(defaultValue = "true") login: Boolean,
        model: Model
    ): String {
        val language = Language.values().find { it.code == lang } ?: Language.ENGLISH
        val themeEnum = Theme.values().find { it.value == theme } ?: Theme.LIGHT
        
        // Render Summon AuthPage component to HTML
        val renderer = SpringBootRenderer()
        val htmlContent = renderer.renderToString {
            AuthPage(language, themeEnum, login)
        }
        
        model.addAttribute("content", htmlContent)
        model.addAttribute("title", if (login) 
            Translations.get("auth.login", language) 
        else 
            Translations.get("auth.register", language)
        )
        model.addAttribute("isLogin", login)
        model.addAttribute("language", lang)
        model.addAttribute("theme", theme)
        
        return "auth"
    }
    
    @GetMapping("/todos")
    fun todos(
        @RequestParam(defaultValue = "en") lang: String,
        @RequestParam(defaultValue = "light") theme: String,
        @RequestParam(defaultValue = "User") username: String,
        model: Model
    ): String {
        val language = Language.values().find { it.code == lang } ?: Language.ENGLISH
        val themeEnum = Theme.values().find { it.value == theme } ?: Theme.LIGHT
        
        // Render Summon TodoPage component to HTML
        val renderer = SpringBootRenderer()
        val htmlContent = renderer.renderToString {
            TodoPage(language, themeEnum, username)
        }
        
        model.addAttribute("content", htmlContent)
        model.addAttribute("title", Translations.get("app.title", language))
        model.addAttribute("username", username)
        model.addAttribute("language", lang)
        model.addAttribute("theme", theme)
        
        return "todos"
    }
    
    @GetMapping("/health")
    fun health(): String {
        return "redirect:/api/health"
    }
}