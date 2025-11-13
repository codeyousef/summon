package codes.yousef.summon.ssr

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Real-world scenario tests for SSR to verify practical applications and use cases
 */
class SSRRealWorldTest {

    @Test
    fun testECommerceProductListing() {
        val renderer = PlatformRenderer()
        
        data class Product(
            val id: Int,
            val name: String,
            val price: Double,
            val category: String,
            val inStock: Boolean
        )
        
        @Composable
        fun ProductListing() {
            val products = remember { 
                mutableStateOf(listOf(
                    Product(1, "Laptop", 999.99, "Electronics", true),
                    Product(2, "Mouse", 29.99, "Electronics", true),
                    Product(3, "Book", 19.99, "Education", false),
                    Product(4, "Headphones", 149.99, "Electronics", true)
                ))
            }
            
            val cartItems = remember { mutableStateOf(0) }
            
            Column(modifier = Modifier()) {
                Text("Product Catalog", modifier = Modifier())
                Text("Cart: ${cartItems.value} items", modifier = Modifier())
                
                products.value.forEach { product ->
                    Row(modifier = Modifier()) {
                        Column(modifier = Modifier()) {
                            Text(product.name, modifier = Modifier())
                            Text("$${product.price}", modifier = Modifier())
                            Text("Category: ${product.category}", modifier = Modifier())
                            Text(if (product.inStock) "In Stock" else "Out of Stock", modifier = Modifier())
                        }
                        
                        if (product.inStock) {
                            Button(
                                onClick = { 
                                    cartItems.value += 1
                                },
                                label = "Add to Cart",
                                modifier = Modifier()
                            )
                        } else {
                            Text("Unavailable", modifier = Modifier())
                        }
                    }
                }
                
                if (cartItems.value > 0) {
                    Button(
                        onClick = { /* proceed to checkout */ },
                        label = "Checkout",
                        modifier = Modifier()
                    )
                }
            }
        }
        
        val html = renderer.renderComposableRoot {
            ProductListing()
        }
        
        // Verify product catalog structure
        assertTrue(html.contains("Product Catalog"), "Should contain catalog title")
        assertTrue(html.contains("Cart: 0 items"), "Should show initial cart state")
        
        // Verify product listings
        assertTrue(html.contains("Laptop"), "Should list laptop")
        assertTrue(html.contains("$999.99"), "Should show laptop price")
        assertTrue(html.contains("Electronics"), "Should show category")
        assertTrue(html.contains("In Stock"), "Should show stock status")
        assertTrue(html.contains("Out of Stock"), "Should show out of stock status")
        
        // Verify interactive elements
        assertTrue(html.contains("Add to Cart"), "Should have add to cart buttons")
        assertTrue(html.contains("Unavailable"), "Should show unavailable for out of stock")
        
        println("E-commerce HTML preview: ${html.take(500)}")
    }

