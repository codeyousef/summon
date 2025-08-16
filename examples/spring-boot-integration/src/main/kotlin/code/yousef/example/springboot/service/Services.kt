package code.yousef.example.springboot.service

import code.yousef.example.springboot.models.*
import code.yousef.example.springboot.repository.TodoRepository
import code.yousef.example.springboot.repository.UserRepository
import code.yousef.example.springboot.security.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService @Autowired constructor(
    private val userRepository: UserRepository
) : UserDetailsService {
    
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.username)
            .password(user.passwordHash)
            .authorities("ROLE_USER")
            .build()
    }
}

@Service
@Transactional
class AuthService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider
) {
    
    fun register(request: RegisterRequest): AuthResponse {
        return try {
            // Check if user already exists
            if (userRepository.existsByUsername(request.username)) {
                return AuthResponse(
                    success = false,
                    message = "Username is already taken!"
                )
            }
            
            if (userRepository.existsByEmail(request.email)) {
                return AuthResponse(
                    success = false,
                    message = "Email is already in use!"
                )
            }
            
            // Create new user
            val user = User(
                username = request.username,
                email = request.email,
                passwordHash = passwordEncoder.encode(request.password)
            )
            
            val savedUser = userRepository.save(user)
            val token = jwtTokenProvider.generateTokenFromUsername(savedUser.username)
            
            AuthResponse(
                success = true,
                message = "User registered successfully",
                token = token,
                user = savedUser.toDto()
            )
        } catch (e: Exception) {
            AuthResponse(
                success = false,
                message = "Registration failed: ${e.message}"
            )
        }
    }
    
    fun login(request: LoginRequest): AuthResponse {
        return try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password)
            )
            
            val token = jwtTokenProvider.generateToken(authentication)
            val user = userRepository.findByUsername(request.username)
                ?: return AuthResponse(success = false, message = "User not found")
            
            AuthResponse(
                success = true,
                message = "Login successful",
                token = token,
                user = user.toDto()
            )
        } catch (e: Exception) {
            AuthResponse(
                success = false,
                message = "Invalid username or password"
            )
        }
    }
    
    fun getCurrentUser(username: String): User? {
        return userRepository.findByUsername(username)
    }
    
    fun updateUserSettings(username: String, request: UpdateUserSettingsRequest): User? {
        val user = userRepository.findByUsername(username) ?: return null
        
        val updatedUser = user.copy(
            language = request.language ?: user.language,
            theme = request.theme ?: user.theme
        )
        
        return userRepository.save(updatedUser)
    }
}

@Service
@Transactional
class TodoService @Autowired constructor(
    private val todoRepository: TodoRepository,
    private val userRepository: UserRepository
) {
    
    fun getTodosByUser(username: String, filter: TodoFilter = TodoFilter.ALL): List<TodoDto> {
        val user = userRepository.findByUsername(username) ?: return emptyList()
        
        val todos = when (filter) {
            TodoFilter.ALL -> todoRepository.findByUserIdOrderByCreatedAtDesc(user.id)
            TodoFilter.ACTIVE -> todoRepository.findByUserIdAndCompletedOrderByCreatedAtDesc(user.id, false)
            TodoFilter.COMPLETED -> todoRepository.findByUserIdAndCompletedOrderByCreatedAtDesc(user.id, true)
        }
        
        return todos.map { it.toDto() }
    }
    
    fun createTodo(username: String, request: CreateTodoRequest): TodoDto? {
        val user = userRepository.findByUsername(username) ?: return null
        
        val todo = Todo(
            text = request.text,
            user = user
        )
        
        val savedTodo = todoRepository.save(todo)
        return savedTodo.toDto()
    }
    
    fun updateTodo(username: String, todoId: Long, request: UpdateTodoRequest): TodoDto? {
        val user = userRepository.findByUsername(username) ?: return null
        val todo = todoRepository.findByIdAndUserId(todoId, user.id) ?: return null
        
        val updatedTodo = todo.copy(
            text = request.text ?: todo.text,
            completed = request.completed ?: todo.completed
        )
        
        val savedTodo = todoRepository.save(updatedTodo)
        return savedTodo.toDto()
    }
    
    fun deleteTodo(username: String, todoId: Long): Boolean {
        val user = userRepository.findByUsername(username) ?: return false
        val todo = todoRepository.findByIdAndUserId(todoId, user.id) ?: return false
        
        todoRepository.delete(todo)
        return true
    }
    
    fun clearCompleted(username: String): List<TodoDto> {
        val user = userRepository.findByUsername(username) ?: return emptyList()
        
        // Delete completed todos
        val completedTodos = todoRepository.findByUserIdAndCompletedOrderByCreatedAtDesc(user.id, true)
        todoRepository.deleteAll(completedTodos)
        
        // Return remaining todos
        return getTodosByUser(username)
    }
    
    fun getTodoStats(username: String): Map<String, Long> {
        val user = userRepository.findByUsername(username) ?: return mapOf(
            "total" to 0L,
            "active" to 0L,
            "completed" to 0L
        )
        
        val activeCount = todoRepository.countActiveByUserId(user.id)
        val completedCount = todoRepository.countCompletedByUserId(user.id)
        
        return mapOf(
            "total" to (activeCount + completedCount),
            "active" to activeCount,
            "completed" to completedCount
        )
    }
}