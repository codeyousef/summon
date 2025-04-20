package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestPath
import java.util.concurrent.atomic.AtomicInteger

/**
 * Extension function to capitalize the first letter of a string
 */
private fun String.capitalize(): String {
    return if (this.isNotEmpty()) this.substring(0, 1).uppercase() + this.substring(1) else this
}

/**
 * Web resource with endpoints for rendering pages with Summon components
 * and handling API requests.
 */
@Path("/")
class WebResource {

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var summonRenderer: SummonRenderer

    // QuteHtmxComponents is no longer needed as we're using the library's components

    @Inject
    lateinit var quteComponents: QuteComponents

    @Context
    lateinit var context: ContainerRequestContext

    private val counter = AtomicInteger(0)

    /**
     * Render the home page.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        println("WebResource.home() called - starting to render with Summon")
        try {
            println("WebResource.home() - Using Summon Components rendering")

            // Render the page with the hero and counter components
            val result = summonRenderer.render(title = "Summon with Quarkus - Home") {
                AppRoot { // Use the Summon AppRoot composable
                    // Add the hero and counter components directly
                    QuteHeroComponent("Quarkus User", quteComponents)
                    CurrentTimeComponent() // Use Summon version
                    FeatureCardsComponent() // Use Summon version
                    QuteCounterComponent(counter.get(), quteComponents)
                }
            }

            println("WebResource.home() finished rendering, result length: ${result.length}")
            return result
        } catch (e: Exception) {
            System.err.println("ERROR in WebResource.home(): ${e.message}")
            e.printStackTrace()
            return errorHtml("Error Rendering Home Page", e)
        }
    }

    /**
     * Render the users page.
     */
    @GET
    @Path("/users")
    @Produces(MediaType.TEXT_HTML)
    fun users(): String {
        println("WebResource.users() START - rendering users page with Summon")
        try {
            val users = userService.getAllUsers()
            return summonRenderer.render(title = "User Management") {
                 AppRoot {
                     // Call the top-level UsersPage composable
                     UsersPage(users)
                 }
            }
        } catch (e: Exception) {
            System.err.println("WebResource.users() ERROR: ${e.message}")
            e.printStackTrace()
            return errorHtml("Error Rendering Users Page", e)
        }
    }

