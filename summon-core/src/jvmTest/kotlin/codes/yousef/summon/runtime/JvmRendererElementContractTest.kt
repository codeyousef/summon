package codes.yousef.summon.runtime

import codes.yousef.summon.components.display.IconType
import codes.yousef.summon.components.feedback.AlertVariant
import codes.yousef.summon.components.feedback.ProgressType
import codes.yousef.summon.components.feedback.ModalSize
import codes.yousef.summon.components.feedback.ModalVariant
import codes.yousef.summon.components.foundation.TrustedSvg
import codes.yousef.summon.components.foundation.TrustedHtml
import codes.yousef.summon.components.navigation.Tab
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JvmRendererElementContractTest {
    @Test
    fun formControlsPreserveValuesBoundsNamesAndDisabledState() {
        val renderer = PlatformRenderer()
        val html = renderer.renderComposableRoot {
            renderer.renderLabel("Named", Modifier(), "named-field")
            renderer.renderLabel("Anonymous", Modifier(), null)
            renderer.renderTextField("value", {}, Modifier().attribute("name", "named-field"), "text")
            renderer.renderTextField("generated", {}, Modifier(), "email")
            renderer.renderSelect(
                selectedValue = "b",
                onSelectedChange = {},
                options = listOf(
                    SelectOption("a", "Alpha"),
                    SelectOption("b", "Beta", disabled = true)
                ),
                modifier = Modifier().attribute("name", "choice")
            )
            renderer.renderSelect(null, {}, listOf(SelectOption(1, "One")), Modifier())
            renderer.renderDatePicker(
                LocalDate(2026, 10, 7), {}, enabled = false,
                min = LocalDate(2026, 1, 1), max = LocalDate(2026, 12, 31), modifier = Modifier()
            )
            renderer.renderDatePicker(null, {}, enabled = true, min = null, max = null, modifier = Modifier())
            renderer.renderTextArea(
                "notes", {}, enabled = false, readOnly = true, rows = 4, maxLength = 80,
                placeholder = "Details", modifier = Modifier().attribute("name", "notes")
            )
            renderer.renderTextArea("free", {}, true, false, null, null, null, Modifier())
            renderer.renderCheckbox(true, {}, enabled = false, modifier = Modifier())
            renderer.renderCheckbox(false, {}, enabled = true, modifier = Modifier())
            renderer.renderSlider(0.5f, {}, 0f..1f, steps = 4, enabled = true, modifier = Modifier())
            renderer.renderSlider(0f, {}, 0f..1f, steps = 0, enabled = false, modifier = Modifier())
            renderer.renderRangeSlider(0.2f..0.8f, {}, 0f..1f, 4, true, Modifier())
            renderer.renderRangeSlider(0f..1f, {}, 0f..1f, 0, false, Modifier())
            renderer.renderTimePicker(LocalTime(9, 30), {}, true, true, Modifier())
            renderer.renderTimePicker(null, {}, false, false, Modifier())
            val trigger = renderer.renderFileUpload({}, "image/*", true, false, "environment", Modifier())
            trigger()
            renderer.renderFileUpload({}, null, false, true, null, Modifier())
            renderer.renderForm({}, Modifier()) {
                renderer.renderText("Submitted form", Modifier())
            }
            renderer.renderForm(null, Modifier()) {
                renderer.renderText("Passive form", Modifier())
            }
        }

        assertContains(html, "for=\"named-field\"")
        assertContains(html, "name=\"named-field\"")
        assertContains(html, "name=\"choice\"")
        assertTrue(
            Regex("""<option(?=[^>]*value="b")(?=[^>]*disabled="disabled")(?=[^>]*selected="selected")[^>]*>Beta</option>""")
                .containsMatchIn(html),
            "Selected disabled option must preserve both states"
        )
        assertContains(html, "min=\"2026-01-01\"")
        assertContains(html, "max=\"2026-12-31\"")
        assertContains(html, "maxlength=\"80\"")
        assertContains(html, "placeholder=\"Details\"")
        assertContains(html, "accept=\"image/*\"")
        assertContains(html, "capture=\"environment\"")
        assertContains(html, "Submitted form")
        assertContains(html, "Passive form")
    }

    @Test
    fun layoutMediaActionsAndModalVariantsRenderAccessibleSemantics() {
        val renderer = PlatformRenderer()
        val html = renderer.renderComposableRoot {
            renderer.startRecomposition()
            renderer.renderText("Text", Modifier().attribute("data-kind", "text"))
            renderer.renderButton({}, Modifier()) { renderer.renderText("Action", Modifier()) }
            renderer.renderButton({}, Modifier().attribute("disabled", "disabled")) {
                renderer.renderText("Disabled action", Modifier())
            }
            renderer.renderRow(Modifier()) { renderer.renderText("Row", Modifier()) }
            renderer.renderColumn(Modifier()) { renderer.renderText("Column", Modifier()) }
            renderer.renderBox(Modifier()) { renderer.renderText("Box", Modifier()) }
            renderer.renderBoxContainer(Modifier()) { renderer.renderText("Container", Modifier()) }
            renderer.renderImage("/image.png", "Preview", Modifier())
            renderer.renderImage("/decorative.png", null, Modifier())
            renderer.renderIcon("check", Modifier(), onClick = {}, svgContent = TrustedSvg.fromAuthorCode("<svg></svg>"), type = IconType.SVG)
            renderer.renderIcon("home", Modifier().attribute("class", "material-icons"), null, null, IconType.FONT)
            renderer.renderIcon("fa-user", Modifier(), null, null, IconType.FONT)
            renderer.renderAlertContainer(AlertVariant.SUCCESS, Modifier()) { renderer.renderText("Success", Modifier()) }
            renderer.renderAlertContainer(null, Modifier()) { renderer.renderText("Neutral", Modifier()) }
            renderer.renderBadge(Modifier()) { renderer.renderText("New", Modifier()) }
            renderer.renderLink("/plain", Modifier())
            renderer.renderLink(Modifier(), "/rich") { renderer.renderText("Rich link", Modifier()) }

            ModalVariant.entries.forEachIndexed { index, variant ->
                renderer.renderModal(
                    onDismiss = {},
                    modifier = if (index == 0) Modifier().attribute("data-summon-modal-label", "Example") else Modifier(),
                    variant = variant,
                    size = ModalSize.entries[index % ModalSize.entries.size],
                    dismissOnBackdropClick = index % 2 == 0,
                    showCloseButton = index % 2 == 0,
                    header = if (index % 2 == 0) ({ renderer.renderText("Header $index", Modifier()) }) else null,
                    footer = if (index % 2 == 0) ({ renderer.renderText("Footer $index", Modifier()) }) else null
                ) { renderer.renderText("Modal $index", Modifier()) }
            }
            renderer.endRecomposition()
        }

        assertContains(html, "type=\"button\"")
        assertContains(html, "data-onclick-action=\"true\"")
        assertContains(html, "alt=\"Preview\"")
        assertContains(html, "<svg></svg>")
        assertContains(html, "material-icons")
        assertContains(html, "role=\"alert\"")
        assertContains(html, "aria-modal=\"true\"")
        assertContains(html, "aria-label=\"Example\"")
        assertContains(html, "Close dialog")
        assertContains(html, "Rich link")
        assertFalse(html.contains("NotImplementedError"))
    }

    @Test
    fun advancedFallbacksRetainContentAndAccessibilityOnSsr() {
        val renderer = PlatformRenderer()
        val html = renderer.renderComposableRoot {
            renderer.renderCanvas(Modifier(), 640, 480) { renderer.renderText("Canvas fallback", Modifier()) }
            renderer.renderCanvas(Modifier(), null, null) { }
            renderer.renderScriptTag("/app.js", async = true, defer = true, type = "module", modifier = Modifier())
            renderer.renderScriptTag("/plain.js", async = false, defer = false, type = null, modifier = Modifier())
            renderer.renderSpan(Modifier()) { renderer.renderText("Span", Modifier()) }
            renderer.renderDivider(Modifier())
            renderer.renderExpansionPanel(Modifier()) { renderer.renderText("Expansion", Modifier()) }
            renderer.renderGrid(Modifier()) { renderer.renderText("Grid", Modifier()) }
            renderer.renderLazyColumn(Modifier()) { renderer.renderText("Column list", Modifier()) }
            renderer.renderLazyColumn(Modifier(), { _, _ -> }) { renderer.renderText("Observed column", Modifier()) }
            renderer.renderLazyColumn(Modifier(), 10f, 2, { _, _ -> }, { _, _ -> }) {
                renderer.renderText("Measured column", Modifier())
            }
            renderer.renderLazyRow(Modifier()) { renderer.renderText("Row list", Modifier()) }
            renderer.renderLazyRow(Modifier(), { _, _ -> }) { renderer.renderText("Observed row", Modifier()) }
            renderer.renderLazyRow(Modifier(), 10f, 2, { _, _ -> }, { _, _ -> }) {
                renderer.renderText("Measured row", Modifier())
            }
            renderer.renderResponsiveLayout(Modifier()) { renderer.renderText("Responsive", Modifier()) }
            renderer.renderSnackbar("Saved", "Undo", {})
            renderer.renderSnackbar("Passive", null, null)
            renderer.renderDropdownMenu(true, {}, Modifier()) { renderer.renderText("Menu", Modifier()) }
            renderer.renderDropdownMenu(false, {}, Modifier()) { renderer.renderText("Hidden menu", Modifier()) }
            renderer.renderTooltip("Explanation", Modifier()) { renderer.renderText("Tip target", Modifier()) }
            renderer.renderModal(true, {}, "Legacy modal", { renderer.renderText("Legacy body", Modifier()) }) {
                renderer.renderText("Legacy action", Modifier())
            }
            renderer.renderModal(false, {}, null, { renderer.renderText("Hidden modal", Modifier()) }, null)
            renderer.renderScreen(Modifier()) { renderer.renderText("Screen", Modifier()) }
            renderer.renderHtml(TrustedHtml.fromAuthorCode("<strong>Trusted</strong>"), Modifier())
            listOf(0, 1, 2).forEach { elevation ->
                renderer.renderSurface(Modifier(), elevation) { renderer.renderText("Surface $elevation", Modifier()) }
            }
            renderer.renderSwipeToDismiss(Any(), { renderer.renderText("Background", Modifier()) }, Modifier()) {
                renderer.renderText("Foreground", Modifier())
            }
            renderer.renderVerticalPager(2, Any(), Modifier()) { renderer.renderText("Vertical $it", Modifier()) }
            renderer.renderVerticalPager(0, Any(), Modifier()) { renderer.renderText("Never", Modifier()) }
            renderer.renderHorizontalPager(2, Any(), Modifier()) { renderer.renderText("Horizontal $it", Modifier()) }
            renderer.renderHorizontalPager(0, Any(), Modifier()) { renderer.renderText("Never", Modifier()) }
            renderer.renderAspectRatioContainer(16f / 9f, Modifier()) { renderer.renderText("Ratio", Modifier()) }
            renderer.renderFilePicker({}, false, true, "image/*", Modifier()) {
                renderer.renderText("Choose", Modifier())
            }
            renderer.renderFilePicker({}, true, false, null, Modifier(), null)

            AlertVariant.entries.forEach { variant ->
                renderer.renderAlert(
                    message = "${variant.name} message",
                    variant = variant,
                    modifier = Modifier(),
                    title = "${variant.name} title",
                    icon = { renderer.renderText("Icon", Modifier()) },
                    actions = { renderer.renderText("Action", Modifier()) }
                )
            }
            renderer.renderAlert("Plain alert", AlertVariant.INFO, Modifier(), null, null, null)
            renderer.renderCard(Modifier(), 2) { renderer.renderText("Raised card", Modifier()) }
            renderer.renderLinearProgressIndicator(0.5f, Modifier(), ProgressType.LINEAR)
            renderer.renderLinearProgressIndicator(null, Modifier(), ProgressType.INDETERMINATE)
            renderer.renderCircularProgressIndicator(0.5f, Modifier(), ProgressType.CIRCULAR)
            renderer.renderCircularProgressIndicator(null, Modifier(), ProgressType.INDETERMINATE)
            renderer.renderProgress(0.25f, ProgressType.LINEAR, Modifier())
            renderer.renderModalBottomSheet({}, Modifier()) { renderer.renderText("Sheet", Modifier()) }
            renderer.renderAlertDialog(
                onDismissRequest = {},
                confirmButton = { renderer.renderText("Confirm", Modifier()) },
                modifier = Modifier(),
                dismissButton = { renderer.renderText("Dismiss", Modifier()) },
                icon = { renderer.renderText("Dialog icon", Modifier()) },
                title = { renderer.renderText("Dialog title", Modifier()) },
                text = { renderer.renderText("Dialog text", Modifier()) }
            )
            renderer.renderAlertDialog({}, { renderer.renderText("Only confirm", Modifier()) }, Modifier(), null, null, null, null)
            renderer.renderRadioButton(true, {}, "Radio label", false, Modifier())
            renderer.renderRadioButton(false, {}, null, true, Modifier())
            renderer.renderCheckbox(true, {}, false, "Check label", Modifier())
            renderer.renderCheckbox(false, {}, true, null, Modifier())
            renderer.renderFormField(Modifier(), "label-id", true, true, "error-id") {
                renderer.renderText("Invalid field", Modifier())
            }
            renderer.renderFormField(Modifier(), null, false, false, null) {
                renderer.renderText("Valid field", Modifier())
            }
            renderer.renderNativeInput("checkbox", Modifier(), "yes", true)
            renderer.renderNativeInput("text", Modifier(), null, null)
            renderer.renderNativeTextarea(Modifier(), "Native text")
            renderer.renderNativeTextarea(Modifier(), null)
            renderer.renderNativeSelect(
                Modifier(),
                listOf(
                    NativeSelectOption("", "Choose", isPlaceholder = true),
                    NativeSelectOption("a", "Alpha", isSelected = true),
                    NativeSelectOption("b", "Beta", isDisabled = true)
                )
            )
            renderer.renderNativeButton("submit", Modifier()) { renderer.renderText("Submit", Modifier()) }
            renderer.renderRadioButton(true, {}, false, Modifier())
            renderer.renderRadioButton(false, {}, true, Modifier())
            renderer.renderSpacer(Modifier())
            renderer.renderAspectRatio(1f, Modifier()) { renderer.renderText("Square", Modifier()) }
            renderer.renderCard(Modifier()) { renderer.renderText("Card", Modifier()) }
            renderer.renderEnhancedLink(
                "/docs", "_blank", "Docs", "Documentation", "docs-help", Modifier(), "Read docs"
            )
            renderer.renderEnhancedLink("/blank", null, null, null, null, Modifier(), " ")
            renderer.renderEnhancedLink("/content", null, null, null, null, Modifier()) {
                renderer.renderText("Content link", Modifier())
            }
            renderer.renderEnhancedLink(
                "/content-details",
                "_self",
                "Content details",
                "Content",
                "content-help",
                Modifier()
            ) {
                renderer.renderText("Detailed content link", Modifier())
            }
            renderer.renderTabLayout(listOf(Tab("One", "one"), Tab("Two", "two")), 1, {}, Modifier())
            renderer.renderTabLayout(Modifier()) { renderer.renderText("Tab body", Modifier()) }
            renderer.renderTabLayout(listOf("A", "B"), "B", {}, Modifier()) {
                renderer.renderText("String tab body", Modifier())
            }
            renderer.renderAnimatedVisibility(true, Modifier())
            renderer.renderAnimatedVisibility(false, Modifier())
            renderer.renderAnimatedVisibility(Modifier()) { renderer.renderText("Visible body", Modifier()) }
            renderer.renderAnimatedContent(Modifier())
            renderer.renderAnimatedContent(Modifier()) { renderer.renderText("Animated body", Modifier()) }
            renderer.renderBlock(Modifier()) { renderer.renderText("Block", Modifier()) }
            renderer.renderInline(Modifier()) { renderer.renderText("Inline", Modifier()) }
            renderer.renderDiv(Modifier()) { renderer.renderText("Div", Modifier()) }
        }

        assertContains(html, "width=\"640\"")
        assertContains(html, "src=\"/app.js\"")
        assertContains(html, "Canvas fallback")
        assertContains(html, "Measured column")
        assertContains(html, "Measured row")
        assertContains(html, "data-onclick-dismiss=\"true\"")
        assertContains(html, "title=\"Explanation\"")
        assertContains(html, "<strong>Trusted</strong>")
        assertContains(html, "Vertical 0")
        assertContains(html, "Horizontal 0")
        assertContains(html, "Progress: 50%")
        assertContains(html, "aria-describedby=\"error-id\"")
        assertContains(html, "aria-required=\"true\"")
        assertContains(html, "hidden=\"hidden\"")
        assertContains(html, "target=\"_blank\"")
        assertContains(html, "data-tab-name=\"B\"")
        assertContains(html, "Animated body")
    }
}
