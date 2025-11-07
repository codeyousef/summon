package code.yousef.summon.integration.quarkus.renderer

import code.yousef.summon.annotation.Composable
import code.yousef.summon.integration.quarkus.htmx.htmlAttribute
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import kotlinx.html.div
import kotlinx.html.stream.createHTML
import kotlinx.html.unsafe
import org.jboss.logging.Logger

/**
 * A renderer that is aware of HTMX attributes and raw HTML content.
 * This class wraps a JvmPlatformRenderer and processes modifiers to handle HTMX attributes
 * and raw HTML content properly.
 */
class HtmxAwareRenderer {
    private val logger = Logger.getLogger(HtmxAwareRenderer::class.java)
    private val platformRenderer = EnhancedJvmPlatformRenderer()

    /**
     * Renders a composable component to a string, handling HTMX attributes and raw HTML content.
     *
     * @param content The composable content to render
     * @return The rendered HTML as a string
     */
    fun renderToString(content: @Composable () -> Unit): String {
        logger.debug("HtmxAwareRenderer.renderToString() - Starting to render composable content")

        return try {
            setPlatformRenderer(platformRenderer)
            // Use the JvmPlatformRenderer to render the composable content
            logger.debug("HtmxAwareRenderer.renderToString() - Using platformRenderer.renderComposableRoot")
            val html = platformRenderer.renderComposableRoot {
                // Provide the platform renderer to the LocalPlatformRenderer
                val provided = LocalPlatformRenderer.provides(platformRenderer)
                provided.run {
                    content()
                }
            }

            logger.debug("HtmxAwareRenderer.renderToString() - Composable content rendered, HTML length: ${html.length}")

            // Process the HTML to handle HTMX attributes
            logger.debug("HtmxAwareRenderer.renderToString() - Processing HTML to handle HTMX attributes")
            val processedHtml = processHtml(html)

            logger.debug("HtmxAwareRenderer.renderToString() - HTML processing completed, processed HTML length: ${processedHtml.length}")

            processedHtml
        } catch (e: Exception) {
            logger.error("HtmxAwareRenderer.renderToString() - Error rendering composable content", e)
            throw e
        } finally {
            clearPlatformRenderer()
        }
    }

    /**
     * Process the HTML to handle HTMX attributes and raw HTML content.
     * This method processes the HTML output from the JvmPlatformRenderer and
     * converts style attributes containing HTMX attributes and raw HTML content
     * into proper HTML attributes and content.
     *
     * @param html The HTML to process
     * @return The processed HTML
     */
    private fun processHtml(html: String): String {
        logger.debug("HtmxAwareRenderer.processHtml() - Starting HTML processing, input HTML length: ${html.length}")

        // Log the first 500 characters of the HTML for debugging
        logger.debug("HtmxAwareRenderer.processHtml() - First 500 chars of input HTML: ${html.take(500)}")

        // Log the last 500 characters of the HTML for debugging
        logger.debug("HtmxAwareRenderer.processHtml() - Last 500 chars of input HTML: ${html.takeLast(500)}")

        var processedHtml = html

        // Process __html_attr_ prefixed attributes
        // This pattern looks for __html_attr_id:value;__html_attr_data-component:value;__html_attr___raw_html:content
        logger.debug("HtmxAwareRenderer.processHtml() - Processing __html_attr_ prefixed attributes")
        val htmlAttrPrefixedRegex =
            Regex("""style="__html_attr_id:([^;]+);__html_attr_data-component:([^;]+);__html_attr___raw_html:([^"]+)""")
        logger.debug("HtmxAwareRenderer.processHtml() - __html_attr_ prefixed attributes regex pattern: ${htmlAttrPrefixedRegex.pattern}")
        var match = htmlAttrPrefixedRegex.find(processedHtml)
        logger.debug("HtmxAwareRenderer.processHtml() - __html_attr_ prefixed attributes match found: ${match != null}")
        var htmlAttrPrefixedCount = 0

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]
            val rawHtml = match.groupValues[3]

