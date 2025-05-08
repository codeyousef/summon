package code.yousef.summon.examples.js

import code.yousef.summon.annotation.App
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.routing.seo.Header
import code.yousef.summon.examples.js.components.TaskForm
import code.yousef.summon.examples.js.components.TaskList
import code.yousef.summon.examples.js.state.Task
import code.yousef.summon.examples.js.state.TaskFilter
import code.yousef.summon.i18n.I18nConfig
import code.yousef.summon.i18n.Language
import code.yousef.summon.i18n.LayoutDirection
import code.yousef.summon.i18n.LocalLanguage
import code.yousef.summon.i18n.LocalLayoutDirection
import code.yousef.summon.i18n.stringResource
import code.yousef.summon.i18n.withDirection
import code.yousef.summon.i18n.changeLanguage
import code.yousef.summon.components.display.Text
import code.yousef.summon.examples.js.core.CompositionLocalProvider
import code.yousef.summon.js.console
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.JsPlatformRendererFacade
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.theme.ThemeManager

/**
 * Theme provider component that provides theme context to the application.
 */
@Composable
private fun ThemeProvider(content: @Composable () -> Unit) {
    // In a real app, this would be a more complex component that provides theme context
    // For this example, we'll just render the content
    console.log("[DEBUG] ThemeProvider: Setting up theme context")
    content()
    console.log("[DEBUG] ThemeProvider: Theme context completed")
}

/**
 * Main application component that wraps the entire UI.
 * This component provides language and theme context to the application
 * and renders the main layout.
 * 
 * The @App annotation marks this as the root composable for the application.
 */
@App
@Composable
fun App(content: @Composable () -> Unit) {
    console.log("[DEBUG] App component started")

    try {
        // Get default language
        val defaultLanguage = I18nConfig.defaultLanguage ?: I18nConfig.supportedLanguages.firstOrNull()
        console.log("[DEBUG] Default language: ${defaultLanguage?.code ?: "none"}")

        // Create language state
        val language = remember { mutableStateOf(defaultLanguage) }
        console.log("[DEBUG] Language state created")

        // Provide language context to the entire application
        console.log("[DEBUG] Setting up language providers")

        // Create a new JsPlatformRendererFacade instance directly
        val renderer = JsPlatformRendererFacade()
        console.log("[DEBUG] Created new JsPlatformRendererFacade instance for composition")

        // Ensure the renderer is provided to LocalPlatformRenderer at the JavaScript level
        js("""
            try {
                console.log("[DEBUG] Ensuring renderer is provided to LocalPlatformRenderer");
                // Make sure we're using the same instance throughout the app
                if (typeof code.yousef.summon.runtime.LocalPlatformRenderer !== 'undefined') {
                    // Create aliases for the functions without the "_k$" suffix
                    if (renderer.renderText_wa0k6q && !renderer.renderText_wa0k6q_k$) {
                        renderer.renderText_wa0k6q_k$ = renderer.renderText_wa0k6q;
                    }
                    if (renderer.renderRow_e96qap && !renderer.renderRow_e96qap_k$) {
                        renderer.renderRow_e96qap_k$ = renderer.renderRow_e96qap;
                    }
                    if (renderer.renderColumn_aiouiv && !renderer.renderColumn_aiouiv_k$) {
                        renderer.renderColumn_aiouiv_k$ = renderer.renderColumn_aiouiv;
                    }
                    if (renderer.renderBlock_bncpua && !renderer.renderBlock_bncpua_k$) {
                        renderer.renderBlock_bncpua_k$ = renderer.renderBlock_bncpua;
                    }
                    if (renderer.renderButton_ahh62j && !renderer.renderButton_ahh62j_k$) {
                        renderer.renderButton_ahh62j_k$ = renderer.renderButton_ahh62j;
                    }
                    if (renderer.renderTextField_w72mnb && !renderer.renderTextField_w72mnb_k$) {
                        renderer.renderTextField_w72mnb_k$ = renderer.renderTextField_w72mnb;
                    }

                    code.yousef.summon.runtime.LocalPlatformRenderer.provides(renderer);
                    console.log("[DEBUG] Renderer provided to LocalPlatformRenderer successfully");
                } else {
                    console.error("[DEBUG] LocalPlatformRenderer not found");
                }
            } catch (e) {
                console.error("[DEBUG] Error providing renderer to LocalPlatformRenderer: " + e);
            }
        """)

        CompositionLocalProvider(
            content = {
                console.log("[DEBUG] Language providers set up successfully")

                // Provide theme context to the entire application
                console.log("[DEBUG] Setting up ThemeProvider")
                ThemeProvider {
                    console.log("[DEBUG] Inside ThemeProvider")

                    // Render the main app content
                    console.log("[DEBUG] Rendering AppContent")
                    try {
                        AppContent()
                        console.log("[DEBUG] AppContent rendered successfully")
                    } catch (e: Throwable) {
                        console.error("[DEBUG] Error rendering AppContent: " + e.message)
                        console.error("[DEBUG] Error stack: " + e.stackTraceToString())
                    }

                    // Render any additional content (typically router-provided content)
                    console.log("[DEBUG] Rendering additional content")
                    try {
                        content()
                        console.log("[DEBUG] Additional content rendered successfully")
                    } catch (e: Throwable) {
                        console.error("[DEBUG] Error rendering additional content: " + e.message)
                    }
                }
            },
            LocalLanguage to { language.value ?: Language("en", "English", LayoutDirection.LTR) },
            LocalLayoutDirection to { language.value?.direction ?: LayoutDirection.LTR },
            // Use a lambda to provide the renderer
            LocalPlatformRenderer to { renderer }
        )
        console.log("[DEBUG] App component completed successfully")
    } catch (e: Throwable) {
        console.error("[DEBUG] Uncaught error in App component: " + e.message)
        console.error("[DEBUG] Error stack: " + e.stackTraceToString())
    }
}

