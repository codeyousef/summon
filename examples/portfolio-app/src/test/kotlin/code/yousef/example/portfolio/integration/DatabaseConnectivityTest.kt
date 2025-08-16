package code.yousef.example.portfolio.integration

import code.yousef.example.portfolio.config.RepositoryConfig
import code.yousef.example.portfolio.models.ContentStatus
import code.yousef.example.portfolio.models.User
import code.yousef.example.portfolio.models.UserRole
import code.yousef.example.portfolio.repository.reactive.UserRepositoryReactiveImpl
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.time.Duration

/**
 * Integration tests for database connectivity and reactive repository operations
 * Tests all database profiles and ensures proper connectivity
 */
@QuarkusTest
class DatabaseConnectivityTest {

    @Inject
    lateinit var userRepository: UserRepositoryReactiveImpl
    
    @Inject
    lateinit var repositoryConfig: RepositoryConfig

    @BeforeEach
    fun setup() {
        // Note: Test data is cleared by @QuarkusTest through database drop-and-create
        // No need to manually delete data
    }

    @Test
    fun testDatabaseConnection() {
        // Test basic database connectivity by counting users
        val subscriber = userRepository.getAllUsers()
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())

        subscriber
            .awaitItem(Duration.ofSeconds(5))
            .assertCompleted()

        // If we get here, database connection is working
        assertTrue(true, "Database connection successful")
    }

    @Test
    fun testUserCRUDOperations() {
        // Create a test user
        val testUser = User.create(
            username = "testuser",
            password = "testpass123",
            email = "test@example.com",
            fullName = "Test User",
            role = UserRole.USER
        )

        // Test Create
        val createdUserSubscriber = userRepository.createUser(testUser)
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())

        val createdUser = createdUserSubscriber
            .awaitItem(Duration.ofSeconds(5))
            .assertCompleted()
            .item

        assertNotNull(createdUser)
        assertEquals("testuser", createdUser.username)
        assertEquals("test@example.com", createdUser.email)

        // Test Read by username
        val foundUserSubscriber = userRepository.getUserByUsername("testuser")
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())

        val foundUser = foundUserSubscriber
            .awaitItem(Duration.ofSeconds(5))
            .assertCompleted()
            .item

        assertNotNull(foundUser)
        assertEquals(createdUser.id, foundUser?.id)

        // Test Update
        val updatedUser = foundUser!!.copy(
            fullName = "Updated Test User",
            status = ContentStatus.PUBLISHED
        )

        val updateSubscriber = userRepository.updateUser(updatedUser)
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())

        val result = updateSubscriber
            .awaitItem(Duration.ofSeconds(5))
            .assertCompleted()
            .item

        assertEquals("Updated Test User", result.fullName)
        assertEquals(ContentStatus.PUBLISHED, result.status)

        // Test Delete
        val deleteSubscriber = userRepository.deleteUser(createdUser.id)
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())

        val deleteResult = deleteSubscriber
            .awaitItem(Duration.ofSeconds(5))
            .assertCompleted()
            .item

        assertTrue(deleteResult, "User should be deleted successfully")

        // Verify deletion
        val verifySubscriber = userRepository.getUserByUsername("testuser")
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())

        val deletedUser = verifySubscriber
            .awaitItem(Duration.ofSeconds(5))
            .assertCompleted()
            .item

        assertNull(deletedUser, "User should not exist after deletion")
    }

    @Test
    fun testDatabaseInitialization() {
        // Test that database initialization works
        val initSubscriber = repositoryConfig.initializeDatabase()
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())

        initSubscriber
            .awaitItem(Duration.ofSeconds(10))
            .assertCompleted()

        // Verify admin user was created
        val adminSubscriber = userRepository.getUserByUsername("admin")
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())

        val adminUser = adminSubscriber
            .awaitItem(Duration.ofSeconds(5))
            .assertCompleted()
            .item

        assertNotNull(adminUser, "Admin user should be created during initialization")
        assertEquals(UserRole.ADMIN, adminUser?.role)
        assertEquals("admin@example.com", adminUser?.email)
    }

    @Test
    fun testDuplicateUsernameHandling() {
        // Create first user
        val user1 = User.create(
            username = "duplicate",
            password = "password1",
            email = "user1@example.com"
        )

        userRepository.createUser(user1)
            .await()
            .atMost(Duration.ofSeconds(5))

        // Try to create another user with same username
        val user2 = User.create(
            username = "duplicate",
            password = "password2",
            email = "user2@example.com"
        )

        val duplicateSubscriber = userRepository.createUser(user2)
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())

        duplicateSubscriber
            .awaitFailure(Duration.ofSeconds(5))
            .assertFailedWith(IllegalArgumentException::class.java, "Username already exists")
    }

    @Test
    fun testTransactionRollback() {
        // This test ensures that failed operations don't leave partial data
        val testUser = User.create(
            username = "rollbacktest",
            password = "testpass",
            email = "rollback@example.com"
        )

        // Create user successfully
        userRepository.createUser(testUser)
            .await()
            .atMost(Duration.ofSeconds(5))

        // Try to create duplicate (should fail)
        val duplicateUser = User.create(
            username = "rollbacktest",
            password = "different",
            email = "different@example.com"
        )

        assertThrows(Exception::class.java) {
            userRepository.createUser(duplicateUser)
                .await()
                .atMost(Duration.ofSeconds(5))
        }

        // Verify original user still exists and wasn't affected
        val existingUser = userRepository.getUserByUsername("rollbacktest")
            .await()
            .atMost(Duration.ofSeconds(5))

        assertNotNull(existingUser)
        assertEquals("rollback@example.com", existingUser?.email)
    }

    @Test
    fun testConcurrentOperations() {
        // Test that reactive operations can handle concurrent access
        val users = (1..5).map { i ->
            User.create(
                username = "concurrent$i",
                password = "password$i",
                email = "concurrent$i@example.com"
            )
        }

        // Create all users concurrently using Mutiny combining
        val allCreated = io.smallrye.mutiny.Multi.createFrom().iterable(users)
            .onItem().transformToUni { user -> userRepository.createUser(user) }
            .merge()
            .collect().asList()

        val createdUsers = allCreated
            .await()
            .atMost(Duration.ofSeconds(10))

        assertEquals(5, createdUsers.size, "All 5 users should be created successfully")

        // Verify all users exist
        val allUsers = userRepository.getAllUsers()
            .await()
            .atMost(Duration.ofSeconds(5))

        val concurrentUsers = allUsers.filter { it.username.startsWith("concurrent") }
        assertEquals(5, concurrentUsers.size, "All concurrent users should be retrievable")
    }
}