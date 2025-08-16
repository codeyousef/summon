package code.yousef.summon.components.style

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Injects global CSS styles into the document head.
 * This component allows you to define styles that apply globally to the entire document.
 *
 * @param css The CSS content to inject
 * @param modifier The modifier to apply to this component
 */
@Composable
fun GlobalStyle(
    css: String,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderGlobalStyle(css)
}

/**
 * Injects CSS keyframe animations into the document head.
 *
 * @param name The name of the keyframe animation
 * @param keyframes The keyframe definition (without @keyframes wrapper)
 * @param modifier The modifier to apply to this component
 */
@Composable
fun GlobalKeyframes(
    name: String,
    keyframes: String,
    modifier: Modifier = Modifier()
) {
    val css = "@keyframes $name { $keyframes }"
    GlobalStyle(css, modifier)
}

/**
 * Injects CSS custom properties (variables) into the document root.
 *
 * @param variables A map of CSS variable names to their values
 * @param modifier The modifier to apply to this component
 */
@Composable
fun CssVariables(
    variables: Map<String, String>,
    modifier: Modifier = Modifier()
) {
    val css = ":root { ${variables.map { (key, value) -> "$key: $value;" }.joinToString(" ")} }"
    GlobalStyle(css, modifier)
}

/**
 * Injects CSS media queries into the document head.
 *
 * @param query The media query (e.g., "@media (max-width: 768px)")
 * @param css The CSS content to apply within the media query
 * @param modifier The modifier to apply to this component
 */
@Composable
fun MediaQuery(
    query: String,
    css: String,
    modifier: Modifier = Modifier()
) {
    val wrappedCss = "$query { $css }"
    GlobalStyle(wrappedCss, modifier)
}