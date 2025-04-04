package code.yousef.summon.examples

import code.yousef.summon.*
import code.yousef.summon.routing.*
import kotlinx.html.TagConsumer

/**
 * Example of a navigation system using Router, NavLink, Redirect, RouteParams, History, and DeepLinking.
 * This shows how to create a simple multi-page application with navigation capabilities.
 */
object NavigationExample {

    /**
     * Creates a main application with navigation.
     */
    fun createApp(): Composable {
        // Create a router with routes
        val routerBuilder = Router.RouterBuilder()
        routerBuilder.route("/") { params ->
            HomePage()
        }
        routerBuilder.route("/about") { params ->
            AboutPage()
        }
        routerBuilder.route("/user/:userId") { params ->
            UserProfilePage(params)
        }
        routerBuilder.route("/settings") { params ->
            SettingsPage()
        }
        routerBuilder.route("/old-page") { params ->
            Redirect.to("/", replace = true)
        }
        routerBuilder.notFound { params ->
            NotFoundPage()
        }
        val router = Router(routerBuilder.routes, routerBuilder.notFoundComponent)
        
        return object : Composable {
            override fun <T> compose(receiver: T): T {
                if (receiver is TagConsumer<*>) {
                    @Suppress("UNCHECKED_CAST")
                    val consumer = receiver as TagConsumer<Any?>
                    
                    // Add DeepLinking meta tags for the current route
                    val path = router.getCurrentPath()
                    val metaTags = DeepLinking.metaTags(
                        path = path,
                        title = "Navigation Example - ${getPageTitle(path)}",
                        description = "An example of Summon navigation system"
                    )
                    
                    // Render meta tags first
                    metaTags.compose(consumer)
                    
                    // Then render the app layout with navigation
                    AppLayout(router).compose(consumer)
                    
                    return consumer as T
                }
                return receiver
            }
        }
    }
    
    /**
     * Helper to get page title based on path
     */
    private fun getPageTitle(path: String): String {
        return when {
            path == "/" -> "Home"
            path == "/about" -> "About"
            path.startsWith("/user/") -> "User Profile"
            path == "/settings" -> "Settings"
            else -> "Not Found"
        }
    }
    
    /**
     * Main application layout with navigation bar
     */
    private class AppLayout(private val router: Router) : Composable {
        override fun <T> compose(receiver: T): T {
            return Column(
                modifier = Modifier()
                    .padding("20px"),
                content = listOf(
                    // Navigation bar
                    Column(
                        modifier = Modifier()
                            .background("#f5f5f5")
                            .padding("10px"),
                        content = listOf(
                            h1("Navigation Example", Modifier().text().textAlign("center").toModifier()),
                            
                            // Navigation links
                            Column(
                                modifier = Modifier().padding("10px"),
                                content = listOf(
                                    NavLink.create(
                                        to = "/",
                                        text = "Home",
                                        className = "nav-item",
                                        activeClassName = "nav-item-active"
                                    ),
                                    NavLink.create(
                                        to = "/about",
                                        text = "About",
                                        className = "nav-item",
                                        activeClassName = "nav-item-active"
                                    ),
                                    NavLink.create(
                                        to = "/user/123",
                                        text = "User Profile",
                                        className = "nav-item",
                                        activeClassName = "nav-item-active"
                                    ),
                                    NavLink.create(
                                        to = "/settings",
                                        text = "Settings",
                                        className = "nav-item",
                                        activeClassName = "nav-item-active"
                                    )
                                )
                            ),
                            
                            // History navigation buttons
                            Column(
                                modifier = Modifier().padding("10px"),
                                content = listOf(
                                    Button(
                                        label = "Back",
                                        onClick = {
                                            router.goBack()
                                        }
                                    ),
                                    Button(
                                        label = "Forward",
                                        onClick = {
                                            router.goForward()
                                        }
                                    )
                                )
                            )
                        )
                    ),
                    
                    // Main content area - render the current route
                    Column(
                        modifier = Modifier()
                            .padding("20px")
                            .background("#ffffff"),
                        content = listOf(
                            router
                        )
                    )
                )
            ).compose(receiver)
        }
    }
    
    /**
     * Home page component
     */
    private class HomePage : Composable {
        override fun <T> compose(receiver: T): T {
            return Column(
                modifier = Modifier().padding("20px"),
                content = listOf(
                    h1("Home Page"),
                    paragraph("Welcome to the home page of our navigation example."),
                    paragraph("Use the navigation bar above to explore different pages.")
                )
            ).compose(receiver)
        }
    }
    
    /**
     * About page component
     */
    private class AboutPage : Composable {
        override fun <T> compose(receiver: T): T {
            return Column(
                modifier = Modifier().padding("20px"),
                content = listOf(
                    h1("About Page"),
                    paragraph("This is the about page of our navigation example."),
                    paragraph("It demonstrates the Router and NavLink components.")
                )
            ).compose(receiver)
        }
    }
    
    /**
     * User profile page component that uses RouteParams
     */
    private class UserProfilePage(private val params: RouteParams) : Composable {
        override fun <T> compose(receiver: T): T {
            val userId = params["userId"] ?: "unknown"
            
            return Column(
                modifier = Modifier().padding("20px"),
                content = listOf(
                    h1("User Profile"),
                    h2("User ID: $userId"),
                    paragraph("This page demonstrates using RouteParams to access URL parameters."),
                    paragraph("The user ID was extracted from the URL: /user/$userId")
                )
            ).compose(receiver)
        }
    }
    
    /**
     * Settings page component
     */
    private class SettingsPage : Composable {
        override fun <T> compose(receiver: T): T {
            return Column(
                modifier = Modifier().padding("20px"),
                content = listOf(
                    h1("Settings Page"),
                    paragraph("This is the settings page of our navigation example."),
                    
                    // Deep linking example
                    h2("Deep Linking Example"),
                    paragraph("Here's a deep link to share the user profile:"),
                    Link(
                        text = "Share User Profile",
                        href = DeepLinking.createUrl(
                            path = "/user/123",
                            queryParams = mapOf("section" to "profile", "highlight" to "true"),
                            fragment = "contact-info"
                        )
                    )
                )
            ).compose(receiver)
        }
    }
    
    /**
     * Not found page component
     */
    private class NotFoundPage : Composable {
        override fun <T> compose(receiver: T): T {
            return Column(
                modifier = Modifier().padding("20px"),
                content = listOf(
                    h1("404 - Page Not Found"),
                    paragraph("The page you are looking for does not exist."),
                    paragraph("Try navigating using the links above.")
                )
            ).compose(receiver)
        }
    }
} 