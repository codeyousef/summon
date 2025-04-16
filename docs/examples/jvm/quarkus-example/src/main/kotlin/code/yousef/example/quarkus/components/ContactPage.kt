package code.yousef.example.quarkus.components

// Import individual extension functions
// Import Modifier class properly
// Import specific modifier functions
import code.yousef.example.quarkus.User
import code.yousef.example.quarkus.utils.marginVH
import code.yousef.example.quarkus.utils.padding
import code.yousef.example.quarkus.utils.paddingVH
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextArea
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.Composable
import kotlinx.html.script
import kotlinx.html.unsafe

/**
 * Contact page component with form.
 */
@Composable
fun ContactPage(currentUser: User? = null) {
    ThemeWrapper {
        // Add navbar
        NavBar(currentUser)

        // Header section
        Box(
            modifier = Modifier().fillMaxWidth()
                .backgroundColor("#007bff")
                .paddingVH(vertical = "60px", horizontal = "24px")
                .color("#fff")
        ) {
            Column(
                modifier = Modifier().fillMaxWidth()
                    .maxWidth("1200px")
                    .marginVH(vertical = "0", horizontal = "auto")
                    .textAlign("center")
            ) {
                Text(
                    text = "Contact Us",
                    modifier = Modifier().fontSize("36px")
                        .fontWeight("bold")
                        .marginBottom("16px")
                )

                Text(
                    text = "Get in touch with our team for support or feedback",
                    modifier = Modifier().fontSize("18px")
                        .maxWidth("600px")
                        .marginVH(vertical = "0", horizontal = "auto")
                        .opacity("0.9")
                )
            }
        }

        // Contact section with form and info
        Box(
            modifier = Modifier().fillMaxWidth()
                .paddingVH(vertical = "60px", horizontal = "24px")
                .backgroundColor("#fff")
        ) {
            Row(
                modifier = Modifier().maxWidth("1200px")
                    .marginVH(vertical = "0", horizontal = "auto")
                    .gap("48px")
                    .flexWrap("wrap")
                    .justifyContent("center")
            ) {
                // Contact information
                Column(
                    modifier = Modifier().width("400px")
                        .maxWidth("100%")
                        .padding(all = "24px")
                ) {
                    Text(
                        text = "Our Information",
                        modifier = Modifier().fontSize("24px")
                            .fontWeight("bold")
                            .marginBottom("32px")
                    )

                    ContactInfoItem(
                        icon = "📍",
                        title = "Office Location",
                        details = "123 Tech Street, Silicon Valley, CA 94043"
                    )

                    ContactInfoItem(
                        icon = "📞",
                        title = "Phone Number",
                        details = "+1 (555) 123-4567"
                    )

                    ContactInfoItem(
                        icon = "✉️",
                        title = "Email Address",
                        details = "contact@example.com"
                    )

                    ContactInfoItem(
                        icon = "⏰",
                        title = "Working Hours",
                        details = "Monday-Friday: 9AM - 6PM"
                    )
                }

                // Contact form
                Column(
                    modifier = Modifier().flex("1")
                        .minWidth("400px")
                        .maxWidth("100%")
                        .padding(all = "32px")
                        .backgroundColor("#f8f9fa")
                        .borderRadius("8px")
                        .style("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.05)")
                ) {
                    Text(
                        text = "Send Us a Message",
                        modifier = Modifier().fontSize("24px")
                            .fontWeight("bold")
                            .marginBottom("24px")
                    )

                    Box(
                        modifier = Modifier().fillMaxWidth()
                            .attribute("id", "contact-form")
                            .attribute("hx-post", "/api/contact")
                            .attribute("hx-target", "#form-response")
                            .attribute("hx-swap", "outerHTML")
                    ) {
                        Column(
                            modifier = Modifier().gap("20px")
                                .fillMaxWidth()
                        ) {
                            // Name field
                            Column(modifier = Modifier().gap("8px")) {
                                Text(
                                    text = "Your Name",
                                    modifier = Modifier().fontSize("14px")
                                        .fontWeight("medium")
                                        .color("#495057")
                                )

                                TextField(
                                    value = currentUser?.name ?: "",
                                    onValueChange = { /* This will be handled by the form submission */ },
                                    modifier = Modifier().fillMaxWidth()
                                        .paddingVH(vertical = "10px", horizontal = "12px")
                                        .border("1px", "solid", "#ced4da")
                                        .borderRadius("4px")
                                        .fontSize("16px")
                                        .backgroundColor("#fff")
                                        .attribute("name", "name")
                                        .attribute("placeholder", "Enter your name")
                                        .attribute("required", "true")
                                )
                            }

                            // Email field
                            Column(modifier = Modifier().gap("8px")) {
                                Text(
                                    text = "Email Address",
                                    modifier = Modifier().fontSize("14px")
                                        .fontWeight("medium")
                                        .color("#495057")
                                )

                                TextField(
                                    value = currentUser?.email ?: "",
                                    onValueChange = { /* This will be handled by the form submission */ },
                                    modifier = Modifier().fillMaxWidth()
                                        .paddingVH(vertical = "10px", horizontal = "12px")
                                        .border("1px", "solid", "#ced4da")
                                        .borderRadius("4px")
                                        .fontSize("16px")
                                        .backgroundColor("#fff")
                                        .attribute("type", "email")
                                        .attribute("name", "email")
                                        .attribute("placeholder", "Enter your email")
                                        .attribute("required", "true")
                                )
                            }

                            // Subject field
                            Column(modifier = Modifier().gap("8px")) {
                                Text(
                                    text = "Subject",
                                    modifier = Modifier().fontSize("14px")
                                        .fontWeight("medium")
                                        .color("#495057")
                                )

                                TextField(
                                    value = "",
                                    onValueChange = { /* This will be handled by the form submission */ },
                                    modifier = Modifier().fillMaxWidth()
                                        .paddingVH(vertical = "10px", horizontal = "12px")
                                        .border("1px", "solid", "#ced4da")
                                        .borderRadius("4px")
                                        .fontSize("16px")
                                        .backgroundColor("#fff")
                                        .attribute("name", "subject")
                                        .attribute("placeholder", "What is this regarding?")
                                        .attribute("required", "true")
                                )
                            }

                            // Message field
                            Column(modifier = Modifier().gap("8px")) {
                                Text(
                                    text = "Message",
                                    modifier = Modifier().fontSize("14px")
                                        .fontWeight("medium")
                                        .color("#495057")
                                )

                                TextArea(
                                    value = "",
                                    onValueChange = { /* This will be handled by the form submission */ },
                                    modifier = Modifier().fillMaxWidth()
                                        .height("120px")
                                        .paddingVH(vertical = "10px", horizontal = "12px")
                                        .border("1px", "solid", "#ced4da")
                                        .borderRadius("4px")
                                        .fontSize("16px")
                                        .backgroundColor("#fff")
                                        .attribute("name", "message")
                                        .attribute("placeholder", "Enter your message here...")
                                        .attribute("required", "true")
                                )
                            }

                            // Response placeholder
                            Box(
                                modifier = Modifier().fillMaxWidth()
                                    .marginTop("16px")
                                    .display("none")
                                    .attribute("id", "form-response")
                            ) {}

                            // Submit button
                            Button(
                                onClick = {},
                                label = "Send Message",
                                modifier = Modifier().fillMaxWidth()
                                    .paddingVH(vertical = "12px", horizontal = "0")
                                    .backgroundColor("#007bff")
                                    .color("#fff")
                                    .fontSize("16px")
                                    .fontWeight("medium")
                                    .border("none", "", "")
                                    .borderRadius("4px")
                                    .cursor("pointer")
                                    .marginTop("8px")
                                    .attribute("type", "submit")
                            )
                        }
                    }
                }
            }
        }

        // Map section
        Box(
            modifier = Modifier().fillMaxWidth()
                .height("400px")
                .backgroundColor("#e9ecef")
                .attribute("id", "map")
        ) {
            // Placeholder for map
            Box(
                modifier = Modifier().fillMaxWidth()
                    .fillMaxHeight()
                    .display("flex")
                    .justifyContent("center")
                    .alignItems("center")
            ) {
                Text(
                    text = "Map will be displayed here",
                    modifier = Modifier().fontSize("18px")
                        .color("#6c757d")
                )
            }
        }

        // Define the JavaScript as a constant string
        val formHandlingScript = """
            document.addEventListener('htmx:afterSwap', function(event) {
                const form = document.getElementById('contact-form');
                if (!form) return;
                
                form.addEventListener('submit', function(e) {
                    const email = form.querySelector('input[name="email"]').value;
                    const errorDiv = document.getElementById('form-response');
                    
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    if (!emailRegex.test(email)) {
                        e.preventDefault();
                        if(errorDiv) {
                            errorDiv.textContent = 'Please enter a valid email address';
                            errorDiv.style.display = 'block';
                            errorDiv.style.padding = '12px';
                            errorDiv.style.backgroundColor = '#f8d7da';
                            errorDiv.style.color = '#842029';
                            errorDiv.style.borderRadius = '4px';
                            errorDiv.style.marginTop = '16px';
                        }
                        return false;
                    }
                });
            });
            
            document.addEventListener('htmx:afterRequest', function(event) {
                if (event.detail.successful && event.detail.target.id === 'form-response') {
                    const form = document.getElementById('contact-form');
                    if (form) {
                        const inputs = form.querySelectorAll('input:not([type="submit"]), textarea');
                        inputs.forEach(function(input) { input.value = ''; });
                        
                        const responseDiv = document.getElementById('form-response');
                         if(responseDiv) {
                            responseDiv.textContent = 'Your message has been sent successfully!';
                            responseDiv.style.display = 'block';
                            responseDiv.style.padding = '12px';
                            responseDiv.style.backgroundColor = '#d1e7dd';
                            responseDiv.style.color = '#0f5132';
                            responseDiv.style.borderRadius = '4px';
                            responseDiv.style.marginTop = '16px';
                            responseDiv.scrollIntoView({ behavior: 'smooth' });
                         }
                    }
                }
            });
            
            console.log('Map initialization would happen here');
            """.trimIndent()

        // Use Box with modifiers to render the script tag
        Box(modifier = Modifier()
            .style("element", "script") // Tell renderer to create a <script> tag
            .style("content", formHandlingScript) // Inject the raw JS content
        ) {}
    }
}

