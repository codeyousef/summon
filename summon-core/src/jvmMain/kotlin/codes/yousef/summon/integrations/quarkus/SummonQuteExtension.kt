package codes.yousef.summon.integration.quarkus

import codes.yousef.summon.runtime.PlatformRenderer
import io.quarkus.qute.EngineBuilder
import io.quarkus.qute.EvalContext
import io.quarkus.qute.NamespaceResolver
import io.quarkus.qute.RawString
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Integrates Summon components with Quarkus Qute templates.
 *
 * This extension allows Summon components to be used within Qute templates.
 * It registers a custom value resolver that renders Summon components to HTML.
 *
 * Usage:
 *
 * 1. In your Quarkus application, add a configuration class:
 *
 * ```kotlin
 * @Singleton
 * class QuteConfig {
 *     @Produces
 *     fun configureSummonQuteExtension(): Consumer<EngineBuilder> {
 *         return SummonQuteExtension()
 *     }
 * }
 * ```
 *
 * 2. In your Qute template, use Summon components:
 *
 * ```HTML
 * {#let myComponent=summon:component(com.example.MyComponent.create())}
 *   {myComponent}
 * {/let}
 * ```
 */
class SummonQuteExtension : Consumer<EngineBuilder> {
    private var config = Config()

    private fun renderToString(component: Any): String = when (component) {
        is String -> QuteComponentRegistry.renderComponent(component)
        is Function0<*> -> PlatformRenderer().renderComposableRoot { component.invoke() }
        else -> throw IllegalArgumentException(
            "Summon Qute components must be a registered component name or a composable function"
        )
    }

    /**
     * Executes the accept operation.
     *
     * @param builder The builder value.
     */
    override fun accept(builder: EngineBuilder) {
        builder.addNamespaceResolver(
            NamespaceResolver.builder("summon")
                .resolveAsync(::resolveNamespace)
                .build()
        )
    }

    private fun resolveNamespace(context: EvalContext): CompletionStage<Any?> {
        val evaluated = context.params.map { context.evaluate(it).toCompletableFuture() }
        return CompletableFuture.allOf(*evaluated.toTypedArray()).thenApply {
            val args = evaluated.map { it.join() }
            when (context.name) {
                "component" -> args.firstOrNull()?.let { component ->
                    RawString(withConfiguredBoundary(renderToString(component)))
                }

                "isComponent" -> {
                    val component = args.firstOrNull()
                    component is Function0<*> ||
                        (component is String && QuteComponentRegistry.contains(component))
                }

                "withContainer" -> args.firstOrNull()?.let { component ->
                    val id = args.getOrNull(1)?.toString()?.let {
                        " id=\"${escapeHtmlAttribute(it)}\""
                    }.orEmpty()
                    val className = args.getOrNull(2)?.toString() ?: "summon-component"
                    RawString(
                        "<div$id class=\"${escapeHtmlAttribute(className)}\">" +
                            withConfiguredBoundary(renderToString(component)) +
                            "</div>"
                    )
                }

                else -> null
            }
        }
    }

    private fun withConfiguredBoundary(html: String): String =
        if (config.includeComments) {
            "<!-- BEGIN SUMMON COMPONENT -->\n$html\n<!-- END SUMMON COMPONENT -->"
        } else {
            html
        }

    private fun escapeHtmlAttribute(value: String): String = buildString(value.length) {
        value.forEach { character ->
            append(
                when (character) {
                    '&' -> "&amp;"
                    '"' -> "&quot;"
                    '<' -> "&lt;"
                    '>' -> "&gt;"
                    else -> character
                }
            )
        }
    }

    /**
     * Provides additional configuration options for customizing Summon rendering in Qute templates.
     */
    class Config {
        /** The true value. */
        var usePrettyPrinting: Boolean = true
        /** The false value. */
        var includeComments: Boolean = false

        /**
         * Sets whether to use pretty printing when rendering Summon components.
         */
        fun usePrettyPrinting(value: Boolean): Config {
            this.usePrettyPrinting = value
            return this
        }

        /**
         * Sets whether to include HTML comments indicating Summon component boundaries.
         */
        fun includeComments(value: Boolean): Config {
            this.includeComments = value
            return this
        }
    }

    /** Provides summon qute extension factory and constant members. */
    companion object {
        /**
         * Creates a Summon Qute extension with the given configuration.
         */
        fun create(configure: Config.() -> Unit): SummonQuteExtension {
            val extension = SummonQuteExtension()
            extension.config = Config().apply(configure)
            return extension
        }

        /**
         * Helper method to render a Summon component to a string.
         */
        fun renderComponent(component: Any): String {
            val extension = SummonQuteExtension()
            return extension.renderToString(component)
        }

        /**
         * Creates a value supplier for a Summon component that can be used in Qute templates.
         */
        fun templateValue(component: Any): Supplier<String> {
            return Supplier { renderComponent(component) }
        }
    }
}
