package code.yousef.example.springboot.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.*
import code.yousef.summon.modifier.*
import code.yousef.summon.modifier.JustifyContent
import code.yousef.summon.modifier.AlignItems
import code.yousef.summon.modifier.FontWeight
import code.yousef.summon.extensions.*

/**
 * Themed navigation component that integrates with Summon's theme system
 * and includes proper navigation highlighting
 */
@Composable
fun ThemedNavigationComponent(currentPage: String = "") {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Div(
        modifier = Modifier()
            .fillMaxWidth()
            .style("background-color", if (theme.isDarkMode == true) "rgba(30, 41, 59, 0.95)" else "rgba(52, 58, 64, 0.95)")
            .style("backdrop-filter", "blur(10px)")
            .style("box-shadow", if (theme.isDarkMode == true) "0 2px 4px rgba(0,0,0,0.3)" else "0 2px 4px rgba(0,0,0,0.1)")
            .padding("1rem 0")
            .style("position", "sticky")
            .style("top", "0")
            .style("z-index", "1000")
    ) {
        Div(
            modifier = Modifier()
                .maxWidth(1200.px)
                .margin("0 auto")
                .padding("0 1rem")
                .style("display", "flex")
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            // Brand/Logo
            NavBrand()
            
            // Navigation Links
            NavLinks(currentPage)
        }
    }
}

@Composable 
fun NavBrand() {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    code.yousef.example.springboot.WorkingLink(
        href = "/",
        modifier = Modifier()
            .style("color", "white")
            .textDecoration("none")
            .fontWeight(FontWeight.Bold.value)
            .fontSize(1.25.rem)
    ) {
        Text("🚀 Summon + Spring Boot")
    }
}

@Composable
fun NavLinks(currentPage: String) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Row(
        modifier = Modifier()
            .style("display", "flex")
            .style("gap", "1.5rem")
            .alignItems(AlignItems.Center)
    ) {
        NavLink("Home", "/", currentPage)
        NavLink("Todos", "/todos", currentPage) 
        NavLink("Users", "/users", currentPage)
        NavLink("Dashboard", "/dashboard", currentPage)
        NavLink("Chat", "/chat", currentPage)
        NavLink("Contact", "/contact", currentPage)
    }
}

@Composable
fun NavLink(label: String, href: String, currentPage: String) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    val isActive = when {
        href == "/" && (currentPage == "home" || currentPage == "") -> true
        href == "/todos" && currentPage == "todos" -> true
        href == "/users" && currentPage == "users" -> true
        href == "/dashboard" && currentPage == "dashboard" -> true
        href == "/chat" && currentPage == "chat" -> true
        href == "/contact" && currentPage == "contact" -> true
        else -> false
    }
    
    code.yousef.example.springboot.WorkingLink(
        href = href,
        modifier = Modifier()
            .style("color", if (isActive) theme.primaryColor else "rgba(255, 255, 255, 0.9)")
            .textDecoration("none")
            .padding("0.5rem 0.75rem")
            .borderRadius(6.px)
            .style("transition", "all 0.2s ease")
            .style("position", "relative")
            .style("font-weight", if (isActive) "600" else "400")
            .style("background-color", if (isActive) "rgba(255, 255, 255, 0.1)" else "transparent")
            .hover(Modifier()
                .style("background-color", "rgba(255, 255, 255, 0.1)")
                .style("color", "white"))
    ) {
        Text(
            text = label,
            modifier = Modifier()
                .fontSize(0.95.rem)
        )
    }
}

/**
 * Themed footer component
 */
@Composable
fun ThemedFooterComponent() {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Div(
        modifier = Modifier()
            .fillMaxWidth()
            .style("background-color", if (theme.isDarkMode == true) "rgba(30, 41, 59, 0.95)" else "rgba(52, 58, 64, 0.95)")
            .style("color", "white")
            .padding("2rem 0")
            .style("margin-top", "auto")
    ) {
        Div(
            modifier = Modifier()
                .maxWidth(1200.px)
                .margin("0 auto")
                .padding("0 1rem")
        ) {
            // Footer Links Section
            Row(
                modifier = Modifier()
                    .justifyContent(JustifyContent.Center)
                    .style("gap", "2rem")
                    .marginBottom(1.5.rem)
            ) {
                FooterLink("Home", "/")
                FooterLink("Todos", "/todos")
                FooterLink("Users", "/users")
                FooterLink("Dashboard", "/dashboard")
                FooterLink("Chat", "/chat")
                FooterLink("Contact", "/contact")
            }
            
            // External Links Section  
            Row(
                modifier = Modifier()
                    .justifyContent(JustifyContent.Center)
                    .style("gap", "2rem")
                    .marginBottom(1.5.rem)
            ) {
                ExternalLink("🔗 GitHub Repository", "https://github.com/codeyousef/summon")
                ExternalLink("🔗 Kotlin", "https://kotlinlang.org/")
                ExternalLink("🔗 Spring Boot", "https://spring.io/projects/spring-boot")
            }
            
            // Copyright Section
            Div(
                modifier = Modifier()
                    .textAlign("center")
            ) {
                Text(
                    text = "© 2025 Summon Spring Boot Example. Powered by Summon Framework.",
                    modifier = Modifier()
                        .margin("0")
                        .fontSize(0.875.rem)
                        .style("color", "rgba(255, 255, 255, 0.6)")
                )
            }
        }
    }
}

@Composable
fun FooterLink(label: String, href: String) {
    code.yousef.example.springboot.WorkingLink(
        href = href,
        modifier = Modifier()
            .style("color", "rgba(255, 255, 255, 0.7)")
            .textDecoration("none") 
            .fontSize(0.95.rem)
            .style("transition", "color 0.2s")
            .hover(Modifier()
                .style("color", "white"))
    ) {
        Text(label)
    }
}

@Composable
fun ExternalLink(label: String, href: String) {
    code.yousef.example.springboot.WorkingLink(
        href = href,
        target = "_blank",
        modifier = Modifier()
            .style("color", "#60a5fa")
            .textDecoration("none")
            .fontSize(0.9.rem)
            .style("transition", "color 0.2s")
            .hover(Modifier()
                .style("color", "#93c5fd"))
    ) {
        Text(label)
    }
}
