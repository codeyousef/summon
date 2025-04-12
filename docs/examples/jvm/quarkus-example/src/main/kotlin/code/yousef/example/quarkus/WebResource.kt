package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import kotlinx.html.*
import kotlinx.html.stream.createHTML
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

    private val counter = AtomicInteger(0)

    /**
     * Render the home page.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        return summonRenderer.render(title = "Summon with Quarkus - Home") {
            AppRoot {
                HeroComponent("Quarkus User")
                CurrentTimeComponent()
                FeatureCardsComponent()
                CounterComponent()
            }
        }
    }

    /**
     * Render the users page.
     */
    @GET
    @Path("/users")
    @Produces(MediaType.TEXT_HTML)
    fun users(): String {
        return summonRenderer.render(title = "User Management") {
            AppRoot {
                H2 { Text("User Management") }
                UserTableComponent(userService.getAllUsers())
            }
        }
    }

    /**
     * Render the dashboard page.
     */
    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_HTML)
    fun dashboard(): String {
        return summonRenderer.render(title = "Dashboard") {
            AppRoot {
                DashboardComponent()
            }
        }
    }

    /**
     * Render the contact page.
     */
    @GET
    @Path("/contact")
    @Produces(MediaType.TEXT_HTML)
    fun contact(): String {
        return summonRenderer.render(title = "Contact Us") {
            AppRoot {
                H2 { Text("Contact Us") }
                ContactFormComponent()
            }
        }
    }

    /**
     * Render the theme customization page.
     */
    @GET
    @Path("/theme")
    @Produces(MediaType.TEXT_HTML)
    fun theme(): String {
        return summonRenderer.render(title = "Theme Customization") {
            AppRoot {
                H2 { Text("Theme Customization") }
//                ThemeSelectionComponent()
            }
        }
    }

    /**
     * Increment the counter.
     */
    @POST
    @Path("/api/counter/increment")
    @Produces(MediaType.TEXT_PLAIN)
    fun incrementCounter(): String {
        return counter.incrementAndGet().toString()
    }

    /**
     * Decrement the counter.
     */
    @POST
    @Path("/api/counter/decrement")
    @Produces(MediaType.TEXT_PLAIN)
    fun decrementCounter(): String {
        return counter.decrementAndGet().toString()
    }

    /**
     * Get the user add/edit form.
     */
    @GET
    @Path("/api/users/form")
    @Produces(MediaType.TEXT_HTML)
    fun getUserForm(): String {
        return UserForm(null, "/api/users").toString()
    }

    /**
     * Get the user edit form.
     */
    @GET
    @Path("/api/users/{id}/edit")
    @Produces(MediaType.TEXT_HTML)
    fun getUserEditForm(@RestPath id: Int): String {
        val user = userService.getUserById(id) ?: return "User not found"

        return UserForm(user, "/api/users/$id").toString()
    }

    /**
     * Cancel the user form.
     */
    @GET
    @Path("/api/users/cancel-form")
    @Produces(MediaType.TEXT_HTML)
    fun cancelUserForm(): String {
        return ""
    }

    /**
     * Add a new user.
     */
    @POST
    @Path("/api/users")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    fun addUser(
        @RestForm("name") name: String,
        @RestForm("email") email: String,
        @RestForm("role") role: String
    ): String {
        userService.addUser(name, email, role)

        return UserTableComponent(userService.getAllUsers()).toString()
    }

    /**
     * Update a user.
     */
    @PUT
    @Path("/api/users/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    fun updateUser(
        @RestPath id: Int,
        @RestForm("name") name: String,
        @RestForm("email") email: String,
        @RestForm("role") role: String
    ): String {
        userService.updateUser(id, name, email, role)

        return UserTableComponent(userService.getAllUsers()).toString()
    }

    /**
     * Delete a user.
     */
    @DELETE
    @Path("/api/users/{id}")
    @Produces(MediaType.TEXT_HTML)
    fun deleteUser(@RestPath id: Int): String {
        userService.deleteUser(id)

        return UserTableComponent(userService.getAllUsers()).toString()
    }

    /**
     * Process contact form submission.
     */
    @POST
    @Path("/api/contact")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    fun processContactForm(
        @RestForm("name") name: String,
        @RestForm("email") email: String,
        @RestForm("subject") subject: String,
        @RestForm("message") message: String
    ): String {
        // In a real app, we would process the form data

        return createHTML().div {
            attributes["class"] = "card"
            style = "background-color: #e8f5e9; border-color: #4caf50;"
            h3 { +"Message Sent!" }
            p {
                +"Thank you for contacting us, $name. We've received your message and will respond to $email shortly."
            }
            h4 { +"Message Details:" }
            p { +"Subject: $subject" }
            p { +"Message: $message" }
            div {
                button {
                    attributes["class"] = "btn"
                    attributes["hx-get"] = "/contact"
                    attributes["hx-target"] = "body"
                    attributes["hx-swap"] = "outerHTML"
                    +"Send Another Message"
                }
            }
        }.toString()
    }

    /**
     * Update theme.
     */
    @POST
    @Path("/api/theme/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    fun updateTheme(
        @RestForm("primary-color") primaryColor: String?,
        @RestForm("text-color") textColor: String?,
        @RestForm("background-color") backgroundColor: String?,
        @RestForm("font-size") fontSize: String?
    ): String {
        // In a real app, we would save these preferences

        return createHTML().div {
            id = "theme-preview"
            style =
                "margin-top: 2rem; padding: 1.5rem; border: 1px solid #ddd; border-radius: 8px; background-color: ${backgroundColor ?: "var(--background-color)"};"
            h4 {
                style = "color: ${primaryColor ?: "var(--primary-color)"};"
                +"Theme Preview"
            }
            p {
                style = "color: ${textColor ?: "var(--text-color)"}; font-size: ${fontSize ?: "16"}px;"
                +"This is a preview of how your theme will look with the selected colors and font size."
            }
            button {
                attributes["class"] = "btn"
                style = "background-color: ${primaryColor ?: "var(--primary-color)"};"
                +"Primary Button"
            }
            button {
                attributes["class"] = "btn btn-secondary"
                style = "margin-left: 1rem;"
                +"Secondary Button"
            }
        }.toString()
    }

    /**
     * Apply a predefined theme class.
     */
    @POST
    @Path("/api/theme/{themeClass}")
    @Produces(MediaType.TEXT_HTML)
    fun applyThemeClass(@RestPath themeClass: String): String {
        // Map theme classes to CSS variables
        val cssVariables = when (themeClass) {
            "light" -> mapOf(
                "--primary-color" to "#3498db",
                "--background-color" to "#ffffff",
                "--text-color" to "#333333",
                "--surface-color" to "#f5f5f5",
                "--error-color" to "#e74c3c",
                "--success-color" to "#2ecc71",
                "--info-color" to "#3498db",
                "--warning-color" to "#f39c12"
            )

            "dark" -> mapOf(
                "--primary-color" to "#9b59b6",
                "--background-color" to "#121212",
                "--text-color" to "#f5f5f5",
                "--surface-color" to "#1e1e1e",
                "--error-color" to "#e74c3c",
                "--success-color" to "#2ecc71",
                "--info-color" to "#3498db",
                "--warning-color" to "#f39c12"
            )

            "forest" -> mapOf(
                "--primary-color" to "#27ae60",
                "--background-color" to "#f0f9f4",
                "--text-color" to "#2c3e50",
                "--surface-color" to "#e8f5e9",
                "--error-color" to "#e74c3c",
                "--success-color" to "#27ae60",
                "--info-color" to "#3498db",
                "--warning-color" to "#f39c12"
            )

            "ocean" -> mapOf(
                "--primary-color" to "#2980b9",
                "--background-color" to "#ebf5fb",
                "--text-color" to "#2c3e50",
                "--surface-color" to "#e1f5fe",
                "--error-color" to "#e74c3c",
                "--success-color" to "#2ecc71",
                "--info-color" to "#2980b9",
                "--warning-color" to "#f39c12"
            )

            else -> mapOf(
                "--primary-color" to "#3498db",
                "--background-color" to "#ffffff",
                "--text-color" to "#333333",
                "--surface-color" to "#f5f5f5"
            )
        }

        // Return HTML with embedded script to update CSS variables
        return createHTML().div {
            attributes["hx-swap-oob"] = "true"
            id = "theme-response"
            script {
                unsafe {
                    +"""
                    (function() {
                        // Update CSS variables
                        ${
                        cssVariables.entries.joinToString("\n                        ") { (key, value) ->
                            "document.documentElement.style.setProperty('$key', '$value');"
                        }
                    }
                        
                        // Update the current theme display
                        const currentThemeElement = document.getElementById('current-theme-name');
                        if (currentThemeElement) {
                            currentThemeElement.textContent = '${themeClass.capitalize()}';
                        }
                        
                        // Update active theme selection
                        document.querySelectorAll('.theme-option').forEach(option => {
                            option.classList.remove('active');
                            if (option.getAttribute('data-theme') === '$themeClass') {
                                option.classList.add('active');
                            }
                        });
                        
                        // Notify the application that theme has changed
                        const event = new CustomEvent('themeChanged', { 
                            detail: { theme: '$themeClass' } 
                        });
                        document.dispatchEvent(event);
                        
                        // HTMX reload to apply changes if needed
                        if (window.htmx) {
                            htmx.trigger('#main-content', 'themeChanged');
                        }
                    })();
                    """
                }
            }
        }.toString()
    }

    /**
     * Serve the script.js file.
     */
    @GET
    @Path("/script.js")
    @Produces("application/javascript")
    fun getScript(): String {
        return """
            document.addEventListener('DOMContentLoaded', function() {
                // Color input value display
                const colorInputs = document.querySelectorAll('input[type="color"]');
                colorInputs.forEach(input => {
                    const valueSpan = document.getElementById(`\${'$'}{input.id}-value`);
                    if (valueSpan) {
                        input.addEventListener('input', function() {
                            valueSpan.textContent = input.value;
                        });
                    }
                });
                
                // Font size slider value display
                const fontSizeInput = document.getElementById('font-size');
                if (fontSizeInput) {
                    const valueSpan = document.getElementById('font-size-value');
                    if (valueSpan) {
                        fontSizeInput.addEventListener('input', function() {
                            valueSpan.textContent = `\${'$'}{fontSizeInput.value}px`;
                        });
                    }
                }
            });
        """.trimIndent()
    }
}

