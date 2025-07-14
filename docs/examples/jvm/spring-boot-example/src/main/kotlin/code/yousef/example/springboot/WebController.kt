package code.yousef.example.springboot

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

/**
 * Web controller for handling MVC routes and rendering pages with Thymeleaf templates.
 * This controller integrates with the SummonThymeleafRenderer to provide Summon components
 * within Spring Boot MVC applications.
 */
@Controller
class WebController @Autowired constructor(
    private val userService: UserService
) {
    
    private val logger = LoggerFactory.getLogger(WebController::class.java)

    /**
     * Render the home page with hero, features, and counter components.
     */
    @GetMapping("/")
    fun home(model: Model): String {
        logger.info("Rendering home page")
        
        try {
            // Render pure Summon components as HTML strings
            model.addAttribute("heroHtml", HeroComponent("Spring Boot User"))
            model.addAttribute("featuresHtml", FeatureCardsComponent())
            model.addAttribute("counterHtml", CounterComponent(0))
            model.addAttribute("currentTimeHtml", CurrentTimeComponent())
            model.addAttribute("quickLinksHtml", QuickLinksComponent())
            model.addAttribute("navigationHtml", NavigationComponent())
            model.addAttribute("footerHtml", FooterComponent())
            model.addAttribute("pageTitle", "Home - Spring Boot Summon Example")
            model.addAttribute("username", "Spring Boot User")
            
            return "index"
        } catch (e: Exception) {
            logger.error("Error rendering home page", e)
            model.addAttribute("error", "Error loading page: ${e.message}")
            return "error"
        }
    }

    /**
     * Render the users page with user management functionality.
     */
    @GetMapping("/users")
    fun users(model: Model): String {
        logger.info("Rendering users page")
        
        try {
            val users = userService.getAllUsers()
            
            // Render pure Summon components
            model.addAttribute("userHeaderHtml", UserPageHeaderComponent())
            model.addAttribute("addUserFormHtml", AddUserFormComponent())
            model.addAttribute("userTableHtml", UserTableComponent(users))
            model.addAttribute("userStatisticsHtml", UserStatisticsComponent(users))
            model.addAttribute("navigationHtml", NavigationComponent())
            model.addAttribute("footerHtml", FooterComponent())
            model.addAttribute("pageTitle", "User Management - Spring Boot Summon Example")
            model.addAttribute("users", users)
            
            return "users"
        } catch (e: Exception) {
            logger.error("Error rendering users page", e)
            model.addAttribute("error", "Error loading users: ${e.message}")
            return "error"
        }
    }

    /**
     * Render the dashboard page with statistics and activity.
     */
    @GetMapping("/dashboard")
    fun dashboard(model: Model): String {
        logger.info("Rendering dashboard page")
        
        try {
            // Render pure Summon components
            model.addAttribute("dashboardHtml", DashboardComponent())
            model.addAttribute("navigationHtml", NavigationComponent())
            model.addAttribute("footerHtml", FooterComponent())
            model.addAttribute("pageTitle", "Dashboard - Spring Boot Summon Example")
            
            return "dashboard"
        } catch (e: Exception) {
            logger.error("Error rendering dashboard page", e)
            model.addAttribute("error", "Error loading dashboard: ${e.message}")
            return "error"
        }
    }

    /**
     * Render the contact page with contact form.
     */
    @GetMapping("/contact")
    fun contact(model: Model): String {
        logger.info("Rendering contact page")
        
        try {
            // Render pure Summon components
            model.addAttribute("contactFormHtml", ContactFormComponent())
            model.addAttribute("navigationHtml", NavigationComponent())
            model.addAttribute("footerHtml", FooterComponent())
            model.addAttribute("pageTitle", "Contact Us - Spring Boot Summon Example")
            
            return "contact"
        } catch (e: Exception) {
            logger.error("Error rendering contact page", e)
            model.addAttribute("error", "Error loading contact page: ${e.message}")
            return "error"
        }
    }

    /**
     * Render the chat page.
     */
    @GetMapping("/chat")
    fun chat(model: Model): String {
        logger.info("Rendering chat page")
        
        try {
            model.addAttribute("pageTitle", "Chat - Spring Boot Summon Example")
            return "chat"
        } catch (e: Exception) {
            logger.error("Error rendering chat page", e)
            model.addAttribute("error", "Error loading chat page: ${e.message}")
            return "error"
        }
    }

    /**
     * Handle user creation form submission.
     */
    @PostMapping("/users")
    fun createUser(
        @RequestParam name: String,
        @RequestParam email: String,
        @RequestParam role: String,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.info("Creating new user: $name")
        
        try {
            val user = User(name = name, email = email, role = role)
            userService.addUser(user)
            
            redirectAttributes.addFlashAttribute("message", "User created successfully!")
            redirectAttributes.addFlashAttribute("messageType", "success")
        } catch (e: Exception) {
            logger.error("Error creating user", e)
            redirectAttributes.addFlashAttribute("message", "Error creating user: ${e.message}")
            redirectAttributes.addFlashAttribute("messageType", "error")
        }
        
        return "redirect:/users"
    }

    /**
     * Handle user update form submission.
     */
    @PostMapping("/users/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestParam name: String,
        @RequestParam email: String,
        @RequestParam role: String,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.info("Updating user: $id")
        
        try {
            val user = User(id = id, name = name, email = email, role = role)
            val updated = userService.updateUser(id, user)
            
            if (updated != null) {
                redirectAttributes.addFlashAttribute("message", "User updated successfully!")
                redirectAttributes.addFlashAttribute("messageType", "success")
            } else {
                redirectAttributes.addFlashAttribute("message", "User not found!")
                redirectAttributes.addFlashAttribute("messageType", "error")
            }
        } catch (e: Exception) {
            logger.error("Error updating user", e)
            redirectAttributes.addFlashAttribute("message", "Error updating user: ${e.message}")
            redirectAttributes.addFlashAttribute("messageType", "error")
        }
        
        return "redirect:/users"
    }

    /**
     * Handle user deletion.
     */
    @PostMapping("/users/{id}/delete")
    fun deleteUser(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.info("Deleting user: $id")
        
        try {
            val deleted = userService.deleteUser(id)
            
            if (deleted) {
                redirectAttributes.addFlashAttribute("message", "User deleted successfully!")
                redirectAttributes.addFlashAttribute("messageType", "success")
            } else {
                redirectAttributes.addFlashAttribute("message", "User not found!")
                redirectAttributes.addFlashAttribute("messageType", "error")
            }
        } catch (e: Exception) {
            logger.error("Error deleting user", e)
            redirectAttributes.addFlashAttribute("message", "Error deleting user: ${e.message}")
            redirectAttributes.addFlashAttribute("messageType", "error")
        }
        
        return "redirect:/users"
    }

    /**
     * Handle contact form submission.
     */
    @PostMapping("/contact")
    fun submitContact(
        @RequestParam name: String,
        @RequestParam email: String,
        @RequestParam subject: String,
        @RequestParam message: String,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.info("Contact form submitted by: $name")
        
        try {
            // In a real application, you would send an email or save to database
            logger.info("Contact form - Name: $name, Email: $email, Subject: $subject, Message: $message")
            
            redirectAttributes.addFlashAttribute("message", "Thank you for your message! We'll get back to you soon.")
            redirectAttributes.addFlashAttribute("messageType", "success")
        } catch (e: Exception) {
            logger.error("Error processing contact form", e)
            redirectAttributes.addFlashAttribute("message", "Error sending message: ${e.message}")
            redirectAttributes.addFlashAttribute("messageType", "error")
        }
        
        return "redirect:/contact"
    }
}