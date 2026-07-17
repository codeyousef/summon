package codes.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConditionalStyleModifiersTest {

    @Test
    fun stateModifiersRetainStructuredDefinitionsAndCompatibilityAttributes() {
        val modifier = Modifier()
            .hover(mapOf("backgroundImage" to "url(https://example.test/image.png)"))
            .focus(mapOf("outline" to "2px solid blue"))
            .active(mapOf("transform" to "scale(0.98)"))
            .focusWithin(mapOf("borderColor" to "purple"))
            .firstChild(mapOf("marginTop" to "0"))
            .lastChild(mapOf("marginBottom" to "0"))
            .nthChild("2n+1", mapOf("backgroundColor" to "silver"))
            .onlyChild(mapOf("display" to "block"))
            .visited(mapOf("color" to "violet"))
            .disabledStyles(mapOf("opacity" to "0.5"))
            .checkedStyles(mapOf("accentColor" to "green"))

        val definitions = modifier.conditionalStyles.map { it as StateStyleDefinition }

        assertEquals(
            listOf(
                ConditionalStyleState.HOVER,
                ConditionalStyleState.FOCUS,
                ConditionalStyleState.ACTIVE,
                ConditionalStyleState.FOCUS_WITHIN,
                ConditionalStyleState.FIRST_CHILD,
                ConditionalStyleState.LAST_CHILD,
                ConditionalStyleState.NTH_CHILD,
                ConditionalStyleState.ONLY_CHILD,
                ConditionalStyleState.VISITED,
                ConditionalStyleState.DISABLED,
                ConditionalStyleState.CHECKED,
            ),
            definitions.map { it.state },
        )
        assertEquals("2n+1", definitions[6].argument)
        assertEquals(
            "url(https://example.test/image.png)",
            definitions.first().styles["backgroundImage"],
        )
        assertTrue(modifier.attributes.containsKey("data-hover-styles"))
        assertTrue(modifier.attributes.containsKey("data-focus-styles"))
        assertTrue(modifier.attributes.containsKey("data-nth-child-styles"))
        assertTrue(modifier.attributes.containsKey("data-checked-styles"))
    }

    @Test
    fun mediaAndScopedModifiersCreateStructuredRules() {
        val query = MediaQuery.And(MediaQuery.MinWidth(768), MediaQuery.CanHover)
        val modifier = Modifier()
            .mediaQuery(query) {
                fontSize("18px").style("backgroundImage", "url(https://example.test/wide.png)")
            }
            .descendant(".card") { color("navy") }
            .child("span") { fontWeight(700) }

        val media = modifier.conditionalStyles[0] as MediaStyleDefinition
        val descendant = modifier.conditionalStyles[1] as ScopedStyleDefinition
        val child = modifier.conditionalStyles[2] as ScopedStyleDefinition

        assertEquals(query, media.query)
        assertEquals("url(https://example.test/wide.png)", media.styles["backgroundImage"])
        assertEquals(SelectorType.DESCENDANT, descendant.selectorType)
        assertEquals(".card", descendant.selector)
        assertEquals(SelectorType.CHILD, child.selectorType)
        assertEquals("span", child.selector)
        assertTrue(modifier.attributes.containsKey("data-media-queries"))
        assertTrue(modifier.attributes.containsKey("data-scoped-styles"))
    }

    @Test
    fun repeatedHoverCompatibilityEncodingPreservesColonsInValues() {
        val modifier = Modifier()
            .hover(mapOf("backgroundImage" to "url(https://example.test/image.png)"))
            .hover(mapOf("color" to "red"))

        assertEquals(2, modifier.conditionalStyles.size)
        assertEquals(
            "backgroundImage:url(https://example.test/image.png);color:red",
            modifier.attributes["data-hover-styles"],
        )
    }

    @Test
    fun thenCloneAndAttributeRemovalPreserveConditionalRuleOrder() {
        val first = Modifier()
            .attribute("data-test", "present")
            .hover(mapOf("color" to "red"))
        val second = Modifier().mediaQuery(MediaQuery.MinWidth(960)) { color("blue") }
        val combined = (first then second).clone().removeAttribute("data-test")

        assertEquals(2, combined.conditionalStyles.size)
        assertEquals(ConditionalStyleState.HOVER, (combined.conditionalStyles[0] as StateStyleDefinition).state)
        assertEquals(MediaQuery.MinWidth(960), (combined.conditionalStyles[1] as MediaStyleDefinition).query)
        assertEquals(null, combined.attributes["data-test"])
    }
}