/**
 * Main application content with state management.
 */
@Composable
private fun AppContent() {
    console.log("[DEBUG] AppContent started")

    // Get current language
    console.log("[DEBUG] Getting current language")
    val currentLanguage = LocalLanguage.current
    console.log("[DEBUG] Current language: \${currentLanguage.code}")

    // Application state
    console.log("[DEBUG] Initializing application state")
    val tasks = remember { mutableStateOf<List<Task>>(loadInitialTasks()) }
    val currentFilter = remember { mutableStateOf<TaskFilter>(TaskFilter.ALL) }
    val isDarkMode = remember { mutableStateOf<Boolean>(ThemeManager.isDarkMode) }
    console.log("[DEBUG] Application state initialized")

    // Side effect to save tasks when they change
    // In a real app, this would save to localStorage or a backend
    console.log("[DEBUG] Tasks updated: " + tasks.value.size + " tasks")

    console.log("[DEBUG] Creating Column layout")
    Column(
        modifier = Modifier()
            .withDirection() // Apply direction attribute based on current language
            .padding("20px")
            .background("#f5f5f5")
            .maxWidth("1200px")
            .margin("0 auto")
    ) {
        // Application header with language selector and theme toggle
        Header(
            className = "app-header",
            modifier = Modifier()
                .padding("20px")
                .background("#ffffff")
                .borderRadius("8px")
                .margin("0 0 20px 0")
                .shadow(),
            content = {
                // Create a custom header content that includes language and theme controls
                Column {
                    // App title
                    Text(
                        text = stringResource("app.title"),
                        modifier = Modifier()
                            .fontSize("24px")
                            .fontWeight("bold")
                            .color("#333333")
                            .marginBottom("16px")
                    )

                    // Controls row with language selector and theme toggle
                    Row(
                        modifier = Modifier()
                            .width("100%")
                            .padding("10px")
                            .margin("10px 0")
                    ) {
                        // Language selector
                        Column(
                            modifier = Modifier()
                                .padding("10px")
                                .margin("0 20px 0 0")
                                .background("#f0f0f0")
                                .borderRadius("4px")
                        ) {
                            Text(
                                text = stringResource("common.language"),
                                modifier = Modifier()
                                    .fontSize("16px")
                                    .fontWeight("bold")
                                    .color("#555555")
                                    .marginBottom("8px")
                            )

                            Row(
                                modifier = Modifier()
                                    .width("100%")
                                    .margin("5px 0")
                            ) {
                                I18nConfig.supportedLanguages.forEach { language ->
                                    Button(
                                        onClick = { changeLanguage(language.code) },
                                        label = language.name,
                                        modifier = Modifier()
                                            .margin("0 5px")
                                            .padding("8px 12px")
                                            .background("#4285f4")
                                            .color("#ffffff")
                                            .borderRadius("4px")
                                            .cursor("pointer")
                                    )
                                }
                            }
                        }

                        // Theme toggle
                        Column(
                            modifier = Modifier()
                                .padding("10px")
                                .margin("0")
                                .background("#f0f0f0")
                                .borderRadius("4px")
                        ) {
                            Text(
                                text = stringResource("theme.title"),
                                modifier = Modifier()
                                    .fontSize("16px")
                                    .fontWeight("bold")
                                    .color("#555555")
                                    .marginBottom("8px")
                            )

                            Button(
                                onClick = { 
                                    isDarkMode.value = !isDarkMode.value
                                    ThemeManager.toggleTheme()
                                },
                                label = if (isDarkMode.value) stringResource("theme.light") else stringResource("theme.dark"),
                                modifier = Modifier()
                                    .margin("0 5px")
                                    .padding("8px 12px")
                                    .background(if (isDarkMode.value) "#f8c555" else "#555555")
                                    .color("#ffffff")
                                    .borderRadius("4px")
                                    .cursor("pointer")
                            )
                        }
                    }
                }
            }
        )

        // Task form for adding new tasks
        TaskForm(
            onTaskAdded = { taskTitle ->
                val newTask = Task(
                    id = "task-" + (tasks.value.size + 1),
                    title = taskTitle,
                    completed = false
                )
                tasks.value = tasks.value + newTask
            },
            modifier = Modifier()
                .padding("20px")
                .margin("0 0 20px 0")
                .background("#ffffff")
                .borderRadius("8px")
                .shadow()
        )

        // Filter controls
        TaskFilterControls(
            currentFilter = currentFilter.value,
            onFilterChanged = { filter -> currentFilter.value = filter },
            modifier = Modifier()
                .padding("15px")
                .margin("0 0 20px 0")
                .background("#ffffff")
                .borderRadius("8px")
                .shadow()
        )

        // Task list with the current filter applied
        TaskList(
            tasks = tasks.value.filter { task ->
                when (currentFilter.value) {
                    TaskFilter.ALL -> true
                    TaskFilter.ACTIVE -> !task.completed
                    TaskFilter.COMPLETED -> task.completed
                }
            },
            onTaskCompleted = { taskId ->
                tasks.value = tasks.value.map { task ->
                    if (task.id == taskId) task.copy(completed = !task.completed) else task
                }
            },
            onTaskDeleted = { taskId ->
                tasks.value = tasks.value.filter { it.id != taskId }
            },
            modifier = Modifier()
                .padding("20px")
                .background("#ffffff")
                .borderRadius("8px")
                .shadow()
                .style("max-height", "400px")
                .style("overflow-y", "auto")
        )
    }
}

