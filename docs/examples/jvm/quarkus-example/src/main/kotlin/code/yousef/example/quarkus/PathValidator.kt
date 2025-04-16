package code.yousef.example.quarkus

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.jboss.logging.Logger

/**
 * Class to validate paths at startup
 */
@ApplicationScoped
class PathValidator {
    private val logger = Logger.getLogger(PathValidator::class.java)

    fun onStart(@Observes event: StartupEvent) {
        logger.info("===== PATH VALIDATOR RUNNING =====")
        println("===== PATH VALIDATOR RUNNING =====")

        // Just print endpoint info to help identify missing leading slashes
        printEndpointInfo()

        logger.info("===== PATH VALIDATOR COMPLETE =====")
        println("===== PATH VALIDATOR COMPLETE =====")
    }

    private fun printEndpointInfo() {
        println("Checking for problematic WebSocket endpoint...")

        // Special check for the WebSocket endpoint that might be causing issues
        try {
            // Just print info to help identify the issue
            println("Looking for ChatSocket class...")
            val socketClass = Class.forName("code.yousef.example.quarkus.ChatSocket")
            println("Found ChatSocket class: $socketClass")

            // Print all annotations
            for (annotation in socketClass.annotations) {
                println("ChatSocket has annotation: ${annotation.annotationClass.java.name}")

                if (annotation.annotationClass.java.name.contains("ServerEndpoint")) {
                    println("Found ServerEndpoint annotation: $annotation")
                    // Try to get the value
                    try {
                        val valueMethod = annotation.annotationClass.java.getDeclaredMethod("value")
                        valueMethod.isAccessible = true
                        val pathValue = valueMethod.invoke(annotation)
                        println("ServerEndpoint path value: $pathValue")
                    } catch (e: Exception) {
                        println("Error getting ServerEndpoint value: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            println("Error checking WebSocket endpoint: ${e.message}")
        }
    }
} 