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
            // Try to access routes using reflection - try multiple possible field names
            // since the internal structure of Router might change between versions
            val possibleFieldNames = listOf("routes", "routeList", "_routes", "routesByPath", "routeMap")
            var routes: List<*>? = null
            var fieldNameUsed = ""

            // Try each possible field name
            for (fieldName in possibleFieldNames) {
                try {
                    val field = router.javaClass.getDeclaredField(fieldName)
                    field.isAccessible = true
                    val fieldValue = field.get(router)

                    // Check if the field is a collection of some kind
                    if (fieldValue is List<*>) {
                        routes = fieldValue
                        fieldNameUsed = fieldName
                        logger.info("Successfully accessed routes using field name: $fieldName")
                        break
                    } else if (fieldValue is Collection<*>) {
                        routes = fieldValue.toList()
                        fieldNameUsed = fieldName
                        logger.info("Successfully accessed routes using field name: $fieldName (converted from Collection)")
                        break
                    } else if (fieldValue is Map<*, *>) {
                        routes = fieldValue.values.toList()
                        fieldNameUsed = fieldName
                        logger.info("Successfully accessed routes using field name: $fieldName (converted from Map values)")
                        break
                    }
                } catch (e: NoSuchFieldException) {
                    logger.debug("Field $fieldName not found in Router class")
                } catch (e: Exception) {
                    logger.debug("Error accessing field $fieldName: ${e.message}")
                }
            }

            // If we couldn't find routes through reflection, try to get some info from the router object itself
            if (routes == null) {
                logger.warn("Could not access routes through reflection. Dumping Router object:")
                dumpObject(router, "  ")
                return
            }

            // Process the routes we found
            logger.info("Found ${routes.size} routes using field: $fieldNameUsed")
            routes.forEachIndexed { index, route ->
                logger.info("Route[$index]: ${route?.javaClass?.name}")

                // Dump all fields of each route for debugging
                if (route != null) {
                    dumpObject(route, "  ")
                }

                // Try to find the path field - again, try multiple possible names
                val pathFieldNames = listOf("path", "pathPattern", "pattern", "routePath", "_path")
                var path: String? = null

                for (pathFieldName in pathFieldNames) {
                    try {
                        val pathField = route?.javaClass?.getDeclaredField(pathFieldName)
                        pathField?.isAccessible = true
                        val pathValue = pathField?.get(route)
                        if (pathValue is String) {
                            path = pathValue
                            logger.info("Found path using field: $pathFieldName")
                            break
                        }
                    } catch (e: Exception) {
                        // Ignore errors for individual field attempts
                    }
                }

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
