package code.yousef.example.quarkus

import jakarta.enterprise.context.ApplicationScoped
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * A service for managing users in the application.
 * This is a simple in-memory implementation for the example.
 */
@ApplicationScoped
class UserService {
    private val users = ConcurrentHashMap<Int, User>()
    private val idCounter = AtomicInteger(1)
    
    init {
        // Add some sample users
        addUser("John Doe", "john.doe@example.com", "Administrator")
        addUser("Jane Smith", "jane.smith@example.com", "Editor")
        addUser("Bob Johnson", "bob.johnson@example.com", "Viewer")
        addUser("Alice Brown", "alice.brown@example.com", "Editor")
        addUser("Charlie Wilson", "charlie.wilson@example.com", "Viewer")
    }
    
    /**
     * Get all users in the system.
     * 
     * @return List of all users
     */
    fun getAllUsers(): List<User> {
        return users.values.toList()
    }
    
    /**
     * Get a user by ID.
     * 
     * @param id The ID of the user to retrieve
     * @return The user or null if not found
     */
    fun getUserById(id: Int): User? {
        return users[id]
    }
    
    /**
     * Add a new user to the system.
     * 
     * @param name The user's name
     * @param email The user's email address
     * @param role The user's role
     * @return The newly created user
     */
    fun addUser(name: String, email: String, role: String): User {
        val id = idCounter.getAndIncrement()
        val user = User(id, name, email, role)
        users[id] = user
        return user
    }
    
    /**
     * Update an existing user.
     * 
     * @param id The ID of the user to update
     * @param name The updated name (optional)
     * @param email The updated email (optional)
     * @param role The updated role (optional)
     * @return The updated user or null if not found
     */
    fun updateUser(id: Int, name: String? = null, email: String? = null, role: String? = null): User? {
        val user = users[id] ?: return null
        
        val updatedUser = user.copy(
            name = name ?: user.name,
            email = email ?: user.email,
            role = role ?: user.role
        )
        
        users[id] = updatedUser
        return updatedUser
    }
    
    /**
     * Delete a user by ID.
     * 
     * @param id The ID of the user to delete
     * @return True if the user was deleted, false if not found
     */
    fun deleteUser(id: Int): Boolean {
        return users.remove(id) != null
    }
} 