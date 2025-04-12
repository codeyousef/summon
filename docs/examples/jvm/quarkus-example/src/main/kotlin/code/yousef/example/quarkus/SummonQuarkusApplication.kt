package code.yousef.example.quarkus

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import org.jboss.logging.Logger

/**
 * Main application class for the Quarkus example.
 * 
 * This class handles application lifecycle events.
 */
@ApplicationScoped
class QuarkusApplication {
    private val logger = Logger.getLogger(QuarkusApplication::class.java)
    
    /**
     * Called when the application starts up.
     */
    fun onStart(@Observes event: StartupEvent) {
        logger.info("Quarkus Example application is starting...")
    }
    
    /**
     * Called when the application shuts down.
     */
    fun onStop(@Observes event: ShutdownEvent) {
        logger.info("Quarkus Example application is shutting down...")
    }
} 