    @Test
    fun testBlogPostWithComments() {
        val renderer = PlatformRenderer()
        
        data class Comment(val author: String, val content: String, val timestamp: String)
        data class BlogPost(val title: String, val content: String, val author: String, val date: String)
        
        @Composable
        fun BlogPostPage() {
            val post = remember {
                mutableStateOf(BlogPost(
                    title = "Getting Started with Server-Side Rendering",
                    content = "SSR is a technique that allows you to render your application on the server before sending it to the client. This improves SEO and initial page load times.",
                    author = "John Doe",
                    date = "2024-01-15"
                ))
            }
            
            val comments = remember {
                mutableStateOf(listOf(
                    Comment("Alice", "Great article! Very helpful.", "2024-01-16"),
                    Comment("Bob", "I learned a lot from this post.", "2024-01-17"),
                    Comment("Charlie", "Could you write more about hydration?", "2024-01-18")
                ))
            }
            
            val newCommentText = remember { mutableStateOf("") }
            val showCommentForm = remember { mutableStateOf(false) }
            
            Column(modifier = Modifier()) {
                // Blog post header
                Text(post.value.title, modifier = Modifier())
                Text("By ${post.value.author} on ${post.value.date}", modifier = Modifier())
                
                // Blog post content
                Text(post.value.content, modifier = Modifier())
                
                // Comments section
                Text("Comments (${comments.value.size})", modifier = Modifier())
                
                comments.value.forEach { comment ->
                    Column(modifier = Modifier()) {
                        Text("${comment.author} - ${comment.timestamp}", modifier = Modifier())
                        Text(comment.content, modifier = Modifier())
                    }
                }
                
                // Comment form toggle
                Button(
                    onClick = { 
                        showCommentForm.value = !showCommentForm.value
                    },
                    label = if (showCommentForm.value) "Hide Comment Form" else "Add Comment",
                    modifier = Modifier()
                )
                
                if (showCommentForm.value) {
                    Column(modifier = Modifier()) {
                        Text("Write a comment:", modifier = Modifier())
                        // Note: In a real app, this would be a TextField
                        Text("Comment field: ${newCommentText.value}", modifier = Modifier())
                        
                        Button(
                            onClick = { 
                                // Add comment logic
                                newCommentText.value = ""
                                showCommentForm.value = false
                            },
                            label = "Submit Comment",
                            modifier = Modifier()
                        )
                    }
                }
            }
        }
        
        val html = renderer.renderComposableRoot {
            BlogPostPage()
        }
        
        // Verify blog post structure
        assertTrue(html.contains("Getting Started with Server-Side Rendering"), "Should contain post title")
        assertTrue(html.contains("By John Doe on 2024-01-15"), "Should contain author and date")
        assertTrue(html.contains("SSR is a technique"), "Should contain post content")
        
        // Verify comments
        assertTrue(html.contains("Comments (3)"), "Should show comment count")
        assertTrue(html.contains("Alice"), "Should show comment authors")
        assertTrue(html.contains("Great article!"), "Should show comment content")
        
        // Verify interactive elements
        assertTrue(html.contains("Add Comment"), "Should have add comment button")
        
        println("Blog post HTML preview: ${html.take(500)}")
    }

    @Test
    fun testDashboardWithCharts() {
        val renderer = PlatformRenderer()
        
        data class ChartData(val label: String, val value: Double)
        data class Metric(val name: String, val current: Double, val previous: Double)
        
        @Composable
        fun Dashboard() {
            val metrics = remember {
                mutableStateOf(listOf(
                    Metric("Revenue", 125000.0, 118000.0),
                    Metric("Users", 3500.0, 3200.0),
                    Metric("Conversion Rate", 2.8, 2.5)
                ))
            }
            
            val chartData = remember {
                mutableStateOf(listOf(
                    ChartData("Jan", 100000.0),
                    ChartData("Feb", 110000.0),
                    ChartData("Mar", 125000.0),
                    ChartData("Apr", 130000.0)
                ))
            }
            
            val selectedPeriod = remember { mutableStateOf("Monthly") }
            
            Column(modifier = Modifier()) {
                Text("Business Dashboard", modifier = Modifier())
                
                // Period selector
                Row(modifier = Modifier()) {
                    Text("Period: ", modifier = Modifier())
                    listOf("Daily", "Weekly", "Monthly").forEach { period ->
                        Button(
                            onClick = { selectedPeriod.value = period },
                            label = period,
                            modifier = Modifier()
                        )
                    }
                }
                
                Text("Selected: ${selectedPeriod.value}", modifier = Modifier())
                
                // Metrics cards
                Text("Key Metrics", modifier = Modifier())
                metrics.value.forEach { metric ->
                    Row(modifier = Modifier()) {
                        Column(modifier = Modifier()) {
                            Text(metric.name, modifier = Modifier())
                            Text("Current: ${metric.current}", modifier = Modifier())
                            Text("Previous: ${metric.previous}", modifier = Modifier())
                            
                            val change = ((metric.current - metric.previous) / metric.previous * 100)
                            val changeText = if (change > 0) "+${change.toInt()}%" else "${change.toInt()}%"
                            Text("Change: $changeText", modifier = Modifier())
                        }
                    }
                }
                
                // Chart representation (textual for SSR)
                Text("Revenue Chart", modifier = Modifier())
                chartData.value.forEach { data ->
                    Row(modifier = Modifier()) {
                        Text(data.label, modifier = Modifier())
                        Text("$${data.value.toInt()}", modifier = Modifier())
                        // ASCII bar chart representation
                        val barLength = (data.value / 10000).toInt()
                        Text("█".repeat(barLength), modifier = Modifier())
                    }
                }
            }
        }
        
        val html = renderer.renderComposableRoot {
            Dashboard()
        }
        
        // Verify dashboard structure
        assertTrue(html.contains("Business Dashboard"), "Should contain dashboard title")
        assertTrue(html.contains("Selected: Monthly"), "Should show selected period")
        
        // Verify metrics
        assertTrue(html.contains("Key Metrics"), "Should contain metrics section")
        assertTrue(html.contains("Revenue"), "Should show revenue metric")
        assertTrue(html.contains("Current: 125000"), "Should show current values")
        assertTrue(html.contains("Change:"), "Should show percentage changes")
        
        // Verify chart
        assertTrue(html.contains("Revenue Chart"), "Should contain chart section")
        assertTrue(html.contains("Jan"), "Should show chart labels")
        assertTrue(html.contains("█"), "Should show ASCII chart bars")
        
        // Verify period buttons
        assertTrue(html.contains("Daily"), "Should have period buttons")
        assertTrue(html.contains("Weekly"), "Should have period buttons")
        assertTrue(html.contains("Monthly"), "Should have period buttons")
        
        println("Dashboard HTML preview: ${html.take(500)}")
    }

