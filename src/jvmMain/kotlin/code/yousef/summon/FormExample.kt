package code.yousef.summon

import kotlinx.html.stream.appendHTML
import kotlinx.html.html
import kotlinx.html.head
import kotlinx.html.title
import kotlinx.html.body
import kotlinx.html.style
import kotlinx.html.unsafe

/**
 * Example demonstrating form components.
 */
fun createContactForm(): String {
    // Create form states
    val nameState = mutableStateOf("")
    val emailState = mutableStateOf("")
    val messageState = mutableStateOf("")
    
    // Create a contact form with validation
    val contactForm = Form(
        content = listOf(
            Text(
                "Contact Us",
                Modifier()
                    .fontSize("24px")
                    .fontWeight("bold")
                    .margin("0 0 20px 0")
            ),
            TextField(
                state = nameState,
                label = "Name",
                placeholder = "Enter your name",
                validators = listOf(
                    RequiredValidator("Name is required")
                ),
                modifier = Modifier()
                    .padding("8px 12px")
                    .border("1px", "solid", "#ccc")
                    .borderRadius("4px")
                    .width("100%")
                    .margin("0 0 16px 0")
            ),
            TextField(
                state = emailState,
                label = "Email",
                placeholder = "Enter your email",
                type = TextFieldType.Email,
                validators = listOf(
                    RequiredValidator("Email is required"),
                    EmailValidator()
                ),
                modifier = Modifier()
                    .padding("8px 12px")
                    .border("1px", "solid", "#ccc")
                    .borderRadius("4px")
                    .width("100%")
                    .margin("0 0 16px 0")
            ),
            TextField(
                state = messageState,
                label = "Message",
                placeholder = "Enter your message",
                validators = listOf(
                    RequiredValidator("Message is required"),
                    MinLengthValidator(10, "Message must be at least 10 characters long")
                ),
                modifier = Modifier()
                    .padding("8px 12px")
                    .border("1px", "solid", "#ccc")
                    .borderRadius("4px")
                    .width("100%")
                    .height("100px")
                    .margin("0 0 16px 0")
            ),
            Button(
                label = "Submit",
                onClick = {},
                modifier = Modifier()
                    .background("#4CAF50")
                    .color("white")
                    .padding("10px 20px")
                    .border("none", "solid", "transparent")
                    .borderRadius("4px")
                    .hover(mapOf("background-color" to "#45a049"))
            )
        ),
        onSubmit = { formData ->
            println("Form submitted with data: $formData")
        },
        modifier = Modifier()
            .padding("20px")
            .background("#f9f9f9")
            .border("1px", "solid", "#e0e0e0")
            .borderRadius("8px")
            .width("500px")
    )
    
    // Create HTML output
    val output = StringBuilder()
    val consumer = output.appendHTML()
    
    consumer.html {
        head {
            title("Contact Form Example")
            style {
                unsafe {
                    raw("""
                    * {
                        box-sizing: border-box;
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    }
                    body {
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        min-height: 100vh;
                        margin: 0;
                        padding: 20px;
                        background-color: #f0f2f5;
                    }
                    """.trimIndent())
                }
            }
            
            // Add the generated hover styles
            style {
                unsafe {
                    raw(CssClassStore.generateCss())
                }
            }
        }
        body {
            // Render our form
            contactForm.compose(consumer)
        }
    }
    
    return output.toString()
} 