@Composable
private fun ContactInfoItem(icon: String, title: String, details: String) {
    Row(
        modifier = Modifier().gap("16px")
            .marginBottom("24px")
            .alignItems("flex-start")
    ) {
        Text(
            text = icon,
            modifier = Modifier().fontSize("24px")
                .marginTop("2px")
        )

        Column(
            modifier = Modifier().gap("4px")
        ) {
            Text(
                text = title,
                modifier = Modifier().fontSize("16px")
                    .fontWeight("medium")
                    .color("#212529")
            )

            Text(
                text = details,
                modifier = Modifier().fontSize("14px")
                    .color("#6c757d")
                    .lineHeight("1.5")
            )
        }
    }
}

/**
 * Contact form submission success message component.
 */
@Composable
fun ContactFormSuccess(name: String, email: String, subject: String, message: String) {
    Box(
        modifier = Modifier()
            .attribute("id", "contact-form")
            .attribute("class", "card")
            .padding("2rem")
            .maxWidth("800px")
            .marginVH(vertical = "0", horizontal = "auto")
    ) {
        Column(modifier = Modifier().width("100%")) {
            // Success message
            Box(
                modifier = Modifier()
                    .attribute("class", "alert alert-success")
                    .backgroundColor("#d4edda")
                    .color("#155724")
                    .padding("1rem")
                    .borderRadius("0.25rem")
                    .marginBottom("1.5rem")
            ) {
                Text(
                    text = "Thank you for your message, $name! We'll get back to you soon."
                )
            }

            // Submission details
            Box(
                modifier = Modifier()
                    .border("1px", "solid", "#dee2e6")
                    .borderRadius("0.25rem")
                    .padding("1.5rem")
                    .marginBottom("1.5rem")
            ) {
                Column(modifier = Modifier().width("100%").gap("1rem")) {
                    Text(
                        text = "Submission Details:",
                        modifier = Modifier().fontWeight("bold").fontSize("1.25rem")
                    )

                    // Name
                    Box(modifier = Modifier().marginBottom("0.5rem")) {
                        Text(
                            text = "Name: $name"
                        )
                    }

                    // Email
                    Box(modifier = Modifier().marginBottom("0.5rem")) {
                        Text(
                            text = "Email: $email"
                        )
                    }

                    // Subject
                    Box(modifier = Modifier().marginBottom("0.5rem")) {
                        Text(
                            text = "Subject: $subject"
                        )
                    }

                    // Message
                    Box(modifier = Modifier()) {
                        Text(
                            text = "Message: $message"
                        )
                    }
                }
            }

            // Return button
            Button(
                onClick = {},
                label = "Return to Contact Form",
                modifier = Modifier()
                    .attribute("hx-get", "/api/contact")
                    .attribute("hx-target", "#contact-form")
                    .attribute("hx-swap", "outerHTML")
                    .attribute("class", "btn btn-secondary")
                    .backgroundColor("#6c757d")
                    .color("white")
                    .border("none", "", "")
                    .paddingVH(vertical = "0.75rem", horizontal = "1.5rem")
                    .borderRadius("0.25rem")
                    .cursor("pointer")
                    .fontWeight("500")
            )
        }
    }
} 