package code.yousef.example.springboot

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

/**
 * REST API controller for handling AJAX requests and providing dynamic content.
 * This controller works alongside the WebController to provide API endpoints
 * for partial page updates and interactive features.
 */
@RestController
@RequestMapping("/api")
class ApiController @Autowired constructor(
    private val userService: UserService
) {
    
    private val logger = LoggerFactory.getLogger(ApiController::class.java)
    private val counter = AtomicInteger(0)

    /**
     * Get current server time for live updates.
     */
    @GetMapping("/time")
    fun getCurrentTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }

    /**
     * Get current date in a formatted string.
     */
    @GetMapping("/date")
    fun getCurrentDate(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
    }

    /**
     * Increment counter and return new value.
     */
    @PostMapping("/counter/increment")
    fun incrementCounter(): Map<String, Any> {
        val newValue = counter.incrementAndGet()
        logger.debug("Counter incremented to: $newValue")
        return mapOf(
            "value" to newValue,
            "timestamp" to LocalDateTime.now().toString()
        )
    }

    /**
     * Decrement counter and return new value.
     */
    @PostMapping("/counter/decrement")
    fun decrementCounter(): Map<String, Any> {
        val newValue = counter.decrementAndGet()
        logger.debug("Counter decremented to: $newValue")
        return mapOf(
            "value" to newValue,
            "timestamp" to LocalDateTime.now().toString()
        )
    }

    /**
     * Get current counter value.
     */
    @GetMapping("/counter")
    fun getCounter(): Map<String, Any> {
        return mapOf(
            "value" to counter.get(),
            "timestamp" to LocalDateTime.now().toString()
        )
    }

    /**
     * Reset counter to zero.
     */
    @PostMapping("/counter/reset")
    fun resetCounter(): Map<String, Any> {
        counter.set(0)
        logger.debug("Counter reset to 0")
        return mapOf(
            "value" to 0,
            "timestamp" to LocalDateTime.now().toString()
        )
    }

    /**
     * Get all users as JSON.
     */
    @GetMapping("/users")
    fun getAllUsers(): List<User> {
        return userService.getAllUsers()
    }

    /**
     * Get a specific user by ID.
     */
    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<User> {
        val user = userService.getUserById(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Create a new user via API.
     */
    @PostMapping("/users")
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        return try {
            val createdUser = userService.addUser(user)
            logger.info("User created via API: ${createdUser.name}")
            ResponseEntity.ok(createdUser)
        } catch (e: Exception) {
            logger.error("Error creating user via API", e)
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Update an existing user via API.
     */
    @PutMapping("/users/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody user: User): ResponseEntity<User> {
        return try {
            val updatedUser = userService.updateUser(id, user)
            if (updatedUser != null) {
                logger.info("User updated via API: ${updatedUser.name}")
                ResponseEntity.ok(updatedUser)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            logger.error("Error updating user via API", e)
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Delete a user via API.
     */
    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Map<String, Any>> {
        return try {
            val deleted = userService.deleteUser(id)
            if (deleted) {
                logger.info("User deleted via API: $id")
                ResponseEntity.ok(mapOf("message" to "User deleted successfully"))
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            logger.error("Error deleting user via API", e)
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Unknown error")))
        }
    }

    /**
     * Search users by name or email.
     */
    @GetMapping("/users/search")
    fun searchUsers(@RequestParam query: String): List<User> {
        return userService.searchUsers(query)
    }

    /**
     * Get users by role.
     */
    @GetMapping("/users/role/{role}")
    fun getUsersByRole(@PathVariable role: String): List<User> {
        return userService.getUsersByRole(role)
    }

    /**
     * Get active users only.
     */
    @GetMapping("/users/active")
    fun getActiveUsers(): List<User> {
        return userService.getActiveUsers()
    }

    /**
     * Activate a user.
     */
    @PostMapping("/users/{id}/activate")
    fun activateUser(@PathVariable id: Long): ResponseEntity<User> {
        return try {
            val user = userService.activateUser(id)
            if (user != null) {
                logger.info("User activated via API: $id")
                ResponseEntity.ok(user)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            logger.error("Error activating user via API", e)
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Deactivate a user.
     */
    @PostMapping("/users/{id}/deactivate")
    fun deactivateUser(@PathVariable id: Long): ResponseEntity<User> {
        return try {
            val user = userService.deactivateUser(id)
            if (user != null) {
                logger.info("User deactivated via API: $id")
                ResponseEntity.ok(user)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            logger.error("Error deactivating user via API", e)
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Render a Summon component and return HTML.
     * This endpoint demonstrates server-side rendering of components for AJAX updates.
     */
    @GetMapping("/component/hero")
    fun getHeroComponent(@RequestParam username: String = "User"): String {
        return try {
            renderHeroComponent(username)
        } catch (e: Exception) {
            logger.error("Error rendering hero component", e)
            "<div class='alert alert-danger'>Error rendering component: ${e.message}</div>"
        }
    }

    /**
     * Render the counter component with a specific value.
     */
    @GetMapping("/component/counter")
    fun getCounterComponent(@RequestParam value: Int = 0): String {
        return try {
            renderCounterComponent(value)
        } catch (e: Exception) {
            logger.error("Error rendering counter component", e)
            "<div class='alert alert-danger'>Error rendering component: ${e.message}</div>"
        }
    }

    /**
     * Render the user table component with current users.
     */
    @GetMapping("/component/users")
    fun getUserTableComponent(): String {
        return try {
            val users = userService.getAllUsers()
            renderUserTableComponent(users)
        } catch (e: Exception) {
            logger.error("Error rendering user table component", e)
            "<div class='alert alert-danger'>Error rendering component: ${e.message}</div>"
        }
    }

    /**
     * Render the dashboard component.
     */
    @GetMapping("/component/dashboard")
    fun getDashboardComponent(): String {
        return try {
            renderDashboardComponent()
        } catch (e: Exception) {
            logger.error("Error rendering dashboard component", e)
            "<div class='alert alert-danger'>Error rendering component: ${e.message}</div>"
        }
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    fun healthCheck(): Map<String, Any> {
        return mapOf(
            "status" to "UP",
            "timestamp" to LocalDateTime.now().toString(),
            "users" to userService.getAllUsers().size,
            "counter" to counter.get()
        )
    }

    /**
     * Get application statistics.
     */
    @GetMapping("/stats")
    fun getStats(): Map<String, Any> {
        val users = userService.getAllUsers()
        return mapOf(
            "totalUsers" to users.size,
            "activeUsers" to users.count { it.active },
            "usersByRole" to users.groupBy { it.role }.mapValues { it.value.size },
            "counterValue" to counter.get(),
            "serverTime" to LocalDateTime.now().toString()
        )
    }
}