package code.yousef.summon.integrations.quarkus

import code.yousef.summon.core.Composable
import code.yousef.summon.JvmPlatformRenderer
import code.yousef.summon.render
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.spi.CDI
import jakarta.inject.Inject
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
 * class UserProfileComponent : Composable {
 *     @Inject
 *     lateinit var userService: UserService
 *
 *     override fun <T> compose(receiver: T): T {
 *         val user = userService.getCurrentUser()
 *         // Render the user profile
 *     }
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
        fun <T : Composable> create(componentClass: KClass<T>): T {
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
            val cdiContainer = CDI.current()

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
        private val renderer = JvmPlatformRenderer()

        /**
         * Renders a component, ensuring all CDI dependencies are injected first.
         *
         * @param component The component to render
         * @return HTML string representation of the component
         */
        fun render(component: Composable): String {
            // First inject any CDI dependencies into the component
            inject(component)

            // Then render the component
            return renderer.render(component)
        }

        /**
         * Recursively injects dependencies into a component and all of its child components.
         *
         * @param component The root component to process
         */
        fun injectRecursive(component: Composable) {
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
        fun <T : Composable> create(componentClass: Class<T>): T {
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
        fun <T : Composable> create(componentClass: Class<T>, initializer: T.() -> Unit): T {
            val instance = componentClass.getDeclaredConstructor().newInstance()
            instance.initializer()
            inject(instance)
            return instance
        }
    }
} 