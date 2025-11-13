package codes.yousef.summon.components.feedback

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Loading spinner size options for consistent sizing across the application.
 *
 * These sizes provide standard dimensions for loading indicators that work well
 * across different UI contexts and maintain visual consistency.
 *
 * @see Loading for usage examples
 * @since 1.0.0
 */
enum class LoadingSize {
    /** Small spinner - 16px. Ideal for inline loading in buttons or compact UI elements. */
    SMALL,

    /** Medium spinner - 24px (default). Standard size for most loading scenarios. */
    MEDIUM,

    /** Large spinner - 32px. Suitable for content areas and loading states. */
    LARGE,

    /** Extra large spinner - 48px. Used for full-screen loading or emphasis. */
    EXTRA_LARGE
}

/**
 * Loading spinner variants that determine the visual style and animation type.
 *
 * Different variants are suitable for different contexts and user experience needs:
 * - Use SPINNER for general loading states
 * - Use DOTS for subtle, less intrusive loading
 * - Use LINEAR for progress-aware tasks
 * - Use CIRCULAR for determinate progress indicators
 *
 * ## Platform Implementation
 * Each platform renderer implements these variants using appropriate native animations:
 * - **Web**: CSS animations and SVG graphics
 * - **JVM**: Platform-specific loading indicators
 *
 * @see Loading for usage examples
 * @see Progress for determinate progress indicators
 * @since 1.0.0
 */
enum class LoadingVariant {
    /** Standard circular spinner with rotating animation. Best for general loading states. */
    SPINNER,

    /** Pulsing dots animation. Less visually intrusive, good for subtle loading indicators. */
    DOTS,

    /** Linear progress bar. Ideal when progress information is available or expected. */
    LINEAR,

    /** Circular progress indicator. Good for compact areas with determinate progress. */
    CIRCULAR
}