    /**
     * Render the dashboard page.
     */
    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_HTML)
    fun dashboard(): String {
        println("WebResource.dashboard() START - rendering dashboard with Summon")
        try {
            return summonRenderer.render(title = "Dashboard") {
                AppRoot {
                    DashboardComponent() // Use the Summon DashboardComponent
                }
            }
        } catch (e: Exception) { 
            System.err.println("WebResource.dashboard() ERROR: ${e.message}")
            e.printStackTrace()
            return errorHtml("Error Rendering Dashboard Page", e)
        }
    }

    /**
     * Render the contact page.
     */
    @GET
    @Path("/contact")
    @Produces(MediaType.TEXT_HTML)
    fun contact(): String {
        println("WebResource.contact() START - rendering contact page with Summon")
        try {
            return summonRenderer.render(title = "Contact Us") {
                AppRoot {
                     ContactFormComponent() // Use the Summon ContactFormComponent
                 }
            }
        } catch (e: Exception) {
            System.err.println("WebResource.contact() ERROR: ${e.message}")
            e.printStackTrace()
            return errorHtml("Error Rendering Contact Page", e)
        }
    }

    /**
     * Render the theme customization page.
     */
    @GET
    @Path("/theme")
    @Produces(MediaType.TEXT_HTML)
    fun theme(): String {
        println("WebResource.theme() - Rendering theme customization page with Summon")
        try {
            return summonRenderer.render(title = "Theme Customization") {
                 AppRoot {
                     ThemePage() // Assuming ThemePage uses Summon components
                 }
            }
        } catch (e: Exception) {
            System.err.println("WebResource.theme() - Error rendering theme page: ${e.message}")
            e.printStackTrace()
            return errorHtml("Error Rendering Theme Page", e)
        }
    }

    // --- API Endpoints --- 

    @POST @Path("/api/counter/increment") @Produces(MediaType.TEXT_PLAIN)
    fun incrementCounter(): String {
        val newValue = counter.incrementAndGet()
        println("=== COUNTER INCREMENT ENDPOINT ===")
        println("Counter incremented to: $newValue")

        // Log all request headers for comprehensive debugging
        println("All request headers:")
        context.headers.forEach { name, values ->
            println("  - $name: ${values.joinToString(", ")}")
        }

        // Log HTMX-specific headers
        println("HTMX headers:")
        println("  - HX-Request: ${context.getHeaderString("HX-Request") ?: "No"}")
        println("  - HX-Trigger: ${context.getHeaderString("HX-Trigger") ?: "None"}")
        println("  - HX-Target: ${context.getHeaderString("HX-Target") ?: "None"}")
        println("  - HX-Current-URL: ${context.getHeaderString("HX-Current-URL") ?: "None"}")

        // Log request details
        println("Request details:")
        println("  - Method: ${context.method}")
        println("  - URI: ${context.uriInfo.requestUri}")
        println("  - Content-Type: ${context.getHeaderString("Content-Type") ?: "Not specified"}")
        println("  - Accept: ${context.getHeaderString("Accept") ?: "Not specified"}")

        // Log response
        println("Returning value: $newValue")
        println("=== END COUNTER INCREMENT ENDPOINT ===")

        // Return the value with additional headers for HTMX debugging
        return newValue.toString()
    }

    @POST @Path("/api/counter/decrement") @Produces(MediaType.TEXT_PLAIN)
    fun decrementCounter(): String {
        val newValue = counter.decrementAndGet()
        println("=== COUNTER DECREMENT ENDPOINT ===")
        println("Counter decremented to: $newValue")

        // Log all request headers for comprehensive debugging
        println("All request headers:")
        context.headers.forEach { name, values ->
            println("  - $name: ${values.joinToString(", ")}")
        }

        // Log HTMX-specific headers
        println("HTMX headers:")
        println("  - HX-Request: ${context.getHeaderString("HX-Request") ?: "No"}")
        println("  - HX-Trigger: ${context.getHeaderString("HX-Trigger") ?: "None"}")
        println("  - HX-Target: ${context.getHeaderString("HX-Target") ?: "None"}")
        println("  - HX-Current-URL: ${context.getHeaderString("HX-Current-URL") ?: "None"}")

        // Log request details
        println("Request details:")
        println("  - Method: ${context.method}")
        println("  - URI: ${context.uriInfo.requestUri}")
        println("  - Content-Type: ${context.getHeaderString("Content-Type") ?: "Not specified"}")
        println("  - Accept: ${context.getHeaderString("Accept") ?: "Not specified"}")

        // Log response
        println("Returning value: $newValue")
        println("=== END COUNTER DECREMENT ENDPOINT ===")

        // Return the value with additional headers for HTMX debugging
        return newValue.toString()
    }

    /**
     * Endpoint to return the current server time.
     * This is used by the CurrentTimeComponent to update the time display every second.
     */
    @GET @Path("/api/time") @Produces(MediaType.TEXT_PLAIN)
    fun getCurrentTime(): String {
        val currentTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
        println("=== TIME ENDPOINT ===")
        println("Returning current time: $currentTime")

        // Log HTMX-specific headers
        println("HTMX headers:")
        println("  - HX-Request: ${context.getHeaderString("HX-Request") ?: "No"}")
        println("  - HX-Trigger: ${context.getHeaderString("HX-Trigger") ?: "None"}")
        println("  - HX-Target: ${context.getHeaderString("HX-Target") ?: "None"}")

        println("=== END TIME ENDPOINT ===")

        return currentTime
    }

    /**
     * Implementation of CounterComponent that uses Qute templates to render HTML with the correct attributes.
     * This ensures that the HTMX attributes are rendered correctly as separate HTML attributes.
     */
    @GET @Path("/api/counter-component") @Produces(MediaType.TEXT_HTML)
    fun counterComponentHtml(): String {
        val currentValue = counter.get()
        println("=== COUNTER COMPONENT ENDPOINT ===")
        println("counterComponentHtml called, returning counter with value: $currentValue")

        // Log HTMX-specific headers
        println("HTMX headers:")
        println("  - HX-Request: ${context.getHeaderString("HX-Request") ?: "No"}")
        println("  - HX-Trigger: ${context.getHeaderString("HX-Trigger") ?: "None"}")
        println("  - HX-Target: ${context.getHeaderString("HX-Target") ?: "None"}")

        // Render the counter component using the Summon renderer
        val html = summonRenderer.render {
            QuteCounterComponent(currentValue, quteComponents)
        }

        println("HTML length: ${html.length}")
        println("=== END COUNTER COMPONENT ENDPOINT ===")

        return html
    }

    /**
     * Implementation of the hero component that uses Qute templates to render HTML with the correct attributes.
     * This ensures that the HTMX attributes are rendered correctly as separate HTML attributes.
     */
    @GET @Path("/api/hero-component") @Produces(MediaType.TEXT_HTML)
    fun heroComponentHtml(@QueryParam("username") username: String = "Quarkus User"): String {
        println("=== HERO COMPONENT ENDPOINT ===")
        println("heroComponentHtml called with username: $username")

        // Log HTMX-specific headers
        println("HTMX headers:")
        println("  - HX-Request: ${context.getHeaderString("HX-Request") ?: "No"}")
        println("  - HX-Trigger: ${context.getHeaderString("HX-Trigger") ?: "None"}")
        println("  - HX-Target: ${context.getHeaderString("HX-Target") ?: "None"}")

        // Render the hero component using the Summon renderer
        val html = summonRenderer.render {
            QuteHeroComponent(username, quteComponents)
        }

        println("HTML length: ${html.length}")
        println("=== END HERO COMPONENT ENDPOINT ===")

        return html
    }

    // --- User Form and CRUD API --- 

    @GET @Path("/api/users/form") @Produces(MediaType.TEXT_HTML)
    fun getUserForm(): String {
        // Use the Summon-based UserFormComponent
        return summonRenderer.render {
            UserFormComponent(null, "/api/users", "post")
        }
    }

    @GET @Path("/api/users/{id}/edit") @Produces(MediaType.TEXT_HTML)
    fun getUserEditForm(@RestPath id: Long): String {
        val user = userService.getUserById(id) ?: return "User not found"
        // Use the Summon-based UserFormComponent
        return summonRenderer.render {
            UserFormComponent(user, "/api/users/$id", "put")
        }
    }

    @GET @Path("/api/users/cancel-form") @Produces(MediaType.TEXT_HTML)
    fun cancelUserForm(): String = "" // Clears the form container

    @POST @Path("/api/users") @Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.TEXT_HTML)
    fun addUser(@RestForm("name") name: String, @RestForm("email") email: String, @RestForm("role") role: String): String {
        try {
            val nextId = userService.getAllUsers().maxOfOrNull { it.id }?.plus(1) ?: 1L
            val user = User(nextId, name, email, role, true)
            userService.addUser(user)
            // Use the Summon-based UserFormComponent
            return summonRenderer.render {
                UserFormComponent(null, "/api/users", "post")
            }
        } catch (e: Exception) {
            System.err.println("ERROR in addUser: ${e.message}")
            return summonRenderer.render {
                Box(modifier = Modifier().style("style", "color:red")) {
                    Text("Error adding user: ${e.message}")
                }
            }
        }
    }

    @PUT @Path("/api/users/{id}") @Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.TEXT_HTML)
    fun updateUser(@RestPath id: Long, @RestForm("name") name: String, @RestForm("email") email: String, @RestForm("role") role: String): String {
        try {
            val currentUser = userService.getUserById(id)
            val updatedUser = User(id, name, email, role, currentUser?.active ?: true)
            userService.updateUser(id, updatedUser)
            // Use the Summon-based UserFormComponent
            return summonRenderer.render {
                UserFormComponent(updatedUser, "/api/users/$id", "put")
            }
         } catch (e: Exception) {
            System.err.println("ERROR in updateUser: ${e.message}")
            return summonRenderer.render {
                Box(modifier = Modifier().style("style", "color:red")) {
                    Text("Error updating user: ${e.message}")
                }
            }
        }
    }

    @DELETE @Path("/api/users/{id}") @Produces(MediaType.TEXT_HTML)
    fun deleteUser(@RestPath id: Long): String {
        try {
            userService.deleteUser(id)
            // Revert to returning HTML string (maybe an empty div or success message?)
            // For now, returning an empty string to potentially clear the table area if targeted directly.
            // A better approach would be needed depending on HTMX target.
            return "" 
        } catch (e: Exception) {
            System.err.println("ERROR in deleteUser: ${e.message}")
            return "<div style='color:red'>Error deleting user: ${e.message}</div>"
        }
    }

    // --- Contact Form API --- 

    @POST @Path("/api/contact") @Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.TEXT_HTML)
    fun processContactForm(@RestForm("name") name: String, @RestForm("email") email: String, @RestForm("subject") subject: String, @RestForm("message") message: String): String {
        // Return simple confirmation string (can be enhanced later)
        return summonRenderer.render {
            Box(modifier = Modifier().style("class", "card").style("style", "background-color: #e8f5e9; padding: 1rem;")) {
                Column {
                    Text("Message Sent!", modifier = Modifier().style("style", "font-size: 1.5rem; font-weight: bold;"))
                    Text("Thank you, $name.")
                }
            }
        }
    }

    // --- Component API Endpoints --- 

    @POST @Path("/api/color-change") @Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.TEXT_PLAIN)
    fun handleColorChange(@RestForm("value") value: String): String {
        // This endpoint handles color picker changes
        // In a real application, you might store this value in a session or database
        println("Color changed to: $value")
        return "Color updated" // Simple response as we're using hx-swap="none"
    }

    @POST @Path("/api/slider-change") @Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.TEXT_PLAIN)
    fun handleSliderChange(@RestForm("value") value: String): String {
        // This endpoint handles slider changes
        // In a real application, you might store this value in a session or database
        println("Slider value changed to: $value")
        return "Slider updated" // Simple response as we're using hx-swap="none"
    }

    // --- Test Endpoints --- 

    @GET @Path("/basic-test") @Produces(MediaType.TEXT_HTML)
    fun basicTest(): String {
        println("WebResource.basicTest() called")
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Basic HTML Test</title>
                <style>
                    body { 
                        font-family: system-ui, sans-serif; 
                        line-height: 1.5;
                        padding: 2rem;
                        max-width: 800px;
                        margin: 0 auto;
                        color: #333;
                    }
                    h1 { color: #4695EB; }
                    .card {
                        border: 1px solid #ddd;
                        border-radius: 8px;
                        padding: 1rem;
                        margin: 1rem 0;
                        background-color: #f9f9f9;
                    }
                </style>
            </head>
            <body>
                <h1>Basic HTML Test</h1>
                <div class="card">
                    <p>This is a simple HTML page without using Summon rendering.</p>
                    <p>If you can see this, then basic HTML response is working.</p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    @GET @Path("/simple-test") @Produces(MediaType.TEXT_HTML)
    fun simpleTest(): String {
        println("WebResource.simpleTest() called - minimal Summon test")
        try {
            val result = summonRenderer.render(title = "Simple Summon Test") {
                 AppRoot {
                     Text(text = "Minimal Summon Test Content")
                     HtmxHeroComponent("Simple Test User")
                 }
            }
            println("WebResource.simpleTest() finished rendering, result length: ${result.length}")
            return result
        } catch (e: Exception) {
            System.err.println("ERROR in WebResource.simpleTest(): ${e.message}")
            e.printStackTrace()
            return errorHtml("Error Rendering Simple Test Page", e)
        }
    }

    @GET @Path("/component-test") @Produces(MediaType.TEXT_HTML)
    fun componentTest(): String {
        println("WebResource.componentTest() called - testing individual Summon components")
        try {
            val result = summonRenderer.render(title = "Summon Component Test") {
                 AppRoot {
                     Column {
                         Text("Summon Component Testing Page")
                         // Test individual Summon components
                         testSummonComponent("NavigationComponent") { NavigationComponent() }
                         testSummonComponent("HtmxHeroComponent") { HtmxHeroComponent("Test User") }
                         testSummonComponent("CurrentTimeComponent") { CurrentTimeComponent() }
                         testSummonComponent("FeatureCardsComponent") { FeatureCardsComponent() }
                         testSummonComponent("HtmxCounterComponent") { HtmxCounterComponent(counter.get()) }
                         testSummonComponent("DashboardComponent") { DashboardComponent() }
                         testSummonComponent("ContactFormComponent") { ContactFormComponent() }
                         testSummonComponent("User Table") { WebResourceUserTable(userService.getAllUsers()) }
                         testSummonComponent("FooterComponent") { FooterComponent() }
                     }
                 }
            }
            println("WebResource.componentTest() finished rendering, result length: ${result.length}")
            return result
        } catch (e: Exception) {
            System.err.println("ERROR in WebResource.componentTest(): ${e.message}")
            e.printStackTrace()
             return errorHtml("Error Rendering Component Test Page", e)
        }
    }

    // Helper for component testing page using Summon
    @Composable
    private fun testSummonComponent(name: String, component: @Composable () -> Unit) {
        Box(modifier = Modifier().style("style", "margin-bottom: 30px; border: 1px solid #ddd; padding: 15px; border-radius: 5px;")) {
            Column {
                Text("Test: $name")
                try {
                    component() // Render the component
                    Text("✅ Rendered successfully", modifier = Modifier().style("style", "color: green; margin-top: 10px;"))
                } catch (e: Exception) {
                    Text("❌ Render failed: ${e.message}", modifier = Modifier().style("style", "color: red; margin-top: 10px;"))
                }
            }
        }
    }

    @GET @Path("/debug-summon") @Produces(MediaType.TEXT_HTML)
    fun debugSummon(): String {
        println("WebResource.debugSummon() called - starting debugging with Summon components")
        try {
            val htmlStart = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Debug Summon</title>
                    <style>
                        body { font-family: system-ui, sans-serif; padding: 2rem; }
                        .debug { margin: 1rem 0; padding: 1rem; border: 1px solid #ddd; }
                        .error { color: red; }
                        .success { color: green; }
                        .section { margin-top: 20px; border-top: 1px solid #ccc; padding-top: 20px; }
                    </style>
                </head>
                <body>
                    <h1>Summon Debugging Page</h1>
                    <p>Investigating why some pages render correctly (like home) while others are blank.</p>
            """.trimIndent()

            val debugOutput = StringBuilder()

            // First section: Basic renderer check
            debugOutput.append("<div class='debug'><h2>Renderer Initialization</h2>")
            try {
                debugOutput.append("<p>SummonRenderer instance: ${summonRenderer::class.java.name}</p>")
                debugOutput.append("<p class='success'>✅ SummonRenderer initialized successfully</p>")
            } catch (e: Exception) {
                debugOutput.append("<p class='error'>❌ SummonRenderer initialization failed: ${e.message}</p>")
                debugOutput.append("<pre>${e.stackTraceToString()}</pre>")
            }
            debugOutput.append("</div>")

            // Second section: Test components using HTMLComponents
            debugOutput.append("<div class='debug section'><h2>Summon Components Test</h2>")
            debugOutput.append(renderDebugSummonComponent("NavigationComponent") { NavigationComponent() })
            debugOutput.append(renderDebugSummonComponent("HtmxHeroComponent") { HtmxHeroComponent("Debug User") })
            debugOutput.append(renderDebugSummonComponent("CurrentTimeComponent") { CurrentTimeComponent() })
            debugOutput.append(renderDebugSummonComponent("FeatureCardsComponent") { FeatureCardsComponent() })
            debugOutput.append(renderDebugSummonComponent("HtmxCounterComponent") { HtmxCounterComponent(counter.get()) })
            debugOutput.append(renderDebugSummonComponent("DashboardComponent") { DashboardComponent() })
            debugOutput.append(renderDebugSummonComponent("ContactFormComponent") { ContactFormComponent() })
            debugOutput.append(renderDebugSummonComponent("User Table") { WebResourceUserTable(userService.getAllUsers()) })
            debugOutput.append(renderDebugSummonComponent("FooterComponent") { FooterComponent() })
            debugOutput.append("</div>")

            // Section 3: Test rendering within a container (replaces AppRoot tests)
            debugOutput.append("<div class='debug section'><h2>Testing Components in AppRoot</h2>")
            debugOutput.append("<div class='component-test'><h3>AppRoot with HtmxHeroComponent</h3>")
            try {
                 val renderedHtml = summonRenderer.render(title = "Debug Render Test 1") {
                     AppRoot {
                         Text("AppRoot Content:")
                         HtmxHeroComponent("Render Test User")
                     }
                 }
                 // We don't insert the full renderedHtml here, just confirm it didn't throw
                 debugOutput.append("<p class='success'>✅ SummonRenderer.render() with AppRoot/HtmxHeroComponent succeeded.</p>")
             } catch (e: Exception) {
                 debugOutput.append("<p class='error'>❌ SummonRenderer.render() with AppRoot/HtmxHeroComponent failed: ${e.message}</p>")
                 debugOutput.append("<pre>${e.stackTraceToString().take(300)}</pre>")
             }
             debugOutput.append("</div>")

             debugOutput.append("<div class='component-test'><h3>AppRoot with DashboardComponent</h3>")
             try {
                  val renderedHtml = summonRenderer.render(title = "Debug Render Test 2") {
                      AppRoot {
                          Text("AppRoot Content:")
                          DashboardComponent()
                      }
                  }
                  debugOutput.append("<p class='success'>✅ SummonRenderer.render() with AppRoot/DashboardComponent succeeded.</p>")
              } catch (e: Exception) {
                  debugOutput.append("<p class='error'>❌ SummonRenderer.render() with AppRoot/DashboardComponent failed: ${e.message}</p>")
                  debugOutput.append("<pre>${e.stackTraceToString().take(300)}</pre>")
              }
              debugOutput.append("</div>")

            debugOutput.append("</div>")

            val htmlEnd = """
                </body>
                </html>
            """.trimIndent()

            val finalHtml = htmlStart + debugOutput.toString() + htmlEnd
            println("WebResource.debugSummon() finished, output length: ${finalHtml.length}")
            return finalHtml

        } catch (e: Exception) {
            System.err.println("ERROR in WebResource.debugSummon(): ${e.message}")
            e.printStackTrace()
            return errorHtml("Error Rendering Debug Page", e)
        }
    }

    // Helper for debug page component rendering using Summon
    private fun renderDebugSummonComponent(name: String, component: @Composable () -> Unit): String {
        val sb = StringBuilder()
        sb.append("<div class='component-test'><h3>$name (Summon)</h3>")
        try {
            // Simplify: Just execute the component to see if composition throws an error.
            // Rendering isolated composable fragments to string seems unreliable here.
             // val renderAttempt: String = summonRenderer.render { component() } // Removed problematic render call
             component() // Directly call the composable to check for composition errors
            sb.append("<p class='success'>✅ $name Summon component composition succeeded (no render output checked).</p>")
        } catch (e: Exception) {
            sb.append("<p class='error'>❌ $name Summon component composition failed: ${e.message}</p>")
            sb.append("<pre>${e.stackTraceToString().take(300)}</pre>")
        }
        sb.append("</div>")
        return sb.toString()
    }

    // --- Theme API --- 

    companion object {
        // Session attribute key for storing theme
        const val SESSION_THEME_KEY = "user_theme"
        // Session attribute key for storing custom theme
        const val SESSION_CUSTOM_THEME_KEY = "user_custom_theme"
    }

    @POST @Path("/api/theme/{theme}") @Produces(MediaType.TEXT_HTML)
    fun changeTheme(@RestPath theme: String, @CookieParam(SESSION_THEME_KEY) currentTheme: String?): Response {
        println("WebResource.changeTheme() - Changing theme to: $theme")

        // Create a cookie to store the theme
        val themeCookie = NewCookie.Builder(SESSION_THEME_KEY)
            .value(theme)
            .path("/")
            .maxAge(60 * 60 * 24 * 30) // 30 days
            .build()

        // Log that we stored the theme in a cookie
        println("Theme '$theme' stored in cookie")

        val htmlContent = """
            <div id="theme-response">Theme changed to $theme</div>
            <script>
                // Apply the theme via CSS variables
                document.documentElement.setAttribute('data-theme', '$theme');

                // Update the current theme name
                document.getElementById('current-theme-name').textContent = '${theme.capitalize()}';

                // Remove active class from all theme options
                document.querySelectorAll('.theme-option').forEach(option => {
                    option.classList.remove('active');
                });

                // Add active class to the selected theme
                document.querySelector(".theme-option[data-theme=\"$theme\"]")?.classList.add('active');

                // Store theme preference in localStorage for client-side persistence
                localStorage.setItem('summon_theme', '$theme');
            </script>
        """.trimIndent()

        // Return the response with the cookie
        return Response.ok(htmlContent)
            .cookie(themeCookie)
            .build()
    }


    @POST @Path("/api/theme/custom") @Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.TEXT_HTML)
    fun applyCustomTheme(
        @FormParam("primaryColor") primaryColor: String,
        @FormParam("textColor") textColor: String,
        @FormParam("backgroundColor") backgroundColor: String,
        @FormParam("fontSize") fontSize: String
    ): Response {
        println("WebResource.applyCustomTheme() - Applying custom theme")

        // Create a JSON string to store the custom theme settings
        val customThemeJson = """
            {
                "primaryColor": "$primaryColor",
                "textColor": "$textColor",
                "backgroundColor": "$backgroundColor",
                "fontSize": "$fontSize"
            }
        """.trimIndent()

        // Create cookies to store the theme settings
        val themeCookie = NewCookie.Builder(SESSION_THEME_KEY)
            .value("custom")
            .path("/")
            .maxAge(60 * 60 * 24 * 30) // 30 days
            .build()

        val customThemeCookie = NewCookie.Builder(SESSION_CUSTOM_THEME_KEY)
            .value(customThemeJson)
            .path("/")
            .maxAge(60 * 60 * 24 * 30) // 30 days
            .build()

        // Log that we stored the custom theme in cookies
        println("Custom theme stored in cookies")

        val htmlContent = """
            <div id="theme-response">Custom theme applied</div>
            <script>
                // Apply the theme via CSS variables
                document.documentElement.style.setProperty('--primary-color', '$primaryColor');
                document.documentElement.style.setProperty('--text-color', '$textColor');
                document.documentElement.style.setProperty('--bg-color', '$backgroundColor');
                document.documentElement.style.setProperty('--font-size', '${fontSize}px');

                // Update the current theme name
                document.getElementById('current-theme-name').textContent = 'Custom';

                // Remove active class from all theme options
                document.querySelectorAll('.theme-option').forEach(option => {
                    option.classList.remove('active');
                });

                // Store custom theme preferences in localStorage for client-side persistence
                localStorage.setItem('summon_theme', 'custom');
                localStorage.setItem('summon_custom_theme', JSON.stringify({
                    primaryColor: '$primaryColor',
                    textColor: '$textColor',
                    backgroundColor: '$backgroundColor',
                    fontSize: '$fontSize'
                }));
            </script>
        """.trimIndent()

        // Return the response with the cookies
        return Response.ok(htmlContent)
            .cookie(themeCookie, customThemeCookie)
            .build()
    }

    @GET @Path("/theme-html") @Produces(MediaType.TEXT_HTML)
    fun themeHtml(): String {
        println("WebResource.themeHtml() - Plain HTML version for comparison")

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Theme Debug - Plain HTML</title>
                <style>
                    body { 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        padding: 20px;
                    }
                    .container {
                        padding: 20px;
                        background-color: #f0f0f0;
                    }
                    .debug-title {
                        font-size: 24px;
                        color: red;
                        font-weight: bold;
                    }
                    .debug-text {
                        margin-top: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="debug-title">Debug Theme Page (Plain HTML)</div>
                    <div class="debug-text">This version uses plain HTML for comparison.</div>
                    <p>If you can see this, but not the Summon version, there's an issue with Summon rendering.</p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    // --- Summon User Table Composable ---
    @Composable
    fun WebResourceUserTable(users: List<User>) {
        Box(modifier = Modifier().style("id", "users-table-wrapper")) { // Wrapper for HTMX
            Box(modifier = Modifier().style("class", "table users-table")) { // Table element
                 Column { // Simulate table rows with Columns/Rows
                     // Header Row
                     Row(modifier = Modifier().style("style", "background-color: var(--panel-color); font-weight: bold;")) {
                         Box(modifier = Modifier().style("style", "padding: 0.75rem; text-align: left; width: 5%;")) { Text("ID") }
                         Box(modifier = Modifier().style("style", "padding: 0.75rem; text-align: left; width: 20%;")) { Text("Name") }
                         Box(modifier = Modifier().style("style", "padding: 0.75rem; text-align: left; width: 30%;")) { Text("Email") }
                         Box(modifier = Modifier().style("style", "padding: 0.75rem; text-align: left; width: 15%;")) { Text("Role") }
                         Box(modifier = Modifier().style("style", "padding: 0.75rem; text-align: center; width: 10%;")) { Text("Active") }
                         Box(modifier = Modifier().style("style", "padding: 0.75rem; text-align: left; width: 20%;")) { Text("Actions") }
                     }
                     // Data Rows
                     if (users.isEmpty()) {
                         Row {
                             Box(modifier = Modifier().style("style", "padding: 1rem; text-align: center; color: #777;").style("colspan", "6")) {
                                 Text("No users found")
                             }
                         }
                     } else {
                         users.forEach { user ->
                             Row(modifier = Modifier().style("style", "border-bottom: 1px solid var(--border-color); align-items: center;")) {
                                 Box(modifier = Modifier().style("style", "padding: 0.75rem; width: 5%;")) { Text(user.id.toString()) }
                                 Box(modifier = Modifier().style("style", "padding: 0.75rem; width: 20%;")) { Text(user.name) }
                                 Box(modifier = Modifier().style("style", "padding: 0.75rem; width: 30%;")) { Text(user.email) }
                                 Box(modifier = Modifier().style("style", "padding: 0.75rem; width: 15%;")) {
                                     Box(modifier = Modifier().style("style", "display: inline-block; padding: 0.25rem 0.5rem; border-radius: 4px; background-color: ${getRoleColor(user.role)}; color: white; font-size: 0.85rem;")) {
                                         Text(user.role)
                                     }
                                 }
                                 Box(modifier = Modifier().style("style", "padding: 0.75rem; text-align: center; width: 10%;")) {
                                     Text(if (user.active) "✓" else "✗", modifier = Modifier().style("style", "font-weight: bold; color: ${if (user.active) "var(--success-color)" else "var(--error-color)"};"))
                                 }
                                 Box(modifier = Modifier().style("style", "padding: 0.75rem; width: 20%;")) {
                                     Row(modifier = Modifier().style("style", "gap: 0.5rem;")) {
                                         Button(
                                             label = "Edit",
                                             onClick = { /* HTMX Triggered */ },
                                             modifier = Modifier()
                                                 .style("class", "btn btn-edit")
                                                 .style("style", "background-color: var(--info-color); color: white; padding: 0.3rem 0.6rem; font-size: 0.9rem;")
                                                 .style("hx-get", "/api/users/${user.id}/edit")
                                                 .style("hx-target", "#user-form-container")
                                                 .style("hx-swap", "innerHTML")
                                         )
                                         Button(
                                             label = "Delete",
                                             onClick = { /* HTMX Triggered */ },
                                             modifier = Modifier()
                                                 .style("class", "btn btn-delete")
                                                 .style("style", "background-color: var(--error-color); color: white; padding: 0.3rem 0.6rem; font-size: 0.9rem;")
                                                 .style("hx-delete", "/api/users/${user.id}")
                                                 .style("hx-target", "#users-table-wrapper") // Target wrapper
                                                 .style("hx-swap", "outerHTML")
                                                 .style("hx-confirm", "Are you sure you want to delete user ${user.name}?")
                                         )
                                     }
                                 }
                             }
                         }
                     }
                 }
            }
        }
    }

    // --- Helper Functions --- 

    private fun getRoleColor(role: String): String {
        return when (role.lowercase()) {
            "admin" -> "#E91E63"  // Pink
            "moderator" -> "#673AB7"  // Deep Purple
            "editor" -> "#3F51B5"  // Indigo
            "user" -> "#2196F3"  // Blue
            else -> "#607D8B"  // Blue Grey
        }
    }

    // Error page helper using Summon components
    private fun errorHtml(title: String, e: Exception): String {
        return summonRenderer.render(title = "Error") {
            Column(modifier = Modifier().style("style", "padding: 1rem;")) {
                Text(
                    text = title,
                    modifier = Modifier().style("style", "color: red; font-size: 1.5rem; font-weight: bold;")
                )
                Text(
                    text = e.message ?: "Unknown error",
                    modifier = Modifier().style("style", "margin: 1rem 0;")
                )
                Box(
                    modifier = Modifier().style("style", "border-top: 1px solid #ddd; padding-top: 1rem; margin-top: 1rem;")
                ) {
                    Text(
                        text = e.stackTraceToString(),
                        modifier = Modifier().style("element", "pre").style("style", "font-family: monospace; font-size: 0.9rem; overflow-x: auto;")
                    )
                }
                Box(
                    modifier = Modifier().style("style", "margin-top: 1rem;")
                ) {
                    Box(
                        modifier = Modifier().style("href", "/").style("style", "color: blue; text-decoration: underline;")
                    ) {
                        Text("Return to Home")
                    }
                }
            }
        }
    }
}

// --- Top-Level Composable Pages/Components --- 

// Moved UsersPage outside the WebResource class
@Composable
fun UsersPage(users: List<User>) {
    Column(modifier = Modifier().style("style", "width: 100%; max-width: 1200px; margin: 0 auto; padding: 1rem;")) {
        // Header section with title and description
        Box(modifier = Modifier().style("style", "background-color: var(--panel-color); padding: 1.5rem; border-radius: 8px; margin-bottom: 1.5rem;")) {
            Column {
                Text(
                    "User Management Dashboard", 
                    modifier = Modifier().style("style", "font-size: 1.75rem; font-weight: bold; color: var(--primary-color); margin-bottom: 0.5rem;")
                )
                Text(
                    "Manage your users with this Summon-powered interface", 
                    modifier = Modifier().style("style", "color: var(--text-secondary-color); font-size: 1rem;")
                )

                // Add action buttons row
                Row(modifier = Modifier().style("style", "margin-top: 1rem; gap: 0.75rem;")) {
                    Button(
                        label = "Add New User",
                        onClick = { /* HTMX Triggered */ },
                        modifier = Modifier()
                            .style("class", "btn btn-primary")
                            .style("style", "background-color: var(--primary-color); color: white; padding: 0.5rem 1rem;")
                            .style("hx-get", "/api/users/new")
                            .style("hx-target", "#user-form-container")
                            .style("hx-swap", "innerHTML")
                    )
                    Button(
                        label = "Refresh List",
                        onClick = { /* HTMX Triggered */ },
                        modifier = Modifier()
                            .style("class", "btn btn-secondary")
                            .style("style", "background-color: var(--secondary-color); color: white; padding: 0.5rem 1rem;")
                            .style("hx-get", "/api/users")
                            .style("hx-target", "#users-table-wrapper")
                            .style("hx-swap", "outerHTML")
                    )
                }
            }
        }

        // Form container with styling
        Box(
            modifier = Modifier()
                .style("id", "user-form-container")
                .style("style", "background-color: var(--panel-color); padding: 1.5rem; border-radius: 8px; margin-bottom: 1.5rem; display: none;")
        ) { 
            // This is empty by default and will be populated by HTMX
            // Adding a placeholder message
            Text(
                "User form will appear here when adding or editing a user", 
                modifier = Modifier().style("style", "color: var(--text-secondary-color); font-style: italic;")
            )
        }

        // Users table section with a title
        Box(modifier = Modifier().style("style", "background-color: var(--panel-color); padding: 1.5rem; border-radius: 8px;")) {
            Column {
                Text(
                    "Current Users", 
                    modifier = Modifier().style("style", "font-size: 1.25rem; font-weight: bold; margin-bottom: 1rem;")
                )

                // Call the Summon table composable
                WebResource().WebResourceUserTable(users)
            }
        }
    }
}

// The kotlinx.html UserForm and helper functions have been replaced with Summon-based components