    @Test
    fun testFormWithValidation() {
        val renderer = PlatformRenderer()
        
        data class FormData(
            val email: String = "",
            val password: String = "",
            val confirmPassword: String = "",
            val termsAccepted: Boolean = false
        )
        
        data class ValidationErrors(
            val email: String? = null,
            val password: String? = null,
            val confirmPassword: String? = null,
            val terms: String? = null
        )
        
        @Composable
        fun RegistrationForm() {
            val formData = remember { mutableStateOf(FormData()) }
            val errors = remember { mutableStateOf(ValidationErrors()) }
            val isSubmitting = remember { mutableStateOf(false) }
            val submitMessage = remember { mutableStateOf("") }
            
            fun validateForm(): ValidationErrors {
                return ValidationErrors(
                    email = if (formData.value.email.isEmpty()) "Email is required" 
                           else if (!formData.value.email.contains("@")) "Invalid email format" 
                           else null,
                    password = if (formData.value.password.length < 8) "Password must be at least 8 characters" else null,
                    confirmPassword = if (formData.value.password != formData.value.confirmPassword) "Passwords don't match" else null,
                    terms = if (!formData.value.termsAccepted) "You must accept terms" else null
                )
            }
            
            Column(modifier = Modifier()) {
                Text("Create Account", modifier = Modifier())
                
                if (submitMessage.value.isNotEmpty()) {
                    Text(submitMessage.value, modifier = Modifier())
                }
                
                // Email field
                Text("Email:", modifier = Modifier())
                Text("Value: ${formData.value.email}", modifier = Modifier())
                errors.value.email?.let { error ->
                    Text("Error: $error", modifier = Modifier())
                }
                
                // Password field
                Text("Password:", modifier = Modifier())
                Text("Value: ${"*".repeat(formData.value.password.length)}", modifier = Modifier())
                errors.value.password?.let { error ->
                    Text("Error: $error", modifier = Modifier())
                }
                
                // Confirm password field
                Text("Confirm Password:", modifier = Modifier())
                Text("Value: ${"*".repeat(formData.value.confirmPassword.length)}", modifier = Modifier())
                errors.value.confirmPassword?.let { error ->
                    Text("Error: $error", modifier = Modifier())
                }
                
                // Terms checkbox
                Row(modifier = Modifier()) {
                    Text(if (formData.value.termsAccepted) "[x]" else "[ ]", modifier = Modifier())
                    Text("I accept the terms and conditions", modifier = Modifier())
                }
                errors.value.terms?.let { error ->
                    Text("Error: $error", modifier = Modifier())
                }
                
                // Buttons
                Row(modifier = Modifier()) {
                    Button(
                        onClick = { 
                            // Simulate form data entry
                            formData.value = FormData(
                                email = "test@example.com",
                                password = "password123",
                                confirmPassword = "password123",
                                termsAccepted = true
                            )
                        },
                        label = "Fill Sample Data",
                        modifier = Modifier()
                    )
                    
                    Button(
                        onClick = { 
                            val validationErrors = validateForm()
                            errors.value = validationErrors
                            
                            val hasErrors = listOf(
                                validationErrors.email,
                                validationErrors.password,
                                validationErrors.confirmPassword,
                                validationErrors.terms
                            ).any { it != null }
                            
                            if (!hasErrors) {
                                submitMessage.value = "Registration successful!"
                            } else {
                                submitMessage.value = "Please fix the errors below"
                            }
                        },
                        label = if (isSubmitting.value) "Submitting..." else "Register",
                        modifier = Modifier()
                    )
                }
                
                Button(
                    onClick = { 
                        formData.value = FormData()
                        errors.value = ValidationErrors()
                        submitMessage.value = ""
                    },
                    label = "Clear Form",
                    modifier = Modifier()
                )
            }
        }
        
        val html = renderer.renderComposableRoot {
            RegistrationForm()
        }
        
        // Verify form structure
        assertTrue(html.contains("Create Account"), "Should contain form title")
        assertTrue(html.contains("Email:"), "Should contain email field")
        assertTrue(html.contains("Password:"), "Should contain password field")
        assertTrue(html.contains("Confirm Password:"), "Should contain confirm password field")
        
        // Verify form controls
        assertTrue(html.contains("[ ]"), "Should contain checkbox")
        assertTrue(html.contains("I accept the terms"), "Should contain terms text")
        
        // Verify buttons
        assertTrue(html.contains("Fill Sample Data"), "Should contain sample data button")
        assertTrue(html.contains("Register"), "Should contain register button")
        assertTrue(html.contains("Clear Form"), "Should contain clear button")
        
        println("Form HTML preview: ${html.take(500)}")
    }

