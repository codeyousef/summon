package code.yousef.example.springboot

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Simple HTML components for the Spring Boot example.
 * These demonstrate server-side rendering patterns without external dependencies.
 */

/**
 * Hero section component with a welcome message.
 */
fun renderHeroComponent(username: String): String {
    return createHTML().div {
        classes = setOf("hero-section")
        style = "background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 4rem 0; text-align: center; margin-bottom: 2rem;"
        
        div {
            classes = setOf("container")
            h1 {
                classes = setOf("display-4", "fw-bold", "mb-3")
                +"Welcome to Spring Boot + Kotlin, $username!"
            }
            p {
                classes = setOf("lead", "mb-4")
                +"Build reactive web applications with Kotlin, Spring Boot, and modern web technologies"
            }
            button {
                classes = setOf("btn", "btn-light", "btn-lg")
                onClick = "scrollToFeatures()"
                +"Get Started"
            }
        }
    }
}

/**
 * Feature cards component that displays key features.
 */
fun renderFeatureCardsComponent(): String {
    return createHTML().div {
        h2 {
            classes = setOf("text-center", "mb-4", "display-6", "fw-bold")
            +"Key Features"
        }
        div {
            classes = setOf("row")
            
            // Feature card 1
            div {
                classes = setOf("col-md-4", "mb-4")
                div {
                    classes = setOf("card", "h-100", "text-center")
                    div {
                        classes = setOf("card-body", "p-4")
                        div {
                            classes = setOf("display-1", "mb-3")
                            +"🔄"
                        }
                        h5 {
                            classes = setOf("card-title", "fw-bold")
                            +"Reactive UI"
                        }
                        p {
                            classes = setOf("card-text")
                            +"Build reactive user interfaces with server-side rendering."
                        }
                    }
                }
            }
            
            // Feature card 2
            div {
                classes = setOf("col-md-4", "mb-4")
                div {
                    classes = setOf("card", "h-100", "text-center")
                    div {
                        classes = setOf("card-body", "p-4")
                        div {
                            classes = setOf("display-1", "mb-3")
                            +"🌱"
                        }
                        h5 {
                            classes = setOf("card-title", "fw-bold")
                            +"Spring Integration"
                        }
                        p {
                            classes = setOf("card-text")
                            +"Seamless integration with Spring Boot and Thymeleaf templates."
                        }
                    }
                }
            }
            
            // Feature card 3
            div {
                classes = setOf("col-md-4", "mb-4")
                div {
                    classes = setOf("card", "h-100", "text-center")
                    div {
                        classes = setOf("card-body", "p-4")
                        div {
                            classes = setOf("display-1", "mb-3")
                            +"🛡️"
                        }
                        h5 {
                            classes = setOf("card-title", "fw-bold")
                            +"Type-Safe"
                        }
                        p {
                            classes = setOf("card-text")
                            +"Kotlin's type safety with declarative HTML generation."
                        }
                    }
                }
            }
        }
    }
}

/**
 * Interactive counter component.
 */
fun renderCounterComponent(initialValue: Int = 0): String {
    return createHTML().div {
        classes = setOf("card", "text-center")
        style = "max-width: 400px; margin: 2rem auto;"
        
        div {
            classes = setOf("card-body", "p-4")
            h5 {
                classes = setOf("card-title", "fw-bold", "mb-3")
                +"Interactive Counter"
            }
            div {
                classes = setOf("display-3", "fw-bold", "text-primary", "mb-4", "counter-value")
                id = "counter-value"
                +initialValue.toString()
            }
            div {
                classes = setOf("d-flex", "gap-3", "justify-content-center")
                button {
                    classes = setOf("btn", "btn-secondary")
                    onClick = "updateCounter('decrement')"
                    +"Decrement"
                }
                button {
                    classes = setOf("btn", "btn-primary")
                    onClick = "updateCounter('increment')"
                    +"Increment"
                }
                button {
                    classes = setOf("btn", "btn-outline-secondary")
                    onClick = "updateCounter('reset')"
                    +"Reset"
                }
            }
        }
    }
}

/**
 * Dashboard component with statistics.
 */