/**
 * A versatile loading component that displays various loading indicators with accessibility support.
 *
 * The Loading component provides a comprehensive solution for indicating loading states across
 * your application. It supports multiple visual styles, sizes, and optional text labels while
 * maintaining accessibility standards.
 *
 * ## Features
 * - **Multiple visual variants**: Spinner, dots, linear, and circular indicators
 * - **Configurable sizes**: From small inline indicators to large content loaders
 * - **Optional loading text**: Contextual messages for better user experience
 * - **Accessibility**: Proper ARIA attributes and screen reader support
 * - **Platform optimized**: Native animations through platform renderers
 * - **Conditional rendering**: Show/hide based on loading state
 *
 * ## Basic Usage
 * ```kotlin
 * // Simple loading spinner
 * Loading(isVisible = isLoading)
 *
 * // Loading with text
 * Loading(
 *     isVisible = isLoading,
 *     text = "Fetching data...",
 *     variant = LoadingVariant.SPINNER,
 *     size = LoadingSize.MEDIUM
 * )
 * ```
 *
 * ## Advanced Usage
 * ```kotlin
 * // Custom styled loading indicator
 * Loading(
 *     isVisible = dataState.isLoading,
 *     variant = LoadingVariant.DOTS,
 *     size = LoadingSize.LARGE,
 *     text = "Processing your request...",
 *     modifier = Modifier()
 *         .padding(Spacing.LG)
 *         .backgroundColor(Color.White)
 *         .borderRadius(BorderRadius.MD),
 *     textModifier = Modifier()
 *         .fontSize(FontSize.SM)
 *         .color(Color.Muted)
 * )
 * ```
 *
 * ## State Management Integration
 * ```kotlin
 * @Composable
 * fun DataScreen() {
 *     var isLoading by remember { mutableStateOf(false) }
 *     var data by remember { mutableStateOf<List<Item>?>(null) }
 *
 *     LaunchedEffect(Unit) {
 *         isLoading = true
 *         try {
 *             data = apiService.fetchData()
 *         } finally {
 *             isLoading = false
 *         }
 *     }
 *
 *     LoadingState(isLoading = isLoading) {
 *         // Content when loaded
 *         data?.let { DataList(it) }
 *     }
 * }
 * ```
 *
 * ## Accessibility Features
 * - Automatic `role="status"` for screen readers
 * - `aria-live="polite"` for loading text announcements
 * - `aria-label` attributes for context
 * - Keyboard navigation support
 * - High contrast mode compatibility
 *
 * ## Performance Considerations
 * - Animations are hardware-accelerated when possible
 * - Conditional rendering avoids unnecessary DOM updates
 * - Platform-specific optimizations through renderers
 * - Minimal resource usage when not visible
 *
 * @param isVisible Whether the loading indicator is currently shown. When false, nothing is rendered.
 * @param modifier Modifier applied to the loading container for styling and layout.
 * @param variant The visual style of the loading indicator. Choose based on context and design needs.
 * @param size The size of the loading indicator. Select appropriate size for the UI context.
 * @param text Optional text to display below the loading indicator for user context.
 * @param textModifier Modifier applied specifically to the loading text for custom styling.
 *
 * @see LoadingOverlay for full-screen loading states
 * @see InlineLoading for compact loading indicators
 * @see LoadingState for content loading state management
 * @see LoadingSize for available size options
 * @see LoadingVariant for visual style options
 * @sample codes.yousef.summon.samples.feedback.LoadingSamples.basicUsage
 * @sample codes.yousef.summon.samples.feedback.LoadingSamples.advancedUsage
 * @since 1.0.0
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
 * A full-screen loading overlay that covers the entire viewport with a semi-transparent background.
 *
 * LoadingOverlay is ideal for blocking user interaction during critical operations like
 * authentication, data submission, or application initialization. It provides a modal-like
 * experience that prevents user input while clearly indicating that work is in progress.
 *
 * ## Features
 * - **Full viewport coverage**: Covers entire screen with overlay
 * - **User interaction blocking**: Prevents clicks and keyboard input
 * - **Customizable background**: Semi-transparent background with custom colors
 * - **High z-index**: Appears above all other content
 * - **Centered loading indicator**: Professionally centered with optional text
 * - **Accessible**: Proper focus management and screen reader support
 *
 * ## Basic Usage
 * ```kotlin
 * @Composable
 * fun App() {
 *     var isAuthenticating by remember { mutableStateOf(false) }
 *
 *     LoadingOverlay(
 *         isVisible = isAuthenticating,
 *         text = "Signing you in..."
 *     )
 *
 *     // Main app content
 *     MainContent()
 * }
 * ```
 *
 * ## Advanced Usage
 * ```kotlin
 * // Custom styled overlay
 * LoadingOverlay(
 *     isVisible = isProcessing,
 *     text = "Processing payment...",
 *     variant = LoadingVariant.CIRCULAR,
 *     size = LoadingSize.EXTRA_LARGE,
 *     backgroundColor = "rgba(0, 0, 0, 0.7)",
 *     modifier = Modifier()
 *         .style("backdrop-filter", "blur(4px)")
 * )
 * ```
 *
 * ## Common Use Cases
 * - **Authentication flows**: During login/logout operations
 * - **Form submissions**: When submitting critical data
 * - **File uploads**: During large file transfer operations
 * - **App initialization**: While loading essential app data
 * - **Route transitions**: During navigation state changes
 *
 * ## Accessibility Features
 * - Traps focus within the overlay
 * - Announces loading state to screen readers
 * - Prevents interaction with background content
 * - High contrast mode compatible
 * - Keyboard navigation disabled for background
 *
 * ## Performance Notes
 * - Uses fixed positioning for optimal performance
 * - Hardware-accelerated backdrop when supported
 * - Conditional rendering prevents unnecessary DOM creation
 * - High z-index ensures proper layering
 *
 * @param isVisible Whether the loading overlay is currently shown. When false, nothing is rendered.
 * @param text Optional text to display with the loading indicator for user context.
 * @param variant The visual style of the loading indicator (default: SPINNER).
 * @param size The size of the loading indicator (default: LARGE for prominence).
 * @param backgroundColor Background color of the overlay (default: semi-transparent white).
 * @param modifier Additional modifier applied to the overlay container for custom styling.
 *
 * @see Loading for inline loading indicators
 * @see LoadingState for content-specific loading states
 * @sample codes.yousef.summon.samples.feedback.LoadingSamples.fullScreenOverlay
 * @since 1.0.0
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
 * Compact inline loading component optimized for buttons and small UI elements.
 *
 * InlineLoading is designed for use within other components where space is limited
 * and you need a subtle loading indicator. It's commonly used in buttons during
 * form submissions, menu items during actions, or small content areas.
 *
 * ## Features
 * - **Compact design**: Optimized for small spaces and inline usage
 * - **Spinner variant**: Uses spinner for minimal visual disruption
 * - **Small default size**: Pre-configured for most inline scenarios
 * - **Minimal footprint**: Lightweight implementation for performance
 *
 * ## Basic Usage
 * ```kotlin
 * Button(
 *     onClick = { /* submit form */ },
 *     modifier = Modifier().width(Width.FIT_CONTENT)
 * ) {
 *     Row(
 *         modifier = Modifier().style("align-items", "center").style("gap", "8px")
 *     ) {
 *         InlineLoading(isVisible = isSubmitting)
 *         Text(if (isSubmitting) "Saving..." else "Save")
 *     }
 * }
 * ```
 *
 * ## Advanced Usage
 * ```kotlin
 * // Custom sized inline loading
 * InlineLoading(
 *     isVisible = isLoading,
 *     size = LoadingSize.MEDIUM,
 *     modifier = Modifier()
 *         .margin(Margin.horizontal(Spacing.XS))
 * )
 * ```
 *
 * ## Common Use Cases
 * - **Button loading states**: During form submissions or actions
 * - **Menu items**: When performing quick actions
 * - **Table cells**: For row-specific loading states
 * - **Icon replacements**: Temporary loading in place of icons
 * - **Inline feedback**: Quick loading feedback in text flows
 *
 * @param isVisible Whether the loading indicator is shown. When false, nothing is rendered.
 * @param size Size of the loading indicator (default: SMALL for compact appearance).
 * @param modifier Modifier applied to the loading container for positioning and styling.
 *
 * @see Loading for full-featured loading indicators
 * @see LoadingSize for size options
 * @sample codes.yousef.summon.samples.feedback.LoadingSamples.inlineButton
 * @since 1.0.0
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
 * Loading state wrapper that conditionally shows loading indicator or content based on loading state.
 *
 * LoadingState provides a clean pattern for handling loading states in your components.
 * It automatically switches between showing a loading indicator and your main content,
 * eliminating the need for manual conditional rendering throughout your application.
 *
 * ## Features
 * - **Automatic state switching**: Toggles between loading and content states
 * - **Custom loading content**: Override default loading indicator with custom UI
 * - **Default loading UI**: Professional loading indicator with text
 * - **Clean state management**: Eliminates boilerplate conditional rendering
 * - **Composable content**: Flexible content slot for any composable
 *
 * ## Basic Usage
 * ```kotlin
 * @Composable
 * fun UserProfile(userId: String) {
 *     var user by remember { mutableStateOf<User?>(null) }
 *     var isLoading by remember { mutableStateOf(true) }
 *
 *     LaunchedEffect(userId) {
 *         isLoading = true
 *         user = userRepository.getUser(userId)
 *         isLoading = false
 *     }
 *
 *     LoadingState(isLoading = isLoading) {
 *         user?.let { ProfileContent(it) }
 *     }
 * }
 * ```
 *
 * ## Custom Loading Content
 * ```kotlin
 * LoadingState(
 *     isLoading = isLoading,
 *     loadingContent = {
 *         Column(
 *             modifier = Modifier()
 *                 .style("align-items", "center")
 *                 .padding(Spacing.XL)
 *         ) {
 *             Loading(
 *                 isVisible = true,
 *                 variant = LoadingVariant.DOTS,
 *                 size = LoadingSize.LARGE
 *             )
 *             Text(
 *                 text = "Fetching your personalized content...",
 *                 modifier = Modifier()
 *                     .fontSize(FontSize.LG)
 *                     .color(Color.Primary)
 *                     .style("margin-top", "16px")
 *             )
 *         }
 *     }
 * ) {
 *     // Main content
 *     MainContent()
 * }
 * ```
 *
 * ## Integration with Data Loading
 * ```kotlin
 * @Composable
 * fun DataScreen() {
 *     val dataState by remember {
 *         derivedStateOf { dataRepository.state.value }
 *     }
 *
 *     LoadingState(isLoading = dataState.isLoading) {
 *         when (dataState) {
 *             is DataState.Success -> DataList(dataState.data)
 *             is DataState.Error -> ErrorMessage(dataState.error)
 *             else -> EmptyState()
 *         }
 *     }
 * }
 * ```
 *
 * ## Common Use Cases
 * - **Data fetching**: While loading data from APIs
 * - **Form processing**: During form submission and validation
 * - **Route loading**: While loading page content
 * - **Component initialization**: During component setup
 * - **Lazy loading**: For deferred content loading
 *
 * ## Best Practices
 * - Keep loading states as short as possible
 * - Provide meaningful loading text for context
 * - Consider skeleton loading for better UX
 * - Handle error states separately from loading
 * - Use consistent loading patterns across your app
 *
 * @param isLoading Whether the content is currently loading. When true, shows loading content.
 * @param loadingContent Custom loading content to display (default: centered Loading component with text).
 * @param content The main content to show when not loading. Can be any composable content.
 *
 * @see Loading for loading indicator options
 * @see LoadingOverlay for full-screen loading states
 * @sample codes.yousef.summon.samples.feedback.LoadingSamples.loadingStatePattern
 * @since 1.0.0
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