@Composable
fun UserForm(
    user: User? = null,
    action: String,
    method: String = "post"
) {
    Box(
        modifier = Modifier()
            .style("hx-target", "#users-table")
            .style("hx-swap", "outerHTML")
            .style("class", "user-form")
            .style("action", action)
            .style("method", method)
    ) {
        Box(modifier = Modifier().style("class", "form-title")) {
            Text(if (user == null) "Add New User" else "Edit User")
        }

        // Hidden input for user id when editing
        if (user != null) {
            Box(modifier = Modifier().style("type", "hidden").style("name", "id").style("value", "${user.id}")) {}
        }

        // Name field
        FormField(label = "Name", name = "name", value = user?.name ?: "")

        // Email field
        FormField(label = "Email", name = "email", value = user?.email ?: "")

        // Role field
        FormField(label = "Role", name = "role", value = user?.role ?: "")

        // Buttons
        Row(modifier = Modifier().style("class", "form-buttons")) {
            Button(
                label = "Cancel",
                onClick = {},
                modifier = Modifier()
                    .style("class", "btn btn-secondary")
                    .style("hx-get", "/api/users")
                    .style("hx-target", "#user-form-container")
                    .style("hx-swap", "innerHTML")
            )

            Button(
                label = if (user == null) "Add User" else "Update User",
                onClick = {},
                modifier = Modifier()
                    .style("class", "btn btn-primary")
                    .style("type", "submit")
            )
        }
    }
}

@Composable
fun FormField(label: String, name: String, value: String = "") {
    Box(modifier = Modifier().style("class", "form-group")) {
        Box(modifier = Modifier().style("for", name)) {
            Text(label)
        }

        TextField(
            value = value,
            onValueChange = {},
            modifier = Modifier()
                .style("name", name)
                .style("id", name)
                .style("type", "text")
                .style("class", "form-control")
                .style("value", value)
                .style("required", "true")
        )
    }
} 