fun renderDashboardComponent(): String {
    return createHTML().div {
        h1 {
            classes = setOf("mb-4")
            +"📊 Dashboard"
        }
        
        // Statistics Cards
        div {
            classes = setOf("row", "mb-4")
            
            // Stat card 1
            div {
                classes = setOf("col-md-3")
                div {
                    classes = setOf("card", "text-center")
                    style = "border-left: 4px solid #3498db;"
                    div {
                        classes = setOf("card-body")
                        div {
                            classes = setOf("h2")
                            style = "color: #3498db;"
                            +"👥"
                        }
                        h5 { +"Users" }
                        div {
                            classes = setOf("h3")
                            style = "color: #3498db;"
                            +"1,234"
                        }
                    }
                }
            }
            
            // Stat card 2
            div {
                classes = setOf("col-md-3")
                div {
                    classes = setOf("card", "text-center")
                    style = "border-left: 4px solid #2ecc71;"
                    div {
                        classes = setOf("card-body")
                        div {
                            classes = setOf("h2")
                            style = "color: #2ecc71;"
                            +"💰"
                        }
                        h5 { +"Revenue" }
                        div {
                            classes = setOf("h3")
                            style = "color: #2ecc71;"
                            +"$8,345"
                        }
                    }
                }
            }
            
            // Stat card 3
            div {
                classes = setOf("col-md-3")
                div {
                    classes = setOf("card", "text-center")
                    style = "border-left: 4px solid #f39c12;"
                    div {
                        classes = setOf("card-body")
                        div {
                            classes = setOf("h2")
                            style = "color: #f39c12;"
                            +"📦"
                        }
                        h5 { +"Products" }
                        div {
                            classes = setOf("h3")
                            style = "color: #f39c12;"
                            +"567"
                        }
                    }
                }
            }
            
            // Stat card 4
            div {
                classes = setOf("col-md-3")
                div {
                    classes = setOf("card", "text-center")
                    style = "border-left: 4px solid #e74c3c;"
                    div {
                        classes = setOf("card-body")
                        div {
                            classes = setOf("h2")
                            style = "color: #e74c3c;"
                            +"🛒"
                        }
                        h5 { +"Orders" }
                        div {
                            classes = setOf("h3")
                            style = "color: #e74c3c;"
                            +"890"
                        }
                    }
                }
            }
        }

        // Recent Activity
        div {
            classes = setOf("card")
            div {
                classes = setOf("card-header")
                h5 {
                    classes = setOf("mb-0")
                    +"Recent Activity"
                }
            }
            div {
                classes = setOf("card-body")
                
                div {
                    classes = setOf("d-flex", "mb-3")
                    div {
                        classes = setOf("me-3")
                        span {
                            classes = setOf("badge", "bg-success", "rounded-pill")
                            +"✅"
                        }
                    }
                    div {
                        strong { +"John Doe" }
                        +" completed order #12345"
                        br
                        small {
                            classes = setOf("text-muted")
                            +"2 hours ago"
                        }
                    }
                }
                
                div {
                    classes = setOf("d-flex", "mb-3")
                    div {
                        classes = setOf("me-3")
                        span {
                            classes = setOf("badge", "bg-info", "rounded-pill")
                            +"🔄"
                        }
                    }
                    div {
                        strong { +"Jane Smith" }
                        +" updated product inventory"
                        br
                        small {
                            classes = setOf("text-muted")
                            +"4 hours ago"
                        }
                    }
                }
                
                div {
                    classes = setOf("d-flex")
                    div {
                        classes = setOf("me-3")
                        span {
                            classes = setOf("badge", "bg-primary", "rounded-pill")
                            +"➕"
                        }
                    }
                    div {
                        strong { +"Mike Johnson" }
                        +" added new product"
                        br
                        small {
                            classes = setOf("text-muted")
                            +"Yesterday"
                        }
                    }
                }
            }
        }
    }
}

/**
 * User table component for displaying users.
 */
