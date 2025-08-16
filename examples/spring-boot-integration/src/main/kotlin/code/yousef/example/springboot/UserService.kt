package code.yousef.example.springboot

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Service class for managing users.
 * Provides methods to retrieve, add, update, and delete users.
 */
@Service
class UserService {

    private val users = ConcurrentHashMap<Long, User>()
    private val idCounter = AtomicLong(0)

    /**
     * Initialize the service with sample users.
     */
    @PostConstruct
    fun init() {
        addUser(User(idCounter.incrementAndGet(), "John Doe", "john.doe@example.com", "admin"))
        addUser(User(idCounter.incrementAndGet(), "Jane Smith", "jane.smith@example.com", "editor"))
        addUser(User(idCounter.incrementAndGet(), "Bob Johnson", "bob.johnson@example.com", "user"))
        addUser(User(idCounter.incrementAndGet(), "Alice Williams", "alice.williams@example.com", "moderator"))
    }

    /**
     * Get all users.
     *
     * @return List of all users
     */
    fun getAllUsers(): List<User> {
        return users.values.toList()
    }

    /**
     * Get a user by ID.
     *
     * @param id The user ID
     * @return The user if found, null otherwise
     */
    fun getUserById(id: Long): User? {
        return users[id]
    }

    /**
     * Add a new user.
     *
     * @param user The user to add
     * @return The added user with assigned ID
     */
    fun addUser(user: User): User {
        val newUser = if (user.id == 0L) {
            user.copy(id = idCounter.incrementAndGet())
        } else {
            user
        }
        users[newUser.id] = newUser
        return newUser
    }

    /**
     * Update an existing user.
     *
     * @param id The ID of the user to update
     * @param updatedUser The updated user data
     * @return The updated user if successful, null if user not found
     */
    fun updateUser(id: Long, updatedUser: User): User? {
        if (users.containsKey(id)) {
            val updated = updatedUser.copy(id = id)
            users[id] = updated
            return updated
        }
        return null
    }

    /**
     * Delete a user by ID.
     *
     * @param id The ID of the user to delete
     * @return true if the user was deleted, false if not found
     */
    fun deleteUser(id: Long): Boolean {
        return users.remove(id) != null
    }

    /**
     * Search users by name or email containing the search term.
     *
     * @param searchTerm The term to search for in name or email
     * @return List of matching users
     */
    fun searchUsers(searchTerm: String): List<User> {
        val term = searchTerm.lowercase()
        return users.values.filter {
            it.name.lowercase().contains(term) || it.email.lowercase().contains(term)
        }
    }

    /**
     * Get users by role.
     *
     * @param role The role to filter by
     * @return List of users with the specified role
     */
    fun getUsersByRole(role: String): List<User> {
        return users.values.filter { it.role == role }
    }

    /**
     * Get all active users.
     *
     * @return List of all active users
     */
    fun getActiveUsers(): List<User> {
        return users.values.filter { it.active }.toList()
    }

    /**
     * Activate a user.
     *
     * @param id The ID of the user to activate
     * @return The updated user if successful, null if user not found
     */
    fun activateUser(id: Long): User? {
        return getUserById(id)?.let { user ->
            if (user.active) {
                user
            } else {
                val updatedUser = user.copy(active = true)
                users[id] = updatedUser
                updatedUser
            }
        }
    }

    /**
     * Deactivate a user.
     *
     * @param id The ID of the user to deactivate
     * @return The updated user if successful, null if user not found
     */
    fun deactivateUser(id: Long): User? {
        return getUserById(id)?.let { user ->
            if (!user.active) {
                user
            } else {
                val updatedUser = user.copy(active = false)
                users[id] = updatedUser
                updatedUser
            }
        }
    }
}