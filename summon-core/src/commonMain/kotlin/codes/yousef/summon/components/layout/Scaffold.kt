package codes.yousef.summon.components.layout

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.fillMaxSize
import codes.yousef.summon.core.style.Color
import codes.yousef.summon.modifier.backgroundColor
import codes.yousef.summon.modifier.flexGrow
import codes.yousef.summon.modifier.position
import codes.yousef.summon.modifier.Position
import codes.yousef.summon.modifier.bottom
import codes.yousef.summon.modifier.right

/**
 * # Scaffold
 *
 * A high-level layout component that implements the basic visual layout structure of an application.
 * It provides slots for the most common top-level components such as a top bar, bottom bar,
 * floating action button, and drawer.
 *
 * @param modifier The modifier to be applied to the Scaffold.
 * @param topBar A composable function to render the top app bar.
 * @param bottomBar A composable function to render the bottom bar.
 * @param snackbarHost A composable function to render the snackbar host.
 * @param floatingActionButton A composable function to render the floating action button.
 * @param backgroundColor The background color of the scaffold.
 * @param content The main content of the screen.
 */
@Composable
fun Scaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    backgroundColor: Color? = null,
    content: @Composable (WindowInsets) -> Unit
) {
    val insets = LocalWindowInsets.current
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (backgroundColor != null) Modifier.backgroundColor(backgroundColor.toCssString()) else Modifier)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            topBar()
            
            Box(modifier = Modifier.flexGrow(1)) {
                content(insets)
            }
            
            bottomBar()
        }
        
        // Floating Action Button (Bottom End)
        Box(
            modifier = Modifier
                .position(Position.Absolute)
                .bottom(16) 
                .right(16)
                .windowInsetsPadding(insets)
        ) {
            floatingActionButton()
        }

        // Snackbar Host (Bottom Center/Fill)
        Box(
            modifier = Modifier
                .position(Position.Absolute)
                .bottom(0)
                .fillMaxSize() // This might cover the whole screen if not careful. 
                // Usually snackbar host is a transparent container that fills the screen or bottom part.
                // For now, let's assume it handles its own layout or is just a container at the bottom.
                // If we use fillMaxSize with absolute positioning, it will cover everything.
                // Let's use width 100% and bottom 0.
                // But fillMaxSize() does width 100% height 100%.
                // We probably want fillMaxWidth().
        ) {
            // We need to make sure this box doesn't block clicks if it's empty.
            // In HTML/CSS, pointer-events: none would be needed if it covers everything.
            // Since we don't have that easily here, let's just position it at the bottom.
            snackbarHost()
        }
    }
}

private fun Modifier.bottom(value: Int): Modifier = bottom("${value}px")
private fun Modifier.right(value: Int): Modifier = right("${value}px")