fun renderUserTableComponent(users: List<User>): String {
    return createHTML().div {
        classes = setOf("card")
        
        div {
            classes = setOf("card-header")
            h5 {
                classes = setOf("mb-0")
                +"User Management"
            }
        }
        
        div {
            classes = setOf("card-body", "p-0")
            if (users.isEmpty()) {
                div {
                    classes = setOf("text-center", "p-4")
                    p {
                        classes = setOf("text-muted")
                        +"No users found"
                    }
                }
            } else {
                div {
                    classes = setOf("table-responsive")
                    table {
                        classes = setOf("table", "table-hover", "mb-0")
                        
                        thead {
                            tr {
                                th { +"ID" }
                                th { +"Name" }
                                th { +"Email" }
                                th { +"Role" }
                                th { +"Status" }
                                th { +"Actions" }
                            }
                        }
                        
                        tbody {
                            users.forEach { user ->
                                tr {
                                    attributes["data-user-id"] = user.id.toString()
                                    
                                    td { +user.id.toString() }
                                    td { +user.name }
                                    td { +user.email }
                                    td {
                                        span {
                                            classes = setOf("badge", "bg-info")
                                            +user.role
                                        }
                                    }
                                    td {
                                        val statusClass = if (user.active) setOf("badge", "bg-success") else setOf("badge", "bg-secondary")
                                        val statusText = if (user.active) "Active" else "Inactive"
                                        span {
                                            classes = statusClass
                                            +statusText
                                        }
                                    }
                                    td {
                                        div {
                                            classes = setOf("btn-group", "btn-group-sm")
                                            attributes["role"] = "group"
                                            
                                            button {
                                                classes = setOf("btn", "btn-outline-primary")
                                                onClick = "editUser(${user.id})"
                                                +"Edit"
                                            }
                                            button {
                                                classes = setOf("btn", "btn-outline-danger")
                                                onClick = "deleteUser(${user.id}, '${user.name}')"
                                                +"Delete"
                                            }
                                            
                                            val toggleClass = if (user.active) setOf("btn", "btn-outline-warning") else setOf("btn", "btn-outline-success")
                                            val toggleText = if (user.active) "Deactivate" else "Activate"
                                            button {
                                                classes = toggleClass
                                                onClick = "toggleUserStatus(${user.id}, ${user.active})"
                                                +toggleText
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Contact form component.
 */
fun renderContactFormComponent(): String {
    return createHTML().div {
        classes = setOf("card")
        style = "max-width: 600px; margin: 2rem auto;"
        
        div {
            classes = setOf("card-header", "text-center")
            h1 {
                classes = setOf("mb-0")
                +"📧 Contact Us"
            }
        }
        
        div {
            classes = setOf("card-body")
            form {
                action = "/contact"
                method = FormMethod.post
                
                div {
                    classes = setOf("mb-3")
                    label {
                        classes = setOf("form-label")
                        htmlFor = "name"
                        +"Name"
                    }
                    input {
                        type = InputType.text
                        classes = setOf("form-control")
                        id = "name"
                        name = "name"
                        required = true
                    }
                }
                
                div {
                    classes = setOf("mb-3")
                    label {
                        classes = setOf("form-label")
                        htmlFor = "email"
                        +"Email"
                    }
                    input {
                        type = InputType.email
                        classes = setOf("form-control")
                        id = "email"
                        name = "email"
                        required = true
                    }
                }
                
                div {
                    classes = setOf("mb-3")
                    label {
                        classes = setOf("form-label")
                        htmlFor = "subject"
                        +"Subject"
                    }
                    input {
                        type = InputType.text
                        classes = setOf("form-control")
                        id = "subject"
                        name = "subject"
                        required = true
                    }
                }
                
                div {
                    classes = setOf("mb-3")
                    label {
                        classes = setOf("form-label")
                        htmlFor = "message"
                        +"Message"
                    }
                    textArea {
                        classes = setOf("form-control")
                        id = "message"
                        name = "message"
                        rows = "6"
                        required = true
                    }
                }
                
                div {
                    classes = setOf("d-grid")
                    button {
                        classes = setOf("btn", "btn-primary", "btn-lg")
                        type = ButtonType.submit
                        +"Send Message"
                    }
                }
            }
        }
    }
}

/**
 * Current time component.
 */
fun renderCurrentTimeComponent(): String {
    val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    val currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
    
    return createHTML().div {
        classes = setOf("card", "text-center", "mb-4")
        
        div {
            classes = setOf("card-body", "py-4")
            h5 {
                classes = setOf("card-title", "text-primary", "mb-3")
                +"Current Server Time"
            }
            div {
                classes = setOf("display-4", "fw-bold", "mb-2", "live-update")
                id = "current-time"
                +currentTime
            }
            p {
                classes = setOf("card-text", "text-muted")
                id = "current-date"
                +currentDate
            }
        }
    }
}