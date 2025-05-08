package code.yousef.summon.examples.js

import code.yousef.summon.annotation.App
import code.yousef.summon.core.RenderUtils
import code.yousef.summon.core.RenderUtilsJs
import code.yousef.summon.examples.js.components.TaskList
import code.yousef.summon.examples.js.core.initializeCustomRenderUtils
import code.yousef.summon.examples.js.state.Task
import code.yousef.summon.examples.js.theme.ThemeManager
import code.yousef.summon.i18n.I18nConfig
import code.yousef.summon.i18n.JsI18nImplementation
import code.yousef.summon.i18n.LayoutDirection
import code.yousef.summon.js.console
import kotlinx.browser.document
import kotlin.test.*

class AppTest {

    @BeforeTest
    fun setup() {
        // Initialize JavaScript-specific implementations
        RenderUtilsJs.initialize()

        // Initialize custom renderComposable implementation
        initializeCustomRenderUtils()

        // Configure supported languages
        I18nConfig.configure {
            language("en", "English")
            language("ar", "العربية", LayoutDirection.RTL)
            language("fr", "Français")

            // Set default language
            setDefault("en")
        }

        // Initialize i18n system
        JsI18nImplementation.init()

        // Initialize theme system with default theme
        ThemeManager.initialize(isDarkMode = false)

        // Create a root element for testing
        val rootElement = document.createElement("div")
        rootElement.id = "root"
        document.body?.appendChild(rootElement)
    }

    @AfterTest
    fun teardown() {
        // Clean up the DOM after each test
        val rootElement = document.getElementById("root")
        rootElement?.let {
            document.body?.removeChild(it)
        }
    }

    @Test
    fun testAppRendersCorrectly() {
        console.log("[DEBUG_LOG] Starting App rendering test")

        // Get the root element
        val rootElement = document.getElementById("root")
        assertNotNull(rootElement, "Root element should exist")

        // Render the App component
        RenderUtils.renderComposable(rootElement) {
            App()
        }

        // Check that the App component rendered something
        assertTrue(rootElement.innerHTML.isNotEmpty(), "App should render content")
        console.log("[DEBUG_LOG] App rendered content: ${rootElement.innerHTML}")

        // Check for specific elements that should be present
        assertTrue(
            rootElement.innerHTML.contains("summon-composable-container"),
            "App should contain composable containers"
        )
    }

    @Test
    fun testTaskListRendersCorrectly() {
        console.log("[DEBUG_LOG] Starting TaskList rendering test")

        // Get the root element
        val rootElement = document.getElementById("root")
        assertNotNull(rootElement, "Root element should exist")

        // Create some test tasks
        val tasks = listOf(
            Task("test-1", "Test Task 1", false),
            Task("test-2", "Test Task 2", true)
        )

        // Render the TaskList component
        RenderUtils.renderComposable(rootElement) {
            TaskList(
                tasks = tasks,
                onTaskCompleted = { },
                onTaskDeleted = { }
            )
        }

        // Check that the TaskList component rendered something
        assertTrue(rootElement.innerHTML.isNotEmpty(), "TaskList should render content")
        console.log("[DEBUG_LOG] TaskList rendered content: ${rootElement.innerHTML}")

        // Check for specific elements that should be present
        assertTrue(rootElement.innerHTML.contains("Test Task 1"), "TaskList should contain task 1")
        assertTrue(rootElement.innerHTML.contains("Test Task 2"), "TaskList should contain task 2")
    }

    @Test
    fun testThemeToggleWorks() {
        console.log("[DEBUG_LOG] Starting theme toggle test")

        // Get the root element
        val rootElement = document.getElementById("root")
        assertNotNull(rootElement, "Root element should exist")

        // Initialize theme with light mode
        ThemeManager.initialize(isDarkMode = false)
        assertFalse(ThemeManager.isDarkMode, "Theme should start in light mode")

        // Render the App component
        RenderUtils.renderComposable(rootElement) {
            App()
        }

        // Check that the App component rendered something
        assertTrue(rootElement.innerHTML.isNotEmpty(), "App should render content")

        // Toggle the theme
        ThemeManager.toggleTheme()
        assertTrue(ThemeManager.isDarkMode, "Theme should be in dark mode after toggle")

        // Re-render the App component
        RenderUtils.renderComposable(rootElement) {
            App()
        }

        // Check that the App component rendered with the new theme
        assertTrue(rootElement.innerHTML.isNotEmpty(), "App should render content with new theme")
        console.log("[DEBUG_LOG] App rendered content with new theme: ${rootElement.innerHTML}")
    }
}
