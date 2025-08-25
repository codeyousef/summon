package code.yousef.example.springboot

import code.yousef.example.springboot.models.User
import code.yousef.example.springboot.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer : CommandLineRunner {
    
    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)
    
    @Autowired
    private lateinit var userRepository: UserRepository
    
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun run(vararg args: String?) {
        // Create test user if it doesn't exist
        if (!userRepository.existsByUsername("testuser")) {
            val testUser = User(
                username = "testuser",
                email = "test@example.com",
                passwordHash = passwordEncoder.encode("password123"),
                language = "en",
                theme = "light"
            )
            userRepository.save(testUser)
            logger.info("Created test user: testuser / password123")
        } else {
            logger.info("Test user already exists")
        }
    }
}