/**
 * Filter controls for the task list.
 */
@Composable
private fun TaskFilterControls(
    currentFilter: TaskFilter,
    onFilterChanged: (TaskFilter) -> Unit,
    modifier: Modifier = Modifier()
) {
    Column(
        modifier = modifier
    ) {
        // Add a title for the filter controls
        Text(
            text = stringResource("filters.title"),
            modifier = Modifier()
                .fontSize("18px")
                .fontWeight("bold")
                .color("#333333")
                .textAlign("center")
                .margin("0 0 10px 0")
        )

        Row(
            modifier = Modifier()
                .width("100%")
                .padding("10px")
                .style("justify-content", "center")
                .style("align-items", "center")
        ) {
            FilterButton(
                label = stringResource("filters.all"),
                isSelected = currentFilter == TaskFilter.ALL,
                onClick = { onFilterChanged(TaskFilter.ALL) }
            )

            FilterButton(
                label = stringResource("filters.active"),
                isSelected = currentFilter == TaskFilter.ACTIVE,
                onClick = { onFilterChanged(TaskFilter.ACTIVE) }
            )

            FilterButton(
                label = stringResource("filters.completed"),
                isSelected = currentFilter == TaskFilter.COMPLETED,
                onClick = { onFilterChanged(TaskFilter.COMPLETED) }
            )
        }
    }
}

/**
 * Button for selecting a task filter.
 */
@Composable
private fun FilterButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        label = label,
        modifier = Modifier()
            .margin("0 5px")
            .padding("8px 12px")
            .background(if (isSelected) "#4285f4" else "#e0e0e0")
            .color(if (isSelected) "#ffffff" else "#333333")
            .borderRadius("4px")
            .cursor("pointer")
    )
}

/**
 * Load initial tasks for the application.
 * In a real app, this would load from localStorage or a backend.
 */
private fun loadInitialTasks(): List<Task> {
    return listOf(
        Task("task-1", "Learn Summon basics", true),
        Task("task-2", "Create a component", false),
        Task("task-3", "Implement state management", false),
        Task("task-4", "Add animations", false),
        Task("task-5", "Support multiple languages", false)
    )
}

// Using Console from Summon library
