package codes.yousef.summon.components.feedback

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.input.ButtonVariant
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Modal variants that determine the modal's visual style and behavior.
 */
enum class ModalVariant {
    /** Standard modal with default styling */
    DEFAULT,

    /** Alert modal for important messages */
    ALERT,

    /** Confirmation modal for user decisions */
    CONFIRMATION,

    /** Full-screen modal that covers the entire viewport */
    FULLSCREEN
}

/**
 * Modal size options for controlling the modal's dimensions.
 */
enum class ModalSize {
    /** Small modal - typically for confirmations */
    SMALL,

    /** Medium modal - default size for most use cases */
    MEDIUM,

    /** Large modal - for complex forms or detailed content */
    LARGE,

    /** Extra large modal - for extensive content */
    EXTRA_LARGE
}

/**
 * A modal dialog component that displays content in an overlay above the main content.
 *
 * Features:
 * - Overlay background with optional dismiss on backdrop click
 * - Keyboard navigation (ESC to close)
 * - Focus management and accessibility
 * - Configurable size and variants
 * - Custom header, content, and footer slots
 *
 * @param isOpen Whether the modal is currently visible
 * @param onDismiss Callback invoked when the modal should be closed (ESC key, backdrop click, etc.)
 * @param modifier Modifier applied to the modal container
 * @param variant The visual variant of the modal
 * @param size The size of the modal dialog
 * @param dismissOnBackdropClick Whether clicking the backdrop should dismiss the modal
 * @param showCloseButton Whether to show a close button in the header
 * @param header Optional composable content for the modal header
 * @param footer Optional composable content for the modal footer (typically action buttons)
 * @param content The main content of the modal
 */
@Composable
fun Modal(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier(),
    variant: ModalVariant = ModalVariant.DEFAULT,
    size: ModalSize = ModalSize.MEDIUM,
    dismissOnBackdropClick: Boolean = true,
    showCloseButton: Boolean = true,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (!isOpen) return

    val renderer = LocalPlatformRenderer.current

    renderer.renderModal(
        onDismiss = onDismiss,
        modifier = modifier,
        variant = variant,
        size = size,
        dismissOnBackdropClick = dismissOnBackdropClick,
        showCloseButton = showCloseButton,
        header = header,
        footer = footer,
        content = content
    )
}

/**
 * Convenience function for creating a simple confirmation modal.
 *
 * @param isOpen Whether the modal is currently visible
 * @param title The title of the confirmation dialog
 * @param message The message to display to the user
 * @param confirmText Text for the confirm button (default: "Confirm")
 * @param cancelText Text for the cancel button (default: "Cancel")
 * @param onConfirm Callback invoked when the user confirms
 * @param onCancel Callback invoked when the user cancels
 * @param modifier Modifier applied to the modal
 */
@Composable
fun ConfirmationModal(
    isOpen: Boolean,
    title: String,
    message: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier()
) {
    Modal(
        isOpen = isOpen,
        onDismiss = onCancel,
        modifier = modifier,
        variant = ModalVariant.CONFIRMATION,
        size = ModalSize.SMALL,
        header = {
            Column(Modifier().padding("16px")) {
                if (title.isNotEmpty()) {
                    Text(
                        text = title,
                        modifier = Modifier()
                            .fontSize("18px")
                            .fontWeight("600")
                            .marginBottom("8px")
                    )
                }
            }
        },
        footer = {
            Row(
                modifier = Modifier()
                    .padding("16px")
                    .style("justify-content", "flex-end")
                    .style("gap", "12px")
            ) {
                Button(
                    onClick = onCancel,
                    label = cancelText,
                    variant = ButtonVariant.SECONDARY
                )
                Button(
                    onClick = onConfirm,
                    label = confirmText,
                    variant = ButtonVariant.PRIMARY
                )
            }
        }
    ) {
        Column(Modifier().padding("16px")) {
            Text(
                text = message,
                modifier = Modifier().style("line-height", "1.5")
            )
        }
    }
}

/**
 * Convenience function for creating an alert modal.
 *
 * @param isOpen Whether the modal is currently visible
 * @param title The title of the alert
 * @param message The message to display to the user
 * @param buttonText Text for the dismiss button (default: "OK")
 * @param onDismiss Callback invoked when the user dismisses the alert
 * @param modifier Modifier applied to the modal
 */
@Composable
fun AlertModal(
    isOpen: Boolean,
    title: String,
    message: String,
    buttonText: String = "OK",
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier()
) {
    Modal(
        isOpen = isOpen,
        onDismiss = onDismiss,
        modifier = modifier,
        variant = ModalVariant.ALERT,
        size = ModalSize.SMALL,
        header = {
            Column(Modifier().padding("16px")) {
                if (title.isNotEmpty()) {
                    Text(
                        text = title,
                        modifier = Modifier()
                            .fontSize("18px")
                            .fontWeight("600")
                            .marginBottom("8px")
                    )
                }
            }
        },
        footer = {
            Row(
                modifier = Modifier()
                    .padding("16px")
                    .style("justify-content", "center")
            ) {
                Button(
                    onClick = onDismiss,
                    label = buttonText,
                    variant = ButtonVariant.PRIMARY
                )
            }
        }
    ) {
        Column(Modifier().padding("16px")) {
            Text(
                text = message,
                modifier = Modifier().style("line-height", "1.5")
            )
        }
    }
}