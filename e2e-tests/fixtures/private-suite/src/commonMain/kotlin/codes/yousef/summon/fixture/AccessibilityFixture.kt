package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.feedback.Modal
import codes.yousef.summon.components.feedback.Toast
import codes.yousef.summon.components.feedback.ToastData
import codes.yousef.summon.components.feedback.ToastVariant
import codes.yousef.summon.components.html.Bdi
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.input.DatePicker
import codes.yousef.summon.components.input.TextArea
import codes.yousef.summon.components.input.TextField
import codes.yousef.summon.components.input.TextFieldType
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.focus.Focusable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf
import kotlinx.datetime.LocalDate

class AccessibilityFixture {
    @Composable
    fun Content() {
        val focusEvents = remember { mutableStateOf<List<String>>(emptyList()) }
        val showFocusTarget = remember { mutableStateOf(true) }
        val inputValue = remember { mutableStateOf("قبل") }
        val areaValue = remember { mutableStateOf("سطر أول") }
        val passwordValue = remember { mutableStateOf("  secret  ") }
        val selectedDate = remember { mutableStateOf<LocalDate?>(LocalDate(2024, 3, 30)) }
        val modalOpen = remember { mutableStateOf(false) }
        val nestedModalOpen = remember { mutableStateOf(false) }
        val showModalInvoker = remember { mutableStateOf(true) }
        val modalDismissals = remember { mutableStateOf(0) }
        val statusToastVisible = remember { mutableStateOf(false) }

        Column(
            Modifier()
                .attribute("dir", "rtl")
                .attribute("lang", "ar")
                .attribute("data-testid", "accessibility-fixture")
        ) {
            Text("Accessibility and IME fixture")
            Text(focusEvents.value.joinToString(","), Modifier().attribute("data-testid", "focus-events"))
            if (showFocusTarget.value) {
                Focusable(
                    modifier = Modifier()
                        .attribute("data-testid", "focus-target")
                        .attribute("aria-label", "Focusable region"),
                    onFocusChanged = { focused ->
                        focusEvents.value = focusEvents.value + if (focused) "focus" else "blur"
                    }
                ) {
                    Text("Focusable region")
                }
            }
            Button(onClick = { showFocusTarget.value = false }, label = "Remove focus target")

            TextField(
                value = inputValue.value,
                onValueChange = { inputValue.value = it },
                label = "Arabic message",
                modifier = Modifier().attribute("data-testid", "ime-input")
            )
            Text(inputValue.value, Modifier().attribute("data-testid", "ime-value"))
            TextArea(
                value = areaValue.value,
                onValueChange = { areaValue.value = it },
                modifier = Modifier()
                    .attribute("data-testid", "ime-textarea")
                    .attribute("aria-label", "Arabic notes")
            )
            Text(areaValue.value, Modifier().attribute("data-testid", "textarea-value"))
            TextField(
                value = passwordValue.value,
                onValueChange = { passwordValue.value = it },
                label = "Private passphrase",
                type = TextFieldType.Password,
                modifier = Modifier().attribute("data-testid", "password-input")
            )
            Text(passwordValue.value.length.toString(), Modifier().attribute("data-testid", "password-length"))
            TextField(
                value = "Disabled",
                onValueChange = {},
                label = "Disabled field",
                isEnabled = false,
                modifier = Modifier().attribute("data-testid", "disabled-input")
            )
            DatePicker(
                value = selectedDate.value,
                onValueChange = { selectedDate.value = it },
                label = "تاريخ الاستحقاق",
                minDate = LocalDate(2024, 1, 1),
                maxDate = LocalDate(2024, 12, 31),
                modifier = Modifier().attribute("data-testid", "date-input")
            )
            Text(
                selectedDate.value?.toString() ?: "none",
                Modifier().attribute("data-testid", "date-value")
            )
            TextField(
                value = "Read only",
                onValueChange = {},
                label = "Read only field",
                isReadOnly = true,
                isError = true,
                modifier = Modifier()
                    .attribute("data-testid", "readonly-input")
                    .attribute("aria-describedby", "field-error")
            )
            Text("Invalid value", Modifier().attribute("id", "field-error"))
            Text(modalDismissals.value.toString(), Modifier().attribute("data-testid", "modal-dismissals"))
            if (showModalInvoker.value) {
                Button(
                    onClick = { modalOpen.value = true },
                    label = "Open accessible dialog",
                    modifier = Modifier().attribute("data-testid", "open-modal")
                )
            }
            Modal(
                isOpen = modalOpen.value,
                onDismiss = {
                    nestedModalOpen.value = false
                    modalOpen.value = false
                    modalDismissals.value += 1
                },
                ariaLabel = "Account settings",
                dismissOnBackdropClick = false,
                header = { Text("Account settings") }
            ) {
                Button(
                    onClick = { showModalInvoker.value = false },
                    label = "Remove invoker",
                    modifier = Modifier().attribute("data-testid", "remove-modal-invoker")
                )
                Button(
                    onClick = { nestedModalOpen.value = true },
                    label = "Open nested dialog",
                    modifier = Modifier().attribute("data-testid", "open-nested-modal")
                )
                Button(
                    onClick = {},
                    label = "Last dialog action",
                    modifier = Modifier().attribute("data-testid", "last-modal-action")
                )
                Modal(
                    isOpen = nestedModalOpen.value,
                    onDismiss = { nestedModalOpen.value = false },
                    ariaLabel = "Nested confirmation",
                    header = { Text("Nested confirmation") }
                ) {
                    Button(
                        onClick = { nestedModalOpen.value = false },
                        label = "Close nested dialog",
                        modifier = Modifier().attribute("data-testid", "close-nested-modal")
                    )
                }
            }
            Button(
                onClick = { statusToastVisible.value = true },
                label = "Show status",
                modifier = Modifier().attribute("data-testid", "show-status")
            )
            if (statusToastVisible.value) {
                Toast(
                    toast = ToastData(
                        id = "bounded-status",
                        message = "Settings saved",
                        variant = ToastVariant.SUCCESS,
                        duration = 0
                    ),
                    onDismiss = { statusToastVisible.value = false },
                    modifier = Modifier().attribute("data-testid", "status-toast")
                )
            }
            Bdi(Modifier().attribute("data-testid", "isolated-address").attribute("dir", "ltr")) {
                Text("alice@example.test")
            }
        }
    }
}
