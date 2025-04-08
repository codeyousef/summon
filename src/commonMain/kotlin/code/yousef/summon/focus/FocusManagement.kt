@Composable
fun Focusable(
    modifier: Modifier = Modifier,
    isFocused: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    // ... existing code ...
} 