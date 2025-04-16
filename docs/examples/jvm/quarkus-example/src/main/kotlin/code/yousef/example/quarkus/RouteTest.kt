package code.yousef.example.quarkus

import io.quarkus.runtime.StartupEvent
import io.vertx.ext.web.Router
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import jakarta.ws.rs.Path
import org.jboss.logging.Logger
import java.lang.reflect.Field

/**
 * Test class to help diagnose route path issues
 */
@ApplicationScoped
class RouteTest {
    private val logger = Logger.getLogger(RouteTest::class.java)

    @Inject
    lateinit var router: Router

    /**
     * Print route information at startup
     */
    fun onStart(@Observes event: StartupEvent) {
        logger.info("===== ROUTE DIAGNOSTICS START =====")

        // Log all JAX-RS classes with their path annotations
        logger.info("Scanning all loaded classes for @Path annotations...")
        val classLoader = Thread.currentThread().contextClassLoader

        // This will scan the ClassLoader classpath for all loaded classes
        val classes = getAllLoadedClasses(classLoader)
        for (clazz in classes) {
            try {
                val pathAnnotation = clazz.getAnnotation(Path::class.java)
                if (pathAnnotation != null) {
                    logger.info("Found @Path annotated class: ${clazz.name} with path: '${pathAnnotation.value}'")

                    // Also check methods
                    for (method in clazz.declaredMethods) {
                        val methodPathAnnotation = method.getAnnotation(Path::class.java)
                        if (methodPathAnnotation != null) {
                            logger.info("  Method ${method.name} has @Path: '${methodPathAnnotation.value}'")
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore errors for individual classes
            }
        }

        // Log routes that don't start with a slash
        logger.info("Examining routes in router...")

        try {
            // Using reflection to access routes since the API doesn't expose them directly
            val routesField = router.javaClass.getDeclaredField("routes")
            routesField.isAccessible = true
            val routes = routesField.get(router) as List<*>

            routes.forEachIndexed { index, route ->
                logger.info("Route[$index]: ${route?.javaClass?.name}")

                // Dump all fields of each route for debugging
                if (route != null) {
                    dumpObject(route, "  ")
                }

                val pathField = route?.javaClass?.getDeclaredField("path")
                pathField?.isAccessible = true
                val path = pathField?.get(route) as? String

                logger.info("Route[$index]: Path = '$path'")
                if (path != null && path.isNotEmpty() && !path.startsWith("/")) {
                    logger.error("PROBLEM FOUND: Route path doesn't start with /: '$path'")
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to inspect routes: ${e.message}", e)
        }

        logger.info("===== ROUTE DIAGNOSTICS END =====")
    }

    private fun dumpObject(obj: Any, indent: String) {
        val clazz = obj.javaClass
        logger.info("${indent}Object of type: ${clazz.name}")

        for (field in getAllFields(clazz)) {
            field.isAccessible = true
            try {
                val value = field.get(obj)
                logger.info("$indent  ${field.name} = $value")
            } catch (e: Exception) {
                logger.info("$indent  ${field.name} = [error accessing: ${e.message}]")
            }
        }
    }

    private fun getAllFields(clazz: Class<*>): List<Field> {
        val fields = mutableListOf<Field>()
        var currentClass: Class<*>? = clazz

        while (currentClass != null && currentClass != Any::class.java) {
            fields.addAll(currentClass.declaredFields)
            currentClass = currentClass.superclass
        }

        return fields
    }

    private fun getAllLoadedClasses(classLoader: ClassLoader): List<Class<*>> {
        val classes = mutableListOf<Class<*>>()

        // Try to get the Quarkus RuntimeClassLoader class loaders (reflection hack)
        try {
            val field = classLoader.javaClass.getDeclaredField("originalClassLoader")
            field.isAccessible = true
            val originalLoader = field.get(classLoader) as ClassLoader

            // Access loaded classes from QuarkusClassLoader
            val loadedClassesField = originalLoader.javaClass.getDeclaredField("loadedClasses")
            loadedClassesField.isAccessible = true

            @Suppress("UNCHECKED_CAST")
            val loadedClasses = loadedClassesField.get(originalLoader) as Map<String, Class<*>>
            classes.addAll(loadedClasses.values)
        } catch (e: Exception) {
            logger.warn("Could not access Quarkus class loader internals: ${e.message}")
        }

        return classes
    }
} 