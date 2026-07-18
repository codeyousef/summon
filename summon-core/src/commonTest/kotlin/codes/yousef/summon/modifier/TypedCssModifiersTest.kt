package codes.yousef.summon.modifier

import codes.yousef.summon.core.style.Color
import codes.yousef.summon.i18n.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TypedCssModifiersTest {

    @Test
    fun textDeclarationsMapToCssProperties() {
        val modifier = Modifier()
            .textUnderlineOffset(4)
            .wordBreak(WordBreak.BreakAll)
            .overflowWrap(OverflowWrap.Anywhere)
            .textOverflow(TextOverflow.Ellipsis)

        assertEquals("4px", modifier.styles["text-underline-offset"])
        assertEquals("break-all", modifier.styles["word-break"])
        assertEquals("anywhere", modifier.styles["overflow-wrap"])
        assertEquals("ellipsis", modifier.styles["text-overflow"])
    }

    @Test
    fun structuredTextShadowsComposeInOrder() {
        val modifier = Modifier().textShadow(
            TextShadow.pixels(0, 2, 4, Color.rgba(0, 0, 0, 128)),
            TextShadow("0", "0", "1rem", "currentColor"),
        )

        assertEquals(
            "0px 2px 4px rgba(0, 0, 0, 0.5019608), 0 0 1rem currentColor",
            modifier.styles["text-shadow"],
        )
    }

    @Test
    fun lineClampIncludesWebkitFallbackByDefault() {
        val modifier = Modifier().lineClamp(3)

        assertEquals("3", modifier.styles["line-clamp"])
        assertEquals("3", modifier.styles["-webkit-line-clamp"])
        assertFailsWith<IllegalArgumentException> { Modifier().lineClamp(0) }
    }

    @Test
    fun lineClampFallbackCanBeDisabled() {
        val modifier = Modifier().lineClamp(2, includeWebkitFallback = false)

        assertEquals("2", modifier.styles["line-clamp"])
        assertEquals(null, modifier.styles["-webkit-line-clamp"])
    }

    @Test
    fun boxAndLogicalInsetDeclarationsAreTyped() {
        val modifier = Modifier()
            .boxSizing(BoxSizing.BorderBox)
            .inset(1, 2, 3, 4)
            .insetInlineStart(8)
            .insetInlineEnd("calc(100% - 2rem)")

        assertEquals("border-box", modifier.styles["box-sizing"])
        assertEquals("1px 2px 3px 4px", modifier.styles["inset"])
        assertEquals("8px", modifier.styles["inset-inline-start"])
        assertEquals("calc(100% - 2rem)", modifier.styles["inset-inline-end"])
    }

    @Test
    fun colorSchemeAndListMarkersUseTypedValues() {
        val modifier = Modifier()
            .colorScheme(ColorScheme.OnlyDark)
            .listStyle(ListStyleType.None)
            .listStyleType(ListStyleType.Square)

        assertEquals("only dark", modifier.styles["color-scheme"])
        assertEquals("none", modifier.styles["list-style"])
        assertEquals("square", modifier.styles["list-style-type"])
    }

    @Test
    fun scrollbarDeclarationsSupportTypedValues() {
        val modifier = Modifier()
            .scrollbarWidth(ScrollbarWidth.Thin)
            .scrollbarColor(Color.hex("#123456"), Color.TRANSPARENT)

        assertEquals("thin", modifier.styles["scrollbar-width"])
        assertEquals(
            "rgba(18, 52, 86, 1.0) rgba(0, 0, 0, 0.0)",
            modifier.styles["scrollbar-color"],
        )
    }

    @Test
    fun outlineDeclarationsComposeWidthStyleColorAndOffset() {
        val modifier = Modifier()
            .outline(3, OutlineStyle.Solid, Color.hex("#8fe3ff"))
            .outlineOffset(2)

        assertEquals("3px solid rgba(143, 227, 255, 1.0)", modifier.styles["outline"])
        assertEquals("2px", modifier.styles["outline-offset"])
    }

    @Test
    fun outlineStyleKeywordUsesTypedValue() {
        val modifier = Modifier().outline(OutlineStyle.None)

        assertEquals("none", modifier.styles["outline"])
    }

    @Test
    fun gridTrackHelpersBuildResponsiveTemplates() {
        val responsive = gridAutoFit(
            gridMinMax(gridTrack("280px"), gridFraction()),
        )
        val modifier = Modifier()
            .gridTemplateColumns(responsive)
            .gridTemplateRows(gridTrack("auto"), gridFraction(2), gridTrack("auto"))

        assertEquals(
            "repeat(auto-fit, minmax(280px, 1fr))",
            modifier.styles["grid-template-columns"],
        )
        assertEquals("auto 2fr auto", modifier.styles["grid-template-rows"])
    }

    @Test
    fun gridTrackHelpersRejectInvalidDefinitions() {
        assertFailsWith<IllegalArgumentException> { gridTrack(" ") }
        assertFailsWith<IllegalArgumentException> { gridFraction(-1) }
        assertFailsWith<IllegalArgumentException> { gridFraction(Double.POSITIVE_INFINITY) }
        assertFailsWith<IllegalArgumentException> { gridRepeat(0, gridFraction()) }
        assertFailsWith<IllegalArgumentException> { gridAutoFill() }
        assertFailsWith<IllegalArgumentException> { Modifier().gridTemplateColumns() }
        assertFailsWith<IllegalArgumentException> { Modifier().gridTemplateRows() }
    }

    @Test
    fun backgroundAttachmentUsesTypedValues() {
        val modifier = Modifier().backgroundAttachment(BackgroundAttachment.Fixed)

        assertEquals("fixed", modifier.styles["background-attachment"])
    }

    @Test
    fun sideBorderKeepsOtherSidesUntouched() {
        val modifier = Modifier().border(BorderSide.Right, 4, BorderStyle.Solid, "#123456")

        assertEquals("4px solid #123456", modifier.styles["border-right"])
        assertEquals(null, modifier.styles["border-left"])
    }

    @Test
    fun directionUsesFrameworkLayoutDirection() {
        assertEquals("rtl", Modifier().direction(LayoutDirection.RTL).styles["direction"])
        assertEquals("ltr", Modifier().direction(LayoutDirection.LTR).styles["direction"])
    }
}
