package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Markdown
import code.yousef.summon.components.foundation.BasicText
import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.theme.Spacing

/**
 * Minimal Markdown editor composed of a text area and optional live preview.
 *
 * @param value Current markdown content
 * @param onValueChange Callback invoked when user edits the content
 * @param modifier Modifier applied to the wrapping column
 * @param placeholder Optional placeholder shown inside the text area
 * @param showPreview Whether to show the rendered Markdown preview
 * @param rows Optional number of visible rows for the text area
 * @param previewHeading Optional heading rendered above the preview when present
 * @param textAreaModifier Modifier applied to the text area
 * @param previewModifier Modifier applied to the preview block
 */
@Composable
fun MarkdownEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    placeholder: String? = null,
    showPreview: Boolean = true,
    rows: Int? = 10,
    previewHeading: String? = "Preview",
    textAreaModifier: Modifier = Modifier(),
    previewModifier: Modifier = Modifier()
) {
    val mergedTextAreaModifier = textAreaModifier.then(modifier)

    TextArea(
        value = value,
        onValueChange = onValueChange,
        modifier = mergedTextAreaModifier,
        rows = rows,
        placeholder = placeholder
    )

    if (showPreview && value.isNotBlank()) {
        Spacer(modifier = Modifier().height(Spacing.sm))
        previewHeading?.let {
            BasicText(
                text = it,
                modifier = Modifier().padding(bottom = "4px", top = "0px", left = "0px", right = "0px")
            )
        }
        Markdown(
            markdownContent = value,
            modifier = previewModifier
        )
    }
}
