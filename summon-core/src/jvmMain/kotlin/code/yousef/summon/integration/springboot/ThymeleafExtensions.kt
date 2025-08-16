package code.yousef.summon.integration.springboot

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import kotlinx.html.div
import kotlinx.html.stream.appendHTML
import org.springframework.stereotype.Component
import org.thymeleaf.context.IExpressionContext
import org.thymeleaf.dialect.AbstractDialect
import org.thymeleaf.dialect.IExpressionObjectDialect
import org.thymeleaf.expression.IExpressionObjectFactory

/**
 * Extensions for integrating Summon with Spring Boot's Thymeleaf templates.
 *
 * IMPORTANT: To use these extensions, you must add the following dependencies to your project:
 * - org.springframework.boot:spring-boot-starter-web
 * - org.springframework.boot:spring-boot-starter-thymeleaf
 */
@Component
class SummonThymeleafDialect : AbstractDialect("summon"), IExpressionObjectDialect {
    private val expressionObjectFactory = SummonExpressionObjectFactory()

    override fun getExpressionObjectFactory(): IExpressionObjectFactory = expressionObjectFactory
}

/**
 * Expression object factory for Thymeleaf integration
 */
class SummonExpressionObjectFactory : IExpressionObjectFactory {
    private val expressionObjectNames = setOf("summon")

    override fun getAllExpressionObjectNames(): Set<String> = expressionObjectNames

    override fun buildObject(context: IExpressionContext, expressionObjectName: String): Any? {
        return if (expressionObjectName == "summon") {
            SummonExpressionObject()
        } else null
    }

    override fun isCacheable(expressionObjectName: String): Boolean = true
}

/**
 * Expression object that provides access to Summon component rendering
 */
class SummonExpressionObject {
    private val componentRegistry = mutableMapOf<String, (Map<String, Any>) -> @Composable () -> Unit>()
    private val renderer = PlatformRenderer()

    /**
     * Register a composable function with a name for use in Thymeleaf templates.
     *
     * @param name The name that will be used to reference this component in templates
     * @param factory A function that takes a map of properties and returns a composable
     */
    fun registerComponent(
        name: String,
        factory: (Map<String, Any>) -> @Composable () -> Unit
    ) {
        componentRegistry[name] = factory
    }

    /**
     * Render a Summon component by name with the provided properties.
     * This function can be called from Thymeleaf templates.
     *
     * @param componentName The name of the component to render
     * @param props The properties to pass to the component
     * @return HTML string output of the rendered component
     */
    fun render(componentName: String, props: Map<String, Any> = emptyMap()): String {
        val factory = componentRegistry[componentName]
            ?: throw IllegalArgumentException("Component '$componentName' not registered")

        val composable = factory(props)

        // Set up the renderer
        setPlatformRenderer(renderer)

        // Render the component to a string
        return buildString {
            appendHTML().div {
                // Render the content
                composable()
            }
        }
    }

    companion object {
        /**
         * Singleton instance for global registration
         */
        private val INSTANCE = SummonExpressionObject()

        /**
         * Register a component globally that will be available to all templates
         */
        fun registerGlobalComponent(
            name: String,
            factory: (Map<String, Any>) -> @Composable () -> Unit
        ) {
            INSTANCE.registerComponent(name, factory)
        }
    }
} 