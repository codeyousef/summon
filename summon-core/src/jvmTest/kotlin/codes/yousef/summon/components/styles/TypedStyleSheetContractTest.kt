package codes.yousef.summon.components.styles

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith

class TypedStyleSheetContractTest {
    @Test
    fun selectorsRulesMediaAndKeyframesRenderWithoutRawCss() {
        val button = StyleSelector.element(StyleElement.Button)
        val active = button
            .withClass("primary")
            .attribute(StyleAttribute.data("state"), "open\"quoted\nline", AttributeMatch.Equals, true)
            .pseudoClass(StylePseudoClass.Hover)
            .pseudoElement(StylePseudoElement.After)
        val related = StyleSelector.element(StyleElement.Nav)
            .child(button)
            .adjacentSibling(StyleSelector.className("next"))
            .generalSibling(StyleSelector.id("rest"))
            .descendant(StyleSelector.element(StyleElement.Span))
            .not(StyleSelector.className("hidden"))
        val group = active.or(related, StyleSelector.Universal, StyleSelector.Root)
        val animation = AnimationName.named("pulse")

        val html = PlatformRenderer().renderComposableRoot {
            TypedStyleSheet {
                rule(group, Modifier().style("fontSize", "16.0").style("opacity", "1"), StyleRulePriority.Important)
                rule(button, Modifier())
                media(MediaQuery.MinWidth(640)) {
                    rule(button.attribute(StyleAttribute.Disabled), Modifier().style("display", "none"))
                }
                media(MediaQuery.MaxWidth(10)) { }
                keyframes(animation) {
                    from(Modifier().style("opacity", "0"))
                    frame(50, Modifier())
                    frame(50, Modifier().style("opacity", "0.5"))
                    to(Modifier().style("opacity", "1"))
                }
                keyframes(AnimationName.named("empty")) { }
            }
        }
        assertContains(html, ".primary[data-state=\"open\\\"quoted\\a line\" i]:hover::after")
        assertContains(html, "nav > button + .next ~ #rest span:not(.hidden)")
        assertContains(html, "font-size: 16 !important")
        assertContains(html, "@media (min-width: 640px)")
        assertContains(html, "@keyframes pulse")
        assertContains(html, "50%")
    }

    @Test
    fun typedSelectorAndAnimationValidationRejectsUnsafeOrImpossibleValues() {
        assertFailsWith<IllegalArgumentException> { StyleAttribute.data("bad name") }
        assertFailsWith<IllegalArgumentException> { StyleAttribute.aria("bad]") }
        assertFailsWith<IllegalArgumentException> { StyleSelector.className(".raw") }
        assertFailsWith<IllegalArgumentException> { StyleSelector.id("#raw") }
        assertFailsWith<IllegalArgumentException> { StyleSelector.all(listOf(StyleSelector.Root)) }
        assertFailsWith<IllegalArgumentException> { StylePseudoClass.NthChild.index(0) }
        assertFailsWith<IllegalArgumentException> { StylePseudoClass.NthChild.sequence(0) }
        StylePseudoClass.NthChild.sequence(2, 1)
        StylePseudoClass.NthChild.sequence(2, -1)
        StylePseudoClass.NthChild.sequence(2)
        assertFailsWith<IllegalArgumentException> { AnimationName.named("bad name") }
        assertFailsWith<IllegalArgumentException> { AnimationIterationCount.Times(0) }
        assertFailsWith<IllegalArgumentException> {
            TypedStyleSheetScope().media(MediaQuery.Custom("screen")) { }
        }
        assertFailsWith<IllegalArgumentException> { TypedKeyframesScope().frame(-1, Modifier()) }
        assertFailsWith<IllegalArgumentException> { TypedKeyframesScope().frame(101, Modifier()) }
    }

    @Test
    fun typedAnimationModifierMapsEveryParameter() {
        val modifier = Modifier().animation(
            AnimationName.named("spin"),
            AnimationDuration.Fast,
            AnimationEasing.EaseInOut,
            AnimationDuration.Instant,
            AnimationIterationCount.Times(3),
            AnimationDirection.Alternate,
            AnimationFillMode.Both
        )
        assertContains(modifier.styles.getValue("animation"), "spin")
        assertContains(modifier.styles.getValue("animation"), "3")
        assertContains(modifier.styles.getValue("animation"), "alternate")
        assertContains(modifier.styles.getValue("animation"), "both")
    }
}