            logger.debug(
                "HtmxAwareRenderer.processHtml() - Found __html_attr_ prefixed attributes: id=$id, component=$component, rawHtml=${
                    rawHtml.take(
                        50
                    )
                }..."
            )
            logger.debug("HtmxAwareRenderer.processHtml() - Full match: $fullMatch")
            logger.debug(
                "HtmxAwareRenderer.processHtml() - HTML context: ${
                    processedHtml.substring(
                        Math.max(
                            0,
                            match.range.first - 50
                        ), Math.min(processedHtml.length, match.range.last + 50)
                    )
                }"
            )
            htmlAttrPrefixedCount++

            // Decode the raw HTML content
            val decodedHtml = rawHtml
                .replace("&quot;", "\"")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&#39;", "'")
                .replace("&#x27;", "'")
                .replace("&#x2F;", "/")
                .replace("&#x60;", "`")
                .replace("&#x3D;", "=")

            // Replace with proper HTML attributes and raw HTML content
            processedHtml =
                processedHtml.replaceFirst(fullMatch, """id="$id" data-component="$component">$decodedHtml""")

            // Find the next match
            match = htmlAttrPrefixedRegex.find(processedHtml, match.range.first + 1)
        }

        logger.debug("HtmxAwareRenderer.processHtml() - Processed $htmlAttrPrefixedCount __html_attr_ prefixed attributes")

        // Process trailing component attributes (;id:component-container;data-component:component>)
        // This pattern handles both standalone attributes and those at the end of a tag
        logger.debug("HtmxAwareRenderer.processHtml() - Processing trailing component attributes")
        val trailingAttrRegex = Regex(""";id:([^;]+);data-component:([^>]+)(?:>|&gt;|"|'|\s)""")
        logger.debug("HtmxAwareRenderer.processHtml() - Trailing component attributes regex pattern: ${trailingAttrRegex.pattern}")
        match = trailingAttrRegex.find(processedHtml)
        logger.debug("HtmxAwareRenderer.processHtml() - Trailing component attributes match found: ${match != null}")
        var trailingAttrCount = 0

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]
            val endChar = fullMatch.last()

            logger.debug("HtmxAwareRenderer.processHtml() - Found trailing component attribute: id=$id, component=$component")
            logger.debug("HtmxAwareRenderer.processHtml() - Full match: $fullMatch")
            logger.debug(
                "HtmxAwareRenderer.processHtml() - HTML context: ${
                    processedHtml.substring(
                        Math.max(
                            0,
                            match.range.first - 50
                        ), Math.min(processedHtml.length, match.range.last + 50)
                    )
                }"
            )
            trailingAttrCount++

            // Replace with proper HTML attributes, preserving the ending character
            val replacement = if (endChar == '>' || endChar == '"' || endChar == '\'' || endChar.isWhitespace()) {
                """ id="$id" data-component="$component"$endChar"""
            } else {
                """ id="$id" data-component="$component">"""
            }

            processedHtml = processedHtml.replaceFirst(fullMatch, replacement)

            // Find the next match
            match = trailingAttrRegex.find(processedHtml, match.range.first + 1)
        }

        logger.debug("HtmxAwareRenderer.processHtml() - Processed $trailingAttrCount trailing component attributes")

        // Also handle the case where &gt; is used instead of >
        val escapedTrailingAttrRegex = Regex(""";id:([^;]+);data-component:([^&]+)&gt;""")
        match = escapedTrailingAttrRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]

            // Replace with proper HTML attributes
            processedHtml = processedHtml.replaceFirst(fullMatch, """ id="$id" data-component="$component">""")

            // Find the next match
            match = escapedTrailingAttrRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where the attributes are at the end of the string without a closing bracket
        val endOfStringAttrRegex = Regex(""";id:([^;]+);data-component:([^;]+)$""")
        match = endOfStringAttrRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]

            // Replace with proper HTML attributes
            processedHtml = processedHtml.replaceFirst(fullMatch, """ id="$id" data-component="$component">""")

            // Find the next match
            match = endOfStringAttrRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where __html_attr_ prefixed attributes are used as standalone attributes
        // This pattern looks for __html_attr_id:value;__html_attr_data-component:value;
        val htmlAttrStandaloneRegex = Regex("""__html_attr_id:([^;]+);__html_attr_data-component:([^;]+);""")
        match = htmlAttrStandaloneRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]

            // Replace with proper HTML attributes
            processedHtml = processedHtml.replaceFirst(fullMatch, """ id="$id" data-component="$component" """)

            // Find the next match
            match = htmlAttrStandaloneRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where __html_attr_ prefixed attributes are used at the beginning of a tag
        // This pattern looks for <div __html_attr_id:value;__html_attr_data-component:value;
        val htmlAttrTagStartRegex = Regex("""<div __html_attr_id:([^;]+);__html_attr_data-component:([^;]+);""")
        match = htmlAttrTagStartRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]

            // Replace with proper HTML attributes
            processedHtml = processedHtml.replaceFirst(fullMatch, """<div id="$id" data-component="$component" """)

            // Find the next match
            match = htmlAttrTagStartRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where style attribute contains id and data-component attributes
        // This pattern looks for style=" id="value" data-component="value" &lt;div style="&quot;text-align:
        val styleWithIdAndComponentRegex =
            Regex("""style="\s*id="([^"]+)"\s*data-component="([^"]+)"\s*&lt;div\s*style="&quot;text-align:""")
        match = styleWithIdAndComponentRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]

            // Replace with proper HTML attributes
            processedHtml =
                processedHtml.replaceFirst(fullMatch, """id="$id" data-component="$component" style="text-align:""")

            // Find the next match
            match = styleWithIdAndComponentRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle another variation of the malformed attributes pattern
        // This pattern looks for style=" id="value" data-component="value" 
        val styleWithIdAndComponentRegex2 = Regex("""style="\s*id="([^"]+)"\s*data-component="([^"]+)"\s*""")
        match = styleWithIdAndComponentRegex2.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]

            // Replace with proper HTML attributes
            processedHtml = processedHtml.replaceFirst(fullMatch, """id="$id" data-component="$component" """)

            // Find the next match
            match = styleWithIdAndComponentRegex2.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where style attribute is followed by id and data-component attributes
        // This pattern looks for style=" id="hero-component-container" data-component="hero"
        val styleFollowedByIdAndComponentRegex = Regex("""style="\s+id="([^"]+)"\s+data-component="([^"]+)"""")
        match = styleFollowedByIdAndComponentRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]

            // Replace with proper HTML attributes
            processedHtml = processedHtml.replaceFirst(fullMatch, """id="$id" data-component="$component" style="""")

            // Find the next match
            match = styleFollowedByIdAndComponentRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where style attribute is followed by id and data-component attributes and a malformed div tag
        // This pattern looks for style=" id="hero-component-container" data-component="hero" &lt;div style="&quot;text-align:
        logger.debug("HtmxAwareRenderer.processHtml() - Processing style attribute followed by id and data-component attributes and a malformed div tag")
        val styleFollowedByIdAndComponentAndMalformedDivRegex =
            Regex("""style="\s*id="([^"]+)"\s*data-component="([^"]+)"\s*&lt;div\s*style="&quot;([^:]+):""")
        logger.debug("HtmxAwareRenderer.processHtml() - Style followed by id and component and malformed div regex pattern: ${styleFollowedByIdAndComponentAndMalformedDivRegex.pattern}")
        match = styleFollowedByIdAndComponentAndMalformedDivRegex.find(processedHtml)
        logger.debug("HtmxAwareRenderer.processHtml() - Style followed by id and component and malformed div match found: ${match != null}")
        var styleFollowedByIdAndComponentAndMalformedDivCount = 0

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]
            val styleProperty = match.groupValues[3]

            logger.debug("HtmxAwareRenderer.processHtml() - Found style attribute followed by id and data-component attributes and a malformed div tag: id=$id, component=$component, styleProperty=$styleProperty")
            logger.debug("HtmxAwareRenderer.processHtml() - Full match: $fullMatch")
            logger.debug(
                "HtmxAwareRenderer.processHtml() - HTML context: ${
                    processedHtml.substring(
                        Math.max(
                            0,
                            match.range.first - 50
                        ), Math.min(processedHtml.length, match.range.last + 50)
                    )
                }"
            )
            styleFollowedByIdAndComponentAndMalformedDivCount++

            // Replace with proper HTML attributes and fix the malformed div tag
            processedHtml = processedHtml.replaceFirst(
                fullMatch,
                """id="$id" data-component="$component"><div style="$styleProperty:"""
            )

            // Find the next match
            match = styleFollowedByIdAndComponentAndMalformedDivRegex.find(processedHtml, match.range.first + 1)
        }

        logger.debug("HtmxAwareRenderer.processHtml() - Processed $styleFollowedByIdAndComponentAndMalformedDivCount style attributes followed by id and data-component attributes and a malformed div tag")

        // Handle the case where id and data-component attributes are followed by a malformed div tag with escaped HTML entities
        // This pattern looks for id="hero-component-container" data-component="hero" &lt;div style="&quot;text-align:"
        logger.debug("HtmxAwareRenderer.processHtml() - Processing id and data-component attributes followed by malformed div tag")
        val idComponentFollowedByMalformedDivRegex =
            Regex("""id="([^"]+)"\s*data-component="([^"]+)"\s*&lt;div\s*style="&quot;([^:]+):""")
        logger.debug("HtmxAwareRenderer.processHtml() - Id and component followed by malformed div regex pattern: ${idComponentFollowedByMalformedDivRegex.pattern}")
        match = idComponentFollowedByMalformedDivRegex.find(processedHtml)
        logger.debug("HtmxAwareRenderer.processHtml() - Id and component followed by malformed div match found: ${match != null}")
        var malformedDivCount = 0

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]
            val styleProperty = match.groupValues[3]

            logger.debug("HtmxAwareRenderer.processHtml() - Found malformed div tag: id=$id, component=$component, styleProperty=$styleProperty")
            logger.debug("HtmxAwareRenderer.processHtml() - Full match: $fullMatch")
            logger.debug(
                "HtmxAwareRenderer.processHtml() - HTML context: ${
                    processedHtml.substring(
                        Math.max(
                            0,
                            match.range.first - 50
                        ), Math.min(processedHtml.length, match.range.last + 50)
                    )
                }"
            )
            malformedDivCount++

            // Replace with proper HTML attributes and fix the malformed div tag
            processedHtml = processedHtml.replaceFirst(
                fullMatch,
                """id="$id" data-component="$component"><div style="$styleProperty:"""
            )

            // Find the next match
            match = idComponentFollowedByMalformedDivRegex.find(processedHtml, match.range.first + 1)
        }

        logger.debug("HtmxAwareRenderer.processHtml() - Processed $malformedDivCount malformed div tags")

        // Handle the case where id and data-component attributes are followed by a malformed div tag with class attribute
        // This pattern looks for id="counter-component-container" data-component="counter" &lt;div class=&quot;card&quot;
        logger.debug("HtmxAwareRenderer.processHtml() - Processing id and data-component attributes followed by malformed div tag with class attribute")
        val idComponentFollowedByMalformedDivWithClassRegex =
            Regex("""id="([^"]+)"\s*data-component="([^"]+)"\s*&lt;div\s*class=&quot;([^&]+)&quot;\s*style="&quot;""")
        logger.debug("HtmxAwareRenderer.processHtml() - Id and component followed by malformed div with class regex pattern: ${idComponentFollowedByMalformedDivWithClassRegex.pattern}")
        match = idComponentFollowedByMalformedDivWithClassRegex.find(processedHtml)
        logger.debug("HtmxAwareRenderer.processHtml() - Id and component followed by malformed div with class match found: ${match != null}")
        var malformedDivWithClassCount = 0

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]
            val className = match.groupValues[3]

            logger.debug("HtmxAwareRenderer.processHtml() - Found malformed div tag with class attribute: id=$id, component=$component, className=$className")
            logger.debug("HtmxAwareRenderer.processHtml() - Full match: $fullMatch")
            logger.debug(
                "HtmxAwareRenderer.processHtml() - HTML context: ${
                    processedHtml.substring(
                        Math.max(
                            0,
                            match.range.first - 50
                        ), Math.min(processedHtml.length, match.range.last + 50)
                    )
                }"
            )
            malformedDivWithClassCount++

            // Replace with proper HTML attributes and fix the malformed div tag
            processedHtml = processedHtml.replaceFirst(
                fullMatch,
                """id="$id" data-component="$component"><div class="$className" style="text-align:"""
            )

            // Find the next match
            match = idComponentFollowedByMalformedDivWithClassRegex.find(processedHtml, match.range.first + 1)
        }

        logger.debug("HtmxAwareRenderer.processHtml() - Processed $malformedDivWithClassCount malformed div tags with class attribute")

        // Handle the case where attributes use equals sign instead of colon and have extra quotation marks
        // This pattern looks for any attribute in the format name=value" with extra quotation marks
        val equalsAttrRegex = Regex("""(\w+)=([^"]+)" (\w+(?:-\w+)*)="([^"]+)""(?:>|&gt;|\s|$)""")
        match = equalsAttrRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val attr1Name = match.groupValues[1]
            val attr1Value = match.groupValues[2]
            val attr2Name = match.groupValues[3]
            val attr2Value = match.groupValues[4]
            val endChar = if (fullMatch.endsWith(">") || fullMatch.endsWith("&gt;")) ">" else ""

            // Replace with proper HTML attributes
            processedHtml =
                processedHtml.replaceFirst(fullMatch, """ $attr1Name="$attr1Value" $attr2Name="$attr2Value"$endChar""")

            // Find the next match
            match = equalsAttrRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where a single attribute uses equals sign instead of colon and has extra quotation marks
        val singleEqualsAttrRegex = Regex("""(\w+)=([^"]+)""(?:>|&gt;|\s|$)""")
        match = singleEqualsAttrRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val attrName = match.groupValues[1]
            val attrValue = match.groupValues[2]
            val endChar = if (fullMatch.endsWith(">") || fullMatch.endsWith("&gt;")) ">" else ""

            // Replace with proper HTML attribute
            processedHtml = processedHtml.replaceFirst(fullMatch, """ $attrName="$attrValue"$endChar""")

            // Find the next match
            match = singleEqualsAttrRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where <div style="div style=..."> is used (malformed opening tag)
        val malformedDivRegex = Regex("""<div style="div style=([^>]+)>""")
        match = malformedDivRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val styleContent = match.groupValues[1]

            // Replace with proper div tag
            processedHtml = processedHtml.replaceFirst(fullMatch, """<div style=$styleContent>""")

            // Find the next match
            match = malformedDivRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where <div <div="" style=...> is used (malformed opening tag)
        val malformedDiv2Regex = Regex("""<div <div=""([^>]+)>""")
        match = malformedDiv2Regex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val attributes = match.groupValues[1]

            // Replace with proper div tag
            processedHtml = processedHtml.replaceFirst(fullMatch, """<div$attributes>""")

            // Find the next match
            match = malformedDiv2Regex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where style="style="text-align: center" padding: 2rem;" is used (malformed style attribute)
        val malformedStyleRegex = Regex("""style="style="([^"]*)" ([^"]*)"""")
        match = malformedStyleRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val style1 = match.groupValues[1]
            val style2 = match.groupValues[2]

            // Combine the styles
            val combinedStyle = "$style1; $style2"

            // Replace with proper style attribute
            processedHtml = processedHtml.replaceFirst(fullMatch, """style="$combinedStyle"""")

            // Find the next match
            match = malformedStyleRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where __html_attr___raw_html is used without a value
        val rawHtmlNoValueRegex = Regex("""__html_attr___raw_html(?:=|:)""")
        match = rawHtmlNoValueRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value

            // Replace with empty string
            processedHtml = processedHtml.replaceFirst(fullMatch, "")

            // Find the next match
            match = rawHtmlNoValueRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where style="style:text-align: center; padding: 2rem;" is used (malformed style attribute)
        val malformedStyleColonRegex = Regex("""style="style:([^"]*)"""")
        match = malformedStyleColonRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val styleContent = match.groupValues[1]

            // Replace with proper style attribute
            processedHtml = processedHtml.replaceFirst(fullMatch, """style="$styleContent"""")

            // Find the next match
            match = malformedStyleColonRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where style has missing quotes
        val missingQuotesRegex = Regex("""style=([^"'][^ >]+)""")
        match = missingQuotesRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val styleContent = match.groupValues[1]

            // Replace with proper style attribute
            processedHtml = processedHtml.replaceFirst(fullMatch, """style="$styleContent"""")

            // Find the next match
            match = missingQuotesRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where there are unescaped quotes in attribute values
        val unescapedQuotesRegex = Regex("""([a-zA-Z0-9-]+)="([^"]*)"([^"]*)"([^"]*)"""")
        match = unescapedQuotesRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val attrName = match.groupValues[1]
            val attrValue1 = match.groupValues[2]
            val attrValue2 = match.groupValues[3]
            val attrValue3 = match.groupValues[4]

            // Combine the attribute values
            val combinedValue = "$attrValue1\"$attrValue2\"$attrValue3"

            // Replace with proper attribute
            processedHtml = processedHtml.replaceFirst(fullMatch, """$attrName="$combinedValue"""")

            // Find the next match
            match = unescapedQuotesRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where there are HTML entities in attribute values that need to be decoded
        val htmlEntitiesRegex = Regex("""([a-zA-Z0-9-]+)="([^"]*&[lg]t;[^"]*)"""")
        match = htmlEntitiesRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val attrName = match.groupValues[1]
            val attrValue = match.groupValues[2]

            // Decode HTML entities
            val decodedValue = attrValue
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&#39;", "'")
                .replace("&#x27;", "'")
                .replace("&#x2F;", "/")
                .replace("&#x60;", "`")
                .replace("&#x3D;", "=")

            // Replace with proper attribute
            processedHtml = processedHtml.replaceFirst(fullMatch, """$attrName="$decodedValue"""")

            // Find the next match
            match = htmlEntitiesRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where there are broken div tags with missing closing brackets
        val brokenDivRegex = Regex("""<div([^>]*?)$""")
        match = brokenDivRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val attributes = match.groupValues[1]

            // Replace with proper div tag
            processedHtml = processedHtml.replaceFirst(fullMatch, """<div$attributes>""")

            // Find the next match
            match = brokenDivRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where there are broken closing div tags
        val brokenClosingDivRegex = Regex("""</div([^>]*)$""")
        match = brokenClosingDivRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val extra = match.groupValues[1]

            // Replace with proper closing div tag
            processedHtml = processedHtml.replaceFirst(fullMatch, """</div>$extra""")

            // Find the next match
            match = brokenClosingDivRegex.find(processedHtml, match.range.first + 1)
        }

        // Handle the case where attributes have extra quotation marks at the end
        // This pattern looks for attributes in the format id="value" data-component="value"">
        val extraQuotesAttrRegex = Regex("""id="([^"]+)" data-component="([^"]+)"">""")
        match = extraQuotesAttrRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val id = match.groupValues[1]
            val component = match.groupValues[2]

            // Replace with proper HTML attributes
            processedHtml = processedHtml.replaceFirst(fullMatch, """id="$id" data-component="$component">""")

            // Find the next match
            match = extraQuotesAttrRegex.find(processedHtml, match.range.first + 1)
        }

        // First, process HTMX attributes in style attributes
        // This regex looks for style attributes containing HTMX attributes or __html_attr_ prefixed attributes
        val styleAttrRegex = Regex("""style="([^"]*)"""")
        match = styleAttrRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val styleContent = match.groupValues[1]

            // Check if this style attribute contains HTMX attributes or __html_attr_ prefixed attributes
            if (styleContent.contains("hx-") || styleContent.contains("__html_attr_") ||
                styleContent.contains("div style=") || styleContent.contains("div class=")
            ) {

                // Handle the case where the style attribute contains escaped HTML with HTMX attributes
                if (styleContent.contains("div style=") || styleContent.contains("div class=")) {
                    // This is likely raw HTML content that was incorrectly rendered as a style attribute
                    // Extract the raw HTML content and decode it
                    val decodedHtml = styleContent
                        .replace("&quot;", "\"")
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replace("&amp;", "&")
                        .replace("&#39;", "'")
                        .replace("&#x27;", "'")
                        .replace("&#x2F;", "/")
                        .replace("&#x60;", "`")
                        .replace("&#x3D;", "=")

                    // Replace the style attribute with the decoded HTML content
                    processedHtml = processedHtml.replaceFirst(fullMatch, decodedHtml)
                } else {
                    // Split the style content by semicolons to get individual style properties
                    val styleProps = styleContent.split(";").filter { it.isNotEmpty() }

                    // Separate regular style properties from HTMX and HTML attributes
                    val regularStyleProps = mutableListOf<String>()
                    val htmlAttributes = mutableListOf<Pair<String, String>>()

                    for (prop in styleProps) {
                        val parts = prop.trim().split(":", limit = 2)
                        if (parts.size == 2) {
                            val key = parts[0].trim()
                            val value = parts[1].trim()

                            if (key.startsWith("hx-") || key.startsWith("__html_attr_")) {
                                val attrName = if (key.startsWith("__html_attr_")) {
                                    key.removePrefix("__html_attr_")
                                } else {
                                    key
                                }
                                htmlAttributes.add(attrName to value)
                            } else {
                                regularStyleProps.add("$key: $value")
                            }
                        } else {
                            // Handle malformed style properties
                            regularStyleProps.add(prop.trim())
                        }
                    }

                    // Create the new style attribute with only regular style properties
                    val newStyleAttr = if (regularStyleProps.isEmpty()) {
                        ""
                    } else {
                        "style=\"${regularStyleProps.joinToString("; ")}\""
                    }

                    // Create the HTML attributes string
                    val htmlAttrsStr = htmlAttributes.joinToString(" ") { (name, value) ->
                        // Properly format the attribute value with quotes
                        val formattedValue = value.replace("\"", "&quot;")
                        "$name=\"$formattedValue\""
                    }

                    // Create the replacement string
                    val replacement = if (newStyleAttr.isEmpty()) {
                        htmlAttrsStr
                    } else if (htmlAttrsStr.isEmpty()) {
                        newStyleAttr
                    } else {
                        "$newStyleAttr $htmlAttrsStr"
                    }

                    // Replace the original style attribute with the new attributes
                    processedHtml = processedHtml.replaceFirst(fullMatch, replacement)
                }
            }

            // Find the next match
            match = styleAttrRegex.find(processedHtml, match.range.first + 1)
        }

        // Next, process raw HTML content
        // This regex looks for style attributes containing __raw_html
        // We need to handle both the standard format and the escaped format
        val rawHtmlRegex = Regex("""style="([^"]*?)__raw_html:([^"]*)"""")
        val escapedRawHtmlRegex = Regex("""style="([^"]*?)__raw_html=([^"]*)"""")
        val htmlAttrRawHtmlRegex = Regex("""__html_attr___raw_html:([^;]*);""")
        val htmlAttrRawHtmlEndRegex = Regex("""__html_attr___raw_html:([^"]*)">\s*</div>""")
        val htmlAttrRawHtmlStandaloneRegex = Regex("""__html_attr___raw_html="([^"]*)"(?!\s*</div>)""")

        // Process __html_attr___raw_html format with semicolon
        match = htmlAttrRawHtmlRegex.find(processedHtml)
        while (match != null) {
            val fullMatch = match.value
            val rawHtml = match.groupValues[1]

            // Decode the raw HTML content
            val decodedHtml = rawHtml
                .replace("&quot;", "\"")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&#39;", "'")
                .replace("&#x27;", "'")
                .replace("&#x2F;", "/")
                .replace("&#x60;", "`")
                .replace("&#x3D;", "=")

            // Replace the __html_attr___raw_html attribute with the decoded HTML content
            processedHtml = processedHtml.replaceFirst(fullMatch, decodedHtml)

            // Find the next match
            match = htmlAttrRawHtmlRegex.find(processedHtml, match.range.first + 1)
        }

        // Process __html_attr___raw_html format at the end of the element
        match = htmlAttrRawHtmlEndRegex.find(processedHtml)
        while (match != null) {
            val fullMatch = match.value
            val rawHtml = match.groupValues[1]

            // Decode the raw HTML content
            val decodedHtml = rawHtml
                .replace("&quot;", "\"")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&#39;", "'")
                .replace("&#x27;", "'")
                .replace("&#x2F;", "/")
                .replace("&#x60;", "`")
                .replace("&#x3D;", "=")

            // Replace the __html_attr___raw_html attribute with the decoded HTML content
            processedHtml = processedHtml.replaceFirst(fullMatch, "\">$decodedHtml</div>")

            // Find the next match
            match = htmlAttrRawHtmlEndRegex.find(processedHtml, match.range.first + 1)
        }

        // Process __html_attr___raw_html format as a standalone attribute
        match = htmlAttrRawHtmlStandaloneRegex.find(processedHtml)
        while (match != null) {
            val fullMatch = match.value
            val rawHtml = match.groupValues[1]

            // Decode the raw HTML content
            val decodedHtml = rawHtml
                .replace("&quot;", "\"")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&#39;", "'")
                .replace("&#x27;", "'")
                .replace("&#x2F;", "/")
                .replace("&#x60;", "`")
                .replace("&#x3D;", "=")

            // Replace the __html_attr___raw_html attribute with the decoded HTML content
            processedHtml = processedHtml.replaceFirst(fullMatch, "\">" + decodedHtml)

            // Find the next match
            match = htmlAttrRawHtmlStandaloneRegex.find(processedHtml, match.range.first + 1)
        }

        // Process standard format
        match = rawHtmlRegex.find(processedHtml)
        while (match != null) {
            val fullMatch = match.value
            val stylePrefix = match.groupValues[1]
            val rawHtml = match.groupValues[2]

            // Extract any remaining style properties
            val remainingStyles = stylePrefix.split(";")
                .filter { it.isNotEmpty() && !it.contains("__raw_html") }
                .joinToString("; ")

            // Create the new style attribute without the raw HTML content
            val newStyleAttr = if (remainingStyles.isEmpty()) {
                ""
            } else {
                "style=\"$remainingStyles\""
            }

            // Decode the raw HTML content
            // Use a more comprehensive HTML entity decoding
            val decodedHtml = rawHtml
                .replace("&quot;", "\"")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&#39;", "'")
                .replace("&#x27;", "'")
                .replace("&#x2F;", "/")
                .replace("&#x60;", "`")
                .replace("&#x3D;", "=")

            // Create the replacement with the raw HTML content
            // Don't add any extra tags that could cause unbalanced HTML
            val replacement = if (newStyleAttr.isEmpty()) {
                decodedHtml
            } else {
                "$newStyleAttr $decodedHtml"
            }

            // Replace the style attribute with the new content
            processedHtml = processedHtml.replaceFirst(fullMatch, replacement)

            // Find the next match
            match = rawHtmlRegex.find(processedHtml, match.range.first + 1)
        }

        // Process escaped format
        match = escapedRawHtmlRegex.find(processedHtml)
        while (match != null) {
            val fullMatch = match.value
            val stylePrefix = match.groupValues[1]
            val rawHtml = match.groupValues[2]

            // Extract any remaining style properties
            val remainingStyles = stylePrefix.split(";")
                .filter { it.isNotEmpty() && !it.contains("__raw_html") }
                .joinToString("; ")

            // Create the new style attribute without the raw HTML content
            val newStyleAttr = if (remainingStyles.isEmpty()) {
                ""
            } else {
                "style=\"$remainingStyles\""
            }

            // Decode the raw HTML content
            // Use a more comprehensive HTML entity decoding
            val decodedHtml = rawHtml
                .replace("&quot;", "\"")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&#39;", "'")
                .replace("&#x27;", "'")
                .replace("&#x2F;", "/")
                .replace("&#x60;", "`")
                .replace("&#x3D;", "=")

            // Create the replacement with the raw HTML content
            // Don't add any extra tags that could cause unbalanced HTML
            val replacement = if (newStyleAttr.isEmpty()) {
                decodedHtml
            } else {
                "$newStyleAttr $decodedHtml"
            }

            // Replace the style attribute with the new content
            processedHtml = processedHtml.replaceFirst(fullMatch, replacement)

            // Find the next match
            match = escapedRawHtmlRegex.find(processedHtml, match.range.first + 1)
        }

        // Fix malformed style attributes
        // This regex looks for style attributes with nested style attributes
        val nestedStyleRegex = Regex("""style="([^"]*?)style=&quot;([^&]*?)&quot;([^"]*)"""")
        match = nestedStyleRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val prefix = match.groupValues[1]
            val nestedStyle = match.groupValues[2]
            val suffix = match.groupValues[3]

            // Combine the styles
            val combinedStyle = "$prefix$nestedStyle$suffix"

            // Create the new style attribute
            val newStyleAttr = "style=\"$combinedStyle\""

            // Replace the original style attribute with the new one
            processedHtml = processedHtml.replaceFirst(fullMatch, newStyleAttr)

            // Find the next match
            match = nestedStyleRegex.find(processedHtml, match.range.first + 1)
        }

        // Fix style attributes with incorrect quotes
        // This regex looks for style attributes with incorrect quotes
        val incorrectQuotesRegex = Regex("""style="style="([^"]*)"""")
        match = incorrectQuotesRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val styleContent = match.groupValues[1]

            // Create the new style attribute
            val newStyleAttr = "style=\"$styleContent\""

            // Replace the original style attribute with the new one
            processedHtml = processedHtml.replaceFirst(fullMatch, newStyleAttr)

            // Find the next match
            match = incorrectQuotesRegex.find(processedHtml, match.range.first + 1)
        }

        // Fix style attributes with incorrect formatting
        // This regex looks for style attributes with incorrect formatting like style="style="text-align: center" padding: 2rem;"
        val incorrectFormattingRegex = Regex("""style="style="([^"]*)" ([^"]*)"""")
        match = incorrectFormattingRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val styleContent1 = match.groupValues[1]
            val styleContent2 = match.groupValues[2]

            // Combine the style contents
            val combinedStyle = "$styleContent1; $styleContent2"

            // Create the new style attribute
            val newStyleAttr = "style=\"$combinedStyle\""

            // Replace the original style attribute with the new one
            processedHtml = processedHtml.replaceFirst(fullMatch, newStyleAttr)

            // Find the next match
            match = incorrectFormattingRegex.find(processedHtml, match.range.first + 1)
        }

        // Fix raw HTML content with __raw_html attribute
        // This regex looks for __raw_html attributes
        val rawHtmlAttrRegex = Regex("""__raw_html="&lt;([^"]*)"""")
        match = rawHtmlAttrRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val rawHtmlContent = match.groupValues[1]

            // Decode the raw HTML content
            val decodedHtml = "<$rawHtmlContent"
                .replace("&quot;", "\"")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&#39;", "'")
                .replace("&#x27;", "'")
                .replace("&#x2F;", "/")
                .replace("&#x60;", "`")
                .replace("&#x3D;", "=")

            // Replace the __raw_html attribute with the decoded HTML content
            processedHtml = processedHtml.replaceFirst(fullMatch, decodedHtml)

            // Find the next match
            match = rawHtmlAttrRegex.find(processedHtml, match.range.first + 1)
        }

        // Fix raw HTML content with __raw_html attribute (alternative format)
        // This regex looks for style attributes with __raw_html attribute
        val rawHtmlStyleRegex = Regex("""style="([^"]*)" __raw_html="([^"]*)"""")
        match = rawHtmlStyleRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val styleContent = match.groupValues[1]
            val rawHtmlContent = match.groupValues[2]

            // Create the style attribute
            val styleAttr = if (styleContent.isNotEmpty()) {
                "style=\"$styleContent\""
            } else {
                ""
            }

            // Decode the raw HTML content
            val decodedHtml = rawHtmlContent
                .replace("&quot;", "\"")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&#39;", "'")
                .replace("&#x27;", "'")
                .replace("&#x2F;", "/")
                .replace("&#x60;", "`")
                .replace("&#x3D;", "=")

            // Create the replacement with the style attribute and decoded HTML content
            val replacement = if (styleAttr.isEmpty()) {
                decodedHtml
            } else {
                "$styleAttr $decodedHtml"
            }

            // Replace the original attributes with the new content
            processedHtml = processedHtml.replaceFirst(fullMatch, replacement)

            // Find the next match
            match = rawHtmlStyleRegex.find(processedHtml, match.range.first + 1)
        }

        // Fix raw HTML content with __raw_html attribute at the end
        // This regex looks for style attributes followed by __raw_html attribute with no value
        val rawHtmlEndRegex = Regex("""style="([^"]*)" __raw_html="&lt"""")
        match = rawHtmlEndRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val styleContent = match.groupValues[1]

            // Create the style attribute
            val styleAttr = if (styleContent.isNotEmpty()) {
                "style=\"$styleContent\""
            } else {
                ""
            }

            // Replace the original attributes with just the style attribute
            // The "&lt" is likely the start of raw HTML content that will be handled by other regex patterns
            processedHtml = processedHtml.replaceFirst(fullMatch, styleAttr)

            // Find the next match
            match = rawHtmlEndRegex.find(processedHtml, match.range.first + 1)
        }

        // Fix multiple style attributes in a single element
        // This regex looks for elements with multiple style attributes
        val multipleStyleRegex = Regex("""style="([^"]*)" style="([^"]*)"""")
        match = multipleStyleRegex.find(processedHtml)

        while (match != null) {
            val fullMatch = match.value
            val style1 = match.groupValues[1]
            val style2 = match.groupValues[2]

            // Combine the styles
            val combinedStyle = "$style1; $style2"

            // Create the new style attribute
            val newStyleAttr = "style=\"$combinedStyle\""

            // Replace the multiple style attributes with the combined one
            processedHtml = processedHtml.replaceFirst(fullMatch, newStyleAttr)

            // Find the next match
            match = multipleStyleRegex.find(processedHtml, match.range.first + 1)
        }

        logger.debug("HtmxAwareRenderer.processHtml() - HTML processing completed, output HTML length: ${processedHtml.length}")
        return processedHtml
    }

    /**
     * Renders a template with the given title and content.
     *
     * @param title The page title
     * @param content The HTML content to include in the template
     * @return The rendered HTML as a string
     */
    fun renderTemplate(title: String, content: String): String {
        logger.debug("HtmxAwareRenderer.renderTemplate() - Starting to render template with title: $title")
        logger.debug("HtmxAwareRenderer.renderTemplate() - Content length: ${content.length}")

        // Create a basic HTML template with HTMX support
        return """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="description" content="A Summon application with Quarkus">
                    <title>$title</title>
                    <!-- Configure HTMX -->
                    <script>
                        window.htmxConfig = {
                            debug: true,
                            processOnLoad: true,
                            historyEnabled: true
                        };
                    </script>
                    <!-- Load HTMX -->
                    <script src="https://unpkg.com/htmx.org@1.9.12"></script>
                    <style>
                        :root {
                          --primary-color: #4695EB;
                          --secondary-color: #FF4081;
                          --background-color: #FFFFFF;
                          --text-color: #333333;
                          --success-color: #4CAF50;
                          --error-color: #F44336;
                          --warning-color: #FF9800;
                          --info-color: #2196F3;
                        }

                        /* HTMX indicator styles */
                        .htmx-indicator {
                          opacity: 0;
                          transition: opacity 200ms ease-in;
                        }
                        .htmx-request .htmx-indicator {
                          opacity: 1;
                          display: inline-block !important;
                        }
                        .htmx-request.htmx-indicator {
                          opacity: 1;
                          display: inline-block !important;
                        }

                        body {
                          font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                          line-height: 1.6;
                          color: var(--text-color);
                          background-color: var(--background-color);
                          margin: 0;
                          padding: 20px;
                        }
                        .container {
                          max-width: 1200px;
                          margin: 0 auto;
                          padding: 0 20px;
                        }
                        .nav {
                          background-color: var(--primary-color);
                          color: white;
                          padding: 1rem;
                          margin-bottom: 1rem;
                          border-radius: 8px;
                        }
                        .nav-list {
                          display: flex;
                          list-style-type: none;
                          margin: 0;
                          padding: 0;
                        }
                        .nav-item {
                          margin-right: 1.5rem;
                        }
                        .nav-link {
                          color: white;
                          text-decoration: none;
                          font-weight: 500;
                        }
                        .nav-link:hover {
                          text-decoration: underline;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <nav class="nav">
                            <ul class="nav-list">
                                <li class="nav-item"><a class="nav-link" href="/" hx-get="/" hx-target="body" hx-swap="innerHTML">Home</a></li>
                                <li class="nav-item"><a class="nav-link" href="/dashboard" hx-get="/dashboard" hx-target="body" hx-swap="innerHTML">Dashboard</a></li>
                                <li class="nav-item"><a class="nav-link" href="/theme" hx-get="/theme" hx-target="body" hx-swap="innerHTML">Theme</a></li>
                                <li class="nav-item"><a class="nav-link" href="/users" hx-get="/users" hx-target="body" hx-swap="innerHTML">Users</a></li>
                                <li class="nav-item"><a class="nav-link" href="/contact" hx-get="/contact" hx-target="body" hx-swap="innerHTML">Contact</a></li>
                            </ul>
                        </nav>
                        <div id="app">
                            $content
                        </div>
                    </div>

                    <!-- Navigation support script -->
                    <script>
                        // Function to process components after navigation
                        function processComponents() {
                            console.log('Processing components after navigation');

                            // Find all components that need to be processed
                            const components = document.querySelectorAll('[data-component]');
                            console.log('Found ' + components.length + ' components to process');

                            // Process each component
                            components.forEach(function(component) {
                                const componentType = component.getAttribute('data-component');
                                const componentId = component.getAttribute('id');
                                console.log('Processing component: ' + componentType + ' (ID: ' + componentId + ')');

                                // Handle different component types
                                switch(componentType) {
                                    case 'hero':
                                        console.log('Processing hero component: ' + componentId);
                                        break;

                                    case 'counter':
                                        console.log('Processing counter component: ' + componentId);
                                        break;

                                    default:
                                        console.log('Unknown component type: ' + componentType);
                                }
                            });
                        }

                        // Process components after navigation
                        document.body.addEventListener('htmx:afterSwap', function(event) {
                            console.log('Navigation occurred, processing components');
                            processComponents();
                        });

                        // Handle browser back/forward navigation
                        window.addEventListener('popstate', function(event) {
                            console.log('Browser navigation occurred, processing components');
                            processComponents();
                        });

                        // Process components on page load
                        document.addEventListener('DOMContentLoaded', function() {
                            console.log('Page loaded, processing components');
                            processComponents();
                        });

                        // Log HTMX events for debugging
                        document.body.addEventListener('htmx:responseError', function(event) {
                            console.error("HTMX Response Error:", event.detail);
                        });

                        document.body.addEventListener('htmx:sendError', function(event) {
                            console.error("HTMX Send Error:", event.detail);
                        });
                    </script>
                </body>
            </html>
        """.trimIndent()
    }

    /**
     * Creates a composable function that renders raw HTML content.
     *
     * @param html The HTML content to render
     * @param modifier Additional modifiers to apply
     */
    @Composable
    fun RawHtml(html: String, modifier: Modifier = Modifier()) {
        logger.debug("HtmxAwareRenderer.RawHtml() - Starting to render raw HTML content, HTML length: ${html.length}")

        try {
            // Get the current platform renderer
            val renderer = LocalPlatformRenderer.current

            // Create a div with the raw HTML content
            logger.debug("HtmxAwareRenderer.RawHtml() - Creating div with raw HTML content")
            val htmlContent = createHTML().div {
                unsafe { raw(html) }
            }

            logger.debug("HtmxAwareRenderer.RawHtml() - HTML content created, length: ${htmlContent.length}")

            // Render a box with the raw HTML content
            // Use htmlAttribute instead of style to ensure proper processing
            logger.debug("HtmxAwareRenderer.RawHtml() - Rendering box with raw HTML content")
            renderer.renderBox(modifier.htmlAttribute("__raw_html", htmlContent)) { }

            logger.debug("HtmxAwareRenderer.RawHtml() - Raw HTML content rendered successfully")
        } catch (e: Exception) {
            logger.error("HtmxAwareRenderer.RawHtml() - Error rendering raw HTML content", e)
            throw e
        }
    }
}
