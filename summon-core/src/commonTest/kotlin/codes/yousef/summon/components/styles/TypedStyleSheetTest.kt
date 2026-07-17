package codes.yousef.summon.components.styles

import codes.yousef.summon.modifier.Display
import codes.yousef.summon.modifier.MediaQuery
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.color
import codes.yousef.summon.modifier.display
import codes.yousef.summon.modifier.opacity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TypedStyleSheetTest {

    @Test
    fun rendersStructuredSelectorsMediaQueriesAndKeyframes() {
        val navigation = StyleSelector.className("site-navigation")
        val currentLink = StyleSelector.element(StyleElement.Anchor)
            .attribute(StyleAttribute.AriaCurrent, "page")

        val css = TypedStyleSheetScope().apply {
            rule(
                navigation
                    .attribute(StyleAttribute.Open)
                    .child(StyleSelector.element(StyleElement.Summary))
                    .pseudoClass(StylePseudoClass.FocusVisible),
                Modifier.color("white")
            )
            rule(
                StyleSelector.element(StyleElement.Input)
                    .attribute(StyleAttribute.Type, "search")
                    .pseudoElement(StylePseudoElement.Placeholder),
                Modifier.opacity(0.7f)
            )
            rule(currentLink.or(StyleSelector.className("active-link")), Modifier.display(Display.Block))
            media(MediaQuery.MaxWidth(640)) {
                rule(navigation, Modifier.display(Display.None))
            }
            keyframes(AnimationName.named("fade-in")) {
                from(Modifier.opacity(0f))
                to(Modifier.opacity(1f))
            }
        }.render()

        assertEquals(
            """.site-navigation[open] > summary:focus-visible { color: white; }
input[type="search"]::placeholder { opacity: 0.7; }
a[aria-current="page"], .active-link { display: block; }
@media (max-width: 640px) { .site-navigation { display: none; } }
@keyframes fade-in { 0% { opacity: 0; } 100% { opacity: 1; } }""",
            css
        )
    }

    @Test
    fun rejectsRawSelectorAndMediaSyntax() {
        assertFailsWith<IllegalArgumentException> {
            StyleSelector.className("button:hover")
        }
        assertFailsWith<IllegalArgumentException> {
            TypedStyleSheetScope().media(MediaQuery.Custom("(width > 1px)")) {
                rule(StyleSelector.Root, Modifier.color("red"))
            }
        }
    }
}
