package code.yousef.summon.components.feedback

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Loading spinner size options
 */
enum class LoadingSize {
    /** Small spinner - 16px */
    SMALL,
    /** Medium spinner - 24px (default) */
    MEDIUM,
    /** Large spinner - 32px */
    LARGE,
    /** Extra large spinner - 48px */
    EXTRA_LARGE
}

/**
 * Loading spinner variants that determine the visual style
 */
enum class LoadingVariant {
    /** Standard circular spinner */
    SPINNER,
    /** Pulsing dots animation */
    DOTS,
    /** Linear progress bar */
    LINEAR,
    /** Circular progress indicator */
    CIRCULAR
}

/**
 * A loading component that displays various loading indicators.
 * 
 * Features:
 * - Multiple visual variants (spinner, dots, linear, circular)
 * - Configurable sizes
 * - Optional loading text
 * - Accessible with proper ARIA attributes
 * - CSS animations handled by platform renderers
 *
 * @param isVisible Whether the loading indicator is currently shown
 * @param modifier Modifier applied to the loading container
 * @param variant The visual style of the loading indicator
 * @param size The size of the loading indicator
 * @param text Optional text to display below the loading indicator
 * @param textModifier Modifier applied to the loading text
 */
@Composable
fun Loading(
    isVisible: Boolean = true,
    modifier: Modifier = Modifier(),
    variant: LoadingVariant = LoadingVariant.SPINNER,
    size: LoadingSize = LoadingSize.MEDIUM,
    text: String? = null,
    textModifier: Modifier = Modifier()
) {
    if (!isVisible) return
    
    val renderer = LocalPlatformRenderer.current
    
    renderer.renderLoading(
        modifier = modifier,
        variant = variant,
        size = size,
        text = text,
        textModifier = textModifier
    )
}

/**
 * A full-screen loading overlay that covers the entire viewport.
 *
 * @param isVisible Whether the loading overlay is currently shown
 * @param text Optional text to display with the loading indicator
 * @param variant The visual style of the loading indicator
 * @param size The size of the loading indicator
 * @param backgroundColor Background color of the overlay (default: semi-transparent)
 * @param modifier Additional modifier applied to the overlay container
 */
@Composable
fun LoadingOverlay(
    isVisible: Boolean,
    text: String? = null,
    variant: LoadingVariant = LoadingVariant.SPINNER,
    size: LoadingSize = LoadingSize.LARGE,
    backgroundColor: String = "rgba(255, 255, 255, 0.8)",
    modifier: Modifier = Modifier()
) {
    if (!isVisible) return
    
    Box(
        modifier = modifier
            .style("position", "fixed")
            .style("top", "0")
            .style("left", "0")
            .width("100vw")
            .height("100vh")
            .backgroundColor(backgroundColor)
            .style("display", "flex")
            .style("align-items", "center")
            .style("justify-content", "center")
            .zIndex(9999)
    ) {
        Column(
            modifier = Modifier()
                .style("align-items", "center")
                .style("gap", "16px")
        ) {
            Loading(
                isVisible = true,
                variant = variant,
                size = size
            )
            
            text?.let {
                Text(
                    text = it,
                    modifier = Modifier()
                        .fontSize("16px")
                        .color("#666")
                        .style("text-align", "center")
                )
            }
        }
    }
}

/**
 * Inline loading component for buttons and small UI elements.
 *
 * @param isVisible Whether the loading indicator is shown
 * @param size Size of the loading indicator (default: SMALL)
 * @param modifier Modifier applied to the loading container
 */
@Composable
fun InlineLoading(
    isVisible: Boolean = true,
    size: LoadingSize = LoadingSize.SMALL,
    modifier: Modifier = Modifier()
) {
    Loading(
        isVisible = isVisible,
        modifier = modifier,
        variant = LoadingVariant.SPINNER,
        size = size
    )
}

/**
 * Loading state wrapper that shows loading indicator while content is loading.
 *
 * @param isLoading Whether the content is currently loading
 * @param loadingContent Custom loading content (default: Loading component)
 * @param content The main content to show when not loading
 */
@Composable
fun LoadingState(
    isLoading: Boolean,
    loadingContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (isLoading) {
        loadingContent?.invoke() ?: Loading(
            isVisible = true,
            text = "Loading...",
            modifier = Modifier()
                .style("display", "flex")
                .style("flex-direction", "column")
                .style("align-items", "center")
                .style("justify-content", "center")
                .padding("32px")
        )
    } else {
        content()
    }
}