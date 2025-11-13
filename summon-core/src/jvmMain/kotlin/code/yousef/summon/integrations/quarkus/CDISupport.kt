package codes.yousef.summon.integration.quarkus

import codes.yousef.summon.runtime.PlatformRenderer
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.enterprise.inject.spi.InjectionPoint
import jakarta.inject.Inject
import jakarta.inject.Qualifier
import kotlin.reflect.KClass

/**
 * Support for CDI injection into Summon components.
 *
 * This utility enables dependency injection in Summon components, making it possible to use
 * CDI beans directly in your UI components. It works by scanning components for fields
 * annotated with @Inject and resolving the dependencies from the CDI container.
 *
 * Usage:
 *
 * ```kotlin
 * @Composable
 * class UserProfileComponent {
 *     @Inject
 *     lateinit var userService: UserService
 *
 *     // Component implementation
 * }
 *
 * // When creating the component, use the CDISupport:
 * val component = CDISupport.create(UserProfileComponent::class)
 * ```
 */
@ApplicationScoped
class CDISupport {

    companion object {
        /**
         * Creates a new instance of a component with CDI dependencies injected.
         *
         * @param componentClass The Kotlin class of the component to create
         * @return A new instance with dependencies injected
         */
        fun <T : Any> create(componentClass: KClass<T>): T {
            val instance = componentClass.java.getDeclaredConstructor().newInstance()
            inject(instance)
            return instance
        }

        /**
         * Injects dependencies into an existing component instance.
         *
         * @param component The component instance to inject dependencies into
         */
        fun inject(component: Any) {
            val cdiContainer = jakarta.enterprise.inject.spi.CDI.current()

            component::class.java.declaredFields.forEach { field ->
                if (field.isAnnotationPresent(Inject::class.java)) {
                    field.isAccessible = true

                    // Get the bean from CDI container
                    val instance = cdiContainer.select(field.type)
                    if (!instance.isUnsatisfied) {
                        field.set(component, instance.get())
                    }
                }
            }
        }
    }

    /**
     * A CDI-aware renderer that automatically injects dependencies into components before rendering.
     */
    @ApplicationScoped
    class CDIAwareRenderer {
        /**
         * Renders a component, ensuring all CDI dependencies are injected first.
         *
         * @param component The component to render
         * @return HTML string representation of the component
         */
        fun render(component: Any): String {
            // First inject any CDI dependencies into the component
            inject(component)

            // Then render the component
            return renderToString(component)
        }

        /**
         * Renders a component to HTML string
         */
        private fun renderToString(component: Any): String {
            // Create a simple HTML representation
            return "<div class=\"summon-component\">Component: ${component::class.simpleName}</div>"
        }

        /**
         * Recursively injects dependencies into a component and all of its child components.
         *
         * @param component The root component to process
         */
        fun injectRecursive(component: Any) {
            inject(component)

            // This would need to be implemented based on how components track their children
            // For now, it's a placeholder to show the concept
        }
    }

    /**
     * A lazy component factory that creates components with CDI support.
     */
    @ApplicationScoped
    class ComponentFactory {
        /**
         * Creates a component instance with CDI support.
         *
         * @param componentClass The class of the component to create
         * @return A new component instance with dependencies injected
         */
        fun <T : Any> create(componentClass: Class<T>): T {
            val instance = componentClass.getDeclaredConstructor().newInstance()
            inject(instance)
            return instance
        }

        /**
         * Creates a component instance with CDI support and initial properties.
         *
         * @param componentClass The class of the component to create
         * @param initializer A lambda to initialize component properties
         * @return A new component instance with dependencies injected and properties initialized
         */
        fun <T : Any> create(componentClass: Class<T>, initializer: T.() -> Unit): T {
            val instance = componentClass.getDeclaredConstructor().newInstance()
            instance.initializer()
            inject(instance)
            return instance
        }
    }
}

/**
 * Provides CDI support for Summon components in Quarkus applications.
 *
 * This class contains utilities for working with Summon components in a CDI environment,
 * including producer methods and qualifiers.
 */
@ApplicationScoped
class SummonCDISupport {

    /**
     * Produces a renderer for Summon components.
     *
     * The renderer can be injected into CDI beans using:
     * ```
     * @Inject
     * lateinit var renderer: PlatformRenderer
     * ```
     */
    @Produces
    fun produceRenderer(): PlatformRenderer {
        return PlatformRenderer()
    }

    /**
     * Qualifier annotation for injecting components by their class.
     *
     * Usage:
     * ```
     * @Inject
     * @ComponentClass(MyComponent::class)
     * lateinit var component: Any
     * ```
     */
    @Qualifier
    @kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
    annotation class ComponentClass(val value: kotlin.reflect.KClass<*>)

    /**
     * Produces a component instance based on the injection point.
     *
     * This method looks for the [ComponentClass] qualifier and creates an instance
     * of the specified component class.
     */
    @Produces
    fun produceComponent(injectionPoint: InjectionPoint): Any {
        // Get the component class from the qualifier
        val qualifier = injectionPoint.qualifiers
            .filterIsInstance<ComponentClass>()
            .firstOrNull()
            ?: throw IllegalArgumentException("Component injection requires @ComponentClass qualifier")

        // Create an instance of the component
        val componentClass = qualifier.value.java
        return componentClass.getDeclaredConstructor().newInstance()
    }

    /**
     * Renders a Summon component to HTML.
     *
     * Usage:
     * ```
     * @Inject
     * lateinit var summonSupport: SummonCDISupport
     *
     * fun handleRequest() {
     *     val html = summonSupport.renderComponent(MyComponent())
     *     // Use the HTML...
     * }
     * ```
     */
    fun renderComponent(component: Any): String {
        // Create a simple HTML representation
        return "<div class=\"summon-component\">Component: ${component::class.simpleName}</div>"
    }

    /**
     * Factory method to create a component instance by its class.
     *
     * Usage:
     * ```
     * @Inject
     * lateinit var summonSupport: SummonCDISupport
     *
     * fun handleRequest() {
     *     val component = summonSupport.createComponent(MyComponent::class.java)
     *     // Use the component...
     * }
     * ```
     */
    fun <T : Any> createComponent(componentClass: Class<T>): T {
        return componentClass.getDeclaredConstructor().newInstance()
    }
} 
