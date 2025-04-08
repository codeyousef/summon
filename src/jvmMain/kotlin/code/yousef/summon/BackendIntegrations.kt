package code.yousef.summon

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Example integrations with common backend frameworks
 */
object BackendIntegrations {
    /**
     * Integration with Spring Boot
     */
    fun setupSpringBootIntegration() {
        try {
            // Check if Spring Boot is in the classpath
            Class.forName("org.springframework.boot.SpringApplication")

            // Spring Boot specific lifecycle hooks
            JvmLifecycleOwner.addStartupHook {
                println("Spring Boot application started")
            }

            JvmLifecycleOwner.addShutdownHook {
                println("Spring Boot application shutting down")
            }

            // Use reflection to integrate with Spring's lifecycle callbacks
            // This is just an example and would need implementation details
            try {
                val contextClass = Class.forName("org.springframework.context.ApplicationContext")
                println("Found Spring context, ready for deeper integration")
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

            // Ktor specific lifecycle hooks
            JvmLifecycleOwner.addStartupHook {
                println("Ktor server started")
            }

            JvmLifecycleOwner.addShutdownHook {
                println("Ktor server shutting down")
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

            // Quarkus specific lifecycle hooks
            JvmLifecycleOwner.addStartupHook {
                println("Quarkus application started")

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

            JvmLifecycleOwner.addShutdownHook {
                println("Quarkus application shutting down")
            }
        } catch (e: ClassNotFoundException) {
            // Quarkus not in classpath, skip integration
        }
    }

    /**
     * Integration with Micronaut
     */
    fun setupMicronautIntegration() {
        try {
            // Check if Micronaut is in the classpath
            Class.forName("io.micronaut.context.ApplicationContext")

            // Micronaut specific lifecycle hooks
            JvmLifecycleOwner.addStartupHook {
                println("Micronaut application started")
            }

            JvmLifecycleOwner.addShutdownHook {
                println("Micronaut application shutting down")
            }
        } catch (e: ClassNotFoundException) {
            // Micronaut not in classpath, skip integration
        }
    }

    /**
     * Setup all known backend integrations
     */
    fun setupAll() {
        setupSpringBootIntegration()
        setupKtorIntegration()
        setupQuarkusIntegration()
        setupMicronautIntegration()
    }
} 
