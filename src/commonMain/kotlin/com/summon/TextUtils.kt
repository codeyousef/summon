package com.summon

/**
 * Utility functions for the Text component.
 */

/**
 * Creates a heading text at level 1 (h1 equivalent).
 * @param text The text content
 * @param modifier Additional styling modifiers
 */
fun h1(text: String, modifier: Modifier = Modifier()): Text = Text(
    text = text,
    modifier = modifier
        .fontSize("2rem")
        .fontWeight("bold")
        .margin("0.67em 0"),
    lineHeight = "1.2",
    fontFamily = "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
)

/**
 * Creates a heading text at level 2 (h2 equivalent).
 * @param text The text content
 * @param modifier Additional styling modifiers
 */
fun h2(text: String, modifier: Modifier = Modifier()): Text = Text(
    text = text,
    modifier = modifier
        .fontSize("1.5rem")
        .fontWeight("bold")
        .margin("0.83em 0"),
    lineHeight = "1.2",
    fontFamily = "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
)

/**
 * Creates a heading text at level 3 (h3 equivalent).
 * @param text The text content
 * @param modifier Additional styling modifiers
 */
fun h3(text: String, modifier: Modifier = Modifier()): Text = Text(
    text = text,
    modifier = modifier
        .fontSize("1.17rem")
        .fontWeight("bold")
        .margin("1em 0"),
    lineHeight = "1.2",
    fontFamily = "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
)

/**
 * Creates a paragraph text (p equivalent).
 * @param text The text content
 * @param modifier Additional styling modifiers
 */
fun paragraph(text: String, modifier: Modifier = Modifier()): Text = Text(
    text = text,
    modifier = modifier.margin("1em 0"),
    lineHeight = "1.5",
    fontFamily = "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
)

/**
 * Creates a caption text (smaller, secondary text).
 * @param text The text content
 * @param modifier Additional styling modifiers
 */
fun caption(text: String, modifier: Modifier = Modifier()): Text = Text(
    text = text,
    modifier = modifier
        .fontSize("0.85rem")
        .color("#666"),
    lineHeight = "1.4",
    fontFamily = "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
)

/**
 * Creates a label text (for form fields or similar).
 * @param text The text content
 * @param modifier Additional styling modifiers
 */
fun label(text: String, modifier: Modifier = Modifier()): Text = Text(
    text = text,
    modifier = modifier
        .fontSize("0.9rem")
        .fontWeight("500")
        .margin("0 0 0.5em 0"),
    lineHeight = "1.4",
    fontFamily = "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
)

/**
 * Creates a monospace text (for code or similar).
 * @param text The text content
 * @param modifier Additional styling modifiers
 */
fun monospace(text: String, modifier: Modifier = Modifier()): Text = Text(
    text = text,
    modifier = modifier
        .background("#f5f5f5")
        .padding("0.2em 0.4em")
        .borderRadius("3px")
        .fontSize("0.9em"),
    fontFamily = "SFMono-Regular, Consolas, 'Liberation Mono', Menlo, monospace",
    lineHeight = "1.4"
)

/**
 * Creates an error text (for error messages).
 * @param text The text content
 * @param modifier Additional styling modifiers
 */
fun errorText(text: String, modifier: Modifier = Modifier()): Text = Text(
    text = text,
    modifier = modifier.color("#d32f2f"),
    fontFamily = "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
    lineHeight = "1.4"
)

/**
 * Creates a success text (for success messages).
 * @param text The text content
 * @param modifier Additional styling modifiers
 */
fun successText(text: String, modifier: Modifier = Modifier()): Text = Text(
    text = text,
    modifier = modifier.color("#2e7d32"),
    fontFamily = "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
    lineHeight = "1.4"
) 