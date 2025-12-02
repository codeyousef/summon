package codes.yousef.summon.components.input

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Renders a code editor with syntax highlighting.
 *
 * @param value The current code content
 * @param onValueChange Callback when code changes
 * @param language The language for syntax highlighting (default: "kotlin")
 * @param readOnly Whether the editor is read-only
 * @param modifier The modifier to apply to this component
 */
@Composable
fun CodeEditor(
    value: String,
    onValueChange: (String) -> Unit,
    language: String = "kotlin",
    readOnly: Boolean = false,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderCodeEditor(value, onValueChange, language, readOnly, modifier)
}
