package code.yousef.summon.integrations.quarkus

import code.yousef.summon.core.Composable
import io.quarkus.runtime.annotations.RegisterForReflection
import java.io.File

/**
 * Support for GraalVM native image generation with Summon components.
 *
 * This utility class provides the necessary configuration and annotations to make
 * Summon components work properly in a GraalVM native image, especially when using
 * Quarkus native builds.
 *
 * The main challenges addressed by this class are:
 * 1. Ensuring proper reflection registration for Summon components
 * 2. Resource inclusion for static assets
 * 3. Proxy generation for CDI-injectable components
 */
@RegisterForReflection
object NativeImageSupport {

    /**
     * Base annotation to mark a Summon component for native image reflection.
     * Use this on components that need to be instantiated in a native image.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    @RegisterForReflection
    annotation class ReflectiveComponent

    /**
     * Annotation to specify that a component includes resource files
     * that should be included in the native image.
     *
     * @param resourcePaths Array of resource paths relative to the class path
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class IncludeResources(val resourcePaths: Array<String>)

    /**
     * Ensures that a component is registered for reflection in a native image.
     *
     * @param componentClass The component class to register
     */
    fun registerForReflection(componentClass: Class<*>) {
        // In an actual implementation, this would generate a reflection configuration
        // file for GraalVM native image building. Here it's just a placeholder.
        println("Registering ${componentClass.name} for reflection in native image")
    }

    /**
     * Generates reflection and resource configuration files for GraalVM native image.
     *
     * @param outputDir The directory where configuration files will be written
     * @param packageNames The package names to scan for components
     */
    fun generateNativeImageConfig(outputDir: File, vararg packageNames: String) {
        outputDir.mkdirs()

        // Write reflection-config.json
        val reflectionConfig = File(outputDir, "reflection-config.json")
        reflectionConfig.writeText(
            """
            [
              {
                "name" : "code.yousef.summon.core.Composable",
                "allDeclaredConstructors" : true,
                "allPublicConstructors" : true,
                "allDeclaredMethods" : true,
                "allPublicMethods" : true,
                "allDeclaredFields" : true,
                "allPublicFields" : true
              }
            ]
            """.trimIndent()
        )

        // Write resource-config.json
        val resourceConfig = File(outputDir, "resource-config.json")
        resourceConfig.writeText(
            """
            {
              "resources": {
                "includes": [
                  {"pattern": ".*\\.css$"},
                  {"pattern": ".*\\.js$"},
                  {"pattern": ".*\\.html$"}
                ]
              }
            }
            """.trimIndent()
        )
    }

    /**
     * Helper class for handling runtime reflection in native image context.
     */
    class ReflectionHelper {
        /**
         * Creates a new instance of a component using reflection.
         * This method is compatible with GraalVM native image.
         *
         * @param componentClass The component class to instantiate
         * @return A new instance of the component
         */
        fun <T : Any> createInstance(componentClass: Class<T>): T {
            return componentClass.getDeclaredConstructor().newInstance()
        }
    }

    /**
     * Registers a module of components for reflection in native image.
     * This can be used in a Quarkus extension to register all components in a module.
     */
    class ModuleRegistry {
        private val componentClasses = mutableListOf<Class<*>>()

        /**
         * Registers a component class for reflection.
         */
        fun register(componentClass: Class<*>) {
            componentClasses.add(componentClass)
        }

        /**
         * Generates the reflection configuration for all registered components.
         */
        fun generateReflectionConfig(): String {
            val entries = componentClasses.joinToString(",\n") { componentClass ->
                """
                {
                  "name" : "${componentClass.name}",
                  "allDeclaredConstructors" : true,
                  "allPublicConstructors" : true,
                  "allDeclaredMethods" : true,
                  "allPublicMethods" : true
                }
                """.trimIndent()
            }

            return "[\n$entries\n]"
        }
    }
} 