    @Test
    fun testAuthenticationFlow() {
        val renderer = PlatformRenderer()
        
        data class User(val username: String, val role: String)
        
        @Composable
        fun AuthenticationApp() {
            val currentUser = remember { mutableStateOf<User?>(null) }
            val loginForm = remember { mutableStateOf(Pair("", "")) } // username, password
            val showLoginForm = remember { mutableStateOf(true) }
            val errorMessage = remember { mutableStateOf("") }
            
            Column(modifier = Modifier()) {
                if (currentUser.value == null) {
                    // Not authenticated
                    if (showLoginForm.value) {
                        Text("Login", modifier = Modifier())
                        
                        if (errorMessage.value.isNotEmpty()) {
                            Text("Error: ${errorMessage.value}", modifier = Modifier())
                        }
                        
                        Text("Username:", modifier = Modifier())
                        Text("Value: ${loginForm.value.first}", modifier = Modifier())
                        
                        Text("Password:", modifier = Modifier())
                        Text("Value: ${"*".repeat(loginForm.value.second.length)}", modifier = Modifier())
                        
                        Row(modifier = Modifier()) {
                            Button(
                                onClick = { 
                                    // Simulate login
                                    if (loginForm.value.first == "admin" && loginForm.value.second == "password") {
                                        currentUser.value = User("admin", "administrator")
                                        errorMessage.value = ""
                                    } else {
                                        errorMessage.value = "Invalid credentials"
                                    }
                                },
                                label = "Login",
                                modifier = Modifier()
                            )
                            
                            Button(
                                onClick = { 
                                    loginForm.value = Pair("admin", "password")
                                },
                                label = "Use Demo Credentials",
                                modifier = Modifier()
                            )
                        }
                    }
                } else {
                    // Authenticated
                    val user = currentUser.value!!
                    
                    Text("Welcome, ${user.username}!", modifier = Modifier())
                    Text("Role: ${user.role}", modifier = Modifier())
                    
                    Column(modifier = Modifier()) {
                        Text("Available Actions:", modifier = Modifier())
                        
                        Button(
                            onClick = { /* view profile */ },
                            label = "View Profile",
                            modifier = Modifier()
                        )
                        
                        if (user.role == "administrator") {
                            Button(
                                onClick = { /* admin panel */ },
                                label = "Admin Panel",
                                modifier = Modifier()
                            )
                            
                            Button(
                                onClick = { /* manage users */ },
                                label = "Manage Users",
                                modifier = Modifier()
                            )
                        }
                        
                        Button(
                            onClick = { 
                                currentUser.value = null
                                loginForm.value = Pair("", "")
                                errorMessage.value = ""
                            },
                            label = "Logout",
                            modifier = Modifier()
                        )
                    }
                }
            }
        }
        
        val html = renderer.renderComposableRoot {
            AuthenticationApp()
        }
        
        // Verify initial authentication state
        assertTrue(html.contains("Login"), "Should show login form initially")
        assertTrue(html.contains("Username:"), "Should contain username field")
        assertTrue(html.contains("Password:"), "Should contain password field")
        assertTrue(html.contains("Use Demo Credentials"), "Should have demo button")
        
        println("Authentication HTML preview: ${html.take(500)}")
    }

