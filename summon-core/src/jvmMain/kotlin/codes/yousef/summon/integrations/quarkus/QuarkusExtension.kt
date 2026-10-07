package codes.yousef.summon.integration.quarkus

import codes.yousef.summon.components.foundation.TrustedHtml
import codes.yousef.summon.integration.quarkus.renderer.HtmxAwareRenderer
import jakarta.servlet.http.HttpServlet

/**
 * Quarkus Extension for Summon - Provides integration with Quarkus for server-side rendering.
 */
object QuarkusExtension {

    // Constants
    /** The property declaration value. */
    const val FEATURE = "summon"

    /**
     * Get the servlet class for web page rendering.
     * This is needed for Quarkus to detect the servlet.
     */
    fun getServletClass(): Class<out HttpServlet> {
        return SummonServlet::class.java
    }

    /**
     * A simple Summon renderer that can be injected via CDI.
     * This is a temporary implementation until we resolve issues with the complete renderer.
     */
    class SummonRenderer {
        private val templateRenderer = HtmxAwareRenderer()

        /**
         * Renders template.
         *
         * @param title The title value.
         * @param content Composable content emitted by this API.
         * @return The resulting value.
         */
        fun renderTemplate(title: String, content: TrustedHtml): String =
            templateRenderer.renderTemplate(title, content)

        /**
         * Renders heading.
         *
         * @param level The level value.
         * @param text The text value.
         * @return The resulting value.
         */
        fun renderHeading(level: Int, text: String): TrustedHtml {
            require(level in 1..6) { "Heading level must be between 1 and 6" }
            return TrustedHtml.fromAuthorCode("<h$level>${escapeHtmlText(text)}</h$level>")
        }

        /**
         * Renders paragraph.
         *
         * @param text The text value.
         * @return The resulting value.
         */
        fun renderParagraph(text: String): TrustedHtml =
            TrustedHtml.fromAuthorCode("<p>${escapeHtmlText(text)}</p>")

        /**
         * Renders button.
         *
         * @param label The label value.
         * @return The resulting value.
         */
        fun renderButton(label: String): TrustedHtml =
            TrustedHtml.fromAuthorCode("<button type=\"button\" class=\"btn\">${escapeHtmlText(label)}</button>")

        private fun escapeHtmlText(value: String): String = buildString(value.length) {
            value.forEach { character ->
                when (character) {
                    '&' -> append("&amp;")
                    '<' -> append("&lt;")
                    '>' -> append("&gt;")
                    '"' -> append("&quot;")
                    '\'' -> append("&#39;")
                    else -> append(character)
                }
            }
        }
    }
}
