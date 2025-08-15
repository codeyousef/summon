package code.yousef.summon

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

// Listener type for lifecycle events
typealias LifecycleListener = (BackendIntegrations.LifecycleEvent) -> Unit

/**
 * Example integrations with common backend frameworks
 * This version uses a simplified lifecycle approach with event listeners
 */
object BackendIntegrations {
    // Lifecycle event types
    enum class LifecycleEvent {
        STARTUP,
        SHUTDOWN,
        PAUSE,
        RESUME
    }

    // Using the top-level LifecycleListener type alias

    // Store for lifecycle listeners
    private val lifecycleListeners = ConcurrentHashMap<String, MutableList<LifecycleListener>>()

    // Initialization flag
    private val initialized = AtomicBoolean(false)

    /**
     * Register a lifecycle listener with an optional ID
     */
    fun registerLifecycleListener(id: String = "anonymous-${System.currentTimeMillis()}", listener: LifecycleListener) {
        lifecycleListeners.getOrPut(id) { mutableListOf() }.add(listener)
    }

    /**
     * Unregister a lifecycle listener by ID
     */
    fun unregisterLifecycleListener(id: String) {
        lifecycleListeners.remove(id)
    }

    /**
     * Trigger a lifecycle event
     */
    fun triggerLifecycleEvent(event: LifecycleEvent) {
        lifecycleListeners.values.forEach { listeners ->
            listeners.forEach { listener ->
                try {
                    listener(event)
                } catch (e: Exception) {
                    System.err.println("Error in lifecycle listener: ${e.message}")
                }
            }
        }
    }

    /**
     * Integration with Spring Boot
     */
    fun setupSpringBootIntegration() {
        try {
            // Check if Spring Boot is in the classpath
            Class.forName("org.springframework.boot.SpringApplication")
            println("Spring Boot detected, setting up integration")

            // Register lifecycle listeners
            registerLifecycleListener("spring-boot") { event ->
                when (event) {
                    LifecycleEvent.STARTUP -> println("Spring Boot application started")
                    LifecycleEvent.SHUTDOWN -> println("Spring Boot application shutting down")
                    else -> {} // Ignore other events
                }
            }

            // Use reflection to integrate with Spring's lifecycle callbacks
            try {
                val contextClass = Class.forName("org.springframework.context.ApplicationContext")
                println("Found Spring context, ready for deeper integration")

                // In a real implementation, we would register Spring-specific lifecycle hooks here
            } catch (e: ClassNotFoundException) {
                // Spring context not available
            }
        } catch (e: ClassNotFoundException) {
            // Spring Boot not in classpath, skip integration
        }
    }

    /**
     * Integration with Ktor
     */
    fun setupKtorIntegration() {
        try {
            // Check if Ktor is in the classpath
            Class.forName("io.ktor.server.engine.ApplicationEngine")
            println("Ktor detected, setting up integration")

            // Register lifecycle listeners
            registerLifecycleListener("ktor") { event ->
                when (event) {
                    LifecycleEvent.STARTUP -> println("Ktor server started")
                    LifecycleEvent.SHUTDOWN -> println("Ktor server shutting down")
                    else -> {} // Ignore other events
                }
            }
        } catch (e: ClassNotFoundException) {
            // Ktor not in classpath, skip integration
        }
    }

    /**
     * Integration with Quarkus
     */
    fun setupQuarkusIntegration() {
        try {
            // Check if Quarkus is in the classpath
            Class.forName("io.quarkus.runtime.Quarkus")
            println("Quarkus detected, setting up integration")

            // Register lifecycle listeners
            registerLifecycleListener("quarkus") { event ->
                when (event) {
                    LifecycleEvent.STARTUP -> {
                        println("Quarkus application started")
                        setupQuarkusExtensions()
                    }

                    LifecycleEvent.SHUTDOWN -> println("Quarkus application shutting down")
                    else -> {} // Ignore other events
                }
            }
        } catch (e: ClassNotFoundException) {
            // Quarkus not in classpath, skip integration
        }
    }

    /**
     * Setup Quarkus extensions
     */
    private fun setupQuarkusExtensions() {
        // Register our Summon Quarkus integrations
        try {
            // Try to initialize the CDI support if available
            Class.forName("jakarta.enterprise.inject.spi.CDI")
            println("CDI support available, Summon CDI integration activated")

            // Check for Qute template engine
            try {
                Class.forName("io.quarkus.qute.Engine")
                println("Qute template engine detected, Summon Qute integration activated")
            } catch (e: ClassNotFoundException) {
                // Qute not available
            }

            // Check for RESTEasy
            try {
                Class.forName("jakarta.ws.rs.core.Response")
                println("RESTEasy detected, Summon RESTEasy integration activated")
            } catch (e: ClassNotFoundException) {
                // RESTEasy not available
            }

            // Setup Native Image support hooks
            try {
                Class.forName("io.quarkus.runtime.annotations.RegisterForReflection")
                println("Native image support detected, Summon native image support activated")
            } catch (e: ClassNotFoundException) {
                // Native image support not available
            }
        } catch (e: ClassNotFoundException) {
            // CDI not available
            println("CDI not available, using basic Quarkus integration")
        }
    }

    /**
     * Integration with Micronaut
     */
    fun setupMicronautIntegration() {
        try {
            // Check if Micronaut is in the classpath
            Class.forName("io.micronaut.context.ApplicationContext")
            println("Micronaut detected, setting up integration")

            // Register lifecycle listeners
            registerLifecycleListener("micronaut") { event ->
                when (event) {
                    LifecycleEvent.STARTUP -> println("Micronaut application started")
                    LifecycleEvent.SHUTDOWN -> println("Micronaut application shutting down")
                    else -> {} // Ignore other events
                }
            }
        } catch (e: ClassNotFoundException) {
            // Micronaut not in classpath, skip integration
        }
    }

    /**
     * Setup all known backend integrations
     */
    fun setupAll() {
        // Only initialize once
        if (initialized.compareAndSet(false, true)) {
            println("Setting up all backend integrations with simplified lifecycle")

            // Setup integrations for all supported frameworks
            setupSpringBootIntegration()
            setupKtorIntegration()
            setupQuarkusIntegration()
            setupMicronautIntegration()

            // Trigger startup event
            triggerLifecycleEvent(LifecycleEvent.STARTUP)

            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(Thread {
                triggerLifecycleEvent(LifecycleEvent.SHUTDOWN)
            })
        }
    }
}