    @Test
    fun testSEOMetadataGeneration() {
        val renderer = PlatformRenderer()
        
        data class PageMetadata(
            val title: String,
            val description: String,
            val keywords: List<String>,
            val author: String
        )
        
        @Composable
        fun SEOOptimizedPage() {
            val metadata = remember {
                mutableStateOf(PageMetadata(
                    title = "Complete SSR Guide - Learn Server-Side Rendering",
                    description = "Master server-side rendering with our comprehensive guide. Improve SEO, performance, and user experience.",
                    keywords = listOf("SSR", "Server-Side Rendering", "SEO", "Performance", "Web Development"),
                    author = "Expert Developer"
                ))
            }
            
            Column(modifier = Modifier()) {
                // Main content
                Text(metadata.value.title, modifier = Modifier())
                Text("By ${metadata.value.author}", modifier = Modifier())
                Text(metadata.value.description, modifier = Modifier())
                
                // Article content
                Text("What is Server-Side Rendering?", modifier = Modifier())
                Text("Server-side rendering (SSR) is a technique where web pages are rendered on the server before being sent to the client. This approach has several benefits:", modifier = Modifier())
                
                Column(modifier = Modifier()) {
                    Text("• Improved SEO: Search engines can crawl the content", modifier = Modifier())
                    Text("• Faster initial page load", modifier = Modifier())
                    Text("• Better performance on slow devices", modifier = Modifier())
                    Text("• Enhanced accessibility", modifier = Modifier())
                }
                
                Text("Implementation Guide", modifier = Modifier())
                Text("To implement SSR in your application, follow these steps:", modifier = Modifier())
                
                repeat(5) { step ->
                    Text("${step + 1}. Step ${step + 1} of the implementation process", modifier = Modifier())
                }
                
                // Related articles
                Text("Related Topics", modifier = Modifier())
                metadata.value.keywords.forEach { keyword ->
                    Button(
                        onClick = { /* navigate to topic */ },
                        label = "Learn about $keyword",
                        modifier = Modifier()
                    )
                }
                
                // Footer with metadata
                Text("Keywords: ${metadata.value.keywords.joinToString(", ")}", modifier = Modifier())
                Text("Last updated: 2024-01-15", modifier = Modifier())
            }
        }
        
        val html = renderer.renderComposableRoot {
            SEOOptimizedPage()
        }
        
        // Verify SEO-optimized content
        assertTrue(html.contains("Complete SSR Guide"), "Should contain optimized title")
        assertTrue(html.contains("Master server-side rendering"), "Should contain meta description")
        assertTrue(html.contains("By Expert Developer"), "Should contain author")
        
        // Verify structured content
        assertTrue(html.contains("What is Server-Side Rendering?"), "Should contain h2-like headings")
        assertTrue(html.contains("Implementation Guide"), "Should contain sections")
        assertTrue(html.contains("• Improved SEO"), "Should contain bullet points")
        
        // Verify related content
        assertTrue(html.contains("Learn about SSR"), "Should contain related topic links")
        assertTrue(html.contains("Keywords: SSR"), "Should contain keyword metadata")
        
        // Verify HTML structure for SEO
        assertTrue(html.contains("<html"), "Should have proper HTML structure")
        assertTrue(html.contains("<body"), "Should have body tag")
        
        println("SEO HTML preview: ${html.take(500)}")
    }
}