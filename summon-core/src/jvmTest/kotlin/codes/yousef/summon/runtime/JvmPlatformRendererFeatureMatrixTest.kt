package codes.yousef.summon.runtime

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.animation.PulseAnimation
import codes.yousef.summon.animation.PulseEffect
import codes.yousef.summon.animation.PulsatingButton
import codes.yousef.summon.animation.StaggeredAnimation
import codes.yousef.summon.accessibility.AccessibilityUtils
import codes.yousef.summon.accessibility.AccessibleElement
import codes.yousef.summon.components.layout.ResponsiveLayout
import codes.yousef.summon.components.layout.ScreenSize
import codes.yousef.summon.components.feedback.Alert
import codes.yousef.summon.components.feedback.AlertVariant
import codes.yousef.summon.components.feedback.Snackbar
import codes.yousef.summon.components.feedback.SnackbarHorizontalPosition
import codes.yousef.summon.components.feedback.SnackbarVariant
import codes.yousef.summon.components.feedback.SnackbarVerticalPosition
import codes.yousef.summon.components.input.FormField
import codes.yousef.summon.components.feedback.ToastAction
import codes.yousef.summon.components.feedback.ToastData
import codes.yousef.summon.components.feedback.ToastVariant
import codes.yousef.summon.desktop.menu.KeyboardShortcut
import codes.yousef.summon.desktop.menu.Menu
import codes.yousef.summon.desktop.menu.MenuItem
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ConditionalStyleState
import codes.yousef.summon.modifier.StateStyleDefinition
import codes.yousef.summon.modifier.withConditionalStyle
import kotlin.time.Duration
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JvmPlatformRendererFeatureMatrixTest {
    @AfterTest
    fun clearCallbacks() = CallbackRegistry.clear()

    @Test
    fun toastsChartsSplitPanesAndMenusRenderEverySemanticVariant() {
        val renderer = PlatformRenderer()
        val html = renderer.renderComposableRoot {
            ToastVariant.entries.forEachIndexed { index, variant ->
                renderer.renderToast(
                    ToastData(
                        id = "toast-$index",
                        message = "message-$variant",
                        variant = variant,
                        dismissible = index != 0,
                        action = if (index == 0) null else ToastAction("Act-$variant") {}
                    ),
                    onDismiss = {},
                    modifier = Modifier()
                )
            }
            renderer.renderChart("line", "[1]", null, Modifier())
            renderer.renderChart("bar", "[2]", "{}", Modifier())
            renderer.renderSplitPane("vertical", Modifier(), { Text("top") }, { Text("bottom") })
            renderer.renderSplitPane("horizontal", Modifier(), { Text("left") }, { Text("right") })
            renderer.renderMenuBar(
                listOf(
                    Menu("Disabled", emptyList(), disabled = true),
                    Menu(
                        "File",
                        listOf(
                            MenuItem.separator(),
                            MenuItem(
                                "Parent",
                                disabled = true,
                                submenu = listOf(MenuItem("Child", disabled = true))
                            ),
                            MenuItem(
                                "Enabled parent",
                                submenu = listOf(MenuItem("Enabled child"))
                            ),
                            MenuItem(
                                "Checked",
                                checked = true,
                                icon = "star",
                                shortcut = KeyboardShortcut("S", ctrl = true, shift = true, alt = true, meta = true)
                            ),
                            MenuItem("Unchecked", checked = false),
                            MenuItem("Plain")
                        )
                    )
                ),
                Modifier()
            )
        }

        ToastVariant.entries.forEach { assertContains(html, "message-$it") }
        assertContains(html, "role=\"alert\"")
        assertContains(html, "aria-live=\"assertive\"")
        assertContains(html, "aria-live=\"polite\"")
        assertContains(html, "data-chart-options=\"{}\"")
        assertContains(html, "data-orientation=\"vertical\"")
        assertContains(html, "data-orientation=\"horizontal\"")
        assertContains(html, "Ctrl+Shift+Alt+Meta+S")
        assertContains(html, "aria-checked=\"true\"")
        assertContains(html, "aria-checked=\"false\"")
        assertContains(html, "summon-submenu")
        assertTrue(html.countOccurrences("disabled=\"disabled\"") >= 3)
        assertFalse(html.substringAfter("message-INFO").substringBefore("message-SUCCESS").contains("Dismiss notification"))
        assertContains(html, "data-onclick-id=")
    }

    private fun String.countOccurrences(value: String): Int = windowed(value.length).count { it == value }
    @Test
    fun alertsSnackbarsAndFormFieldsRenderEveryOptionalSemanticPath() {
        val renderer = PlatformRenderer()
        val html = renderer.renderComposableRoot {
            AlertVariant.entries.forEachIndexed { index, variant ->
                Alert(
                    message = "alert-$variant",
                    variant = variant,
                    title = if (index % 2 == 0) "title-$variant" else null,
                    icon = if (index == 0) ({ Text("custom-icon") }) else null,
                    actions = if (index == 1) ({ Text("action") }) else null,
                    onDismiss = if (index == 2) ({}) else null,
                )
            }
            SnackbarVariant.entries.forEachIndexed { index, variant ->
                Snackbar(
                    message = "snackbar-$variant",
                    variant = variant,
                    action = if (index % 2 == 0) "Undo" else null,
                    onAction = if (index % 2 == 0) ({}) else null,
                    onDismiss = if (index % 2 == 1) ({}) else null,
                    icon = if (index == 0) ({ Text("custom-snackbar-icon") }) else null,
                    duration = Duration.INFINITE,
                    horizontalPosition = SnackbarHorizontalPosition.entries[index % SnackbarHorizontalPosition.entries.size],
                    verticalPosition = SnackbarVerticalPosition.entries[index % SnackbarVerticalPosition.entries.size],
                )
            }
            FormField(
                label = { Text("Required") },
                helperText = { Text("helper") },
                errorText = { Text("error") },
                isError = true,
                isRequired = true,
            ) { Text("field") }
            FormField(helperText = { Text("plain helper") }) { Text("plain field") }
            FormField(isError = true) { Text("error without message") }
        }

        AlertVariant.entries.forEach { assertContains(html, "alert-$it") }
        SnackbarVariant.entries.forEach { assertContains(html, "snackbar-$it") }
        assertContains(html, "custom-icon")
        assertContains(html, "Required")
        assertContains(html, "error")
        assertContains(html, "plain helper")
    }

    @Test
    fun responsiveAndAccessibleContainersRenderClientServerAndFallbackPaths() {
        val renderer = PlatformRenderer()
        val html = renderer.renderComposableRoot {
            AccessibleElement(
                content = { Text("predefined") },
                role = AccessibilityUtils.NodeRole.BUTTON,
                label = "Primary action",
                relations = mapOf("describedby" to "help"),
            )
            AccessibleElement(content = { Text("custom") }, customRole = "feed")
            AccessibleElement(content = { Text("unadorned") })
            ResponsiveLayout(
                content = ScreenSize.entries.associateWith { size -> ({ Text("client-$size") }) },
                defaultContent = { Text("client-default") },
            )
            ResponsiveLayout(
                content = mapOf(ScreenSize.SMALL to { Text("server-small") }),
                defaultContent = { Text("server-fallback") },
                detectScreenSizeClient = false,
                serverSideScreenSize = ScreenSize.SMALL,
            )
            ResponsiveLayout(
                content = emptyMap(),
                defaultContent = { Text("missing-fallback") },
                detectScreenSizeClient = false,
                serverSideScreenSize = ScreenSize.XLARGE,
            )
        }
        assertContains(html, "role=\"button\"")
        assertContains(html, "aria-label=\"Primary action\"")
        assertContains(html, "aria-describedby=\"help\"")
        assertContains(html, "role=\"feed\"")
        ScreenSize.entries.forEach { assertContains(html, "client-$it") }
        assertContains(html, "client-default")
        assertContains(html, "server-small")
        assertContains(html, "missing-fallback")
        assertFalse(html.contains("server-fallback"))
    }

    @Test
    fun customAnimationComponentsEmitEveryPulseIdentityAndTimingAttribute() {
        val html = PlatformRenderer().renderComposableRoot {
            PulseEffect.entries.forEach { effect ->
                PulsatingButton("pulse-$effect", {}, pulseEffect = effect)
            }
            PulseAnimation { Text("pulse-content") }
            StaggeredAnimation { Text("stagger-content") }
        }
        assertContains(html, "pulse-scale-animation")
        assertContains(html, "pulse-opacity-animation")
        assertContains(html, "pulse-color-animation")
        assertContains(html, "pulse-content")
        assertContains(html, "data-stagger-delay=\"100\"")
    }

    @Test
    fun conditionalStyleStateMatrixRendersEverySupportedSelector() {
        val modifier = ConditionalStyleState.entries.fold(Modifier()) { current, state ->
            val value = if (state == ConditionalStyleState.HOVER) "red !important" else "red"
            current.withConditionalStyle(
                StateStyleDefinition(
                    state = state,
                    styles = mapOf("backgroundColor" to value),
                    argument = if (state == ConditionalStyleState.NTH_CHILD) "2n+1" else null,
                )
            )
        }
        val html = PlatformRenderer().renderComposableRoot {
            Text("states", modifier)
        }
        listOf(
            ":hover", ":focus", ":focus-visible", ":active", ":focus-within", ":first-child",
            ":last-child", ":nth-child(2n+1)", ":only-child", ":visited", ":disabled", ":checked",
        ).forEach { selector -> assertContains(html, selector) }
        assertContains(html, "background-color: red !important;")
    }

}
