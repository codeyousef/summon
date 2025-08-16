package code.yousef.example.portfolio.integration

import code.yousef.example.portfolio.models.User
import code.yousef.example.portfolio.models.UserRole
import code.yousef.example.portfolio.repository.reactive.UserRepositoryReactiveImpl
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import io.vertx.core.Vertx
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Tests to verify Hibernate Reactive context and proper reactive execution
 * Ensures all database operations execute within Vert.x context
 */
@QuarkusTest
class ReactiveContextTest {

    @Inject
    lateinit var userRepository: UserRepositoryReactiveImpl
    
    @Inject
    lateinit var vertx: Vertx

    @BeforeEach
    fun setup() {
        // Note: Test data is cleared by @QuarkusTest through database drop-and-create
        // No need to manually delete data
    }

    @Test
    fun testVertxContextIsPresent() {
        val hasContext = AtomicBoolean(false)
        
        vertx.runOnContext {
            hasContext.set(Vertx.currentContext() != null)
        }
        
        // Wait for context check
        Thread.sleep(100)
        
        assertTrue(hasContext.get(), "Vert.x context should be present")
    }

    @Test
    fun testReactiveOperationInVertxContext() {
        val testUser = User.create(
            username = "contexttest",
            password = "test123",
            email = "context@test.com",
            role = UserRole.USER
        )

        // Execute within Vert.x context
        val subscriber = Uni.createFrom().emitter<User> { emitter ->
            vertx.runOnContext {
                userRepository.createUser(testUser)
                    .subscribe()
                    .with(
                        { user -> emitter.complete(user) },
                        { error -> emitter.fail(error) }
                    )
            }
        }
        .subscribe()
        .withSubscriber(UniAssertSubscriber.create())

        val createdUser = subscriber
            .awaitItem(Duration.ofSeconds(5))
            .assertCompleted()
            .item

        assertNotNull(createdUser)
        assertEquals("contexttest", createdUser.username)
    }

    @Test
    fun testTransactionalOperationWithContext() {
        val testUser = User.create(
            username = "txtest",
            password = "test123",
            email = "tx@test.com"
        )

        // Test transactional operation
        val subscriber = Panache.withTransaction {
            userRepository.createUser(testUser)
                .flatMap { created ->
                    // Verify within same transaction
                    userRepository.getUserByUsername(created.username)
                }
        }
        .subscribe()
        .withSubscriber(UniAssertSubscriber.create())

        val foundUser = subscriber
            .awaitItem(Duration.ofSeconds(5))
            .assertCompleted()
            .item

        assertNotNull(foundUser)
        assertEquals("txtest", foundUser?.username)
    }

    @Test
    fun testReactiveChainWithMultipleOperations() {
        // Test complex reactive chain
        val users = listOf(
            User.create("chain1", "pass1", "chain1@test.com"),
            User.create("chain2", "pass2", "chain2@test.com"),
            User.create("chain3", "pass3", "chain3@test.com")
        )

        val subscriber = Panache.withTransaction {
            // Create users sequentially in same transaction
            Uni.join().all(
                users.map { user -> userRepository.createUser(user) }
            ).andFailFast()
        }
        .flatMap {
            // Then retrieve all users
            userRepository.getAllUsers()
        }
        .subscribe()
        .withSubscriber(UniAssertSubscriber.create())

        val allUsers = subscriber
            .awaitItem(Duration.ofSeconds(10))
            .assertCompleted()
            .item

        assertTrue(allUsers.size >= 3, "Should have created at least 3 users")
        assertTrue(allUsers.any { it.username == "chain1" })
        assertTrue(allUsers.any { it.username == "chain2" })
        assertTrue(allUsers.any { it.username == "chain3" })
    }

    @Test
    fun testRollbackOnError() {
        val user1 = User.create("rollback1", "pass1", "rollback1@test.com")
        val user2 = User.create("rollback1", "pass2", "duplicate@test.com") // Duplicate username

        val subscriber = Panache.withTransaction {
            userRepository.createUser(user1)
                .flatMap { 
                    // This should fail due to duplicate username
                    userRepository.createUser(user2)
                }
        }
        .subscribe()
        .withSubscriber(UniAssertSubscriber.create())

        subscriber
            .awaitFailure(Duration.ofSeconds(5))
            .assertFailedWith(IllegalArgumentException::class.java)

        // Verify rollback - no users should exist
        val checkSubscriber = userRepository.getUserByUsername("rollback1")
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())

        val foundUser = checkSubscriber
            .awaitItem(Duration.ofSeconds(5))
            .assertCompleted()
            .item

        assertNull(foundUser, "User should not exist after rollback")
    }

    @Test
    fun testDockerConnectivity() {
        // Test that we can connect to database (implicitly tests Docker)
        val subscriber = Panache.withTransaction {
            userRepository.getAllUsers()
        }
        .subscribe()
        .withSubscriber(UniAssertSubscriber.create())

        subscriber
            .awaitItem(Duration.ofSeconds(10))
            .assertCompleted()

        // If we get here, Docker and database connectivity is working
        assertTrue(true, "Docker connectivity verified")
    }

    @Test
    fun testReactiveContextPropagation() {
        val contextPresent = AtomicBoolean(false)
        
        val subscriber = Uni.createFrom().item {
            // Check context in reactive chain
            contextPresent.set(Vertx.currentContext() != null)
            "test"
        }
        .flatMap { _ ->
            Panache.withTransaction {
                userRepository.getAllUsers()
            }
        }
        .subscribe()
        .withSubscriber(UniAssertSubscriber.create())

        subscriber
            .awaitItem(Duration.ofSeconds(5))
            .assertCompleted()

        assertTrue(contextPresent.get(), "Context should propagate through reactive chain")
    }

    @Test
    fun testConcurrentReactiveOperations() {
        val users = (1..10).map { i ->
            User.create("concurrent$i", "pass$i", "concurrent$i@test.com")
        }

        // Execute concurrent creates
        val subscriber = Uni.join().all(
            users.map { user ->
                Panache.withTransaction {
                    userRepository.createUser(user)
                }
            }
        ).andFailFast()
        .subscribe()
        .withSubscriber(UniAssertSubscriber.create())

        val results = subscriber
            .awaitItem(Duration.ofSeconds(15))
            .assertCompleted()
            .item

        assertEquals(10, results.size, "All concurrent operations should complete")
        
        // Verify all users were created
        val allUsersSubscriber = userRepository.getAllUsers()
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())

        val allUsers = allUsersSubscriber
            .awaitItem(Duration.ofSeconds(5))
            .assertCompleted()
            .item

        val concurrentUsers = allUsers.filter { it.username.startsWith("concurrent") }
        assertEquals(10, concurrentUsers.size, "All concurrent users should be created")
    }
}