# MarkdownEditor

The `MarkdownEditor` composable provides a lightweight Markdown authoring experience by pairing Summon's multiline
`TextArea` with an optional live preview rendered via `Markdown`.

```kotlin
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
)
```

## Parameters

- `value` – Current Markdown content supplied by the caller.
- `onValueChange` – Callback invoked whenever the user edits the field.
- `modifier` – Modifier applied to the text area (and overall layout).
- `placeholder` – Optional placeholder rendered inside the editor when it is empty.
- `showPreview` – Toggles the Markdown preview below the editor (defaults to `true`).
- `rows` – Number of visible rows for the underlying `TextArea` (defaults to 10).
- `previewHeading` – Optional heading text shown above the preview (`"Preview"` by default, `null` to hide).
- `textAreaModifier` – Additional modifier applied specifically to the `TextArea`.
- `previewModifier` – Modifier applied to the preview `Markdown` composable.

## Example

```kotlin
@Composable
fun PostEditor(body: String, onBodyChange: (String) -> Unit) {
    MarkdownEditor(
        value = body,
        onValueChange = onBodyChange,
        placeholder = "Write release notes in Markdown...",
        textAreaModifier = Modifier
            .padding(Spacing.sm)
            .height("240px"),
        previewModifier = Modifier
            .padding(Spacing.sm)
            .backgroundColor("#f7f7f7")
            .padding("12px")
    )
}
```

## Preview Behavior

- The preview is rendered only when `showPreview` is `true` *and* the editor content is non-blank.
- Preview output is sanitized through the existing `Markdown` component, so untrusted input remains safe.
- Pass `previewHeading = null` if you want a preview without any heading text.

## Integration Notes

- `MarkdownEditor` does not manage focus or validation; wrap it with `FormField` or other helpers if you need labels,
  helper text, or error messaging.
- Because the editor relies on `TextArea`, it automatically inherits Summon's accessibility and modifier rules.
- For multi-column layouts, compose the editor and preview manually instead of relying on the built-in preview (
  `showPreview = false`) if you need full control over placement.
