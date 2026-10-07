package codes.yousef.summon.components.feedback

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class ModalContractTest {
    private fun render(block: () -> Unit) = PlatformRenderer().renderComposableRoot(block)

    @Test
    fun closedModalEmitsNothingAndOpenModalCarriesPoliciesAndSlots() {
        val closed = render { Modal(false, {}) { Text("private") } }
        assertFalse(closed.contains("private"))

        val open = render {
            Modal(
                isOpen = true,
                onDismiss = {},
                variant = ModalVariant.FULLSCREEN,
                size = ModalSize.EXTRA_LARGE,
                dismissOnBackdropClick = false,
                showCloseButton = false,
                dismissOnEscape = false,
                ariaLabel = "Preferences",
                header = { Text("header") },
                footer = { Text("footer") }
            ) { Text("content") }
        }
        assertContains(open, "role=\"dialog\"")
        assertContains(open, "aria-modal=\"true\"")
        assertContains(open, "data-summon-dismiss-on-escape=\"false\"")
        assertContains(open, "data-summon-modal-label=\"Preferences\"")
        assertContains(open, "header")
        assertContains(open, "footer")
        assertContains(open, "content")
        assertFalse(open.contains("aria-label=\"Close modal\""))
    }

    @Test
    fun confirmationAndAlertCoverNamedAndUnnamedHeaders() {
        val html = render {
            ConfirmationModal(true, "Confirm title", "Confirm message", onConfirm = {}, onCancel = {})
            ConfirmationModal(true, "", "Untitled confirmation", onConfirm = {}, onCancel = {})
            AlertModal(true, "Alert title", "Alert message", onDismiss = {})
            AlertModal(true, "", "Untitled alert", onDismiss = {})
        }
        assertContains(html, "Confirm title")
        assertContains(html, "Confirm message")
        assertContains(html, "Untitled confirmation")
        assertContains(html, "Alert title")
        assertContains(html, "Alert message")
        assertContains(html, "Untitled alert")
        assertContains(html, ">Confirm<")
        assertContains(html, ">Cancel<")
        assertContains(html, ">OK<")
    }
}
